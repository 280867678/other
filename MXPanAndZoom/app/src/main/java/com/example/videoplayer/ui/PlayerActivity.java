package com.example.videoplayer.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.videoplayer.DeviceUtils;
import com.example.videoplayer.R;

import java.io.IOException;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener, Handler.Callback {
    protected PlayerActivity.TopLayout topLayout;
//    protected PlayerActivity.UILayout uiLayout;
    /**
     * 1 视频缩放  2 视频平移  4 平移和缩放
     */
    private int _gestures = 4;      //1 视频缩放  2 视频平移  4 平移和缩放
    protected final Handler handler = new Handler(this);
    private SurfaceHolder _surfaceHolderCreated;
    protected MediaPlayer pp;
    private final int[] screen_offset = new int[2];
//    private final int[] subview_screen_offset = new int[2];
    private SurfaceView _surfaceView;
    private float _zoomInsideWidth;
    private float _zoomInsideHeight;
    private float _zoomCropHeight;
    private float _zoomCropWidth;

    private int _swiping = 0;
//    private int _lastSwiping = 0;
    private int _touchStartX;
    private int _touchStartX2;
    private int _touchStartY;
    private int _touchStartY2;

    private int _zoomDragBeginWidth;
    private int _zoomDragBeginHeight;
    private float _zoomMinWidth;
    private float _zoomMinHeight;
    private float _zoomMaxWidth;
    private float _zoomMaxHeight;
    private int _zoomWidth;
    private int _zoomHeight;
    private int _panX;
    private int _panY;
    private int _panStartX;
    private int _panStartY;
    private int _videoZoom = 1;   //0:  //拉伸   1:  //适应屏幕  2: //100%  3: //裁剪

    private TextView _supremeText;
    private View _supremeContainer;

    private static final float MAX_ZOOM = 2.0f;
    private static final float MIN_ZOOM = 0.25f;

//    private boolean _wasSetOrientationFromThisVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        Activity的setContentView之前设置全屏的flag
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Display display = getWindowManager().getDefaultDisplay();
        setTitle(display.getHeight() + " ** " + display.getWidth());
//        Log.e("Display:", display.getHeight() + " ** " + display.getWidth());

//        Log.e("dragMinimumPixel:", String.valueOf(DeviceUtils.dragMinimumPixel));
//        设置横屏代码：
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        findViewById(R.id.player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pp.start();
            }
        });


        if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予
            ActivityCompat.requestPermissions(PlayerActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        this.topLayout = (TopLayout) findViewById(R.id.top_layout);
//        this.uiLayout = (UILayout) findViewById(R.id.ui_layout);
        this._supremeText = (TextView) findViewById(R.id.supremeText);
        this._supremeContainer = findViewById(R.id.supremeContainer);
        this.topLayout.setSystemUiVisibility(View.INVISIBLE);


        this.topLayout.activity = this;
//        this.uiLayout.activity = this;

        this.topLayout.setOnTouchListener(this);
        topLayout.setsi;


//        topLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if (topLayout.getSystemUiVisibility() != visibility) {
//                    if(visibility == 0){
//                        pp.start();
//                    }
//                }
//            }
//        });
        this.pp = new MediaPlayer();
//        @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
//        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//            if (this._listener != null) {
//                this._listener.onVideoSizeChanged(this, width, height);
//            }
//        }
//        this.pp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//            @Override
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
////                Log.e("onVideoSizeChanged :","宽度："+width+" 高度："+height);
//                onVideoSizeChangeds(width, height);
//            }
//        });


        applyIntent(intent, savedInstanceState);
    }

    private void applyIntent(Intent intent, @Nullable Bundle saved) {
        boolean start;
        if (saved != null) {
            start = saved.getBoolean("playing", true);

        } else {
            start = true;
        }

        load();
        if (start) {
            this.pp.start();
        }
    }


    public static double canonicalizeRadian(double angle) {
        int numCirculation;
        if (angle < 0.0d) {
            numCirculation = (int) ((angle - 3.141592653589793d) / 6.283185307179586d);
        } else {
            numCirculation = (int) ((angle + 3.141592653589793d) / 6.283185307179586d);
        }
        return angle - (numCirculation * 6.283185307179586d);
    }


    @Override
    public void onResume() {
        super.onResume();

        createSurfaceView();
//        DisplayMetrics metrics = new DisplayMetrics();
//        Display display = getWindowManager().getDefaultDisplay();
//        display.getMetrics(metrics);
        this.pp.start();
    }


    private void createSurfaceView() {
        if (this._surfaceView == null && this._surfaceHolderCreated == null) {
            this._surfaceView = new SurfaceView(this);
            this._surfaceView.setId(R.id.surfaceView);

            SurfaceHolder holder = this._surfaceView.getHolder();
            holder.addCallback(this);
//            holder.setFormat(4);

            RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(-1, -1);
            if ((this._gestures & 6) == 0) {
                l.addRule(13);
            }
            this.topLayout.addView(this._surfaceView, getTopLayoutZOrder(2), l);

        }
    }


    final int getTopLayoutZOrder(int type) {
        int order = 0;
        if (type == 0) {
            return 0;
        }
        if (this._surfaceView != null && this.topLayout.indexOfChild(this._surfaceView) >= 0) {
            order = 0 + 1;
        }
        if (type != 1) {
//            if (this._coverView != null && this.topLayout.indexOfChild(this._coverView) >= 0) {
            order++;
//            }
            if (type != 2) {
//                Log.e("getTopLayoutZOrder::", "type != 2");

//                if (this._subView.getParent() == this.topLayout && this.topLayout.indexOfChild(this._subView) >= 0) {
//                    order++;
//                }
//                if (type != 3 && this._subtitleOverlay != null && this.topLayout.indexOfChild(this._subtitleOverlay) >= 0) {
//                    return order + 1;
//                }
                return order;
            }
            return order;
        }
        return order;
    }


    public static class TopLayout extends RelativeLayout implements Runnable {
        PlayerActivity activity;

        public TopLayout(Context context) {
            super(context);
        }

        public TopLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TopLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

//        @Override // android.view.View
//        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//            super.onSizeChanged(w, h, oldw, oldh);
//            if (this.activity != null) {
//                post(this);
//            }
//        }

        @Override // java.lang.Runnable
        public void run() {
//            this.activity.onTopLayoutSizeChanged(this);
        }
    }


    public void onTopLayoutSizeChanged(View v) {
        if (!isFinishing()) {

            int topWidth = this.topLayout.getWidth();
            int topHeight = this.topLayout.getHeight();

            if (this._surfaceView != null && ((RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams()).getRules()[13] == 0) {
                int surfaceWidth = this._surfaceView.getWidth();
                int surfaceHeight = this._surfaceView.getHeight();
//                if (surfaceWidth > 4 && surfaceHeight > 4) {
//                    Log.e(TAG,"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
////                    if ((topWidth > topHeight) == (surfaceWidth > surfaceHeight)) {
////                    }
//                    this._panX -= 4;
////                    Log.e("updateSizes::", "第236行");
//                    updateSizes();
//                    this._panX += 4;
//                    this.handler.removeMessages(12);
//                    this.handler.sendEmptyMessage(12);
//                    return;
//                }
//                Log.e("updateSizes::", "第243行");
                updateSizes();
            } else {
//                Log.e("updateSizes::", "第246行");
//                updateSizes();
            }
        }
    }


    protected void updateSizes() {

        recalcSizingVariables();
        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
//            Log.e("updateSizes::", "this._zoomWidth != 0  && this._zoomHeight != 0 ");
            setDisplaySize(this._zoomWidth, this._zoomHeight);
        } //else {
//            setZoomMode(this._videoZoom);
//        }

//        Log.e("updateSizes::", "第263行");

    }


