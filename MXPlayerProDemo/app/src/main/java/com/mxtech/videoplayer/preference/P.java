package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import com.mxtech.DateTimeUtils;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.LocaleUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.graphics.Fonts;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.os.Cpu;
import com.mxtech.os.Model;
import com.mxtech.os.SysInfo;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.text.TypefaceUtils;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.ActivityThemed;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.Lock;
import com.mxtech.videoplayer.MediaButtonReceiver;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.list.MediaListFragment;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@SuppressLint({"InlinedApi"})
/* loaded from: classes.dex */
public final class P {
    public static final int AUTO = 2;
    static final float DEFAULT_BRIGHTNESS = 0.5f;
    private static int DEFAULT_FULLSCREEN = 0;
    public static final int DEFAULT_GESTURE_SEEK_SPEED = 10;
    public static final int DEFAULT_INTERFACE_AUDIO_HIDE_DELAY = 2000;
    public static final int DEFAULT_MOVE_INTERVAL = 10;
    private static final int DEFAULT_NEW_MEDIA_PERIOD_MS = 604800000;
    public static final int DEFAULT_OSD_BACK_COLOR = -2013265920;
    public static final int DEFAULT_OSD_TEXT_COLOR = -3355444;
    public static final int DISP_DURATION_AS_TEXT = 3;
    public static final int DISP_DURATION_OVER_THUMB = 2;
    public static final int DISP_NO_DURATION = 1;
    static final String EXT_PREFERENCE_NAME = "video_exts";
    public static final int FIELD_DATE = 4;
    public static final int FIELD_DURATION = 256;
    public static final int FIELD_EXTENSION = 16;
    public static final int FIELD_FRAME_RATE = 128;
    public static final int FIELD_LOCATION = 8;
    public static final int FIELD_PLAYED_TIME = 32;
    public static final int FIELD_RESOLUTION = 64;
    public static final int FIELD_SIZE = 2;
    public static final int FIELD_THUMBNAIL = 1;
    public static final int LIST_ALL_FOLDERS = 1;
    public static final int LIST_FILES = 0;
    public static final int LIST_FOLDERS = 3;
    public static final int LIST_MASK_VIEW_FOLDERS = 1;
    public static int MAX_BOTTOM_PADDING = 0;
    public static final int MAX_SUBTEXT_SIZE = 60;
    public static final float MAX_SUBTITLE_SCALE = 4.0f;
    public static final int MIN_SUBTEXT_SIZE = 16;
    public static final float MIN_SUBTITLE_SCALE = 0.5f;
    public static final int OFF = 1;
    public static final int OMX_CODEC_FLV1 = 2048;
    public static final int OMX_CODEC_H263 = 8;
    public static final int OMX_CODEC_H264 = 2;
    public static final int OMX_CODEC_H264_HI10P = 4;
    public static final int OMX_CODEC_H265 = 1;
    public static final int OMX_CODEC_H265_MAIN10P = 256;
    public static final int OMX_CODEC_MJPEG = 1024;
    public static final int OMX_CODEC_MP43 = 65536;
    public static final int OMX_CODEC_MPEG1 = 4096;
    public static final int OMX_CODEC_MPEG2 = 32;
    public static final int OMX_CODEC_MPEG4 = 16;
    public static final int OMX_CODEC_VC1 = 128;
    public static final int OMX_CODEC_VP8 = 64;
    public static final int OMX_CODEC_VP9 = 512;
    public static final int OMX_CODEC_WMV1 = 8192;
    public static final int OMX_CODEC_WMV2 = 16384;
    public static final int OMX_CODEC_WMV3 = 32768;
    public static final int ON = 0;
    public static final int PREVIOUS_RESTART_THRESHOLD = 3000;
    public static final int REFRESH_BUTTON = 1;
    public static final int REFRESH_SWIPE = 2;
    public static final int SCREEN_ORIENTATION_MATCH_VIDEO = 99999;
    public static final int SCREEN_ORIENTATION_NULL = Integer.MIN_VALUE;
    public static final int SORT_BY_DATE = 4;
    public static final int SORT_BY_DURATION = 64;
    public static final int SORT_BY_EXTENSION = 5;
    public static final int SORT_BY_FRAME_RATE = 32;
    public static final int SORT_BY_LOCATION = 8;
    public static final int SORT_BY_RESOLUTION = 16;
    public static final int SORT_BY_SIZE = 3;
    public static final int SORT_BY_STATUS = 7;
    public static final int SORT_BY_TITLE = 1;
    public static final int SORT_BY_TITLE_NUMERIC = 2;
    public static final int SORT_BY_WATCH_TIME = 6;
    private static final int SORT_DESCNEDING = 128;
    public static final int STEREO_MODE_AUTO_REVERSE_STEREO = 99;
    public static final int STEREO_MODE_MONO = 1;
    public static final int STEREO_MODE_REVERSE_STEREO = 2;
    public static final int STEREO_MODE_STEREO = 0;
    public static final float TEXT_BORDER_THICKNESS_DEFAULT = 0.08f;
    public static final float TEXT_BORDER_THICKNESS_MAX = 0.3f;
    public static final float TEXT_BORDER_THICKNESS_MIN = 0.05f;
    public static final float TEXT_BORDER_THICKNESS_STANDARD = 0.1f;
    public static final float TEXT_BORDER_THICKNESS_UNIT = 0.01f;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE = 1;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE_AFTER_SHOWING_CONTROLLER = 0;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE_WITH_SHOWING_CONTROLLER = 3;
    public static final int TOUCH_ACTION_UI_TOGGLE = 2;
    public static final String VALUE_ASK = "ask";
    public static final String VALUE_RESUME = "resume";
    public static final String VALUE_STARTOVER = "startover";
    private static final int VOLUME_PANEL_VISIBLE_PERIOD = 3750;
    private static Typeface _cachedTypeface = null;
    private static String _cachedTypefaceName = null;
    private static int _cachedTypefaceStyle = 0;
    private static SortedSet<String> _defaultAudioExtensions = null;
    private static int _interfaceAutoHideDelay = 0;
    private static TreeMap<String, Integer> _mediaExts = null;
    private static String[] _nativeLocaleNames = null;
    private static Locale[] _nativeLocales = null;
    private static int _numNativeLocales = 0;
    private static long _volumePanelShowTime = 0;
    public static boolean allowEditing = false;
    public static int audioDelay = 0;
    public static int audioRampUp = 0;
    public static String audioText = null;
    public static boolean autoSelectSubtitle = false;
    public static Integer bluetoothAudioDelay = null;
    public static boolean cacheThumbnail = false;
    private static Locale defaultLocale = null;
    private static int defaultOMXSupportedVideoCodecs = 0;
    private static int defaultOMXSupportedVideoCodecs_includeSWCodecs = 0;
    public static float defaultSubTextSize = 0.0f;
    public static boolean deleteSubtitleFilesTogether = false;
    public static boolean elapsedTimeShowAlways = false;
    public static boolean hasHandsetSoftMenuKey = false;
    public static boolean hasPermanentMenuKey = false;
    public static boolean hasWheelHeuristic = false;
    public static String httpUserAgent = null;
    public static boolean hwAudioTrackSelectable = false;
    public static boolean improve_ssa_rendering = false;
    public static boolean interfaceAutoHide = false;
    public static boolean interfaceHardwareAcceleration = false;
    public static boolean isAudioPlayer = false;
    public static boolean isFlyme = false;
    private static final String kOptButton = "button";
    private static final String kOptSwipe = "swipe";
    public static boolean list_background_load_thumbnail;
    public static boolean list_draw_playtime_over_thumbnail;
    public static int list_duration_display;
    public static int list_fields;
    public static int list_last_media_typeface;
    public static int list_refresh_methods;
    public static boolean list_selection_mode;
    public static int[] list_sortings;
    public static int list_view;
    static ComponentName localAudioDelegateName;
    public static int localVolume;
    public static boolean log_opensubtitles;
    public static String mediaText;
    public static long newTaggedPeriodMs;
    public static boolean oldTablet;
    public static int overVolume;
    public static int playbackKeyUpdownAction;
    public static int playbackTouchAction;
    public static int playbackWheelAction;
    public static boolean playerBackToList;
    public static int playerLooping;
    public static Locale[] preferredAudioLocales;
    public static Locale[] preferredSubtitleLocales;
    public static boolean prevnext_to_rewff;
    public static boolean quickZoom;
    public static boolean rememberPlaybackSelections;
    public static boolean respectMediaButtons;
    public static boolean respectNomedia;
    public static SortedMap<File, Boolean> scanRoots;
    public static float screenBrightness;
    public static boolean screenBrightnessAuto;
    public static int screenOrientation;
    public static boolean scrollDownToLastMedia;
    public static boolean seekPreviews;
    public static int seekPreviewsNetwork;
    public static boolean showHidden;
    public static boolean showInterfaceAtTheStartup;
    public static boolean shuffle;
    public static boolean softAudio;
    public static boolean softAudioLocal;
    public static boolean softAudioNetwork;
    public static int softButtonsVisibility;
    public static int subtitleAlignment;
    public static int subtitleBkColor;
    public static boolean subtitleBkColorEnabled;
    public static int subtitleBorderColor;
    public static int subtitleBottomPaddingDp;
    public static String subtitleCharset;
    public static File subtitleFolder;
    public static String subtitleFontNameOrPath;
    public static float subtitleScale;
    public static int subtitleTextColor;
    public static int subtitleTypefaceStyle;
    public static boolean subtitle_force_ltr;
    public static boolean syncSystemVolume;
    public static boolean tabletFromConfig;
    public static boolean toggleOnMediaPlayButton;
    public static int uiVersion;
    public static String videoText;
    public static boolean videoZoomLimited;
    public static boolean volumeBoost;
    static ComponentName webAudioDelegateName;
    static ComponentName webDelegateName;
    public static final String TAG = App.TAG + ".P";
    public static final FileFilter mediaFileFilter = new FileFilter() { // from class: com.mxtech.videoplayer.preference.P.1
        @Override // java.io.FileFilter
        public boolean accept(File file) {
            String ext;
            if (file.isFile()) {
                if ((P.showHidden || !file.isHidden()) && (ext = FileUtils.getExtension(file)) != null) {
                    return P._mediaExts.containsKey(ext);
                }
                return false;
            }
            return false;
        }
    };
    private static OrderedSharedPreferences.OnSharedPreferenceChangeListener _changeListener = new OrderedSharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.P.2
        @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
            char c = 65535;
            switch (key.hashCode()) {
                case -2130845108:
                    if (key.equals(Key.INTERFACE_AUTO_HIDE)) {
                        c = '/';
                        break;
                    }
                    break;
                case -2126866314:
                    if (key.equals(Key.LIST_SELECTION_MODE)) {
                        c = '5';
                        break;
                    }
                    break;
                case -2014376706:
                    if (key.equals(Key.LIST_REFRESH_METHODS)) {
                        c = '3';
                        break;
                    }
                    break;
                case -1973115562:
                    if (key.equals(Key.SSA_BROKEN_FONT_IGNORE)) {
                        c = 17;
                        break;
                    }
                    break;
                case -1860414862:
                    if (key.equals(Key.VIDEO_ZOOM_LIMITED)) {
                        c = ')';
                        break;
                    }
                    break;
                case -1814521868:
                    if (key.equals(Key.SOFTWARE_AUDIO)) {
                        c = 29;
                        break;
                    }
                    break;
                case -1734179495:
                    if (key.equals(Key.REMEMBER_SELECTIONS)) {
                        c = '!';
                        break;
                    }
                    break;
                case -1661509926:
                    if (key.equals(Key.PLAYBACK_TOUCH_ACTION)) {
                        c = '\"';
                        break;
                    }
                    break;
                case -1648671133:
                    if (key.equals(Key.SUBTITLE_SCALE)) {
                        c = 15;
                        break;
                    }
                    break;
                case -1625193145:
                    if (key.equals(Key.IMPROVE_SSA_RENDERING)) {
                        c = 11;
                        break;
                    }
                    break;
                case -1414326832:
                    if (key.equals(Key.INTERFACE_AUTO_HIDE_DELAY)) {
                        c = '0';
                        break;
                    }
                    break;
                case -1363594690:
                    if (key.equals(Key.VOLUME_BOOST)) {
                        c = '*';
                        break;
                    }
                    break;
                case -1355641911:
                    if (key.equals(Key.BLUETOOTH_AUDIO_DELAY)) {
                        c = ',';
                        break;
                    }
                    break;
                case -1269271051:
                    if (key.equals(Key.LIST_VIEW)) {
                        c = '4';
                        break;
                    }
                    break;
                case -1154108890:
                    if (key.equals(Key.MEDIA_BUTTONS)) {
                        c = '&';
                        break;
                    }
                    break;
                case -1097092248:
                    if (key.equals(Key.LOOP)) {
                        c = 4;
                        break;
                    }
                    break;
                case -1067991150:
                    if (key.equals(Key.SEEK_PREVIEWS)) {
                        c = 7;
                        break;
                    }
                    break;
                case -954424000:
                    if (key.equals(Key.TV_MODE)) {
                        c = 19;
                        break;
                    }
                    break;
                case -865986796:
                    if (key.equals(Key.SCROLL_DOWN_TO_LAST_MEDIA)) {
                        c = 21;
                        break;
                    }
                    break;
                case -701842395:
                    if (key.equals(Key.SUBTITLE_CHARSET)) {
                        c = 22;
                        break;
                    }
                    break;
                case -430050411:
                    if (key.equals(Key.NEW_TAGGED_PERIOD)) {
                        c = '(';
                        break;
                    }
                    break;
                case -422252322:
                    if (key.equals(Key.SOFTWARE_AUDIO_LOCAL)) {
                        c = 30;
                        break;
                    }
                    break;
                case -214468747:
                    if (key.equals(Key.DELETE_SUBTITLE_FILES_TOGETHER)) {
                        c = ' ';
                        break;
                    }
                    break;
                case -20732391:
                    if (key.equals(Key.PREVNEXT_TO_REWFF)) {
                        c = '\'';
                        break;
                    }
                    break;
                case 35704549:
                    if (key.equals(Key.SOFT_MAIN_KEYS)) {
                        c = 26;
                        break;
                    }
                    break;
                case 70025845:
                    if (key.equals(Key.SUBTITLE_FOLDER)) {
                        c = 14;
                        break;
                    }
                    break;
                case 141820665:
                    if (key.equals(Key.SUBTITLE_AUTOLOAD)) {
                        c = '\f';
                        break;
                    }
                    break;
                case 196055551:
                    if (key.equals(Key.INTERFACE_HW_ACCEL)) {
                        c = 28;
                        break;
                    }
                    break;
                case 214489388:
                    if (key.equals(Key.SHOW_HIDDEN)) {
                        c = 23;
                        break;
                    }
                    break;
                case 327429450:
                    if (key.equals(Key.RESPECT_NOMEDIA)) {
                        c = '%';
                        break;
                    }
                    break;
                case 358876298:
                    if (key.equals(Key.BACK_TO_LIST)) {
                        c = 2;
                        break;
                    }
                    break;
                case 360750443:
                    if (key.equals(Key.OMX_VIDEO_CODECS)) {
                        c = 0;
                        break;
                    }
                    break;
                case 568373612:
                    if (key.equals(Key.HTTP_USER_AGENT)) {
                        c = '7';
                        break;
                    }
                    break;
                case 590254188:
                    if (key.equals(Key.SOFT_BUTTONS)) {
                        c = 25;
                        break;
                    }
                    break;
                case 625077054:
                    if (key.equals(Key.PLAYBACK_WHEEL_ACTION)) {
                        c = '$';
                        break;
                    }
                    break;
                case 770703322:
                    if (key.equals(Key.AUDIO_DELAY)) {
                        c = '+';
                        break;
                    }
                    break;
                case 840662004:
                    if (key.equals(Key.PLAYBACK_KEY_UPDOWN_ACTION)) {
                        c = '#';
                        break;
                    }
                    break;
                case 962630559:
                    if (key.equals(Key.SUBTITLE_LANGUAGE)) {
                        c = 16;
                        break;
                    }
                    break;
                case 992811425:
                    if (key.equals(Key.AUDIO_LANGUAGE)) {
                        c = 5;
                        break;
                    }
                    break;
                case 1017897539:
                    if (key.equals(Key.HW_AUDIO_TRACK_SELECTABLE)) {
                        c = '.';
                        break;
                    }
                    break;
                case 1070909549:
                    if (key.equals(Key.TOGGLE_ON_MEDIA_PLAY_BUTTON)) {
                        c = '-';
                        break;
                    }
                    break;
                case 1106724203:
                    if (key.equals(Key.SHOW_INTERFACE_AT_THE_STARTUP)) {
                        c = 27;
                        break;
                    }
                    break;
                case 1153526520:
                    if (key.equals(Key.AUDIO_FADE_IN_ON_SEEK)) {
                        c = '\n';
                        break;
                    }
                    break;
                case 1342504755:
                    if (key.equals(Key.ALLOW_EDITS)) {
                        c = 6;
                        break;
                    }
                    break;
                case 1354101259:
                    if (key.equals(Key.LIST_LAST_MEDIA_TYPEFACE)) {
                        c = '6';
                        break;
                    }
                    break;
                case 1400027106:
                    if (key.equals(Key.AUDIO_FADE_IN_ON_START)) {
                        c = '\t';
                        break;
                    }
                    break;
                case 1498990821:
                    if (key.equals(Key.FONT_FOLDER)) {
                        c = 18;
                        break;
                    }
                    break;
                case 1528361110:
                    if (key.equals(Key.SUBTITLE_FORCE_LTR)) {
                        c = '\r';
                        break;
                    }
                    break;
                case 1632487231:
                    if (key.equals(Key.ANDROID_40_COMPATIBLE_MODE)) {
                        c = 24;
                        break;
                    }
                    break;
                case 1679844069:
                    if (key.equals(Key.QUICK_ZOOM)) {
                        c = '1';
                        break;
                    }
                    break;
                case 1719926895:
                    if (key.equals(Key.CACHE_THUMBNAIL)) {
                        c = '2';
                        break;
                    }
                    break;
                case 1837757078:
                    if (key.equals(Key.LOG_OPENSUBTITLES)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1920544902:
                    if (key.equals(Key.SYNC_SYSTEM_VOLUME)) {
                        c = 20;
                        break;
                    }
                    break;
                case 2072332025:
                    if (key.equals(Key.SHUFFLE)) {
                        c = 3;
                        break;
                    }
                    break;
                case 2082241488:
                    if (key.equals(Key.SOFTWARE_AUDIO_NETWORK)) {
                        c = 31;
                        break;
                    }
                    break;
                case 2096392500:
                    if (key.equals(Key.SEEK_PREVIEWS_NETWORK)) {
                        c = '\b';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    if (App.native_initialized) {
                        FFPlayer.setAllowedOMXCodecs(P.getOMXVideoCodecs());
                        return;
                    }
                    return;
                case 1:
                    P.log_opensubtitles = App.prefs.getBoolean(Key.LOG_OPENSUBTITLES, App.DEBUG);
                    return;
                case 2:
                    P.playerBackToList = App.prefs.getBoolean(Key.BACK_TO_LIST, false);
                    return;
                case 3:
                    P.shuffle = App.prefs.getBoolean(Key.SHUFFLE, false);
                    return;
                case 4:
                    P.playerLooping = App.prefs.getInt(Key.LOOP, 0);
                    return;
                case 5:
                    P.updatePreferredAudioLocales();
                    return;
                case 6:
                    P.allowEditing = prefs.getBoolean(Key.ALLOW_EDITS, true);
                    return;
                case 7:
                    P.seekPreviews = P.getSeekPreviews();
                    return;
                case '\b':
                    P.seekPreviewsNetwork = prefs.getInt(Key.SEEK_PREVIEWS_NETWORK, 2);
                    return;
                case '\t':
                case '\n':
                    P.audioRampUp = P.access$200();
                    return;
                case 11:
                    P.updateImproveSSARendering();
                    return;
                case '\f':
                    P.autoSelectSubtitle = prefs.getBoolean(Key.SUBTITLE_AUTOLOAD, true);
                    return;
                case '\r':
                    P.subtitle_force_ltr = P.access$400();
                    return;
                case 14:
                    P.updateSubtitleFolder();
                    return;
                case 15:
                    P.subtitleScale = prefs.getFloat(Key.SUBTITLE_SCALE, 1.0f);
                    return;
                case 16:
                    P.updatePreferredSubtitleLocales();
                    return;
                case 17:
                case 18:
                    L.updateFontConf(true);
                    return;
                case 19:
                    DeviceUtils.forceTVMode(P.getTVMode());
                    P.updateSyncSystemVolume();
                    return;
                case 20:
                    P.updateSyncSystemVolume();
                    return;
                case 21:
                    P.scrollDownToLastMedia = prefs.getBoolean(key, true);
                    return;
                case 22:
                    P.setSubtitleCharset(prefs.getString(key, ""));
                    return;
                case 23:
                    P.showHidden = prefs.getBoolean(Key.SHOW_HIDDEN, false);
                    return;
                case 24:
                case 25:
                case 26:
                    P.loadUIConfig();
                    return;
                case 27:
                    P.showInterfaceAtTheStartup = P.access$700();
                    return;
                case 28:
                    P.interfaceHardwareAcceleration = prefs.getBoolean(Key.INTERFACE_HW_ACCEL, true);
                    return;
                case 29:
                    P.softAudio = prefs.getBoolean(key, false);
                    return;
                case 30:
                    P.softAudioLocal = prefs.getBoolean(key, false);
                    return;
                case 31:
                    P.softAudioNetwork = prefs.getBoolean(key, false);
                    return;
                case ' ':
                    P.deleteSubtitleFilesTogether = prefs.getBoolean(Key.DELETE_SUBTITLE_FILES_TOGETHER, true);
                    return;
                case '!':
                    P.rememberPlaybackSelections = prefs.getBoolean(Key.REMEMBER_SELECTIONS, true);
                    return;
                case '\"':
                    P.playbackTouchAction = prefs.getInt(key, 2);
                    return;
                case '#':
                    P.playbackKeyUpdownAction = prefs.getInt(key, 1);
                    return;
                case '$':
                    P.playbackWheelAction = prefs.getInt(key, 0);
                    return;
                case '%':
                    P.respectNomedia = prefs.getBoolean(key, true);
                    return;
                case '&':
                    P.respectMediaButtons = prefs.getBoolean(Key.MEDIA_BUTTONS, true);
                    if (!P.respectMediaButtons) {
                        MediaButtonReceiver.unregisterAllReceivers();
                        return;
                    }
                    return;
                case '\'':
                    P.prevnext_to_rewff = prefs.getBoolean(Key.PREVNEXT_TO_REWFF, false);
                    return;
                case '(':
                    P.newTaggedPeriodMs = P.access$800();
                    return;
                case ')':
                    P.videoZoomLimited = P.access$900();
                    return;
                case '*':
                    P.volumeBoost = P.access$1000();
                    if (P.volumeBoost) {
                        P.overVolume = prefs.getInt(Key.OVER_VOLUME, 0);
                        return;
                    } else {
                        P.overVolume = 0;
                        return;
                    }
                case '+':
                    P.updateDefaultAudioOffset();
                    return;
                case ',':
                    P.updateBluetoothAudioOffset();
                    return;
                case '-':
                    P.toggleOnMediaPlayButton = prefs.getBoolean(Key.TOGGLE_ON_MEDIA_PLAY_BUTTON, false);
                    return;
                case '.':
                    P.hwAudioTrackSelectable = P.access$1300();
                    return;
                case '/':
                    P.interfaceAutoHide = prefs.getBoolean(key, true);
                    return;
                case '0':
                    int unused = P._interfaceAutoHideDelay = prefs.getInt(key, 0);
                    return;
                case '1':
                    P.quickZoom = prefs.getBoolean(key, true);
                    return;
                case '2':
                    P.cacheThumbnail = prefs.getBoolean(key, true);
                    return;
                case '3':
                    P.updateListRefreshMethods();
                    return;
                case '4':
                    P.list_view = prefs.getInt(Key.LIST_VIEW, 1);
                    return;
                case '5':
                    P.list_selection_mode = prefs.getBoolean(Key.LIST_SELECTION_MODE, false);
                    return;
                case '6':
                    P.list_last_media_typeface = prefs.getInt(Key.LIST_LAST_MEDIA_TYPEFACE, 0);
                    return;
                case '7':
                    P.httpUserAgent = P.access$1600();
                    return;
                default:
                    return;
            }
        }
    };
    private static String[] EXCLUSIVE_DIRS_REL = {"Ringtones", "Notifications", "media/audio/notifications", "Android/media/com.google.android.talk/Notifications", "Android/media/com.google.android.talk/Ringtones"};
    private static String[] EXCLUSIVE_DIRS_ABS = {"/storage/emulated/legacy", "/mnt/media_rw", "/mnt/shell"};
    private static String COLOR_FORMAT_RGB565 = "rgb565";
    private static String COLOR_FORMAT_RGBX8888 = "rgbx8888";
    private static String COLOR_FORMAT_YV12 = "yv12";

