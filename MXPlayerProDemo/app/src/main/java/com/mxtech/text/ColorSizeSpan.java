package com.mxtech.text;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.UpdateAppearance;

/* loaded from: classes.dex */
public class ColorSizeSpan extends MetricAffectingSpan implements UpdateAppearance {
    private final int _color;
    private final float _proportion;

    public ColorSizeSpan(int color, float proportion) {
        this._color = color;
        this._proportion = proportion;
    }

    public int getForegroundColor() {
        return this._color;
    }

    public float getSizeChange() {
        return this._proportion;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setTextSize(ds.getTextSize() * this._proportion);
        ds.setColor(this._color);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint ds) {
        ds.setTextSize(ds.getTextSize() * this._proportion);
    }
}
