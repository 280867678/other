package com.mxtech.videoplayer;

import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

import androidx.core.view.ViewCompat;

public class ScreenStyle {

    private ColorStateList _controlColorNormalStateList;
    private int _controlColorNormal;
    private ColorFilter _controlColorNormalFilter;
    private boolean _frameBorder;
    private int _frameColor;
    private static ScreenStyle singleton;

    public static ScreenStyle getInstance() {
        if (singleton == null) {
            singleton = new ScreenStyle();
        }
        return singleton;
    }




    public ColorStateList getControlNormalColorStateList() {
        if (this._controlColorNormalStateList == null) {
            this._controlColorNormalStateList = ColorStateList.valueOf(this._controlColorNormal);
        }
        return this._controlColorNormalStateList;
    }

    public ColorFilter getControlNormalColorFilter() {
        if (this._controlColorNormalFilter == null) {
            this._controlColorNormalFilter = new PorterDuffColorFilter(this._controlColorNormal, PorterDuff.Mode.SRC_ATOP);
        }
        return this._controlColorNormalFilter;
    }

    public int getControlColorNormal() {
        return this._controlColorNormal;
    }

    public boolean getFrameBorder() {
        return this._frameBorder;
    }

    public void setFrameBackgroundTo(View view, int draw) {
        ColorDrawable d;
        GradientDrawable d2;
        Drawable old = view.getBackground();
        if ((draw & 1) != 0) {
            if (old instanceof GradientDrawable) {
                d2 = (GradientDrawable) old;
            } else {
                d2 = new GradientDrawable();
            }
            d2.setStroke(1, (getControlColorNormal() & ViewCompat.MEASURED_SIZE_MASK) | -2013265920);
            if ((draw & 2) != 0) {
                d2.setColor(getFrameColor());
            } else {
                d2.setColor(0);
            }
            view.setBackgroundDrawable(d2);
        } else if ((draw & 2) != 0) {
            if (Build.VERSION.SDK_INT < 11) {
                view.setBackgroundColor(getFrameColor());
                return;
            }
            if (old instanceof ColorDrawable) {
                d = (ColorDrawable) old;
            } else {
                d = new ColorDrawable();
            }
            d.setColor(getFrameColor());
            view.setBackgroundDrawable(d);
        } else {
            view.setBackgroundDrawable(null);
        }
    }

    public int getFrameColor() {
        return this._frameColor;
    }





}
