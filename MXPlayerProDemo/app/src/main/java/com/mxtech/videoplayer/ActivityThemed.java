package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ActionMode;
import android.view.Window;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;

/* loaded from: classes.dex */
public abstract class ActivityThemed extends ActivityVPBase {
    private boolean _hasStatusBarColor;
    protected int actionModeStatusBarColor;
    protected int colorPrimaryDark;
    protected boolean hardwareAccelerated;

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCreate(Bundle saved, int resId) {
        P.applyHardwareAccelerationToWindow(this);
        setTheme(getThemeResourceId());
        if (Build.VERSION.SDK_INT >= 21) {
            TypedArray a = obtainStyledAttributes(R.styleable.ActivityThemed);
            this.colorPrimaryDark = a.getColor(R.styleable.ActivityThemed_colorPrimaryDark, ViewCompat.MEASURED_STATE_MASK);
            this.actionModeStatusBarColor = a.getColor(R.styleable.ActivityThemed_actionModeStatusBarColor, 0);
            a.recycle();
            this._hasStatusBarColor = true;
        } else {
            this.colorPrimaryDark = ViewCompat.MEASURED_STATE_MASK;
            this.actionModeStatusBarColor = 0;
        }
        super.onCreate(saved);
        if (resId != 0) {
            setContentView(resId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    @SuppressLint({"NewApi"})
    public void onStart() {
        boolean hasColor;
        int i = ViewCompat.MEASURED_STATE_MASK;
        if (this.colorPrimaryDark != -16777216 && this._hasStatusBarColor != (hasColor = App.prefs.getBoolean(Key.LIST_COLORIZE_NOTIFICATION_BAR, false))) {
            this._hasStatusBarColor = hasColor;
            Window window = getWindow();
            if (hasColor) {
                i = this.colorPrimaryDark;
            }
            window.setStatusBarColor(i);
        }
        super.onStart();
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    @SuppressLint({"NewApi"})
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (this.actionModeStatusBarColor != 0 && this._hasStatusBarColor) {
            getWindow().setStatusBarColor(this.actionModeStatusBarColor);
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    @SuppressLint({"NewApi"})
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (this.actionModeStatusBarColor != 0 && this._hasStatusBarColor) {
            getWindow().setStatusBarColor(this.colorPrimaryDark);
        }
    }

    protected int getThemeResourceId() {
        return P.getTheme();
    }
}
