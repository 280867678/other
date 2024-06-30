package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public final class ScreenToolbar extends Toolbar implements ViewTreeObserver.OnGlobalLayoutListener{
    public ScreenToolbar(@NonNull Context context) {
        super(context);
    }

    public ScreenToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onGlobalLayout() {

    }
}
