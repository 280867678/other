package com.mxtech.preference;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

@TargetApi(11)
/* loaded from: classes.dex */
public class MXPreferenceFragment extends PreferenceFragment {
    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView;
        View layout = super.onCreateView(inflater, container, savedInstanceState);
        if (Build.VERSION.SDK_INT < 21 && layout != null && (listView = (ListView) layout.findViewById(16908298)) != null) {
            ToolbarPreferenceActivity.stylizeListView(listView);
        }
        return layout;
    }

    @Override // android.preference.PreferenceFragment
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        MXPreferenceActivity.cleanseDisposedPreferences(preferenceScreen);
        super.setPreferenceScreen(preferenceScreen);
    }

    protected PreferenceGroup findPreferenceParent(Preference preference) {
        return MXPreferenceActivity.findPreferenceParent(getPreferenceScreen(), preference);
    }
}
