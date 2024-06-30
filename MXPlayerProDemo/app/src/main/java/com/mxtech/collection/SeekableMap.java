package com.mxtech.collection;

/* loaded from: classes2.dex */
public class SeekableMap {
    private long _nativeContext;

    private native void native_create(int i, int i2);

    private native void native_destroy();

    public native int begin();

    public native int end();

    public native Object get(int i);

    public native boolean isEmpty();

    public native int next();

    public native int previous();

    public native Object put(int i, Object obj);

    public native boolean seek(int i);

    public native int size();

    public SeekableMap(int minKey, int maxKey) {
        native_create(minKey, maxKey);
    }

    protected void finalize() throws Throwable {
        native_destroy();
        super.finalize();
    }
}
