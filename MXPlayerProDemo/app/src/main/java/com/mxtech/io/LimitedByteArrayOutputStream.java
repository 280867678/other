package com.mxtech.io;

import com.flurry.android.Constants;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;

/* loaded from: classes.dex */
public class LimitedByteArrayOutputStream extends OutputStream {
    protected byte[] buf;
    protected int count;
    private final int limit;

    public LimitedByteArrayOutputStream(int limit) {
        this.buf = new byte[32];
        this.limit = limit;
    }

    public LimitedByteArrayOutputStream(int limit, int size) {
        if (size >= 0) {
            this.buf = new byte[size];
            this.limit = limit;
            return;
        }
        throw new IllegalArgumentException("size < 0");
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }

    private void expand(int i) {
        if (this.count + i > this.buf.length) {
            if (this.count + i > this.limit) {
                throw new BufferOverflowException();
            }
            byte[] newbuf = new byte[(this.count + i) * 2];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }

    public synchronized void reset() {
        this.count = 0;
    }

    public int size() {
        return this.count;
    }

    public synchronized byte[] toByteArray() {
        byte[] newArray;
        newArray = new byte[this.count];
        System.arraycopy(this.buf, 0, newArray, 0, this.count);
        return newArray;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    @Deprecated
    public String toString(int hibyte) {
        char[] newBuf = new char[size()];
        for (int i = 0; i < newBuf.length; i++) {
            newBuf[i] = (char) (((hibyte & 255) << 8) | (this.buf[i] & Constants.UNKNOWN));
        }
        return new String(newBuf);
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.buf, 0, this.count, charsetName);
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] buffer, int offset, int len) {
        if (len != 0) {
            expand(len);
            System.arraycopy(buffer, offset, this.buf, this.count, len);
            this.count += len;
        }
    }

    @Override // java.io.OutputStream
    public synchronized void write(int oneByte) {
        if (this.count == this.buf.length) {
            expand(1);
        }
        byte[] bArr = this.buf;
        int i = this.count;
        this.count = i + 1;
        bArr[i] = (byte) oneByte;
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }
}
