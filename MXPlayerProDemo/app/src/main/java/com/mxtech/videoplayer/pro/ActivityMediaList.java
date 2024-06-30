package com.mxtech.videoplayer.pro;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.mxtech.StringUtils;
import com.mxtech.market.AmazonAppstoreNavigator;
import com.mxtech.videoplayer.ActivityMessenger;
import com.mxtech.videoplayer.L;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ActivityMediaList extends com.mxtech.videoplayer.ActivityMediaList {
    private Snackbar _getAccountsPermissionSnackBar;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityMediaList, com.mxtech.videoplayer.ActivityList, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        if (L.authorizer != null && !"android.intent.action.SEARCH".equals(getIntent().getAction())) {
            PackageManager pm = getPackageManager();
            String installerName = pm.getInstallerPackageName(getPackageName());
            if (installerName != null && installerName.startsWith(AmazonAppstoreNavigator.URL_PREFIX) && !getString(R.string.target_market).equals(AmazonAppstoreNavigator.URL_PREFIX)) {
                Map<String, String> map = new HashMap<>();
                map.put("store_name", getString(R.string.amazon_appstore));
                map.put("store_company", getString(R.string.amazon));
                map.put("app_name", getString(getApplicationInfo().labelRes));
                ActivityMessenger.showMessage(this, StringUtils.format(getString(R.string.scam_notice_pro), map, "{", "}", false), getString(R.string.scam_alert_pro));
                finish();
            }
        }
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase
    public boolean isSnackbarVisible() {
        return this._getAccountsPermissionSnackBar != null || super.isSnackbarVisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityVPBase
    @SuppressLint({"NewApi"})
    public void onShowSnackbar(View topLayout) {
        if (this._getAccountsPermissionSnackBar == null) {
            if (((App) App.context).needRequestGetAccountsPermission()) {
                this._getAccountsPermissionSnackBar = Snackbar.make(topLayout, (int) R.string.rational_get_accounts, -2).setAction(17039370, new View.OnClickListener() { // from class: com.mxtech.videoplayer.pro.ActivityMediaList.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        ActivityMediaList.this.requestPermissions(new String[]{"android.permission.GET_ACCOUNTS"}, 2);
                    }
                }).setCallback(new Snackbar.Callback() { // from class: com.mxtech.videoplayer.pro.ActivityMediaList.1
                    @Override // android.support.design.widget.Snackbar.Callback
                    public void onDismissed(Snackbar snackbar, int event) {
                        ActivityMediaList.this._getAccountsPermissionSnackBar = null;
                    }
                });
                this._getAccountsPermissionSnackBar.show();
                return;
            }
            super.onShowSnackbar(topLayout);
        }
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase, android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                Licensor.check(this);
                return;
            } else if (!isFinishing()) {
                showSnackbar();
                return;
            } else {
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
