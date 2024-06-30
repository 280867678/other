package com.mxtech.subtitle;

import android.net.Uri;
import android.util.Log;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.subtitle.ISubtitleClient;
import com.mxtech.subtitle.TextSubtitle;
import java.util.Locale;

/* loaded from: classes2.dex */
public class FFSubtitle implements ISubtitle, IMediaPlayer.Listener, ISubtitleClient.IMediaListener {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_PLAYBACK_COMPLETED = 6;
    private static final int STATE_PLAYING = 4;
    private static final int STATE_PREPARED = 3;
    public static final int STATE_PREPARING = 2;
    public static final String TAG = ISubtitle.TAG + ".FF";
    private final ISubtitleClient _client;
    private final int _defaultFlags;
    private final boolean _defaultIsRenderingComplex;
    private final int _defaultPriority;
    private boolean _enabled;
    private final FFPlayer _ff;
    private final Locale _locale;
    private final String _name;
    private int _state;
    private ISubtitle _subtitle;
    private final String _typename;
    private final Uri _uri;

    public FFSubtitle(Uri uri, String type, String name, String text, ISubtitleClient client) throws Exception {
        this._state = 0;
        this._ff = new FFPlayer(this, 0, false, client);
        try {
            this._ff.setDataSource(uri, text);
            this._ff.prepareAsync();
            this._state = 2;
            this._typename = type;
            this._uri = uri;
            this._enabled = false;
            this._client = client;
            TextSubtitle.AnalyzeResult analyzed = TextSubtitle.analyzeName(uri, client.mediaUri().getLastPathSegment());
            this._name = name == null ? analyzed.name : name;
            this._locale = analyzed.locale;
            this._defaultFlags = 4325376;
            this._defaultPriority = 4;
            this._defaultIsRenderingComplex = false;
            client.registerMediaListener(this);
        } catch (Exception e) {
            this._ff.close();
            throw e;
        }
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void close() {
        this._client.unregisterMediaListener(this);
        if (this._subtitle != null) {
            this._subtitle.close();
        }
        this._ff.close();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return this._typename;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int flags() {
        return this._subtitle != null ? this._subtitle.flags() | 131072 : this._defaultFlags;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int priority() {
        return this._subtitle != null ? this._subtitle.priority() : this._defaultPriority;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean isSupported() {
        if (this._state == -1) {
            return false;
        }
        if (this._subtitle != null) {
            return this._subtitle.isSupported();
        }
        return true;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean isRenderingComplex() {
        return this._subtitle != null ? this._subtitle.isRenderingComplex() : this._defaultIsRenderingComplex;
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
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void enable(boolean enabled) {
        this._enabled = enabled;
        if (this._subtitle != null) {
            this._ff.setInformativeVideoSize(this._client.mediaWidth(), this._client.mediaHeight());
            this._subtitle.enable(enabled);
        }
        updatePlaybackState();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean update(int pos) {
        if (this._subtitle == null || this._state < 3) {
            return false;
        }
        this._ff.updateClock(pos);
        return this._subtitle.update(pos);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int previous() {
        if (this._subtitle != null) {
            return this._subtitle.previous();
        }
        return -1;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int next() {
        if (this._subtitle != null) {
            return this._subtitle.next();
        }
        return Integer.MAX_VALUE;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public Object content(int flags) {
        if (this._subtitle == null || this._state == -1) {
            return null;
        }
        return this._subtitle.content(flags);
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSeekComplete(IMediaPlayer mp) {
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onPrepared(IMediaPlayer mp) {
        this._state = 3;
        updatePlaybackState();
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        this._state = -1;
        return true;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoDeviceChanged(IMediaPlayer mp) {
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCompletion(IMediaPlayer mp) {
        this._state = 6;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSubtitleAdded(IMediaPlayer mp, ISubtitle subtitle) {
        if (this._subtitle != null) {
            Log.w(TAG, "Does not support multiple subtitles as of yet.");
            subtitle.close();
            return;
        }
        this._subtitle = subtitle;
        enable(this._enabled);
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCoverArtChanged(IMediaPlayer mp) {
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onAudioStreamChanged(IMediaPlayer mp, int streamIndex) {
    }

    private void updatePlaybackState() {
        if (this._state >= 3 && this._subtitle != null) {
            if (this._enabled && this._client.isPlaying()) {
                if (this._state != 4) {
                    this._ff.start();
                    this._state = 4;
                }
            } else if (this._state != 5) {
                this._ff.pause();
                this._state = 5;
            }
        }
    }

    @Override // com.mxtech.subtitle.ISubtitleClient.IMediaListener
    public void onMediaSeekTo(int pos, int timeout) {
        this._ff.seekTo(pos, timeout);
    }

    @Override // com.mxtech.subtitle.ISubtitleClient.IMediaListener
    public void onMediaPlay() {
        updatePlaybackState();
    }

    @Override // com.mxtech.subtitle.ISubtitleClient.IMediaListener
    public void onMediaPause() {
        updatePlaybackState();
    }

    @Override // com.mxtech.subtitle.ISubtitleClient.IMediaListener
    public void onMediaPlaybackCompleted() {
        updatePlaybackState();
    }

    @Override // com.mxtech.subtitle.ISubtitleClient.IMediaListener
    public void onMediaClosed() {
        updatePlaybackState();
    }
}
