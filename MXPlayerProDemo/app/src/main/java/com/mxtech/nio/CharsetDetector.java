package com.mxtech.nio;

/* loaded from: classes2.dex */
public class CharsetDetector implements Decodable {
    public static int FLAG_DECODE = 1;
    public static int FLAG_IGNORE_MARKUP = 2;
    private long _nativeContext;

    private native void native_create();

    @Override // com.mxtech.nio.Decodable
    public native void close();

    public native boolean detect(byte[] bArr, int i) throws Exception;

    public native boolean detectFile(String str, int i) throws Exception;

    @Override // com.mxtech.nio.Decodable
    public native String makeString();

    @Override // com.mxtech.nio.Decodable
    public native void normalizeLineBreak();

    @Override // com.mxtech.nio.Decodable
    public native void trim();

    public CharsetDetector() {
        native_create();
    }

    public boolean detect(byte[] input) throws Exception {
        return detect(input, 0);
    }

    public boolean detectFile(String path) throws Exception {
        return detectFile(path, 0);
    }
}
