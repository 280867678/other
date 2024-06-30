package com.mxtech.graphics;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

/* loaded from: classes.dex */
public final class GraphicUtils {
    public static Drawable safeMutate(Drawable d) {
        return (Build.VERSION.SDK_INT >= 8 || !(d instanceof StateListDrawable)) ? d.mutate() : d;
    }
}
