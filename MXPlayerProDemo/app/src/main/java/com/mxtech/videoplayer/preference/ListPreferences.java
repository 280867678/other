package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.widget.Toast;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public final class ListPreferences extends AbstractPreferenceActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_list);
        updateList((PreferenceGroup) findPreference("category_list_appearance"), findPreference(Key.RESPECT_NOMEDIA), findPreference(Key.SHOW_HIDDEN));
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_list);
            ListPreferences.updateList((PreferenceGroup) findPreference("category_list_appearance"), findPreference(Key.RESPECT_NOMEDIA), findPreference(Key.SHOW_HIDDEN));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateList(PreferenceGroup appearanceGroup, Preference prefRespectNoMedia, Preference prefShowHidden) {
        prefRespectNoMedia.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.ListPreferences.1
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!((Boolean) newValue).booleanValue()) {
                    Toast.makeText(preference.getContext(), R.string.alert_rescan, 0).show();
                    return true;
                }
                return true;
            }
        });
        prefShowHidden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.ListPreferences.2
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((Boolean) newValue).booleanValue()) {
                    Toast.makeText(preference.getContext(), R.string.alert_rescan, 0).show();
                    return true;
                }
                return true;
            }
        });
    }
}
