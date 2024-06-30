package com.mxtech.media;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;

import com.mxtech.subtitle.ISubtitle;

/* loaded from: classes.dex */
public interface IMediaPlayer extends IMediaInfoAux {
    public static final int AVMEDIA_TYPE_ATTACHMENT = 4;
    public static final int AVMEDIA_TYPE_AUDIO = 1;
    public static final int AVMEDIA_TYPE_DATA = 2;
    public static final int AVMEDIA_TYPE_SUBTITLE = 3;
    public static final int AVMEDIA_TYPE_UNKNOWN = -1;
    public static final int AVMEDIA_TYPE_VIDEO = 0;
    public static final int AV_DISPOSITION_ATTACHED_PIC = 1024;
    public static final int AV_DISPOSITION_CAPTIONS = 65536;
    public static final int AV_DISPOSITION_CLEAN_EFFECTS = 512;
    public static final int AV_DISPOSITION_COMMENT = 8;
    public static final int AV_DISPOSITION_DEFAULT = 1;
    public static final int AV_DISPOSITION_DESCRIPTIONS = 131072;
    public static final int AV_DISPOSITION_DUB = 2;
    public static final int AV_DISPOSITION_FORCED = 64;
    public static final int AV_DISPOSITION_HEARING_IMPAIRED = 128;
    public static final int AV_DISPOSITION_KARAOKE = 32;
    public static final int AV_DISPOSITION_LYRICS = 16;
    public static final int AV_DISPOSITION_METADATA = 262144;
    public static final int AV_DISPOSITION_ORIGINAL = 4;
    public static final int AV_DISPOSITION_VISUAL_IMPAIRED = 256;
    public static final int CAN_CHANGE_AUDIO_CHANNEL = 32;
    public static final int CAN_CHANGE_AUDIO_OFFSET = 16;
    public static final int CAN_CHANGE_PLAYBACK_SPEED = 8;
    public static final int CAN_DESELECT_AUDIOTRACK = 2;
    public static final int CAN_MULTIPLY_VOLUME = 4;
    public static final int CAN_SELECT_AUDIOTRACK = 1;
    public static final int COVER_DEFAULT = 0;
    public static final int COVER_LANDSCAPE = 1;
    public static final int EFAIL = -3;
    public static final int ENONE = -1;
    public static final int ERESTART = -4;
    public static final int OK = 0;
    public static final int PROCESS_DEINTERLACE_NONE = 0;
    public static final int PROCESS_DEINTERLACE_W3FDIF = 2;
    public static final int PROCESS_DEINTERLACE_YADIF = 1;
    public static final int PROCESS_MASK_DEINTERLACE = 3;
    public static final int STEREO_MODE_MONO = 1;
    public static final int STEREO_MODE_REVERSE_STEREO = 2;
    public static final int STEREO_MODE_STEREO = 0;
    public static final int TYPE_ATTACHED_PIC = -1000;

    /* loaded from: classes.dex */
    public interface Listener {
        void onAudioStreamChanged(IMediaPlayer iMediaPlayer, int i);

        void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i);

        void onCompletion(IMediaPlayer iMediaPlayer);

        void onCoverArtChanged(IMediaPlayer iMediaPlayer);

        boolean onError(IMediaPlayer iMediaPlayer, int i, int i2);

        boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2);

        void onPrepared(IMediaPlayer iMediaPlayer);

        void onSeekComplete(IMediaPlayer iMediaPlayer);

        void onSubtitleAdded(IMediaPlayer iMediaPlayer, ISubtitle iSubtitle);

        void onVideoDeviceChanged(IMediaPlayer iMediaPlayer);

        void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i2);
    }

    int changeAudioStream(int i, int i2);

    void close();

    int duration();

    int getAudioChannelCount(int i);

    int getAudioStream();

    int getCharacteristics();

    Bitmap[] getCovers() throws OutOfMemoryError;

    int getCurrentPosition();

    int getDefaultAudioStream();

    int getProcessing();

    double getSpeed();

    boolean hasDisplay();

    boolean hasVideoTrack();

    int height();

    IMediaInfo info();

    boolean isPlaying();

    boolean isPrepared();

    void pause();

    void prepareAsync() throws Exception;

    void reconfigAudioDevice();

    boolean removeAudioStream(int i);

    void seekTo(int i, int i2) throws IllegalStateException;

    void setAudioOffset(int i);

    void setAudioStreamType(int i);

    void setDisplay(SurfaceHolder surfaceHolder);

    void setListener(Listener listener);

    void setProcessing(int i);

    void setScreenOnWhilePlaying(boolean z);

    void setSpeed(double d);

    void setStereoMode(int i);

    void setVolume(float f, float f2);

    void setVolumeModifier(float f);

    void start();

    int width();
}
