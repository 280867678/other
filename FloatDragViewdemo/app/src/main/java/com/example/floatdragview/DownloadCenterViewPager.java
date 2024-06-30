package com.example.floatdragview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class DownloadCenterViewPager extends ViewPager {

    /* renamed from: a */
    private boolean f18096a;

    public DownloadCenterViewPager(Context context) {
        super(context);
        this.f18096a = true;
    }

    public DownloadCenterViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f18096a = true;
    }

    public void setCanScroll(boolean z) {
        this.f18096a = z;
    }

    @Override // android.support.p003v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.f18096a) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // android.support.p003v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.f18096a) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }
}
