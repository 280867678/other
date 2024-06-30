package com.mxtech.videoplayer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import com.mxtech.ImageUtils;

/* loaded from: classes.dex */
public class ThumbDrawable extends BitmapDrawable {
    private static final String TAG = App.TAG + ".ThumbDrawable";
    public final boolean hasDuration;
    public final boolean hasThumb;
    @Nullable
    public Bitmap source;

    public ThumbDrawable(Resources res, Bitmap shaped, @Nullable Bitmap source, boolean hasThumb, boolean hasTime) {
        super(res, shaped);
        this.source = source;
        this.hasThumb = hasThumb;
        this.hasDuration = hasTime;
    }

    public int getSize() {
        int size = ImageUtils.getSize(getBitmap());
        if (this.source != null) {
            return size + ImageUtils.getSize(this.source);
        }
        return size;
    }

    public void shrink() {
        this.source = null;
    }

    public String toString() {
        return super.toString() + " (thumb: " + (this.hasThumb ? 'O' : 'X') + ", duration: " + (this.hasDuration ? 'O' : 'X') + ")";
    }
}
