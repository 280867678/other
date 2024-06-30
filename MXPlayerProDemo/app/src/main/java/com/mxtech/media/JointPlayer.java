package com.mxtech.media;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.videoplayer.App;
import java.util.List;

/* loaded from: classes.dex */
public final class JointPlayer implements IMediaPlayer, IMediaPlayer.Listener, Runnable {
    private static final int AUDIO_CHARACTERISTICS = 55;
    public static final int FLAG_DONT_RESTORE_VOLUME = 4096;
    private static final int MISMATCH_THRESHOLD = 5000;
    public static final int MIX_AUDIO = 1;
    public static final int MIX_SUBTITLE = 2;
    public static final int ORIENTATION_LANDSCAPE = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_SQUARE = 2;
    private static final byte PSEUDO_NULL = -1;
    private static final byte PSEUDO_PAUSED = 0;
    private static final byte PSEUDO_PLAYING = 1;
    private static final int SECONDARY_SEEK_TIMEOUT = 10000;
    private static final int SLOWDOWN_THRESHOLD = 3000;
    private static final int SYNC_DELAY_FOR_SMALL_DIFF = 5000;
    private static final int SYNC_TOLERANCE = 40;
    private static final int SYNC_TOLERANCE_LARGE = 150;
    public static final String TAG = App.TAG + ".Joint";
    private int _givenAudioOffset;
    private boolean _ignorePrimaryTracks;
    private final SecondaryListener _l2;
    private Listener _listener;
    private int _mix;
    private final boolean _needTrackIndexTranslation;
    private boolean _notifySeekCompletion;
    private IMediaPlayer _primary;
    private FFPlayer _secondary;
    private long _syncMissBeginTime;
    private final Handler _handler = new Handler();
    private byte _pseudoState = -1;
    private float _userLeftVolume = 1.0f;
    private float _userRightVolume = 1.0f;
    private float _givenVolumeModifier = 1.0f;

    /* loaded from: classes.dex */
    public interface Listener extends IMediaPlayer.Listener {
        void onSecondaryError(FFPlayer fFPlayer, int i, int i2);

        void onSyncSeekBegin();

        void onSyncSeekEnd();
    }

