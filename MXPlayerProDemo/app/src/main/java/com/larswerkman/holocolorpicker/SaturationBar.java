package com.larswerkman.holocolorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes2.dex */
public class SaturationBar extends View {
    private static final boolean ORIENTATION_DEFAULT = true;
    public static final boolean ORIENTATION_HORIZONTAL = true;
    public static final boolean ORIENTATION_VERTICAL = false;
    private static final String STATE_COLOR = "color";
    private static final String STATE_ORIENTATION = "orientation";
    private static final String STATE_PARENT = "parent";
    private static final String STATE_SATURATION = "saturation";
    private int mBarLength;
    private Paint mBarPaint;
    private Paint mBarPointerHaloPaint;
    private int mBarPointerHaloRadius;
    private Paint mBarPointerPaint;
    private int mBarPointerPosition;
    private int mBarPointerRadius;
    private RectF mBarRect;
    private int mBarThickness;
    private int mColor;
    private float[] mHSVColor;
    private boolean mIsMovingPointer;
    private boolean mOrientation;
    private ColorPicker mPicker;
    private float mPosToSatFactor;
    private int mPreferredBarLength;
    private float mSatToPosFactor;
    private int oldChangedListenerSaturation;
    private OnSaturationChangedListener onSaturationChangedListener;
    private Shader shader;

    /* loaded from: classes2.dex */
    public interface OnSaturationChangedListener {
        void onSaturationChanged(int i);
    }

    public void setOnSaturationChangedListener(OnSaturationChangedListener listener) {
        this.onSaturationChangedListener = listener;
    }

    public OnSaturationChangedListener getOnSaturationChangedListener() {
        return this.onSaturationChangedListener;
    }

    public SaturationBar(Context context) {
        super(context);
        this.mBarRect = new RectF();
        this.mHSVColor = new float[3];
        this.mPicker = null;
        init(null, 0, 0);
    }

