package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.StringLineReader;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.parser.IParser;
import com.mxtech.parser.NumberParser;
import com.mxtech.parser.TrimmingNumberParser;

/* loaded from: classes2.dex */
public final class PJSSubtitle extends TextSubtitle {
    private static final int LINE_TOLERANCE = 5;
    public static final String TYPENAME = "PJS";

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        LineParser parser = new LineParser(new TrimmingNumberParser(), ',');
        int lineCount = 0;
        SeekableRangeMap<String> texts = TextSubtitle.newTextCollection();
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (parser.parse(line)) {
                    String lineText = parser.text.trim();
                    if (lineText.length() >= 2 && lineText.charAt(0) == '\"' && lineText.charAt(lineText.length() - 1) == '\"') {
                        lineText = lineText.substring(1, lineText.length() - 1);
                    }
                    texts.putRange(parser.startTimecode * 100, parser.endTimecode * 100, lineText);
                } else if (texts.isEmpty() && (lineCount = lineCount + 1) > 5) {
                    return null;
                }
            } else if (texts.isEmpty()) {
                return null;
            } else {
                return new ISubtitle[]{new PJSSubtitle(uri, name, client, texts)};
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class LineParser {
        private final char delimiter;
        public int endTimecode;
        private final IParser parser;
        public int startTimecode;
        public String text;

        public LineParser(char delimiter) {
            this(new NumberParser(), delimiter);
        }

        public LineParser(IParser parser, char delimiter) {
            this.parser = parser;
            this.delimiter = delimiter;
        }

        public boolean parse(String line) {
            int endTime_end;
            int lineLen = line.length();
            if (lineLen <= 0) {
                return false;
            }
            try {
                int startTime_end = line.indexOf(this.delimiter);
                if (startTime_end < 0 || (endTime_end = line.indexOf(this.delimiter, startTime_end + 1)) < 0) {
                    return false;
                }
                this.startTimecode = this.parser.parseToInt(line.substring(0, startTime_end));
                this.endTimecode = this.parser.parseToInt(line.substring(startTime_end + 1, endTime_end));
                this.text = line.substring(endTime_end + 1);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public PJSSubtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
        super(uri, name, client, texts);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }
}
