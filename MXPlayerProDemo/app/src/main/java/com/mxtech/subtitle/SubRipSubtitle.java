package com.mxtech.subtitle;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.mxtech.StringLineReader;
import com.mxtech.StringUtils;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.parser.TimeParser;
import com.mxtech.text.HtmlEx;

/* loaded from: classes2.dex */
public final class SubRipSubtitle extends TextSubtitle {
    private static final int LINE_TOLERANCE = 12;
    private static final int STATE_CAPTION = 4;
    private static final int STATE_SEQUENCE = 2;
    private static final int STATE_TIMELINE = 3;
    public static final String TYPENAME = "SubRip";
    public static final String TAG = TextSubtitle.TAG + ".SubRip";
    private static final HtmlStyledTextPreprocessor _htmlPreprocessor = new HtmlStyledTextPreprocessor();
    private static final String[] LINE_BREAKES = {"|"};
    private static final String[] LINE_BREAKES_HTML = {"\n", "|"};

    @Nullable
    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        if (text.regionMatches(true, 0, "WEBVTT", 0, 6)) {
            return null;
        }
        SeekableRangeMap<String> texts = newTextCollection();
        parse(text, texts);
        if (texts.isEmpty()) {
            return null;
        }
        return new ISubtitle[]{new SubRipSubtitle(uri, name, client, texts)};
    }

    public static void parse(String text, SeekableRangeMap<String> texts) {
        boolean formatVerified = false;
        int unverifiedLineCount = 0;
        int state = 2;
        int begin = 0;
        int end = 0;
        int lineStart = 0;
        int lineEnd = 0;
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (formatVerified || (unverifiedLineCount = unverifiedLineCount + 1) <= 12) {
                    String line2 = line.trim();
                    int lineLen = line2.length();
                    switch (state) {
                        case 2:
                            if (lineLen == 0) {
                                continue;
                            } else if (line2.indexOf("-->") >= 0) {
                                break;
                            } else {
                                try {
                                    Integer.parseInt(line2);
                                    state = 3;
                                } catch (NumberFormatException e) {
                                }
                            }
                        case 4:
                            if (lineLen == 0) {
                                if (lineStart < lineEnd) {
                                    texts.putRange(begin, end, text.substring(lineStart, lineEnd).trim());
                                }
                                state = 2;
                            } else {
                                lineEnd = reader.nextPosition();
                                continue;
                            }
                    }
                    int arrow = line2.indexOf("-->");
                    if (arrow > 0 && arrow + 3 < lineLen) {
                        try {
                            begin = TimeParser.parse(line2.substring(0, arrow).trim());
                            String pastArrow = line2.substring(arrow + 3).trim();
                            int space = StringUtils.indexAnyOf(pastArrow, StringUtils.SPACE_CHARS);
                            if (space > 0) {
                                pastArrow = pastArrow.substring(0, space);
                            }
                            end = TimeParser.parse(pastArrow);
                            state = 4;
                            lineStart = reader.nextPosition();
                            lineEnd = lineStart;
                            formatVerified = true;
                        } catch (NumberFormatException e2) {
                        }
                    }
                } else {
                    return;
                }
            } else if (state == 4 && lineStart < lineEnd) {
                texts.putRange(begin, end, text.substring(lineStart, lineEnd).trim());
                return;
            } else {
                return;
            }
        }
    }

    private SubRipSubtitle(Uri uri, String name, ISubtitleClient source, SeekableRangeMap<String> texts) {
        super(uri, name, source, texts);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    public static CharSequence stylizeText(String source, int flags) {
        String source2 = _htmlPreprocessor.process(SubtitleUtils.removeSSATags(source));
        if (_htmlPreprocessor.containsHtmlTag) {
            String html = SubtitleUtils.replaceLineBreaksWith(source2, LINE_BREAKES_HTML, "<br/>");
            return HtmlEx.fromHtml(html, (flags & 256) != 0 ? 0 : 1);
        }
        return SubtitleUtils.replaceLineBreaksWith(source2, LINE_BREAKES, "\n");
    }

    @Override // com.mxtech.subtitle.TextSubtitle
    protected CharSequence stylize(String source, int flags) {
        return stylizeText(source, flags);
    }
}
