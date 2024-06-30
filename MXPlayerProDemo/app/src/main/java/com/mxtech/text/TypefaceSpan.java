package com.mxtech.text;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/* loaded from: classes2.dex */
public class TypefaceSpan extends MetricAffectingSpan {
    private final Typeface _typeface;

    public TypefaceSpan(Typeface typeface) {
        this._typeface = typeface;
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint paint) {
        paint.setTypeface(this._typeface);
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint paint) {
        paint.setTypeface(this._typeface);
    }
}
