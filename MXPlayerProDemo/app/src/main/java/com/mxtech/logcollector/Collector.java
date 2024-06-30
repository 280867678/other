package com.mxtech.logcollector;

import android.os.SystemClock;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/* loaded from: classes2.dex */
public class Collector {
    public static final int ERROR_CANNOT_RUN_LOGCAT = -2;
    public static final int ERROR_IO_ERROR = -1;
    public static final int SUCCESS = 0;
    static final String TAG = "MX.LogCollector";

    public static int collect(String outPath, boolean compress, String format, String fullCmdLine) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("logcat");
        cmdLine.add("-d");
        cmdLine.add("-v");
        cmdLine.add(format);
        if (fullCmdLine != null) {
            cmdLine.addAll(Arrays.asList(fullCmdLine.split(" ")));
        }
        try {
            long begin = SystemClock.uptimeMillis();
            OutputStream out = new FileOutputStream(outPath);
            if (compress) {
                out = new GZIPOutputStream(new BufferedOutputStream(out));
            }
            try {
                Process proc = Runtime.getRuntime().exec((String[]) cmdLine.toArray(new String[cmdLine.size()]));
                InputStream in = proc.getInputStream();
                int totalRead = 0;
                byte[] buf = new byte[4096];
                while (true) {
                    int read = in.read(buf);
                    if (read < 0) {
                        break;
                    }
                    totalRead += read;
                    out.write(buf, 0, read);
                }
                if (totalRead == 0) {
                    in.close();
                    out.close();
                    return -2;
                }
                in.close();
                out.close();
                Log.v(TAG, "Log collected and zipped into '" + outPath + "' (" + (SystemClock.uptimeMillis() - begin) + "ms)");
                return 0;
            } catch (IOException e) {
                Log.e(TAG, "", e);
                out.close();
                return -2;
            }
        } catch (IOException e2) {
            Log.e(TAG, "", e2);
            return -1;
        }
    }
}
