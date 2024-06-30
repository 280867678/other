package com.carrydream.cardrecorder.tool;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.math.BigDecimal;

/* loaded from: classes.dex */
public class CacheUtils {
    public static String getTotalCacheSize(Context context) throws Exception {
        long folderSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals("mounted")) {
            folderSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(folderSize);
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals("mounted")) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }

    public static void cleanDatabaseByName(Context context, String str) {
        context.deleteDatabase(str);
    }

    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    public static void cleanCustomCache(String str) {
        deleteFilesByDirectory(new File(str));
    }

    public static void cleanApplicationData(Context context, String... strArr) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (strArr == null) {
            return;
        }
        for (String str : strArr) {
            cleanCustomCache(str);
        }
    }

    private static void deleteFilesByDirectory(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                file2.delete();
            }
        }
    }

    public static long getFolderSize(File file) throws Exception {
        long length;
        long j = 0;
        try {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    length = getFolderSize(listFiles[i]);
                } else {
                    length = listFiles[i].length();
                }
                j += length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public static void deleteFolderFile(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            File file = new File(str);
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    deleteFolderFile(file2.getAbsolutePath(), true);
                }
            }
            if (z) {
                if (!file.isDirectory()) {
                    file.delete();
                } else if (file.listFiles().length == 0) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFormatSize(double d) {
        double d2 = d / 1024.0d;
        double d3 = d2 / 1024.0d;
        if (d3 < 1.0d) {
            BigDecimal bigDecimal = new BigDecimal(Double.toString(d2));
            return bigDecimal.setScale(2, 4).toPlainString() + "K";
        }
        double d4 = d3 / 1024.0d;
        if (d4 < 1.0d) {
            BigDecimal bigDecimal2 = new BigDecimal(Double.toString(d3));
            return bigDecimal2.setScale(2, 4).toPlainString() + "M";
        }
        double d5 = d4 / 1024.0d;
        if (d5 < 1.0d) {
            BigDecimal bigDecimal3 = new BigDecimal(Double.toString(d4));
            return bigDecimal3.setScale(2, 4).toPlainString() + "G";
        }
        BigDecimal bigDecimal4 = new BigDecimal(d5);
        return bigDecimal4.setScale(2, 4).toPlainString() + ExifInterface.GPS_DIRECTION_TRUE;
    }

    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }
}
