package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class Player implements Handler.Callback{
    private MediaRouter _mediaRouter;
    private MediaRouter.Callback _mediaRouterCallback;
    private MediaPlayer _mp;
    private float _maxBaseVolume;




    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case 1:
                this._handler.sendEmptyMessageDelayed(1, 100L);
//                this._mp.sync();
                int position = this._mp.getCurrentPosition();
//                if (this._repeatEnd >= 0 && position >= this._repeatEnd) {
//                    Log.i(TAG, "Repeat A-B: jump to start point (" + this._repeatStart + "ms) as end point (" + this._repeatEnd + "ms) reaches.");
//                    seekTo(this._repeatStart, 6000);
//                }
                this._client.update(this, position);
                return true;
            case 2:
                if (isInPlaybackState()) {
                    setState(6);
//                    for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
//                        ml.onMediaPlaybackCompleted();
//                    }
                    return true;
                }
                return true;
            case 3:
                int iteration = (msg.arg1 >> 16) + 1;
                int volumeLevel = msg.arg1 & 65535;
                int lastPosition = msg.arg2;
                int pos = this._mp.getCurrentPosition();
                if (pos != lastPosition || volumeLevel >= 2 || iteration >= 600) {
                    volumeLevel++;
                    float volume = 0.1f * volumeLevel;
                    if (volume >= 1.0f) {
//                        setSymmetricBaseVolume(this._maxBaseVolume);
//                        this._rampupPrepared = false;
                        return true;
                    }
                    float volume2 = volume * this._maxBaseVolume;
                    this._mp.setVolume(volume2, volume2);
                }
                this._handler.sendMessageDelayed(this._handler.obtainMessage(3, (iteration << 16) | volumeLevel, pos), 50L);
                return true;
            case 4:
//                updateDuration();
                return true;
            default:
                return false;
        }
    }



    public interface Client {
//        boolean canStart();
//
//        int mediaTimeToSubtitleTime(int i);
//
////        void onAudioCodecUnsupported(FFPlayer fFPlayer, int i);
//
////        void onAudioStreamChanged(JointPlayer jointPlayer, int i);
//
//        void onBufferingUpdate(int i);
//
//        void onCoverArtChanged();
//
//        void onDurationKnown(int i);
//
////        void onEmbeddedSubtitleAdded(ISubtitle iSubtitle);
//
//        void onRebootToChangeDisplay(int i);
//
////        void onRemoteResourceLoaded(List<ISubtitle> list, Bitmap bitmap, Uri uri);
//
//        void onRemoteResourceLoadingBegin(int i);
//
//        void onRemoteResourceLoadingCanceled();
//
////        void onSSAPrepared(SubStationAlphaMedia subStationAlphaMedia);
//
//        void onSeekBegin(int i);
//
//        void onSeekComplete();
//
//        void onSetupFontCache(boolean z);

        void onStateChanged(int i, int i2);

//        void onSubtitleClosed(ISubtitle iSubtitle);

//        void onSubtitleInvalidated();
//
//        void onSubtitlesClosed();
//
//        void onTargetStateChanged(int i, int i2);
//
//        void onTryNextDecoder(byte b, byte b2, boolean z);
//
//        void onVideoDeviceChanged();
//
//        void onVideoFilteringFailed(int i);
//
        void onVideoSizeChanged(int i, int i2);

        void update(Player player, int i);

//        void updatePersistent(Uri uri, MediaDatabase.State state, List<ISubtitle> list);
    }

    @SuppressLint("WrongConstant")
    public Player(Context context, Client client) {
        if (client == null) {
            throw new IllegalStateException();
        }
//        this._httpFactory = httpFactory;
//        this._context = context.getApplicationContext();
//        this._client = client;
//        setSoftAudio(P.softAudio);
        if (Build.VERSION.SDK_INT >= 16) {
            this._mediaRouter = (MediaRouter) context.getSystemService("media_router");
            if (this._mediaRouter != null) {
                this._mediaRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.mxtech.videoplayer.Player.1
                    @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
                    public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                        if (_mp != null) {
//                            _mp.reconfigAudioDevice();
                        }
                    }
                };
            }
        }
    }


    public boolean isInPlaybackState() {
        return true;
    }


    public int getDisplayWidth() {
        return 1080;
    }

    public int getDisplayHeight() {
        return 720;
    }


    private  float _horzRatio;
    private  float _vertRatio;
    private  int _displayWidth;
    private  int _displayHeight;

    @SuppressLint("LongLogTag")
    public  void printLog(int width, int height){
        Log.e("打印 this._horzRatio：", String.valueOf(this._horzRatio));
        Log.e("打印 this._vertRatio：", String.valueOf(this._vertRatio));
        Log.e("打印 width：", String.valueOf(width));
        Log.e("打印 height：", String.valueOf(height));
        if (this._horzRatio > 0.0f && this._vertRatio > 0.0f) {
            Log.e("打印 _horzRatio > 0.0f：", "_vertRatio > 0.0f");
            if (width < height) {
                Log.e("打印 width < height：", "width < height");
//                this._displayWidth = width;
//                this._displayHeight = (int) ((width * this._vertRatio) / this._horzRatio);
            } else {
                Log.e("打印 else   _width < height：", "width < height");
//                this._displayWidth = (int) ((height * this._horzRatio) / this._vertRatio);
//                this._displayHeight = height;
            }
        } else {
            Log.e("打印 else    _horzRatio > 0.0f：", "获取视频的宽高");
//            boolean correct = P.getCorrectHWAspectRatio();
//            this._displayWidth = this._mp.displayWidth(correct);
//            this._displayHeight = this._mp.displayHeight(correct);
        }


    }

    public  void printLogTest(){
        printLog(888,666);
    }


    public void recalcVideoSize() {
        updateSize(this._mp.getVideoWidth(), this._mp.getVideoHeight());
    }

    private void updateSize(int width, int height) {
        printLog(width,height);
        if (this._horzRatio > 0.0f && this._vertRatio > 0.0f) {
            if (width < height) {
                this._displayWidth = width;
                this._displayHeight = (int) ((width * this._vertRatio) / this._horzRatio);
            } else {
                this._displayWidth = (int) ((height * this._horzRatio) / this._vertRatio);
                this._displayHeight = height;
            }
        } else {
//            boolean correct = P.getCorrectHWAspectRatio();
//            this._displayWidth = this._mp.displayWidth(correct);
//            this._displayHeight = this._mp.displayHeight(correct);
        }
        this._client.onVideoSizeChanged(width, height);
    }