    static /* synthetic */ boolean access$100() {
        return getAndroid40CompatibleMode();
    }

    static /* synthetic */ boolean access$1000() {
        return isVolumeBoostEnabled();
    }

    static /* synthetic */ boolean access$1300() {
        return getHWAudioTrackSelectable();
    }

    static /* synthetic */ String access$1600() {
        return getHttpUserAgent();
    }

    static /* synthetic */ int access$200() {
        return getAudioRampUp();
    }

    static /* synthetic */ boolean access$400() {
        return getSubtitleForceLtr();
    }

    static /* synthetic */ boolean access$700() {
        return getShowInterfaceAtTheStartup();
    }

    static /* synthetic */ long access$800() {
        return getNewTaggedPeriod();
    }

    static /* synthetic */ boolean access$900() {
        return getVideoZoomLimited();
    }

    private static void initDelegateNames(Context pkgContext) {
        webDelegateName = new ComponentName(pkgContext, "com.mxtech.videoplayer.ActivityScreen$WebDelegate");
        webAudioDelegateName = new ComponentName(pkgContext, "com.mxtech.videoplayer.ActivityScreen$WebAudioDelegate");
        localAudioDelegateName = new ComponentName(pkgContext, "com.mxtech.videoplayer.ActivityScreen$LocalAudioDelegate");
    }

