package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.widget.Toast;
import com.mxtech.StringUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.preference.AppCompatListPreference;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;


/* loaded from: classes.dex */
public final class PlayerPreferences extends AbstractPreferenceActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_player);
        updatePlayer((PreferenceGroup) findPreference("category_player_playback"), (PreferenceGroup) findPreference("category_player_misc"));
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_player);
            PlayerPreferences.updatePlayer((PreferenceGroup) findPreference("category_player_playback"), (PreferenceGroup) findPreference("category_player_misc"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PassiveRestarter implements Preference.OnPreferenceChangeListener, ActivityRegistry.StateChangeListener {
        private final int _msgId;

        PassiveRestarter(int msgId) {
            this._msgId = msgId;
        }

        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ActivityRegistry.registerStateListener(this);
            Toast.makeText(preference.getContext(), this._msgId, 1).show();
            return true;
        }

        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
        public void onActivityCreated(Activity activity) {
        }

        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
        public void onActivityStateChanged(Activity activity, int state) {
        }

        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
        public void onActivityRemoved(Activity activity) {
            if (!ActivityRegistry.hasActivity()) {
                App.quit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updatePlayer(PreferenceGroup groupPlayback, PreferenceGroup groupMisc) {
        AppCompatDialogPreference prefSpeed = (AppCompatDialogPreference) groupPlayback.findPreference(Key.DEFAULT_PLAYBACK_SPEED);
        if (prefSpeed != null) {
            prefSpeed.setDialogMessage(StringUtils.getString_s(R.string.default_playback_speed_dialog_message, 25, 400));
        }
        Preference prefSoftKeys = groupMisc.findPreference(Key.SOFT_MAIN_KEYS);
        if (prefSoftKeys != null) {
            prefSoftKeys.setOnPreferenceChangeListener(new PassiveRestarter(R.string.ask_restart_app));
        }
        AppCompatListPreference pref = (AppCompatListPreference) groupMisc.findPreference(Key.SCREEN_LOCK_MODE);
        if (pref != null) {
            pref.setEntries(new CharSequence[]{StringUtils.getString_s(R.string.screen_lock_mode_name, 1), StringUtils.getString_s(R.string.screen_lock_mode_name, 2), StringUtils.getString_s(R.string.screen_lock_mode_name, 3)});
        }
    }
}
