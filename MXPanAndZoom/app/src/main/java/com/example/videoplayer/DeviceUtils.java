package com.example.videoplayer;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DeviceUtils {
    public static int densityDpi;
    public static float dragMinimumPixel;


    static {
        APP.context.getPackageManager();
        Resources res = APP.context.getResources();
        DisplayMetrics disp = res.getDisplayMetrics();
//        onConfigurationChanged(res.getConfiguration());
        densityDpi = disp.densityDpi;
        dragMinimumPixel = (float) MeterToPixel(0.003d);
    }

    public static double MeterToPixel(double meter) {
        return 39.3701d * meter * densityDpi;
    }



}
