package com.mxtech.videoplayer.subtitle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;
import com.mxtech.subtitle.DrawableFrame;
import com.mxtech.subtitle.DrawableFrame2;
import com.mxtech.subtitle.Frame;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import java.util.List;

/* loaded from: classes.dex */
public class SubtitleOverlay extends ViewSwitcher implements Animation.AnimationListener {
    public static final int ANIM_FADE_OUT = 1;
    public static final int ANIM_NONE = 0;
    public static final int ANIM_SLIDE_LEFT = 2;
    public static final int ANIM_SLIDE_RIGHT = 3;
    public static final String TAG = ISubtitle.TAG + ".Overlay";
    private Animation _animFadeOut;
    private Animation _animSlideInLeft;
    private Animation _animSlideInRight;
    private Animation _animSlideOutLeft;
    private Animation _animSlideOutRight;
    private Listener _listener;
    private float _scale;
    private FrameBuffer _sharedBuffer;
    private boolean _sharedBufferEnabled;
    private int _videoHeight;
    private int _videoWidth;

    /* loaded from: classes.dex */
    public interface Listener {
        void onFrameAnimationEnd(SubtitleOverlay subtitleOverlay);

        void onSizeChanged(SubtitleOverlay subtitleOverlay, int i, int i2);
    }

    public SubtitleOverlay(Context context) {
        super(context);
        this._scale = 1.0f;
        ViewGroup.LayoutParams l = new ViewGroup.LayoutParams(-1, -1);
        addView(new RenderView(context), l);
        addView(new RenderView(context), l);
        setEnableFadeOut(P.getSubtitleFadeout());
    }

    public void setListener(Listener listener) {
        this._listener = listener;
    }

    public void setEnableFadeOut(boolean enable) {
        if (enable) {
            this._animFadeOut = AnimationUtils.loadAnimation(getContext(), 17432577);
            this._animFadeOut.setAnimationListener(this);
        } else {
            this._animFadeOut = null;
        }
        setOutAnimation(this._animFadeOut);
    }

    public void setVideoSize(int width, int height) {
        if (this._videoWidth != width || this._videoHeight != height) {
            this._videoWidth = width;
            this._videoHeight = height;
            for (int i = 0; i < 2; i++) {
                RenderView v = (RenderView) getChildAt(i);
                if (v.hasFrames()) {
                    v.updateBounds();
                    v.expireFrameCache();
                    v.invalidate();
                }
            }
        }
    }

    public void setFrameScale(float scale) {
        if (scale != this._scale) {
            this._scale = scale;
            for (int i = 0; i < 2; i++) {
                RenderView v = (RenderView) getChildAt(i);
                if (v.hasFrames()) {
                    v.updateBounds();
                    v.expireFrameCache();
                    v.invalidate();
                }
            }
        }
    }

    public void setRenderingComplex(boolean complex) {
        getChildAt(0).setDrawingCacheEnabled(complex);
        getChildAt(1).setDrawingCacheEnabled(complex);
    }

    public void clearFrames() {
        ((RenderView) getChildAt(0)).clearFrames();
        ((RenderView) getChildAt(1)).clearFrames();
    }

