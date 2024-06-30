package com.mxtech.subtitle;

import android.net.Uri;
import android.text.SpannableStringBuilder;
import com.mxtech.StringLineReader;
import com.mxtech.StringUtils;
import com.mxtech.collection.SeekableMap;
import com.mxtech.subtitle.TextSubtitle;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class TMPlayerSubtitle implements ISubtitle {
    private static final int LINE_TOLERANCE = 5;
    public static final String TYPENAME = "TMPlayer";
    private final Locale _locale;
    private final String _name;
    private final SeekableMap _texts;
    private final Uri _uri;

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        LineParser parser = new LineParser();
        int lineCount = 0;
        SeekableMap texts = new SeekableMap(-1, Integer.MAX_VALUE);
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (parser.parse(line)) {
                    texts.put(parser.seconds * 1000, parser.text.length() > 0 ? parser.text : null);
                } else if (texts.isEmpty() && (lineCount = lineCount + 1) > 5) {
                    return null;
                }
            } else if (texts.isEmpty()) {
                return null;
            } else {
                return new ISubtitle[]{new TMPlayerSubtitle(uri, name, client, texts)};
            }
        }
    }

    private TMPlayerSubtitle(Uri uri, String name, ISubtitleClient client, SeekableMap texts) {
        this._uri = uri;
        TextSubtitle.AnalyzeResult analized = TextSubtitle.analyzeName(uri, client.mediaUri().getLastPathSegment());
        this._name = name == null ? analized.name : name;
        this._locale = analized.locale;
        this._texts = texts;
    }

    /* loaded from: classes2.dex */
    private static class LineParser {
        private static final char[] TIME_TEXT_SEPARATOR = {':', '=', ','};
        public int seconds;
        public String text;

        private LineParser() {
        }

        public boolean parse(String source) {
            try {
                int end = source.indexOf(58);
                if (end < 0) {
                    return false;
                }
                int hour = Integer.parseInt(source.substring(0, end));
                int begin = end + 1;
                int end2 = source.indexOf(58, begin);
                if (end2 >= 0) {
                    int minute = Integer.parseInt(source.substring(begin, end2));
                    int begin2 = end2 + 1;
                    int end3 = StringUtils.indexAnyOf(source, TIME_TEXT_SEPARATOR, begin2);
                    if (end3 >= 0) {
                        int second = Integer.parseInt(source.substring(begin2, end3));
                        this.seconds = (hour * 60 * 60) + (minute * 60) + second;
                        this.text = source.substring(end3 + 1);
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

    @Override // com.mxtech.subtitle.ISubtitle
    public void close() {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int flags() {
        return 2228225;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int priority() {
        return 3;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean isSupported() {
        return true;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean isRenderingComplex() {
        return false;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public Uri uri() {
        return this._uri;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String name() {
        return this._name;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public Locale locale() {
        return this._locale;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void setTranslation(int sync, double speed) {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void enable(boolean enabled) {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean update(int pos) {
        return this._texts.seek(pos);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int previous() {
        return this._texts.previous();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int next() {
        return this._texts.next();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public Object content(int flags) {
        String text = (String) this._texts.get(this._texts.begin());
        if (text != null) {
            return PolishStylizer.stylize(SpannableStringBuilder.valueOf(MicroDVDStylizer.stylize(text, flags)));
        }
        return null;
    }
}
