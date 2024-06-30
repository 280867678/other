package com.carrydream.cardrecorder.tool;

import android.util.Log;
import com.carrydream.cardrecorder.tool.RxConstUtils;
//import com.google.firebase.firestore.util.ExponentialBackoff;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class RxTimeUtils {
    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String milliseconds2String(long j) {
        return milliseconds2String(j, DEFAULT_SDF);
    }

    public static String milliseconds2String(long j, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(new Date(j));
    }

    public static long string2Milliseconds(String str) {
        return string2Milliseconds(str, DEFAULT_SDF);
    }

    public static long string2Milliseconds(String str, SimpleDateFormat simpleDateFormat) {
        try {
            return simpleDateFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public static Date string2Date(String str) {
        return string2Date(str, DEFAULT_SDF);
    }

    public static Date string2Date(String str, SimpleDateFormat simpleDateFormat) {
        return new Date(string2Milliseconds(str, simpleDateFormat));
    }

    public static String date2String(Date date) {
        return date2String(date, DEFAULT_SDF);
    }

    public static String date2String(Date date, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(date);
    }

    public static long date2Milliseconds(Date date) {
        return date.getTime();
    }

    public static Date milliseconds2Date(long j) {
        return new Date(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.carrydream.cardrecorder.tool.RxTimeUtils$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit;

        static {
            int[] iArr = new int[RxConstUtils.TimeUnit.values().length];
            $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit = iArr;
            try {
                iArr[RxConstUtils.TimeUnit.MSEC.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit[RxConstUtils.TimeUnit.SEC.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit[RxConstUtils.TimeUnit.MIN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit[RxConstUtils.TimeUnit.HOUR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit[RxConstUtils.TimeUnit.DAY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private static long milliseconds2Unit(long j, RxConstUtils.TimeUnit timeUnit) {
        int i = AnonymousClass1.$SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$TimeUnit[timeUnit.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            return -1L;
                        }
                        return j / 86400000;
                    }
                    return j / 3600000;
                }
                return j / 60000;
            }
            return j / 1000;
        }
        return j / 1;
    }

    public static long getIntervalTime(String str, String str2, RxConstUtils.TimeUnit timeUnit) {
        return getIntervalTime(str, str2, timeUnit, DEFAULT_SDF);
    }

    public static long getIntervalTime(String str, String str2, RxConstUtils.TimeUnit timeUnit, SimpleDateFormat simpleDateFormat) {
        return Math.abs(milliseconds2Unit(string2Milliseconds(str, simpleDateFormat) - string2Milliseconds(str2, simpleDateFormat), timeUnit));
    }

    public static long getIntervalTime(Date date, Date date2, RxConstUtils.TimeUnit timeUnit) {
        return Math.abs(milliseconds2Unit(date2Milliseconds(date2) - date2Milliseconds(date), timeUnit));
    }

    public static long getCurTimeMills() {
        return System.currentTimeMillis();
    }

    public static String getCurTimeString() {
        return date2String(new Date());
    }

    public static String getCurTimeString(SimpleDateFormat simpleDateFormat) {
        return date2String(new Date(), simpleDateFormat);
    }

    public static Date getCurTimeDate() {
        return new Date();
    }

    public static long getIntervalByNow(String str, RxConstUtils.TimeUnit timeUnit) {
        return getIntervalByNow(str, timeUnit, DEFAULT_SDF);
    }

    public static long getIntervalByNow(String str, RxConstUtils.TimeUnit timeUnit, SimpleDateFormat simpleDateFormat) {
        return getIntervalTime(getCurTimeString(), str, timeUnit, simpleDateFormat);
    }

    public static long getIntervalByNow(Date date, RxConstUtils.TimeUnit timeUnit) {
        return getIntervalTime(getCurTimeDate(), date, timeUnit);
    }

    public static boolean isLeapYear(int i) {
        return (i % 4 == 0 && i % 100 != 0) || i % 400 == 0;
    }

    public static String simpleDateFormat(String str, Date date) {
        if (RxDataUtils.isNullString(str)) {
            str = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(str).format(date);
    }

    public static String Date2Timestamp(Date date) {
        return String.valueOf(date.getTime()).substring(0, 10);
    }

    public static Date string2Date(String str, String str2) {
        try {
            return new SimpleDateFormat(str).parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String string2Timestamp(String str, String str2) {
        return Date2Timestamp(string2Date(str, str2));
    }

    public static String getCurrentDateTime(String str) {
        return simpleDateFormat(str, new Date());
    }

    public static String getDate(String str, String str2) {
        return simpleDateFormat(str2, new Date(RxDataUtils.stringToInt(str) * 1000));
    }

    public static String getYestoryDate(String str) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -1);
        return simpleDateFormat(str, calendar.getTime());
    }

    public static String formatTime(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return simpleDateFormat.format(Long.valueOf(j));
    }

    public static long formatSeconds(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        long j = 0;
        try {
            j = simpleDateFormat.parse(str).getTime();
            Log.d("时间戳", j + "");
            return j;
        } catch (ParseException e) {
            e.printStackTrace();
            return j;
        }
    }

    public static int getDaysByYearMonth(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, i);
        calendar.set(2, i2 - 1);
        calendar.set(5, 1);
        calendar.roll(5, -1);
        return calendar.get(5);
    }

    public static int stringForWeek(String str) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(str));
        if (calendar.get(7) == 1) {
            return 7;
        }
        return calendar.get(7) - 1;
    }

    public static int stringForWeek(String str, SimpleDateFormat simpleDateFormat) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(str));
        if (calendar.get(7) == 1) {
            return 7;
        }
        return calendar.get(7) - 1;
    }
}
