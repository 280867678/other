package com.mask.photo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes2.dex */
public class FileUtils {
    private static Date date = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());

    public static String getDateName() {
        return getDateName(null);
    }

    public static String getDateName(String str) {
        date.setTime(System.currentTimeMillis());
        String format = dateFormat.format(date);
        if (TextUtils.isEmpty(str)) {
            return format;
        }
        return str + "_" + format;
    }

    public static File getCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    public static File getCachePhotoDir(Context context) {
        return new File(getCacheDir(context), Environment.DIRECTORY_PICTURES);
    }

    public static File getCachePhotoCompressDir(Context context) {
        return new File(getCachePhotoDir(context), "Compress");
    }
}
