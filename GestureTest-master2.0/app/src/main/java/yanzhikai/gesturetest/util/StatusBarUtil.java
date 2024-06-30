package yanzhikai.gesturetest.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

public class StatusBarUtil {
    private static String f82228a = "StatusBarUtil";

    /* renamed from: b */
    private static boolean f82229b = false;

    /* renamed from: c */
    private static boolean f82230c = false;

    /* renamed from: d */
    private static boolean f82231d = false;

    /* renamed from: e */
    private static boolean f82232e;

    public static boolean m5905b(Context context) {
        if (!f82229b) {
            f82230c = m5899d(context) || m5895f(context) || m5894g(context) || m5914a();
            f82229b = true;
        }
        return f82230c;
    }


    public static boolean m5899d(Context context) {

        try {
            Class<?> loadClass = context.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
            return ((Boolean) loadClass.getMethod("hasNotchInScreen", new Class[0]).invoke(loadClass, new Object[0])).booleanValue();
        } catch (Exception unused) {
            Log.e("test", "hasNotchInScreen Exception");
            return false;
        }

    }

    private static boolean m5895f(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    private static boolean m5894g(Context context) {
        try {
            return ((Boolean) Class.forName("android.util.FtFeature").getMethod("isFeatureSupport", Integer.TYPE).invoke(null, 32)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean m5914a() {
        Class<?> cls = null;
        try {
            cls = Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return "1".equals((String) cls.getDeclaredMethod("get", String.class).invoke(cls.newInstance(), "ro.miui.notch"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void m5910a(View view) {
        if (view == null || Build.VERSION.SDK_INT < 19) {
            return;
        }
        view.setSystemUiVisibility(5894);
    }




}
