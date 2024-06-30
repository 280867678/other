package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.text.HtmlEx;

/* loaded from: classes2.dex */
public final class WebVTTSubtitleBasic extends TextSubtitle {
    public static final String TYPENAME = "WebVTT";
    public static final String TAG = TextSubtitle.TAG + ".WebVTT.Basic";
    private static final HtmlStyledTextPreprocessor _htmlPreprocessor = new HtmlStyledTextPreprocessor();
    private static final String[] LINE_BREAKES = {"\n"};

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        if (text.regionMatches(true, 0, "WEBVTT", 0, 6)) {
            SeekableRangeMap<String> texts = newTextCollection();
            SubRipSubtitle.parse(text, texts);
            if (texts.isEmpty()) {
                return null;
            }
            return new ISubtitle[]{new WebVTTSubtitleBasic(uri, name, client, texts)};
        }
        return null;
    }

    private WebVTTSubtitleBasic(Uri uri, String name, ISubtitleClient source, SeekableRangeMap<String> texts) {
        super(uri, name, source, texts);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    public static CharSequence stylizeText(String source, int flags) {
        String html = SubtitleUtils.replaceLineBreaksWith(_htmlPreprocessor.processStrict(source), LINE_BREAKES, "<br/>");
        return HtmlEx.fromHtml(html, (flags & 256) != 0 ? 0 : 1);
    }

    @Override // com.mxtech.subtitle.TextSubtitle
    protected CharSequence stylize(String source, int flags) {
        return stylizeText(source, flags);
    }
}
