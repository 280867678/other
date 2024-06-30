package com.mxtech;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import com.mxtech.app.MXApplication;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;

/* loaded from: classes.dex */
public final class DeviceUtils {
    public static final String ANDROID_TV_DEFAULT_INTENT_RECEIVER = "com.google.android.tv.frameworkpackagestubs";
    public static final double DRAG_MINIMUM_DISTANCE = 0.003d;
    private static final int MB = 1048576;
    public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
    public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
    private static final String TAG = "MX.DeviceUtils";
    private static boolean _tvModeForced;
    public static float density;
    public static int densityDpi;
    public static float dragMinimumPixel;
    public static boolean hasQwertKeyboard;
    public static boolean hasTouchScreen;
    public static boolean isLeanbackTV;
    public static boolean isTV;
    public static int largestPixels;
    public static float scaledDensity;
    public static int smallestPixels;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init(Context context) {
        context.getPackageManager();
        Resources res = context.getResources();
        DisplayMetrics disp = res.getDisplayMetrics();
        onConfigurationChanged(res.getConfiguration());
        densityDpi = disp.densityDpi;
        density = disp.density;
        scaledDensity = disp.scaledDensity;
        if (disp.widthPixels > disp.heightPixels) {
            smallestPixels = disp.heightPixels;
            largestPixels = disp.widthPixels;
        } else {
            smallestPixels = disp.widthPixels;
            largestPixels = disp.heightPixels;
        }
        dragMinimumPixel = (float) MeterToPixel(0.003d);
    }

    public static boolean getTVMode(Configuration config) {
        return Build.VERSION.SDK_INT >= 13 && (config.uiMode & 15) == 4;
    }

    public static boolean getTVMode() {
        return getTVMode(MXApplication.context.getResources().getConfiguration());
    }

    public static void forceTVMode(boolean force) {
        _tvModeForced = force;
        if (force) {
            isTV = true;
        } else {
            isTV = getTVMode();
        }
    }

    @SuppressLint({"NewApi"})
    public static void onConfigurationChanged(Configuration config) {
        isTV = getTVMode(config);
        isLeanbackTV = isTV && Build.VERSION.SDK_INT >= 17;
        hasTouchScreen = config.touchscreen != 1;
        hasQwertKeyboard = config.keyboard == 2;
        if (_tvModeForced) {
            isTV = true;
        }
    }

    @SuppressLint({"NewApi"})
    public static int getScreenWidthDp(Resources res) {
        return Build.VERSION.SDK_INT >= 13 ? res.getConfiguration().screenWidthDp : (int) (res.getDisplayMetrics().widthPixels / density);
    }

    @SuppressLint({"NewApi"})
    public static int getScreenHeightDp(Resources res) {
        return Build.VERSION.SDK_INT >= 13 ? res.getConfiguration().screenHeightDp : (int) (res.getDisplayMetrics().heightPixels / density);
    }

