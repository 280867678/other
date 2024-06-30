package com.mxtech.net;

import android.net.Uri;
import com.mxtech.videoplayer.list.MediaListFragment;

/* loaded from: classes.dex */
public final class UriUtils {
    public static boolean isPointingSameObject(Uri left, Uri right) {
        if (left.equals(right)) {
            return true;
        }
        String schemeLeft = left.getScheme();
        String schemeRight = right.getScheme();
        if (schemeLeft == null) {
            if (MediaListFragment.TYPE_FILE.equals(schemeRight)) {
                String l = left.toString();
                String r = right.toString();
                if (l.length() + 7 == r.length() && r.endsWith(l)) {
                    return true;
                }
            }
        } else if (MediaListFragment.TYPE_FILE.equals(schemeLeft) && schemeRight == null) {
            String l2 = left.toString();
            String r2 = right.toString();
            if (l2.length() == r2.length() + 7 && l2.endsWith(r2)) {
                return true;
            }
        }
        return false;
    }
}
