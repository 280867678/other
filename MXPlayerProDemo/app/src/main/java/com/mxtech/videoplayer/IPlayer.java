package com.mxtech.videoplayer;

import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;

/* loaded from: classes.dex */
public interface IPlayer {
    int currentPosition();

    int getAudioSync();

    View getDecorView();

    Display getDisplay();

    int getOrientation();

    int getRepeatPointA();

    int getRepeatPointB();

    double getSpeed();

    Object getSupremeTextToken();

    boolean isInPlaybackState();

    boolean isSeekable();

    void next();

    void pause(int i);

    void previous();

    void resume();

    void seekTo(int i, int i2);

    void setAudioSync(int i);

    void setRepeatPoints(int i, int i2);

    void setSpeed(double d);

    void setSubtitleCanvasSize(int i, int i2);

    void setSupremeText(int i);

    void setSupremeText(CharSequence charSequence);

    void setSupremeText(CharSequence charSequence, Drawable drawable, boolean z);

    void setSupremeText(CharSequence charSequence, Drawable drawable, boolean z, Object obj);

    void showFloatingBar(int i, boolean z);

    void start();

    void toggle(boolean z);

    void update(int i);
}
