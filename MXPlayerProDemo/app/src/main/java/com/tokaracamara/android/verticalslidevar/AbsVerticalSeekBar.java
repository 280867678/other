package com.tokaracamara.android.verticalslidevar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/* loaded from: classes2.dex */
public class AbsVerticalSeekBar extends VerticalProgressBar {
    private static final int NO_ALPHA = 255;
    private float mDisabledAlpha;
    boolean mIsUserSeekable;
    private int mKeyProgressIncrement;
    private Drawable mThumb;
    private int mThumbOffset;
    float mTouchProgressOffset;

    public AbsVerticalSeekBar(Context context) {
        super(context);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsVerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsVerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyle, 0);
        Drawable thumb = a.getDrawable(R.styleable.SeekBar_android_thumb);
        setThumb(thumb);
        int thumbOffset = a.getDimensionPixelOffset(R.styleable.SeekBar_android_thumbOffset, getThumbOffset());
        setThumbOffset(thumbOffset);
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.Theme, 0, 0);
        this.mDisabledAlpha = a2.getFloat(R.styleable.Theme_android_disabledAlpha, 0.5f);
        a2.recycle();
    }

    public void setThumb(Drawable thumb) {
        if (thumb != null) {
            thumb.setCallback(this);
            this.mThumbOffset = thumb.getIntrinsicHeight() / 2;
        }
        this.mThumb = thumb;
        invalidate();
    }

    public int getThumbOffset() {
        return this.mThumbOffset;
    }

    public void setThumbOffset(int thumbOffset) {
        this.mThumbOffset = thumbOffset;
        invalidate();
    }

    public void setKeyProgressIncrement(int increment) {
        if (increment < 0) {
            increment = -increment;
        }
        this.mKeyProgressIncrement = increment;
    }

    public int getKeyProgressIncrement() {
        return this.mKeyProgressIncrement;
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar
    public synchronized void setMax(int max) {
        super.setMax(max);
        if (this.mKeyProgressIncrement == 0 || getMax() / this.mKeyProgressIncrement > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(getMax() / 20.0f)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return who == this.mThumb || super.verifyDrawable(who);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (255.0f * this.mDisabledAlpha));
        }
        if (this.mThumb != null && this.mThumb.isStateful()) {
            int[] state = getDrawableState();
            this.mThumb.setState(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar
    public void onProgressRefresh(float scale, boolean fromUser) {
        Drawable thumb = this.mThumb;
        if (thumb != null) {
            setThumbPos(getHeight(), thumb, scale, Integer.MIN_VALUE);
            invalidate();
        }
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar, android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Drawable d = getCurrentDrawable();
        Drawable thumb = this.mThumb;
        int thumbWidth = thumb == null ? 0 : thumb.getIntrinsicWidth();
        int trackWidth = Math.min(this.mMaxWidth, (w - getPaddingRight()) - getPaddingLeft());
        int max = getMax();
        float scale = max > 0 ? getProgress() / max : 0.0f;
        if (thumbWidth > trackWidth) {
            int gapForCenteringTrack = (thumbWidth - trackWidth) / 2;
            if (thumb != null) {
                setThumbPos(h, thumb, scale, gapForCenteringTrack * (-1));
            }
            if (d != null) {
                d.setBounds(gapForCenteringTrack, 0, ((w - getPaddingRight()) - getPaddingLeft()) - gapForCenteringTrack, (h - getPaddingBottom()) - getPaddingTop());
                return;
            }
            return;
        }
        if (d != null) {
            d.setBounds(0, 0, (w - getPaddingRight()) - getPaddingLeft(), (h - getPaddingBottom()) - getPaddingTop());
        }
        int gap = (trackWidth - thumbWidth) / 2;
        if (thumb != null) {
            setThumbPos(h, thumb, scale, gap);
        }
    }

    private void setThumbPos(int h, Drawable thumb, float scale, int gap) {
        int leftBound;
        int rightBound;
        int available = (h - getPaddingTop()) - getPaddingBottom();
        int thumbWidth = thumb.getIntrinsicWidth();
        int thumbHeight = thumb.getIntrinsicHeight();
        int thumbPos = (int) ((1.0f - scale) * ((available - thumbHeight) + (this.mThumbOffset * 2)));
        if (gap == Integer.MIN_VALUE) {
            Rect oldBounds = thumb.getBounds();
            leftBound = oldBounds.left;
            rightBound = oldBounds.right;
        } else {
            leftBound = gap;
            rightBound = gap + thumbWidth;
        }
        thumb.setBounds(leftBound, thumbPos, rightBound, thumbPos + thumbHeight);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar, android.view.View
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mThumb != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop() - this.mThumbOffset);
            this.mThumb.draw(canvas);
            canvas.restore();
        }
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalProgressBar, android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getCurrentDrawable();
        int thumbWidth = this.mThumb == null ? 0 : this.mThumb.getIntrinsicWidth();
        int dw = 0;
        int dh = 0;
        if (d != null) {
            Math.max(this.mMinWidth, Math.min(this.mMaxWidth, d.getIntrinsicWidth()));
            dw = Math.max(thumbWidth, 0);
            dh = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, d.getIntrinsicHeight()));
        }
        setMeasuredDimension(resolveSize(dw + getPaddingLeft() + getPaddingRight(), widthMeasureSpec), resolveSize(dh + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mIsUserSeekable && isEnabled()) {
            switch (event.getAction()) {
                case 0:
                    setPressed(true);
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    return true;
                case 1:
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                    invalidate();
                    return true;
                case 2:
                    trackTouchEvent(event);
                    attemptClaimDrag();
                    return true;
                case 3:
                    onStopTrackingTouch();
                    setPressed(false);
                    invalidate();
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void trackTouchEvent(MotionEvent event) {
        float scale;
        int height = getHeight();
        int available = (height - getPaddingTop()) - getPaddingBottom();
        int y = height - ((int) event.getY());
        float progress = 0.0f;
        if (y < getPaddingBottom()) {
            scale = 0.0f;
        } else if (y > height - getPaddingTop()) {
            scale = 1.0f;
        } else {
            scale = (y - getPaddingBottom()) / available;
            progress = this.mTouchProgressOffset;
        }
        int max = getMax();
        setProgress((int) (progress + (max * scale)), true);
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    void onStartTrackingTouch() {
    }

    void onStopTrackingTouch() {
    }

    void onKeyChange() {
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int progress = getProgress();
        switch (keyCode) {
            case 19:
                if (progress < getMax()) {
                    setProgress(this.mKeyProgressIncrement + progress, true);
                    onKeyChange();
                    return true;
                }
                break;
            case 20:
                if (progress > 0) {
                    setProgress(progress - this.mKeyProgressIncrement, true);
                    onKeyChange();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
