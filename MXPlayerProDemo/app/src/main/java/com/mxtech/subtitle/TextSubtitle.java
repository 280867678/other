package com.mxtech.subtitle;

import android.net.Uri;
import android.text.SpannableStringBuilder;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.collection.SeekableRangeMap;
import com.mxtech.media.MediaUtils;
import java.util.Locale;

/* loaded from: classes2.dex */
public abstract class TextSubtitle implements ISubtitle {
    protected final Locale locale;
    protected final String name;
    protected final SeekableRangeMap<String> texts;
    protected final Uri uri;

    /* loaded from: classes2.dex */
    public static class AnalyzeResult {
        Locale locale;
        String name;
    }

    public static final AnalyzeResult analyzeName(Uri source, String mediaFileName) {
        AnalyzeResult result = new AnalyzeResult();
        String subtitleFileName = source.getLastPathSegment();
        if (subtitleFileName != null) {
            if (mediaFileName != null) {
                int subNameLen = subtitleFileName.length();
                int mediaDot = mediaFileName.lastIndexOf(46);
                if (mediaDot < 0) {
                    mediaDot = mediaFileName.length();
                }
                if (subNameLen >= mediaDot + 2 && mediaFileName.regionMatches(true, 0, subtitleFileName, 0, mediaDot) && subtitleFileName.charAt(mediaDot) == '.') {
                    int lastDot = subtitleFileName.lastIndexOf(46);
                    int secondDot = subtitleFileName.indexOf(46, mediaDot + 1);
                    if ((secondDot < 0 || secondDot == lastDot) && mediaDot != lastDot) {
                        String lang = subtitleFileName.substring(mediaDot + 1, lastDot);
                        int len = lang.length();
                        if (len == 2 || len == 3) {
                            result.locale = LocaleUtils.parse(lang);
                            if (result.locale.toString().length() > 0) {
                                result.name = result.locale.getDisplayLanguage();
                            }
                        }
                        result.locale = null;
                        result.name = StringUtils.capitalize(lang);
                    }
                }
            }
            int len2 = subtitleFileName.length();
            if (len2 > 4 && subtitleFileName.regionMatches(true, len2 - 4, ".IDX", 0, 4)) {
                subtitleFileName = subtitleFileName.substring(0, len2 - 4);
            }
            result.name = MediaUtils.getDecorativeName(subtitleFileName);
        } else {
            result.name = source.toString();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static SeekableRangeMap<String> newTextCollection() {
        return new SeekableRangeMap<>(-1, Integer.MAX_VALUE);
    }

    protected TextSubtitle(Uri uri, String name, ISubtitleClient source) {
        this(uri, name, source, newTextCollection());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TextSubtitle(Uri uri, String name, ISubtitleClient client, SeekableRangeMap<String> texts) {
        this.uri = uri;
        this.texts = texts;
        AnalyzeResult analyzed = analyzeName(uri, client.mediaUri().getLastPathSegment());
        this.name = name == null ? analyzed.name : name;
        this.locale = analyzed.locale;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void close() {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int flags() {
        return 2228224;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final int priority() {
        return 3;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final boolean isSupported() {
        return true;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final boolean isRenderingComplex() {
        return false;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final Uri uri() {
        return this.uri;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final String name() {
        return this.name;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final Locale locale() {
        return this.locale;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void setTranslation(int sync, double speed) {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final void enable(boolean enabled) {
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final boolean update(int pos) {
        return this.texts.seek(pos);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final int previous() {
        return this.texts.previous();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final int next() {
        return this.texts.next();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public final Object content(int flags) {
        Object v = this.texts.get(this.texts.begin());
        if (v != null) {
            if (v instanceof String) {
                return stylize((String) v, flags);
            }
            Object[] datas = (Object[]) v;
            SpannableStringBuilder b = SpannableStringBuilder.valueOf(stylize((String) datas[0], flags));
            for (int i = 1; i < datas.length; i++) {
                StringUtils.closeSpans(b);
                b.append('\n').append(stylize((String) datas[i], flags));
            }
            return b;
        }
        return null;
    }

    protected CharSequence stylize(String source, int flags) {
        return source;
    }
}
