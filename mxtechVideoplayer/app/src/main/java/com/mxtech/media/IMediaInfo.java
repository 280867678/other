package com.mxtech.media;

import java.util.Locale;

/* loaded from: classes.dex */
public interface IMediaInfo {
    String album();

    String albumArtist();

    String artist();

    void close();

    String composer();

    String copyright();

    String description();

    int displayHeight();

    String displayLocales();

    int displayWidth();

    int duration();

    String encoded_by();

    String encoder();

    String format();

    String genre();

    int height();

    Locale[] locales();

    String mimeType();

    String performer();

    String publisher();

    String title();

    int width();

    String year();
}
