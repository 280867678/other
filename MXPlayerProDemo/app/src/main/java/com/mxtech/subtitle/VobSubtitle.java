package com.mxtech.subtitle;

import android.net.Uri;
import android.util.Log;

/* loaded from: classes2.dex */
public class VobSubtitle {
    private static final String REF_STRING = "# VobSub index file,";
    public static final String TYPENAME = "VobSub";

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        if (text.startsWith(REF_STRING)) {
            try {
                return new ISubtitle[]{new FFSubtitle(uri, TYPENAME, name, text, client)};
            } catch (Exception e) {
                Log.w(ISubtitle.TAG, "", e);
            }
        }
        return null;
    }
}
