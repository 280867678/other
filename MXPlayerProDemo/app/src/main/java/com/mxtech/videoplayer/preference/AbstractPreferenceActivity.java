package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.Window;
import com.mxtech.app.MXApplication;
import com.mxtech.preference.ToolbarPreferenceActivity;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;

/* loaded from: classes.dex */
public abstract class AbstractPreferenceActivity extends ToolbarPreferenceActivity {
    private static final String TAG = App.TAG + ".AbstractPreferenceActivity";
    private boolean _hasStatusBarColor;
    @Nullable
    private P.PreferencesSession _preferencesSession;
    protected int colorPrimaryDark;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        P.applyHardwareAccelerationToWindow(this);
        setTheme(P.getTheme());
        if (Build.VERSION.SDK_INT >= 21) {
            TypedArray a = obtainStyledAttributes(R.styleable.ActivityThemed);
            this.colorPrimaryDark = a.getColor(R.styleable.ActivityThemed_colorPrimaryDark, ViewCompat.MEASURED_STATE_MASK);
            a.recycle();
            this._hasStatusBarColor = true;
        } else {
            this.colorPrimaryDark = ViewCompat.MEASURED_STATE_MASK;
        }
        super.onCreate(savedInstanceState);
        if (!((MXApplication) getApplication()).initInteractive(this)) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.app.Activity
    @SuppressLint({"NewApi"})
    public void onStart() {
        boolean hasColor;
        int i = ViewCompat.MEASURED_STATE_MASK;
        if (Build.VERSION.SDK_INT >= 21 && this.colorPrimaryDark != -16777216 && this._hasStatusBarColor != (hasColor = App.prefs.getBoolean(Key.LIST_COLORIZE_NOTIFICATION_BAR, false))) {
            this._hasStatusBarColor = hasColor;
            Window window = getWindow();
            if (hasColor) {
                i = this.colorPrimaryDark;
            }
            window.setStatusBarColor(i);
        }
        this._preferencesSession = new P.PreferencesSession();
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onStop() {
        if (this._preferencesSession != null) {
            this._preferencesSession.apply();
        }
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }
}
