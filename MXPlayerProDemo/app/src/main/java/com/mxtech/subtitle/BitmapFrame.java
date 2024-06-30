package com.mxtech.subtitle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/* loaded from: classes2.dex */
public class BitmapFrame implements DrawableFrame {
    private final Bitmap _bitmap;
    private RectF _bound;
    private final int _orgCanvasHeight;
    private final int _orgCanvasWidth;
    private Rect _src;
    private final float x;
    private final float y;

    public BitmapFrame(float x, float y, Bitmap bitmap) {
        this(x, y, bitmap, 0, 0);
    }

    public BitmapFrame(float x, float y, Bitmap bitmap, int orgCanvasWidth, int orgCanvasHeight) {
        this._bound = new RectF();
        this.x = x;
        this.y = y;
        this._orgCanvasWidth = orgCanvasWidth;
        this._orgCanvasHeight = orgCanvasHeight;
        this._bitmap = bitmap;
        this._src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    @Override // com.mxtech.subtitle.Frame
    public void updateBounds(int canvasWidth, int canvasHeight, int videoWidth, int videoHeight, float scale) {
        int srcWidth = this._orgCanvasWidth != 0 ? this._orgCanvasWidth : videoWidth;
        int srcHeight = this._orgCanvasHeight != 0 ? this._orgCanvasHeight : videoHeight;
        this._bound.left = (this.x * canvasWidth) / srcWidth;
        this._bound.right = ((this.x + this._bitmap.getWidth()) * canvasWidth) / srcWidth;
        this._bound.top = (this.y * canvasHeight) / srcHeight;
        this._bound.bottom = ((this.y + this._bitmap.getHeight()) * canvasHeight) / srcHeight;
        if (scale != 1.0f) {
            float hCenter = (this._bound.left + this._bound.right) / 2.0f;
            float vCenter = (this._bound.top + this._bound.bottom) / 2.0f;
            this._bound.left = ((this._bound.left - hCenter) * scale) + hCenter;
            this._bound.right = ((this._bound.right - hCenter) * scale) + hCenter;
            this._bound.top = ((this._bound.top - vCenter) * scale) + vCenter;
            this._bound.bottom = ((this._bound.bottom - vCenter) * scale) + vCenter;
        }
        if (this._bound.left < 0.0f) {
            this._bound.right -= this._bound.left;
            this._bound.left = 0.0f;
        }
        if (this._bound.right > canvasWidth) {
            this._bound.left -= this._bound.right - canvasWidth;
            this._bound.right = canvasWidth;
            if (this._bound.left < 0.0f) {
                this._bound.left = 0.0f;
            }
        }
        if (this._bound.top < 0.0f) {
            this._bound.bottom -= this._bound.top;
            this._bound.top = 0.0f;
        }
        if (this._bound.bottom > canvasHeight) {
            this._bound.top -= this._bound.bottom - canvasHeight;
            this._bound.bottom = canvasHeight;
            if (this._bound.top < 0.0f) {
                this._bound.top = 0.0f;
            }
        }
    }

    @Override // com.mxtech.subtitle.DrawableFrame
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this._bitmap, this._src, this._bound, (Paint) null);
    }
}
