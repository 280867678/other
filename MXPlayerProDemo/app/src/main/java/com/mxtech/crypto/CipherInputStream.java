package com.mxtech.crypto;

import com.mxtech.app.AppUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: classes2.dex */
public class CipherInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private long _cipher;
    private byte[] _cipherText;
    private final InputStream _s;
    private byte[] _skipBuffer;

    public CipherInputStream(InputStream is, int keyIndex, long salt) {
        this(is, keyIndex, salt, new byte[8192]);
    }

    public CipherInputStream(InputStream is, int keyIndex, byte[] salt, int bufferSize) {
        this(is, keyIndex, ByteBuffer.wrap(salt).getLong(), new byte[bufferSize]);
    }

    public CipherInputStream(InputStream is, int keyIndex, byte[] salt, int saltOffset, int saltSize, int bufferSize) {
        this(is, keyIndex, ByteBuffer.wrap(salt, saltOffset, saltSize).getLong(), new byte[bufferSize]);
    }

    public CipherInputStream(InputStream is, int keyIndex, long salt, int bufferSize) {
        this(is, keyIndex, salt, new byte[bufferSize]);
    }

    public CipherInputStream(InputStream is, int keyIndex, long salt, byte[] iobuffer) {
        this._s = is;
        this._cipher = AppUtils.ss_ctor(keyIndex, salt);
        this._cipherText = iobuffer;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        AppUtils.ss_dtor(this._cipher);
        this._cipher = 0L;
        this._s.close();
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this._s.available();
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int offset, int count) throws IOException {
        int total = 0;
        while (count > 0) {
            int read = this._s.read(this._cipherText, 0, Math.min(count, this._cipherText.length));
            if (read < 0) {
                if (total == 0) {
                    return -1;
                }
                return total;
            }
            AppUtils.ss_d(this._cipher, this._cipherText, read, buffer, offset);
            count -= read;
            total += read;
            offset += read;
        }
        return total;
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int encrypted = this._s.read();
        return encrypted < 0 ? encrypted : AppUtils.ss_d(this._cipher, encrypted);
    }

    @Override // java.io.InputStream
    public long skip(long bytes) throws IOException {
        int read;
        long total = 0;
        if (this._skipBuffer == null) {
            this._skipBuffer = new byte[this._cipherText.length];
        }
        while (bytes > 0 && (read = this._s.read(this._cipherText, 0, (int) Math.min(bytes, this._cipherText.length))) >= 0) {
            AppUtils.ss_d(this._cipher, this._cipherText, read, this._skipBuffer, 0);
            bytes -= read;
            total += read;
        }
        return total;
    }
}
