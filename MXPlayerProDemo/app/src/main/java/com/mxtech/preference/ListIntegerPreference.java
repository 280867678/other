package com.mxtech.preference;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes2.dex */
public class ListIntegerPreference extends AppCompatListPreference {
    public ListIntegerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListIntegerPreference(Context context) {
        super(context);
    }

    @Override // android.preference.Preference
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(defaultReturnValue != null ? Integer.valueOf(defaultReturnValue).intValue() : 0));
    }

    @Override // android.preference.Preference
    protected boolean persistString(String value) {
        if (value != null) {
            try {
                if (value.length() > 0) {
                    return persistInt(Integer.valueOf(value).intValue());
                }
            } catch (NumberFormatException e) {
            }
        }
        return persistInt(0);
    }
}
