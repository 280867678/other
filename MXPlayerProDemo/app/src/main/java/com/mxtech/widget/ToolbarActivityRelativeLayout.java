package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.widget.RelativeLayout;

/* loaded from: classes2.dex */
public class ToolbarActivityRelativeLayout extends RelativeLayout {
    public ToolbarActivityRelativeLayout(Context context) {
        super(context);
    }

    public ToolbarActivityRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarActivityRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint({"NewApi"})
    public ToolbarActivityRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
