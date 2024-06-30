package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.mxtech.FileUtils;
import com.mxtech.IOUtils;
import com.mxtech.Misc;
import com.mxtech.StringUtils;
import com.mxtech.hardware.BluetoothSpeakerDetector;
import com.mxtech.image.ImageScanner;
import com.mxtech.io.LimitedByteArrayOutputStream;
import com.mxtech.media.BuiltinPlayer;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.media.IStreamInfo;
import com.mxtech.media.JointPlayer;
import com.mxtech.media.MediaUtils;
import com.mxtech.net.HttpFactory;
import com.mxtech.os.AsyncTask2;
import com.mxtech.os.Model;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.ISubtitleClient;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.subtitle.SubStationAlphaSubtitle;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.SubView;
import com.mxtech.videoplayer.subtitle.service.DataSubtitle;
import com.mxtech.videoplayer.subtitle.service.Subtitle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class Player implements JointPlayer.Listener, Handler.Callback, ISubtitleClient, BluetoothSpeakerDetector.OnBluetoothSpeakerStateListener {
    private static final int CLOSEMP_KEEP_BOTH = 3;
    private static final int CLOSEMP_KEEP_PRIMARY = 1;
    private static final int CLOSEMP_KEEP_SECONDARY = 2;
    public static final byte DECODER_FFMPEG_BASED = 6;
    public static final byte DECODER_HARDWARE = 1;
    public static final byte DECODER_OMX = 4;
    public static final byte DECODER_SOFTWARE = 2;
    public static final int FLAG_CLEAR_FOR_RELOADING = 512;
    public static final int FLAG_DONT_UPDATE = 128;
    public static final int FLAG_FLASH = 208;
    public static final int FLAG_KEEP_TARGETSTATE = 16;
    public static final int FLAG_NO_RAMPING = 256;
    public static final int FLAG_PAUSE_TEMPORARY = 80;
    public static final int FLAG_RESTORE_STATE = 1;
    public static final int FLAG_SAVE_TARGETSTATE = 32;
    public static final int FLAG_SILENT = 64;
    private static final int HE_FLAG_MANUALLY_REJECTED = 2;
    private static final int HE_FLAG_RECOVERABLE = 1;
    private static final int INITIAL_PERIOD = 5000;
    public static final int LOADING_COVER = 2;
    public static final int LOADING_SUBTITLE = 1;
    public static final int LOADING_SUBTITLE_CONFIDENCE = 200;
    public static final int LOADING_SUBTITLE_EXPLICIT = 100;
    private static final int MAX_RAMPUP_ITERATION = 600;
    private static final int MSG_RAMP_UP = 3;
    private static final int MSG_SIMULATE_COMPLETION = 2;
    private static final int MSG_UPDATE = 1;
    private static final int MSG_UPDATE_DURATION = 4;
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
    public static final int STATE_PAUSED = 5;
    public static final int STATE_PLAYBACK_COMPLETED = 6;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_UNLOADED = 1;
    private static final int UPDATE_DURATION_AFTER = 2000;
    public static final int UPDATE_INTERVAL = 100;
    private BluetoothSpeakerDetector _BTSpeakerDetector;
    private byte _availableDecoders;
    private Client _client;
    private final Context _context;
    private Bitmap[] _covers;
    private byte _decoder;
    private int _decodingOptions;
    private Uri _defaultExternalCoverUri;
    private boolean _defaultSubLoaded;
    private int _displayHeight;
    private int _displayWidth;
    private DNSLookupTask _dnsLookupTask;
    private int _duration;
    private boolean _explicitRatio;
    private boolean _fastSeekable;
    private File _file;
    private FontSetupTask _fontSetupTask;
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
    private String _overriddenTypefaceName;
    private MediaDatabase.State _persistent;
    @Nullable
    private Map<String, String> _privateHeader;
    private boolean _rampupPrepared;
    private RemoteLoader _remoteLoader;
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
    private static final String TAG = App.TAG + ".Player";
    private static final String TAG_SEEK = App.TAG + ".Player.Seek";
    private static final float[] VOLUMES_15 = {1.0f, 1.0686392f, 1.1410385f, 1.217404f, 1.2979528f, 1.3829142f, 1.4725299f, 1.5670549f, 1.666758f, 1.771923f, 1.882849f, 1.9998517f, 2.123264f, 2.2534368f, 2.3907409f, 2.5355663f};
    private final Handler _handler = new Handler(Looper.getMainLooper(), this);
    private int _state = 0;
    private int _targetState = 0;
    private int _savedState = 0;
    private final List<ISubtitle> _subs = new ArrayList();
    private final Set<ISubtitleClient.IMediaListener> _mediaListeners = new HashSet();
    private final Map<Uri, Subtitle> _remoteSubAux = new HashMap();
    private double _userSpeed = 1.0d;
    private int _stereoMode = 0;
    private short _lastUserSelectedAudioTrack = Short.MIN_VALUE;

    /* loaded from: classes.dex */
    public interface Client {
        boolean canStart();

        int mediaTimeToSubtitleTime(int i);

        void onAudioCodecUnsupported(FFPlayer fFPlayer, int i);

        void onAudioStreamChanged(JointPlayer jointPlayer, int i);

        void onBufferingUpdate(int i);

        void onCoverArtChanged();

        void onDurationKnown(int i);

        void onEmbeddedSubtitleAdded(ISubtitle iSubtitle);

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

        void onTargetStateChanged(int i, int i2);

        void onTryNextDecoder(byte b, byte b2, boolean z);

        void onVideoDeviceChanged();

        void onVideoFilteringFailed(int i);

        void onVideoSizeChanged(int i, int i2);

        void update(Player player, int i);

        void updatePersistent(Uri uri, MediaDatabase.State state, List<ISubtitle> list);
    }

    public Player(Context context, Client client, HttpFactory httpFactory) {
        if (client == null) {
            throw new IllegalStateException();
        }
        this._httpFactory = httpFactory;
        this._context = context.getApplicationContext();
        this._client = client;
        setSoftAudio(P.softAudio);
        if (Build.VERSION.SDK_INT >= 16) {
            this._mediaRouter = (MediaRouter) context.getSystemService("media_router");
            if (this._mediaRouter != null) {
                this._mediaRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.mxtech.videoplayer.Player.1
                    @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
                    public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                        if (Player.this._mp != null) {
                            Player.this._mp.reconfigAudioDevice();
                        }
                    }
                };
            }
        }
    }

    public void setClient(Client client) {
        if (client == null) {
            throw new IllegalStateException();
        }
        this._client = client;
    }

    public Client getClient() {
        return this._client;
    }

    @SuppressLint({"InlinedApi"})
    private void closeMP(int flags) {
        FFPlayer p;
        if (this._kept != null && (flags & 3) == 0) {
            List<ISubtitle> subs = this._kept.getSubtitles();
            if (subs != null) {
                for (ISubtitle sub : subs) {
                    this._subs.remove(sub);
                    this._client.onSubtitleClosed(sub);
                    sub.close();
                }
            }
            this._kept.close();
            this._kept = null;
        }
        if (this._mp != null) {
            if ((flags & 1) != 0 && this._kept == null) {
                IMediaPlayer p2 = this._mp.getPrimary();
                if ((p2 instanceof FFPlayer) && p2.isPrepared()) {
                    this._kept = (FFPlayer) this._mp.detachPrimary();
                    this._kept.pause();
                    this._kept.setDisplay(null, 2);
                }
            }
            if ((flags & 2) != 0 && this._kept == null && (p = this._mp.getSecondary()) != null && p.isPrepared()) {
                this._kept = this._mp.detachSecondary();
                this._kept.pause();
                this._kept.setDisplay(null, 2);
            }
            List<ISubtitle> subs2 = this._mp.getSubtitles();
            if (subs2 != null) {
                for (ISubtitle sub2 : subs2) {
                    this._subs.remove(sub2);
                    this._client.onSubtitleClosed(sub2);
                    sub2.close();
                }
            }
            this._mp.close();
            this._mp = null;
            this._isChangingDisplay = false;
            this._stereoMode = 0;
            if (this._mediaRouterCallback != null) {
                this._mediaRouter.removeCallback(this._mediaRouterCallback);
            }
        }
        if (this._dnsLookupTask != null) {
            this._dnsLookupTask.cancel(true);
            this._dnsLookupTask = null;
        }
        if (isInActiveState()) {
            setState(1);
        }
        for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
            ml.onMediaClosed();
        }
    }

    public void clear() {
        clear(0);
    }

    public void clear(int flags) {
        suspend();
        this._uri = null;
        this._file = null;
        this._host = null;
        this._title = null;
        this._duration = 0;
        this._availableDecoders = (byte) 0;
        this._lastSucceededDecoder = (byte) 0;
        this._decoder = (byte) 0;
        setSoftAudio(P.softAudio);
        this._persistent = null;
        if ((flags & 512) == 0) {
            this._privateHeader = null;
        }
        this._verboseUntil = 0L;
        this._lastError = 0;
        this._horzRatio = 0.0f;
        this._vertRatio = 0.0f;
        this._explicitRatio = false;
        this._displayWidth = 0;
        this._displayHeight = 0;
        if ((flags & 16) == 0) {
            setTargetState(0);
        }
        setState(0);
    }

    public void suspend() {
        closeSubtitles(true);
        closeCovers();
        closeMP(0);
    }

    public void suspendForRestart() {
        closeMP(3);
    }

    private Uri analyzeUri(Uri uri) {
        String uriStr = uri.toString();
        int headerStartIndex = uriStr.indexOf(MetadataChangeSet.CUSTOM_PROPERTY_SIZE_LIMIT_BYTES);
        if (headerStartIndex > 0) {
            this._privateHeader = decodeHeader(uriStr.substring(headerStartIndex + 1));
            return Uri.parse(uriStr.substring(0, headerStartIndex).trim());
        }
        return uri;
    }

    public void setDataSource(Uri uri, byte requestedDecoder, int requestedDecodingOptions, int flags) throws IllegalStateException {
        if (this._state != 0) {
            throw new IllegalStateException();
        }
        this._requireIP = false;
        this._hostnameResolved = false;
        String scheme = uri.getScheme();
        if (scheme != null && (StringUtils.startsWithIgnoreCase(scheme, "http") || StringUtils.startsWithIgnoreCase(scheme, "mms") || StringUtils.startsWithIgnoreCase(scheme, "rt"))) {
            uri = analyzeUri(uri);
            this._uri = uri;
            String hostname = uri.getHost();
            if (hostname != null && hostname.length() > 0) {
                try {
                    if (hostname.equals("localhost")) {
                        this._host = InetAddress.getByAddress(new byte[]{Byte.MAX_VALUE, 0, 0, 1});
                    } else {
                        String[] parts = hostname.split("\\.");
                        if (parts.length != 4) {
                            this._requireIP = true;
                        } else {
                            try {
                                byte[] nums = new byte[4];
                                for (int i = 0; i < 4; i++) {
                                    int val = Integer.parseInt(parts[i]);
                                    if (val < 0 || val > 255) {
                                        this._requireIP = true;
                                        break;
                                    }
                                    nums[i] = (byte) val;
                                }
                                this._host = InetAddress.getByAddress(nums);
                            } catch (NumberFormatException e) {
                                this._requireIP = true;
                            }
                        }
                    }
                } catch (UnknownHostException e2) {
                    Log.e(TAG, "", e2);
                }
            }
        }
        if (this._uri == null) {
            this._file = MediaUtils.fileFromUri(uri);
            if (this._file != null) {
                this._uri = Uri.fromFile(this._file);
                Log.v(TAG, "Canonicalizing URI(2) " + uri + " -> " + this._uri);
            } else {
                this._uri = uri;
            }
        }
        if (this._file != null) {
            if (P.softAudioLocal) {
                setSoftAudio(true);
            }
        } else if (P.softAudioNetwork) {
            setSoftAudio(true);
        }
        this._title = MediaUtils.retrieveTitle(this._uri, (P.list_fields & 16) != 0, null);
        if ((flags & 1) != 0) {
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                this._persistent = mdb.readState(this._uri);
                mdb.release();
            } catch (SQLiteException e3) {
                Log.e(TAG, "", e3);
                this._persistent = null;
            }
            if (this._persistent != null) {
                this._horzRatio = this._persistent.horzRatio;
                this._vertRatio = this._persistent.vertRatio;
                this._explicitRatio = true;
            }
        }
        pickupDecoder(requestedDecoder, requestedDecodingOptions);
        setState(1);
    }

    public void changeDecoder(byte decoder) {
        changeDecoder(decoder, -1);
    }

    public void changeDecoder(byte decoder, int options) {
        if (((decoder & 6) != 0) != ((this._decoder & 6) != 0)) {
            suspendForRestart();
        } else if ((P.audioRampUp & 1) != 0 && isInPlaybackState() && this._mp.hasVideoTrack()) {
            prepareRampUp();
        }
        this._availableDecoders = (byte) (this._availableDecoders | decoder);
        this._decoder = decoder;
        if (options != -1) {
            this._decodingOptions = options;
        }
    }

    public void setSoftAudio(boolean softAudio) {
        if (this._softAudio != softAudio) {
            this._softAudio = softAudio;
        }
    }

    public boolean getUserSoftAudio() {
        return this._softAudio;
    }

    public int countAudioTracks() {
        int[] streamTypes;
        int count = 0;
        for (int type : this._mp.getStreamTypes()) {
            if (type == 1) {
                count++;
            }
        }
        return count;
    }

    public int changeAudioTrackByUserRequest(int which, int streamIndex) {
        this._lastUserSelectedAudioTrack = (short) streamIndex;
        int flags = 0;
        if ((P.audioRampUp & 1) != 0) {
            prepareRampUp();
            flags = 0 | 4096;
        }
        IMediaPlayer primary = this._mp.getPrimary();
        int current = this._mp.getAudioStream();
        if ((primary instanceof BuiltinPlayer) && !this._softAudio && (which == 0 || (this._mp.getCharacteristics() & 1) != 0)) {
            this._mp.mixAudio(-1, flags);
            if ((current >= 0 && ((BuiltinPlayer) primary).getMutedAudioStream() == current) || (current != streamIndex && (current == -1 || current > 0 || which > 0))) {
                int status = this._mp.changeAudioStream(streamIndex, 0);
                switch (status) {
                    case IMediaPlayer.ERESTART /* -4 */:
                        return -4;
                    case 0:
                        break;
                    default:
                        return changeAudioTrack_FF(primary, streamIndex);
                }
            }
            if (this._rampupPrepared && this._state == 4) {
                rampUp();
            }
            return 0;
        }
        return changeAudioTrack_FF(primary, streamIndex);
    }

    private int changeAudioTrack_FF(IMediaPlayer primary, int streamIndex) {
        int flags = 0 | 1024;
        FFPlayer secondary = this._mp.getSecondary();
        if (secondary != null) {
            if ((this._mp.getMixing() & 1) != 0) {
                secondary.changeAudioStream(streamIndex, flags);
                return 0;
            }
            this._mp.mixAudio(streamIndex, flags);
            return 0;
        } else if (primary instanceof FFPlayer) {
            ((FFPlayer) primary).changeAudioStream(streamIndex, flags);
            return 0;
        } else {
            return 0;
        }
    }

    public void removeAudioTrack() {
        this._lastUserSelectedAudioTrack = (short) -100;
        int current_stream_index = this._mp.forceGetAudioStream();
        this._mp.mixAudio(-1, 4096);
        this._mp.removeAudioStream(current_stream_index);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DNSLookupTask extends AsyncTask2<Void, Void, InetAddress> {
        private SurfaceHolder _holder;
        private final String _hostname;
        private final boolean _useSpeedupTricks;
        private final int _videoFlags;

        DNSLookupTask(String hostname, SurfaceHolder holder, int videoFlags, boolean useSpeedupTricks) {
            this._hostname = hostname;
            this._holder = holder;
            this._videoFlags = videoFlags;
            this._useSpeedupTricks = useSpeedupTricks;
        }

        void setDisplay(SurfaceHolder holder) {
            this._holder = holder;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public InetAddress doInBackground(Void... params) {
            try {
                return InetAddress.getByName(this._hostname);
            } catch (UnknownHostException e) {
                Log.i(Player.TAG, "", e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(InetAddress result) {
            if (Player.this._dnsLookupTask == this) {
                Player.this._dnsLookupTask = null;
                Player.this._host = result;
                if (result == null) {
                    Player.this._lastError = 1;
                    Player.this.setState(-1);
                } else if (Player.this._state == 2) {
                    Player.this._hostnameResolved = true;
                    try {
                        Player.this.doLoad(this._holder, this._videoFlags, this._useSpeedupTricks);
                    } catch (Exception e) {
                        Log.e(Player.TAG, "", e);
                        Player.this.handleError(0);
                    }
                } else {
                    Log.e(Player.TAG, "Unknown state " + Player.this._state + " while looking up DNS");
                }
            }
        }
    }

    public boolean load(SurfaceHolder surfaceHolder, int videoFlags, boolean useSpeedupTricks) throws IllegalStateException {
        if (this._state != 1) {
            throw new IllegalStateException();
        }
        if (this._requireIP && !this._hostnameResolved) {
            this._dnsLookupTask = new DNSLookupTask(this._uri.getHost(), surfaceHolder, videoFlags, useSpeedupTricks);
            this._dnsLookupTask.executeParallel(new Void[0]);
        } else {
            try {
                doLoad(surfaceHolder, videoFlags, useSpeedupTricks);
            } catch (Exception e) {
                Log.e(TAG, "", e);
                handleError(0);
                return false;
            }
        }
        setState(2);
        return true;
    }

    public boolean isLocalMedia() {
        return this._host == null || this._host.isSiteLocalAddress() || this._host.isLoopbackAddress();
    }

    @Nullable
    private Map<String, String> mergeMap(@Nullable Map<String, String> m1, @Nullable Map<String, String> m2) {
        if (m1 != null) {
            if (m2 != null) {
                Map<String, String> merged = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                merged.putAll(m1);
                merged.putAll(m2);
                return merged;
            }
            return m1;
        }
        return m2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"InlinedApi"})
    public void doLoad(SurfaceHolder surfaceHolder, int videoFlags, boolean useSpeedupTricks) throws Exception {
        Map<String, String> headers = mergeMap(this._httpFactory.getHttpHeaders(this._uri), this._privateHeader);
        if (this._decoder == 1) {
            BuiltinPlayer primary = new BuiltinPlayer(this._context, this, this._uri, headers, App.prefs.getBoolean(Key.SUBTITLE_SHOW_HW, false) ? 1 : 0);
            try {
                if (App.prefs.contains(Key.CALIBRATE_HW_PLAY_POSITION)) {
                    primary.calibratePlaybackPosition((int) (App.prefs.getFloat(Key.CALIBRATE_HW_PLAY_POSITION, 0.0f) * 1000.0f));
                }
                if (surfaceHolder != null) {
                    primary.setDisplay(surfaceHolder);
                    primary.setScreenOnWhilePlaying(true);
                }
                FFPlayer secondary = null;
                try {
                    if (this._kept != null) {
                        secondary = this._kept;
                        this._kept = null;
                    } else if ((this._availableDecoders & 6) != 0) {
                        if (P.softAudio || P.softAudioNetwork || isLocalMedia()) {
                            FFPlayer secondary2 = new FFPlayer(null, getAudioCreationFlag(), useSpeedupTricks, this);
                            try {
                                secondary2.setDataSource(this._context, this._uri, headers);
                                secondary = secondary2;
                            } catch (Throwable th) {
                                th = th;
                                secondary = secondary2;
                                if (secondary != null) {
                                    secondary.close();
                                }
                                throw th;
                            }
                        } else {
                            secondary = null;
                            Log.w(TAG, "Skipping 2ndary player due to host is not local - " + this._host);
                        }
                    } else {
                        secondary = null;
                    }
                    this._mp = new JointPlayer(primary, secondary, 0);
                    primary = null;
                    FFPlayer secondary3 = null;
                    if (0 != 0) {
                        secondary3.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } finally {
                if (primary != null) {
                    primary.close();
                }
            }
        } else {
            FFPlayer primary2 = null;
            try {
                if (this._kept != null) {
                    primary2 = this._kept;
                    this._kept = null;
                } else {
                    FFPlayer primary3 = new FFPlayer(this, getAudioCreationFlag(), useSpeedupTricks, this);
                    try {
                        primary3.setDataSource(this._context, this._uri, headers);
                        primary2 = primary3;
                    } catch (Throwable th3) {
                        th = th3;
                        primary2 = primary3;
                        if (primary2 != null) {
                            primary2.close();
                        }
                        throw th;
                    }
                }
                primary2.removeAudioStream();
                if (surfaceHolder != null) {
                    primary2.setCoreLimit(P.getCoreLimit());
                    primary2.setDisplay(surfaceHolder, (this._decoder == 4 ? 32 : 64) | videoFlags);
                    primary2.setScreenOnWhilePlaying(true);
                }
                this._mp = new JointPlayer(primary2, null, 0);
                FFPlayer primary4 = null;
                if (0 != 0) {
                    primary4.close();
                }
            } catch (Throwable th4) {
                th = th4;
            }
        }
        this._mp.setListener((JointPlayer.Listener) this);
        this._mp.setAudioStreamType(3);
        this._mp.prepareAsync();
        if (this._persistent != null) {
            this._mp.setProcessing(this._persistent.process);
            this._userAudioOffset = this._persistent.audioOffset;
        } else {
            int deinterlacer = App.prefs.getInt(Key.DEFAULT_DEINTERLACER, 0);
            if (deinterlacer != 0) {
                this._mp.setProcessing(deinterlacer);
            }
        }
        if (this._mediaRouterCallback != null) {
            this._mediaRouter.addCallback(1, this._mediaRouterCallback);
        }
    }

    public void setVerbose() {
        this._verboseUntil = SystemClock.uptimeMillis() + 5000;
    }

    private void pickupDecoder(byte requestedDecoder, int requestedDecodingOptions) {
        String ext;
        int decoder;
        this._availableDecoders = (byte) 3;
        if (P.isOMXDecoderVisible()) {
            this._availableDecoders = (byte) (this._availableDecoders | 4);
        }
        this._decodingOptions = requestedDecodingOptions;
        if (requestedDecoder != 0 && (this._availableDecoders & requestedDecoder) != 0) {
            this._decoder = requestedDecoder;
            if (this._persistent != null) {
                if (this._decoder == 1 && this._persistent.decoder != 1) {
                    this._persistent.audioStream = (short) -1;
                    this._persistent.audioDecoder = (byte) 0;
                    return;
                }
                setSoftAudio(this._persistent.audioDecoder == 2);
            }
        } else if (this._persistent != null && (this._availableDecoders & this._persistent.decoder) != 0) {
            this._decoder = this._persistent.decoder;
            this._decodingOptions = this._persistent.decodingOption;
            setSoftAudio(this._persistent.audioDecoder == 2);
        } else if (this._file == null) {
            if (App.prefs.getBoolean(Key.SOFTWARE_DECODER_NETWORK, false)) {
                this._decoder = (byte) 2;
            } else if (overrideOMXDecoder(P.getOMXDecoderNetwork())) {
                this._decoder = (byte) 4;
            } else {
                this._decoder = (byte) 1;
            }
        } else {
            if (App.prefs.getBoolean(Key.SOFTWARE_DECODER_LOCAL, false)) {
                this._decoder = (byte) 2;
            } else if (overrideOMXDecoder(P.getOMXDecoderLocal())) {
                this._decoder = (byte) 4;
            } else {
                this._decoder = (byte) 1;
            }
            String path = this._uri.getPath();
            if (path != null && (ext = FileUtils.getExtension(path)) != null && (decoder = P.getDecoder(ext, -1)) > 0) {
                byte decoderExt = (byte) (decoder & 255);
                if ((this._availableDecoders & decoderExt) != 0) {
                    this._decoder = decoderExt;
                }
            }
        }
    }

    public void useSpeedupTricks(boolean use) {
        FFPlayer ff = this._mp.getFFPlayer();
        if (ff != null) {
            ff.setFixedFastMode(use);
        }
    }

    private boolean overrideOMXDecoder(boolean defaultValue) {
        return defaultValue;
    }

    private byte pickupNextDecoder(byte last, byte available, boolean manuallyRejected) {
        FFPlayer ff;
        int omxcodec;
        if (this._lastSucceededDecoder != last && (this._lastSucceededDecoder & available) != 0) {
            return this._lastSucceededDecoder;
        }
        byte next = 0;
        switch (last) {
            case 1:
                if ((available & 4) != 0 && P.getTryOMXIfHWFails()) {
                    next = 4;
                    break;
                } else {
                    next = 2;
                    break;
                }
                break;
            case 2:
                if (this._file != null) {
                    if (overrideOMXDecoder(P.getOMXDecoderLocal())) {
                        next = 4;
                        break;
                    } else {
                        next = 1;
                        break;
                    }
                } else if (overrideOMXDecoder(P.getOMXDecoderNetwork())) {
                    next = 4;
                    break;
                } else {
                    next = 1;
                    break;
                }
            case 4:
                if ((available & 1) != 0 && P.getTryHWIfOMXFails()) {
                    next = 1;
                    if (this._mp != null) {
                        if (this._mp.needHardwareVideoForceBlock()) {
                            Log.w(TAG, "HW decoder auto trying rejected for dangerous codec.");
                            next = 2;
                            break;
                        } else if (Build.VERSION.SDK_INT >= 16 && (ff = this._mp.getFFPlayer()) != null && (omxcodec = ff.getCodec()) != 0 && (P.getDefaultOMXVideoCodecs(true) & omxcodec) == 0) {
                            Log.w(TAG, "HW decoder auto trying rejected for unsupported codec.");
                            next = 2;
                            break;
                        }
                    }
                } else {
                    next = 2;
                    break;
                }
                break;
        }
        return (byte) (available & next);
    }

    private boolean isInEarlyStage() {
        if (this._state == 1 || this._state == 2) {
            return true;
        }
        return isInPlaybackState() && SystemClock.uptimeMillis() < this._verboseUntil;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleError(int what) {
        handleError(what, 0);
    }

    private void handleError(int what, int flags) {
        this._availableDecoders = (byte) (this._availableDecoders & (this._decoder ^ (-1)));
        byte failedDecoder = this._decoder;
        this._decoder = pickupNextDecoder(this._decoder, this._availableDecoders, (flags & 2) != 0);
        if (this._decoder == 0) {
            this._lastError = what;
            setState(-1);
            return;
        }
        int closingFlags = 2;
        if ((flags & 1) != 0) {
            closingFlags = 2 | 1;
        }
        closeMP(closingFlags);
        if (this._persistent != null && this._persistent.position < 5000) {
            this._persistent.position = 0;
        }
        this._client.onTryNextDecoder(failedDecoder, this._decoder, isInEarlyStage());
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoDeviceChanged(IMediaPlayer mp) {
        this._isChangingDisplay = false;
        this._client.onVideoDeviceChanged();
        if (this._targetState == 4 && this._state != 6) {
            start();
        }
    }

    private boolean canChangeHWSurface() {
        if (Build.VERSION.SDK_INT >= 16) {
            return true;
        }
        switch (Model.model) {
            case 30:
            case 51:
            case Model.EVO_4G_PLUS /* 160 */:
            case Model.ICONIA_500_SERIES /* 170 */:
            case Model.OPTIMUS_ONE /* 180 */:
                return true;
            default:
                return false;
        }
    }

    public void setDisplay(SurfaceHolder holder, int flags) {
        IMediaPlayer primary;
        int flags2;
        if (this._dnsLookupTask != null) {
            this._dnsLookupTask.setDisplay(holder);
        }
        if (this._mp != null && (primary = this._mp.getPrimary()) != null) {
            if (holder == null || !isInPlaybackState() || this._mp.hasVideoTrack()) {
                if (primary instanceof FFPlayer) {
                    if (this._decoder == 4) {
                        flags2 = flags | 32;
                    } else {
                        flags2 = flags | 64;
                    }
                    FFPlayer ff = (FFPlayer) primary;
                    ff.setCoreLimit(P.getCoreLimit());
                    if (holder != null) {
                        pause(80);
                        if ((P.audioRampUp & 1) != 0) {
                            prepareRampUp();
                        }
                    }
                    this._isChangingDisplay = ff.setDisplay(holder, flags2);
                    if (!this._isChangingDisplay && holder != null) {
                        resume();
                    }
                } else if (holder != null) {
                    if (canChangeHWSurface()) {
                        primary.setDisplay(holder);
                        return;
                    }
                    pause(16);
                    save();
                    suspendForRestart();
                    this._client.onRebootToChangeDisplay(flags);
                } else {
                    primary.setDisplay(null);
                }
            }
        }
    }

    public void setAspectRatio(float horzRatio, float vertRatio, boolean explicit) {
        if (horzRatio != this._horzRatio || vertRatio != this._vertRatio) {
            this._explicitRatio = explicit;
            this._horzRatio = horzRatio;
            this._vertRatio = vertRatio;
            int width = this._mp.width();
            int height = this._mp.height();
            if (width > 0 && height > 0) {
                updateSize(width, height);
            }
        }
    }

    public void setAspectRatioExplicity(boolean explicit) {
        this._explicitRatio = explicit;
    }

    public void recalcVideoSize() {
        updateSize(this._mp.width(), this._mp.height());
    }

    private void updateSize(int width, int height) {
        if (this._horzRatio > 0.0f && this._vertRatio > 0.0f) {
            if (width < height) {
                this._displayWidth = width;
                this._displayHeight = (int) ((width * this._vertRatio) / this._horzRatio);
            } else {
                this._displayWidth = (int) ((height * this._horzRatio) / this._vertRatio);
                this._displayHeight = height;
            }
        } else {
            boolean correct = P.getCorrectHWAspectRatio();
            this._displayWidth = this._mp.displayWidth(correct);
            this._displayHeight = this._mp.displayHeight(correct);
        }
        this._client.onVideoSizeChanged(width, height);
    }

    public float getHorzRatio() {
        return this._horzRatio;
    }

    public float getVertRatio() {
        return this._vertRatio;
    }

    public int getSourceWidth() {
        return this._mp.width();
    }

    public int getSourceHeight() {
        return this._mp.height();
    }

    public int getDisplayWidth() {
        return this._displayWidth;
    }

    public int getDisplayHeight() {
        return this._displayHeight;
    }

    public boolean isLandscape() {
        return this._mp.width() > this._mp.height();
    }

    @Nullable
    public Uri getUri() {
        return this._uri;
    }

    @Nullable
    public File getFile() {
        return this._file;
    }

    public boolean isFile() {
        return this._file != null;
    }

    @Nullable
    public String getTitle() {
        return this._title;
    }

    public int getLastError() {
        return this._lastError;
    }

    public int getDuration() {
        return this._duration;
    }

    public double getSpeed() {
        if (isInPlaybackState()) {
            return this._mp.getSpeed();
        }
        return 1.0d;
    }

    public void setSpeed(double speed) {
        if (isInPlaybackState() && (this._mp.getCharacteristics() & 8) != 0) {
            this._userSpeed = speed;
            this._mp.setSpeed(speed);
            updateAudioOffset();
        }
    }

    public void setStereoMode(int stereoMode) {
        if (this._stereoMode != stereoMode) {
            this._stereoMode = stereoMode;
            this._mp.setStereoMode(stereoMode);
        }
    }

    public int getState() {
        return this._state;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int state) {
        if (this._state != state) {
            setState_l(state);
        }
    }

    private void setState_l(int state) {
        int old = this._state;
        this._state = state;
        if (this._state == 4) {
            if (!this._handler.hasMessages(1)) {
                this._handler.sendEmptyMessageDelayed(1, 100L);
            }
            if (this._rampupPrepared) {
                rampUp();
            }
        } else {
            this._handler.removeMessages(1);
            this._handler.removeMessages(3);
        }
        this._client.onStateChanged(old, state);
    }

    public int getTargetState() {
        return this._targetState;
    }

    private void setTargetState(int state) {
        this._savedState = 0;
        if (this._targetState != state) {
            int old = this._targetState;
            this._targetState = state;
            this._client.onTargetStateChanged(old, state);
        }
    }

    public boolean isInValidState() {
        return this._state >= 1;
    }

    public boolean isInActiveState() {
        return this._state >= 2;
    }

    public boolean isInPlaybackState() {
        return this._state >= 3;
    }

    public byte getAvailableDecoders() {
        return this._availableDecoders;
    }

    public byte getDecoder() {
        return this._decoder;
    }

    public int getDecodingOptions() {
        return this._decodingOptions;
    }

    @Nullable
    public JointPlayer mp() {
        return this._mp;
    }

    @Nullable
    public FFPlayer getPrimaryFF() {
        if (this._mp != null) {
            IMediaPlayer primary = this._mp.getPrimary();
            if (primary instanceof FFPlayer) {
                return (FFPlayer) primary;
            }
        }
        return null;
    }

    public FFPlayer getFFPlayer() {
        if (this._mp != null) {
            return this._mp.getFFPlayer();
        }
        return null;
    }

    public MediaDatabase.State getPersistent() {
        return this._persistent;
    }

    public int getCurrentPosition() {
        return this._mp.getCurrentPosition();
    }

    private void prepareRampUp() {
        setSymmetricBaseVolume(0.0f);
        this._rampupPrepared = true;
        this._handler.removeMessages(3);
    }

    private void rampUp() {
        if (this._mp.getAudioStream() == -1) {
            cancelRampUp();
        } else if (!this._handler.hasMessages(3)) {
            this._handler.sendMessageDelayed(this._handler.obtainMessage(3, 0, this._mp.getCurrentPosition()), 50L);
        }
    }

    private void cancelRampUp() {
        setSymmetricBaseVolume(this._maxBaseVolume);
        this._rampupPrepared = false;
        this._handler.removeMessages(3);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                this._handler.sendEmptyMessageDelayed(1, 100L);
                this._mp.sync();
                int position = this._mp.getCurrentPosition();
                if (this._repeatEnd >= 0 && position >= this._repeatEnd) {
                    Log.i(TAG, "Repeat A-B: jump to start point (" + this._repeatStart + "ms) as end point (" + this._repeatEnd + "ms) reaches.");
                    seekTo(this._repeatStart, 6000);
                }
                this._client.update(this, position);
                return true;
            case 2:
                if (isInPlaybackState()) {
                    setState(6);
                    for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
                        ml.onMediaPlaybackCompleted();
                    }
                    return true;
                }
                return true;
            case 3:
                int iteration = (msg.arg1 >> 16) + 1;
                int volumeLevel = msg.arg1 & 65535;
                int lastPosition = msg.arg2;
                int pos = this._mp.getCurrentPosition();
                if (pos != lastPosition || volumeLevel >= 2 || iteration >= MAX_RAMPUP_ITERATION) {
                    volumeLevel++;
                    float volume = 0.1f * volumeLevel;
                    if (volume >= 1.0f) {
                        setSymmetricBaseVolume(this._maxBaseVolume);
                        this._rampupPrepared = false;
                        return true;
                    }
                    float volume2 = volume * this._maxBaseVolume;
                    this._mp.setVolume(volume2, volume2);
                }
                this._handler.sendMessageDelayed(this._handler.obtainMessage(3, (iteration << 16) | volumeLevel, pos), 50L);
                return true;
            case 4:
                updateDuration();
                return true;
            default:
                return false;
        }
    }

    public void start() {
        setTargetState(4);
        if (isInPlaybackState() && !this._isChangingDisplay && this._client.canStart()) {
            this._mp.start();
            setState(4);
            for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
                ml.onMediaPlay();
            }
        }
    }

    public void resume() {
        if (this._targetState == 4 || this._savedState == 4) {
            start();
        }
    }

    public void pause(int flags) {
        int oldTargetState = this._targetState;
        if ((flags & 16) == 0) {
            setTargetState(5);
        }
        if ((flags & 32) != 0) {
            this._savedState = oldTargetState;
        }
        if (isInPlaybackState()) {
            if ((flags & 256) == 0 && (P.audioRampUp & 1) != 0) {
                prepareRampUp();
            }
            this._mp.pause();
            setState(5);
            for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
                ml.onMediaPause();
            }
        }
    }

    public boolean isSeekable() {
        return this._duration > 0;
    }

    public void seekTo(int position, int timeout) {
        if (isSeekable()) {
            if ((P.audioRampUp & 2) != 0) {
                prepareRampUp();
            }
            this._seeked = true;
            seekTo_l(position, timeout);
            this._client.onSeekBegin(position);
            if (this._duration > 0 && position >= this._duration - 1) {
                this._handler.sendEmptyMessage(2);
            }
        }
    }

    private void seekTo_l(int position, int timeout) {
        for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
            ml.onMediaSeekTo(this._client.mediaTimeToSubtitleTime(position), timeout);
        }
        this._mp.seekTo(position, timeout);
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSeekComplete(IMediaPlayer mp) {
        this._client.onSeekComplete();
        if (this._rampupPrepared && this._state == 4) {
            rampUp();
        }
    }

    @Override // com.mxtech.media.JointPlayer.Listener
    public void onSyncSeekBegin() {
        if (this._mp != null && (P.audioRampUp & 2) != 0) {
            prepareRampUp();
        }
    }

    @Override // com.mxtech.media.JointPlayer.Listener
    public void onSyncSeekEnd() {
        if (this._rampupPrepared && this._state == 4) {
            rampUp();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
        if (updateDuration()) {
            if (this._ssa != null) {
                this._ssa.setVideoSize(width, height);
            }
            updateSize(width, height);
        }
    }

    public void applyBaseVolume() {
        if (P.syncSystemVolume) {
            this._maxBaseVolume = 1.0f;
        } else {
            this._maxBaseVolume = MediaUtils.getVolumeFromLevel(P.localVolume, L.maxAudioVolumeLevel);
        }
        if (!this._rampupPrepared) {
            setSymmetricBaseVolume(this._maxBaseVolume);
        }
    }

    private void setSymmetricBaseVolume(float volume) {
        this._mp.setVolume(volume, volume);
    }

    public void applyOverVolume() {
        float vol;
        int streamVolume = P.syncSystemVolume ? L.audioManager.getStreamVolume(3) : P.localVolume;
        if (streamVolume >= L.maxAudioVolumeLevel) {
            if (L.maxAudioVolumeLevel == 15) {
                if (P.overVolume > L.maxAudioVolumeLevel) {
                    P.overVolume = L.maxAudioVolumeLevel;
                }
                vol = VOLUMES_15[P.overVolume];
            } else {
                double x = 1.0d + ((P.overVolume / L.maxAudioVolumeLevel) * 0.5d);
                vol = (float) ((Math.exp(x * 1.6d) - 1.0d) / (Math.exp(1.6d) - 1.0d));
            }
            this._mp.setVolumeModifier(vol);
        }
    }

    public int getAudioOffset() {
        return this._userAudioOffset;
    }

    public void setAudioOffset(int millis) {
        this._userAudioOffset = millis;
        if (this._mp != null) {
            updateAudioOffset();
        }
    }

    private void updateAudioOffset() {
        int offset;
        if (P.bluetoothAudioDelay != null) {
            if (this._BTSpeakerDetector == null) {
                this._BTSpeakerDetector = new BluetoothSpeakerDetector();
                this._BTSpeakerDetector.setOnBluetoothSpeakerStateListener(this);
            }
            if (this._BTSpeakerDetector.isBluetoothSpeakerUsed()) {
                offset = P.bluetoothAudioDelay.intValue();
            } else {
                offset = P.audioDelay;
            }
        } else {
            offset = P.audioDelay;
        }
        this._mp.setAudioOffset((int) (offset + (this._userAudioOffset / this._userSpeed)));
    }

    public void disburden() {
        if (this._BTSpeakerDetector != null) {
            this._BTSpeakerDetector.close();
            this._BTSpeakerDetector = null;
        }
    }

    @Override // com.mxtech.hardware.BluetoothSpeakerDetector.OnBluetoothSpeakerStateListener
    public void onBluetoothSpeakerConnected(BluetoothSpeakerDetector detector) {
        if (this._mp != null) {
            updateAudioOffset();
        }
    }

    @Override // com.mxtech.hardware.BluetoothSpeakerDetector.OnBluetoothSpeakerStateListener
    public void onBluetoothSpeakerDisconnected(BluetoothSpeakerDetector detector) {
        if (this._mp != null) {
            updateAudioOffset();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onPrepared(IMediaPlayer mp) {
        double speed;
        int i;
        FFPlayer secondary;
        this._seeked = false;
        this._duration = this._mp.getPrimary().duration();
        if (this._persistent != null && this._persistent.playbackSpeed > SubView.SUBTITLE_DRAG_GAP) {
            speed = this._persistent.playbackSpeed;
            this._userSpeed = speed;
        } else {
            speed = App.prefs.getInt(Key.DEFAULT_PLAYBACK_SPEED, 100) / 100.0d;
        }
        this._mp.setSpeed(speed);
        IMediaPlayer p = this._mp.getPrimary();
        this._fastSeekable = (p instanceof FFPlayer) && !((FFPlayer) p).isMpegTS();
        if (this._decoder == 1 && (secondary = this._mp.getSecondary()) != null) {
            int mix = 0;
            int defaultAudioStream2 = secondary.getDefaultAudioStream();
            boolean audioIsNotPlayable = P.hwAudioTrackSelectable && p.getAudioStream() < 0;
            if (defaultAudioStream2 >= 0) {
                if (this._mp.hasVideoTrack()) {
                    if (this._softAudio) {
                        mix = 0 | 1;
                    } else if (audioIsNotPlayable) {
                        mix = 0 | 1;
                    } else if (this._persistent != null && this._persistent.audioStream >= 0 && (this._mp.getCharacteristics() & 1) == 0 && defaultAudioStream2 >= 0 && this._persistent.audioStream != defaultAudioStream2) {
                        mix = 0 | 1;
                    }
                }
            } else if (audioIsNotPlayable) {
                int track = 0;
                int[] streamTypes = secondary.getStreamTypes();
                int length = streamTypes.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    int type = streamTypes[i2];
                    if (type == 1 && !secondary.isDecoderSupported(track)) {
                        this._client.onAudioCodecUnsupported(secondary, track);
                        break;
                    } else {
                        track++;
                        i2++;
                    }
                }
            }
            if (this._persistent != null && this._persistent.subtitles != null) {
                MediaDatabase.State.Subtitle[] subtitleArr = this._persistent.subtitles;
                int length2 = subtitleArr.length;
                int i3 = 0;
                while (true) {
                    if (i3 >= length2) {
                        break;
                    }
                    MediaDatabase.State.Subtitle sub = subtitleArr[i3];
                    if (!sub.enabled || !SubtitleFactory.isEmbeddedSubtitle(sub.uri)) {
                        i3++;
                    } else {
                        mix |= 2;
                        break;
                    }
                }
            }
            if (mix != 0) {
                this._mp.prepareMix(mix);
            }
        }
        applyBaseVolume();
        applyOverVolume();
        updateAudioOffset();
        pickupInitialAudioStream();
        int oldCount = this._subs.size();
        List<ISubtitle> subs = this._mp.getSubtitles();
        if (subs != null) {
            Iterator<ISubtitle> it = subs.iterator();
            while (it.hasNext()) {
                ISubtitle sub2 = it.next();
                while (true) {
                    if (i < oldCount) {
                        i = this._subs.get(i) != sub2 ? i + 1 : 0;
                    } else {
                        this._subs.add(sub2);
                        break;
                    }
                }
            }
        }
        if ((P.audioRampUp & 1) != 0) {
            prepareRampUp();
        }
        setState(3);
        this._verboseUntil = SystemClock.uptimeMillis() + 5000;
        this._lastSucceededDecoder = this._decoder;
        if (this._duration > 0) {
            if (checkVideoOutput()) {
                onDurationKnown();
            } else {
                return;
            }
        } else if (!this._mp.getPrimary().hasVideoTrack()) {
            this._handler.sendEmptyMessageDelayed(4, 2000L);
        }
        if (this._targetState == 4) {
            start();
        }
    }

    private boolean updateDuration() {
        this._handler.removeMessages(4);
        if (isInPlaybackState()) {
            if (!checkVideoOutput()) {
                return false;
            }
            if (this._duration == 0) {
                this._duration = this._mp.getPrimary().duration();
                if (this._duration > 0) {
                    onDurationKnown();
                }
            }
        }
        return true;
    }

    public int getAudioCreationFlag() {
        return 1024;
    }

    private int pickupInitialAudioStream() {
        Locale[] localeArr;
        Locale[] locales;
        int[] types = this._mp.getStreamTypes();
        if (this._persistent != null) {
            if (this._persistent.audioStream >= 0) {
                if (this._persistent.audioStream < types.length && types[this._persistent.audioStream] == 1) {
                    return this._mp.changeAudioStream(this._persistent.audioStream, getAudioCreationFlag());
                }
            } else if (this._persistent.audioStream == -100) {
                this._mp.removeAudioStream(this._mp.forceGetAudioStream());
                return 0;
            }
        }
        for (Locale preferred : P.preferredAudioLocales) {
            for (int i = 0; i < types.length; i++) {
                if (types[i] == 1) {
                    IStreamInfo streamInfo = this._mp.streamInfo(i);
                    if (streamInfo.isValid()) {
                        for (Locale locale : streamInfo.locales()) {
                            if (preferred.equals(locale)) {
                                return this._mp.changeAudioStream(i, getAudioCreationFlag());
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            }
        }
        if ((this._mp.getCharacteristics() & 1) != 0) {
            int i2 = this._mp.getDefaultAudioStream();
            if (i2 >= 0) {
                return this._mp.changeAudioStream(i2, getAudioCreationFlag());
            }
            int i3 = 0;
            for (int type : types) {
                if (type == 1 && this._mp.streamInfo(i3).isValid()) {
                    return this._mp.changeAudioStream(i3, getAudioCreationFlag());
                }
                i3++;
            }
            int i4 = 0;
            for (int type2 : types) {
                if (type2 == 1) {
                    return this._mp.changeAudioStream(i4, getAudioCreationFlag());
                }
                i4++;
            }
            return 0;
        }
        return 0;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onAudioStreamChanged(IMediaPlayer mp, int streamIndex) {
        if (streamIndex < 0 && (this._mp.getMixing() & 1) != 0) {
            this._mp.mixAudio(-1, 0);
        }
        this._client.onAudioStreamChanged((JointPlayer) mp, streamIndex);
        if (this._rampupPrepared && this._state == 4) {
            rampUp();
        }
    }

    public int getShortSeekTimeout() {
        if (this._fastSeekable && P.getFastSeek()) {
            return 0;
        }
        return SEEK_TIMEOUT_SHORT;
    }

    @SuppressLint({"NewApi"})
    private boolean checkVideoOutput() {
        if ((this._availableDecoders & 6) != 0 && this._mp.hasDisplay()) {
            IMediaPlayer p = this._mp.getPrimary();
            FFPlayer ff = this._mp.getSecondary();
            if (ff != null && p.width() == 0 && ff.hasVideoTrack()) {
                Log.e(TAG, "Abandon HW decoder as video output does not exist.");
                handleError(-1010);
                return false;
            }
        }
        return true;
    }

    private void onDurationKnown() {
        if (!this._seeked) {
            if (this._persistent != null && this._persistent.position > 0) {
                seekTo_l(this._persistent.position, getShortSeekTimeout());
            } else if (this._mp.getCurrentPosition() != 0) {
                seekTo_l(0, getShortSeekTimeout());
            }
        }
        this._client.onDurationKnown(this._duration);
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public Context getContext() {
        return this._context;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public int frameTime() {
        if (this._mp != null) {
            return this._mp.frameTime();
        }
        return 0;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public Uri mediaUri() {
        return this._uri;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public int mediaDuration() {
        return this._duration;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public int mediaWidth() {
        if (this._mp != null) {
            return this._mp.width();
        }
        return 0;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public int mediaHeight() {
        if (this._mp != null) {
            return this._mp.height();
        }
        return 0;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public boolean isPlaying() {
        return this._state == 4;
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public SubStationAlphaMedia getSubStationAlphaMedia(int flags, FFPlayer ff) {
        if (this._ssa != null) {
            return this._ssa;
        }
        if ((flags & 1) != 0) {
            return null;
        }
        if (ff == null && this._mp != null) {
            ff = this._mp.getFFPlayer();
        }
        if (ff != null && ((flags & 2) != 0 || ff.isPrepared())) {
            this._ssa = ff.getSubStationAlphaMedia();
        }
        if (this._ssa == null) {
            this._ssa = new SubStationAlphaMedia();
        }
        this._ssa.setFontScale(this._context.getResources().getConfiguration().fontScale * P.subtitleScale);
        if (this._mp != null) {
            this._ssa.setVideoSize(getSourceWidth(), getSourceHeight());
        } else if (ff != null) {
            this._ssa.setVideoSize(ff.width(), ff.height());
        }
        if (this._subCanvasWidth != 0 && this._subCanvasHeight != 0) {
            this._ssa.setCanvasSize(this._subCanvasWidth, this._subCanvasHeight);
        }
        this._client.onSSAPrepared(this._ssa);
        return this._ssa;
    }

    public void setSubtitleCanvasSize(int width, int height) {
        this._subCanvasWidth = width;
        this._subCanvasHeight = height;
        if (this._ssa != null) {
            this._ssa.setCanvasSize(width, height);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class FontSetupTask extends AsyncTask2<Void, Void, Void> {
        private final String defaultFontName = P.getSubtitleFontFamilyName();
        private final SubStationAlphaMedia ssa;

        FontSetupTask(SubStationAlphaMedia ssa) {
            this.ssa = ssa;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Player.this._client.onSetupFontCache(true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... params) {
            this.ssa.setupFonts(this.defaultFontName);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            Player.this._fontSetupTask = null;
            Player.this._client.onSetupFontCache(false);
        }
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public void setupFonts(boolean rebuild) {
        if (this._ssa != null && this._fontSetupTask == null) {
            if (rebuild || !this._ssa.isFontsSetup()) {
                this._fontSetupTask = new FontSetupTask(this._ssa);
                this._fontSetupTask.executeParallel(new Void[0]);
            }
        }
    }

    public boolean isFontSettingUp() {
        return this._fontSetupTask != null;
    }

    public void overrideFonts(String typefaceName) {
        if (this._ssa != null && !TextUtils.equals(typefaceName, this._overriddenTypefaceName)) {
            this._client.onSetupFontCache(true);
            this._overriddenTypefaceName = typefaceName;
            this._ssa.overrideFonts(typefaceName);
            SubStationAlphaMedia subStationAlphaMedia = this._ssa;
            if (typefaceName == null) {
                typefaceName = P.getSubtitleFontFamilyName();
            }
            subStationAlphaMedia.setupFonts(typefaceName);
            this._client.onSetupFontCache(false);
        }
    }

    public boolean isFontOverridden() {
        return this._overriddenTypefaceName != null;
    }

    private ISubtitle findSubtitleStartingWith(Uri uri) {
        String uriString = uri.toString();
        for (ISubtitle sub : this._subs) {
            if (sub.uri().toString().startsWith(uriString)) {
                return sub;
            }
        }
        return null;
    }

    private boolean hasStartingWith(List<RemoteItem> list, String uriString) {
        if (list == null) {
            return false;
        }
        for (RemoteItem item : list) {
            if (item.uri.toString().startsWith(uriString)) {
                return true;
            }
        }
        return false;
    }

    public void loadDefaultSubs(PlayLister lister, boolean loadRemoteCover) {
        String videoFileName;
        File videoFolder;
        String[] strArr;
        File[] scan;
        MediaDatabase.State.Subtitle[] subtitleArr;
        ArrayList<RemoteItem> remoteItems = null;
        if (this._persistent != null && this._persistent.subtitles != null) {
            for (MediaDatabase.State.Subtitle s : this._persistent.subtitles) {
                String scheme = s.uri.getScheme();
                if (MediaListFragment.TYPE_FILE.equals(scheme)) {
                    File file = new File(s.uri.getSchemeSpecificPart());
                    if (findSubtitleStartingWith(Uri.fromFile(file)) == null) {
                        try {
                            ISubtitle[] subs = SubtitleFactory.createFromFile(file, s.name, this, s.typename);
                            if (subs.length > 0) {
                                this._subs.addAll(Arrays.asList(subs));
                            }
                        } catch (Throwable e) {
                            Log.e(TAG, "", e);
                        }
                    }
                } else if ("http".equals(scheme) || "https".equals(scheme)) {
                    String ssp = s.uri.getSchemeSpecificPart();
                    if (!hasStartingWith(remoteItems, ssp)) {
                        if (remoteItems == null) {
                            remoteItems = new ArrayList<>();
                        }
                        remoteItems.add(RemoteItem.newSubtitle(Uri.parse(ssp), s.name, null, 100));
                    }
                }
            }
        }
        if (this._file != null) {
            videoFileName = this._file.getName();
            videoFolder = this._file.getParentFile();
        } else {
            videoFileName = this._uri.getLastPathSegment();
            videoFolder = null;
        }
        if (videoFileName != null) {
            for (File file2 : SubtitleFactory.scan(videoFileName, videoFolder, P.subtitleFolder)) {
                if (findSubtitleStartingWith(Uri.fromFile(file2)) == null) {
                    try {
                        this._subs.addAll(Arrays.asList(SubtitleFactory.createFromFile(file2, null, this, null)));
                    } catch (Throwable e2) {
                        Log.e(TAG, "", e2);
                    }
                }
            }
        }
        if (this._file == null) {
            if (lister.isRemoteListValid()) {
                Uri[] allRemoteSubs = lister.getRemoteSubtitles();
                if (allRemoteSubs != null) {
                    String videoPath = this._uri.toString();
                    for (Uri remote : allRemoteSubs) {
                        String subtitlePath = remote.toString();
                        if (SubtitleFactory.isSubtitleOf(videoPath, subtitlePath, false) && !hasStartingWith(remoteItems, subtitlePath)) {
                            if (remoteItems == null) {
                                remoteItems = new ArrayList<>();
                            }
                            remoteItems.add(RemoteItem.newSubtitle(remote, null, null, 200));
                        }
                    }
                }
            } else {
                String scheme2 = this._uri.getScheme();
                String query = this._uri.getQuery();
                if (query == null && ("http".equals(scheme2) || "https".equals(scheme2))) {
                    if (remoteItems == null) {
                        remoteItems = new ArrayList<>();
                    }
                    String pathBody = FileUtils.stripExtension(this._uri.toString());
                    for (String ext : SubtitleFactory.ALL_EXTENSIONS) {
                        Uri uri = Uri.parse(pathBody + '.' + ext);
                        if (!hasStartingWith(remoteItems, uri.toString())) {
                            remoteItems.add(RemoteItem.newSubtitle(uri, null, null, 0));
                        }
                    }
                }
            }
        }
        if (loadRemoteCover) {
            remoteItems = loadRemoteCover_l(lister, remoteItems);
        }
        if (remoteItems != null && remoteItems.size() > 0) {
            loadRemote((RemoteItem[]) remoteItems.toArray(new RemoteItem[remoteItems.size()]));
        }
    }

    public void loadSubs(Uri[] uris, @Nullable String[] names, @Nullable String[] filenames, PlayLister lister, boolean loadRemoteCover) {
        ArrayList<RemoteItem> remoteItems = null;
        for (int i = 0; i < uris.length; i++) {
            Uri uri = uris[i];
            String name = names != null ? names[i] : null;
            String filename = filenames != null ? filenames[i] : null;
            String scheme = uri.getScheme();
            if (scheme == null || MediaListFragment.TYPE_FILE.equals(scheme)) {
                try {
                    ISubtitle[] subs = SubtitleFactory.createFromFile(new File(uri.getPath()), name, this, null);
                    if (subs.length > 0) {
                        this._subs.addAll(Arrays.asList(subs));
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "", e);
                }
            } else if ("http".equals(scheme) || "https".equals(scheme)) {
                if (remoteItems == null) {
                    remoteItems = new ArrayList<>();
                }
                remoteItems.add(RemoteItem.newSubtitle(uri, name, filename, 100));
            }
        }
        if (loadRemoteCover) {
            remoteItems = loadRemoteCover_l(lister, remoteItems);
        }
        if (remoteItems != null) {
            loadRemote((RemoteItem[]) remoteItems.toArray(new RemoteItem[remoteItems.size()]));
        }
    }

    public void loadRemoteCovers(PlayLister lister) {
        ArrayList<RemoteItem> remoteItems = loadRemoteCover_l(lister, null);
        if (remoteItems != null) {
            loadRemote((RemoteItem[]) remoteItems.toArray(new RemoteItem[remoteItems.size()]));
        }
    }

    private ArrayList<RemoteItem> loadRemoteCover_l(PlayLister lister, ArrayList<RemoteItem> remoteItems) {
        String[] extensions;
        if (this._file == null) {
            if (lister.isRemoteListValid()) {
                String videoPath = this._uri.toString();
                Uri[] covers = lister.getRemoteCovers();
                if (covers != null) {
                    int length = covers.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Uri remote = covers[i];
                        String coverPath = remote.toString();
                        if (!ImageScanner.isCoverOf(videoPath, coverPath, false)) {
                            i++;
                        } else {
                            if (remoteItems == null) {
                                remoteItems = new ArrayList<>();
                            }
                            remoteItems.add(RemoteItem.newCover(remote, 0));
                        }
                    }
                }
            } else {
                String scheme = this._uri.getScheme();
                String query = this._uri.getQuery();
                if (query == null && ("http".equals(scheme) || "https".equals(scheme))) {
                    if (remoteItems == null) {
                        remoteItems = new ArrayList<>();
                    }
                    String pathBody = FileUtils.stripExtension(this._uri.toString());
                    for (String ext : ImageScanner.getExtensions()) {
                        remoteItems.add(RemoteItem.newCover(Uri.parse(pathBody + '.' + ext), 0));
                    }
                }
            }
        }
        return remoteItems;
    }

    public boolean isRemoteLoading() {
        return this._remoteLoader != null;
    }

    public void loadRemote(RemoteItem[] items) {
        cancelRemoteLoading();
        int loadings = 0;
        for (RemoteItem item : items) {
            if (item.type == 0) {
                loadings |= 1;
            } else if (item.type == 1) {
                loadings |= 2;
            }
            loadings |= item.flags;
        }
        this._client.onRemoteResourceLoadingBegin(loadings);
        this._remoteLoader = new RemoteLoader();
        this._remoteLoader.executeParallel(items);
    }

    public void cancelRemoteLoading() {
        if (this._remoteLoader != null) {
            this._remoteLoader.cancel(true);
            this._remoteLoader = null;
            this._client.onRemoteResourceLoadingCanceled();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RemoteItem {
        static final int COVER = 1;
        static final int SUBTITLE = 0;
        @Nullable
        final String filename;
        final int flags;
        @Nullable
        final String name;
        final int type;
        final Uri uri;

        static RemoteItem newSubtitle(Uri uri, @Nullable String name, @Nullable String filename, int flags) {
            return new RemoteItem(uri, name, filename, 0, flags);
        }

        static RemoteItem newCover(Uri uri, int flags) {
            return new RemoteItem(uri, null, null, 1, flags);
        }

        private RemoteItem(Uri uri, String name, String filename, int type, int flags) {
            this.uri = uri;
            this.name = name;
            this.filename = filename;
            this.type = type;
            this.flags = flags;
        }

        public String toString() {
            return this.type == 0 ? "Subtitle " + this.uri : "Cover art " + this.uri;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RemoteContent {
        final String content;
        @Nullable
        final Bitmap cover;
        @Nullable
        final String filename;
        @Nullable
        final String name;
        final byte[] rawData;
        final Uri uri;

        RemoteContent(Uri uri, String name, String filename, String content, byte[] rawData) {
            this.uri = uri;
            this.name = name;
            this.filename = filename;
            this.content = content;
            this.rawData = rawData;
            this.cover = null;
        }

        RemoteContent(Uri uri, Bitmap cover) {
            this.uri = uri;
            this.name = null;
            this.filename = null;
            this.content = null;
            this.rawData = null;
            this.cover = cover;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class RemoteLoader extends AsyncTask2<RemoteItem, Void, List<RemoteContent>> {
        private final String TAG;

        private RemoteLoader() {
            this.TAG = Player.TAG + ".RemoteLoader";
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<RemoteContent> doInBackground(RemoteItem... params) {
            List<RemoteContent> contents = new ArrayList<>();
            LimitedByteArrayOutputStream bytesStream = new LimitedByteArrayOutputStream(SubtitleFactory.MAX_FILE_SIZE, 4096);
            Bitmap cover = null;
            try {
                int length = params.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        RemoteItem item = params[i];
                        if (isCancelled()) {
                            contents = null;
                            try {
                                break;
                            } catch (IOException e) {
                            }
                        } else {
                            if (item.type != 1 || cover == null) {
                                String scheme = item.uri.getScheme();
                                if ("http".equals(scheme) || "https".equals(scheme)) {
                                    HttpURLConnection conn = Player.this._httpFactory.openGetConnection(item.uri.toString());
                                    try {
                                        if (item.type == 0) {
                                            long len = conn.getContentLength();
                                            if (len > 20971520) {
                                                Log.e(this.TAG, "Subtitle too large: " + len);
                                            } else {
                                                bytesStream.reset();
                                                InputStream s = conn.getInputStream();
                                                IOUtils.transferStream(s, bytesStream, (int) SubtitleFactory.MAX_FILE_SIZE);
                                                s.close();
                                                byte[] raw = bytesStream.toByteArray();
                                                contents.add(new RemoteContent(item.uri, item.name, item.filename, SubtitleFactory.decode(raw), raw));
                                                Log.v(this.TAG, "Loaded subtitle " + item + " successfully. (" + raw.length + "B)");
                                            }
                                        } else {
                                            InputStream s2 = conn.getInputStream();
                                            try {
                                                cover = BitmapFactory.decodeStream(s2, null, ThumbStorage.decodingOptions);
                                                if (cover != null) {
                                                    contents.add(new RemoteContent(item.uri, cover));
                                                    Log.v(this.TAG, "Loaded cover art " + item + " successfully. (" + cover.getByteCount() + "B)");
                                                } else {
                                                    Log.w(this.TAG, "Can't decode image from " + item.uri);
                                                }
                                                s2.close();
                                            } catch (OutOfMemoryError e2) {
                                                Log.e(this.TAG, "Can't decode image from " + item.uri, e2);
                                                s2.close();
                                            }
                                        }
                                    } finally {
                                    }
                                }
                            }
                            i++;
                        }
                    } else {
                        try {
                            bytesStream.close();
                            break;
                        } catch (IOException e3) {
                        }
                    }
                }
                return contents;
            } finally {
                try {
                    bytesStream.close();
                } catch (IOException e4) {
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<RemoteContent> contents) {
            String filename;
            if (Player.this._remoteLoader == this) {
                Player.this._remoteLoader = null;
                ArrayList arrayList = null;
                Bitmap cover = null;
                if (contents != null && contents.size() > 0) {
                    for (RemoteContent item : contents) {
                        if (item.cover != null) {
                            cover = item.cover;
                            Player.this._covers = new Bitmap[]{cover, null};
                            Player.this._defaultExternalCoverUri = item.uri;
                        } else {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            ISubtitle[] subtitles = SubtitleFactory.create(item.content, item.uri, item.name, Player.this, null);
                            for (ISubtitle subtitle : subtitles) {
                                arrayList.add(subtitle);
                                Uri sourceUri = Misc.removeFragment(subtitle.uri());
                                if (!Player.this._remoteSubAux.containsKey(sourceUri)) {
                                    if (item.filename != null) {
                                        filename = item.filename;
                                    } else {
                                        filename = sourceUri.getLastPathSegment();
                                        if (filename != null) {
                                            if (!SubtitleFactory.isSupportedFile(filename)) {
                                            }
                                        }
                                    }
                                    Player.this._remoteSubAux.put(sourceUri, new DataSubtitle(sourceUri, filename, item.rawData));
                                }
                            }
                        }
                    }
                    if (arrayList != null) {
                        Player.this._subs.addAll(arrayList);
                    }
                }
                Player.this._client.onRemoteResourceLoaded(arrayList, cover, null);
            }
        }
    }

    public boolean hasCover() {
        return this._covers != null;
    }

    public Bitmap getCover(int orientation) {
        int coverIndex;
        if (this._covers == null) {
            return null;
        }
        if (orientation == 2) {
            if (this._covers[1] != null) {
                coverIndex = 1;
            } else {
                coverIndex = 0;
            }
        } else if (this._covers[0] != null) {
            coverIndex = 0;
        } else {
            coverIndex = 1;
        }
        return this._covers[coverIndex];
    }

    public Bitmap getCover(int targetWidth, int targetHeight) {
        int index;
        if (this._covers == null) {
            return null;
        }
        if (targetWidth <= targetHeight) {
            index = 0;
        } else {
            index = 1;
        }
        if (this._covers[index] != null) {
            return this._covers[index];
        }
        return this._covers[(index + 1) % 2];
    }

    public Uri getDefaultExternalCoverUri() {
        return this._defaultExternalCoverUri;
    }

    public void addCover(int index, Bitmap bitmap, Uri coverSourceUri) {
        if (this._covers == null) {
            this._covers = new Bitmap[2];
        }
        if (index == 0) {
            this._covers[0] = bitmap;
            this._defaultExternalCoverUri = coverSourceUri;
            return;
        }
        this._covers[1] = bitmap;
    }

    public Bitmap[] loadLocalCovers() throws OutOfMemoryError {
        File coverFile;
        if (this._covers == null) {
            if (this._file != null && (coverFile = ImageScanner.scanCoverFile(this._file)) != null) {
                Bitmap cover = BitmapFactory.decodeFile(coverFile.getPath(), ThumbStorage.decodingOptions);
                if (cover != null) {
                    addCover(0, cover, Uri.fromFile(coverFile));
                    return this._covers;
                }
                Log.w(TAG, "Can't decode " + coverFile);
            }
            this._covers = this._mp.getCovers();
        }
        return this._covers;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCoverArtChanged(IMediaPlayer mp) {
        if (this._defaultExternalCoverUri == null) {
            this._covers = null;
            this._client.onCoverArtChanged();
        }
    }

    public void closeCovers() {
        cancelRemoteLoading();
        this._covers = null;
        this._defaultExternalCoverUri = null;
    }

    private void waitPreparation(IMediaPlayer mp, int timeoutMillis) {
        if (this._state == 2) {
            long begin = SystemClock.uptimeMillis();
            while (!mp.isPrepared()) {
                long elapsed = SystemClock.uptimeMillis() - begin;
                if (elapsed <= timeoutMillis) {
                    SystemClock.sleep(10L);
                } else {
                    return;
                }
            }
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        if (this._mp.getPrimary() instanceof FFPlayer) {
            switch (what) {
                case FFPlayer.MX_INFO_VIDEO_DECODER_MANUALLY_REJECTED /* 100000001 */:
                    waitPreparation(this._mp.getPrimary(), 100);
                    handleError(what, 3);
                    break;
                case FFPlayer.MX_INFO_VIDEO_DECODER_INIT_FAILED /* 100000002 */:
                    waitPreparation(this._mp.getPrimary(), 100);
                    handleError(what, 1);
                    break;
                case FFPlayer.MX_INFO_VIDEO_FILTERING_FAILED /* 100000003 */:
                    this._client.onVideoFilteringFailed(extra);
                    break;
            }
        }
        return true;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        handleError(what);
        return true;
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onCompletion(IMediaPlayer mp) {
        setState(6);
        for (ISubtitleClient.IMediaListener ml : this._mediaListeners) {
            ml.onMediaPlaybackCompleted();
        }
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        this._client.onBufferingUpdate(percent);
    }

    @Override // com.mxtech.media.IMediaPlayer.Listener
    public void onSubtitleAdded(IMediaPlayer mp, ISubtitle subtitle) {
        this._subs.add(subtitle);
        this._client.onEmbeddedSubtitleAdded(subtitle);
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public void onSubtitleInvalidated() {
        this._client.onSubtitleInvalidated();
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public void registerMediaListener(ISubtitleClient.IMediaListener listener) {
        this._mediaListeners.add(listener);
    }

    @Override // com.mxtech.subtitle.ISubtitleClient
    public void unregisterMediaListener(ISubtitleClient.IMediaListener listener) {
        this._mediaListeners.remove(listener);
    }

    @Override // com.mxtech.media.JointPlayer.Listener
    public void onSecondaryError(FFPlayer ff, int what, int extra) {
        this._availableDecoders = (byte) (this._availableDecoders & (-7));
    }

    public List<ISubtitle> getSubtitles() {
        return this._subs;
    }

    public boolean hasSubtitles() {
        return this._subs.size() > 0;
    }

    @Nullable
    public Subtitle getSubtitleAuxData(Uri sourceUri) {
        return this._remoteSubAux.get(sourceUri);
    }

    public void replaceSubtitles(List<ISubtitle> newSubs) {
        cancelRemoteLoading();
        List<ISubtitle> subsToClose = new ArrayList<>(this._subs);
        this._subs.clear();
        this._remoteSubAux.clear();
        this._client.onSubtitlesClosed();
        for (ISubtitle sub : subsToClose) {
            sub.close();
        }
        this._subs.addAll(newSubs);
        for (ISubtitle sub2 : newSubs) {
            if (sub2 instanceof SubStationAlphaSubtitle) {
                return;
            }
        }
        this._ssa = null;
        this._overriddenTypefaceName = null;
    }

    public void closeSubtitles(boolean closeEmbedded) {
        cancelRemoteLoading();
        boolean keepSSA = false;
        List<ISubtitle> subsToClose = new ArrayList<>(this._subs.size());
        if (closeEmbedded) {
            subsToClose.addAll(this._subs);
            this._subs.clear();
        } else {
            Iterator<ISubtitle> it = this._subs.iterator();
            while (it.hasNext()) {
                ISubtitle sub = it.next();
                int flags = sub.flags();
                if ((65536 & flags) != 0) {
                    if ((1048576 & flags) != 0) {
                        keepSSA = true;
                    }
                } else {
                    it.remove();
                    subsToClose.add(sub);
                }
            }
        }
        this._remoteSubAux.clear();
        this._client.onSubtitlesClosed();
        for (ISubtitle sub2 : subsToClose) {
            sub2.close();
        }
        if (!keepSSA) {
            this._ssa = null;
            this._overriddenTypefaceName = null;
        }
        this._defaultSubLoaded = false;
    }

    public boolean isDefaultSubLoaded() {
        return this._defaultSubLoaded;
    }

    public void setDefaultSubLoaded() {
        this._defaultSubLoaded = true;
    }

    @Nullable
    private static String encodeHeader(@Nullable Map<String, String> headerMap) {
        int numEntry;
        if (headerMap == null || (numEntry = headerMap.size()) <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            i++;
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                if (i < numEntry) {
                    sb.append('&');
                }
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "", e);
            }
        }
        return sb.toString();
    }

    @Nullable
    private static Map<String, String> decodeHeader(@Nullable String headerStr) {
        Map<String, String> map;
        String key;
        String value;
        int i = 0;
        if (headerStr == null) {
            return null;
        }
        String[] split = headerStr.split("&");
        int length = split.length;
        Map<String, String> map2 = null;
        while (i < length) {
            String pair = split[i];
            int equalIndex = pair.indexOf(61);
            if (equalIndex > 0) {
                try {
                    key = URLDecoder.decode(pair.substring(0, equalIndex), "UTF-8").trim();
                    value = URLDecoder.decode(pair.substring(equalIndex + 1), "UTF-8").trim();
                } catch (Exception e) {
                    e = e;
                    map = map2;
                }
                if (key.length() > 0 && value.length() > 0) {
                    map = map2 == null ? new TreeMap<>(String.CASE_INSENSITIVE_ORDER) : map2;
                    try {
                        map.put(key, value);
                    } catch (Exception e2) {
                        e = e2;
                        Log.e(TAG, "", e);
                        i++;
                        map2 = map;
                    }
                    i++;
                    map2 = map;
                }
            }
            map = map2;
            i++;
            map2 = map;
        }
        return map2;
    }

    public MediaDatabase.State updatePersistent() {
        if (this._persistent == null) {
            this._persistent = new MediaDatabase.State();
        }
        this._persistent.position = this._mp.getCurrentPosition();
        this._persistent.decoder = this._decoder;
        this._persistent.decodingOption = this._decodingOptions;
        this._persistent.process = this._mp.getProcessing();
        if (this._lastUserSelectedAudioTrack != Short.MIN_VALUE) {
            this._persistent.audioStream = this._lastUserSelectedAudioTrack;
        }
        this._persistent.audioDecoder = this._softAudio ? (byte) 2 : (byte) 1;
        this._persistent.audioOffset = this._userAudioOffset;
        if (this._explicitRatio) {
            this._persistent.horzRatio = this._horzRatio;
            this._persistent.vertRatio = this._vertRatio;
        } else {
            this._persistent.horzRatio = 0.0f;
            this._persistent.vertRatio = 0.0f;
        }
        this._persistent.playbackSpeed = this._userSpeed;
        this._client.updatePersistent(this._uri, this._persistent, this._subs);
        return this._persistent;
    }

    public void save() {
        MediaDatabase.State state;
        if (isInPlaybackState()) {
            if (this._state == 3 || this._state == 5 || this._state == 4 || (this._state == 6 && P.rememberPlaybackSelections)) {
                state = updatePersistent();
            } else {
                state = null;
            }
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                SQLiteDatabase db = mdb.db;
                db.beginTransaction();
                if (state != null) {
                    if (this._state == 6) {
                        state.startOver();
                    }
                    mdb.writeState(this._uri, state);
                } else if (this._state == 6) {
                    mdb.deleteState(this._uri);
                }
                if (this._file != null) {
                    ContentValues filesInfo = new ContentValues(2);
                    long currentTime = System.currentTimeMillis();
                    filesInfo.put("LastWatchTime", Long.valueOf(currentTime));
                    if (this._state == 6) {
                        filesInfo.put("FinishTime", Long.valueOf(currentTime));
                    }
                    mdb.upsertVideoFile(this._file, filesInfo);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                mdb.release();
            } catch (SQLiteException e) {
                Log.e(TAG, "", e);
            }
        }
    }

    public String toStateString(int state) {
        switch (state) {
            case -1:
                return "ERROR";
            case 0:
                return "IDLE";
            case 1:
                return "UNLOADED";
            case 2:
                return "PREPARING";
            case 3:
                return "PREPARED";
            case 4:
                return "PLAYING";
            case 5:
                return "PAUSED";
            case 6:
                return "PLAYBACK_COMPLETED";
            default:
                return "UNKNOWN(" + state + ')';
        }
    }

    public boolean isSoftAudioUsed() {
        FFPlayer ff;
        return (((this._decoder & 6) == 0 && (this._mp.getMixing() & 1) == 0) || (ff = this._mp.getFFPlayer()) == null || ff.isOMXAudioDecoderUsed()) ? false : true;
    }

    public void setRepeatPoints(int A, int B) {
        if (A >= 0 && B >= 0) {
            if (A < B) {
                this._repeatStart = A;
                this._repeatEnd = B;
                return;
            }
            this._repeatStart = B;
            this._repeatEnd = A;
            return;
        }
        this._repeatStart = -1;
        this._repeatEnd = -1;
    }
}
