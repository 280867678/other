package com.mxtech.videoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;

import com.mxtech.DeviceUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.JointPlayer;
import com.mxtech.media.MediaUtils;
import com.mxtech.net.HttpFactoryWithCustomHeaders;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.preference.P;
import com.mxtech.preference.Tuner;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.service.PlayService;
import com.mxtech.videoplayer.subtitle.SubView;
import com.mxtech.videoplayer.widget.PlaybackController;
import com.mxtech.videoplayer.widget.ScreenToolbar;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class ActivityScreen extends AppCompatActivity implements Player.Client, Handler.Callback, ActionBar.OnMenuVisibilityListener,SeekBar.OnSeekBarChangeListener,View.OnTouchListener, OrderedSharedPreferences.OnSharedPreferenceChangeListener,PlaybackController.ControlTarget{
    private final HttpFactoryWithCustomHeaders _httpFactory = new HttpFactoryWithCustomHeaders();
    protected Player pp;
    private SurfaceView surfaceView;
    public static final String TAG = "MX.Screen";
    protected LayoutInflater layoutInflater;
    protected boolean started;
//    private static final int CONTROLLER_VISIBLE_CHECK = 5;
//    private static final int COVER_VIEW = 1;
//    private static final int DELTASEEK_TEXT_HIDE_AFTER = 750;
//    private static final int DIRECTION_BACKWARD = 3;
//    private static final int DIRECTION_DOWNWARD = 2;
//    private static final int DIRECTION_FORWARD = 4;
//    private static final int DIRECTION_UNDETERMINED = 0;
//    private static final int DIRECTION_UPWARD = 1;
//    private static final int DIRECT_RENDER_API = 11;
//    private static final int DIRTY_BRIGHTNESS = 1;
//    private static final int DIRTY_LOCAL_VOLUME = 16;
//    private static final int DIRTY_OVER_VOLUME = 4;
//    private static final int DIRTY_SHOW_LEFT_TIME = 8;
//    private static final int DIRTY_ZOOM_MODE = 2;
//    private static final int DOUBE_TAP_CLOSE_INTERVAL = 2000;
//    private static final int DOUBLE_TAP_THRESHOLD = 400;
//    public static final String EXTRA_DAR_HORZ = "DAR_horz";
//    public static final String EXTRA_DAR_VERT = "DAR_vert";
//    public static final String EXTRA_DECODE_MODE = "decode_mode";
//    public static final String EXTRA_DURATION = "duration";
//    public static final String EXTRA_END_BY = "end_by";
//    public static final String EXTRA_FILENAME = "filename";
//    public static final String EXTRA_FILENAMES = "video_list.filename";
//    public static final String EXTRA_HASHES_OPENSUBTITLES = "video_list.hash.opensubtitles";
//    public static final String EXTRA_HASH_OPENSUBTITLES = "hash.opensubtitles";
//    public static final String EXTRA_HEADERS = "headers";
//    public static final String EXTRA_LAUNCHER = "launcher";
//    public static final String EXTRA_LIST = "video_list";
//    public static final String EXTRA_POSITION = "position";
//    public static final String EXTRA_RETURN_RESULT = "return_result";
//    public static final String EXTRA_SECURE_URI = "secure_uri";
//    public static final String EXTRA_SIZE = "size";
//    public static final String EXTRA_SIZES = "video_list.size";
//    public static final String EXTRA_STICKY = "sticky";
//    public static final String EXTRA_SUBTITLES = "subs";
//    public static final String EXTRA_SUBTITLES_ENABLE = "subs.enable";
//    public static final String EXTRA_SUBTITLES_FILENAME = "subs.filename";
//    public static final String EXTRA_SUBTITLES_NAME = "subs.name";
//    public static final String EXTRA_SUPPRESS_ERROR_MESSAGE = "suppress_error_message";
//    public static final String EXTRA_TITLE = "title";
//    public static final String EXTRA_TITLES = "video_list.name";
//    public static final String EXTRA_VIDEO = "video";
//    public static final String EXTRA_VIDEO_ZOOM = "video_zoom";
//    private static final int FLUSH_AFTER = 1000;
//    private static final int GESTURE_HORIZONTAL = 1;
//    private static final int GESTURE_MASK_ZOOM_PAN = 16;
//    private static final int GESTURE_NONE = 0;
//    private static final int GESTURE_PAN = 18;
//    private static final int GESTURE_PLAYBACK_SPEED = 3;
//    private static final int GESTURE_SUBTITLE_SCALE = 37;
//    private static final int GESTURE_SUBTITLE_SCROLL = 33;
//    private static final int GESTURE_SUBTITLE_SIZE = 36;
//    private static final int GESTURE_SUBTITLE_UPDOWN_TEXT = 34;
//    private static final int GESTURE_VERTICAL = 2;
//    private static final int GESTURE_ZOOM = 17;
//    private static final int GESTURE_ZOOM_AND_PAN = 19;
//    public static final int INTRINSIC_SUBTITLE_SYNC = 50;
//    private static final int LEVEL_NONE = 0;
//    private static final int LEVEL_PAUSE = 2;
//    private static final int LEVEL_PLAY = 1;
//    private static final int LF_FIRST_VIDEO = 32;
//    private static final int LF_FORCE_RESUME = 4;
//    private static final int LF_NO_SPLASH = 1;
//    private static final int LF_SILENT = 3;
//    private static final int LF_USER_PROMPT = 16;
//    private static final int LOCK_INPUT = 1;
//    private static final int LOCK_ROTATION = 2;
//    private static final int LONG_FONTCACHE_BUILD_TIME = 2000;
//    private static final int MAX_FLOATING_BAR_ON_ONE_SIDE = 2;
//    private static final float MAX_ZOOM = 2.0f;
//    private static final float MIN_ZOOM = 0.25f;
    private static final long MOVE_REPEAT_SPEED = 50;
//    private static final int MSG_FLUSH_CHANGES = 10;
//    private static final int MSG_HIDE_SUPREME_TEXT = 4;
//    private static final int MSG_NEXT_VIDEO = 8;
//    private static final int MSG_NOTIFY_REMOTE_LOADING = 13;
//    private static final int MSG_REQUEST_LAYOUT_SURFACE = 12;
//    private static final int MSG_SET_DISPLAY_SIZE = 5;
//    private static final int MSG_SHOW_FONTCACHE_MESSAGE = 9;
//    private static final int MSG_SURFACE_CREATED = 7;
//    private static final int MSG_UPDATE_SYSTEM_UI = 6;
//    private static final int MUST_SHOW = 4;
//    private static final int NOTIFY_REMOTE_LOADING_AFTER = 1000;
//    private static final int OSD = 4;
//    private static final int PREVIEW_SEEK_THRESHOLD = 500;
//    public static final int RESULT_ERROR = 1;
//    public static final String RESULT_VIEW = "com.mxtech.intent.result.VIEW";
//    private static final int SEEKTODELTA_DOSEEK = 1;
//    private static final int SEEKTODELTA_SHOWUP_SEEKBAR = 2;
//    private static final int SELECT = 1;
//    private static final int SELECT_EMBEDDED = 2;
//    private static final int SUBTITLE_OVERLAY = 3;
//    private static final int SUBVIEW = 2;
//    private static final int SURFACE_VIEW = 0;
//    public static final String TAG = "MX.Screen";
//    private static final int TEXT_TOUCH_BACK_COLOR = 1625152989;
//    private static final int TRACKBALL_SEEK_SPEED = 20000;
//    private static final float VERTICAL_BAR_DRAG_SPEED = 0.004f;
//    private static final int X = 0;
//    private static final int Y = 1;
//    public static final int ZOOM_CROP = 3;
//    public static final int ZOOM_INSIDE = 1;
//    public static final int ZOOM_ORIGINAL = 2;
//    public static final int ZOOM_STRETCH = 0;
//    private static final int ZOOM_TEXT_HIDE_AFTER = 500;
//    private static final IntentFilter _batteryClockIntentFilter;
//    public static final String kEndByPlaybackCompletion = "playback_completion";
//    public static final String kEndByUser = "user";
//    private static final int kMaxExtraList = 400;
//    private static final String kStateCurrentUri = "uri";
//    private static final String kStateLockedOrientation = "locked_orientation";
//    private static final String kStatePlaying = "playing";
//    private static final String kStateRotationLocked = "rotation_locked";
//    private static final String kStateStickyAutoReset = "sticky_auto_reset";
//    private static final String kStateStickyRequested = "sticky_requested";
    private boolean _alwaysShowStatusBar;
    private boolean _alwaysShowStatusText;
    private boolean _askedTryCustomCodec;
    private MenuItem _audioMenuItem;
    private boolean _autoHideInterface;
//    private AuxNotificationView _auxNotification;
    private ZoomButton _backwardButton;
    private ImageView _batteryCharging;
//    private BatteryClockActionView _batteryClockActionView;
    private boolean _batteryClockInTitleBar;
    private boolean _batteryClockIntentRegistered;
    private MenuItem _batteryClockMenuItem;
//    private ScreenVerticalBar _brightnessBarLayout;
    private boolean _buttonBacklightOff;
    private boolean _canManualRotateScreen;
    private MenuItem _captionMenuItem;
    private PlaybackController _controller;
    private ImageView _coverView;
//    private AskCustomCodec _customCodecAsker;
    private MenuItem _decoderMenuItem;
    private boolean _defaultSticky;
    private boolean _defaultStickyAudio;
    private boolean _delayNextSystemUiHide21;
    private int _delayedResizeHeight;
    private int _delayedResizeWidth;
    private int _dirty;
    private boolean _displaySeekingPosition;
    private boolean _doubleTapZooming;
    private Toast _double_tap_close_toast;
//    private ScreenVerticalBar _dragBar;
    private double _dragBegunLevel;
    private int _durationInSeconds;
    private TextView _durationText;
    private TextView _elapsedTimeText;
//    private ArrayMap<Uri, SubtitleServiceManager.MediaSubtitle> _externalServiceSubs;
    private boolean _fitSubtitleOverlayToVideo;
    private boolean _forceIntrinsicOffset;
    private ZoomButton _forwardButton;
    private int _fullscreen;
    private float _gestureSeekSpeed;
    private int _gestures;
    private boolean _hasVideoTrack;
    private boolean _headsetPlugged;
    private boolean _ignoreNextFocusLoss;
    private Animation _indicatorFadeIn;
    private Animation _indicatorFadeOut;
    private boolean _isSubtitleTextDragging;
    private long _lastResizeTime;
    private long _lastSingleTapTime;
//    @Nullable
    private File _lastSubtitleDir;
    private int _lastSwipeSeekPreviewTarget;
    private int _lastSystemUIVisibility;
    private long _last_double_tap_backkey_time;
    private int _loadHoldCount;
    private int _loadingFlags;
    private byte _loadingRequestedDecoder;
    private ContentLoadingProgressBar _loadingSplash;
    private int _loadingVideoFlags;
    private Lock _lock;
    private ImageButton _lockButton;
//    @Nullable
//    private LockButtonHandler _lockButtonHandler;
    private int _lockRecoverOrientation;
    private boolean _lockShowInterfaceOnTouched;
    private int _lockedTopHeight;
    private int _lockedTopWidth;
    private int _naviMoveIntervalMillis;
    private boolean _naviMoving;
    private boolean _needToUpdateSizes;
    private View _nextButton;
    private OrientationEventListener _orientationEventListener;
    private int _osdBackColor;
    private boolean _osdBackground;
    private boolean _osdBottom;
    private int _osdTextColor;
    private int _panStartX;
    private int _panStartY;
    private int _panX;
    private int _panY;
    private boolean _pauseIfObscured;
    private ImageView _pauseIndicator;
    private ImageButton _playPauseButton;
    private TextView _posText;
    private int _powerPlugged;
    private View _prevButton;
    private boolean _receivingMediaButtonEvents;
    private boolean _rotationLocked;
    private ImageButton _screenRotateButton;
    private Animation _screenRotateButtonFadeIn;
    private Animation _screenRotateButtonFadeOut;
//    private SubtitleServiceManager _sdm;
    private SeekBar _seekBar;
    private boolean _sessionPaused;
    private boolean _showLeftTime;
    private boolean _showScreenRotateButton;
    private boolean _showingLoadingSplash;
//    private ScreenVerticalBar _soundBarLayout;
    private float _startScale;
    private int _startSubtitleBottomPadding;
    private float _startSubtitleTextSizeSp;
    private View _statusLayout;
    private RelativeLayout.LayoutParams _statusOutLayout;
    private TextView _statusText;
    private int _stereoMode;
    private boolean _stickyRequested;
    private TableLayout _subNaviBar;
    private ViewGroup _subNaviBarButtonContainer;
    private View _subNaviBarLeftSpace;
    private View _subNaviBarRightSpace;
    private SubView _subView;
//    private SubtitleOverlay _subtitleOverlay;
//    private SoftReference<SubtitleOverlay> _subtitleOverlayRef;
//    private SubtitlePanel _subtitlePanel;
//    @Nullable
//    private Media _subtitleServiceMedia;
    private boolean _subtitleServiceMediaTried;
    private View _supremeContainer;
    private Animation _supremeFadeOut;
    private ImageView _supremeImage;
    private Drawable _supremePlaybackSpeed;
    private TextView _supremeText;
    private Drawable _supremeTextSize;
    private Drawable _supremeUpdown;
    private byte _surfaceBoundDecoder;
    private SurfaceHolder _surfaceHolderCreated;
    private int _surfaceType;
    private SurfaceView _surfaceView;
    private boolean _switchedToBackgroundPlay;
    private View.OnSystemUiVisibilityChangeListener _systemUIVisibilityChangeListener;
    private RelativeLayout _systemWindowFittable;
    private DateFormat _timeFormat;
    private int _touchStartX;
    private int _touchStartX2;
    private int _touchStartY;
    private int _touchStartY2;
//    private Tracker _tracker;
    private int _videoZoom;
    private int _videoZoomDelay;
    private boolean _wasSetOrientationFromThisVideo;
    private ZoomButton _zoomButton;
//    private ZoomButtonHandler _zoomButtonHandler;
    private float _zoomCropHeight;
    private float _zoomCropWidth;
    private int _zoomDragBeginHeight;
    private int _zoomDragBeginWidth;
    private int _zoomHeight;
    private float _zoomInsideHeight;
    private float _zoomInsideWidth;
    private float _zoomMaxHeight;
    private float _zoomMaxWidth;
    private float _zoomMinHeight;
    private float _zoomMinWidth;
    private int _zoomWidth;
//    protected LayoutInflater layoutInflater;
//    protected Player pp;
    protected TopLayout topLayout;
    protected UILayout uiLayout;
    private static final String UNKNOWN_DURATION = DateUtils.formatElapsedTime(L.sb, 0);
    static final RelativeSizeSpan smallSizeSpan = new RelativeSizeSpan(0.65f);
    private static final IntentFilter _defaultIntentFilter = new IntentFilter();
    protected final Handler handler = new Handler(this);
//    private final HttpFactoryWithCustomHeaders _httpFactory = new HttpFactoryWithCustomHeaders();
    private String _durationString = UNKNOWN_DURATION;
    private int _batteryLevel = -1;
//    private final ArrayList<IFloatingBar> _floatingBars = new ArrayList<>();
//    private final BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() { // from class: com.mxtech.videoplayer.ActivityScreen.3
//        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
//        @Override // android.content.BroadcastReceiver
//        public void onReceive(Context context, Intent intent) {
//            char c;
//            String action = intent.getAction();
//            switch (action.hashCode()) {
//                case -1676458352:
//                    if (action.equals("android.intent.action.HEADSET_PLUG")) {
//                        c = 0;
//                        break;
//                    }
//                    c = 65535;
//                    break;
//                case -1538406691:
//                    if (action.equals("android.intent.action.BATTERY_CHANGED")) {
//                        c = 3;
//                        break;
//                    }
//                    c = 65535;
//                    break;
//                case -1513032534:
//                    if (action.equals("android.intent.action.TIME_TICK")) {
//                        c = 1;
//                        break;
//                    }
//                    c = 65535;
//                    break;
//                case -549244379:
//                    if (action.equals("android.media.AUDIO_BECOMING_NOISY")) {
//                        c = 4;
//                        break;
//                    }
//                    c = 65535;
//                    break;
//                case 505380757:
//                    if (action.equals("android.intent.action.TIME_SET")) {
//                        c = 2;
//                        break;
//                    }
//                    c = 65535;
//                    break;
//                default:
//                    c = 65535;
//                    break;
//            }
//            switch (c) {
//                case 0:
//                    _headsetPlugged = intent.getIntExtra("state", 0) == 1;
//                    updateStereoMode();
//                    return;
//                case 1:
//                case 2:
//                    updateBatteryClock();
//                    return;
//                case 3:
//                    int rawlevel = intent.getIntExtra("level", -1);
//                    int scale = intent.getIntExtra("scale", -1);
//                    int plugged = intent.getIntExtra("plugged", 0);
//                    if (scale > 0) {
//                        int newLevel = (rawlevel * 100) / scale;
//                        if (newLevel != _batteryLevel || _powerPlugged != plugged) {
//                            _batteryLevel = newLevel;
//                            _powerPlugged = plugged;
//                            updateBatteryClock();
//                            return;
//                        }
//                        return;
//                    }
//                    return;
//                case 4:
//                    if (App.prefs.getBoolean(Key.PAUSE_ON_HEADSET_DISCONNECTED, true)) {
//                        pp.pause();
//                        return;
//                    }
//                    return;
//                default:
//                    return;
//            }
//        }
//    };
    private int _consecutiveSeekBegin = -1;
    private int _lastDownKey = 0;
    private int _lastLeftControllerSeconds = Integer.MIN_VALUE;
    private int _lastRightControllerSeconds = Integer.MIN_VALUE;
    private int _lastLeftElapsedTextSeconds = Integer.MIN_VALUE;
    private int _lastRightElapsedTextSeconds = Integer.MIN_VALUE;
    private Bitmap _lastCover = null;
    private int _lastState = 0;
    private boolean _stickyAutoReset = true;
    private int _lastRequestedOrientation = Integer.MIN_VALUE;
    private int _lastUserRequestedOrientation = Integer.MIN_VALUE;
    private int _swiping = 0;
    private int _lastSwiping = 0;
    private final int[] screen_offset = new int[2];
    private final int[] subview_screen_offset = new int[2];







    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);


