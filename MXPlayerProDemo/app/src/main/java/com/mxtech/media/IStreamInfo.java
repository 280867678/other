package com.mxtech.media;

/* loaded from: classes2.dex */
public interface IStreamInfo extends IMediaInfo {
    int bitRate();

    int channelCount();

    long channelLayout();

    int disposition();

    int frameTime();

    boolean isValid();

    String profile();

    int sampleRate();

    int type();
}
