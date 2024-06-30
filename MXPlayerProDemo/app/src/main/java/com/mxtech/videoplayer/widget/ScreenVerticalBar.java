package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.IPlayer;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.ScreenStyle;
import com.tokaracamara.android.verticalslidevar.VerticalSeekBar;

/* loaded from: classes.dex */
public abstract class ScreenVerticalBar extends RelativeLayout implements VerticalSeekBar.OnSeekBarChangeListener, Animation.AnimationListener {
    private static final int SHOW_TIMEOUT = 1000;
    private static final int SUPREME_TEXT_TIMEOUT = 1000;
    private Animation _hideAnimation;
    private Runnable _hideTask;
    private int _pinCount;
    private Runnable _removeTask;
    private CharSequence _supremeText;
    private Runnable _supremeTextFadeoutTask;
    protected VerticalSeekBar bar;
    protected VerticalSeekBar bar2;
    protected IPlayer player;

    protected abstract void update(int i, int i2);

    public ScreenVerticalBar(Context context) {
        super(context);
        this._hideTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.1
            @Override // java.lang.Runnable
            public void run() {
                ScreenVerticalBar.this.hide(true);
            }
        };
        this._removeTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.2
            @Override // java.lang.Runnable
            public void run() {
                ViewGroup parent = (ViewGroup) ScreenVerticalBar.this.getParent();
                if (parent != null) {
                    parent.removeView(ScreenVerticalBar.this);
                }
            }
        };
        this._supremeTextFadeoutTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.3
            @Override // java.lang.Runnable
            public void run() {
                ScreenVerticalBar.this.fadeOutSupremeText();
            }
        };
    }

    public ScreenVerticalBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._hideTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.1
            @Override // java.lang.Runnable
            public void run() {
                ScreenVerticalBar.this.hide(true);
            }
        };
        this._removeTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.2
            @Override // java.lang.Runnable
            public void run() {
                ViewGroup parent = (ViewGroup) ScreenVerticalBar.this.getParent();
                if (parent != null) {
                    parent.removeView(ScreenVerticalBar.this);
                }
            }
        };
        this._supremeTextFadeoutTask = new Runnable() { // from class: com.mxtech.videoplayer.widget.ScreenVerticalBar.3
            @Override // java.lang.Runnable
            public void run() {
                ScreenVerticalBar.this.fadeOutSupremeText();
            }
        };
    }

    public final void setPlayer(IPlayer player) {
        this.player = player;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        Context context = getContext();
        this.bar = (VerticalSeekBar) findViewById(R.id.bar);
        this.bar.setOnSeekBarChangeListener(this);
        this.bar2 = (VerticalSeekBar) findViewById(R.id.bar2);
        if (this.bar2 != null) {
            this.bar2.setOnSeekBarChangeListener(this);
        }
        this._hideAnimation = AnimationUtils.loadAnimation(context, R.anim.fast_fade_out);
        this._hideAnimation.setAnimationListener(this);
    }

    public final void show() {
        App.handler.removeCallbacks(this._removeTask);
        if (this._pinCount == 0) {
            App.handler.removeCallbacks(this._hideTask);
            App.handler.postDelayed(this._hideTask, 1000L);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
            getParent().requestLayout();
        }
    }

    public final void setSupremeText(CharSequence text, Drawable image) {
        if (this.player != null) {
            this._supremeText = text;
            this.player.setSupremeText(text, image, false);
            if (this._pinCount == 0) {
                App.handler.removeCallbacks(this._supremeTextFadeoutTask);
                App.handler.postDelayed(this._supremeTextFadeoutTask, 1000L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void fadeOutSupremeText() {
        if (this.player != null && this.player.getSupremeTextToken() == this._supremeText) {
            this.player.setSupremeText((CharSequence) null);
        }
    }

    public final void hide(boolean animate) {
        if (getVisibility() == 0) {
            setVisibility(4);
            if (animate) {
                startAnimation(this._hideAnimation);
                return;
            }
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            }
        }
    }

    public final void pin() {
        int i = this._pinCount;
        this._pinCount = i + 1;
        if (i == 0) {
            App.handler.removeCallbacks(this._hideTask);
            App.handler.removeCallbacks(this._supremeTextFadeoutTask);
        }
    }

    public final void unpin() {
        int i = this._pinCount - 1;
        this._pinCount = i;
        if (i == 0) {
            App.handler.postDelayed(this._hideTask, 1000L);
            fadeOutSupremeText();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        show();
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent event) {
        show();
        return super.dispatchTrackballEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        show();
        return super.dispatchKeyEvent(event);
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalSeekBar.OnSeekBarChangeListener
    public final void onStartTrackingTouch(VerticalSeekBar seekBar) {
        pin();
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalSeekBar.OnSeekBarChangeListener
    public final void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            update(this.bar.getProgress(), this.bar2 != null ? this.bar2.getProgress() : 0);
        }
    }

    public int getCurrent() {
        return (this.bar2 != null ? this.bar2.getProgress() : 0) + this.bar.getProgress();
    }

    public int getMax() {
        return (this.bar2 != null ? this.bar2.getMax() : 0) + this.bar.getMax();
    }

    public void change(int delta) {
        setValue(getCurrent() + delta);
    }

    public void setValue(int value) {
        int value2;
        int max = this.bar.getMax();
        if (value > max) {
            value2 = value - max;
            value = max;
        } else {
            value2 = 0;
        }
        setValue(value, value2);
    }

    public void setValue(int value, int value2) {
        boolean changed = false;
        if (value < 0) {
            value = 0;
        } else {
            int max = this.bar.getMax();
            if (value > max) {
                value = max;
            }
        }
        if (this.bar.getProgress() != value) {
            this.bar.setProgress(value);
            changed = true;
        }
        if (this.bar2 != null) {
            if (value2 < 0) {
                value2 = 0;
            } else {
                int max2 = this.bar2.getMax();
                if (value2 > max2) {
                    value2 = max2;
                }
            }
            if (this.bar2.getProgress() != value2) {
                this.bar2.setProgress(value2);
                changed = true;
            }
        }
        if (changed) {
            update(value, value2);
        }
    }

    @Override // com.tokaracamara.android.verticalslidevar.VerticalSeekBar.OnSeekBarChangeListener
    public final void onStopTrackingTouch(VerticalSeekBar seekBar) {
        unpin();
    }

    @Override // android.view.animation.Animation.AnimationListener
    public final void onAnimationStart(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public final void onAnimationRepeat(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public final void onAnimationEnd(Animation animation) {
        if (getVisibility() != 0) {
            App.handler.post(this._removeTask);
        }
    }

    public void setStyle(ScreenStyle style, int changes) {
        if ((changes & 8) != 0) {
            Drawable background = getBackground();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(style.getFrameColor());
                return;
            }
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                Drawable background2 = child.getBackground();
                if (background2 instanceof GradientDrawable) {
                    ((GradientDrawable) background2).setColor(style.getFrameColor());
                }
            }
        }
    }
}
