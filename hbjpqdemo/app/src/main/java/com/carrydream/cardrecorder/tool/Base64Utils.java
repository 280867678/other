package com.carrydream.cardrecorder.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/* loaded from: classes.dex */
public class Base64Utils {
    public static Bitmap stringToBitmap(String str) {
        try {
            byte[] decode = Base64.decode(str.split(",")[1], 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
