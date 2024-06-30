package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.location.places.Place;
import com.mxtech.DeviceUtils;
import com.mxtech.NumericUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

@SuppressLint({"ClickableViewAccessibility"})
/* loaded from: classes.dex */
public final class Lock extends Handler implements Animation.AnimationListener, View.OnTouchListener, View.OnClickListener, Thread.UncaughtExceptionHandler {
    public static final int FLAG_INACTIVATE_SYSTEM_UI = 256;
    public static final int FLAG_SHOW_TOAST = 128;
    public static final int INPUT_KEY = 2;
    public static final int INPUT_SYSTEM_UI = 1;
    public static final int INPUT_TOUCH = 3;
    public static final int MODE_KIDS = 1;
    public static final int MODE_KIDS_WITH_EFFECT = 2;
    public static final int MODE_NORMAL = 0;
    private static final int MSG_HIDE = 0;
    private static final float NEAR_DISTANCE = 20.0f;
    private static final float ROTATION_DELTA = 8.0f;
    private int _activeVisibility;
    private ImageView _button;
    private Thread.UncaughtExceptionHandler _defaultUEH;
    private Animation _fadeOut;
    private int _inactiveVisibility;
    private View _layout;
    private Listener _listener;
    private int _lockMode;
    private int _mode;
    private Toast _toast;
    private WindowManager _windowManager;
    public static final String TAG = App.TAG + ".Lock";
    private static final String[] MODELS = {"trace_car", "trace_airplane", "trace_butterfly", "trace_clover", "trace_bird", "trace_rabbit", "trace_snail", "trace_dog", "trace_fish", "trace_cat", "trace_dog_footprint"};
    private static final float UNLOCK_CORNER = 70.0f;
    private static final float UNLOCK_CORNER_PX = DeviceUtils.DIPToPixel(UNLOCK_CORNER);
    private static final float NEAR_DISTANCE_SQUARE_PX = DeviceUtils.DIPToPixel(400.0f);

    /* loaded from: classes.dex */
    public interface Listener {
        boolean onKeyLocked(Lock lock, KeyEvent keyEvent);

        boolean onTouch(Lock lock, View view, MotionEvent motionEvent);

        void onUnlocked(Lock lock);

        void onWindowFocusChanged(Lock lock, boolean z);

        void setSystemUiVisibility(int i);
    }

    public void setListener(Listener listener) {
        this._listener = listener;
    }

    public boolean isLocked() {
        return this._layout != null;
    }

