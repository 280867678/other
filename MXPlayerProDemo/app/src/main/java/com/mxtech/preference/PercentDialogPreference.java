package com.mxtech.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class PercentDialogPreference extends AppCompatEditTextPreference implements DialogInterface.OnShowListener, View.OnClickListener {
    private int _defaultValue;
    private int _maxValue;
    private int _minValue;
    private boolean _showResetButton;

    public PercentDialogPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public PercentDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public PercentDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    public PercentDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentDialogPreference, defStyleAttr, defStyleRes);
        this._showResetButton = a.getBoolean(R.styleable.PercentDialogPreference_showResetButton, false);
        if (this._showResetButton) {
            Object defaultValue = onGetDefaultValue(a, R.styleable.PercentDialogPreference_android_defaultValue);
            if (defaultValue instanceof Integer) {
                this._defaultValue = ((Integer) defaultValue).intValue();
            } else if (defaultValue instanceof String) {
                this._defaultValue = Integer.valueOf((String) defaultValue).intValue();
            } else {
                this._showResetButton = false;
            }
        }
        this._minValue = a.getInt(R.styleable.PercentDialogPreference_minValue, Integer.MIN_VALUE);
        this._maxValue = a.getInt(R.styleable.PercentDialogPreference_maxValue, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override // android.preference.Preference
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(defaultReturnValue != null ? Integer.valueOf(defaultReturnValue).intValue() : this._defaultValue));
    }

    @Override // android.preference.Preference
    protected boolean persistString(String s) {
        int value;
        if (s != null) {
            try {
                if (s.length() > 0) {
                    value = Integer.valueOf(s).intValue();
                    if (value < this._minValue) {
                        value = this._minValue;
                    } else if (value > this._maxValue) {
                        value = this._maxValue;
                    }
                    return persistInt(value);
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        value = this._defaultValue;
        return persistInt(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        if (this._showResetButton) {
            builder.setNeutralButton(Integer.toString(this._defaultValue) + '%', (DialogInterface.OnClickListener) null);
        }
    }

    @Override // com.mxtech.preference.AppCompatDialogPreference
    protected void onPrepareDialog(Dialog dialog) {
        if (this._showResetButton) {
            dialog.setOnShowListener(this);
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Button resetButton = ((AlertDialog) dialog).getButton(-3);
        if (resetButton != null) {
            resetButton.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        EditText tv = getEditText();
        if (tv != null) {
            tv.setText(Integer.toString(this._defaultValue));
        }
    }
}
