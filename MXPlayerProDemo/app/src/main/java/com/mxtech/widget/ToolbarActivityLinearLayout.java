package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.widget.LinearLayout;

/* loaded from: classes2.dex */
public class ToolbarActivityLinearLayout extends LinearLayout {
    public ToolbarActivityLinearLayout(Context context) {
        super(context);
    }

    public ToolbarActivityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint({"NewApi"})
    public ToolbarActivityLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint({"NewApi"})
    public ToolbarActivityLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    @SuppressLint({"NewApi"})
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        if (11 > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > 13) {
            return super.startActionModeForChild(originalView, callback);
        }
        return null;
    }
}
