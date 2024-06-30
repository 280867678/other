package com.mxtech;

import android.content.Context;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public final class Library {
    public static final String PACKAGE_COMMON = "com.mxtech.";
    public static boolean advertise;
    public static String defaultLibraryDirectory;
    public static int defaultResId;

    public static void init(Context context, int defaultResId2) {
        StringUtils.byteText = context.getString(R.string.byteText);
        defaultResId = defaultResId2;
        DeviceUtils.init(context);
    }
}
