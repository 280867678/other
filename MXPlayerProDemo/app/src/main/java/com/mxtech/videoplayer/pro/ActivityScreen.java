package com.mxtech.videoplayer.pro;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/* loaded from: classes.dex */
public class ActivityScreen extends com.mxtech.videoplayer.ActivityScreen {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityVPBase
    @SuppressLint({"NewApi"})
    public void onShowSnackbar(View topLayout) {
        if (((App) App.context).needRequestGetAccountsPermission()) {
            if (!isFinishing()) {
                showDialog((ActivityScreen) new AlertDialog.Builder(this).setMessage(R.string.rational_get_accounts).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create(), new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.pro.ActivityScreen.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialog) {
                        ActivityScreen.this.finish();
                    }
                });
                return;
            }
            return;
        }
        super.onShowSnackbar(topLayout);
    }
}
