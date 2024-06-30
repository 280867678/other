package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class PlaybackController extends LinearLayout implements Handler.Callback, ViewTreeObserver.OnGlobalLayoutListener{
    private int _pinCount;
    private final Handler _handler;




    public PlaybackController(Context context) {
        super(context);
        this._handler = new Handler(this);
    }

    public PlaybackController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this._handler = new Handler(this);
    }

    public PlaybackController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._handler = new Handler(this);
    }

    public PlaybackController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._handler = new Handler(this);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
//            setVisibleParts(0, 2, true);
            return true;
        }
        return false;
    }

    @Override
    public void onGlobalLayout() {

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
//        if (i == 0 && this._autoHide && this._player.getState() == 4) {
//            this._handler.sendEmptyMessageDelayed(1, P.getInterfaceAutoHideDelay(this));
//        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }



    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        if (h > 0 && (this._visibleParts & 5) == 5 && this._height != h) {
//            this._height = h;
//            if (this._target != null) {
//                this._target.onDefaultHeightChanged(this, h);
//            }
//        }
        super.onSizeChanged(w, h, oldw, oldh);
    }




}
