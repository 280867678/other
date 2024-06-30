package com.mxtech.crypto;

import com.mxtech.app.AppUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* loaded from: classes2.dex */
public class CipherOutputStream extends OutputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private long _cipher;
    private byte[] _cipherText;
    private final OutputStream _s;

    public CipherOutputStream(OutputStream os, int keyIndex, long salt) {
        this(os, keyIndex, salt, new byte[8192]);
    }

    public CipherOutputStream(OutputStream os, int keyIndex, byte[] salt, int bufferSize) {
        this(os, keyIndex, ByteBuffer.wrap(salt).getLong(), new byte[bufferSize]);
    }

    public CipherOutputStream(OutputStream os, int keyIndex, byte[] salt, int saltOffset, int saltSize, int bufferSize) {
        this(os, keyIndex, ByteBuffer.wrap(salt, saltOffset, saltSize).getLong(), new byte[bufferSize]);
    }

    public CipherOutputStream(OutputStream os, int keyIndex, long salt, int bufferSize) {
        this(os, keyIndex, salt, new byte[bufferSize]);
    }

    public CipherOutputStream(OutputStream os, int keyIndex, long salt, byte[] iobuffer) {
        this._s = os;
        this._cipher = AppUtils.ss_ctor(keyIndex, salt);
        this._cipherText = iobuffer;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        AppUtils.ss_dtor(this._cipher);
        this._cipher = 0L;
        this._s.close();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this._s.flush();
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        while (count > 0) {
            int bytes = Math.min(count, this._cipherText.length);
            AppUtils.ss_e(this._cipher, buffer, offset, bytes, this._cipherText);
            this._s.write(this._cipherText, 0, bytes);
            count -= bytes;
            offset += bytes;
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        this._s.write(AppUtils.ss_e(this._cipher, oneByte));
    }
}
