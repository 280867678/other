package com.mxtech.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.preference.Preference;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.AttributeSet;
import com.mxtech.DeviceUtils;
import com.mxtech.videoplayer.pro.R;
import java.util.Set;

/* loaded from: classes.dex */
public class AppCompatPreference extends Preference {
    private static final int LEANBACK_TV = 2;
    private static final String TAG = "MX.AppCompatPreference";
    private static final int TOUCH_SCREEN = 4;
    private static final int TV = 1;
    private static final int TV_SYSTEM = 8;
    boolean disposed;

    @TargetApi(21)
    public AppCompatPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public AppCompatPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public AppCompatPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842894);
    }

    public AppCompatPreference(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppCompatPreference, defStyleAttr, defStyleRes);
        if (a.getBoolean(R.styleable.AppCompatPreference_dispose, false)) {
            this.disposed = true;
            return;
        }
        int minSdkVersion = a.getInt(R.styleable.AppCompatPreference_android_minSdkVersion, 0);
        if (Build.VERSION.SDK_INT < minSdkVersion) {
            this.disposed = true;
            return;
        }
        int maxSdkVersion = a.getInt(R.styleable.AppCompatPreference_android_maxSdkVersion, Integer.MAX_VALUE);
        if (Build.VERSION.SDK_INT > maxSdkVersion) {
            this.disposed = true;
            return;
        }
        int requiredFeatures = a.getInt(R.styleable.AppCompatPreference_requiredFeatures, 0);
        if (!hasAllFeatures(requiredFeatures)) {
            this.disposed = true;
            return;
        }
        int exclusiveFeatures = a.getInt(R.styleable.AppCompatPreference_exclusiveFeatures, 0);
        if (hasAnyFeatures(exclusiveFeatures)) {
            this.disposed = true;
        } else {
            a.recycle();
        }
    }

    private boolean hasAllFeatures(int features) {
        if ((features & 1) == 0 || DeviceUtils.isTV) {
            if ((features & 2) == 0 || DeviceUtils.isLeanbackTV) {
                if ((features & 4) == 0 || DeviceUtils.hasTouchScreen) {
                    return (features & 8) == 0 || DeviceUtils.getTVMode();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean hasAnyFeatures(int features) {
        if ((features & 1) == 0 || !DeviceUtils.isTV) {
            if ((features & 2) == 0 || !DeviceUtils.isLeanbackTV) {
                if ((features & 4) == 0 || !DeviceUtils.hasTouchScreen) {
                    return (features & 8) != 0 && DeviceUtils.getTVMode();
                }
                return true;
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final String[] readStringArray(Parcel parcel) {
        String[] array = null;
        int length = parcel.readInt();
        if (length >= 0) {
            array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = parcel.readString();
            }
        }
        return array;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final void writeStringArray(Parcel parcel, String[] val) {
        if (val != null) {
            int N = val.length;
            parcel.writeInt(N);
            for (String str : val) {
                parcel.writeString(str);
            }
            return;
        }
        parcel.writeInt(-1);
    }

    @Override // android.preference.Preference
    public boolean persistStringSet(Set<String> values) {
        if (shouldPersist()) {
            if (values.equals(getPersistedStringSet(null))) {
                return true;
            }
            SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
            editor.putStringSet(getKey(), values);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
            return true;
        }
        return false;
    }

    @Override // android.preference.Preference
    public Set<String> getPersistedStringSet(Set<String> defaultReturnValue) {
        return !shouldPersist() ? defaultReturnValue : getPreferenceManager().getSharedPreferences().getStringSet(getKey(), defaultReturnValue);
    }
}
