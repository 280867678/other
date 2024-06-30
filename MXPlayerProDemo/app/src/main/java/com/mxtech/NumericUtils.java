package com.mxtech;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.Formatter;
import com.mxtech.videoplayer.pro.R;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Random;

/* loaded from: classes2.dex */
public final class NumericUtils {
    public static final ThreadLocal<Random> ThreadLocalRandom = new ThreadLocal<Random>() { // from class: com.mxtech.NumericUtils.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public Random initialValue() {
            return new SecureRandom();
        }
    };

    public static long parseHeadingLong(String s, int begin, int end) {
        long val;
        long val2 = -1;
        for (int i = begin; i < end; i++) {
            char ch = s.charAt(i);
            if ('0' > ch || ch > '9') {
                break;
            }
            if (val2 < 0) {
                val = 0;
            } else {
                val = val2 * 10;
            }
            val2 = val + (ch - '0');
        }
        return val2;
    }

    public static long parseHeadingLong(String s) {
        return parseHeadingLong(s, 0, s.length());
    }

    public static String formatShortAndLongSize(Context context, long size) {
        return size < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID ? NumberFormat.getIntegerInstance().format(size) + ' ' + context.getString(R.string.byteText) : Formatter.formatShortFileSize(context, size) + " (" + NumberFormat.getIntegerInstance().format(size) + ' ' + context.getString(R.string.byteText) + ')';
    }
}
