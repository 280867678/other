package com.mxtech.os;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.DialogPlatform;

/* loaded from: classes2.dex */
public abstract class AsyncTaskInteractive<Params, Progress, Result> extends AsyncTask2<Params, Progress, Result> {
    private final DialogPlatform _dialogPlatform;
    @Nullable
    private CharSequence _message;
    @Nullable
    private AppCompatProgressDialog _progressDialog;

    public AsyncTaskInteractive(DialogPlatform dialogPlatform) {
        this._dialogPlatform = dialogPlatform;
    }

    public AsyncTaskInteractive(DialogPlatform dialogPlatform, int progressMessageResId) {
        this._dialogPlatform = dialogPlatform;
        setProgressMessage(progressMessageResId);
    }

    public AsyncTaskInteractive(DialogPlatform dialogPlatform, CharSequence progressMessage) {
        this._dialogPlatform = dialogPlatform;
        setProgressMessage(progressMessage);
    }

    public void setProgressMessage(int resId) {
        this._message = this._dialogPlatform.getContext().getString(resId);
    }

    public void setProgressMessage(CharSequence message) {
        this._message = message;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPreExecute() {
        if (this._message != null) {
            Context context = this._dialogPlatform.getContext();
            this._progressDialog = new AppCompatProgressDialog(context);
            this._progressDialog.setProgressStyle(0);
            this._progressDialog.setMessage(this._message);
            this._dialogPlatform.showDialog(this._progressDialog, new DialogInterface.OnDismissListener() { // from class: com.mxtech.os.AsyncTaskInteractive.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialog) {
                    AsyncTaskInteractive.this._dialogPlatform.getDialogRegistry().onDismiss(dialog);
                    AsyncTaskInteractive.this.cancel(true);
                    AsyncTaskInteractive.this._progressDialog = null;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Result result) {
        if (this._progressDialog != null) {
            this._progressDialog.dismiss();
            this._progressDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onCancelled() {
        if (this._progressDialog != null) {
            this._progressDialog.dismiss();
            this._progressDialog = null;
        }
    }
}
