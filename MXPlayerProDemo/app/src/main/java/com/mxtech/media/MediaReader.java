package com.mxtech.media;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public class MediaReader {
    public static native void cancel(long j);

    public static native int displayHeight(long j);

    public static native int displayWidth(long j);

    public static native int duration(long j);

    public static native Bitmap extractThumb(long j, int i, int i2, int i3, boolean z);

    public static native int frameTime(long j);

    public static native String getFormat(long j, int i);

    public static native String getMetadata(long j, int i, String str);

    public static native int getStreamBitRate(long j, int i);

    public static native int getStreamChannelCount(long j, int i);

    public static native long getStreamChannelLayout(long j, int i);

    public static native String getStreamCodec(long j, int i, boolean z);

    public static native int getStreamCodecId(long j, int i);

    public static native int getStreamCount(long j);

    public static native int getStreamDisplayHeight(long j, int i);

    public static native int getStreamDisplayWidth(long j, int i);

    public static native int getStreamDisposition(long j, int i);

    public static native int getStreamFrameTime(long j, int i);

    public static native int getStreamHeight(long j, int i);

    public static native String getStreamMetadata(long j, int i, int i2, String str);

    public static native String getStreamProfile(long j, int i);

    public static native int getStreamSampleRate(long j, int i);

    public static native int getStreamType(long j, int i);

    public static native int[] getStreamTypes(long j);

    public static native int getStreamWidth(long j, int i);

    public static native boolean hasEmbeddedSubtitle(long j);

    public static native int height(long j);

    public static native int isInterlaced(long j);

    public static native long native_create(String str, boolean z) throws Exception;

    public static native void native_release(long j);

    public static native int rotation(long j);

    public static native int width(long j);

    public static String getStreamCodec(long context, int stream) {
        return getStreamCodec(context, stream, true);
    }
}
