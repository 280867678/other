package com.mxtech.preference;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.mxtech.app.AppCompatUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class ToolbarPreferenceActivity extends MXPreferenceActivity {
    private int _displayOptions;
    private int _lastToolbarOrientation = -1;
    private Toolbar _toolbar;
    private View.OnClickListener _upNavigator;
    private boolean _usingAppCompatTheme;
    protected int orientation;

    private View.OnClickListener getUpNavigator() {
        if (this._upNavigator == null) {
            this._upNavigator = new View.OnClickListener() { // from class: com.mxtech.preference.ToolbarPreferenceActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ToolbarPreferenceActivity.this.onBackPressed();
                }
            };
        }
        return this._upNavigator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void stylizeListView(@NonNull ListView listView) {
        TypedArray a = listView.getContext().obtainStyledAttributes(new int[]{R.attr.listChoiceBackgroundIndicator});
        Drawable listSelector = a.getDrawable(0);
        if (listSelector != null) {
            listView.setSelector(listSelector);
        }
        a.recycle();
        listView.setCacheColorHint(0);
        listView.setDivider(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        if (layoutInflater.getFactory() == null) {
            layoutInflater.setFactory(this);
        }
        super.onCreate(savedInstanceState);
        Configuration cfg = getResources().getConfiguration();
        this.orientation = cfg.orientation;
        if (Build.VERSION.SDK_INT < 21) {
            stylizeListView(getListView());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.MXPreferenceActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        resetToolbarsIfNeeded();
    }

    @Override // com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.ListActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        TypedArray a = obtainStyledAttributes(new int[]{R.attr.windowActionBar});
        this._usingAppCompatTheme = a.hasValue(0);
        a.recycle();
        resetToolbarsIfNeeded();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.orientation != newConfig.orientation) {
            this.orientation = newConfig.orientation;
            onOrientationChanged(this.orientation);
        }
    }

    protected void onOrientationChanged(int orientation) {
        resetToolbarsIfNeeded(orientation);
    }

    @Override // com.mxtech.preference.MXPreferenceActivity
    public void setSupportActionBar(Toolbar toolbar) {
        this._toolbar = toolbar;
        this._displayOptions = AppCompatUtils.applyToolbarDisplayOptions(this._toolbar);
        if ((this._displayOptions & 8) != 0) {
            this._toolbar.setTitle(getTitle());
        }
        if ((this._displayOptions & 4) != 0) {
            this._toolbar.setNavigationOnClickListener(getUpNavigator());
        }
        super.setSupportActionBar(toolbar);
    }

    private void resetToolbarsIfNeeded() {
        resetToolbarsIfNeeded(getResources().getConfiguration().orientation);
    }

    private void resetToolbarsIfNeeded(int orientation) {
        if ((this._toolbar == null || this.started) && orientation != this._lastToolbarOrientation) {
            resetToolbar(orientation);
        }
    }

    private void resetToolbar(int orientation) {
        Toolbar toolbar;
        this._lastToolbarOrientation = orientation;
        if (this._usingAppCompatTheme && (toolbar = AppCompatUtils.resetToolbar(this, 0)) != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override // android.app.Activity
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (this._toolbar != null && (this._displayOptions & 8) != 0) {
            this._toolbar.setTitle(title);
        }
    }
}