    public static boolean isExternalStorageMounted(boolean allowReadOnly) {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            return true;
        }
        return allowReadOnly && "mounted_ro".equals(state);
    }

    public static int getStorageErrorMessageId(String state) {
        if ("bad_removal".equals(state)) {
            int resId = R.string.error_media_bad_removal;
            return resId;
        } else if ("checking".equals(state)) {
            int resId2 = R.string.error_media_checking;
            return resId2;
        } else if ("mounted_ro".equals(state)) {
            int resId3 = R.string.error_media_mounted_read_only;
            return resId3;
        } else if ("nofs".equals(state)) {
            int resId4 = R.string.error_media_nofs;
            return resId4;
        } else if ("removed".equals(state)) {
            int resId5 = R.string.error_media_removed;
            return resId5;
        } else if ("shared".equals(state)) {
            int resId6 = R.string.error_media_shared;
            return resId6;
        } else if ("unmountable".equals(state)) {
            int resId7 = R.string.error_media_unmountable;
            return resId7;
        } else if ("unmounted".equals(state)) {
            int resId8 = R.string.error_media_unmounted;
            return resId8;
        } else {
            int resId9 = R.string.error_media_general;
            return resId9;
        }
    }

    public static double MeterToPixel(double meter) {
        return 39.3701d * meter * densityDpi;
    }

    public static double PixelToMeter(double pixel) {
        return pixel / (39.3701d * densityDpi);
    }

    public static float DIPToPixel(float dip) {
        return density * dip;
    }

    public static float PixelToDIP(float pixel) {
        return pixel / density;
    }

    public static float PixelToSP(float pixel) {
        return pixel / scaledDensity;
    }

    public static float SPToPixel(float sp) {
        return scaledDensity * sp;
    }

    public static final void setBrightness(Window window, float brightness) {
        WindowManager.LayoutParams layout = window.getAttributes();
        if (layout.screenBrightness != brightness) {
            Log.v(TAG, "Brightness for " + window + ": " + layout.screenBrightness + " --> " + brightness);
            layout.screenBrightness = brightness;
            window.setAttributes(layout);
        }
    }

    @TargetApi(8)
    public static final void showButtonBacklight(Window window, boolean on) {
        float value = on ? -1.0f : 0.0f;
        WindowManager.LayoutParams layout = window.getAttributes();
        if (layout.buttonBrightness != value) {
            layout.buttonBrightness = value;
            window.setAttributes(layout);
        }
    }

    public static float getSystemBrightness(Context context, float defaultBrightness) {
        ContentResolver cr = context.getContentResolver();
        int mode = Settings.System.getInt(cr, SCREEN_BRIGHTNESS_MODE, 0);
        return mode == 1 ? defaultBrightness : (1.0f + Settings.System.getFloat(cr, Key.SCREEN_BRIGHTNESS, 255.0f * defaultBrightness)) / 256.0f;
    }

    public static final void makeFullScreen(Window window) {
        window.requestFeature(1);
        window.addFlags(1024);
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager locs = (LocationManager) context.getSystemService("location");
        if (locs == null) {
            return null;
        }
        int pid = Process.myPid();
        int uid = Process.myUid();
        boolean permitFineLocation = context.checkPermission("android.permission.ACCESS_FINE_LOCATION", pid, uid) == 0;
        boolean permitCoarseLocation = context.checkPermission("android.permission.ACCESS_COARSE_LOCATION", pid, uid) == 0;
        if (permitFineLocation) {
            try {
                Location loc = locs.getLastKnownLocation("gps");
                if (loc != null) {
                    return loc;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        if (permitFineLocation || permitCoarseLocation) {
            try {
                Location loc2 = locs.getLastKnownLocation("network");
                if (loc2 != null) {
                    return loc2;
                }
            } catch (IllegalArgumentException e2) {
            }
        }
        if (Build.VERSION.SDK_INT > 7 && permitFineLocation) {
            try {
                Location loc3 = locs.getLastKnownLocation("passive");
                if (loc3 != null) {
                    return loc3;
                }
            } catch (IllegalArgumentException e3) {
            }
        }
        return null;
    }

    public static String getTelephonyCountry(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (tm == null) {
            return null;
        }
        return tm.getNetworkCountryIso();
    }

    public static boolean isWifiActive(Context context) {
        NetworkInfo active;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        return (connectivity == null || (active = connectivity.getActiveNetworkInfo()) == null || active.getType() != 1) ? false : true;
    }

    @SuppressLint({"NewApi"})
    public static boolean isTabletFromDeviceConfig(Resources res, int uiVersion) {
        if (Build.VERSION.SDK_INT < 13) {
            return Build.VERSION.SDK_INT >= 11;
        }
        Configuration cfg = res.getConfiguration();
        return cfg.smallestScreenWidthDp >= 600;
    }

    public static boolean isOldTabletFromDeviceConfig(Context context) {
        return isOldTabletFromDeviceConfig(context, Build.VERSION.SDK_INT);
    }

    @SuppressLint({"NewApi"})
    public static boolean isOldTabletFromDeviceConfig(Context context, int uiVersion) {
        if (uiVersion >= 16) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 13) {
            return Build.VERSION.SDK_INT >= 11;
        }
        Configuration cfg = context.getResources().getConfiguration();
        boolean isTablet = cfg.smallestScreenWidthDp >= 600;
        return isTablet;
    }

    public static boolean isFreeStyleSamsungMultiWindowSupport(Context context) {
        PackageManager packagemanager = context.getPackageManager();
        if (packagemanager != null) {
            return packagemanager.hasSystemFeature("com.sec.feature.multiwindow.tablet");
        }
        return false;
    }

    public static int getTopStatusbarHeight(Window window) {
        Rect r = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        return r.top;
    }

    public static int getBottomStatusbarHeight(Window window) {
        if (Build.VERSION.SDK_INT < 14) {
            return 0;
        }
        Rect r = new Rect();
        Point realSize = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        display.getRealSize(realSize);
        Log.v(TAG, "window-visible-display-frame: [left=" + r.left + " top=" + r.top + " right=" + r.right + " bottom=" + r.bottom + "] real-display-size: [width=" + realSize.x + " height=" + realSize.y + "]");
        return realSize.y - r.bottom;
    }

    public static int getScreenOrientation(Context context, Display display) {
        int orientation = context.getResources().getConfiguration().orientation;
        int rotation = display.getOrientation();
        switch (rotation) {
            case 1:
                return orientation != 2 ? 9 : 0;
            case 2:
                return orientation == 2 ? 8 : 9;
            case 3:
                return orientation != 2 ? 1 : 8;
            default:
                return orientation == 2 ? 0 : 1;
        }
    }
}
