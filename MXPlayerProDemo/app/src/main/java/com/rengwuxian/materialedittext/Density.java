package com.rengwuxian.materialedittext;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Density {
    Density() {
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(1, dp, r.getDisplayMetrics());
        return Math.round(px);
    }
}
