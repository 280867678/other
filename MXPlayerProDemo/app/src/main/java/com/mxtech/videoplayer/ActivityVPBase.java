package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mxtech.app.MXAppCompatActivity;
import com.mxtech.app.ToolbarAppCompatActivity;
import com.mxtech.io.DocumentTreeRegistry;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.Notice;
import java.io.File;

/* loaded from: classes.dex */
public class ActivityVPBase extends ToolbarAppCompatActivity {
    protected static final int REQUEST_EXTERNAL_STORAGE_ACCESS = 1;
    protected static final int REQUEST_OPEN_DOCUMENT_TREE = 99;
    private int _lastFileOperationNumFailed;
    private int _lastFileOperationNumTotal;
    private FileOperationSink _lastFileOperationSink;
    private Snackbar _snackBar;

    /* loaded from: classes.dex */
    public interface FileOperationSink {
        void onFileOperationFailed(int i, int i2);

        void onFileOperationRetry();
    }

    public final void showNoticeDialog() {
        Notice.NoticeDialog dlg = Notice.createDialog(this, R.raw.notice);
        if (dlg != null) {
            dlg.setButton(-1, getString(17039370), (DialogInterface.OnClickListener) null);
            showDialog((ActivityVPBase) dlg, new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.ActivityVPBase.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialog) {
                    ActivityVPBase.this.dialogRegistry.onDismiss(dialog);
                    ActivityVPBase.this.onNoticeDismissed(ActivityVPBase.this.started);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNoticeDismissed(boolean byUser) {
    }

    @SuppressLint({"NewApi", "InflateParams"})
    public void handleFileWritingFailure(@Nullable File file, final int numTotal, final int numFailed, final FileOperationSink sink) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (!isFinishing()) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ViewGroup content = (ViewGroup) getLayoutInflater().inflate(R.layout.file_write_failure, (ViewGroup) null);
                TextView text = (TextView) content.findViewById(R.id.text);
                TextView comment = (TextView) content.findViewById(R.id.comment);
                text.setText(R.string.saf_request_permission);
                comment.setText(R.string.saf_reason);
                ab.setView(content);
                ab.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.ActivityVPBase.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                            intent.setFlags(67);
                            ActivityVPBase.this.startActivityForResult(intent, 99);
                            ActivityVPBase.this._lastFileOperationSink = sink;
                            ActivityVPBase.this._lastFileOperationNumTotal = numTotal;
                            ActivityVPBase.this._lastFileOperationNumFailed = numFailed;
                        } catch (Exception e) {
                            Log.e(MXAppCompatActivity.TAG, "", e);
                        }
                    }
                });
                ab.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                showDialog((ActivityVPBase) ab.create());
                return;
            }
            return;
        }
        sink.onFileOperationFailed(numTotal, numFailed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (this._lastFileOperationSink != null) {
                FileOperationSink sink = this._lastFileOperationSink;
                this._lastFileOperationSink = null;
                if (resultCode == -1) {
                    Log.i(MXAppCompatActivity.TAG, "ACTION_OPEN_DOCUMENT_TREE returned " + data);
                    Uri treeUri = data.getData();
                    if (treeUri != null) {
                        DocumentTreeRegistry.register(treeUri);
                        sink.onFileOperationRetry();
                        return;
                    }
                }
                sink.onFileOperationFailed(this._lastFileOperationNumTotal, this._lastFileOperationNumFailed);
                return;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public final void defaultFileOperationRetry() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        showSnackbar();
    }

    public void showSnackbar() {
        View parent = getSnackbarParent();
        if (parent != null) {
            onShowSnackbar(parent);
        }
    }

    public View getSnackbarParent() {
        return null;
    }

    public boolean isSnackbarVisible() {
        return this._snackBar != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onShowSnackbar(View topLayout) {
        if (this._snackBar == null) {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
                if (shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE") || App.prefs.getBoolean(Key.EVER_REQUESTED_STORAGE_WRITE_PERMISSION, false)) {
                    this._snackBar = Snackbar.make(topLayout, R.string.rational_external_storage_access, -2).setAction(17039370, new View.OnClickListener() { // from class: com.mxtech.videoplayer.ActivityVPBase.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            ActivityVPBase.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                        }
                    }).setCallback(new Snackbar.Callback() { // from class: com.mxtech.videoplayer.ActivityVPBase.3
                        @Override // android.support.design.widget.Snackbar.Callback
                        public void onShown(Snackbar snackbar) {
                            SharedPreferences.Editor editor = App.prefs.edit();
                            editor.putBoolean(Key.EVER_REQUESTED_STORAGE_WRITE_PERMISSION, true);
                            editor.apply();
                        }

                        @Override // android.support.design.widget.Snackbar.Callback
                        public void onDismissed(Snackbar snackbar, int event) {
                            ActivityVPBase.this._snackBar = null;
                        }
                    });
                    this._snackBar.show();
                    return;
                }
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
            if (L.simpleSnackbarMessages.size() > 0) {
                final String message = L.simpleSnackbarMessages.get(0);
                this._snackBar = Snackbar.make(topLayout, message, -2).setAction(17039370, new View.OnClickListener() { // from class: com.mxtech.videoplayer.ActivityVPBase.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        L.simpleSnackbarMessages.remove(message);
                    }
                }).setCallback(new Snackbar.Callback() { // from class: com.mxtech.videoplayer.ActivityVPBase.5
                    @Override // android.support.design.widget.Snackbar.Callback
                    public void onDismissed(Snackbar snackbar, int event) {
                        ActivityVPBase.this._snackBar = null;
                        if (!ActivityVPBase.this.isFinishing()) {
                            ActivityVPBase.this.showSnackbar();
                        }
                    }
                });
                this._snackBar.show();
            }
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                P.onAcquiredStorageWritePermission();
                L.observer.resetAsync();
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
