package com.mxtech.videoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ZoomButton;

import com.mxtech.videoplayer.subtitle.SubView;
import com.mxtech.videoplayer.widget.PlaybackController;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Locale;

public class ActivityScreen extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, ActionBar.OnMenuVisibilityListener, Handler.Callback, Player.Client {
    protected ActivityScreen.TopLayout topLayout;
    private PlaybackController _controller;
    protected ActivityScreen.UILayout uiLayout;
    private int _gestures = 4;      //1 视频缩放  2 视频平移  4 平移和缩放
    protected final Handler handler = new Handler(this);
    private SurfaceHolder _surfaceHolderCreated;
    private ImageView _coverView;
    private SubtitleOverlay _subtitleOverlay;
    private SubView _subView;
    private int _surfaceType;
    protected MediaPlayer pp;
    private final int[] screen_offset = new int[2];
    private final int[] subview_screen_offset = new int[2];
    private SurfaceView _surfaceView;
    //    protected Player pp;
    private ZoomButton _zoomButton;
    private float _zoomInsideWidth;
    private float _zoomInsideHeight;
    private float _zoomCropHeight;
    private float _zoomCropWidth;

    private int _swiping = 0;
    private int _lastSwiping = 0;
    private int _touchStartX;
    private int _touchStartX2;
    private int _touchStartY;
    private int _touchStartY2;

    private int _zoomDragBeginWidth ;
    private int _zoomDragBeginHeight ;
    private float _zoomMinWidth;
    private float _zoomMinHeight;
    private float _zoomMaxWidth;
    private float _zoomMaxHeight;
    private int _zoomWidth;
    private int _zoomHeight;
    private int _panX ;
    private int _panY ;
    private int _panStartX;
    private int _panStartY;
    private int _videoZoom = 1;   //0:  //拉伸   1:  //适应屏幕  2: //100%  3: //裁剪

    private boolean isStac = false;


    @SuppressLint("LongLogTag")
    public void 打印() {
        Log.e("","开始@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#开始");
        Log.e("ActivityScreen:打印：_zoomDragBeginWidth：", String.valueOf(_zoomDragBeginWidth));
        Log.e("ActivityScreen:打印：_zoomDragBeginHeight：", String.valueOf(_zoomDragBeginHeight));
        Log.e("ActivityScreen:打印：_zoomMinWidth：", String.valueOf(_zoomMinWidth));
        Log.e("ActivityScreen:打印：_zoomMinHeight：", String.valueOf(_zoomMinHeight));
        Log.e("ActivityScreen:打印：_zoomMaxWidth：", String.valueOf(_zoomMaxWidth));
        Log.e("ActivityScreen:打印：_zoomMaxHeight：", String.valueOf(_zoomMaxHeight));
        Log.e("ActivityScreen:打印：_zoomWidth：", String.valueOf(_zoomWidth));
        Log.e("ActivityScreen:打印：_zoomHeight：", String.valueOf(_zoomHeight));
        Log.e("ActivityScreen:打印：_panX：", String.valueOf(_panX));
        Log.e("ActivityScreen:打印：_panY：", String.valueOf(_panY));
        Log.e("ActivityScreen:打印：_panStartX：", String.valueOf(_panStartX));
        Log.e("ActivityScreen:打印：_panStartY：", String.valueOf(_panStartY));
        Log.e("ActivityScreen:打印：_videoZoom：", String.valueOf(_videoZoom));
        Log.e("ActivityScreen:打印：_swiping：", String.valueOf(_swiping));
        Log.e("ActivityScreen:打印：_zoomInsideWidth：", String.valueOf(_zoomInsideWidth));
        Log.e("ActivityScreen:打印：_zoomInsideHeight：", String.valueOf(_zoomInsideHeight));
        Log.e("ActivityScreen:打印：_zoomCropHeight：", String.valueOf(_zoomCropHeight));
        Log.e("ActivityScreen:打印：_zoomCropWidth：", String.valueOf(_zoomCropWidth));
        Log.e("ActivityScreen:打印：_lastSwiping：", String.valueOf(_lastSwiping));
        Log.e("ActivityScreen:打印：_touchStartX：", String.valueOf(_touchStartX));
        Log.e("ActivityScreen:打印：_touchStartX2：", String.valueOf(_touchStartX2));
        Log.e("ActivityScreen:打印：_touchStartY：", String.valueOf(_touchStartY));
        Log.e("ActivityScreen:打印：_touchStartY2：", String.valueOf(_touchStartY2));

        Log.e("ActivityScreen:打印：_gestures：", String.valueOf(_gestures));
        Log.e("","结束@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#结束");

    }





    public void 打印测试() {
        打印();
    }


    private int _naviMoveIntervalMillis;
    private boolean _alwaysShowStatusText;
    private boolean _alwaysShowStatusBar;
    private int _fullscreen;
    private int _videoZoomDelay;
    private float _gestureSeekSpeed;
    private boolean _fitSubtitleOverlayToVideo;
    private boolean _buttonBacklightOff;
    private boolean _osdBottom;
    private boolean _osdBackground;
    private int _osdTextColor;
    private int _osdBackColor;
    private boolean _showLeftTime;
    private boolean _useSpeedupTricks;
    private boolean _displaySeekingPosition;
    private boolean _showScreenRotateButton;
    private boolean _defaultStickyAudio;
    private int _stereoMode;
    private boolean _pauseIfObscured;
    private boolean _defaultSticky;

    @SuppressLint("LongLogTag")
    public void 打印onCreate初始话数据() {

        Log.e("ActivityScreen:打印onCreate初始话数据：this._naviMoveIntervalMillis：", String.valueOf(this._naviMoveIntervalMillis));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._alwaysShowStatusText：", String.valueOf(this._alwaysShowStatusText));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._alwaysShowStatusBar：", String.valueOf(this._alwaysShowStatusBar));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._fullscreen：", String.valueOf(this._fullscreen));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._videoZoomDelay：", String.valueOf(this._videoZoomDelay));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._gestures：", String.valueOf(this._gestures));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._gestureSeekSpeed：", String.valueOf(this._gestureSeekSpeed));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._fitSubtitleOverlayToVideo：", String.valueOf(this._fitSubtitleOverlayToVideo));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._buttonBacklightOff：", String.valueOf(this._buttonBacklightOff));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._osdBottom：", String.valueOf(this._osdBottom));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._osdBackground：", String.valueOf(this._osdBackground));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._osdTextColor：", String.valueOf(this._osdTextColor));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._osdBackColor：", String.valueOf(this._osdBackColor));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._showLeftTime：", String.valueOf(this._showLeftTime));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._useSpeedupTricks：", String.valueOf(this._useSpeedupTricks));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._displaySeekingPosition：", String.valueOf(this._displaySeekingPosition));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._showScreenRotateButton：", String.valueOf(this._showScreenRotateButton));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._defaultStickyAudio：", String.valueOf(this._defaultStickyAudio));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._stereoMode：", String.valueOf(this._stereoMode));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._videoZoom：", String.valueOf(this._videoZoom));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._defaultSticky：", String.valueOf(this._defaultSticky));
        Log.e("ActivityScreen:打印onCreate初始话数据：this._pauseIfObscured：", String.valueOf(this._pauseIfObscured));
    }

    public void 打印测试2() {
        打印onCreate初始话数据();
    }

    private static final float MAX_ZOOM = 2.0f;
    private static final float MIN_ZOOM = 0.25f;
    private int _lastDownKey = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

