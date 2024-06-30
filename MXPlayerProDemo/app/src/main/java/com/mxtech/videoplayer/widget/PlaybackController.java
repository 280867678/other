package com.mxtech.videoplayer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.Player;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.ScreenStyle;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public final class PlaybackController extends LinearLayout implements Handler.Callback, ViewTreeObserver.OnGlobalLayoutListener {
    public static final int ANIMATE = 2;
    private static final int MSG_HIDE = 1;
    public static final int NO_ANIMATE = 0;
    public static final int PART_CONTROL_BUTTONS = 4;
    public static final int PART_SEEK_BAR = 1;
    public static final int PART_TITLE = 2;
    private static final String STYLEABLE_BUTTON = "styleable.button";
    private static final String STYLEABLE_FRAME = "styleable.frame";
    private static final String STYLEABLE_PREFIX = "styleable.";
    private static final String STYLEABLE_TEXT = "styleable.text";
    private boolean _autoHide;
    private final Handler _handler;
    private int _height;
    private Animation _hideAnimation;
    private boolean _hiding;
    private int _pinCount;
    private Player _player;
    private int _prevVisibleParts;
    private SeekBar _progressBar;
    private int _progressHeight;
    private int _progressNarrowHeight;
    private int _progressPaddingBottom;
    private int _progressPaddingTop;
    private View _progressPanel;
    private Animation _showAnimation;
    private View _subNaviBar;
    private View _subNaviBarBottomPadder;
    private View _subNaviBarTopPadder;
    private ControlTarget _target;
    private Drawable _thumbDrawable;
    private int _visibleParts;
    public static final String TAG = App.TAG + ".Controller";
    private static final int[] BUTTON_STATE_PRESSED = {16842919};
    private static final int[] BUTTON_STATE_SELECTED = {16842913};
    private static final int[] BUTTON_STATE_FOCUSED = {16842908};
    private static final int[] BUTTON_STATE_DEFAULT = new int[0];

    /* loaded from: classes.dex */
    public interface ControlTarget {
        boolean canHide(PlaybackController playbackController);

        boolean canShow(PlaybackController playbackController, int i);

        void onControllerHidingCompleted(PlaybackController playbackController);

        void onControllerVisibilityChanged(PlaybackController playbackController, int i, int i2, boolean z);

        void onDefaultHeightChanged(PlaybackController playbackController, int i);

        void seekByWheel(int i);
    }

    public PlaybackController(Context context) {
        super(context);
        this._handler = new Handler(this);
        this._autoHide = true;
    }

    public PlaybackController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._handler = new Handler(this);
        this._autoHide = true;
    }

    public void setShowAnimation(int resId) {
        setShowAnimation(AnimationUtils.loadAnimation(getContext(), resId));
    }

    public void setShowAnimation(Animation animation) {
        this._showAnimation = animation;
    }

    public void setHideAnimation(int resId) {
        setHideAnimation(AnimationUtils.loadAnimation(getContext(), resId));
    }

    public void setHideAnimation(Animation animation) {
        this._hideAnimation = animation;
        this._hideAnimation.setAnimationListener(new AnimationHandler());
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            setVisibleParts(0, 2, true);
            return true;
        }
        return false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this._subNaviBar = findViewById(R.id.subNaviBar);
        this._subNaviBarTopPadder = findViewById(R.id.subNaviBarTopPadder);
        this._progressPanel = findViewById(R.id.progressPanel);
        this._progressBar = (SeekBar) findViewById(R.id.progressBar);
        this._progressNarrowHeight = getContext().getResources().getDimensionPixelSize(R.dimen.video_progress_narrow_height);
        this._subNaviBarBottomPadder = findViewById(R.id.subNaviBarBottomPadder);
        if (this._subNaviBarBottomPadder != null) {
            if (Build.VERSION.SDK_INT < 16 || !P.tabletFromConfig || ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
                ((ViewGroup) this._subNaviBarBottomPadder.getParent()).removeView(this._subNaviBarBottomPadder);
                this._subNaviBarBottomPadder = null;
            }
        }
    }

    private void saveProgressLayout() {
        this._progressHeight = this._progressBar.getLayoutParams().height;
        this._progressPaddingTop = this._progressBar.getPaddingTop();
        this._progressPaddingBottom = this._progressBar.getPaddingBottom();
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (h > 0 && (this._visibleParts & 5) == 5 && this._height != h) {
            this._height = h;
            if (this._target != null) {
                this._target.onDefaultHeightChanged(this, h);
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int getDefaultHeight() {
        return this._height;
    }

    public void setPlayer(Player player) {
        this._player = player;
    }

    public void enableAutoHide(boolean enable) {
        this._autoHide = enable;
        if (enable) {
            if (this._player.getState() == 4) {
                this._handler.sendEmptyMessageDelayed(1, P.getInterfaceAutoHideDelay(this));
                return;
            }
            return;
        }
        this._handler.removeMessages(1);
    }

    public void setOnVisibilityChangedListener(ControlTarget listener) {
        this._target = listener;
    }

    public boolean hitTest(int x, int y) {
        return y >= ((View) getParent()).getHeight() - this._height;
    }

    public void extendVisibility() {
        if (this._autoHide && this._pinCount == 0) {
            this._handler.removeMessages(1);
            if (this._player.getState() == 4) {
                this._handler.sendEmptyMessageDelayed(1, P.getInterfaceAutoHideDelay(this));
            }
        }
    }

    public void show(int animation) {
        setVisibleParts(-1, animation);
        extendVisibility();
    }

    public void hide(int animation) {
        setVisibleParts(0, animation);
    }

    public void setVisibleParts(int parts, int animation) {
        setVisibleParts(parts, animation, false);
    }

    private void setVisibleParts(int parts, int animation, boolean autoHide) {
        if (this._visibleParts != parts) {
            if (parts != 0) {
                if (this._target == null || this._target.canShow(this, parts)) {
                    if (this._subNaviBar != null) {
                        if ((parts & 4) != 0) {
                            this._progressBar.setPadding(this._progressBar.getPaddingLeft(), this._progressPaddingTop, this._progressBar.getPaddingRight(), this._progressPaddingBottom);
                            this._progressBar.getLayoutParams().height = this._progressHeight;
                            this._progressBar.requestLayout();
                            this._subNaviBar.setVisibility(0);
                        } else {
                            if (this._progressHeight > 0) {
                                int paddingDiff = (this._progressHeight - this._progressNarrowHeight) / 2;
                                this._progressBar.setPadding(this._progressBar.getPaddingLeft(), this._progressPaddingTop - paddingDiff, this._progressBar.getPaddingRight(), this._progressPaddingBottom - paddingDiff);
                                this._progressBar.getLayoutParams().height = this._progressNarrowHeight;
                                this._progressBar.requestLayout();
                            } else {
                                this._progressBar.setPadding(this._progressBar.getPaddingLeft(), 0, this._progressBar.getPaddingRight(), 0);
                            }
                            this._subNaviBar.setVisibility(8);
                        }
                    }
                    if ((parts & 1) != 0) {
                        setVisibility(0);
                        this._hiding = false;
                        if (animation == 2 && (this._visibleParts & 1) == 0 && this._showAnimation != null) {
                            startAnimation(this._showAnimation);
                        }
                    }
                } else {
                    return;
                }
            } else if (this._pinCount <= 0) {
                if (this._target == null || this._target.canHide(this)) {
                    if (animation == 2 && this._hideAnimation != null) {
                        this._hiding = true;
                        setVisibility(4);
                        startAnimation(this._hideAnimation);
                    } else {
                        setVisibility(8);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
            this._prevVisibleParts = this._visibleParts;
            this._visibleParts = parts;
            if (this._target != null) {
                this._target.onControllerVisibilityChanged(this, parts, animation, autoHide);
            }
        }
    }

    public int getVisibleParts() {
        return this._visibleParts;
    }

    public int getPrevVisibleParts() {
        return this._prevVisibleParts;
    }

    public boolean isHiding() {
        return this._hiding;
    }

    public final void pin() {
        int i = this._pinCount;
        this._pinCount = i + 1;
        if (i == 0) {
            this._handler.removeMessages(1);
        }
    }

    public final void unpin() {
        int i = this._pinCount - 1;
        this._pinCount = i;
        if (i == 0 && this._autoHide && this._player.getState() == 4) {
            this._handler.sendEmptyMessageDelayed(1, P.getInterfaceAutoHideDelay(this));
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (P.playbackWheelAction == 0 && event.getAction() == 8 && this._target != null) {
            extendVisibility();
            P.hasWheelHeuristic = true;
            int delta = Math.round(event.getAxisValue(9));
            if (delta != 0) {
                this._target.seekByWheel(delta);
                return true;
            }
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    public void update(int state) {
        if (this._autoHide) {
            if (state == 4 && getVisibility() == 0) {
                if (!this._handler.hasMessages(1)) {
                    this._handler.sendEmptyMessageDelayed(1, P.getInterfaceAutoHideDelay(this));
                    return;
                }
                return;
            }
            this._handler.removeMessages(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AnimationHandler implements Animation.AnimationListener, Runnable {
        private AnimationHandler() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            PlaybackController.this._handler.post(this);
            PlaybackController.this._hiding = false;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (PlaybackController.this.getVisibility() != 0) {
                PlaybackController.this.setVisibility(8);
                if (PlaybackController.this._target != null) {
                    PlaybackController.this._target.onControllerHidingCompleted(PlaybackController.this);
                }
            }
        }
    }

    private static void setStyleRecursive(View view, ScreenStyle style, int changes) {
        Object tag = view.getTag();
        if (tag instanceof String) {
            String stringTag = (String) tag;
            if (stringTag.startsWith(STYLEABLE_PREFIX)) {
                if (STYLEABLE_FRAME.equals(stringTag)) {
                    if ((changes & 8) != 0) {
                        view.setBackgroundColor(style.getFrameColor());
                    }
                } else if (STYLEABLE_BUTTON.equals(stringTag)) {
                    if ((changes & 136) != 0) {
                        StateListDrawable background = new StateListDrawable();
                        int highlightColor = style.getControlColorHighlight();
                        background.addState(BUTTON_STATE_PRESSED, new ColorDrawable(highlightColor));
                        background.addState(BUTTON_STATE_SELECTED, new ColorDrawable(highlightColor));
                        background.addState(BUTTON_STATE_FOCUSED, new ColorDrawable(highlightColor));
                        background.addState(BUTTON_STATE_DEFAULT, new ColorDrawable(style.getFrameColor()));
                        view.setBackgroundDrawable(background);
                    }
                    if ((changes & 64) != 0 && (view instanceof ImageView)) {
                        ImageView iv = (ImageView) view;
                        Drawable d = iv.getDrawable();
                        if (d != null) {
                            GraphicUtils.safeMutate(d).setColorFilter(style.getControlNormalColorFilter());
                        }
                    }
                } else if (STYLEABLE_TEXT.equals(stringTag) && (view instanceof TextView)) {
                    ((TextView) view).setTextColor(style.getControlColorNormal());
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setStyleRecursive(group.getChildAt(i), style, changes);
            }
        }
    }

    private int getProgressStyleId(int style) {
        return style == 0 ? R.style.FlatSeekBar : R.style.MaterialSeekBar;
    }

    public void setStyle(ScreenStyle style, int changes) {
        Context context = getContext();
        if ((changes & 80) != 0) {
            if ((changes & 16) != 0) {
                if (style.getFrameBorder()) {
                    setPadding(1, 1, 1, 1);
                } else {
                    setPadding(0, 0, 0, 0);
                    setBackgroundDrawable(null);
                }
            }
            if (style.getFrameBorder()) {
                style.setFrameBackgroundTo(this, 1);
            }
        }
        setStyleRecursive(this, style, changes);
        if ((changes & 100) != 0) {
            int progressStyleId = getProgressStyleId(style.getProgressBarStyle());
            TypedArray a = context.obtainStyledAttributes(progressStyleId, R.styleable.VideoSeekBar);
            try {
                if ((changes & 4) != 0) {
                    int minHeight = a.getDimensionPixelSize(R.styleable.VideoSeekBar_android_minHeight, 0);
                    Drawable backgroundDrawable = a.getDrawable(R.styleable.VideoSeekBar_android_background);
                    Drawable thumbDrawable = a.getDrawable(R.styleable.VideoSeekBar_android_thumb);
                    if (thumbDrawable != null) {
                        GraphicUtils.safeMutate(thumbDrawable).setColorFilter(style.getProgressBarColorFilter());
                    }
                    Drawable progressDrawable = a.getDrawable(R.styleable.VideoSeekBar_android_progressDrawable);
                    if (progressDrawable != null) {
                        GraphicUtils.safeMutate(progressDrawable).setColorFilter(style.getProgressBarColorFilter());
                    } else {
                        progressDrawable = createSeekBarProgressDrawable(style, a.getDrawable(R.styleable.VideoSeekBar_progressBackgroundDrawable), a.getDrawable(R.styleable.VideoSeekBar_progressProgressDrawable), a.getDrawable(R.styleable.VideoSeekBar_progressSecondaryProgressDrawable));
                    }
                    this._progressBar.setMinimumHeight(minHeight);
                    this._progressBar.setBackgroundDrawable(backgroundDrawable);
                    try {
                        this._progressBar.setThumb(thumbDrawable);
                    } catch (NullPointerException e) {
                    }
                    this._thumbDrawable = thumbDrawable;
                    this._progressBar.setProgressDrawable(progressDrawable);
                    changes &= -257;
                    changeProgressBarPlacement(style, true);
                    getViewTreeObserver().addOnGlobalLayoutListener(this);
                } else {
                    if (a.hasValue(R.styleable.VideoSeekBar_android_progressDrawable)) {
                        Drawable progressDrawable2 = this._progressBar.getProgressDrawable();
                        if (progressDrawable2 != null) {
                            GraphicUtils.safeMutate(progressDrawable2).setColorFilter(style.getProgressBarColorFilter());
                        }
                    } else {
                        this._progressBar.setProgressDrawable(createSeekBarProgressDrawable(style, a.getDrawable(R.styleable.VideoSeekBar_progressBackgroundDrawable), a.getDrawable(R.styleable.VideoSeekBar_progressProgressDrawable), a.getDrawable(R.styleable.VideoSeekBar_progressSecondaryProgressDrawable)));
                    }
                    if (this._thumbDrawable != null) {
                        GraphicUtils.safeMutate(this._thumbDrawable).setColorFilter(style.getProgressBarColorFilter());
                    }
                }
            } finally {
                a.recycle();
            }
        }
        if ((changes & 256) != 0) {
            changeProgressBarPlacement(style, false);
        }
        invalidate();
    }

    private void changeProgressBarPlacement(ScreenStyle style, boolean overrideHeightTo0) {
        int[] layoutStyleable;
        int indexHeight;
        int indexPaddingTop;
        int indexPaddingBottom;
        int indexPaddingLeft;
        int indexPaddingRight;
        int placement = style.getProgressBarPlacement();
        if (placement == 0) {
            layoutStyleable = R.styleable.VideoSeekBarAboveLayout;
            indexHeight = R.styleable.VideoSeekBarAboveLayout_height_above;
            indexPaddingTop = R.styleable.VideoSeekBarAboveLayout_paddingTop_above;
            indexPaddingBottom = R.styleable.VideoSeekBarAboveLayout_paddingBottom_above;
            indexPaddingLeft = R.styleable.VideoSeekBarAboveLayout_android_paddingLeft;
            indexPaddingRight = R.styleable.VideoSeekBarAboveLayout_android_paddingRight;
            this._subNaviBar.bringToFront();
            if (this._subNaviBarTopPadder != null) {
                this._subNaviBarTopPadder.setVisibility(8);
            }
            if (this._subNaviBarBottomPadder != null) {
                this._subNaviBarBottomPadder.setVisibility(0);
            }
        } else {
            layoutStyleable = R.styleable.VideoSeekBarBelowLayout;
            indexHeight = R.styleable.VideoSeekBarBelowLayout_height_below;
            indexPaddingTop = R.styleable.VideoSeekBarBelowLayout_paddingTop_below;
            indexPaddingBottom = R.styleable.VideoSeekBarBelowLayout_paddingBottom_below;
            indexPaddingLeft = R.styleable.VideoSeekBarBelowLayout_android_paddingLeft;
            indexPaddingRight = R.styleable.VideoSeekBarBelowLayout_android_paddingRight;
            this._progressPanel.bringToFront();
            if (this._subNaviBarTopPadder != null) {
                this._subNaviBarTopPadder.setVisibility(0);
            }
            if (this._subNaviBarBottomPadder != null) {
                this._subNaviBarBottomPadder.setVisibility(8);
            }
        }
        int progressStyleId = getProgressStyleId(style.getProgressBarStyle());
        TypedArray a = getContext().obtainStyledAttributes(progressStyleId, layoutStyleable);
        try {
            this._progressBar.setPadding(a.getDimensionPixelSize(indexPaddingLeft, 0), a.getDimensionPixelSize(indexPaddingTop, 0), a.getDimensionPixelSize(indexPaddingRight, 0), a.getDimensionPixelSize(indexPaddingBottom, 0));
            ViewGroup.LayoutParams l = this._progressBar.getLayoutParams();
            if (overrideHeightTo0) {
                l.height = 0;
            } else {
                l.height = a.getLayoutDimension(indexHeight, 0);
            }
            this._progressBar.requestLayout();
            saveProgressLayout();
        } finally {
            a.recycle();
        }
    }

    private Drawable createSeekBarProgressDrawable(ScreenStyle style, Drawable background, Drawable progress, Drawable progress2) {
        GraphicUtils.safeMutate(background).setColorFilter(style.getControlNormalColorFilter());
        GraphicUtils.safeMutate(progress).setColorFilter(style.getProgressBarColorFilter());
        GraphicUtils.safeMutate(progress2).setColorFilter(style.getControlNormalColorFilter());
        LayerDrawable d = new LayerDrawable(new Drawable[]{background, progress2, progress});
        d.setId(0, 16908288);
        d.setId(1, 16908303);
        d.setId(2, 16908301);
        return d;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
        changeProgressBarPlacement(ScreenStyle.getInstance(), false);
    }
}
