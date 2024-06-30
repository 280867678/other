package com.mxtech.preference;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;

import androidx.core.view.ViewCompat;

import com.mxtech.app.AppUtils;
import com.mxtech.os.Model;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.pro.R;

import java.io.File;
import java.util.Locale;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

public class P {
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
    public static final float MAX_SUBTITLE_SCALE = 4.0f;
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
    public static final String TAG = "MX.P";
    public static final float TEXT_BORDER_THICKNESS_DEFAULT = 0.08f;
    public static final float TEXT_BORDER_THICKNESS_MAX = 0.3f;
    public static final float TEXT_BORDER_THICKNESS_MIN = 0.05f;
    public static final float TEXT_BORDER_THICKNESS_STANDARD = 0.1f;
    public static final float TEXT_BORDER_THICKNESS_UNIT = 0.01f;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE = 1;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE_AFTER_SHOWING_CONTROLLER = 0;
    public static final int TOUCH_ACTION_PLAYBACK_TOGGLE_WITH_SHOWING_CONTROLLER = 3;
    public static final int TOUCH_ACTION_UI_TOGGLE = 2;
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
    public static boolean backToList = false;
    public static Integer bluetoothAudioDelay = null;
    public static boolean cacheThumbnail = false;
    private static Locale defaultLocale = null;
    private static int defaultOMXSupportedVideoCodecs = 0;
    private static int defaultOMXSupportedVideoCodecs_includeSWCodecs = 0;
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
    public static final int kSubtitleTextSizeMaxSp = 60;
    public static final int kSubtitleTextSizeMinSp = 16;
    public static boolean keepScreenOn;
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
    public static int playerLooping;
    public static Locale[] preferredAudioLocales;
    public static Locale[] preferredSubtitleLocales;
    public static boolean prevnext_to_rewff;
    public static boolean quickZoom;
    public static boolean rememberPlaybackSelections;
    public static boolean respectMediaButtons;
    public static boolean respectNomedia;
//    public static Resume resume;
    public static boolean resumeFirstOnly;
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
    public static boolean smartPrevious;
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
    public static boolean subtitleForceLTR;
    public static int subtitleMaxBottomPaddingDp;
    public static float subtitleScale;
    public static int subtitleTextColor;
    public static int subtitleTypefaceStyle;
    public static boolean syncSystemVolume;
    public static boolean tabletFromConfig;
    public static boolean toggleOnMediaPlayButton;
    public static int uiVersion;
    public static boolean useSpeedupTricks;
    public static boolean video;
    public static String videoText;
    public static boolean videoZoomLimited;
    public static boolean volumeBoost;
    static ComponentName webAudioDelegateName;
    static ComponentName webDelegateName;

    public static int getFullScreen() {
        if (oldTablet) {
            return 0;
        }
        return App.prefs.getInt(Key.FULLSCREEN, 0);
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

    public static int getGestures() {
        int defaultValue = 3000 | 1;
        return App.prefs.getInt(Key.GESTURES, defaultValue);
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


    public static boolean useAlternativeOMXDecoder() {
        return App.prefs.contains(Key.OMX_DECODER_ALTERNATIVE) ? App.prefs.getBoolean(Key.OMX_DECODER_ALTERNATIVE, false) : getDefaultAlternativeOMXDecoder();
    }

    public static boolean getDefaultAlternativeOMXDecoder() {
        return Model.isCyanogenMod();
    }



    public static File getFontFolder() {
        String typefaceDirPath = App.prefs.getString(Key.FONT_FOLDER, null);
        return typefaceDirPath != null ? new File(typefaceDirPath) : Environment.getExternalStorageDirectory();
    }


//    public static boolean interfaceHardwareAcceleration = false;
    public static void applyHardwareAccelerationToWindow(Activity activity) {
        if (interfaceHardwareAcceleration) {
            activity.getWindow().setFlags(ViewCompat.MEASURED_STATE_TOO_SMALL, ViewCompat.MEASURED_STATE_TOO_SMALL);
        }
    }

    public static int getPlaybackTheme() {
        return getPlaybackThemeStyleId(getPlaybackThemeName());
    }

    public static String getPlaybackThemeName() {
        String theme = App.prefs.getString(Key.PLAYBACK_THEME, null);
        return (theme == null || theme.length() <= 0) ? App.context.getString(R.string.default_playback_theme) : theme;
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
//            case 0:
//                return R.style.PlaybackWhiteTheme;
//            case 1:
//                return R.style.PlaybackBlackTheme;
//            case 2:
//                return R.style.PlaybackBrownTheme;
//            case 3:
//                return R.style.PlaybackDarkTheme;
//            case 4:
//                return R.style.PlaybackDarkNavyTheme;
//            case 5:
//                return R.style.PlaybackIndigoTheme;
//            case 6:
//                return R.style.PlaybackRedTheme;
//            case 7:
//                return R.style.PlaybackPinkTheme;
//            case '\b':
//                return R.style.PlaybackFLPinkTheme;
//            case '\t':
//                return R.style.PlaybackPurpleTheme;
//            case '\n':
//                return R.style.PlaybackGreenTheme;
//            case 11:
//                return R.style.PlaybackOrangeTheme;
//            case '\f':
//                return R.style.PlaybackBlackTheme_RedAccent;
//            case '\r':
//                return R.style.PlaybackBlackTheme_PinkAccent;
//            case 14:
//                return R.style.PlaybackBlackTheme_FLPinkAccent;
//            case 15:
//                return R.style.PlaybackBlackTheme_OrangeAccent;
//            case 16:
//                return R.style.PlaybackBlackTheme_YellowAccent;
//            case 17:
//                return R.style.PlaybackBlackTheme_BrownAccent;
//            case 18:
//                return R.style.PlaybackBlackTheme_GreenAccent;
//            case 19:
//            case 20:
            default:
                return R.style.PlaybackBlackTheme_BlueAccent;
//            case 21:
//                return R.style.PlaybackBlackTheme_IndigoAccent;
//            case 22:
//                return R.style.PlaybackBlackTheme_PurpleAccent;
        }
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

    public static boolean getNaviShowMoveButtons() {
        return App.prefs.contains(Key.NAVI_SHOW_MOVE_BUTTONS) ? App.prefs.getBoolean(Key.NAVI_SHOW_MOVE_BUTTONS, false) : App.context.getResources().getBoolean(R.bool.default_navi_show_move_buttons);
    }



}
