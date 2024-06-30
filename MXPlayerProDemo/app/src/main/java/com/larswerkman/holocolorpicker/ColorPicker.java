package com.larswerkman.holocolorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes2.dex */
public class ColorPicker extends View {
    private static final int[] COLORS = {SupportMenu.CATEGORY_MASK, -65281, -16776961, -16711681, -16711936, InputDeviceCompat.SOURCE_ANY, SupportMenu.CATEGORY_MASK};
    private static final String STATE_ANGLE = "angle";
    private static final String STATE_OLD_COLOR = "color";
    private static final String STATE_PARENT = "parent";
    private static final String STATE_SHOW_OLD_COLOR = "showColor";
    private float _pointerColorAngle;
    private float mAngle;
    private Paint mCenterHaloPaint;
    protected int mCenterNewColor;
    private Paint mCenterNewPaint;
    protected int mCenterOldColor;
    private Paint mCenterOldPaint;
    protected RectF mCenterRectangle;
    private int mColorCenterHaloRadius;
    protected int mColorCenterRadius;
    private int mColorPointerHaloRadius;
    private int mColorPointerRadius;
    private Paint mColorWheelPaint;
    private int mColorWheelRadius;
    private RectF mColorWheelRectangle;
    private int mColorWheelThickness;
    private float[] mHSV;
    private OpacityBar mOpacityBar;
    private Paint mPointerColor;
    private Paint mPointerHaloPaint;
    private int mPreferredColorCenterHaloRadius;
    private int mPreferredColorCenterRadius;
    private int mPreferredColorWheelRadius;
    private SVBar mSVbar;
    private SaturationBar mSaturationBar;
    protected boolean mShowCenterOldColor;
    private float mSlopX;
    private float mSlopY;
    private boolean mTouchAnywhereOnColorWheelEnabled;
    protected float mTranslationOffset;
    private boolean mUserIsMovingPointer;
    private ValueBar mValueBar;
    private int oldChangedListenerColor;
    private int oldSelectedListenerColor;
    private OnColorChangedListener onColorChangedListener;
    private OnColorSelectedListener onColorSelectedListener;

    /* loaded from: classes2.dex */
    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    /* loaded from: classes2.dex */
    public interface OnColorSelectedListener {
        void onColorSelected(int i);
    }

