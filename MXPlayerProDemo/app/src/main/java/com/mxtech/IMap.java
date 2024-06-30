package com.mxtech;

import java.io.IOException;

/* loaded from: classes2.dex */
public interface IMap {
    boolean contains(int i);

    int[] getAllKeys();

    boolean getBoolean(int i, boolean z);

    int getInt(int i, int i2);

    int[] getIntArray(int i, int[] iArr);

    long getLong(int i, long j);

    String getString(int i, String str);

    void putBoolean(int i, boolean z) throws IOException;

    void putInt(int i, int i2) throws IOException;

    void putIntArray(int i, int[] iArr) throws IOException;

    void putLong(int i, long j) throws IOException;

    void putString(int i, String str) throws IOException;

    boolean remove(int i) throws IOException;
}
