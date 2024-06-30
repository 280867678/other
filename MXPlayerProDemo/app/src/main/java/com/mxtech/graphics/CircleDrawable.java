package com.mxtech.graphics;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;

/* loaded from: classes2.dex */
public class CircleDrawable extends Drawable {
    private static final String TAG = "MX.CircleDrawable";
    private Bitmap _backBuffer;
    private Canvas _backBufferCanvas;
    private boolean _backBufferValid;
    @Nullable
    private ColorStateList _circleColor;
    private float _circleRadius;
    private int _currentCircleColor;
    private int _currentShadowColor;
    private int _currentStrokeColor;
    private final Paint _paint = new Paint(1);
    @Nullable
    private ColorStateList _shadowColor;
    private float _shadowDx;
    private float _shadowDy;
    private float _shadowRadius;
    private boolean _stateful;
    @Nullable
    private ColorStateList _strokeColor;
    private Paint _strokePaint;

    public CircleDrawable() {
    }

    public CircleDrawable(@Nullable ColorStateList color, @Nullable ColorStateList shadowColor, @Nullable ColorStateList strokeColor, float shadowRadius, float shadowDx, float shadowDy, float strokeWidth) {
        configure(color, shadowColor, strokeColor, shadowRadius, shadowDx, shadowDy, strokeWidth);
    }

    public Paint getPaint() {
        return this._paint;
    }

    public void configure(@Nullable ColorStateList circleColor, @Nullable ColorStateList shadowColor, @Nullable ColorStateList strokeColor, float shadowRadius, float shadowDx, float shadowDy, float strokeWidth) {
        boolean z = true;
        this._paint.setStyle(Paint.Style.FILL);
        this._circleColor = circleColor;
        this._shadowColor = shadowColor;
        this._shadowRadius = shadowRadius;
        this._shadowDx = shadowDx;
        this._shadowDy = shadowDy;
        if (strokeWidth > 0.0f) {
            this._strokeColor = strokeColor;
        } else {
            this._strokeColor = null;
        }
        if (this._strokeColor != null) {
            if (this._strokePaint == null) {
                this._strokePaint = new Paint(1);
                this._strokePaint.setStyle(Paint.Style.STROKE);
                this._strokePaint.setStrokeWidth(strokeWidth);
            }
        } else {
            this._strokePaint = null;
        }
        if ((this._circleColor == null || !this._circleColor.isStateful()) && ((this._shadowColor == null || !this._shadowColor.isStateful()) && (this._strokeColor == null || !this._strokeColor.isStateful()))) {
            z = false;
        }
        this._stateful = z;
        onStateChange(getState());
        onBoundsChange(getBounds());
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this._stateful;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean needRedraw = false;
        if (this._circleColor != null) {
            int color = this._circleColor.getColorForState(state, 0);
            if (color != this._currentCircleColor) {
                this._currentCircleColor = color;
                this._paint.setColor(color);
                needRedraw = true;
            }
        } else if (this._currentCircleColor != 0) {
            this._paint.setColor(0);
            this._currentCircleColor = 0;
            needRedraw = true;
        }
        if (this._shadowColor != null && this._shadowRadius != 0.0f) {
            int color2 = this._shadowColor.getColorForState(state, 0);
            if (color2 != this._currentShadowColor) {
                if (color2 != 0) {
                    this._paint.setShadowLayer(this._shadowRadius, this._shadowDx, this._shadowDy, color2);
                } else {
                    this._paint.clearShadowLayer();
                }
                this._currentShadowColor = color2;
                needRedraw = true;
            }
        } else if (this._currentShadowColor != 0) {
            this._paint.clearShadowLayer();
            this._currentShadowColor = 0;
            needRedraw = true;
        }
        if (this._strokeColor != null) {
            int color3 = this._strokeColor.getColorForState(state, 0);
            if (color3 != this._currentStrokeColor) {
                this._currentStrokeColor = color3;
                this._strokePaint.setColor(color3);
                needRedraw = true;
            }
        } else if (this._currentStrokeColor != 0) {
            this._currentStrokeColor = 0;
            needRedraw = true;
        }
        if (needRedraw) {
            this._backBufferValid = false;
            invalidateSelf();
        }
        return needRedraw;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this._circleRadius = (float) (((bounds.width() / 2) - this._shadowRadius) - Math.hypot(this._shadowDx, this._shadowDy));
    }

    private void doDraw(Canvas canvas, int left, int top, int width, int height) {
        canvas.drawCircle((width / 2) + left, (height / 2) + top, this._circleRadius, this._paint);
        if (this._currentStrokeColor != 0) {
            canvas.drawCircle((width / 2) + left, (height / 2) + top, this._circleRadius, this._strokePaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bound = getBounds();
        int width = bound.width();
        int height = bound.height();
        if (this._currentShadowColor != 0 && Build.VERSION.SDK_INT >= 11) {
            if (this._backBuffer == null || this._backBuffer.getWidth() != width || this._backBuffer.getHeight() != height) {
                this._backBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                this._backBufferCanvas = new Canvas(this._backBuffer);
                this._backBufferValid = false;
            }
            if (!this._backBufferValid) {
                this._backBuffer.eraseColor(0);
                doDraw(this._backBufferCanvas, 0, 0, width, height);
                this._backBufferValid = true;
            }
            canvas.drawBitmap(this._backBuffer, bound.left, bound.top, (Paint) null);
            return;
        }
        doDraw(canvas, bound.left, bound.top, width, height);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this._paint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this._paint.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        if (!super.setVisible(visible, restart)) {
            return false;
        }
        if (!visible && this._backBuffer != null) {
            this._backBuffer = null;
            this._backBufferCanvas = null;
        }
        return true;
    }
}
