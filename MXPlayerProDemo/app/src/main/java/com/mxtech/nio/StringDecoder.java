package com.mxtech.nio;

/* loaded from: classes2.dex */
public class StringDecoder implements Decodable {
    private long _nativeContext;

    private native void native_create(String str) throws Exception;

    @Override // com.mxtech.nio.Decodable
    public native void close();

    public native boolean decode(byte[] bArr) throws Exception;

    public native boolean decodeFile(String str) throws Exception;

    @Override // com.mxtech.nio.Decodable
    public native String makeString();

    @Override // com.mxtech.nio.Decodable
    public native void normalizeLineBreak();

    @Override // com.mxtech.nio.Decodable
    public native void trim();

    public StringDecoder() throws Exception {
        native_create(null);
    }

    public StringDecoder(String charset) throws Exception {
        native_create(charset);
    }
}
