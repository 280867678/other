package com.mxtech.app.dele;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;

import com.mxtech.ImageUtils;
import com.mxtech.NumericUtils;
import com.mxtech.videoplayer.App;

public final class MediaMetadataSafeRetriever extends MediaMetadataRetriever {
    static final int EXTRACT_FIRST_BEGIN = 10000000;
    static final int EXTRACT_FIRST_END = 30000000;
    private static final int METADATA_KEY_V9_FIRST = 11;
    static final long THUMB_MAX_EXTRACT_GAP = 10000000;

    @Override // android.media.MediaMetadataRetriever
    public String extractMetadata(int keyCode) {
        if (Build.VERSION.SDK_INT >= 9 || keyCode < 11) {
            try {
                return super.extractMetadata(keyCode);
            } catch (NoSuchMethodError e) {
                Log.w(App.TAG, "", e);
                return null;
            }
        }
        return null;
    }

    public Bitmap extractFrame() {
        try {
            return getFrameAtTime();
        } catch (NoSuchMethodError e) {
            Log.w(App.TAG, "", e);
            return null;
        }
    }

    @SuppressLint("WrongConstant")
    public Bitmap extractThumbnail(int width, int height, int iteration, boolean allowBlanc) throws Exception {
        Bitmap bitmap;
        try {
            String durationStr = super.extractMetadata(9);
            if (durationStr == null) {
                return null;
            }
            long durationUS = Integer.parseInt(durationStr) * 1000;
            if (durationUS <= 0) {
                return null;
            }
            long time = NumericUtils.ThreadLocalRandom.get().nextInt(20000000) + EXTRACT_FIRST_BEGIN;
            if (time > durationUS) {
                time = 0;
            }
            long gap = durationUS / iteration;
            if (gap > THUMB_MAX_EXTRACT_GAP) {
                gap = THUMB_MAX_EXTRACT_GAP;
            }
            int i = 0;
            while (i < iteration && (bitmap = super.getFrameAtTime(time, 2)) != null) {
                if ((allowBlanc && i == iteration - 1) || !ImageUtils.isBitmapBlanc(bitmap)) {
                    return extractThumbnail(bitmap, width, height, 2);
                }
                bitmap.recycle();
                i++;
                time += gap;
            }
            return null;
        } catch (NoSuchMethodError e) {
            Log.w(App.TAG, "", e);
        }
        return null;
    }







    public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
        Bitmap extractThumbnail;
        try {
            if (Build.VERSION.SDK_INT < 8) {
                extractThumbnail = extractMiniThumb(source, width, height, (options & 2) != 0);
            } else {
                extractThumbnail = ThumbnailUtils.extractThumbnail(source, width, height, options);
            }
            return extractThumbnail;
        } catch (NoSuchMethodError e) {
            Log.w(App.TAG, "", e);
            return null;
        }
    }

    public static native Bitmap extractMiniThumb(Bitmap bitmap, int i, int i2, boolean z);

}