////        AppUtils.getNativeLibraryDir();
//
//        if (((App) getApplication()).initInteractive(ActivityScreen.this)) {
////            this._supremeFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
////            this._supremeFadeOut.setAnimationListener(new SupremeFadeOutHandler());
////            this.uiLayout.setController(this._controller);
////            this.uiLayout.setOnTouchListener(this);
////            this._controller.setOnVisibilityChangedListener(this);
////            this._controller.bringToFront();
////            ScreenStyle style = ScreenStyle.getInstance();
////            setStyle(style, -1);
////            style.addOnChangeListener(this);
////            Uri intentUri = intent.getData();
////            if (intentUri == null) {
////                Log.e(TAG, "Finishing " + this + " because 'dat' is not provided.");
////                finish();
////                return;
////            }
////            if (!restorePlayService(intent, saved)) {
////                applyIntent(intent, intentUri, saved);
////            }
////            App.prefs.registerOnSharedPreferenceChangeListener(this);
//
//            Log.e("ActivityScreen:","initInteractive");
//
//
//
//
//
//
//
//
//
//
//
//
//
//        surfaceView = findViewById(R.id.surface_view);
//        FFPlayer ffPlayer = new FFPlayer(ActivityScreen.this);
//        Map<String, String> headers = new HashMap<>();
//        try {
////            ffPlayer.setDataSource(Uri.parse("/storage/emulated/0/VMOSfiletransferstation/稳定 月 入8.6w左右，代价是肯吃苦熬夜, 其实富起来就两三年！！！ [BV1Fi421Z73U_p1].mp4"),headers);
////
////                ffPlayer.setDisplay(surfaceView.getHolder(),getDisplay());
////            ffPlayer.prepareAsync();
////            ffPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//
//
//
//        resetPlayer(new Player(this._httpFactory));
//    }








        ((App) getApplication()).init();
        Intent intent = getIntent();
        AppUtils.dumpParams(TAG, this, "onCreate", intent, saved);
        canonicalizeIntent(intent);
        P.applyHardwareAccelerationToWindow(this);
        setTheme(P.getPlaybackTheme());
