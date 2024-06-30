package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.mxtech.graphics.SelectorDrawable;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class ColoredButton extends ImageButton {
    public ColoredButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ColoredButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ColoredButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"NewApi"})
    public ColoredButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private Drawable newDrawable(TypedArray a, int shapeId, int overlayId) {
        Drawable shape = a.getDrawable(R.styleable.ColoredButton_shape).mutate();
        Drawable overlay = a.getDrawable(R.styleable.ColoredButton_overlay);
        if (overlay != null) {
            LayerDrawable ld = new LayerDrawable(new Drawable[]{shape, overlay});
            ld.setId(0, shapeId);
            ld.setId(1, overlayId);
            return ld;
        }
        LayerDrawable ld2 = new LayerDrawable(new Drawable[]{shape});
        ld2.setId(0, shapeId);
        return ld2;
    }

    private Drawable newDrawable(TypedArray a) {
        SelectorDrawable sl = new SelectorDrawable();
        int tintShapeDefault = a.getColor(R.styleable.ColoredButton_tintShapeDefault, 0);
        int tintShapePressed = a.getColor(R.styleable.ColoredButton_tintShapePressed, tintShapeDefault);
        int tintOverlayDefault = a.getColor(R.styleable.ColoredButton_tintOverlayDefault, 0);
        int tintOverlayPressed = a.getColor(R.styleable.ColoredButton_tintOverlayPressed, tintOverlayDefault);
        if (tintShapeDefault != 0) {
            sl.setTintTo(R.id.default_shape, tintShapeDefault);
        }
        if (tintShapePressed != 0) {
            sl.setTintTo(R.id.pressed_shape, tintShapePressed);
        }
        if (tintOverlayDefault != 0) {
            sl.setTintTo(R.id.default_overlay, tintOverlayDefault);
        }
        if (tintOverlayPressed != 0) {
            sl.setTintTo(R.id.pressed_overlay, tintOverlayPressed);
        }
        sl.addState(new int[]{16842919}, newDrawable(a, R.id.pressed_shape, R.id.pressed_overlay));
        sl.addState(new int[0], newDrawable(a, R.id.default_shape, R.id.default_overlay));
        return sl;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColoredButton, defStyleAttr, defStyleRes);
        if (a.hasValue(R.styleable.ColoredButton_shape)) {
            setImageDrawable(newDrawable(a));
        }
        a.recycle();
    }
}
