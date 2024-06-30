package com.mxtech.license;

/* loaded from: classes2.dex */
public interface ILicenseVerifier {
    public static final int E_APPLICATION = -101;
    public static final int E_NETWORK = -102;
    public static final int E_UNAUTHORIZED = -100;
    public static final int E_UNKNOWN = -1;

    /* loaded from: classes.dex */
    public interface IListener {
        void onLicenseCheckFailed(ILicenseVerifier iLicenseVerifier, int i, int i2);

        void onLicensed(ILicenseVerifier iLicenseVerifier);
    }

    void close();

    long getLastVerifyTime();
}
