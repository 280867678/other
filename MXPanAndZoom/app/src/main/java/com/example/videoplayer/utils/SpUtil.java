package com.example.videoplayer.utils;

import android.os.Parcelable;
import com.tencent.mmkv.MMKV;
import java.util.Collections;
import java.util.Set;

/**
 * @Author : Liu XiaoRan
 * @Email : 592923276@qq.com
 * @Date : on 2022/10/25 17:38.
 * @Description :
 */
public class SpUtil {
    private static MMKV mmkv;

    static {
        mmkv = MMKV.defaultMMKV();
    }

    public static void put(String key, Object value) {
        if (value instanceof String) {
            mmkv.encode(key, (String) value);
        } else if (value instanceof Float) {
            mmkv.encode(key, (Float) value);
        } else if (value instanceof Boolean) {
            mmkv.encode(key, (Boolean) value);
        } else if (value instanceof Integer) {
            mmkv.encode(key, (Integer) value);
        } else if (value instanceof Long) {
            mmkv.encode(key, (Long) value);
        } else if (value instanceof Double) {
            mmkv.encode(key, (Double) value);
        } else if (value instanceof byte[]) {
            mmkv.encode(key, (byte[]) value);
        } else if (value == null) {
            return;
        }
    }

    public static <T extends Parcelable> void put(String key, T t) {
        if (t != null) {
            mmkv.encode(key, t);
        }
    }

    public static void put(String key, Set<String> sets) {
        if (sets != null) {
            mmkv.encode(key, sets);
        }
    }

    public static Integer getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public static Double getDouble(String key) {
        return mmkv.decodeDouble(key, 0.00);
    }

    public static Long getLong(String key) {
        return mmkv.decodeLong(key, 0L);
    }

    public static Boolean getBoolean(String key) {
        return mmkv.decodeBool(key, false);
    }

    public static Float getFloat(String key) {
        return mmkv.decodeFloat(key, 0F);
    }

    public static byte[] getByteArray(String key) {
        return mmkv.decodeBytes(key);
    }

    public static String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    public static <T extends Parcelable> T getParcelable(String key, Class<T> tClass) {
        return mmkv.decodeParcelable(key, tClass);
    }

    public static Set<String> getStringSet(String key) {
        return mmkv.decodeStringSet(key, Collections.emptySet());
    }

    public static void removeKey(String key) {
        mmkv.removeValueForKey(key);
    }

    public static void clearAll() {
        mmkv.clearAll();
    }
}
