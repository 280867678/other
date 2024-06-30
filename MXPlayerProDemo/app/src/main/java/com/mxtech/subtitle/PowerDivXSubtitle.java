package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.StringLineReader;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.parser.TimeParser;
import com.mxtech.subtitle.MicroDVDStylizer;

/* loaded from: classes2.dex */
public final class PowerDivXSubtitle extends TextSubtitle {
    private static final int LINE_TOLERANCE = 5;
    public static final String TYPENAME = "PowerDivX";

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        MicroDVDStylizer.LineParser parser = new MicroDVDStylizer.LineParser(new TimeParser(), '{', '}');
        int lineCount = 0;
        SeekableRangeMap<String> texts = newTextCollection();
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (parser.parse(line)) {
                    texts.putRange(parser.startTimecode, parser.endTimecode, parser.text);
                } else if (texts.isEmpty() && (lineCount = lineCount + 1) > 5) {
                    return null;
                }
            } else if (texts.isEmpty()) {
                return null;
            } else {
                return new ISubtitle[]{new PowerDivXSubtitle(uri, name, client, texts)};
            }
        }
    }

    private PowerDivXSubtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
        super(uri, name, client, texts);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.TextSubtitle
    protected CharSequence stylize(String source, int flags) {
        return MicroDVDStylizer.stylize(source, flags);
    }
}