    public static Map<String, Integer> copyVideoExts() {
        TreeMap<String, Integer> newMap = new TreeMap<>(_mediaExts.comparator());
        newMap.putAll(_mediaExts);
        return newMap;
    }

    /* loaded from: classes.dex */
    public static class PreferencesSession {
        private final boolean _savedInterfaceHardwareAcceleration = P.interfaceHardwareAcceleration;
        private final boolean _savedAndroid40CompatibleMode = P.access$100();
        private final int _savedTheme = P.getTheme();
        private final int _savedPlaybackTheme = P.getPlaybackTheme();

        private static void recreate(Collection<?> activities) {
            for (Object activity : activities) {
                if (!(activity instanceof ActivityPreferences)) {
                    AppUtils.recreateIfPossible((Activity) activity);
                }
            }
        }

        public void apply() {
            boolean recreateAll = P.interfaceHardwareAcceleration != this._savedInterfaceHardwareAcceleration;
            boolean recreateScreens = (P.access$100() == this._savedAndroid40CompatibleMode && P.getPlaybackTheme() == this._savedPlaybackTheme) ? false : true;
            boolean themeChanged = P.getTheme() != this._savedTheme;
            if (recreateAll) {
                recreate(ActivityRegistry.getAll());
            } else {
                if (recreateScreens) {
                    recreate(ActivityRegistry.findByClass(ActivityScreen.class));
                }
                if (themeChanged) {
                    recreate(ActivityRegistry.findByClass(ActivityThemed.class));
                }
            }
            if (themeChanged) {
                L.thumbCache.clear();
            }
        }
    }

