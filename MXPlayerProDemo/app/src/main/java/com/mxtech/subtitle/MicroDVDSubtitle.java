package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.StringLineReader;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.subtitle.MicroDVDStylizer;

/* loaded from: classes2.dex */
public final class MicroDVDSubtitle extends TextSubtitle {
    private static final int LINE_TOLERANCE = 5;
    public static final String TYPENAME = "MicroDVD";

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        MicroDVDStylizer.LineParser parser = new MicroDVDStylizer.LineParser('{', '}');
        int lineCount = 0;
        SeekableRangeMap<String> texts = newTextCollection();
        long frameTime = client.frameTime();
        StringLineReader reader = new StringLineReader(text);
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (parser.parse(line)) {
                    int startTime = (int) ((parser.startTimecode * frameTime) / 1000000);
                    int endTime = (int) ((parser.endTimecode * frameTime) / 1000000);
                    texts.putRange(startTime, endTime, parser.text);
                } else if (texts.isEmpty() && (lineCount = lineCount + 1) > 5) {
                    return null;
                }
            } else if (texts.isEmpty()) {
                return null;
            } else {
                return new ISubtitle[]{new MicroDVDSubtitle(uri, name, client, texts)};
            }
        }
    }

    private MicroDVDSubtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
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
