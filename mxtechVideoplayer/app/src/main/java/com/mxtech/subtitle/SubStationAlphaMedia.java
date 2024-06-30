package com.mxtech.subtitle;

/* loaded from: classes.dex */
public final class SubStationAlphaMedia {
    private long _nativeContext;

    private native void native_create();

    private native void native_destroy();

    public native boolean isFontsSetup();

    public native void overrideFonts(String str);

    public native void setCanvasSize(int i, int i2);

    public native void setDirectRendering(boolean z);

    public native void setFontScale(float f);

    public native void setIgnoreFading(boolean z);

    public native void setVideoSize(int i, int i2);

    public native void setupFonts(String str);

    public SubStationAlphaMedia() {
        native_create();
    }

    private SubStationAlphaMedia(long nativeContext) {
        this._nativeContext = nativeContext;
    }

    protected void finalize() throws Throwable {
        native_destroy();
        super.finalize();
    }
}
