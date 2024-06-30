package com.mxtech.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

/* loaded from: classes2.dex */
public class RubySpan extends ReplacementSpan implements LineHeightSpan.WithDensity {
    public static final String TAG = "MX.Ruby";
    private final Rect _bounds;
    private int _maxWidth;
    private CharSequence _rt;
    private final TextPaint _workPaint;

    public RubySpan(CharSequence text) {
        this(text, 0);
    }

    public RubySpan(CharSequence text, int maxWidth) {
        this._workPaint = new TextPaint();
        this._bounds = new Rect();
        this._rt = text;
        this._maxWidth = maxWidth;
    }

    public void setMaxWidth(int width) {
        this._maxWidth = width;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RubySyncSpan[] rubySyncSpanArr;
        this._workPaint.set(paint);
        boolean hasRubySyncSpan = false;
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            CharacterStyle[] spans = (CharacterStyle[]) spanned.getSpans(start, end, CharacterStyle.class);
            for (CharacterStyle span : spans) {
                if (!(span instanceof ReplacementSpan)) {
                    span.updateDrawState(this._workPaint);
                }
            }
            RubySyncSpan[] syncs = (RubySyncSpan[]) spanned.getSpans(start, end, RubySyncSpan.class);
            if (syncs.length > 0) {
                if (!(this._rt instanceof Spannable)) {
                    this._rt = new SpannableString(this._rt);
                }
                for (RubySyncSpan sync : syncs) {
                    ((Spannable) this._rt).setSpan(sync, 0, this._rt.length(), 33);
                }
                hasRubySyncSpan = true;
            }
        }
        paint.getTextBounds(text.toString(), start, end, this._bounds);
        canvas.drawText(text, start, end, x, y, this._workPaint);
        this._workPaint.set(paint);
        this._workPaint.setTextSize(getRubyTextSize(paint));
        this._workPaint.setTextAlign(Paint.Align.CENTER);
        int gap = (int) getGap(paint);
        if (this._rt instanceof Spanned) {
            if (!hasRubySyncSpan && (this._rt instanceof Spannable)) {
                Spannable spannable = (Spannable) this._rt;
                for (RubySyncSpan sync2 : (RubySyncSpan[]) spannable.getSpans(0, this._rt.length(), RubySyncSpan.class)) {
                    spannable.removeSpan(sync2);
                }
            }
            for (CharacterStyle span2 : (CharacterStyle[]) ((Spanned) this._rt).getSpans(0, this._rt.length(), CharacterStyle.class)) {
                span2.updateDrawState(this._workPaint);
            }
        }
        canvas.drawText(this._rt, 0, this._rt.length(), x + (this._bounds.width() / 2), (this._bounds.top + y) - gap, this._workPaint);
        this._workPaint.set(paint);
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        this._workPaint.set(paint);
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            MetricAffectingSpan[] spans = (MetricAffectingSpan[]) spanned.getSpans(start, end, MetricAffectingSpan.class);
            for (MetricAffectingSpan span : spans) {
                if (!(span instanceof ReplacementSpan)) {
                    span.updateMeasureState(this._workPaint);
                }
            }
        }
        this._workPaint.getTextBounds(text.toString(), start, end, this._bounds);
        int width = this._bounds.width();
        int height = this._bounds.height();
        this._workPaint.setTextSize(getRubyTextSize(paint));
        this._workPaint.setTextAlign(Paint.Align.CENTER);
        this._workPaint.getTextBounds(this._rt.toString(), 0, this._rt.length(), this._bounds);
        int height2 = height + ((int) getGap(paint)) + this._bounds.height();
        if (fm != null) {
            fm.ascent = -height2;
            fm.top = -height2;
        }
        this._workPaint.set(paint);
        if (width > this._maxWidth) {
            return this._maxWidth;
        }
        return width;
    }

    @Override // android.text.style.LineHeightSpan.WithDensity
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
        if (start < end && end <= text.length()) {
            paint.getTextBounds(text.toString(), start, end, this._bounds);
            int mainTop = this._bounds.top;
            this._workPaint.set(paint);
            this._workPaint.setTextSize(getRubyTextSize(paint));
            this._workPaint.getTextBounds(this._rt.toString(), 0, this._rt.length(), this._bounds);
            int rubyHeight = this._bounds.height();
            int gap = (int) getGap(paint);
            int ascent = (mainTop - gap) - rubyHeight;
            int need = (-ascent) + fm.ascent;
            if (need > 0) {
                fm.ascent -= need;
                fm.top -= need;
            }
        }
    }

    @Override // android.text.style.LineHeightSpan
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        chooseHeight(text, start, end, spanstartv, v, fm, this._workPaint);
    }

    private float getGap(Paint paint) {
        return paint.getTextSize() / 6.0f;
    }

    private float getRubyTextSize(Paint paint) {
        return paint.getTextSize() * 0.5f;
    }
}