//    public static class UILayout extends RelativeLayout {
//        //        private PlaybackController _controller;
//        PlayerActivity activity;
//
//        public UILayout(Context context) {
//            super(context);
//        }
//
//        public UILayout(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        public UILayout(Context context, AttributeSet attrs, int defStyle) {
//            super(context, attrs, defStyle);
//        }
//
////        void setController(PlaybackController controller) {
////            this._controller = controller;
////        }
//
//        @Override // android.view.View
//        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//            super.onSizeChanged(w, h, oldw, oldh);
////            if (this.activity != null) {
////                this.activity.onUILayoutSizeChanged(this);
////            }
//        }
//
////        @Override // android.view.ViewGroup, android.view.View
////        public boolean dispatchTouchEvent(MotionEvent event) {
////            int action = event.getAction();
////            switch (action) {
////                case 0:
//////                    this._controller.pin();
////                    break;
////                case 1:
////                case 3:
//////                    this._controller.unpin();
////                    break;
////            }
////            return super.dispatchTouchEvent(event);
////        }
//    }


    public void onUILayoutSizeChanged(View v) {
    }

    private int _delayedResizeHeight;
    private int _delayedResizeWidth;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case 5:
                setDisplaySize(this._delayedResizeWidth, this._delayedResizeHeight);
                return true;
            case 7:
                if (this._surfaceHolderCreated != null) {

                    try {
//                        pp.setDataSource("/storage/emulated/0/video.mp4");
//                        setState(1, 0);
//                        this.pp.setDisplay(this._surfaceHolderCreated);
//                        pp.prepare();
//                        Log.e("播放的视频宽度：", "开始播放");
//
//                        Log.e("播放的视频宽度：", String.valueOf(pp.getVideoWidth()));
//                        Log.e("播放的视频高度：", String.valueOf(pp.getVideoHeight()));
////                        _zoomWidth = pp.getVideoWidth();
////                        _zoomHeight = pp.getVideoHeight();
//                        pp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//                                pp.start();
//                            }
//                        });


                        if (isInActiveState()) {
//                            if (needVideoOutput()) {
                            try {
                                this.pp.setDisplay(this._surfaceHolderCreated);
                            } catch (IllegalArgumentException e) {
                                Log.e("setDisplay", "", e);
                            }
//                            }
//                            this._surfaceBoundDecoder = this.pp.getDecoder();
//                            updateDecoderIndicator();
                            return true;
                        } else if (isInValidState()) {
                            load();
                            return true;
                        } else {
                            return true;
                        }


                    } catch (IllegalArgumentException e) {
                        Log.e("TAG", "", e);
                        return true;
                    }

                }


                return true;

            default:
                return false;
            case 12:
