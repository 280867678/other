package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.media.MediaMetadataSafeRetriever;
//import android.media.ThumbnailSafeUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.FFReader;
import com.mxtech.media.service.IFFService;
import com.mxtech.os.SysInfo;
import com.mxtech.videoplayer.directory.MediaDirectory;
import com.mxtech.videoplayer.directory.MediaDirectoryService;
import com.mxtech.videoplayer.list.ListHelper;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes2.dex */
public class MediaLoader {
    public static final int ALL = 7;
    public static final int COVER = 4;
    private static final int FLAG_SHAPE_DURATION = 2;
    private static final int FLAG_SHAPE_THUMBNAIL = 1;
    public static final int INFO = 1;
    public static final int THUMB = 2;
    private static final int THUMB_ITERATION = 5;
    private static boolean _shrinked;
    private final Client _client;
    private boolean _closed;
    private final Handler _handler;
    private HeavyThread _heavy;
    private LightThread _light;
    private final ThumbShaper _shaper;
    public static final String TAG = App.TAG + ".Loader";
    private static final String TAG_LIGHT = TAG + ".Light";
    private static final String TAG_HEAVY = TAG + ".Heavy";

    /* loaded from: classes.dex */
    public interface Client {
        IFFService getFFService(IFFService iFFService);

        void releaseFFService(IFFService iFFService);
    }

