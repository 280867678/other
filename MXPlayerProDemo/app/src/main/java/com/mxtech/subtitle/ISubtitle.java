package com.mxtech.subtitle;

import android.net.Uri;
import com.mxtech.videoplayer.App;
import java.util.Comparator;
import java.util.Locale;

/* loaded from: classes.dex */
public interface ISubtitle {
    public static final int CONTENT_FRAME = 4194304;
    public static final int CONTENT_NEED_RENDERING = 6291456;
    public static final int CONTENT_SUBSTATIONALPHA = 1048576;
    public static final int CONTENT_TEXT = 2097152;
    public static final int FLAG_AUTO_SELECTABLE = 131072;
    public static final int FLAG_EMBEDDED = 65536;
    public static final int FLAG_ITALIC_TAGGED = 1;
    public static final int FLAG_USE_RUBY_TAG = 256;
    public static final int PRIORITY_HIGH = 4;
    public static final int PRIORITY_HIGHER = 5;
    public static final int PRIORITY_HIGHEST = 6;
    public static final int PRIORITY_LOW = 2;
    public static final int PRIORITY_LOWEST = 1;
    public static final int PRIORITY_NORMAL = 3;
    public static final String TAG = App.TAG + ".Subtitle";
    public static final Comparator<ISubtitle> PRIORITY_COMPARATOR = new Comparator<ISubtitle>() { // from class: com.mxtech.subtitle.ISubtitle.1
        @Override // java.util.Comparator
        public int compare(ISubtitle l, ISubtitle r) {
            return r.priority() - l.priority();
        }
    };

    void close();

    Object content(int i);

    void enable(boolean z);

    int flags();

    boolean isRenderingComplex();

    boolean isSupported();

    Locale locale();

    String name();

    int next();

    int previous();

    int priority();

    void setTranslation(int i, double d);

    String typename();

    boolean update(int i);

    Uri uri();
}
