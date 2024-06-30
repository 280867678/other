package com.mxtech;

import android.util.Log;
import com.mxtech.app.MXApplication;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes2.dex */
public class ArrayUtils {
    public static final int[] EMPTY_INT_ARRAY = new int[0];

    public static int[] toPrimitive(List<Integer> array) {
        int size = array.size();
        if (size == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = array.get(i).intValue();
        }
        return result;
    }

    public static <T> T[] safeSort(T[] values) {
        try {
            Arrays.sort(values);
        } catch (Exception e) {
            Log.e(MXApplication.TAG, "", e);
        }
        return values;
    }

    public static <T> T[] safeSort(T[] values, Comparator<? super T> comparator) {
        try {
            Arrays.sort(values, comparator);
        } catch (Exception e) {
            Log.e(MXApplication.TAG, "", e);
        }
        return values;
    }
}
