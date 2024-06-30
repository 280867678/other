package com.mxtech.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/* loaded from: classes2.dex */
public class MultiValue {
    public static <K, V> void put(Map<K, Object> map, K key, V value) {
        Object mapped = map.get(key);
        if (mapped == null) {
            map.put(key, value);
        } else if (mapped instanceof ArrayList) {
            ((ArrayList) mapped).add(value);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(mapped);
            arrayList.add(value);
            map.put(key, arrayList);
        }
    }

    public static int size(Object mapped) {
        if (mapped instanceof ArrayList) {
            return ((ArrayList) mapped).size();
        }
        return 1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <V> V get(Object mapped, int index) {
        if (mapped instanceof ArrayList) {
            return (V) ((ArrayList) mapped).get(index);
        }
        if (index == 0) {
            return mapped;
        }
        throw new IllegalArgumentException();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <V> V[] toArray(Object mapped) {
        if (mapped instanceof ArrayList) {
            ArrayList<V> list = (ArrayList) mapped;
            int count = list.size();
            V[] array = (V[]) ((Object[]) Array.newInstance(list.get(0).getClass(), count));
            for (int i = 0; i < count; i++) {
                array[i] = list.get(i);
            }
            return array;
        }
        V[] array2 = (V[]) ((Object[]) Array.newInstance(mapped.getClass(), 1));
        array2[0] = mapped;
        return array2;
    }
}
