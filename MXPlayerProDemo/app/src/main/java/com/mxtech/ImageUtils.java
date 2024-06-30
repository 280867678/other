package com.mxtech;

import android.graphics.Bitmap;
import android.os.Build;

/* loaded from: classes2.dex */
public class ImageUtils {
    public static native boolean isBitmapBlanc(Bitmap bitmap) throws Exception;

    public static int getSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 19) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
