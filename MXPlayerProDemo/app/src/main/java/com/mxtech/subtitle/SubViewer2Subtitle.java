package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.StringLineReader;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.parser.TimeParser;
import com.mxtech.text.HtmlEx;

/* loaded from: classes2.dex */
public final class SubViewer2Subtitle extends TextSubtitle {
    private static final String[] LINE_BREAKES = {"|", "[br]"};
    private static final String[] LINE_BREAKES_HTML = {"\n", "|", "[br]"};
    private static final int LINE_TOLERANCE = 5;
    private static final int STATE_CAPTION = 4;
    private static final int STATE_INFORMATION = 1;
    private static final int STATE_OUTER_SECTION = 0;
    private static final int STATE_TIMELINE = 3;
    public static final String TYPENAME = "SubViewer 2";
    private final HtmlStyledTextPreprocessor _htmlPreprocessor;

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        int close;
        boolean formatVerified = false;
        int unverifiedLineCount = 0;
        int state = 0;
        int begin = 0;
        int end = 0;
        int lineStart = 0;
        int lineEnd = 0;
        int delay = 0;
        int frameTime = client.frameTime();
        SeekableRangeMap<String> texts = TextSubtitle.newTextCollection();
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (!formatVerified) {
                    unverifiedLineCount++;
                    if (unverifiedLineCount > 5) {
                        return null;
                    }
                }
                String line2 = line.trim();
                int lineLen = line2.length();
                switch (state) {
                    case 0:
                    case 1:
                        if (lineLen > 2 && line2.charAt(0) == '[' && (close = line2.indexOf(93, 1)) > 0) {
                            String section = line2.substring(1, close);
                            if (state == 0) {
                                if ("INFORMATION".equalsIgnoreCase(section)) {
                                    formatVerified = true;
                                    state = 1;
                                    break;
                                } else if (!"SUBTITLE".equalsIgnoreCase(section)) {
                                    break;
                                } else {
                                    formatVerified = true;
                                    state = 3;
                                    break;
                                }
                            } else if ("DELAY".equalsIgnoreCase(section)) {
                                try {
                                    int delayInFrames = Integer.parseInt(line2.substring(close + 1).trim());
                                    delay = (int) ((delayInFrames * frameTime) / 1000000);
                                    break;
                                } catch (NumberFormatException e) {
                                    break;
                                }
                            } else if (!"END INFORMATION".equalsIgnoreCase(section)) {
                                break;
                            } else {
                                state = 0;
                                break;
                            }
                        }
                        break;
                    case 3:
                        if (lineLen > 0 && line2.charAt(0) == '[') {
                            break;
                        } else {
                            int comma = line2.indexOf(44);
                            if (comma > 0 && comma + 1 < lineLen) {
                                try {
                                    begin = TimeParser.parse(line2.substring(0, comma).trim());
                                    end = TimeParser.parse(line2.substring(comma + 1).trim());
                                    state = 4;
                                    lineStart = reader.nextPosition();
                                    lineEnd = lineStart;
                                    break;
                                } catch (NumberFormatException e2) {
                                    break;
                                }
                            }
                        }
                        break;
                    case 4:
                        if (lineLen == 0) {
                            if (lineStart < lineEnd) {
                                texts.putRange(begin + delay, end + delay, text.substring(lineStart, lineEnd).trim());
                            }
                            state = 3;
                            break;
                        } else {
                            lineEnd = reader.nextPosition();
                            break;
                        }
                }
            } else {
                if (state == 4 && lineStart < lineEnd) {
                    texts.putRange(begin + delay, end + delay, text.substring(lineStart, lineEnd).trim());
                }
                if (texts.isEmpty()) {
                    return null;
                }
                return new ISubtitle[]{new SubViewer2Subtitle(uri, name, client, texts)};
            }
        }
    }

    private SubViewer2Subtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
        super(uri, name, client, texts);
        this._htmlPreprocessor = new HtmlStyledTextPreprocessor();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.TextSubtitle
    protected CharSequence stylize(String source, int flags) {
        String source2 = this._htmlPreprocessor.process(source);
        if (this._htmlPreprocessor.containsHtmlTag) {
            String html = SubtitleUtils.replaceLineBreaksWith(source2, LINE_BREAKES_HTML, "<br/>");
            return HtmlEx.fromHtml(html, (flags & 256) != 0 ? 0 : 1);
        }
        return SubtitleUtils.replaceLineBreaksWith(source2, LINE_BREAKES, "\n");
    }
}
