package com.mxtech.os;

import android.content.Context;
import android.util.Log;
import com.mxtech.IOUtils;
import com.mxtech.StringLineReader;
import com.mxtech.app.MXApplication;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class SysInfo {
    private static long _frequency = -1;

    public static final Map<String, String> readAll() throws IOException {
        FileInputStream cpuinfo = new FileInputStream("/proc/cpuinfo");
        try {
            String result = new String(IOUtils.readStream(cpuinfo));
            Log.v(MXApplication.TAG, result);
            StringLineReader lineReader = new StringLineReader(result);
            Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            while (true) {
                String line = lineReader.readLine();
                if (line == null) {
                    return map;
                }
                int colonIndex = line.indexOf(58);
                if (colonIndex > 0) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    if (key != null && value != null) {
                        map.put(key, value);
                    }
                }
            }
        } finally {
            cpuinfo.close();
        }
    }

    public static boolean isWritingSlow() {
        return Model.model == 10;
    }

    public static boolean isFast() {
        return getCPUClockFrequency() >= -2147483648L || Cpu.coreCount > 2 || Cpu.family == 5 || Cpu.family == 2 || Cpu.family == 4;
    }

    public static long getCPUClockFrequency() {
        if (_frequency < 0) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"), 256);
                _frequency = Long.parseLong(reader.readLine()) * 1000;
                if (_frequency < 0) {
                    Log.e(MXApplication.TAG, "Got invalid frequency: " + _frequency + "hz");
                    _frequency = 0L;
                }
                reader.close();
            } catch (Exception e) {
                Log.w(MXApplication.TAG, "", e);
                _frequency = 0L;
            }
        }
        return _frequency;
    }

    public static boolean isInMultiWindow(Context context) {
        return false;
    }

    public static String readProperty(String name) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process exec = runtime.exec(new String[]{"getprop", name});
            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String readLine = reader.readLine();
            reader.close();
            exec.destroy();
            return readLine;
        } catch (IOException e) {
            Log.e(MXApplication.TAG, "", e);
            return null;
        }
    }
}
