package com.mxtech.media;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import com.mxtech.LocaleUtils;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.preference.P;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public final class BuiltinPlayer implements IMediaPlayer, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener {
    public static final int SHOW_HW_SUB = 1;
    public static final String TAG = App.TAG + ".Builtin";
    private static Method _setSubGate;
    private final Context _context;
    private boolean _everStarted;
    private boolean _hasDisplay;
    private final Map<String, String> _headers;
    private IMediaPlayer.Listener _listener;
    private boolean _muted;
    private PlaybackParams _playbackParams;
    private int _positionCalibration;
    private boolean _prepared;
    private int _processing;
    private final Uri _uri;
    private final MediaPlayer _mp = new MediaPlayer();
    private int _muted_stream_index = -1;
    private float _volumeLeft = 1.0f;
    private float _volumeRight = 1.0f;
    private double _speed = 1.0d;
    private int _selectedAudioTrackIndex = -3;

    static {
        if (Build.VERSION.SDK_INT >= 9) {
            try {
                _setSubGate = MediaPlayer.class.getDeclaredMethod("setSubGate", Boolean.TYPE);
            } catch (Exception e) {
            }
        }
    }

    public BuiltinPlayer(Context context, IMediaPlayer.Listener listener, Uri uri, Map<String, String> headers, int flags) {
        this._listener = listener;
        this._context = context;
        this._uri = uri;
        this._headers = headers;
        if ((flags & 1) == 0 && _setSubGate != null) {
            try {
                _setSubGate.invoke(this._mp, false);
            } catch (Exception e) {
            }
        }
    }

    public static boolean isHardSubtitleSupported() {
        return _setSubGate != null;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setListener(IMediaPlayer.Listener listener) {
        this._listener = listener;
    }

    public void calibratePlaybackPosition(int millis) {
        Log.d(TAG, "Calibrating " + millis + "ms");
        this._positionCalibration = millis;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public IMediaInfo info() {
        return new BuiltinMovie(this._uri) { // from class: com.mxtech.media.BuiltinPlayer.1
            @Override // com.mxtech.media.BuiltinMovie, com.mxtech.media.IMediaInfo
            public int duration() {
                return BuiltinPlayer.this.duration();
            }

            @Override // com.mxtech.media.BuiltinMovie, com.mxtech.media.IMediaInfo
            public int height() {
                return BuiltinPlayer.this.height();
            }

            @Override // com.mxtech.media.BuiltinMovie, com.mxtech.media.IMediaInfo
            public int width() {
                return BuiltinPlayer.this.width();
            }
        };
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int duration() {
        int duration = this._mp.getDuration();
        if (duration <= 0) {
            return 0;
        }
        return duration;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int height() {
        return this._mp.getVideoHeight();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int width() {
        return this._mp.getVideoWidth();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCurrentPosition() {
        int pos = this._mp.getCurrentPosition();
        if (this._positionCalibration != 0 && pos != 0) {
            int pos2 = pos + this._positionCalibration;
            if (pos2 < 0) {
                return 0;
            }
            return pos2;
        }
        return pos;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean isPrepared() {
        return this._prepared;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean isPlaying() {
        return this._mp.isPlaying();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public double getSpeed() {
        if (Build.VERSION.SDK_INT >= 23) {
            return this._speed;
        }
        return 1.0d;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setSpeed(double speed) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                if (this._playbackParams == null) {
                    this._playbackParams = new PlaybackParams();
                }
                this._speed = speed;
                this._playbackParams.setSpeed((float) speed);
                this._mp.setPlaybackParams(this._playbackParams);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setProcessing(int processing) {
        this._processing = processing;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getProcessing() {
        return this._processing;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void pause() {
        if (this._everStarted) {
            this._mp.pause();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    @SuppressLint({"NewApi"})
    public void prepareAsync() throws Exception {
        this._mp.setOnPreparedListener(this);
        this._mp.setOnVideoSizeChangedListener(this);
        this._mp.setOnErrorListener(this);
        this._mp.setOnInfoListener(this);
        this._mp.setOnCompletionListener(this);
        this._mp.setOnSeekCompleteListener(this);
        this._mp.setOnBufferingUpdateListener(this);
        if (this._headers != null && Build.VERSION.SDK_INT > 7) {
            try {
                this._mp.setDataSource(this._context, this._uri, this._headers);
            } catch (Throwable th) {
                Log.e(TAG, "Cannot pass header to the media player.");
                this._mp.setDataSource(this._context, this._uri);
            }
        } else {
            this._mp.setDataSource(this._context, this._uri);
        }
        this._mp.prepareAsync();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void close() {
        Log.v(TAG, "=== Begin closing built-in player");
        this._mp.release();
        Log.v(TAG, "=== End closing built-in player");
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCharacteristics() {
        int chars = 0;
        if (P.hwAudioTrackSelectable) {
            chars = 0 | 1;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return chars | 8;
        }
        return chars;
    }

    @TargetApi(16)
    static String getTrackTypeName(int type) {
        switch (type) {
            case 1:
                return "Video";
            case 2:
                return "Audio";
            case 3:
                return "TimedText";
            case 4:
                return "Subtitle";
            case 5:
                return "Metadata";
            default:
                return "Unknown";
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void seekTo(int pos, int timeout) {
        if (this._positionCalibration != 0 && pos != 0 && (pos = pos - this._positionCalibration) < 0) {
            pos = 0;
        }
        this._mp.seekTo(pos);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setAudioStreamType(int streamType) {
        this._mp.setAudioStreamType(streamType);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean hasDisplay() {
        return this._hasDisplay;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setDisplay(SurfaceHolder sh) {
        this._mp.setDisplay(sh);
        this._hasDisplay = sh != null;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean hasVideoTrack() {
        MediaPlayer.TrackInfo[] trackInfo;
        if (width() > 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                for (MediaPlayer.TrackInfo track : this._mp.getTrackInfo()) {
                    if (track.getTrackType() == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "", e);
            }
        }
        return false;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setScreenOnWhilePlaying(boolean screenOn) {
        this._mp.setScreenOnWhilePlaying(screenOn);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setVolume(float left, float right) {
        this._volumeLeft = left;
        this._volumeRight = right;
        if (!this._muted) {
            this._mp.setVolume(left, right);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setVolumeModifier(float modifier) {
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void start() {
        this._mp.start();
        this._everStarted = true;
    }

    @Override // com.mxtech.media.IMediaPlayer
    @SuppressLint({"NewApi"})
    public int getDefaultAudioStream() {
        MediaPlayer.TrackInfo[] trackInfo;
        if (Build.VERSION.SDK_INT >= 16) {
            int i = 0;
            try {
                for (MediaPlayer.TrackInfo track : this._mp.getTrackInfo()) {
                    if (track.getTrackType() != 2) {
                        i++;
                    } else {
                        return i;
                    }
                }
                return -1;
            } catch (Exception e) {
                Log.w(TAG, "", e);
            }
        }
        return -3;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getAudioStream() {
        if (this._muted) {
            return -1;
        }
        if (this._selectedAudioTrackIndex == -3) {
            this._selectedAudioTrackIndex = getDefaultAudioStream();
        }
        return this._selectedAudioTrackIndex;
    }

    @Override // com.mxtech.media.IMediaPlayer
    @SuppressLint({"NewApi"})
    public int changeAudioStream(int index, int flags) {
        boolean unmuting = this._muted_stream_index == index;
        unmute();
        if (unmuting) {
            return 0;
        }
        if (P.hwAudioTrackSelectable) {
            if (this._everStarted) {
                this._selectedAudioTrackIndex = index;
                return -4;
            }
            try {
                this._mp.selectTrack(index);
                this._selectedAudioTrackIndex = index;
                return 0;
            } catch (Exception e) {
            }
        }
        return -3;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setAudioOffset(int millis) {
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void reconfigAudioDevice() {
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean removeAudioStream(int current_stream_index) {
        this._mp.setVolume(0.0f, 0.0f);
        this._muted = true;
        this._muted_stream_index = current_stream_index;
        return true;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getAudioChannelCount(int index) {
        return 0;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setStereoMode(int stereoMode) {
    }

    public int getMutedAudioStream() {
        return this._muted_stream_index;
    }

    private void unmute() {
        this._mp.setVolume(this._volumeLeft, this._volumeRight);
        this._muted = false;
        this._muted_stream_index = -1;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public Bitmap[] getCovers() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int frameTime() {
        return 0;
    }

    @Override // com.mxtech.media.IMediaInfoAux
    @SuppressLint({"NewApi"})
    public boolean hasEmbeddedSubtitle() {
        MediaPlayer.TrackInfo[] trackInfo;
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                for (MediaPlayer.TrackInfo track : this._mp.getTrackInfo()) {
                    if (track.getTrackType() == 3) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                Log.w(TAG, "", e);
                return false;
            }
        }
        return false;
    }

    @Override // com.mxtech.media.IMediaInfoAux
    @SuppressLint({"NewApi"})
    public int getStreamCount() {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                return this._mp.getTrackInfo().length;
            } catch (Exception e) {
                Log.w(TAG, "", e);
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaPlayer.TrackInfo[] getTrackInfo() {
        try {
            MediaPlayer.TrackInfo[] tracks = this._mp.getTrackInfo();
            if (tracks == null) {
                Log.w(TAG, "Track info returned 'null'.");
                return new MediaPlayer.TrackInfo[0];
            }
            return tracks;
        } catch (Throwable e) {
            Log.w(TAG, "", e);
            return new MediaPlayer.TrackInfo[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int trackTypeToAVType(int type) {
        switch (type) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 3;
            default:
                return -1;
        }
    }

    @Override // com.mxtech.media.IMediaInfoAux
    @SuppressLint({"NewApi"})
    public int[] getStreamTypes() {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                MediaPlayer.TrackInfo[] tracks = this._mp.getTrackInfo();
                int[] avtypes = new int[tracks.length];
                int length = tracks.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    MediaPlayer.TrackInfo track = tracks[i];
                    int i3 = i2 + 1;
                    avtypes[i2] = trackTypeToAVType(track.getTrackType());
                    i++;
                    i2 = i3;
                }
                return avtypes;
            } catch (Exception e) {
                Log.w(TAG, "", e);
            }
        }
        return new int[0];
    }

    @Override // com.mxtech.media.IMediaInfoAux
    @SuppressLint({"NewApi"})
    public IStreamInfo streamInfo(int streamIndex) {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                MediaPlayer.TrackInfo[] tracks = this._mp.getTrackInfo();
                if (streamIndex < tracks.length) {
                    return new TrackStreamInfo(tracks[streamIndex]);
                }
            } catch (Exception e) {
                Log.w(TAG, "", e);
            }
        }
        return new NullStreamInfo();
    }

    /* loaded from: classes2.dex */
    static class TrackStreamInfo implements IStreamInfo {
        private final MediaPlayer.TrackInfo _track;

        TrackStreamInfo(MediaPlayer.TrackInfo track) {
            this._track = track;
        }

        @Override // com.mxtech.media.IMediaInfo
        public void close() {
        }

        @Override // com.mxtech.media.IStreamInfo
        public boolean isValid() {
            return true;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String description() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String album() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String artist() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String composer() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String genre() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String title() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String mimeType() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String albumArtist() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String copyright() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoder() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoded_by() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String performer() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String publisher() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String year() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        @SuppressLint({"NewApi"})
        public Locale[] locales() {
            String lang = this._track.getLanguage();
            if (LocaleUtils.UNDEFINED.equalsIgnoreCase(lang)) {
                return new Locale[0];
            }
            Locale locale = LocaleUtils.parse(lang);
            return locale.getLanguage().length() == 0 ? new Locale[0] : new Locale[]{locale};
        }

        @Override // com.mxtech.media.IMediaInfo
        @SuppressLint({"NewApi"})
        public String displayLocales() {
            String lang = this._track.getLanguage();
            return LocaleUtils.UNDEFINED.equalsIgnoreCase(lang) ? "" : LocaleUtils.parse(lang).getDisplayLanguage();
        }

        @Override // com.mxtech.media.IMediaInfo
        public int duration() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int width() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int height() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayWidth() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayHeight() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        @SuppressLint({"NewApi"})
        public int type() {
            switch (this._track.getTrackType()) {
                case 1:
                    return 0;
                case 2:
                    return 1;
                case 3:
                    return 3;
                default:
                    return -1;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int disposition() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String format() {
            return null;
        }

        @Override // com.mxtech.media.IStreamInfo
        public String profile() {
            return null;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int frameTime() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int bitRate() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int sampleRate() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public long channelLayout() {
            return 0L;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int channelCount() {
            return 0;
        }
    }

    /* loaded from: classes2.dex */
    static class NullStreamInfo implements IStreamInfo {
        NullStreamInfo() {
        }

        @Override // com.mxtech.media.IMediaInfo
        public void close() {
        }

        @Override // com.mxtech.media.IStreamInfo
        public boolean isValid() {
            return false;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String description() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String album() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String artist() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String composer() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String genre() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String title() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String mimeType() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String albumArtist() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String copyright() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoder() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoded_by() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String performer() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String publisher() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String year() {
            return null;
        }

        @Override // com.mxtech.media.IMediaInfo
        public Locale[] locales() {
            return new Locale[0];
        }

        @Override // com.mxtech.media.IMediaInfo
        public String displayLocales() {
            return "";
        }

        @Override // com.mxtech.media.IMediaInfo
        public int duration() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int width() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int height() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayWidth() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayHeight() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int type() {
            return -1;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int disposition() {
            return 0;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String format() {
            return null;
        }

        @Override // com.mxtech.media.IStreamInfo
        public String profile() {
            return null;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int frameTime() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int bitRate() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int sampleRate() {
            return 0;
        }

        @Override // com.mxtech.media.IStreamInfo
        public long channelLayout() {
            return 0L;
        }

        @Override // com.mxtech.media.IStreamInfo
        public int channelCount() {
            return 0;
        }
    }

    @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (this._listener != null) {
            this._listener.onVideoSizeChanged(this, width, height);
        }
    }

    @Override // android.media.MediaPlayer.OnSeekCompleteListener
    public void onSeekComplete(MediaPlayer mp) {
        if (this._listener != null) {
            this._listener.onSeekComplete(this);
        }
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    @SuppressLint({"NewApi"})
    public void onPrepared(MediaPlayer mp) {
        this._prepared = true;
        if (this._listener != null) {
            this._listener.onPrepared(this);
        }
    }

    @Override // android.media.MediaPlayer.OnInfoListener
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (this._listener == null) {
            return false;
        }
        return this._listener.onInfo(this, what, extra);
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (this._listener == null) {
            return false;
        }
        return this._listener.onError(this, what, extra);
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mp) {
        if (this._listener != null) {
            this._listener.onCompletion(this);
        }
    }

    @Override // android.media.MediaPlayer.OnBufferingUpdateListener
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (this._listener != null) {
            this._listener.onBufferingUpdate(this, percent);
        }
    }
}
