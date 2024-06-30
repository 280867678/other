package com.example.videoplayer.adapter;

import java.util.Formatter;
import java.util.Locale;

public class CommonUtil {

    public static String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000L;
        long seconds = totalSeconds % 60L;
        long minutes = totalSeconds / 60L % 60L;
        long hours = totalSeconds / 3600L;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        return hours > 0L ? mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString() : mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }


}
