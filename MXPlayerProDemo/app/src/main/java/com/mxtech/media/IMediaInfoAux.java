package com.mxtech.media;

/* loaded from: classes.dex */
public interface IMediaInfoAux {
    int frameTime();

    int getStreamCount();

    int[] getStreamTypes();

    boolean hasEmbeddedSubtitle();

    IStreamInfo streamInfo(int i);
}
