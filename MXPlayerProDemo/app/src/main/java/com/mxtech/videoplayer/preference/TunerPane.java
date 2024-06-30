package com.mxtech.videoplayer.preference;

import android.content.SharedPreferences;
import android.view.View;

/* loaded from: classes2.dex */
public abstract class TunerPane {
    boolean dirty;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void applyChanges(SharedPreferences.Editor editor);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract View[] getTopmostFocusableViews();
}
