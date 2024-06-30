package com.mxtech.subtitle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import com.mxtech.subtitle.TextSubtitle;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class SubStationAlphaSubtitle implements ISubtitle {
    public static final String TYPENAME = "SubStation Alpha";
    private final ISubtitleClient _client;
    private int _lastSeekTarget = -1;
    private final Locale _locale;
    private final String _name;
    private final long _nativeContext;
    private final Uri _uri;

    private native boolean _update(int i);

    private native int getCurrentEventTime();

    private static native long native_create(String str, SubStationAlphaMedia subStationAlphaMedia, boolean z);

    private static native void native_destroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public native void renderFrame(Bitmap bitmap, int i);

    @Override // com.mxtech.subtitle.ISubtitle
    public native int next();

    @Override // com.mxtech.subtitle.ISubtitle
    public native int previous();

    @Override // com.mxtech.subtitle.ISubtitle
    public native void setTranslation(int i, double d);

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        return create(uri, type, name, text, client, false);
    }

    static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client, boolean useFFmpeg) {
        long nativeContext = native_create(text, client.getSubStationAlphaMedia(0, null), useFFmpeg);
        if (nativeContext == 0) {
            return null;
        }
        try {
            return new ISubtitle[]{new SubStationAlphaSubtitle(uri, name, client, nativeContext)};
        } catch (Throwable e) {
            native_destroy(nativeContext);
            throw new RuntimeException(e);
        }
    }

    private SubStationAlphaSubtitle(Uri uri, String name, ISubtitleClient client, long nativeContext) {
        this._uri = uri;
        this._client = client;
        this._nativeContext = nativeContext;
        TextSubtitle.AnalyzeResult analized = TextSubtitle.analyzeName(uri, client.mediaUri().getLastPathSegment());
        this._name = name == null ? analized.name : name;
        this._locale = analized.locale;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public void close() {
        native_destroy(this._nativeContext);
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public String typename() {
        return TYPENAME;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int flags() {
        return 5373952;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public int priority() {
        return 5;
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
    public void enable(boolean enabled) {
        if (enabled) {
            this._client.setupFonts(false);
        }
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public boolean update(int pos) {
        boolean changed = _update(pos);
        if (changed) {
            this._lastSeekTarget = pos;
        }
        return changed;
    }

    @Override // com.mxtech.subtitle.ISubtitle
    public Object content(int flags) {
        int pos;
        if (this._lastSeekTarget >= 0) {
            pos = this._lastSeekTarget;
        } else {
            pos = getCurrentEventTime();
            if (pos < 0) {
                return null;
            }
        }
        return new Frame(pos);
    }

    /* loaded from: classes2.dex */
    private class Frame implements DrawableFrame2 {
        private final int _pos;

        Frame(int pos) {
            this._pos = pos;
        }

        @Override // com.mxtech.subtitle.Frame
        public void updateBounds(int canvasWidth, int canvasHeight, int videoWidth, int videoHeight, float scale) {
        }

        @Override // com.mxtech.subtitle.DrawableFrame
        public void draw(Canvas canvas) {
        }

        @Override // com.mxtech.subtitle.DrawableFrame2
        public void draw(Canvas canvas, Bitmap bitmap) {
            SubStationAlphaSubtitle.this.renderFrame(bitmap, this._pos);
        }
    }
}