//        this._tracker = ((App) App.context).getDefaultTracker();
        this.layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        this._naviMoveIntervalMillis = App.prefs.getInt(Key.NAVI_MOVE_INTERVAL, 10) * 1000;
        this._alwaysShowStatusText = App.prefs.getBoolean(Key.STATUS_TEXT_SHOW_ALWAYS, false);
        this._alwaysShowStatusBar = P.oldTablet && App.prefs.getBoolean(Key.STATUS_BAR_SHOW_ALWAYS, false);
        this._fullscreen = P.getFullScreen();
        this._videoZoomDelay = P.getVideoZoomDelay();
        this._gestures = P.getGestures();
        this._gestureSeekSpeed = App.prefs.getFloat(Key.GESTURE_SEEK_SPEED, 10.0f) * 100000.0f;
        this._fitSubtitleOverlayToVideo = App.prefs.getBoolean(Key.SUBTITLE_FIT_OVERLAY_TO_VIDEO, true);
        this._buttonBacklightOff = P.getButtonBacklightOff();
        this._osdBottom = App.prefs.getBoolean(Key.OSD_BOTTOM, false);
        this._osdBackground = App.prefs.getBoolean(Key.OSD_BACKGROUND, true);
        this._osdTextColor = App.prefs.getInt(Key.OSD_TEXT_COLOR, P.DEFAULT_OSD_TEXT_COLOR);
        this._osdBackColor = App.prefs.getInt(Key.OSD_BACK_COLOR, P.DEFAULT_OSD_BACK_COLOR);
        this._showLeftTime = App.prefs.getBoolean(Key.SHOW_LEFT_TIME, false);
        this._displaySeekingPosition = App.prefs.getBoolean(Key.DISPLAY_SEEKING_POSITION, true);
        this._showScreenRotateButton = !DeviceUtils.isTV && App.prefs.getBoolean(Key.SCREEN_ROTATION_BUTTON, true);
        this._defaultStickyAudio = App.prefs.getBoolean(Key.STICKY_AUDIO, true);
        this._stereoMode = App.prefs.getInt(Key.STEREO_MODE, 0);
        if (intent.hasExtra("video_zoom")) {
            this._videoZoom = intent.getIntExtra("video_zoom", 0);
        } else {
            this._videoZoom = App.prefs.getInt("video_zoom", 1);
        }
        if (intent.hasExtra("sticky")) {
            this._defaultSticky = intent.getBooleanExtra("sticky", false);
        } else {
            this._defaultSticky = App.prefs.getBoolean("sticky", false);
        }
        setContentView(R.layout.screen);






        this._systemWindowFittable = (RelativeLayout) findViewById(R.id.systemWindowFittable);
        if (this._systemWindowFittable != null && P.uiVersion < 16) {
            ViewGroup parent = (ViewGroup) this._systemWindowFittable.getParent();
            int numSiblings = parent.getChildCount();
            int i = 0;
            while (true) {
                if (i >= numSiblings) {
                    break;
                } else if (parent.getChildAt(i) != this._systemWindowFittable) {
                    i++;
                } else {
                    parent.removeViewAt(i);
                    int numChildren = this._systemWindowFittable.getChildCount();
                    int k = 0;
                    int i2 = i;
                    while (k < numChildren) {
                        View child = this._systemWindowFittable.getChildAt(0);
                        this._systemWindowFittable.removeViewAt(0);
                        parent.addView(child, i2);
                        k++;
                        i2++;
                    }
                    this._systemWindowFittable = null;
                }
            }
        }
        setVolumeControlStream(3);
        this._supremeContainer = findViewById(R.id.supremeContainer);
        this._supremeImage = (ImageView) findViewById(R.id.supremeImage);
        this._supremeText = (TextView) findViewById(R.id.supremeText);
        this.topLayout = (TopLayout) findViewById(R.id.top_layout);
        this.uiLayout = (UILayout) findViewById(R.id.ui_layout);
        this._controller = (PlaybackController) findViewById(R.id.controller);
        this._seekBar = (SeekBar) findViewById(R.id.progressBar);
        this._subNaviBar = (TableLayout) findViewById(R.id.subNaviBar);
        this._subNaviBarButtonContainer = (ViewGroup) findViewById(R.id.subNaviBarButtonContainer);
        this._subNaviBarLeftSpace = findViewById(R.id.subNaviBarLeftSpace);
        this._subNaviBarRightSpace = findViewById(R.id.subNaviBarRightSpace);
        this._subView = (SubView) findViewById(R.id.subtitleView);
        this._prevButton = findViewById(R.id.prev);
        this._nextButton = findViewById(R.id.next);
        this._backwardButton = (ZoomButton) findViewById(R.id.backward);
        this._forwardButton = (ZoomButton) findViewById(R.id.forward);
        this._lockButton = (ImageButton) findViewById(R.id.lock);
        this._zoomButton = (ZoomButton) findViewById(R.id.zoom);
        this._posText = (TextView) findViewById(R.id.posText);
        this._durationText = (TextView) findViewById(R.id.durationText);
        this._pauseIndicator = (ImageView) findViewById(R.id.pauseIndicator);
        this._playPauseButton = (ImageButton) findViewById(R.id.playpause);
        this._screenRotateButton = (ImageButton) findViewById(R.id.rotate_screen);
        this._loadingSplash = (ContentLoadingProgressBar) findViewById(R.id.loadingSplash);
        resetPlayer(new Player(this._httpFactory));
        this._seekBar.setOnSeekBarChangeListener(this);
        if (P.getItalicTag()) {
            this._subView.enableItalicTag(true);
        }
        this.topLayout.activity = this;
        this.uiLayout.activity = this;
