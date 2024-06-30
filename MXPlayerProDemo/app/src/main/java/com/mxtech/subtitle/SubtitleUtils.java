package com.mxtech.subtitle;

/* loaded from: classes2.dex */
public final class SubtitleUtils {
    private static final StringBuilder buffer = new StringBuilder();

    public static String removeSSATags(String s) {
        int tagEnd;
        StringBuilder sb = null;
        int tagStart = 0;
        while (true) {
            if (sb != null) {
                tagStart = sb.indexOf("{\\", tagStart);
                if (tagStart < 0) {
                    return sb.toString();
                }
                tagEnd = sb.indexOf("}", tagStart + 2);
                if (tagEnd < 0) {
                    return sb.toString();
                }
            } else {
                tagStart = s.indexOf("{\\", tagStart);
                if (tagStart < 0 || (tagEnd = s.indexOf(125, tagStart + 2)) < 0) {
                    return s;
                }
                sb = buffer;
                sb.replace(0, sb.length(), s);
            }
            sb.delete(tagStart, tagEnd + 1);
        }
    }

    public static String replaceLineBreaksWith(String s, String[] lineBreakes, String replacement) {
        int index;
        StringBuilder sb = null;
        for (String lb : lineBreakes) {
            int index2 = 0;
            while (true) {
                if (sb != null) {
                    index = sb.indexOf(lb, index2);
                    if (index < 0) {
                        break;
                    }
                    sb.replace(index, lb.length() + index, replacement);
                    index2 = index + replacement.length();
                } else {
                    index = s.indexOf(lb, index2);
                    if (index >= 0) {
                        sb = buffer;
                        sb.replace(0, sb.length(), s);
                        sb.replace(index, lb.length() + index, replacement);
                        index2 = index + replacement.length();
                    }
                }
            }
        }
        return sb != null ? sb.toString() : s;
    }
}
