package com.mxtech.videoplayer;

import android.graphics.Bitmap;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mxtech.media.FFPlayer;
import com.mxtech.media.JointPlayer;
import com.mxtech.net.HttpFactory;
import com.mxtech.preference.P;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.SubStationAlphaMedia;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {

    private static final int CLOSEMP_KEEP_BOTH = 3;
    private static final int CLOSEMP_KEEP_PRIMARY = 1;
    private static final int CLOSEMP_KEEP_SECONDARY = 2;
    public static final byte DECODER_FFMPEG_BASED = 6;
    public static final byte DECODER_HARDWARE = 1;
    public static final byte DECODER_OMX = 4;
    public static final byte DECODER_SOFTWARE = 2;
    public static final int FLAG_FOR_RELOADING = 512;
    public static final int FLAG_RESTORE_STATE = 256;
    private static final int HE_FLAG_MANUALLY_REJECTED = 2;
    private static final int HE_FLAG_RECOVERABLE = 1;
    private static final int INITIAL_PERIOD = 5000;
    public static final int LOADING_COVER = 2;
    public static final int LOADING_SUBTITLE = 1;
    public static final int LOADING_SUBTITLE_CONFIDENCE = 200;
    public static final int LOADING_SUBTITLE_EXPLICIT = 100;
    private static final int MAX_RAMPUP_ITERATION = 600;
    private static final boolean PREFER_SW_AUDIO = true;
    private static final int PREPARATION_WAIT_MILLIS = 100;
    private static final float RAMPING_CHANGE_PER_STEP = 0.1f;
    private static final int RAMPING_INTERVAL = 50;
    private static final float RAMPING_SPEED = 2.0f;
    public static final int RAMPUP_NONE = 0;
    public static final int RAMPUP_ON_SEEK = 2;
    public static final int RAMPUP_ON_START = 1;
    public static final int SEEK_TIMEOUT_LONG = 6000;
    private static final int SEEK_TIMEOUT_SHORT = 2500;
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 6;
    public static final int STATE_PLAYING = 5;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_UNLOADED = 1;
    private static final String TAG = "MX.Player";
    private static final int UPDATE_DURATION_AFTER = 2000;
    public static final int UPDATE_INTERVAL = 100;
    private static final float[] VOLUMES_15 = {1.0f, 1.0686392f, 1.1410385f, 1.217404f, 1.2979528f, 1.3829142f, 1.4725299f, 1.5670549f, 1.666758f, 1.771923f, 1.882849f, 1.9998517f, 2.123264f, 2.2534368f, 2.3907409f, 2.5355663f};
    private static final int kMsgMimicCompletion = 2;
    private static final int kMsgRampUp = 3;
    private static final int kMsgUpdate = 1;
    private static final int kMsgUpdateDuration = 4;
//    private BluetoothSpeakerDetector _BTSpeakerDetector;
    private byte _availableDecoders;
    private Client _client;
    private Bitmap[] _covers;
    private byte _decoder;
    private int _decodingOptions;
    private Uri _defaultExternalCoverUri;
    private boolean _defaultSubLoaded;
    private int _displayHeight;
    private int _displayWidth;
//    private DNSLookupTask _dnsLookupTask;
    private int _duration;
    private boolean _explicitRatio;
    private boolean _fastSeekable;
    private File _file;
//    private FontSetupTask _fontSetupTask;
    private float _horzRatio;
    private InetAddress _host;
    private boolean _hostnameResolved;
    private final HttpFactory _httpFactory;
    private boolean _isChangingDisplay;
    private FFPlayer _kept;
    private int _lastError;
    private byte _lastSucceededDecoder;
    private float _maxBaseVolume;
    private MediaRouter _mediaRouter;
    private MediaRouter.Callback _mediaRouterCallback;
    private JointPlayer _mp;
    @Nullable
//    private Navigator _navigator;
    private String _overriddenTypefaceName;
    private MediaDatabase.State _persistent;
    private boolean _presentPlaying;
    private boolean _presentTargetState;
    @Nullable
    private Map<String, String> _privateHeader;
    private boolean _rampupPrepared;
//    private RemoteLoader _remoteLoader;
    private int _repeatEnd;
    private int _repeatStart;
    private boolean _requireIP;
    private boolean _seeked;
    private boolean _softAudio;
    private SubStationAlphaMedia _ssa;
    private int _subCanvasHeight;
    private int _subCanvasWidth;
    private String _title;
    private Uri _uri;
    private int _userAudioOffset;
    private long _verboseUntil;
    private float _vertRatio;
//    private final AudioFocusManager _audioFocusManager = new AudioFocusManager(this);
//    private final Handler _handler = new Handler(Looper.getMainLooper(), this);
    private int _loadingDirection = 1;
    private int _state = 0;
    private int _targetState = 0;
    private final List<ISubtitle> _subs = new ArrayList();
//    private final Set<ISubtitleClient.IMediaListener> _mediaListeners = new HashSet();
//    private final Map<Uri, Subtitle> _remoteSubAux = new HashMap();
    private double _userSpeed = 1.0d;
    private int _stereoMode = 0;
    private short _lastUserSelectedAudioTrack = Short.MIN_VALUE;

    public int getState() {
        return this._state;
    }





    public interface Client {
        boolean canStart();

        void load(Uri uri, byte b, int i);

        int mediaTimeToSubtitleTime(int i);

        void onAudioCodecUnsupported(FFPlayer fFPlayer, int i);

        void onAudioStreamChanged(JointPlayer jointPlayer, int i);

        void onBufferingUpdate(int i);

        void onCoverArtChanged();

        void onDurationKnown(int i);

        void onEmbeddedSubtitleAdded(ISubtitle iSubtitle);

        void onNetworkListingComplete();

        void onPresentingStateChanged(boolean z);

        void onRebootToChangeDisplay(int i);

        void onRemoteResourceLoaded(List<ISubtitle> list, Bitmap bitmap, Uri uri);

        void onRemoteResourceLoadingBegin(int i);

        void onRemoteResourceLoadingCanceled();

        void onSSAPrepared(SubStationAlphaMedia subStationAlphaMedia);

        void onSeekBegin(int i);

        void onSeekComplete();

        void onSetupFontCache(boolean z);

        void onStateChanged(int i, int i2);

        void onSubtitleClosed(ISubtitle iSubtitle);

        void onSubtitleInvalidated();

        void onSubtitlesClosed();

        void onTryNextDecoder(byte b, byte b2, boolean z);

        void onVideoDeviceChanged();

        void onVideoFilteringFailed(int i);

        void onVideoSizeChanged(int i, int i2);

        void update(Player player, int i);

        void updatePersistent(Uri uri, MediaDatabase.State state, List<ISubtitle> list);
    }

    public Player(HttpFactory httpFactory) {
        this._httpFactory = httpFactory;
//        setSoftAudio(P.softAudio);
//        if (Build.VERSION.SDK_INT >= 16) {
//            this._mediaRouter = (MediaRouter) App.context.getSystemService("media_router");
//            if (this._mediaRouter != null) {
//                this._mediaRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.mxtech.videoplayer.Player.1
//                    @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
//                    public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
//                        if (Player.this._mp != null) {
//                            Player.this._mp.reconfigAudioDevice();
//                        }
//                    }
//                };
//            }
//        }
//        App.prefs.registerOnSharedPreferenceChangeListener(this);
    }


    public void setClient(@NonNull Client client) {
        this._client = client;
    }


    public void toggle(boolean silent) {
        if (this._targetState == 5) {
            pause(silent ? 4 : 0);
        } else {
            start();
        }
    }

    public void toggle() {
        toggle(false);
    }


    public void next() {
//        if (isInValidState() && this._navigator != null) {
//            int direction = P.shuffle ? 0 : 1;
//            Uri next = this._navigator.getAndUpdate(this._uri, direction);
//            if (next != null) {
//                save();
//                loadNext(next, direction);
//            }
//        }
    }


    public void previous() {
        previous(P.smartPrevious);
    }

    public void previous(boolean smart) {
        Uri prev;
//        if (isInValidState()) {
//            boolean canRestart = canSeek() && smart && isInPlaybackState();
//            if (canRestart && getCurrentPosition() >= 3000) {
//                seekTo(0, 6000);
//            } else if (this._navigator != null && (prev = this._navigator.getAndUpdate(this._uri, -1)) != null) {
//                save();
//                loadNext(prev, -1);
//            }
//        }
    }


    public void pause(int flags) {
//        if ((flags & 1) == 0) {
//            setTargetState(4);
//        }
//        this._presentTargetState = (flags & 2) != 0;
//        if (isInPlaybackState()) {
//            if ((flags & 16) == 0 && (P.audioRampUp & 1) != 0) {
//                prepareRampUp();
//            }
//            this._mp.pause();
//            setState(4, flags);
//            for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
//                ml.onMediaPause();
//            }
//        }
//        updatePresentingState();


        Log.e("player:","pause null code");
    }


    public void start() {
//        setTargetState(5);
//        if (isInPlaybackState() && !this._isChangingDisplay && this._audioFocusManager.canStartPlayback() && this._client.canStart()) {
//            this._mp.start();
//            setState(5, 0);
//            for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
//                ml.onMediaPlay();
//            }
//        }
//        updatePresentingState();
        Log.e("player:","start null code");
    }

    public void pause() {
        pause(0);
    }



}
