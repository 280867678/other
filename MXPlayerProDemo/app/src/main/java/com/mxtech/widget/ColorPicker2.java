package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mxtech.DrawUtils;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.subtitle.SubView;

/* loaded from: classes2.dex */
public final class ColorPicker2 extends ColorPicker {
    private static int _colorNormal = 1342177280;
    private Bitmap _alphaPatternBitmap;
    private int _colorAccent;
    private boolean _horizontal;

    public ColorPicker2(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ColorPicker2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ColorPicker2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    public ColorPicker2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPicker2, defStyleAttr, defStyleRes);
        setFocusable(a.getBoolean(R.styleable.ColorPicker2_android_focusable, true));
        setFocusableInTouchMode(a.getBoolean(R.styleable.ColorPicker2_android_focusableInTouchMode, true));
        this._colorAccent = (a.getColor(R.styleable.ColorPicker2_colorAccent, -7829368) & ViewCompat.MEASURED_SIZE_MASK) | Integer.MIN_VALUE;
        this._horizontal = a.getBoolean(R.styleable.ColorPicker2_bar_orientation_horizontal, true);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.larswerkman.holocolorpicker.ColorPicker, android.view.View
    public void onDraw(Canvas canvas) {
        if (Color.alpha(this.mCenterNewColor) != 255 || (this.mShowCenterOldColor && Color.alpha(this.mCenterOldColor) != 255)) {
            canvas.drawBitmap(getAlphaPatternBitmap(), this.mTranslationOffset - this.mColorCenterRadius, this.mTranslationOffset - this.mColorCenterRadius, (Paint) null);
        }
        super.onDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.larswerkman.holocolorpicker.ColorPicker, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this._alphaPatternBitmap = null;
    }

    private Bitmap getAlphaPatternBitmap() {
        if (this._alphaPatternBitmap == null) {
            this._alphaPatternBitmap = Bitmap.createBitmap(this.mColorCenterRadius * 2, this.mColorCenterRadius * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this._alphaPatternBitmap);
            DrawUtils.drawAlphaPattern(canvas);
            Bitmap maskBitmap = Bitmap.createBitmap(this.mColorCenterRadius * 2, this.mColorCenterRadius * 2, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);
            Paint paint = new Paint(1);
            paint.setColor(-1);
            paint.setStyle(Paint.Style.FILL);
            maskCanvas.translate(this.mColorCenterRadius, this.mColorCenterRadius);
            maskCanvas.drawCircle(0.0f, 0.0f, this.mColorCenterRadius, paint);
            Paint erasePaint = new Paint();
            erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(maskBitmap, 0.0f, 0.0f, erasePaint);
        }
        return this._alphaPatternBitmap;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this._horizontal) {
            if (keyCode == 21) {
                change(false, event);
                return true;
            } else if (keyCode == 22) {
                change(true, event);
                return true;
            }
        } else if (keyCode == 19) {
            change(true, event);
            return true;
        } else if (keyCode == 20) {
            change(false, event);
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

    public float getHue() {
        float[] hsvColor = new float[3];
        Color.colorToHSV(getColor(), hsvColor);
        return hsvColor[0];
    }

    public void change(float delta) {
        double angle = (getAngle() + Math.toRadians(delta)) % 6.283185307179586d;
        if (angle < SubView.SUBTITLE_DRAG_GAP) {
            angle += 6.283185307179586d;
        }
        setAngle((float) angle);
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

    @Override // com.larswerkman.holocolorpicker.ColorPicker, android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            requestFocus();
        }
        return super.onTouchEvent(event);
    }
}
