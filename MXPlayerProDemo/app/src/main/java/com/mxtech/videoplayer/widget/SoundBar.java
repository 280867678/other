package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import com.tokaracamara.android.verticalslidevar.VerticalSeekBar;

/* loaded from: classes.dex */
public final class SoundBar extends ScreenVerticalBar {
    private VerticalSeekBar _bar2;
    private Drawable _supremeVolume;
    private Drawable _supremeVolumeMute;

    public SoundBar(Context context) {
        super(context);
    }

    public SoundBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.widget.ScreenVerticalBar, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this._bar2 = this.bar2;
        this.bar.setMax(L.maxVolumeDisplayLevel);
        this.bar2.setMax(L.maxVolumeDisplayLevel);
    }

    @Override // com.mxtech.videoplayer.widget.ScreenVerticalBar, android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        int value2;
        if (visibility == 0) {
            int value = P.getVolume() / L.volumeIncrementBy;
            if (value > L.maxVolumeDisplayLevel) {
                value2 = value - L.maxVolumeDisplayLevel;
                value = L.maxVolumeDisplayLevel;
            } else {
                value2 = 0;
            }
            this.bar.setProgress(value);
            if (this.bar2 != null) {
                this.bar2.setProgress(value2);
            }
            update(value, value2);
        }
        super.setVisibility(visibility);
    }

    public void allowOverVolume(boolean allow) {
        if (allow) {
            this._bar2.setVisibility(0);
            this.bar2 = this._bar2;
            return;
        }
        this._bar2.setVisibility(8);
        this.bar2 = null;
    }

    @Override // com.mxtech.videoplayer.widget.ScreenVerticalBar
    protected void update(int value, int value2) {
        int flags;
        ActivityScreen activity = (ActivityScreen) this.player;
        int oldBaseLevel = (P.syncSystemVolume ? L.audioManager.getStreamVolume(3) : P.localVolume) / L.volumeIncrementBy;
        if (oldBaseLevel != value) {
            if (P.syncSystemVolume) {
                if (activity.needVolumeSafetyWarnDialog() && !activity.isLocked()) {
                    flags = 1;
                    P.updateVolumePanelShowTime();
                } else {
                    flags = 0;
                }
                setStreamVolume(3, L.volumeIncrementBy * value, flags);
            } else {
                P.localVolume = L.volumeIncrementBy * value;
            }
            activity.onBaseVolumeChanged();
        }
        if (this.bar2 != null && P.overVolume != L.volumeIncrementBy * value2) {
            P.overVolume = L.volumeIncrementBy * value2;
            activity.onOverVolumeChanged();
        }
        if (value > 0) {
            if (this._supremeVolume == null) {
                this._supremeVolume = getContext().getResources().getDrawable(R.drawable.supreme_volume);
            }
            if (this.bar2 != null) {
                value += value2;
            }
            setSupremeText(" " + value, this._supremeVolume);
            return;
        }
        if (this._supremeVolumeMute == null) {
            this._supremeVolumeMute = getContext().getResources().getDrawable(R.drawable.supreme_volume_mute);
        }
        setSupremeText("", this._supremeVolumeMute);
    }

    private static void setStreamVolume(int streamType, int volume, int flags) {
        int iteration;
        int direction;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int current = L.audioManager.getStreamVolume(streamType);
                int change = volume - current;
                if (change < 0) {
                    iteration = -change;
                    direction = -1;
                } else {
                    iteration = change;
                    direction = 1;
                }
                for (int i = 0; i < iteration; i++) {
                    L.audioManager.adjustStreamVolume(streamType, direction, flags);
                }
                return;
            }
            L.audioManager.setStreamVolume(streamType, volume, flags);
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
    }
}
