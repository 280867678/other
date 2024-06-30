package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.logcollector.Sender;
import com.mxtech.market.INavigator;
import com.mxtech.market.Market;
import com.mxtech.videoplayer.preference.Serializer;
import java.io.File;
import java.util.HashMap;

/* loaded from: classes.dex */
public final class ErrorReportSender implements DialogInterface.OnClickListener, Sender.Client {
    private static final int STEP_ASK_LOG_COLLECTOR = 2;
    private static final int STEP_ASK_SYSLOG = 1;
    private final Activity _activity;
    private final DialogRegistry _dialogRegistry;
    private CheckBox _includeAppSettings;
    private CheckBox _includeSysLog;
    private CheckBox _includeSysSettings;
    private CheckBox _saveToFile;
    private int _step;

    @SuppressLint({"InflateParams"})
    public ErrorReportSender(Activity activity) {
        this._activity = activity;
        this._dialogRegistry = DialogRegistry.registryOf(activity);
        AlertDialog dlg = new AlertDialog.Builder(activity).setTitle(R.string.error_report).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        ViewGroup content = (ViewGroup) dlg.getLayoutInflater().inflate(R.layout.ask_syslog, (ViewGroup) null);
        TextView comment = (TextView) content.findViewById(R.id.comment);
        this._includeSysLog = (CheckBox) content.findViewById(R.id.sys_log);
        this._includeSysSettings = (CheckBox) content.findViewById(R.id.sys_settings);
        this._includeAppSettings = (CheckBox) content.findViewById(R.id.app_settings);
        this._saveToFile = (CheckBox) content.findViewById(R.id.save_to_a_file);
        if (!L.canSendMultipleFiles(activity)) {
            this._saveToFile.setChecked(true);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("faq", "<a href='" + activity.getString(R.string.faq_url) + "'>" + activity.getString(R.string.faq) + "</a>");
        map.put("forum", "<a href='" + activity.getString(R.string.forum_url) + "'>" + activity.getString(R.string.forum) + "</a>");
        String html = StringUtils.format(activity.getString(R.string.ask_syslog_comment), map, false);
        comment.setText(Html.fromHtml(html), TextView.BufferType.SPANNABLE);
        comment.setMovementMethod(LinkMovementMethod.getInstance());
        dlg.setView(content);
        dlg.setCanceledOnTouchOutside(true);
        if (this._dialogRegistry != null) {
            dlg.setOnDismissListener(this._dialogRegistry);
            this._dialogRegistry.register(dlg);
        }
        dlg.show();
        this._step = 1;
    }

    private void openMarket() {
        INavigator market = Market.getNavigator(this._activity);
        Intent intent = new Intent("android.intent.action.VIEW");
        try {
            try {
                intent.setData(Uri.parse(market.productDetailUri(Sender.LOG_COLLECTOR_PACKAGE_NAME)));
                this._activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                try {
                    intent.setData(Uri.parse(StringUtils.getString_s(R.string.direct_download_url, Sender.LOG_COLLECTOR_PACKAGE_NAME, L.getSysArchId())));
                    this._activity.startActivity(intent);
                } catch (ActivityNotFoundException e2) {
                    DialogUtils.alertAndFinish(this._activity, StringUtils.getString_s(R.string.market_not_found, market.name(this._activity)));
                }
            }
        } catch (Exception e3) {
            Log.e(App.TAG, "", e3);
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (!this._activity.isFinishing()) {
            if (this._step == 1) {
                int items = 0;
                if (this._includeSysLog.isChecked()) {
                    items = 0 | 1;
                }
                if (this._includeSysSettings.isChecked()) {
                    items |= 2;
                }
                if (this._includeAppSettings.isChecked()) {
                    items |= 4;
                }
                new Sender(this._activity, this).sendLog(items, this._saveToFile.isChecked());
            } else if (this._step == 2 && which == -1) {
                openMarket();
            }
        }
    }

    @Override // com.mxtech.logcollector.Sender.Client
    public boolean exportSettingsTo(File file) {
        return Serializer.exportTo(file, 1);
    }

    @Override // com.mxtech.logcollector.Sender.Client
    public void onCannotRunLogCollector(int status) {
        if (!this._activity.isFinishing()) {
            if (status == -2) {
                AlertDialog.Builder b = new AlertDialog.Builder(this._activity);
                b.setTitle(R.string.error_report);
                b.setMessage(StringUtils.getString_s(R.string.ask_log_collector, this._activity.getString(R.string.logcollector_name)));
                b.setPositiveButton(17039379, this);
                b.setNegativeButton(17039369, this);
                AlertDialog dlg = b.create();
                dlg.setCanceledOnTouchOutside(true);
                if (this._dialogRegistry != null) {
                    dlg.setOnDismissListener(this._dialogRegistry);
                    this._dialogRegistry.register(dlg);
                }
                dlg.show();
                this._step = 2;
                return;
            }
            DialogUtils.alert(this._activity, (CharSequence) this._activity.getString(R.string.error_io_error));
        }
    }

    @Override // com.mxtech.logcollector.Sender.Client
    public String getEmailSubject() {
        try {
            PackageManager packageManager = this._activity.getPackageManager();
            PackageInfo pkg = packageManager.getPackageInfo(this._activity.getPackageName(), 0);
            String label = this._activity.getString(pkg.applicationInfo.labelRes);
            return "[ERROR] " + label + " " + pkg.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(App.TAG, "", e);
            return "[ERROR]";
        }
    }

    @Override // com.mxtech.logcollector.Sender.Client
    public String getEmailRecipient() {
        return this._activity.getString(R.string.bug_report_receptionist);
    }
}
