package com.example.floatdragview.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.example.floatdragview.APP;

public class DipPixelUtil {
    @SuppressLint({"DefaultLocale"})
    public static int dip2px(float f) {
        return (int) ((f * getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2pix(float f) {
        return (int) ((f * getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    private static Resources getResources() {
        try {
            return APP.context.getResources();
        } catch (Throwable unused) {
            return Resources.getSystem();
        }
    }
}