    public static boolean isSupportedProtocol(String protocol) {
        String lowerCase = protocol.toLowerCase(Locale.US);
        char c = 65535;
        switch (lowerCase.hashCode()) {
            case 3143036:
                if (lowerCase.equals(MediaListFragment.TYPE_FILE)) {
                    c = 2;
                    break;
                }
                break;
            case 3213448:
                if (lowerCase.equals("http")) {
                    c = 0;
                    break;
                }
                break;
            case 99617003:
                if (lowerCase.equals("https")) {
                    c = 1;
                    break;
                }
                break;
            case 951530617:
                if (lowerCase.equals("content")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
                return true;
            default:
                return false;
        }
    }

    public static int getVideoZoomDelay() {
        String str = App.prefs.getString(Key.VIDEO_ZOOM_DELAY, null);
        if (str != null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
            }
        }
        if (Build.VERSION.SDK_INT < 14 && (Model.model == 10 || Model.model == 20)) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putString(Key.VIDEO_ZOOM_DELAY, "200");
            AppUtils.apply(editor);
            return 200;
        }
        return 0;
    }

    private static boolean getVideoZoomLimited() {
        if (App.prefs.contains(Key.VIDEO_ZOOM_LIMITED)) {
            return App.prefs.getBoolean(Key.VIDEO_ZOOM_LIMITED, false);
        }
        if (Model.model == 80) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.VIDEO_ZOOM_LIMITED, true);
            AppUtils.apply(editor);
            return false;
        }
        return false;
    }

    private static boolean isAudioPlayer() {
        return App.context.getPackageManager().getComponentEnabledSetting(localAudioDelegateName) == 1;
    }

    public static boolean isPlayLink() {
        int state = App.context.getPackageManager().getComponentEnabledSetting(webDelegateName);
        return state == 1 || state == 0;
    }

    public static void init(Context context) {
        OrderedSharedPreferences prefs = App.prefs;
        Resources res = context.getResources();
        log_opensubtitles = App.prefs.getBoolean(Key.LOG_OPENSUBTITLES, App.DEBUG);
        httpUserAgent = getHttpUserAgent();
        playerBackToList = App.prefs.getBoolean(Key.BACK_TO_LIST, false);
        shuffle = App.prefs.getBoolean(Key.SHUFFLE, false);
        playerLooping = App.prefs.getInt(Key.LOOP, 0);
        loadSortingRules();
        list_fields = getListDisplayFields();
        list_draw_playtime_over_thumbnail = prefs.getBoolean(Key.LIST_DRAW_PLAYTIME_OVER_THUMBNAIL, true);
        updateDispDuration();
        list_view = prefs.getInt(Key.LIST_VIEW, 1);
        list_background_load_thumbnail = SysInfo.isFast();
        list_selection_mode = prefs.getBoolean(Key.LIST_SELECTION_MODE, false);
        list_last_media_typeface = prefs.getInt(Key.LIST_LAST_MEDIA_TYPEFACE, 0);
        updateListRefreshMethods();
        allowEditing = App.prefs.getBoolean(Key.ALLOW_EDITS, true);
        newTaggedPeriodMs = getNewTaggedPeriod();
        deleteSubtitleFilesTogether = App.prefs.getBoolean(Key.DELETE_SUBTITLE_FILES_TOGETHER, true);
        audioRampUp = getAudioRampUp();
        scrollDownToLastMedia = App.prefs.getBoolean(Key.SCROLL_DOWN_TO_LAST_MEDIA, false);
        respectMediaButtons = prefs.getBoolean(Key.MEDIA_BUTTONS, true);
        prevnext_to_rewff = prefs.getBoolean(Key.PREVNEXT_TO_REWFF, false);
        toggleOnMediaPlayButton = prefs.getBoolean(Key.TOGGLE_ON_MEDIA_PLAY_BUTTON, false);
        initDelegateNames(context.getApplicationContext());
        if (Build.VERSION.SDK_INT >= 11) {
            interfaceHardwareAcceleration = prefs.getBoolean(Key.INTERFACE_HW_ACCEL, true);
        } else {
            interfaceHardwareAcceleration = false;
        }
        setUseAsAudioPlayer(isAudioPlayer());
        cacheThumbnail = prefs.getBoolean(Key.CACHE_THUMBNAIL, true);
        MAX_BOTTOM_PADDING = (((int) DeviceUtils.PixelToDIP((DeviceUtils.smallestPixels * 2) / 5)) / 10) * 10;
        isFlyme = Build.DISPLAY.startsWith("Flyme");
        updateSubtitleFolder();
        defaultSubTextSize = getDefaultSubTextSize(res);
        subtitleScale = prefs.getFloat(Key.SUBTITLE_SCALE, 1.0f);
        screenBrightnessAuto = prefs.getBoolean(Key.SCREEN_BRIGHTNESS_AUTO, false);
        screenBrightness = prefs.getFloat(Key.SCREEN_BRIGHTNESS, 0.5f);
        volumeBoost = isVolumeBoostEnabled();
        if (volumeBoost) {
            overVolume = prefs.getInt(Key.OVER_VOLUME, 0);
        }
        updateSyncSystemVolume();
        updateDefaultAudioOffset();
        updateBluetoothAudioOffset();
        videoZoomLimited = getVideoZoomLimited();
        quickZoom = prefs.getBoolean(Key.QUICK_ZOOM, true);
        if (DeviceUtils.isTV) {
            screenOrientation = 10;
        } else {
            screenOrientation = prefs.getInt(Key.SCREEN_ORIENTATION, SCREEN_ORIENTATION_MATCH_VIDEO);
        }
        autoSelectSubtitle = prefs.getBoolean(Key.SUBTITLE_AUTOLOAD, true);
        subtitle_force_ltr = getSubtitleForceLtr();
        subtitleTextColor = prefs.getInt(Key.SUBTITLE_TEXT_COLOR, -1);
        subtitleBorderColor = prefs.getInt(Key.SUBTITLE_BORDER_COLOR, ViewCompat.MEASURED_STATE_MASK);
        subtitleFontNameOrPath = prefs.getString(Key.SUBTITLE_FONT_NAME, null);
        subtitleTypefaceStyle = prefs.getInt(Key.SUBTITLE_FONT_STYLE, 1);
        subtitleAlignment = prefs.getInt(Key.SUBTITLE_ALIGNMENT, 1);
        if (prefs.contains(Key.SUBTITLE_BKCOLOR)) {
            subtitleBkColor = prefs.getInt(Key.SUBTITLE_BKCOLOR, 0);
        } else {
            subtitleBkColor = res.getColor(R.color.default_subtitle_bkcolor);
        }
        subtitleBkColorEnabled = Color.alpha(subtitleBkColor) != 0 && prefs.getBoolean(Key.SUBTITLE_BKCOLOR_ENABLED, false);
        subtitleBottomPaddingDp = getSubtitleBottomPaddingDp();
        elapsedTimeShowAlways = prefs.getBoolean(Key.ELAPSED_TIME_SHOW_ALWAYS, false);
        softAudio = prefs.getBoolean(Key.SOFTWARE_AUDIO, false);
        softAudioLocal = prefs.getBoolean(Key.SOFTWARE_AUDIO_LOCAL, false);
        softAudioNetwork = prefs.getBoolean(Key.SOFTWARE_AUDIO_NETWORK, false);
        setSubtitleCharset(prefs.getString(Key.SUBTITLE_CHARSET, ""));
        updateImproveSSARendering();
        seekPreviews = getSeekPreviews();
        seekPreviewsNetwork = prefs.getInt(Key.SEEK_PREVIEWS_NETWORK, 2);
        hwAudioTrackSelectable = getHWAudioTrackSelectable();
        if (prefs.getInt(Key.SCAN_ROOTS_VERSION, 0) != Build.VERSION.SDK_INT) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(Key.SCAN_ROOTS);
            editor.putInt(Key.SCAN_ROOTS_VERSION, Build.VERSION.SDK_INT);
            AppUtils.apply(editor);
        }
        String roots = prefs.getString(Key.SCAN_ROOTS, null);
        if (roots == null) {
            scanRoots = getDefaultScanRoots();
        } else {
            String[] rootPathsEncoded = roots.split(File.pathSeparator);
            scanRoots = new TreeMap(FileUtils.CASE_INSENSITIVE_ORDER);
            for (String encodedPath : rootPathsEncoded) {
                int len = encodedPath.length();
                if (len > 0) {
                    boolean visible = encodedPath.charAt(0) != '!';
                    if (!visible) {
                        encodedPath = encodedPath.substring(1);
                    }
                    scanRoots.put(new File(Uri.decode(encodedPath)), Boolean.valueOf(visible));
                }
            }
        }
        respectNomedia = prefs.getBoolean(Key.RESPECT_NOMEDIA, true);
        showHidden = prefs.getBoolean(Key.SHOW_HIDDEN, false);
        playbackTouchAction = prefs.getInt(Key.PLAYBACK_TOUCH_ACTION, 2);
        playbackKeyUpdownAction = prefs.getInt(Key.PLAYBACK_KEY_UPDOWN_ACTION, 1);
        playbackWheelAction = prefs.getInt(Key.PLAYBACK_WHEEL_ACTION, 0);
        rememberPlaybackSelections = prefs.getBoolean(Key.REMEMBER_SELECTIONS, true);
        initVideoExtensionList();
        loadUIConfig();
        interfaceAutoHide = prefs.getBoolean(Key.INTERFACE_AUTO_HIDE, true);
        _interfaceAutoHideDelay = prefs.getInt(Key.INTERFACE_AUTO_HIDE_DELAY, 2000);
        showInterfaceAtTheStartup = getShowInterfaceAtTheStartup();
        updatePreferredAudioLocales();
        updatePreferredSubtitleLocales();
        App.prefs.registerOnSharedPreferenceChangeListener(_changeListener);
    }

    private static int getAudioRampUp() {
        return (App.prefs.getBoolean(Key.AUDIO_FADE_IN_ON_START, true) ? 1 : 0) | (App.prefs.getBoolean(Key.AUDIO_FADE_IN_ON_SEEK, true) ? 2 : 0);
    }

    public static boolean getTVMode() {
        return App.prefs.contains(Key.TV_MODE) ? App.prefs.getBoolean(Key.TV_MODE, false) : App.context.getResources().getBoolean(R.bool.default_tv_mode);
    }

    public static String getThemeName() {
        String theme = App.prefs.getString(Key.LIST_THEME, null);
        return (theme == null || theme.length() <= 0) ? App.context.getString(R.string.default_theme) : theme;
    }

    public static int getTheme() {
        return getThemeStyleId(getThemeName());
    }

    public static int getThemeStyleId(String theme) {
        char c = 65535;
        switch (theme.hashCode()) {
            case -2042902176:
                if (theme.equals("black_brownAccent")) {
                    c = 20;
                    break;
                }
                break;
            case -1852469876:
                if (theme.equals("dark_gray")) {
                    c = 5;
                    break;
                }
                break;
            case -1852277025:
                if (theme.equals("dark_navy")) {
                    c = 7;
                    break;
                }
                break;
            case -1591991258:
                if (theme.equals("dark_gray2")) {
                    c = 6;
                    break;
                }
                break;
            case -1590166554:
                if (theme.equals("black_purpleAccent")) {
                    c = 24;
                    break;
                }
                break;
            case -1184235822:
                if (theme.equals("indigo")) {
                    c = '\b';
                    break;
                }
                break;
            case -1008851410:
                if (theme.equals("orange")) {
                    c = 14;
                    break;
                }
                break;
            case -976943172:
                if (theme.equals("purple")) {
                    c = '\f';
                    break;
                }
                break;
            case -780781233:
                if (theme.equals("fl_pink")) {
                    c = 11;
                    break;
                }
                break;
            case -120534404:
                if (theme.equals("black_indigoAccent")) {
                    c = 23;
                    break;
                }
                break;
            case 112785:
                if (theme.equals("red")) {
                    c = '\t';
                    break;
                }
                break;
            case 3027034:
                if (theme.equals("blue")) {
                    c = 3;
                    break;
                }
                break;
            case 3441014:
                if (theme.equals("pink")) {
                    c = '\n';
                    break;
                }
                break;
            case 93818879:
                if (theme.equals("black")) {
                    c = 2;
                    break;
                }
                break;
            case 94011702:
                if (theme.equals("brown")) {
                    c = 4;
                    break;
                }
                break;
            case 98619139:
                if (theme.equals("green")) {
                    c = '\r';
                    break;
                }
                break;
            case 113101865:
                if (theme.equals("white")) {
                    c = 1;
                    break;
                }
                break;
            case 210485371:
                if (theme.equals("black_redAccent")) {
                    c = 15;
                    break;
                }
                break;
            case 1078876857:
                if (theme.equals("black_fl_pinkAccent")) {
                    c = 17;
                    break;
                }
                break;
            case 1446103405:
                if (theme.equals("black_greenAccent")) {
                    c = 21;
                    break;
                }
                break;
            case 1786955268:
                if (theme.equals("black_blueAccent")) {
                    c = 22;
                    break;
                }
                break;
            case 1878446624:
                if (theme.equals("black_pinkAccent")) {
                    c = 16;
                    break;
                }
                break;
            case 1913191640:
                if (theme.equals("black_orangeAccent")) {
                    c = 18;
                    break;
                }
                break;
            case 2100620958:
                if (theme.equals("black_yellowAccent")) {
                    c = 19;
                    break;
                }
                break;
        }
        switch (c) {
            case 2:
                return R.style.BlackTheme;
            case 3:
                return R.style.BlueTheme;
            case 4:
                return R.style.BrownTheme;
            case 5:
                return R.style.DarkGrayTheme;
            case 6:
                return R.style.DarkGray2Theme;
            case 7:
                return R.style.DarkNavyTheme;
            case '\b':
                return R.style.IndigoTheme;
            case '\t':
                return R.style.RedTheme;
            case '\n':
                return R.style.PinkTheme;
            case 11:
                return R.style.FLPinkTheme;
            case '\f':
                return R.style.PurpleTheme;
            case '\r':
                return R.style.GreenTheme;
            case 14:
                return R.style.OrangeTheme;
            case 15:
                return R.style.BlackTheme_RedAccent;
            case 16:
                return R.style.BlackTheme_PinkAccent;
            case 17:
                return R.style.BlackTheme_FLPinkAccent;
            case 18:
                return R.style.BlackTheme_OrangeAccent;
            case 19:
                return R.style.BlackTheme_YellowAccent;
            case 20:
                return R.style.BlackTheme_BrownAccent;
            case 21:
                return R.style.BlackTheme_GreenAccent;
            case 22:
                return R.style.BlackTheme_BlueAccent;
            case 23:
                return R.style.BlackTheme_IndigoAccent;
            case 24:
                return R.style.BlackTheme_PurpleAccent;
            default:
                return R.style.WhiteTheme;
        }
    }

    public static String getPlaybackThemeName() {
        String theme = App.prefs.getString(Key.PLAYBACK_THEME, null);
        return (theme == null || theme.length() <= 0) ? App.context.getString(R.string.default_playback_theme) : theme;
    }

    public static int getPlaybackTheme() {
        return getPlaybackThemeStyleId(getPlaybackThemeName());
    }

    public static int getPlaybackThemeStyleId(String theme) {
        char c = 65535;
        switch (theme.hashCode()) {
            case -2042902176:
                if (theme.equals("black_brownAccent")) {
                    c = 17;
                    break;
                }
                break;
            case -1852469876:
                if (theme.equals("dark_gray")) {
                    c = 3;
                    break;
                }
                break;
            case -1852277025:
                if (theme.equals("dark_navy")) {
                    c = 4;
                    break;
                }
                break;
            case -1590166554:
                if (theme.equals("black_purpleAccent")) {
                    c = 22;
                    break;
                }
                break;
            case -1184235822:
                if (theme.equals("indigo")) {
                    c = 5;
                    break;
                }
                break;
            case -1008851410:
                if (theme.equals("orange")) {
                    c = 11;
                    break;
                }
                break;
            case -976943172:
                if (theme.equals("purple")) {
                    c = '\t';
                    break;
                }
                break;
            case -780781233:
                if (theme.equals("fl_pink")) {
                    c = '\b';
                    break;
                }
                break;
            case -120534404:
                if (theme.equals("black_indigoAccent")) {
                    c = 21;
                    break;
                }
                break;
            case 112785:
                if (theme.equals("red")) {
                    c = 6;
                    break;
                }
                break;
            case 3441014:
                if (theme.equals("pink")) {
                    c = 7;
                    break;
                }
                break;
            case 93818879:
                if (theme.equals("black")) {
                    c = 1;
                    break;
                }
                break;
            case 94011702:
                if (theme.equals("brown")) {
                    c = 2;
                    break;
                }
                break;
            case 98619139:
                if (theme.equals("green")) {
                    c = '\n';
                    break;
                }
                break;
            case 113101865:
                if (theme.equals("white")) {
                    c = 0;
                    break;
                }
                break;
            case 210485371:
                if (theme.equals("black_redAccent")) {
                    c = '\f';
                    break;
                }
                break;
            case 1078876857:
                if (theme.equals("black_fl_pinkAccent")) {
                    c = 14;
                    break;
                }
                break;
            case 1446103405:
                if (theme.equals("black_greenAccent")) {
                    c = 18;
                    break;
                }
                break;
            case 1786955268:
                if (theme.equals("black_blueAccent")) {
                    c = 20;
                    break;
                }
                break;
            case 1878446624:
                if (theme.equals("black_pinkAccent")) {
                    c = '\r';
                    break;
                }
                break;
            case 1913191640:
                if (theme.equals("black_orangeAccent")) {
                    c = 15;
                    break;
                }
                break;
            case 2100620958:
                if (theme.equals("black_yellowAccent")) {
                    c = 16;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return R.style.PlaybackWhiteTheme;
            case 1:
                return R.style.PlaybackBlackTheme;
            case 2:
                return R.style.PlaybackBrownTheme;
            case 3:
                return R.style.PlaybackDarkTheme;
            case 4:
                return R.style.PlaybackDarkNavyTheme;
            case 5:
                return R.style.PlaybackIndigoTheme;
            case 6:
                return R.style.PlaybackRedTheme;
            case 7:
                return R.style.PlaybackPinkTheme;
            case '\b':
                return R.style.PlaybackFLPinkTheme;
            case '\t':
                return R.style.PlaybackPurpleTheme;
            case '\n':
                return R.style.PlaybackGreenTheme;
            case 11:
                return R.style.PlaybackOrangeTheme;
            case '\f':
                return R.style.PlaybackBlackTheme_RedAccent;
            case '\r':
                return R.style.PlaybackBlackTheme_PinkAccent;
            case 14:
                return R.style.PlaybackBlackTheme_FLPinkAccent;
            case 15:
                return R.style.PlaybackBlackTheme_OrangeAccent;
            case 16:
                return R.style.PlaybackBlackTheme_YellowAccent;
            case 17:
                return R.style.PlaybackBlackTheme_BrownAccent;
            case 18:
                return R.style.PlaybackBlackTheme_GreenAccent;
            case 19:
            case 20:
            default:
                return R.style.PlaybackBlackTheme_BlueAccent;
            case 21:
                return R.style.PlaybackBlackTheme_IndigoAccent;
            case 22:
                return R.style.PlaybackBlackTheme_PurpleAccent;
        }
    }

    private static long getNewTaggedPeriod() {
        String period = App.prefs.getString(Key.NEW_TAGGED_PERIOD, null);
        if (period != null) {
            try {
                return Long.parseLong(period) * 1000 * 60 * 60 * 24;
            } catch (NumberFormatException e) {
            }
        }
        return DateTimeUtils.WEEK;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateDefaultAudioOffset() {
        audioDelay = (int) (App.prefs.getFloat(Key.AUDIO_DELAY, 0.0f) * 1000.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateBluetoothAudioOffset() {
        if (Build.VERSION.SDK_INT >= 11 && App.prefs.contains(Key.BLUETOOTH_AUDIO_DELAY) && BluetoothAdapter.getDefaultAdapter() != null) {
            bluetoothAudioDelay = Integer.valueOf((int) (App.prefs.getFloat(Key.BLUETOOTH_AUDIO_DELAY, 0.0f) * 1000.0f));
        } else {
            bluetoothAudioDelay = null;
        }
    }

    public static void setUseAsAudioPlayer(boolean useAsAudioPlayer) {
        isAudioPlayer = useAsAudioPlayer;
        Locale defaultLocale2 = Locale.getDefault();
        videoText = App.context.getString(R.string.detail_group_video).toLowerCase(defaultLocale2);
        audioText = App.context.getString(R.string.audio).toLowerCase(defaultLocale2);
        if (isAudioPlayer) {
            mediaText = App.context.getString(R.string.detail_group_media).toLowerCase(defaultLocale2);
        } else {
            mediaText = videoText;
        }
        if (L.observer != null) {
            L.observer.resetAsync();
        }
    }

    private static int getListDisplayFields() {
        if (App.prefs.contains(Key.LIST_FIELDS)) {
            return App.prefs.getInt(Key.LIST_FIELDS, 0);
        }
        if (App.prefs.contains(Key.LIST_FIELDS_OBSOLETE)) {
            return App.prefs.getInt(Key.LIST_FIELDS_OBSOLETE, 0) | 256;
        }
        return App.prefs.getBoolean(Key.LIST_THUMBNAIL_OBSOLETE, true) ? 257 : 256;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateListRefreshMethods() {
        if (DeviceUtils.hasTouchScreen) {
            Collection<String> methodSet = App.prefs.getStringSet(Key.LIST_REFRESH_METHODS, null);
            if (methodSet == null) {
                methodSet = Arrays.asList(App.context.getResources().getStringArray(R.array.list_refresh_method_default));
            }
            list_refresh_methods = 0;
            for (String m : methodSet) {
                if (kOptButton.equals(m)) {
                    list_refresh_methods |= 1;
                } else if (kOptSwipe.equals(m)) {
                    list_refresh_methods |= 2;
                }
            }
            return;
        }
        list_refresh_methods = 1;
    }

    public static void updateDispDuration() {
        int newDisp;
        int oldDisp = list_duration_display;
        if ((list_fields & 256) != 0) {
            if ((list_fields & 1) != 0 && list_draw_playtime_over_thumbnail) {
                newDisp = 2;
            } else {
                newDisp = 3;
            }
        } else {
            newDisp = 1;
        }
        if (oldDisp != newDisp) {
            list_duration_display = newDisp;
            if (oldDisp == 2 || newDisp == 2) {
                L.thumbCache.clear();
            }
        }
    }

    public static int[] getFullSortingRule(int sorting) {
        switch (sorting) {
            case -64:
            case 64:
                return new int[]{-64, 2, 7, -6};
            case -32:
            case 32:
                return new int[]{-32, -16, 2, 7, -6};
            case -16:
            case 16:
                return new int[]{-16, -32, 2, 7, -6};
            case -8:
            case 8:
                return new int[]{8};
            case -7:
            case 7:
                return new int[]{7, 2, -6};
            case -6:
            case 6:
                return new int[]{-6, 2, 7};
            case -5:
            case 5:
                return new int[]{5, 2, 7, -6};
            case IMediaPlayer.ERESTART /* -4 */:
            case 4:
                return new int[]{-4, 2, 7, -6};
            case IMediaPlayer.EFAIL /* -3 */:
            case 3:
                return new int[]{-3, 2, 7, -6};
            default:
                return new int[]{2, 7, -6};
        }
    }

    private static void loadSortingRules() {
        long encodedSorting = App.prefs.getLong(Key.LIST_SORTS, 0L);
        if (encodedSorting != 0) {
            int numMethods = 0;
            while (numMethods < 8 && (encodedSorting >> (numMethods * 8)) != 0) {
                numMethods++;
            }
            list_sortings = new int[numMethods];
            for (int i = 0; i < numMethods; i++) {
                int sorting = (int) ((encodedSorting >> (i * 8)) & 255);
                if ((sorting & 128) != 0) {
                    sorting = -(sorting & (-129));
                }
                list_sortings[i] = sorting;
            }
            return;
        }
        list_sortings = getFullSortingRule(2);
    }

    public static void changeSortingRules(int[] sortings) {
        SharedPreferences.Editor editor = App.prefs.edit();
        changeSortingRules(sortings, editor);
        AppUtils.apply(editor);
    }

    public static void changeSortingRules(int[] sortings, SharedPreferences.Editor edit) {
        list_sortings = sortings;
        long encodedSorting = 0;
        for (int i = 0; i < list_sortings.length; i++) {
            int sorting = list_sortings[i];
            if (sorting < 0) {
                sorting = (-sorting) | 128;
            }
            encodedSorting |= sorting << (i * 8);
        }
        edit.putLong(Key.LIST_SORTS, encodedSorting);
    }

    public static void setListView(int view) {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putInt(Key.LIST_VIEW, view);
        AppUtils.apply(editor);
    }

    private static boolean getShowInterfaceAtTheStartup() {
        if (App.prefs.contains(Key.SHOW_INTERFACE_AT_THE_STARTUP)) {
            return App.prefs.getBoolean(Key.SHOW_INTERFACE_AT_THE_STARTUP, false);
        }
        return Model.manufacturer == 10110 && Cpu.family == 2 && Build.VERSION.SDK_INT >= 18;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateImproveSSARendering() {
        improve_ssa_rendering = App.prefs.getBoolean(Key.IMPROVE_SSA_RENDERING, false);
    }

    @SuppressLint({"NewApi"})
    private static boolean getHWAudioTrackSelectable() {
        if (Build.VERSION.SDK_INT < 16) {
            return false;
        }
        if (App.prefs.contains(Key.HW_AUDIO_TRACK_SELECTABLE)) {
            return App.prefs.getBoolean(Key.HW_AUDIO_TRACK_SELECTABLE, false);
        }
        boolean support = Build.VERSION.SDK_INT >= 18;
        if (Build.BRAND.equalsIgnoreCase("google")) {
            support = true;
        } else if (Cpu.family == 2) {
            support = true;
        } else {
            switch (Model.manufacturer) {
                case 10000:
                    if (Build.VERSION.SDK_INT >= 18) {
                        support = true;
                        break;
                    }
                    break;
                case Model.HTC /* 10040 */:
                case Model.ROCKCHIP /* 10090 */:
                case Model.MEIZU /* 10120 */:
                    support = true;
                    break;
            }
        }
        if (support) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.HW_AUDIO_TRACK_SELECTABLE, true);
            editor.apply();
            return true;
        }
        return false;
    }

    @TargetApi(14)
    private static boolean getHasPermanentMenuKey(Context context) {
        boolean defaultPermanentKey;
        if (App.prefs.contains(Key.SOFT_MAIN_KEYS)) {
            return !App.prefs.getBoolean(Key.SOFT_MAIN_KEYS, false);
        }
        switch (Model.model) {
            case 42:
            case 1004:
            case 1005:
            case 1006:
            case 1007:
            case 1008:
            case 1009:
            case 1010:
            case 1011:
            case 1012:
            case 1013:
            case 1014:
            case Model.HTC_BUTTERFLY /* 1100 */:
            case Model.HTC_ONE /* 1200 */:
                defaultPermanentKey = true;
                break;
            case Model.OPTIMUS_G2 /* 181 */:
            case 200:
            case Model.VEGA_RACER2 /* 230 */:
            case Model.VEGA_S5 /* 231 */:
            case Model.VEGA_R3 /* 232 */:
            case Model.VEGA_NO6 /* 233 */:
            case 260:
            case Model.STUTTGART /* 270 */:
                defaultPermanentKey = false;
                break;
            default:
                defaultPermanentKey = ViewConfiguration.get(context).hasPermanentMenuKey();
                break;
        }
        if (!defaultPermanentKey) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.SOFT_MAIN_KEYS, true);
            editor.apply();
        }
        return defaultPermanentKey;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loadUIConfig() {
        Resources res = App.context.getResources();
        uiVersion = getAndroid40CompatibleMode() ? 15 : Build.VERSION.SDK_INT;
        oldTablet = DeviceUtils.isOldTabletFromDeviceConfig(App.context, uiVersion);
        tabletFromConfig = DeviceUtils.isTabletFromDeviceConfig(res, uiVersion);
        if (Build.VERSION.SDK_INT >= 14) {
            hasPermanentMenuKey = getHasPermanentMenuKey(App.context);
            hasHandsetSoftMenuKey = (hasPermanentMenuKey || oldTablet) ? false : true;
        } else {
            hasPermanentMenuKey = Build.VERSION.SDK_INT < 11;
            hasHandsetSoftMenuKey = false;
        }
        updateSoftButtonsVisibility();
        DEFAULT_FULLSCREEN = getDefaultFullScreen(res);
    }

    @SuppressLint({"NewApi"})
    private static boolean getAndroid40CompatibleMode() {
        if (Build.VERSION.SDK_INT < 16) {
            return false;
        }
        if (App.prefs.contains(Key.ANDROID_40_COMPATIBLE_MODE)) {
            return App.prefs.getBoolean(Key.ANDROID_40_COMPATIBLE_MODE, false);
        }
        if (isFlyme) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.ANDROID_40_COMPATIBLE_MODE, true);
            editor.apply();
            return true;
        }
        return false;
    }

    private static int getDefaultFullScreen(Resources res) {
        if (uiVersion < 11 || Model.manufacturer != 10070) {
            return (uiVersion < 16 || oldTablet) ? 0 : 2;
        }
        return 2;
    }

    public static int getFullScreen() {
        if (oldTablet) {
            return 0;
        }
        return App.prefs.getInt(Key.FULLSCREEN, DEFAULT_FULLSCREEN);
    }

    public static boolean getBatteryClockInTitleBar() {
        return App.prefs.getBoolean(Key.BATTERY_CLOCK_IN_TITLE_BAR, !oldTablet && DEFAULT_FULLSCREEN == 0);
    }

    private static void loadLocalVolume() {
        localVolume = App.prefs.getInt(Key.LOCAL_VOLUME, -1);
        if (localVolume < 0 || localVolume > L.maxAudioVolumeLevel) {
            localVolume = L.maxAudioVolumeLevel;
        }
    }

    private static boolean isVolumeBoostEnabled() {
        return App.prefs.contains(Key.VOLUME_BOOST) ? App.prefs.getBoolean(Key.VOLUME_BOOST, true) : App.context.getResources().getBoolean(R.bool.default_volume_boost);
    }

    public static int getVolume() {
        int volume = syncSystemVolume ? L.audioManager.getStreamVolume(3) : localVolume;
        if (volume < L.maxAudioVolumeLevel) {
            return volume;
        }
        return L.maxAudioVolumeLevel + overVolume;
    }

    private static void updateSoftButtonsVisibility() {
        if (!hasHandsetSoftMenuKey) {
            softButtonsVisibility = 0;
        } else if (App.prefs.contains(Key.SOFT_BUTTONS)) {
            softButtonsVisibility = App.prefs.getInt(Key.SOFT_BUTTONS, 2);
            if (softButtonsVisibility == 1 && uiVersion < 19) {
                Log.d(TAG, "Overriding soft buttons visibility: OFF -> AUTO");
                softButtonsVisibility = 2;
            }
        } else if (App.prefs.contains(Key.AUTO_HIDE_SOFT_BUTTONS)) {
            softButtonsVisibility = App.prefs.getBoolean(Key.AUTO_HIDE_SOFT_BUTTONS, Model.manufacturer != 10070) ? 2 : 0;
        } else {
            softButtonsVisibility = 2;
        }
    }

    public static void onAcquiredStorageWritePermission() {
        String roots = App.prefs.getString(Key.SCAN_ROOTS, null);
        if (roots == null) {
            scanRoots = getDefaultScanRoots();
        }
        updateSubtitleFolder();
    }

    @SuppressLint({"NewApi"})
    private static SortedMap<File, Boolean> getDefaultScanRoots() {
        File mountRoot;
        String[] strArr;
        String[] strArr2;
        File[] storageFiles;
        SortedMap<File, Boolean> roots = new TreeMap<>(FileUtils.CASE_INSENSITIVE_ORDER);
        File externalStorage = Environment.getExternalStorageDirectory();
        String externalStoragePath = externalStorage.getPath();
        String externalStorageCanonPath = FileUtils.getCanonicalOrAbsolutePath(externalStorage);
        if (Build.VERSION.SDK_INT >= 11) {
            Log.v(TAG, "Primary-external-storage: " + externalStoragePath + " (Emulated: " + Environment.isExternalStorageEmulated() + " Canonical-path: " + FileUtils.getCanonicalOrAbsolutePath(externalStorage) + ")");
        }
        int secondSeparator = externalStoragePath.indexOf(File.separatorChar, 1);
        if (secondSeparator > 0) {
            mountRoot = new File(externalStoragePath.substring(0, secondSeparator));
        } else {
            mountRoot = externalStorage;
        }
        for (String rel_path : EXCLUSIVE_DIRS_REL) {
            File exclusive = new File(externalStorage.getPath() + '/' + rel_path);
            if (exclusive.isDirectory()) {
                roots.put(exclusive, false);
            }
        }
        for (String abs_path : EXCLUSIVE_DIRS_ABS) {
            File exclusive2 = new File(abs_path);
            if (exclusive2.isDirectory()) {
                roots.put(exclusive2, false);
            }
        }
        if (!externalStorageCanonPath.equals(externalStoragePath)) {
            roots.put(new File(externalStorageCanonPath), false);
        }
        File mntDir = new File("/mnt");
        if (externalStoragePath.startsWith("/storage/emulated") && (storageFiles = FileUtils.listFiles(new File("/storage"))) != null) {
            for (File file : storageFiles) {
                if (file.isDirectory() && !"emulated".equals(file.getName())) {
                    String canonPath = FileUtils.getCanonicalOrAbsolutePath(file);
                    if (canonPath.startsWith("/storage/emulated")) {
                        Log.v(TAG, "Exclude " + file + " as it is symbolic link to " + canonPath);
                        roots.put(file, false);
                    } else {
                        Log.v(TAG, file + " --> " + canonPath);
                    }
                }
            }
        }
        File[] mntFiles = FileUtils.listFiles(mntDir);
        if (mntFiles != null) {
            for (File file2 : mntFiles) {
                if (file2.isDirectory()) {
                    String canonPath2 = FileUtils.getCanonicalOrAbsolutePath(file2);
                    Log.v(TAG, file2 + " --> " + canonPath2);
                    if (FileUtils.isLocatedIn(canonPath2, "/storage") && FileUtils.isLocatedIn(roots.keySet(), canonPath2)) {
                        Log.v(TAG, "Exclude " + file2 + " as it is symbolic link to " + canonPath2);
                        roots.put(file2, false);
                    }
                }
            }
        }
        roots.put(mntDir, true);
        roots.put(mountRoot, true);
        if (Build.VERSION.SDK_INT >= 23 || !FileUtils.isLocatedIn(roots, externalStorage.getPath())) {
            roots.put(externalStorage, true);
        }
        File removable = new File("/Removable");
        if (removable.isDirectory()) {
            if (!removable.getPath().equals(FileUtils.getCanonicalOrAbsolutePath(removable))) {
                Log.i(TAG, "Skip " + removable + " as it is a symbolic link.");
            } else {
                File removableEmulated = new File("/Removable/emulated");
                if (removableEmulated.isDirectory() && !removableEmulated.getPath().equals(FileUtils.getCanonicalOrAbsolutePath(removableEmulated))) {
                    Log.i(TAG, "Skip " + removable + " as symbolic link " + removableEmulated + " found.");
                } else {
                    roots.put(removable, true);
                }
            }
        }
        return roots;
    }

    public static void setSubtitleCharset(String charsetName) {
        if (charsetName.length() > 0) {
            subtitleCharset = charsetName;
        } else {
            subtitleCharset = null;
        }
    }

    public static int getSubtitleBottomPaddingDp() {
        int padding;
        if (App.prefs.contains(Key.SUBTITLE_BOTTOM_PADDING)) {
            padding = App.prefs.getInt(Key.SUBTITLE_BOTTOM_PADDING, 0);
        } else {
            padding = App.context.getResources().getInteger(R.integer.default_subtitle_bottom_padding_dp);
        }
        if (padding > MAX_BOTTOM_PADDING) {
            int padding2 = MAX_BOTTOM_PADDING;
            return padding2;
        }
        return padding;
    }

    public static Typeface getSubtitleFont() {
        if (_cachedTypeface != null && _cachedTypefaceStyle == subtitleTypefaceStyle) {
            if (_cachedTypefaceName == null) {
                if (subtitleFontNameOrPath == null) {
                    return _cachedTypeface;
                }
            } else if (_cachedTypefaceName.equals(subtitleFontNameOrPath)) {
                return _cachedTypeface;
            }
        }
        try {
            _cachedTypeface = TypefaceUtils.createTypeface(subtitleFontNameOrPath, subtitleTypefaceStyle);
            _cachedTypefaceName = subtitleFontNameOrPath;
            _cachedTypefaceStyle = subtitleTypefaceStyle;
            return _cachedTypeface;
        } catch (RuntimeException e) {
            Log.e(TAG, subtitleFontNameOrPath, e);
            if (subtitleFontNameOrPath == null) {
                return Typeface.DEFAULT;
            }
            subtitleFontNameOrPath = null;
            return getSubtitleFont();
        }
    }

    public static String getSubtitleFontFamilyName() {
        if (subtitleFontNameOrPath == null || "sans-serif".equals(subtitleFontNameOrPath)) {
            return Build.VERSION.SDK_INT < 14 ? "Droid Sans" : "Roboto";
        } else if ("serif".equals(subtitleFontNameOrPath)) {
            return "Droid Serif";
        } else {
            if ("monospace".equals(subtitleFontNameOrPath)) {
                return "Droid Sans Mono";
            }
            return Fonts.getFamilyName(subtitleFontNameOrPath);
        }
    }

    public static float getBrightness() {
        if (screenBrightnessAuto) {
            return -1.0f;
        }
        return screenBrightness;
    }

    public static void setScanRoots(SortedMap<File, Boolean> roots) {
        SharedPreferences.Editor editor = App.prefs.edit();
        if (roots != null) {
            scanRoots = roots;
            int i = 0;
            String[] encoded = new String[roots.size()];
            for (Map.Entry<File, Boolean> entry : roots.entrySet()) {
                String path = entry.getKey().getPath();
                if (!entry.getValue().booleanValue()) {
                    path = '!' + path;
                }
                encoded[i] = Uri.encode(path);
                i++;
            }
            editor.putString(Key.SCAN_ROOTS, TextUtils.join(File.pathSeparator, encoded));
        } else {
            scanRoots = getDefaultScanRoots();
            editor.remove(Key.SCAN_ROOTS);
        }
        AppUtils.apply(editor);
    }

    public static int findLocatedFolder(String path) {
        int lenVisible = -1;
        int lenHidden = -1;
        for (Map.Entry<File, Boolean> root : scanRoots.entrySet()) {
            String rootPath = root.getKey().getPath();
            if (FileUtils.isLocatedIn(path, rootPath)) {
                int rootLen = rootPath.length();
                if (root.getValue().booleanValue()) {
                    if (lenVisible < rootLen) {
                        lenVisible = rootLen;
                    }
                } else if (lenHidden < rootLen) {
                    lenHidden = rootLen;
                }
            }
        }
        if (lenVisible < lenHidden) {
            return -1;
        }
        return lenVisible;
    }

    private static void initVideoExtensionList() {
        Map<String, ?> map = App.context.getSharedPreferences(EXT_PREFERENCE_NAME, 0).getAll();
        TreeMap<String, Integer> list = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (map.size() == 0) {
            addVideoDefault(list);
            if (isAudioPlayer) {
                addAudioDefault(list);
            }
        } else {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    list.put(entry.getKey(), (Integer) value);
                }
            }
        }
        _mediaExts = list;
    }

    private static void addVideoDefault(Map<String, Integer> list) {
        String[] stringArray;
        String[] stringArray2;
        for (String ext : App.context.getResources().getStringArray(R.array.video_exts)) {
            list.put(ext, 0);
        }
        for (String ext2 : App.context.getResources().getStringArray(R.array.video_hw_exts)) {
            list.put(ext2, 1);
        }
    }

    private static void addAudioDefault(Map<String, Integer> list) {
        String[] stringArray;
        String[] stringArray2;
        for (String ext : App.context.getResources().getStringArray(R.array.audio_exts)) {
            if (!list.containsKey(ext)) {
                list.put(ext, 0);
            }
        }
        for (String ext2 : App.context.getResources().getStringArray(R.array.audio_hw_exts)) {
            if (!list.containsKey(ext2)) {
                list.put(ext2, 1);
            }
        }
    }

    private static void removeAudioDefault(Map<String, Integer> list) {
        for (String ext : getDefaultAudioExtensions()) {
            list.remove(ext);
        }
    }

    public static SortedSet<String> getDefaultAudioExtensions() {
        String[] stringArray;
        String[] stringArray2;
        if (_defaultAudioExtensions == null) {
            Resources res = App.context.getResources();
            _defaultAudioExtensions = new TreeSet(String.CASE_INSENSITIVE_ORDER);
            _defaultAudioExtensions.addAll(Arrays.asList(res.getStringArray(R.array.audio_exts)));
            _defaultAudioExtensions.addAll(Arrays.asList(res.getStringArray(R.array.audio_hw_exts)));
            for (String videoExt : res.getStringArray(R.array.video_exts)) {
                _defaultAudioExtensions.remove(videoExt);
            }
            for (String videoExt2 : res.getStringArray(R.array.video_hw_exts)) {
                _defaultAudioExtensions.remove(videoExt2);
            }
        }
        return _defaultAudioExtensions;
    }

    private static boolean saveExtensionList(SharedPreferences prefs, Map<String, Integer> map) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        if (map != null) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                edit.putInt(entry.getKey(), entry.getValue().intValue());
            }
        }
        return edit.commit();
    }

    public static void enableAudioExtension() {
        addAudioDefault(_mediaExts);
        SharedPreferences prefs = App.context.getSharedPreferences(EXT_PREFERENCE_NAME, 0);
        if (prefs.getAll().size() > 0) {
            saveExtensionList(prefs, _mediaExts);
        }
    }

    public static void disableAudioExtension() {
        removeAudioDefault(_mediaExts);
        SharedPreferences prefs = App.context.getSharedPreferences(EXT_PREFERENCE_NAME, 0);
        if (prefs.getAll().size() > 0) {
            saveExtensionList(prefs, _mediaExts);
        }
    }

    public static boolean resetVideoExtensionList(Map<String, Integer> map) {
        _mediaExts.clear();
        if (map != null) {
            _mediaExts.putAll(map);
        } else {
            addVideoDefault(_mediaExts);
            if (isAudioPlayer) {
                addAudioDefault(_mediaExts);
            }
        }
        return saveExtensionList(App.context.getSharedPreferences(EXT_PREFERENCE_NAME, 0), map);
    }

    public static boolean isSupportedMediaFile(String path) {
        return _mediaExts.containsKey(FileUtils.getExtension(path));
    }

    public static boolean isSupportedMediaExtension(String ext) {
        return _mediaExts.containsKey(ext);
    }

    public static int getDecoder(String ext, int defaultValue) {
        Integer value = _mediaExts.get(ext);
        if (value == null) {
            return defaultValue;
        }
        int defaultValue2 = value.intValue();
        return defaultValue2;
    }

    public static Map<String, Integer> getAllVideoExtensions() {
        return new HashMap(_mediaExts);
    }

    public static float getSubtitleTextSize() {
        float size = App.prefs.getFloat(Key.SUBTITLE_TEXT_SIZE, defaultSubTextSize);
        if (size <= 0.0f) {
            float size2 = App.prefs.getInt(Key.SUBTITLE_TEXT_SIZE_OBSOLETE, (int) defaultSubTextSize);
            return size2 <= 0.0f ? defaultSubTextSize : size2;
        }
        return size;
    }

    private static boolean getSubtitleForceLtr() {
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        }
        return App.prefs.getBoolean(Key.SUBTITLE_FORCE_LTR, true);
    }

    private static float getDefaultSubTextSize(Resources res) {
        float denominator;
        DisplayMetrics metrics = res.getDisplayMetrics();
        int layout = res.getConfiguration().screenLayout & 15;
        if (layout >= 4) {
            denominator = 19.2f;
        } else {
            denominator = 18.0f;
        }
        float size = Math.min(metrics.widthPixels, metrics.heightPixels) / (metrics.scaledDensity * denominator);
        if (size < 16.0f) {
            return 16.0f;
        }
        if (size > 60.0f) {
            return 60.0f;
        }
        return size;
    }

    public static float canonicalizeSubtitleScale(float scale) {
        if (scale < 0.5f) {
            return 0.5f;
        }
        if (scale > 4.0f) {
            return 4.0f;
        }
        if (0.995f <= scale && scale < 1.005f) {
            return 1.0f;
        }
        return scale;
    }

    public static boolean getNaviShowMoveButtons() {
        return App.prefs.contains(Key.NAVI_SHOW_MOVE_BUTTONS) ? App.prefs.getBoolean(Key.NAVI_SHOW_MOVE_BUTTONS, false) : App.context.getResources().getBoolean(R.bool.default_navi_show_move_buttons);
    }

    public static boolean getSubtitleBorderEnabled() {
        return App.prefs.getBoolean(Key.SUBTITLE_BORDER_ENABLED, false);
    }

    public static boolean getSubtitleShadownEnabled() {
        return App.prefs.getBoolean(Key.SUBTITLE_SHADOW_ENABLED, true);
    }

    public static void applyHardwareAccelerationToWindow(Activity activity) {
        if (interfaceHardwareAcceleration) {
            activity.getWindow().setFlags(ViewCompat.MEASURED_STATE_TOO_SMALL, ViewCompat.MEASURED_STATE_TOO_SMALL);
        }
    }

    public static boolean getItalicTag() {
        if (App.prefs.contains(Key.ITALIC_TAG)) {
            return App.prefs.getBoolean(Key.ITALIC_TAG, false);
        }
        Locale locale = Locale.getDefault();
        if ("pl".equals(locale.getLanguage())) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.ITALIC_TAG, true);
            AppUtils.apply(editor);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSubtitleFolder() {
        String path = App.prefs.getString(Key.SUBTITLE_FOLDER, null);
        if (path != null) {
            subtitleFolder = new File(path);
        } else {
            subtitleFolder = new File(Environment.getExternalStorageDirectory(), "Subtitles");
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putString(Key.SUBTITLE_FOLDER, subtitleFolder.getPath());
            AppUtils.apply(editor);
        }
        subtitleFolder.mkdirs();
    }

    public static int getCoreLimit() {
        int core;
        String val = App.prefs.getString(Key.CORE_LIMIT, null);
        return (val == null || (core = Integer.parseInt(val)) <= 0) ? Cpu.coreCount : core;
    }

    public static File getFontFolder() {
        String typefaceDirPath = App.prefs.getString(Key.FONT_FOLDER, null);
        return typefaceDirPath != null ? new File(typefaceDirPath) : Environment.getExternalStorageDirectory();
    }

    public static boolean getCorrectHWAspectRatio() {
        return App.prefs.getBoolean(Key.CORRECT_HW_ASPECT_RATIO, true);
    }

    public static boolean getButtonBacklightOff() {
        if (Build.VERSION.SDK_INT < 8) {
            return false;
        }
        if (App.prefs.contains(Key.BUTTON_BACKLIGHT_OFF)) {
            return App.prefs.getBoolean(Key.BUTTON_BACKLIGHT_OFF, false);
        }
        if (Model.manufacturer != 10000 || Build.VERSION.SDK_INT >= 14) {
            if (Model.manufacturer != 10040 || Build.VERSION.SDK_INT < 14 || Build.VERSION.SDK_INT >= 16) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putBoolean(Key.BUTTON_BACKLIGHT_OFF, true);
                AppUtils.apply(editor);
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean getSubtitleFadeout() {
        return App.prefs.getBoolean(Key.SUBTITLE_FADEOUT, Build.VERSION.SDK_INT >= 14);
    }

    public static boolean isOMXDecoderEnabled() {
        if (L.hasOMXDecoder) {
            if (App.prefs.contains(Key.OMX_DECODER)) {
                return App.prefs.getBoolean(Key.OMX_DECODER, false);
            }
            return Build.VERSION.SDK_INT == 21 && Build.MODEL.startsWith("Nexus") && !Build.MODEL.equalsIgnoreCase("Nexus 10");
        }
        return false;
    }

    public static boolean getTryOMXIfHWFails() {
        return L.hasOMXDecoder && App.prefs.getBoolean(Key.TRY_OMX_IF_HW_FAILS, false);
    }

    public static boolean getTryHWIfOMXFails() {
        return App.prefs.contains(Key.TRY_HW_IF_OMX_FAILS) ? App.prefs.getBoolean(Key.TRY_HW_IF_OMX_FAILS, false) : App.context.getResources().getBoolean(R.bool.default_try_hw_if_omx_fails);
    }

    public static boolean isOMXDecoderVisible() {
        return isOMXDecoderEnabled() || getTryOMXIfHWFails();
    }

    public static int getDefaultOMXVideoCodecs(boolean includeSWCodecs) {
        if (Build.VERSION.SDK_INT >= 16) {
            if (includeSWCodecs) {
                if (defaultOMXSupportedVideoCodecs_includeSWCodecs == 0) {
                    defaultOMXSupportedVideoCodecs_includeSWCodecs = FFPlayer.getOMXSupportedVideoCodecs(true);
                }
                return defaultOMXSupportedVideoCodecs_includeSWCodecs;
            }
            if (defaultOMXSupportedVideoCodecs == 0) {
                defaultOMXSupportedVideoCodecs = FFPlayer.getOMXSupportedVideoCodecs(false);
            }
            return defaultOMXSupportedVideoCodecs;
        }
        return 4218;
    }

    public static int getDefaultOMXVideoCodecs() {
        return getDefaultOMXVideoCodecs(false);
    }

    public static int getOMXVideoCodecs() {
        return App.prefs.contains(Key.OMX_VIDEO_CODECS) ? App.prefs.getInt(Key.OMX_VIDEO_CODECS, 0) : getDefaultOMXVideoCodecs();
    }

    public static boolean getOMXDecoderLocal() {
        return isOMXDecoderEnabled() && App.prefs.getBoolean(Key.OMX_DECODER_LOCAL, true);
    }

    public static boolean getOMXDecoderNetwork() {
        return isOMXDecoderEnabled() && App.prefs.getBoolean(Key.OMX_DECODER_NETWORK, true);
    }

    public static boolean canUseOmxAudioOnSwVideo() {
        return isOMXDecoderEnabled() || App.prefs.getBoolean(Key.OMX_AUDIO_WITH_SW_VIDEO, true);
    }

    @SuppressLint({"NewApi"})
    public static boolean getSeekPreviews() {
        if (App.prefs.contains(Key.SEEK_PREVIEWS)) {
            return App.prefs.getBoolean(Key.SEEK_PREVIEWS, false);
        }
        if (Build.VERSION.SDK_INT >= 11) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.SEEK_PREVIEWS, true);
            editor.apply();
            return true;
        }
        return false;
    }

    public static void updateSyncSystemVolume() {
        boolean z = true;
        syncSystemVolume = (DeviceUtils.isTV || !App.prefs.getBoolean(Key.SYNC_SYSTEM_VOLUME, true)) ? false : false;
        if (!syncSystemVolume) {
            loadLocalVolume();
        }
    }

    public static boolean getFastSeek() {
        return App.prefs.getBoolean(Key.FAST_SEEK, true);
    }

    private static int parseColorFormat(String format) {
        if (COLOR_FORMAT_RGB565.equals(format)) {
            return 4;
        }
        if (COLOR_FORMAT_RGBX8888.equals(format)) {
            return 2;
        }
        return 842094169;
    }

    public static int getColorFormat() {
        return parseColorFormat(App.prefs.getString(Key.COLOR_FORMAT, COLOR_FORMAT_RGB565));
    }

    public static boolean getHeadsetShowSystemVolumeUI() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        return App.prefs.getBoolean(Key.HEADSET_SHOW_SYSTEM_VOLUME_UI, true);
    }

    public static int getInterfaceAutoHideDelay(Object caller) {
        if (!(caller instanceof Lock)) {
            long delayFromVolumePanelHideTime = (_volumePanelShowTime + 3750) - SystemClock.uptimeMillis();
            if (delayFromVolumePanelHideTime > _interfaceAutoHideDelay) {
                return (int) delayFromVolumePanelHideTime;
            }
        }
        return _interfaceAutoHideDelay;
    }

    public static void updateVolumePanelShowTime() {
        _volumePanelShowTime = SystemClock.uptimeMillis();
    }

    public static int getGestures() {
        int defaultValue = 3000 | 1;
        return App.prefs.getInt(Key.GESTURES, defaultValue);
    }

    public static void updatePreferredSubtitleLocales() {
        preferredSubtitleLocales = getPreferredLocalesFrom(Key.SUBTITLE_LANGUAGE);
    }

    public static void updatePreferredAudioLocales() {
        preferredAudioLocales = getPreferredLocalesFrom(Key.AUDIO_LANGUAGE);
    }

    private static Locale[] getPreferredLocalesFrom(String key) {
        ArrayList<Locale> locales = new ArrayList<>();
        Locale[] selected = LocaleUtils.parseLocales(App.prefs.getString(key, null));
        defaultLocale = Locale.getDefault();
        locales.addAll(Arrays.asList(selected));
        if (!locales.contains(defaultLocale)) {
            locales.add(defaultLocale);
        }
        boolean defaultLocaleIsNative = false;
        loadNativeLocales();
        int i = 0;
        while (true) {
            if (i >= _numNativeLocales) {
                break;
            } else if (!_nativeLocales[i].equals(defaultLocale)) {
                i++;
            } else {
                defaultLocaleIsNative = true;
                break;
            }
        }
        if (!defaultLocaleIsNative) {
            String language = defaultLocale.getLanguage();
            if (language.length() != defaultLocale.toString().length()) {
                Locale locale = new Locale(language);
                if (!locales.contains(locale)) {
                    locales.add(locale);
                }
            }
        }
        return (Locale[]) locales.toArray(new Locale[locales.size()]);
    }

    public static void onConfigurationChanged(Configuration newConfig) {
        if (defaultLocale != null && !defaultLocale.equals(newConfig.locale)) {
            updatePreferredSubtitleLocales();
            updatePreferredAudioLocales();
        }
    }

    public static Locale[] getNativeLocales() {
        loadNativeLocales();
        return _nativeLocales;
    }

    public static String[] getNativeLocaleNames() {
        loadNativeLocales();
        return _nativeLocaleNames;
    }

    private static void loadNativeLocales() {
        if (_nativeLocales == null) {
            Resources res = App.context.getResources();
            String[] locales = res.getStringArray(R.array.all_locales);
            _numNativeLocales = locales.length;
            _nativeLocales = new Locale[_numNativeLocales];
            for (int i = 0; i < _numNativeLocales; i++) {
                _nativeLocales[i] = LocaleUtils.parse(locales[i]);
            }
            _nativeLocaleNames = res.getStringArray(R.array.all_locale_names);
        }
    }

    public static String getNativeLocaleName(Locale locale) {
        return getNativeLocaleName(locale, false);
    }

    public static String getNativeLocaleName(Locale locale, boolean simple) {
        loadNativeLocales();
        for (int i = 0; i < _numNativeLocales; i++) {
            if (_nativeLocales[i].equals(locale)) {
                return _nativeLocaleNames[i];
            }
        }
        if (simple) {
            String lang = locale.getLanguage();
            if (lang.length() < locale.toString().length()) {
                for (int i2 = 0; i2 < _numNativeLocales; i2++) {
                    if (_nativeLocales[i2].toString().equals(lang)) {
                        return _nativeLocaleNames[i2];
                    }
                }
            }
        }
        return locale.getDisplayName();
    }

    public static String getNativeLocaleNames(Locale[] locales) {
        return getNativeLocaleNames(locales, ", ", false);
    }

    public static String getNativeLocaleNames(Locale[] locales, boolean simple) {
        return getNativeLocaleNames(locales, ", ", simple);
    }

    public static String getNativeLocaleNames(Locale[] locales, String delimiter, boolean simple) {
        ArrayList<String> names = new ArrayList<>(locales.length);
        for (Locale locale : locales) {
            String name = getNativeLocaleName(locale, simple);
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return TextUtils.join(delimiter, names);
    }

    @TargetApi(9)
    public static void setAutoSelectSubtitle(boolean enable) {
        if (autoSelectSubtitle != enable) {
            autoSelectSubtitle = enable;
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.SUBTITLE_AUTOLOAD, enable);
            editor.apply();
        }
    }

    @SuppressLint({"NewApi"})
    public static int getScreenLockMode() {
        int mode;
        if (Build.VERSION.SDK_INT >= 23) {
            return 2;
        }
        if (App.prefs.contains(Key.SCREEN_LOCK_MODE)) {
            int mode2 = App.prefs.getInt(Key.SCREEN_LOCK_MODE, 0);
            return mode2;
        } else if (Build.VERSION.SDK_INT == 19) {
            if (Model.model == 1300) {
                mode = 1;
            } else if (Model.model != 1301) {
                return 0;
            } else {
                mode = 2;
            }
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putInt(Key.SCREEN_LOCK_MODE, mode);
            editor.apply();
            return mode;
        } else {
            return 0;
        }
    }

    public static String getDefaultHttpUserAgent() {
        try {
            PackageInfo packageInfo = App.context.getPackageManager().getPackageInfo(App.context.getPackageName(), 0);
            L.sb.setLength(0);
            L.sb.append("Mozilla/5.0 (Linux; Android ").append(Build.VERSION.RELEASE).append("; ").append(Locale.getDefault().toString().replace('_', '-')).append("; ").append(Build.MODEL).append(" Build/").append(Build.DISPLAY).append(") ").append(App.context.getString(R.string.default_user_agent_app_name)).append('/').append(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
        }
        return L.sb.toString();
    }

    private static String getHttpUserAgent() {
        String httpUserAgent2 = App.prefs.getString(Key.HTTP_USER_AGENT, null);
        if (httpUserAgent2 != null) {
            String httpUserAgent3 = httpUserAgent2.trim();
            if (httpUserAgent3.length() > 0) {
                return httpUserAgent3;
            }
        }
        return getDefaultHttpUserAgent();
    }
}