    @SuppressLint({"ShowToast", "NewApi"})
    public void lock(final Activity context, boolean plus, int topMargin, boolean turnOffBacklight, boolean infoViewOnTouched) {
        int type;
        if (!isLocked()) {
            Log.d(TAG, "LOCK " + this);
            this._windowManager = (WindowManager) context.getSystemService("window");
            if (DeviceUtils.hasTouchScreen) {
                this._mode = App.prefs.getInt(Key.LOCK_MODE, 0);
            } else {
                this._mode = 0;
            }
            if (isKidsLock()) {
                try {
                    Resources kidsLockResources = context.getResources();
                    this._layout = new KidsView(context, kidsLockResources, this._mode == 2);
                } catch (Exception e) {
                    this._mode = 0;
                }
            }
            WindowManager.LayoutParams p = new WindowManager.LayoutParams();
            View decorView = context.getWindow().getDecorView();
            if (P.uiVersion >= 11) {
                if (P.softButtonsVisibility == 1) {
                    this._inactiveVisibility = 4611;
                    if (infoViewOnTouched) {
                        this._activeVisibility = 4610;
                    } else {
                        this._activeVisibility = this._inactiveVisibility;
                    }
                    if (P.getFullScreen() != 1) {
                        if (infoViewOnTouched) {
                            this._activeVisibility |= 1024;
                        } else {
                            this._activeVisibility |= Place.TYPE_SUBPREMISE;
                        }
                        this._inactiveVisibility |= Place.TYPE_SUBPREMISE;
                    }
                } else if (P.softButtonsVisibility == 2) {
                    if (P.uiVersion >= 16) {
                        if (P.uiVersion >= 19) {
                            this._inactiveVisibility = 2051;
                            if (infoViewOnTouched) {
                                this._activeVisibility = 512;
                            } else {
                                this._activeVisibility = this._inactiveVisibility;
                            }
                        } else {
                            if (infoViewOnTouched) {
                                this._activeVisibility = 512;
                            } else {
                                this._activeVisibility = 513;
                            }
                            this._inactiveVisibility = 3;
                        }
                        if (P.getFullScreen() != 1) {
                            if (infoViewOnTouched) {
                                this._activeVisibility |= 1024;
                            } else {
                                this._activeVisibility |= 4;
                            }
                            this._inactiveVisibility |= 4;
                        }
                    } else {
                        this._activeVisibility = 1;
                        this._inactiveVisibility = 2;
                    }
                } else if (P.oldTablet && App.prefs.getBoolean(Key.STATUS_BAR_SHOW_ALWAYS, false)) {
                    this._inactiveVisibility = 0;
                    this._activeVisibility = 0;
                } else if (P.uiVersion >= 16) {
                    if (P.getFullScreen() != 1) {
                        if (infoViewOnTouched) {
                            this._activeVisibility = 1024;
                        } else {
                            this._activeVisibility = 5;
                        }
                        this._inactiveVisibility = 5;
                    } else {
                        if (infoViewOnTouched) {
                            this._activeVisibility = 0;
                        } else {
                            this._activeVisibility = 1;
                        }
                        this._inactiveVisibility = 1;
                    }
                } else {
                    if (P.oldTablet && infoViewOnTouched) {
                        this._activeVisibility = 0;
                    } else {
                        this._activeVisibility = 1;
                    }
                    this._inactiveVisibility = 1;
                }
                p.systemUiVisibility = this._activeVisibility;
            }
            if (P.uiVersion < 16) {
                p.flags |= 1024;
            }
            p.token = decorView.getWindowToken();
            p.packageName = context.getPackageName();
            p.screenBrightness = context.getWindow().getAttributes().screenBrightness;
            if (turnOffBacklight) {
                p.buttonBrightness = 0.0f;
            }
            if (isKidsLock()) {
                this._toast = Toast.makeText(context, R.string.kids_lock_summary, 0);
                p.flags |= ISubtitle.CONTENT_FRAME;
            } else {
                this._layout = new Layout(context);
                context.getLayoutInflater().inflate(R.layout.lock, (ViewGroup) this._layout);
                this._button = (ImageView) ((ViewGroup) this._layout).getChildAt(0);
                ((ViewGroup.MarginLayoutParams) this._button.getLayoutParams()).topMargin += topMargin;
                this._button.setImageResource(plus ? R.drawable.ic_button_lock_plus : R.drawable.ic_lock_white_24dp);
                GraphicUtils.safeMutate(this._button.getDrawable()).setColorFilter(null);
                this._button.setOnClickListener(this);
                this._layout.setOnTouchListener(this);
            }
            this._lockMode = P.getScreenLockMode();
            if (this._lockMode == 1) {
                type = CastStatusCodes.NOT_ALLOWED;
            } else if (this._lockMode == 2) {
                type = 1000;
            } else {
                type = 2010;
            }
            AppUtils.setupLockWindow(this._windowManager, this._layout, p, type);
            this._defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
            if (P.uiVersion >= 11 && P.softButtonsVisibility != 0 && P.uiVersion < 19) {
                this._layout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.mxtech.videoplayer.Lock.1
                    @Override // android.view.View.OnSystemUiVisibilityChangeListener
                    public void onSystemUiVisibilityChange(int visibility) {
                        if (ActivityRegistry.isVisible(context)) {
                            if (visibility == 0 || visibility == 1) {
                                Lock.this.showNotification(1);
                            }
                        }
                    }
                });
            }
            showNotification(384);
        }
    }

    public boolean isKidsLock() {
        return this._mode == 1 || this._mode == 2;
    }

    public boolean hasWindowFocus() {
        return this._layout.hasWindowFocus();
    }

    public boolean canTouchSystemVolumeWindow() {
        return this._lockMode != 0;
    }

    @SuppressLint({"NewApi"})
    public void unlock() {
        String str;
        if (isLocked()) {
            String str2 = TAG;
            StringBuilder append = new StringBuilder().append("UNLOCK ").append(this);
            if (!(this._layout instanceof KidsView)) {
                str = "";
            } else {
                str = " KeyGuard Lock=" + ((KidsView) this._layout)._keyguardLock;
            }
            Log.d(str2, append.append(str).append(Build.VERSION.SDK_INT >= 11 ? " HW Accel=" + this._layout.isHardwareAccelerated() : "").toString());
            this._windowManager.removeViewImmediate(this._layout);
            Thread.setDefaultUncaughtExceptionHandler(this._defaultUEH);
            if (this._layout instanceof KidsView) {
                ((KidsView) this._layout).close();
            }
            hideLockImage();
            removeMessages(0);
            if (P.uiVersion >= 11) {
                this._layout.setOnSystemUiVisibilityChangeListener(null);
            }
            this._windowManager = null;
            this._layout = null;
            this._button = null;
            this._fadeOut = null;
            if (this._listener != null) {
                this._listener.onUnlocked(this);
            }
        }
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (this._windowManager != null && this._layout != null) {
                this._windowManager.removeViewImmediate(this._layout);
            }
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }
        this._defaultUEH.uncaughtException(thread, ex);
    }

    @SuppressLint({"NewApi"})
    private void setSystemUiVisibility(int visibility) {
        if (this._layout != null) {
            this._layout.setSystemUiVisibility(visibility);
        }
        if (this._lockMode == 2 && this._listener != null) {
            this._listener.setSystemUiVisibility(visibility);
        }
    }

    @SuppressLint({"NewApi"})
    public void showNotification(int inputType) {
        if (P.uiVersion >= 11) {
            if ((inputType & 256) == 0) {
                setSystemUiVisibility(this._activeVisibility);
            } else {
                setSystemUiVisibility(this._inactiveVisibility);
            }
        }
        if (isKidsLock()) {
            if (inputType == 2 || (inputType & 128) != 0) {
                this._toast.show();
            }
        } else {
            this._button.setVisibility(0);
        }
        removeMessages(0);
        sendEmptyMessageDelayed(0, P.getInterfaceAutoHideDelay(this));
    }

    public void hideLockImage() {
    }

    public void changeBrightness(float brightness) {
        WindowManager.LayoutParams l = (WindowManager.LayoutParams) this._layout.getLayoutParams();
        l.screenBrightness = brightness;
        this._layout.requestLayout();
    }

    @Override // android.os.Handler
    @SuppressLint({"NewApi"})
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            if (this._button != null && this._button.getVisibility() == 0) {
                if (this._fadeOut == null) {
                    this._fadeOut = AnimationUtils.loadAnimation(this._layout.getContext(), R.anim.fast_fade_out);
                    this._fadeOut.setAnimationListener(this);
                }
                this._button.startAnimation(this._fadeOut);
            }
            if (P.uiVersion >= 11) {
                setSystemUiVisibility(this._inactiveVisibility);
            }
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
        if (this._button != null) {
            this._button.setVisibility(8);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        v.performHapticFeedback(1);
        unlock();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            showNotification(3);
        }
        if (this._listener != null) {
            return this._listener.onTouch(this, v, event);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class TouchTrace {
        static final float ALPHA_MAX = 1.0f;
        final int color;
        final long downTime;
        final int motionId;
        final int pointer;
        final boolean scaleUp;
        long upTime;
        float x;
        float y;

        abstract boolean draw(Canvas canvas, long j);

        TouchTrace(int motionId, int pointerId, float x, float y, long downTime, boolean scaleUp) {
            Random rng = NumericUtils.ThreadLocalRandom.get();
            this.motionId = motionId;
            this.pointer = pointerId;
            this.x = x;
            this.y = y;
            this.downTime = downTime;
            this.color = Color.HSVToColor(new float[]{rng.nextInt(360), 0.7f + (rng.nextFloat() * 0.3f), 1.0f});
            this.scaleUp = scaleUp;
        }

        final int calcAlpha(long now) {
            if (this.upTime > 0) {
                int diff = (int) (now - this.upTime);
                if (diff >= 1500) {
                    return 0;
                }
                return (int) ((1.0f - (diff / 1500.0f)) * 255.0f * 1.0f);
            }
            return 255;
        }

        final float calcScale(long now) {
            if (!this.scaleUp || this.upTime <= 0) {
                return 1.0f;
            }
            return 1.0f + ((((float) (now - this.upTime)) * 1.3333334f) / 1000.0f);
        }

        final boolean setColor(long now, Paint paint) {
            int alpha = calcAlpha(now);
            if (alpha == 0) {
                return false;
            }
            paint.setColor((this.color & ViewCompat.MEASURED_SIZE_MASK) | (alpha << 24));
            return true;
        }

        protected boolean needToRedraw() {
            return this.upTime > 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class CircleTrace extends TouchTrace {
        private static final float RADIUS = 40.0f;
        private static final float RADIUS_PX = DeviceUtils.DIPToPixel(RADIUS);
        private final Paint paint;

        CircleTrace(int motionId, int pointerId, float x, float y, long downTime, Paint paint, boolean scaleUp) {
            super(motionId, pointerId, x, y, downTime, scaleUp);
            this.paint = paint;
        }

        @Override // com.mxtech.videoplayer.Lock.TouchTrace
        boolean draw(Canvas canvas, long now) {
            if (!setColor(now, this.paint)) {
                return false;
            }
            float scale = calcScale(now);
            if (scale == 1.0f) {
                canvas.drawCircle(this.x, this.y, RADIUS_PX, this.paint);
            } else {
                canvas.save();
                canvas.translate(this.x, this.y);
                canvas.scale(scale, scale);
                canvas.drawCircle(0.0f, 0.0f, RADIUS_PX, this.paint);
                canvas.restore();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RectangleTrace extends TouchTrace {
        private static final float HALFWIDTH = 36.0f;
        private static final RectF RECT;
        private final Paint paint;
        private float rotation;
        private float rotationDelta;

        static {
            float halfWidth = DeviceUtils.DIPToPixel(HALFWIDTH);
            RECT = new RectF(-halfWidth, -halfWidth, halfWidth, halfWidth);
        }

        RectangleTrace(int motionId, int pointerId, float x, float y, long downTime, Paint paint, boolean scaleUp) {
            super(motionId, pointerId, x, y, downTime, scaleUp);
            Random rng = NumericUtils.ThreadLocalRandom.get();
            this.paint = paint;
            this.rotation = rng.nextInt(360);
            if (rng.nextBoolean()) {
                this.rotationDelta = Lock.ROTATION_DELTA;
            } else {
                this.rotationDelta = -8.0f;
            }
        }

        @Override // com.mxtech.videoplayer.Lock.TouchTrace
        boolean draw(Canvas canvas, long now) {
            if (!setColor(now, this.paint)) {
                return false;
            }
            float scale = calcScale(now);
            canvas.save();
            canvas.translate(this.x, this.y);
            float f = this.rotation + this.rotationDelta;
            this.rotation = f;
            canvas.rotate(f);
            if (scale != 1.0f) {
                canvas.scale(scale, scale);
            }
            canvas.drawRect(RECT, this.paint);
            canvas.restore();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class TriangleTrace extends TouchTrace {
        private static final Path PATH;
        private static final float WIDTH = 80.0f;
        private final Paint paint;
        private float rotation;
        private float rotationDelta;

        static {
            float width = DeviceUtils.DIPToPixel(WIDTH);
            PATH = new Path();
            PATH.moveTo(width * 0.5f, ((-width) * 0.8660254f) / 3.0f);
            PATH.lineTo(0.0f, ((width * 0.8660254f) * 2.0f) / 3.0f);
            PATH.lineTo((-width) * 0.5f, ((-width) * 0.8660254f) / 3.0f);
            PATH.lineTo(width * 0.5f, ((-width) * 0.8660254f) / 3.0f);
            PATH.close();
            PATH.setFillType(Path.FillType.EVEN_ODD);
        }

        TriangleTrace(int motionId, int pointerId, float x, float y, long downTime, Paint paint, boolean scaleUp) {
            super(motionId, pointerId, x, y, downTime, scaleUp);
            Random rng = NumericUtils.ThreadLocalRandom.get();
            this.paint = paint;
            this.rotation = rng.nextInt(360);
            if (rng.nextBoolean()) {
                this.rotationDelta = Lock.ROTATION_DELTA;
            } else {
                this.rotationDelta = -8.0f;
            }
        }

        @Override // com.mxtech.videoplayer.Lock.TouchTrace
        boolean draw(Canvas canvas, long now) {
            if (!setColor(now, this.paint)) {
                return false;
            }
            float scale = calcScale(now);
            canvas.save();
            canvas.translate(this.x, this.y);
            float f = this.rotation + this.rotationDelta;
            this.rotation = f;
            canvas.rotate(f);
            if (scale != 1.0f) {
                canvas.scale(scale, scale);
            }
            canvas.drawPath(PATH, this.paint);
            canvas.restore();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class BitmapTrace extends TouchTrace {
        protected float b;
        protected final Bitmap bitmap;
        private int currentAlpha;
        protected float g;
        protected final ColorMatrix metrix;
        protected final Paint paint;
        protected float r;

        BitmapTrace(int motionId, int pointerId, float x, float y, long downTime, Bitmap bitmap, boolean scaleUp) {
            super(motionId, pointerId, x, y, downTime, scaleUp);
            this.bitmap = bitmap;
            this.paint = new Paint();
            this.metrix = new ColorMatrix();
            this.r = Color.red(this.color) / 255.0f;
            this.g = Color.green(this.color) / 255.0f;
            this.b = Color.blue(this.color) / 255.0f;
        }

        @Override // com.mxtech.videoplayer.Lock.TouchTrace
        boolean draw(Canvas canvas, long now) {
            int alpha = calcAlpha(now);
            if (alpha == 0) {
                return false;
            }
            if (this.currentAlpha != alpha) {
                this.currentAlpha = alpha;
                this.metrix.setScale(this.r, this.g, this.b, alpha / 255.0f);
                this.paint.setColorFilter(new ColorMatrixColorFilter(this.metrix));
            }
            this.metrix.setScale(this.r, this.g, this.b, alpha / 255.0f);
            this.paint.setColorFilter(new ColorMatrixColorFilter(this.metrix));
            float scale = calcScale(now);
            if (scale != 1.0f) {
                canvas.save();
                canvas.translate(this.x, this.y);
                canvas.scale(scale, scale);
                canvas.drawBitmap(this.bitmap, (-this.bitmap.getWidth()) / 2, (-this.bitmap.getHeight()) / 2, this.paint);
                canvas.restore();
            } else {
                canvas.drawBitmap(this.bitmap, this.x - (this.bitmap.getWidth() / 2), this.y - (this.bitmap.getHeight() / 2), this.paint);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RotatingBitmapTrace extends BitmapTrace {
        private int currentAlpha;
        private float rotation;
        private float rotationDelta;

        RotatingBitmapTrace(int motionId, int pointerId, float x, float y, long downTime, Bitmap bitmap, boolean scaleUp) {
            super(motionId, pointerId, x, y, downTime, bitmap, scaleUp);
            Random rng = NumericUtils.ThreadLocalRandom.get();
            this.rotation = rng.nextInt(360);
            if (rng.nextBoolean()) {
                this.rotationDelta = Lock.ROTATION_DELTA;
            } else {
                this.rotationDelta = -8.0f;
            }
        }

        @Override // com.mxtech.videoplayer.Lock.BitmapTrace, com.mxtech.videoplayer.Lock.TouchTrace
        boolean draw(Canvas canvas, long now) {
            int alpha = calcAlpha(now);
            if (alpha == 0) {
                return false;
            }
            if (this.currentAlpha != alpha) {
                this.currentAlpha = alpha;
                this.metrix.setScale(this.r, this.g, this.b, alpha / 255.0f);
                this.paint.setColorFilter(new ColorMatrixColorFilter(this.metrix));
            }
            canvas.save();
            canvas.translate(this.x, this.y);
            float f = this.rotation + this.rotationDelta;
            this.rotation = f;
            canvas.rotate(f);
            float scale = calcScale(now);
            if (scale != 1.0f) {
                canvas.scale(scale, scale);
            }
            canvas.drawBitmap(this.bitmap, (-this.bitmap.getWidth()) / 2, (-this.bitmap.getHeight()) / 2, this.paint);
            canvas.restore();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class KidsView extends View implements Runnable {
        private static final int ANIMATION_UPATE_INTERVAL = 75;
        private static final int CORNER_PRESS_MAX_INTERVAL = 2000;
        private static final int DISPLAY_PERIOD = 1500;
        private static final short LEFT_BOTTOM = 3;
        private static final short LEFT_TOP = 0;
        private static final short RIGHT_BOTTOM = 2;
        private static final short RIGHT_TOP = 1;
        private static final float SCALE_SPEED = 1.3333334f;
        private static final float STROKE_WIDTH = 10.0f;
        private HashMap<Integer, Bitmap> _bitmaps;
        private Paint _fillPaint;
        private KeyguardManager.KeyguardLock _keyguardLock;
        private final Resources _kidsLockResources;
        private long _lastCornerPressTime;
        private short _leftCornerPress;
        private int _motionSeq;
        private short _prevPressedCorner;
        private int _soundID;
        private SoundPool _soundPool;
        private Paint _strokePaint;
        private final ArrayList<TouchTrace> _traces;
        private final boolean _useEffects;

        @SuppressLint({"UseSparseArrays"})
        public KidsView(Context context, Resources kidsLockResources, boolean useEffects) throws IOException {
            super(context);
            this._traces = new ArrayList<>();
            this._prevPressedCorner = (short) -1;
            this._kidsLockResources = kidsLockResources;
            this._useEffects = useEffects;
            if (this._useEffects) {
                this._fillPaint = new Paint();
                this._fillPaint.setAntiAlias(true);
                this._strokePaint = new Paint();
                this._strokePaint.setAntiAlias(true);
                this._strokePaint.setStyle(Paint.Style.STROKE);
                this._strokePaint.setStrokeWidth(DeviceUtils.DIPToPixel(STROKE_WIDTH));
                this._bitmaps = new HashMap<>();
                AssetManager assets = this._kidsLockResources.getAssets();
                AssetFileDescriptor fd = assets.openFd("effect0.ogg");
                try {
                    this._soundPool = new SoundPool(16, 3, 0);
                    this._soundID = this._soundPool.load(fd, 1);
                } finally {
                    fd.close();
                }
            }
            try {
                this._keyguardLock = L.keyguardManager.newKeyguardLock(getClass().getName());
                this._keyguardLock.disableKeyguard();
            } catch (SecurityException e) {
                Log.e(Lock.TAG, "", e);
                this._keyguardLock = null;
            }
        }

        public void close() {
            if (this._keyguardLock != null) {
                this._keyguardLock.reenableKeyguard();
            }
        }

        private TouchTrace createBitmapTrace(int pointerId, float x, float y, long downTime, String resName, boolean rotate, boolean scaleUp) throws IOException {
            String packageName = App.context.getPackageName();
            int resId = this._kidsLockResources.getIdentifier(resName, "drawable", packageName);
            if (resId == 0) {
                Log.e(Lock.TAG, "'" + resName + "' not found from " + packageName);
                throw new IOException();
            }
            Bitmap bitmap = this._bitmaps.get(Integer.valueOf(resId));
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(this._kidsLockResources, resId);
                this._bitmaps.put(Integer.valueOf(resId), bitmap);
            }
            return rotate ? new RotatingBitmapTrace(this._motionSeq, pointerId, x, y, downTime, bitmap, scaleUp) : new BitmapTrace(this._motionSeq, pointerId, x, y, downTime, bitmap, scaleUp);
        }

        private void newTrace(int pointerId, float x, float y, long downTime) throws IOException {
            TouchTrace trace;
            float freq;
            Iterator<TouchTrace> it = this._traces.iterator();
            while (it.hasNext()) {
                TouchTrace t = it.next();
                float distX = x - t.x;
                float distY = y - t.y;
                float distSquare = (distX * distX) + (distY * distY);
                if (distSquare < Lock.NEAR_DISTANCE_SQUARE_PX) {
                    return;
                }
            }
            Random rng = NumericUtils.ThreadLocalRandom.get();
            if (!rng.nextBoolean()) {
                trace = createBitmapTrace(pointerId, x, y, downTime, Lock.MODELS[rng.nextInt(Lock.MODELS.length)], false, true);
            } else {
                switch (rng.nextInt(10)) {
                    case 0:
                        trace = new CircleTrace(this._motionSeq, pointerId, x, y, downTime, this._fillPaint, true);
                        break;
                    case 1:
                        trace = new CircleTrace(this._motionSeq, pointerId, x, y, downTime, this._strokePaint, true);
                        break;
                    case 2:
                        trace = new RectangleTrace(this._motionSeq, pointerId, x, y, downTime, this._fillPaint, true);
                        break;
                    case 3:
                        trace = new RectangleTrace(this._motionSeq, pointerId, x, y, downTime, this._strokePaint, true);
                        break;
                    case 4:
                        trace = new TriangleTrace(this._motionSeq, pointerId, x, y, downTime, this._fillPaint, true);
                        break;
                    case 5:
                        trace = new TriangleTrace(this._motionSeq, pointerId, x, y, downTime, this._strokePaint, true);
                        break;
                    case 6:
                        trace = createBitmapTrace(pointerId, x, y, downTime, "trace_star_fill", true, true);
                        break;
                    case 7:
                        trace = createBitmapTrace(pointerId, x, y, downTime, "trace_star_stroke", true, true);
                        break;
                    case 8:
                        trace = createBitmapTrace(pointerId, x, y, downTime, "trace_heart_fill", false, true);
                        break;
                    default:
                        trace = createBitmapTrace(pointerId, x, y, downTime, "trace_heart_stroke", false, true);
                        break;
                }
            }
            this._traces.add(trace);
            invalidate();
            if (rng.nextBoolean()) {
                freq = 1.0f / (1.0f + rng.nextFloat());
            } else {
                freq = 1.0f * (1.0f + rng.nextFloat());
            }
            this._soundPool.play(this._soundID, 0.07f, 0.07f, 0, 0, freq);
        }

        @Override // android.view.View
        @SuppressLint({"NewApi"})
        public boolean dispatchTouchEvent(MotionEvent ev) {
            int index;
            int action = ev.getAction();
            int actionMasked = action & 255;
            switch (actionMasked) {
                case 0:
                    Lock.this.removeMessages(0);
                    this._motionSeq++;
                    long downTime = ev.getDownTime();
                    int id = ev.getPointerId(0);
                    float x = ev.getX();
                    float y = ev.getY();
                    if (x >= getWidth() || y >= getHeight()) {
                        Lock.this.showNotification(128);
                    }
                    short corner = -1;
                    if (x < Lock.UNLOCK_CORNER_PX) {
                        if (y >= Lock.UNLOCK_CORNER_PX) {
                            if (y > getHeight() - Lock.UNLOCK_CORNER_PX) {
                                corner = LEFT_BOTTOM;
                            }
                        } else {
                            corner = LEFT_TOP;
                        }
                    } else if (x > getWidth() - Lock.UNLOCK_CORNER_PX) {
                        if (y >= Lock.UNLOCK_CORNER_PX) {
                            if (y > getHeight() - Lock.UNLOCK_CORNER_PX) {
                                corner = RIGHT_BOTTOM;
                            }
                        } else {
                            corner = RIGHT_TOP;
                        }
                    }
                    if (corner == -1) {
                        this._prevPressedCorner = (short) -1;
                    } else if (this._prevPressedCorner == -1 || (this._prevPressedCorner + RIGHT_TOP) % 4 != corner || downTime - this._lastCornerPressTime > 2000) {
                        this._prevPressedCorner = corner;
                        this._lastCornerPressTime = downTime;
                        this._leftCornerPress = LEFT_BOTTOM;
                    } else {
                        short s = (short) (this._leftCornerPress - 1);
                        this._leftCornerPress = s;
                        if (s > 0) {
                            this._prevPressedCorner = corner;
                            this._lastCornerPressTime = downTime;
                        } else {
                            performHapticFeedback(1);
                            Lock.this.unlock();
                            return true;
                        }
                    }
                    if (this._useEffects) {
                        try {
                            newTrace(id, x, y, downTime);
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                    break;
                case 1:
                case 3:
                    if (this._traces.size() == 0) {
                        Lock.this.sendEmptyMessageDelayed(0, P.getInterfaceAutoHideDelay(Lock.this));
                        break;
                    } else {
                        Iterator<TouchTrace> it = this._traces.iterator();
                        while (it.hasNext()) {
                            TouchTrace t = it.next();
                            if (t.motionId == this._motionSeq) {
                                int index2 = ev.findPointerIndex(t.pointer);
                                if (index2 >= 0) {
                                    t.x = ev.getX(index2);
                                    t.y = ev.getY(index2);
                                    t.upTime = ev.getEventTime();
                                } else if (t.upTime == 0) {
                                    t.upTime = ev.getEventTime();
                                }
                            }
                        }
                        invalidate();
                        break;
                    }
                case 2:
                    if (this._traces.size() != 0) {
                        Iterator<TouchTrace> it2 = this._traces.iterator();
                        while (it2.hasNext()) {
                            TouchTrace t2 = it2.next();
                            if (t2.motionId == this._motionSeq && (index = ev.findPointerIndex(t2.pointer)) >= 0) {
                                t2.x = ev.getX(index);
                                t2.y = ev.getY(index);
                            }
                        }
                        invalidate();
                        break;
                    }
                    break;
                case 5:
                    int index3 = ev.getActionIndex();
                    int id2 = ev.getPointerId(index3);
                    if (this._useEffects) {
                        try {
                            newTrace(id2, ev.getX(index3), ev.getY(index3), ev.getEventTime());
                            break;
                        } catch (IOException e2) {
                            break;
                        }
                    }
                    break;
                case 6:
                    int index4 = ev.getActionIndex();
                    int id3 = ev.getPointerId(index4);
                    Iterator<TouchTrace> it3 = this._traces.iterator();
                    while (true) {
                        if (!it3.hasNext()) {
                            break;
                        } else {
                            TouchTrace t3 = it3.next();
                            if (t3.motionId == this._motionSeq && t3.pointer == id3) {
                                t3.x = ev.getX(index4);
                                t3.y = ev.getY(index4);
                                t3.upTime = ev.getEventTime();
                                invalidate();
                                break;
                            }
                        }
                    }
                    break;
            }
            return true;
        }

        @Override // android.view.View
        @SuppressLint({"NewApi"})
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this._traces.size() != 0) {
                long now = SystemClock.uptimeMillis();
                boolean needToRedraw = false;
                Iterator<TouchTrace> it = this._traces.iterator();
                while (it.hasNext()) {
                    TouchTrace t = it.next();
                    if (!t.draw(canvas, now)) {
                        it.remove();
                    } else if (!needToRedraw) {
                        needToRedraw = t.needToRedraw();
                    }
                }
                Lock.this.removeCallbacks(this);
                if (needToRedraw) {
                    long delay = 75 - (SystemClock.uptimeMillis() - now);
                    if (delay > 0) {
                        Lock.this.postDelayed(this, delay);
                    } else {
                        Lock.this.post(this);
                    }
                } else if (P.uiVersion >= 11 && this._traces.size() == 0) {
                    setSystemUiVisibility(Lock.this._inactiveVisibility);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            invalidate();
        }

        @Override // android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (Lock.this._listener != null) {
                Lock.this._listener.onWindowFocusChanged(Lock.this, hasWindowFocus);
            }
        }

        @Override // android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (Lock.this._listener != null) {
                return Lock.this._listener.onKeyLocked(Lock.this, event);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Layout extends LinearLayoutCompat {
        Layout(Context context) {
            super(context);
        }

        @Override // android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (Lock.this._listener != null) {
                Lock.this._listener.onWindowFocusChanged(Lock.this, hasWindowFocus);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (Lock.this._listener != null) {
                return Lock.this._listener.onKeyLocked(Lock.this, event);
            }
            return true;
        }
    }
}
