package com.mxtech.videoplayer.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mxtech.preference.AppCompatEditTextPreference;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class HttpUserAgentPreference extends AppCompatEditTextPreference implements DialogInterface.OnShowListener, View.OnClickListener {
    private String _defaultUserAgent;

    public HttpUserAgentPreference(Context context) {
        super(context);
        init();
    }

    public HttpUserAgentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HttpUserAgentPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HttpUserAgentPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setDefaultValue(P.httpUserAgent);
        this._defaultUserAgent = P.getDefaultHttpUserAgent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setNeutralButton(App.context.getString(R.string.button_reset), (DialogInterface.OnClickListener) null);
    }

    @Override // com.mxtech.preference.AppCompatDialogPreference
    protected void onPrepareDialog(Dialog dialog) {
        dialog.setOnShowListener(this);
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
            tv.setText(this._defaultUserAgent);
        }
    }

    @Override // android.preference.Preference
    protected String getPersistedString(String defaultReturnValue) {
        return P.httpUserAgent;
    }

    @Override // android.preference.Preference
    protected boolean persistString(String value) {
        if (TextUtils.equals(value, this._defaultUserAgent)) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.remove(getKey());
            editor.apply();
            return true;
        }
        return super.persistString(value);
    }
}