    public ColorPicker(Context context) {
        super(context);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        this.mSVbar = null;
        this.mOpacityBar = null;
        this.mSaturationBar = null;
        this.mTouchAnywhereOnColorWheelEnabled = true;
        this.mValueBar = null;
        init(null, 0, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        this.mSVbar = null;
        this.mOpacityBar = null;
        this.mSaturationBar = null;
        this.mTouchAnywhereOnColorWheelEnabled = true;
        this.mValueBar = null;
        init(attrs, 0, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        this.mSVbar = null;
        this.mOpacityBar = null;
        this.mSaturationBar = null;
        this.mTouchAnywhereOnColorWheelEnabled = true;
        this.mValueBar = null;
        init(attrs, defStyle, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        this.mSVbar = null;
        this.mOpacityBar = null;
        this.mSaturationBar = null;
        this.mTouchAnywhereOnColorWheelEnabled = true;
        this.mValueBar = null;
        init(attrs, defStyleAttr, defStyleRes);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.onColorChangedListener = listener;
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return this.onColorChangedListener;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    public OnColorSelectedListener getOnColorSelectedListener() {
        return this.onColorSelectedListener;
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker, defStyleAttr, defStyleRes);
        Resources b = getContext().getResources();
        this.mColorWheelThickness = a.getDimensionPixelSize(R.styleable.ColorPicker_color_wheel_thickness, b.getDimensionPixelSize(R.dimen.color_wheel_thickness));
        this.mColorWheelRadius = a.getDimensionPixelSize(R.styleable.ColorPicker_color_wheel_radius, b.getDimensionPixelSize(R.dimen.color_wheel_radius));
        this.mPreferredColorWheelRadius = this.mColorWheelRadius;
        this.mColorCenterRadius = a.getDimensionPixelSize(R.styleable.ColorPicker_color_center_radius, b.getDimensionPixelSize(R.dimen.color_center_radius));
        this.mPreferredColorCenterRadius = this.mColorCenterRadius;
        this.mColorCenterHaloRadius = a.getDimensionPixelSize(R.styleable.ColorPicker_color_center_halo_radius, b.getDimensionPixelSize(R.dimen.color_center_halo_radius));
        this.mPreferredColorCenterHaloRadius = this.mColorCenterHaloRadius;
        this.mColorPointerRadius = a.getDimensionPixelSize(R.styleable.ColorPicker_color_pointer_radius, b.getDimensionPixelSize(R.dimen.color_pointer_radius));
        this.mColorPointerHaloRadius = a.getDimensionPixelSize(R.styleable.ColorPicker_color_pointer_halo_radius, b.getDimensionPixelSize(R.dimen.color_pointer_halo_radius));
        a.recycle();
        this.mAngle = -1.5707964f;
        this._pointerColorAngle = -99999.0f;
        Shader s = new SweepGradient(0.0f, 0.0f, COLORS, (float[]) null);
        this.mColorWheelPaint = new Paint(1);
        this.mColorWheelPaint.setShader(s);
        this.mColorWheelPaint.setStyle(Paint.Style.STROKE);
        this.mColorWheelPaint.setStrokeWidth(this.mColorWheelThickness);
        this.mPointerHaloPaint = new Paint(1);
        this.mPointerHaloPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPointerHaloPaint.setAlpha(80);
        this.mPointerColor = new Paint(1);
        this.mCenterNewPaint = new Paint(1);
        this.mCenterNewPaint.setStyle(Paint.Style.FILL);
        this.mCenterOldPaint = new Paint(1);
        this.mCenterOldPaint.setStyle(Paint.Style.FILL);
        this.mCenterHaloPaint = new Paint(1);
        this.mCenterHaloPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mCenterHaloPaint.setAlpha(0);
        this.mShowCenterOldColor = true;
        update();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        canvas.translate(this.mTranslationOffset, this.mTranslationOffset);
        canvas.drawOval(this.mColorWheelRectangle, this.mColorWheelPaint);
        float[] pointerPosition = calculatePointerPosition(this.mAngle);
        canvas.drawCircle(pointerPosition[0], pointerPosition[1], this.mColorPointerHaloRadius, this.mPointerHaloPaint);
        canvas.drawCircle(pointerPosition[0], pointerPosition[1], this.mColorPointerRadius, this.mPointerColor);
        canvas.drawCircle(0.0f, 0.0f, this.mColorCenterHaloRadius, this.mCenterHaloPaint);
        if (this.mShowCenterOldColor) {
            canvas.drawArc(this.mCenterRectangle, 90.0f, 180.0f, true, this.mCenterOldPaint);
            canvas.drawArc(this.mCenterRectangle, 270.0f, 180.0f, true, this.mCenterNewPaint);
            return;
        }
        canvas.drawArc(this.mCenterRectangle, 0.0f, 360.0f, true, this.mCenterNewPaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int intrinsicSize = (this.mPreferredColorWheelRadius + this.mColorPointerHaloRadius) * 2;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(intrinsicSize, widthSize);
        } else {
            width = intrinsicSize;
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        } else if (heightMode == Integer.MIN_VALUE) {
            height = Math.min(intrinsicSize, heightSize);
        } else {
            height = intrinsicSize;
        }
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        this.mTranslationOffset = min * 0.5f;
        this.mColorWheelRadius = ((min / 2) - this.mColorWheelThickness) - this.mColorPointerHaloRadius;
        this.mColorWheelRectangle.set(-this.mColorWheelRadius, -this.mColorWheelRadius, this.mColorWheelRadius, this.mColorWheelRadius);
        this.mColorCenterRadius = (int) (this.mPreferredColorCenterRadius * (this.mColorWheelRadius / this.mPreferredColorWheelRadius));
        this.mColorCenterHaloRadius = (int) (this.mPreferredColorCenterHaloRadius * (this.mColorWheelRadius / this.mPreferredColorWheelRadius));
        this.mCenterRectangle.set(-this.mColorCenterRadius, -this.mColorCenterRadius, this.mColorCenterRadius, this.mColorCenterRadius);
    }

    private int calculateColor(float angle) {
        int opacity;
        Color.colorToHSV(this.mCenterNewColor, this.mHSV);
        float saturation = this.mHSV[1];
        float value = this.mHSV[2];
        if (this.mSaturationBar != null) {
            Color.colorToHSV(this.mSaturationBar.getColor(), this.mHSV);
            saturation = this.mHSV[1];
        }
        if (this.mValueBar != null) {
            Color.colorToHSV(this.mValueBar.getColor(), this.mHSV);
            value = this.mHSV[2];
        }
        if (this.mOpacityBar != null) {
            opacity = this.mOpacityBar.getOpacity();
        } else {
            opacity = Color.alpha(this.mCenterNewColor);
        }
        return calculateColor(angle, saturation, value, opacity);
    }

    private int calculateColor(float angle, float saturation, float value, int opacity) {
        float angle2 = (float) ((angle * (-1.0f)) % 6.283185307179586d);
        if (angle2 < 0.0f) {
            angle2 = (float) (angle2 + 6.283185307179586d);
        }
        this.mHSV[0] = (float) Math.toDegrees(angle2);
        this.mHSV[1] = saturation;
        this.mHSV[2] = value;
        int color = Color.HSVToColor(opacity, this.mHSV);
        return color;
    }

    public int getColor() {
        return this.mCenterNewColor;
    }

    public void setPointerHaloColor(int color) {
        if (color != this.mPointerHaloPaint.getColor()) {
            this.mPointerHaloPaint.setColor(color);
            invalidate();
        }
    }

    public int getPointerHaloColor() {
        return this.mPointerHaloPaint.getColor();
    }

    public void setColor(int color) {
        this.mAngle = colorToAngle(color);
        if (this.mOpacityBar != null) {
            this.mOpacityBar.setOpacity(Color.alpha(color));
        }
        if (this.mSVbar != null) {
            Color.colorToHSV(color, this.mHSV);
            if (this.mHSV[1] < this.mHSV[2]) {
                this.mSVbar.setSaturation(this.mHSV[1]);
            } else {
                this.mSVbar.setValue(this.mHSV[2]);
            }
        }
        if (this.mSaturationBar != null) {
            Color.colorToHSV(color, this.mHSV);
            this.mSaturationBar.setSaturation(this.mHSV[1]);
        }
        if (this.mValueBar != null && this.mSaturationBar == null) {
            Color.colorToHSV(color, this.mHSV);
            this.mValueBar.setValue(this.mHSV[2]);
        } else if (this.mValueBar != null) {
            Color.colorToHSV(color, this.mHSV);
            this.mValueBar.setValue(this.mHSV[2]);
        }
        setNewCenterColor(color);
    }

    public float getAngle() {
        return this.mAngle;
    }

    public void setAngle(float angle) {
        this.mAngle = angle;
        update();
    }

    private float colorToAngle(int color) {
        float[] colors = new float[3];
        Color.colorToHSV(color, colors);
        return (float) Math.toRadians(-colors[0]);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = event.getX() - this.mTranslationOffset;
        float y = event.getY() - this.mTranslationOffset;
        switch (event.getAction()) {
            case 0:
                float[] pointerPosition = calculatePointerPosition(this.mAngle);
                if (x >= pointerPosition[0] - this.mColorPointerHaloRadius && x <= pointerPosition[0] + this.mColorPointerHaloRadius && y >= pointerPosition[1] - this.mColorPointerHaloRadius && y <= pointerPosition[1] + this.mColorPointerHaloRadius) {
                    this.mSlopX = x - pointerPosition[0];
                    this.mSlopY = y - pointerPosition[1];
                    this.mUserIsMovingPointer = true;
                    invalidate();
                    break;
                } else if (x >= (-this.mColorCenterRadius) && x <= this.mColorCenterRadius && y >= (-this.mColorCenterRadius) && y <= this.mColorCenterRadius && this.mShowCenterOldColor) {
                    this.mCenterHaloPaint.setAlpha(80);
                    setColor(getOldCenterColor());
                    invalidate();
                    break;
                } else if (Math.sqrt((x * x) + (y * y)) <= this.mColorWheelRadius + this.mColorPointerHaloRadius && Math.sqrt((x * x) + (y * y)) >= this.mColorWheelRadius - this.mColorPointerHaloRadius && this.mTouchAnywhereOnColorWheelEnabled) {
                    this.mUserIsMovingPointer = true;
                    invalidate();
                    break;
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                break;
            case 1:
                this.mUserIsMovingPointer = false;
                this.mCenterHaloPaint.setAlpha(0);
                if (this.onColorSelectedListener != null && this.mCenterNewColor != this.oldSelectedListenerColor) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                }
                invalidate();
                break;
            case 2:
                if (this.mUserIsMovingPointer) {
                    setAngle((float) Math.atan2(y - this.mSlopY, x - this.mSlopX));
                    break;
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            case 3:
                if (this.onColorSelectedListener != null && this.mCenterNewColor != this.oldSelectedListenerColor) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                    break;
                }
                break;
        }
        return true;
    }

    private float[] calculatePointerPosition(float angle) {
        float x = (float) (this.mColorWheelRadius * Math.cos(angle));
        float y = (float) (this.mColorWheelRadius * Math.sin(angle));
        return new float[]{x, y};
    }

    public void addSVBar(SVBar bar) {
        this.mSVbar = bar;
        this.mSVbar.setColorPicker(this);
        this.mSVbar.setColor(this.mPointerColor.getColor());
    }

    public void addOpacityBar(OpacityBar bar) {
        this.mOpacityBar = bar;
        this.mOpacityBar.setColorPicker(this);
        this.mOpacityBar.setColor(this.mPointerColor.getColor());
    }

    public void addSaturationBar(SaturationBar bar) {
        this.mSaturationBar = bar;
        this.mSaturationBar.setColorPicker(this);
        this.mSaturationBar.setColor(this.mPointerColor.getColor());
    }

    public void addValueBar(ValueBar bar) {
        this.mValueBar = bar;
        this.mValueBar.setColorPicker(this);
        this.mValueBar.setColor(this.mPointerColor.getColor());
    }

    public void update() {
        setNewCenterColor(calculateColor(this.mAngle));
    }

    private void setNewCenterColor(int color) {
        boolean redraw = false;
        if (color != this.mCenterNewColor) {
            this.mCenterNewColor = color;
            this.mCenterNewPaint.setColor(color);
            if (this.mCenterOldColor == 0) {
                this.mCenterOldColor = color;
                this.mCenterOldPaint.setColor(color);
            }
            if (this.onColorChangedListener != null && color != this.oldChangedListenerColor) {
                this.onColorChangedListener.onColorChanged(color);
                this.oldChangedListenerColor = color;
            }
            redraw = true;
        }
        if (this._pointerColorAngle != this.mAngle) {
            this._pointerColorAngle = this.mAngle;
            int painterColor = calculateColor(this.mAngle, 1.0f, 1.0f, 255);
            this.mPointerColor.setColor(painterColor);
            if (this.mOpacityBar != null) {
                this.mOpacityBar.setColor(painterColor);
            }
            if (this.mValueBar != null) {
                this.mValueBar.setColor(painterColor);
            }
            if (this.mSaturationBar != null) {
                this.mSaturationBar.setColor(painterColor);
            }
            if (this.mSVbar != null) {
                this.mSVbar.setColor(painterColor);
            }
            redraw = true;
        }
        if (redraw) {
            invalidate();
        }
    }

    public void setOldCenterColor(int color) {
        if (this.mCenterOldColor != color) {
            this.mCenterOldColor = color;
            this.mCenterOldPaint.setColor(color);
            invalidate();
        }
    }

    public int getOldCenterColor() {
        return this.mCenterOldColor;
    }

    public void setShowOldCenterColor(boolean show) {
        if (this.mShowCenterOldColor != show) {
            this.mShowCenterOldColor = show;
            invalidate();
        }
    }

    public boolean getShowOldCenterColor() {
        return this.mShowCenterOldColor;
    }

    public void changeOpacityBarColor(int color) {
        if (this.mOpacityBar != null) {
            this.mOpacityBar.setColor(color);
        }
    }

    public void changeSaturationBarColor(int color) {
        if (this.mSaturationBar != null) {
            this.mSaturationBar.setColor(color);
        }
    }

    public void changeValueBarColor(int color) {
        if (this.mValueBar != null) {
            this.mValueBar.setColor(color);
        }
    }

    public boolean hasOpacityBar() {
        return this.mOpacityBar != null;
    }

    public boolean hasValueBar() {
        return this.mValueBar != null;
    }

    public boolean hasSaturationBar() {
        return this.mSaturationBar != null;
    }

    public boolean hasSVBar() {
        return this.mSVbar != null;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable(STATE_PARENT, superState);
        state.putFloat(STATE_ANGLE, this.mAngle);
        state.putInt(STATE_OLD_COLOR, this.mCenterOldColor);
        state.putBoolean(STATE_SHOW_OLD_COLOR, this.mShowCenterOldColor);
        return state;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;
        Parcelable superState = savedState.getParcelable(STATE_PARENT);
        super.onRestoreInstanceState(superState);
        this.mAngle = savedState.getFloat(STATE_ANGLE);
        setOldCenterColor(savedState.getInt(STATE_OLD_COLOR));
        this.mShowCenterOldColor = savedState.getBoolean(STATE_SHOW_OLD_COLOR);
        update();
    }

    public void setTouchAnywhereOnColorWheelEnabled(boolean TouchAnywhereOnColorWheelEnabled) {
        this.mTouchAnywhereOnColorWheelEnabled = TouchAnywhereOnColorWheelEnabled;
    }

    public boolean getTouchAnywhereOnColorWheel() {
        return this.mTouchAnywhereOnColorWheelEnabled;
    }
}