    /* loaded from: classes.dex */
    public interface Receiver {
        void onCompleted(MediaLoader mediaLoader, Result result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface Worker {
        void run(Param param);
    }

    /* loaded from: classes2.dex */
    public static class Result {
        public byte audioTrackCount;
        public int duration;
        public int frameTime;
        public int height;
        public Boolean interlaced;
        public boolean noThumb;
        public final int requested;
        public byte subtitleTrackCount;
        public int subtitleTrackTypes;
        public final Object tag;
        public int tried;
        public byte videoTrackCount;
        public int width;

        private Result(int requested, Object tag) {
            this.tag = tag;
            this.requested = requested;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Param extends Result implements Runnable {
        @Nullable
        public final File coverFile;
        @Nullable
        public final Bitmap defaultThumb;
        @Nullable
        public String durationText;
        public final File file;
        public final int flags;
        public int loads;
        private final Receiver receiver;
        @Nullable
        public final Uri uri;
        private Worker worker;

        Param(int loads, @Nullable Uri uri, File file, @Nullable File coverFile, @Nullable String durationText, @Nullable Bitmap defaultThumb, Receiver receiver, Object tag, int flags) {
            super(loads, tag);
            this.loads = loads;
            this.uri = uri;
            this.file = file;
            this.coverFile = coverFile;
            this.durationText = durationText;
            this.defaultThumb = defaultThumb;
            this.receiver = receiver;
            this.flags = flags;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.worker != null) {
                this.worker.run(this);
            } else {
                this.receiver.onCompleted(MediaLoader.this, this);
            }
        }
    }

    public MediaLoader(Client client, ThumbShaper shaper, Handler handler) {
        this._client = client;
        this._shaper = shaper;
        this._handler = handler;
    }

    public synchronized void cancelAllPendingRequests() {
        if (this._light != null) {
            this._light.handler.removeCallbacksAndMessages(null);
        }
        if (this._heavy != null) {
            this._heavy.handler.removeCallbacksAndMessages(null);
        }
    }

    public void close(boolean join) {
        LightThread light;
        HeavyThread heavy;
        synchronized (this) {
            this._closed = true;
            if (this._light != null) {
                light = this._light;
                this._light = null;
            } else {
                light = null;
            }
            if (this._heavy != null) {
                heavy = this._heavy;
                this._heavy = null;
            } else {
                heavy = null;
            }
        }
        if (light != null) {
            light.quit();
        }
        if (heavy != null) {
            heavy.quit();
        }
        if (join) {
            if (light != null) {
                try {
                    light.join();
                } catch (InterruptedException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (heavy != null) {
                heavy.join();
            }
        }
        if (light != null) {
            light.handler.removeCallbacksAndMessages(null);
        }
        if (heavy != null) {
            heavy.handler.removeCallbacksAndMessages(null);
        }
        this._handler.removeCallbacksAndMessages(this);
    }

    public void requestWithThumbShaping(int loads, Uri mediaUri, File mediaFile, @Nullable File coverFile, @Nullable String durationText, @Nullable Bitmap defaultThumb, Receiver receiver, Object tag, boolean needDurationShaping) {
        Param param = new Param(loads, mediaUri, mediaFile, coverFile, durationText, defaultThumb, receiver, tag, (needDurationShaping ? 2 : 0) | 1);
        if ((loads & 1) == 0) {
            postToLight(param);
        } else {
            postToHeavy(param);
        }
    }

    public void request(int loads, File mediaFile, File coverFile, Receiver receiver, Object tag) {
        Param param = new Param(loads, null, mediaFile, coverFile, null, null, receiver, tag, 0);
        if ((loads & 1) == 0) {
            postToLight(param);
        } else {
            postToHeavy(param);
        }
    }

    private synchronized void postToLight(Param param) {
        if (!this._closed) {
            if (this._light == null) {
                this._light = new LightThread();
            }
            param.worker = this._light;
            this._light.handler.post(param);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void postToHeavy(Param param) {
        if (!this._closed) {
            if (this._heavy == null) {
                this._heavy = new HeavyThread();
            }
            param.worker = this._heavy;
            this._heavy.handler.post(param);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToCaller(Param param, @Nullable Bitmap thumb) {
        String durationText;
        if ((param.flags & 3) != 0) {
            if ((param.flags & 1) != 0) {
                if (thumb == null) {
                    thumb = param.defaultThumb;
                }
            } else {
                thumb = null;
            }
            if ((param.flags & 2) != 0) {
                if (param.duration > 0) {
                    durationText = ListHelper.formatDuration(param.duration);
                } else {
                    durationText = param.durationText;
                }
            } else {
                durationText = null;
            }
            if (thumb != null || durationText != null) {
                ThumbDrawable shaped = new ThumbDrawable(App.context.getResources(), this._shaper.build(thumb, durationText), thumb, thumb != null, durationText != null);
                L.thumbCache.put(param.uri, shaped, param.coverFile);
            }
        }
        if ((param.tried & 2) != 0 && thumb == null) {
            param.noThumb = true;
        }
        param.worker = null;
        this._handler.postAtTime(param, this, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class LightThread extends HandlerThread implements Worker {
        private final byte[] _iobuffer;
        final Handler handler;

        LightThread() {
            super(MediaLoader.TAG_LIGHT);
            this._iobuffer = new byte[8388608];
            start();
            this.handler = new Handler(getLooper());
        }

        @Override // com.mxtech.videoplayer.MediaLoader.Worker
        @SuppressLint({"InlinedApi"})
        public void run(Param param) {
            Bitmap thumb;
            if ((param.loads & 4) != 0) {
                param.tried |= 4;
                try {
                    Bitmap cover = BitmapFactory.decodeFile(param.coverFile.getPath(), ThumbStorage.decodingOptions);
                    if (cover == null) {
                        Log.w(MediaLoader.TAG_LIGHT, "Can't decode " + param.coverFile);
                    } else {
                        Bitmap thumb2 = ThumbnailSafeUtils.extractThumbnail(cover, L.THUMB_WIDTH, L.THUMB_HEIGHT, 2);
                        if (thumb2 != null) {
                            MediaLoader.this.postToCaller(param, thumb2);
                            return;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    Log.e(MediaLoader.TAG_LIGHT, "Can't decode " + param.coverFile, e);
                    return;
                }
            }
            if ((param.loads & 2) == 0) {
                MediaLoader.this.postToCaller(param, null);
                return;
            }
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                int fileId = mdb.getFileID(param.file);
                if (fileId > 0 && (thumb = L.thumbStorage.read(fileId, param.file, this._iobuffer)) != null) {
                    param.tried |= 2;
                    MediaLoader.this.postToCaller(param, thumb);
                    mdb.release();
                } else {
                    mdb.release();
                    param.loads &= -5;
                    MediaLoader.this.postToHeavy(param);
                }
            } catch (SQLiteException e2) {
                Log.e(MediaLoader.TAG_LIGHT, "", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Unsaved {
        final File file;
        final int fileId;
        final Bitmap thumb;

        Unsaved(int fileId, File file, Bitmap thumb) {
            this.fileId = fileId;
            this.file = file;
            this.thumb = thumb;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class HeavyThread extends HandlerThread implements Worker, MessageQueue.IdleHandler, MediaDirectoryService.Accessor {
        private FFReader _busyReader;
        private boolean _hasIdleHandler;
        private final byte[] _iobuffer;
        private boolean _quitting;
        private IFFService _service;
        private LinkedList<Unsaved> _unsavedList;
        final Handler handler;

        HeavyThread() {
            super(MediaLoader.TAG_HEAVY);
            this._iobuffer = new byte[8388608];
            start();
            this.handler = new Handler(getLooper());
        }

        @Override // android.os.HandlerThread
        public boolean quit() {
            synchronized (this) {
                this._quitting = true;
                if (this._busyReader != null) {
                    this._busyReader.cancel();
                }
            }
            return super.quit();
        }

        @Override // com.mxtech.videoplayer.MediaLoader.Worker
        @SuppressLint({"InlinedApi"})
        public void run(Param param) {
            boolean store;
            int[] streamTypes;
            Bitmap thumb = null;
            boolean fromCover = false;
            if (param.file.length() > 0) {
                store = true;
            } else {
                Log.d(MediaLoader.TAG_HEAVY, "Skip missing file " + param.file);
                param.loads = 0;
                param.tried = 7;
                store = false;
            }
            if ((param.loads & 4) != 0) {
                param.tried |= 4;
                try {
                    Bitmap cover = BitmapFactory.decodeFile(param.coverFile.getPath(), ThumbStorage.decodingOptions);
                    if (cover != null) {
                        thumb = ThumbnailSafeUtils.extractThumbnail(cover, L.THUMB_WIDTH, L.THUMB_HEIGHT, 2);
                        if (thumb != null) {
                            param.loads &= -7;
                            fromCover = true;
                        }
                    } else {
                        Log.w(MediaLoader.TAG_HEAVY, "Can't decode " + param.coverFile);
                    }
                } catch (OutOfMemoryError e) {
                    Log.e(MediaLoader.TAG_HEAVY, "Can't decode " + param.coverFile, e);
                }
            }
            if (param.loads != 0) {
                this._service = MediaLoader.this._client.getFFService(this._service);
                if (this._service != null) {
                    param.tried |= param.loads;
                    try {
                        FFReader reader = new FFReader(this._service, Uri.fromFile(param.file), true);
                        if ((param.loads & 1) != 0) {
                            param.duration = reader.duration();
                            param.width = reader.displayWidth();
                            param.height = reader.displayHeight();
                            param.frameTime = reader.frameTime();
                            int stream_index = 0;
                            for (int type : reader.getStreamTypes()) {
                                switch (type) {
                                    case 0:
                                        param.videoTrackCount = (byte) (param.videoTrackCount + 1);
                                        break;
                                    case 1:
                                        param.audioTrackCount = (byte) (param.audioTrackCount + 1);
                                        break;
                                    case 3:
                                        param.subtitleTrackCount = (byte) (param.subtitleTrackCount + 1);
                                        int codec_index = FFPlayer.getSubtitleCodecIndex(reader.getStreamCodecId(stream_index));
                                        if (codec_index >= 0) {
                                            param.subtitleTrackTypes |= 1 << codec_index;
                                            break;
                                        } else {
                                            break;
                                        }
                                }
                                stream_index++;
                            }
                            param.loads &= -2;
                        }
                        if ((param.loads & 2) != 0) {
                            if ((param.loads & 1) == 0 || param.videoTrackCount != 0) {
                                synchronized (this) {
                                    if (this._quitting) {
                                        reader.close();
                                        return;
                                    }
                                    this._busyReader = reader;
                                    try {
                                        Log.i(MediaLoader.TAG_HEAVY, "Extracting ffmpeg thumb from " + param.file.getPath());
                                        thumb = reader.extractThumb(L.THUMB_WIDTH, L.THUMB_HEIGHT, 5, true);
                                        param.interlaced = reader.isInterlaced();
                                        synchronized (this) {
                                            this._busyReader = null;
                                        }
                                        if (thumb != null) {
                                            param.loads &= -7;
                                        } else if (this._quitting) {
                                            param.tried &= -3;
                                            param.loads &= -3;
                                        }
                                    } catch (Throwable th) {
                                        synchronized (this) {
                                            this._busyReader = null;
                                            throw th;
                                        }
                                    }
                                }
                            } else {
                                reader.close();
                            }
                        }
                        reader.close();
                    } catch (Exception e2) {
                        Log.w(MediaLoader.TAG_HEAVY, "Can't load from ffmpeg.", e2);
                    }
                }
            }
            if (param.loads != 0) {
                if (this._quitting) {
                    return;
                }
                param.tried |= param.loads;
                MediaMetadataSafeRetriever retriever = new MediaMetadataSafeRetriever();
                try {
                    retriever.setDataSource(param.file.getPath());
                    if ((param.loads & 1) != 0) {
                        String duration = retriever.extractMetadata(9);
                        if (duration != null) {
                            try {
                                param.duration = Integer.parseInt(duration);
                            } catch (NumberFormatException e3) {
                            }
                        }
                        String width = retriever.extractMetadata(18);
                        if (width != null) {
                            try {
                                param.width = Integer.parseInt(width);
                            } catch (NumberFormatException e4) {
                            }
                        }
                        String height = retriever.extractMetadata(19);
                        if (height != null) {
                            try {
                                param.height = Integer.parseInt(height);
                            } catch (NumberFormatException e5) {
                            }
                        }
                    }
                    if ((param.loads & 2) != 0) {
                        Log.i(MediaLoader.TAG_HEAVY, "Extracting built-in thumb from " + param.file.getPath());
                        try {
                            thumb = retriever.extractThumbnail(L.THUMB_WIDTH, L.THUMB_HEIGHT, 5, true);
                            if (thumb != null) {
                                param.loads &= -7;
                            }
                        } catch (OutOfMemoryError e6) {
                            Log.e(MediaLoader.TAG_HEAVY, "Can't extract thumbnail from " + param.file, e6);
                        }
                    }
                } catch (Exception e7) {
                    Log.w(MediaLoader.TAG_HEAVY, "Can't load from built-in.");
                } finally {
                    retriever.release();
                }
            }
            MediaLoader.this.postToCaller(param, thumb);
            if (!store || (param.tried & 3) == 0) {
                return;
            }
            ContentValues values = new ContentValues();
            if ((param.tried & 1) != 0) {
                values.put("Read", (Integer) 1);
                values.put("VideoTrackCount", Byte.valueOf(param.videoTrackCount));
                values.put("AudioTrackCount", Byte.valueOf(param.audioTrackCount));
                values.put("SubtitleTrackCount", Byte.valueOf(param.subtitleTrackCount));
                values.put("SubtitleTrackTypes", Integer.valueOf(param.subtitleTrackTypes));
                values.put("Duration", Integer.valueOf(param.duration));
                values.put("FrameTime", Integer.valueOf(param.frameTime));
                values.put("Width", Integer.valueOf(param.width));
                values.put("Height", Integer.valueOf(param.height));
            }
            if (param.interlaced != null) {
                values.put("Interlaced", param.interlaced);
            }
            if ((param.tried & 2) != 0) {
                values.put("NoThumbnail", Integer.valueOf(thumb == null ? 1 : 0));
            }
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                int fileId = mdb.upsertVideoFile(param.file, values, true);
                if (thumb != null && !fromCover) {
                    if (!MediaLoader._shrinked && !this._hasIdleHandler) {
                        Looper.myQueue().addIdleHandler(this);
                        this._hasIdleHandler = true;
                    }
                    if (P.cacheThumbnail) {
                        if (SysInfo.isWritingSlow()) {
                            delaySave(fileId, param.file, thumb);
                        } else {
                            L.thumbStorage.write(fileId, param.file, thumb, this._iobuffer);
                        }
                    }
                }
                mdb.release();
            } catch (Exception e8) {
                Log.e(MediaLoader.TAG_HEAVY, "", e8);
            }
        }

        private void delaySave(int fileId, File file, Bitmap thumb) {
            if (!this._hasIdleHandler) {
                Looper.myQueue().addIdleHandler(this);
                this._hasIdleHandler = true;
            }
            if (this._unsavedList == null) {
                this._unsavedList = new LinkedList<>();
            }
            this._unsavedList.add(new Unsaved(fileId, file, thumb));
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (this._service != null) {
                MediaLoader.this._client.releaseFFService(this._service);
                this._service = null;
            }
            if (!MediaLoader._shrinked) {
                L.directoryService.requestModify(this);
                boolean unused = MediaLoader._shrinked = true;
            }
            if (this._unsavedList == null) {
                this._hasIdleHandler = false;
                return false;
            }
            do {
                Unsaved entry = this._unsavedList.poll();
                if (entry == null) {
                    return true;
                }
                L.thumbStorage.write(entry.fileId, entry.file, entry.thumb, this._iobuffer);
                if (this.handler.hasMessages(3)) {
                    break;
                }
            } while (!this.handler.hasMessages(2));
            return true;
        }

        @Override // android.os.HandlerThread, java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            if (this._unsavedList != null) {
                Iterator<Unsaved> it = this._unsavedList.iterator();
                while (it.hasNext()) {
                    Unsaved entry = it.next();
                    L.thumbStorage.write(entry.fileId, entry.file, entry.thumb, this._iobuffer);
                }
            }
            if (this._service != null) {
                MediaLoader.this._client.releaseFFService(this._service);
                this._service = null;
            }
        }

        @Override // com.mxtech.videoplayer.directory.MediaDirectoryService.Accessor
        public void run(MediaDirectoryService.MockController mockController, MediaDirectory mdir, Message message) {
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                mdb.clearExpired(mdir);
                mdb.release();
            } catch (SQLiteException e) {
                Log.e(MediaLoader.TAG_HEAVY, "", e);
            }
        }
    }
}
