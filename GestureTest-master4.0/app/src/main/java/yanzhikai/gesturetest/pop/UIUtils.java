package yanzhikai.gesturetest.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

public class UIUtils {

    /* renamed from: a */
    private static Context f78219a;

    /* renamed from: b */
    private static volatile C29826a f78220b;

    public static class C29826a {

        /* renamed from: a */
        public int f78223a;

        /* renamed from: b */
        public float f78224b;

        private C29826a() {
        }

        /* synthetic */ C29826a(byte b) {
            this();
        }

        /* renamed from: a */
        public final boolean m38933a() {
            return this.f78223a > 0 && this.f78224b > 0.0f;
        }
    }

    public static int dip2px(Context context, float f) {
        if (context == null) {
            return dip2px(f);
        }
        float f2 = 0.0f;
        if (f78220b != null) {
            f2 = f78220b.f78224b;
        } else {
            DisplayMetrics m38935a = m38935a(context);
            if (m38935a != null) {
                f2 = m38935a.density;
            }
        }
        return (int) ((f * f2) + 0.5f);
    }

    public static int dip2px(float f) {
        float f2;
        if (f78220b != null) {
            f2 = f78220b.f78224b;
        } else {
            DisplayMetrics m38935a = m38935a(f78219a);
            f2 = m38935a != null ? m38935a.density : 0.0f;
        }
        return (int) ((f * f2) + 0.5f);
    }

    private static DisplayMetrics m38935a(Context context) {
        if (context == null) {
            return null;
        }
        Resources resources = context.getResources();
        if (resources == null) {
            resources = Resources.getSystem();
        }
        if (resources == null) {
            return null;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        m38934a(displayMetrics);
        return displayMetrics;
    }

    private static void m38934a(DisplayMetrics displayMetrics) {
        if (displayMetrics == null || f78220b != null) {
            return;
        }
        C29826a c29826a = new C29826a((byte) 0);
        c29826a.f78223a = displayMetrics.densityDpi;
        c29826a.f78224b = displayMetrics.density;
        if (c29826a.m38933a()) {
            f78220b = c29826a;
        }
    }



    @SuppressLint("ResourceType")
    public static int getStatusBarHeight(Activity activity) {
        int identifier = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int dimensionPixelSize = identifier > 0 ? activity.getResources().getDimensionPixelSize(identifier) : 0;
        if (dimensionPixelSize <= 0) {
            Rect rect = new Rect();
            Window window = activity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            return window.findViewById(16908290).getTop() - rect.top;
        }
        return dimensionPixelSize;
    }




}
