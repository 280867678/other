package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/* loaded from: classes2.dex */
public class GroupListView extends ExpandableListView {
    private int _ensuredPosition;

    public GroupListView(Context context) {
        super(context);
        this._ensuredPosition = -1;
    }

    public GroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._ensuredPosition = -1;
    }

    public GroupListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._ensuredPosition = -1;
    }

    @SuppressLint({"NewApi"})
    public GroupListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._ensuredPosition = -1;
    }

    public void ensureVisible(int position) {
        this._ensuredPosition = position;
    }

    @Override // android.widget.ExpandableListView, android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    @SuppressLint({"NewApi"})
    protected void dispatchDraw(Canvas canvas) {
        if (this._ensuredPosition >= 0 && getCount() > 0) {
            try {
                if (this._ensuredPosition < getFirstVisiblePosition() || this._ensuredPosition >= getLastVisiblePosition()) {
                    setSelection(this._ensuredPosition);
                    if (Build.VERSION.SDK_INT >= 11) {
                        smoothScrollToPositionFromTop(this._ensuredPosition, getHeight() / 4, 0);
                    }
                    return;
                }
            } finally {
                this._ensuredPosition = -1;
            }
        }
        super.dispatchDraw(canvas);
    }
}
