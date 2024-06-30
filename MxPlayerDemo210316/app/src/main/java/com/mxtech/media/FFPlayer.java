package com.mxtech.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mxtech.app.AppUtils;
import com.mxtech.subtitle.DrawableFrame2;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.subtitle.SubStationAlphaSubtitle;
import com.mxtech.text.NativeString;

import java.io.FileDescriptor;
import java.util.Map;

public class FFPlayer implements Handler.Callback{
    Context mContext;



    private long _nativeClient;
    private long _nativePlayer;



    public FFPlayer(Context context) {
        mContext = context;

        Looper looper = Looper.myLooper();
        if (looper != null) {
            this._eventHandler = new Handler(looper, this);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this._eventHandler = new Handler(looper2, this);
            } else {
                throw new IllegalStateException("No looper found");
            }
        }


    }

    static {
//        AppUtils.loadLibrary(libPath, "mxutil");
//        AppUtils.loadLibrary(ffmpegPath, L.FFMPEG);
//        AppUtils.loadLibrary(libPath, "mxvp");
//        System.loadLibrary("mxutil");
//        System.loadLibrary("ffmpeg.mx");
//        System.loadLibrary("mxvp");








//        String[] supportedTypes;
        nativeClassInit();
//        if (Build.VERSION.SDK_INT >= 16) {
//            int numCodecs = MediaCodecList.getCodecCount();
//            for (int i = 0; i < numCodecs; i++) {
//                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
//                if (!codecInfo.isEncoder()) {
//                    boolean HW = isHardwareComponent(codecInfo.getName());
//                    for (String type : codecInfo.getSupportedTypes()) {
//                        if (HW || type.startsWith("audio/")) {
//                            registerCodecMime(type);
//                        }
//                    }
//                }
//            }
//        }
        setAllowedOMXCodecs(getOMXSupportedVideoCodecs(true));
//        SUBTITLE_CODEC_SHORTNAMES = new String[]{"DVD", "DVB", "TXT", "XSUB", "SSA", "TXT", "PGS", "TEL", "SRT", null, null, "JSS", null, null, null, null, "SRT", "VTT", null, null, null, "SSA", null, null};
    }





    public void setDataSource(Uri uri, @Nullable Map<String, String> headers) throws Exception {
        String headersText;
        if ("content".equals(uri.getScheme())) {
//            this._fd = App.cr.openAssetFileDescriptor(uri, "r");
//            setDataSource(App.context, this._fd.getFileDescriptor(), MediaUtils.ffmpegPathFrom(uri), this._fd.getStartOffset(), this._fd.getLength());
            Log.e("FFPlayer.setDataSource:","\"content\".equals(uri.getScheme())");
            return;
        }
        String userAgent = null;
        String cookies = null;
        if (headers != null) {
            StringBuilder b = new StringBuilder();
            for (Map.Entry<String, String> pair : headers.entrySet()) {
                String key = pair.getKey();
                String value = pair.getValue();
                if ("user-agent".equalsIgnoreCase(key)) {
                    userAgent = value;
                } else if ("cookies".equalsIgnoreCase(key)) {
                    cookies = value;
                } else {
                    b.append(key).append(": ").append(value).append("\r\n");
                }
            }
            headersText = b.toString();
        } else {
            headersText = null;
        }
        Log.e("FFPlayer.setDataSource:","Uri:= "+MediaUtils.ffmpegPathFrom(uri));
        setDataSource(mContext, MediaUtils.ffmpegPathFrom(uri), headersText, userAgent, cookies);
    }

    public void setDataSource(Uri uri, String content) throws Exception {
        setDataSource(content, MediaUtils.ffmpegPathFrom(uri));
    }

    public void setDataSource(Uri uri, NativeString content) throws Exception {
        setDataSource(content, MediaUtils.ffmpegPathFrom(uri));
    }


    private native void setDataSource(Context context, FileDescriptor fileDescriptor, @Nullable String str, long j, long j2) throws IllegalStateException;

    private native void setDataSource(Context context, String str, String str2, String str3, String str4) throws IllegalStateException;

    private native void setDataSource(NativeString nativeString, String str) throws IllegalStateException;

    private native void setDataSource(String str, String str2) throws IllegalStateException;



    private static native void nativeClassInit();





    private final Handler _eventHandler;
    private void postEvent(int what, int arg1, int arg2, Object obj) {
        if ((what != 11 && what != 20) || !this._eventHandler.hasMessages(what)) {
            this._eventHandler.sendMessage(this._eventHandler.obtainMessage(what, arg1, arg2, obj));
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
//        Log.e("FFPlayer:","handleMessage:" + msg.what);
        return false;
    }


    private int getProperty(int property) {
//        Log.e("getProperty:", String.valueOf(property));
//        switch (property) {
//            case 1:
//                if (Build.VERSION.SDK_INT >= 17) {
//                    return 1;
//                }
//                break;
//            case 2:
//                if (Build.VERSION.SDK_INT >= 17) {
//                    return 2;
//                }
//                break;
//        }
        return 1;
    }





    private static native boolean isHardwareComponent(String str);

    private static native void registerCodecMime(String str);

    public native int getCodec();

    public static native int getCodec(String str);

    public static native void setAllowedOMXCodecs(int i);

    private native long native_create(SubStationAlphaMedia subStationAlphaMedia, int i, boolean z) throws Exception;

    private native void native_release() throws Exception;

    private native int clock();

    public native void updateClock(int i);




//    @Override // com.mxtech.media.IMediaPlayer
    public native boolean isPrepared();

//    @Override // com.mxtech.media.IMediaInfoAux
    public native boolean hasEmbeddedSubtitle();

    private native int getDefaultAudioStream_l();

//    @Override // com.mxtech.media.IMediaPlayer
    public native int getAudioStream();

    private native void prepareAsync_() throws IllegalStateException;

    private native boolean changeAudioStream_l(int i, int i2);

//    @Override // com.mxtech.media.IMediaPlayer
    public native void reconfigAudioDevice();


//    @Override // com.mxtech.media.IMediaPlayer
    public native void setAudioOffset(int i);


//    @Override // com.mxtech.media.IMediaPlayer
    public native void setProcessing(int i);


























    private native void _pause();

    private native void _seekTo(int i, int i2) throws IllegalStateException;

    private native void _start() throws IllegalStateException;

    private native boolean attachSubtitleTrack_SubStationAlphaSubtitle(SubStationAlphaSubtitle subStationAlphaSubtitle);

    private native boolean attachSubtitleTrack_SubTrack(long j);

//    private native boolean changeAudioStream_l(int i, int i2);
//
//    private native int clock();

    private native void detachSubtitleTrack_SubStationAlphaSubtitle(SubStationAlphaSubtitle subStationAlphaSubtitle);

    private native void detachSubtitleTrack_SubTrack(long j);

    private native int displayHeight_();

    private native int displayWidth_();

    /* JADX INFO: Access modifiers changed from: private */
    public native void enableSubtitleTrack(int i, boolean z) throws IllegalStateException;

//    public static native int getCodec(String str);
//
//    private native int getDefaultAudioStream_l();

    private native String getMetadata(int i, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamBitRate(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamChannelCount(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native long getStreamChannelLayout(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamDisplayHeight(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamDisplayWidth(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamDisposition(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamFrameTime(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamHeight(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native String getStreamMetadata(int i, int i2, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamSampleRate(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamType(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int getStreamWidth(int i);

    private static native int getSubtitleCodecIndex(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public native Object getSubtitleFrames(long j);

    private native int height_();

    public static native boolean is10bitsColorFormat(int i);

//    private static native boolean isHardwareComponent(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean isSupportedSubtitleTrack(long j);

//    private static native void nativeClassInit();
//
//    private native long native_create(SubStationAlphaMedia subStationAlphaMedia, int i, boolean z) throws Exception;
//
//    private native void native_release() throws Exception;

    /* JADX INFO: Access modifiers changed from: private */
    public native int nextSubtitle(long j);

//    private native void prepareAsync_() throws IllegalStateException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int previousSubtitle(long j);

//    private static native void registerCodecMime(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void renderSubStationAlphaFrame(long j, Bitmap bitmap, int i);

//    public static native void setAllowedOMXCodecs(int i);
//
//    private native void setDataSource(Context context, FileDescriptor fileDescriptor, @Nullable String str, long j, long j2) throws IllegalStateException;
//
//    private native void setDataSource(Context context, String str, String str2, String str3, String str4) throws IllegalStateException;
//
//    private native void setDataSource(NativeString nativeString, String str) throws IllegalStateException;
//
//    private native void setDataSource(String str, String str2) throws IllegalStateException;

    private native void setSpeed_(double d);

    private native void setSubtitleTranslation_(int i, double d);

    private native boolean setSurface(Surface surface, double d, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean updateSubtitle(long j, int i);

    private native int width_();

    public native int calcDisplayWidth(int i);

    public native boolean canSwitchToOMXDecoder();

//    @Override // com.mxtech.media.IMediaPlayer
    public native int duration();

    /* JADX INFO: Access modifiers changed from: package-private */
    public native void forceAudioTimeSync(boolean z);

//    @Override // com.mxtech.media.IMediaInfoAux
    public native int frameTime();

//    @Override // com.mxtech.media.IMediaPlayer
//    public native int getAudioStream();

//    public native int getCodec();

    public native int getConnectivity();

//    @Override // com.mxtech.media.IMediaPlayer
    public native Bitmap[] getCovers() throws OutOfMemoryError;

    public native String getFormat(int i);

//    @Override // com.mxtech.media.IMediaPlayer
    public native int getProcessing();

    public native String getStreamCodec(int i, int i2);

    public native int getStreamCodecId(int i);

//    @Override // com.mxtech.media.IMediaInfoAux
    public native int getStreamCount();

    public native String getStreamProfile(int i);

//    @Override // com.mxtech.media.IMediaInfoAux
    public native int[] getStreamTypes();

    public native SubStationAlphaMedia getSubStationAlphaMedia_();

    public native int getVideoStreamIndex();

//    @Override // com.mxtech.media.IMediaInfoAux
//    public native boolean hasEmbeddedSubtitle();

//    @Override // com.mxtech.media.IMediaPlayer
    public native boolean hasVideoTrack();

    public native boolean isDecoderSupported(int i);

    public native boolean isMpegTS();

    public native boolean isOMXAudioDecoderUsed();

    public native boolean isOMXVideoDecoderUsed();

//    @Override // com.mxtech.media.IMediaPlayer
//    public native boolean isPrepared();

    public native int pixelFormat();

//    @Override // com.mxtech.media.IMediaPlayer
//    public native void reconfigAudioDevice();

    public native boolean removeAudioStream();

//    @Override // com.mxtech.media.IMediaPlayer
//    public native void setAudioOffset(int i);

//    @Override // com.mxtech.media.IMediaPlayer
    public native void setAudioStreamType(int i);

    public native void setCoreLimit(int i);

    public native void setFixedFastMode(boolean z);

    public native void setInformativeVideoSize(int i, int i2);

//    @Override // com.mxtech.media.IMediaPlayer
//    public native void setProcessing(int i);

//    @Override // com.mxtech.media.IMediaPlayer
    public native void setStereoMode(int i);

//    @Override // com.mxtech.media.IMediaPlayer
    public native void setVolume(float f, float f2);

//    @Override // com.mxtech.media.IMediaPlayer
    public native void setVolumeModifier(float f);

//    public native void updateClock(int i);

































    private static class SubStationAlphaFrame implements DrawableFrame2 {
        private final int _arg0;
        private RectF _bound;
        private final long _nativeTrack;

        SubStationAlphaFrame(long nativeTrack, int arg0) {
            this._nativeTrack = nativeTrack;
            this._arg0 = arg0;
        }

        @Override // com.mxtech.subtitle.Frame
        public void updateBounds(int canvasWidth, int canvasHeight, int videoWidth, int videoHeight, float scale) {
        }

        @Override // com.mxtech.subtitle.DrawableFrame
        public void draw(Canvas canvas) {
        }

        @Override // com.mxtech.subtitle.DrawableFrame2
        public void draw(Canvas canvas, Bitmap bitmap) {
            FFPlayer.renderSubStationAlphaFrame(this._nativeTrack, bitmap, this._arg0);
        }
    }




    public static class SubtitleTrackContext {
        boolean best;
        long nativeContext;
        int outputType;
        int streamIndex;

        public SubtitleTrackContext(int streamIndex, int outputType, boolean best, long nativeContext) {
            this.streamIndex = streamIndex;
            this.outputType = outputType;
            this.best = best;
            this.nativeContext = nativeContext;
        }
    }



    public static int getOMXSupportedVideoCodecs(boolean includeSWCodecs) {
        String[] supportedTypes;
        int codecs = 0;
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                if (!includeSWCodecs) {
                    String component = codecInfo.getName();
                    if (isHardwareComponent(component)) {
                        if (!"OMX.Nvidia.vc1.decode".equals(component)) {
                            if ("OMX.Nvidia.mjpeg.decoder".equals(component)) {
                            }
                        }
                    }
                }
                for (String type : codecInfo.getSupportedTypes()) {
                    if (type.startsWith("video/")) {
                        int codec = getCodec(type);
                        if ((38911 & codec) != 0) {
                            codecs |= codec;
                            if (codec == 2) {
                                try {
                                    MediaCodecInfo.CodecCapabilities cap = codecInfo.getCapabilitiesForType(type);
                                    MediaCodecInfo.CodecProfileLevel[] codecProfileLevelArr = cap.profileLevels;
                                    int length = codecProfileLevelArr.length;
                                    int i2 = 0;
                                    while (true) {
                                        if (i2 < length) {
                                            MediaCodecInfo.CodecProfileLevel pl = codecProfileLevelArr[i2];
                                            if (pl.profile != 16) {
                                                i2++;
                                            } else {
                                                codecs |= 4;
                                                break;
                                            }
                                        }
                                    }
                                } catch (Throwable e) {
                                    Log.w("MX.Player.FF/CodecInfo", "Thrown while reading codec compatiblities for `" + type + "`", e);
                                }
                            } else if (codec == 1) {
                                MediaCodecInfo.CodecCapabilities cap2 = codecInfo.getCapabilitiesForType(type);
                                MediaCodecInfo.CodecProfileLevel[] codecProfileLevelArr2 = cap2.profileLevels;
                                int length2 = codecProfileLevelArr2.length;
                                int i3 = 0;
                                while (true) {
                                    if (i3 < length2) {
                                        MediaCodecInfo.CodecProfileLevel pl2 = codecProfileLevelArr2[i3];
                                        if (pl2.profile != 2) {
                                            i3++;
                                        } else {
                                            codecs |= 256;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if ((32896 & codecs) != 0) {
            return codecs | 32896;
        }
        return codecs;
    }




























    private boolean _screenOnWhilePlaying;
    private boolean _stayAwake;
    private SurfaceHolder _surfaceHolder;
    public static final String TAG = "MX.Player.FF";

    public void setDisplay(SurfaceHolder sh, Display display) {
        setDisplay(sh, display, 0);
    }


    public boolean setDisplay(@Nullable SurfaceHolder sh, @Nullable Display display, int flags) {
        boolean result;
        if (sh != null) {
            float refreshRate = display != null ? display.getRefreshRate() : 0.0f;
            if (refreshRate <= 0.0f) {
                Log.w(TAG, "Adjust invalid refresh rate [" + refreshRate + "] to [60]. (display:" + display + ")");
                refreshRate = 60.0f;
            }
            result = setSurface(sh.getSurface(), refreshRate, flags);
        } else {
            result = setSurface(null, 0.0d, flags);
        }
        this._surfaceHolder = sh;
        updateSurfaceScreenOn();
        return result;
    }


    private void updateSurfaceScreenOn() {
        if (this._surfaceHolder != null) {
            this._surfaceHolder.setKeepScreenOn(this._screenOnWhilePlaying && this._stayAwake);
        }
    }



    public void prepareAsync() throws IllegalStateException {
        Log.d(TAG, "PrepareAsync()");
//        this._prepareInvoked = true;
        prepareAsync_();
    }


    public void start() {
//        this._playing = true;
        stayAwake(true);
        _start();
    }


    private void stayAwake(boolean awake) {
        this._stayAwake = awake;
        updateSurfaceScreenOn();
    }







}
