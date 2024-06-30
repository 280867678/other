package com.mxtech.subtitle;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import com.mxtech.StringUtils;
import com.mxtech.parser.IParser;
import com.mxtech.parser.NumberParser;
import com.mxtech.text.HtmlEx;

/* loaded from: classes2.dex */
public final class MicroDVDStylizer {
    private static final HtmlStyledTextPreprocessor _htmlPreprocessor = new HtmlStyledTextPreprocessor();

    public static CharSequence stylize(String source, int flags) {
        SpannableStringBuilder b;
        Object strikethroughSpan;
        char[] ctrlChars = {'{', '|'};
        int scan = 0;
        String source2 = _htmlPreprocessor.process(source);
        if (_htmlPreprocessor.containsHtmlTag) {
            b = SpannableStringBuilder.valueOf(HtmlEx.fromHtml(source2, (flags & 256) != 0 ? 0 : 1));
        } else {
            b = new SpannableStringBuilder(source2);
        }
        while (true) {
            scan = StringUtils.indexAnyOf(b, ctrlChars, scan);
            if (scan >= 0) {
                char ch = b.charAt(scan);
                if (ch == '{') {
                    int closeBracket = StringUtils.indexOf(b, 125, scan + 1);
                    if (closeBracket >= 0) {
                        CharSequence tag = b.subSequence(scan + 1, closeBracket);
                        b.delete(scan, closeBracket + 1);
                        if (tag.length() > 2 && tag.charAt(1) == ':') {
                            char ctrl = tag.charAt(0);
                            if (ctrl == 'y' || ctrl == 'Y') {
                                char style = tag.charAt(2);
                                switch (style) {
                                    case 'B':
                                    case 'b':
                                        strikethroughSpan = new StyleSpan(1);
                                        b.setSpan(strikethroughSpan, scan, b.length(), 33);
                                        break;
                                    case 'I':
                                    case 'i':
                                        strikethroughSpan = new StyleSpan(2);
                                        b.setSpan(strikethroughSpan, scan, b.length(), 33);
                                        break;
                                    case 'S':
                                    case 's':
                                        strikethroughSpan = new StrikethroughSpan();
                                        b.setSpan(strikethroughSpan, scan, b.length(), 33);
                                        break;
                                    case 'U':
                                    case 'u':
                                        strikethroughSpan = new UnderlineSpan();
                                        b.setSpan(strikethroughSpan, scan, b.length(), 33);
                                        break;
                                }
                            } else if (ctrl == 'c' || ctrl == 'C') {
                                if (tag.length() == 9 && tag.charAt(2) == '$') {
                                    try {
                                        int color = Integer.parseInt(tag.subSequence(3, 9).toString(), 16);
                                        ForegroundColorSpan span = new ForegroundColorSpan(Color.rgb(Color.blue(color), Color.green(color), Color.red(color)));
                                        b.setSpan(span, scan, b.length(), 33);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    } else {
                        scan++;
                    }
                } else {
                    b.replace(scan, scan + 1, (CharSequence) "\n");
                    scan++;
                }
            } else {
                return b;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class LineParser {
        private final char closeBracket;
        public int endTimecode;
        private final char openBracket;
        private final IParser parser;
        public int startTimecode;
        public String text;

        public LineParser(char openBracket, char closeBracket) {
            this(new NumberParser(), openBracket, closeBracket);
        }

        public LineParser(IParser parser, char openBracket, char closeBracket) {
            this.parser = parser;
            this.openBracket = openBracket;
            this.closeBracket = closeBracket;
        }

        public boolean parse(String line) {
            int lineLen = line.length();
            if (lineLen <= 0 || line.charAt(0) != this.openBracket) {
                return false;
            }
            try {
                int startClose = line.indexOf(this.closeBracket, 1);
                if (startClose >= 0) {
                    this.startTimecode = this.parser.parseToInt(line.substring(1, startClose));
                    if (startClose + 1 >= lineLen || line.charAt(startClose + 1) != this.openBracket) {
                        return false;
                    }
                    int endOpen = startClose + 1;
                    int endClose = line.indexOf(this.closeBracket, endOpen + 1);
                    if (endClose >= 0) {
                        this.endTimecode = this.parser.parseToInt(line.substring(endOpen + 1, endClose));
                        this.text = line.substring(endClose + 1);
                        return true;
                    }
                    return false;
                }
                return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
