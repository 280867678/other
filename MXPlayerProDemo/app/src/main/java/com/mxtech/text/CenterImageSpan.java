package com.mxtech.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

/* loaded from: classes2.dex */
public class CenterImageSpan extends ReplacementSpan {
    public static final int ALIGN_CENTER = 2;
    private final Drawable _drawable;

    public CenterImageSpan(Drawable drawable) {
        this._drawable = drawable;
    }

    public Drawable getDrawable() {
        return this._drawable;
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return this._drawable.getBounds().right;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        int transY = ((bottom - top) / 2) - (this._drawable.getBounds().bottom / 2);
        canvas.translate(x, transY);
        this._drawable.draw(canvas);
        canvas.restore();
    }
}
