package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.os.Bundle;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;

@TargetApi(11)
/* loaded from: classes2.dex */
public final class DevelopmentPreferences extends AbstractPreferenceActivity {
    private static final String TAG = App.TAG + ".DevelopmentPreferences";

    /* loaded from: classes2.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_development);
        }
    }
}
