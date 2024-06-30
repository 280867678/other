package com.mxtech.media;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.subtitle.DrawableFrame2;
import com.mxtech.subtitle.Frame;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.ISubtitleClient;
import com.mxtech.subtitle.SubRipSubtitle;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.subtitle.SubStationAlphaSubtitle;
import com.mxtech.subtitle.WebVTTSubtitleBasic;
import com.mxtech.videoplayer.App;

import com.mxtech.videoplayer.Verifier;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public final class FFPlayer implements IMediaPlayer, Handler.Callback {
    private static final int AVAILABLE_OMX_CODEC = 38911;
    public static final int CONNECTIVITY_FAST = 0;
    public static final int CONNECTIVITY_LAGGY = 1;
    public static final int CONNECTIVITY_UNKNOWN = -1;
    public static final int FLAG_BLOCK = 2;
    public static final int FLAG_CHECK_ALLOWANCE = 128;
    public static final int FLAG_CLEAR = 8;
    public static final int FLAG_DONT_TRY_HARDWARE_AUDIO = 256;
    public static final int FLAG_DONT_TRY_SOFTWARE_AUDIO = 512;
    public static final int FLAG_FASTMODE = 1;
    public static final int FLAG_HARDWARE_VIDEO = 32;
    public static final int FLAG_MASK_AUDIO = 1920;
    public static final int FLAG_MASK_VIDEO = 239;
    public static final int FLAG_PREFER_SOFTWARE_AUDIO = 1024;
    public static final int FLAG_RELEASE = 4;
    public static final int FLAG_SOFTWARE_VIDEO = 64;
    public static final int GSC_FULLNAME = 1;
    private static final int MEDIA_AUDIO_STREAM_CHANGED = 6;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_COVER_ART_CHANGED = 20;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_PLAYBACK_COMPLETED = 2;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_SUBTITLE_INVALIDATED = 11;
    private static final int MEDIA_SUBTITLE_TRACK_ADDED = 10;
    private static final int MEDIA_VIDEO_DEVICE_CHANGED = 299;
    public static final int MX_INFO_FIRST = 100000000;
    public static final int MX_INFO_VIDEO_DECODER_INIT_FAILED = 100000002;
    public static final int MX_INFO_VIDEO_DECODER_MANUALLY_REJECTED = 100000001;
    public static final int MX_INFO_VIDEO_FILTERING_FAILED = 100000003;
    public static final int NB_SUBTITLE_CODEC = 24;
    private static final int PROPERTY_PRIMITIVE_AUDIO_FRAMES_PER_BUFFER = 2;
    private static final int PROPERTY_PRIMITIVE_AUDIO_SAMPLE_RATE = 1;
    public static final int SUBTITLE_ASS = 3;
    public static final int SUBTITLE_BITMAP = 1;
    public static final int SUBTITLE_CODEC_INDEX_ASS = 21;
    public static final int SUBTITLE_CODEC_INDEX_DVB_SUBTITLE = 1;
    public static final int SUBTITLE_CODEC_INDEX_DVB_TELETEXT = 7;
    public static final int SUBTITLE_CODEC_INDEX_DVD_SUBTITLE = 0;
    public static final int SUBTITLE_CODEC_INDEX_EIA_608 = 10;
    public static final int SUBTITLE_CODEC_INDEX_HDMV_PGS_SUBTITLE = 6;
    public static final int SUBTITLE_CODEC_INDEX_HDMV_TEXT_SUBTITLE = 23;
    public static final int SUBTITLE_CODEC_INDEX_JACOSUB = 11;
    public static final int SUBTITLE_CODEC_INDEX_MICRODVD = 9;
    public static final int SUBTITLE_CODEC_INDEX_MOV_TEXT = 5;
    public static final int SUBTITLE_CODEC_INDEX_MPL2 = 18;
    public static final int SUBTITLE_CODEC_INDEX_PJS = 20;
    public static final int SUBTITLE_CODEC_INDEX_REALTEXT = 13;
    public static final int SUBTITLE_CODEC_INDEX_SAMI = 12;
    public static final int SUBTITLE_CODEC_INDEX_SRT = 8;
    public static final int SUBTITLE_CODEC_INDEX_SSA = 4;
    public static final int SUBTITLE_CODEC_INDEX_STL = 22;
    public static final int SUBTITLE_CODEC_INDEX_SUBRIP = 16;
    public static final int SUBTITLE_CODEC_INDEX_SUBVIEWER = 15;
    public static final int SUBTITLE_CODEC_INDEX_SUBVIEWER1 = 14;
    public static final int SUBTITLE_CODEC_INDEX_TEXT = 2;
    public static final int SUBTITLE_CODEC_INDEX_VPLAYER = 19;
    public static final int SUBTITLE_CODEC_INDEX_WEBVTT = 17;
    public static final int SUBTITLE_CODEC_INDEX_XSUB = 3;
    public static final int SUBTITLE_NONE = 0;
    public static final int SUBTITLE_TEXT = 2;
    public static final String SUB_SCHEMA = "ffsub";
    public static final int VFILTER_TYPE_SUBTITLE = 2;
    private final Handler _eventHandler;
    private AssetFileDescriptor _fd;
    private IMediaPlayer.Listener _listener;
    private long _nativeClient;
    private long _nativePlayer;
    private OnSubtitleEnabledListener _onSubtitleEnabledListener;
    private boolean _playing;
    private boolean _rot90;
    private int _rotationDegrees;
    private boolean _screenOnWhilePlaying;
    private boolean _stayAwake;
    private final ISubtitleClient _subClient;
    private int _subTrackSequence;
    private List<ISubtitle> _subTracks;
    private SurfaceHolder _surfaceHolder;
    public static final String TAG = App.TAG + ".FF";
    private static final String TAG_CODEC = TAG + ".CodecInfo";
    public static final String[] SUBTITLE_CODEC_SHORTNAMES = {"DVD", "DVB", "TXT", "XSUB", "SSA", "TXT", "PGS", "TEL", "SRT", null, null, "JSS", null, null, null, null, "SRT", "VTT", null, null, null, "SSA", null, null};
    private int _lastRequestedAudioStreamIndex = -1;
    private double _speed = 1.0d;
    private int _subtitleSync = 0;
    private double _subtitleSpeed = 1.0d;
    private int _seekTarget = -1;

    /* loaded from: classes2.dex */
    interface OnSubtitleEnabledListener {
        void onSubtitleEnabled(FFPlayer fFPlayer, SubTrack subTrack, boolean z);
    }

    private native void _pause();

    private native void _seekTo(int i, int i2) throws IllegalStateException;

    private native void _start() throws IllegalStateException;

    private native boolean attachSubtitleTrack_SubStationAlphaSubtitle(SubStationAlphaSubtitle subStationAlphaSubtitle);

    private native boolean attachSubtitleTrack_SubTrack(long j);

    private native boolean changeAudioStream_l(int i, int i2);

    private native int clock();

    private native void detachSubtitleTrack_SubStationAlphaSubtitle(SubStationAlphaSubtitle subStationAlphaSubtitle);

    private native void detachSubtitleTrack_SubTrack(long j);

    private native int displayHeight_();

    private native int displayWidth_();

    /* JADX INFO: Access modifiers changed from: private */
    public native void enableSubtitleTrack(int i, boolean z) throws IllegalStateException;

    public static native int getCodec(String str);

    private native int getDefaultAudioStream_l();

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

    private static native boolean isHardwareComponent(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean isSupportedSubtitleTrack(long j);

    private native long native_create(SubStationAlphaMedia subStationAlphaMedia, int i, boolean z) throws Exception;

    private native void native_release() throws Exception;

    /* JADX INFO: Access modifiers changed from: private */
    public native int nextSubtitle(long j);

    private native void prepareAsync_() throws IllegalStateException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int previousSubtitle(long j);

    private static native void registerCodecMime(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void renderSubStationAlphaFrame(long j, Bitmap bitmap, int i);

    public static native void setAllowedOMXCodecs(int i);

    private native void setDataSource(Context context, FileDescriptor fileDescriptor, long j, long j2) throws IllegalStateException;

    private native void setDataSource(Context context, String str, String str2, String str3, String str4) throws IllegalStateException;

    private native void setDataSource(String str, String str2) throws IllegalStateException;

    private native void setSpeed_(double d);

    private native void setSubtitleTranslation_(int i, double d);

    private native boolean setSurface(Surface surface, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean updateSubtitle(long j, int i);

    private native int width_();

    public native int calcDisplayWidth(int i);

    public native boolean canSwitchToOMXDecoder();

    @Override // com.mxtech.media.IMediaPlayer
    public native int duration();

    /* JADX INFO: Access modifiers changed from: package-private */
    public native void forceAudioTimeSync(boolean z);

    @Override // com.mxtech.media.IMediaInfoAux
    public native int frameTime();

    @Override // com.mxtech.media.IMediaPlayer
    public native int getAudioStream();

    public native int getCodec();

    public native int getConnectivity();

    @Override // com.mxtech.media.IMediaPlayer
    public native Bitmap[] getCovers() throws OutOfMemoryError;

    public native String getFormat(int i);

    @Override // com.mxtech.media.IMediaPlayer
    public native int getProcessing();

    public native String getStreamCodec(int i, int i2);

    public native int getStreamCodecId(int i);

    @Override // com.mxtech.media.IMediaInfoAux
    public native int getStreamCount();

    public native String getStreamProfile(int i);

    @Override // com.mxtech.media.IMediaInfoAux
    public native int[] getStreamTypes();

    public native SubStationAlphaMedia getSubStationAlphaMedia();

    public native int getVideoStreamIndex();

    @Override // com.mxtech.media.IMediaInfoAux
    public native boolean hasEmbeddedSubtitle();

    @Override // com.mxtech.media.IMediaPlayer
    public native boolean hasVideoTrack();

    public native boolean isDecoderSupported(int i);

    public native boolean isMpegTS();

    public native boolean isOMXAudioDecoderUsed();

    public native boolean isOMXVideoDecoderUsed();

    @Override // com.mxtech.media.IMediaPlayer
    public native boolean isPrepared();

    public native int pixelFormat();

    @Override // com.mxtech.media.IMediaPlayer
    public native void reconfigAudioDevice();

    public native boolean removeAudioStream();

    @Override // com.mxtech.media.IMediaPlayer
    public native void setAudioOffset(int i);

    @Override // com.mxtech.media.IMediaPlayer
    public native void setAudioStreamType(int i);

    public native void setCoreLimit(int i);

    public native void setFixedFastMode(boolean z);

    public native void setInformativeVideoSize(int i, int i2);

    @Override // com.mxtech.media.IMediaPlayer
    public native void setProcessing(int i);

    @Override // com.mxtech.media.IMediaPlayer
    public native void setStereoMode(int i);

    @Override // com.mxtech.media.IMediaPlayer
    public native void setVolume(float f, float f2);

    @Override // com.mxtech.media.IMediaPlayer
    public native void setVolumeModifier(float f);

    public native void updateClock(int i);

    static /* synthetic */ int access$1504(FFPlayer x0) {
        int i = x0._subTrackSequence + 1;
        x0._subTrackSequence = i;
        return i;
    }

    @SuppressLint({"InlinedApi"})
    public static void native_static_init() {
        String[] supportedTypes;
        if (Build.VERSION.SDK_INT >= 16) {
            int numCodecs = MediaCodecList.getCodecCount();
            for (int i = 0; i < numCodecs; i++) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
                if (!codecInfo.isEncoder()) {
                    boolean HW = isHardwareComponent(codecInfo.getName());
                    for (String type : codecInfo.getSupportedTypes()) {
                        if (HW || type.startsWith("audio/")) {
                            registerCodecMime(type);
                        }
                    }
                }
            }
        }
        setAllowedOMXCodecs(P.getOMXVideoCodecs());
    }

    @TargetApi(16)
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
                        if ((AVAILABLE_OMX_CODEC & codec) != 0) {
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
                                    Log.w(TAG_CODEC, "Thrown while reading codec compatiblities for `" + type + "`", e);
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

    public FFPlayer(IMediaPlayer.Listener listener, int defaultAudioCreationFlags, boolean fixedFastMode, ISubtitleClient client) throws Exception {
        this._listener = listener;
        this._subClient = client;
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this._eventHandler = new Handler(looper, this);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this._eventHandler = new Handler(looper2, this);
            } else {
                throw new IllegalStateException("no looper found");
            }
        }
        this._nativeClient = Verifier.fromSecureToken(native_create(client.getSubStationAlphaMedia(1, null), P.canUseOmxAudioOnSwVideo() ? defaultAudioCreationFlags : defaultAudioCreationFlags | 256, fixedFastMode));
    }

    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws Exception {
        String headersText;
        if ("content".equals(uri.getScheme())) {
            this._fd = App.cr.openAssetFileDescriptor(uri, "r");
            setDataSource(context, this._fd.getFileDescriptor(), this._fd.getStartOffset(), this._fd.getLength());
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
        setDataSource(context, MediaUtils.ffmpegPathFrom(uri), headersText, userAgent, cookies);
    }

    public void setDataSource(Uri uri, String content) throws Exception {
        setDataSource(content, MediaUtils.ffmpegPathFrom(uri));
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setListener(IMediaPlayer.Listener listener) {
        this._listener = listener;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCharacteristics() {
        return 63;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnSubtitleEnabledListener(OnSubtitleEnabledListener listener) {
        this._onSubtitleEnabledListener = listener;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCurrentPosition() {
        return this._seekTarget >= 0 ? this._seekTarget : clock();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getDefaultAudioStream() {
        int idx = getDefaultAudioStream_l();
        if (idx < 0) {
            return -1;
        }
        return idx;
    }

    public List<ISubtitle> getSubtitles() {
        return this._subTracks;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int changeAudioStream(int index, int flags) {
        this._lastRequestedAudioStreamIndex = index;
        if (!P.canUseOmxAudioOnSwVideo()) {
            flags |= 256;
        }
        return changeAudioStream_l(index, flags) ? 0 : -3;
    }

    public int getLastRequestedAudioStreamIndex() {
        return this._lastRequestedAudioStreamIndex;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean removeAudioStream(int current_stream_index) {
        return removeAudioStream();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getAudioChannelCount(int index) {
        return getStreamChannelCount(index);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public double getSpeed() {
        return this._speed;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setSpeed(double speed) {
        if (speed != this._speed) {
            this._speed = speed;
            setSpeed_(speed);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean isPlaying() {
        return this._playing;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void prepareAsync() throws IllegalStateException {
        prepareAsync_();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void close() {
        stayAwake(false);
        try {
            Log.v(TAG, "=== Begin closing soft player");
            native_release();
            Log.v(TAG, "=== End closing soft player");
        } catch (Exception e) {
            Log.e(TAG, "Exception thrown while releasing native player", e);
        }
        try {
            if (this._fd != null) {
                AssetFileDescriptor fd = this._fd;
                this._fd = null;
                fd.close();
            }
        } catch (IOException e2) {
            Log.e(TAG, "", e2);
        }
        this._eventHandler.removeCallbacksAndMessages(null);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void seekTo(int msec, int timeout) throws IllegalStateException {
        if (msec < 0) {
            msec = 0;
        }
        _seekTo(msec, timeout);
        this._seekTarget = msec;
    }

    public boolean needHardwareVideoForceBlock() {
        int omxcodec = getCodec();
        return (omxcodec == 4 || omxcodec == 256) && (P.getDefaultOMXVideoCodecs(true) & omxcodec) == 0;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean hasDisplay() {
        return this._surfaceHolder != null;
    }

    public boolean setDisplay(SurfaceHolder sh, int flags) {
        boolean res = setSurface(sh != null ? sh.getSurface() : null, flags);
        this._surfaceHolder = sh;
        updateSurfaceScreenOn();
        return res;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setDisplay(SurfaceHolder sh) {
        setDisplay(sh, 0);
    }

    private void stayAwake(boolean awake) {
        this._stayAwake = awake;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        if (this._surfaceHolder != null) {
            this._surfaceHolder.setKeepScreenOn(this._screenOnWhilePlaying && this._stayAwake);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setScreenOnWhilePlaying(boolean screenOn) {
        this._screenOnWhilePlaying = screenOn;
        updateSurfaceScreenOn();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void start() {
        this._playing = true;
        stayAwake(true);
        _start();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void pause() {
        this._playing = false;
        stayAwake(false);
        _pause();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public IMediaInfo info() {
        IMediaInfo info = new IMediaInfo() { // from class: com.mxtech.media.FFPlayer.1
            @Override // com.mxtech.media.IMediaInfo
            public String format() {
                return FFPlayer.this.getFormat(1);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String description() {
                return FFPlayer.this.getMetadata(103);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String album() {
                return FFPlayer.this.getMetadata(1);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String artist() {
                return FFPlayer.this.getMetadata(2);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String composer() {
                return FFPlayer.this.getMetadata(4);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String genre() {
                return FFPlayer.this.getMetadata(6);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String title() {
                return FFPlayer.this.getMetadata(7);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String mimeType() {
                return FFPlayer.this.getMetadata(12);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String albumArtist() {
                return FFPlayer.this.getMetadata(13);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String copyright() {
                return FFPlayer.this.getMetadata(14);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String encoder() {
                return FFPlayer.this.getMetadata(15);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String encoded_by() {
                return FFPlayer.this.getMetadata(16);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String performer() {
                return FFPlayer.this.getMetadata(17);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String publisher() {
                return FFPlayer.this.getMetadata(18);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String year() {
                return FFPlayer.this.getMetadata(5);
            }

            @Override // com.mxtech.media.IMediaInfo
            public Locale[] locales() {
                String langs = FFPlayer.this.getMetadata(102);
                return (langs == null || langs.length() == 0 || LocaleUtils.UNDEFINED.equalsIgnoreCase(langs)) ? new Locale[0] : LocaleUtils.parseLocales(langs);
            }

            @Override // com.mxtech.media.IMediaInfo
            public String displayLocales() {
                return FFReader.toDisplayLocales(locales());
            }

            @Override // com.mxtech.media.IMediaInfo
            public int duration() {
                return FFPlayer.this.duration();
            }

            @Override // com.mxtech.media.IMediaInfo
            public int width() {
                return FFPlayer.this.width();
            }

            @Override // com.mxtech.media.IMediaInfo
            public int height() {
                return FFPlayer.this.height();
            }

            @Override // com.mxtech.media.IMediaInfo
            public int displayWidth() {
                return FFPlayer.this.displayWidth();
            }

            @Override // com.mxtech.media.IMediaInfo
            public int displayHeight() {
                return FFPlayer.this.displayHeight();
            }

            @Override // com.mxtech.media.IMediaInfo
            public void close() {
            }
        };
        return info;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int width() {
        return this._rot90 ? height_() : width_();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int height() {
        return this._rot90 ? width_() : height_();
    }

    public int displayWidth() {
        return this._rot90 ? displayHeight_() : displayWidth_();
    }

    public int displayHeight() {
        return this._rot90 ? displayWidth_() : displayHeight_();
    }

    public int calcDisplayHeight(int height) {
        if (this._rot90) {
            return calcDisplayWidth(height);
        }
        return height;
    }

    static String getStreamTypeName(int type) {
        switch (type) {
            case 0:
                return "Video";
            case 1:
                return "Audio";
            case 2:
                return "Data";
            case 3:
                return "Subtitle";
            case 4:
                return "Attachment";
            default:
                return "Unknown";
        }
    }

    public boolean attachSubtitleTrack(ISubtitle track) {
        if (!(track instanceof SubTrack)) {
            if (track instanceof SubStationAlphaSubtitle) {
                return attachSubtitleTrack_SubStationAlphaSubtitle((SubStationAlphaSubtitle) track);
            }
            return false;
        }
        return attachSubtitleTrack_SubTrack(((SubTrack) track)._nativeTrack);
    }

    public void detachSubtitleTrack(ISubtitle track) {
        if (!(track instanceof SubTrack)) {
            if (track instanceof SubStationAlphaSubtitle) {
                detachSubtitleTrack_SubStationAlphaSubtitle((SubStationAlphaSubtitle) track);
                return;
            }
            return;
        }
        detachSubtitleTrack_SubTrack(((SubTrack) track)._nativeTrack);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getMetadata(int key) {
        return getMetadata(key, LocaleUtils.getIOS3DefaultLanguage());
    }

    public String getStreamCodec(int stream) {
        return getStreamCodec(stream, 1);
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public IStreamInfo streamInfo(int streamIndex) {
        return new StreamInfo(streamIndex);
    }

    /* loaded from: classes2.dex */
    private class StreamInfo implements IStreamInfo {
        private final int index;

        StreamInfo(int index) {
            this.index = index;
        }

        @Override // com.mxtech.media.IMediaInfo
        public void close() {
        }

        @Override // com.mxtech.media.IStreamInfo
        public boolean isValid() {
            int type = type();
            return type == 0 ? width() > 0 : type != 1 || channelCount() > 0;
        }

        private String getStreamMetadata(int key) {
            return FFPlayer.this.getStreamMetadata(this.index, key, LocaleUtils.getIOS3DefaultLanguage());
        }

        @Override // com.mxtech.media.IMediaInfo
        public String description() {
            return getStreamMetadata(103);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String album() {
            return getStreamMetadata(1);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String artist() {
            return getStreamMetadata(2);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String composer() {
            return getStreamMetadata(4);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String genre() {
            return getStreamMetadata(6);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String title() {
            return getStreamMetadata(7);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String mimeType() {
            return getStreamMetadata(12);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String albumArtist() {
            return getStreamMetadata(13);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String copyright() {
            return getStreamMetadata(14);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoder() {
            return getStreamMetadata(15);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoded_by() {
            return getStreamMetadata(16);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String performer() {
            return getStreamMetadata(17);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String publisher() {
            return getStreamMetadata(18);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String year() {
            return getStreamMetadata(5);
        }

        @Override // com.mxtech.media.IMediaInfo
        public Locale[] locales() {
            String langs = getStreamMetadata(102);
            return (langs == null || langs.length() == 0 || LocaleUtils.UNDEFINED.equalsIgnoreCase(langs)) ? new Locale[0] : LocaleUtils.parseLocales(langs);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String displayLocales() {
            return FFReader.toDisplayLocales(locales());
        }

        @Override // com.mxtech.media.IMediaInfo
        public int duration() {
            return FFPlayer.this.duration();
        }

        @Override // com.mxtech.media.IMediaInfo
        public int width() {
            return FFPlayer.this._rot90 ? FFPlayer.this.getStreamHeight(this.index) : FFPlayer.this.getStreamWidth(this.index);
        }

        @Override // com.mxtech.media.IMediaInfo
        public int height() {
            return FFPlayer.this._rot90 ? FFPlayer.this.getStreamWidth(this.index) : FFPlayer.this.getStreamHeight(this.index);
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayWidth() {
            return FFPlayer.this._rot90 ? FFPlayer.this.getStreamDisplayHeight(this.index) : FFPlayer.this.getStreamDisplayWidth(this.index);
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayHeight() {
            return FFPlayer.this._rot90 ? FFPlayer.this.getStreamDisplayWidth(this.index) : FFPlayer.this.getStreamDisplayHeight(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int type() {
            return FFPlayer.this.getStreamType(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int disposition() {
            return FFPlayer.this.getStreamDisposition(this.index);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String format() {
            return FFPlayer.this.getStreamCodec(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public String profile() {
            return FFPlayer.this.getStreamProfile(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int frameTime() {
            return FFPlayer.this.getStreamFrameTime(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int bitRate() {
            return FFPlayer.this.getStreamBitRate(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int sampleRate() {
            return FFPlayer.this.getStreamSampleRate(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public long channelLayout() {
            return FFPlayer.this.getStreamChannelLayout(this.index);
        }

        @Override // com.mxtech.media.IStreamInfo
        public int channelCount() {
            return FFPlayer.this.getStreamChannelCount(this.index);
        }
    }

    public static int getSubtitleCodecIndex(int codecId) {
        return getSubtitleCodecIndex(codecId, 24);
    }

    /* loaded from: classes2.dex */
    public class SubTrack implements ISubtitle {
        static final String SSP = ".";
        public static final String TYPENAME = "Inbound";
        private final boolean _best;
        private boolean _enabled;
        private final int _flags;
        private final boolean _isWebVTT;
        private final Locale _locale;
        private final String _name;
        private final long _nativeTrack;
        private final int _outputType;
        private final int _stream;
        private final Uri _uri;

        SubTrack(int stream, long nativeTrack, int outputType, boolean best) {
            FFPlayer.access$1504(FFPlayer.this);
            int codecIndex = FFPlayer.getSubtitleCodecIndex(FFPlayer.this.getStreamCodecId(stream));
            this._isWebVTT = codecIndex == 17;
            this._outputType = outputType;
            this._best = best;
            if (outputType == 3) {
                FFPlayer.this._subClient.getSubStationAlphaMedia(2, FFPlayer.this);
            }
            int flags = 65536;
            switch (this._outputType) {
                case 1:
                    flags = 65536 | ISubtitle.CONTENT_FRAME;
                    break;
                case 2:
                    flags = 65536 | 2097152;
                    break;
                case 3:
                    flags = 65536 | 5242880;
                    break;
            }
            if (codecIndex != 1 && codecIndex != 7) {
                flags |= 131072;
            }
            this._flags = flags;
            this._stream = stream;
            this._nativeTrack = nativeTrack;
            String ios3lang = LocaleUtils.getIOS3DefaultLanguage();
            String lang = FFPlayer.this.getStreamMetadata(stream, 102, ios3lang);
            String name = FFPlayer.this.getStreamMetadata(stream, 7, ios3lang);
            if (lang == null || lang.equalsIgnoreCase(LocaleUtils.UNDEFINED)) {
                this._locale = null;
            } else {
                int firstComma = lang.indexOf(44);
                this._locale = LocaleUtils.parse(firstComma > 0 ? lang.substring(0, firstComma).trim() : lang);
            }
            if ((name == null || name.length() == 0 || name.equalsIgnoreCase("unknown")) && (this._locale == null || (name = this._locale.getDisplayName()) == null || name.length() <= 0)) {
                name = StringUtils.getString_s(R.string.name_by_track, Integer.valueOf(FFPlayer.this._subTrackSequence));
            }
            this._name = name;
            this._uri = Uri.fromParts(FFPlayer.SUB_SCHEMA, SSP, Integer.toString(stream));
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public void close() {
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public String typename() {
            return TYPENAME;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public int flags() {
            return this._flags;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public int priority() {
            if (this._best) {
                return 6;
            }
            switch (this._outputType) {
                case 1:
                    return 4;
                case 2:
                    return 3;
                case 3:
                    return 5;
                default:
                    return 1;
            }
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public boolean isSupported() {
            return FFPlayer.this.isSupportedSubtitleTrack(this._nativeTrack);
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public boolean isRenderingComplex() {
            return false;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public Uri uri() {
            return this._uri;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public String name() {
            return this._name;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public Locale locale() {
            return this._locale;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public void setTranslation(int sync, double speed) {
            FFPlayer.this.setSubtitleTranslation(sync, speed);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isEnabled() {
            return this._enabled;
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public void enable(boolean enable) {
            this._enabled = enable;
            if (enable && this._outputType == 3) {
                FFPlayer.this._subClient.setupFonts(false);
            }
            try {
                FFPlayer.this.enableSubtitleTrack(this._stream, enable);
                if (FFPlayer.this._onSubtitleEnabledListener != null) {
                    FFPlayer.this._onSubtitleEnabledListener.onSubtitleEnabled(FFPlayer.this, this, enable);
                }
            } catch (IllegalStateException e) {
                Log.w(TAG, "", e);
            }
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public boolean update(int pos) {
            return FFPlayer.this.updateSubtitle(this._nativeTrack, pos);
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public int previous() {
            return FFPlayer.this.previousSubtitle(this._nativeTrack);
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public int next() {
            return FFPlayer.this.nextSubtitle(this._nativeTrack);
        }

        private CharSequence stylizeText(String text, int flags) {
            return this._isWebVTT ? WebVTTSubtitleBasic.stylizeText(text, flags) : SubRipSubtitle.stylizeText(text, flags);
        }

        @Override // com.mxtech.subtitle.ISubtitle
        public Object content(int flags) {
            Object content = FFPlayer.this.getSubtitleFrames(this._nativeTrack);
            if (content != null && !(content instanceof Frame)) {
                if (content instanceof String) {
                    return stylizeText((String) content, flags);
                }
                Object[] frames = (Object[]) content;
                int i = 0;
                for (Object frame : frames) {
                    if (frame instanceof String) {
                        frames[i] = stylizeText((String) frame, flags);
                    }
                    i++;
                }
                return frames;
            }
            return content;
        }
    }

    /* loaded from: classes2.dex */
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

    /* JADX INFO: Access modifiers changed from: private */
    public void setSubtitleTranslation(int sync, double speed) {
        if (sync != this._subtitleSync || speed != this._subtitleSpeed) {
            this._subtitleSync = sync;
            this._subtitleSpeed = speed;
            setSubtitleTranslation_(sync, speed);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        boolean z = false;
        switch (msg.what) {
            case 1:
                if (this._listener != null) {
                    this._listener.onPrepared(this);
                    break;
                }
                break;
            case 2:
                this._playing = false;
                stayAwake(false);
                if (this._listener != null) {
                    this._listener.onCompletion(this);
                    break;
                }
                break;
            case 3:
                if (this._listener != null) {
                    this._listener.onBufferingUpdate(this, msg.arg1);
                    break;
                }
                break;
            case 4:
                this._seekTarget = -1;
                if (this._listener != null) {
                    this._listener.onSeekComplete(this);
                    break;
                }
                break;
            case 5:
                this._rotationDegrees = msg.arg2;
                if (this._rotationDegrees == 90 || this._rotationDegrees == 270) {
                    z = true;
                }
                this._rot90 = z;
                if (this._listener != null) {
                    int width = msg.arg1 >> 16;
                    int height = msg.arg1 & 65535;
                    if (this._rot90) {
                        width = height;
                        height = width;
                    }
                    this._listener.onVideoSizeChanged(this, width, height);
                    break;
                }
                break;
            case 6:
                if (this._listener != null) {
                    this._listener.onAudioStreamChanged(this, msg.arg1);
                    break;
                }
                break;
            case 10:
                if (this._subTracks == null) {
                    this._subTracks = new ArrayList();
                }
                SubtitleTrackContext info = (SubtitleTrackContext) msg.obj;
                ISubtitle sub = new SubTrack(info.streamIndex, info.nativeContext, info.outputType, info.best);
                this._subTracks.add(sub);
                if (this._listener != null) {
                    this._listener.onSubtitleAdded(this, sub);
                    break;
                }
                break;
            case 11:
                this._subClient.onSubtitleInvalidated();
                break;
            case 20:
                if (this._listener != null) {
                    this._listener.onCoverArtChanged(this);
                    break;
                }
                break;
            case 100:
                Log.e(TAG, "Error (" + msg.arg1 + "," + msg.arg2 + ")");
                this._playing = false;
                if (this._listener != null && !this._listener.onError(this, msg.arg1, msg.arg2)) {
                    this._listener.onCompletion(this);
                }
                stayAwake(false);
                break;
            case 200:
                Log.i(TAG, "Info (" + msg.arg1 + "," + msg.arg2 + ")");
                if (this._listener != null) {
                    this._listener.onInfo(this, msg.arg1, msg.arg2);
                    break;
                }
                break;
            case MEDIA_VIDEO_DEVICE_CHANGED /* 299 */:
                if (this._listener != null) {
                    this._listener.onVideoDeviceChanged(this);
                    break;
                }
                break;
            default:
                Log.e(TAG, "Unknown message type " + msg.what);
                break;
        }
        return true;
    }

    /* loaded from: classes2.dex */
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

    private void postEvent(int what, int arg1, int arg2, Object obj) {
        if ((what != 11 && what != 20) || !this._eventHandler.hasMessages(what)) {
            this._eventHandler.sendMessage(this._eventHandler.obtainMessage(what, arg1, arg2, obj));
        }
    }

    @SuppressLint({"InlinedApi"})
    private int getProperty(int property) {
        switch (property) {
            case 1:
                if (Build.VERSION.SDK_INT >= 17) {
                    return getPrimitiveAudioProperty("android.media.property.OUTPUT_SAMPLE_RATE");
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= 17) {
                    return getPrimitiveAudioProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
                }
                break;
        }
        return 0;
    }

    @TargetApi(17)
    private int getPrimitiveAudioProperty(String property) {
        AudioManager am = (AudioManager) App.context.getSystemService("audio");
        String value = am.getProperty(property);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "", e);
            }
        }
        return 0;
    }
}
