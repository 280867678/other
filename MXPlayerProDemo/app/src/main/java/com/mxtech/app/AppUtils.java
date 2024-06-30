package com.mxtech.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.mxtech.Misc;
import com.mxtech.preference.MXPreferenceActivity;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes.dex */
public final class AppUtils {
    public static final int SD_LVL_PUBLIC_KEY = 1;
    public static final int SD_STREAMCIPHER_KEY_0 = 2;
    public static final String TAG = "MX.AppUtils";
    private static boolean _quitting;

    private static native long get(int i);

    private static native boolean get();

    public static native byte[] getSD(int i);

    private static native int getSystemTime();

    private static native void is(int i);

    private static native boolean is(byte[] bArr);

    private static native void put(int i);

    private static native int set(int i);

    private static native void set(Object obj, Object obj2, Object obj3, int i);

    public static native long ss_ctor(int i, long j);

    public static native int ss_d(long j, int i);

    public static native void ss_d(long j, byte[] bArr, int i, byte[] bArr2, int i2);

    public static native void ss_dtor(long j);

    public static native int ss_e(long j, int i);

    public static native void ss_e(long j, byte[] bArr, int i, int i2, byte[] bArr2);

    public static File getExternalDir(Context context) {
        return new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName());
    }

    public static File getExternalCacheDir(Context context) {
        return Build.VERSION.SDK_INT < 8 ? new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/cache") : context.getExternalCacheDir();
    }

    public static String getApplicationLabel() {
        try {
            PackageInfo info = MXApplication.context.getPackageManager().getPackageInfo(MXApplication.context.getPackageName(), 0);
            return MXApplication.context.getString(info.applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public static void uninstallCurrent(Context context) throws Exception {
        Uri packageURI = Uri.fromParts("package", context.getPackageName(), null);
        Intent uninstallIntent = new Intent("android.intent.action.DELETE", packageURI);
        context.startActivity(uninstallIntent);
    }

    public static boolean isSuperClass(Class<?> parent, Class<?> child) {
        while (child != null) {
            Class<?> superClass = child.getSuperclass();
            if (parent.equals(superClass)) {
                return true;
            }
            child = superClass;
        }
        return false;
    }

    public static Class<?> findActivityKindOf(Context context, Class<?> target) throws PackageManager.NameNotFoundException, ClassNotFoundException {
        ActivityInfo[] activityInfoArr;
        PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
        if (info.activities == null) {
            throw new ClassNotFoundException();
        }
        ClassLoader loader = AppUtils.class.getClassLoader();
        for (ActivityInfo a : info.activities) {
            if (a.targetActivity == null) {
                try {
                    Class<?> cls = loader.loadClass(a.name);
                    if (cls.equals(target) || isSuperClass(target, cls)) {
                        return cls;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new ClassNotFoundException();
    }

    public static boolean checkPermission(Context context, String permission) {
        int pid = Process.myPid();
        int uid = Process.myUid();
        return context.checkPermission(permission, pid, uid) == 0;
    }

    public static void exit(int delayed) {
        set(delayed);
    }

    public static void updateGoogleLicenseStatus(int status) {
        put(status);
    }

    public static void updateMXLicenseResult(int result) {
        is(result);
    }

    public static boolean applyLicenseProxy() {
        return get();
    }

    public static long getVerifiedTime(int serviceCode) {
        return get(serviceCode);
    }

    public static void setupLockWindow(WindowManager manager, View v, WindowManager.LayoutParams l, int type) {
        set(manager, v, l, type ^ (-1));
    }

    public static boolean checkSignature(byte[] bytes) {
        return is(bytes);
    }

    @SuppressLint({"NewApi"})
    public static void apply(SharedPreferences.Editor edit) {
        if (Build.VERSION.SDK_INT >= 9) {
            try {
                edit.apply();
                return;
            } catch (NoSuchMethodError e) {
                Log.w(TAG, "SharedPreferences.Editor.apply() is missed.");
            }
        }
        edit.commit();
    }

    public static boolean isQuitting() {
        return _quitting;
    }

    public static void quit(Context context) {
        quit(context, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class QuitHandler extends Handler {
        private final Context _context;

        QuitHandler(Context context) {
            super(Looper.getMainLooper());
            this._context = context;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (ActivityRegistry.activities.size() == 0) {
                    AppUtils.terminate(this._context, (Intent) msg.obj);
                    return;
                }
                for (Activity a : ActivityRegistry.activities.keySet()) {
                    a.finish();
                }
                sendMessageDelayed(obtainMessage(0, msg.obj), 1000L);
                return;
            }
            super.handleMessage(msg);
        }
    }

    public static void quit(Context context, Intent restart) {
        Log.d(TAG, "Quitting");
        _quitting = true;
        for (Activity a : ActivityRegistry.activities.keySet()) {
            a.finish();
        }
        Handler handler = new QuitHandler(context);
        handler.sendMessageDelayed(handler.obtainMessage(0, restart), 1000L);
    }

    public static void terminate(Context context) {
        terminate(context, null);
    }

    @SuppressLint({"NewApi"})
    public static void terminate(Context context, Intent restart) {
        Log.d(TAG, "Terminate");
        if (restart != null) {
            restart.addFlags(268468224);
            AlarmManager alm = (AlarmManager) context.getSystemService("alarm");
            alm.set(1, System.currentTimeMillis() + 100, PendingIntent.getActivity(context, 0, restart, 0));
        }
        if (Build.VERSION.SDK_INT >= 8 && checkPermission(context, "android.permission.KILL_BACKGROUND_PROCESSES")) {
            ActivityManager mng = (ActivityManager) context.getSystemService("activity");
            mng.killBackgroundProcesses(context.getPackageName());
        }
        System.runFinalization();
        System.gc();
        System.exit(0);
    }

    public static Intent makeMainActivty(ComponentName mainActivity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(mainActivity);
        intent.addCategory("android.intent.category.LAUNCHER");
        return intent;
    }

    public static String getNativeLibraryDir() {
        return getNativeLibraryDir(MXApplication.context.getApplicationInfo());
    }

    @SuppressLint({"NewApi"})
    public static String getNativeLibraryDir(ApplicationInfo app) {
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                return app.nativeLibraryDir;
            }
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }
        return app.dataDir + "/lib";
    }

    public static void loadLibrary(String directory, String name) throws UnsatisfiedLinkError {
        String path = new File(directory, System.mapLibraryName(name)).getAbsolutePath();
        System.load(path);
    }

    public static long getBuildDate(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile file = new ZipFile(info.sourceDir);
            ZipEntry entry = file.getEntry("classes.dex");
            long time = entry.getTime();
            file.close();
            return time;
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return 0L;
        }
    }

    public static int getVersion(int versionCode) {
        return versionCode % 10000;
    }

    @Nullable
    private static Themifier getThemifier(Context context) {
        if (context instanceof MXAppCompatActivity) {
            MXAppCompatActivity activity = (MXAppCompatActivity) context;
            if (activity.themifier == null) {
                activity.themifier = new Themifier(context);
            }
            return activity.themifier;
        } else if (context instanceof MXPreferenceActivity) {
            MXPreferenceActivity activity2 = (MXPreferenceActivity) context;
            if (activity2.themifier == null) {
                activity2.themifier = new Themifier(context);
            }
            return activity2.themifier;
        } else {
            return null;
        }
    }

    @SuppressLint({"NewApi"})
    public static Drawable getDrawable(Context context, int resId, boolean themify) {
        if (themify) {
            if (Build.VERSION.SDK_INT >= 21) {
                return context.getDrawable(resId);
            }
            Drawable d = context.getResources().getDrawable(resId);
            Themifier themifier = getThemifier(context);
            if (themifier != null) {
                themifier.themify(d);
                return d;
            }
            return d;
        }
        return context.getResources().getDrawable(resId);
    }

    public static void themify(Context context, Drawable drawable) {
        Themifier themifier;
        if (Build.VERSION.SDK_INT < 21 && (themifier = getThemifier(context)) != null) {
            themifier.themify(drawable);
        }
    }

    @SuppressLint({"NewApi"})
    public static void recreateIfPossible(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else if (activity instanceof MXAppCompatActivity) {
            ((MXAppCompatActivity) activity).recreate();
        } else {
            activity.finish();
        }
    }

    public static boolean mayRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkRootMethod3() {
        String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    public static Activity getActivityFrom(Context context) {
        while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    @Nullable
    public static Uri[] getExtraURIs(Intent intent, String key) {
        Parcelable[] parcels = intent.getParcelableArrayExtra(key);
        if (parcels != null) {
            Uri[] uris = new Uri[parcels.length];
            System.arraycopy(parcels, 0, uris, 0, parcels.length);
            return uris;
        }
        return null;
    }

    public static void dumpParams(String tag, Object thiz, String methodName, Intent intent, @Nullable Bundle saved) {
        StringBuilder sb = new StringBuilder();
        Bundle extras = intent.getExtras();
        sb.setLength(0);
        sb.append(methodName + " (").append(thiz).append(')');
        Log.v(tag, sb.toString());
        sb.setLength(0);
        sb.append("* dat=").append(intent.getData());
        Log.v(tag, sb.toString());
        sb.setLength(0);
        sb.append("* typ=").append(intent.getType());
        Log.v(tag, sb.toString());
        if (extras != null && extras.size() > 0) {
            sb.setLength(0);
            sb.append("    << Extra >>\n");
            int i = 0;
            for (String key : extras.keySet()) {
                i++;
                sb.append(' ').append(i).append(") ").append(key).append('=');
                Misc.appendDetails(sb, extras.get(key));
                sb.append('\n');
            }
            Log.v(tag, sb.toString());
        }
        if (saved != null) {
            sb.setLength(0);
            sb.append("    << Saved >>\n");
            int i2 = 0;
            for (String key2 : saved.keySet()) {
                i2++;
                sb.append(' ').append(i2).append(") ").append(key2).append('=');
                Misc.appendDetails(sb, saved.get(key2));
                sb.append('\n');
            }
            Log.v(tag, sb.toString());
        }
    }
}
