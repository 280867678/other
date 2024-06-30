package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ZoomButton;
import com.mxtech.graphics.CircleDrawable;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class CircleZoomButton extends ZoomButton {
    private int _currentTintColor;
    private ColorStateList _tintList;

    public CircleZoomButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CircleZoomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CircleZoomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"NewApi"})
    public CircleZoomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageButton, defStyleAttr, defStyleRes);
        CircleDrawable background = new CircleDrawable(a.getColorStateList(R.styleable.CircleImageButton_circleColor), a.getColorStateList(R.styleable.CircleImageButton_shadowColor), a.getColorStateList(R.styleable.CircleImageButton_strokeColor), a.getDimension(R.styleable.CircleImageButton_shadowRadius, 0.0f), a.getDimension(R.styleable.CircleImageButton_shadowDx, 0.0f), a.getDimension(R.styleable.CircleImageButton_shadowDy, 0.0f), a.getDimension(R.styleable.CircleImageButton_strokeWidth, 1.0f));
        this._tintList = a.getColorStateList(R.styleable.CircleImageButton_tint);
        a.recycle();
        setBackgroundDrawable(background);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        int color;
        if (this._tintList != null && (color = this._tintList.getColorForState(getDrawableState(), 0)) != this._currentTintColor) {
            GraphicUtils.safeMutate(getDrawable()).setColorFilter(color, PorterDuff.Mode.SRC_IN);
            this._currentTintColor = color;
        }
        super.drawableStateChanged();
    }
}
