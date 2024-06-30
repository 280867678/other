package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SubMenu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.MenuUtils;
import com.mxtech.Misc;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.database.DataSetObservable2;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.image.ImageScanner;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.IMediaInfo;
import com.mxtech.media.IMediaInfoAux;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.media.JointPlayer;
import com.mxtech.media.MediaUtils;
import com.mxtech.media.Playlist;
import com.mxtech.net.HttpFactoryWithCustomHeaders;
import com.mxtech.net.UriUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.Lock;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.PlayLister;
import com.mxtech.videoplayer.Player;
import com.mxtech.videoplayer.ScreenStyle;
import com.mxtech.videoplayer.audio.AudioSyncBar;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.ActivityPreferences;
import com.mxtech.videoplayer.preference.DecoderPreferences;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.service.PlayService;
import com.mxtech.videoplayer.subtitle.SubView;
import com.mxtech.videoplayer.subtitle.SubtitleOpener;
import com.mxtech.videoplayer.subtitle.SubtitleOverlay;
import com.mxtech.videoplayer.subtitle.SubtitlePanel;
import com.mxtech.videoplayer.subtitle.SubtitleSpeedBar;
import com.mxtech.videoplayer.subtitle.SubtitleSyncBar;
import com.mxtech.videoplayer.subtitle.service.FileSubtitle;
import com.mxtech.videoplayer.subtitle.service.Media;
import com.mxtech.videoplayer.subtitle.service.Subtitle;
import com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager;
import com.mxtech.videoplayer.widget.AudioFocusManager;
import com.mxtech.videoplayer.widget.BatteryClockActionView;
import com.mxtech.videoplayer.widget.BrightnessBar;
import com.mxtech.videoplayer.widget.DirectMediaOpener;
import com.mxtech.videoplayer.widget.IFloatingBar;
import com.mxtech.videoplayer.widget.MediaInfoDialog;
import com.mxtech.videoplayer.widget.PlaybackController;
import com.mxtech.videoplayer.widget.PlaybackSpeedBar;
import com.mxtech.videoplayer.widget.RepeatABBar;
import com.mxtech.videoplayer.widget.ScreenToolbar;
import com.mxtech.videoplayer.widget.ScreenVerticalBar;
import com.mxtech.videoplayer.widget.SleepTimer;
import com.mxtech.videoplayer.widget.SoundBar;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ActivityScreen extends ActivityVPBase implements IPlayer, IMediaRuntimeInfo, SurfaceHolder.Callback, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, PlaybackController.ControlTarget, SubtitlePanel.Client, Lock.Listener, PlayService.PlayClient, Tuner.Listener, Player.Client, PlayLister.Listener, SubView.Client, ActionBar.OnMenuVisibilityListener, Handler.Callback, DialogRegistry.Listener, OrderedSharedPreferences.OnSharedPreferenceChangeListener, AudioFocusManager.Client, ScreenStyle.OnChangeListener, SubtitleServiceManager.OnDownloadListener {
    private static final int CONTROLLER_VISIBLE_CHECK = 5;
    private static final int COVER_VIEW = 1;
    private static final int DELTASEEK_TEXT_HIDE_AFTER = 750;
    private static final int DIRECTION_BACKWARD = 3;
    private static final int DIRECTION_DOWNWARD = 2;
    private static final int DIRECTION_FORWARD = 4;
    private static final int DIRECTION_UNDETERMINED = 0;
    private static final int DIRECTION_UPWARD = 1;
    private static final int DIRECT_RENDER_API = 11;
    private static final int DIRTY_BRIGHTNESS = 1;
    private static final int DIRTY_LOCAL_VOLUME = 16;
    private static final int DIRTY_OVER_VOLUME = 4;
    private static final int DIRTY_SHOW_LEFT_TIME = 8;
    private static final int DIRTY_ZOOM_MODE = 2;
    private static final int DOUBE_TAP_CLOSE_INTERVAL = 2000;
    private static final int DOUBLE_TAP_THRESHOLD = 400;
    public static final String EXTRA_DAR_HORZ = "DAR_horz";
    public static final String EXTRA_DAR_VERT = "DAR_vert";
    public static final String EXTRA_DECODE_MODE = "decode_mode";
    public static final String EXTRA_DURATION = "duration";
    public static final String EXTRA_END_BY = "end_by";
    public static final String EXTRA_FILENAME = "filename";
    public static final String EXTRA_FILENAMES = "video_list.filename";
    public static final String EXTRA_HASHES_OPENSUBTITLES = "video_list.hash.opensubtitles";
    public static final String EXTRA_HASH_OPENSUBTITLES = "hash.opensubtitles";
    public static final String EXTRA_HEADERS = "headers";
    public static final String EXTRA_LIST = "video_list";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_RETURN_RESULT = "return_result";
    public static final String EXTRA_SECURE_URI = "secure_uri";
    public static final String EXTRA_SIZE = "size";
    public static final String EXTRA_SIZES = "video_list.size";
    public static final String EXTRA_STICKY = "sticky";
    public static final String EXTRA_SUBTITLES = "subs";
    public static final String EXTRA_SUBTITLES_ENABLE = "subs.enable";
    public static final String EXTRA_SUBTITLES_FILENAME = "subs.filename";
    public static final String EXTRA_SUBTITLES_NAME = "subs.name";
    public static final String EXTRA_SUPPRESS_ERROR_MESSAGE = "suppress_error_message";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TITLES = "video_list.name";
    public static final String EXTRA_VIDEO_ZOOM = "video_zoom";
    private static final int FLUSH_AFTER = 1000;
    private static final int GESTURE_HORIZONTAL = 1;
    private static final int GESTURE_MASK_ZOOM_PAN = 16;
    private static final int GESTURE_NONE = 0;
    private static final int GESTURE_PAN = 18;
    private static final int GESTURE_PLAYBACK_SPEED = 3;
    private static final int GESTURE_SUBTITLE_SCALE = 37;
    private static final int GESTURE_SUBTITLE_SCROLL = 33;
    private static final int GESTURE_SUBTITLE_SIZE = 36;
    private static final int GESTURE_SUBTITLE_UPDOWN_TEXT = 34;
    private static final int GESTURE_VERTICAL = 2;
    private static final int GESTURE_ZOOM = 17;
    private static final int GESTURE_ZOOM_AND_PAN = 19;
    public static final int INTRINSIC_SUBTITLE_SYNC = 50;
    private static final int LEVEL_NONE = 0;
    private static final int LEVEL_PAUSE = 2;
    private static final int LEVEL_PLAY = 1;
    private static final int LOAD_FIRST_VIDEO = 32;
    private static final int LOAD_FORCE_RESUME = 4;
    private static final int LOAD_NO_SPLASH = 1;
    private static final int LOAD_SILENT = 3;
    private static final int LOAD_USER_PROMPT = 16;
    private static final int LOCK_INPUT = 1;
    private static final int LOCK_ROTATION = 2;
    private static final int LONG_FONTCACHE_BUILD_TIME = 2000;
    private static final int MAX_FLOATING_BAR_ON_ONE_SIDE = 2;
    private static final float MAX_ZOOM = 2.0f;
    private static final float MIN_ZOOM = 0.25f;
    private static final long MOVE_REPEAT_SPEED = 50;
    private static final int MSG_FLUSH_CHANGES = 10;
    private static final int MSG_HIDE_SUPREME_TEXT = 4;
    private static final int MSG_NEXT_VIDEO = 8;
    private static final int MSG_NOTIFY_REMOTE_LOADING = 13;
    private static final int MSG_REQUEST_LAYOUT_SURFACE = 12;
    private static final int MSG_SET_DISPLAY_SIZE = 5;
    private static final int MSG_SHOW_FONTCACHE_MESSAGE = 9;
    private static final int MSG_SURFACE_CREATED = 7;
    private static final int MSG_UPDATE_SYSTEM_UI = 6;
    private static final int MUST_SHOW = 4;
    private static final int NOTIFY_REMOTE_LOADING_AFTER = 1000;
    private static final int OSD = 4;
    private static final int PREVIEW_SEEK_THRESHOLD = 500;
    public static final int RESULT_ERROR = 1;
    public static final String RESULT_VIEW = "com.mxtech.intent.result.VIEW";
    private static final int SEEKTODELTA_DOSEEK = 1;
    private static final int SEEKTODELTA_SHOWUP_SEEKBAR = 2;
    private static final int SELECT = 1;
    private static final int SELECT_EMBEDDED = 2;
    private static final String STATE_LOCKED_ORIENTATION = "locked_orientation";
    private static final String STATE_PLAY_URI = "uri";
    private static final String STATE_ROTATION_LOCKED = "rotation_locked";
    private static final String STATE_STICKY_AUTO_RESET = "sticky_auto_reset";
    private static final String STATE_STICKY_REQUESTED = "sticky_requested";
    private static final String STATE_TARGET_STATE = "target_state";
    private static final int SUBTITLE_OVERLAY = 3;
    private static final int SUBVIEW = 2;
    private static final int SURFACE_VIEW = 0;
    private static final int TEXT_TOUCH_BACK_COLOR = 1625152989;
    private static final int TRACKBALL_SEEK_SPEED = 20000;
    private static final float VERTICAL_BAR_DRAG_SPEED = 0.004f;
    private static final int X = 0;
    private static final int Y = 1;
    public static final int ZOOM_CROP = 3;
    public static final int ZOOM_INSIDE = 1;
    public static final int ZOOM_ORIGINAL = 2;
    public static final int ZOOM_STRETCH = 0;
    private static final int ZOOM_TEXT_HIDE_AFTER = 500;
    private static final IntentFilter _batteryClockIntentFilter;
    public static final String kEndByPlaybackCompletion = "playback_completion";
    public static final String kEndByUser = "user";
    private static final int kMaxExtraList = 400;
    private boolean _alwaysShowStatusBar;
    private boolean _alwaysShowStatusText;
    private boolean _askedTryCustomCodec;
    private AudioFocusManager _audioFocusManager;
    private MenuItem _audioMenuItem;
    private boolean _autoHideInterface;
    private AuxNotificationView _auxNotification;
    private ZoomButton _backwardButton;
    private ImageView _batteryCharging;
    private BatteryClockActionView _batteryClockActionView;
    private boolean _batteryClockInTitleBar;
    private boolean _batteryClockIntentRegistered;
    private MenuItem _batteryClockMenuItem;
    private ScreenVerticalBar _brightnessBarLayout;
    private boolean _buttonBacklightOff;
    private boolean _canManualRotateScreen;
    private MenuItem _captionMenuItem;
    private PlaybackController _controller;
    private ImageView _coverView;
    private AskCustomCodec _customCodecAsker;
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
    private ScreenVerticalBar _dragBar;
    private double _dragBegunLevel;
    private int _durationInSeconds;
    private TextView _durationText;
    private TextView _elapsedTimeText;
    private ArrayMap<Uri, SubtitleServiceManager.MediaSubtitle> _externalServiceSubs;
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
    private int _lastPauseFlags;
    private long _lastResizeTime;
    private long _lastSingleTapTime;
    private File _lastSubtitleDir;
    private int _lastSwipeSeekPreviewTarget;
    private int _lastSystemUIVisibility;
    private long _last_double_tap_backkey_time;
    private PlayLister _lister;
    private int _loadHoldCount;
    private int _loadingFlags;
    private byte _loadingRequestedDecoder;
    private ContentLoadingProgressBar _loadingSplash;
    private int _loadingVideoFlags;
    private Lock _lock;
    private ImageButton _lockButton;
    @Nullable
    private LockButtonHandler _lockButtonHandler;
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
    private int _repeatA;
    private int _repeatB;
    private boolean _rotationLocked;
    private ImageButton _screenRotateButton;
    private Animation _screenRotateButtonFadeIn;
    private Animation _screenRotateButtonFadeOut;
    private SubtitleServiceManager _sdm;
    private SeekBar _seekBar;
    private boolean _showLeftTime;
    private boolean _showScreenRotateButton;
    private boolean _showingLoadingSplash;
    private ScreenVerticalBar _soundBarLayout;
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
    private SubtitleOverlay _subtitleOverlay;
    private SoftReference<SubtitleOverlay> _subtitleOverlayRef;
    private SubtitlePanel _subtitlePanel;
    @Nullable
    private Media _subtitleServiceMedia;
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
    private Tracker _tracker;
    private boolean _useSpeedupTricks;
    private int _videoZoom;
    private int _videoZoomDelay;
    private boolean _wasSetOrientationFromThisVideo;
    private ZoomButton _zoomButton;
    private ZoomButtonHandler _zoomButtonHandler;
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
    protected LayoutInflater layoutInflater;
    protected Player pp;
    protected TopLayout topLayout;
    protected UILayout uiLayout;
    private static final String UNKNOWN_DURATION = DateUtils.formatElapsedTime(L.sb, 0);
    public static final String TAG = App.TAG + ".Screen";
    static final RelativeSizeSpan smallSizeSpan = new RelativeSizeSpan(0.65f);
    private static final IntentFilter _defaultIntentFilter = new IntentFilter();
    protected final Handler handler = new Handler(this);
    private final HttpFactoryWithCustomHeaders _httpFactory = new HttpFactoryWithCustomHeaders();
    private String _durationString = UNKNOWN_DURATION;
    private int _batteryLevel = -1;
    private final ArrayList<IFloatingBar> _floatingBars = new ArrayList<>();
    private int _lastLoadingDirection = 1;
    private final BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() { // from class: com.mxtech.videoplayer.ActivityScreen.3
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String action = intent.getAction();
            switch (action.hashCode()) {
                case -1676458352:
                    if (action.equals("android.intent.action.HEADSET_PLUG")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1538406691:
                    if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1513032534:
                    if (action.equals("android.intent.action.TIME_TICK")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -549244379:
                    if (action.equals("android.media.AUDIO_BECOMING_NOISY")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 505380757:
                    if (action.equals("android.intent.action.TIME_SET")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    ActivityScreen.this._headsetPlugged = intent.getIntExtra("state", 0) == 1;
                    ActivityScreen.this.updateStereoMode();
                    return;
                case 1:
                case 2:
                    ActivityScreen.this.updateBatteryClock();
                    return;
                case 3:
                    int rawlevel = intent.getIntExtra("level", -1);
                    int scale = intent.getIntExtra("scale", -1);
                    int plugged = intent.getIntExtra("plugged", 0);
                    if (scale > 0) {
                        int newLevel = (rawlevel * 100) / scale;
                        if (newLevel != ActivityScreen.this._batteryLevel || ActivityScreen.this._powerPlugged != plugged) {
                            ActivityScreen.this._batteryLevel = newLevel;
                            ActivityScreen.this._powerPlugged = plugged;
                            ActivityScreen.this.updateBatteryClock();
                            return;
                        }
                        return;
                    }
                    return;
                case 4:
                    if (App.prefs.getBoolean(Key.PAUSE_ON_HEADSET_DISCONNECTED, true)) {
                        ActivityScreen.this.pp.pause(0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
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
    private int _swiping = 0;
    private int _lastSwiping = 0;
    private final int[] screen_offset = new int[2];
    private final int[] subview_screen_offset = new int[2];

    static /* synthetic */ int access$3504(ActivityScreen x0) {
        int i = x0._loadHoldCount + 1;
        x0._loadHoldCount = i;
        return i;
    }

    static /* synthetic */ int access$3506(ActivityScreen x0) {
        int i = x0._loadHoldCount - 1;
        x0._loadHoldCount = i;
        return i;
    }

    static {
        _defaultIntentFilter.addAction("android.intent.action.HEADSET_PLUG");
        _defaultIntentFilter.addAction("android.media.AUDIO_BECOMING_NOISY");
        _batteryClockIntentFilter = new IntentFilter();
        _batteryClockIntentFilter.addAction("android.intent.action.TIME_TICK");
        _batteryClockIntentFilter.addAction("android.intent.action.TIME_SET");
        _batteryClockIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void static_init(Context context) {
    }

    public static final void launch(Context context, Uri uri, @Nullable Uri[] playlist, byte decodeMode) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", uri, context, AppUtils.findActivityKindOf(context, ActivityScreen.class));
            if (decodeMode != 0) {
                intent.putExtra(EXTRA_DECODE_MODE, decodeMode);
            }
            if (playlist != null) {
                if (playlist.length > 400) {
                    File autogen = Playlist.Autogen.create(uri, playlist);
                    if (autogen != null) {
                        intent.setData(Uri.fromFile(autogen));
                    }
                } else {
                    intent.putExtra(EXTRA_LIST, playlist);
                }
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public ActivityScreen() {
        this.dialogRegistry.addListener(this);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 4:
                setSupremeText((CharSequence) null);
                if (isControllerSeekingVisible()) {
                    this._controller.hide(2);
                    return true;
                }
                return true;
            case 5:
                setDisplaySize(this._delayedResizeWidth, this._delayedResizeHeight);
                return true;
            case 6:
                updateSystemLayout();
                return true;
            case 7:
                if (this._surfaceHolderCreated != null) {
                    if (this.pp.isInActiveState()) {
                        try {
                            this.pp.setDisplay(this._surfaceHolderCreated, 0);
                            this._surfaceBoundDecoder = this.pp.getDecoder();
                            updateDecoderIndicator();
                            return true;
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "", e);
                            return true;
                        }
                    } else if (this.pp.isInValidState()) {
                        load(null, this._loadingFlags, this._loadingRequestedDecoder, this._loadingVideoFlags);
                        return true;
                    } else {
                        return true;
                    }
                }
                return true;
            case 8:
                if (isFinishing() || !this.pp.isInPlaybackState()) {
                    return true;
                }
                nextAuto(msg.arg1 == 1);
                return true;
            case 9:
                if (isFinishing()) {
                    return true;
                }
                showDialog((ActivityScreen) new FontCacheProgressDialog());
                return true;
            case 10:
                flushChanges();
                return true;
            case 11:
            default:
                return false;
            case 12:
                updateSizes();
                return true;
            case 13:
                setAuxNotification(getString(R.string.subtitle_searching));
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryClock() {
        if (this._alwaysShowStatusText || (this._batteryClockInTitleBar && (this._controller.getVisibleParts() & 2) != 0)) {
            updateBatteryClock(true);
        }
    }

    private void updateBatteryClock(boolean updateController) {
        if (this._timeFormat != null) {
            L.sb.setLength(0);
            if (this._batteryLevel >= 0) {
                L.sb.append(this._batteryLevel).append("%   ");
            }
            L.sb.append(this._timeFormat.format(new Date()));
            String text = L.sb.toString();
            if (this._statusText != null) {
                this._statusText.setText(text, TextView.BufferType.NORMAL);
            }
            if (updateController) {
                if (this._batteryClockActionView != null) {
                    this._batteryClockActionView.setContent(text, this._powerPlugged != 0);
                }
                if (this._batteryCharging != null) {
                    int old = this._batteryCharging.getVisibility();
                    int value = this._powerPlugged != 0 ? 0 : 8;
                    if (old != value) {
                        this._batteryCharging.setVisibility(value);
                    }
                }
            }
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public int getIntrinsicOffset() {
        return (this._forceIntrinsicOffset || this.pp.getState() == 4) ? 50 : 0;
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public final void onSubtitleVisibilityChanged(SubView v, ISubtitle subtitle) {
        updateSubviewLayout();
        updateUserLayout();
    }

    private void updateSubviewLayout() {
        if (this._systemWindowFittable != null) {
            if (this._subView.hasVisibleTextSubtitles()) {
                if (this._subView.getParent() == this.topLayout) {
                    this.topLayout.removeView(this._subView);
                    this._systemWindowFittable.addView(this._subView, 0);
                }
            } else if (this._subView.getParent() == this._systemWindowFittable) {
                this._systemWindowFittable.removeView(this._subView);
                this.topLayout.addView(this._subView, getTopLayoutZOrder(2));
            }
        }
    }

    private void addSubtitles(List<ISubtitle> subtitles, MediaDatabase.State states, Uri[] enabledSubs, int flags) {
        if (enabledSubs != null) {
            for (ISubtitle s : subtitles) {
                Uri uri = s.uri();
                boolean enabled = false;
                int length = enabledSubs.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        Uri enabledUri = enabledSubs[i];
                        if (!UriUtils.isPointingSameObject(uri, enabledUri)) {
                            i++;
                        } else {
                            enabled = true;
                            break;
                        }
                    }
                }
                this._subView.addSubtitle(s, enabled);
            }
        } else if (states != null && states.hasSubtitles()) {
            for (ISubtitle s2 : subtitles) {
                Uri uri2 = s2.uri();
                boolean enabled2 = false;
                MediaDatabase.State.Subtitle[] subtitleArr = states.subtitles;
                int length2 = subtitleArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 < length2) {
                        MediaDatabase.State.Subtitle subState = subtitleArr[i2];
                        if (!uri2.equals(subState.uri)) {
                            i2++;
                        } else {
                            enabled2 = subState.enabled;
                            break;
                        }
                    }
                }
                this._subView.addSubtitle(s2, enabled2);
            }
        } else {
            ISubtitle enabled3 = null;
            if ((flags & 4) != 0 || ((flags & 1) != 0 && this._subView.getEnabledSubtitleCount() == 0)) {
                List<ISubtitle> orderedSubs = new ArrayList<>(subtitles);
                Collections.sort(orderedSubs, ISubtitle.PRIORITY_COMPARATOR);
                Locale[] localeArr = P.preferredSubtitleLocales;
                int length3 = localeArr.length;
                int i3 = 0;
                loop5: while (true) {
                    if (i3 < length3) {
                        Locale preferred = localeArr[i3];
                        for (ISubtitle s3 : orderedSubs) {
                            int subFlags = s3.flags();
                            if ((131072 & subFlags) != 0 && ((flags & 2) != 0 || (65536 & subFlags) == 0)) {
                                if (preferred.equals(s3.locale())) {
                                    enabled3 = s3;
                                    break loop5;
                                }
                            }
                        }
                        i3++;
                    } else {
                        for (ISubtitle s4 : orderedSubs) {
                            int subFlags2 = s4.flags();
                            if ((131072 & subFlags2) != 0 && ((flags & 2) != 0 || (65536 & subFlags2) == 0)) {
                                enabled3 = s4;
                                break;
                            }
                        }
                    }
                }
            }
            Iterator<ISubtitle> it = subtitles.iterator();
            while (it.hasNext()) {
                ISubtitle s5 = it.next();
                this._subView.addSubtitle(s5, enabled3 == s5);
            }
        }
        if (this._subtitlePanel != null) {
            this._subtitlePanel.update();
        }
        this._externalServiceSubs = null;
        setCaptionButtonVisibility(true);
        updateSubviewLayout();
    }

    private void setCaptionButtonVisibility(boolean visible) {
        if (this._captionMenuItem != null) {
            this._captionMenuItem.setVisible(visible);
            this._captionMenuItem.setEnabled(visible);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rebuildFonts() {
        this.pp.setupFonts(true);
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onSSAPrepared(SubStationAlphaMedia ssa) {
        if (App.prefs.getBoolean(Key.SSA_FONT_IGNORE, false)) {
            this.pp.overrideFonts(P.getSubtitleFontFamilyName());
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onVideoFilteringFailed(int type) {
        if (type == 0 || type == 2) {
            this._subView.setSSARenderingMode(false, this.pp);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onEmbeddedSubtitleAdded(ISubtitle subtitle) {
        if (this.pp.isInPlaybackState()) {
            List<ISubtitle> subs = new ArrayList<>(1);
            subs.add(subtitle);
            addSubtitles(subs, this.pp.getPersistent(), getGivenVisibleSubtitleURIs(), P.autoSelectSubtitle ? 3 : 0);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onSubtitleClosed(ISubtitle subtitle) {
        this._subView.removeSubtile(subtitle);
        if (this._subtitlePanel != null) {
            this._subtitlePanel.update();
        }
        this._externalServiceSubs = null;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onSubtitlesClosed() {
        this._subView.clear();
        setCaptionButtonVisibility(false);
        if (this._subtitlePanel != null) {
            this._subtitlePanel.update();
        }
        this._externalServiceSubs = null;
    }

    private void updateSubtitleOverlayLayout() {
        if (this._fitSubtitleOverlayToVideo || !this._hasVideoTrack) {
            if (this._surfaceView != null) {
                matchSubtitleOverlayToVideo();
                return;
            }
            return;
        }
        matchSubtitleOverlayToScreen();
    }

    private void matchSubtitleOverlayToVideo() {
        RelativeLayout.LayoutParams sl = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._subtitleOverlay.getLayoutParams();
        l.leftMargin = sl.leftMargin;
        l.topMargin = sl.topMargin;
        l.rightMargin = sl.rightMargin;
        l.bottomMargin = sl.bottomMargin;
        l.width = sl.width;
        l.height = sl.height;
        int[] rules = l.getRules();
        System.arraycopy(sl.getRules(), 0, rules, 0, rules.length);
        this._subtitleOverlay.requestLayout();
    }

    @SuppressLint({"NewApi"})
    private void matchSubtitleOverlayToScreen() {
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._subtitleOverlay.getLayoutParams();
        ViewGroup parent = (ViewGroup) this._subtitleOverlay.getParent();
        if (this._zoomInsideWidth > 0.0f && this._zoomInsideHeight > 0.0f) {
            l.width = (int) this._zoomInsideWidth;
            l.height = (int) this._zoomInsideHeight;
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            if (l.width > parentWidth) {
                int gap = parentWidth - l.width;
                l.leftMargin = gap / 2;
                l.rightMargin = gap - l.leftMargin;
            } else {
                l.rightMargin = 0;
                l.leftMargin = 0;
            }
            if (l.height > parentHeight) {
                int gap2 = parentHeight - l.height;
                l.topMargin = gap2 / 2;
                l.bottomMargin = gap2 - l.topMargin;
            } else {
                l.bottomMargin = 0;
                l.topMargin = 0;
            }
            l.addRule(14, -1);
        } else {
            l.width = -1;
            l.height = -1;
            l.bottomMargin = 0;
            l.topMargin = 0;
            l.rightMargin = 0;
            l.leftMargin = 0;
        }
        this._subtitleOverlay.requestLayout();
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public final SubtitleOverlay createSubtitleOverlay(SubView subView) {
        int w;
        int h;
        if (this._subtitleOverlay != null) {
            return this._subtitleOverlay;
        }
        if (this._subtitleOverlayRef != null) {
            this._subtitleOverlay = this._subtitleOverlayRef.get();
        }
        if (this._subtitleOverlay == null) {
            this._subtitleOverlay = new SubtitleOverlay(this);
            this._subtitleOverlayRef = new SoftReference<>(this._subtitleOverlay);
        }
        this._subtitleOverlay.setFrameScale(P.subtitleScale);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        if (this.pp.mp() != null) {
            if (this._hasVideoTrack) {
                w = this.pp.getSourceWidth();
                h = this.pp.getSourceHeight();
            } else {
                w = -1;
                h = -1;
            }
            this._subtitleOverlay.setVideoSize(w, h);
        }
        this.topLayout.addView(this._subtitleOverlay, getTopLayoutZOrder(3));
        updateSubtitleOverlayLayout();
        return this._subtitleOverlay;
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public final void removeSubtitleOverlay(SubView subView, SubtitleOverlay overlay) {
        this.topLayout.removeView(overlay);
        this._subtitleOverlay = null;
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public boolean attachSubtitleToMediaPlayer(ISubtitle s) {
        FFPlayer ff = this.pp.getPrimaryFF();
        if (ff != null) {
            return ff.attachSubtitleTrack(s);
        }
        return false;
    }

    @Override // com.mxtech.videoplayer.subtitle.SubView.Client
    public void detachSubtitleFromMediaPlayer(ISubtitle s) {
        FFPlayer ff = this.pp.getPrimaryFF();
        if (ff != null) {
            ff.detachSubtitleTrack(s);
        }
    }

    private void updateSubtitleBkColor() {
        if (P.subtitleBkColorEnabled) {
            this._subView.setBackgroundColor(P.subtitleBkColor);
        } else {
            this._subView.setBackgroundDrawable(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDisplaySize(int width, int height) {
        if (this._surfaceView != null) {
            if (P.videoZoomLimited && this.pp.getDecoder() != 2) {
                width = Math.min(width, this.topLayout.getWidth());
                height = Math.min(height, this.topLayout.getHeight());
            }
            if (width > 0 && height > 0) {
                long now = SystemClock.uptimeMillis();
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
                if ((l.width != width || l.height != height) && this.pp.getDecoder() != 2 && this._lastResizeTime + this._videoZoomDelay > now) {
                    if (!this.handler.hasMessages(5)) {
                        this.handler.sendEmptyMessageAtTime(5, this._lastResizeTime + this._videoZoomDelay);
                    }
                    this._delayedResizeWidth = width;
                    this._delayedResizeHeight = height;
                    return;
                }
                this._lastResizeTime = now;
                l.width = width;
                l.height = height;
                int topWidth = this.topLayout.getWidth();
                int topHeight = this.topLayout.getHeight();
                int gap = topWidth - width;
                l.leftMargin = (gap / 2) + this._panX;
                l.rightMargin = gap - l.leftMargin;
                int gap2 = topHeight - height;
                l.topMargin = (gap2 / 2) + this._panY;
                l.bottomMargin = gap2 - l.topMargin;
                this._surfaceView.requestLayout();
                if (this._subtitleOverlay != null && this._fitSubtitleOverlayToVideo) {
                    matchSubtitleOverlayToVideo();
                }
            }
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onVideoSizeChanged(int width, int height) {
        if (width > 0 && height > 0) {
            boolean orientationSet = false;
            if (P.screenOrientation == 99999 && !this._wasSetOrientationFromThisVideo) {
                setOrientation(P.screenOrientation);
                orientationSet = true;
            }
            if (!orientationSet) {
                updateScreenRotationButton(P.screenOrientation);
            }
            if (!this._hasVideoTrack) {
                this._hasVideoTrack = true;
                removeCover();
                updateStickyState(false);
            }
            if (this._surfaceHolderCreated != null) {
                setSurfaceSize(width, height);
                if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
                    this._needToUpdateSizes = true;
                } else {
                    updateSizes();
                }
            }
        }
        if (this._subtitleOverlay != null) {
            if (this._hasVideoTrack) {
                this._subtitleOverlay.setVideoSize(width, height);
            } else {
                this._subtitleOverlay.setVideoSize(-1, -1);
            }
        }
    }

    private void setSurfaceSize(int width, int height) {
        this._surfaceHolderCreated.setFixedSize(width, height);
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public int mediaTimeToSubtitleTime(int timeMs) {
        return this._subView.mediaTimeToSubtitleTime(timeMs);
    }

    protected void updateSizes() {
        this._needToUpdateSizes = false;
        updateUserLayout();
        recalcSizingVariables();
        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
            setDisplaySize(this._zoomWidth, this._zoomHeight);
        } else {
            setZoomMode(this._videoZoom);
        }
        if (this._subtitleOverlay != null && !this._fitSubtitleOverlayToVideo) {
            matchSubtitleOverlayToScreen();
            this._subtitleOverlay.requestLayout();
        }
    }

    private void recalcSizingVariables() {
        if (this.pp.isInActiveState()) {
            int width = this.pp.getDisplayWidth();
            int height = this.pp.getDisplayHeight();
            int topWidth = this.topLayout.getWidth();
            int topHeight = this.topLayout.getHeight();
            if (width > 0 && height > 0 && topWidth > 0 && topHeight > 0) {
                int idealHeight = (topWidth * height) / width;
                if (idealHeight <= topHeight) {
                    this._zoomInsideWidth = topWidth;
                    this._zoomInsideHeight = idealHeight;
                } else {
                    int idealWidth = (topHeight * width) / height;
                    this._zoomInsideWidth = idealWidth;
                    this._zoomInsideHeight = topHeight;
                }
                int idealHeight2 = (topWidth * height) / width;
                if (idealHeight2 >= topHeight) {
                    this._zoomCropWidth = topWidth;
                    this._zoomCropHeight = idealHeight2;
                } else {
                    int idealWidth2 = (topHeight * width) / height;
                    this._zoomCropWidth = idealWidth2;
                    this._zoomCropHeight = topHeight;
                }
                this._zoomMinWidth = width * MIN_ZOOM;
                this._zoomMinHeight = height * MIN_ZOOM;
                if (this._zoomMinWidth > this._zoomInsideWidth) {
                    this._zoomMinWidth = this._zoomInsideWidth;
                    this._zoomMinHeight = this._zoomInsideHeight;
                }
                this._zoomMaxHeight = Math.max(topHeight, height) * MAX_ZOOM;
                this._zoomMaxWidth = (width * this._zoomMaxHeight) / height;
                if (this._zoomMaxWidth < this._zoomCropWidth) {
                    this._zoomMaxWidth = this._zoomCropWidth;
                    this._zoomMaxHeight = this._zoomCropHeight;
                }
                if (this._zoomWidth > 0 && this._zoomHeight > 0) {
                    this._zoomHeight = (this._zoomWidth * height) / width;
                }
            }
        }
    }

    public final void setZoomMode(int mode) {
        switch (mode) {
            case 0:
                setDisplaySize(this.topLayout.getWidth(), this.topLayout.getHeight());
                break;
            case 1:
            default:
                setDisplaySize((int) this._zoomInsideWidth, (int) this._zoomInsideHeight);
                break;
            case 2:
                setDisplaySize(this.pp.getDisplayWidth(), this.pp.getDisplayHeight());
                break;
            case 3:
                setDisplaySize((int) this._zoomCropWidth, (int) this._zoomCropHeight);
                break;
        }
        if (this._zoomButton != null) {
            this._zoomButton.getDrawable().setLevel(getNextZoomMode(mode));
        }
    }

    public final Point getSizeFor(int zoomMode) {
        switch (zoomMode) {
            case 0:
                return new Point(this.topLayout.getWidth(), this.topLayout.getHeight());
            case 1:
            default:
                return new Point((int) this._zoomInsideWidth, (int) this._zoomInsideHeight);
            case 2:
                return new Point(this.pp.getDisplayWidth(), this.pp.getDisplayHeight());
            case 3:
                return new Point((int) this._zoomCropWidth, (int) this._zoomCropHeight);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void load() {
        if (this.pp.getState() == 1) {
            load(null, this._loadingFlags, this._loadingRequestedDecoder, this._loadingVideoFlags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void load(Uri uri, int flags, byte requestedDecoder, int videoFlags) {
        String resumeLast;
        int dataFlags;
        boolean askResume;
        boolean startover;
        if (this._loadHoldCount > 0) {
            Log.d(TAG, "Loading holded. (count=" + this._loadHoldCount + ")");
        } else if (this.pp.getClient() != this) {
            Log.d(TAG, "Player is bound to other object: " + this.pp.getClient());
        } else {
            int state = this.pp.getState();
            this._loadingFlags = flags;
            this._loadingRequestedDecoder = requestedDecoder;
            this._loadingVideoFlags = videoFlags;
            if (state == 0) {
                arrangeExtras(getIntent(), uri);
                if ((this._loadingFlags & 32) == 0 && App.prefs.getBoolean(Key.RESUME_ONLY_FIRST, false)) {
                    resumeLast = P.VALUE_STARTOVER;
                } else {
                    resumeLast = App.prefs.getString(Key.RESUME_LAST, P.VALUE_ASK);
                }
                if ((this._loadingFlags & 4) != 0 || P.VALUE_RESUME.equals(resumeLast)) {
                    dataFlags = 1;
                    askResume = false;
                    startover = false;
                } else if (P.VALUE_STARTOVER.equals(resumeLast)) {
                    if (P.rememberPlaybackSelections) {
                        dataFlags = 1;
                    } else {
                        dataFlags = 0;
                    }
                    askResume = false;
                    startover = true;
                } else {
                    dataFlags = 1;
                    askResume = true;
                    startover = false;
                }
                this._subtitleServiceMedia = null;
                this._subtitleServiceMediaTried = false;
                this.pp.setDataSource(uri, requestedDecoder, 0, dataFlags);
                this._wasSetOrientationFromThisVideo = false;
                MediaDatabase.State persist = this.pp.getPersistent();
                if (persist != null) {
                    if (persist.canResume()) {
                        if (askResume) {
                            updateTitle();
                            new ResumeAsker();
                            return;
                        } else if (startover) {
                            persist.startOver();
                        }
                    } else {
                        persist.startOver();
                    }
                }
            } else if (state != 1) {
                Log.w(TAG, "Invalid state while loading: " + state);
                return;
            }
            if (checkDecodingEnvironment()) {
                if (this.pp.getPersistent() != null) {
                    this._loadingVideoFlags &= -129;
                }
                this._surfaceBoundDecoder = this.pp.getDecoder();
                this.pp.load(this._surfaceHolderCreated, this._loadingVideoFlags, this._useSpeedupTricks);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkDecodingEnvironment() {
        byte decoder = this.pp.getDecoder();
        if (!canUseStickyMode()) {
            this._stickyRequested = false;
        }
        if (this._surfaceView != null && !canUseSurface(this._surfaceType, this._surfaceBoundDecoder, decoder)) {
            Log.v(TAG, "Can't reuse surface view. surface-type=" + this._surfaceType + " surface-bound-decoder=" + ((int) this._surfaceBoundDecoder) + " new-decoder=" + ((int) decoder));
            removeSurfaceView();
            createSurfaceView();
        }
        return this._surfaceHolderCreated != null;
    }

    private boolean canUseSurface(int type, byte lastDecoder, byte newDecoder) {
        boolean z = false;
        if (Build.VERSION.SDK_INT < 11) {
            return surfaceTypeForDecoder(newDecoder) == type;
        }
        switch (lastDecoder) {
            case 1:
                return newDecoder == 1;
            case 2:
                return newDecoder == 2;
            case 3:
            default:
                return true;
            case 4:
                if (newDecoder == 1 || newDecoder == 4) {
                    z = true;
                }
                return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canUseStickyMode() {
        return Build.VERSION.SDK_INT >= 14 || this.pp.getDecoder() != 1;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onRebootToChangeDisplay(int videoFlags) {
        load(null, 1, (byte) 0, videoFlags);
    }

    /* loaded from: classes.dex */
    private class TryNextModeDialogHandler extends AlertDialog.Builder implements DialogInterface.OnDismissListener {
        TryNextModeDialogHandler(byte previousDecoder) {
            super(ActivityScreen.this);
            super.setMessage(StringUtils.getString_s(R.string.decoder_mode_failure, L.getDecoderText(ActivityScreen.this, previousDecoder)));
            super.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            ActivityScreen.this.showDialog((ActivityScreen) create(), (DialogInterface.OnDismissListener) this);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            ActivityScreen.this.dialogRegistry.unregister(dialog);
            if (!ActivityScreen.this.isFinishing()) {
                ActivityScreen.this.load();
            }
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onTryNextDecoder(byte failedDecoder, byte nextDecoder, boolean initialError) {
        if (!isFinishing()) {
            this._loadingRequestedDecoder = nextDecoder;
            if (initialError) {
                this._loadingVideoFlags |= 128;
                if ((this._loadingFlags & 16) != 0) {
                    this._loadingFlags &= -17;
                    new TryNextModeDialogHandler(failedDecoder);
                    return;
                }
                Log.i(TAG, "Trying next decoder: " + ((int) failedDecoder) + " -> " + ((int) nextDecoder));
                load();
                return;
            }
            load(null, 3, nextDecoder, 128);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onVideoDeviceChanged() {
        doUpdateDirectRendering();
    }

    public static void arrangeExtras(Intent intent, Uri newUri) {
        int index = -1;
        Parcelable[] uris = intent.getParcelableArrayExtra(EXTRA_LIST);
        if (uris != null) {
            int i = 0;
            while (true) {
                if (i >= uris.length) {
                    break;
                } else if (!((Uri) uris[i]).equals(newUri)) {
                    i++;
                } else {
                    index = i;
                    break;
                }
            }
        }
        if (index >= 0) {
            String[] titles = intent.getStringArrayExtra(EXTRA_TITLES);
            long[] sizes = intent.getLongArrayExtra(EXTRA_SIZES);
            String[] filenames = intent.getStringArrayExtra(EXTRA_FILENAMES);
            String[] os_hashes = intent.getStringArrayExtra(EXTRA_HASHES_OPENSUBTITLES);
            if (titles != null && titles.length == uris.length) {
                String newTitle = titles[index];
                if (newTitle != null) {
                    intent.putExtra("title", newTitle);
                } else {
                    intent.removeExtra("title");
                }
            }
            if (sizes != null && sizes.length == uris.length) {
                long newSize = sizes[index];
                if (newSize != 0) {
                    intent.putExtra(EXTRA_SIZE, newSize);
                } else {
                    intent.removeExtra(EXTRA_SIZE);
                }
            }
            if (filenames != null && filenames.length == uris.length) {
                String newFilename = filenames[index];
                if (newFilename != null) {
                    intent.putExtra(EXTRA_FILENAME, newFilename);
                } else {
                    intent.removeExtra(EXTRA_FILENAME);
                }
            }
            if (os_hashes != null && os_hashes.length == uris.length) {
                String newHash = os_hashes[index];
                if (newHash != null) {
                    intent.putExtra(EXTRA_HASH_OPENSUBTITLES, newHash);
                } else {
                    intent.removeExtra(EXTRA_HASH_OPENSUBTITLES);
                }
            }
        } else {
            intent.removeExtra("title");
            intent.removeExtra(EXTRA_SIZE);
            intent.removeExtra(EXTRA_FILENAME);
            intent.removeExtra(EXTRA_HASH_OPENSUBTITLES);
        }
        intent.removeExtra(EXTRA_POSITION);
        intent.removeExtra(EXTRA_SUBTITLES);
        intent.removeExtra(EXTRA_SUBTITLES_ENABLE);
        intent.removeExtra(EXTRA_SUBTITLES_NAME);
        intent.removeExtra(EXTRA_SUBTITLES_FILENAME);
    }

    private String getMediaTitle() {
        String givenTitle = getIntent().getStringExtra("title");
        return givenTitle != null ? givenTitle : this.pp.getTitle();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateTitle() {
        int resId;
        int i = 0;
        CharSequence title = getMediaTitle();
        if (L.sleepTimer != null) {
            Configuration config = getResources().getConfiguration();
            if (config.orientation == 2 && (config.screenLayout & 15) < 3) {
                resId = R.drawable.ic_crescent_12dp;
            } else {
                resId = R.drawable.ic_crescent_16dp;
            }
            Drawable d = getResources().getDrawable(resId);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            d.mutate().setAlpha(153);
            d.setColorFilter(ScreenStyle.getInstance().getControlNormalColorFilter());
            ImageSpan span = new ImageSpan(d, (11 > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > 13) ? 1 : 1);
            int len = title.length();
            SpannableStringBuilder ssb = new SpannableStringBuilder(title);
            ssb.append((CharSequence) "  ");
            ssb.setSpan(span, len + 1, len + 2, 33);
            title = ssb;
        }
        setTitle(title);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getTitleNoDecor() {
        return getTitle().toString().trim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDecoderIndicator() {
        if (this.pp.isInValidState() && this._decoderMenuItem != null) {
            this._decoderMenuItem.setTitle(L.getDecoderAbbrText(this, this.pp.getDecoder()));
        }
    }

    /* loaded from: classes.dex */
    private class DecoderSwitcher extends BaseAdapter implements View.OnClickListener, DialogInterface.OnClickListener {
        final int[] DECODER_NAMES;
        final int IDX_HW;
        final int IDX_OMX;
        final int IDX_SW;
        private int _current;
        private boolean _disableOMX;
        private int _itemLayoutId;
        private LayoutInflater _layoutInflater;

        DecoderSwitcher() {
            FFPlayer ff;
            if (P.isOMXDecoderVisible()) {
                this.DECODER_NAMES = new int[]{R.string.decoder_hw, R.string.decoder_omx, R.string.decoder_sw};
                this.IDX_HW = 0;
                this.IDX_OMX = 1;
                this.IDX_SW = 2;
                JointPlayer mp = ActivityScreen.this.pp.mp();
                if (mp != null && (ff = mp.getFFPlayer()) != null && !ff.canSwitchToOMXDecoder()) {
                    this._disableOMX = true;
                    return;
                }
                return;
            }
            this.DECODER_NAMES = new int[]{R.string.decoder_hw, R.string.decoder_sw};
            this.IDX_HW = 0;
            this.IDX_OMX = 0;
            this.IDX_SW = 1;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            invoke();
        }

        void invoke() {
            if (!ActivityScreen.this.isFinishing() && ActivityScreen.this.pp.isInActiveState() && ActivityScreen.this.dialogRegistry.size() <= 0) {
                switch (ActivityScreen.this.pp.getDecoder()) {
                    case 1:
                        this._current = this.IDX_HW;
                        break;
                    case 2:
                        this._current = this.IDX_SW;
                        break;
                    case 4:
                        this._current = this.IDX_OMX;
                        break;
                }
                AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.decoder_selector_title).setSingleChoiceItems(this, this._current, this).create();
                TypedArray a = ActivityScreen.this.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
                this._itemLayoutId = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 17367058);
                a.recycle();
                this._layoutInflater = dlg.getLayoutInflater();
                ActivityScreen.this.showDialog((ActivityScreen) dlg);
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.DECODER_NAMES.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return !this._disableOMX;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return (this._disableOMX && position == this.IDX_OMX) ? false : true;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = this._layoutInflater.inflate(this._itemLayoutId, parent, false);
            }
            TextView tv = (TextView) v.findViewById(16908308);
            tv.setEnabled(isEnabled(position));
            tv.setText(this.DECODER_NAMES[position]);
            return v;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            byte decoder;
            dialog.dismiss();
            if (!ActivityScreen.this.isFinishing() && ActivityScreen.this.pp.isInActiveState() && which != this._current && isEnabled(which)) {
                ActivityScreen.this.pp.save();
                if (which == this.IDX_HW) {
                    decoder = 1;
                } else if (which == this.IDX_OMX) {
                    decoder = 4;
                } else {
                    decoder = 2;
                }
                ActivityScreen.this.pp.changeDecoder(decoder);
                if (!ActivityScreen.this.pp.isInActiveState()) {
                    ActivityScreen.this.load(null, 19, decoder, 0);
                    return;
                }
                ActivityScreen.this._loadingFlags |= 19;
                ActivityScreen.this.pp.setVerbose();
                ActivityScreen.this.checkDecodingEnvironment();
                ActivityScreen.this.updateDecoderIndicator();
            }
        }
    }

    private void updateAudioMediaListener() {
        if (Build.VERSION.SDK_INT >= 8) {
            if (this._audioFocusManager == null) {
                this._audioFocusManager = new AudioFocusManager(this, getClass().getSimpleName());
            }
            this._audioFocusManager.update();
        }
    }

    @Override // com.mxtech.videoplayer.widget.AudioFocusManager.Client
    public void onGainAudioFocus(AudioFocusManager manager) {
        resume();
    }

    @Override // com.mxtech.videoplayer.widget.AudioFocusManager.Client
    public void onLoseAudioFocus(AudioFocusManager manager, boolean temporary) {
        pause((temporary ? 32 : 0) | 64);
    }

    @Override // com.mxtech.videoplayer.widget.AudioFocusManager.Client
    public boolean needAudioFocus(AudioFocusManager manager) {
        return this.started;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public boolean canStart() {
        if (this._consecutiveSeekBegin >= 0 || this.pp.isFontSettingUp()) {
            return false;
        }
        if (isSticky() || (this.foreground && !L.keyguardManager.inKeyguardRestrictedInputMode() && this.dialogRegistry.size() <= 0)) {
            if (Build.VERSION.SDK_INT >= 8) {
                if (this._audioFocusManager == null) {
                    this._audioFocusManager = new AudioFocusManager(this, getClass().getSimpleName());
                }
                if (!this._audioFocusManager.canStartPlayback()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void pause(int flags) {
        if (this.pp.getClient() == this) {
            this._lastPauseFlags = flags;
            this.pp.pause(flags);
            this._lastPauseFlags = 0;
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void resume() {
        this.pp.resume();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void toggle(boolean silent) {
        if (this.pp.getTargetState() == 4) {
            pause(silent ? 64 : 0);
        } else {
            start();
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void start() {
        updateAudioMediaListener();
        this.pp.start();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final boolean isSeekable() {
        return this.pp.isSeekable();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final boolean isInPlaybackState() {
        return this.pp.isInPlaybackState();
    }

    protected final void loadNext(Uri uri, int direction) {
        this.pp.setDisplay(null, 8);
        this.pp.clear(16);
        this._lastLoadingDirection = direction;
        load(uri, 0, getIntent().getByteExtra(EXTRA_DECODE_MODE, (byte) 0), 128);
        if (this._surfaceView == null) {
            createSurfaceView();
        }
        start();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void previous() {
        boolean canRestart = true;
        if (this.pp.isInValidState()) {
            if (!this.pp.isSeekable() || !App.prefs.getBoolean(Key.SMART_PREV, true) || !this.pp.isInPlaybackState()) {
                canRestart = false;
            }
            if (canRestart && this.pp.getCurrentPosition() >= 3000) {
                this.pp.seekTo(0, 6000);
                return;
            }
            Uri prev = this._lister.getAndUpdate(this.pp.getUri(), -1);
            if (prev != null) {
                this.pp.save();
                loadNext(prev, -1);
            }
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void next() {
        if (this.pp.isInValidState()) {
            int direction = P.shuffle ? 0 : 1;
            Uri next = this._lister.getAndUpdate(this.pp.getUri(), direction);
            if (next != null) {
                this.pp.save();
                loadNext(next, direction);
            }
        }
    }

    private void nextAuto(boolean immediate) {
        if (!P.playerBackToList) {
            if (P.playerLooping == 1) {
                if (immediate) {
                    if (this.pp.isSeekable()) {
                        pause(80);
                        this.pp.seekTo(0, 6000);
                    } else {
                        Uri uri = this.pp.getUri();
                        byte decoder = this.pp.getDecoder();
                        this.pp.clear(16);
                        load(uri, 0, decoder, 0);
                    }
                    start();
                    return;
                }
                this.handler.sendMessageDelayed(this.handler.obtainMessage(8, 1, 0), 100L);
                return;
            }
            int direction = P.shuffle ? 0 : 1;
            Uri next = this._lister.getAndUpdate(this.pp.getUri(), direction);
            if (next != null) {
                loadNext(next, direction);
                return;
            }
        }
        returnOk(kEndByPlaybackCompletion);
        this.pp.clear();
        if (this._lock == null || !this._lock.isKidsLock()) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void skipCurrent() {
        Uri current;
        Uri uri;
        if (!P.playerBackToList && (current = this.pp.getUri()) != null && (uri = this._lister.get(current, this._lastLoadingDirection)) != null) {
            loadNext(uri, this._lastLoadingDirection);
            return;
        }
        returnResult(1);
        finish();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void seekTo(int pos, int timeout) {
        if (this.pp.isInPlaybackState()) {
            this.pp.seekTo(pos, timeout);
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public double getSpeed() {
        return this.pp.getSpeed();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public void setSpeed(double speed) {
        this.pp.setSpeed(speed);
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void update(Player player, int position) {
        update(position, true);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public void update(int position) {
        update(position, true);
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onSeekBegin(int pos) {
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onSeekComplete() {
        update(this.pp.getCurrentPosition(), false);
    }

    protected void update(int pos, boolean animateHide) {
        if (this._consecutiveSeekBegin < 0) {
            setVideoProgress(pos);
        }
        this._subView.update(pos, false, animateHide);
    }

    /* loaded from: classes.dex */
    private final class NaviHandler implements View.OnClickListener, View.OnTouchListener {
        private final int _secTo;

        NaviHandler(int direction) {
            this._secTo = direction;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ActivityScreen.this.seekToDelta(this._secTo * ActivityScreen.this._naviMoveIntervalMillis, ActivityScreen.this._naviMoving ? 0 : 1);
            if (ActivityScreen.this._naviMoving) {
                ActivityScreen.this.consecutiveSeekBegin();
            } else {
                ActivityScreen.this.resume();
            }
        }

        @Override // android.view.View.OnTouchListener
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            if (ActivityScreen.this.pp.isInPlaybackState()) {
                int action = event.getAction();
                if (action == 0) {
                    ActivityScreen.this._naviMoving = true;
                    if (!ActivityScreen.this.showSeekPreviews()) {
                        ActivityScreen.this.pause(80);
                    }
                } else if (action == 3 || action == 1) {
                    ActivityScreen.this._naviMoving = false;
                    if (ActivityScreen.this._consecutiveSeekBegin >= 0) {
                        ActivityScreen.this._consecutiveSeekBegin = -1;
                        ActivityScreen.this.seekTo(ActivityScreen.this._seekBar.getProgress(), 6000);
                        ActivityScreen.this.resume();
                    }
                }
            }
            return false;
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle saved) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        AppUtils.dumpParams(TAG, this, "onCreate", intent, saved);
        P.applyHardwareAccelerationToWindow(this);
        setTheme(P.getPlaybackTheme());
        super.onCreate(saved);
        this._tracker = ((App) App.context).getDefaultTracker();
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
        this._useSpeedupTricks = App.prefs.getBoolean(Key.USE_SPEEDUP_TRICKS, false);
        this._displaySeekingPosition = App.prefs.getBoolean(Key.DISPLAY_SEEKING_POSITION, true);
        this._showScreenRotateButton = !DeviceUtils.isTV && App.prefs.getBoolean(Key.SCREEN_ROTATION_BUTTON, true);
        this._defaultStickyAudio = App.prefs.getBoolean(Key.STICKY_AUDIO, true);
        this._stereoMode = App.prefs.getInt(Key.STEREO_MODE, 0);
        if (extras != null && extras.containsKey("video_zoom")) {
            this._videoZoom = extras.getInt("video_zoom");
        } else {
            this._videoZoom = App.prefs.getInt("video_zoom", 1);
        }
        if (extras != null && extras.containsKey("sticky")) {
            this._defaultSticky = extras.getBoolean("sticky");
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
        this._subView.setPlayer(this);
        this._subView.setClient(this);
        this._seekBar.setOnSeekBarChangeListener(this);
        if (P.getItalicTag()) {
            this._subView.enableItalicTag(true);
        }
        this.topLayout.activity = this;
        this.uiLayout.activity = this;
        setActionBarShowAnimation(R.anim.fast_fade_in);
        setActionBarHideAnimation(R.anim.fast_fade_out);
        this._controller.setShowAnimation(R.anim.fast_fade_in);
        this._controller.setHideAnimation(R.anim.fast_fade_out);
        reconfigHttpFactory();
        this.pp = new Player(this, this, this._httpFactory);
        if (App.prefs.getBoolean(Key.KEEP_SCREEN_ON, false)) {
            this.topLayout.setKeepScreenOn(true);
        }
        this._pauseIfObscured = App.prefs.getBoolean(Key.PAUSE_IF_OBSTRUCTED, false);
        if (Build.VERSION.SDK_INT >= 11) {
            this._lastSystemUIVisibility = this.topLayout.getSystemUiVisibility();
            this._systemUIVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() { // from class: com.mxtech.videoplayer.ActivityScreen.1
                @Override // android.view.View.OnSystemUiVisibilityChangeListener
                @TargetApi(11)
                public void onSystemUiVisibilityChange(int visibility) {
                    if (ActivityScreen.this.started && ActivityScreen.this._lastSystemUIVisibility != visibility) {
                        ActivityScreen.this._lastSystemUIVisibility = visibility;
                        if (visibility == 0) {
                            if (P.softButtonsVisibility != 0 && P.uiVersion < 19) {
                                switch (P.playbackTouchAction) {
                                    case 1:
                                        Log.v(ActivityScreen.TAG, "Toggle playback silently in response to system ui visibility change to " + ((Object) ViewUtils.toSystemUiString(visibility)) + " (=" + visibility + ", state=" + ActivityScreen.this.pp.getState() + ')');
                                        ActivityScreen.this.toggle(true);
                                        break;
                                    case 2:
                                        ActivityScreen.this.showController();
                                        break;
                                    case 3:
                                        ActivityScreen.this.showController();
                                        ActivityScreen.this.pp.pause(0);
                                        break;
                                    default:
                                        ActivityScreen.this.showController();
                                        break;
                                }
                                ActivityScreen.this._lastSwiping = 0;
                                ActivityScreen.this._lastSingleTapTime = SystemClock.uptimeMillis();
                            }
                            if (ActivityScreen.this._autoHideInterface) {
                                ActivityScreen.this.handler.removeMessages(6);
                                ActivityScreen.this.handler.sendEmptyMessageDelayed(6, P.getInterfaceAutoHideDelay(this));
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
                ActivityScreen.this._dirty |= 8;
                ActivityScreen.this.setShowLeftTime(!ActivityScreen.this._showLeftTime);
                ActivityScreen.this.scheduleFlush();
            }
        });
        setProgressText(0, true);
        this._screenRotateButton.setOnClickListener(new ScreenRotateButtonHandler());
        if (this._playPauseButton != null) {
            PlayPauseButtonHandler handler = new PlayPauseButtonHandler();
            this._playPauseButton.setOnClickListener(handler);
            this._playPauseButton.setOnLongClickListener(handler);
        }
        if (this._prevButton != null) {
            this._prevButton.setOnClickListener(new PrevButtonHandler());
        }
        if (this._nextButton != null) {
            NextButtonHandler handler2 = new NextButtonHandler();
            this._nextButton.setOnClickListener(handler2);
            this._nextButton.setOnLongClickListener(handler2);
        }
        onShowMoveButtons(null, P.getNaviShowMoveButtons());
        onShowPrevNextButtons(null, App.prefs.getBoolean(Key.SHOW_PREV_NEXT, true));
        if (this._backwardButton != null) {
            NaviHandler handler3 = new NaviHandler(-1);
            this._backwardButton.setZoomSpeed(MOVE_REPEAT_SPEED);
            this._backwardButton.setOnTouchListener(handler3);
            this._backwardButton.setOnClickListener(handler3);
        }
        if (this._forwardButton != null) {
            NaviHandler handler4 = new NaviHandler(1);
            this._forwardButton.setZoomSpeed(MOVE_REPEAT_SPEED);
            this._forwardButton.setOnTouchListener(handler4);
            this._forwardButton.setOnClickListener(handler4);
        }
        if (DeviceUtils.isTV) {
            this._lockButton.setImageDrawable(null);
            this._lockButton.setEnabled(false);
            this._lockButton.setFocusable(false);
            this._prevButton.setNextFocusLeftId(R.id.zoom);
            this._zoomButton.setNextFocusRightId(R.id.prev);
        } else {
            this._lockButtonHandler = new LockButtonHandler();
        }
        this._zoomButtonHandler = new ZoomButtonHandler();
        this._zoomButton.setOnLongClickListener(this._zoomButtonHandler);
        this._zoomButton.setOnClickListener(this._zoomButtonHandler);
        if (((MXApplication) getApplication()).initInteractive(this)) {
            this._supremeFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
            this._supremeFadeOut.setAnimationListener(new SupremeFadeOutHandler());
            this.uiLayout.setController(this._controller);
            this.uiLayout.setOnTouchListener(this);
            this._controller.setOnVisibilityChangedListener(this);
            this._controller.bringToFront();
            this._controller.setPlayer(this.pp);
            ScreenStyle style = ScreenStyle.getInstance();
            setStyle(style, -1);
            style.addOnChangeListener(this);
            applyIntent(getIntent(), saved);
            App.prefs.registerOnSharedPreferenceChangeListener(this);
        }
    }

    private void updateLayoutForZoomPan() {
        if (this._surfaceView != null) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
            l.addRule(13, ((this._gestures & 6) != 0 || this._doubleTapZooming) ? 0 : -1);
            this._surfaceView.requestLayout();
            if (this._subtitleOverlay != null) {
                updateSubtitleOverlayLayout();
            }
        }
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        boolean z = true;
        char c = 65535;
        switch (key.hashCode()) {
            case -2134478533:
                if (key.equals(Key.DISPLAY_SEEKING_POSITION)) {
                    c = '/';
                    break;
                }
                break;
            case -2130845108:
                if (key.equals(Key.INTERFACE_AUTO_HIDE)) {
                    c = 3;
                    break;
                }
                break;
            case -2085891459:
                if (key.equals(Key.SCREEN_ORIENTATION)) {
                    c = '+';
                    break;
                }
                break;
            case -2062656836:
                if (key.equals(Key.NAVI_SHOW_MOVE_BUTTONS)) {
                    c = '\b';
                    break;
                }
                break;
            case -2002887168:
                if (key.equals(Key.STICKY_AUDIO)) {
                    c = '\r';
                    break;
                }
                break;
            case -1926120873:
                if (key.equals(Key.SUBTITLE_BORDER_COLOR)) {
                    c = 24;
                    break;
                }
                break;
            case -1774608526:
                if (key.equals(Key.BUTTON_BACKLIGHT_OFF)) {
                    c = '(';
                    break;
                }
                break;
            case -1661509926:
                if (key.equals(Key.PLAYBACK_TOUCH_ACTION)) {
                    c = '-';
                    break;
                }
                break;
            case -1648671133:
                if (key.equals(Key.SUBTITLE_SCALE)) {
                    c = 20;
                    break;
                }
                break;
            case -1625193145:
                if (key.equals(Key.IMPROVE_SSA_RENDERING)) {
                    c = 2;
                    break;
                }
                break;
            case -1617666793:
                if (key.equals("video_zoom")) {
                    c = '2';
                    break;
                }
                break;
            case -1501707373:
                if (key.equals(Key.SUBTITLE_BKCOLOR)) {
                    c = 26;
                    break;
                }
                break;
            case -1399387045:
                if (key.equals(Key.VIDEO_ZOOM_DELAY)) {
                    c = '0';
                    break;
                }
                break;
            case -1363594690:
                if (key.equals(Key.VOLUME_BOOST)) {
                    c = '&';
                    break;
                }
                break;
            case -1171355494:
                if (key.equals(Key.NAVI_MOVE_INTERVAL)) {
                    c = '\n';
                    break;
                }
                break;
            case -1069594120:
                if (key.equals(Key.PAUSE_IF_OBSTRUCTED)) {
                    c = '\"';
                    break;
                }
                break;
            case -954424000:
                if (key.equals(Key.TV_MODE)) {
                    c = 0;
                    break;
                }
                break;
            case -949907189:
                if (key.equals(Key.ITALIC_TAG)) {
                    c = ')';
                    break;
                }
                break;
            case -905241840:
                if (key.equals(Key.SUBTITLE_FONT_STYLE)) {
                    c = 19;
                    break;
                }
                break;
            case -892259863:
                if (key.equals("sticky")) {
                    c = '1';
                    break;
                }
                break;
            case -693423350:
                if (key.equals(Key.SCREEN_BRIGHTNESS_AUTO)) {
                    c = '\f';
                    break;
                }
                break;
            case -693044695:
                if (key.equals(Key.SUBTITLE_SHADOW_ENABLED)) {
                    c = '!';
                    break;
                }
                break;
            case -667694992:
                if (key.equals(Key.OSD_TEXT_COLOR)) {
                    c = 28;
                    break;
                }
                break;
            case -570462981:
                if (key.equals(Key.SUBTITLE_FIT_OVERLAY_TO_VIDEO)) {
                    c = 15;
                    break;
                }
                break;
            case -438284523:
                if (key.equals(Key.BATTERY_CLOCK_IN_TITLE_BAR)) {
                    c = 27;
                    break;
                }
                break;
            case -295951080:
                if (key.equals(Key.STATUS_BAR_SHOW_ALWAYS)) {
                    c = 5;
                    break;
                }
                break;
            case -268858280:
                if (key.equals(Key.SUBTITLE_TEXT_COLOR)) {
                    c = 23;
                    break;
                }
                break;
            case -151612022:
                if (key.equals(Key.STEREO_MODE)) {
                    c = 14;
                    break;
                }
                break;
            case -146320061:
                if (key.equals(Key.SHOW_LEFT_TIME)) {
                    c = 22;
                    break;
                }
                break;
            case -127445846:
                if (key.equals(Key.OSD_BACK_COLOR)) {
                    c = 29;
                    break;
                }
                break;
            case -36018744:
                if (key.equals(Key.SUBTITLE_BOTTOM_PADDING)) {
                    c = '5';
                    break;
                }
                break;
            case 26323580:
                if (key.equals(Key.SUBTITLE_ALIGNMENT)) {
                    c = '#';
                    break;
                }
                break;
            case 110066619:
                if (key.equals(Key.FULLSCREEN)) {
                    c = '*';
                    break;
                }
                break;
            case 115448781:
                if (key.equals(Key.OSD_BACKGROUND)) {
                    c = ' ';
                    break;
                }
                break;
            case 294493941:
                if (key.equals(Key.USE_SPEEDUP_TRICKS)) {
                    c = 11;
                    break;
                }
                break;
            case 479586408:
                if (key.equals(Key.SUBTITLE_BORDER_THICKNESS)) {
                    c = 7;
                    break;
                }
                break;
            case 568373612:
                if (key.equals(Key.HTTP_USER_AGENT)) {
                    c = 1;
                    break;
                }
                break;
            case 708258608:
                if (key.equals(Key.SUBTITLE_TEXT_SIZE)) {
                    c = 17;
                    break;
                }
                break;
            case 756440255:
                if (key.equals(Key.CORRECT_HW_ASPECT_RATIO)) {
                    c = 16;
                    break;
                }
                break;
            case 1164026052:
                if (key.equals(Key.STATUS_TEXT_SHOW_ALWAYS)) {
                    c = 4;
                    break;
                }
                break;
            case 1240838805:
                if (key.equals(Key.SUBTITLE_BKCOLOR_ENABLED)) {
                    c = 25;
                    break;
                }
                break;
            case 1497303360:
                if (key.equals(Key.SCREEN_ROTATION_BUTTON)) {
                    c = ',';
                    break;
                }
                break;
            case 1564413528:
                if (key.equals(Key.KEEP_SCREEN_ON)) {
                    c = '.';
                    break;
                }
                break;
            case 1565066941:
                if (key.equals(Key.SHOW_PREV_NEXT)) {
                    c = '%';
                    break;
                }
                break;
            case 1679844069:
                if (key.equals(Key.QUICK_ZOOM)) {
                    c = '4';
                    break;
                }
                break;
            case 1680545707:
                if (key.equals(Key.GESTURE_SEEK_SPEED)) {
                    c = 31;
                    break;
                }
                break;
            case 1735689732:
                if (key.equals(Key.SCREEN_BRIGHTNESS)) {
                    c = 6;
                    break;
                }
                break;
            case 1762644523:
                if (key.equals(Key.SUBTITLE_FADEOUT)) {
                    c = '$';
                    break;
                }
                break;
            case 1770588234:
                if (key.equals(Key.OSD_BOTTOM)) {
                    c = '3';
                    break;
                }
                break;
            case 1864575285:
                if (key.equals(Key.SUBTITLE_BORDER_ENABLED)) {
                    c = 30;
                    break;
                }
                break;
            case 1873242756:
                if (key.equals(Key.SSA_FONT_IGNORE)) {
                    c = 21;
                    break;
                }
                break;
            case 1920544902:
                if (key.equals(Key.SYNC_SYSTEM_VOLUME)) {
                    c = '\'';
                    break;
                }
                break;
            case 1967475786:
                if (key.equals(Key.GESTURES)) {
                    c = '\t';
                    break;
                }
                break;
            case 2048841036:
                if (key.equals(Key.SUBTITLE_FONT_NAME)) {
                    c = 18;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                returnOk(kEndByUser);
                finish();
                return;
            case 1:
                reconfigHttpFactory();
                return;
            case 2:
                doUpdateDirectRendering();
                return;
            case 3:
                if (!this.pp.isInPlaybackState() || !this._hasVideoTrack || !P.interfaceAutoHide) {
                    z = false;
                }
                this._autoHideInterface = z;
                this._controller.enableAutoHide(this._autoHideInterface);
                return;
            case 4:
                this._alwaysShowStatusText = prefs.getBoolean(key, false);
                updateBatteryClockIntentReceiver();
                updateStatusLayout();
                return;
            case 5:
                if (!P.oldTablet || !prefs.getBoolean(key, false)) {
                    z = false;
                }
                this._alwaysShowStatusBar = z;
                updateSystemLayout();
                return;
            case 6:
                changeBrightness(P.getBrightness());
                return;
            case 7:
                float thickness = prefs.getFloat(key, 0.08f);
                this._subView.setBorderThickness(thickness, thickness);
                return;
            case '\b':
                onShowMoveButtons(null, prefs.getBoolean(Key.NAVI_SHOW_MOVE_BUTTONS, false));
                return;
            case '\t':
                this._gestures = P.getGestures();
                updateLayoutForZoomPan();
                return;
            case '\n':
                this._naviMoveIntervalMillis = App.prefs.getInt(Key.NAVI_MOVE_INTERVAL, 10) * 1000;
                return;
            case 11:
                this._useSpeedupTricks = prefs.getBoolean(key, false);
                if (this.pp.isInActiveState()) {
                    this.pp.useSpeedupTricks(this._useSpeedupTricks);
                    return;
                }
                return;
            case '\f':
                changeBrightness(P.getBrightness());
                return;
            case '\r':
                this._defaultStickyAudio = prefs.getBoolean(Key.STICKY_AUDIO, true);
                updateStickyState(false);
                return;
            case 14:
                this._stereoMode = App.prefs.getInt(Key.STEREO_MODE, 0);
                updateStereoMode();
                return;
            case 15:
                this._fitSubtitleOverlayToVideo = prefs.getBoolean(Key.SUBTITLE_FIT_OVERLAY_TO_VIDEO, true);
                if (this._subtitleOverlay != null) {
                    updateSubtitleOverlayLayout();
                    return;
                }
                return;
            case 16:
                if (this.pp.mp() != null) {
                    this.pp.recalcVideoSize();
                    return;
                }
                return;
            case 17:
                this._subView.setTextSize(prefs.getFloat(Key.SUBTITLE_TEXT_SIZE, 20.0f));
                return;
            case 18:
            case 19:
                updateTypeface();
                return;
            case 20:
                setSubtitleScale(P.subtitleScale);
                return;
            case 21:
                this.pp.overrideFonts(prefs.getBoolean(key, false) ? P.getSubtitleFontFamilyName() : null);
                return;
            case 22:
                boolean newVal = prefs.getBoolean(Key.SHOW_LEFT_TIME, false);
                if (newVal != this._showLeftTime) {
                    setShowLeftTime(newVal);
                    return;
                }
                return;
            case 23:
                this._subView.setTextColor(P.subtitleTextColor);
                return;
            case 24:
                this._subView.setBorderColor(P.subtitleBorderColor);
                return;
            case 25:
            case 26:
                updateSubtitleBkColor();
                return;
            case 27:
                if (this._batteryClockMenuItem != null) {
                    this._batteryClockInTitleBar = P.getBatteryClockInTitleBar();
                    updateBatteryClockInTitleBar();
                    updateBatteryClock();
                    return;
                }
                return;
            case 28:
                changeOSDTextColor(prefs.getInt(Key.OSD_TEXT_COLOR, P.DEFAULT_OSD_TEXT_COLOR));
                return;
            case 29:
                changeOSDBackColor(prefs.getInt(Key.OSD_BACK_COLOR, P.DEFAULT_OSD_BACK_COLOR));
                return;
            case 30:
                this._subView.enableBorder(P.getSubtitleBorderEnabled());
                return;
            case 31:
                this._gestureSeekSpeed = prefs.getFloat(key, 10.0f) * 100000.0f;
                return;
            case ' ':
                this._osdBackground = prefs.getBoolean(key, false);
                updateOSDPlacement();
                return;
            case '!':
                this._subView.enableShadow(P.getSubtitleShadownEnabled());
                return;
            case '\"':
                this._pauseIfObscured = prefs.getBoolean(key, false);
                return;
            case '#':
                this._subView.setGravity(P.subtitleAlignment | 80);
                return;
            case '$':
                boolean fadeout = prefs.getBoolean(key, false);
                this._subView.setEnableFadeOut(fadeout);
                if (this._subtitleOverlay != null) {
                    this._subtitleOverlay.setEnableFadeOut(fadeout);
                    return;
                }
                return;
            case '%':
                onShowPrevNextButtons(null, prefs.getBoolean(key, true));
                return;
            case '&':
                applyOverVolume();
                return;
            case '\'':
                applyBaseVolume();
                return;
            case '(':
                this._buttonBacklightOff = P.getButtonBacklightOff();
                return;
            case ')':
                this._subView.enableItalicTag(P.getItalicTag());
                return;
            case '*':
                setFullscreen(P.getFullScreen());
                return;
            case '+':
                setOrientation(P.screenOrientation);
                return;
            case ',':
                this._showScreenRotateButton = prefs.getBoolean(Key.SCREEN_ROTATION_BUTTON, true);
                updateScreenRotationButton(this._lastRequestedOrientation);
                return;
            case '-':
                updatePlayPauseIndicator();
                updateLayout();
                return;
            case '.':
                this.topLayout.setKeepScreenOn(prefs.getBoolean(key, false));
                return;
            case '/':
                this._displaySeekingPosition = prefs.getBoolean(Key.DISPLAY_SEEKING_POSITION, true);
                return;
            case '0':
                this._videoZoomDelay = P.getVideoZoomDelay();
                return;
            case '1':
                this._defaultSticky = prefs.getBoolean("sticky", false);
                updateStickyState(false);
                return;
            case '2':
                this._videoZoom = prefs.getInt("video_zoom", 1);
                return;
            case '3':
                this._osdBottom = prefs.getBoolean(key, false);
                updateOSDPlacement();
                return;
            case '4':
                if (this._zoomButton != null) {
                    this._zoomButton.getDrawable().setLevel(getNextZoomMode(this._videoZoom));
                    return;
                }
                return;
            case '5':
                onSubtitleBottomPaddingChanged(null, P.subtitleBottomPaddingDp);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyIntent(Intent intent, @Nullable Bundle saved) {
        boolean start;
        byte requestedDecoder;
        Uri mediaUri;
        Parcelable[] parcel;
        Playlist.Autogen.PL pl;
        PlayService.ReturnedData detached;
        Uri intentUri = intent.getData();
        Uri playUri = null;
        if (intentUri == null) {
            Log.e(TAG, "Finishing " + this + " because 'dat' is not provided.");
            finish();
            return;
        }
        Uri[] playlist = null;
        int videoFlags = 0;
        int loadingFlags = 0;
        boolean restored = false;
        boolean fromPlayService = false;
        if (saved != null) {
            if (0 == 0 && (playUri = (Uri) saved.getParcelable("uri")) != null) {
                loadingFlags = 0 | 5;
            }
            start = saved.getInt(STATE_TARGET_STATE, 4) == 4;
            requestedDecoder = 0;
        } else {
            start = true;
            requestedDecoder = intent.getByteExtra(EXTRA_DECODE_MODE, (byte) 0);
            if (requestedDecoder != 0) {
                loadingFlags = 0 | 16;
            } else {
                videoFlags = 0 | 128;
            }
        }
        if (playUri == null && "android.intent.action.SEARCH".equals(intent.getAction())) {
            String url = intent.getStringExtra(SearchIntents.EXTRA_QUERY);
            if (url != null) {
                try {
                    MediaDatabase mdb = MediaDatabase.getInstance();
                    mdb.writeDirectOpenLog(url);
                    mdb.release();
                } catch (SQLiteException e) {
                    Log.e(TAG, "", e);
                }
            }
            List<String> results = intent.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                url = results.get(0);
            }
            if (url != null) {
                if (url.indexOf("://") < 0) {
                    url = "http://" + url;
                }
                playUri = Uri.parse(url);
            }
        }
        if (PlayService.instance != null && (detached = PlayService.instance.returnPlayer(this)) != null) {
            this._subtitleServiceMedia = null;
            this._subtitleServiceMediaTried = false;
            if (detached.player != this.pp) {
                if (this.pp != null) {
                    this.pp.clear(16);
                }
                this.pp = detached.player;
                this._controller.setPlayer(this.pp);
                this._lister = detached.lister;
            }
            this.pp.setClient(this);
            if (intentUri.equals(detached.intentUri)) {
                if (detached.savedStates != null) {
                    restoreState(detached.savedStates);
                    restored = true;
                }
                replayState();
                if (this.pp.getState() == 1) {
                    load(null, 1, (byte) 0, 0);
                }
                this._stickyRequested = true;
                start = false;
                fromPlayService = true;
            } else {
                this._stickyRequested = false;
                this.pp.clear();
            }
        }
        if (!fromPlayService) {
            if (!intent.hasExtra(EXTRA_LIST) && Playlist.Autogen.isAutoGenFile(intentUri, false) && (pl = Playlist.Autogen.load(new File(intentUri.getPath()))) != null && pl.playlist.length > 0) {
                playlist = pl.playlist;
                if (pl.startUri != null) {
                    playUri = pl.startUri;
                } else {
                    playUri = playlist[0];
                }
            }
            if (playUri == null) {
                playUri = intentUri;
            }
            if (playlist == null && (parcel = intent.getParcelableArrayExtra(EXTRA_LIST)) != null && parcel.length > 0) {
                playlist = new Uri[parcel.length];
                System.arraycopy(parcel, 0, playlist, 0, parcel.length);
            }
            Uri playUri2 = MediaUtils.canonicalizeUri(playUri);
            if (intent.hasExtra(EXTRA_POSITION)) {
                loadingFlags |= 4;
            }
            if (this.pp.getState() == 0) {
                if (this._lister != null) {
                    this._lister.close();
                    this._lister = null;
                }
                this._lister = PlayLister.create(this._lister, playUri2, playlist, this._httpFactory);
                this._lister.setListener(this);
                if (playUri2.equals(this._lister.getContainer())) {
                    if (P.shuffle) {
                        mediaUri = this._lister.getRandom();
                    } else {
                        mediaUri = this._lister.getAt(0);
                    }
                    if (mediaUri != null) {
                        playUri2 = mediaUri;
                    } else if (MediaUtils.isFileUri(playUri2) && new File(playUri2.getPath()).isDirectory()) {
                        if (!isFinishing()) {
                            showDialog((ActivityScreen) new AlertDialog.Builder(this).setMessage(getString(P.isAudioPlayer ? R.string.no_media_in_this_folder : R.string.no_videos_in_this_folder)).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create(), (DialogInterface.OnDismissListener) new ErrorMessageHandler());
                            return;
                        }
                        return;
                    }
                }
                load(playUri2, loadingFlags, requestedDecoder, videoFlags);
            }
        }
        if (start) {
            start();
        }
        if (!restored && saved != null) {
            restoreState(saved);
        }
        updateStickyState(true);
    }

    private void restoreState(Bundle saved) {
        this._stickyRequested = saved.getBoolean(STATE_STICKY_REQUESTED, false);
        this._stickyAutoReset = saved.getBoolean(STATE_STICKY_AUTO_RESET, true);
        if (saved.getBoolean(STATE_ROTATION_LOCKED)) {
            lockRotation(true, saved.getInt(STATE_LOCKED_ORIENTATION));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppUtils.dumpParams(TAG, this, "onNewIntent", intent, null);
        returnOk(kEndByUser);
        Uri intentUri = getIntent().getData();
        if (!intentUri.equals(intent.getData())) {
            Playlist.Autogen.cleanup(intentUri);
        }
        setIntent(intent);
        applyIntent(intent, null);
    }

    @Override // android.app.Activity
    protected void onRestart() {
        PlayService.ReturnedData detached;
        Log.v(TAG, "onRestart (" + this + ")");
        super.onRestart();
        if (PlayService.instance != null && (detached = PlayService.instance.returnPlayer(this)) != null) {
            if (detached.player == this.pp) {
                this.pp.setClient(this);
                replayState();
            } else {
                detached.close();
            }
        }
        if (this.pp.getState() == 1) {
            load(null, 1, (byte) 0, 0);
        }
    }

    @Override // com.mxtech.videoplayer.service.PlayService.PlayClient
    public final void receive(Player player, Bundle savedStates) {
        if (player != this.pp) {
            Log.e(TAG, "Unknown player is received from the play service. given=" + player + " existing=" + this.pp);
            player.disburden();
            player.clear();
        } else if (isFinishing()) {
            this.pp.disburden();
            this.pp.clear();
        } else {
            if (!this.started) {
                this.pp.suspend();
                this.pp.disburden();
            }
            this.pp.setClient(this);
            replayState();
        }
    }

    private void updateBatteryClockIntentReceiver() {
        if (this._batteryClockIntentRegistered || !this.started) {
            return;
        }
        if (this._alwaysShowStatusText || this._batteryClockInTitleBar) {
            registerReceiver(this._broadcastReceiver, _batteryClockIntentFilter);
            this._batteryClockIntentRegistered = true;
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        getSupportActionBar().addOnMenuVisibilityListener(this);
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        getSupportActionBar().addOnMenuVisibilityListener(this);
        ((ScreenToolbar) toolbar).setStyle(ScreenStyle.getInstance(), -1);
        TextView tv = (TextView) ViewUtils.findViewByClass(toolbar, TextView.class);
        if (tv != null) {
            tv.setSingleLine(false);
            tv.setMaxLines(2);
        }
    }

    @Override // android.support.v7.app.ActionBar.OnMenuVisibilityListener
    public void onMenuVisibilityChanged(boolean isVisible) {
        if (isVisible) {
            this._controller.pin();
        } else {
            this._controller.unpin();
        }
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase
    public View getSnackbarParent() {
        return this.topLayout;
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart (" + this + "): video-uri=" + this.pp.getUri());
        this._timeFormat = android.text.format.DateFormat.getTimeFormat(this);
        updateAudioMediaListener();
        changeBrightness(P.getBrightness());
        updateBatteryClock();
        registerReceiver(this._broadcastReceiver, _defaultIntentFilter);
        updateBatteryClockIntentReceiver();
        DecoderPreferences.checkCustomCodecFile(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume (" + this + "): uri=" + this.pp.getUri());
        createSurfaceView();
        resume();
        this._tracker.setScreenName("Playback");
        this._tracker.send(new HitBuilders.ScreenViewBuilder().build());
        FlurryAgent.onStartSession(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Log.v(TAG, "onPause (" + this + "): uri=" + this.pp.getUri());
        pause(80);
        if (L.keyguardManager.inKeyguardRestrictedInputMode() && (this.pp.getDecoder() & 6) != 0) {
            removeSurfaceView();
        }
        if (this._lock != null && !this._lock.isKidsLock()) {
            this._lock.unlock();
        }
        this._subView.resetUpdateLock();
        this.pp.save();
        super.onPause();
        FlurryAgent.onEndSession(this);
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        Log.v(TAG, "onStop (" + this + "): uri=" + this.pp.getUri());
        removeSurfaceView();
        if (canSwitchSticky()) {
            this.pp.closeSubtitles(false);
            Bundle savedStates = new Bundle();
            saveInstanceStates(savedStates);
            PlayService.instance.transferPlayer(isFinishing() ? null : this, this.pp, this._lister, getIntent(), savedStates);
            this._switchedToBackgroundPlay = true;
        } else {
            this._switchedToBackgroundPlay = false;
            if (!isFinishing()) {
                this.pp.suspend();
                this.pp.disburden();
            }
        }
        this._timeFormat = null;
        super.onStop();
        updateAudioMediaListener();
        try {
            unregisterReceiver(this._broadcastReceiver);
            this._batteryClockIntentRegistered = false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "", e);
        }
    }

    private void saveInstanceStates(Bundle out) {
        Uri uri;
        if (this.pp != null && this.pp.getClient() == this && (uri = this.pp.getUri()) != null) {
            out.putParcelable("uri", uri);
            out.putInt(STATE_TARGET_STATE, this.pp.getTargetState());
        }
        if (this._rotationLocked) {
            out.putBoolean(STATE_ROTATION_LOCKED, true);
            out.putInt(STATE_LOCKED_ORIENTATION, getRequestedOrientation());
        }
        out.putBoolean(STATE_STICKY_REQUESTED, this._stickyRequested);
        out.putBoolean(STATE_STICKY_AUTO_RESET, this._stickyAutoReset);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleFlush() {
        this.handler.removeMessages(10);
        this.handler.sendEmptyMessageDelayed(10, 1000L);
    }

    private void flushChanges() {
        if (this._dirty != 0) {
            SharedPreferences.Editor editor = App.prefs.edit();
            if ((this._dirty & 2) != 0) {
                editor.putInt("video_zoom", this._videoZoom);
            }
            if ((this._dirty & 1) != 0) {
                editor.putBoolean(Key.SCREEN_BRIGHTNESS_AUTO, P.screenBrightnessAuto);
                editor.putFloat(Key.SCREEN_BRIGHTNESS, P.screenBrightness);
            }
            if ((this._dirty & 4) != 0) {
                editor.putInt(Key.OVER_VOLUME, P.overVolume);
            }
            if ((this._dirty & 16) != 0) {
                editor.putInt(Key.LOCAL_VOLUME, P.localVolume);
            }
            if ((this._dirty & 8) != 0) {
                editor.putBoolean(Key.SHOW_LEFT_TIME, this._showLeftTime);
            }
            AppUtils.apply(editor);
            this._dirty = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle out) {
        Log.v(TAG, "onSaveInstanceState (" + this + ")");
        saveInstanceStates(out);
        flushChanges();
        super.onSaveInstanceState(out);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Log.v(TAG, "onDestroy (" + this + ") - isFinishing() = " + isFinishing());
        if (this._sdm != null) {
            this._sdm.cancel();
        }
        if (this._lister != null) {
            this._lister.close();
        }
        flushChanges();
        if (this.pp != null && this.pp.getClient() == this) {
            this.pp.disburden();
            this.pp.clear();
        }
        App.prefs.unregisterOnSharedPreferenceChangeListener(this);
        ScreenStyle.getInstance().removeOnChangeListener(this);
        if (!this._switchedToBackgroundPlay) {
            Playlist.Autogen.cleanup(getIntent().getData());
        }
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this._lastRequestedOrientation == Integer.MIN_VALUE) {
            setOrientation(P.screenOrientation);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"NewApi"})
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!this.foreground) {
            this._ignoreNextFocusLoss = false;
        } else if (hasFocus) {
            resume();
            if (this._needToUpdateSizes && !L.keyguardManager.inKeyguardRestrictedInputMode()) {
                updateSizes();
            }
            this._controller.update(this.pp.getState());
        } else if (!this._ignoreNextFocusLoss) {
            if (this._pauseIfObscured && !isSticky()) {
                Log.v(TAG, "Pause playback temporarily as main window losing focus. pause-if-obscured=" + this._pauseIfObscured);
                pause(80);
            }
        } else {
            this._ignoreNextFocusLoss = false;
        }
    }

    @Override // com.mxtech.app.DialogRegistry.Listener
    public final void onDialogRegistered(DialogRegistry registry, DialogInterface dialog) {
        if (!isSticky()) {
            Log.v(TAG, "Pause playback temporarily as dialog(s) registered.");
            pause(80);
        }
    }

    @Override // com.mxtech.app.DialogRegistry.Listener
    public final void onDialogUnregistered(DialogRegistry registry, DialogInterface dialog) {
        if (registry.size() == 0) {
            resume();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* loaded from: classes.dex */
    public final class ResumeAsker implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener, CompoundButton.OnCheckedChangeListener {
        private int _choice;
        private View _howToReset;
        private CheckBox _useByDefault;

        @SuppressLint({"InflateParams"})
        ResumeAsker() {
            if (!ActivityScreen.this.isFinishing()) {
                AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(ActivityScreen.this.getTitleNoDecor()).setPositiveButton(R.string.resume, this).setNegativeButton(R.string.start_over, this).setCancelable(true).create();
                View v = dlg.getLayoutInflater().inflate(R.layout.ask_resume, (ViewGroup) null);
                this._howToReset = v.findViewById(R.id.how_to_reset);
                this._useByDefault = (CheckBox) v.findViewById(R.id.use_by_default);
                this._useByDefault.setOnCheckedChangeListener(this);
                TextView annexView = (TextView) v.findViewById(R.id.how_to_reset);
                L.sb.setLength(0);
                L.localizeSettingsPath(R.string.how_to_reset_resume_last, L.sb);
                annexView.setText(L.sb.toString());
                dlg.setView(v);
                dlg.setOnShowListener(this);
                try {
                    ActivityScreen.this.showDialog((ActivityScreen) dlg, (DialogInterface.OnDismissListener) this);
                    ActivityScreen.access$3504(ActivityScreen.this);
                } catch (WindowManager.BadTokenException e) {
                    Log.e(ActivityScreen.TAG, "", e);
                    ActivityScreen.this.dialogRegistry.unregister(dlg);
                }
            }
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Button positiveButton = ((AlertDialog) dialog).getButton(-1);
            if (positiveButton != null) {
                positiveButton.requestFocus();
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            this._choice = which;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            MediaDatabase.State persist;
            ActivityScreen.access$3506(ActivityScreen.this);
            ActivityScreen.this.dialogRegistry.unregister(dialog);
            switch (this._choice) {
                case -2:
                    if (P.rememberPlaybackSelections && (persist = ActivityScreen.this.pp.getPersistent()) != null) {
                        persist.startOver();
                        ActivityScreen.this.load();
                        break;
                    } else {
                        Uri uri = ActivityScreen.this.pp.getUri();
                        if (uri != null) {
                            ActivityScreen.this.pp.clear(528);
                            try {
                                MediaDatabase mdb = MediaDatabase.getInstance();
                                mdb.deleteState(uri);
                                mdb.release();
                            } catch (SQLiteException e) {
                                Log.e(ActivityScreen.TAG, "", e);
                                if (!ActivityScreen.this.isFinishing()) {
                                    DialogUtils.alert(ActivityScreen.this, R.string.error_database);
                                }
                            }
                            ActivityScreen.this.load(uri, ActivityScreen.this._loadingFlags, ActivityScreen.this._loadingRequestedDecoder, ActivityScreen.this._loadingVideoFlags);
                            ActivityScreen.this.start();
                            break;
                        }
                    }
                    break;
                case -1:
                    ActivityScreen.this.load();
                    break;
                default:
                    ActivityScreen.this.finish();
                    ActivityScreen.this.returnResult(0);
                    return;
            }
            if (this._useByDefault.isChecked()) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putString(Key.RESUME_LAST, this._choice == -1 ? P.VALUE_RESUME : P.VALUE_STARTOVER);
                AppUtils.apply(editor);
            }
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this._howToReset.setVisibility(isChecked ? 0 : 8);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void updatePersistent(Uri uri, MediaDatabase.State save, List<ISubtitle> subs) {
        save.subtitleOffset = this._subView.getSync();
        save.subtitleSpeed = this._subView.getSpeed();
        save.repeatA = this._repeatA;
        save.repeatB = this._repeatB;
        if (this._doubleTapZooming) {
            save.zoomHeight = (short) 0;
            save.zoomWidth = (short) 0;
            save.panY = (short) 0;
            save.panX = (short) 0;
        } else {
            save.zoomWidth = (short) this._zoomWidth;
            save.zoomHeight = (short) this._zoomHeight;
            save.panX = (short) this._panX;
            save.panY = (short) this._panY;
        }
        subs.clear();
        int subCount = this._subView.getSubtitleCount();
        if (subCount > 0) {
            save.subtitles = new MediaDatabase.State.Subtitle[subCount];
            for (int i = 0; i < subCount; i++) {
                ISubtitle sub = this._subView.getSubtitle(i);
                save.subtitles[i] = new MediaDatabase.State.Subtitle(sub.uri(), sub.name(), sub.typename(), this._subView.isSubtitleEnabled(i));
                subs.add(sub);
            }
        }
    }

    public final boolean isSubtitlePanelVisible() {
        return this._subtitlePanel != null;
    }

    public final void showSubtitlePanel() {
        if (this._subtitlePanel == null) {
            this._subtitlePanel = new SubtitlePanel((ViewGroup) this.uiLayout.getParent(), this._subView, this.dialogRegistry, this.layoutInflater, this, this.pp.getUri());
            this._subtitlePanel.show();
            updateUserLayout();
        }
    }

    public final boolean hideSubtitlePanel() {
        if (this._subtitlePanel != null) {
            this._subtitlePanel.hide();
            this._subtitlePanel = null;
            updateLayout();
            return true;
        }
        return false;
    }

    private boolean hasFloatingBars() {
        return this._floatingBars.size() > 0;
    }

    @Nullable
    private IFloatingBar getFloatingBar(int id) {
        Iterator<IFloatingBar> it = this._floatingBars.iterator();
        while (it.hasNext()) {
            IFloatingBar bar = it.next();
            if (bar.getLayout().getId() == id) {
                return bar;
            }
        }
        return null;
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public void showFloatingBar(int id, boolean show) {
        IFloatingBar bar = getFloatingBar(id);
        if (bar != null) {
            if (!show) {
                ViewGroup layout = bar.getLayout();
                ((ViewGroup) layout.getParent()).removeView(layout);
                this._floatingBars.remove(bar);
            } else {
                return;
            }
        } else if (show) {
            this._floatingBars.add(newFloatingBar(id));
        } else {
            return;
        }
        updateUserLayout();
    }

    private void toggleFloatingBar(int id) {
        IFloatingBar bar = getFloatingBar(id);
        if (bar != null) {
            ViewGroup layout = bar.getLayout();
            ((ViewGroup) layout.getParent()).removeView(layout);
            this._floatingBars.remove(bar);
        } else {
            this._floatingBars.add(newFloatingBar(id));
        }
        updateUserLayout();
    }

    private void hideAllFloatingBars() {
        Iterator<IFloatingBar> it = this._floatingBars.iterator();
        while (it.hasNext()) {
            IFloatingBar bar = it.next();
            ViewGroup layout = bar.getLayout();
            ((ViewGroup) layout.getParent()).removeView(layout);
        }
        this._floatingBars.clear();
        updateUserLayout();
    }

    private IFloatingBar newFloatingBar(int id) throws IllegalArgumentException {
        if (id == R.id.playback_speed_bar) {
            return new PlaybackSpeedBar((ViewGroup) this.uiLayout.getParent(), this.layoutInflater, this);
        }
        if (id == R.id.subtitle_sync_bar) {
            return new SubtitleSyncBar((ViewGroup) this.uiLayout.getParent(), this.layoutInflater, this._subView, this);
        }
        if (id == R.id.subtitle_speed_bar) {
            return new SubtitleSpeedBar((ViewGroup) this.uiLayout.getParent(), this.layoutInflater, this._subView, this);
        }
        if (id == R.id.repeat_ab_bar) {
            return new RepeatABBar((ViewGroup) this.uiLayout.getParent(), this.layoutInflater, this);
        }
        if (id == R.id.audio_sync_bar) {
            return new AudioSyncBar((ViewGroup) this.uiLayout.getParent(), this.layoutInflater, this);
        }
        throw new IllegalArgumentException("Unknown floating bar: id=" + id);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public void setAudioSync(int millis) {
        this.pp.setAudioOffset(millis);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public int getAudioSync() {
        return this.pp.getAudioOffset();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public int getRepeatPointA() {
        return this._repeatA;
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public int getRepeatPointB() {
        return this._repeatB;
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public void setRepeatPoints(int A, int B) {
        this._repeatA = A;
        this._repeatB = B;
        this.pp.setRepeatPoints(A, B);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"InflateParams"})
    /* loaded from: classes.dex */
    public class AspectRatioSelector implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private final CheckBox _defaultBtn;
        private float _h;
        private final float[] _horzRatios;
        private final int _numPredefinedRatios;
        private float _v;
        private final float[] _vertRatios;

        AspectRatioSelector() {
            int listResId;
            int horzResId;
            int vertResId;
            int selection;
            this._h = ActivityScreen.this.pp.getHorzRatio();
            this._v = ActivityScreen.this.pp.getVertRatio();
            Resources res = ActivityScreen.this.getResources();
            if (ActivityScreen.this.pp.isLandscape()) {
                listResId = R.array.aspect_ratios_landscape;
                horzResId = R.array.aspect_longer_ratios;
                vertResId = R.array.aspect_shorter_ratios;
            } else {
                listResId = R.array.aspect_ratios_portrait;
                horzResId = R.array.aspect_shorter_ratios;
                vertResId = R.array.aspect_longer_ratios;
            }
            this._horzRatios = readRatios(res, horzResId);
            this._vertRatios = readRatios(res, vertResId);
            this._numPredefinedRatios = this._horzRatios.length;
            if (this._h > 0.0f && this._v > 0.0f) {
                int i = 0;
                while (true) {
                    if (i < this._numPredefinedRatios) {
                        if (this._horzRatios[i] != this._h || this._vertRatios[i] != this._v) {
                            i++;
                        } else {
                            selection = i + 1;
                            break;
                        }
                    } else {
                        selection = this._numPredefinedRatios + 1;
                        break;
                    }
                }
            } else {
                selection = 0;
            }
            AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.aspect_ratio).setSingleChoiceItems(listResId, selection, this).create();
            View view = dlg.getLayoutInflater().inflate(R.layout.alertdialog_with_footer, (ViewGroup) null);
            this._defaultBtn = (CheckBox) view.findViewById(R.id.footer);
            this._defaultBtn.setText(R.string.apply_to_all_videos);
            this._defaultBtn.setChecked(App.prefs.contains(Key.ASPECT_RATIO_H));
            dlg.setView(view);
            ActivityScreen.this.showDialog((ActivityScreen) dlg, (DialogInterface.OnDismissListener) this);
        }

        private float[] readRatios(Resources res, int resId) {
            String[] s = res.getStringArray(resId);
            int numRatios = s.length;
            float[] ratios = new float[numRatios];
            for (int i = 0; i < numRatios; i++) {
                ratios[i] = Float.parseFloat(s[i]);
            }
            return ratios;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (!ActivityScreen.this.pp.isInPlaybackState()) {
                dialog.dismiss();
            } else if (which == -1) {
                TextView widthView = (TextView) ((Dialog) dialog).findViewById(R.id.widthRatio);
                TextView heightView = (TextView) ((Dialog) dialog).findViewById(R.id.heightRatio);
                try {
                    float widthRatio = Float.parseFloat(widthView.getText().toString());
                    float heightRatio = Float.parseFloat(heightView.getText().toString());
                    if (widthRatio > 0.0f && heightRatio > 0.0f) {
                        SharedPreferences.Editor editor = App.prefs.edit();
                        editor.putFloat(Key.CUSTOM_ASPECT_RATIO_HORZ, widthRatio);
                        editor.putFloat(Key.CUSTOM_ASPECT_RATIO_VERT, heightRatio);
                        AppUtils.apply(editor);
                        setAspectRatio(widthRatio, heightRatio);
                    }
                } catch (NumberFormatException e) {
                }
            } else if (which >= 0) {
                if (which == 0) {
                    setAspectRatio(0.0f, 0.0f);
                } else if (which < this._numPredefinedRatios + 1) {
                    setAspectRatio(this._horzRatios[which - 1], this._vertRatios[which - 1]);
                } else if (!ActivityScreen.this.isFinishing()) {
                    AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.enter_custom_aspect_ratio).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                    View view = dlg.getLayoutInflater().inflate(R.layout.custom_aspect_ratio, (ViewGroup) null);
                    float lastWidthRatio = App.prefs.getFloat(Key.CUSTOM_ASPECT_RATIO_HORZ, 0.0f);
                    float lastHeightRatio = App.prefs.getFloat(Key.CUSTOM_ASPECT_RATIO_VERT, 0.0f);
                    if (lastWidthRatio > 0.0f && lastHeightRatio > 0.0f) {
                        NumberFormat formatter = DecimalFormat.getInstance();
                        if (formatter instanceof DecimalFormat) {
                            ((DecimalFormat) formatter).applyPattern("#.####");
                        }
                        TextView widthView2 = (TextView) view.findViewById(R.id.widthRatio);
                        TextView heightView2 = (TextView) view.findViewById(R.id.heightRatio);
                        widthView2.setText(formatter.format(lastWidthRatio));
                        heightView2.setText(formatter.format(lastHeightRatio));
                    }
                    dlg.setView(view);
                    ActivityScreen.this.showDialog((ActivityScreen) dlg);
                }
            }
        }

        private void setAspectRatio(float h, float v) {
            this._h = h;
            this._v = v;
            ActivityScreen.this.pp.setAspectRatio(h, v, true);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this._defaultBtn.isChecked()) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putFloat(Key.ASPECT_RATIO_H, this._h);
                editor.putFloat(Key.ASPECT_RATIO_V, this._v);
                AppUtils.apply(editor);
            } else if (App.prefs.contains(Key.ASPECT_RATIO_H)) {
                SharedPreferences.Editor editor2 = App.prefs.edit();
                editor2.remove(Key.ASPECT_RATIO_H);
                editor2.remove(Key.ASPECT_RATIO_V);
                AppUtils.apply(editor2);
            }
            ActivityScreen.this.pp.setAspectRatioExplicity(true);
            ActivityScreen.this.dialogRegistry.unregister(dialog);
        }
    }

    @SuppressLint({"InflateParams"})
    private void invokeAspectRatioSelector() {
        if (!isFinishing() && this.dialogRegistry.size() <= 0 && this.pp.isInPlaybackState()) {
            new AspectRatioSelector();
        }
    }

    private void invokeCaptionSelector() {
        if (!isFinishing() && this.dialogRegistry.size() <= 0 && this.pp.isInPlaybackState() && this._subView.getSubtitleCount() > 0) {
            new CaptionSelector();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CaptionSelector implements DialogInterface.OnMultiChoiceClickListener {
        private final ISubtitle[] _allSubs;

        CaptionSelector() {
            int count = ActivityScreen.this._subView.getSubtitleCount();
            this._allSubs = ActivityScreen.this._subView.getAllSubtitles();
            CharSequence[] texts = new CharSequence[count];
            boolean[] states = new boolean[count];
            for (int i = 0; i < count; i++) {
                ISubtitle sub = ActivityScreen.this._subView.getSubtitle(i);
                texts[i] = SubtitleFactory.getDecorativeName(sub, this._allSubs);
                states[i] = ActivityScreen.this._subView.isSubtitleEnabled(i);
            }
            ActivityScreen.this.showDialog((ActivityScreen) new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.subtitle).setMultiChoiceItems(texts, states, this).create());
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (which < ActivityScreen.this._subView.getSubtitleCount()) {
                if (isChecked) {
                    ISubtitle sub = ActivityScreen.this._subView.getSubtitle(which);
                    if (!sub.isSupported()) {
                        Toast.makeText(ActivityScreen.this, StringUtils.getString_s(R.string.not_supported_subtitle_with_name, SubtitleFactory.getDecorativeName(sub, this._allSubs).toString()), 0).show();
                        return;
                    }
                }
                ActivityScreen.this._subView.enableSubtitle(which, isChecked);
                P.setAutoSelectSubtitle(ActivityScreen.this._subView.getEnabledSubtitleCount() > 0);
                if (ActivityScreen.this._subtitlePanel != null) {
                    ActivityScreen.this._subtitlePanel.update();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AudioTrackEntry {
        final String name;
        final int streamIndex;

        AudioTrackEntry(Context context) {
            this.name = context.getString(R.string.disable);
            this.streamIndex = -1;
        }

        AudioTrackEntry(Context context, IMediaInfoAux core, int streamIndex, int seq) {
            this.streamIndex = streamIndex;
            IMediaInfo info = core.streamInfo(streamIndex);
            try {
                String name = info.title();
                String lang = info.displayLocales();
                name = (name == null || name.length() == 0) ? StringUtils.getString_s(R.string.audiotrack_format, Integer.valueOf(seq + 1)) : name;
                if (lang != null && lang.length() > 0) {
                    name = name + " - " + lang;
                }
                this.name = name;
            } finally {
                info.close();
            }
        }
    }

    private void invokeAudioTrackSelector() {
        int seq;
        if (!isFinishing() && this.dialogRegistry.size() <= 0 && this.pp.isInPlaybackState()) {
            JointPlayer mp = this.pp.mp();
            int current = -1;
            ArrayList<AudioTrackEntry> tracks = new ArrayList<>();
            int currentAudioStreamIndex = mp.getAudioStream();
            int streamIndex = 0;
            int[] streamTypes = mp.getStreamTypes();
            int length = streamTypes.length;
            int i = 0;
            int seq2 = 0;
            while (i < length) {
                int type = streamTypes[i];
                if (type == 1) {
                    if (currentAudioStreamIndex == streamIndex) {
                        current = tracks.size();
                    }
                    seq = seq2 + 1;
                    tracks.add(new AudioTrackEntry(this, mp, streamIndex, seq2));
                } else {
                    seq = seq2;
                }
                streamIndex++;
                i++;
                seq2 = seq;
            }
            if (tracks.size() > 0) {
                tracks.add(new AudioTrackEntry(this));
                if (currentAudioStreamIndex == -3) {
                    current = 0;
                } else if (currentAudioStreamIndex == -1) {
                    current = tracks.size() - 1;
                }
                new AudioTrackSelector(tracks, current);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AudioTrackSelector extends BaseAdapter implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private int _current;
        private boolean _isSelected;
        private final int _itemLayoutId;
        private final AudioTrackEntry[] _tracks;
        private CheckBox _useSoftDecoder;

        @SuppressLint({"InflateParams"})
        AudioTrackSelector(List<AudioTrackEntry> tracks, int current) {
            this._tracks = (AudioTrackEntry[]) tracks.toArray(new AudioTrackEntry[tracks.size()]);
            this._current = current;
            AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.select_audio_track).setSingleChoiceItems(this, current, this).create();
            View view = dlg.getLayoutInflater().inflate(R.layout.alertdialog_with_footer, (ViewGroup) null);
            this._useSoftDecoder = (CheckBox) view.findViewById(R.id.footer);
            this._useSoftDecoder.setText(R.string.use_sw_audio_decoder);
            this._useSoftDecoder.setChecked(ActivityScreen.this.pp.getUserSoftAudio());
            dlg.setView(view);
            TypedArray a = ActivityScreen.this.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
            this._itemLayoutId = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 17367058);
            a.recycle();
            ActivityScreen.this.showDialog((ActivityScreen) dlg, (DialogInterface.OnDismissListener) this);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this._tracks.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = ActivityScreen.this.layoutInflater.inflate(this._itemLayoutId, parent, false);
            }
            TextView tv = (TextView) v.findViewById(16908308);
            tv.setText(this._tracks[position].name);
            return v;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            select(which);
        }

        private void select(int which) {
            if (ActivityScreen.this.pp.isInPlaybackState() && !this._isSelected) {
                this._isSelected = true;
                boolean softAudio = this._useSoftDecoder.isChecked();
                boolean decoderChoiceChanged = softAudio != ActivityScreen.this.pp.getUserSoftAudio();
                if (decoderChoiceChanged) {
                    ActivityScreen.this.pp.setSoftAudio(softAudio);
                }
                if (which >= 0) {
                    if (which != this._current || (decoderChoiceChanged && ActivityScreen.this.pp.isSoftAudioUsed() != softAudio)) {
                        ActivityScreen.this.pp.save();
                        AudioTrackEntry entry = this._tracks[which];
                        if (entry.streamIndex >= 0) {
                            if (ActivityScreen.this.pp.changeAudioTrackByUserRequest(which, entry.streamIndex) == -4) {
                                ActivityScreen.this.restart_player();
                                return;
                            }
                            return;
                        }
                        ActivityScreen.this.pp.removeAudioTrack();
                    }
                }
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            select(this._current);
            ActivityScreen.this.dialogRegistry.unregister(dialog);
        }
    }

    private static boolean isBannedCodec(String name) {
        int len;
        if (name == null || (len = name.length()) < 3) {
            return false;
        }
        switch (name.charAt(len - 1)) {
            case '3':
                return "ac3".equals(name) || "eac3".equals(name);
            case 'a':
                return "dca".equals(name);
            case 'd':
                return "ac3_fixed".equals(name);
            case 'p':
                return "mlp".equals(name);
            case 's':
                return "dts".equals(name);
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AskCustomCodec implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        AskCustomCodec(String streamTypeText, String codecName) {
            if (!ActivityScreen.this.isFinishing()) {
                L.sb.setLength(0);
                L.sb.append(StringUtils.getString_s(R.string.unsupported_codec, streamTypeText, codecName)).append(' ').append(ActivityScreen.this.getString(R.string.ask_try_custom_codec, new Object[]{"<i>" + L.getSysArchName() + "</i>"}));
                ActivityScreen.this.showDialog((ActivityScreen) new AlertDialog.Builder(ActivityScreen.this).setTitle(ActivityScreen.this.getTitleNoDecor()).setMessage(Html.fromHtml(L.sb.toString())).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).setPositiveButton(17039379, this).create());
                ActivityScreen.this._customCodecAsker = this;
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            try {
                ActivityScreen.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(L.getCustomCodecRealUrl())));
            } catch (Exception e) {
                Log.e(ActivityScreen.TAG, "", e);
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            ActivityScreen.this.dialogRegistry.unregister(dialog);
            ActivityScreen.this._customCodecAsker = null;
        }
    }

    public static void notifyUnsupportedCodec(Context context, String streamTypeText, String codecName) {
        String codecName2;
        boolean banned = isBannedCodec(codecName);
        if (codecName != null) {
            codecName2 = codecName.toUpperCase(Locale.US);
        } else {
            codecName2 = "Unknown";
        }
        if (banned) {
            int ask = L.authorizer.getCustomCodecInteractionMode();
            if ((context instanceof ActivityScreen) && ask >= 2) {
                ActivityScreen activity = (ActivityScreen) context;
                if (activity._customCodecAsker == null) {
                    if (!activity._askedTryCustomCodec) {
                        activity._askedTryCustomCodec = true;
                        activity.getClass();
                        new AskCustomCodec(streamTypeText, codecName2);
                        return;
                    }
                } else {
                    return;
                }
            }
            L.sb.setLength(0);
            L.sb.append(StringUtils.getString_s(R.string.unsupported_codec, streamTypeText, codecName2));
            if (ask >= 1) {
                L.sb.append('\n').append(context.getString(R.string.try_custom_codec));
            }
            Toast.makeText(context, L.sb.toString(), 0).show();
            return;
        }
        Toast.makeText(context, StringUtils.getString_s(R.string.unsupported_codec, streamTypeText, codecName2), 0).show();
    }

    public static final void checkCodec(Context context, JointPlayer mp) {
        int requested;
        int videoIndex;
        FFPlayer ff = mp.getFFPlayer();
        if (ff != null) {
            if ((mp.getPrimary() instanceof FFPlayer) && !ff.isOMXVideoDecoderUsed() && (videoIndex = ff.getVideoStreamIndex()) >= 0 && !ff.isDecoderSupported(videoIndex)) {
                notifyUnsupportedCodec(context, context.getString(R.string.detail_group_video).toLowerCase(Locale.getDefault()), ff.getStreamCodec(videoIndex));
            } else if (!ff.isOMXAudioDecoderUsed() && (requested = ff.getLastRequestedAudioStreamIndex()) >= 0 && !ff.isDecoderSupported(requested)) {
                notifyUnsupportedCodec(context, context.getString(R.string.audio).toLowerCase(Locale.getDefault()), ff.getStreamCodec(requested));
            }
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onAudioCodecUnsupported(FFPlayer ff, int audioStreamIndex) {
        notifyUnsupportedCodec(this, getString(R.string.audio).toLowerCase(Locale.getDefault()), ff.getStreamCodec(audioStreamIndex));
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onAudioStreamChanged(JointPlayer mp, int streamIndex) {
        FFPlayer ff;
        String name;
        if (streamIndex >= 0 && (ff = this.pp.getFFPlayer()) != null && (name = getCodecProfileCombined(ff, streamIndex)) != null) {
            this._tracker.send(new HitBuilders.EventBuilder(L.isUsingCustomCodec() ? "ACodec_custom" : "ACodec", name).build());
        }
        checkCodec(this, mp);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restart_player() {
        this.pp.save();
        this.pp.suspendForRestart();
        load(null, 3, (byte) 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setZoomSupremeText(CharSequence text, int width, int height) {
        setSupremeText(text);
    }

    private int getNextZoomMode(int mode) {
        float multiplier;
        switch (this._videoZoom) {
            case 0:
                return 3;
            case 1:
                return 0;
            case 2:
            default:
                if (!P.quickZoom) {
                    int topWidth = this.topLayout.getWidth();
                    int topHeight = this.topLayout.getHeight();
                    int width = this.pp.getDisplayWidth();
                    int height = this.pp.getDisplayHeight();
                    if (width > 0) {
                        if (this._zoomWidth == 0) {
                            multiplier = 1.5f;
                        } else {
                            multiplier = 0.5f + (this._zoomWidth / width);
                        }
                        int nextZoomWidth = (int) (width * multiplier);
                        int nextZoomHeight = (int) (height * multiplier);
                        if (nextZoomWidth < topWidth && nextZoomHeight < topHeight) {
                            return 2;
                        }
                    }
                }
                return 1;
            case 3:
                return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zoomNext() {
        String text;
        float multiplier;
        if (this.pp.isInPlaybackState() && this._surfaceView != null) {
            this._panY = 0;
            this._panX = 0;
            switch (this._videoZoom) {
                case 0:
                    this._videoZoom = 3;
                    text = getString(R.string.zoom_crop).toUpperCase(Locale.getDefault());
                    break;
                case 1:
                    this._videoZoom = 0;
                    text = getString(R.string.zoom_stretch).toUpperCase(Locale.getDefault());
                    break;
                case 2:
                default:
                    if (!P.quickZoom) {
                        int topWidth = this.topLayout.getWidth();
                        int topHeight = this.topLayout.getHeight();
                        int width = this.pp.getDisplayWidth();
                        int height = this.pp.getDisplayHeight();
                        if (width > 0) {
                            if (this._zoomWidth == 0) {
                                multiplier = 1.5f;
                            } else {
                                multiplier = 0.5f + (this._zoomWidth / width);
                            }
                            this._zoomWidth = (int) (width * multiplier);
                            this._zoomHeight = (int) (height * multiplier);
                            if (this._zoomWidth < topWidth && this._zoomHeight < topHeight) {
                                setDisplaySize(this._zoomWidth, this._zoomHeight);
                                setZoomSupremeText(Integer.toString(((int) (multiplier / 0.5f)) * 50) + '%', this._zoomWidth, this._zoomHeight);
                                if (this._zoomButton != null) {
                                    this._zoomButton.getDrawable().setLevel(getNextZoomMode(2));
                                }
                                this.handler.sendEmptyMessageDelayed(4, 500L);
                                return;
                            }
                        }
                    }
                    this._videoZoom = 1;
                    text = getString(R.string.zoom_inside).toUpperCase(Locale.getDefault());
                    break;
                case 3:
                    this._videoZoom = 2;
                    text = "100%";
                    break;
            }
            this._zoomHeight = 0;
            this._zoomWidth = 0;
            setZoomMode(this._videoZoom);
            this._dirty |= 2;
            scheduleFlush();
            setZoomSupremeText(text, this._surfaceView.getWidth(), this._surfaceView.getHeight());
            this.handler.sendEmptyMessageDelayed(4, 500L);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem tools;
        SubMenu submenu;
        SubMenu submenu2;
        SubMenu submenu3;
        getMenuInflater().inflate(R.menu.screen, menu);
        this._decoderMenuItem = menu.findItem(R.id.decoder);
        this._batteryClockMenuItem = menu.findItem(R.id.clockBattery);
        this._audioMenuItem = menu.findItem(R.id.audio);
        this._captionMenuItem = menu.findItem(R.id.caption);
        if (this._decoderMenuItem != null) {
            updateDecoderIndicator();
        }
        if (this._batteryClockMenuItem != null) {
            this._batteryClockActionView = (BatteryClockActionView) MenuItemCompat.getActionView(this._batteryClockMenuItem);
            this._batteryClockInTitleBar = P.getBatteryClockInTitleBar();
            updateBatteryClockInTitleBar();
            updateBatteryClock();
        } else {
            this._batteryClockInTitleBar = false;
        }
        if (this._captionMenuItem != null) {
            setCaptionButtonVisibility(this._subView.getSubtitleCount() > 0);
        }
        if (DeviceUtils.isTV) {
            MenuItem display = menu.findItem(R.id.submenu_display);
            if (display != null && (submenu3 = display.getSubMenu()) != null) {
                submenu3.removeItem(R.id.rotation);
            }
            MenuItem tools2 = menu.findItem(R.id.submenu_tools);
            if (tools2 != null && (submenu2 = tools2.getSubMenu()) != null) {
                submenu2.removeItem(R.id.lock);
            }
        }
        handleOrientationChange(this.orientation);
        if (!L.canShare(this) && (tools = menu.findItem(R.id.submenu_tools)) != null && (submenu = tools.getSubMenu()) != null) {
            submenu.removeItem(R.id.share);
        }
        return true;
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        int audioStream;
        super.onPrepareOptionsMenu(menu);
        ((App) getApplication()).updateHelpCommand(menu);
        MenuItem item = menu.findItem(R.id.sticky);
        if (item != null) {
            item.setEnabled(canUseStickyMode());
            item.setChecked(this._stickyRequested && !this._stickyAutoReset);
        }
        MenuItem item2 = menu.findItem(R.id.rotation);
        if (item2 != null) {
            item2.setChecked(!this._rotationLocked);
        }
        MenuItem item3 = menu.findItem(R.id.deinterlace);
        if (item3 != null) {
            if (this.pp.isInPlaybackState() && this._hasVideoTrack && this.pp.getDecoder() == 2) {
                item3.setVisible(true);
                item3.setEnabled(true);
                int processing = this.pp.mp().getProcessing();
                MenuItem yadifItem = menu.findItem(R.id.deinterlace_yadif);
                if (yadifItem != null) {
                    yadifItem.setChecked((processing & 1) != 0);
                }
                MenuItem w3fdifItem = menu.findItem(R.id.deinterlace_w3fdif);
                if (w3fdifItem != null) {
                    w3fdifItem.setChecked((processing & 2) != 0);
                }
            } else {
                item3.setVisible(false);
                item3.setEnabled(false);
            }
        }
        MenuItem item4 = menu.findItem(R.id.use_speedup_tricks);
        if (item4 != null) {
            if (this.pp.isInPlaybackState() && this._hasVideoTrack && this.pp.getDecoder() == 2) {
                item4.setVisible(true);
                item4.setEnabled(true);
                MenuItem menuItem = menu.findItem(R.id.use_speedup_tricks);
                if (menuItem != null) {
                    menuItem.setChecked(this._useSpeedupTricks);
                }
            } else {
                item4.setVisible(false);
                item4.setEnabled(false);
            }
        }
        MenuItem item5 = menu.findItem(R.id.aspect_ratio);
        if (item5 != null) {
            item5.setEnabled(this.pp.isInPlaybackState() && this._hasVideoTrack);
        }
        MenuItem quitItem = menu.findItem(R.id.quit);
        if (quitItem != null) {
            boolean enabled = App.prefs.getBoolean(Key.QUIT_BUTTON, false);
            quitItem.setVisible(enabled);
            quitItem.setEnabled(enabled);
        }
        MenuItem item6 = menu.findItem(R.id.shuffle);
        if (item6 != null) {
            item6.setChecked(P.shuffle);
        }
        MenuItem item7 = menu.findItem(R.id.loop_one);
        if (item7 != null) {
            item7.setChecked(P.playerLooping == 1);
        }
        MenuItem item8 = menu.findItem(R.id.loop_all);
        if (item8 != null) {
            item8.setChecked(P.playerLooping == 9);
        }
        MenuItem deleteItem = menu.findItem(R.id.delete);
        if (deleteItem != null) {
            deleteItem.setVisible(P.allowEditing);
            deleteItem.setEnabled(P.allowEditing && this.pp.getFile() != null);
        }
        MenuItem renameItem = menu.findItem(R.id.rename);
        if (renameItem != null) {
            renameItem.setVisible(P.allowEditing);
            renameItem.setEnabled(P.allowEditing && this.pp.getFile() != null);
        }
        boolean hasSubtitle = this._subView.hasSubtitles();
        MenuItem subtitleSync = menu.findItem(R.id.subtitle_sync);
        if (subtitleSync != null) {
            subtitleSync.setEnabled(hasSubtitle);
        }
        MenuItem subtitleSpeed = menu.findItem(R.id.subtitle_speed);
        if (subtitleSpeed != null) {
            subtitleSpeed.setEnabled(hasSubtitle);
        }
        MenuItem subtitleSearch = menu.findItem(R.id.subtitle_search);
        MenuItem subtitleRate = menu.findItem(R.id.subtitle_rate);
        MenuItem subtitleUpload = menu.findItem(R.id.subtitle_upload);
        int subtitleServiceCaps = SubtitleServiceManager.getCapabilities(getSubtitleServiceMedia(), collectExternalSubtitles().size());
        subtitleSearch.setEnabled((subtitleServiceCaps & 1) != 0);
        subtitleRate.setEnabled((subtitleServiceCaps & 2) != 0);
        subtitleUpload.setEnabled((subtitleServiceCaps & 4) != 0);
        MenuItem sleepTimer = menu.findItem(R.id.sleep_timer);
        if (sleepTimer != null) {
            sleepTimer.setChecked(L.sleepTimer != null);
        }
        IMediaPlayer mp = this.pp.mp();
        MenuItem playbackSpeed = menu.findItem(R.id.playback_speed);
        if (playbackSpeed != null) {
            playbackSpeed.setEnabled((mp == null || (mp.getCharacteristics() & 8) == 0) ? false : true);
        }
        MenuItem stereoMode = menu.findItem(R.id.stereo_mode);
        if (stereoMode != null) {
            boolean enable = false;
            if (mp != null && (mp.getCharacteristics() & 32) != 0 && (audioStream = mp.getAudioStream()) >= 0 && mp.getAudioChannelCount(audioStream) >= 2) {
                enable = true;
                switch (this._stereoMode) {
                    case 1:
                        menu.findItem(R.id.mono).setChecked(true);
                        break;
                    case 2:
                        menu.findItem(R.id.reverse_stereo).setChecked(true);
                        break;
                    case 99:
                        menu.findItem(R.id.auto_reverse_stereo).setChecked(true);
                        break;
                    default:
                        menu.findItem(R.id.stereo).setChecked(true);
                        break;
                }
            }
            stereoMode.setEnabled(enable);
        }
        MenuItem audioSync = menu.findItem(R.id.audio_sync);
        if (audioSync != null) {
            audioSync.setEnabled((mp == null || (mp.getCharacteristics() & 16) == 0) ? false : true);
        }
        if (this._audioMenuItem != null) {
            boolean selectable = isAudioTrackSelectable();
            this._audioMenuItem.setVisible(selectable);
            this._audioMenuItem.setEnabled(selectable);
            return true;
        }
        return true;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == 108 && this._audioMenuItem != null && this._audioMenuItem.isEnabled() && MenuUtils.isInOverflow(this._audioMenuItem)) {
            this._audioMenuItem.setVisible(false);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        if (featureId == 108 && this._audioMenuItem != null && this._audioMenuItem.isEnabled()) {
            this._audioMenuItem.setVisible(true);
        }
        super.onPanelClosed(featureId, menu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStereoMode() {
        if (this.pp.mp() != null) {
            if (this._stereoMode == 99) {
                this.pp.setStereoMode(resolveAutoReverseStereoMode(this, this._headsetPlugged, getWindowManager().getDefaultDisplay()));
                if (this._orientationEventListener == null) {
                    this._orientationEventListener = new OrientationEventListener(this) { // from class: com.mxtech.videoplayer.ActivityScreen.4
                        @Override // android.view.OrientationEventListener
                        public void onOrientationChanged(int orientation) {
                            ActivityScreen.this.updateStereoMode();
                        }
                    };
                    this._orientationEventListener.enable();
                    return;
                }
                return;
            }
            this.pp.setStereoMode(this._stereoMode);
        }
        if (this._orientationEventListener != null) {
            this._orientationEventListener.disable();
        }
    }

    public static int resolveAutoReverseStereoMode(Context context, boolean headsetPlugged, Display display) {
        if (headsetPlugged) {
            return 0;
        }
        int screenOrientation = DeviceUtils.getScreenOrientation(context, display);
        return screenOrientation == 8 ? 2 : 0;
    }

    /* loaded from: classes.dex */
    private class MediaOpener extends DirectMediaOpener {
        MediaOpener() {
            super(ActivityScreen.this);
        }

        @Override // com.mxtech.videoplayer.widget.DirectMediaOpener
        protected void open(Uri uri) {
            ActivityScreen.this.returnOk(ActivityScreen.kEndByUser);
            Playlist.Autogen.cleanup(ActivityScreen.this.getIntent().getData());
            Intent i = ActivityScreen.this.getIntent();
            i.setData(uri);
            i.removeExtra(ActivityScreen.EXTRA_HEADERS);
            i.removeExtra("video_zoom");
            i.removeExtra("sticky");
            i.removeExtra("suppress_error_message");
            i.removeExtra(ActivityScreen.EXTRA_SECURE_URI);
            i.removeExtra(ActivityScreen.EXTRA_DAR_HORZ);
            i.removeExtra(ActivityScreen.EXTRA_DAR_VERT);
            i.removeExtra(ActivityScreen.EXTRA_POSITION);
            i.removeExtra(ActivityScreen.EXTRA_SUBTITLES);
            i.removeExtra(ActivityScreen.EXTRA_SUBTITLES_NAME);
            i.removeExtra(ActivityScreen.EXTRA_SUBTITLES_FILENAME);
            i.removeExtra(ActivityScreen.EXTRA_SUBTITLES_ENABLE);
            i.removeExtra(ActivityScreen.EXTRA_LIST);
            i.removeExtra("title");
            i.removeExtra(ActivityScreen.EXTRA_TITLES);
            i.removeExtra(ActivityScreen.EXTRA_SIZE);
            i.removeExtra(ActivityScreen.EXTRA_SIZES);
            i.removeExtra(ActivityScreen.EXTRA_FILENAME);
            i.removeExtra(ActivityScreen.EXTRA_FILENAMES);
            i.removeExtra(ActivityScreen.EXTRA_HASH_OPENSUBTITLES);
            i.removeExtra(ActivityScreen.EXTRA_HASHES_OPENSUBTITLES);
            i.removeExtra(ActivityScreen.EXTRA_RETURN_RESULT);
            ActivityScreen.this.reconfigHttpFactory();
            ActivityScreen.this.pp.save();
            ActivityScreen.this.pp.clear();
            ActivityScreen.this.applyIntent(i, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconfigHttpFactory() {
        this._httpFactory.clearHeaders();
        this._httpFactory.addHeader("User-Agent", P.httpUserAgent);
        String[] givenHeaders = getIntent().getStringArrayExtra(EXTRA_HEADERS);
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

    @Override // com.mxtech.videoplayer.subtitle.SubtitlePanel.Client
    public final boolean getFullScreen() {
        return this._fullscreen == 0;
    }

    /* loaded from: classes.dex */
    private class MediaFileDeleteHandler implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, ActivityVPBase.FileOperationSink {
        private HashSet<File> _auxFiles = new HashSet<>();
        private boolean _deleted;
        private int _direction;
        private File _mediaFile;
        private Uri _next;

        @SuppressLint({"InflateParams"})
        MediaFileDeleteHandler() {
            this._mediaFile = ActivityScreen.this.pp.getFile();
            if (this._mediaFile != null) {
                String mediaFileName = this._mediaFile.getName();
                if (P.deleteSubtitleFilesTogether) {
                    this._auxFiles.addAll(Arrays.asList(SubtitleFactory.scan(mediaFileName, this._mediaFile.getParentFile())));
                }
                File coverFile = ImageScanner.scanCoverFile(this._mediaFile);
                if (coverFile != null) {
                    this._auxFiles.add(coverFile);
                }
                L.sb.setLength(0);
                L.sb.append(mediaFileName);
                Iterator<File> it = this._auxFiles.iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    L.sb.append(", ").append(file.getName());
                }
                AlertDialog dlg = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.menu_delete).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                View layout = dlg.getLayoutInflater().inflate(R.layout.delete_confirm, (ViewGroup) null);
                ((TextView) layout.findViewById(R.id.message)).setText(StringUtils.getString_s(R.string.edit_inquire_delete, ActivityScreen.this.getResources().getQuantityString(R.plurals.files, this._auxFiles.size() + 1)));
                ((TextView) layout.findViewById(R.id.content)).setText(L.sb.toString());
                dlg.setView(layout);
                ActivityScreen.this.showDialog((ActivityScreen) dlg);
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            MediaDatabase mdb;
            SQLiteDatabase db;
            Log.v(ActivityScreen.TAG, "Deleting 1 + " + this._auxFiles.size() + " files & updating database.");
            if (!ActivityScreen.this.isFinishing()) {
                Uri mediaUri = ActivityScreen.this.pp.getUri();
                ActivityScreen.this.pp.save();
                ActivityScreen.this.pp.clear(16);
                try {
                    mdb = MediaDatabase.getInstance();
                    db = mdb.db;
                    db.beginTransaction();
                    try {
                    } finally {
                        db.endTransaction();
                    }
                } catch (SQLiteException e) {
                    Log.e(ActivityScreen.TAG, "", e);
                }
                if (!MediaUtils.delete(mdb, 0, this._mediaFile)) {
                    Log.v(ActivityScreen.TAG, this._mediaFile.getPath() + " was NOT deleted. (exists:" + this._mediaFile.exists() + " canRead:" + this._mediaFile.canRead() + " canWrite:" + this._mediaFile.canWrite() + ")");
                    ActivityScreen.this.handleFileWritingFailure(this._mediaFile, 1, 1, this);
                    ActivityScreen.this.load(mediaUri, 7, (byte) 0, 0);
                    mdb.release();
                    return;
                }
                if (!P.playerBackToList) {
                    this._direction = P.shuffle ? 0 : 1;
                    this._next = ActivityScreen.this._lister.get(mediaUri, this._direction);
                    ActivityScreen.this._lister.remove(mediaUri);
                }
                Iterator<File> it = this._auxFiles.iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    if (FileUtils.delete(App.cr, file)) {
                        Log.v(ActivityScreen.TAG, file.getPath() + " was deleted.");
                    } else {
                        Log.v(ActivityScreen.TAG, file.getPath() + " was NOT deleted.");
                    }
                }
                this._deleted = true;
                db.setTransactionSuccessful();
                mdb.release();
                next();
            }
        }

        private void next() {
            if (this._next != null) {
                ActivityScreen.this.loadNext(this._next, this._direction);
            } else {
                ActivityScreen.this.finish();
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            ActivityScreen.this.dialogRegistry.unregister(dialog);
            if (this._deleted) {
                next();
            }
        }

        @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
        public void onFileOperationRetry() {
            ActivityScreen.this.defaultFileOperationRetry();
        }

        @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
        public void onFileOperationFailed(int numTotal, int numFailed) {
            if (!ActivityScreen.this.isFinishing()) {
                ActivityScreen.this.showDialog((ActivityScreen) L.createFileWriteFailureDialog(ActivityScreen.this, App.getFileDeletionFailureMessage(numTotal, numFailed)), (DialogInterface.OnDismissListener) this);
            }
        }
    }

    /* loaded from: classes.dex */
    private class MediaFileRenameHandler implements DialogInterface.OnClickListener {
        private File _mediaFile;
        private String _mediaFileName;
        private Uri _mediaUri;

        MediaFileRenameHandler() {
            this._mediaFile = ActivityScreen.this.pp.getFile();
            if (this._mediaFile != null) {
                this._mediaUri = ActivityScreen.this.pp.getUri();
                this._mediaFileName = this._mediaFile.getName();
                DialogUtils.showSimpleInputDialog(ActivityScreen.this, FileUtils.stripExtension(this._mediaFileName), this, R.string.edit_rename_to);
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            Uri newUri;
            ActivityScreen.this.pp.save();
            ActivityScreen.this.pp.clear(528);
            File[] subFiles = SubtitleFactory.scan(this._mediaFileName, this._mediaFile.getParentFile());
            File coverFile = ImageScanner.scanCoverFile(this._mediaFile);
            String newName = StringUtils.trimRight(((TextView) ((Dialog) dialog).findViewById(16908291)).getText().toString().trim(), '.');
            File newFile = FileUtils.changeName(this._mediaFile, newName);
            if (MediaUtils.renameMediaTo(ActivityScreen.this, this._mediaFile, newFile, subFiles, coverFile) >= 0) {
                newUri = Uri.fromFile(newFile);
                ActivityScreen.this._lister.rename(this._mediaUri, newUri);
            } else {
                newUri = this._mediaUri;
            }
            ActivityScreen.this.load(newUri, 7, (byte) 0, 0);
        }
    }

    @Override // com.mxtech.app.MXAppCompatActivity, com.mxtech.app.PopupMenuHack.OptionsItemSelector
    @SuppressLint({"InflateParams"})
    public boolean onOptionsItemSelected2(MenuItem item) {
        int stereoMode;
        if (isFinishing()) {
            return true;
        }
        int id = item.getItemId();
        if (id == 16908332) {
            if (goBack(true)) {
                returnOk(kEndByUser);
                super.onBackPressed();
            }
            return true;
        } else if (id == R.id.decoder) {
            new DecoderSwitcher().invoke();
            return true;
        } else if (id == R.id.audio) {
            invokeAudioTrackSelector();
            return true;
        } else if (id == R.id.audio_sync) {
            toggleFloatingBar(R.id.audio_sync_bar);
            return true;
        } else if (id == R.id.stereo || id == R.id.reverse_stereo || id == R.id.auto_reverse_stereo || id == R.id.mono) {
            SharedPreferences.Editor editor = App.prefs.edit();
            if (id == R.id.reverse_stereo) {
                stereoMode = 2;
            } else if (id == R.id.auto_reverse_stereo) {
                stereoMode = 99;
            } else if (id == R.id.mono) {
                stereoMode = 1;
            } else {
                stereoMode = 0;
            }
            editor.putInt(Key.STEREO_MODE, stereoMode);
            AppUtils.apply(editor);
            return true;
        } else if (id == R.id.caption) {
            invokeCaptionSelector();
            return true;
        } else if (id == R.id.open_subtitle) {
            new SubtitleOpener(this, this.dialogRegistry, this.pp.getUri(), this._subView.getSubtitleCount() > 0, this);
            return true;
        } else if (id == R.id.open_url) {
            new MediaOpener();
            return true;
        } else if (id == R.id.shuffle) {
            SharedPreferences.Editor editor2 = App.prefs.edit();
            editor2.putBoolean(Key.SHUFFLE, !P.shuffle);
            AppUtils.apply(editor2);
            return true;
        } else if (id == R.id.loop_all) {
            SharedPreferences.Editor editor3 = App.prefs.edit();
            editor3.putInt(Key.LOOP, P.playerLooping == 9 ? 0 : 9);
            AppUtils.apply(editor3);
            return true;
        } else if (id == R.id.loop_one) {
            SharedPreferences.Editor editor4 = App.prefs.edit();
            editor4.putInt(Key.LOOP, P.playerLooping == 1 ? 0 : 1);
            AppUtils.apply(editor4);
            return true;
        } else if (id == R.id.sticky) {
            if (canUseStickyMode()) {
                if (this._stickyRequested && !this._stickyAutoReset) {
                    this._stickyRequested = false;
                    this._stickyAutoReset = true;
                } else {
                    this._stickyRequested = true;
                    this._stickyAutoReset = false;
                }
                updateStickyState(true);
            }
            return true;
        } else if (id == R.id.zoom) {
            this._zoomButtonHandler.invokeZoomBox();
            return true;
        } else if (id == R.id.aspect_ratio) {
            invokeAspectRatioSelector();
            return true;
        } else if (id == R.id.rotation) {
            lockRotation(item.isChecked());
            return true;
        } else if (id == R.id.deinterlace_yadif) {
            JointPlayer mp = this.pp.mp();
            if (mp != null) {
                int processing = mp.getProcessing() & (-4);
                if (!item.isChecked()) {
                    processing |= 1;
                }
                mp.setProcessing(processing);
            }
            return true;
        } else if (id == R.id.deinterlace_w3fdif) {
            JointPlayer mp2 = this.pp.mp();
            if (mp2 != null) {
                int processing2 = mp2.getProcessing() & (-4);
                if (!item.isChecked()) {
                    processing2 |= 2;
                }
                mp2.setProcessing(processing2);
            }
            return true;
        } else if (id == R.id.use_speedup_tricks) {
            SharedPreferences.Editor editor5 = App.prefs.edit();
            editor5.putBoolean(Key.USE_SPEEDUP_TRICKS, !this._useSpeedupTricks);
            AppUtils.apply(editor5);
            return true;
        } else if (id == R.id.quit) {
            returnOk(kEndByUser);
            App.quit();
            return true;
        } else if (id == R.id.detail_info) {
            if (this.pp.isInPlaybackState()) {
                IMediaInfo info = this.pp.mp().info();
                try {
                    MediaDatabase mdb = MediaDatabase.getInstance();
                    int flags = 2;
                    try {
                        if (getIntent().getBooleanExtra(EXTRA_SECURE_URI, false)) {
                            flags = 2 | 8;
                        }
                        showDialog((ActivityScreen) new MediaInfoDialog(this, this.pp.getUri(), info, this.pp.mp(), this, mdb, flags));
                    } finally {
                        mdb.release();
                    }
                } catch (SQLiteException e) {
                    Log.e(TAG, "", e);
                    DialogUtils.alert(this, R.string.error_database);
                } finally {
                    info.close();
                }
            }
            return true;
        } else if (id == R.id.repeat_ab) {
            toggleFloatingBar(R.id.repeat_ab_bar);
            return true;
        } else if (id == R.id.subtitle_search) {
            if (this.pp.isInPlaybackState()) {
                searchSubtitles();
            }
            return true;
        } else if (id == R.id.subtitle_rate) {
            rateSubtitles();
            return true;
        } else if (id == R.id.subtitle_upload) {
            uploadSubtitles();
            return true;
        } else if (id == R.id.subtitle_sync) {
            toggleFloatingBar(R.id.subtitle_sync_bar);
            return true;
        } else if (id == R.id.subtitle_speed) {
            toggleFloatingBar(R.id.subtitle_speed_bar);
            return true;
        } else if (id == R.id.subtitle_panel) {
            if (isSubtitlePanelVisible()) {
                hideSubtitlePanel();
            } else {
                showSubtitlePanel();
            }
            return true;
        } else if (id == R.id.subtitle_settings) {
            showTuner(1);
            return true;
        } else if (id == R.id.tune_display) {
            showTuner(0);
            return true;
        } else if (id == R.id.playback_speed) {
            toggleFloatingBar(R.id.playback_speed_bar);
            return true;
        } else if (id == R.id.preference) {
            try {
                startActivity(new Intent(getApplicationContext(), AppUtils.findActivityKindOf(this, ActivityPreferences.class)));
            } catch (Exception e2) {
                Log.e(TAG, "", e2);
            }
            return true;
        } else if (id == R.id.lock) {
            if (this._lockButtonHandler != null) {
                this._lockButtonHandler.invokeLockBox();
            }
            return true;
        } else if (id == R.id.share) {
            shareCurrentItem();
            return true;
        } else if (id == R.id.delete) {
            new MediaFileDeleteHandler();
            return true;
        } else if (id == R.id.rename) {
            new MediaFileRenameHandler();
            return true;
        } else if (id == R.id.sleep_timer) {
            new SleepTimer(this);
            return true;
        } else if (((App) getApplication()).handleHelpCommand(this, id)) {
            return true;
        } else {
            return super.onOptionsItemSelected2(item);
        }
    }

    private SubtitleServiceManager getSubtitleServiceManager() {
        if (this._sdm == null) {
            this._sdm = new SubtitleServiceManager(this, 85);
            this._sdm.setOnDownloadListener(this);
        }
        return this._sdm;
    }

    private void searchSubtitles() {
        Media media = getSubtitleServiceMedia();
        if (media != null) {
            SubtitleServiceManager sdm = getSubtitleServiceManager();
            sdm.cancel();
            sdm.search(media);
        }
    }

    private void rateSubtitles() {
        if (this.pp.isInPlaybackState()) {
            SubtitleServiceManager sdm = getSubtitleServiceManager();
            sdm.cancel();
            Collection<SubtitleServiceManager.MediaSubtitle> subs = collectExternalSubtitles();
            if (subs.size() > 0) {
                sdm.rate((SubtitleServiceManager.MediaSubtitle[]) subs.toArray(new SubtitleServiceManager.MediaSubtitle[subs.size()]));
            }
        }
    }

    private void uploadSubtitles() {
        if (this.pp.isInPlaybackState()) {
            SubtitleServiceManager sdm = getSubtitleServiceManager();
            sdm.cancel();
            Collection<SubtitleServiceManager.MediaSubtitle> subs = collectExternalSubtitles();
            if (subs.size() > 0) {
                sdm.upload((SubtitleServiceManager.MediaSubtitle[]) subs.toArray(new SubtitleServiceManager.MediaSubtitle[subs.size()]), this._subView.getSync() != 0);
            }
        }
    }

    @Nullable
    private Media getSubtitleServiceMedia() {
        if (!this.pp.isInPlaybackState()) {
            return null;
        }
        if (!this._subtitleServiceMediaTried) {
            this._subtitleServiceMediaTried = true;
            Intent intent = getIntent();
            File file = this.pp.getFile();
            String filename = intent.getStringExtra(EXTRA_FILENAME);
            if (filename == null) {
                if (file != null) {
                    filename = file.getName();
                } else {
                    filename = this.pp.getUri().getLastPathSegment();
                    if (filename == null || !P.isSupportedMediaFile(filename)) {
                        return null;
                    }
                }
            }
            IMediaInfo info = this.pp.mp().info();
            try {
                this._subtitleServiceMedia = new Media(this.pp.getUri(), this._httpFactory, filename, file, getMediaTitle(), info.title(), this.pp.getDuration(), this.pp.frameTime(), intent.getLongExtra(EXTRA_SIZE, 0L), intent.getStringExtra(EXTRA_HASH_OPENSUBTITLES));
            } finally {
                info.close();
            }
        }
        return this._subtitleServiceMedia;
    }

    private Collection<SubtitleServiceManager.MediaSubtitle> collectExternalSubtitles() {
        ISubtitle[] allSubtitles;
        Subtitle searchSub;
        if (this._externalServiceSubs == null) {
            this._externalServiceSubs = new ArrayMap<>();
            Media media = getSubtitleServiceMedia();
            if (media != null) {
                for (ISubtitle sub : this._subView.getAllSubtitles()) {
                    if ((sub.flags() & 65536) == 0) {
                        Uri sourceUri = Misc.removeFragment(sub.uri());
                        if (!this._externalServiceSubs.containsKey(sourceUri)) {
                            String scheme = sourceUri.getScheme();
                            if (scheme == null || scheme.equals(MediaListFragment.TYPE_FILE)) {
                                searchSub = new FileSubtitle(sourceUri);
                            } else {
                                searchSub = this.pp.getSubtitleAuxData(sourceUri);
                                if (searchSub == null) {
                                }
                            }
                            this._externalServiceSubs.put(sourceUri, new SubtitleServiceManager.MediaSubtitle(media, searchSub));
                        }
                    }
                }
            }
        }
        return this._externalServiceSubs.values();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.OnDownloadListener
    public void onDowloadBegin(SubtitleServiceManager manager) {
        setAuxNotification(getString(R.string.subtitle_downloading));
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.OnDownloadListener
    public void onDownloadEnd(SubtitleServiceManager manager) {
        setAuxNotification(null);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.OnDownloadListener
    public void onDwonloaded(SubtitleServiceManager loader, Media media, String queryText, File file) {
        onLoadSubtitle(file, false);
    }

    public void onSleepTimerChanged(@Nullable SleepTimer sleepTimer) {
        updateTitle();
    }

    public void onSleepTimerFired(SleepTimer sleepTimer) {
        this.pp.pause(0);
        updateTitle();
    }

    private void shareCurrentItem() {
        String mime;
        Uri uri = this.pp.getUri();
        if (uri != null) {
            Intent intent = new Intent("android.intent.action.SEND");
            if (this.pp.getFile() != null) {
                if (this.pp.isInPlaybackState()) {
                    if (this._hasVideoTrack) {
                        mime = "video/*";
                    } else {
                        mime = "audio/*";
                    }
                } else {
                    String ext = FileUtils.getExtension(uri.toString());
                    if (ext != null) {
                        if (P.getDefaultAudioExtensions().contains(ext)) {
                            mime = "audio/*";
                        } else {
                            mime = "video/*";
                        }
                    } else {
                        mime = "*/*";
                    }
                }
                intent.putExtra("android.intent.extra.STREAM", uri);
            } else {
                mime = "text/plain";
                intent.putExtra("android.intent.extra.TEXT", uri.toString());
            }
            intent.setType(mime);
            Intent chooser = Intent.createChooser(intent, getString(R.string.share));
            chooser.addFlags(268435456);
            try {
                startActivity(chooser);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    private void showTuner(int category) {
        if (this.dialogRegistry.size() <= 0) {
            flushChanges();
            showDialog((ActivityScreen) new Tuner(this, this, this.dialogRegistry, -1, category));
        }
    }

    private boolean isControllerAnyVisible() {
        return this._controller.getVisibleParts() != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isControllerFullyVisible() {
        return (this._controller.getVisibleParts() & 5) == 5;
    }

    private boolean wasControllerFullyVisible() {
        return (this._controller.getPrevVisibleParts() & 5) == 5;
    }

    private boolean isControllerSeekingVisible() {
        return (this._controller.getVisibleParts() & 5) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showController() {
        if (!isControllerFullyVisible()) {
            this._controller.show(2);
            if (this._playPauseButton != null) {
                this._playPauseButton.requestFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TrackingSpan {
        public int lastPosition;
        public final int startPosition;

        public TrackingSpan(int pos) {
            this.startPosition = pos;
            this.lastPosition = pos;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void consecutiveSeekBegin() {
        if (this._consecutiveSeekBegin < 0) {
            this._consecutiveSeekBegin = currentPosition();
            this._lastSwipeSeekPreviewTarget = -999999;
        }
    }

    private void previewSeekTo(int msec) {
        if (msec < this._lastSwipeSeekPreviewTarget - 500 || msec > this._lastSwipeSeekPreviewTarget + 500) {
            this._lastSwipeSeekPreviewTarget = msec;
            this.pp.seekTo(msec, -1);
            if (this.pp.getTargetState() == 4 && this.pp.getState() == 6) {
                this.pp.mp().start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekToDelta(int delta, int flags) {
        boolean newSpan;
        if (this.pp.isInPlaybackState()) {
            if ((flags & 2) != 0 && !isControllerAnyVisible()) {
                this._controller.setVisibleParts(1, 0);
            }
            this._controller.extendVisibility();
            CharSequence text = this._supremeText.getText();
            TrackingSpan span = null;
            if (text instanceof Spanned) {
                Spanned s = (Spanned) text;
                TrackingSpan[] spans = (TrackingSpan[]) s.getSpans(0, s.length(), TrackingSpan.class);
                if (spans.length > 0) {
                    span = spans[0];
                }
            }
            if (span == null) {
                span = new TrackingSpan(this._seekBar.getProgress());
                newSpan = true;
            } else {
                newSpan = false;
            }
            int duration = this.pp.getDuration();
            int next = span.lastPosition + delta;
            if (next < 0) {
                next = 0;
            } else if (next >= duration) {
                next = duration - 1;
            }
            if ((flags & 1) != 0) {
                setVideoProgress(next);
                this.pp.seekTo(next, 6000);
            } else if (span.lastPosition != next) {
                if (showSeekPreviews()) {
                    previewSeekTo(next);
                }
                setVideoProgress(next);
            }
            if (this._displaySeekingPosition) {
                setSeekSupremeText(next, next - span.startPosition, span);
            } else {
                if (text.length() > 0 || newSpan) {
                    SpannableString s2 = new SpannableString("");
                    s2.setSpan(span, 0, 0, 17);
                    text = s2;
                }
                setSupremeText(text);
            }
            this.handler.sendEmptyMessageDelayed(4, 750L);
            span.lastPosition = next;
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        if (this.dialogRegistry.size() > 0) {
            return super.dispatchTrackballEvent(ev);
        }
        this._controller.extendVisibility();
        switch (ev.getAction()) {
            case 0:
                toggle(true);
                return true;
            case 1:
            default:
                return true;
            case 2:
                seekToDelta((int) (ev.getX() * 20000.0f), 3);
                return true;
        }
    }

    @Override // android.app.Activity, com.mxtech.videoplayer.service.PlayService.PlayClient
    public void finish() {
        if (!isFinishing()) {
            if (this.pp.isFontSettingUp()) {
                if (!this.dialogRegistry.containsInstanceOf(FontCacheProgressDialog.class)) {
                    this.handler.removeMessages(9);
                    showDialog((ActivityScreen) new FontCacheProgressDialog());
                    return;
                }
                return;
            }
            super.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void returnResult(int code) {
        returnResult(code, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void returnOk(String endBy) {
        returnResult(-1, endBy);
    }

    private void returnResult(int code, @Nullable String endBy) {
        if (getIntent().getBooleanExtra(EXTRA_RETURN_RESULT, false)) {
            Uri uri = this.pp.getUri();
            if (uri == null) {
                Log.v(TAG, "Activity result: Can't return result because media player object is already closed.");
                return;
            }
            Intent result = new Intent(RESULT_VIEW, uri);
            if (this.pp.isInPlaybackState()) {
                if (this.pp.getState() != 6) {
                    result.putExtra(EXTRA_POSITION, this.pp.getCurrentPosition());
                    result.putExtra(EXTRA_DURATION, this.pp.getDuration());
                }
                byte decodeMode = this.pp.getDecoder();
                result.putExtra(EXTRA_DECODE_MODE, decodeMode);
            }
            if (endBy != null) {
                result.putExtra(EXTRA_END_BY, endBy);
            }
            setResult(code, result);
            Log.v(TAG, "Activity result: Successfully returned.");
            return;
        }
        Log.v(TAG, "Activity result: Result is not returned because 'return_result' extra was not set.");
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (goBack(false)) {
            returnOk(kEndByUser);
            super.onBackPressed();
        }
    }

    private boolean goBack(boolean fromHomeAsUpButton) {
        if (!fromHomeAsUpButton) {
            if (removePopupLikes()) {
                return false;
            }
            if (!DeviceUtils.hasTouchScreen && isControllerFullyVisible()) {
                this._controller.hide(0);
                return false;
            } else if (App.prefs.getBoolean(Key.DOUBLE_TAP_BACK_KEY, false)) {
                long now = SystemClock.uptimeMillis();
                if (this._last_double_tap_backkey_time + 2000 < now) {
                    if (this._double_tap_close_toast == null) {
                        this._double_tap_close_toast = Toast.makeText(this, R.string.press_back_key_again_to_close, 0);
                    }
                    this._double_tap_close_toast.show();
                    this._last_double_tap_backkey_time = now;
                    return false;
                }
                this._double_tap_close_toast.cancel();
            }
        }
        return true;
    }

    @Override // com.mxtech.videoplayer.MediaButtonReceiver.IReceiver
    public final void onMediaKeyReceived(KeyEvent event) {
        handleMediaKeyEvent(event.getAction(), event.getKeyCode());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void handleMediaKeyEvent(int action, int keyCode) {
        if (P.prevnext_to_rewff) {
            if (keyCode == 87) {
                keyCode = 90;
            } else if (keyCode == 88) {
                keyCode = 89;
            }
        }
        if (action == 0) {
            switch (keyCode) {
                case 89:
                    handleKeydown_Left(1);
                    return;
                case 90:
                    handleKeydown_Right(1);
                    return;
                default:
                    return;
            }
        } else if (action == 1) {
            switch (keyCode) {
                case 79:
                case 85:
                    break;
                case 86:
                    if (this.pp.isInPlaybackState()) {
                        this.pp.pause(0);
                        this.pp.seekTo(0, 6000);
                        return;
                    }
                    return;
                case 87:
                    next();
                    return;
                case 88:
                    previous();
                    return;
                case 89:
                case 90:
                    handleKeyup_LeftRight();
                    return;
                case TransportMediator.KEYCODE_MEDIA_PLAY /* 126 */:
                    if (!P.toggleOnMediaPlayButton) {
                        start();
                        return;
                    }
                    break;
                case TransportMediator.KEYCODE_MEDIA_PAUSE /* 127 */:
                    pause(64);
                    setPauseIndicatorImageLevel(2);
                    return;
                default:
                    return;
            }
            toggle(true);
            if (this.pp.getTargetState() == 5) {
                setPauseIndicatorImageLevel(2);
            }
            if (!DeviceUtils.hasTouchScreen) {
                showController();
            }
        }
    }

    private boolean handleVolumeKeyDown(int keyCode) {
        if (!DeviceUtils.isTV && P.syncSystemVolume) {
            ScreenVerticalBar bar = getSoundBar();
            bar.show();
            bar.change(keyCode == 24 ? 1 : -1);
            return true;
        }
        return false;
    }

    private boolean handleVolumeKeyUp(int keyCode) {
        return !DeviceUtils.isTV && P.syncSystemVolume;
    }

    @SuppressLint({"NewApi"})
    private boolean handleKeyEvent(int action, int keyCode, @Nullable KeyEvent event) {
        ScreenVerticalBar bar;
        L.sb.setLength(0);
        L.sb.append("Key pressed - action:").append(action).append(" code:").append(keyCode);
        if (event != null) {
            L.sb.append(" repeat:").append(event.getRepeatCount()).append(" meta:").append(event.getMetaState());
        }
        Log.v(TAG, L.sb.toString());
        if (action == 0) {
            this._lastDownKey = keyCode;
            switch (keyCode) {
                case 19:
                case 20:
                    if (!isDPADNavigationRequired()) {
                        if (P.playbackKeyUpdownAction == 2) {
                            bar = getBrightnessBar();
                        } else {
                            bar = getSoundBar();
                        }
                        bar.show();
                        bar.change(keyCode == 19 ? 1 : -1);
                        return true;
                    }
                    break;
                case 21:
                    if (!isDPADNavigationRequired() || this._seekBar.hasFocus()) {
                        handleKeydown_Left(1);
                        return true;
                    }
                    break;
                case 22:
                    if (!isDPADNavigationRequired() || this._seekBar.hasFocus()) {
                        handleKeydown_Right(1);
                        return true;
                    }
                    break;
                case 24:
                case 25:
                    if (handleVolumeKeyDown(keyCode)) {
                        return true;
                    }
                    break;
                case 102:
                    handleKeydown_Left(3);
                    return true;
                case 103:
                    handleKeydown_Right(3);
                    return true;
                case 106:
                    handleKeydown_Left(1);
                    return true;
                case 107:
                    handleKeydown_Right(1);
                    return true;
            }
        } else if (action == 1 && (event == null || event.getRepeatCount() == 0)) {
            if (this._lastDownKey != keyCode) {
                handleKeyEvent(0, keyCode, null);
            }
            this._lastDownKey = 0;
            switch (keyCode) {
                case 21:
                case 22:
                case 102:
                case 103:
                case 106:
                case 107:
                    return handleKeyup_LeftRight();
                case 23:
                    if (!isDPADNavigationRequired()) {
                        showController();
                        return true;
                    }
                    break;
                case 24:
                case 25:
                    if (handleVolumeKeyUp(keyCode)) {
                        return true;
                    }
                    break;
                case 62:
                case 108:
                    toggle(true);
                    if (this.pp.getTargetState() == 5) {
                        setPauseIndicatorImageLevel(2);
                        return true;
                    }
                    return true;
            }
        }
        if (P.respectMediaButtons && MediaButtonReceiver.isMediaKey(keyCode)) {
            handleMediaKeyEvent(action, keyCode);
            return true;
        }
        return false;
    }

    private void handleKeydown_Left(int factor) {
        handleKeydown_LeftRight(factor, -1);
    }

    private void handleKeydown_Right(int factor) {
        handleKeydown_LeftRight(factor, 1);
    }

    private void handleKeydown_LeftRight(int factor, int direction) {
        if (this.pp.isInPlaybackState()) {
            if (!showSeekPreviews()) {
                Log.v(TAG, "Pause playback temporarily responding to key left/right");
                pause(80);
            }
            consecutiveSeekBegin();
            seekToDelta(this._naviMoveIntervalMillis * direction * factor, 2);
        }
    }

    private boolean handleKeyup_LeftRight() {
        if (this._consecutiveSeekBegin >= 0) {
            this._consecutiveSeekBegin = -1;
            seekTo(this._seekBar.getProgress(), 6000);
            resume();
            return true;
        }
        return false;
    }

    private boolean isDPADNavigationRequired() {
        return isControllerFullyVisible() || isSubtitlePanelVisible() || hasFloatingBars();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        this._controller.extendVisibility();
        if (handleKeyEvent(event.getAction(), event.getKeyCode(), event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this._autoHideInterface) {
            this._controller.extendVisibility();
            delaySoftButtonsHide();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // com.mxtech.videoplayer.Lock.Listener
    public boolean onKeyLocked(Lock lock, KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 23:
                if (DeviceUtils.hasTouchScreen) {
                    return true;
                }
                lock.unlock();
                return true;
            case 24:
            case 25:
                if (action == 0) {
                    return handleVolumeKeyDown(keyCode);
                }
                if (action == 1) {
                    return handleVolumeKeyUp(keyCode);
                }
                return true;
            default:
                if (P.respectMediaButtons && MediaButtonReceiver.isMediaKey(keyCode)) {
                    handleMediaKeyEvent(action, keyCode);
                    return true;
                }
                lock.showNotification(2);
                return true;
        }
    }

    @Override // com.mxtech.videoplayer.Lock.Listener
    @SuppressLint({"ClickableViewAccessibility"})
    public final boolean onTouch(Lock lock, View v, MotionEvent event) {
        return handleUITouch(v, event, 0);
    }

    @Override // com.mxtech.videoplayer.Lock.Listener
    public void onUnlocked(Lock lock) {
        this._lock = null;
        if (Build.VERSION.SDK_INT >= 11 && this._systemWindowFittable != null && this._fullscreen == 2 && !this._lockShowInterfaceOnTouched) {
            setWindowFullScreen(false);
        }
        if (this._lockButtonHandler != null && (this._lockButtonHandler.getLockTargets() & 2) != 0) {
            lockRotation(false);
        }
        showController();
    }

    @Override // com.mxtech.videoplayer.Lock.Listener
    public void onWindowFocusChanged(Lock lock, boolean focused) {
        if (focused) {
            resume();
        }
    }

    private boolean removePopupLikes() {
        boolean screenCleared = false;
        if (hideSubtitlePanel()) {
            screenCleared = true;
        }
        if (hasFloatingBars()) {
            hideAllFloatingBars();
            return true;
        }
        return screenCleared;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onBufferingUpdate(int percent) {
        if (percent < 100) {
            this._seekBar.setSecondaryProgress((this._seekBar.getMax() * percent) / 100);
        } else {
            this._seekBar.setSecondaryProgress(0);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onSubtitleInvalidated() {
        this._subView.refresh();
    }

    private void handleOrientationChange(int orientation) {
        if (this._captionMenuItem != null) {
            MenuItemCompat.setShowAsAction(this._captionMenuItem, orientation != 2 ? 1 : 2);
        }
        this._subView.onOrientationChanged(orientation);
        if (!this._hasVideoTrack && this._coverView != null) {
            updateCoverView();
        }
        if (L.sleepTimer != null) {
            updateTitle();
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity
    protected void onOrientationChanged(int orientation) {
        SleepTimer sleepTimer;
        super.onOrientationChanged(orientation);
        handleOrientationChange(orientation);
        if (!isFinishing() && (sleepTimer = (SleepTimer) this.dialogRegistry.findByType(SleepTimer.class)) != null) {
            sleepTimer.dismiss();
            new SleepTimer(this);
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public int getOrientation() {
        return this.orientation;
    }

    @Override // android.content.ContextWrapper, android.content.Context, com.mxtech.videoplayer.IPlayer
    public final Display getDisplay() {
        return getWindowManager().getDefaultDisplay();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final View getDecorView() {
        return getWindow().getDecorView();
    }

    @Override // android.view.SurfaceHolder.Callback
    @SuppressLint({"NewApi"})
    public final void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "=== Enter surfaceCreated. holder=" + holder + " _surfaceHolderCreated=" + this._surfaceHolderCreated + "_surfaceView=" + this._surfaceView);
        if (Build.VERSION.SDK_INT >= 11) {
            Log.v(TAG, "HW Accel=" + this.topLayout.isHardwareAccelerated());
        }
        this._surfaceHolderCreated = holder;
        this.handler.sendEmptyMessage(7);
        Log.d(TAG, "=== Leave surfaceCreated.");
    }

    @Override // android.view.SurfaceHolder.Callback
    public final void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "=== Enter surfaceDestroyed. holder=" + holder + " _surfaceHolderCreated=" + this._surfaceHolderCreated + "_surfaceView=" + this._surfaceView);
        this.pp.setDisplay(null, 2);
        this._surfaceHolderCreated = null;
        this._surfaceBoundDecoder = (byte) 0;
        Log.d(TAG, "=== Leave surfaceDestroyed.");
    }

    @Override // android.view.SurfaceHolder.Callback
    public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private int surfaceTypeForDecoder(byte decoder) {
        return decoder == 2 ? 0 : 3;
    }

    @SuppressLint({"NewApi"})
    private void createSurfaceView() {
        if (this._surfaceView == null && this._surfaceHolderCreated == null) {
            this._surfaceView = new SurfaceView(this);
            this._surfaceView.setId(R.id.surfaceView);
            SurfaceHolder holder = this._surfaceView.getHolder();
            holder.addCallback(this);
            holder.setFormat(P.getColorFormat());
            this._surfaceType = surfaceTypeForDecoder(this.pp.getDecoder());
            holder.setType(this._surfaceType);
            RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(-1, -1);
            if ((this._gestures & 6) == 0 && !this._doubleTapZooming) {
                l.addRule(13);
            }
            this.topLayout.addView(this._surfaceView, getTopLayoutZOrder(0), l);
            if (this._subtitleOverlay != null) {
                updateSubtitleOverlayLayout();
            }
        }
    }

    private void removeSurfaceView() {
        if (this._surfaceView != null) {
            this.topLayout.removeView(this._surfaceView);
            this._surfaceView = null;
        }
        if (this._surfaceHolderCreated != null) {
            this._surfaceHolderCreated.removeCallback(this);
            this._surfaceHolderCreated = null;
        }
        this.pp.setDisplay(null, 2);
    }

    private void updateProgressText() {
        if (this.pp.isInPlaybackState()) {
            setProgressText(this.pp.getCurrentPosition(), (this._controller.getVisibleParts() & 1) != 0);
        }
    }

    private void clearCachedProgressTextInfo() {
        this._lastLeftControllerSeconds = Integer.MIN_VALUE;
        this._lastRightControllerSeconds = Integer.MIN_VALUE;
        this._lastLeftElapsedTextSeconds = Integer.MIN_VALUE;
        this._lastRightElapsedTextSeconds = Integer.MIN_VALUE;
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
            String leftText = formatElapsedTime_l(L.sb, leftSeconds);
            String rightText = this._showLeftTime ? formatElapsedTime_l(L.sb, rightSeconds) : this._durationString;
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

    private String formatElapsedTime_l(StringBuilder b, int seconds) {
        if (seconds < 0) {
            DateUtils.formatElapsedTime(L.sb, -seconds);
            L.sb.insert(0, '-');
            return L.sb.toString();
        }
        return DateUtils.formatElapsedTime(L.sb, seconds);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onStartTrackingTouch(SeekBar seekBar) {
        consecutiveSeekBegin();
        this._controller.pin();
        if (this.pp.isSeekable() && !showSeekPreviews()) {
            pause(80);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (this.pp.isInPlaybackState() && fromUser) {
            setProgressText(progress, true);
            if (this._consecutiveSeekBegin >= 0 && this._displaySeekingPosition) {
                setSeekSupremeText(progress, progress - this._consecutiveSeekBegin, null);
            }
            if (this._consecutiveSeekBegin < 0) {
                this.pp.seekTo(progress, this.pp.getShortSeekTimeout());
            } else if (showSeekPreviews()) {
                previewSeekTo(progress);
            }
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onStopTrackingTouch(SeekBar seekBar) {
        this._consecutiveSeekBegin = -1;
        this._controller.unpin();
        if (this.pp.isInPlaybackState()) {
            this.pp.seekTo(seekBar.getProgress(), this.pp.getShortSeekTimeout());
        }
        resume();
        setSupremeText(null, null, false);
    }

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

    @Override // com.mxtech.videoplayer.subtitle.SubtitlePanel.Client
    public final void updateLayout() {
        updateUserLayout(this._controller.getVisibleParts(), 0);
        updateSystemLayout();
    }

    private void updateUserLayout() {
        updateUserLayout(this._controller.getVisibleParts(), 0);
    }

    private void updateUserLayout(int parts, int controllerAnimation) {
        int above;
        int bottomMost;
        if ((parts & 2) != 0) {
            showActionBar(controllerAnimation == 2);
        } else {
            hideActionBar(controllerAnimation == 2);
        }
        updateStatusLayout();
        boolean moveUpSubView = this._subView.hasVisibleTextSubtitles();
        boolean controllerFullyVisible = isControllerFullyVisible();
        int subviewMargin = 0;
        int controllerMargin = 0;
        if (this._subtitlePanel != null) {
            int barHeight = this._subtitlePanel.getBarHeight();
            controllerMargin = 0 + barHeight;
            if (moveUpSubView) {
                subviewMargin = 0 + barHeight;
            }
        }
        if (moveUpSubView) {
            if (controllerFullyVisible) {
                subviewMargin += this._controller.getDefaultHeight();
            }
            subviewMargin -= this._subView.getPaddingBottom();
            if (subviewMargin < 0) {
                subviewMargin = 0;
            }
        }
        ViewGroup.MarginLayoutParams l = (ViewGroup.MarginLayoutParams) this._controller.getLayoutParams();
        if (l.bottomMargin != controllerMargin) {
            l.bottomMargin = controllerMargin;
            this._controller.requestLayout();
        }
        ViewGroup.MarginLayoutParams l2 = (ViewGroup.MarginLayoutParams) this._subView.getLayoutParams();
        if (l2.bottomMargin != subviewMargin) {
            l2.bottomMargin = subviewMargin;
            this._subView.requestLayout();
        }
        int numRight = 0;
        boolean right = true;
        if (controllerFullyVisible) {
            above = R.id.controller;
            bottomMost = above;
        } else if (this._subtitlePanel != null) {
            above = R.id.bar;
            bottomMost = above;
        } else {
            above = 0;
            bottomMost = 0;
        }
        Iterator<IFloatingBar> it = this._floatingBars.iterator();
        while (it.hasNext()) {
            IFloatingBar bar = it.next();
            ViewGroup layout = bar.getLayout();
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            int[] rules = layoutParam.getRules();
            if (above != 0) {
                rules[2] = above;
                rules[12] = 0;
            } else {
                rules[2] = 0;
                rules[12] = -1;
            }
            if (right) {
                rules[11] = -1;
                numRight++;
                if (numRight == 2) {
                    right = false;
                    above = bottomMost;
                } else {
                    above = layout.getId();
                }
            } else {
                rules[11] = 0;
                above = layout.getId();
            }
            layout.requestLayout();
        }
        updateScreenRotationButtonVisibility(controllerAnimation);
        updatePauseIndicatorVisibility(controllerAnimation);
    }

    private void updateSystemLayout() {
        updateSystemLayout(false);
    }

    @SuppressLint({"NewApi"})
    private void updateSystemLayout(boolean delaySystemUiHide) {
        boolean visible;
        int visibility = -1;
        this.handler.removeMessages(6);
        if (this._lock == null) {
            visible = isControllerFullyVisible() || needDefaultControllerHeight();
        } else {
            visible = (this._controller.getVisibleParts() & 1) == 1;
        }
        if (!needToHideToolbarAfterControllerHidden() || visible || getToolbarTransformation() == 0) {
            if (P.oldTablet) {
                visibility = (this._alwaysShowStatusBar || visible) ? 0 : 1;
            } else if (P.uiVersion < 16) {
                if (this._fullscreen == 2) {
                    setWindowFullScreen(visible ? false : true);
                }
                if (P.uiVersion >= 11) {
                    if (P.hasHandsetSoftMenuKey && P.softButtonsVisibility == 2) {
                        if (visible) {
                            visibility = 0;
                        } else if (delaySystemUiHide) {
                            this.handler.sendEmptyMessageDelayed(6, P.getInterfaceAutoHideDelay(this));
                            visibility = 0;
                        } else {
                            visibility = 2;
                        }
                    } else {
                        visibility = visible ? 0 : 1;
                    }
                }
            } else if (this._lock == null) {
                int visibility2 = 0;
                if (needToKeepStableLayoutWhileHidingUi() && (isControllerFullyVisible() || (wasControllerFullyVisible() && this._controller.isHiding()))) {
                    visibility2 = 0 | 256;
                }
                if (P.softButtonsVisibility == 1) {
                    visibility = visibility2 | 4610;
                    if (!visible) {
                        visibility |= 1;
                    }
                    delaySystemUiHide = false;
                } else if (P.softButtonsVisibility == 0) {
                    if (visible) {
                        visibility = visibility2 | 256;
                    } else {
                        visibility = visibility2 | 1;
                        delaySystemUiHide = false;
                    }
                } else {
                    visibility = visibility2 | 512;
                    if (!visible) {
                        if (P.uiVersion >= 19) {
                            visibility |= 2051;
                            delaySystemUiHide = false;
                        } else if (delaySystemUiHide) {
                            this.handler.sendEmptyMessageDelayed(6, P.getInterfaceAutoHideDelay(this));
                        } else {
                            visibility |= 3;
                        }
                    }
                }
                if (this._fullscreen != 1) {
                    visibility |= 1024;
                    if (!visible && !delaySystemUiHide) {
                        visibility |= 4;
                    }
                }
            }
            if (visibility != -1) {
                this.topLayout.setSystemUiVisibility(visibility);
            }
            if (this._buttonBacklightOff) {
                DeviceUtils.showButtonBacklight(getWindow(), visible);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x006b, code lost:
        if ((r3 == r2) != (r1 == r0)) goto L38;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onTopLayoutSizeChanged(View v) {
        if (!isFinishing()) {
            int topWidth = this.topLayout.getWidth();
            int topHeight = this.topLayout.getHeight();
            if (this._systemWindowFittable != null && this._lock != null) {
                if (topWidth > this._lockedTopWidth || topHeight > this._lockedTopHeight) {
                    this._lockedTopWidth = topWidth;
                    this._lockedTopHeight = topHeight;
                } else {
                    return;
                }
            }
            if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
                this._needToUpdateSizes = true;
            } else if (this._surfaceView != null && ((RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams()).getRules()[13] == 0) {
                int surfaceWidth = this._surfaceView.getWidth();
                int surfaceHeight = this._surfaceView.getHeight();
                if (surfaceWidth > 4 && surfaceHeight > 4) {
                    if ((topWidth > topHeight) == (surfaceWidth > surfaceHeight)) {
                    }
                    this._panX -= 4;
                    updateSizes();
                    this._panX += 4;
                    this.handler.removeMessages(12);
                    this.handler.sendEmptyMessage(12);
                    return;
                }
                updateSizes();
            } else {
                updateSizes();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUILayoutSizeChanged(View v) {
    }

    /* loaded from: classes.dex */
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

    /* loaded from: classes.dex */
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

    private void delaySoftButtonsHide() {
        if (this.handler.hasMessages(6)) {
            this.handler.removeMessages(6);
            this.handler.sendEmptyMessageDelayed(6, P.getInterfaceAutoHideDelay(this));
        }
    }

    private void updateOSDPlacement() {
        RelativeLayout.LayoutParams l;
        boolean requestLayout = false;
        int alignParentBottom = this._osdBottom ? -1 : 0;
        if (this._elapsedTimeText != null) {
            RelativeLayout.LayoutParams l2 = (RelativeLayout.LayoutParams) this._elapsedTimeText.getLayoutParams();
            int[] rules = l2.getRules();
            if (rules[12] != alignParentBottom) {
                rules[12] = alignParentBottom;
                requestLayout = true;
            }
            if (this._osdBackground) {
                if (this._elapsedTimeText.getBackground() == null) {
                    this._elapsedTimeText.setBackgroundColor(this._osdBackColor);
                }
            } else if (this._elapsedTimeText.getBackground() != null) {
                this._elapsedTimeText.setBackgroundDrawable(null);
            }
        }
        if (this._statusLayout != null) {
            ViewGroup.LayoutParams statusL = this._statusLayout.getLayoutParams();
            if (statusL instanceof RelativeLayout.LayoutParams) {
                l = (RelativeLayout.LayoutParams) statusL;
            } else {
                l = this._statusOutLayout;
            }
            if (l != null) {
                int[] rules2 = l.getRules();
                if (rules2[12] != alignParentBottom) {
                    rules2[12] = alignParentBottom;
                    requestLayout = true;
                }
            }
            if (this._osdBackground && l == statusL) {
                if (this._statusLayout.getBackground() == null) {
                    this._statusLayout.setBackgroundColor(this._osdBackColor);
                }
            } else if (this._statusLayout.getBackground() != null) {
                this._statusLayout.setBackgroundDrawable(null);
            }
            if (l == statusL) {
                setStatusTextColor(this._osdTextColor);
            } else {
                setStatusTextColor(P.DEFAULT_OSD_TEXT_COLOR);
            }
        }
        if (this._auxNotification != null) {
            updateAuxNotificationLayout(this._auxNotification, (RelativeLayout.LayoutParams) this._auxNotification.getLayoutParams());
            if (this._osdBackground) {
                if (this._auxNotification.getBackground() == null) {
                    this._auxNotification.setBackgroundColor(this._osdBackColor);
                }
            } else if (this._auxNotification.getBackground() != null) {
                this._auxNotification.setBackgroundDrawable(null);
            }
        }
        if (requestLayout) {
            this.topLayout.requestLayout();
        }
    }

    final int getTopLayoutZOrder(int type) {
        int order = 0;
        if (type == 0) {
            return 0;
        }
        if (this._surfaceView != null && this.topLayout.indexOfChild(this._surfaceView) >= 0) {
            order = 0 + 1;
        }
        if (type != 1) {
            if (this._coverView != null && this.topLayout.indexOfChild(this._coverView) >= 0) {
                order++;
            }
            if (type != 2) {
                if (this._subView.getParent() == this.topLayout && this.topLayout.indexOfChild(this._subView) >= 0) {
                    order++;
                }
                if (type != 3 && this._subtitleOverlay != null && this.topLayout.indexOfChild(this._subtitleOverlay) >= 0) {
                    return order + 1;
                }
                return order;
            }
            return order;
        }
        return order;
    }

    private void updateStatusLayout() {
        boolean visible = isControllerFullyVisible();
        if (P.elapsedTimeShowAlways) {
            if (this._elapsedTimeText == null) {
                this._elapsedTimeText = (TextView) this.layoutInflater.inflate(R.layout.elapsed_time, (ViewGroup) this.topLayout, false);
                this._elapsedTimeText.setTextColor(this._osdTextColor);
                setProgressText(currentPosition(), visible);
                this.topLayout.addView(this._elapsedTimeText, getTopLayoutZOrder(4));
            }
            this._elapsedTimeText.setVisibility(visible ? 8 : 0);
        } else if (this._elapsedTimeText != null) {
            this.topLayout.removeView(this._elapsedTimeText);
            this._elapsedTimeText = null;
            this._lastLeftElapsedTextSeconds = Integer.MIN_VALUE;
            this._lastRightElapsedTextSeconds = Integer.MIN_VALUE;
        }
        if (this._alwaysShowStatusText) {
            if (this._statusLayout == null) {
                this._statusLayout = this.layoutInflater.inflate(R.layout.status_layout, (ViewGroup) this.topLayout, false);
                loadStatusViews();
                updateBatteryClock(visible);
                this.topLayout.addView(this._statusLayout, getTopLayoutZOrder(4));
            }
            this._statusLayout.setVisibility(visible ? 8 : 0);
        } else if (this._statusLayout != null) {
            this.topLayout.removeView(this._statusLayout);
            this._statusLayout = null;
            this._statusText = null;
            this._batteryCharging = null;
        }
        updateOSDPlacement();
    }

    private void loadStatusViews() {
        this._statusText = (TextView) this._statusLayout.findViewById(R.id.statusText);
        this._batteryCharging = (ImageView) this._statusLayout.findViewById(R.id.batteryCharging);
        if (this._statusText != null) {
            this._statusText.setTextColor(this._osdTextColor);
        }
        if (this._batteryCharging != null) {
            this._batteryCharging.setColorFilter(this._osdTextColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private boolean needToHideToolbarAfterControllerHidden() {
        return Build.VERSION.SDK_INT >= 21 && (this._fullscreen == 2 || P.softButtonsVisibility == 2);
    }

    private boolean needToKeepStableLayoutWhileHidingUi() {
        return Build.VERSION.SDK_INT >= 16 && (this._fullscreen == 2 || P.softButtonsVisibility == 2);
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public void onControllerVisibilityChanged(PlaybackController controller, int visibleParts, int animation, boolean autoHide) {
        if (!isControllerSeekingVisible()) {
            updateUserLayout(visibleParts, animation);
            if (needToHideToolbarAfterControllerHidden()) {
                if (!autoHide) {
                    this._delayNextSystemUiHide21 = true;
                }
                updateSystemLayout(false);
            } else {
                updateSystemLayout(autoHide ? false : true);
            }
        }
        if ((visibleParts & 1) != 0 && this.pp.isInPlaybackState()) {
            setVideoProgress(this.pp.getCurrentPosition());
        }
        if ((visibleParts & 2) != 0 && this._batteryClockInTitleBar) {
            updateBatteryClock(true);
        }
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public final void onControllerHidingCompleted(PlaybackController controller) {
        if (needToKeepStableLayoutWhileHidingUi() && !needToHideToolbarAfterControllerHidden() && !this.handler.hasMessages(6)) {
            updateSystemLayout();
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity
    protected void onToolbarTransformationChanged(int from, int to) {
        if (needToHideToolbarAfterControllerHidden() && from == 2 && to == 0) {
            updateSystemLayout(this._delayNextSystemUiHide21);
            this._delayNextSystemUiHide21 = false;
        }
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public void onDefaultHeightChanged(PlaybackController controller, int height) {
        updateUserLayout(controller.getVisibleParts(), 0);
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public final boolean canShow(PlaybackController controller, int parts) {
        return this._lock == null || (this._lockShowInterfaceOnTouched && parts == 3);
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public final boolean canHide(PlaybackController controller) {
        return this.dialogRegistry.size() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ErrorMessageHandler implements DialogInterface.OnDismissListener {
        private ErrorMessageHandler() {
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            ActivityScreen.this.dialogRegistry.unregister(dialog);
            ActivityScreen.this.skipCurrent();
        }
    }

    private void onError() {
        if (!isFinishing() && this.pp.getUri() != null) {
            if (this._surfaceHolderCreated != null) {
                this._surfaceHolderCreated.setKeepScreenOn(false);
            }
            if (getIntent().getBooleanExtra("suppress_error_message", false) || App.prefs.getBoolean("suppress_error_message", false)) {
                skipCurrent();
                return;
            }
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            ErrorMessageHandler handler = new ErrorMessageHandler();
            b.setMessage(this.pp.isFile() ? R.string.error_text_video_play_unknown_file : R.string.error_text_video_play_unknown_link);
            b.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            Log.w(TAG, b.toString());
            showDialog((ActivityScreen) b.create(), (DialogInterface.OnDismissListener) handler);
        }
    }

    private void onIdle() {
        this.dialogRegistry.dismissAll();
        this._seekBar.setMax(0);
        this._subView.resetUpdateLock();
        setTitle("");
        hideSubtitlePanel();
        hideAllFloatingBars();
        this._zoomWidth = 0;
        this._zoomHeight = 0;
        this._panX = 0;
        this._panY = 0;
        this._hasVideoTrack = false;
        this._durationInSeconds = 0;
        this._durationString = UNKNOWN_DURATION;
        this._durationText.setText(this._durationString);
        removeCover();
        setProgressText(0, true);
    }

    private void onSuspended(int oldState) {
        this._seekBar.setSecondaryProgress(0);
    }

    private boolean needDefaultControllerHeight() {
        if (this._controller.getDefaultHeight() > 0) {
            return false;
        }
        if (P.playbackTouchAction == 3 || P.playbackTouchAction == 1) {
            return true;
        }
        return P.showInterfaceAtTheStartup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showSeekPreviews() {
        int connectivity;
        if (P.seekPreviews) {
            FFPlayer ff = this.pp.getFFPlayer();
            if (ff != null) {
                connectivity = ff.getConnectivity();
                if (connectivity == 1) {
                    return false;
                }
            } else {
                connectivity = -1;
            }
            if (!this.pp.isLocalMedia() && P.seekPreviewsNetwork != 0) {
                return P.seekPreviewsNetwork == 2 && connectivity == 0;
            }
            return true;
        }
        return false;
    }

    private void onPreparing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_DAR_HORZ) && extras.containsKey(EXTRA_DAR_VERT)) {
            this.pp.setAspectRatio(extras.getFloat(EXTRA_DAR_HORZ), extras.getFloat(EXTRA_DAR_VERT), false);
        }
        setFullscreen(this._fullscreen);
        if (this._surfaceHolderCreated != null) {
            this._surfaceHolderCreated.setKeepScreenOn(true);
        }
        MediaDatabase.State restore = this.pp.getPersistent();
        if (restore != null) {
            this._zoomWidth = restore.zoomWidth;
            this._zoomHeight = restore.zoomHeight;
            this._panX = restore.panX;
            this._panY = restore.panY;
        }
        if (needDefaultControllerHeight()) {
            showController();
        }
        updateDecoderIndicator();
        if ((this._loadingFlags & 1) == 0 && App.prefs.getBoolean(Key.LOADING_CIRCLE_ANIMATION, true)) {
            this._showingLoadingSplash = true;
            this._loadingSplash.show();
        }
        updateTitle();
    }

    @Nullable
    private Uri[] getGivenVisibleSubtitleURIs() {
        return getExtraURIs(EXTRA_SUBTITLES_ENABLE);
    }

    private void initialLoadSubs() {
        if (!this.pp.isDefaultSubLoaded()) {
            this.pp.setDefaultSubLoaded();
            boolean requireCover = (this._hasVideoTrack || this.pp.hasCover()) ? false : true;
            Intent intent = getIntent();
            Uri[] URIs = AppUtils.getExtraURIs(intent, EXTRA_SUBTITLES);
            if (URIs != null) {
                String[] names = intent.getStringArrayExtra(EXTRA_SUBTITLES_NAME);
                String[] filenames = intent.getStringArrayExtra(EXTRA_SUBTITLES_FILENAME);
                if (names != null && names.length != URIs.length) {
                    Log.e(TAG, "`subs.name`(" + names.length + ") has different size with `" + EXTRA_SUBTITLES + "`(" + URIs.length + ").");
                    names = null;
                }
                if (filenames != null && filenames.length != URIs.length) {
                    Log.e(TAG, "`subs.filename`(" + filenames.length + ") has different size with `" + EXTRA_SUBTITLES + "`(" + URIs.length + ").");
                    filenames = null;
                }
                this.pp.loadSubs(URIs, names, filenames, this._lister, requireCover);
                return;
            }
            this.pp.loadDefaultSubs(this._lister, requireCover);
        }
    }

    private void onPrepared() {
        String name;
        this._hasVideoTrack = this.pp.mp().hasVideoTrack();
        this._autoHideInterface = this._hasVideoTrack && P.interfaceAutoHide;
        this._controller.enableAutoHide(this._autoHideInterface);
        if (!this._autoHideInterface) {
            showController();
        }
        doUpdateDirectRendering();
        if (!this._lister.isRemoteListing()) {
            initialLoadSubs();
        }
        this._subView.clear();
        MediaDatabase.State persistent = this.pp.getPersistent();
        if (persistent != null) {
            this._subView.setTranslation(persistent.subtitleOffset, persistent.subtitleSpeed);
            setRepeatPoints(persistent.repeatA, persistent.repeatB);
        } else {
            this._subView.setSync((int) (App.prefs.getFloat(Key.SUBTITLE_DEFAULT_SYNC, 0.0f) * 1000.0f));
            setRepeatPoints(-1, -1);
        }
        updateStereoMode();
        List<ISubtitle> subs = this.pp.getSubtitles();
        if (subs.size() > 0) {
            addSubtitles(subs, persistent, getGivenVisibleSubtitleURIs(), P.autoSelectSubtitle ? 3 : 0);
        }
        if (App.prefs.contains(Key.ASPECT_RATIO_H)) {
            this.pp.setAspectRatio(App.prefs.getFloat(Key.ASPECT_RATIO_H, 0.0f), App.prefs.getFloat(Key.ASPECT_RATIO_V, 0.0f), false);
        }
        Intent intent = getIntent();
        int removeAt = intent.getIntExtra(EXTRA_POSITION, 0);
        if (removeAt > 0) {
            this.pp.seekTo(removeAt, this._subView.containsEnabledSubtitle() ? 6000 : this.pp.getShortSeekTimeout());
        }
        this._showingLoadingSplash = false;
        this._loadingSplash.hide();
        if (this._surfaceHolderCreated != null && this.pp.getTargetState() != 4) {
            this._surfaceHolderCreated.setKeepScreenOn(false);
        }
        this._lister.loadRemoteAdjascentUri(this.pp.getUri());
        supportInvalidateOptionsMenu();
        if (!this._hasVideoTrack) {
            if (!this.pp.isRemoteLoading()) {
                createCoverView();
            }
            if (P.screenOrientation == 99999 && !this._wasSetOrientationFromThisVideo) {
                setOrientation(P.screenOrientation);
            }
        }
        FFPlayer ff = this.pp.getFFPlayer();
        if (ff != null) {
            String formatName = ff.getFormat(0);
            if (formatName != null) {
                this._tracker.send(new HitBuilders.EventBuilder(L.isUsingCustomCodec() ? "Format_custom" : "Format", formatName).build());
            }
            int videoIndex = ff.getVideoStreamIndex();
            if (videoIndex >= 0 && (name = getCodecProfileCombined(ff, videoIndex)) != null) {
                this._tracker.send(new HitBuilders.EventBuilder(L.isUsingCustomCodec() ? "VCodec_custom" : "VCodec", name).build());
            }
        }
    }

    @Nullable
    private static String getCodecProfileCombined(FFPlayer ff, int streamIndex) {
        String profileName;
        String codecName = ff.getStreamCodec(streamIndex, 0);
        if (codecName != null && (profileName = ff.getStreamProfile(streamIndex)) != null) {
            return codecName + "@" + profileName;
        }
        return codecName;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onDurationKnown(int duration) {
        this._durationString = DateUtils.formatElapsedTime(L.sb, duration / 1000);
        this._durationInSeconds = duration / 1000;
        this._seekBar.setMax(duration);
        if (this._showLeftTime) {
            updateProgressText();
        } else {
            this._durationText.setText(this._durationString);
        }
    }

    @Override // com.mxtech.videoplayer.PlayLister.Listener
    public final void onNetworkListingCompleted(PlayLister lister) {
        if (this.pp.isInPlaybackState()) {
            initialLoadSubs();
            List<ISubtitle> subs = this.pp.getSubtitles();
            if (subs.size() > 0) {
                addSubtitles(subs, this.pp.getPersistent(), getGivenVisibleSubtitleURIs(), P.autoSelectSubtitle ? 1 : 0);
            }
            this._lister.loadRemoteAdjascentUri(this.pp.getUri());
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onRemoteResourceLoadingBegin(int loadings) {
        if ((loadings & 236) != 0) {
            this.handler.sendEmptyMessageDelayed(13, 1000L);
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onRemoteResourceLoadingCanceled() {
        this.handler.removeMessages(13);
        setAuxNotification(null);
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onRemoteResourceLoaded(List<ISubtitle> subs, Bitmap cover, Uri coverUri) {
        this.handler.removeMessages(13);
        setAuxNotification(null);
        if (this.pp.isInPlaybackState()) {
            if (!this._hasVideoTrack) {
                createCoverView();
            }
            if (subs != null) {
                addSubtitles(subs, this.pp.getPersistent(), getGivenVisibleSubtitleURIs(), P.autoSelectSubtitle ? 1 : 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AuxNotificationView extends TextView implements Animation.AnimationListener {
        private Animation _animFadeOut;
        private boolean _hiding;

        AuxNotificationView(Context context) {
            super(context);
        }

        void setNotifyText(CharSequence message) {
            this._hiding = false;
            super.setText(message);
        }

        void clearNotifyText() {
            if (!this._hiding) {
                if (this._animFadeOut == null) {
                    this._animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fast_fade_out);
                    this._animFadeOut.setAnimationListener(this);
                }
                this._hiding = true;
                startAnimation(this._animFadeOut);
            }
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (this._hiding) {
                ActivityScreen.this.uiLayout.removeView(ActivityScreen.this._auxNotification);
                ActivityScreen.this._auxNotification = null;
            }
        }
    }

    private void setAuxNotification(CharSequence message) {
        boolean show = message != null && message.length() > 0;
        if (show) {
            if (this._auxNotification == null) {
                this._auxNotification = new AuxNotificationView(this);
                this._auxNotification.setId(R.id.aux_notifier);
                this._auxNotification.setIncludeFontPadding(false);
                this._auxNotification.setTextColor(this._osdTextColor);
                if (this._osdBackground) {
                    this._auxNotification.setBackgroundColor(this._osdBackColor);
                }
                RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(-2, -2);
                updateAuxNotificationLayout(this._auxNotification, l);
                this.uiLayout.addView(this._auxNotification, l);
            }
            this._auxNotification.setNotifyText(message);
        } else if (this._auxNotification != null) {
            this._auxNotification.clearNotifyText();
        }
    }

    private void updateAuxNotificationLayout(TextView v, RelativeLayout.LayoutParams l) {
        boolean placeBottomLeft = (P.elapsedTimeShowAlways && this._osdBottom) ? false : true;
        int ruleCenterHorizontal = placeBottomLeft ? 0 : -1;
        int[] rules = l.getRules();
        boolean changed = false;
        if (rules[12] != -1) {
            rules[12] = -1;
            changed = true;
        }
        if (rules[14] != ruleCenterHorizontal) {
            rules[14] = ruleCenterHorizontal;
            changed = true;
        }
        if (changed) {
            v.requestLayout();
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public final void onSetupFontCache(boolean on) {
        if (on) {
            if (!this.dialogRegistry.containsInstanceOf(FontCacheProgressDialog.class)) {
                pause(80);
                this.handler.sendEmptyMessageDelayed(9, 2000L);
                return;
            }
            return;
        }
        this.handler.removeMessages(9);
        DialogInterface progress = this.dialogRegistry.findByType(FontCacheProgressDialog.class);
        if (progress != null) {
            progress.dismiss();
        }
        this._subView.refresh();
        resume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FontCacheProgressDialog extends AppCompatProgressDialog {
        FontCacheProgressDialog() {
            super(ActivityScreen.this);
            setCancelable(false);
            setProgressStyle(0);
            setMessage(ActivityScreen.this.getString(R.string.wait_for_building_fontcache));
        }
    }

    private boolean isAudioTrackSelectable() {
        return this.pp.isInPlaybackState() && this.pp.countAudioTracks() > 0;
    }

    private void onPaused() {
        if ((this._lastPauseFlags & 128) == 0) {
            this._forceIntrinsicOffset = true;
            update(this.pp.getCurrentPosition(), true);
            this._forceIntrinsicOffset = false;
        }
        if ((this._lastPauseFlags & 64) == 0) {
            if (this._lock != null) {
                if (!this._lock.isKidsLock()) {
                    this._lock.unlock();
                } else {
                    return;
                }
            }
            showController();
        }
    }

    private void onPlaybackCompleted() {
        this.pp.save();
        if (L.sleepTimer != null && L.sleepTimer.finishLastMedia && L.sleepTimer.fired) {
            L.sleepTimer.clear();
            returnOk(kEndByPlaybackCompletion);
            finish();
            return;
        }
        this.handler.sendEmptyMessage(8);
    }

    @Override // com.mxtech.videoplayer.IMediaRuntimeInfo
    public final Uri getExternalCoverArt() {
        return this.pp.getDefaultExternalCoverUri();
    }

    @SuppressLint({"NewApi"})
    private void createCoverView() {
        try {
            if (this.pp.loadLocalCovers() != null) {
                this._lastCover = null;
                if (this._coverView == null) {
                    this._coverView = new ImageView(this);
                    RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(-1, -1);
                    this.topLayout.addView(this._coverView, getTopLayoutZOrder(1), l);
                    this._coverView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                updateCoverView();
            }
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "", e);
        }
    }

    private void updateCoverView() {
        Bitmap cover = this.pp.getCover(this.orientation);
        if (this._lastCover != cover) {
            this._coverView.setImageBitmap(cover);
            this._lastCover = cover;
        }
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onCoverArtChanged() {
        if (!this._hasVideoTrack && !this.pp.isRemoteLoading()) {
            createCoverView();
        }
    }

    private void removeCover() {
        if (this._coverView != null) {
            this.topLayout.removeView(this._coverView);
            this._coverView = null;
        }
        this._lastCover = null;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onStateChanged(int oldState, int newState) {
        switch (newState) {
            case -1:
                removeCover();
                onError();
                break;
            case 0:
                onIdle();
                break;
            case 1:
                removeCover();
                onSuspended(oldState);
                break;
            case 2:
                onPreparing();
                break;
            case 3:
                onPrepared();
                break;
            case 5:
                onPaused();
                break;
            case 6:
                if (this._consecutiveSeekBegin < 0) {
                    onPlaybackCompleted();
                    break;
                } else {
                    return;
                }
        }
        updateStickyState(newState == 3);
        this._controller.update(newState);
        updateLayout();
        this._lastState = newState;
    }

    @Override // com.mxtech.videoplayer.Player.Client
    public void onTargetStateChanged(int oldState, int newState) {
        if (this._playPauseButton != null) {
            if (newState == 4) {
                this._playPauseButton.getDrawable().setLevel(2);
            } else {
                this._playPauseButton.getDrawable().setLevel(1);
            }
        }
        if (this._stickyAutoReset && newState != 4) {
            this._stickyRequested = false;
        }
        updatePlayPauseIndicator();
    }

    @SuppressLint({"InlinedApi"})
    private void replayState() {
        this._loadingFlags = 1;
        setVideoProgress(0);
        onTargetStateChanged(this.pp.getTargetState(), this.pp.getTargetState());
        switch (this.pp.getState()) {
            case 1:
                onStateChanged(this._lastState, 1);
                break;
            case 2:
                onStateChanged(this._lastState, 1);
                onStateChanged(1, 2);
                break;
            case 3:
                onStateChanged(this._lastState, 1);
                onStateChanged(1, 2);
                onStateChanged(2, 3);
                break;
            case 4:
                onStateChanged(this._lastState, 1);
                onStateChanged(1, 2);
                onStateChanged(2, 3);
                onStateChanged(3, 4);
                break;
            case 5:
                onStateChanged(this._lastState, 1);
                onStateChanged(1, 2);
                onStateChanged(2, 3);
                onStateChanged(3, 5);
                break;
            case 6:
                onStateChanged(this._lastState, 6);
                break;
        }
        onDurationKnown(this.pp.getDuration());
        this._loadingFlags = 0;
    }

    private void updatePlayPauseIndicator() {
        int level;
        if (canSwitchSticky()) {
            level = 1;
        } else if (this.pp.getTargetState() == 5 && P.playbackTouchAction == 1) {
            level = 2;
        } else {
            level = 0;
        }
        setPauseIndicatorImageLevel(level);
    }

    private void setPauseIndicatorImageLevel(int level) {
        if (this._pauseIndicator.getDrawable().getLevel() != level) {
            this._pauseIndicator.setImageLevel(level);
            updatePauseIndicatorVisibility();
        }
    }

    private void updatePauseIndicatorVisibility() {
        updatePauseIndicatorVisibility(0);
    }

    private void updatePauseIndicatorVisibility(int controllerAnimation) {
        switch (this._pauseIndicator.getDrawable().getLevel()) {
            case 0:
                this._pauseIndicator.setVisibility(8);
                return;
            case 1:
                if (isControllerFullyVisible() || !this._hasVideoTrack) {
                    if (controllerAnimation == 2 && this._pauseIndicator.getVisibility() != 0) {
                        if (this._indicatorFadeIn == null) {
                            this._indicatorFadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in);
                        }
                        this._pauseIndicator.startAnimation(this._indicatorFadeIn);
                    }
                    this._pauseIndicator.setVisibility(0);
                    return;
                } else if (this._pauseIndicator.getVisibility() == 0) {
                    if (controllerAnimation == 2 && this._pauseIndicator.getVisibility() == 0) {
                        if (this._indicatorFadeOut == null) {
                            this._indicatorFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
                        }
                        this._pauseIndicator.startAnimation(this._indicatorFadeOut);
                    }
                    this._pauseIndicator.setVisibility(8);
                    return;
                } else {
                    return;
                }
            case 2:
                this._pauseIndicator.setVisibility(0);
                return;
            default:
                return;
        }
    }

    private boolean isSticky() {
        if (this._stickyRequested || this._defaultSticky) {
            return true;
        }
        if (!this._hasVideoTrack) {
            return this._defaultStickyAudio;
        }
        return false;
    }

    private boolean canSwitchSticky() {
        return isSticky() && PlayService.instance != null && !PlayService.instance.hasPlayer() && this.pp.getTargetState() == 4 && this.pp.isInPlaybackState();
    }

    private void updateStickyState(boolean forceUpdate) {
        updatePlayPauseIndicator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LockButtonHandler implements View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnDismissListener {
        private static final int LEVEL_LOCK = 0;
        private static final int LEVEL_LOCK_PLUS = 1;
        private static final int LEVEL_ROTATION_LOCKED = 2;
        private static final int LEVEL_ROTATION_UNLOCKED = 3;
        private int _targets;

        LockButtonHandler() {
            ActivityScreen.this._lockButton.setOnClickListener(this);
            ActivityScreen.this._lockButton.setOnLongClickListener(this);
            this._targets = App.prefs.getInt(Key.LOCK_TARGET, 1);
            updateView();
        }

        int getLockTargets() {
            return this._targets;
        }

        void updateView() {
            int level;
            switch (this._targets) {
                case 2:
                    if (ActivityScreen.this._rotationLocked) {
                        level = 2;
                        break;
                    } else {
                        level = 3;
                        break;
                    }
                case 3:
                    level = 1;
                    break;
                default:
                    level = 0;
                    break;
            }
            ActivityScreen.this._lockButton.getDrawable().setLevel(level);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if ((this._targets & 2) != 0) {
                ActivityScreen.this.lockRotation(!ActivityScreen.this._rotationLocked);
            }
            if ((this._targets & 1) != 0) {
                if (ActivityScreen.this.lockControl((this._targets & 2) != 0)) {
                    ActivityScreen.this._ignoreNextFocusLoss = true;
                }
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            return invokeLockBox();
        }

        public boolean invokeLockBox() {
            if (ActivityScreen.this.isFinishing()) {
                return false;
            }
            ActivityScreen activityScreen = ActivityScreen.this;
            AlertDialog.Builder title = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.lock_target);
            int i = R.array.lock_targets;
            boolean[] zArr = new boolean[2];
            zArr[0] = (this._targets & 1) != 0;
            zArr[1] = (this._targets & 2) != 0;
            activityScreen.showDialog((ActivityScreen) title.setMultiChoiceItems(i, zArr, this).setPositiveButton(17039370, this).setNeutralButton(R.string.close, this).create(), (DialogInterface.OnDismissListener) this);
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                if ((this._targets & 2) != 0) {
                    ActivityScreen.this.lockRotation(true);
                }
                if ((this._targets & 1) != 0) {
                    if (ActivityScreen.this.lockControl((this._targets & 2) != 0)) {
                        ActivityScreen.this.resume();
                    }
                }
            }
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            int target = which == 0 ? 1 : 2;
            if (isChecked) {
                this._targets |= target;
            } else {
                this._targets &= target ^ (-1);
            }
            updateView();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (App.prefs.getInt(Key.LOCK_TARGET, 1) != this._targets) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putInt(Key.LOCK_TARGET, this._targets);
                AppUtils.apply(editor);
            }
            ActivityScreen.this.dialogRegistry.unregister(dialog);
        }
    }

    @Override // com.mxtech.videoplayer.Lock.Listener
    @SuppressLint({"NewApi"})
    public final void setSystemUiVisibility(int visibility) {
        this.topLayout.setSystemUiVisibility(visibility);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean lockControl(boolean plus) {
        int topMargin;
        if (isFinishing() || this._lock != null) {
            return false;
        }
        this._lockShowInterfaceOnTouched = App.prefs.getBoolean(Key.LOCK_SHOW_INTERFACE, false);
        if (Build.VERSION.SDK_INT >= 11 && this._systemWindowFittable != null && this._fullscreen == 2 && !this._lockShowInterfaceOnTouched) {
            setWindowFullScreen(true);
        }
        this._lock = new Lock();
        this._lockedTopWidth = this.topLayout.getWidth();
        this._lockedTopHeight = this.topLayout.getHeight();
        this._lock.setListener(this);
        removePopupLikes();
        if (this._lockShowInterfaceOnTouched) {
            topMargin = getSupportActionBar().getHeight();
        } else {
            topMargin = 0;
        }
        this._lock.lock(this, plus, topMargin, this._buttonBacklightOff, this._lockShowInterfaceOnTouched);
        this._controller.hide(2);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void lockRotation(boolean lock) {
        if (lock) {
            lockRotation(true, DeviceUtils.getScreenOrientation(this, getWindowManager().getDefaultDisplay()));
        } else {
            lockRotation(false, 0);
        }
    }

    private void lockRotation(boolean lock, int lockedOrientation) {
        if (lock) {
            if (this._lastRequestedOrientation == Integer.MIN_VALUE) {
                Log.w(TAG, "Can't lock rotation since last-requested-orientation is null.");
                return;
            }
            this._lockRecoverOrientation = this._lastRequestedOrientation;
            this._rotationLocked = true;
            setOrientation_l(lockedOrientation);
        } else {
            this._rotationLocked = false;
            setOrientation_l(this._lockRecoverOrientation);
        }
        if (this._lockButtonHandler != null) {
            this._lockButtonHandler.updateView();
        }
    }

    private void setOrientation(int screenOrientation) {
        if (!this._rotationLocked) {
            setOrientation_l(screenOrientation);
        }
    }

    private void setOrientation_l(int req) {
        if (req == 99999) {
            if (this.pp.isInActiveState()) {
                int width = this.pp.getDisplayWidth();
                int height = this.pp.getDisplayHeight();
                if (width == 0 || height == 0 || width == height) {
                    req = 2;
                } else if (width > height) {
                    if (Build.VERSION.SDK_INT >= 9) {
                        req = 6;
                    } else {
                        req = 0;
                    }
                } else if (Build.VERSION.SDK_INT >= 9) {
                    req = 7;
                } else {
                    req = 1;
                }
                this._wasSetOrientationFromThisVideo = true;
            } else {
                return;
            }
        }
        try {
            setRequestedOrientation(req);
            this._lastRequestedOrientation = req;
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
        updateScreenRotationButton(req);
    }

    private void updateScreenRotationButton(int screenOrientation) {
        boolean show;
        if (!this._showScreenRotateButton || this._rotationLocked || screenOrientation == 4 || screenOrientation == 10) {
            show = false;
        } else if (P.screenOrientation == 99999) {
            if (this.pp.isInActiveState()) {
                int width = this.pp.getDisplayWidth();
                int height = this.pp.getDisplayHeight();
                show = width > 0 && height > 0 && width != height;
            } else {
                show = false;
            }
        } else {
            show = true;
        }
        if (show) {
            if (!this._canManualRotateScreen) {
                this._canManualRotateScreen = true;
                updateScreenRotationButtonVisibility();
            }
        } else if (this._canManualRotateScreen) {
            this._canManualRotateScreen = false;
            this._screenRotateButton.setVisibility(8);
        }
    }

    private void updateScreenRotationButtonVisibility() {
        updateScreenRotationButtonVisibility(0);
    }

    @SuppressLint({"InlinedApi"})
    private void updateScreenRotationButtonVisibility(int controllerAnimation) {
        if (this._canManualRotateScreen) {
            if (isControllerFullyVisible()) {
                if (controllerAnimation == 2 && this._screenRotateButton.getVisibility() != 0) {
                    if (this._screenRotateButtonFadeIn == null) {
                        this._screenRotateButtonFadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in);
                    }
                    this._screenRotateButton.startAnimation(this._screenRotateButtonFadeIn);
                }
                this._screenRotateButton.setVisibility(0);
            } else if (controllerAnimation == 2 && this._screenRotateButton.getVisibility() == 0) {
                if (this._screenRotateButtonFadeOut == null) {
                    this._screenRotateButtonFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
                    this._screenRotateButtonFadeOut.setAnimationListener(new Animation.AnimationListener() { // from class: com.mxtech.videoplayer.ActivityScreen.5
                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation) {
                            if (!ActivityScreen.this.isControllerFullyVisible()) {
                                ActivityScreen.this._screenRotateButton.setVisibility(8);
                            }
                        }
                    });
                }
                this._screenRotateButton.startAnimation(this._screenRotateButtonFadeOut);
            } else {
                this._screenRotateButton.setVisibility(8);
            }
        }
    }

    /* loaded from: classes.dex */
    private class ScreenRotateButtonHandler implements View.OnClickListener {
        private ScreenRotateButtonHandler() {
        }

        @Override // android.view.View.OnClickListener
        @SuppressLint({"InlinedApi"})
        public void onClick(View view) {
            int req;
            switch (P.screenOrientation) {
                case -1:
                case 2:
                case 3:
                case 5:
                    if (ActivityScreen.this._lastRequestedOrientation == P.screenOrientation) {
                        if (ActivityScreen.this.orientation == 2) {
                            if (Build.VERSION.SDK_INT >= 9) {
                                req = 7;
                                break;
                            } else {
                                req = 1;
                                break;
                            }
                        } else if (Build.VERSION.SDK_INT >= 9) {
                            req = 6;
                            break;
                        } else {
                            req = 0;
                            break;
                        }
                    } else {
                        req = P.screenOrientation;
                        break;
                    }
                case 0:
                    if (ActivityScreen.this.orientation == 2) {
                        req = 1;
                        break;
                    } else {
                        req = 0;
                        break;
                    }
                case 1:
                    if (ActivityScreen.this.orientation == 1) {
                        req = 0;
                        break;
                    } else {
                        req = 1;
                        break;
                    }
                case 6:
                    if (ActivityScreen.this.orientation == 2) {
                        req = 7;
                        break;
                    } else {
                        req = 6;
                        break;
                    }
                case 7:
                    if (ActivityScreen.this.orientation == 1) {
                        req = 6;
                        break;
                    } else {
                        req = 7;
                        break;
                    }
                case 8:
                    if (ActivityScreen.this.orientation == 2) {
                        req = 9;
                        break;
                    } else {
                        req = 8;
                        break;
                    }
                case 9:
                    if (ActivityScreen.this.orientation == 1) {
                        req = 8;
                        break;
                    } else {
                        req = 9;
                        break;
                    }
                case P.SCREEN_ORIENTATION_MATCH_VIDEO /* 99999 */:
                    if (ActivityScreen.this.orientation == 2) {
                        if (Build.VERSION.SDK_INT >= 9) {
                            req = 7;
                            break;
                        } else {
                            req = 1;
                            break;
                        }
                    } else if (Build.VERSION.SDK_INT >= 9) {
                        req = 6;
                        break;
                    } else {
                        req = 0;
                        break;
                    }
                default:
                    return;
            }
            ActivityScreen.this.setRequestedOrientation(req);
            ActivityScreen.this._lastRequestedOrientation = req;
        }
    }

    /* loaded from: classes.dex */
    private class PlayPauseButtonHandler implements View.OnClickListener, View.OnLongClickListener {
        private PlayPauseButtonHandler() {
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (ActivityScreen.this.pp.getTargetState() != 4) {
                if (ActivityScreen.this.canUseStickyMode()) {
                    ActivityScreen.this._stickyRequested = true;
                    ActivityScreen.this.start();
                    return true;
                }
                Toast.makeText(ActivityScreen.this, ActivityScreen.this.getString(R.string.sticky_mode_not_allowed_with_hw_decoder), 0).show();
                return true;
            }
            return false;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ActivityScreen.this.toggle(false);
        }
    }

    /* loaded from: classes.dex */
    private class PrevButtonHandler implements View.OnClickListener {
        private PrevButtonHandler() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ActivityScreen.this.previous();
        }
    }

    /* loaded from: classes.dex */
    private class NextButtonHandler implements ListAdapter, View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, AdapterView.OnItemClickListener {
        private static final int ITEM_LOOP_ALL = 1;
        private static final int ITEM_LOOP_ONE = 0;
        private static final int ITEM_SHUFFLE = 2;
        private static final int NB_ITEMS = 3;
        private int _itemLayoutId;
        private LayoutInflater _layoutInflater;
        private ListView _listView;
        private DataSetObservable _listViewObserver;
        private int _loop;
        private boolean _shuffle;

        private NextButtonHandler() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ActivityScreen.this.next();
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (ActivityScreen.this.isFinishing()) {
                return false;
            }
            this._loop = P.playerLooping;
            this._shuffle = P.shuffle;
            AlertDialog dialog = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.next).setAdapter(this, null).setPositiveButton(17039370, this).setNeutralButton(R.string.close, (DialogInterface.OnClickListener) null).create();
            this._listView = dialog.getListView();
            this._listView.setChoiceMode(2);
            this._listView.setOnItemClickListener(this);
            this._listViewObserver = new DataSetObservable2();
            this._layoutInflater = dialog.getLayoutInflater();
            TypedArray a = ActivityScreen.this.obtainStyledAttributes(R.styleable.AlertDialog);
            this._itemLayoutId = a.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, R.layout.select_dialog_multichoice_material);
            a.recycle();
            ActivityScreen.this.showDialog((ActivityScreen) dialog, (DialogInterface.OnDismissListener) this);
            updateCheckedStates();
            return true;
        }

        private void updateCheckedStates() {
            switch (this._loop) {
                case 0:
                    this._listView.setItemChecked(0, false);
                    this._listView.setItemChecked(1, false);
                    break;
                case 1:
                    this._listView.setItemChecked(0, true);
                    this._listView.setItemChecked(1, false);
                    break;
                case 9:
                    this._listView.setItemChecked(0, false);
                    this._listView.setItemChecked(1, true);
                    break;
            }
            this._listView.setItemChecked(2, this._shuffle);
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    if (this._listView.isItemChecked(0)) {
                        this._loop = 1;
                    } else {
                        this._loop = 0;
                    }
                    updateCheckedStates();
                    return;
                case 1:
                    if (this._listView.isItemChecked(1)) {
                        this._loop = 9;
                    } else {
                        this._loop = 0;
                    }
                    updateCheckedStates();
                    return;
                case 2:
                    this._shuffle = this._listView.isItemChecked(2);
                    updateCheckedStates();
                    return;
                default:
                    return;
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            apply();
            ActivityScreen.this.next();
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return true;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver observer) {
            this._listViewObserver.registerObserver(observer);
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this._listViewObserver.unregisterObserver(observer);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return 3;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View view, ViewGroup parent) {
            String text;
            if (view == null) {
                view = this._layoutInflater.inflate(this._itemLayoutId, parent, false);
            }
            TextView textView = (TextView) view.findViewById(16908308);
            switch (position) {
                case 0:
                    text = ActivityScreen.this.getString(R.string.loop_one);
                    break;
                case 1:
                    text = ActivityScreen.this.getString(R.string.loop_all);
                    break;
                case 2:
                    text = ActivityScreen.this.getString(R.string.shuffle);
                    break;
                default:
                    text = "";
                    break;
            }
            textView.setText(text);
            textView.setEnabled(isEnabled(position));
            return view;
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return false;
        }

        private void apply() {
            if (this._shuffle != P.shuffle || this._loop != P.playerLooping) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putBoolean(Key.SHUFFLE, this._shuffle);
                editor.putInt(Key.LOOP, this._loop);
                AppUtils.apply(editor);
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            ActivityScreen.this.dialogRegistry.onDismiss(dialog);
            apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ZoomAdapterEntry {
        final CharSequence text;
        final int zoomMode;
        final float zoomRatio;

        ZoomAdapterEntry(CharSequence text, float ratio) {
            this.text = text;
            this.zoomMode = -1;
            this.zoomRatio = ratio;
        }

        ZoomAdapterEntry(CharSequence text, int mode) {
            this.text = text;
            this.zoomMode = mode;
            this.zoomRatio = 0.0f;
        }
    }

    /* loaded from: classes.dex */
    private class ZoomButtonHandler extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener {
        private ArrayList<ZoomAdapterEntry> _entries;
        private int _itemLayoutId;

        private ZoomButtonHandler() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (ActivityScreen.this.pp.isInPlaybackState() && ActivityScreen.this._hasVideoTrack) {
                ActivityScreen.this.showController();
                ActivityScreen.this.zoomNext();
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            return invokeZoomBox();
        }

        public boolean invokeZoomBox() {
            if (!ActivityScreen.this.pp.isInPlaybackState() || ActivityScreen.this.isFinishing() || !ActivityScreen.this._hasVideoTrack) {
                return false;
            }
            int width = ActivityScreen.this.pp.getDisplayWidth();
            int height = ActivityScreen.this.pp.getDisplayHeight();
            if (width <= 8 || height <= 8) {
                return false;
            }
            this._entries = new ArrayList<>();
            this._entries.add(new ZoomAdapterEntry((CharSequence) ActivityScreen.this.getString(R.string.zoom_inside), 1));
            this._entries.add(new ZoomAdapterEntry((CharSequence) ActivityScreen.this.getString(R.string.zoom_stretch), 0));
            this._entries.add(new ZoomAdapterEntry((CharSequence) ActivityScreen.this.getString(R.string.zoom_crop), 3));
            this._entries.add(new ZoomAdapterEntry((CharSequence) "100%", 2));
            int currentIndex = -1;
            if (ActivityScreen.this._zoomWidth == 0) {
                switch (ActivityScreen.this._videoZoom) {
                    case 0:
                        currentIndex = 1;
                        break;
                    case 1:
                        currentIndex = 0;
                        break;
                    case 2:
                        currentIndex = 3;
                        break;
                    case 3:
                        currentIndex = 2;
                        break;
                }
            }
            int topWidth = ActivityScreen.this.topLayout.getWidth();
            int topHeight = ActivityScreen.this.topLayout.getHeight();
            float mul = 1.5f;
            while (true) {
                float zoomWidth = width * mul;
                float zoomHeight = height * mul;
                if (zoomWidth < topWidth && zoomHeight < topHeight) {
                    if (currentIndex == -1 && zoomWidth == ActivityScreen.this._zoomWidth) {
                        currentIndex = this._entries.size();
                    }
                    this._entries.add(new ZoomAdapterEntry(Integer.toString(((int) (mul / 0.5f)) * 50) + '%', mul));
                    mul += 0.5f;
                }
            }
            AlertDialog dialog = new AlertDialog.Builder(ActivityScreen.this).setTitle(R.string.zoom).setSingleChoiceItems(this, currentIndex, this).create();
            TypedArray a = ActivityScreen.this.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
            this._itemLayoutId = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 17367058);
            a.recycle();
            ActivityScreen.this.showDialog((ActivityScreen) dialog);
            return true;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this._entries.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = ActivityScreen.this.layoutInflater.inflate(this._itemLayoutId, parent, false);
            }
            TextView tv = (TextView) v.findViewById(16908308);
            tv.setText(this._entries.get(position).text);
            return tv;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            String text;
            dialog.dismiss();
            if (ActivityScreen.this.pp.isInPlaybackState() && ActivityScreen.this._hasVideoTrack) {
                ZoomAdapterEntry entry = this._entries.get(which);
                ActivityScreen.this._panX = ActivityScreen.this._panY = 0;
                if (entry.zoomMode >= 0) {
                    switch (entry.zoomMode) {
                        case 0:
                            text = ActivityScreen.this.getString(R.string.zoom_stretch).toUpperCase(Locale.getDefault());
                            break;
                        case 1:
                            text = ActivityScreen.this.getString(R.string.zoom_inside).toUpperCase(Locale.getDefault());
                            break;
                        case 2:
                        default:
                            text = "100%";
                            break;
                        case 3:
                            text = ActivityScreen.this.getString(R.string.zoom_crop).toUpperCase(Locale.getDefault());
                            break;
                    }
                    ActivityScreen.this._videoZoom = entry.zoomMode;
                    ActivityScreen.this._zoomWidth = ActivityScreen.this._zoomHeight = 0;
                    ActivityScreen.this.setZoomMode(ActivityScreen.this._videoZoom);
                    ActivityScreen.this._dirty |= 2;
                    ActivityScreen.this.scheduleFlush();
                    ActivityScreen.this.setZoomSupremeText(text, ActivityScreen.this._surfaceView.getWidth(), ActivityScreen.this._surfaceView.getHeight());
                } else {
                    ActivityScreen.this._zoomWidth = (int) (entry.zoomRatio * ActivityScreen.this.pp.getDisplayWidth());
                    ActivityScreen.this._zoomHeight = (int) (entry.zoomRatio * ActivityScreen.this.pp.getDisplayHeight());
                    ActivityScreen.this.setDisplaySize(ActivityScreen.this._zoomWidth, ActivityScreen.this._zoomHeight);
                    ActivityScreen.this.setZoomSupremeText(Integer.toString((int) (entry.zoomRatio * 100.0f)) + '%', ActivityScreen.this._zoomWidth, ActivityScreen.this._zoomHeight);
                }
                ActivityScreen.this.handler.sendEmptyMessageDelayed(4, 500L);
            }
        }
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final int currentPosition() {
        if (this.pp.isInPlaybackState()) {
            return this.pp.getCurrentPosition();
        }
        return 0;
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final Object getSupremeTextToken() {
        return this._supremeText.getTag();
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void setSubtitleCanvasSize(int width, int height) {
        this.pp.setSubtitleCanvasSize(width, height);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void setSupremeText(int resId) {
        setSupremeText(getString(resId), null, true, null);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void setSupremeText(CharSequence text) {
        setSupremeText(text, null, true, null);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    public final void setSupremeText(CharSequence text, Drawable image, boolean animate) {
        setSupremeText(text, image, animate, null);
    }

    @Override // com.mxtech.videoplayer.IPlayer
    @SuppressLint({"NewApi"})
    public final void setSupremeText(CharSequence text, Drawable image, boolean animate, Object token) {
        this.handler.removeMessages(4);
        if (text != null) {
            try {
                this._supremeFadeOut.cancel();
            } catch (Throwable th) {
            }
            this._supremeImage.setImageDrawable(image);
            this._supremeText.setText(text);
            TextView textView = this._supremeText;
            if (token == null) {
                token = text;
            }
            textView.setTag(token);
            this._supremeContainer.setVisibility(0);
            if (this._showingLoadingSplash) {
                this._loadingSplash.hide();
                return;
            }
            return;
        }
        this._supremeText.setTag(null);
        if (this._supremeContainer.getVisibility() == 0) {
            if (animate) {
                this._supremeFadeOut.reset();
                this._supremeContainer.startAnimation(this._supremeFadeOut);
                return;
            }
            hideSupremeText();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSupremeText() {
        this._supremeContainer.setVisibility(8);
        this._supremeImage.setImageDrawable(null);
        this._supremeText.setText((CharSequence) null);
        if (this._showingLoadingSplash) {
            this._loadingSplash.show();
        }
    }

    private void showPlaybackSpeedSupremeText(double speed) {
        this.pp.setSpeed(speed);
        double speed2 = this.pp.getSpeed();
        IFloatingBar bar = getFloatingBar(R.id.playback_speed_bar);
        if (bar != null) {
            ((PlaybackSpeedBar) bar).updateSpeedText(speed2);
        }
        if (this._supremePlaybackSpeed == null) {
            this._supremePlaybackSpeed = getResources().getDrawable(R.drawable.supreme_playback_speed);
        }
        L.sb.setLength(0);
        L.sb.append(' ').append(Math.round(100.0d * speed2)).append('%');
        setSupremeText(L.sb.toString(), this._supremePlaybackSpeed, false);
    }

    /* loaded from: classes.dex */
    private class SupremeFadeOutHandler implements Animation.AnimationListener {
        private SupremeFadeOutHandler() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (ActivityScreen.this._supremeText.getTag() == null) {
                ActivityScreen.this.hideSupremeText();
            }
        }
    }

    private SoundBar getSoundBar() {
        JointPlayer mp;
        SoundBar bar = (SoundBar) this.topLayout.findViewById(R.id.soundBar);
        if (bar == null) {
            if (this._soundBarLayout != null) {
                this.topLayout.addView(this._soundBarLayout);
                bar = (SoundBar) this._soundBarLayout.findViewById(R.id.soundBar);
            } else {
                bar = (SoundBar) this.layoutInflater.inflate(R.layout.soundbar, this.topLayout).findViewById(R.id.soundBar);
                this._soundBarLayout = bar;
                bar.setPlayer(this);
            }
        }
        boolean allow = false;
        if (P.volumeBoost && (mp = this.pp.mp()) != null && (mp.getCharacteristics() & 4) != 0) {
            allow = true;
        }
        bar.allowOverVolume(allow);
        return bar;
    }

    public final boolean needVolumeSafetyWarnDialog() {
        return P.getHeadsetShowSystemVolumeUI() && this._headsetPlugged;
    }

    public final boolean isLocked() {
        return this._lock != null;
    }

    private BrightnessBar getBrightnessBar() {
        BrightnessBar bar = (BrightnessBar) this.topLayout.findViewById(R.id.brightnessBar);
        if (bar != null) {
            return bar;
        }
        if (this._brightnessBarLayout != null) {
            this.topLayout.addView(this._brightnessBarLayout);
            return (BrightnessBar) this._brightnessBarLayout.findViewById(R.id.brightnessBar);
        }
        BrightnessBar bar2 = (BrightnessBar) this.layoutInflater.inflate(R.layout.brightbar, this.topLayout).findViewById(R.id.brightnessBar);
        this._brightnessBarLayout = bar2;
        bar2.setPlayer(this);
        return bar2;
    }

    public final void changeBrightness(float brightness) {
        DeviceUtils.setBrightness(getWindow(), brightness);
        if (this._lock != null) {
            this._lock.changeBrightness(brightness);
        }
        this._dirty |= 1;
        scheduleFlush();
    }

    public final void onBaseVolumeChanged() {
        if (!P.syncSystemVolume) {
            this._dirty |= 16;
            scheduleFlush();
        }
        applyBaseVolume();
    }

    public final void onOverVolumeChanged() {
        this._dirty |= 4;
        applyOverVolume();
        scheduleFlush();
    }

    private void applyBaseVolume() {
        if (this.pp.mp() != null) {
            this.pp.applyBaseVolume();
        }
    }

    private void applyOverVolume() {
        if (this.pp.mp() != null) {
            this.pp.applyOverVolume();
        }
    }

    private void setSeekSupremeText(int pos, int delta, Object marker) {
        int posInSecond = pos / 1000;
        int deltaInSecond = delta / 1000;
        SpannableStringBuilder text = new SpannableStringBuilder();
        if (posInSecond >= 0) {
            text.append((CharSequence) DateUtils.formatElapsedTime(L.sb, posInSecond));
        } else {
            text.append('-').append((CharSequence) DateUtils.formatElapsedTime(L.sb, -posInSecond));
        }
        int smallBegin = text.length();
        text.append('\n').append('[');
        if (deltaInSecond >= 0) {
            text.append('+').append((CharSequence) DateUtils.formatElapsedTime(L.sb, deltaInSecond));
        } else {
            text.append('-').append((CharSequence) DateUtils.formatElapsedTime(L.sb, -deltaInSecond));
        }
        text.append(']');
        text.setSpan(smallSizeSpan, smallBegin, text.length(), 33);
        if (marker != null) {
            text.setSpan(marker, 0, 0, 17);
        }
        setSupremeText(text);
    }

    private void setVideoProgress(int position) {
        boolean seekbarVisible = (this._controller.getVisibleParts() & 1) != 0;
        if (seekbarVisible) {
            this._seekBar.setProgress(position);
        }
        if (seekbarVisible || P.elapsedTimeShowAlways) {
            setProgressText(position, seekbarVisible);
        }
    }

    @Override // android.app.Activity
    @TargetApi(12)
    public boolean onGenericMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 8:
                P.hasWheelHeuristic = true;
                int delta = Math.round(event.getAxisValue(9));
                if (delta != 0) {
                    switch (P.playbackWheelAction) {
                        case 1:
                            changeSoundVolumeByWheel(delta);
                            return true;
                        case 2:
                            if (P.screenBrightnessAuto || DeviceUtils.isTV) {
                                return true;
                            }
                            changeBrightnessByWheel(delta);
                            return true;
                        case 3:
                            seekByWheel(delta);
                            return true;
                        default:
                            boolean brightOn = (P.screenBrightnessAuto || DeviceUtils.isTV) ? false : true;
                            if (brightOn && 1 != 0) {
                                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                                if (event.getRawX() < screenWidth / 2) {
                                    changeBrightnessByWheel(delta);
                                    return true;
                                }
                                changeSoundVolumeByWheel(delta);
                                return true;
                            } else if (brightOn) {
                                changeBrightnessByWheel(delta);
                                return true;
                            } else if (1 != 0) {
                                changeSoundVolumeByWheel(delta);
                                return true;
                            } else {
                                return true;
                            }
                    }
                }
                return true;
            case 7:
            case 9:
                P.hasWheelHeuristic = true;
                break;
        }
        return super.onGenericMotionEvent(event);
    }

    private void changeSoundVolumeByWheel(int delta) {
        SoundBar bar = getSoundBar();
        bar.show();
        bar.change(delta);
    }

    private void changeBrightnessByWheel(int delta) {
        BrightnessBar bar = getBrightnessBar();
        bar.show();
        bar.change(delta);
    }

    @Override // com.mxtech.videoplayer.widget.PlaybackController.ControlTarget
    public void seekByWheel(int delta) {
        this._controller.extendVisibility();
        seekToDelta(this._naviMoveIntervalMillis * delta, 3);
    }

    private boolean isInSubTextBoundOrBackground(float x, float y, float tolerance) {
        this._subView.getLocationOnScreen(this.subview_screen_offset);
        float sub_x = x - this.subview_screen_offset[0];
        float sub_y = y - this.subview_screen_offset[1];
        return (this._subView.getBackground() != null && sub_y >= (-tolerance)) || this._subView.isPositionOnTextBound(sub_x, sub_y, tolerance);
    }

    private int getActionIndex(int action) {
        return (65280 & action) >> 8;
    }

    @Override // android.view.View.OnTouchListener
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent event) {
        return handleUITouch(v, event, this._gestures);
    }

    private int getDirection(float x0, float y0, float x1, float y1, double minDistance) {
        double deltaX = x1 - x0;
        double deltaY = y1 - y0;
        if (deltaX != SubView.SUBTITLE_DRAG_GAP || deltaY != SubView.SUBTITLE_DRAG_GAP) {
            double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
            if (distance >= minDistance) {
                double angle = Math.atan2(deltaY, deltaX);
                if (2.6179938779914944d < angle || angle < -2.6179938779914944d) {
                    return 3;
                }
                if (-0.5235987755982988d < angle && angle < 0.5235987755982988d) {
                    return 4;
                }
                if (1.0471975511965976d < angle && angle < 2.0943951023931953d) {
                    return 2;
                }
                if (-2.0943951023931953d < angle && angle < -1.0471975511965976d) {
                    return 1;
                }
            }
        }
        return 0;
    }

    private static boolean isSameDirection(int dir1, int dir2, int req1, int req2) {
        if (dir1 == 0 || dir2 == 0 || dir1 != dir2) {
            return false;
        }
        return dir1 == req1 || dir1 == req2;
    }

    private static boolean isOppositeDirection(int dir1, int dir2, int req1, int req2) {
        return dir1 != dir2 && (dir1 == 0 || dir1 == req1 || dir1 == req2) && (dir2 == 0 || dir2 == req1 || dir2 == req2);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public final boolean handleUITouch(View v, MotionEvent event, int gestures) {
        float ratio;
        float height;
        float width;
        boolean reachedLimit;
        ISubtitle subtitle;
        int action = event.getAction();
        v.getLocationOnScreen(this.screen_offset);

        // 0 = MotionEvent.ACTION_DOWN
        switch (action) {
            case 0:
                if (this._swiping == 0) {
                    this._touchStartX = ((int) event.getX()) + this.screen_offset[0];
                    this._touchStartY = ((int) event.getY()) + this.screen_offset[1];
                    if ((gestures & 512) != 0 && this._subView.getVisibility() != 8 && isInSubTextBoundOrBackground(this._touchStartX, this._touchStartY, SubView.SUBTITLE_DRAG_GAP_px)) {
                        this._isSubtitleTextDragging = true;
                        this._subView.setTextBackColor(TEXT_TOUCH_BACK_COLOR);
                        this._subView.lockUpdate();
                        return true;
                    }
                    return true;
                }
                return true;
            // 1 = MotionEvent.ACTION_UP
            // 3 = MotionEvent.ACTION_CANCEL
            case 1:
            case 3:
                switch (this._swiping) {
                    case 0:
                        if (action == 1) {
                            long now = SystemClock.uptimeMillis();
                            if (v instanceof UILayout) {
                                if ((gestures & 1088) != 0 && this._lastSwiping == 0 && now < this._lastSingleTapTime + 400 && P.playbackTouchAction == 2) {
                                    if ((gestures & 64) != 0) {
                                        toggle(true);
                                        break;
                                    } else {
                                        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
                                            this._zoomWidth = 0;
                                            this._zoomHeight = 0;
                                            this._panY = 0;
                                            this._panX = 0;
                                            this._doubleTapZooming = false;
                                            setZoomMode(this._videoZoom);
                                        } else {
                                            int touchX = ((int) event.getX(0)) + this.screen_offset[0];
                                            int touchY = ((int) event.getY(0)) + this.screen_offset[1];
                                            int topWidth = this.topLayout.getWidth();
                                            int topHeight = this.topLayout.getHeight();
                                            Point size = getSizeFor(this._videoZoom);
                                            this._zoomWidth = size.x * 2;
                                            this._zoomHeight = size.y * 2;
                                            this._panX = (-(touchX - (topWidth / 2))) * 2;
                                            this._panY = (-(touchY - (topHeight / 2))) * 2;
                                            int gapX = (topWidth - this._zoomWidth) / 2;
                                            int gapY = (topHeight - this._zoomHeight) / 2;
                                            int pannedX = gapX + this._panX;
                                            int pannedY = gapY + this._panY;
                                            if (pannedX > 0) {
                                                this._panX = -gapX;
                                            } else if (this._zoomWidth + pannedX < topWidth) {
                                                this._panX = gapX;
                                            }
                                            if (pannedY > 0) {
                                                this._panY = -gapY;
                                            } else if (this._zoomHeight + pannedY < topHeight) {
                                                this._panY = gapY;
                                            }
                                            this._doubleTapZooming = true;
                                            setDisplaySize(this._zoomWidth, this._zoomHeight);
                                        }
                                        updateLayoutForZoomPan();
                                        break;
                                    }
                                } else {
                                    this._lastSingleTapTime = now;
                                    switch (P.playbackTouchAction) {
                                        case 1:
                                            if (isInInterfacePosition(this._touchStartX, this._touchStartY)) {
                                                showController();
                                                break;
                                            } else {
                                                toggle(true);
                                                break;
                                            }
                                        case 2:
                                            if (isControllerFullyVisible()) {
                                                this._controller.hide(2);
                                                break;
                                            } else {
                                                showController();
                                                break;
                                            }
                                        case 3:
                                            if (isInInterfacePosition(this._touchStartX, this._touchStartY)) {
                                                showController();
                                                break;
                                            } else if (this.pp.getTargetState() == 4) {
                                                pause(0);
                                                break;
                                            } else {
                                                start();
                                                this._controller.hide(2);
                                                break;
                                            }
                                        default:
                                            if (isControllerFullyVisible()) {
                                                toggle(true);
                                                break;
                                            } else {
                                                showController();
                                                break;
                                            }
                                    }
                                }
                            } else if (this._lockShowInterfaceOnTouched) {
                                this._controller.setVisibleParts(3, 0);
                                this._controller.extendVisibility();
                                break;
                            }
                        }
                        break;
                    case 1:
                        this._consecutiveSeekBegin = -1;
                        this._controller.unpin();
                        if (isControllerSeekingVisible()) {
                            this._controller.hide(0);
                        }
                        setSupremeText(null, null, false);
                        if (this.pp.isInPlaybackState()) {
                            this.pp.seekTo(this._seekBar.getProgress(), this.pp.getShortSeekTimeout());
                        }
                        resume();
                        break;
                    case 2:
                        this._dragBar.unpin();
                        this._dragBar.hide(true);
                        this._dragBar = null;
                        break;
                    case 3:
                    case 17:
                    case 18:
                    case 19:
                        setSupremeText((CharSequence) null);
                        break;
                    case 33:
                        this._subView.unlockUpdate();
                        if (action == 1) {
                            double distance = (event.getX() + this.screen_offset[0]) - this._touchStartX;
                            if (distance >= SubView.SUBTITLE_SCROLL_MINIMUM_MOVE_px) {
                                this._subView.seekAdjascent(-1);
                                break;
                            } else if (distance <= (-SubView.SUBTITLE_SCROLL_MINIMUM_MOVE_px)) {
                                this._subView.seekAdjascent(1);
                                break;
                            }
                        }
                        break;
                    case 34:
                        setSupremeText((CharSequence) null);
                        if (action == 1) {
                            SharedPreferences.Editor editor = App.prefs.edit();
                            editor.putInt(Key.SUBTITLE_BOTTOM_PADDING, P.subtitleBottomPaddingDp);
                            AppUtils.apply(editor);
                            break;
                        }
                        break;
                    case 36:
                    case 37:
                        this._subView.unlockUpdate();
                        setSupremeText((CharSequence) null);
                        if (action == 1) {
                            SharedPreferences.Editor editor2 = App.prefs.edit();
                            if (this._swiping == 36) {
                                editor2.putFloat(Key.SUBTITLE_TEXT_SIZE, DeviceUtils.PixelToSP(this._subView.getTextSize()));
                            } else {
                                editor2.putFloat(Key.SUBTITLE_SCALE, P.subtitleScale);
                            }
                            AppUtils.apply(editor2);
                            break;
                        }
                        break;
                }
                this._lastSwiping = this._swiping;
                this._swiping = 0;
                if (this._isSubtitleTextDragging) {
                    this._isSubtitleTextDragging = false;
                    this._subView.setTextBackColor(0);
                    this._subView.unlockUpdate();
                    return true;
                }
                return true;
            // 2 = MotionEvent.ACTION_MOVE
            case 2:
                switch (this._swiping) {
                    case 0:
                        int numPointer = event.getPointerCount();
                        if (numPointer == 1) {
                            int x = ((int) event.getX()) + this.screen_offset[0];
                            int y = ((int) event.getY()) + this.screen_offset[1];
                            int direction = getDirection(this._touchStartX, this._touchStartY, x, y, DeviceUtils.dragMinimumPixel);
                            switch (direction) {
                                case 1:
                                case 2:
                                    this._touchStartY = y;
                                    if ((gestures & 512) != 0 && this._isSubtitleTextDragging) {
                                        this._swiping = 34;
                                        this._startSubtitleBottomPadding = this._subView.getPaddingBottom();
                                        return true;
                                    }
                                    boolean brightOn = (P.screenBrightnessAuto || DeviceUtils.isTV || (gestures & 32) == 0) ? false : true;
                                    boolean volumeOn = (gestures & 16) != 0;
                                    if (brightOn && volumeOn) {
                                        if (this._touchStartX < this.uiLayout.getWidth() / 2) {
                                            this._dragBar = getBrightnessBar();
                                        } else {
                                            this._dragBar = getSoundBar();
                                        }
                                    } else if (brightOn) {
                                        this._dragBar = getBrightnessBar();
                                    } else if (volumeOn) {
                                        this._dragBar = getSoundBar();
                                    } else {
                                        return true;
                                    }
                                    this._swiping = 2;
                                    this._dragBar.pin();
                                    this._dragBar.show();
                                    this._dragBegunLevel = this._dragBar.getCurrent();
                                    return true;
                                case 3:
                                case 4:
                                    this._touchStartX = x;
                                    if (this.pp.isSeekable()) {
                                        if ((gestures & 128) != 0 && this._subView.getVisibility() != 8) {
                                            this._subView.getLocationOnScreen(this.subview_screen_offset);
                                            if (y >= this.subview_screen_offset[1]) {
                                                this._swiping = 33;
                                                this._subView.lockUpdate();
                                                return true;
                                            }
                                        }
                                        if ((gestures & 8) != 0) {
                                            pause(80);
                                            this._swiping = 1;
                                            consecutiveSeekBegin();
                                            this._controller.pin();
                                            if (!isControllerAnyVisible()) {
                                                this._controller.setVisibleParts(1, 0);
                                                return true;
                                            }
                                            return true;
                                        }
                                        return true;
                                    }
                                    return true;
                                default:
                                    return true;
                            }
                        } else if (numPointer >= 2) {
                            float x2 = event.getX(0) + this.screen_offset[0];
                            float x22 = event.getX(1) + this.screen_offset[0];
                            float y2 = event.getY(0) + this.screen_offset[1];
                            float y22 = event.getY(1) + this.screen_offset[1];
                            float deltaX = x2 - this._touchStartX;
                            float deltaY = y2 - this._touchStartY;
                            double distance2 = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                            float deltaX2 = x22 - this._touchStartX2;
                            float deltaY2 = y22 - this._touchStartY2;
                            double distance22 = Math.sqrt((deltaX2 * deltaX2) + (deltaY2 * deltaY2));
                            if (distance2 + distance22 >= DeviceUtils.dragMinimumPixel) {
                                int direction1 = getDirection(this._touchStartX, this._touchStartY, x2, y2, SubView.SUBTITLE_DRAG_GAP);
                                int direction2 = getDirection(this._touchStartX2, this._touchStartY2, x22, y22, SubView.SUBTITLE_DRAG_GAP);
                                if ((gestures & 2048) != 0 && isSameDirection(direction1, direction2, 1, 2)) {
                                    this._swiping = 3;
                                    this._touchStartY = (int) y2;
                                    this._dragBegunLevel = getSpeed();
                                    showPlaybackSpeedSupremeText(this._dragBegunLevel);
                                    return true;
                                }
                                if ((gestures & 256) != 0 && this._subView.getVisibility() != 8 && isOppositeDirection(direction1, direction2, 4, 3) && (subtitle = this._subView.getFirstVisibleSubtitle()) != null) {
                                    this._subView.getLocationOnScreen(this.subview_screen_offset);
                                    if (y2 >= this.subview_screen_offset[1] && y22 >= this.subview_screen_offset[1]) {
                                        this._touchStartX = (int) x2;
                                        this._touchStartY = (int) y2;
                                        this._touchStartX2 = (int) x22;
                                        this._touchStartY2 = (int) y22;
                                        this._zoomDragBeginWidth = (int) Math.abs(x2 - x22);
                                        this._zoomDragBeginHeight = (int) Math.abs(y2 - y22);
                                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
                                            int flags = subtitle.flags();
                                            if ((5242880 & flags) != 0) {
                                                this._startScale = P.subtitleScale;
                                                this._swiping = 37;
                                            } else {
                                                this._startSubtitleTextSizeSp = DeviceUtils.PixelToSP(this._subView.getTextSize());
                                                this._swiping = 36;
                                            }
                                            this._subView.lockUpdate();
                                            return true;
                                        }
                                        return true;
                                    }
                                }
                                if (this.pp.isInPlaybackState() && (gestures & 7) != 0 && this._surfaceView != null) {
                                    int videoWidth = this.pp.getDisplayWidth();
                                    int videoHeight = this.pp.getDisplayHeight();
                                    if (videoWidth > 0 && videoHeight > 0) {
                                        this._touchStartX = (int) x2;
                                        this._touchStartY = (int) y2;
                                        this._touchStartX2 = (int) x22;
                                        this._touchStartY2 = (int) y22;
                                        this._panStartX = this._panX;
                                        this._panStartY = this._panY;
                                        this._zoomDragBeginWidth = this._surfaceView.getWidth();
                                        this._zoomDragBeginHeight = (this._zoomDragBeginWidth * videoHeight) / videoWidth;
                                        if ((gestures & 4) != 0) {
                                            this._swiping = 19;
                                            return true;
                                        } else if ((gestures & 7) == 3) {
                                            double angle = Math.atan2(deltaY, deltaX);
                                            double angle2 = Math.atan2(deltaY2, deltaX2);
                                            boolean isPan = Math.abs(Misc.canonicalizeRadian(angle - angle2)) < 0.6283185307179586d;
                                            if (isPan) {
                                                this._swiping = 18;
                                                return true;
                                            } else if (!isSameDirection(direction1, direction2, 1, 2) && !isSameDirection(direction1, direction2, 4, 3)) {
                                                this._swiping = 17;
                                                return true;
                                            } else {
                                                return true;
                                            }
                                        } else if ((gestures & 1) != 0) {
                                            if (!isSameDirection(direction1, direction2, 1, 2) && !isSameDirection(direction1, direction2, 4, 3)) {
                                                this._swiping = 17;
                                                return true;
                                            }
                                            return true;
                                        } else {
                                            this._swiping = 18;
                                            return true;
                                        }
                                    }
                                    return true;
                                }
                                return true;
                            }
                            return true;
                        } else {
                            return true;
                        }
                    case 1:
                        if (this.pp.isSeekable()) {
                            int newPos = (int) (this._consecutiveSeekBegin + (this._gestureSeekSpeed * DeviceUtils.PixelToMeter((event.getX() + this.screen_offset[0]) - this._touchStartX)));
                            int duration = this.pp.getDuration();
                            if (newPos < 0) {
                                newPos = 0;
                            } else if (newPos >= duration) {
                                newPos = duration - 1;
                            }
                            setVideoProgress(newPos);
                            if (this._displaySeekingPosition) {
                                setSeekSupremeText(newPos, newPos - this._consecutiveSeekBegin, null);
                            }
                            if (this.pp.isInPlaybackState() && showSeekPreviews()) {
                                previewSeekTo(newPos);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    case 2:
                        this._dragBar.setValue((int) (this._dragBegunLevel + (this._dragBar.getMax() * DeviceUtils.PixelToDIP((this._touchStartY - event.getY()) - this.screen_offset[1]) * VERTICAL_BAR_DRAG_SPEED)));
                        return true;
                    case 3:
                        if (event.getPointerCount() >= 2) {
                            double newSpeed = PlaybackSpeedBar.getValidSpeed(this._dragBegunLevel + (((int) (DeviceUtils.PixelToDIP((this._touchStartY - event.getY()) - this.screen_offset[1]) * VERTICAL_BAR_DRAG_SPEED * 30.0f)) * 0.05d));
                            if (this.pp.getSpeed() != newSpeed) {
                                showPlaybackSpeedSupremeText(newSpeed);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    case 17:
                    case 18:
                    case 19:
                        if (this.pp.isInPlaybackState() && this._surfaceView != null) {
                            int videoWidth2 = this.pp.getDisplayWidth();
                            int videoHeight2 = this.pp.getDisplayHeight();
                            if (videoWidth2 > 0 && videoHeight2 > 0) {
                                if (((this._swiping == 17) | (this._swiping == 19)) && event.getPointerCount() >= 2) {
                                    float distanceXNow = Math.abs(event.getX(0) - event.getX(1));
                                    float distanceYNow = Math.abs(event.getY(0) - event.getY(1));
                                    if (distanceXNow > distanceYNow) {
                                        width = (this._zoomDragBeginWidth + distanceXNow) - Math.abs(this._touchStartX - this._touchStartX2);
                                        height = (videoHeight2 * width) / videoWidth2;
                                    } else {
                                        height = (this._zoomDragBeginHeight + distanceYNow) - Math.abs(this._touchStartY - this._touchStartY2);
                                        width = (videoWidth2 * height) / videoHeight2;
                                    }
                                    if (width < this._zoomMinWidth) {
                                        width = this._zoomMinWidth;
                                        height = this._zoomMinHeight;
                                        reachedLimit = true;
                                    } else if (width > this._zoomMaxWidth) {
                                        width = this._zoomMaxWidth;
                                        height = this._zoomMaxHeight;
                                        reachedLimit = true;
                                    } else {
                                        reachedLimit = false;
                                    }
                                    if (this._zoomWidth != ((int) width) || this._zoomHeight != ((int) height) || reachedLimit) {
                                        this._zoomWidth = (int) width;
                                        this._zoomHeight = (int) height;
                                        L.sb.setLength(0);
                                        L.sb.append((this._zoomWidth * 100) / videoWidth2).append('%');
                                        setZoomSupremeText(L.sb.toString(), this._zoomWidth, this._zoomHeight);
                                    }
                                }
                                if ((this._swiping == 18) | (this._swiping == 19)) {
                                    this._panX = this._panStartX + ((int) ((event.getX(0) + this.screen_offset[0]) - this._touchStartX));
                                    this._panY = this._panStartY + ((int) ((event.getY(0) + this.screen_offset[1]) - this._touchStartY));
                                }
                                if (this._zoomWidth != 0 && this._zoomHeight != 0) {
                                    setDisplaySize(this._zoomWidth, this._zoomHeight);
                                    return true;
                                }
                                setZoomMode(this._videoZoom);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    case 34:
                        float newPadding = DeviceUtils.PixelToDIP(((this._startSubtitleBottomPadding + this._touchStartY) - event.getY()) - this.screen_offset[1]);
                        if (this.orientation == 1) {
                            newPadding = (DeviceUtils.smallestPixels * newPadding) / DeviceUtils.largestPixels;
                        }
                        if (newPadding < 0.0f) {
                            newPadding = 0.0f;
                        } else if (newPadding > P.MAX_BOTTOM_PADDING) {
                            newPadding = P.MAX_BOTTOM_PADDING;
                        }
                        P.subtitleBottomPaddingDp = Math.round(newPadding);
                        float newPaddingPx = DeviceUtils.DIPToPixel(newPadding);
                        this._subView.setSubtitlePadding(newPaddingPx);
                        if (this._supremeUpdown == null) {
                            this._supremeUpdown = getResources().getDrawable(R.drawable.supreme_updown);
                        }
                        L.sb.setLength(0);
                        setSupremeText(L.sb.append(' ').append(P.subtitleBottomPaddingDp).toString(), this._supremeUpdown, false);
                        return true;
                    case 36:
                    case 37:
                        if (event.getPointerCount() >= 2) {
                            float distanceXNow2 = Math.abs(event.getX(0) - event.getX(1));
                            float distanceYNow2 = Math.abs(event.getY(0) - event.getY(1));
                            if (distanceXNow2 > distanceYNow2) {
                                ratio = distanceXNow2 / this._zoomDragBeginWidth;
                            } else {
                                ratio = distanceYNow2 / this._zoomDragBeginHeight;
                            }
                            L.sb.setLength(0);
                            if (this._swiping == 36) {
                                float newSizeSp = this._startSubtitleTextSizeSp * ratio;
                                if (newSizeSp < 16.0f) {
                                    newSizeSp = 16.0f;
                                } else if (newSizeSp > 60.0f) {
                                    newSizeSp = 60.0f;
                                }
                                this._subView.setTextSize(newSizeSp);
                                L.sb.append(' ').append(Math.round(newSizeSp));
                            } else {
                                float scale = P.canonicalizeSubtitleScale(this._startScale * ratio);
                                setSubtitleScale(scale);
                                L.sb.append(' ').append(Math.round(100.0f * scale)).append('%');
                            }
                            if (this._supremeTextSize == null) {
                                this._supremeTextSize = getResources().getDrawable(R.drawable.supreme_text_size);
                            }
                            setSupremeText(L.sb.toString(), this._supremeTextSize, false);
                            return true;
                        }
                        return true;
                    default:
                        return true;
                }

                // 5 = MotionEvent.ACTION_POINTER_DOWN
            case 5:
            case MediaRouter.GlobalMediaRouter.CallbackHandler.MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED /* 261 */:
                if (event.getPointerCount() == 2) {
                    float x3 = event.getX(0) + this.screen_offset[0];
                    float x23 = event.getX(1) + this.screen_offset[0];
                    float y3 = event.getY(0) + this.screen_offset[1];
                    float y23 = event.getY(1) + this.screen_offset[1];
                    if ((this._swiping & 16) != 0) {
                        if (this._surfaceView != null) {
                            int videoWidth3 = this.pp.getDisplayWidth();
                            int videoHeight3 = this.pp.getDisplayHeight();
                            if (videoWidth3 > 0 && videoHeight3 > 0) {
                                this._touchStartX = (int) x3;
                                this._touchStartY = (int) y3;
                                this._touchStartX2 = (int) x23;
                                this._touchStartY2 = (int) y23;
                                this._panStartX = this._panX;
                                this._panStartY = this._panY;
                                this._zoomDragBeginWidth = this._surfaceView.getWidth();
                                this._zoomDragBeginHeight = (this._zoomDragBeginWidth * videoHeight3) / videoWidth3;
                                return true;
                            }
                            return true;
                        }
                        return true;
                    } else if (this._swiping == 36 || this._swiping == 37) {
                        this._touchStartX = (int) x3;
                        this._touchStartY = (int) y3;
                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
                        this._zoomDragBeginWidth = (int) Math.abs(x3 - x23);
                        this._zoomDragBeginHeight = (int) Math.abs(y3 - y23);
                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
                            if (this._swiping == 37) {
                                this._startScale = P.subtitleScale;
                                return true;
                            }
                            this._startSubtitleTextSizeSp = DeviceUtils.PixelToSP(this._subView.getTextSize());
                            return true;
                        }
                        return true;
                    } else if (this._swiping == 3) {
                        this._touchStartX = (int) x3;
                        this._touchStartY = (int) y3;
                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
                        this._dragBegunLevel = getSpeed();
                        return true;
                    } else if (this._swiping == 0) {
                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
                        return true;
                    } else {
                        return true;
                    }
                }
                return true;
            // 6 = MotionEvent.ACTION_POINTER_UP
            case 6:
            case MediaRouter.GlobalMediaRouter.CallbackHandler.MSG_ROUTE_SELECTED /* 262 */:
                if (event.getPointerCount() == 2 && (this._swiping & 16) != 0) {
                    int erased = getActionIndex(action);
                    int left = erased == 1 ? 0 : 1;
                    this._touchStartX = ((int) event.getX(left)) + this.screen_offset[0];
                    this._touchStartY = ((int) event.getY(left)) + this.screen_offset[1];
                    this._panStartX = this._panX;
                    this._panStartY = this._panY;
                    return true;
                }
                return true;
            default:
                return true;
        }
    }

    public boolean isInInterfacePosition(int x, int y) {
        if (this._controller.hitTest(x, y)) {
            return true;
        }
        int actionBarHeight = getSupportActionBar().getHeight();
        return y < actionBarHeight;
    }

    private void doUpdateDirectRendering() {
        boolean enable;
        if (P.improve_ssa_rendering) {
            byte decoder = this.pp.getDecoder();
            if (decoder == 4) {
                enable = Build.VERSION.SDK_INT >= 11;
            } else if (decoder == 2) {
                enable = true;
            } else {
                enable = false;
            }
        } else {
            enable = false;
        }
        this._subView.setSSARenderingMode(enable, this.pp);
    }

    @Override // com.mxtech.videoplayer.subtitle.SubtitlePanel.Client
    public final File getLastSubtitleDir() {
        return this._lastSubtitleDir;
    }

    @Override // com.mxtech.videoplayer.subtitle.SubtitlePanel.Client
    public final void onLoadSubtitle(File file, boolean append) {
        int msgId;
        ISubtitle[] subtitle;
        if (!isFinishing() && this.pp.isInPlaybackState()) {
            this._lastSubtitleDir = file.getParentFile();
            do {
                try {
                    subtitle = SubtitleFactory.createFromFile(file, null, this.pp, null);
                } catch (Throwable e) {
                    if (e instanceof UnsupportedEncodingException) {
                        msgId = R.string.error_invalid_subtitle_file;
                    } else if (e instanceof UnsupportedCharsetException) {
                        msgId = R.string.error_invalid_subtitle_charset;
                    } else {
                        msgId = R.string.error_read_subtitle_file;
                    }
                }
                if (subtitle.length > 0) {
                    List<ISubtitle> subsAsList = Arrays.asList(subtitle);
                    if (!append) {
                        this.pp.replaceSubtitles(subsAsList);
                    }
                    addSubtitles(subsAsList, null, null, 5);
                    this.pp.save();
                    updateUserLayout();
                    return;
                }
                msgId = R.string.error_invalid_subtitle_file;
                file = SubtitleFactory.getAlternativeFile(this._lastSubtitleDir, file.getName());
            } while (file != null);
            DialogUtils.alert(this, msgId);
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.SubtitlePanel.Client
    public void onClose() {
        hideSubtitlePanel();
    }

    private void setWindowFullScreen(boolean fullscreen) {
        int i;
        Log.d(TAG, "Set fullscreen = " + fullscreen);
        Window window = getWindow();
        if (fullscreen) {
            i = 1024;
        } else {
            i = 0;
        }
        window.setFlags(i, 1024);
    }

    @SuppressLint({"NewApi"})
    private void setFullscreen(int fullscreen) {
        this._fullscreen = fullscreen;
        boolean systemWindowFittableChanged = false;
        if (fullscreen == 1) {
            setWindowFullScreen(false);
            if (this._systemWindowFittable != null) {
                this._systemWindowFittable.setFitsSystemWindows(false);
                systemWindowFittableChanged = true;
            }
        } else if (fullscreen == 0) {
            setWindowFullScreen(true);
            if (this._systemWindowFittable != null) {
                this._systemWindowFittable.setFitsSystemWindows(true);
                systemWindowFittableChanged = true;
            }
        } else if (this._systemWindowFittable != null) {
            setWindowFullScreen(false);
            this._systemWindowFittable.setFitsSystemWindows(true);
            systemWindowFittableChanged = true;
        }
        if (systemWindowFittableChanged) {
            if (Build.VERSION.SDK_INT >= 21) {
                this._systemWindowFittable.requestApplyInsets();
            } else {
                this._systemWindowFittable.requestFitSystemWindows();
            }
        }
        updateLayout();
    }

    private void updateBatteryClockInTitleBar() {
        if (this._batteryClockMenuItem != null) {
            this._batteryClockMenuItem.setVisible(this._batteryClockInTitleBar);
        }
        updateBatteryClockIntentReceiver();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onScreenOrientationChanged(Tuner tuner, int screenOrientation) {
        setOrientation(screenOrientation);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onDisplayBatteryClockInTitleBar(Tuner tuner, boolean show) {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putBoolean(Key.BATTERY_CLOCK_IN_TITLE_BAR, show);
        AppUtils.apply(editor);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSysStatusbarVisibilityChanged(Tuner tuner, int visibility) {
        setFullscreen(visibility);
        this.topLayout.requestLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSoftButtonsVisibilityChanged(Tuner tuner, int visibility) {
        P.softButtonsVisibility = visibility;
        updateSystemLayout();
        this.topLayout.requestLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onTouchActionChanged(Tuner tuner, int action) {
        P.playbackTouchAction = action;
        updateLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onShowMoveButtons(Tuner tuner, boolean show) {
        this._backwardButton.setVisibility(show ? 0 : 8);
        this._forwardButton.setVisibility(show ? 0 : 8);
        updateSubNavibarLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onShowPrevNextButtons(Tuner tuner, boolean show) {
        this._prevButton.setVisibility(show ? 0 : 8);
        this._nextButton.setVisibility(show ? 0 : 8);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleShadowChanged(Tuner tuner, boolean enabled) {
        this._subView.enableShadow(enabled);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleBorderChanged(Tuner tuner, boolean enabled, int color, float thickness) {
        this._subView.enableBorder(enabled);
        this._subView.setBorderColor(color);
        this._subView.setBorderThickness(thickness, thickness);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleBottomPaddingChanged(Tuner tuner, int padding) {
        this._subView.setSubtitlePadding(DeviceUtils.DIPToPixel(padding));
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleBkChanged(Tuner tuner, boolean enabled, int color) {
        P.subtitleBkColorEnabled = enabled;
        P.subtitleBkColor = color;
        updateSubtitleBkColor();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleOverlayFitChanged(Tuner tuner, boolean fitToVideo) {
        this._fitSubtitleOverlayToVideo = fitToVideo;
        if (this._subtitleOverlay != null) {
            updateSubtitleOverlayLayout();
        }
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleTextSizeChanged(Tuner tuner, int size) {
        this._subView.setTextSize(size);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleScaleChanged(Tuner tuner, float scale) {
        setSubtitleScale(scale);
    }

    private void setSubtitleScale(float scale) {
        if (this._subtitleOverlay != null) {
            this._subtitleOverlay.setFrameScale(scale);
        }
        SubStationAlphaMedia ssa = this.pp.getSubStationAlphaMedia(1, null);
        if (ssa != null) {
            ssa.setFontScale(getResources().getConfiguration().fontScale * scale);
            this._subView.refresh();
        }
        P.subtitleScale = scale;
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSSAFontIgnoreChanged(Tuner tuner, boolean ignore) {
        this.pp.overrideFonts(ignore ? P.getSubtitleFontFamilyName() : null);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSSABrokenFontIgnoreChanged(Tuner tuner, boolean ignore) {
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onStatusTextShowAlwaysChanged(Tuner tuner, boolean showAlways) {
        this._alwaysShowStatusText = showAlways;
        updateBatteryClockIntentReceiver();
        updateStatusLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onElapsedTimeShowAlwaysChanged(Tuner tuner, boolean showAlways) {
        P.elapsedTimeShowAlways = showAlways;
        updateStatusLayout();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onOSDPlacementChanged(Tuner tuner, boolean bottom, boolean background) {
        this._osdBottom = bottom;
        this._osdBackground = background;
        updateOSDPlacement();
    }

    private void changeOSDTextColor(int color) {
        this._osdTextColor = color;
        if (this._elapsedTimeText != null) {
            this._elapsedTimeText.setTextColor(color);
        }
        if (this._statusLayout != null) {
            if (this._statusLayout.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                setStatusTextColor(this._osdTextColor);
            } else {
                setStatusTextColor(P.DEFAULT_OSD_TEXT_COLOR);
            }
        }
        if (this._auxNotification != null) {
            this._auxNotification.setTextColor(color);
        }
    }

    private void changeOSDBackColor(int color) {
        this._osdBackColor = color;
        boolean changed = false;
        if (this._statusLayout != null) {
            this._statusLayout.setBackgroundDrawable(null);
            changed = true;
        }
        if (this._elapsedTimeText != null) {
            this._elapsedTimeText.setBackgroundDrawable(null);
            changed = true;
        }
        if (this._auxNotification != null) {
            this._auxNotification.setBackgroundDrawable(null);
            changed = true;
        }
        if (changed) {
            updateOSDPlacement();
        }
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onOSDColorChanged(Tuner tuner, int textColor, int backColor) {
        if (this._osdTextColor != textColor) {
            changeOSDTextColor(textColor);
        }
        if (this._osdBackColor != backColor) {
            changeOSDBackColor(backColor);
        }
    }

    private void setStatusTextColor(int color) {
        if (this._statusText != null && this._statusText.getTextColors().getDefaultColor() != color) {
            this._statusText.setTextColor(color);
            if (this._batteryCharging != null) {
                this._batteryCharging.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onShowScreenRotationButtonChanged(Tuner tuner, boolean show) {
        this._showScreenRotateButton = show;
        updateScreenRotationButton(this._lastRequestedOrientation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setShowLeftTime(boolean show) {
        this._showLeftTime = show;
        if (show) {
            clearCachedProgressTextInfo();
            updateProgressText();
            return;
        }
        this._durationText.setText(this._durationString);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onShowLeftTimeChanged(Tuner tuner, boolean showLeftTime) {
        setShowLeftTime(showLeftTime);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onScreenBrightnessChanged(Tuner tuner, float level) {
        changeBrightness(level);
        if (tuner != null) {
            DeviceUtils.setBrightness(tuner.getWindow(), level);
        }
    }

    private void updateTypeface() {
        this._subView.setTypeface(P.getSubtitleFont(), P.subtitleTypefaceStyle);
        if (this.pp.isFontOverridden()) {
            this.pp.overrideFonts(P.getSubtitleFontFamilyName());
        }
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleTypefaceChanged(Tuner tuner, String typefaceName, int style) {
        P.subtitleFontNameOrPath = typefaceName;
        P.subtitleTypefaceStyle = style;
        updateTypeface();
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleTextColorChanged(Tuner tuner, int color) {
        this._subView.setTextColor(color);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public final void onSubtitleAlignmentChanged(Tuner tuner, int gravity) {
        this._subView.setGravity(gravity | 80);
    }

    @Override // com.mxtech.videoplayer.preference.Tuner.Listener
    public void onScreenStyleChanged(Tuner tuner, ScreenStyle style, int changes) {
        setStyle(style, changes);
    }

    private void setStyle(ScreenStyle style, int changes) {
        this._controller.setStyle(style, changes);
        if (this.toolbar != null) {
            ((ScreenToolbar) this.toolbar).setStyle(style, changes);
        }
        getSoundBar().setStyle(style, changes);
        getBrightnessBar().setStyle(style, changes);
        setOnScreenButtonStyle(style, changes);
    }

    private Drawable setColorTo(Drawable d, int color) {
        if (d instanceof GradientDrawable) {
            ((GradientDrawable) GraphicUtils.safeMutate(d)).setColor(color);
        } else if (d instanceof ShapeDrawable) {
            ((ShapeDrawable) GraphicUtils.safeMutate(d)).setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        return d;
    }

    private Drawable createCircleButtonBackground(ScreenStyle style) {
        Resources res = getResources();
        StateListDrawable d = new StateListDrawable();
        int highlightColor = style.getControlColorHighlight();
        int frameColor = style.getFrameColor();
        d.addState(new int[]{16842919}, setColorTo(GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_circle_button_shape)), highlightColor));
        d.addState(new int[]{16842913}, setColorTo(GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_circle_button_shape)), highlightColor));
        d.addState(new int[]{16842908}, setColorTo(GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_circle_button_shape)), highlightColor));
        d.addState(new int[0], setColorTo(GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_circle_button_shape)), frameColor));
        return d;
    }

    private void setOnScreenButtonStyle(ScreenStyle style, int changes) {
        Drawable d;
        Drawable rotationButtonDrawable;
        Drawable rotationBackgroundDrawable;
        Drawable indicatorButtonDrawable;
        Drawable indicatorBackgroundDrawable;
        if ((changes & 512) != 0) {
            Resources res = getResources();
            int background = style.getOnScreenButtonBackground();
            if (background == 1) {
                ColorFilter controlNormalColorFilter = style.getControlNormalColorFilter();
                rotationButtonDrawable = GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_rotate_white));
                rotationButtonDrawable.setColorFilter(controlNormalColorFilter);
                rotationBackgroundDrawable = createCircleButtonBackground(style);
                indicatorButtonDrawable = GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_play_pause_indiator));
                indicatorButtonDrawable.setColorFilter(controlNormalColorFilter);
                indicatorBackgroundDrawable = setColorTo(GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_circle_button_shape)), style.getFrameColor());
            } else {
                rotationButtonDrawable = GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_rotate));
                rotationBackgroundDrawable = null;
                indicatorButtonDrawable = GraphicUtils.safeMutate(res.getDrawable(R.drawable.ic_play_pause_indicator_circle));
                indicatorBackgroundDrawable = null;
            }
            this._screenRotateButton.setImageDrawable(rotationButtonDrawable);
            this._screenRotateButton.setBackgroundDrawable(rotationBackgroundDrawable);
            this._pauseIndicator.setImageDrawable(indicatorButtonDrawable);
            this._pauseIndicator.setBackgroundDrawable(indicatorBackgroundDrawable);
        } else if ((changes & 200) != 0) {
            int background2 = style.getOnScreenButtonBackground();
            if (background2 == 1) {
                if ((changes & 136) != 0) {
                    this._screenRotateButton.setBackgroundDrawable(createCircleButtonBackground(style));
                }
                if ((changes & 8) != 0 && (d = this._pauseIndicator.getBackground()) != null) {
                    setColorTo(d, style.getFrameColor());
                }
                if ((changes & 64) != 0) {
                    ColorFilter controlNormalColorFilter2 = style.getControlNormalColorFilter();
                    Drawable d2 = this._screenRotateButton.getDrawable();
                    if (d2 != null) {
                        GraphicUtils.safeMutate(d2).setColorFilter(controlNormalColorFilter2);
                    }
                    Drawable d3 = this._pauseIndicator.getDrawable();
                    if (d3 != null) {
                        GraphicUtils.safeMutate(d3).setColorFilter(controlNormalColorFilter2);
                    }
                }
            }
        }
    }

    @Override // com.mxtech.videoplayer.ScreenStyle.OnChangeListener
    public void onStyleChanged(ScreenStyle style, int changes) {
        setStyle(style, changes);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        Tuner tuner = (Tuner) this.dialogRegistry.findByType(Tuner.class);
        if (tuner != null) {
            tuner.onLowMemory();
        }
        super.onLowMemory();
    }
}
