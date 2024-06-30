package com.mxtech;

import java.io.File;

/* loaded from: classes2.dex */
public final class DBUtils {
    public static CharSequence escapeLikeString(CharSequence input) {
        int len = input.length();
        int last = 0;
        StringBuilder output = null;
        for (int i = 0; i < len; i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '%':
                case '\\':
                case '_':
                    if (output == null) {
                        output = new StringBuilder(len + 1);
                    }
                    if (last < i) {
                        output.append(input, last, i);
                    }
                    output.append('\\');
                    last = i;
                    break;
                case '\'':
                    if (output == null) {
                        output = new StringBuilder(len + 1);
                    }
                    if (last < i) {
                        output.append(input, last, i);
                    }
                    output.append('\'');
                    last = i;
                    break;
            }
        }
        return output == null ? input : output.append(input, last, len);
    }

    public static StringBuilder appendEscapedLikeString(StringBuilder output, CharSequence input) {
        int len = input.length();
        int last = 0;
        for (int i = 0; i < len; i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '%':
                case '\\':
                case '_':
                    if (last < i) {
                        output.append(input, last, i);
                    }
                    output.append('\\');
                    last = i;
                    break;
                case '\'':
                    if (last < i) {
                        output.append(input, last, i);
                    }
                    output.append('\'');
                    last = i;
                    break;
            }
        }
        return output.append(input, last, len);
    }

    public static CharSequence stripLikeCtrlCharacters(CharSequence input) {
        StringBuilder b = null;
        int len = input.length();
        int appendEnd = 0;
        for (int i = 0; i < len; i++) {
            char ch = input.charAt(i);
            if (ch == '%' || ch == '_') {
                if (b == null) {
                    b = new StringBuilder(input.subSequence(0, i));
                } else {
                    b.append(input, appendEnd, i);
                }
                appendEnd = i + 1;
            }
        }
        if (b != null) {
            b.append(input, appendEnd, len);
            return b;
        }
        return input;
    }

    public static void appendPathFilter(StringBuilder b, CharSequence column, CharSequence path, boolean includeSubFolder) {
        b.append(column).append(" LIKE '");
        appendEscapedLikeString(b, path).append(File.separatorChar).append("%' ESCAPE '\\'");
        if (!includeSubFolder) {
            b.append(" AND SUBSTR(").append(column).append(',').append(path.length() + 2).append(") NOT LIKE '%").append(File.separatorChar).append("%'");
        }
    }

    public static String pathFilterString(CharSequence column, CharSequence path, boolean includeSubFolder) {
        StringBuilder b = new StringBuilder();
        appendPathFilter(b, column, path, includeSubFolder);
        return b.toString();
    }
}
