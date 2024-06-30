package com.mxtech.text;

public final class NativeString {
    public static final int kNormalize = 1;
    private final long _nativeContext;

    private static native void nativeClassInit();

    private native void native_release();

    public native String get(long j);

    public native String get(long j, int i);

    public native String getNormalized(long j);

    public native int length();

    public native boolean startsWith(String str);

    public native boolean startsWithIgnoreCase(String str);

    static {
        nativeClassInit();
    }

    public static void ensureClassInit() {
    }

    private NativeString(long nativeContext) {
        this._nativeContext = nativeContext;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        native_release();
    }
}
