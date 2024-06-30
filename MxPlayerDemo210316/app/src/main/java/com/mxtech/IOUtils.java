package com.mxtech;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes2.dex */
public final class IOUtils {
    public static byte[] readStream(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        try {
            transferStream(stream, out);
            return out.toByteArray();
        } finally {
            out.close();
        }
    }

    public static void readFully(InputStream stream, byte[] buffer) throws EOFException, IOException {
        readFully(stream, buffer, 0, buffer.length);
    }

    public static void readFully(InputStream stream, byte[] buffer, int offset, int count) throws EOFException, IOException {
        while (count > 0) {
            int read = stream.read(buffer, offset, count);
            if (read < 0) {
                throw new EOFException();
            }
            offset += read;
            count -= read;
        }
    }

    public static int readToEnd(InputStream stream, byte[] buffer) throws IOException {
        return readToEnd(stream, buffer, 0, buffer.length);
    }

    public static int readToEnd(InputStream stream, byte[] buffer, int offset, int count) throws IOException {
        int total = 0;
        while (count > 0) {
            int read = stream.read(buffer, offset, count);
            if (read < 0) {
                break;
            }
            offset += read;
            count -= read;
            total += read;
        }
        return total;
    }

    public static void transferStream(InputStream input, OutputStream output) throws IOException {
        transferStream(input, output, new byte[8192]);
    }

    public static void transferStream(InputStream input, OutputStream output, byte[] buf) throws IOException {
        while (true) {
            int read = input.read(buf);
            if (read >= 0) {
                output.write(buf, 0, read);
            } else {
                return;
            }
        }
    }

    public static void transferStream(InputStream input, OutputStream output, int readLimit) throws IOException {
        transferStream(input, output, new byte[8192], readLimit);
    }

    public static void transferStream(InputStream input, OutputStream output, byte[] buf, int readLimit) throws IOException {
        int total = 0;
        while (true) {
            int read = input.read(buf);
            if (read >= 0) {
                total += read;
                if (total > readLimit) {
                    throw new IOException("Input size (" + total + ") exceed maximum size (" + readLimit + ").");
                }
                output.write(buf, 0, read);
            } else {
                return;
            }
        }
    }
}
