package com.carrydream.cardrecorder.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.text.DecimalFormat;

/* loaded from: classes.dex */
public class BitmapCompare {
    private static final String DIFFERENT_SIZE = "图片大小不同";
    private static final String RESULT_FORMAT = "00.0%";

    public static String similarity(String str, String str2) {
        return similarity(BitmapFactory.decodeFile(str), BitmapFactory.decodeFile(str2));
    }

    public static String similarity(Bitmap bitmap, Bitmap bitmap2) {
        int width = bitmap.getWidth();
        int width2 = bitmap2.getWidth();
        int height = bitmap.getHeight();
        if (height == bitmap2.getHeight() && width == width2) {
            int[] iArr = new int[width];
            int[] iArr2 = new int[width2];
            reset();
            for (int i = 0; i < height; i++) {
                int i2 = i;
                bitmap.getPixels(iArr, 0, width, 0, i2, width, 1);
                bitmap2.getPixels(iArr2, 0, width2, 0, i2, width2, 1);
                comparePixels(iArr, iArr2, width);
            }
            return percent(Count.sT, Count.sF + Count.sT);
        }
        return DIFFERENT_SIZE;
    }

    private static void comparePixels(int[] iArr, int[] iArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] == iArr2[i2]) {
                Count.access$008();
            } else {
                Count.access$108();
            }
        }
    }

    private static String percent(int i, int i2) {
        return new DecimalFormat(RESULT_FORMAT).format((i * 1.0d) / i2);
    }

    private static void reset() {
        int unused = Count.sT = 0;
        int unused2 = Count.sF = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Count {
        private static int sF;
        private static int sT;

        private Count() {
        }

        static /* synthetic */ int access$008() {
            int i = sT;
            sT = i + 1;
            return i;
        }

        static /* synthetic */ int access$108() {
            int i = sF;
            sF = i + 1;
            return i;
        }
    }
}
