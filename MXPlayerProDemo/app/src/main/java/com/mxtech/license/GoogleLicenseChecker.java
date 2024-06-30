package com.mxtech.license;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import com.mxtech.NumericUtils;
import com.mxtech.app.AppUtils;

/* loaded from: classes2.dex */
public final class GoogleLicenseChecker extends Binder implements ServiceConnection, IInterface, Handler.Callback {
    private static final String LISTENER_DESCRIPTOR = "com.android.vending.licensing.ILicenseResultListener";
    private static final int MSG_RESPONSE_RECEIVED = 2;
    private static final int MSG_TIMEOUT = 1;
    private static final String SERVICE_DESCRIPTOR = "com.android.vending.licensing.ILicensingService";
    private static final int TIMEOUT_MS = 30000;
    static final int TRANSACTION_checkLicense = 1;
    static final int TRANSACTION_verifyLicense = 1;
    private long _nonce;
    private final Client mClient;
    private final Context mContext;
    private final Handler mHandler = new Handler(this);
    private IBinder mRemote;

    /* loaded from: classes2.dex */
    public interface Client {
        void check(int i);
    }

    public GoogleLicenseChecker(Context context, Client client) {
        attachInterface(this, LISTENER_DESCRIPTOR);
        this.mContext = context;
        this.mClient = client;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == 1598968902) {
            reply.writeString(LISTENER_DESCRIPTOR);
            return true;
        } else if ((code * 3) - 1 == 2) {
            data.enforceInterface(LISTENER_DESCRIPTOR);
            int status = GoogleUtils.getLicenseStatus(code, data, this._nonce);
            AppUtils.updateGoogleLicenseStatus(status);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, status, 0));
            return true;
        } else {
            return super.onTransact(code, data, reply, flags);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x001d, code lost:
        if (r3.mContext.bindService(r0, r3, 1) == false) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void checkAccess() {
        if (this.mRemote != null) {
            runChecks();
        } else {
            try {
                Intent intent = new Intent(SERVICE_DESCRIPTOR);
                intent.setPackage("com.android.vending");
            } catch (SecurityException e) {
            }
        }
        this.mClient.check(-1);
    }

    private void runChecks() {
        try {
            this.mHandler.sendEmptyMessageDelayed(1, 30000L);
            Parcel data = Parcel.obtain();
            this._nonce = NumericUtils.ThreadLocalRandom.get().nextLong();
            data.writeInterfaceToken(SERVICE_DESCRIPTOR);
            data.writeLong(this._nonce);
            data.writeString(this.mContext.getPackageName());
            data.writeStrongBinder(this);
            this.mRemote.transact(1, data, null, 1);
            data.recycle();
        } catch (RemoteException e) {
            this.mClient.check(-1);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            this.mClient.check(-1);
            cleanupService();
            return true;
        } else if (msg.what == 2) {
            this.mHandler.removeMessages(1);
            this.mClient.check(msg.arg1);
            cleanupService();
            return true;
        } else {
            return false;
        }
    }

    @Override // android.content.ServiceConnection
    public synchronized void onServiceConnected(ComponentName name, IBinder service) {
        this.mRemote = service;
        runChecks();
    }

    @Override // android.content.ServiceConnection
    public synchronized void onServiceDisconnected(ComponentName name) {
        this.mRemote = null;
    }

    private void cleanupService() {
        if (this.mRemote != null) {
            try {
                this.mContext.unbindService(this);
            } catch (IllegalArgumentException e) {
            }
            this.mRemote = null;
        }
    }

    public synchronized void onDestroy() {
        cleanupService();
    }
}
