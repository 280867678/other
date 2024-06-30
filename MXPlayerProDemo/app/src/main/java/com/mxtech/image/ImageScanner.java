package com.mxtech.image;

import android.os.Build;
import com.mxtech.FileUtils;
import java.io.File;

/* loaded from: classes.dex */
public class ImageScanner {
    private static final String[] EXTENSIONS = {"jpg", "png", "gif", "jpeg", "bmp"};
    private static final String[] EXTENSIONS_WEBP = {"jpg", "png", "gif", "jpeg", "bmp", "webp"};
    private static final int VERSION_WEBP_SUPPORTED = 17;

    public static final String[] getExtensions() {
        return Build.VERSION.SDK_INT >= 17 ? EXTENSIONS_WEBP : EXTENSIONS;
    }

    public static boolean isSupportedExtension(String ext) {
        return isSupportedExtension(ext, 0);
    }

    public static boolean isSupportedFile(File file) {
        String path = file.getPath();
        int lastDot = path.lastIndexOf(46);
        if (lastDot < 0) {
            return false;
        }
        return isSupportedExtension(path, lastDot + 1);
    }

    public static boolean isSupportedExtension(String name, int offset) {
        if (name.length() > offset + 1) {
            switch (name.charAt(offset)) {
                case 'B':
                case 'b':
                    return "BMP".regionMatches(true, 0, name, offset, 3);
                case 'G':
                case 'g':
                    return "GIF".regionMatches(true, 0, name, offset, 3);
                case 'J':
                case 'j':
                    if ("JPG".regionMatches(true, 0, name, offset, 3) || "JPEG".regionMatches(true, 0, name, offset, 3)) {
                        return true;
                    }
                    break;
                case 'P':
                case 'p':
                    return "PNG".regionMatches(true, 0, name, offset, 3);
                case 'W':
                case 'w':
                    if (Build.VERSION.SDK_INT >= 17) {
                        return "WEBP".regionMatches(true, 0, name, offset, 3);
                    }
                    break;
            }
        }
        return false;
    }

    public static boolean isCoverOf(String videoFileName, String coverFileName) {
        return isCoverOf(videoFileName, coverFileName, true);
    }

    public static boolean isCoverOf(String videoFileName, String coverFileName, boolean testExtension) {
        int namePartLen = videoFileName.lastIndexOf(46);
        return namePartLen == coverFileName.lastIndexOf(46) && videoFileName.regionMatches(true, 0, coverFileName, 0, namePartLen) && (!testExtension || isSupportedExtension(coverFileName, namePartLen + 1));
    }

    public static File scanCoverFile(File videoFile) {
        String videoFileName;
        int namePartLen;
        File folder = videoFile.getParentFile();
        File[] files = FileUtils.listFiles(folder);
        if (files != null && (namePartLen = (videoFileName = videoFile.getName()).lastIndexOf(46)) > 0) {
            for (File file : files) {
                String fileName = file.getName();
                if (namePartLen == fileName.lastIndexOf(46) && videoFileName.regionMatches(true, 0, fileName, 0, namePartLen) && isSupportedExtension(fileName, namePartLen + 1)) {
                    return file;
                }
            }
        }
        return null;
    }
}
