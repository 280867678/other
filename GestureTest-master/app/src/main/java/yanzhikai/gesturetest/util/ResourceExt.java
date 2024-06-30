package yanzhikai.gesturetest.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

//import com.xunlei.uikit.utils.darkmode.ThemeDayNightContextWarp;


import yanzhikai.gesturetest.App;

public final class ResourceExt {
    /* renamed from: a */
    public static final int m5919a(Context context, @ColorRes int i) {
        if (context == null) {
            context = getContext();
        }
        return ContextCompat.getColor(context, i);
    }

//    @Deprecated(message = "不传入context会导致当前页面切换暗色失效")
    /* renamed from: a */
    public static final int m5920a(@ColorRes int i) {
        return ContextCompat.getColor(getContext(), i);
    }

    /* renamed from: b */
    public static final String m5918b(@StringRes int i) {
        String string = m5921a().getString(i);
//        Intrinsics.checkNotNullExpressionValue(string, "res.getString(id)");
        return string;
    }

    /* renamed from: c */
    public static final Drawable m5916c(@DrawableRes int i) {
        return ContextCompat.getDrawable(getContext(), i);
    }

    /* renamed from: b */
    public static final Drawable m5917b(Context context, @DrawableRes int i) {
        if (context == null) {
            context = getContext();
        }
        return ContextCompat.getDrawable(context, i);
    }

    /* renamed from: d */
    public static final int m5915d(@DimenRes int i) {
        return m5921a().getDimensionPixelOffset(i);
    }

    /* renamed from: a */
    private static final Resources m5921a() {
        Resources resources = getContext().getResources();
//        Intrinsics.checkNotNullExpressionValue(resources, "context.resources");
        return resources;
    }

    private static final Context getContext() {
//        return ThemeDayNightContextWarp.f82226a.getContext();
        return App.context;
    }
}

