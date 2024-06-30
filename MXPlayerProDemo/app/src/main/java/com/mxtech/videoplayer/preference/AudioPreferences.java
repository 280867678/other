package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.widget.Toast;
import com.mxtech.preference.AppCompatCheckBoxPreference;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.App;


/* loaded from: classes.dex */
public final class AudioPreferences extends AbstractPreferenceActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_audio);
        updateUseAsAudioPlayer((AppCompatCheckBoxPreference) findPreference(Key.AUDIO_PLAYER));
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_audio);
            AudioPreferences.updateUseAsAudioPlayer((AppCompatCheckBoxPreference) findPreference(Key.AUDIO_PLAYER));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateUseAsAudioPlayer(AppCompatCheckBoxPreference pref) {
        pref.setChecked(P.isAudioPlayer);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.AudioPreferences.1
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PackageManager pm = App.context.getPackageManager();
                P.setUseAsAudioPlayer(((Boolean) newValue).booleanValue());
                int newState = P.isAudioPlayer ? 1 : 2;
                pm.setComponentEnabledSetting(P.localAudioDelegateName, newState, 1);
                if (P.isPlayLink()) {
                    pm.setComponentEnabledSetting(P.webAudioDelegateName, newState, 1);
                }
                if (P.isAudioPlayer) {
                    P.enableAudioExtension();
                    Toast.makeText(App.context, R.string.alert_rescan, 0).show();
                } else {
                    P.disableAudioExtension();
                }
                return true;
            }
        });
    }
}
