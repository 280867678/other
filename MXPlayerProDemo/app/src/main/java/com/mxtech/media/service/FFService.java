package com.mxtech.media.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.mxtech.Library;
import com.mxtech.app.AppUtils;
import com.mxtech.media.MediaReader;
import com.mxtech.media.service.IFFService;
import com.mxtech.os.Cpu;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class FFService extends Service {
    public static final String EXTRA_CODEC_PACKAGE_OR_SYSTEM_LIB = "codec_package_name";
    public static final String EXTRA_CUSTOM_FFMPEG_PATH = "custom_ffmpeg_path";
    private static final String TAG = App.TAG + ".FFService";
    private static boolean _initialized = false;
    private final AtomicInteger _workCount = new AtomicInteger();
    private final Set<Long> _readers = new HashSet();
    private final IFFService.Stub _binder = new IFFService.Stub() { // from class: com.mxtech.media.service.FFService.1
        @Override // com.mxtech.media.service.IFFService
        public long r_create(String path, boolean localFileOnly) throws RemoteException {
            try {
                if (FFService.this._workCount.incrementAndGet() < 0) {
                    Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                    return 0L;
                }
                long context = MediaReader.native_create(path, localFileOnly);
                synchronized (FFService.this) {
                    FFService.this._readers.add(Long.valueOf(context));
                }
                return context;
            } catch (Exception e) {
                Log.e(FFService.TAG, "MediaReader creation failed.");
                return 0L;
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public void r_release(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return;
            }
            try {
                synchronized (FFService.this) {
                    FFService.this._readers.remove(Long.valueOf(context));
                }
                MediaReader.native_release(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public void r_cancel(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return;
            }
            try {
                MediaReader.cancel(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_frameTime(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.frameTime(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_duration(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.duration(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_rotation(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.rotation(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_height(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.height(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_width(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.width(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_displayWidth(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.displayWidth(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_displayHeight(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.displayHeight(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_isInterlaced(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return -1;
            }
            try {
                return MediaReader.isInterlaced(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public boolean r_hasEmbeddedSubtitle(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return false;
            }
            try {
                return MediaReader.hasEmbeddedSubtitle(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public Bitmap r_extractThumb(long context, int width, int height, int iteration, boolean allowBlanc) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.extractThumb(context, width, height, iteration, allowBlanc);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public String r_getFormat(long context, int mode) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getFormat(context, mode);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public String r_getMetadata(long context, int key, String lang3) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getMetadata(context, key, lang3);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public String r_getStreamMetadata(long context, int streamIndex, int key, String lang3) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getStreamMetadata(context, streamIndex, key, lang3);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamWidth(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamWidth(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamHeight(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamHeight(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamDisplayWidth(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamDisplayWidth(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamDisplayHeight(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamDisplayHeight(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamType(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamType(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamDisposition(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamDisposition(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamCodecId(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamCodecId(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public String r_getStreamCodec(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getStreamCodec(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public String r_getStreamProfile(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getStreamProfile(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamFrameTime(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamFrameTime(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamBitRate(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamBitRate(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamSampleRate(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamSampleRate(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public long r_getStreamChannelLayout(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0L;
            }
            try {
                return MediaReader.getStreamChannelLayout(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamChannelCount(long context, int stream) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamChannelCount(context, stream);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int r_getStreamCount(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return 0;
            }
            try {
                return MediaReader.getStreamCount(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }

        @Override // com.mxtech.media.service.IFFService
        public int[] r_getStreamTypes(long context) throws RemoteException {
            if (FFService.this._workCount.incrementAndGet() < 0) {
                Log.i(FFService.TAG, "Service is being destroyed. tid:" + Process.myTid());
                return null;
            }
            try {
                return MediaReader.getStreamTypes(context);
            } finally {
                FFService.this._workCount.decrementAndGet();
            }
        }
    };

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        String libPath;
        synchronized (FFService.class) {
            if (!_initialized) {
                try {
                    String codecPackageOrSystemLib = intent.getStringExtra(EXTRA_CODEC_PACKAGE_OR_SYSTEM_LIB);
                    String ffmpegPath = intent.getStringExtra(EXTRA_CUSTOM_FFMPEG_PATH);
                    if (codecPackageOrSystemLib.startsWith(Library.PACKAGE_COMMON)) {
                        PackageManager pm = getPackageManager();
                        ApplicationInfo info = pm.getApplicationInfo(codecPackageOrSystemLib, 0);
                        libPath = AppUtils.getNativeLibraryDir(info);
                    } else {
                        libPath = codecPackageOrSystemLib;
                    }
                    if (ffmpegPath == null) {
                        ffmpegPath = libPath;
                    }
                    AppUtils.loadLibrary(libPath, "mxutil");
                    AppUtils.loadLibrary(ffmpegPath, L.FFMPEG);
                    AppUtils.loadLibrary(libPath, "mxvp");
                    L.native_init(this, 2, Build.VERSION.SDK_INT, null, getFilesDir().getPath(), null, Cpu.coreCount, 0, 0);
                    _initialized = true;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "", e);
                    return null;
                }
            }
        }
        return this._binder.asBinder();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        while (!this._workCount.compareAndSet(0, -99999999)) {
            synchronized (this) {
                for (Long l : this._readers) {
                    MediaReader.cancel(l.longValue());
                }
            }
            Log.i(TAG, "Waiting for " + this._workCount.get() + " unfinished jobs.");
            SystemClock.sleep(500L);
        }
        for (Long l2 : this._readers) {
            long context = l2.longValue();
            Log.i(TAG, "Releasing dead object " + context);
            MediaReader.native_release(context);
        }
        super.onDestroy();
    }
}
