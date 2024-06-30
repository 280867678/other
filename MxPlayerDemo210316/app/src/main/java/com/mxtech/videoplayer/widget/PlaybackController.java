package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

public class PlaybackController extends LinearLayout implements Handler.Callback {
    private final Handler _handler;
    private boolean _autoHide;
    private ControlTarget _target;

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

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
//        this._subNaviBar = findViewById(R.id.subNaviBar);
//        this._subNaviBarTopPadder = findViewById(R.id.subNaviBarTopPadder);
//        this._progressPanel = findViewById(R.id.progressPanel);
//        this._progressBar = (SeekBar) findViewById(R.id.progressBar);
//        this._progressNarrowHeight = getContext().getResources().getDimensionPixelSize(R.dimen.video_progress_narrow_height);
//        this._subNaviBarBottomPadder = findViewById(R.id.subNaviBarBottomPadder);
//        if (this._subNaviBarBottomPadder != null) {
//            if (Build.VERSION.SDK_INT < 16 || !P.tabletFromConfig || ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
//                ((ViewGroup) this._subNaviBarBottomPadder.getParent()).removeView(this._subNaviBarBottomPadder);
//                this._subNaviBarBottomPadder = null;
//            }
//        }
    }

//    @Override // android.view.View
//    protected void onFinishInflate() {
//        super.onFinishInflate();
////        this._subNaviBar = findViewById(R.id.subNaviBar);
////        this._subNaviBarTopPadder = findViewById(R.id.subNaviBarTopPadder);
////        this._progressPanel = findViewById(R.id.progressPanel);
////        this._progressBar = (SeekBar) findViewById(R.id.progressBar);
////        this._progressNarrowHeight = getContext().getResources().getDimensionPixelSize(R.dimen.video_progress_narrow_height);
////        this._subNaviBarBottomPadder = findViewById(R.id.subNaviBarBottomPadder);
////        if (this._subNaviBarBottomPadder != null) {
////            if (Build.VERSION.SDK_INT < 16 || !P.tabletFromConfig || ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
////                ((ViewGroup) this._subNaviBarBottomPadder.getParent()).removeView(this._subNaviBarBottomPadder);
////                this._subNaviBarBottomPadder = null;
////            }
////        }
//    }

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


    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
//        if (P.playbackWheelAction == 0 && event.getAction() == 8 && this._target != null) {
//            extendVisibility();
//            P.hasWheelHeuristic = true;
//            int delta = Math.round(event.getAxisValue(9));
//            if (delta != 0) {
//                this._target.seekByWheel(delta);
//                return true;
//            }
//            return true;
//        }
        return super.onGenericMotionEvent(event);
    }





    public void pin() {
    }

    public void unpin() {
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }


    public void setOnVisibilityChangedListener(ControlTarget listener) {
        this._target = listener;
    }


}