//        setActionBarShowAnimation(R.anim.fast_fade_in);
//        setActionBarHideAnimation(R.anim.fast_fade_out);
//        this._controller.setShowAnimation(R.anim.fast_fade_in);
//        this._controller.setHideAnimation(R.anim.fast_fade_out);
        reconfigHttpFactory();
        this._pauseIfObscured = App.prefs.getBoolean(Key.PAUSE_IF_OBSTRUCTED, false);
        if (Build.VERSION.SDK_INT >= 11) {
            this._lastSystemUIVisibility = this.topLayout.getSystemUiVisibility();
            this._systemUIVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() { // from class: com.mxtech.videoplayer.ActivityScreen.1
                @Override // android.view.View.OnSystemUiVisibilityChangeListener
                @TargetApi(11)
                public void onSystemUiVisibilityChange(int visibility) {
                    if (started && _lastSystemUIVisibility != visibility) {
                        _lastSystemUIVisibility = visibility;
                        if (visibility == 0) {
                            if (P.softButtonsVisibility != 0 && P.uiVersion < 19) {
                                switch (P.playbackTouchAction) {
                                    case 1:
                                        Log.v(ActivityScreen.TAG, "Toggle playback silently in response to system ui visibility change to " + ((Object) ViewUtils.toSystemUiString(visibility)) + " (=" + visibility + ", state=" + pp.getState() + ')');
                                        pp.toggle(true);
                                        break;
                                    case 2:
                                        showController();
                                        break;
                                    case 3:
                                        showController();
                                        pp.pause();
                                        break;
                                    default:
                                        showController();
                                        break;
                                }
                                _lastSwiping = 0;
                                _lastSingleTapTime = SystemClock.uptimeMillis();
                            }
                            if (_autoHideInterface) {
                                handler.removeMessages(6);
                                handler.sendEmptyMessageDelayed(6, P.getInterfaceAutoHideDelay(this));
                            }
                        }
                    }
                }
            };
            this.topLayout.setOnSystemUiVisibilityChangeListener(this._systemUIVisibilityChangeListener);
        }
        this._durationText.setText(this._durationString);
        this._durationText.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.ActivityScreen.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
