package com.mxtech.preference;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes2.dex */
public class EditFloatPreference extends AppCompatEditTextPreference {
    public EditFloatPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditFloatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditFloatPreference(Context context) {
        super(context);
    }

    @Override // android.preference.Preference
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedFloat(defaultReturnValue != null ? Float.valueOf(defaultReturnValue).floatValue() : 0.0f));
    }

    @Override // android.preference.Preference
    protected boolean persistString(String value) {
        if (value != null) {
            try {
                if (value.length() > 0) {
                    return persistFloat(Float.valueOf(value).floatValue());
                }
            } catch (NumberFormatException e) {
            }
        }
        return persistFloat(0.0f);
    }
}
