package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* loaded from: classes2.dex */
public class OptionBar extends LinearLayout {
    public final Rect rect;

    public OptionBar(Context context) {
        super(context);
        this.rect = new Rect();
    }

    public OptionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.rect = new Rect();
    }

    public boolean hitTest(int x, int y) {
        return this.rect.contains(x, y);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (l < r && t < b) {
            this.rect.left = l;
            this.rect.top = t;
            this.rect.right = r;
            this.rect.bottom = b;
        }
    }
}
