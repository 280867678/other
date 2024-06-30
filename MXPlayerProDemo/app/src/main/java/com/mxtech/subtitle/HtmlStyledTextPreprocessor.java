package com.mxtech.subtitle;

import com.mxtech.StringUtils;

/* loaded from: classes2.dex */
public final class HtmlStyledTextPreprocessor {
    private static final int FLAG_REMOVE_UNSUPPORTED = 2;
    private static final int FLAG_SCAN_ONLY = 1;
    private boolean _scanOnly;
    private final StringBuilder b = new StringBuilder();
    public boolean containsHtmlTag;

    public String process(String source) {
        this.containsHtmlTag = false;
        process_l(source, 1);
        if (this.containsHtmlTag) {
            this.b.setLength(0);
            process_l(source, 0);
            return this.b.toString();
        }
        return source;
    }

    public String processStrict(String source) {
        this.b.setLength(0);
        process_l(source, 2);
        return this.b.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x003c, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0059, code lost:
        if (r6._scanOnly != false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x005b, code lost:
        com.mxtech.StringUtils.appendHtmlEncoded(r6.b, r7, r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void process_l(String source, int flags) {
        int end;
        int last = 0;
        int len = source.length();
        this._scanOnly = (flags & 1) != 0;
        while (true) {
            int start = source.indexOf(60, last);
            if (start < 0) {
                break;
            }
            if (!this._scanOnly) {
                StringUtils.appendHtmlEncoded(this.b, source, last, start);
            }
            last = start;
            if (start + 1 < len) {
                if (source.charAt(start + 1) == '/') {
                    if (start + 2 < len && (end = processTags(source, start, start + 2)) > 0) {
                        if (!this._scanOnly) {
                            last = end;
                        } else {
                            return;
                        }
                    }
                } else {
                    int end2 = processTags(source, start, start + 1);
                    if (end2 > 0) {
                        if (!this._scanOnly) {
                            last = end2;
                        } else {
                            return;
                        }
                    }
                }
            }
            int end3 = source.indexOf(62, last);
            if (end3 < 0) {
                break;
            }
            if ((flags & 3) == 0) {
                StringUtils.appendHtmlEncoded(this.b, source, last, end3 + 1);
            }
            last = end3 + 1;
        }
    }

    private int processTags(String source, int bracketIndex, int tagIndex) {
        switch (source.charAt(tagIndex)) {
            case 'A':
            case 'a':
                int processed = processTag(source, bracketIndex, tagIndex, "a");
                if (processed > 0) {
                    return processed;
                }
                break;
            case 'B':
            case 'b':
                int processed2 = processTag(source, bracketIndex, tagIndex, "b");
                if (processed2 > 0) {
                    return processed2;
                }
                int processed3 = processTag(source, bracketIndex, tagIndex, "big");
                if (processed3 > 0) {
                    return processed3;
                }
                int processed4 = processTag(source, bracketIndex, tagIndex, "blockquote");
                if (processed4 > 0) {
                    return processed4;
                }
                break;
            case 'C':
            case 'c':
                int processed5 = processTag(source, bracketIndex, tagIndex, "cite");
                if (processed5 > 0) {
                    return processed5;
                }
                break;
            case 'D':
            case 'd':
                int processed6 = processTag(source, bracketIndex, tagIndex, "dfn");
                if (processed6 > 0) {
                    return processed6;
                }
                break;
            case 'E':
            case 'e':
                int processed7 = processTag(source, bracketIndex, tagIndex, "em");
                if (processed7 > 0) {
                    return processed7;
                }
                break;
            case 'F':
            case 'f':
                int processed8 = processTag(source, bracketIndex, tagIndex, "font");
                if (processed8 > 0) {
                    return processed8;
                }
                break;
            case 'H':
            case 'h':
                int processed9 = processTag(source, bracketIndex, tagIndex, "h1");
                if (processed9 > 0) {
                    return processed9;
                }
                int processed10 = processTag(source, bracketIndex, tagIndex, "h2");
                if (processed10 > 0) {
                    return processed10;
                }
                int processed11 = processTag(source, bracketIndex, tagIndex, "h3");
                if (processed11 > 0) {
                    return processed11;
                }
                int processed12 = processTag(source, bracketIndex, tagIndex, "h4");
                if (processed12 > 0) {
                    return processed12;
                }
                int processed13 = processTag(source, bracketIndex, tagIndex, "h5");
                if (processed13 > 0) {
                    return processed13;
                }
                int processed14 = processTag(source, bracketIndex, tagIndex, "h6");
                if (processed14 > 0) {
                    return processed14;
                }
                break;
            case 'I':
            case 'i':
                int processed15 = processTag(source, bracketIndex, tagIndex, "i");
                if (processed15 > 0) {
                    return processed15;
                }
                break;
            case 'R':
            case 'r':
                int processed16 = processTag(source, bracketIndex, tagIndex, "ruby");
                if (processed16 > 0) {
                    return processed16;
                }
                int processed17 = processTag(source, bracketIndex, tagIndex, "rt");
                if (processed17 > 0) {
                    return processed17;
                }
                break;
            case 'S':
            case 's':
                int processed18 = processTag(source, bracketIndex, tagIndex, "strong");
                if (processed18 > 0) {
                    return processed18;
                }
                int processed19 = processTag(source, bracketIndex, tagIndex, "small");
                if (processed19 > 0) {
                    return processed19;
                }
                int processed20 = processTag(source, bracketIndex, tagIndex, "sup");
                if (processed20 > 0) {
                    return processed20;
                }
                int processed21 = processTag(source, bracketIndex, tagIndex, "sub");
                if (processed21 > 0) {
                    return processed21;
                }
                break;
            case 'T':
            case 't':
                int processed22 = processTag(source, bracketIndex, tagIndex, "tt");
                if (processed22 > 0) {
                    return processed22;
                }
                break;
            case 'U':
            case 'u':
                int processed23 = processTag(source, bracketIndex, tagIndex, "u");
                if (processed23 > 0) {
                    return processed23;
                }
                break;
        }
        return -1;
    }

    private int processTag(String source, int bracketIndex, int tagIndex, String tag) {
        int closing_bracket_index;
        int tag_len = tag.length();
        if (source.regionMatches(true, tagIndex, tag, 0, tag_len) && source.length() > tagIndex + tag_len) {
            char char_after_tag = source.charAt(tagIndex + tag_len);
            if (char_after_tag == '>') {
                if (this._scanOnly) {
                    this.containsHtmlTag = true;
                } else {
                    this.b.append((CharSequence) source, bracketIndex, tagIndex + tag_len + 1);
                }
                return tagIndex + tag_len + 1;
            }
            if (Character.isWhitespace(char_after_tag) && (closing_bracket_index = source.indexOf(62, tagIndex + tag_len + 1)) >= 0) {
                if (this._scanOnly) {
                    this.containsHtmlTag = true;
                } else {
                    this.b.append((CharSequence) source, bracketIndex, closing_bracket_index + 1);
                }
                return closing_bracket_index + 1;
            }
            return -1;
        }
        return -1;
    }
}
