package com.mxtech;

import android.content.Context;
import android.text.format.DateUtils;
import java.util.Calendar;

/* loaded from: classes2.dex */
public class DateTimeUtils {
    public static final long DAY = 86400000;
    public static final long HOUR = 3600000;
    public static final long MINUTE = 60000;
    public static final long SECOND = 1000;
    public static final long WEEK = 604800000;

    public static String formatShortest(Context context, long then, long now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        long now_year = calendar.get(1);
        long now_day = calendar.get(6);
        calendar.setTimeInMillis(then);
        long then_year = calendar.get(1);
        long then_day = calendar.get(6);
        if (now_year != then_year) {
            return DateUtils.formatDateTime(context, then, 655380);
        }
        if (now_day != then_day) {
            return DateUtils.formatDateTime(context, then, 524304);
        }
        return DateUtils.formatDateTime(context, then, 524289);
    }
}
