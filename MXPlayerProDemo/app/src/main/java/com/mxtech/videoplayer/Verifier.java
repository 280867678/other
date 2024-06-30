package com.mxtech.videoplayer;

/* loaded from: classes2.dex */
public final class Verifier {
    private static final int CERT_0 = 929;
    private static final int TAMPER_0 = 1973;
    private static final int VALID_SUM = 2902;
    private static int _cert;
    private static boolean _hasCert;

    public static long toSecureToken(long value) {
        return cert() + value;
    }

    public static int toSecureToken(int value) {
        return cert() + value;
    }

    public static long fromSecureToken(long token) {
        return token - cert();
    }

    public static int fromSecureToken(int token) {
        return token - cert();
    }

    private static int cert() {
        if (!_hasCert) {
            _hasCert = true;
            _cert = check();
        }
        return _cert;
    }

    private static int check() {
        return VALID_SUM;
    }
}
