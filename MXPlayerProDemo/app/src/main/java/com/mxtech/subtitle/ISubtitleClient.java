package com.mxtech.subtitle;

import android.content.Context;
import android.net.Uri;
import com.mxtech.media.FFPlayer;

/* loaded from: classes.dex */
public interface ISubtitleClient {
    public static final int FLAG_DONT_CREATE = 1;
    public static final int FLAG_FORCE_READ_FFMPEG = 2;

    /* loaded from: classes2.dex */
    public interface IMediaListener {
        void onMediaClosed();

        void onMediaPause();

        void onMediaPlay();

        void onMediaPlaybackCompleted();

        void onMediaSeekTo(int i, int i2);
    }

    int frameTime();

    Context getContext();

    SubStationAlphaMedia getSubStationAlphaMedia(int i, FFPlayer fFPlayer);

    boolean isPlaying();

    int mediaDuration();

    int mediaHeight();

    Uri mediaUri();

    int mediaWidth();

    void onSubtitleInvalidated();

    void registerMediaListener(IMediaListener iMediaListener);

    void setupFonts(boolean z);

    void unregisterMediaListener(IMediaListener iMediaListener);
}
