package com.mxtech.graphics;

import android.annotation.SuppressLint;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class SelectorDrawable extends StateListDrawable {
    @SuppressLint({"UseSparseArrays"})
    private final Map<Integer, ColorFilter> _colorFilters = new HashMap();

    public final void setTintTo(int id, int color) {
        setTintTo(id, new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    public void setTintTo(int id, @Nullable ColorFilter cf) {
        this._colorFilters.put(Integer.valueOf(id), cf);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.StateListDrawable, android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] states) {
        if (super.onStateChange(states)) {
            return applyTintTo(getCurrent());
        }
        return false;
    }

    private boolean applyTintTo(Drawable drawable) {
        boolean changed = false;
        if (drawable instanceof LayerDrawable) {
            for (Map.Entry<Integer, ColorFilter> entry : this._colorFilters.entrySet()) {
                int id = entry.getKey().intValue();
                Drawable d = ((LayerDrawable) drawable).findDrawableByLayerId(id);
                if (d != null) {
                    d.setColorFilter(entry.getValue());
                    changed = true;
                }
            }
        }
        return changed;
    }
}
