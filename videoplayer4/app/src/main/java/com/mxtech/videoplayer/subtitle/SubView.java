package com.mxtech.videoplayer.subtitle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;

public final class SubView extends ViewSwitcher implements Handler.Callback{
    public SubView(Context context) {
        super(context);
    }

    public SubView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }
}
