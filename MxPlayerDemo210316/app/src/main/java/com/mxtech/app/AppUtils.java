package com.mxtech.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mxtech.Misc;
import com.mxtech.videoplayer.App;

import java.io.File;

public class AppUtils {



    private static native long get(int i);

    private static native boolean get();

    public static native byte[] getSD(int i);

    private static native int getSystemTime();

    private static native void is(int i);

    private static native boolean is(byte[] bArr);

    private static native void put(int i);

    private static native void pv(long j);

    private static native long pw();

    private static native int set(int i);

    private static native void set(Object obj, Object obj2, Object obj3, int i);

    public static native long ss_ctor(int i, long j);

    public static native int ss_d(long j, int i);

    public static native void ss_d(long j, byte[] bArr, int i, byte[] bArr2, int i2);

    public static native void ss_dtor(long j);

    public static native int ss_e(long j, int i);

    public static native void ss_e(long j, byte[] bArr, int i, int i2, byte[] bArr2);




    public static int getVersion(int versionCode) {
        return versionCode % 10000;
    }




    public static void loadLibrary(String directory, String name) throws UnsatisfiedLinkError {
        String path = new File(directory, System.mapLibraryName(name)).getAbsolutePath();
        System.load(path);
    }



    public static String getNativeLibraryDir() {
        return getNativeLibraryDir(App.context.getApplicationInfo());
    }
    public static String getNativeLibraryDir(ApplicationInfo app) {
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                return app.nativeLibraryDir;
            }
        } catch (Throwable e) {
            Log.e("AppUtils:", "", e);
        }
        return app.dataDir + "/lib";
    }









    public static final String TAG = "MX.AppUtils";

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




    public static void dumpParams(String tag, Object thiz, String methodName, @NonNull Intent intent, @Nullable Bundle saved) {
        StringBuilder sb = new StringBuilder();
        String tag2 = tag + "/Dump";
        sb.append(methodName).append(" (").append(thiz).append(')');
        Log.v(tag2, sb.toString());
        sb.setLength(0);
        sb.append("* data=").append(intent.getData());
        Log.v(tag2, sb.toString());
        sb.setLength(0);
        sb.append("* type=").append(intent.getType());
        Log.v(tag2, sb.toString());
        sb.setLength(0);
        sb.append("* Package=").append(intent.getPackage());
        Log.v(tag2, sb.toString());
        sb.setLength(0);
        sb.append("* Component=").append(intent.getComponent());
        Log.v(tag2, sb.toString());
        sb.setLength(0);
        sb.append("* Flags=").append(intent.getFlags());
        Log.v(tag2, sb.toString());
        Bundle extras = intent.getExtras();
        if (extras != null && extras.size() > 0) {
            sb.setLength(0);
            sb.append("    << Extra >>\n");
            int i = 0;
            for (String key : extras.keySet()) {
                i++;
                sb.append(' ').append(i).append(") ").append(key).append('=');
                Misc.appendDetails(sb, extras.get(key), 3);
                sb.append('\n');
            }
            Log.v(tag2, sb.toString());
        }
        if (saved != null) {
            sb.setLength(0);
            sb.append("    << Saved >>\n");
            int i2 = 0;
            for (String key2 : saved.keySet()) {
                i2++;
                sb.append(' ').append(i2).append(") ").append(key2).append('=');
                Misc.appendDetails(sb, saved.get(key2), 3);
                sb.append('\n');
            }
            Log.v(tag2, sb.toString());
        }
    }




}
