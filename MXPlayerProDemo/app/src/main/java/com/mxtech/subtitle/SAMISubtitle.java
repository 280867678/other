package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.collection.SeekableMap;
import com.mxtech.text.HtmlEx;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class SAMISubtitle implements ISubtitle {
    private static final char[] HTML_CHARACTERS = {'<', '&'};
    public static final String TYPENAME = "SAMI";
    private final Locale _locale;
    private final String _name;
    private final SeekableMap _texts = new SeekableMap(-1, Integer.MAX_VALUE);
    private final Uri _uri;
    final String clazz;

    public SAMISubtitle(int index, Uri source, String name, String localeText, String clazz) {
        this.clazz = clazz;
        if (localeText == null || localeText.equalsIgnoreCase(LocaleUtils.UNDEFINED)) {
            String clazzLo = clazz.toLowerCase(Locale.US);
            if (clazzLo.contains("krcc")) {
                this._locale = Locale.KOREAN;
            } else if (clazzLo.contains("encc")) {
                this._locale = Locale.ENGLISH;
            } else if (clazzLo.contains("jpcc")) {
                this._locale = Locale.JAPANESE;
            } else {
                this._locale = null;
            }
        } else {
            this._locale = LocaleUtils.parse(localeText);
        }
        if ((name == null || name.length() == 0 || name.equalsIgnoreCase("unknown")) && (this._locale == null || (name = this._locale.getDisplayName()) == null || name.length() <= 0)) {
            if (clazz.length() > 0) {
                name = clazz;
            } else {
                name = StringUtils.getString_s(R.string.name_by_track, Integer.valueOf(index + 1));
            }
        }
        this._name = name;
        this._uri = Uri.parse(source.toString() + '#' + Uri.encode(clazz));
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void close() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void append(int begin, String text) {
        String old = (String) this._texts.put(begin, text);
        if (old != null) {
            if (text != null) {
                L.sb.setLength(0);
                this._texts.put(begin, L.sb.append(old).append("<br>").append(text).toString());
                return;
            }
            this._texts.put(begin, old);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this._texts.isEmpty();
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int flags() {
        return 2228224;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int priority() {
        return 4;
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
        String text;
        int begin = this._texts.begin();
        if (begin >= 0 && (text = (String) this._texts.get(begin)) != null) {
            if (StringUtils.indexAnyOf(text, HTML_CHARACTERS) >= 0) {
                return HtmlEx.fromHtml(text, (flags & 256) != 0 ? 0 : 1);
            }
            return text;
        }
        return null;
    }
}
