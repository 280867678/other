package com.mxtech.collection;

/* loaded from: classes2.dex */
public final class SeekableRangeMap<T> {
    private long _nativeContext;

    private native void native_create(int i, int i2);

    private native void native_destroy();

    public native int begin();

    public native int end();

    public native Object get(int i);

    public native boolean isEmpty();

    public native int next();

    public native int previous();

    public native void putRange(int i, int i2, T t);

    public native boolean seek(int i);

    public SeekableRangeMap(int minKey, int maxKey) {
        native_create(minKey, maxKey);
    }

    private SeekableRangeMap(long nativeContext) {
        this._nativeContext = nativeContext;
    }

    protected void finalize() throws Throwable {
        native_destroy();
        super.finalize();
    }
}
