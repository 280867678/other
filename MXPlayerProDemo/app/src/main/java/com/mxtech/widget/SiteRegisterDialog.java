package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.DialogPlatform;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.MXApplication;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.pro.R;
import java.util.regex.Pattern;

@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public final class SiteRegisterDialog extends AlertDialog implements DialogInterface.OnShowListener, View.OnClickListener, DialogInterface.OnDismissListener {
    private static final Pattern EMAIL_ADDRESS_PATTERN;
    public static final int FLAG_ASYNC_CREATE_ACCOUNT = 4;
    public static final int FLAG_REQUIRE_EMAIL_ADDRESS = 2;
    public static final int FLAG_REQUIRE_USERNAME = 1;
    public static final int STATUS_EMAIL_ADDRESS_ALREADY_USED = -2;
    public static final int STATUS_NETWORK_ERROR = -11;
    public static final int STATUS_OK = 0;
    public static final int STATUS_OK_NEED_EMAIL_CONFIRM = 1;
    public static final int STATUS_SERVER_ERROR = -10;
    public static final int STATUS_UNKNOWN_ERROR = -100;
    public static final int STATUS_USERNAME_ALREADY_EXIST = -1;
    private static final String TAG = "MX.SiteRegisterDialog";
    private AppCompatProgressDialog _accountCreationProgress;
    private AsyncTask _accountCreationTask;
    private final Client _client;
    private final DialogPlatform _dialogPlatform;
    @Nullable
    private DecorEditText _emailInput;
    private final int _flags;
    private DecorEditText _passwordInput;
    @Nullable
    private DecorEditText _usernameInput;
    private TextView _warningView;

    /* loaded from: classes2.dex */
    public interface Client {
        int createAccount(SiteRegisterDialog siteRegisterDialog, String str, String str2, String str3) throws InterruptedException;

        String getErrorMessage(SiteRegisterDialog siteRegisterDialog, int i);

        void onAccountCreated(SiteRegisterDialog siteRegisterDialog, String str, String str2);
    }

    static {
        if (Build.VERSION.SDK_INT < 8) {
            EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+");
        } else {
            EMAIL_ADDRESS_PATTERN = Patterns.EMAIL_ADDRESS;
        }
    }

    public SiteRegisterDialog(DialogPlatform dialogPlatform, int flags, Client client) {
        super(dialogPlatform.getContext());
        this._flags = flags;
        this._dialogPlatform = dialogPlatform;
        this._client = client;
        init();
    }

    public SiteRegisterDialog(DialogPlatform dialogPlatform, int theme, int flags, Client client) {
        super(dialogPlatform.getContext(), theme);
        this._flags = flags;
        this._dialogPlatform = dialogPlatform;
        this._client = client;
        init();
    }

    @SuppressLint({"InflateParams"})
    private void init() {
        ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.site_register, (ViewGroup) null);
        if ((this._flags & 1) != 0) {
            this._usernameInput = (DecorEditText) layout.findViewById(R.id.username);
        } else {
            layout.findViewById(R.id.username).setVisibility(8);
        }
        if ((this._flags & 2) != 0) {
            this._emailInput = (DecorEditText) layout.findViewById(R.id.email_address);
            this._emailInput.setConstraint(1, Integer.MAX_VALUE, EMAIL_ADDRESS_PATTERN, MXApplication.context.getString(R.string.email_invalid));
        } else {
            layout.findViewById(R.id.email_address).setVisibility(8);
        }
        this._passwordInput = (DecorEditText) layout.findViewById(R.id.password);
        this._warningView = (TextView) layout.findViewById(R.id.warning);
        setView(layout);
        setButton(-1, MXApplication.context.getString(17039370), (DialogInterface.OnClickListener) null);
        setButton(-2, MXApplication.context.getString(17039360), (DialogInterface.OnClickListener) null);
        setOnShowListener(this);
    }

    public final DecorEditText getUsernameEdit() {
        return this._usernameInput;
    }

    public final DecorEditText getPasswordEdit() {
        return this._passwordInput;
    }

    @Override // android.app.Dialog
    public void show() {
        setOnDismissListener(this);
        super.show();
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Button button = getButton(-1);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this._accountCreationTask == null && !this._dialogPlatform.isFinishing()) {
            boolean valid = true;
            if (this._emailInput != null) {
                this._emailInput.trim();
                if (!this._emailInput.validate()) {
                    valid = false;
                }
            }
            if (this._usernameInput != null) {
                this._usernameInput.trim();
                if (!this._usernameInput.validate()) {
                    valid = false;
                }
            }
            if (this._passwordInput != null && !this._passwordInput.validate()) {
                valid = false;
            }
            if (valid) {
                final String email = this._emailInput != null ? this._emailInput.getText().toString() : null;
                final String username = this._usernameInput != null ? this._usernameInput.getText().toString() : null;
                final String password = this._passwordInput != null ? this._passwordInput.getText().toString() : null;
                this._warningView.setVisibility(8);
                if ((this._flags & 4) != 0) {
                    this._accountCreationTask = new AsyncTask2<Void, Void, Integer>() { // from class: com.mxtech.widget.SiteRegisterDialog.1
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public Integer doInBackground(Void... params) {
                            try {
                                return Integer.valueOf(SiteRegisterDialog.this._client.createAccount(SiteRegisterDialog.this, email, username, password));
                            } catch (Exception e) {
                                return null;
                            }
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public void onPostExecute(Integer result) {
                            if (SiteRegisterDialog.this._accountCreationTask == this) {
                                SiteRegisterDialog.this._accountCreationTask = null;
                                if (SiteRegisterDialog.this._accountCreationProgress != null) {
                                    SiteRegisterDialog.this._accountCreationProgress.dismiss();
                                }
                                if (result != null) {
                                    if (result.intValue() == 0) {
                                        SiteRegisterDialog.this.onAccountCreationSucceeded(username, password, false);
                                    } else if (result.intValue() == 1) {
                                        SiteRegisterDialog.this.onAccountCreationSucceeded(username, password, true);
                                    } else {
                                        SiteRegisterDialog.this.onAccountCreationFailed(result.intValue());
                                    }
                                }
                            }
                        }

                        @Override // android.os.AsyncTask
                        protected void onCancelled() {
                            onPostExecute((Integer) null);
                        }
                    }.executeParallel(new Void[0]);
                    Context context = getContext();
                    this._accountCreationProgress = new AppCompatProgressDialog(context);
                    this._accountCreationProgress.setProgressStyle(0);
                    this._accountCreationProgress.setMessage(context.getString(R.string.account_creation_notify));
                    this._dialogPlatform.showDialog(this._accountCreationProgress, this);
                    return;
                }
                try {
                    int status = this._client.createAccount(this, email, username, password);
                    if (status == 0) {
                        onAccountCreationSucceeded(username, password, false);
                    } else if (status == 1) {
                        onAccountCreationSucceeded(username, password, true);
                    } else {
                        onAccountCreationFailed(status);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        DialogRegistry dialogRegistry = this._dialogPlatform.getDialogRegistry();
        dialogRegistry.onDismiss(dialog);
        if (this._accountCreationTask != null) {
            this._accountCreationTask.cancel(true);
            this._accountCreationTask = null;
        }
        if (this._accountCreationProgress == dialog) {
            this._accountCreationProgress = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccountCreationSucceeded(String username, String password, boolean showEmailConfirmRequired) {
        this._client.onAccountCreated(this, username, password);
        dismiss();
        if (showEmailConfirmRequired) {
            Toast.makeText(getContext(), R.string.need_email_verification, 1).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccountCreationFailed(int status) {
        switch (status) {
            case -2:
                if (this._emailInput != null) {
                    this._emailInput.setError(R.string.email_already_used);
                    return;
                }
                return;
            case -1:
                if (this._usernameInput != null) {
                    this._usernameInput.setError(R.string.username_already_exist);
                    return;
                }
                return;
            default:
                this._warningView.setVisibility(0);
                this._warningView.setText(this._client.getErrorMessage(this, status));
                return;
        }
    }
}
