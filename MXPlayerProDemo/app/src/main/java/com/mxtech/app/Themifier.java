package com.mxtech.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.pro.R;

@SuppressLint({"InlinedApi"})
/* loaded from: classes.dex */
public final class Themifier {
    static final String TAG = "MX.Themifier";
    private final ColorFilter[] _colorFilters = new ColorFilter[R.styleable.Themifier.length];

    /* JADX INFO: Access modifiers changed from: package-private */
    public Themifier(Context context) {
        try {
            TypedArray a = context.obtainStyledAttributes(R.styleable.Themifier);
            for (int i = a.getIndexCount() - 1; i >= 0; i--) {
                int index = a.getIndex(i);
                int tintColor = a.getColor(index, 0);
                if (tintColor != 0) {
                    this._colorFilters[index] = new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                }
            }
            a.recycle();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void themify(Drawable d) {
        int colorIndex;
        if (d.isStateful()) {
            boolean checked = false;
            int[] states = d.getState();
            int i = 0;
            while (true) {
                if (i >= states.length) {
                    break;
                } else if (states[i] != 16842912) {
                    i++;
                } else {
                    checked = true;
                    break;
                }
            }
            if (checked) {
                colorIndex = R.styleable.Themifier_colorControlActivated;
            } else {
                colorIndex = R.styleable.Themifier_colorControlNormal;
            }
            ColorFilter colorFilter = this._colorFilters[colorIndex];
            if (colorFilter != null) {
                GraphicUtils.safeMutate(d).setColorFilter(colorFilter);
                return;
            }
            return;
        }
        ColorFilter colorFilter2 = this._colorFilters[R.styleable.Themifier_colorControlNormal];
        if (colorFilter2 != null) {
            GraphicUtils.safeMutate(d).setColorFilter(colorFilter2);
        }
    }
}
