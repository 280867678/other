package com.mxtech.media;

import android.net.Uri;

public class MediaUtils {



    public static String ffmpegPathFrom(Uri uri) {
        String scheme = uri.getScheme();
        return (scheme == null || "file".equals(scheme)) ? uri.getPath() : uri.toString();
    }




}
