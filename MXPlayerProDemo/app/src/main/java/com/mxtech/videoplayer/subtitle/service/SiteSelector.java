package com.mxtech.videoplayer.subtitle.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.mxtech.StringUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.crypto.UsernamePasswordSaver;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.widget.DecorEditText;
import com.mxtech.widget.SiteRegisterDialog;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class SiteSelector {
    private static final String TAG = App.TAG + ".SiteSelector";
    private final AlertDialog _dialog;
    private final ArrayList<SiteHandler> _handlers = new ArrayList<>();

    /* loaded from: classes2.dex */
    public interface OnSiteSeletedListener {
        void onSiteSelected(SiteSelector siteSelector, String[] strArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"InflateParams", "NewApi"})
    public SiteSelector(DialogPlatform dialogPlatform, String[] defaultEnabledSites, final OnSiteSeletedListener onSiteSelectedListener) {
        Context context = dialogPlatform.getContext();
        this._dialog = new AlertDialog.Builder(context).create();
        View layout = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_sites_selector, (ViewGroup) null);
        this._handlers.add(new SiteHandler(dialogPlatform, layout, new OpenSubtitles(), isSiteEnabled(defaultEnabledSites, "opensubtitles.org"), Key.CREDENTIAL_OPENSUBTITLES, R.id.opensubtitles, R.id.opensubtitles_register, R.id.opensubtitles_userinfo_layout, R.id.opensubtitles_username, R.id.opensubtitles_password, R.id.opensubtitles_warning));
        this._dialog.setView(layout);
        this._dialog.setTitle(R.string.sites);
        this._dialog.setButton(-2, context.getString(17039360), (DialogInterface.OnClickListener) null);
        this._dialog.setButton(-1, context.getString(17039370), (DialogInterface.OnClickListener) null);
        this._dialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.mxtech.videoplayer.subtitle.service.SiteSelector.1
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialog) {
                Button okButton = ((AlertDialog) dialog).getButton(-1);
                if (okButton != null) {
                    okButton.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.subtitle.service.SiteSelector.1.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            if (SiteSelector.this.save()) {
                                onSiteSelectedListener.onSiteSelected(SiteSelector.this, SiteSelector.this.getSelectedSites());
                                SiteSelector.this._dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
        dialogPlatform.showDialog(this._dialog);
    }

    private boolean isSiteEnabled(String[] sites, String name) {
        for (String site : sites) {
            if (site.equals(name)) {
                return true;
            }
        }
        return false;
    }

    AlertDialog getDialog() {
        return this._dialog;
    }

    boolean save() {
        Iterator<SiteHandler> it = this._handlers.iterator();
        while (it.hasNext()) {
            SiteHandler handler = it.next();
            if (!handler.validate()) {
                return false;
            }
        }
        Iterator<SiteHandler> it2 = this._handlers.iterator();
        while (it2.hasNext()) {
            SiteHandler handler2 = it2.next();
            handler2.save();
        }
        return true;
    }

    String[] getSelectedSites() {
        ArrayList<String> enabledSites = new ArrayList<>();
        Iterator<SiteHandler> it = this._handlers.iterator();
        while (it.hasNext()) {
            SiteHandler handler = it.next();
            if (handler.isEnabled()) {
                enabledSites.add(handler.name());
            }
        }
        return (String[]) enabledSites.toArray(new String[enabledSites.size()]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SiteHandler extends ClickableSpan implements CompoundButton.OnCheckedChangeListener, TextWatcher, SiteRegisterDialog.Client {
        private final CheckBox _checkBox;
        private final Context _context;
        private final DialogPlatform _dialogPlatform;
        private final int _features;
        private UsernamePasswordSaver.Info _initialLoginInfo;
        private final String _keyCredential;
        private boolean _passwordChanged;
        @Nullable
        private final DecorEditText _passwordInput;
        @Nullable
        private final TextView _registerView;
        private final SubtitleService _service;
        @Nullable
        private final View _userInfoLayout;
        @Nullable
        private final DecorEditText _usernameInput;
        @Nullable
        private final TextView _warningView;

        SiteHandler(DialogPlatform dialogPlatform, View layout, SubtitleService service, boolean defaultEnabled, @Nullable String keyCredential, int idCheckbox, int idRegister, int idUserInfoLayout, int idUsername, int idPassword, int idWarning) {
            this._context = dialogPlatform.getContext();
            this._dialogPlatform = dialogPlatform;
            this._service = service;
            this._features = service.getFeatures();
            this._keyCredential = keyCredential;
            if (keyCredential != null) {
                this._userInfoLayout = layout.findViewById(idUserInfoLayout);
                this._usernameInput = (DecorEditText) layout.findViewById(idUsername);
                this._passwordInput = (DecorEditText) layout.findViewById(idPassword);
                this._warningView = (TextView) layout.findViewById(idWarning);
                this._registerView = (TextView) layout.findViewById(idRegister);
                if (this._registerView != null) {
                    if ((this._features & 3) != 0) {
                        SpannableString message = new SpannableString(this._context.getString(R.string.register));
                        message.setSpan(this, 0, message.length(), 33);
                        this._registerView.setText(message);
                        this._registerView.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        this._registerView.setVisibility(8);
                    }
                }
                if (this._warningView != null) {
                    String warning = StringUtils.getString_s(R.string.subtitle_site_login_info_missing_warning, this._service.name());
                    if ((this._features & 4) != 0) {
                        SpannableStringBuilder sb = new SpannableStringBuilder(warning).append(' ').append((CharSequence) this._context.getString(R.string.ask_visit_for_more_information));
                        String message2 = sb.toString();
                        int anchorStart = message2.indexOf(91);
                        if (anchorStart >= 0) {
                            int anchorEnd = message2.indexOf(93, anchorStart + 1);
                            if (anchorEnd > 0) {
                                sb.setSpan(this, anchorStart + 1, anchorEnd, 33);
                                sb.delete(anchorEnd, anchorEnd + 1);
                                sb.delete(anchorStart, anchorStart + 1);
                                this._warningView.setText(sb);
                                this._warningView.setTextColor(this._warningView.getTextColors().getDefaultColor());
                                this._warningView.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                        } else {
                            this._warningView.setText(warning);
                        }
                    }
                }
                if (this._usernameInput != null && this._passwordInput != null) {
                    String cred = App.prefs.getString(this._keyCredential, null);
                    if (cred != null) {
                        this._initialLoginInfo = UsernamePasswordSaver.decrypt(cred);
                        if (this._initialLoginInfo != null) {
                            this._usernameInput.setText(this._initialLoginInfo.username);
                            this._passwordInput.setText(this._initialLoginInfo.password);
                        }
                    }
                    this._service.setupInputs(this._context, this._usernameInput, this._passwordInput);
                    this._passwordInput.addTextChangedListener(this);
                }
            } else {
                this._userInfoLayout = null;
                this._usernameInput = null;
                this._passwordInput = null;
                this._registerView = null;
                this._warningView = null;
            }
            this._checkBox = (CheckBox) layout.findViewById(idCheckbox);
            this._checkBox.setChecked(defaultEnabled);
            this._checkBox.setOnCheckedChangeListener(this);
            updateLayout();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this._passwordChanged = true;
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }

        boolean isEnabled() {
            return this._checkBox.isChecked();
        }

        boolean validate() {
            if (this._usernameInput != null && this._passwordInput != null) {
                this._usernameInput.trim();
                if (this._usernameInput.length() > 0) {
                    boolean valid = true;
                    if (!this._usernameInput.validate()) {
                        valid = false;
                    }
                    if (!this._passwordInput.validate()) {
                        return false;
                    }
                    return valid;
                }
            }
            return true;
        }

        void save() {
            String password;
            String value;
            if (this._usernameInput != null && this._passwordInput != null) {
                String username = this._usernameInput.getText().toString().trim();
                if (this._passwordChanged) {
                    password = this._passwordInput.getText().toString();
                } else {
                    password = this._initialLoginInfo != null ? this._initialLoginInfo.password : "";
                }
                if (!UsernamePasswordSaver.equals(this._initialLoginInfo, username, password)) {
                    SharedPreferences.Editor editor = App.prefs.edit();
                    boolean saved = false;
                    if (username.length() > 0 && (value = UsernamePasswordSaver.encrypt(username, password)) != null) {
                        editor.putString(this._keyCredential, value);
                        saved = true;
                    }
                    if (!saved) {
                        editor.remove(this._keyCredential);
                    }
                    AppUtils.apply(editor);
                }
            }
        }

        String name() {
            return this._service.name();
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateLayout();
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            if (!this._dialogPlatform.isFinishing()) {
                if (widget == this._registerView) {
                    if ((this._features & 1) != 0) {
                        int flags = 4;
                        if ((this._features & 32) != 0) {
                            flags = 4 | 2;
                        }
                        if ((this._features & 16) != 0) {
                            flags |= 1;
                        }
                        SiteRegisterDialog dlg = new SiteRegisterDialog(this._dialogPlatform, flags, this);
                        this._service.setupInputs(this._context, dlg.getUsernameEdit(), dlg.getPasswordEdit());
                        dlg.setTitle(StringUtils.getString_s(R.string.sign_up_for, this._service.name()));
                        this._dialogPlatform.showDialog(dlg, null);
                    } else if ((this._features & 2) != 0) {
                        openLink(this._service.getRegisterUrl());
                    }
                } else if (widget == this._warningView) {
                    openLink(this._service.getInfoUrl());
                }
            }
        }

        private void openLink(String url) {
            try {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                this._context.startActivity(browserIntent);
            } catch (Exception e) {
                Log.e(SiteSelector.TAG, "", e);
            }
        }

        private void updateLayout() {
            if (this._userInfoLayout != null) {
                if (this._checkBox.isChecked()) {
                    this._userInfoLayout.setVisibility(0);
                } else {
                    this._userInfoLayout.setVisibility(8);
                }
            }
        }

        @Override // com.mxtech.widget.SiteRegisterDialog.Client
        public int createAccount(SiteRegisterDialog dialog, String emailAddress, String username, String password) throws InterruptedException {
            try {
                this._service.createAccount(emailAddress, username, password);
                return (this._features & 64) != 0 ? 1 : 0;
            } catch (SubtitleService.EmailAlreadyUsedException e) {
                return -2;
            } catch (SubtitleService.NetworkException e2) {
                return -11;
            } catch (SubtitleService.UsernameExistException e3) {
                return -1;
            } catch (SubtitleService.ServerException e4) {
                return -10;
            } catch (SubtitleService.SubtitleServiceException e5) {
                return -100;
            }
        }

        @Override // com.mxtech.widget.SiteRegisterDialog.Client
        public String getErrorMessage(SiteRegisterDialog dialog, int status) {
            int resId;
            switch (status) {
                case SiteRegisterDialog.STATUS_NETWORK_ERROR /* -11 */:
                    resId = R.string.error_network;
                    break;
                case SiteRegisterDialog.STATUS_SERVER_ERROR /* -10 */:
                    resId = R.string.error_server;
                    break;
                default:
                    resId = R.string.error_unknown;
                    break;
            }
            return App.context.getString(resId);
        }

        @Override // com.mxtech.widget.SiteRegisterDialog.Client
        public void onAccountCreated(SiteRegisterDialog dialog, String username, String password) {
            if (this._usernameInput != null) {
                this._usernameInput.setText(username);
            }
            if (this._passwordInput != null) {
                this._passwordInput.setText(password);
            }
        }
    }
}