    public SaturationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBarRect = new RectF();
        this.mHSVColor = new float[3];
        this.mPicker = null;
        init(attrs, 0, 0);
    }

    public SaturationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mBarRect = new RectF();
        this.mHSVColor = new float[3];
        this.mPicker = null;
        init(attrs, defStyle, 0);
    }

    public SaturationBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mBarRect = new RectF();
        this.mHSVColor = new float[3];
        this.mPicker = null;
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorBars, defStyleAttr, defStyleRes);
        Resources b = getContext().getResources();
        this.mBarThickness = a.getDimensionPixelSize(R.styleable.ColorBars_bar_thickness, b.getDimensionPixelSize(R.dimen.bar_thickness));
        this.mBarLength = a.getDimensionPixelSize(R.styleable.ColorBars_bar_length, b.getDimensionPixelSize(R.dimen.bar_length));
        this.mPreferredBarLength = this.mBarLength;
        this.mBarPointerRadius = a.getDimensionPixelSize(R.styleable.ColorBars_bar_pointer_radius, b.getDimensionPixelSize(R.dimen.bar_pointer_radius));
        this.mBarPointerHaloRadius = a.getDimensionPixelSize(R.styleable.ColorBars_bar_pointer_halo_radius, b.getDimensionPixelSize(R.dimen.bar_pointer_halo_radius));
        this.mOrientation = a.getBoolean(R.styleable.ColorBars_bar_orientation_horizontal, true);
        a.recycle();
        this.mBarPaint = new Paint(1);
        this.mBarPaint.setShader(this.shader);
        this.mBarPointerPosition = this.mBarLength + this.mBarPointerHaloRadius;
        this.mBarPointerHaloPaint = new Paint(1);
        this.mBarPointerHaloPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mBarPointerHaloPaint.setAlpha(80);
        this.mBarPointerPaint = new Paint(1);
        this.mBarPointerPaint.setColor(-8257792);
        this.mPosToSatFactor = 1.0f / this.mBarLength;
        this.mSatToPosFactor = this.mBarLength / 1.0f;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpec;
        int length;
        int intrinsicSize = this.mPreferredBarLength + (this.mBarPointerHaloRadius * 2);
        if (this.mOrientation) {
            measureSpec = widthMeasureSpec;
        } else {
            measureSpec = heightMeasureSpec;
        }
        int lengthMode = View.MeasureSpec.getMode(measureSpec);
        int lengthSize = View.MeasureSpec.getSize(measureSpec);
        if (lengthMode == 1073741824) {
            length = lengthSize;
        } else if (lengthMode == Integer.MIN_VALUE) {
            length = Math.min(intrinsicSize, lengthSize);
        } else {
            length = intrinsicSize;
        }
        int barPointerHaloRadiusx2 = this.mBarPointerHaloRadius * 2;
        this.mBarLength = length - barPointerHaloRadiusx2;
        if (!this.mOrientation) {
            setMeasuredDimension(barPointerHaloRadiusx2, this.mBarLength + barPointerHaloRadiusx2);
        } else {
            setMeasuredDimension(this.mBarLength + barPointerHaloRadiusx2, barPointerHaloRadiusx2);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int x1;
        int y1;
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mOrientation) {
            x1 = this.mBarLength + this.mBarPointerHaloRadius;
            y1 = this.mBarThickness;
            this.mBarLength = w - (this.mBarPointerHaloRadius * 2);
            this.mBarRect.set(this.mBarPointerHaloRadius, this.mBarPointerHaloRadius - (this.mBarThickness / 2), this.mBarLength + this.mBarPointerHaloRadius, this.mBarPointerHaloRadius + (this.mBarThickness / 2));
        } else {
            x1 = this.mBarThickness;
            y1 = this.mBarLength + this.mBarPointerHaloRadius;
            this.mBarLength = h - (this.mBarPointerHaloRadius * 2);
            this.mBarRect.set(this.mBarPointerHaloRadius - (this.mBarThickness / 2), this.mBarPointerHaloRadius, this.mBarPointerHaloRadius + (this.mBarThickness / 2), this.mBarLength + this.mBarPointerHaloRadius);
        }
        if (!isInEditMode()) {
            this.shader = new LinearGradient(this.mBarPointerHaloRadius, 0.0f, x1, y1, new int[]{-1, Color.HSVToColor(255, this.mHSVColor)}, (float[]) null, Shader.TileMode.CLAMP);
        } else {
            this.shader = new LinearGradient(this.mBarPointerHaloRadius, 0.0f, x1, y1, new int[]{-1, -8257792}, (float[]) null, Shader.TileMode.CLAMP);
            Color.colorToHSV(-8257792, this.mHSVColor);
        }
        this.mBarPaint.setShader(this.shader);
        this.mPosToSatFactor = 1.0f / this.mBarLength;
        this.mSatToPosFactor = this.mBarLength / 1.0f;
        float[] hsvColor = new float[3];
        Color.colorToHSV(this.mColor, hsvColor);
        if (!isInEditMode()) {
            this.mBarPointerPosition = Math.round((this.mSatToPosFactor * hsvColor[1]) + this.mBarPointerHaloRadius);
        } else {
            this.mBarPointerPosition = this.mBarLength + this.mBarPointerHaloRadius;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int cX;
        int cY;
        canvas.drawRect(this.mBarRect, this.mBarPaint);
        if (this.mOrientation) {
            cX = this.mBarPointerPosition;
            cY = this.mBarPointerHaloRadius;
        } else {
            cX = this.mBarPointerHaloRadius;
            cY = this.mBarPointerPosition;
        }
        canvas.drawCircle(cX, cY, this.mBarPointerHaloRadius, this.mBarPointerHaloPaint);
        canvas.drawCircle(cX, cY, this.mBarPointerRadius, this.mBarPointerPaint);
    }

    public float getSaturationResolution() {
        return this.mPosToSatFactor;
    }

    public void setPointerHaloColor(int color) {
        this.mBarPointerHaloPaint.setColor(color);
        invalidate();
    }

    public int getPointerHaloColor() {
        return this.mBarPointerHaloPaint.getColor();
    }

    public boolean getOrientation() {
        return this.mOrientation;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        float dimen;
        getParent().requestDisallowInterceptTouchEvent(true);
        if (this.mOrientation) {
            dimen = event.getX();
        } else {
            dimen = event.getY();
        }
        switch (event.getAction()) {
            case 0:
                this.mIsMovingPointer = true;
                if (dimen >= this.mBarPointerHaloRadius && dimen <= this.mBarPointerHaloRadius + this.mBarLength) {
                    this.mBarPointerPosition = Math.round(dimen);
                    calculateColor(Math.round(dimen));
                    this.mBarPointerPaint.setColor(this.mColor);
                    if (this.mPicker != null) {
                        this.mPicker.update();
                        this.mPicker.changeValueBarColor(this.mColor);
                        this.mPicker.changeOpacityBarColor(this.mColor);
                    }
                    invalidate();
                    break;
                }
                break;
            case 1:
                this.mIsMovingPointer = false;
                break;
            case 2:
                if (this.mIsMovingPointer) {
                    if (dimen >= this.mBarPointerHaloRadius && dimen <= this.mBarPointerHaloRadius + this.mBarLength) {
                        this.mBarPointerPosition = Math.round(dimen);
                        calculateColor(Math.round(dimen));
                        this.mBarPointerPaint.setColor(this.mColor);
                        if (this.mPicker != null) {
                            this.mPicker.update();
                            this.mPicker.changeValueBarColor(this.mColor);
                            this.mPicker.changeOpacityBarColor(this.mColor);
                        }
                        invalidate();
                    } else if (dimen < this.mBarPointerHaloRadius) {
                        this.mBarPointerPosition = this.mBarPointerHaloRadius;
                        this.mColor = -1;
                        this.mBarPointerPaint.setColor(this.mColor);
                        if (this.mPicker != null) {
                            this.mPicker.update();
                            this.mPicker.changeValueBarColor(this.mColor);
                            this.mPicker.changeOpacityBarColor(this.mColor);
                        }
                        invalidate();
                    } else if (dimen > this.mBarPointerHaloRadius + this.mBarLength) {
                        this.mBarPointerPosition = this.mBarPointerHaloRadius + this.mBarLength;
                        this.mColor = Color.HSVToColor(this.mHSVColor);
                        this.mBarPointerPaint.setColor(this.mColor);
                        if (this.mPicker != null) {
                            this.mPicker.update();
                            this.mPicker.changeValueBarColor(this.mColor);
                            this.mPicker.changeOpacityBarColor(this.mColor);
                        }
                        invalidate();
                    }
                }
                if (this.onSaturationChangedListener != null && this.oldChangedListenerSaturation != this.mColor) {
                    this.onSaturationChangedListener.onSaturationChanged(this.mColor);
                    this.oldChangedListenerSaturation = this.mColor;
                    break;
                }
                break;
        }
        return true;
    }

    public void setColor(int color) {
        int x1;
        int y1;
        if (this.mOrientation) {
            x1 = this.mBarLength + this.mBarPointerHaloRadius;
            y1 = this.mBarThickness;
        } else {
            x1 = this.mBarThickness;
            y1 = this.mBarLength + this.mBarPointerHaloRadius;
        }
        Color.colorToHSV(color, this.mHSVColor);
        this.shader = new LinearGradient(this.mBarPointerHaloRadius, 0.0f, x1, y1, new int[]{-1, color}, (float[]) null, Shader.TileMode.CLAMP);
        this.mBarPaint.setShader(this.shader);
        calculateColor(this.mBarPointerPosition);
        this.mBarPointerPaint.setColor(this.mColor);
        if (this.mPicker != null) {
            this.mPicker.update();
            if (this.mPicker.hasValueBar()) {
                this.mPicker.changeValueBarColor(this.mColor);
            } else if (this.mPicker.hasOpacityBar()) {
                this.mPicker.changeOpacityBarColor(this.mColor);
            }
        }
        invalidate();
    }

    public void setSaturation(float saturation) {
        this.mBarPointerPosition = Math.round(this.mSatToPosFactor * saturation) + this.mBarPointerHaloRadius;
        calculateColor(this.mBarPointerPosition);
        this.mBarPointerPaint.setColor(this.mColor);
        if (this.mPicker != null) {
            this.mPicker.update();
            this.mPicker.changeValueBarColor(this.mColor);
            this.mPicker.changeOpacityBarColor(this.mColor);
        }
        invalidate();
    }

    private void calculateColor(int coord) {
        int coord2 = coord - this.mBarPointerHaloRadius;
        if (coord2 < 0) {
            coord2 = 0;
        } else if (coord2 > this.mBarLength) {
            coord2 = this.mBarLength;
        }
        this.mColor = Color.HSVToColor(new float[]{this.mHSVColor[0], this.mPosToSatFactor * coord2, 1.0f});
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColorPicker(ColorPicker picker) {
        this.mPicker = picker;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable(STATE_PARENT, superState);
        state.putFloatArray(STATE_COLOR, this.mHSVColor);
        float[] hsvColor = new float[3];
        Color.colorToHSV(this.mColor, hsvColor);
        state.putFloat(STATE_SATURATION, hsvColor[1]);
        return state;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;
        Parcelable superState = savedState.getParcelable(STATE_PARENT);
        super.onRestoreInstanceState(superState);
        setColor(Color.HSVToColor(savedState.getFloatArray(STATE_COLOR)));
        setSaturation(savedState.getFloat(STATE_SATURATION));
    }
}
