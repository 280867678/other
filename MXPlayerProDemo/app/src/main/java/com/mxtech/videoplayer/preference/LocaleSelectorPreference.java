package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import com.mxtech.LocaleUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.preference.AppCompatPreference;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.widget.LocaleMultiSelector;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class LocaleSelectorPreference extends AppCompatPreference implements DialogInterface.OnClickListener {
    private AlertDialog _dialog;
    private int _dialogFlags;
    private String _dialogTitle;
    private Locale[] _locales;
    private CharSequence _originalSummary;
    @Nullable
    private LocaleMultiSelector _selector;

    @TargetApi(21)
    public LocaleSelectorPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public LocaleSelectorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public LocaleSelectorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public LocaleSelectorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this._originalSummary = getSummary();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LocaleSelectorPreference, defStyleAttr, defStyleRes);
        if (a.getBoolean(R.styleable.LocaleSelectorPreference_ignoreCountryCode, false)) {
            this._dialogFlags |= 1;
        }
        this._dialogTitle = a.getString(R.styleable.LocaleSelectorPreference_android_dialogTitle);
        if (this._dialogTitle == null) {
            this._dialogTitle = a.getString(R.styleable.LocaleSelectorPreference_android_title);
        }
        a.recycle();
    }

    @Override // android.preference.Preference
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return LocaleUtils.parseLocales(a.getString(index));
    }

    @Override // android.preference.Preference
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        if (restore) {
            defaultValue = LocaleUtils.parseLocales(getPersistedString(null));
        }
        this._locales = (Locale[]) defaultValue;
        updateSummary();
    }

    @Override // android.preference.Preference
    protected void onClick() {
        if (this._dialog == null || !this._dialog.isShowing()) {
            showDialog(null);
        }
    }

    private void showDialog(Bundle state) {
        Context context = getContext();
        Activity activity = AppUtils.getActivityFrom(context);
        if (activity == null || !activity.isFinishing()) {
            this._selector = new LocaleMultiSelector(context);
            this._selector.setFlags(this._dialogFlags);
            this._selector.setDefaultLocales(this._locales);
            this._dialog = this._selector.create();
            if (this._dialogTitle != null) {
                this._dialog.setTitle(this._dialogTitle);
            }
            if (state != null) {
                this._dialog.onRestoreInstanceState(state);
            }
            this._dialog.setCanceledOnTouchOutside(true);
            this._dialog.setButton(-1, context.getString(17039370), this);
            this._dialog.setButton(-2, context.getString(17039360), (DialogInterface.OnClickListener) null);
            this._dialog.show();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        Locale[] localeArr;
        this._locales = this._selector.getSelected();
        L.sb.setLength(0);
        boolean first = true;
        for (Locale locale : this._locales) {
            if (first) {
                first = false;
            } else {
                L.sb.append(',');
            }
            L.sb.append(locale.toString());
        }
        persistString(L.sb.toString());
        updateSummary();
    }

    private void updateSummary() {
        if (this._locales.length > 0) {
            setSummary(P.getNativeLocaleNames(this._locales));
        } else {
            setSummary(this._originalSummary);
        }
    }

    @Override // android.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (this._dialog == null || !this._dialog.isShowing()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.dialogBundle = this._dialog.onSaveInstanceState();
        return myState;
    }

    @Override // android.preference.Preference
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        showDialog(myState.dialogBundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.mxtech.videoplayer.preference.LocaleSelectorPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            this.dialogBundle = source.readBundle();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }
}
