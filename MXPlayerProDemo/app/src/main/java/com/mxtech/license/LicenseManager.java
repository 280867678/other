package com.mxtech.license;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.mxtech.DateTimeUtils;
import com.mxtech.DeviceUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.license.ILicenseVerifier;

/* loaded from: classes.dex */
public final class LicenseManager {
    public static final long DAY = 86400000;
    public static final long HOUR = 3600000;
    static final String META_TARGET = "target";
    static final String META_USER = "user";
    public static final long MINUTE = 60000;
    private static final String PERMISSION_CHECK_LICENSE = "com.android.vending.CHECK_LICENSE";
    static final String PROXY = "com.mxtech.lproxy";
    public static final long SECOND = 1000;
    private static Checker _checker;
    private static long _proxyApproveTime;
    private static final long HIGH_TOLERANCE = DateTimeUtils.WEEK;
    private static final long VALIDITY_PERIOD = 86400000;
    private static final long PROXY_VALIDITY_PERIOD = 1;

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean findLicenseProxy(Context context) {
        return true;
    }

    private static boolean shouldCheckLicense(Context context, long verifyTime) {
        long now = System.currentTimeMillis();
        if (now < VALIDITY_PERIOD + verifyTime) {
            return false;
        }
        return now < HIGH_TOLERANCE + verifyTime ? DeviceUtils.isWifiActive(context) : now >= _proxyApproveTime + PROXY_VALIDITY_PERIOD;
    }

    /* loaded from: classes2.dex */
    private static class Checker implements ILicenseVerifier.IListener, Handler.Callback, ILicenseVerifier {
        private static final int MSG_FAILURE = 0;
        private final Context _context;
        private Handler _handler;
        private final ILicenseVerifier.IListener _listener;

        Checker(Context context, ILicenseVerifier.IListener listener) {
            this._context = context;
            this._listener = listener;
            Checker unused = LicenseManager._checker = this;
        }

        void postFailure() {
            if (this._handler == null) {
                this._handler = new Handler(this);
            }
            this._handler.sendMessage(this._handler.obtainMessage(0, -100, 0));
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                Checker unused = LicenseManager._checker = null;
                this._listener.onLicenseCheckFailed(this, msg.arg1, msg.arg2);
                return true;
            }
            return false;
        }

        @Override // com.mxtech.license.ILicenseVerifier.IListener
        public void onLicensed(ILicenseVerifier verifier) {
            Checker unused = LicenseManager._checker = null;
            this._listener.onLicensed(verifier);
        }

        @Override // com.mxtech.license.ILicenseVerifier.IListener
        public void onLicenseCheckFailed(ILicenseVerifier verifier, int reason, int extra) {
            if (reason != -100 && verifier.getLastVerifyTime() > 0) {
                Checker unused = LicenseManager._checker = null;
                this._listener.onLicensed(verifier);
            } else if (!(verifier instanceof GooglePlayStoreVerifier) || !AppUtils.checkPermission(this._context, "android.permission.GET_ACCOUNTS")) {
                if (!LicenseManager.findLicenseProxy(this._context) || !AppUtils.applyLicenseProxy()) {
                    Checker unused2 = LicenseManager._checker = null;
                    this._listener.onLicenseCheckFailed(verifier, reason, extra);
                    return;
                }
                long unused3 = LicenseManager._proxyApproveTime = System.currentTimeMillis();
                Checker unused4 = LicenseManager._checker = null;
                this._listener.onLicensed(verifier);
            } else {
                verifier.close();
                new MXVerifier(this._context, this);
            }
        }

        @Override // com.mxtech.license.ILicenseVerifier
        public long getLastVerifyTime() {
            return 0L;
        }

        @Override // com.mxtech.license.ILicenseVerifier
        public void close() {
        }
    }

    public static void checkLicense(Context context, ILicenseVerifier.IListener listener) {
        long verifyTime = 0;
        boolean permCheckLicense = AppUtils.checkPermission(context, PERMISSION_CHECK_LICENSE);
        boolean permGetAccounts = AppUtils.checkPermission(context, "android.permission.GET_ACCOUNTS");
        if (permCheckLicense) {
            verifyTime = AppUtils.getVerifiedTime(1);
        }
        if (permGetAccounts) {
            verifyTime = Math.max(verifyTime, AppUtils.getVerifiedTime(2));
        }
        if (shouldCheckLicense(context, verifyTime)) {
            Checker checker = new Checker(context, listener);
            if (permCheckLicense) {
                new GooglePlayStoreVerifier(context, checker);
            } else if (permGetAccounts) {
                new MXVerifier(context, checker);
            } else {
                checker.postFailure();
            }
        }
    }

    public static boolean isLicenseVerified(Context context) {
        if (!AppUtils.checkPermission(context, PERMISSION_CHECK_LICENSE) || AppUtils.getVerifiedTime(1) <= 0) {
            return AppUtils.checkPermission(context, "android.permission.GET_ACCOUNTS") && AppUtils.getVerifiedTime(2) > 0;
        }
        return true;
    }

    public static boolean isCheckingLicense() {
        return _checker != null;
    }
}