//        设置横屏代码：
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (ContextCompat.checkSelfPermission(ActivityScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予
            ActivityCompat.requestPermissions(ActivityScreen.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }


        this._zoomButton = (ZoomButton) findViewById(R.id.zoom);
        this._subView = (SubView) findViewById(R.id.subtitleView);
        this.topLayout = (TopLayout) findViewById(R.id.top_layout);
        this.uiLayout = (UILayout) findViewById(R.id.ui_layout);
        this._controller = (PlaybackController) findViewById(R.id.controller);
        this.topLayout.activity = this;
        this.uiLayout.activity = this;


        if ((this._gestures & 6) == 0) {
            Log.e("_gestures & 6 ", "YES   OK");
        } else {
            Log.e("_gestures & 6 结果", String.valueOf((this._gestures & 6)));
        }


        this.uiLayout.setOnTouchListener(this);

//        this._gestures = P.getGestures();
//        this._gestures = 1;

        this.pp = new MediaPlayer();
//        this.pp = new Player(this,this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        this._videoZoom = extras.getInt("video_zoom");


        Log.e("分界线：", "￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥");
        打印();
        打印onCreate初始话数据();
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.v(TAG, "onResume (" + this + "): uri=" + this.pp.getUri());
        Log.e("onResume::", "onResume");
        createSurfaceView();
//        resume();
//        this._tracker.setScreenName("Playback");
//        this._tracker.send(new HitBuilders.ScreenViewBuilder().build());
//        FlurryAgent.onStartSession(this);


        if (this._subtitleOverlay == null) {
            this._subtitleOverlay = new SubtitleOverlay(this);
//            this._subtitleOverlayRef = new SoftReference<>(this._subtitleOverlay);
        }
//        this._subtitleOverlay.setFrameScale(P.subtitleScale);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
//        if (this.pp.mp() != null) {
//            if (this._hasVideoTrack) {
//                w = this.pp.getSourceWidth();
//                h = this.pp.getSourceHeight();
//            } else {
//                w = -1;
//                h = -1;
//            }
//            this._subtitleOverlay.setVideoSize(w, h);
//        }
        this.topLayout.addView(this._subtitleOverlay, getTopLayoutZOrder(3));
        updateSubtitleOverlayLayout();


    }

    private void updateSubtitleOverlayLayout() {
//        if (this._fitSubtitleOverlayToVideo || !this._hasVideoTrack) {
        if (this._surfaceView != null) {
            matchSubtitleOverlayToVideo();
            return;
        }
//            return;
//        }
        matchSubtitleOverlayToScreen();
    }

    private int surfaceTypeForDecoder(byte decoder) {
        return 3;//decoder == 2 ? 0 : 3
    }


    private void createSurfaceView() {
        if (this._surfaceView == null && this._surfaceHolderCreated == null) {
            this._surfaceView = new SurfaceView(this);
            this._surfaceView.setId(R.id.surfaceView);
//            this._surfaceView.setBackgroundResource(R.drawable.border_style);
            SurfaceHolder holder = this._surfaceView.getHolder();
            holder.addCallback(this);
//            holder.setFormat(P.getColorFormat());
            this._surfaceType = surfaceTypeForDecoder((byte) 3);
            holder.setType(this._surfaceType);
            RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(-1, -1);
            if ((this._gestures & 6) == 0 ) {
            l.addRule(13);
            }
            this.topLayout.addView(this._surfaceView, getTopLayoutZOrder(2), l);
            if (this._subtitleOverlay != null) {
                updateSubtitleOverlayLayout();
            }
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
            if (this._coverView != null && this.topLayout.indexOfChild(this._coverView) >= 0) {
                order++;
            }
            if (type != 2) {
                if (this._subView.getParent() == this.topLayout && this.topLayout.indexOfChild(this._subView) >= 0) {
                    order++;
                }
                if (type != 3 && this._subtitleOverlay != null && this.topLayout.indexOfChild(this._subtitleOverlay) >= 0) {
                    return order + 1;
                }
                return order;
            }
            return order;
        }
        return order;
    }


    public static class TopLayout extends RelativeLayout implements Runnable {
        ActivityScreen activity;

        public TopLayout(Context context) {
            super(context);
        }

        public TopLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TopLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.activity != null) {
                post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.activity.onTopLayoutSizeChanged(this);
        }
    }


    public void onTopLayoutSizeChanged(View v) {
        if (!isFinishing()) {
            int topWidth = this.topLayout.getWidth();
            int topHeight = this.topLayout.getHeight();
//            if (this._systemWindowFittable != null && this._lock != null) {
//                if (topWidth > this._lockedTopWidth || topHeight > this._lockedTopHeight) {
//                    this._lockedTopWidth = topWidth;
//                    this._lockedTopHeight = topHeight;
//                } else {
//                    return;
//                }
//            }
//            if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
//                this._needToUpdateSizes = true;
//            } else
            if (this._surfaceView != null && ((RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams()).getRules()[13] == 0) {
                int surfaceWidth = this._surfaceView.getWidth();
                int surfaceHeight = this._surfaceView.getHeight();
                if (surfaceWidth > 4 && surfaceHeight > 4) {
                    if ((topWidth > topHeight) == (surfaceWidth > surfaceHeight)) {
                    }
                    this._panX -= 4;
                    updateSizes();
                    this._panX += 4;
                    this.handler.removeMessages(12);
                    this.handler.sendEmptyMessage(12);
                    return;
                }
                updateSizes();
            } else {
                updateSizes();
            }
        }
    }


    protected void updateSizes() {
//        this._needToUpdateSizes = false;
//        updateUserLayout();
        recalcSizingVariables();
        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
            setDisplaySize(this._zoomWidth, this._zoomHeight);
        } //else {
//            setZoomMode(this._videoZoom);
//        }
//        if (this._subtitleOverlay != null && !this._fitSubtitleOverlayToVideo) {
        matchSubtitleOverlayToScreen();
        this._subtitleOverlay.requestLayout();
//        }
        Log.e("updateSizes::", "updateSizes");

    }


    private void matchSubtitleOverlayToScreen() {
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._subtitleOverlay.getLayoutParams();
        ViewGroup parent = (ViewGroup) this._subtitleOverlay.getParent();
        if (this._zoomInsideWidth > 0.0f && this._zoomInsideHeight > 0.0f) {
            l.width = (int) this._zoomInsideWidth;
            l.height = (int) this._zoomInsideHeight;
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            if (l.width > parentWidth) {
                int gap = parentWidth - l.width;
                l.leftMargin = gap / 2;
                l.rightMargin = gap - l.leftMargin;
            } else {
                l.rightMargin = 0;
                l.leftMargin = 0;
            }
            if (l.height > parentHeight) {
                int gap2 = parentHeight - l.height;
                l.topMargin = gap2 / 2;
                l.bottomMargin = gap2 - l.topMargin;
            } else {
                l.bottomMargin = 0;
                l.topMargin = 0;
            }
            l.addRule(14, -1);
        } else {
            l.width = -1;
            l.height = -1;
            l.bottomMargin = 0;
            l.topMargin = 0;
            l.rightMargin = 0;
            l.leftMargin = 0;
        }
        this._subtitleOverlay.requestLayout();
    }


    private void matchSubtitleOverlayToVideo() {
        RelativeLayout.LayoutParams sl = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._subtitleOverlay.getLayoutParams();
        l.leftMargin = sl.leftMargin;
        l.topMargin = sl.topMargin;
        l.rightMargin = sl.rightMargin;
        l.bottomMargin = sl.bottomMargin;
        l.width = sl.width;
        l.height = sl.height;
        int[] rules = l.getRules();
        System.arraycopy(sl.getRules(), 0, rules, 0, rules.length);
        this._subtitleOverlay.requestLayout();
    }


    public static class UILayout extends RelativeLayout {
        private PlaybackController _controller;
        ActivityScreen activity;

        public UILayout(Context context) {
            super(context);
        }

        public UILayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public UILayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        void setController(PlaybackController controller) {
            this._controller = controller;
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.activity != null) {
                this.activity.onUILayoutSizeChanged(this);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case 0:
//                    this._controller.pin();
                    break;
                case 1:
                case 3:
//                    this._controller.unpin();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }


    public void onUILayoutSizeChanged(View v) {
    }

    private byte _surfaceBoundDecoder;
    private int _loadingFlags;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Log.e("handleMessage::", String.valueOf(msg.what));
        switch (msg.what) {

            case 7:
                if (this._surfaceHolderCreated != null) {

                    try {
                        pp.setDataSource("/storage/emulated/0/video.mp4");
                        this.pp.setDisplay(this._surfaceHolderCreated);

                        pp.prepare();
                        Log.e("播放的视频宽度：", String.valueOf(pp.getVideoWidth()));
                        Log.e("播放的视频高度：", String.valueOf(pp.getVideoHeight()));
//                        _zoomWidth = pp.getVideoWidth();
//                        _zoomHeight = pp.getVideoHeight();
//                        isStac = true;
//                        pp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
                                pp.start();
//                            }
//                        });

                        return true;
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "", e);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


//                if (this._surfaceHolderCreated != null) {
//                    if (this.pp.isInActiveState()) {
//                        try {
//                            this.pp.setDisplay(this._surfaceHolderCreated, 0);
////                            this._surfaceBoundDecoder = this.pp.getDecoder();
////                            updateDecoderIndicator();
//                            return true;
//                        } catch (IllegalArgumentException e) {
//                            Log.e(TAG, "", e);
//                            return true;
//                        }
//                    } else if (this.pp.isInValidState()) {
////                        load(null, this._loadingFlags, this._loadingRequestedDecoder, this._loadingVideoFlags);
//                        return true;
//                    } else {
//                        return true;
//                    }
//                }
                return true;

            default:
                return false;
            case 12:
                updateSizes();
                return true;
        }

    }

    public static final String TAG = "MX/J" + ".Screen";

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this._surfaceHolderCreated = holder;
        this.handler.sendEmptyMessage(7);
        Log.d(TAG, "=== Leave surfaceCreated.");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//        this.pp.setDisplay(null, 2);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMenuVisibilityChanged(boolean isVisible) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        int a = this._gestures;
        Log.e("this._gestures的值：", String.valueOf(a));
        return handleUITouch(v, event, a);
    }


    @SuppressLint("LongLogTag")
    public final boolean handleUITouch(View v, MotionEvent event, int gestures) {
        Log.e("","开始@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#开始");
        打印();
        Log.e("","结束@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#结束");
        float ratio;
        float height;
        float width;
        boolean reachedLimit;
//        ISubtitle subtitle;
        int action = event.getAction();
        v.getLocationOnScreen(this.screen_offset);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (this._swiping == 0) {
                    this._touchStartX = ((int) event.getX()) + this.screen_offset[0];
                    this._touchStartY = ((int) event.getY()) + this.screen_offset[1];
//                    if ((gestures & 512) != 0  && isInSubTextBoundOrBackground(this._touchStartX, this._touchStartY, SubView.SUBTITLE_DRAG_GAP_px)) {
//                        this._isSubtitleTextDragging = true;
//                        this._subView.setTextBackColor(TEXT_TOUCH_BACK_COLOR);
//                        this._subView.lockUpdate();
//                        return true;
//                    }
                    return true;
                }
                Log.e("handleUITouch::", "MotionEvent.ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_UP:
                Log.e("handleUITouch::", "MotionEvent.ACTION_UP");
            case MotionEvent.ACTION_CANCEL:
                Log.e("这里打印_swiping的值：", String.valueOf(this._swiping));
                Log.e("这里打印action的值：", String.valueOf(action));
                switch (this._swiping) {

                    case 0:
                        if (action == 1) {
                            long now = SystemClock.uptimeMillis();
                            if (v instanceof UILayout) {
                                Log.e("这里打印V的值：", "v instanceof UILayout");


                                if ((gestures & 1088) != 0 && this._lastSwiping == 0) {
                                    if ((gestures & 64) != 0) {
//                                        toggle(true);
                                        break;
                                    } else {
                                        if (this._zoomWidth != 0 && this._zoomHeight != 0) {
                                            this._zoomWidth = 0;
                                            this._zoomHeight = 0;
                                            this._panY = 0;
                                            this._panX = 0;
//                                            this._doubleTapZooming = false;
                                            setZoomMode(this._videoZoom);
                                        } else {
                                            int touchX = ((int) event.getX(0)) + this.screen_offset[0];
                                            int touchY = ((int) event.getY(0)) + this.screen_offset[1];
                                            int topWidth = this.topLayout.getWidth();
                                            int topHeight = this.topLayout.getHeight();
                                            Point size = getSizeFor(this._videoZoom);
                                            this._zoomWidth = size.x * 2;
                                            this._zoomHeight = size.y * 2;
                                            this._panX = (-(touchX - (topWidth / 2))) * 2;
                                            this._panY = (-(touchY - (topHeight / 2))) * 2;
                                            int gapX = (topWidth - this._zoomWidth) / 2;
                                            int gapY = (topHeight - this._zoomHeight) / 2;
                                            int pannedX = gapX + this._panX;
                                            int pannedY = gapY + this._panY;
                                            if (pannedX > 0) {
                                                this._panX = -gapX;
                                            } else if (this._zoomWidth + pannedX < topWidth) {
                                                this._panX = gapX;
                                            }
                                            if (pannedY > 0) {
                                                this._panY = -gapY;
                                            } else if (this._zoomHeight + pannedY < topHeight) {
                                                this._panY = gapY;
                                            }
//                                            this._doubleTapZooming = true;
                                            setDisplaySize(this._zoomWidth, this._zoomHeight);
                                        }
                                        updateLayoutForZoomPan();
                                        break;
                                    }
                                } else {
//                                    this._lastSingleTapTime = now;
//                                    switch (P.playbackTouchAction) {
//                                        case 1:
//                                            if (isInInterfacePosition(this._touchStartX, this._touchStartY)) {
//                                                showController();
//                                                break;
//                                            } else {
//                                                toggle(true);
//                                                break;
//                                            }
//                                        case 2:
//                                            if (isControllerFullyVisible()) {
//                                                this._controller.hide(2);
//                                                break;
//                                            } else {
//                                                showController();
//                                                break;
//                                            }
//                                        case 3:
//                                            if (isInInterfacePosition(this._touchStartX, this._touchStartY)) {
//                                                showController();
//                                                break;
//                                            } else if (this.pp.getTargetState() == 4) {
//                                                pause(0);
//                                                break;
//                                            } else {
//                                                start();
//                                                this._controller.hide(2);
//                                                break;
//                                            }
//                                        default:
//                                            if (isControllerFullyVisible()) {
//                                                toggle(true);
//                                                break;
//                                            } else {
//                                                showController();
//                                                break;
//                                            }
//                                    }
                                }
                            }
//                            else if (this._lockShowInterfaceOnTouched) {
//                                this._controller.setVisibleParts(3, 0);
//                                this._controller.extendVisibility();
//                                break;
//                            }
                        }
                        break;
                    case 1:
//                        this._consecutiveSeekBegin = -1;
//                        this._controller.unpin();
//                        if (isControllerSeekingVisible()) {
//                            this._controller.hide(0);
//                        }
//                        setSupremeText(null, null, false);
//                        if (this.pp.isInPlaybackState()) {
//                            this.pp.seekTo(this._seekBar.getProgress(), this.pp.getShortSeekTimeout());
//                        }
//                        resume();
                        break;
                    case 2:
//                        this._dragBar.unpin();
//                        this._dragBar.hide(true);
//                        this._dragBar = null;
                        break;
                    case 3:
                    case 17:
                    case 18:
                    case 19:
                        setSupremeText((CharSequence) null);
                        break;
                    case 33:
//                        this._subView.unlockUpdate();
//                        if (action == 1) {
//                            double distance = (event.getX() + this.screen_offset[0]) - this._touchStartX;
//                            if (distance >= SubView.SUBTITLE_SCROLL_MINIMUM_MOVE_px) {
//                                this._subView.seekAdjascent(-1);
//                                break;
//                            } else if (distance <= (-SubView.SUBTITLE_SCROLL_MINIMUM_MOVE_px)) {
//                                this._subView.seekAdjascent(1);
//                                break;
//                            }
//                        }
                        break;
                    case 34:
//                        setSupremeText((CharSequence) null);
//                        if (action == 1) {
//                            SharedPreferences.Editor editor = App.prefs.edit();
//                            editor.putInt(Key.SUBTITLE_BOTTOM_PADDING, P.subtitleBottomPaddingDp);
//                            AppUtils.apply(editor);
//                            break;
//                        }
                        break;
                    case 36:
                    case 37:
//                        this._subView.unlockUpdate();
//                        setSupremeText((CharSequence) null);
//                        if (action == 1) {
//                            SharedPreferences.Editor editor2 = App.prefs.edit();
//                            if (this._swiping == 36) {
//                                editor2.putFloat(Key.SUBTITLE_TEXT_SIZE, DeviceUtils.PixelToSP(this._subView.getTextSize()));
//                            } else {
//                                editor2.putFloat(Key.SUBTITLE_SCALE, P.subtitleScale);
//                            }
//                            AppUtils.apply(editor2);
//                            break;
//                        }
                        break;
                }
                this._lastSwiping = this._swiping;
                this._swiping = 0;
//                if (this._isSubtitleTextDragging) {
//                    this._isSubtitleTextDragging = false;
//                    this._subView.setTextBackColor(0);
//                    this._subView.unlockUpdate();
//                    return true;
//                }

                Log.e("handleUITouch::", "MotionEvent.ACTION_CANCEL");
                return true;
            case MotionEvent.ACTION_MOVE:
                switch (this._swiping) {
                    case 0:
                        int numPointer = event.getPointerCount();
                        if (numPointer == 1) {
                            Log.e("handleUITouch::", "numPointer == 1");
                            int x = ((int) event.getX()) + this.screen_offset[0];
                            int y = ((int) event.getY()) + this.screen_offset[1];
                            int direction = getDirection(this._touchStartX, this._touchStartY, x, y, DeviceUtils.dragMinimumPixel);
                            Log.e("handleUITouch:direction:", String.valueOf(direction));
                            switch (direction) {
                                case 1:
                                case 2:
                                    this._touchStartY = y;
                                    if ((gestures & 512) != 0) {
                                        this._swiping = 34;
//                                        this._startSubtitleBottomPadding = this._subView.getPaddingBottom();
                                        return true;
                                    }
//                                    boolean brightOn = (P.screenBrightnessAuto || DeviceUtils.isTV || (gestures & 32) == 0) ? false : true;
//                                    boolean volumeOn = (gestures & 16) != 0;
//                                    if (brightOn && volumeOn) {
//                                        if (this._touchStartX < this.uiLayout.getWidth() / 2) {
//                                            this._dragBar = getBrightnessBar();
//                                        } else {
//                                            this._dragBar = getSoundBar();
//                                        }
//                                    } else if (brightOn) {
//                                        this._dragBar = getBrightnessBar();
//                                    } else if (volumeOn) {
//                                        this._dragBar = getSoundBar();
//                                    } else {
//                                        return true;
//                                    }
                                    this._swiping = 2;
//                                    this._dragBar.pin();
//                                    this._dragBar.show();
//                                    this._dragBegunLevel = this._dragBar.getCurrent();
                                    return true;
                                case 3:
                                case 4:
                                    this._touchStartX = x;
//                                    if (this.pp.isSeekable()) {
                                    if ((gestures & 128) != 0) {
                                        this._subView.getLocationOnScreen(this.subview_screen_offset);
                                        if (y >= this.subview_screen_offset[1]) {
                                            this._swiping = 33;
//                                                this._subView.lockUpdate();
                                            return true;
                                        }
                                    }
                                    if ((gestures & 8) != 0) {
//                                            pause(80);
                                        this._swiping = 1;
//                                            consecutiveSeekBegin();
//                                            this._controller.pin();
//                                            if (!isControllerAnyVisible()) {
//                                                this._controller.setVisibleParts(1, 0);
//                                                return true;
//                                            }
                                        return true;
                                    }
                                    return true;
//                                    }
//                                    return true;
                                default:
                                    return true;
                            }
                        } else if (numPointer >= 2) {
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

                            Log.e("ACTION_MOVE::", "numPointer >= 2");
                            if (distance2 + distance22 >= DeviceUtils.dragMinimumPixel) {
                                int direction1 = getDirection(this._touchStartX, this._touchStartY, x2, y2, 0.0d);
                                int direction2 = getDirection(this._touchStartX2, this._touchStartY2, x22, y22, 0.0d);
                                Log.e("ACTION_MOVE::", String.valueOf(direction1));
                                Log.e("ACTION_MOVE::", String.valueOf(direction2));

                                if ((gestures & 2048) != 0 && isSameDirection(direction1, direction2, 1, 2)) {
                                    this._swiping = 3;
                                    this._touchStartY = (int) y2;
//                                    this._dragBegunLevel = getSpeed();
//                                    showPlaybackSpeedSupremeText(this._dragBegunLevel);
                                    return true;
                                }
                                if ((gestures & 256) != 0) {
//                                    this._subView.getLocationOnScreen(this.subview_screen_offset);
                                    if (y2 >= this.subview_screen_offset[1] && y22 >= this.subview_screen_offset[1]) {
                                        this._touchStartX = (int) x2;
                                        this._touchStartY = (int) y2;
                                        this._touchStartX2 = (int) x22;
                                        this._touchStartY2 = (int) y22;
                                        this._zoomDragBeginWidth = (int) Math.abs(x2 - x22);
                                        this._zoomDragBeginHeight = (int) Math.abs(y2 - y22);
                                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
//                                            int flags = subtitle.flags();
//                                            if ((5242880 & flags) != 0) {
//                                                this._startScale = P.subtitleScale;
//                                                this._swiping = 37;
//                                            } else {
//                                                this._startSubtitleTextSizeSp = DeviceUtils.PixelToSP(this._subView.getTextSize());
                                            this._swiping = 36;
//                                            }
//                                            this._subView.lockUpdate();
                                            return true;
                                        }
                                        return true;
                                    }
                                }
                                if ((gestures & 7) != 0 && this._surfaceView != null) {
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
                                        } else if ((gestures & 7) == 3) {
                                            double angle = Math.atan2(deltaY, deltaX);
                                            double angle2 = Math.atan2(deltaY2, deltaX2);
                                            boolean isPan = Math.abs(Misc.canonicalizeRadian(angle - angle2)) < 0.6283185307179586d;
                                            if (isPan) {
                                                this._swiping = 18;
                                                return true;
                                            } else if (!isSameDirection(direction1, direction2, 1, 2) && !isSameDirection(direction1, direction2, 4, 3)) {
                                                this._swiping = 17;
                                                return true;
                                            } else {
                                                return true;
                                            }
                                        } else if ((gestures & 1) != 0) {
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
//                    case 1:
//                        if (this.pp.isSeekable()) {
//                            int newPos = (int) (this._consecutiveSeekBegin + (this._gestureSeekSpeed * DeviceUtils.PixelToMeter((event.getX() + this.screen_offset[0]) - this._touchStartX)));
//                            int duration = this.pp.getDuration();
//                            if (newPos < 0) {
//                                newPos = 0;
//                            } else if (newPos >= duration) {
//                                newPos = duration - 1;
//                            }
//                            setVideoProgress(newPos);
//                            if (this._displaySeekingPosition) {
//                                setSeekSupremeText(newPos, newPos - this._consecutiveSeekBegin, null);
//                            }
//                            if (this.pp.isInPlaybackState() && showSeekPreviews()) {
//                                previewSeekTo(newPos);
//                                return true;
//                            }
//                            return true;
//                        }
//                        return true;
//                    case 2:
//                        this._dragBar.setValue((int) (this._dragBegunLevel + (this._dragBar.getMax() * DeviceUtils.PixelToDIP((this._touchStartY - event.getY()) - this.screen_offset[1]) * VERTICAL_BAR_DRAG_SPEED)));
//                        return true;
//                    case 3:
//                        if (event.getPointerCount() >= 2) {
//                            double newSpeed = PlaybackSpeedBar.getValidSpeed(this._dragBegunLevel + (((int) (DeviceUtils.PixelToDIP((this._touchStartY - event.getY()) - this.screen_offset[1]) * VERTICAL_BAR_DRAG_SPEED * 30.0f)) * 0.05d));
//                            if (this.pp.getSpeed() != newSpeed) {
//                                showPlaybackSpeedSupremeText(newSpeed);
//                                return true;
//                            }
//                            return true;
//                        }
//                        return true;
                    case 17:
                    case 18:
                    case 19:
                        if (this._surfaceView != null) {
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
                                        L.sb.setLength(0);
                                        L.sb.append((this._zoomWidth * 100) / videoWidth2).append('%');
                                        Log.e("缩放百分比：", String.valueOf((this._zoomWidth * 100) / videoWidth2) + '%');
                                        setZoomSupremeText(L.sb.toString(), this._zoomWidth, this._zoomHeight);
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
//                    case 34:
//                        float newPadding = DeviceUtils.PixelToDIP(((this._startSubtitleBottomPadding + this._touchStartY) - event.getY()) - this.screen_offset[1]);
//                        if (this.orientation == 1) {
//                            newPadding = (DeviceUtils.smallestPixels * newPadding) / DeviceUtils.largestPixels;
//                        }
//                        if (newPadding < 0.0f) {
//                            newPadding = 0.0f;
//                        } else if (newPadding > P.MAX_BOTTOM_PADDING) {
//                            newPadding = P.MAX_BOTTOM_PADDING;
//                        }
//                        P.subtitleBottomPaddingDp = Math.round(newPadding);
//                        float newPaddingPx = DeviceUtils.DIPToPixel(newPadding);
//                        this._subView.setSubtitlePadding(newPaddingPx);
//                        if (this._supremeUpdown == null) {
//                            this._supremeUpdown = getResources().getDrawable(R.drawable.supreme_updown);
//                        }
//                        L.sb.setLength(0);
//                        setSupremeText(L.sb.append(' ').append(P.subtitleBottomPaddingDp).toString(), this._supremeUpdown, false);
//                        return true;
//                    case 36:
//                    case 37:
//                        if (event.getPointerCount() >= 2) {
//                            float distanceXNow2 = Math.abs(event.getX(0) - event.getX(1));
//                            float distanceYNow2 = Math.abs(event.getY(0) - event.getY(1));
//                            if (distanceXNow2 > distanceYNow2) {
//                                ratio = distanceXNow2 / this._zoomDragBeginWidth;
//                            } else {
//                                ratio = distanceYNow2 / this._zoomDragBeginHeight;
//                            }
//                            L.sb.setLength(0);
//                            if (this._swiping == 36) {
//                                float newSizeSp = this._startSubtitleTextSizeSp * ratio;
//                                if (newSizeSp < 16.0f) {
//                                    newSizeSp = 16.0f;
//                                } else if (newSizeSp > 60.0f) {
//                                    newSizeSp = 60.0f;
//                                }
//                                this._subView.setTextSize(newSizeSp);
//                                L.sb.append(' ').append(Math.round(newSizeSp));
//                            } else {
//                                float scale = P.canonicalizeSubtitleScale(this._startScale * ratio);
//                                setSubtitleScale(scale);
//                                L.sb.append(' ').append(Math.round(100.0f * scale)).append('%');
//                            }
//                            if (this._supremeTextSize == null) {
//                                this._supremeTextSize = getResources().getDrawable(R.drawable.supreme_text_size);
//                            }
//                            setSupremeText(L.sb.toString(), this._supremeTextSize, false);
//                            return true;
//                        }
//                        return true;
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
                    if ((this._swiping & 16) != 0) {
                        if (this._surfaceView != null) {
                            int videoWidth3 = this.pp.getVideoWidth();//this.pp.getDisplayWidth();
                            int videoHeight3 = this.pp.getVideoHeight();//this.pp.getDisplayHeight();
                            if (videoWidth3 > 0 && videoHeight3 > 0) {
                                this._touchStartX = (int) x3;
                                this._touchStartY = (int) y3;
                                this._touchStartX2 = (int) x23;
                                this._touchStartY2 = (int) y23;
                                this._panStartX = this._panX;
                                this._panStartY = this._panY;
                                this._zoomDragBeginWidth = this._surfaceView.getWidth();
                                this._zoomDragBeginHeight = (this._zoomDragBeginWidth * videoHeight3) / videoWidth3;
                                return true;
                            }
                            return true;
                        }
                        return true;
                    } else if (this._swiping == 36 || this._swiping == 37) {
                        this._touchStartX = (int) x3;
                        this._touchStartY = (int) y3;
                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
                        this._zoomDragBeginWidth = (int) Math.abs(x3 - x23);
                        this._zoomDragBeginHeight = (int) Math.abs(y3 - y23);
                        if (this._zoomDragBeginWidth > 0 && this._zoomDragBeginHeight > 0) {
                            if (this._swiping == 37) {
//                                this._startScale = P.subtitleScale;
                                return true;
                            }
//                            this._startSubtitleTextSizeSp = DeviceUtils.PixelToSP(this._subView.getTextSize());
                            return true;
                        }
                        return true;
                    } else if (this._swiping == 3) {
                        this._touchStartX = (int) x3;
                        this._touchStartY = (int) y3;
                        this._touchStartX2 = (int) x23;
                        this._touchStartY2 = (int) y23;
//                        this._dragBegunLevel = getSpeed();
                        return true;
                    } else if (this._swiping == 0) {
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

    public final void setSupremeText(CharSequence text, Drawable image, boolean animate, Object token) {
        this.handler.removeMessages(4);
//        if (text != null) {
//            try {
//                this._supremeFadeOut.cancel();
//            } catch (Throwable th) {
//            }
//            this._supremeImage.setImageDrawable(image);
//            this._supremeText.setText(text);
//            TextView textView = this._supremeText;
//            if (token == null) {
//                token = text;
//            }
//            textView.setTag(token);
//            this._supremeContainer.setVisibility(0);
//            if (this._showingLoadingSplash) {
//                this._loadingSplash.hide();
//                return;
//            }
//            return;
//        }
//        this._supremeText.setTag(null);
//        if (this._supremeContainer.getVisibility() == 0) {
//            if (animate) {
//                this._supremeFadeOut.reset();
//                this._supremeContainer.startAnimation(this._supremeFadeOut);
//                return;
//            }
//            hideSupremeText();
//        }


        Log.e("setSupremeText::", "setSupremeText");


    }


    public void setDisplaySize(int width, int height) {
        Log.e("setDisplaySize::", "widthL:" + width + "  ::  height:" + height);


        if (this._surfaceView != null) {
//            if (P.videoZoomLimited && this.pp.getDecoder() != 2) {
//                width = Math.min(width, this.topLayout.getWidth());
//                height = Math.min(height, this.topLayout.getHeight());
//            }
            if (width > 0 && height > 0) {
                long now = SystemClock.uptimeMillis();
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
//                if ((l.width != width || l.height != height) && this.pp.getDecoder() != 2 && this._lastResizeTime + this._videoZoomDelay > now) {
//                    if (!this.handler.hasMessages(5)) {
//                        this.handler.sendEmptyMessageAtTime(5, this._lastResizeTime + this._videoZoomDelay);
//                    }
//                    this._delayedResizeWidth = width;
//                    this._delayedResizeHeight = height;
//                    return;
//                }
//                this._lastResizeTime = now;
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
//                if (this._subtitleOverlay != null && this._fitSubtitleOverlayToVideo) {
                matchSubtitleOverlayToVideo();
//                }
            }
        }
    }


    public final void setZoomMode(int mode) {
        Log.e("setZoomMode:uidemo:", String.valueOf(mode));
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
                setDisplaySize(this.pp.getVideoWidth(), this.pp.getVideoHeight()); //this.pp.getDisplayWidth(), this.pp.getDisplayHeight()
                break;
            case 3: //裁剪
//                Log.e("setZoomMode::3:", "(int) this._zoomCropWidth: " + String.valueOf((int) this._zoomCropWidth) + "   (int) this._zoomCropWidth: " + String.valueOf((int) this._zoomCropHeight));
                setDisplaySize((int) this._zoomCropWidth, (int) this._zoomCropHeight);
                break;
        }
        if (this._zoomButton != null) {
//            Log.e("setZoomMode::444:", " this._zoomButton != null ");
            this._zoomButton.getDrawable().setLevel(getNextZoomMode(mode));
        }
    }


    private int getNextZoomMode(int mode) {
        float multiplier;
        switch (this._videoZoom) {
            case 0:
                return 3;
            case 1:
                return 0;
            case 2:
            default:
//                if (!P.quickZoom) {
                int topWidth = this.topLayout.getWidth();
                int topHeight = this.topLayout.getHeight();
                int width = this.pp.getVideoWidth();
                int height = this.pp.getVideoHeight();
                if (width > 0) {
                    if (this._zoomWidth == 0) {
                        multiplier = 1.5f;
                    } else {
                        multiplier = 0.5f + (this._zoomWidth / width);
                    }
                    int nextZoomWidth = (int) (width * multiplier);
                    int nextZoomHeight = (int) (height * multiplier);
                    if (nextZoomWidth < topWidth && nextZoomHeight < topHeight) {
                        return 2;
                    }
                }
//                }
                return 1;
            case 3:
                return 2;
        }
    }


    public final Point getSizeFor(int zoomMode) {
        Log.e("getSizeFor:", String.valueOf(zoomMode));
        switch (zoomMode) {
            case 0:
                return new Point(this.topLayout.getWidth(), this.topLayout.getHeight());
            case 1:
            default:
                return new Point((int) this._zoomInsideWidth, (int) this._zoomInsideHeight);
            case 2:
                return new Point(this.pp.getVideoWidth(), this.pp.getVideoHeight());//this.pp.getDisplayWidth(), this.pp.getDisplayHeight()
            case 3:
                return new Point((int) this._zoomCropWidth, (int) this._zoomCropHeight);
        }
    }

    @SuppressLint("LongLogTag")
    private void updateLayoutForZoomPan() {
        if (this._surfaceView != null) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) this._surfaceView.getLayoutParams();
            l.addRule(13, 0);
            this._surfaceView.requestLayout();
//            if (this._subtitleOverlay != null) {
//                updateSubtitleOverlayLayout();
//            }
            Log.e("updateLayoutForZoomPan:::", "this._surfaceView.requestLayout()");

        } else {
            Log.e("_surfaceView:::", "_surfaceView  is null");
        }
    }


    @SuppressLint("LongLogTag")
    private void recalcSizingVariables() {
//        if (this.pp.isInActiveState()) {
        int width = 20;//this.pp.getDisplayWidth();
        int height = 30;//this.pp.getDisplayHeight();
        int topWidth = this.topLayout.getWidth();
        int topHeight = this.topLayout.getHeight();

        Log.e("topLayout.getWidth():::", String.valueOf(topLayout.getWidth()));
        Log.e("topLayout.getHeight():::", String.valueOf(topLayout.getHeight()));
        if (width > 0 && height > 0 && topWidth > 0 && topHeight > 0) {
            int idealHeight = (topWidth * height) / width;
            if (idealHeight <= topHeight) {
                this._zoomInsideWidth = topWidth;
                this._zoomInsideHeight = idealHeight;
            } else {
                int idealWidth = (topHeight * width) / height;
                this._zoomInsideWidth = idealWidth;
                this._zoomInsideHeight = topHeight;
            }
            int idealHeight2 = (topWidth * height) / width;
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
            if (this._zoomMinWidth > this._zoomInsideWidth) {
                this._zoomMinWidth = this._zoomInsideWidth;
                this._zoomMinHeight = this._zoomInsideHeight;
            }
            this._zoomMaxHeight = Math.max(topHeight, height) * MAX_ZOOM;
            this._zoomMaxWidth = (width * this._zoomMaxHeight) / height;
            if (this._zoomMaxWidth < this._zoomCropWidth) {
                this._zoomMaxWidth = this._zoomCropWidth;
                this._zoomMaxHeight = this._zoomCropHeight;
            }
            if (this._zoomWidth > 0 && this._zoomHeight > 0) {
                this._zoomHeight = (this._zoomWidth * height) / width;
            }
        }
//        }
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


    @Override
    // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
//        this._controller.extendVisibility();
        if (handleKeyEvent(event.getAction(), event.getKeyCode(), event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    private boolean handleKeyEvent(int action, int keyCode, @Nullable KeyEvent event) {
//        ScreenVerticalBar bar;
        L.sb.setLength(0);
        L.sb.append("Key pressed - action:").append(action).append(" code:").append(keyCode);
        if (event != null) {
            L.sb.append(" repeat:").append(event.getRepeatCount()).append(" meta:").append(event.getMetaState());
        }
        Log.v(TAG, L.sb.toString());
        if (action == 0) {
            this._lastDownKey = keyCode;
            switch (keyCode) {
                case 19:
                case 20:
//                    if (!isDPADNavigationRequired()) {
//                        if (P.playbackKeyUpdownAction == 2) {
//                            bar = getBrightnessBar();
//                        } else {
//                            bar = getSoundBar();
//                        }
//                        bar.show();
//                        bar.change(keyCode == 19 ? 1 : -1);
//                        return true;
//                    }
                    break;
                case 21:
//                    if (!isDPADNavigationRequired() || this._seekBar.hasFocus()) {
//                        handleKeydown_Left(1);
//                        return true;
//                    }
                    break;
                case 22:
//                    if (!isDPADNavigationRequired() || this._seekBar.hasFocus()) {
//                        handleKeydown_Right(1);
//                        return true;
//                    }
                    break;
                case 24:
                case 25:
//                    if (handleVolumeKeyDown(keyCode)) {
//                        return true;
//                    }
                    break;
                case 102:
//                    handleKeydown_Left(3);
                    return true;
                case 103:
//                    handleKeydown_Right(3);
                    return true;
                case 106:
//                    handleKeydown_Left(1);
                    return true;
                case 107:
//                    handleKeydown_Right(1);
                    return true;
            }
        } else if (action == 1 && (event == null || event.getRepeatCount() == 0)) {
            if (this._lastDownKey != keyCode) {
                handleKeyEvent(0, keyCode, null);
            }
            this._lastDownKey = 0;
            switch (keyCode) {
                case 21:
                case 22:
                case 102:
                case 103:
                case 106:
                case 107:
//                    return handleKeyup_LeftRight();
                case 23:
//                    if (!isDPADNavigationRequired()) {
//                        showController();
//                        return true;
//                    }
                    break;
                case 24:
                case 25:
//                    if (handleVolumeKeyUp(keyCode)) {
//                        return true;
//                    }
                    break;
                case 62:
                case 108:
//                    toggle(true);
//                    if (this.pp.getTargetState() == 5) {
//                        setPauseIndicatorImageLevel(2);
//                        return true;
//                    }
                    return true;
            }
        }
//        if (P.respectMediaButtons && MediaButtonReceiver.isMediaKey(keyCode)) {
//            handleMediaKeyEvent(action, keyCode);
//            return true;
//        }
        return false;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (this._autoHideInterface) {
//            this._controller.extendVisibility();
//            delaySoftButtonsHide();
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    // android.content.ContextWrapper, android.content.Context, com.mxtech.videoplayer.IPlayer
    public final Display getDisplay() {
        return getWindowManager().getDefaultDisplay();
    }





    private void setSurfaceSize(int width, int height) {
        this._surfaceHolderCreated.setFixedSize(width, height);
    }



    @Override // com.mxtech.videoplayer.Player.Client
    public void onVideoSizeChanged(int width, int height) {
        if (width > 0 && height > 0) {
//            boolean orientationSet = false;
//            if (P.screenOrientation == 99999 && !this._wasSetOrientationFromThisVideo) {
//                setOrientation(P.screenOrientation);
//                orientationSet = true;
//            }
//            if (!orientationSet) {
//                updateScreenRotationButton(P.screenOrientation);
//            }
//            if (!this._hasVideoTrack) {
//                this._hasVideoTrack = true;
//                removeCover();
//                updateStickyState(false);
//            }
            if (this._surfaceHolderCreated != null) {
                setSurfaceSize(width, height);
//                if (L.keyguardManager.inKeyguardRestrictedInputMode()) {
//                    this._needToUpdateSizes = true;
//                } else {
                    updateSizes();
//                }
            }
        }
        if (this._subtitleOverlay != null) {
//            if (this._hasVideoTrack) {
//                this._subtitleOverlay.setVideoSize(width, height);
//            } else {
//                this._subtitleOverlay.setVideoSize(-1, -1);
//            }
        }
    }

































    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("onNewIntent：", "android.support.v4.app.FragmentActivity, android.app.Activity");
//        AppUtils.dumpParams(TAG, this, "onNewIntent", intent, null);
//        returnOk(kEndByUser);
//        Uri intentUri = getIntent().getData();
//        if (!intentUri.equals(intent.getData())) {
//            Playlist.Autogen.cleanup(intentUri);
//        }
//        setIntent(intent);
//        applyIntent(intent, null);
    }

//    @Override // android.app.Activity
//    protected void onRestart() {
////        PlayService.ReturnedData detached;
////        Log.v(TAG, "onRestart (" + this + ")");
////        super.onRestart();
////        if (PlayService.instance != null && (detached = PlayService.instance.returnPlayer(this)) != null) {
////            if (detached.player == this.pp) {
////                this.pp.setClient(this);
////                replayState();
////            } else {
////                detached.close();
////            }
////        }
////        if (this.pp.getState() == 1) {
////            load(null, 1, (byte) 0, 0);
////        }
//    }
//    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
//    public void onPause() {
////        Log.v(TAG, "onPause (" + this + "): uri=" + this.pp.getUri());
////        pause(80);
////        if (L.keyguardManager.inKeyguardRestrictedInputMode() && (this.pp.getDecoder() & 6) != 0) {
//            removeSurfaceView();
////        }
////        if (this._lock != null && !this._lock.isKidsLock()) {
////            this._lock.unlock();
////        }
////        this._subView.resetUpdateLock();
////        this.pp.save();
////        super.onPause();
////        FlurryAgent.onEndSession(this);
//    }


    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle out) {
        Log.v(TAG, "onSaveInstanceState (" + this + ")");
//        saveInstanceStates(out);
//        flushChanges();
        super.onSaveInstanceState(out);
    }

    @Override
    // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Log.v(TAG, "onDestroy (" + this + ") - isFinishing() = " + isFinishing());
//        if (this._sdm != null) {
//            this._sdm.cancel();
//        }
//        if (this._lister != null) {
//            this._lister.close();
//        }
//        flushChanges();
//        if (this.pp != null && this.pp.getClient() == this) {
//            this.pp.disburden();
//            this.pp.clear();
//        }
//        App.prefs.unregisterOnSharedPreferenceChangeListener(this);
//        ScreenStyle.getInstance().removeOnChangeListener(this);
//        if (!this._switchedToBackgroundPlay) {
//            Playlist.Autogen.cleanup(getIntent().getData());
//        }
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (this._lastRequestedOrientation == Integer.MIN_VALUE) {
//            setOrientation(P.screenOrientation);
//        }
    }

//    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
//    public boolean dispatchKeyEvent(KeyEvent event) {
////        this._controller.extendVisibility();
//        if (handleKeyEvent(event.getAction(), event.getKeyCode(), event)) {
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }


    private int _lastState = 0;

    @Override // com.mxtech.videoplayer.Player.Client
    public void onStateChanged(int oldState, int newState) {
        Log.e("onStateChanged：", "oldState：" + oldState + "  newState：" + newState);
        switch (newState) {
            case -1:
                removeCover();
//                onError();
                break;
            case 0:
//                onIdle();
                break;
            case 1:
                removeCover();
                onSuspended(oldState);
                break;
            case 2:
//                onPreparing();
                break;
            case 3:
//                onPrepared();
                break;
            case 5:
//                onPaused();
                break;
            case 6:
//                if (this._consecutiveSeekBegin < 0) {
//                    onPlaybackCompleted();
//                    break;
//                } else {
                return;
//                }
        }
//        updateStickyState(newState == 3);
//        this._controller.update(newState);
        updateLayout();
        this._lastState = newState;
    }

    public final void updateLayout() {
//        updateUserLayout(this._controller.getVisibleParts(), 0);
//        updateSystemLayout();
    }


    private void removeCover() {
        if (this._coverView != null) {
            this.topLayout.removeView(this._coverView);
            this._coverView = null;
        }
//        this._lastCover = null;
    }

    private void onSuspended(int oldState) {
//        this._seekBar.setSecondaryProgress(0);
    }


    @Override // com.mxtech.videoplayer.Player.Client
    public void update(Player player, int position) {
        update(position, true);
    }

    protected void update(int pos, boolean animateHide) {
//        if (this._consecutiveSeekBegin < 0) {
//            setVideoProgress(pos);
//        }
//        this._subView.update(pos, false, animateHide);

        onPreparing();
    }







    private boolean _showingLoadingSplash;
    private ContentLoadingProgressBar _loadingSplash;
    private void onPreparing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("DAR_horz") && extras.containsKey("DAR_vert")) {
//            this.pp.setAspectRatio(extras.getFloat("DAR_horz"), extras.getFloat("DAR_vert"), false);
        }
        setFullscreen(this._fullscreen);
        if (this._surfaceHolderCreated != null) {
            this._surfaceHolderCreated.setKeepScreenOn(true);
        }
//        MediaDatabase.State restore = this.pp.getPersistent();
//        if (restore != null) {
//            this._zoomWidth = restore.zoomWidth;
//            this._zoomHeight = restore.zoomHeight;
//            this._panX = restore.panX;
//            this._panY = restore.panY;
//            Log.e("onPreparing：", "_zoomWidth：" + _zoomWidth + "  _zoomHeight：" + _zoomHeight + "  _panX：" + _panX + "  _panY：" +_panY);
//
//
//        }
        if (needDefaultControllerHeight()) {
            showController();
        }
        updateDecoderIndicator();
        if ((this._loadingFlags & 1) == 0 && APP.prefs.getBoolean("loading_circle_animation", true)) {
            this._showingLoadingSplash = true;
            this._loadingSplash.show();
        }
        updateTitle();
    }

    private void updateTitle() {
    }

    private void updateDecoderIndicator() {
    }

    private void showController() {
    }

    private boolean needDefaultControllerHeight() {
        return false;
    }

    private void setFullscreen(int fullscreen) {

    }



}