//                Log.e("updateSizes::", "第357行");
                updateSizes();
                return true;
        }

    }


    public static final String TAG = "MX.Screen";

    private void load() {
        int state = getState();
        if (state == 0) {
//            this._wasSetOrientationFromThisVideo = false;
            setDataSource();
        } else if (state != 1) {
            Log.w(TAG, "Invalid state while loading: " + state);
            return;
        }
//        if (checkDecodingEnvironment()) {

//            if (this.pp.getPersistent() != null) {
//                this._loadingVideoFlags &= -129;
//            }
//            this._surfaceBoundDecoder = this.pp.getDecoder();
        if (needVideoOutput()) {
            prepare(this._surfaceHolderCreated, getWindowManager().getDefaultDisplay());
        } else {
            prepare(null, null);
        }
        if (this._surfaceView == null) {
            createSurfaceView();
        }
//        }

    }


    public boolean prepare(SurfaceHolder surfaceHolder, Display display) throws IllegalStateException {
        if (this._state != 1) {
            throw new IllegalStateException();
        }
//        if (this._requireIP && !this._hostnameResolved) {
//            this._dnsLookupTask = new DNSLookupTask(this._uri.getHost(), surfaceHolder, display, videoFlags);
//            this._dnsLookupTask.executeParallel(new Void[0]);
//        } else {
        try {
            doPrepare(surfaceHolder, display);
        } catch (Exception e) {
            Log.e(TAG, "", e);
//                handleError(0);
            return false;
        }
//        }
        setState(2, 0);
//        updatePresentingState();
        return true;
    }


    public void doPrepare(SurfaceHolder surfaceHolder, Display display) throws Exception {

//        if (this._decoder == 1) {
//            MediaPlayer primary = this.pp;
//            if (surfaceHolder != null) {
//                primary.setDisplay(surfaceHolder);
//                primary.setScreenOnWhilePlaying(true);
//            }
//            primary.setDataSource("/storage/emulated/0/video.mp4");
//
//        } else {


        MediaPlayer primary3 = this.pp;

        primary3.setDataSource("/storage/emulated/0/video.mp4");


        if (surfaceHolder != null) {
            primary3.setDisplay(surfaceHolder);
            primary3.setScreenOnWhilePlaying(true);
        }

//        }

        this.pp.setAudioStreamType(3);
        this.pp.prepare();
//        Log.e("doPrepare:", "开始播放啦");
//        this.pp.start();

    }