//                _dirty |= 8;
//                setShowLeftTime(!_showLeftTime);
//                scheduleFlush();
                Toast.makeText(ActivityScreen.this,"this._durationText.setOnClickListener",Toast.LENGTH_LONG).show();
            }
        });
        setProgressText(0, true);
        this._screenRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityScreen.this,"this._screenRotateButton.setOnClickListener",Toast.LENGTH_LONG).show();
            }
        });
        if (this._playPauseButton != null) {
//            PlayPauseButtonHandler handler = new PlayPauseButtonHandler();
            this._playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._playPauseButton.setOnClickListener",Toast.LENGTH_LONG).show();
                }
            });
            this._playPauseButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._playPauseButton.setOnLongClickListener",Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }
        if (this._prevButton != null) {
            this._prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._prevButton.setOnClickListener",Toast.LENGTH_LONG).show();
                }
            });
        }
        if (this._nextButton != null) {
//            NextButtonHandler handler2 = new NextButtonHandler();
            this._nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._nextButton.setOnClickListener",Toast.LENGTH_LONG).show();
                }
            });
            this._nextButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._nextButton.setOnLongClickListener",Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }
        onShowMoveButtons(null, P.getNaviShowMoveButtons());
        onShowPrevNextButtons(null, App.prefs.getBoolean(Key.SHOW_PREV_NEXT, true));
        if (this._backwardButton != null) {
//            NavigationButtonHandler handler3 = new NavigationButtonHandler(-1);
            this._backwardButton.setZoomSpeed(MOVE_REPEAT_SPEED);
            this._backwardButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(ActivityScreen.this,"this._backwardButton.setOnTouchListener",Toast.LENGTH_LONG).show();
                    return false;
                }
            });
            this._backwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._backwardButton.setOnClickListener",Toast.LENGTH_LONG).show();
                }
            });
        }
        if (this._forwardButton != null) {
//            NavigationButtonHandler handler4 = new NavigationButtonHandler(1);
            this._forwardButton.setZoomSpeed(MOVE_REPEAT_SPEED);
            this._forwardButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(ActivityScreen.this,"this._forwardButton.setOnTouchListener",Toast.LENGTH_LONG).show();
                    return false;
                }
            });
            this._forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityScreen.this,"this._forwardButton.setOnClickListener",Toast.LENGTH_LONG).show();
                }
            });
        }
        if (DeviceUtils.isTV) {
            this._lockButton.setImageDrawable(null);
            this._lockButton.setEnabled(false);
            this._lockButton.setFocusable(false);
            this._prevButton.setNextFocusLeftId(R.id.zoom);
            this._zoomButton.setNextFocusRightId(R.id.prev);
        } else {
//            this._lockButtonHandler = new LockButtonHandler();
        }
