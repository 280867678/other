package com.mask.mediaprojection.interfaces;

import android.graphics.Bitmap;

/* loaded from: classes2.dex */
public abstract class ScreenCaptureCallback {
    public void onBitmap(Bitmap bitmap, boolean z) {
    }

    public void onFail() {
    }

    public void onSuccess(Bitmap bitmap) {
    }
}
