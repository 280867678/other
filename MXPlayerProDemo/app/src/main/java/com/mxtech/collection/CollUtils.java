package com.mxtech.collection;

import android.support.v4.util.LruCache;
import android.util.SparseBooleanArray;
import java.util.Map;

/* loaded from: classes2.dex */
public class CollUtils {
    public static int countTrue(SparseBooleanArray array) {
        int count = 0;
        for (int i = array.size() - 1; i >= 0; i--) {
            if (array.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static <K, V> LruCache<K, V> resizeLruCache(LruCache<K, V> old, int maxSize) {
        old.trimToSize(maxSize);
        LruCache<K, V> newCache = new LruCache<>(maxSize);
        for (Map.Entry<K, V> entry : old.snapshot().entrySet()) {
            newCache.put(entry.getKey(), entry.getValue());
        }
        return newCache;
    }
}
