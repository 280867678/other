package com.mxtech.nio;

/* loaded from: classes2.dex */
public interface Decodable {
    public static final String TAG = "MX.nio";

    void close();

    String makeString();

    void normalizeLineBreak();

    void trim();
}