//    public boolean prepare(SurfaceHolder surfaceHolder, Display display, int videoFlags) throws IllegalStateException {
//        if (this._state != 1) {
//            throw new IllegalStateException();
//        }
//        if (this._requireIP && !this._hostnameResolved) {
//            this._dnsLookupTask = new DNSLookupTask(this._uri.getHost(), surfaceHolder, display, videoFlags);
//            this._dnsLookupTask.executeParallel(new Void[0]);
//        } else {
//            try {
//                doPrepare(surfaceHolder, display, videoFlags);
//            } catch (Exception e) {
//                Log.e(TAG, "", e);
//                handleError(0);
//                return false;
//            }
//        }
//        setState(2, 0);
//        updatePresentingState();
//        return true;
//    }


    public void setDataSource() {
        if (this._state != 0) {
            throw new IllegalStateException();
        }

        setState(1, 0);
    }


    private boolean needVideoOutput() {
        return needVideoOutput(true);
    }

    private boolean needVideoOutput(boolean settingsValue) {
        boolean a = getIntent().getBooleanExtra("video", settingsValue);
        return a;
    }


    public boolean checkDecodingEnvironment() {
//        byte decoder = this.pp.getDecoder();
//        if (!canUseStickyMode()) {
//            this._stickyRequested = false;
//        }
        if (this._surfaceView == null) {
            return this._surfaceHolderCreated != null;
        }
        removeSurfaceView();
        createSurfaceView();
        return false;
    }

    private void removeSurfaceView() {
        if (this._surfaceView != null) {
            this.topLayout.removeView(this._surfaceView);
            this._surfaceView = null;
        }
        if (this._surfaceHolderCreated != null) {
            this._surfaceHolderCreated.removeCallback(this);
            this._surfaceHolderCreated = null;
        }
        this.pp.setDisplay(null);
    }


    public void setState(int state, int flags) {
        if (this._state != state) {
            this._state = state;
//            if (this._state == 5) {
//                if (!this._handler.hasMessages(1)) {
//                    this._handler.sendEmptyMessageDelayed(1, 100L);
//                }
//                if (this._rampupPrepared) {
//                    rampUp();
//                }
//            } else {
//                this._handler.removeMessages(1);
//                this._handler.removeMessages(3);
//            }
//            this._audioFocusManager.update();
//            this._client.onStateChanged(state, flags);
        }
    }

    public int getState() {
        return this._state;
    }

    public boolean isInValidState() {
        return this._state >= 1;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this._surfaceHolderCreated = holder;
        this.handler.sendEmptyMessage(7);

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        this.pp.setDisplay(null);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return handleUITouch(v, event, this._gestures);
    }


    public final boolean handleUITouch(View v, MotionEvent event, int gestures) {

        float height;
        float width;
        boolean reachedLimit;
        int action = event.getAction();
        v.getLocationOnScreen(this.screen_offset);
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                switch (this._swiping) {
//                    case 0:
////                        if (action == MotionEvent.ACTION_UP) {
////                            if (v instanceof UILayout) {
////                                if ((gestures & 1088) != 0 && this._lastSwiping == 0) {
//////                                    if ((gestures & 64) != 0) {
//////                                        pp.start();
//////                                        break;
//////                                    } else {
//////                                        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
//////                                            this._zoomWidth = 0;
//////                                            this._zoomHeight = 0;
//////                                            this._panY = 0;
//////                                            this._panX = 0;
//////
//////
//////
//////                                            setZoomMode(this._videoZoom);
//////                                        } else {
//////
//////                                            int touchX = ((int) event.getX(0)) + this.screen_offset[0];
//////                                            int touchY = ((int) event.getY(0)) + this.screen_offset[1];
//////                                            int topWidth = this.topLayout.getWidth();
//////                                            int topHeight = this.topLayout.getHeight();
//////                                            Point size = getSizeFor(this._videoZoom);
//////                                            this._zoomWidth = size.x * 2;
//////                                            this._zoomHeight = size.y * 2;
//////                                            this._panX = (-(touchX - (topWidth / 2))) * 2;
//////                                            this._panY = (-(touchY - (topHeight / 2))) * 2;
//////                                            int gapX = (topWidth - this._zoomWidth) / 2;
//////                                            int gapY = (topHeight - this._zoomHeight) / 2;
//////                                            int pannedX = gapX + this._panX;
//////                                            int pannedY = gapY + this._panY;
//////                                            if (pannedX > 0) {
//////                                                this._panX = -gapX;
//////                                            } else if (this._zoomWidth + pannedX < topWidth) {
//////                                                this._panX = gapX;
//////                                            }
//////                                            if (pannedY > 0) {
//////                                                this._panY = -gapY;
//////                                            } else if (this._zoomHeight + pannedY < topHeight) {
//////                                                this._panY = gapY;
//////                                            }
//////                                            setDisplaySize(this._zoomWidth, this._zoomHeight);
//////                                        }
////
////                                    updateLayoutForZoomPan();
////                                        break;
//////                                    }
////                                } else {
////
////                                    this.pp.start();
////                                }
////                            }
////                        }
//                        break;
//                    case 1:
//                        break;
//                    case 2:
//                        break;
//                    case 3:
//                    case 17:
//                    case 18:
                    case 19:


                        setSupremeText((CharSequence) null);
                        break;
//                    case 33:
//                    case 34:
//                    case 36:
//                    case 37:
//                        break;
                }


//                this._lastSwiping = this._swiping;
                this._swiping = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                switch (this._swiping) {
                    case 0:
                        int numPointer = event.getPointerCount();
//                        if (numPointer == 1) {
//
//                            int x = ((int) event.getX()) + this.screen_offset[0];
//                            int y = ((int) event.getY()) + this.screen_offset[1];
//                            int direction = getDirection(this._touchStartX, this._touchStartY, x, y, DeviceUtils.dragMinimumPixel);
//                            switch (direction) {
//                                case 1:
//                                case 2:
//                                    this._touchStartY = y;
//                                    if ((gestures & 512) != 0) {
//                                        this._swiping = 34;
//                                        return true;
//                                    }
//                                    this._swiping = 2;
//                                    return true;
//                                case 3:
//                                case 4:
//                                    this._touchStartX = x;
//                                    if ((gestures & 128) != 0) {
//                                        if (y >= this.subview_screen_offset[1]) {
//                                            this._swiping = 33;
//                                            return true;
//                                        }
//                                    }
//                                    if ((gestures & 8) != 0) {
//                                        this._swiping = 1;
//                                        return true;
//                                    }
//                                    return true;
//
//                                default:
//                                    return true;
//                            }
//                        } else
                            if (numPointer >= 2) {

                            float x2 = event.getX(0) + this.screen_offset[0];
                            float x22 = event.getX(1) + this.screen_offset[0];
                            float y2 = event.getY(0) + this.screen_offset[1];
                            float y22 = event.getY(1) + this.screen_offset[1];
                            float deltaX = x2 - this._touchStartX;
                            float deltaY = y2 - this._touchStartY;
                            double distance2 = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                            float deltaX2 = x22 - this._touchStartX2;
                            float deltaY2 = y22 - this._touchStartY2;
                            double distance22 = Math.sqrt((deltaX2 * deltaX2) + (deltaY2 * deltaY2));
                            if (distance2 + distance22 >= DeviceUtils.dragMinimumPixel) {
                                int direction1 = getDirection(this._touchStartX, this._touchStartY, x2, y2, 0.0d);
                                int direction2 = getDirection(this._touchStartX2, this._touchStartY2, x22, y22, 0.0d);
//                                if ((gestures & 2048) != 0 && isSameDirection(direction1, direction2, 1, 2)) {
//
//                                    this._swiping = 3;
//                                    this._touchStartY = (int) y2;
//                                    return true;
//                                }
//                                if ((gestures & 256) != 0) {
//
//                                    if (y2 >= this.subview_screen_offset[1] && y22 >= this.subview_screen_offset[1]) {
//                                        this._touchStartX = (int) x2;
//                                        this._touchStartY = (int) y2;
//                                        this._touchStartX2 = (int) x22;
//                                        this._touchStartY2 = (int) y22;
//                                        this._zoomDragBeginWidth = (int) Math.abs(x2 - x22);
//                                        this._zoomDragBeginHeight = (int) Math.abs(y2 - y22);
//                                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
//
////                                                this._swiping = 37;
//
//                                            this._swiping = 36;
//
//                                            return true;
//                                        }
//                                        return true;
//                                    }
//                                }
                                if ((gestures & 7) != 0 && this._surfaceView != null) {

//                                    Log.e("ontouch：", "开始播放  gestures");
                                    int videoWidth = this.pp.getVideoWidth(); //this.pp.getDisplayWidth();
                                    int videoHeight = this.pp.getVideoHeight(); //this.pp.getDisplayHeight();
                                    if (videoWidth > 0 && videoHeight > 0) {
                                        this._touchStartX = (int) x2;
                                        this._touchStartY = (int) y2;
                                        this._touchStartX2 = (int) x22;
                                        this._touchStartY2 = (int) y22;
                                        this._panStartX = this._panX;
                                        this._panStartY = this._panY;
                                        this._zoomDragBeginWidth = this._surfaceView.getWidth();
                                        this._zoomDragBeginHeight = (this._zoomDragBeginWidth * videoHeight) / videoWidth;
                                        if ((gestures & 4) != 0) {

                                            this._swiping = 19;
                                            return true;
                                        }
//                                        else if ((gestures & 7) == 3) {
//
//                                            double angle = Math.atan2(deltaY, deltaX);
//                                            double angle2 = Math.atan2(deltaY2, deltaX2);
//                                            boolean isPan = Math.abs(canonicalizeRadian(angle - angle2)) < 0.6283185307179586d;
//                                            if (isPan) {
//                                                this._swiping = 18;
//                                                return true;
//                                            } else if (!isSameDirection(direction1, direction2, 1, 2) && !isSameDirection(direction1, direction2, 4, 3)) {
//                                                this._swiping = 17;
//                                                return true;
//                                            } else {
//                                                return true;
//                                            }
//                                        }

                                        else if ((gestures & 1) != 0) {

                                            if (!isSameDirection(direction1, direction2, 1, 2) && !isSameDirection(direction1, direction2, 4, 3)) {
                                                this._swiping = 17;
                                                return true;
                                            }
                                            return true;
                                        } else {

                                            this._swiping = 18;
                                            return true;
                                        }
                                    }
                                    return true;
                                }
                                return true;
                            }
                            return true;
                        } else {
                            return true;
                        }

                    case 17:
                    case 18:
                    case 19:

                        if (this._surfaceView != null) {
//                            Log.e("ontouch：", "开始播放  this._surfaceView != null 612");
                            int videoWidth2 = this.pp.getVideoWidth();//this.pp.getDisplayWidth();
                            int videoHeight2 = this.pp.getVideoHeight();//this.pp.getDisplayHeight();
                            if (videoWidth2 > 0 && videoHeight2 > 0) {
                                if (((this._swiping == 17) | (this._swiping == 19)) && event.getPointerCount() >= 2) {

                                    float distanceXNow = Math.abs(event.getX(0) - event.getX(1));
                                    float distanceYNow = Math.abs(event.getY(0) - event.getY(1));
                                    if (distanceXNow > distanceYNow) {
                                        width = (this._zoomDragBeginWidth + distanceXNow) - Math.abs(this._touchStartX - this._touchStartX2);
                                        height = (videoHeight2 * width) / videoWidth2;
                                    } else {
                                        height = (this._zoomDragBeginHeight + distanceYNow) - Math.abs(this._touchStartY - this._touchStartY2);
                                        width = (videoWidth2 * height) / videoHeight2;
                                    }
                                    if (width < this._zoomMinWidth) {
                                        width = this._zoomMinWidth;
                                        height = this._zoomMinHeight;
                                        reachedLimit = true;
                                    } else if (width > this._zoomMaxWidth) {
                                        width = this._zoomMaxWidth;
                                        height = this._zoomMaxHeight;
                                        reachedLimit = true;
                                    } else {
                                        reachedLimit = false;
                                    }
                                    if (this._zoomWidth != ((int) width) || this._zoomHeight != ((int) height) || reachedLimit) {
                                        this._zoomWidth = (int) width;
                                        this._zoomHeight = (int) height;
                                        StringBuffer sb = new StringBuffer();
                                        sb.setLength(0);
                                        sb.append((this._zoomWidth * 100) / videoWidth2).append('%');
//                                        Log.e("缩放百分比：", String.valueOf((this._zoomWidth * 100) / videoWidth2) + '%');
//                                        Log.e("缩放百分比：", "_zoomWidth的高度：" + _zoomWidth + "   _zoomWidth * 100的高度：" + (this._zoomWidth * 100) + "   videoWidth2的高度：" + videoWidth2);

                                        setZoomSupremeText(sb.toString(), this._zoomWidth, this._zoomHeight);
                                    }
                                }
                                if ((this._swiping == 18) | (this._swiping == 19)) {
                                    this._panX = this._panStartX + ((int) ((event.getX(0) + this.screen_offset[0]) - this._touchStartX));
                                    this._panY = this._panStartY + ((int) ((event.getY(0) + this.screen_offset[1]) - this._touchStartY));
                                }
                                if (this._zoomWidth != 0 && this._zoomHeight != 0) {
                                    setDisplaySize(this._zoomWidth, this._zoomHeight);
                                    return true;
                                }
                                setZoomMode(this._videoZoom);
                                return true;
                            }
                            return true;
                        }
                        return true;

                    default:
                        return true;
                }
            case MotionEvent.ACTION_POINTER_DOWN:
            case 261 /* 261 */:
                if (event.getPointerCount() == 2) {
                    float x3 = event.getX(0) + this.screen_offset[0];
                    float x23 = event.getX(1) + this.screen_offset[0];
                    float y3 = event.getY(0) + this.screen_offset[1];
                    float y23 = event.getY(1) + this.screen_offset[1];
//                    if ((this._swiping & 16) != 0) {
//
//                        if (this._surfaceView != null) {
////                            Log.e("ontouch：", "开始播放  this._surfaceView != null 676");
//                            int videoWidth3 = this.pp.getVideoWidth();//this.pp.getDisplayWidth();
//                            int videoHeight3 = this.pp.getVideoHeight();//this.pp.getDisplayHeight();
//                            if (videoWidth3 > 0 && videoHeight3 > 0) {
//                                this._touchStartX = (int) x3;
//                                this._touchStartY = (int) y3;
//                                this._touchStartX2 = (int) x23;
//                                this._touchStartY2 = (int) y23;
//                                this._panStartX = this._panX;
//                                this._panStartY = this._panY;
//                                this._zoomDragBeginWidth = this._surfaceView.getWidth();
//                                this._zoomDragBeginHeight = (this._zoomDragBeginWidth * videoHeight3) / videoWidth3;
//                                return true;
//                            }
//                            return true;
//                        }
//                        return true;
//                    } else
//                        if (this._swiping == 36 || this._swiping == 37) {
//
//                        this._touchStartX = (int) x3;
//                        this._touchStartY = (int) y3;
//                        this._touchStartX2 = (int) x23;
//                        this._touchStartY2 = (int) y23;
//                        this._zoomDragBeginWidth = (int) Math.abs(x3 - x23);
//                        this._zoomDragBeginHeight = (int) Math.abs(y3 - y23);
//                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
//                            if (this._swiping == 37) {
//                                return true;
//                            }
//                            return true;
//                        }
//                        return true;
//                    } else
//                        if (this._swiping == 3) {
//
//                        this._touchStartX = (int) x3;
//                        this._touchStartY = (int) y3;
//                        this._touchStartX2 = (int) x23;
//                        this._touchStartY2 = (int) y23;
//                        return true;
//                    } else
                        if (this._swiping == 0) {

                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
                        return true;
                    } else {
                        return true;
                    }
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
            case 262 /* 262 */:
                if (event.getPointerCount() == 2 && (this._swiping & 16) != 0) {

                    int erased = getActionIndex(action);
                    int left = erased == 1 ? 0 : 1;
                    this._touchStartX = ((int) event.getX(left)) + this.screen_offset[0];
                    this._touchStartY = ((int) event.getY(left)) + this.screen_offset[1];
                    this._panStartX = this._panX;
                    this._panStartY = this._panY;
                    return true;
                }
                return true;
            default:
                return true;
        }
    }

    private int getActionIndex(int action) {
        return (65280 & action) >> 8;
    }


    private int getDirection(float x0, float y0, float x1, float y1, double minDistance) {
        double deltaX = x1 - x0;
        double deltaY = y1 - y0;
        if (deltaX != 0.0d || deltaY != 0.0d) {
            double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
            if (distance >= minDistance) {
                double angle = Math.atan2(deltaY, deltaX);
                if (2.6179938779914944d < angle || angle < -2.6179938779914944d) {
                    return 3;
                }
                if (-0.5235987755982988d < angle && angle < 0.5235987755982988d) {
                    return 4;
                }
                if (1.0471975511965976d < angle && angle < 2.0943951023931953d) {
                    return 2;
                }
                if (-2.0943951023931953d < angle && angle < -1.0471975511965976d) {
                    return 1;
                }
            }
        }
        return 0;
    }


    private static boolean isSameDirection(int dir1, int dir2, int req1, int req2) {
        if (dir1 == 0 || dir2 == 0 || dir1 != dir2) {
            return false;
        }
        return dir1 == req1 || dir1 == req2;
    }


    public void setZoomSupremeText(CharSequence text, int width, int height) {
        setSupremeText(text);
    }

    public final void setSupremeText(CharSequence text) {
        setSupremeText(text, null, true, null);
    }

    /**
     * 设置显示缩放百分比
     *
     * @param text
     * @param image
     * @param animate
     * @param token
     */
    public final void setSupremeText(CharSequence text, Drawable image, boolean animate, Object token) {
        this.handler.removeMessages(4);
        if (text != null) {
            this._supremeText.setText(text);
            TextView textView = this._supremeText;
            if (token == null) {
                token = text;
            }
            textView.setTag(token);
            this._supremeContainer.setVisibility(View.VISIBLE);
            return;
        }
        this._supremeText.setTag(null);
        if (this._supremeContainer.getVisibility() == View.VISIBLE) {
            hideSupremeText();
        }


    }

    public void hideSupremeText() {
        this._supremeContainer.setVisibility(View.GONE);
        this._supremeText.setText((CharSequence) null);
    }


    public void setDisplaySize(int width, int height) {
//        Log.e("setDisplaySize::", "width：" + width + " height:" + height);
        if (this._surfaceView != null) {
            if (width > 0 && height > 0) {
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
                l.width = width;
                l.height = height;
                int topWidth = this.topLayout.getWidth();
                int topHeight = this.topLayout.getHeight();
                int gap = topWidth - width;
                l.leftMargin = (gap / 2) + this._panX;
                l.rightMargin = gap - l.leftMargin;
                int gap2 = topHeight - height;
                l.topMargin = (gap2 / 2) + this._panY;
                l.bottomMargin = gap2 - l.topMargin;
                this._surfaceView.requestLayout();
            }
        }
    }

