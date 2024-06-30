package com.carrydream.cardrecorder.tool;

import android.text.TextUtils;
import com.huantansheng.easyphotos.utils.file.FileUtils;
import java.util.Locale;

/* loaded from: classes.dex */
public class LogUtil {
    private static final String customTagPrefix = "x_log";
    private static final boolean isDebug = false;

    public static void d(String str) {
    }

    public static void d(String str, Throwable th) {
    }

    public static void e(String str) {
    }

    public static void e(String str, Throwable th) {
    }

    public static void i(String str) {
    }

    public static void i(String str, Throwable th) {
    }

    public static void v(String str) {
    }

    public static void v(String str, Throwable th) {
    }

    public static void w(String str) {
    }

    public static void w(String str, Throwable th) {
    }

    public static void w(Throwable th) {
    }

    public static void wtf(String str) {
    }

    public static void wtf(String str, Throwable th) {
    }

    public static void wtf(Throwable th) {
    }

    private LogUtil() {
    }

    private static String generateTag() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        String format = String.format(Locale.getDefault(), "%s.%s(L:%d)", className.substring(className.lastIndexOf(FileUtils.HIDDEN_PREFIX) + 1), stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber()));
        if (TextUtils.isEmpty(customTagPrefix)) {
            return format;
        }
        return "x_log:" + format;
    }
}