//        this._zoomButtonHandler = new ZoomButtonHandler();
        this._zoomButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityScreen.this,"this._zoomButton.setOnLongClickListener",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        this._zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityScreen.this,"this._zoomButton.setOnClickListener",Toast.LENGTH_LONG).show();
            }
        });
        if (((App) getApplication()).initInteractive(this)) {
            this._supremeFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
            this._supremeFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (_supremeText.getTag() == null) {
                        hideSupremeText();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.uiLayout.setController(this._controller);
            this.uiLayout.setOnTouchListener(this);
            this._controller.setOnVisibilityChangedListener(this);
            this._controller.bringToFront();
            ScreenStyle style = ScreenStyle.getInstance();
            setStyle(style, -1);
//            style.addOnChangeListener(this);
            Uri intentUri = intent.getData();
            if (intentUri == null) {
                Log.e(TAG, "Finishing " + this + " because 'dat' is not provided.");
                finish();
                return;
            }
            if (!restorePlayService(intent, saved)) {
                applyIntent(intent, intentUri, saved);
            }
            App.prefs.registerOnSharedPreferenceChangeListener(this);
        }




    }


    private void setStyle(ScreenStyle style, int changes) {
//        this._controller.setStyle(style, changes);
//        if (this.toolbar != null) {
//            ((ScreenToolbar) this.toolbar).setStyle(style, changes);
//        }
//        getSoundBar().setStyle(style, changes);
//        getBrightnessBar().setStyle(style, changes);
//        setOnScreenButtonStyle(style, changes);
    }



    private void showController() {
    }

    private void setProgressText(int current, boolean updateController) {
        boolean updateElapsedText;
        int leftSeconds = current / 1000;
        int rightSeconds = this._showLeftTime ? leftSeconds - this._durationInSeconds : this._durationInSeconds;
        if (updateController && leftSeconds == this._lastLeftControllerSeconds && rightSeconds == this._lastRightControllerSeconds) {
            updateController = false;
        }
        if (this._elapsedTimeText != null) {
            updateElapsedText = (leftSeconds == this._lastLeftElapsedTextSeconds && rightSeconds == this._lastRightElapsedTextSeconds) ? false : true;
        } else {
            updateElapsedText = false;
        }
        if (updateController || updateElapsedText) {
            String leftText = formatElapsedTime(L.sb, leftSeconds);
            String rightText = this._showLeftTime ? formatElapsedTime(L.sb, rightSeconds) : this._durationString;
            if (updateController) {
                this._posText.setText(leftText, TextView.BufferType.NORMAL);
                if (this._showLeftTime) {
                    this._durationText.setText(rightText, TextView.BufferType.NORMAL);
                }
                this._lastLeftControllerSeconds = leftSeconds;
                this._lastRightControllerSeconds = rightSeconds;
            }
            if (updateElapsedText) {
                L.sb.setLength(0);
                this._elapsedTimeText.setText(L.sb.append(leftText).append(" / ").append(rightText).toString(), TextView.BufferType.NORMAL);
                this._lastLeftElapsedTextSeconds = leftSeconds;
                this._lastRightElapsedTextSeconds = rightSeconds;
            }
        }
    }

    private String formatElapsedTime(StringBuilder sb, int seconds) {
        if (seconds < 0) {
            DateUtils.formatElapsedTime(sb, -seconds);
            sb.insert(0, '-');
            return L.sb.toString();
        }
        return DateUtils.formatElapsedTime(sb, seconds);
    }


    private void canonicalizeIntent(@NonNull Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && MediaUtils.isMediaStoreUri(uri)) {
            intent.setData(MediaUtils.translateContentUriToFileUri(uri));
        }
    }





    private void resetPlayer(@NonNull Player player) {
        this.pp = player;
        this.pp.setClient(this);
//        this._controller.setPlayer(this.pp);
//        this._subView.setPlayer(this.pp, this);
    }
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        getSupportActionBar().addOnMenuVisibilityListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void load(Uri uri, byte b, int i) {
        load(uri, 0, b, i);
    }

    public final void load(Uri uri, int flags, byte requestedDecoder, int videoFlags) {
//        Resume resume;
//        int sourceFlags;
//        if (this._loadHoldCount > 0) {
//            Log.d(TAG, "Loading holded. (count=" + this._loadHoldCount + ")");
//        } else if (this.pp.getClient() != this) {
//            Log.d(TAG, "Player is bound to other object: " + this.pp.getClient());
//        } else {
//            int state = this.pp.getState();
//            this._loadingFlags = flags;
//            this._loadingRequestedDecoder = requestedDecoder;
//            this._loadingVideoFlags = videoFlags;
//            if (state == 0) {
//                arrangeExtras(getIntent(), uri);
//                if ((flags & 4) != 0) {
//                    resume = Resume.Last;
//                } else if (((flags & 32) == 0 && P.resumeFirstOnly) || (this._lock != null && this._lock.isKidsLock())) {
//                    resume = Resume.Startover;
//                } else {
//                    resume = P.resume;
//                }
//                if (resume == Resume.Startover && !P.rememberPlaybackSelections) {
//                    sourceFlags = 0;
//                } else {
//                    sourceFlags = 256;
//                }
//                this._subtitleServiceMedia = null;
//                this._subtitleServiceMediaTried = false;
//                if (requestedDecoder == 0) {
//                    requestedDecoder = getIntent().getByteExtra(EXTRA_DECODE_MODE, (byte) 0);
//                }
//                this.pp.setDataSource(uri, requestedDecoder, 0, sourceFlags);
//                this._wasSetOrientationFromThisVideo = false;
//                MediaDatabase.State persist = this.pp.getPersistent();
//                if (persist != null) {
//                    if (persist.canResume()) {
//                        if (resume == Resume.Ask) {
//                            updateTitle();
//                            new ResumeAsker();
//                            return;
//                        } else if (resume == Resume.Startover) {
//                            persist.startOver();
//                        }
//                    } else {
//                        persist.startOver();
//                    }
//                }
//            } else if (state != 1) {
//                Log.w(TAG, "Invalid state while loading: " + state);
//                return;
//            }
//            if (checkDecodingEnvironment()) {
//                if (this.pp.getPersistent() != null) {
//                    this._loadingVideoFlags &= -129;
//                }
//                this._surfaceBoundDecoder = this.pp.getDecoder();
//                if (needVideoOutput()) {
//                    this.pp.prepare(this._surfaceHolderCreated, getDisplay(), this._loadingVideoFlags);
//                } else {
//                    this.pp.prepare(null, null, this._loadingVideoFlags);
//                }
//                if (this._surfaceView == null) {
//                    createSurfaceView();
//                }
//            }
//        }
    }





    @Override
    public int mediaTimeToSubtitleTime(int i) {
        return 0;
    }

    @Override
    public void onAudioCodecUnsupported(FFPlayer fFPlayer, int i) {

    }

    @Override
    public void onAudioStreamChanged(JointPlayer jointPlayer, int i) {

    }

    @Override
    public void onBufferingUpdate(int i) {

    }

    @Override
    public void onCoverArtChanged() {

    }

    @Override
    public void onDurationKnown(int i) {

    }

    @Override
    public void onEmbeddedSubtitleAdded(ISubtitle iSubtitle) {

    }

    @Override
    public void onNetworkListingComplete() {

    }

    @Override
    public void onPresentingStateChanged(boolean z) {

    }

    @Override
    public void onRebootToChangeDisplay(int i) {

    }

    @Override
    public void onRemoteResourceLoaded(List<ISubtitle> list, Bitmap bitmap, Uri uri) {

    }

    @Override
    public void onRemoteResourceLoadingBegin(int i) {

    }

    @Override
    public void onRemoteResourceLoadingCanceled() {

    }

    @Override
    public void onSSAPrepared(SubStationAlphaMedia subStationAlphaMedia) {

    }

    @Override
    public void onSeekBegin(int i) {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onSetupFontCache(boolean z) {

    }

    @Override
    public void onStateChanged(int i, int i2) {

    }

    @Override
    public void onSubtitleClosed(ISubtitle iSubtitle) {

    }

    @Override
    public void onSubtitleInvalidated() {

    }

    @Override
    public void onSubtitlesClosed() {

    }

    @Override
    public void onTryNextDecoder(byte b, byte b2, boolean z) {

    }

    @Override
    public void onVideoDeviceChanged() {

    }

    @Override
    public void onVideoFilteringFailed(int i) {

    }

    @Override
    public void onVideoSizeChanged(int i, int i2) {

    }

    @Override
    public void update(Player player, int i) {

    }

    @Override
    public void updatePersistent(Uri uri, MediaDatabase.State state, List<ISubtitle> list) {

    }

    @Override
    public void onMenuVisibilityChanged(boolean isVisible) {

    }









    public void onTopLayoutSizeChanged(View v) {
//        if (!isFinishing()) {
//            int topWidth = this.topLayout.getWidth();
//            int topHeight = this.topLayout.getHeight();
//            if (this._systemWindowFittable != null && this._lock != null) {
//                if (topWidth > this._lockedTopWidth || topHeight > this._lockedTopHeight) {
//                    this._lockedTopWidth = topWidth;
//                    this._lockedTopHeight = topHeight;
//                } else {
//                    return;
//                }
//            }
//            if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
//                this._needToUpdateSizes = true;
//            } else if (this._surfaceView != null && ((RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams()).getRules()[13] == 0) {
//                int surfaceWidth = this._surfaceView.getWidth();
//                int surfaceHeight = this._surfaceView.getHeight();
//                if (surfaceWidth > 4 && surfaceHeight > 4) {
//                    if ((topWidth > topHeight) == (surfaceWidth > surfaceHeight)) {
//                    }
//                    this._panX -= 4;
//                    updateSizes();
//                    this._panX += 4;
//                    this.handler.removeMessages(12);
//                    this.handler.sendEmptyMessage(12);
//                    return;
//                }
//                updateSizes();
//            } else {
//                updateSizes();
//            }
//        }
    }

    public void onUILayoutSizeChanged(View v) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        this.started = true;
        super.onStart();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(OrderedSharedPreferences orderedSharedPreferences, String str) {

    }

    @Override
    public boolean canHide(PlaybackController playbackController) {
        return false;
    }

    @Override
    public boolean canShow(PlaybackController playbackController, int i) {
        return false;
    }

    @Override
    public void onControllerHidingCompleted(PlaybackController playbackController) {

    }

    @Override
    public void onControllerVisibilityChanged(PlaybackController playbackController, int i, int i2, boolean z) {

    }

    @Override
    public void onDefaultHeightChanged(PlaybackController playbackController, int i) {

    }

    @Override
    public void seekByWheel(int i) {

    }


    public static class TopLayout extends RelativeLayout implements Runnable {
        ActivityScreen activity;

        public TopLayout(Context context) {
            super(context);
        }

        public TopLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TopLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.activity != null) {
                post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.activity.onTopLayoutSizeChanged(this);
        }
    }



    public static class UILayout extends RelativeLayout {
        private PlaybackController _controller;
        ActivityScreen activity;

        public UILayout(Context context) {
            super(context);
        }

        public UILayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public UILayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        void setController(PlaybackController controller) {
            this._controller = controller;
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.activity != null) {
                this.activity.onUILayoutSizeChanged(this);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case 0:
                    this._controller.pin();
                    break;
                case 1:
                case 3:
                    this._controller.unpin();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }







    public void reconfigHttpFactory() {
        this._httpFactory.clearHeaders();
        this._httpFactory.addHeader("User-Agent", P.httpUserAgent);
        String[] givenHeaders = getIntent().getStringArrayExtra("headers");
        if (givenHeaders != null) {
            if ((givenHeaders.length & 1) != 0) {
                Log.e(TAG, "Incorrect header. header array should be packed by this order; key, value, key, value,...");
                return;
            }
            for (int i = 0; i < givenHeaders.length; i += 2) {
                this._httpFactory.addHeader(givenHeaders[i], givenHeaders[i + 1]);
            }
        }
    }


    @SuppressLint("WrongConstant")
    public final void onShowMoveButtons(Tuner tuner, boolean show) {
        this._backwardButton.setVisibility(show ? 0 : 8);
        this._forwardButton.setVisibility(show ? 0 : 8);
        updateSubNavibarLayout();
    }

    @SuppressLint("WrongConstant")
    private void updateSubNavibarLayout() {
        if (DeviceUtils.isTV || this._forwardButton.getVisibility() == 0) {
            if (this._subNaviBarButtonContainer.getChildAt(0) != this._zoomButton) {
                this._subNaviBarButtonContainer.removeView(this._subNaviBarLeftSpace);
                this._subNaviBarButtonContainer.removeView(this._subNaviBarRightSpace);
                this._subNaviBarButtonContainer.removeView(this._zoomButton);
                this._subNaviBarButtonContainer.removeView(this._lockButton);
                this._subNaviBarButtonContainer.addView(this._lockButton, 0);
                this._subNaviBarButtonContainer.addView(this._subNaviBarLeftSpace, 1);
                this._subNaviBarButtonContainer.addView(this._subNaviBarRightSpace, 7);
                this._subNaviBarButtonContainer.addView(this._zoomButton, 8);
                this._subNaviBar.setColumnStretchable(0, false);
                this._subNaviBar.setColumnStretchable(1, true);
                this._subNaviBar.setColumnStretchable(7, true);
                this._subNaviBar.setColumnStretchable(8, false);
            }
        } else if (this._subNaviBarButtonContainer.getChildAt(0) != this._subNaviBarLeftSpace) {
            this._subNaviBarButtonContainer.removeView(this._subNaviBarLeftSpace);
            this._subNaviBarButtonContainer.removeView(this._subNaviBarRightSpace);
            this._subNaviBarButtonContainer.removeView(this._zoomButton);
            this._subNaviBarButtonContainer.removeView(this._lockButton);
            this._subNaviBarButtonContainer.addView(this._subNaviBarLeftSpace, 0);
            this._subNaviBarButtonContainer.addView(this._lockButton, 1);
            this._subNaviBarButtonContainer.addView(this._zoomButton, 7);
            this._subNaviBarButtonContainer.addView(this._subNaviBarRightSpace, 8);
            this._subNaviBar.setColumnStretchable(0, true);
            this._subNaviBar.setColumnStretchable(1, false);
            this._subNaviBar.setColumnStretchable(7, false);
            this._subNaviBar.setColumnStretchable(8, true);
        }
    }


    @SuppressLint("WrongConstant")
    public final void onShowPrevNextButtons(Tuner tuner, boolean show) {
        this._prevButton.setVisibility(show ? 0 : 8);
        this._nextButton.setVisibility(show ? 0 : 8);
    }

    @SuppressLint("WrongConstant")
    public void hideSupremeText() {
        this._supremeContainer.setVisibility(8);
        this._supremeImage.setImageDrawable(null);
        this._supremeText.setText((CharSequence) null);
        if (this._showingLoadingSplash) {
            this._loadingSplash.show();
        }
    }





    private boolean restorePlayService(@Nullable Intent newIntent, @Nullable Bundle savedStates) {
//        PlayService.ReturnData detached;
        boolean restore = false;
//        if (PlayService.instance != null && (detached = PlayService.instance.returnPlayer(this)) != null) {
//            this._subtitleServiceMedia = null;
//            this._subtitleServiceMediaTried = false;
//            if (detached.player != this.pp) {
//                if (this.pp != null) {
//                    this.pp.close();
//                }
//                resetPlayer(detached.player);
//            } else {
//                this.pp.setClient(this);
//            }
//            if (newIntent != null) {
//                if (PlayService.kLauncher.equals(newIntent.getStringExtra(EXTRA_LAUNCHER))) {
//                    restore = true;
//                    newIntent.removeExtra(EXTRA_LAUNCHER);
//                } else if (savedStates != null) {
//                    restore = true;
//                } else if (newIntent.getData().equals(this.pp.getUri()) && isByteExtraEquals(newIntent, detached.intent, EXTRA_DECODE_MODE)) {
//                    restore = true;
//                }
//            } else {
//                restore = true;
//            }
//            if (restore) {
//                if (detached.savedStates != null) {
//                    restoreState(detached.savedStates);
//                }
//                replayState();
//                if (this.pp.getState() == 1) {
//                    load(null, 1, (byte) 0, 0);
//                }
//            } else {
//                this._stickyRequested = false;
//                this.pp.clear();
//                Playlist.Autogen.cleanup(detached.intent);
//            }
//        }
        return restore;
    }



    public void applyIntent(Intent intent, Uri intentUri, @Nullable Bundle saved) {
//        boolean start;
//        byte requestedDecoder;
//        Uri[] playlist;
//        Uri playUri = null;
//        int videoFlags = 0;
//        int loadFlags = 32;
//        if (saved != null) {
//            if (0 == 0 && (playUri = (Uri) saved.getParcelable("uri")) != null) {
//                loadFlags = 32 | 5;
//            }
//            start = saved.getBoolean(kStatePlaying, true);
//            requestedDecoder = 0;
//        } else {
//            start = true;
//            requestedDecoder = intent.getByteExtra(EXTRA_DECODE_MODE, (byte) 0);
//            if (requestedDecoder != 0) {
//                loadFlags = 32 | 16;
//            } else {
//                videoFlags = 0 | 128;
//            }
//        }
//        Parcelable[] parcel = intent.getParcelableArrayExtra(EXTRA_LIST);
//        if (parcel != null && parcel.length > 0) {
//            playlist = new Uri[parcel.length];
//            System.arraycopy(parcel, 0, playlist, 0, parcel.length);
//        } else {
//            playlist = Playlist.Autogen.load(intent);
//        }
//        if (playUri == null) {
//            playUri = intentUri;
//        }
//        this.pp.setPlaylist(playUri, playlist);
//        if (intent.hasExtra(EXTRA_POSITION)) {
//            loadFlags |= 4;
//        }
//        load(playUri, loadFlags, requestedDecoder, videoFlags);
//        if (start) {
//            this.pp.start();
//        }
//        if (saved != null) {
//            restoreState(saved);
//        }
//        updateStickyState(true);
    }





}