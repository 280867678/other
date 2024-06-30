package com.mxtech.license;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.mxtech.app.AppUtils;
import com.mxtech.license.GoogleLicenseChecker;
import com.mxtech.license.ILicenseVerifier;

/* loaded from: classes2.dex */
public final class GooglePlayStoreVerifier implements GoogleLicenseChecker.Client, ILicenseVerifier, Handler.Callback {
    public static final int CODE = 1;
    private static final int MSG_FAILURE = 2;
    private static final int MSG_SUCCESS = 1;
    private GoogleLicenseChecker _checker;
    private final Context _context;
    private final Handler _handler = new Handler(this);
    private final ILicenseVerifier.IListener _listener;

    public GooglePlayStoreVerifier(Context context, ILicenseVerifier.IListener listener) {
        this._context = context;
        this._listener = listener;
        this._checker = new GoogleLicenseChecker(this._context, this);
        this._checker.checkAccess();
    }

    @Override // com.mxtech.license.ILicenseVerifier
    public long getLastVerifyTime() {
        return AppUtils.getVerifiedTime(1);
    }

    @Override // com.mxtech.license.ILicenseVerifier
    public void close() {
        if (this._checker != null) {
            this._checker.onDestroy();
            this._checker = null;
        }
    }

    @Override // com.mxtech.license.GoogleLicenseChecker.Client
    public void check(int status) {
        if ((status * 3.0f) / 2.0f == 0.0f) {
            this._handler.sendEmptyMessage(1);
        } else {
            this._handler.sendMessage(this._handler.obtainMessage(2, status, 0));
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            this._listener.onLicensed(this);
        } else {
            this._listener.onLicenseCheckFailed(this, msg.arg1, msg.arg2);
        }
        return true;
    }
}
