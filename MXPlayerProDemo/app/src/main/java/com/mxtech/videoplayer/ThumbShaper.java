package com.mxtech.videoplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.Log;
import com.mxtech.DeviceUtils;
import java.io.File;

/* loaded from: classes2.dex */
public class ThumbShaper {
    public static String TAG = App.TAG + ".ThumbShaper";
    private static final int TEXT_BGCOLOR = -16777216;
    private static final int TEXT_COLOR = -3355444;
    private static final float TEXT_SIZE = 13.0f;
    private final int _canvasHeight;
    private final int _canvasWidth;
    private final Config _config;
    private final Handler _handler;
    private final Paint _textBgPaint;
    private final Rect _textBound = new Rect();
    private final TextPaint _textPaint;
    private HandlerThread _thread;
    private Handler _threadHandler;
    private final Rect _thumbDest;

    /* loaded from: classes2.dex */
    public static class Config {
        public int defaultHeight;
        public int defaultWidth;
        public int durationPaddingBottom;
        public int durationPaddingLeft;
        public int durationPaddingRight;
        public int durationPaddingTop;
        public Frame frame;
        public Typeface typeface;
    }

    /* loaded from: classes2.dex */
    public interface Frame {
        void draw(Canvas canvas);

        int getIntrinsicHeight();

        int getIntrinsicWidth();
    }

    /* loaded from: classes2.dex */
    public interface Receiver {
        void onCompleted(ThumbShaper thumbShaper);
    }

    /* loaded from: classes2.dex */
    public static class TranslucentFrame implements Frame {
        public Bitmap eraseMask;
        public Paint erasePaint;
        public Bitmap shadow;

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.eraseMask, 0.0f, 0.0f, this.erasePaint);
            canvas.drawBitmap(this.shadow, 0.0f, 0.0f, (Paint) null);
        }

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public int getIntrinsicWidth() {
            return this.eraseMask.getWidth();
        }

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public int getIntrinsicHeight() {
            return this.eraseMask.getHeight();
        }
    }

    /* loaded from: classes2.dex */
    public static class SolidFrame implements Frame {
        public Drawable overlay;

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public void draw(Canvas canvas) {
            this.overlay.draw(canvas);
        }

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public int getIntrinsicWidth() {
            return this.overlay.getIntrinsicWidth();
        }

        @Override // com.mxtech.videoplayer.ThumbShaper.Frame
        public int getIntrinsicHeight() {
            return this.overlay.getIntrinsicHeight();
        }
    }

    public ThumbShaper(Config config, Handler handler) {
        this._handler = handler;
        this._config = config;
        this._canvasWidth = Math.max(config.defaultWidth, config.frame != null ? config.frame.getIntrinsicWidth() : 0);
        this._canvasHeight = Math.max(config.defaultHeight, config.frame != null ? config.frame.getIntrinsicHeight() : 0);
        this._thumbDest = new Rect();
        this._thumbDest.left = (this._canvasWidth - config.defaultWidth) / 2;
        this._thumbDest.right = this._thumbDest.left + config.defaultWidth;
        this._thumbDest.top = (this._canvasHeight - config.defaultHeight) / 2;
        this._thumbDest.bottom = this._thumbDest.top + config.defaultHeight;
        this._textPaint = new TextPaint();
        this._textPaint.setTextSize(DeviceUtils.SPToPixel(TEXT_SIZE));
        this._textPaint.setColor(-3355444);
        this._textPaint.setAntiAlias(true);
        this._textPaint.setTypeface(config.typeface);
        this._textBgPaint = new Paint();
        this._textBgPaint.setColor(-16777216);
        if (config.frame instanceof SolidFrame) {
            ((SolidFrame) config.frame).overlay.setBounds(0, 0, this._canvasWidth, this._canvasHeight);
        }
    }

    public synchronized Bitmap build(@Nullable Bitmap thumb, @Nullable String durationText) {
        Bitmap output;
        output = Bitmap.createBitmap(this._canvasWidth, this._canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        if (thumb != null) {
            canvas.drawBitmap(thumb, (Rect) null, this._thumbDest, (Paint) null);
        } else {
            canvas.drawColor(-16777216, PorterDuff.Mode.SRC);
        }
        if (durationText != null) {
            this._textPaint.getTextBounds(durationText, 0, durationText.length(), this._textBound);
            int textOffsetX = this._textBound.left;
            int textOffsetY = this._textBound.top;
            int textX = (this._thumbDest.right - this._textBound.width()) - this._config.durationPaddingRight;
            int textY = (this._thumbDest.bottom - this._textBound.height()) - this._config.durationPaddingBottom;
            this._textBound.left = textX - this._config.durationPaddingLeft;
            this._textBound.top = textY - this._config.durationPaddingTop;
            this._textBound.right = this._thumbDest.right;
            this._textBound.bottom = this._thumbDest.bottom;
            canvas.drawRect(this._textBound, this._textBgPaint);
            canvas.drawText(durationText, textX - textOffsetX, textY - textOffsetY, this._textPaint);
        }
        if (this._config.frame != null) {
            this._config.frame.draw(canvas);
        }
        return output;
    }

    /* loaded from: classes2.dex */
    private class Param implements Runnable {
        static final int PHRASE_BUILD = 1;
        static final int PHRASE_RESULT = 2;
        final Receiver callback;
        @Nullable
        final File coverFile;
        @Nullable
        final String duration;
        int phrase = 1;
        @Nullable
        final Bitmap thumb;
        final Uri uri;

        Param(@Nullable Bitmap thumb, @Nullable String duration, Receiver callback, Uri uri, @Nullable File coverFile) {
            this.thumb = thumb;
            this.duration = duration;
            this.callback = callback;
            this.uri = uri;
            this.coverFile = coverFile;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.phrase == 1) {
                Bitmap processed = ThumbShaper.this.build(this.thumb, this.duration);
                boolean hasThumb = this.thumb != null;
                boolean hasDuration = this.duration != null;
                L.thumbCache.put(this.uri, new ThumbDrawable(App.context.getResources(), processed, this.thumb, hasThumb, hasDuration), this.coverFile);
                this.phrase = 2;
                ThumbShaper.this._handler.postAtTime(this, ThumbShaper.this, 0L);
                return;
            }
            this.callback.onCompleted(ThumbShaper.this);
        }
    }

    public void buildParallel(@Nullable Bitmap thumb, @Nullable String duration, Receiver callback, Uri uri, @Nullable File coverFile) {
        if (this._thread == null) {
            this._thread = new HandlerThread(TAG);
            this._thread.start();
            this._threadHandler = new Handler(this._thread.getLooper());
        }
        this._threadHandler.post(new Param(thumb, duration, callback, uri, coverFile));
    }

    public void cancelAllPendingRequests() {
        if (this._thread != null) {
            this._threadHandler.removeCallbacksAndMessages(null);
            this._handler.removeCallbacksAndMessages(this);
        }
    }

    public void closeParallel() {
        if (this._thread != null) {
            try {
                this._thread.quit();
                this._thread.interrupt();
                this._thread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "", e);
            }
            this._thread = null;
            this._threadHandler.removeCallbacksAndMessages(null);
            this._threadHandler = null;
            this._handler.removeCallbacksAndMessages(this);
        }
    }
}