    public JointPlayer(IMediaPlayer primary, FFPlayer secondary, int mix) {
        if (mix != 0 && secondary == null) {
            throw new IllegalArgumentException("Secondary player is not provided while mixing is enabled.");
        }
        Log.i(TAG, "Creating a joint player. 1st=" + primary + " 2nd=" + secondary + " mix=" + mix);
        this._primary = primary;
        this._secondary = secondary;
        this._mix = mix;
        primary.setListener(this);
        this._needTrackIndexTranslation = (primary instanceof BuiltinPlayer) && (primary.getCharacteristics() & 1) != 0;
        if (secondary != null) {
            this._l2 = new SecondaryListener();
            secondary.setListener(this._l2);
            secondary.setOnSubtitleEnabledListener(this._l2);
        } else {
            this._l2 = null;
        }
        if ((mix & 1) != 0) {
            this._primary.setVolume(0.0f, 0.0f);
            secondary.forceAudioTimeSync(true);
        } else if (secondary != null) {
            secondary.removeAudioStream();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setListener(IMediaPlayer.Listener listener) {
    }

    public void setListener(Listener listener) {
        this._listener = listener;
    }

    public FFPlayer getFFPlayer() {
        return this._primary instanceof FFPlayer ? (FFPlayer) this._primary : this._secondary;
    }

    public IMediaPlayer getPrimary() {
        return this._primary;
    }

    public FFPlayer getSecondary() {
        return this._secondary;
    }

    public IMediaPlayer getInformativePlayer() {
        return this._secondary != null ? this._secondary : this._primary;
    }

    public int getMixing() {
        return this._mix;
    }

    public void prepareMix(int mix) {
        Log.i(TAG, "Mix <- " + mix);
        if (mix != 0 && this._secondary == null) {
            throw new IllegalArgumentException("Secondary player is not provided while mixing is enabled.");
        }
        boolean changeAudioMix = (this._mix & 1) != (mix & 1);
        this._mix = mix;
        if ((mix & 1) != 0) {
            this._primary.setVolume(0.0f, 0.0f);
            this._secondary.forceAudioTimeSync(true);
        } else {
            this._secondary.forceAudioTimeSync(false);
        }
        if (changeAudioMix) {
            applyAudioSettings();
        }
    }

    public boolean mixAudio(int audioStreamIndex, int flags) {
        boolean result = mix(audioStreamIndex, (this._mix & 2) != 0, flags);
        if (result) {
            applyAudioSettings();
        }
        return result;
    }

    private void applyAudioSettings() {
        setVolumeModifier(this._givenVolumeModifier);
        setAudioOffset(this._givenAudioOffset);
    }

    private boolean mix(int audioStreamIndex, boolean enableSubtitle, int flags) {
        if (this._secondary == null) {
            return false;
        }
        int newMix = (audioStreamIndex >= 0 ? 1 : 0) | (enableSubtitle ? 2 : 0);
        if (this._mix != newMix) {
            this._mix = newMix;
            Log.i(TAG, "Mix Audio=" + audioStreamIndex + " Subtitle=" + enableSubtitle);
            if ((this._mix & 1) != 0) {
                this._primary.setVolume(0.0f, 0.0f);
                this._secondary.forceAudioTimeSync(true);
                this._secondary.changeAudioStream(audioStreamIndex, flags & FFPlayer.FLAG_MASK_AUDIO);
                if ((flags & 4096) == 0) {
                    this._secondary.setVolume(this._userLeftVolume, this._userRightVolume);
                }
            } else {
                this._secondary.forceAudioTimeSync(false);
                this._secondary.removeAudioStream();
                if ((flags & 4096) == 0) {
                    this._primary.setVolume(this._userLeftVolume, this._userRightVolume);
                }
            }
            if (this._mix != 0) {
                doInitialSync();
            } else {
                pause_2();
            }
            return true;
        }
        return false;
    }

    public boolean mixSubtitle(boolean enable) {
        if (this._secondary == null) {
            return false;
        }
        boolean prev = (this._mix & 2) != 0;
        if (prev != enable) {
            Log.v(TAG, "Mix Subtitle=" + enable);
            if (enable) {
                this._mix |= 2;
            } else {
                this._mix &= -3;
            }
            if (this._mix != 0) {
                doInitialSync();
            } else {
                pause_2();
            }
            return true;
        }
        return false;
    }

    public List<ISubtitle> getSubtitles() {
        FFPlayer ff = getFFPlayer();
        if (ff != null) {
            return ff.getSubtitles();
        }
        return null;
    }

    public void sync() {
        if (this._mix != 0 && this._pseudoState == -1 && this._primary.isPlaying() && this._secondary.isPlaying()) {
            doSync();
        }
    }

    private void doInitialSync() {
        int pos1 = this._primary.getCurrentPosition();
        int pos2 = this._secondary.getCurrentPosition();
        if (pos1 == pos2) {
            if (this._pseudoState == -1) {
                if (this._primary.isPlaying()) {
                    start_2();
                    return;
                } else {
                    pause_2();
                    return;
                }
            }
            return;
        }
        Log.d(TAG, "Initial sync 2nd to 1st. delta=" + (pos1 - pos2) + "ms 1=" + pos1 + " 2=" + pos2);
        if (this._pseudoState == -1) {
            setPseudoState(this._primary.isPlaying() ? (byte) 1 : (byte) 0);
        }
        pause_1();
        pause_2();
        if (this._listener != null) {
            this._listener.onSyncSeekBegin();
        }
        this._secondary.seekTo(pos1, 10000);
    }

    private boolean doSync() {
        int pos1 = this._primary.getCurrentPosition();
        if ((this._mix & 1) != 0) {
            int pos2 = this._secondary.getCurrentPosition();
            int delta = pos1 - pos2;
            if (-40 <= delta && delta <= 40) {
                this._syncMissBeginTime = 0L;
                return true;
            }
            if (-3000 < delta && delta < 3000) {
                if (-150 < delta && delta < SYNC_TOLERANCE_LARGE) {
                    long now = SystemClock.uptimeMillis();
                    if (this._syncMissBeginTime == 0) {
                        this._syncMissBeginTime = now;
                        return true;
                    } else if (this._syncMissBeginTime + 5000 > now) {
                        return true;
                    }
                }
                this._syncMissBeginTime = 0L;
                if (delta < 0) {
                    Log.d(TAG, "Pause 2nd for " + (-delta) + "ms 1=" + pos1 + " 2=" + pos2);
                    start_1();
                    pause_2();
                    this._handler.postDelayed(this, -delta);
                } else {
                    Log.d(TAG, "Pause 1st for " + delta + "ms 1=" + pos1 + " 2=" + pos2);
                    start_2();
                    pause_1();
                    this._handler.postDelayed(this, delta);
                }
            } else {
                Log.d(TAG, "Reposition(update) 2nd to sync 1st. delta=" + delta + "ms 1=" + pos1 + " 2=" + pos2);
                pause_1();
                pause_2();
                if (this._listener != null) {
                    this._listener.onSyncSeekBegin();
                }
                this._secondary.seekTo(pos1, 10000);
            }
            setPseudoState((byte) 1);
            return false;
        }
        this._secondary.updateClock(pos1);
        return true;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this._pseudoState == 1) {
            if (this._mix != 0) {
                if (doSync()) {
                    start_2();
                } else {
                    return;
                }
            }
            start_1();
            Log.v(TAG, "Restart 1st/2nd after pausing for synching 1=" + this._primary.getCurrentPosition() + " 2=" + (this._mix != 0 ? this._secondary.getCurrentPosition() : -1));
        } else if (this._pseudoState == 0) {
            if (this._mix != 0) {
                pause_2();
            }
            pause_1();
        }
        setPseudoState((byte) -1);
    }

    public IMediaPlayer detachPrimary() {
        if (this._primary == null) {
            return null;
        }
        this._primary.setListener(null);
        IMediaPlayer iMediaPlayer = this._primary;
        this._primary = null;
        setPseudoState((byte) -1);
        this._mix = 0;
        if (this._secondary != null) {
            this._secondary.forceAudioTimeSync(false);
            return iMediaPlayer;
        }
        return iMediaPlayer;
    }

    public FFPlayer detachSecondary() {
        if (this._secondary == null) {
            return null;
        }
        this._secondary.setListener(null);
        this._secondary.forceAudioTimeSync(false);
        FFPlayer fFPlayer = this._secondary;
        this._secondary = null;
        setPseudoState((byte) -1);
        this._mix = 0;
        return fFPlayer;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void close() {
        try {
            if (this._secondary != null) {
                this._secondary.close();
                this._secondary = null;
            }
            this._handler.removeCallbacksAndMessages(null);
            this._listener = null;
            setPseudoState((byte) -1);
            this._mix = 0;
        } finally {
            if (this._primary != null) {
                this._primary.close();
                this._primary = null;
            }
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCharacteristics() {
        int chars;
        int chars1 = this._primary.getCharacteristics();
        if (this._ignorePrimaryTracks) {
            chars1 &= -56;
        }
        if (this._secondary != null) {
            int chars2 = this._secondary.getCharacteristics();
            int chars3 = (this._mix & 1) != 0 ? 0 | (chars2 & 55) : 0 | (chars1 & 55);
            if (this._mix == 0) {
                chars = chars3 | (chars1 & 8);
            } else {
                chars = chars3 | (chars1 & chars2 & 8);
            }
            return chars;
        }
        return chars1;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int duration() {
        int duration = this._primary.duration();
        if (duration == 0 && this._secondary != null) {
            return this._secondary.duration();
        }
        return duration;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int width() {
        return this._primary.width();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int height() {
        return this._primary.height();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public IMediaInfo info() {
        return this._secondary != null ? this._secondary.info() : this._primary.info();
    }

    public static int getOrientation(IMediaPlayer mp) {
        int w = mp.width();
        int h = mp.height();
        if (w > h) {
            return 0;
        }
        if (w < h) {
            return 1;
        }
        return 2;
    }

    public int displayWidth(boolean correctHW) {
        if (this._primary instanceof FFPlayer) {
            return ((FFPlayer) this._primary).displayWidth();
        }
        int primaryWidth = this._primary.width();
        if (correctHW && this._secondary != null && getOrientation(this._primary) == getOrientation(this._secondary)) {
            int width = this._secondary.displayWidth();
            Log.v(TAG, "Primary width:" + primaryWidth + ", Secondary width:" + width);
            if (width <= 0) {
                return this._secondary.calcDisplayWidth(primaryWidth);
            }
            return width;
        }
        return primaryWidth;
    }

    public int displayHeight(boolean correctHW) {
        if (this._primary instanceof FFPlayer) {
            return ((FFPlayer) this._primary).displayHeight();
        }
        int primaryHeight = this._primary.height();
        if (correctHW && this._secondary != null && getOrientation(this._primary) == getOrientation(this._secondary)) {
            int height = this._secondary.displayHeight();
            Log.v(TAG, "Primary height:" + primaryHeight + ", Secondary height:" + height);
            if (height <= 0) {
                return this._secondary.calcDisplayHeight(primaryHeight);
            }
            return height;
        }
        return primaryHeight;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getCurrentPosition() {
        return this._primary.getCurrentPosition();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean isPrepared() {
        return this._primary.isPrepared() && (this._secondary == null || this._secondary.isPrepared());
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean isPlaying() {
        if (this._pseudoState == -1) {
            return this._primary.isPlaying();
        }
        return this._pseudoState == 1;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public double getSpeed() {
        return this._primary.getSpeed();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setSpeed(double speed) {
        this._primary.setSpeed(speed);
        if (this._secondary != null) {
            this._secondary.setSpeed(this._primary.getSpeed());
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setProcessing(int processing) {
        this._primary.setProcessing(processing);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getProcessing() {
        return this._primary.getProcessing();
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean hasDisplay() {
        return this._primary.hasDisplay();
    }

    @Override // com.mxtech.media.IMediaPlayer
    @Deprecated
    public void setDisplay(SurfaceHolder sh) {
        this._primary.setDisplay(sh);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setScreenOnWhilePlaying(boolean screenOn) {
        this._primary.setScreenOnWhilePlaying(screenOn);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setAudioStreamType(int streamtype) {
        this._primary.setAudioStreamType(streamtype);
        if (this._secondary != null) {
            this._secondary.setAudioStreamType(streamtype);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setVolume(float left, float right) {
        this._userLeftVolume = left;
        this._userRightVolume = right;
        if ((this._mix & 1) != 0) {
            this._secondary.setVolume(left, right);
        } else {
            this._primary.setVolume(left, right);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setVolumeModifier(float modifier) {
        this._givenVolumeModifier = modifier;
        if ((this._mix & 1) != 0) {
            this._secondary.setVolumeModifier(modifier);
        } else {
            this._primary.setVolumeModifier(modifier);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void prepareAsync() throws Exception {
        if (this._secondary != null && !this._secondary.isPrepared()) {
            this._secondary.prepareAsync();
        } else if (!this._primary.isPrepared()) {
            preparePrimary();
        } else {
            this._handler.post(new Runnable() { // from class: com.mxtech.media.JointPlayer.1
                @Override // java.lang.Runnable
                public void run() {
                    JointPlayer.this.doPrepared();
                }
            });
        }
    }

    public boolean needHardwareVideoForceBlock() {
        FFPlayer ff = getFFPlayer();
        if (ff != null) {
            return ff.needHardwareVideoForceBlock();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preparePrimary() throws Exception {
        if ((this._primary instanceof BuiltinPlayer) && needHardwareVideoForceBlock()) {
            Log.w(TAG, "HW decoder is rejected automatically due to danger.");
            this._handler.post(new Runnable() { // from class: com.mxtech.media.JointPlayer.2
                @Override // java.lang.Runnable
                public void run() {
                    if (JointPlayer.this._listener != null) {
                        JointPlayer.this._listener.onError(JointPlayer.this, 1, 0);
                    }
                }
            });
            return;
        }
        this._primary.prepareAsync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void start_1() {
        Log.v(TAG, "Start 1st player. 1st_position=" + this._primary.getCurrentPosition() + " 2nd_position=" + (this._secondary != null ? Integer.toString(this._secondary.getCurrentPosition()) : "null"));
        this._primary.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pause_1() {
        Log.v(TAG, "Pause 1st player. 1st_position=" + this._primary.getCurrentPosition() + " 2nd_position=" + (this._secondary != null ? Integer.toString(this._secondary.getCurrentPosition()) : "null"));
        this._primary.pause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void start_2() {
        Log.v(TAG, "Start 2nd player. 1st_position=" + this._primary.getCurrentPosition() + " 2nd_position=" + (this._secondary != null ? Integer.toString(this._secondary.getCurrentPosition()) : "null"));
        this._secondary.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pause_2() {
        Log.v(TAG, "Pause 2nd player. 1st_position=" + this._primary.getCurrentPosition() + " 2nd_position=" + (this._secondary != null ? Integer.toString(this._secondary.getCurrentPosition()) : "null"));
        this._secondary.pause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPseudoState(byte playing) {
        this._pseudoState = playing;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void start() {
        Log.v(TAG, "Start");
        if (this._pseudoState != -1) {
            setPseudoState((byte) 1);
            return;
        }
        start_1();
        if (this._mix != 0 && !this._notifySeekCompletion) {
            start_2();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void pause() {
        Log.v(TAG, "Pause");
        if (this._pseudoState != -1) {
            setPseudoState((byte) 0);
            return;
        }
        pause_1();
        if (this._mix != 0) {
            pause_2();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void seekTo(int msec, int preciseTimeout) throws IllegalStateException {
        if (this._mix != 0) {
            pause_2();
            this._handler.removeCallbacksAndMessages(null);
            if (this._pseudoState == 1) {
                start_1();
                setPseudoState((byte) -1);
            } else if (this._pseudoState == 0) {
                pause_1();
                setPseudoState((byte) -1);
            }
        }
        this._primary.seekTo(msec, preciseTimeout);
        this._notifySeekCompletion = true;
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int frameTime() {
        return getInformativePlayer().frameTime();
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public boolean hasEmbeddedSubtitle() {
        return getInformativePlayer().hasEmbeddedSubtitle();
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int getStreamCount() {
        return getInformativePlayer().getStreamCount();
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int[] getStreamTypes() {
        return getInformativePlayer().getStreamTypes();
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public IStreamInfo streamInfo(int streamIndex) {
        return getInformativePlayer().streamInfo(streamIndex);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getDefaultAudioStream() {
        return getInformativePlayer().getDefaultAudioStream();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void doPrepared() {
        if (this._primary != null && this._secondary != null && (this._primary.getCharacteristics() & 1) != 0) {
            MediaPlayer.TrackInfo[] tracks = ((BuiltinPlayer) this._primary).getTrackInfo();
            int[] streams = this._secondary.getStreamTypes();
            int audioTrack1 = 0;
            int audioTrack2 = 0;
            int i = 0;
            for (MediaPlayer.TrackInfo t : tracks) {
                if (t != null && t.getTrackType() == 2) {
                    audioTrack1++;
                }
                i++;
            }
            int i2 = 0;
            for (int s : streams) {
                if (s == 1) {
                    audioTrack2++;
                }
                i2++;
            }
            if (audioTrack1 != audioTrack2) {
                this._ignorePrimaryTracks = true;
            }
        }
        if (this._listener != null) {
            this._listener.onPrepared(this);
        }
    }

    @SuppressLint({"NewApi"})
    private int translateToBuiltinIndex(int ff_index) {
        if (this._primary != null && this._secondary != null) {
            MediaPlayer.TrackInfo[] tracks = ((BuiltinPlayer) this._primary).getTrackInfo();
            int[] streams = this._secondary.getStreamTypes();
            if (ff_index < streams.length) {
                int avtype = streams[ff_index];
                int same_type = 0;
                for (int i = 0; i < ff_index; i++) {
                    if (avtype == streams[i]) {
                        same_type++;
                    }
                }
                int i2 = 0;
                for (MediaPlayer.TrackInfo t : tracks) {
                    if (t != null && BuiltinPlayer.trackTypeToAVType(t.getTrackType()) == avtype) {
                        if (same_type == 0) {
                            return i2;
                        }
                        same_type--;
                    }
                    i2++;
                }
            }
        }
        return -3;
    }

    @SuppressLint({"NewApi"})
    private int translateToFFIndex(int builtin_index) {
        MediaPlayer.TrackInfo builtinTrack;
        if (this._primary != null && this._secondary != null) {
            MediaPlayer.TrackInfo[] tracks = ((BuiltinPlayer) this._primary).getTrackInfo();
            int[] streams = this._secondary.getStreamTypes();
            if (builtin_index < tracks.length && (builtinTrack = tracks[builtin_index]) != null) {
                int avtype = BuiltinPlayer.trackTypeToAVType(builtinTrack.getTrackType());
                int same_type = 0;
                for (int i = 0; i < builtin_index; i++) {
                    MediaPlayer.TrackInfo track = tracks[i];
                    if (track != null && avtype == BuiltinPlayer.trackTypeToAVType(track.getTrackType())) {
                        same_type++;
                    }
                }
                int i2 = 0;
                for (int s : streams) {
                    if (s == avtype) {
                        if (same_type == 0) {
                            return i2;
                        }
                        same_type--;
                    }
                    i2++;
                }
            }
        }
        return -3;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getAudioStream() {
        int new_idx;
        if ((this._mix & 1) != 0) {
            return this._secondary.getAudioStream();
        }
        int idx = this._primary.getAudioStream();
        if (idx != -1) {
            if (this._ignorePrimaryTracks) {
                return -3;
            }
            if (this._needTrackIndexTranslation && idx >= 0 && (new_idx = translateToFFIndex(idx)) >= 0) {
                return new_idx;
            }
            return idx;
        }
        return idx;
    }

    public int forceGetAudioStream() {
        int index = getAudioStream();
        if (index == -3) {
            return getDefaultAudioStream();
        }
        return index;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int changeAudioStream(int index, int flags) {
        if ((this._mix & 1) != 0) {
            return this._secondary.changeAudioStream(index, flags);
        }
        if (this._primary instanceof BuiltinPlayer) {
            BuiltinPlayer bp = (BuiltinPlayer) this._primary;
            if (bp.getMutedAudioStream() == index) {
                return bp.changeAudioStream(index, flags);
            }
        }
        if (this._ignorePrimaryTracks) {
            return -3;
        }
        return (!this._needTrackIndexTranslation || (index = translateToBuiltinIndex(index)) >= 0) ? this._primary.changeAudioStream(index, flags) : index;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void reconfigAudioDevice() {
        if (this._primary != null) {
            this._primary.reconfigAudioDevice();
        }
        if (this._secondary != null) {
            this._secondary.reconfigAudioDevice();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setAudioOffset(int millis) {
        if (this._givenAudioOffset != millis) {
            this._givenAudioOffset = millis;
            if ((this._mix & 1) != 0) {
                this._secondary.setAudioOffset(millis);
            } else {
                this._primary.setAudioOffset(millis);
            }
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean removeAudioStream(int current_stream_index) {
        return (this._mix & 1) != 0 ? this._secondary.removeAudioStream() : this._primary.removeAudioStream(current_stream_index);
    }

    private IMediaPlayer getAudioPlayer() {
        return (this._mix & 1) != 0 ? this._secondary : this._primary;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public int getAudioChannelCount(int index) {
        return getInformativePlayer().getAudioChannelCount(index);
    }

    @Override // com.mxtech.media.IMediaPlayer
    public void setStereoMode(int stereoMode) {
        if (this._primary != null) {
            this._primary.setStereoMode(stereoMode);
        }
        if (this._secondary != null) {
            this._secondary.setStereoMode(stereoMode);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer
    public Bitmap[] getCovers() throws OutOfMemoryError {
        Bitmap[] covers = this._primary.getCovers();
        if (covers == null && this._secondary != null) {
            return this._secondary.getCovers();
        }
        return covers;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onPrepared(IMediaPlayer mp) {
        Log.v(TAG, "1st prepared. duration=" + mp.duration() + "ms");
        doPrepared();
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
        Log.v(TAG, "Video size: " + width + " x " + height);
        if (this._listener != null) {
            this._listener.onVideoSizeChanged(this, width, height);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSeekComplete(IMediaPlayer mp) {
        if (this._mix != 0) {
            if (this._pseudoState == -1) {
                setPseudoState(this._primary.isPlaying() ? (byte) 1 : (byte) 0);
            }
            pause_1();
            pause_2();
            this._handler.removeCallbacksAndMessages(null);
            this._secondary.seekTo(this._primary.getCurrentPosition(), 10000);
            return;
        }
        if (this._notifySeekCompletion) {
            this._notifySeekCompletion = false;
            if (this._listener != null) {
                this._listener.onSeekComplete(this);
            }
        }
        this._syncMissBeginTime = 0L;
    }

    @Override // com.mxtech.media.IMediaPlayer
    public boolean hasVideoTrack() {
        if (this._primary.hasVideoTrack()) {
            return true;
        }
        FFPlayer ff = getFFPlayer();
        if (ff != null) {
            return ff.hasVideoTrack();
        }
        return false;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        if (this._listener != null) {
            return this._listener.onInfo(this, what, extra);
        }
        return false;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        Log.e(TAG, "1st error: what=" + what + " extra=" + extra);
        if (this._listener != null) {
            return this._listener.onError(this, what, extra);
        }
        return false;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoDeviceChanged(IMediaPlayer mp) {
        if (this._listener != null) {
            this._listener.onVideoDeviceChanged(this);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCompletion(IMediaPlayer mp) {
        if (this._listener != null) {
            this._listener.onCompletion(this);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        if (this._listener != null) {
            this._listener.onBufferingUpdate(this, percent);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSubtitleAdded(IMediaPlayer mp, ISubtitle subtitle) {
        if (this._listener != null) {
            this._listener.onSubtitleAdded(this, subtitle);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCoverArtChanged(IMediaPlayer mp) {
        if (this._listener != null) {
            this._listener.onCoverArtChanged(this);
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onAudioStreamChanged(IMediaPlayer mp, int streamIndex) {
        if (this._listener != null) {
            this._listener.onAudioStreamChanged(this, streamIndex);
        }
    }

    /* loaded from: classes2.dex */
    private class SecondaryListener implements IMediaPlayer.Listener, Runnable, FFPlayer.OnSubtitleEnabledListener {
        private static final int SEEK_FINISH_CHECK_INTERVAL = 250;

        private SecondaryListener() {
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onAudioStreamChanged(IMediaPlayer mp, int streamIndex) {
            if (JointPlayer.this._listener != null) {
                JointPlayer.this._listener.onAudioStreamChanged(JointPlayer.this, streamIndex);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (JointPlayer.this._mix != 0 && JointPlayer.this._pseudoState != -1) {
                if (JointPlayer.this._pseudoState == 1) {
                    int pos1 = JointPlayer.this._primary.getCurrentPosition();
                    int pos2 = JointPlayer.this._secondary.getCurrentPosition();
                    int delta = pos1 - pos2;
                    if (-5000 > delta || delta > 0) {
                        JointPlayer.this.start_2();
                        if (JointPlayer.this._listener != null) {
                            JointPlayer.this._listener.onSyncSeekEnd();
                        }
                    } else {
                        Log.d(JointPlayer.TAG, "2nd is not started since 1st position is not advanced. 1st=" + JointPlayer.this._primary.getCurrentPosition() + " 2nd=" + JointPlayer.this._secondary.getCurrentPosition());
                        JointPlayer.this._handler.postDelayed(this, 250L);
                        return;
                    }
                } else {
                    JointPlayer.this.pause_1();
                    JointPlayer.this.pause_2();
                }
                JointPlayer.this.setPseudoState((byte) -1);
            }
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onSeekComplete(IMediaPlayer mp) {
            boolean startDelayed = false;
            if (JointPlayer.this._pseudoState != -1) {
                if (JointPlayer.this._pseudoState == 1) {
                    if (JointPlayer.this._secondary != null) {
                        startDelayed = JointPlayer.this._handler.postDelayed(this, 250L);
                    }
                    JointPlayer.this.start_1();
                } else if (JointPlayer.this._pseudoState == 0) {
                    if (JointPlayer.this._secondary != null) {
                        JointPlayer.this.pause_2();
                    }
                    JointPlayer.this.pause_1();
                }
                if (!startDelayed) {
                    JointPlayer.this.setPseudoState((byte) -1);
                }
            }
            if (JointPlayer.this._notifySeekCompletion) {
                JointPlayer.this._notifySeekCompletion = false;
                if (JointPlayer.this._listener != null) {
                    JointPlayer.this._listener.onSeekComplete(JointPlayer.this);
                }
            } else if (!startDelayed && JointPlayer.this._listener != null) {
                JointPlayer.this._listener.onSyncSeekEnd();
            }
            JointPlayer.this._syncMissBeginTime = 0L;
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onPrepared(IMediaPlayer mp) {
            if (JointPlayer.this._primary == null || JointPlayer.this._primary.isPrepared()) {
                JointPlayer.this.doPrepared();
                return;
            }
            try {
                JointPlayer.this.preparePrimary();
            } catch (Exception e) {
                Log.e(JointPlayer.TAG, "", e);
                if (JointPlayer.this._listener != null) {
                    JointPlayer.this._listener.onError(JointPlayer.this, 1, 0);
                }
            }
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            return true;
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            Log.e(JointPlayer.TAG, "2nd(audio) error: what=" + what + " extra=" + extra);
            if (JointPlayer.this._listener != null) {
                JointPlayer.this._listener.onSecondaryError((FFPlayer) mp, what, extra);
            }
            if (JointPlayer.this._secondary != null) {
                JointPlayer.this._secondary.close();
                JointPlayer.this._secondary = null;
                JointPlayer.this._mix = 0;
            }
            if (JointPlayer.this._primary != null) {
                if (JointPlayer.this._primary.isPrepared()) {
                    if (JointPlayer.this._pseudoState == 1) {
                        JointPlayer.this.start_1();
                    } else if (JointPlayer.this._pseudoState == 0) {
                        JointPlayer.this.pause_1();
                    }
                } else {
                    try {
                        JointPlayer.this.preparePrimary();
                    } catch (Exception e) {
                        Log.e(JointPlayer.TAG, "", e);
                        if (JointPlayer.this._listener != null) {
                            JointPlayer.this._listener.onError(JointPlayer.this, 1, 0);
                        }
                    }
                }
            }
            JointPlayer.this.setPseudoState((byte) -1);
            return true;
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onVideoDeviceChanged(IMediaPlayer mp) {
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onCompletion(IMediaPlayer mp) {
            onSeekComplete(mp);
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onSubtitleAdded(IMediaPlayer mp, ISubtitle subtitle) {
            if (JointPlayer.this._listener != null) {
                JointPlayer.this._listener.onSubtitleAdded(JointPlayer.this, subtitle);
            }
        }

        @Override // com.mxtech.media.FFPlayer.OnSubtitleEnabledListener
        public void onSubtitleEnabled(FFPlayer ff, FFPlayer.SubTrack sub, boolean enabled) {
            if (JointPlayer.this._primary != null) {
                if (enabled) {
                    JointPlayer.this.mixSubtitle(true);
                    return;
                }
                for (ISubtitle s : ff.getSubtitles()) {
                    if (((FFPlayer.SubTrack) s).isEnabled()) {
                        return;
                    }
                }
                JointPlayer.this.mixSubtitle(false);
            }
        }

        @Override // com.mxtech.media.IMediaPlayer.Listener
        public void onCoverArtChanged(IMediaPlayer mp) {
            if (JointPlayer.this._listener != null) {
                JointPlayer.this._listener.onCoverArtChanged(JointPlayer.this);
            }
        }
    }
}
