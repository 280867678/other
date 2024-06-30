package yanzhikai.gesturetest.util;

import android.app.Activity;
import android.os.Build;

public class MethodCompat {

    public static boolean m43025b(Activity activity) {
        return !m43034a(activity);
    }

    public static boolean m43034a(Activity activity) {
        if (activity == null) {
            return true;
        }
        return (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) || activity.isFinishing() || activity.getWindow() == null;
    }


}
