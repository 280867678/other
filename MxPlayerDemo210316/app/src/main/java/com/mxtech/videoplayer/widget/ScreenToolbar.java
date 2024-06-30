package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.mxtech.videoplayer.ScreenStyle;

import java.util.ArrayList;

public final class ScreenToolbar extends Toolbar implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "MX.Screen.Toolbar";
    private final ArrayList<Drawable> _styledDrawables;
    private final ArrayList<TextView> _styledTextViews;

    public ScreenToolbar(Context context) {
        super(context);
        this._styledTextViews = new ArrayList<>();
        this._styledDrawables = new ArrayList<>();
    }

    public ScreenToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._styledTextViews = new ArrayList<>();
        this._styledDrawables = new ArrayList<>();
    }

    public ScreenToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._styledTextViews = new ArrayList<>();
        this._styledDrawables = new ArrayList<>();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void setNormalColorRecursive(View view, ScreenStyle style) {
        ImageSpan[] imageSpanArr;
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (!this._styledTextViews.contains(view)) {
                this._styledTextViews.add(tv);
                tv.setTextColor(style.getControlNormalColorStateList());
                CharSequence text = tv.getText();
                if (text instanceof Spanned) {
                    Spanned spannedText = (Spanned) text;
                    for (ImageSpan span : (ImageSpan[]) spannedText.getSpans(0, text.length(), ImageSpan.class)) {
                        span.getDrawable().mutate().setColorFilter(style.getControlNormalColorFilter());
                    }
                }
            }
            Drawable[] drawables = tv.getCompoundDrawables();
            for (Drawable d : drawables) {
                if (d != null && !this._styledDrawables.contains(d)) {
                    this._styledDrawables.add(d);
                    GraphicUtils.safeMutate(d).setColorFilter(style.getControlNormalColorFilter());
                }
            }
        } else if (view instanceof ImageView) {
            Drawable d2 = ((ImageView) view).getDrawable();
            if (d2 != null && !this._styledDrawables.contains(d2)) {
                this._styledDrawables.add(d2);
                GraphicUtils.safeMutate(d2).setColorFilter(style.getControlNormalColorFilter());
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setNormalColorRecursive(group.getChildAt(i), style);
            }
        }
    }

    public void setStyle(ScreenStyle style, int changes) {
        if ((changes & 64) != 0) {
            setTitleTextColor(style.getControlColorNormal());
            this._styledTextViews.clear();
            this._styledDrawables.clear();
            setNormalColorRecursive(this, style);
        }
        if (style.getFrameBorder()) {
            if ((changes & 88) != 0) {
                style.setFrameBackgroundTo(this, 3);
            }
        } else if ((changes & 24) != 0) {
            style.setFrameBackgroundTo(this, 2);
        }
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        setNormalColorRecursive(this, ScreenStyle.getInstance());
    }
}

 class GraphicUtils {
    public static Drawable safeMutate(Drawable d) {
        return (Build.VERSION.SDK_INT >= 8 || !(d instanceof StateListDrawable)) ? d.mutate() : d;
    }
}