package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.mxtech.videoplayer.pro.R;
import java.util.List;

/* loaded from: classes.dex */
public final class ActivityPreferences extends AbstractPreferenceActivity {
    private static boolean _firstCall = true;

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        if (_firstCall) {
            _firstCall = false;
            P.getVideoZoomDelay();
            P.getItalicTag();
            P.getCorrectHWAspectRatio();
            P.getButtonBacklightOff();
            P.getSubtitleFadeout();
            P.getFastSeek();
            P.getSeekPreviews();
            P.getColorFormat();
            P.getScreenLockMode();
        }
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 11) {
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override // android.preference.PreferenceActivity
    @TargetApi(11)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.preference_header, target);
    }
}
