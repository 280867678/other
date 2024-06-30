package com.mxtech;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import com.mxtech.app.MXApplication;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/* loaded from: classes.dex */
public final class StringUtils {
    public static String byteText;
    public static final char[] SPACE_CHARS = {' ', '\t'};
    public static final Comparator<String> CASE_INSENSITIVE_NUMRIC_ORDER = new Comparator<String>() { // from class: com.mxtech.StringUtils.1
        @Override // java.util.Comparator
        public int compare(String left, String right) {
            return StringUtils.compareToIgnoreCaseNumeric(left, right);
        }
    };

    public static String formatFrequency(long freq, int fractionDigits) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        if (fractionDigits >= 0) {
            formatter.setMaximumFractionDigits(fractionDigits);
        }
        if (freq >= 1000000000) {
            return formatter.format(freq / 1.0E9d) + " GHz";
        }
        if (freq >= 1000000) {
            return formatter.format(freq / 1000000.0d) + " MHz";
        }
        if (freq >= 10000) {
            return formatter.format(freq / 1000.0d) + " KHz";
        }
        return formatter.format(freq) + " Hz";
    }

    public static String getSelected(CharSequence text) {
        int end;
        int start = Selection.getSelectionStart(text);
        return (start < 0 || (end = Selection.getSelectionEnd(text)) <= start) ? "" : text.subSequence(start, end).toString();
    }

    public static void unselect(CharSequence text) {
        if (text instanceof Spannable) {
            Selection.removeSelection((Spannable) text);
        }
    }

    public static int indexOfIgnoreCase(String context, String s) {
        return indexOfIgnoreCase(context, 0, s);
    }

    public static boolean containsIgnoreCase(String context, String s) {
        return indexOfIgnoreCase(context, 0, s) >= 0;
    }

    public static int indexOfIgnoreCase(String context, int from, String s) {
        int s2_len = s.length();
        if (s2_len == 0) {
            return 0;
        }
        int s1_len = context.length();
        int s2_first = Character.toLowerCase(s.codePointAt(0));
        int numMatched = 0;
        int lookingFor = s2_first;
        for (int i = from; i < s1_len; i++) {
            int code = Character.toLowerCase(context.codePointAt(i));
            if (code == lookingFor) {
                numMatched++;
                if (numMatched == s2_len) {
                    return (i - s2_len) + 1;
                }
                lookingFor = Character.toLowerCase(s.codePointAt(numMatched));
            } else {
                numMatched = 0;
                lookingFor = s2_first;
            }
        }
        return -1;
    }

    public static boolean startsWithIgnoreCase(String context, String prefix) {
        return context.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWithIgnoreCase(String context, String suffix) {
        int suffixLen = suffix.length();
        return context.regionMatches(true, context.length() - suffixLen, suffix, 0, suffixLen);
    }

    public static int indexAnyOf(CharSequence seq, char[] search) {
        return indexAnyOf(seq, search, 0);
    }

    public static int indexAnyOf(CharSequence seq, char[] search, int start) {
        int length = seq.length();
        for (int i = start; i < length; i++) {
            char ch = seq.charAt(i);
            for (char s : search) {
                if (s == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence seq, int ch) {
        return indexOf(seq, ch, 0);
    }

    public static int indexOf(CharSequence seq, int ch, int start) {
        int length = seq.length();
        for (int i = start; i < length; i++) {
            if (seq.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    public static void closeSpans(Spannable s) {
        Object[] spans;
        int end = s.length();
        for (Object span : s.getSpans(end, end, Object.class)) {
            int flags = s.getSpanFlags(span);
            int markFlags = flags & 51;
            if (markFlags != 33 && markFlags != 17) {
                s.setSpan(span, s.getSpanStart(span), end, (flags & (-52)) | 33);
            }
        }
    }

    public static String capitalize(String s, StringBuilder sb) {
        int len = s.length();
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                int code = s.codePointAt(i);
                if (Character.isWhitespace(code)) {
                    break;
                } else if (!Character.isLowerCase(code)) {
                    return s;
                }
            }
            sb.setLength(0);
            sb.appendCodePoint(Character.toUpperCase(s.codePointAt(0))).append((CharSequence) s, 1, len);
            return sb.toString();
        }
        return s;
    }

    public static String capitalize(String s) {
        int len = s.length();
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                int code = s.codePointAt(i);
                if (Character.isWhitespace(code)) {
                    break;
                } else if (!Character.isLowerCase(code)) {
                    return s;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.appendCodePoint(Character.toUpperCase(s.codePointAt(0))).append((CharSequence) s, 1, len);
            return sb.toString();
        }
        return s;
    }

    public static String[] splitToWords(String str) {
        String str2 = str + ' ';
        int begin = -1;
        int length = str2.length();
        ArrayList<String> coll = null;
        for (int i = 0; i < length; i++) {
            char ch = str2.charAt(i);
            if (Character.isLetter(ch)) {
                if (begin < 0) {
                    begin = i;
                }
            } else if (begin >= 0) {
                int wordLen = i - begin;
                if (wordLen > 1) {
                    if (coll == null) {
                        coll = new ArrayList<>();
                    }
                    coll.add(str2.substring(begin, i));
                }
                begin = -1;
            }
        }
        return coll == null ? new String[0] : (String[]) coll.toArray(new String[coll.size()]);
    }

    public static String joinIntArray(CharSequence delimeter, int[] array) {
        StringBuilder b = new StringBuilder();
        for (int val : array) {
            if (b.length() > 0) {
                b.append(delimeter);
            }
            b.append(val);
        }
        return b.toString();
    }

    public static String format(String str, Map<String, String> values, boolean keepNotMapped) {
        return format(str, values, "%(", ")", keepNotMapped);
    }

    public static String format(String str, Map<String, String> values, String startMark, String endMark, boolean keepNotMapped) {
        int close;
        int close2;
        int startMarkLen = startMark.length();
        int endMarkLen = endMark.length();
        StringBuilder b = null;
        int cursor = 0;
        while (true) {
            if (b != null) {
                int open = b.indexOf(startMark, cursor);
                if (open < 0 || (close = b.indexOf(endMark, open + startMarkLen)) < 0) {
                    break;
                }
                String value = values.get(b.substring(open + startMarkLen, close));
                if (value != null) {
                    b.replace(open, close + endMarkLen, value);
                    cursor = open + value.length();
                } else if (keepNotMapped) {
                    cursor = close + endMarkLen;
                } else {
                    b.delete(open, close + endMarkLen);
                }
            } else {
                int open2 = str.indexOf(startMark, cursor);
                if (open2 < 0 || (close2 = str.indexOf(endMark, open2 + startMarkLen)) < 0) {
                    break;
                }
                String value2 = values.get(str.substring(open2 + startMarkLen, close2));
                if (value2 != null) {
                    b = new StringBuilder(str);
                    b.replace(open2, close2 + endMarkLen, value2);
                    cursor = open2 + value2.length();
                } else if (keepNotMapped) {
                    cursor = close2 + endMarkLen;
                } else {
                    b = new StringBuilder(str);
                    b.delete(open2, close2 + endMarkLen);
                }
            }
        }
        if (b != null) {
            return b.toString();
        }
        return str;
    }

    public static String trimRight(String src, char ch) {
        int i = src.length() - 1;
        while (i >= 0 && src.charAt(i) == ch) {
            i--;
        }
        return src.substring(0, i + 1);
    }

    public static void appendHtmlEncoded(StringBuilder sb, CharSequence s) {
        appendHtmlEncoded(sb, s, 0, s.length());
    }

    public static boolean equalsIgnoreCase(@Nullable String s1, @Nullable String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return true;
            }
            return false;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static int compareToIgnoreCase(@Nullable String s1, @Nullable String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            }
            return -1;
        } else if (s2 == null) {
            return 1;
        } else {
            return s1.compareToIgnoreCase(s2);
        }
    }

    public static int compareToIgnoreCaseNumeric(@Nullable String s1, @Nullable String s2) {
        char c1;
        char c2;
        int diff;
        char prev;
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            }
            return -1;
        } else if (s2 == null) {
            return 1;
        } else {
            int len1 = s1.length();
            int len2 = s2.length();
            int end = len1 < len2 ? len1 : len2;
            for (int i = 0; i < end; i++) {
                char c12 = s1.charAt(i);
                char c22 = s2.charAt(i);
                if (c12 != c22 && (diff = (c1 = foldCase(c12)) - (c2 = foldCase(c22))) != 0) {
                    boolean digit1 = '0' <= c1 && c1 <= '9';
                    boolean digit2 = '0' <= c2 && c2 <= '9';
                    if (digit1 && digit2) {
                        int f1 = i;
                        while (f1 > 0) {
                            char ch = s1.charAt(f1 - 1);
                            if (ch < '0' || ch > '9') {
                                break;
                            }
                            f1--;
                        }
                        int f2 = i;
                        while (f2 > 0) {
                            char ch2 = s2.charAt(f2 - 1);
                            if (ch2 < '0' || ch2 > '9') {
                                break;
                            }
                            f2--;
                        }
                        long n1 = NumericUtils.parseHeadingLong(s1, f1, len1);
                        long n2 = NumericUtils.parseHeadingLong(s2, f2, len2);
                        if (n1 < n2) {
                            return -1;
                        }
                        if (n1 > n2) {
                            return 1;
                        }
                        return diff;
                    } else if (i > 0) {
                        if ((digit1 || digit2) && '0' <= (prev = s1.charAt(i - 1)) && prev <= '9') {
                            if (digit1) {
                                return 1;
                            }
                            return -1;
                        }
                        return diff;
                    } else {
                        return diff;
                    }
                }
            }
            return len1 - len2;
        }
    }

    public static void appendHtmlEncoded(StringBuilder sb, CharSequence s, int begin, int end) {
        for (int i = begin; i < end; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
    }

    public static String getString_s(int id, Object... formatArgs) {
        try {
            return MXApplication.context.getString(id, formatArgs);
        } catch (Exception e) {
            Log.w(MXApplication.TAG, "", e);
            return MXApplication.context.getString(id);
        }
    }

    public static String getQuantityString_s(int id, int quantity, Object... formatArgs) {
        try {
            return MXApplication.context.getResources().getQuantityString(id, quantity, formatArgs);
        } catch (Exception e) {
            Log.w(MXApplication.TAG, "", e);
            return "@" + Integer.toString(id);
        }
    }

    public static CharSequence getStringItalic_s(int resId, String arg1) {
        return Html.fromHtml(getString_s(resId, "<i>" + TextUtils.htmlEncode(arg1) + "</i>").replace("\n", "<br/>"));
    }

    public static CharSequence getStringItalic_s(int resId, String arg1, String arg2) {
        return Html.fromHtml(getString_s(resId, "<i>" + TextUtils.htmlEncode(arg1) + "</i>", "<i>" + TextUtils.htmlEncode(arg2) + "</i>").replace("\n", "<br/>"));
    }

    public static CharSequence getStringItalic_s(int resId, Object... formatArgs) {
        int numArgs = formatArgs.length;
        Object[] taggedArgs = new Object[numArgs];
        for (int i = 0; i < numArgs; i++) {
            taggedArgs[i] = "<i>" + TextUtils.htmlEncode(formatArgs[i].toString()) + "</i>";
        }
        return Html.fromHtml(getString_s(resId, taggedArgs).replace("\n", "<br/>"));
    }

    public static char foldCase(char ch) {
        if (ch <= 0 || ch >= 128) {
            return Character.toLowerCase(Character.toUpperCase(ch));
        }
        if ('A' <= ch && ch <= 'Z') {
            return (char) (ch + ' ');
        }
        return ch;
    }

    public static int foldCase(int codePoint) {
        if (codePoint <= 0 || codePoint >= 128) {
            return Character.toLowerCase(Character.toUpperCase(codePoint));
        }
        if (65 <= codePoint && codePoint <= 90) {
            return codePoint + 32;
        }
        return codePoint;
    }

    public static String replaceString(String string, String find, char replacePrefix, int replaceResId, char replaceSuffix) {
        StringBuilder sb = null;
        int start = 0;
        while (true) {
            int index = string.indexOf(find, start);
            if (index < 0) {
                break;
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append((CharSequence) string, start, index);
            sb.append(replacePrefix);
            sb.append(MXApplication.context.getString(replaceResId));
            sb.append(replaceSuffix);
            start = index + find.length();
        }
        if (sb != null) {
            sb.append((CharSequence) string, start, string.length());
            return sb.toString();
        }
        return string;
    }

    public static String localizeSquareBracket(String string, String find, int replaceResId) {
        return replaceString(string, '[' + find + ']', '[', replaceResId, ']');
    }

    public static String makeHyperlinkAnchor(String string, String link, char startChar, char endChar) {
        int anchorEnd;
        int anchorStart = string.indexOf(startChar);
        if (anchorStart >= 0 && (anchorEnd = string.indexOf(endChar, anchorStart + 1)) > anchorStart + 1) {
            StringBuilder sb = new StringBuilder();
            sb.append((CharSequence) string, 0, anchorStart).append("<a href=\"").append(link).append("\">").append((CharSequence) string, anchorStart + 1, anchorEnd).append("</a>").append((CharSequence) string, anchorEnd + 1, string.length());
            return sb.toString();
        }
        return string;
    }

    public static String makeSquareBracketHyperlinkAnchor(String string, String link) {
        return makeHyperlinkAnchor(string, link, '[', ']');
    }

    public static String makeHyperlinkAnchor(int resId, String link, char startChar, char endChar) {
        return makeHyperlinkAnchor(MXApplication.context.getString(resId), link, startChar, endChar);
    }

    public static String makeSquareBracketHyperlinkAnchor(int resId, String link) {
        return makeHyperlinkAnchor(MXApplication.context.getString(resId), link, '[', ']');
    }
}
