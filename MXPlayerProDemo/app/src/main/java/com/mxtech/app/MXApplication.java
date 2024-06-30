package com.mxtech.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mxtech.DeviceUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import java.util.Locale;

/* loaded from: classes.dex */
public class MXApplication extends Application {
    public static final boolean DEBUG = false;
    public static final String TAG = "MX";
    public static Context context;
    public static Handler handler;
    public static boolean native_initialized;
    public static OrderedSharedPreferences prefs;
    private Locale _backupLocale;
    private Locale _customLocale;
    private boolean _preInitCalled = false;
    private boolean _initCalled = false;

    @Override // android.app.Application
    public final void onCreate() {
        if (!this._preInitCalled) {
            this._preInitCalled = true;
            onPreInit();
        }
        super.onCreate();
        try {
            String packageName = getPackageName();
            PackageManager pm = getPackageManager();
            PackageInfo pkg = pm.getPackageInfo(packageName, 0);
            Log.i(TAG, "Application=[" + getString(pkg.applicationInfo.labelRes) + "] Version=[" + pkg.versionName + "] Manufacturer=[" + Build.MANUFACTURER + "] Model=[" + Build.MODEL + "] Display=[" + Build.DISPLAY + "] Brand=[" + Build.BRAND + "] Product=[" + Build.PRODUCT + "] Android=[" + Build.VERSION.RELEASE + ']');
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
        }
        if (!this._initCalled) {
            this._initCalled = true;
            onInit();
        }
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        SQLiteDatabase.releaseMemory();
    }

    public final void init() {
        if (!this._preInitCalled) {
            this._preInitCalled = true;
            onPreInit();
        }
        if (!this._initCalled) {
            this._initCalled = true;
            onInit();
        }
    }

    public final boolean initInteractive(Activity caller) {
        return onInitInteractive(caller);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPreInit() {
        context = this;
        handler = new Handler();
        if (prefs == null) {
            prefs = new OrderedSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        }
        if (Build.VERSION.SDK_INT >= 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onInit() {
        this._backupLocale = getResources().getConfiguration().locale;
    }

    protected boolean onInitInteractive(Activity caller) {
        return true;
    }

    @SuppressLint({"NewApi"})
    public void setCustomLocale(Locale locale) {
        this._customLocale = locale;
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public Locale getCustomLocale() {
        return this._customLocale;
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    @SuppressLint({"NewApi"})
    public void onConfigurationChanged(Configuration newConfig) {
        if (this._customLocale == null) {
            if (this._backupLocale != null && newConfig.locale != null && !this._backupLocale.equals(newConfig.locale)) {
                AppUtils.quit(this);
            }
        } else if (!this._customLocale.equals(newConfig.locale)) {
            Locale.setDefault(this._customLocale);
            if (Build.VERSION.SDK_INT >= 17) {
                newConfig.setLocale(this._customLocale);
            } else {
                newConfig.locale = this._customLocale;
            }
            Resources res = getResources();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        DeviceUtils.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }
}
