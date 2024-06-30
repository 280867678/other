package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import com.mxtech.videoplayer.preference.P;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class ScreenStyle {
    public static final int CHANGE_CONTROL_COLOR_HIGHLIGHT = 128;
    public static final int CHANGE_CONTROL_COLOR_NORMAL = 64;
    public static final int CHANGE_FRAME_BORDER = 16;
    public static final int CHANGE_FRAME_COLOR = 8;
    public static final int CHANGE_ON_SCREEN_BUTTON_BACKGROUND = 512;
    public static final int CHANGE_PRESET = 2;
    public static final int CHANGE_PROGRESS_BAR_COLOR = 32;
    public static final int CHANGE_PROGRESS_BAR_PLACEMENT = 256;
    public static final int CHANGE_PROGRESS_BAR_STYLE = 4;
    public static final int DRAW_FILL = 2;
    public static final int DRAW_STROKE = 1;
    public static final int ON_SCREEN_BUTTON_BACKGROUND_CIRCLE = 1;
    public static final int ON_SCREEN_BUTTON_BACKGROUND_NONE = 0;
    public static final int PLACE_ABOVE_BUTTONS = 0;
    public static final int PLACE_BELOW_BUTTONS = 1;
    public static final int PRESET_DEFAULT = 0;
    public static final int PRESET_INVERSE = 1;
    public static final int PRORGESS_BAR_FLAT = 0;
    public static final int PRORGESS_BAR_MATERIAL = 1;
    private static ScreenStyle singleton;
    private int _changes;
    private int _controlColorHighlight;
    private int _controlColorNormal;
    private ColorFilter _controlColorNormalFilter;
    private ColorStateList _controlColorNormalStateList;
    private boolean _frameBorder;
    private int _frameColor;
    private ArrayList<OnChangeListener> _onChangeListeners;
    private int _onScreenButtonBackground;
    private int _preset = App.prefs.getInt(Key.PRESET, 0);
    private int _progressBarColor;
    private ColorFilter _progressBarColorFilter;
    private int _progressBarPlacement;
    private int _progressBarStyle;
    public int defaultControlColorHighlight;
    public int defaultControlColorNormal;
    public boolean defaultFrameBorder;
    public int defaultFrameColor;
    public int defaultOnScreenButtonBackground;
    public int defaultProgressBarColor;
    public int defaultProgressBarPlacement;
    public int defaultProgressBarStyle;

    /* loaded from: classes2.dex */
    public static class Key {
        public static final String CONTROL_COLOR_HIGHLIGHT = "screen.style.control.color.highlight";
        public static final String CONTROL_COLOR_NORMAL = "screen.style.control.color.normal";
        public static final String FRAME_BORDER = "screen.style.frame_border";
        public static final String FRAME_COLOR = "screen.style.frame_color";
        public static final String ON_SCREEN_BUTTON_BACKGROUND = "screen.style.on_screen_button_background";
        public static final String PRESET = "screen.style.preset.2";
        public static final String PROGRESS_BAR_COLOR = "screen.style.progress_bar.color";
        public static final String PROGRESS_BAR_PLACEMENT = "screen.style.progress_bar.placement";
        public static final String PROGRESS_BAR_STYLE = "screen.style.progress_bar.style";
    }

    /* loaded from: classes.dex */
    public interface OnChangeListener {
        void onStyleChanged(ScreenStyle screenStyle, int i);
    }

    public static ScreenStyle getInstance() {
        if (singleton == null) {
            singleton = new ScreenStyle();
        }
        return singleton;
    }

    public static void setInstance(ScreenStyle style) {
        if (singleton != null) {
            style._onChangeListeners = singleton._onChangeListeners;
        }
        singleton = style;
    }

    public ScreenStyle() {
        loadPreset(this._preset);
        this._frameColor = App.prefs.getInt(Key.FRAME_COLOR, this.defaultFrameColor);
        this._frameBorder = App.prefs.getBoolean(Key.FRAME_BORDER, this.defaultFrameBorder);
        this._progressBarStyle = App.prefs.getInt(Key.PROGRESS_BAR_STYLE, this.defaultProgressBarStyle);
        this._progressBarColor = App.prefs.getInt(Key.PROGRESS_BAR_COLOR, this.defaultProgressBarColor);
        this._controlColorNormal = App.prefs.getInt(Key.CONTROL_COLOR_NORMAL, this.defaultControlColorNormal);
        this._controlColorHighlight = App.prefs.getInt(Key.CONTROL_COLOR_HIGHLIGHT, this.defaultControlColorHighlight);
        this._progressBarPlacement = App.prefs.getInt(Key.PROGRESS_BAR_PLACEMENT, this.defaultProgressBarPlacement);
        this._onScreenButtonBackground = App.prefs.getInt(Key.ON_SCREEN_BUTTON_BACKGROUND, this.defaultOnScreenButtonBackground);
    }

    public void addOnChangeListener(OnChangeListener listener) {
        if (this._onChangeListeners == null) {
            this._onChangeListeners = new ArrayList<>();
        }
        this._onChangeListeners.add(listener);
    }

    public void removeOnChangeListener(OnChangeListener listener) {
        if (this._onChangeListeners != null) {
            this._onChangeListeners.remove(listener);
        }
    }

    private void loadPreset(int preset) {
        int presetResId;
        switch (preset) {
            case 1:
                presetResId = R.style.Preset_Inverse;
                break;
            default:
                presetResId = R.style.Preset;
                break;
        }
        TypedArray defaultPreset = App.context.obtainStyledAttributes(presetResId, R.styleable.Preset);
        this.defaultFrameColor = defaultPreset.getColor(R.styleable.Preset_presetFrameColor, 0);
        this.defaultFrameBorder = defaultPreset.getBoolean(R.styleable.Preset_presetFrameBorder, false);
        this.defaultProgressBarStyle = defaultPreset.getInt(R.styleable.Preset_presetProgressBarStyle, 0);
        this.defaultProgressBarColor = defaultPreset.getColor(R.styleable.Preset_presetProgressBarColor, 0);
        this.defaultControlColorNormal = defaultPreset.getColor(R.styleable.Preset_presetControlColorNormal, 0);
        this.defaultControlColorHighlight = defaultPreset.getColor(R.styleable.Preset_presetControlColorHighlight, 0);
        this.defaultProgressBarPlacement = defaultPreset.getInt(R.styleable.Preset_presetProgressBarPlacement, 0);
        this.defaultOnScreenButtonBackground = defaultPreset.getInt(R.styleable.Preset_presetOnScreenButtonBackground, 0);
        defaultPreset.recycle();
    }

    public void flushChanges(SharedPreferences.Editor edit) {
        if ((this._changes & 2) != 0) {
            edit.putInt(Key.PRESET, this._preset);
        }
        if ((this._changes & 256) != 0) {
            edit.putInt(Key.PROGRESS_BAR_PLACEMENT, this._progressBarPlacement);
        }
        if ((this._changes & 4) != 0) {
            edit.putInt(Key.PROGRESS_BAR_STYLE, this._progressBarStyle);
        }
        if ((this._changes & 8) != 0) {
            edit.putInt(Key.FRAME_COLOR, this._frameColor);
        }
        if ((this._changes & 16) != 0) {
            edit.putBoolean(Key.FRAME_BORDER, this._frameBorder);
        }
        if ((this._changes & 32) != 0) {
            edit.putInt(Key.PROGRESS_BAR_COLOR, this._progressBarColor);
        }
        if ((this._changes & 64) != 0) {
            edit.putInt(Key.CONTROL_COLOR_NORMAL, this._controlColorNormal);
        }
        if ((this._changes & 128) != 0) {
            edit.putInt(Key.CONTROL_COLOR_HIGHLIGHT, this._controlColorHighlight);
        }
        if ((this._changes & 512) != 0) {
            edit.putInt(Key.ON_SCREEN_BUTTON_BACKGROUND, this._onScreenButtonBackground);
        }
        if (this._onChangeListeners != null) {
            int changes = this._changes;
            this._changes = 0;
            Iterator it = ((ArrayList) this._onChangeListeners.clone()).iterator();
            while (it.hasNext()) {
                OnChangeListener listener = (OnChangeListener) it.next();
                listener.onStyleChanged(this, changes);
            }
            return;
        }
        this._changes = 0;
    }

    public int getPreset() {
        return this._preset;
    }

    public int getProgressBarStyle() {
        return this._progressBarStyle;
    }

    public int getFrameColor() {
        return this._frameColor;
    }

    public boolean getFrameBorder() {
        return this._frameBorder;
    }

    public int getProgressBarColor() {
        return this._progressBarColor;
    }

    public int getControlColorNormal() {
        return this._controlColorNormal;
    }

    public int getControlColorHighlight() {
        return this._controlColorHighlight;
    }

    public int getOnScreenButtonBackground() {
        return this._onScreenButtonBackground;
    }

    public ColorFilter getProgressBarColorFilter() {
        if (this._progressBarColorFilter == null) {
            this._progressBarColorFilter = new PorterDuffColorFilter(this._progressBarColor, PorterDuff.Mode.SRC_ATOP);
        }
        return this._progressBarColorFilter;
    }

    public ColorFilter getControlNormalColorFilter() {
        if (this._controlColorNormalFilter == null) {
            this._controlColorNormalFilter = new PorterDuffColorFilter(this._controlColorNormal, PorterDuff.Mode.SRC_ATOP);
        }
        return this._controlColorNormalFilter;
    }

    public ColorStateList getControlNormalColorStateList() {
        if (this._controlColorNormalStateList == null) {
            this._controlColorNormalStateList = ColorStateList.valueOf(this._controlColorNormal);
        }
        return this._controlColorNormalStateList;
    }

    public int getProgressBarPlacement() {
        return this._progressBarPlacement;
    }

    public void setPreset(int preset) {
        if (this._preset != preset) {
            this._changes |= 2;
            this._preset = preset;
            loadPreset(preset);
        }
    }

    public void reset() {
        setFrameColor(this.defaultFrameColor);
        setFrameBorder(this.defaultFrameBorder);
        setProgressBarStyle(this.defaultProgressBarStyle);
        setProgressBarColor(this.defaultProgressBarColor);
        setProgressBarPlacement(this.defaultProgressBarPlacement);
        setControlColorNormal(this.defaultControlColorNormal);
        setControlColorHighlight(this.defaultControlColorHighlight);
        setOnScreenButtonBackground(this.defaultOnScreenButtonBackground);
    }

    public void setProgressBarStyle(int style) {
        if (this._progressBarStyle != style) {
            this._changes |= 4;
            this._progressBarStyle = style;
        }
    }

    public void setFrameColor(int color) {
        if (this._frameColor != color) {
            this._changes |= 8;
            this._frameColor = color;
        }
    }

    public void setFrameBorder(boolean border) {
        if (this._frameBorder != border) {
            this._changes |= 16;
            this._frameBorder = border;
        }
    }

    public void setProgressBarColor(int color) {
        if (this._progressBarColor != color) {
            this._changes |= 32;
            this._progressBarColor = color;
            this._progressBarColorFilter = null;
        }
    }

    public void setControlColorNormal(int color) {
        if (this._controlColorNormal != color) {
            this._changes |= 64;
            this._controlColorNormal = color;
            this._controlColorNormalFilter = null;
            this._controlColorNormalStateList = null;
        }
    }

    public void setControlColorHighlight(int color) {
        if (this._controlColorHighlight != color) {
            this._changes |= 128;
            this._controlColorHighlight = color;
        }
    }

    public void setProgressBarPlacement(int placement) {
        if (this._progressBarPlacement != placement) {
            this._changes |= 256;
            this._progressBarPlacement = placement;
        }
    }

    public void setOnScreenButtonBackground(int background) {
        if (this._onScreenButtonBackground != background) {
            this._changes |= 512;
            this._onScreenButtonBackground = background;
        }
    }

    @SuppressLint({"NewApi"})
    public void setFrameBackgroundTo(View view, int draw) {
        ColorDrawable d;
        GradientDrawable d2;
        Drawable old = view.getBackground();
        if ((draw & 1) != 0) {
            if (old instanceof GradientDrawable) {
                d2 = (GradientDrawable) old;
            } else {
                d2 = new GradientDrawable();
            }
            d2.setStroke(1, (getControlColorNormal() & ViewCompat.MEASURED_SIZE_MASK) | P.DEFAULT_OSD_BACK_COLOR);
            if ((draw & 2) != 0) {
                d2.setColor(getFrameColor());
            } else {
                d2.setColor(0);
            }
            view.setBackgroundDrawable(d2);
        } else if ((draw & 2) != 0) {
            if (Build.VERSION.SDK_INT < 11) {
                view.setBackgroundColor(getFrameColor());
                return;
            }
            if (old instanceof ColorDrawable) {
                d = (ColorDrawable) old;
            } else {
                d = new ColorDrawable();
            }
            d.setColor(getFrameColor());
            view.setBackgroundDrawable(d);
        } else {
            view.setBackgroundDrawable(null);
        }
    }
}