//    private JointPlayer _mp;


    public int getSourceWidth() {
        return this._mp.getVideoWidth();
    }

    public int getSourceHeight() {
        return this._mp.getVideoHeight();
    }



    @Nullable
    public MediaPlayer mp() {
        return this._mp;
    }





    private int _state = 0;
    public void setDataSource(Uri uri, byte requestedDecoder, int requestedDecodingOptions, int flags) throws IllegalStateException {
        if (this._state != 0) {
            throw new IllegalStateException();
        }
//        this._requireIP = false;
//        this._hostnameResolved = false;
//        String scheme = uri.getScheme();
//        if (scheme != null && (StringUtils.startsWithIgnoreCase(scheme, "http") || StringUtils.startsWithIgnoreCase(scheme, "mms") || StringUtils.startsWithIgnoreCase(scheme, "rt"))) {
//            uri = analyzeUri(uri);
//            this._uri = uri;
//            String hostname = uri.getHost();
//            if (hostname != null && hostname.length() > 0) {
//                try {
//                    if (hostname.equals("localhost")) {
//                        this._host = InetAddress.getByAddress(new byte[]{Byte.MAX_VALUE, 0, 0, 1});
//                    } else {
//                        String[] parts = hostname.split("\\.");
//                        if (parts.length != 4) {
//                            this._requireIP = true;
//                        } else {
//                            try {
//                                byte[] nums = new byte[4];
//                                for (int i = 0; i < 4; i++) {
//                                    int val = Integer.parseInt(parts[i]);
//                                    if (val < 0 || val > 255) {
//                                        this._requireIP = true;
//                                        break;
//                                    }
//                                    nums[i] = (byte) val;
//                                }
//                                this._host = InetAddress.getByAddress(nums);
//                            } catch (NumberFormatException e) {
//                                this._requireIP = true;
//                            }
//                        }
//                    }
//                } catch (UnknownHostException e2) {
//                    Log.e(TAG, "", e2);
//                }
//            }
//        }
//        if (this._uri == null) {
//            this._file = MediaUtils.fileFromUri(uri);
//            if (this._file != null) {
//                this._uri = Uri.fromFile(this._file);
//                Log.v(TAG, "Canonicalizing URI(2) " + uri + " -> " + this._uri);
//            } else {
//                this._uri = uri;
//            }
//        }
//        if (this._file != null) {
//            if (P.softAudioLocal) {
//                setSoftAudio(true);
//            }
//        } else if (P.softAudioNetwork) {
//            setSoftAudio(true);
//        }
//        this._title = MediaUtils.retrieveTitle(this._uri, (P.list_fields & 16) != 0, null);
//        if ((flags & 1) != 0) {
//            try {
//                MediaDatabase mdb = MediaDatabase.getInstance();
//                this._persistent = mdb.readState(this._uri);
//                mdb.release();
//            } catch (SQLiteException e3) {
//                Log.e(TAG, "", e3);
//                this._persistent = null;
//            }
//            if (this._persistent != null) {
//                this._horzRatio = this._persistent.horzRatio;
//                this._vertRatio = this._persistent.vertRatio;
//                this._explicitRatio = true;
//            }
//        }
//        pickupDecoder(requestedDecoder, requestedDecodingOptions);
        setState(1);
    }

    public void setState(int state) {
        if (this._state != state) {
            setState_l(state);
        }
    }


    private final Handler _handler = new Handler(Looper.getMainLooper(), this);
    private Player.Client _client;

    private void setState_l(int state) {
        int old = this._state;
        this._state = state;
        if (this._state == 4) {
            if (!this._handler.hasMessages(1)) {
                this._handler.sendEmptyMessageDelayed(1, 100L);
            }
//            if (this._rampupPrepared) {
//                rampUp();
//            }
        } else {
            this._handler.removeMessages(1);
            this._handler.removeMessages(3);
        }
        this._client.onStateChanged(old, state);
    }



    public void setDisplay(SurfaceHolder holder, int flags) {
        MediaPlayer primary;
        int flags2;
//        if (this._dnsLookupTask != null) {
//            this._dnsLookupTask.setDisplay(holder);
//        }
        if (this._mp != null ) {
//            if (holder == null || !isInPlaybackState() || this._mp.hasVideoTrack()) {
//                if (primary instanceof FFPlayer) {
//                    if (this._decoder == 4) {
//                        flags2 = flags | 32;
//                    } else {
//                        flags2 = flags | 64;
//                    }
//                    FFPlayer ff = (FFPlayer) primary;
//                    ff.setCoreLimit(P.getCoreLimit());
//                    if (holder != null) {
//                        pause(80);
//                        if ((P.audioRampUp & 1) != 0) {
//                            prepareRampUp();
//                        }
//                    }
//                    this._isChangingDisplay = ff.setDisplay(holder, flags2);
//                    if (!this._isChangingDisplay && holder != null) {
//                        resume();
//                    }
//                } else if (holder != null) {
//                    if (canChangeHWSurface()) {
//                        primary.setDisplay(holder);
            _mp.setDisplay(holder);
//                        return;
//                    }
//                    pause(16);
//                    save();
//                    suspendForRestart();
//                    this._client.onRebootToChangeDisplay(flags);
//                } else {
//                    primary.setDisplay(null);
//                }
//            }
        }
    }

    public boolean isInActiveState() {
        return this._state >= 2;
    }

    public boolean isInValidState() {
        return this._state >= 1;
    }
    private byte _decoder= (byte) 2;

    public byte getDecoder() {
        return this._decoder;
    }

    public int getState() {
        return this._state;
    }





    public boolean load(SurfaceHolder surfaceHolder, int videoFlags, boolean useSpeedupTricks) throws IllegalStateException {
        if (this._state != 1) {
            throw new IllegalStateException();
        }
//        if (this._requireIP && !this._hostnameResolved) {
//            this._dnsLookupTask = new DNSLookupTask(this._uri.getHost(), surfaceHolder, videoFlags, useSpeedupTricks);
//            this._dnsLookupTask.executeParallel(new Void[0]);
//        } else {
//            try {
        try {
            doLoad(surfaceHolder, videoFlags, useSpeedupTricks);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("load  error:",  e.getMessage());
            return false;
        }
//            } catch (Exception e) {
//                Log.e(TAG, "", e);
//                handleError(0);
//                return false;
//            }
//        }
        setState(2);
        return true;
    }





    public void doLoad(SurfaceHolder surfaceHolder, int videoFlags, boolean useSpeedupTricks) throws Exception {
//        Map<String, String> headers = mergeMap(this._httpFactory.getHttpHeaders(this._uri), this._privateHeader);
        if (true) {
//            BuiltinPlayer primary = new BuiltinPlayer(this._context, this, this._uri, headers, App.prefs.getBoolean(Key.SUBTITLE_SHOW_HW, false) ? 1 : 0);
//            try {
//                if (App.prefs.contains(Key.CALIBRATE_HW_PLAY_POSITION)) {
//                    primary.calibratePlaybackPosition((int) (App.prefs.getFloat(Key.CALIBRATE_HW_PLAY_POSITION, 0.0f) * 1000.0f));
//                }
//                if (surfaceHolder != null) {
//                    primary.setDisplay(surfaceHolder);
//                    primary.setScreenOnWhilePlaying(true);
//                }
//                FFPlayer secondary = null;
//                try {
//                    if (this._kept != null) {
//                        secondary = this._kept;
//                        this._kept = null;
//                    } else if ((this._availableDecoders & 6) != 0) {
//                        if (P.softAudio || P.softAudioNetwork || isLocalMedia()) {
                            MediaPlayer secondary2 = new MediaPlayer();
//                            try {
                                secondary2.setDataSource("/storage/emulated/0/video.mp4");
                                secondary2.prepare();
                                this._mp = secondary2;

            setState(4);




//                                secondary = secondary2;
//                            } catch (Throwable th) {
//                                th = th;
//                                secondary = secondary2;
//                                if (secondary != null) {
//                                    secondary.close();
//                                }
//                                throw th;
//                            }
//                        } else {
//                            secondary = null;
//                            Log.w(TAG, "Skipping 2ndary player due to host is not local - " + this._host);
//                        }
//                    } else {
//                        secondary = null;
//                    }
//                    this._mp = new JointPlayer(primary, secondary, 0);
//                    primary = null;
//                    FFPlayer secondary3 = null;
//                    if (0 != 0) {
//                        secondary3.close();
//                    }
//                } catch (Throwable th2) {
//                    th = th2;
//                }
//            } finally {
//                if (primary != null) {
//                    primary.close();
//                }
//            }
        } else {
            MediaPlayer primary2 = null;
//            try {
//                if (this._kept != null) {
//                    primary2 = this._kept;
//                    this._kept = null;
//                } else {
            MediaPlayer primary3 = new MediaPlayer();
//                    try {
                        primary3.setDataSource("/storage/emulated/0/video.mp4");
                        primary3.prepare();
                        primary2 = primary3;
//                    } catch (Throwable th3) {
//                        th = th3;
//                        primary2 = primary3;
//                        if (primary2 != null) {
//                            primary2.close();
//                        }
//                        throw th;
//                    }
//                }
//                primary2.removeAudioStream();
                if (surfaceHolder != null) {
//                    primary2.setCoreLimit(P.getCoreLimit());
                    primary2.setDisplay(surfaceHolder);
//                    primary2.setScreenOnWhilePlaying(true);
                }
//                this._mp = new JointPlayer(primary2, null, 0);
            this._mp = primary2;

            setState(4);
//                FFPlayer primary4 = null;
//                if (0 != 0) {
//                    primary4.close();
//                }
//            } catch (Throwable th4) {
//                th = th4;
//            }
        }
//        this._mp.setListener((JointPlayer.Listener) this);
        this._mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
           @Override
           public void onPrepared(MediaPlayer mp) {
               _mp.start();
           }
        });
        this._mp.setAudioStreamType(3);
        this._mp.prepareAsync();
