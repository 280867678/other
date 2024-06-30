package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;

/* loaded from: classes.dex */
public final class BrightnessBar extends ScreenVerticalBar {
    private static final double[] BRIGHTNESS_15 = {0.05d, 0.07582662371999739d, 0.10701134881332847d, 0.1435541752799933d, 0.1854551031199918d, 0.232714132333324d, 0.2853312629199899d, 0.34330649487998954d, 0.40663982821332284d, 0.47533126291998984d, 0.5493807989999906d, 0.628788436453325d, 0.7135541752799933d, 0.8036780154799952d, 0.8991599570533307d, 1.0d};
    private static final double SQRT_0_05 = 0.22360679774997896d;
    private Drawable _supremeIcon;

    public BrightnessBar(Context context) {
        super(context);
    }

    public BrightnessBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.widget.ScreenVerticalBar, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.bar.setMax(getMaxBrightness(getContext()));
    }

    public static int getMaxBrightness(Context context) {
        if (L.maxVolumeDisplayLevel < 15) {
            return 15;
        }
        return L.maxVolumeDisplayLevel;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (visibility == 0) {
            int value = brightToLevel(this.bar.getMax(), P.getBrightness());
            this.bar.setProgress(value);
            update(value, 0);
        }
        super.setVisibility(visibility);
    }

    public static double levelToBright(int max, int level) {
        if (max == 15) {
            return BRIGHTNESS_15[level];
        }
        double x = SQRT_0_05 + ((0.7763932022500211d / max) * level);
        double bright = x * x;
        if (bright < 0.05d) {
            return 0.05d;
        }
        return bright;
    }

    public static int brightToLevel(int max, double bright) {
        if (bright < 0.05d) {
            bright = 0.05d;
        }
        double x = Math.sqrt(bright);
        double level = ((x - SQRT_0_05) * max) / 0.7763932022500211d;
        return (int) Math.round(level);
    }

    @Override // com.mxtech.videoplayer.widget.ScreenVerticalBar
    protected void update(int level, int level2) {
        float newBrightness = (float) levelToBright(this.bar.getMax(), level);
        if (P.screenBrightnessAuto || newBrightness != P.screenBrightness) {
            P.screenBrightnessAuto = false;
            P.screenBrightness = newBrightness;
            ((ActivityScreen) this.player).changeBrightness(newBrightness);
        }
        if (this._supremeIcon == null) {
            this._supremeIcon = getContext().getResources().getDrawable(R.drawable.supreme_brightness);
        }
        setSupremeText(" " + Integer.toString(level), this._supremeIcon);
    }
}
