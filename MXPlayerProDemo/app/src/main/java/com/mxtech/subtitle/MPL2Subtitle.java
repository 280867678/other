package com.mxtech.subtitle;

import android.net.Uri;
import android.text.SpannableStringBuilder;
import com.mxtech.StringLineReader;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.subtitle.MicroDVDStylizer;

/* loaded from: classes2.dex */
public final class MPL2Subtitle extends TextSubtitle {
    private static final int LINE_TOLERANCE = 5;
    public static final String TYPENAME = "MPL2";

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        MicroDVDStylizer.LineParser parser = new MicroDVDStylizer.LineParser('[', ']');
        int lineCount = 0;
        SeekableRangeMap<String> texts = newTextCollection();
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (parser.parse(line)) {
                    texts.putRange(parser.startTimecode * 100, parser.endTimecode * 100, parser.text);
                } else if (texts.isEmpty() && (lineCount = lineCount + 1) > 5) {
                    return null;
                }
            } else if (texts.isEmpty()) {
                return null;
            } else {
                return new ISubtitle[]{new MPL2Subtitle(uri, name, client, texts)};
            }
        }
    }

    private MPL2Subtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
        super(uri, name, client, texts);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.TextSubtitle, com.mxtech.subtitle.ISubtitle
    public int flags() {
        return super.flags() | 1;
    }

    @Override // com.mxtech.subtitle.TextSubtitle
    protected CharSequence stylize(String source, int flags) {
        return PolishStylizer.stylize(SpannableStringBuilder.valueOf(MicroDVDStylizer.stylize(source, flags)));
    }
}