//        if (this._persistent != null) {
//            this._mp.setProcessing(this._persistent.process);
//            this._userAudioOffset = this._persistent.audioOffset;
//        } else {
//            int deinterlacer = App.prefs.getInt(Key.DEFAULT_DEINTERLACER, 0);
//            if (deinterlacer != 0) {
//                this._mp.setProcessing(deinterlacer);
//            }
//        }
        if (this._mediaRouterCallback != null) {
            this._mediaRouter.addCallback(1, this._mediaRouterCallback);
        }
    }

//    private MediaDatabase.State _persistent




    public void start() {
//        setTargetState(4);
//        if (isInPlaybackState() && !this._isChangingDisplay && this._client.canStart()) {
            this._mp.start();
            setState(4);
//            for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
//                ml.onMediaPlay();
//            }
//        }
    }



    public void setAspectRatio(float horzRatio, float vertRatio, boolean explicit) {
//        if (horzRatio != this._horzRatio || vertRatio != this._vertRatio) {
//            this._explicitRatio = explicit;
//            this._horzRatio = horzRatio;
//            this._vertRatio = vertRatio;
            int width = this._mp.getVideoWidth();
            int height = this._mp.getVideoHeight();
            if (width > 0 && height > 0) {
                updateSize(width, height);
            }
//        }
    }

    private MediaDatabase.State _persistent;

    public MediaDatabase.State getPersistent() {
        return this._persistent;
    }








































































    public void setDisplay(SurfaceHolder holder) {

    }

    public void prepare() {
    }


    public int getVideoWidth() {
        return 1024;
    }

    public int getVideoHeight() {
        return 720;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
    }

    public void setDataSource(String uri)  {

    }




}