    @Override // android.view.ViewGroup, android.view.View
    @SuppressLint({"NewApi"})
    protected void onAttachedToWindow() {
        this._sharedBufferEnabled = Build.VERSION.SDK_INT < 11 || !isHardwareAccelerated();
        if (this._sharedBufferEnabled) {
            if (this._sharedBuffer == null) {
                this._sharedBuffer = new FrameBuffer();
            }
            this._sharedBuffer.updateBounds(getWidth(), getHeight());
        }
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this._sharedBuffer = null;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this._sharedBuffer != null) {
            this._sharedBuffer.updateBounds(w, h);
        }
        if (this._listener != null) {
            this._listener.onSizeChanged(this, w, h);
        }
    }

    public boolean setFrames(List<Frame> frames, int animation) {
        RenderView current = (RenderView) getCurrentView();
        if (frames != null || current.hasFrames()) {
            switch (animation) {
                case 0:
                    current.setFrames(frames);
                    break;
                case 1:
                    ((RenderView) getNextView()).setFrames(frames);
                    showNext();
                    break;
                case 2:
                    ((RenderView) getNextView()).setFrames(frames);
                    if (this._animSlideInRight == null) {
                        this._animSlideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                        this._animSlideInRight.setAnimationListener(this);
                    }
                    if (this._animSlideOutLeft == null) {
                        this._animSlideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
                    }
                    setInAnimation(this._animSlideInRight);
                    setOutAnimation(this._animSlideOutLeft);
                    showNext();
                    setInAnimation(null);
                    setOutAnimation(this._animFadeOut);
                    break;
                case 3:
                    ((RenderView) getNextView()).setFrames(frames);
                    if (this._animSlideInLeft == null) {
                        this._animSlideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                        this._animSlideInLeft.setAnimationListener(this);
                    }
                    if (this._animSlideOutRight == null) {
                        this._animSlideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
                    }
                    setInAnimation(this._animSlideInLeft);
                    setOutAnimation(this._animSlideOutRight);
                    showNext();
                    setInAnimation(null);
                    setOutAnimation(this._animFadeOut);
                    break;
                default:
                    Log.e(TAG, "Unknown animation code " + animation);
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override // android.widget.ViewAnimator
    public void showNext() {
        super.showNext();
        if (getOutAnimation() == null) {
            onAnimationEnd(null);
        }
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationStart(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationRepeat(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationEnd(Animation animation) {
        ((RenderView) getNextView()).clearFrames();
        if (this._listener != null) {
            this._listener.onFrameAnimationEnd(this);
        }
    }

    /* loaded from: classes2.dex */
    public class RenderView extends RelativeLayout {
        private FrameBuffer _frameBuffer;
        private List<Frame> _frames;
        private boolean _hasContent;

        public RenderView(Context context) {
            super(context);
            this._hasContent = false;
            setWillNotDraw(false);
        }

        public boolean hasFrames() {
            return this._frames != null;
        }

        public void clearFrames() {
            if (this._frames != null) {
                this._frames = null;
            }
            this._hasContent = false;
        }

        public void setFrames(List<Frame> frames) {
            if (this._frames != null) {
                this._frames = null;
            }
            if (frames != null) {
                this._frames = frames;
                updateBounds();
            }
            this._hasContent = false;
            invalidate();
        }

        public void expireFrameCache() {
            this._hasContent = false;
        }

        void updateBounds() {
            if (SubtitleOverlay.this._videoWidth != 0 && SubtitleOverlay.this._videoHeight != 0) {
                int width = getWidth();
                int height = getHeight();
                if (width != 0 && height != 0) {
                    int videoWidth = SubtitleOverlay.this._videoWidth > 0 ? SubtitleOverlay.this._videoWidth : width;
                    int videoHeight = SubtitleOverlay.this._videoHeight > 0 ? SubtitleOverlay.this._videoHeight : height;
                    for (Frame frame : this._frames) {
                        frame.updateBounds(width, height, videoWidth, videoHeight, SubtitleOverlay.this._scale);
                    }
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        @SuppressLint({"NewApi"})
        protected void onAttachedToWindow() {
            if (!SubtitleOverlay.this._sharedBufferEnabled) {
                if (this._frameBuffer == null) {
                    this._frameBuffer = new FrameBuffer();
                }
                this._frameBuffer.updateBounds(getWidth(), getHeight());
            }
            super.onAttachedToWindow();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this._frameBuffer != null) {
                this._frameBuffer = null;
                this._hasContent = false;
            }
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this._frames != null) {
                updateBounds();
            }
            if (this._frameBuffer != null) {
                this._frameBuffer.updateBounds(w, h);
                this._hasContent = false;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (SubtitleOverlay.this._videoWidth != 0 && SubtitleOverlay.this._videoHeight != 0 && this._frames != null) {
                FrameBuffer buffer = null;
                for (Frame frame : this._frames) {
                    if (frame instanceof DrawableFrame2) {
                        if (!this._hasContent) {
                            if (SubtitleOverlay.this._sharedBuffer != null) {
                                if (buffer == null) {
                                    SubtitleOverlay.this._sharedBuffer.eraseColor(0);
                                }
                                buffer = SubtitleOverlay.this._sharedBuffer;
                            } else if (this._frameBuffer != null) {
                                if (buffer == null) {
                                    this._frameBuffer.eraseColor(0);
                                }
                                buffer = this._frameBuffer;
                                this._hasContent = true;
                            }
                            Bitmap bitmap = buffer.getBitmap();
                            if (bitmap != null) {
                                ((DrawableFrame2) frame).draw(canvas, bitmap);
                            }
                        } else {
                            buffer = this._frameBuffer;
                        }
                    } else if (frame instanceof DrawableFrame) {
                        ((DrawableFrame) frame).draw(canvas);
                    }
                }
                if (buffer != null) {
                    buffer.draw(canvas);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private static class FrameBuffer {
        private Bitmap _bitmap;
        private int _canvasHeight;
        private int _canvasWidth;
        private boolean _outOfMemory;

        private FrameBuffer() {
        }

        void updateBounds(int canvasWidth, int canvasHeight) {
            this._canvasWidth = canvasWidth;
            this._canvasHeight = canvasHeight;
            if (this._bitmap != null) {
                if (this._bitmap.getWidth() < canvasWidth || this._bitmap.getHeight() < canvasHeight) {
                    this._bitmap = null;
                    this._outOfMemory = false;
                }
            }
        }

        void draw(Canvas canvas) {
            if (this._bitmap != null) {
                canvas.drawBitmap(this._bitmap, 0.0f, 0.0f, (Paint) null);
            }
        }

        void eraseColor(int color) {
            if (this._bitmap != null) {
                this._bitmap.eraseColor(color);
            }
        }

        Bitmap getBitmap() {
            if (this._bitmap == null && !this._outOfMemory && this._canvasWidth > 8 && this._canvasHeight > 8) {
                try {
                    this._bitmap = Bitmap.createBitmap(this._canvasWidth, this._canvasHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    Log.e(SubtitleOverlay.TAG, "Can't create frame buffer. Size:" + this._canvasWidth + " x " + this._canvasHeight, e);
                    this._outOfMemory = true;
                }
            }
            return this._bitmap;
        }
    }
}
