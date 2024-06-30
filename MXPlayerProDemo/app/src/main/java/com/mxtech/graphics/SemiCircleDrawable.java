package com.mxtech.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;

/* loaded from: classes2.dex */
public class SemiCircleDrawable extends Drawable {
    private int _colorDown;
    private int _colorUp;
    private final Paint _paint = new Paint();
    private final RectF _rect = new RectF();
    private int _strokeColor;

    public SemiCircleDrawable() {
        this._paint.setStyle(Paint.Style.FILL);
        this._paint.setFlags(1);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this._paint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this._colorUp = (this._colorUp & ViewCompat.MEASURED_SIZE_MASK) | (alpha << 24);
        this._colorDown = (this._colorDown & ViewCompat.MEASURED_SIZE_MASK) | (alpha << 24);
        this._strokeColor = (this._strokeColor & ViewCompat.MEASURED_SIZE_MASK) | (alpha << 24);
        invalidateSelf();
    }

    public final void setColor(int color) {
        setColors(color, color);
    }

    public void setColors(int colorUp, int colorDown) {
        this._colorUp = colorUp;
        this._colorDown = colorDown;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this._paint.setColorFilter(cf);
        invalidateSelf();
    }

    public void setStroke(int width, int color) {
        this._paint.setStrokeWidth(width);
        this._strokeColor = color;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this._rect.set(getBounds());
        this._paint.setStyle(Paint.Style.FILL);
        if (this._colorUp != this._colorDown) {
            this._paint.setColor(this._colorUp);
            canvas.drawArc(this._rect, -180.0f, 180.0f, true, this._paint);
            this._paint.setColor(this._colorDown);
            canvas.drawArc(this._rect, 0.0f, 180.0f, true, this._paint);
        } else {
            this._paint.setColor(this._colorUp);
            canvas.drawOval(this._rect, this._paint);
        }
        if (Color.alpha(this._strokeColor) != 0) {
            this._paint.setStyle(Paint.Style.STROKE);
            this._paint.setColor(this._strokeColor);
            canvas.drawOval(this._rect, this._paint);
        }
    }
}
