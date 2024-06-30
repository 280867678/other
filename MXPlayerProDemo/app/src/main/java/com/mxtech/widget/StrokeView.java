package com.mxtech.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.mxtech.app.MXApplication;
import com.mxtech.text.RubySyncSpan;

/* loaded from: classes2.dex */
public class StrokeView extends FrameLayout {
    private static final int SHADOW_COLOR = Integer.MIN_VALUE;
    private static final int SHADOW_COLOR_NOSTROKE = -16777216;
    private static final float SHADOW_DISTANCE_DIVIDER = 20.0f;
    private static final float SHADOW_RADIUS = 1.0f;
    private static final float SHADOW_RADIUS_NOSTROKE = 2.0f;
    private boolean _bold;
    private float _boldThickness;
    private boolean _hasShadow;
    private final MainText _mainText;
    private int _minLines;
    private float _normalThickness;
    private final StrokeSpan _strokeSpan;
    private StrokeText _strokeText;

    public StrokeView(Context context) {
        super(context);
        this._strokeSpan = new StrokeSpan();
        this._mainText = new MainText(getContext());
        addView(this._mainText);
    }

    public void enableShadow(boolean enable) {
        this._hasShadow = enable;
        update();
    }

    private void update() {
        if (this._hasShadow) {
            float distance = this._mainText.getTextSize() / SHADOW_DISTANCE_DIVIDER;
            if (this._strokeText != null) {
                this._strokeText.setShadowLayer(1.0f, distance, distance, Integer.MIN_VALUE);
                this._mainText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            } else {
                this._mainText.setShadowLayer(SHADOW_RADIUS_NOSTROKE, distance, distance, -16777216);
            }
        } else {
            if (this._strokeText != null) {
                this._strokeText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            }
            this._mainText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
        float width = this._mainText.getTextSize() * (this._bold ? this._boldThickness : this._normalThickness);
        this._mainText.getPaint().setStrokeWidth(width);
        if (this._strokeText != null) {
            this._strokeText.getPaint().setStrokeWidth(width);
        }
    }

    public void setBold(boolean bold) {
        this._bold = bold;
        setViewBold(this._mainText, bold);
        if (this._strokeText != null) {
            setViewBold(this._strokeText, bold);
        }
        update();
    }

    private void setViewBold(TextView tv, boolean bold) {
        TextPaint paint = tv.getPaint();
        int flags = paint.getFlags();
        Typeface tf = this._mainText.getTypeface();
        if (bold && (tf == null || !tf.isBold())) {
            paint.setFlags(flags | 32);
        } else {
            paint.setFlags(flags & (-33));
        }
    }

    public void setTextSize(int unit, float size) {
        this._mainText.setTextSize(unit, size);
        if (this._strokeText != null) {
            this._strokeText.setTextSize(unit, size);
        }
        update();
    }

    public void setBorderColor(int color) {
        this._strokeSpan.borderColor = color;
        if (this._strokeText != null) {
            this._strokeText.invalidate();
        }
    }

    public void setBorderThickness(float normalThickness, float boldThickness) {
        this._normalThickness = normalThickness;
        this._boldThickness = boldThickness;
        update();
        if (this._strokeText != null) {
            this._strokeText.invalidate();
        }
    }

    public void setTypeface(Typeface tf) {
        this._mainText.setTypeface(tf);
        if (this._strokeText != null) {
            this._strokeText.setTypeface(tf);
        }
    }

    public void setTextColor(int color) {
        this._mainText.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        this._mainText.setTextColor(colors);
    }

    public void setGravity(int gravity) {
        this._mainText.setGravity(gravity);
        if (this._strokeText != null) {
            this._strokeText.setGravity(gravity);
        }
    }

    public void setMinLines(int line) {
        this._minLines = line;
        this._mainText.setMinLines(line);
        if (this._strokeText != null) {
            this._strokeText.setMinLines(line);
        }
    }

    public void enableStroke(boolean enable) {
        if (enable != (this._strokeText != null)) {
            if (enable) {
                this._strokeText = new StrokeText();
                this._strokeText.setText(this._mainText.getText());
                this._strokeText.setTextSize(0, this._mainText.getTextSize());
                this._strokeText.setTypeface(this._mainText.getTypeface());
                this._strokeText.setGravity(this._mainText.getGravity());
                this._strokeText.setMinLines(this._minLines);
                setViewBold(this._strokeText, this._bold);
                addView(this._strokeText, 0);
            } else {
                removeView(this._strokeText);
                this._strokeText = null;
            }
            update();
        }
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        try {
            this._mainText.setText(text, type);
            if (this._strokeText != null) {
                this._strokeText.setText(text, type);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.w(MXApplication.TAG, "", e);
        }
    }

    public CharSequence getText() {
        return this._mainText.getText();
    }

    public float getTextSize() {
        return this._mainText.getTextSize();
    }

    public boolean isPositionOnText(float x, float y, float tolerance) {
        Layout layout;
        if (this._mainText.length() == 0 || (layout = this._mainText.getLayout()) == null) {
            return false;
        }
        float y2 = y - (this._mainText.getTotalPaddingTop() + getPaddingTop());
        if (y2 < (-tolerance) || y2 >= ((this._mainText.getHeight() - this._mainText.getTotalPaddingBottom()) - getPaddingBottom()) + tolerance) {
            return false;
        }
        float y3 = y2 + this._mainText.getScrollY();
        int line = layout.getLineForVertical((int) y3);
        if (line == 0) {
            int top = layout.getLineTop(line);
            if (y3 < top - tolerance) {
                return false;
            }
        } else if (line == layout.getLineCount() - 1) {
            int bottom = layout.getLineBottom(line);
            if (y3 >= bottom + tolerance) {
                return false;
            }
        }
        float x2 = x - (this._mainText.getTotalPaddingLeft() + getPaddingLeft());
        if (x2 < (-tolerance) || x2 >= ((this._mainText.getWidth() - this._mainText.getTotalPaddingRight()) - getPaddingRight()) + tolerance) {
            return false;
        }
        float x3 = x2 + this._mainText.getScrollX();
        float left = layout.getLineLeft(line);
        if (x3 >= left - tolerance) {
            float right = layout.getLineRight(line);
            return x3 < right + tolerance;
        }
        return false;
    }

    public boolean isPositionOnTextBound(float x, float y, float tolerance) {
        Layout layout;
        if (this._mainText.length() == 0 || (layout = this._mainText.getLayout()) == null) {
            return false;
        }
        float y2 = y - (this._mainText.getTotalPaddingTop() + getPaddingTop());
        if (y2 < (-tolerance) || y2 >= ((this._mainText.getHeight() - this._mainText.getTotalPaddingBottom()) - getPaddingBottom()) + tolerance) {
            return false;
        }
        float y3 = y2 + this._mainText.getScrollY();
        float x2 = x - (this._mainText.getTotalPaddingLeft() + getPaddingLeft());
        if (x2 < (-tolerance) || x2 >= ((this._mainText.getWidth() - this._mainText.getTotalPaddingRight()) - getPaddingRight()) + tolerance) {
            return false;
        }
        float x3 = x2 + this._mainText.getScrollX();
        float left = Float.MAX_VALUE;
        float right = 0.0f;
        float top = Float.MAX_VALUE;
        float bottom = 0.0f;
        for (int i = layout.getLineCount() - 1; i >= 0; i--) {
            left = Math.min(left, layout.getLineLeft(i));
            top = Math.min(top, layout.getLineTop(i));
            right = Math.max(right, layout.getLineRight(i));
            bottom = Math.max(bottom, layout.getLineBottom(i));
        }
        return left - tolerance <= x3 && x3 < right + tolerance && top - tolerance <= y3 && y3 < bottom + tolerance;
    }

    /* loaded from: classes2.dex */
    private static class TextViewImpl extends TextView {
        public TextViewImpl(Context context) {
            super(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class StrokeText extends TextViewImpl {
        public StrokeText() {
            super(StrokeView.this.getContext());
            TextPaint paint = super.getPaint();
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setFlags(257);
        }

        @Override // android.widget.TextView
        public void setText(CharSequence text, TextView.BufferType type) {
            if (text != null && text.length() > 0) {
                Spannable s = new SpannableString(text);
                s.setSpan(StrokeView.this._strokeSpan, 0, s.length(), 33);
                super.setText(s, TextView.BufferType.SPANNABLE);
                return;
            }
            super.setText(text, type);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MainText extends TextViewImpl {
        public MainText(Context context) {
            super(context);
            TextPaint paint = super.getPaint();
            paint.setFlags(257);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class StrokeSpan extends CharacterStyle implements RubySyncSpan {
        int borderColor = -16777216;

        StrokeSpan() {
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint paint) {
            paint.setColor(this.borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setFlags(paint.getFlags() & (-25));
        }
    }
}