//    public void setDisplaySize(int width, int height) {
//        if (this._surfaceView != null) {
////            if (P.videoZoomLimited && this.pp.getDecoder() != 2) {
////                width = Math.min(width, this.topLayout.getWidth());
////                height = Math.min(height, this.topLayout.getHeight());
////            }
//            if (width > 0 && height > 0) {
//                long now = SystemClock.uptimeMillis();
//                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
//                if ((l.width != width || l.height != height) ) {
//                    if (!this.handler.hasMessages(5)) {
//                        this.handler.sendEmptyMessageAtTime(5,now);
//                    }
//                    this._delayedResizeWidth = width;
//                    this._delayedResizeHeight = height;
//                    return;
//                }
//
//                l.width = width;
//                l.height = height;
//                int topWidth = this.topLayout.getWidth();
//                int topHeight = this.topLayout.getHeight();
//                int gap = topWidth - width;
//                l.leftMargin = (gap / 2) + this._panX;
//                l.rightMargin = gap - l.leftMargin;
//                int gap2 = topHeight - height;
//                l.topMargin = (gap2 / 2) + this._panY;
//                l.bottomMargin = gap2 - l.topMargin;
//                this._surfaceView.requestLayout();
//            }
//        }
//    }


    public final void setZoomMode(int mode) {
//        Log.e("setZoomMode:::", String.valueOf(mode));
        switch (mode) {
            case 0:  //拉伸
//                Log.e("setZoomMode::0:", "topLayout.getWidth(): " + String.valueOf(this.topLayout.getWidth()) + "   topLayout.getHeight(): " + String.valueOf(this.topLayout.getHeight()));
                setDisplaySize(this.topLayout.getWidth(), this.topLayout.getHeight());
                break;
            case 1:  //适应屏幕
            default:
//                Log.e("setZoomMode::default:", "(int) this._zoomInsideWidth: " + String.valueOf((int) this._zoomInsideWidth) + "   (int) this._zoomInsideHeight: " + String.valueOf((int) this._zoomInsideHeight));
                setDisplaySize((int) this._zoomInsideWidth, (int) this._zoomInsideHeight);
                break;
            case 2: //100%
//                Log.e("setZoomMode::2:", "this.pp.getDisplayWidth(): " + String.valueOf(this.pp.getDisplayWidth()) + "   this.pp.getDisplayHeight(): " + String.valueOf(this.pp.getDisplayHeight()));
//                Log.e("setZoomMode：", "开始播放  2  858");
//                Log.e("setZoomMode::2:", "this.pp.getVideoWidth(): " + String.valueOf(this.pp.getVideoWidth()) + "   this.pp.getVideoHeight(): " + String.valueOf(this.pp.getVideoHeight()));
                setDisplaySize(this.pp.getVideoWidth(), this.pp.getVideoHeight()); //this.pp.getDisplayWidth(), this.pp.getDisplayHeight()
                break;
            case 3: //裁剪
//                Log.e("setZoomMode::3:", "(int) this._zoomCropWidth: " + String.valueOf((int) this._zoomCropWidth) + "   (int) this._zoomCropWidth: " + String.valueOf((int) this._zoomCropHeight));
                setDisplaySize((int) this._zoomCropWidth, (int) this._zoomCropHeight);
                break;
        }

    }


    public final Point getSizeFor(int zoomMode) {
//        Log.e("getSizeFor:::", String.valueOf(zoomMode));
        switch (zoomMode) {
            case 0:
                return new Point(this.topLayout.getWidth(), this.topLayout.getHeight());
            case 1:
            default:
                return new Point((int) this._zoomInsideWidth, (int) this._zoomInsideHeight);
            case 2:
//                Log.e("getSizeFor：", "开始播放  2  882");
                return new Point(this.pp.getVideoWidth(), this.pp.getVideoHeight());//this.pp.getDisplayWidth(), this.pp.getDisplayHeight()
            case 3:
                return new Point((int) this._zoomCropWidth, (int) this._zoomCropHeight);
        }
    }


    private void updateLayoutForZoomPan() {
        if (this._surfaceView != null) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
            l.addRule(13, 0);
            this._surfaceView.requestLayout();
        }
    }


    private int _state = 0;


    public boolean isInActiveState() {
        return this._state >= 2;
    }

    private void recalcSizingVariables() {
        if (isInActiveState()) {
//            Log.e("recalcSizingVariables：", "this.pp.isInActiveState()  902");
            int width = pp.getVideoWidth();//this.pp.getDisplayWidth();
            int height = pp.getVideoHeight();//this.pp.getDisplayHeight();
            int topWidth = this.topLayout.getWidth();
            int topHeight = this.topLayout.getHeight();
            if (width > 0 && height > 0 && topWidth > 0 && topHeight > 0) {
//                Log.e("recalcSizingVariables打印高宽度：", "width："+width+" height："+height+" topWidth："+topWidth+" topHeight："+topHeight);
                int idealHeight = (topWidth * height) / width;
//                Log.e("recalcSizingVariables打印高宽度：", "idealHeight："+idealHeight);
                if (idealHeight <= topHeight) {
                    this._zoomInsideWidth = topWidth;
                    this._zoomInsideHeight = idealHeight;
                } else {
                    int idealWidth = (topHeight * width) / height;
                    this._zoomInsideWidth = idealWidth;
                    this._zoomInsideHeight = topHeight;
                }
                int idealHeight2 = (topWidth * height) / width;
//                Log.e("recalcSizingVariables打印高宽度：", "idealHeight2："+idealHeight2);
                if (idealHeight2 >= topHeight) {
                    this._zoomCropWidth = topWidth;
                    this._zoomCropHeight = idealHeight2;
                } else {
                    int idealWidth2 = (topHeight * width) / height;
                    this._zoomCropWidth = idealWidth2;
                    this._zoomCropHeight = topHeight;
                }
                this._zoomMinWidth = width * MIN_ZOOM;
                this._zoomMinHeight = height * MIN_ZOOM;
//                Log.e("recalcSizingVariables打印高宽度：", "_zoomMinWidth："+_zoomMinWidth);
//                Log.e("recalcSizingVariables打印高宽度：", "_zoomMinHeight："+_zoomMinHeight);
                if (this._zoomMinWidth > this._zoomInsideWidth) {
                    this._zoomMinWidth = this._zoomInsideWidth;
                    this._zoomMinHeight = this._zoomInsideHeight;
                }
                this._zoomMaxHeight = Math.max(topHeight, height) * MAX_ZOOM;
                this._zoomMaxWidth = (width * this._zoomMaxHeight) / height;
//                Log.e("recalcSizingVariables打印高宽度：", "_zoomMaxHeight："+_zoomMaxHeight);
//                Log.e("recalcSizingVariables打印高宽度：", "_zoomMaxWidth："+_zoomMaxWidth);
                if (this._zoomMaxWidth < this._zoomCropWidth) {
                    this._zoomMaxWidth = this._zoomCropWidth;
                    this._zoomMaxHeight = this._zoomCropHeight;
                }
                if (this._zoomWidth > 0 && this._zoomHeight > 0) {
                    this._zoomHeight = (this._zoomWidth * height) / width;
                }
            }
        }
    }

















    public void onVideoSizeChangeds(int width, int height) {
        if (width > 0 && height > 0) {
//                setOrientation();

            if (this._surfaceHolderCreated != null) {
                setSurfaceSize(width, height);
                updateSizes();
            }
        }
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    public void updateSizesss() {
        int videoWidth = pp.getVideoWidth();
        int videoHeight = pp.getVideoHeight();

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) _surfaceView.getWidth(),(float) videoHeight / (float) _surfaceView.getHeight());
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) _surfaceView.getHeight()),(float) videoHeight/(float) _surfaceView.getWidth());
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        _surfaceView.setLayoutParams(layoutParams);
        setDisplaySize(videoWidth,videoHeight);

    }


    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    public void setOrientation() {
        int videoWidth = pp.getVideoWidth();
        int videoHeight = pp.getVideoHeight();
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
//        Log.e(TAG, "changeVideoSize: deviceHeight="+deviceHeight+"deviceWidth="+deviceWidth);
        float devicePercent = 0;
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            devicePercent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
        }else { //横屏
            devicePercent = (float) deviceHeight / (float) deviceWidth; //横屏状态下高度小与宽度,求比
        }

        if (videoWidth > videoHeight){ //判断视频的宽大于高,那么我们就优先满足视频的宽度铺满屏幕的宽度,然后在按比例求出合适比例的高度
            videoWidth = deviceWidth;//将视频宽度等于设备宽度,让视频的宽铺满屏幕
            videoHeight = (int)(deviceWidth*devicePercent);//设置了视频宽度后,在按比例算出视频高度

        }else {  //判断视频的高大于宽,那么我们就优先满足视频的高度铺满屏幕的高度,然后在按比例求出合适比例的宽度
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
                videoHeight = deviceHeight;
                /**
                 * 接受在宽度的轻微拉伸来满足视频铺满屏幕的优化
                 */
                float videoPercent = (float) videoWidth / (float) videoHeight;//求视频比例 注意是宽除高 与 上面的devicePercent 保持一致
                float differenceValue = Math.abs(videoPercent - devicePercent);//相减求绝对值
//                Log.e("devicePercent=", String.valueOf(devicePercent));
//                Log.e("videoPercent=", String.valueOf(videoPercent));
//                Log.e("differenceValue=", String.valueOf(differenceValue));
                if (differenceValue < 0.3){ //如果小于0.3比例,那么就放弃按比例计算宽度直接使用屏幕宽度
                    videoWidth = deviceWidth;
                }else {
                    videoWidth = (int)(videoWidth/devicePercent);//注意这里是用视频宽度来除
                }

            }else { //横屏
                videoHeight = deviceHeight;
                videoWidth = (int)(deviceHeight*devicePercent);

            }

        }

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) _surfaceView.getLayoutParams();
//        layoutParams.width = videoWidth;
//        layoutParams.height = videoHeight;
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        layoutParams.verticalBias = 0.5f;
//        layoutParams.horizontalBias = 0.5f;
//        _surfaceView.setLayoutParams(layoutParams);
        setDisplaySize(videoWidth,videoHeight);

    }







