package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private Drawable _checkMarkDrawable;
    private int _checkMarkPaddingBottom;
    private int _checkMarkPaddingLeft;
    private int _checkMarkPaddingRight;
    private int _checkMarkPaddingTop;
    private int _checkMarkResId;
    private boolean _checked;
    private boolean _editing;
    private int _gravity;
    private int _leftPaddingOnEditingBegin;

    public CheckableRelativeLayout(Context context) {
        super(context);
        this._gravity = 16;
        init(context, null, 0, 0);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._gravity = 16;
        init(context, attrs, 0, 0);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._gravity = 16;
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"NewApi"})
    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._gravity = 16;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setWillNotDraw(false);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.CheckableRelativeLayout, defStyleAttr, defStyleRes);
        try {
            this._checkMarkResId = a.getResourceId(R.styleable.CheckableRelativeLayout_android_checkMark, 0);
            this._gravity = a.getInt(R.styleable.CheckableRelativeLayout_checkMarkGravity, 16);
            int dimensionPixelSize = a.getDimensionPixelSize(R.styleable.CheckableRelativeLayout_checkMarkPadding, 0);
            this._checkMarkPaddingBottom = dimensionPixelSize;
            this._checkMarkPaddingTop = dimensionPixelSize;
            this._checkMarkPaddingRight = dimensionPixelSize;
            this._checkMarkPaddingLeft = dimensionPixelSize;
            this._checkMarkPaddingLeft = a.getDimensionPixelSize(R.styleable.CheckableRelativeLayout_checkMarkPaddingLeft, this._checkMarkPaddingLeft);
            this._checkMarkPaddingRight = a.getDimensionPixelSize(R.styleable.CheckableRelativeLayout_checkMarkPaddingRight, this._checkMarkPaddingRight);
            this._checkMarkPaddingTop = a.getDimensionPixelSize(R.styleable.CheckableRelativeLayout_checkMarkPaddingTop, this._checkMarkPaddingTop);
            this._checkMarkPaddingBottom = a.getDimensionPixelSize(R.styleable.CheckableRelativeLayout_checkMarkPaddingBottom, this._checkMarkPaddingBottom);
            if (a.getBoolean(R.styleable.CheckableRelativeLayout_edit, true)) {
                beginEdit();
            }
        } finally {
            a.recycle();
        }
    }

    public void setCheckMarkPadding(int left, int top, int right, int bottom) {
        this._checkMarkPaddingLeft = left;
        this._checkMarkPaddingTop = top;
        this._checkMarkPaddingRight = right;
        this._checkMarkPaddingBottom = bottom;
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this._checked;
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this._checked);
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean check) {
        if (check != this._checked) {
            this._checked = check;
            refreshDrawableState();
        }
    }

    public final boolean isEditing() {
        return this._editing;
    }

    public void beginEdit() {
        Drawable d;
        if (!this._editing) {
            this._leftPaddingOnEditingBegin = getPaddingLeft();
            this._editing = true;
            if (this._checkMarkResId != 0 && (d = AppCompatDrawableManager.get().getDrawable(getContext(), this._checkMarkResId)) != null) {
                setCheckMarkDrawable(d);
            }
        }
    }

    public void endEdit() {
        if (this._editing) {
            this._editing = false;
            setCheckMarkDrawable((Drawable) null);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (this._checkMarkDrawable != null) {
            this._checkMarkDrawable.setVisible(visibility == 0, false);
        }
    }

    public void setCheckMarkDrawable(int resid) {
        Drawable d;
        if (resid == 0 || resid != this._checkMarkResId) {
            this._checkMarkResId = resid;
            if (this._editing) {
                if (this._checkMarkResId != 0) {
                    d = AppCompatDrawableManager.get().getDrawable(getContext(), this._checkMarkResId);
                } else {
                    d = null;
                }
                setCheckMarkDrawable(d);
            }
        }
    }

    public void setCheckMarkDrawable(Drawable d) {
        if (this._checkMarkDrawable != null) {
            this._checkMarkDrawable.setCallback(null);
            unscheduleDrawable(this._checkMarkDrawable);
        }
        int newLeftPadding = this._leftPaddingOnEditingBegin;
        if (d != null) {
            d.setCallback(this);
            d.setVisible(getVisibility() == 0, false);
            d.setState(getDrawableState());
            newLeftPadding += this._checkMarkPaddingLeft + d.getIntrinsicWidth() + this._checkMarkPaddingRight;
        }
        setPadding(newLeftPadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        this._checkMarkDrawable = d;
        requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    @SuppressLint({"NewApi"})
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this._checkMarkDrawable != null) {
            this._checkMarkDrawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == this._checkMarkDrawable || super.verifyDrawable(who);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int y;
        super.onDraw(canvas);
        Drawable d = this._checkMarkDrawable;
        if (d != null) {
            int width = d.getIntrinsicWidth();
            int height = d.getIntrinsicHeight();
            int x = this._checkMarkPaddingLeft;
            int verticalGravity = this._gravity & 112;
            switch (verticalGravity) {
                case 16:
                    y = (getHeight() - height) / 2;
                    break;
                case 80:
                    y = (getHeight() - height) - this._checkMarkPaddingBottom;
                    break;
                default:
                    y = this._checkMarkPaddingTop;
                    break;
            }
            d.setBounds(x, y, x + width, y + height);
            d.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(CHECKED_STATE_SET.length + extraSpace);
        if (this._editing && this._checked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this._checkMarkDrawable != null && this._checkMarkDrawable.setState(getDrawableState())) {
            invalidate();
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            event.setChecked(this._checked);
        }
        return populated;
    }
}
