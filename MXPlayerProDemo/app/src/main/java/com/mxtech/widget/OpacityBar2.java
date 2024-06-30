package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.mxtech.DrawUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public final class OpacityBar2 extends OpacityBar {
    private static int _colorNormal = 1342177280;
    private int _colorAccent;

    public OpacityBar2(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public OpacityBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public OpacityBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public OpacityBar2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerBars, defStyleAttr, defStyleRes);
        setFocusable(a.getBoolean(R.styleable.ColorPickerBars_android_focusable, true));
        setFocusableInTouchMode(a.getBoolean(R.styleable.ColorPickerBars_android_focusableInTouchMode, true));
        this._colorAccent = (a.getColor(R.styleable.ColorPickerBars_colorAccent, -7829368) & ViewCompat.MEASURED_SIZE_MASK) | Integer.MIN_VALUE;
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.larswerkman.holocolorpicker.OpacityBar, android.view.View
    public void onDraw(Canvas canvas) {
        DrawUtils.drawAlphaPattern(canvas, this.mBarRect);
        super.onDraw(canvas);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getOrientation()) {
            if (keyCode == 21) {
                change(false, event);
                return true;
            } else if (keyCode == 22) {
                change(true, event);
                return true;
            }
        } else if (keyCode == 19) {
            change(false, event);
            return true;
        } else if (keyCode == 20) {
            change(true, event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void change(boolean increase, KeyEvent event) {
        int delta;
        if (event.getAction() == 0) {
            if (increase) {
                delta = event.getRepeatCount() + 1;
            } else {
                delta = (-event.getRepeatCount()) - 1;
            }
            change(delta);
        }
    }

    public void change(int delta) {
        int opacity = getOpacity() + delta;
        if (opacity < 0) {
            opacity = 0;
        } else if (opacity > 255) {
            opacity = 255;
        }
        setOpacity(opacity);
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        int[] drawableState;
        super.drawableStateChanged();
        int oldColor = getPointerHaloColor();
        int newColor = _colorNormal;
        for (int state : getDrawableState()) {
            if (state == 16842913 || state == 16842908) {
                newColor = this._colorAccent;
                break;
            }
        }
        if (oldColor != newColor) {
            setPointerHaloColor(newColor);
        }
    }

    @Override // com.larswerkman.holocolorpicker.OpacityBar, android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            requestFocus();
        }
        return super.onTouchEvent(event);
    }
}
