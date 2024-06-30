package yanzhikai.gesturetest.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenTool {

    public static int getWidthRealTime(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        m38936a(context, displayMetrics);
        return displayMetrics.widthPixels;
    }

    private static void m38936a(Context context, DisplayMetrics displayMetrics) {
        if (context == null) {
            return;
        }
        Display display = null;
        try {
            @SuppressLint("WrongConstant") WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (windowManager != null) {
                display = windowManager.getDefaultDisplay();
            }
        } catch (RuntimeException e) {
//            C23083a.m50436a(e, 15885);
//            ExceptionUtils.printStackTrace((Exception) e);
        }
        if (display != null) {
            if (Build.VERSION.SDK_INT < 17) {
                display.getMetrics(displayMetrics);
                return;
            }
            try {
                display.getRealMetrics(displayMetrics);
            } catch (RuntimeException e2) {
//                C23083a.m50436a(e2, 15886);
//                ExceptionUtils.printStackTrace((Exception) e2);
            }
        }
    }



    public static boolean isLandScape(Context context) {
        return (context == null || context.getResources() == null || context.getResources().getConfiguration().orientation != 2) ? false : true;
    }









    @SuppressLint("WrongConstant")
    public static boolean isLandscape(Activity activity) {
        if (activity != null) {
            return 4 == activity.getRequestedOrientation() ? 2 == activity.getResources().getConfiguration().orientation : activity.getRequestedOrientation() == 0 || 6 == activity.getRequestedOrientation() || 8 == activity.getRequestedOrientation() || 11 == activity.getRequestedOrientation();
        }
        return false;
    }





}