//    public static int screenOrientation = 10;
    //    @Override // com.mxtech.videoplayer.Player.Client
//    public void onVideoSizeChangeds(int width, int height) {
//        if (width > 0 && height > 0) {
//            boolean orientationSet = false;
//            if (screenOrientation == 99999 && !this._wasSetOrientationFromThisVideo) {
//                setOrientation(screenOrientation);
//                orientationSet = true;
//            }
////            if (!orientationSet) {
////                updateScreenRotationButton(P.screenOrientation);
////            }
////            if (!this._hasVideoTrack) {
////                this._hasVideoTrack = true;
////                removeCover();
////                updateStickyState(false);
////            }
//            if (this._surfaceHolderCreated != null) {
//                setSurfaceSize(width, height);
////                if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
////                    this._needToUpdateSizes = true;
////                } else {
//                updateSizes();
////                }
//            }
//        }
////        if (this._subtitleOverlay != null) {
////            if (this._hasVideoTrack) {
////                this._subtitleOverlay.setVideoSize(width, height);
////            } else {
////                this._subtitleOverlay.setVideoSize(-1, -1);
////            }
////        }
//    }


    private void setOrientation(int screenOrientation) {
//        if (!this._rotationLocked) {
            doSetOrientation(screenOrientation);
//        }
    }

    private void doSetOrientation(int req) {
        if (req == 99999) {
            if (isInActiveState()) {
                int width = this.pp.getVideoWidth();
                int height = this.pp.getVideoHeight();
                if (width == 0 || height == 0 || width == height) {
                    req = 2;
                } else if (width > height) {
                    if (Build.VERSION.SDK_INT >= 9) {
                        req = 6;
                    } else {
                        req = 0;
                    }
                } else if (Build.VERSION.SDK_INT >= 9) {
                    req = 7;
                } else {
                    req = 1;
                }
//                this._wasSetOrientationFromThisVideo = true;
            } else {
                return;
            }
        }
        try {
            setRequestedOrientation(req);
//            this._lastRequestedOrientation = req;
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
//        updateScreenRotationButton(req);
    }




    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"NewApi"})
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (!this.foreground) {
//            this._ignoreNextFocusLoss = false;
//        } else
            if (hasFocus) {
            this.pp.start();
//            if (this._needToUpdateSizes && !L.keyguardManager.inKeyguardRestrictedInputMode()) {
//                updateSizes();
//            }
//            this._controller.update(this.pp.getState());
        }
//        else if (!this._ignoreNextFocusLoss) {
//            if (this._pauseIfObscured && !isSticky()) {
//                Log.v(TAG, "Pause playback temporarily as main window losing focus. pause-if-obscured=" + this._pauseIfObscured);
//                this.pp.pause(7);
//            }
//        } else {
//            this._ignoreNextFocusLoss = false;
//        }
    }




    private void setSurfaceSize(int width, int height) {
        this._surfaceHolderCreated.setFixedSize(width, height);
    }


}