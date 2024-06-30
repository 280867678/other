package yanzhikai.gesturetest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: yanzhikai
 * Description: 封装了GestureDetector的Layout，提供VideoGestureListener接口给外部处理手势
 * Email: yanzhikai_yjk@qq.com
 */

public class VideoGestureRelativeLayout extends RelativeLayout {
    private static final String TAG = "gesturetest";
    private static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2, FF_REW = 3, LONG_PRESS = 4;
    private static Context mContext;
    private
    @ScrollMode
    int mScrollMode = NONE;

    @IntDef({NONE, VOLUME, BRIGHTNESS, FF_REW})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollMode {
    }

    private VideoPlayerOnGestureListener mOnGestureListener;
    private GestureDetector mGestureDetector;
    private VideoGestureListener mVideoGestureListener;
    //横向偏移检测，让快进快退不那么敏感
    private int offsetX = 1;
    private boolean hasFF_REW = false;

    public VideoGestureRelativeLayout(Context context) {
        super(context);
        mContext=context;
        init(context);
    }

    public VideoGestureRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init(context);
    }

    private void init(Context context) {
        mOnGestureListener = new VideoPlayerOnGestureListener(this);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        //取消长按，不然会影响滑动
//        mGestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch: event:" + event.toString());
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (hasFF_REW) {
                        if (mVideoGestureListener != null) {
                            mVideoGestureListener.onEndFF_REW(event);
                        }
                        hasFF_REW = false;
                    }

                    /**
                     * thunder
                     */
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onTouchUp(event);
                    }
                    isLongClick = false;
                }

                /**
                 * thunder
                 */
                onTouchs(event);
                //监听触摸事件
                return mGestureDetector.onTouchEvent(event);
            }
        });

//        setOnGenericMotionListener(new OnGenericMotionListener() {
//            @Override
//            public boolean onGenericMotion(View v, MotionEvent event) {
//                Log.d(TAG, "onGenericMotion: " + event.toString());
//                //监听鼠标点击事件
//                return mGestureDetector.onGenericMotionEvent(event);
//            }
//        });
    }

    private float rawX;
    private float rawY;
    /**
     * onThreeFingerDown
     * 当触摸事件为按下时（actionMasked == 0），记录下当前的触摸位置；
     * 当触摸事件为移动时（actionMasked == 2），计算移动的距离，并调用 onScroll 方法进行相应处理；
     * 当触摸事件为三个手指按下时（actionMasked == 5），并且当前触摸点数为3时，调用 mo34772a 方法进行处理
     * @param motionEvent
     * @return
     */
    public boolean onTouchs(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.rawX = motionEvent.getRawX();
            this.rawY = motionEvent.getRawY();
        } else if (actionMasked != 1) {
            if (actionMasked == 2) {
                float rawX = motionEvent.getRawX() - this.rawX;
                float rawY = motionEvent.getRawY() - this.rawY;
                this.rawX = motionEvent.getRawX();
                this.rawY = motionEvent.getRawY();
                if (Math.abs(rawX) > 0.0f || Math.abs(rawY) > 0.0f) {

                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onScroll(motionEvent, rawX, rawY);
                    }
                }
            } else if (actionMasked == 5) {

                Log.e("PlayerGestureView", "onThreeFingerDown, checkThreeFingerDown getPointerCount = " + motionEvent.getPointerCount());
                this.rawX = motionEvent.getRawX();
                this.rawY = motionEvent.getRawY();
                if (motionEvent.getPointerCount() == 3 && this.mVideoGestureListener != null) {
                    Log.e("PlayerGestureView", "onThreeFingerDown, mOnGestureListener");
                    this.mVideoGestureListener.mo34772a(motionEvent);
                }
            }
        }
        return true;
    }


    long timeDown;
    boolean isLongClick;
    long timeMove;
    long durationMs;
    public class VideoPlayerOnGestureListener extends GestureDetector.SimpleOnGestureListener {


        private VideoGestureRelativeLayout videoGestureRelativeLayout;

        public VideoPlayerOnGestureListener(VideoGestureRelativeLayout videoGestureRelativeLayout) {
            this.videoGestureRelativeLayout = videoGestureRelativeLayout;
        }

        private float f59275d;
        private float f59276e;

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            this.f59275d = e.getRawX();
            this.f59276e = e.getRawY();
             timeDown = System.currentTimeMillis();
             isLongClick = false;
            hasFF_REW = false;
            //每次按下都重置为NONE
            mScrollMode = NONE;
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onDown(e);
            }
            return true;
        }



        @SuppressLint("WrongConstant")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: e1:" + e1.getX() + "," + e1.getY());
            Log.d(TAG, "onScroll: e2:" + e2.getX() + "," + e2.getY());
            Log.d(TAG, "onScroll: X:" + distanceX + "  Y:" + distanceY);
             timeMove = System.currentTimeMillis();
             durationMs = timeMove - timeDown;
            if (durationMs > 500) {
                vibrate();
                isLongClick = true;
                Log.d(TAG, "onTouch: isLongClick=" + isLongClick);
                //长按事件，可以移动
//                moveX = event.getRawX();
//                moveY = event.getRawY();
//                //移动的距离
//                float dx = moveX - downX;
//                float dy = moveY - downY;
//                //重新设置控件的位置。移动
//                ViewGroup.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) tvTitle.getLayoutParams();
//                params.leftMargin += dx;
//                params.topMargin += dy;
//                tvTitle.setLayoutParams(params);
//                //重置
//                downX = moveX;
//                downY = moveY;
            }
//————————————————
//            版权声明：本文为CSDN博主「张雨zy」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//            原文链接：https://blog.csdn.net/yu540135101/article/details/103810310
            switch (mScrollMode) {
                case NONE:
                    Log.d(TAG, "NONE: ");
                    //offset是让快进快退不要那么敏感的值
                    if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
                        mScrollMode = FF_REW;
                    } else {
                        if (e1.getX() < getWidth() / 2) {
                            mScrollMode = BRIGHTNESS;
                        } else {
                            mScrollMode = VOLUME;
                        }
                    }
//                    if(isLongClick){
//                        mScrollMode = LONG_PRESS;
//                    }
                    break;
                case VOLUME:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                    }
                    Log.d(TAG, "VOLUME: ");
                    break;
                case BRIGHTNESS:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                    }
                    Log.d(TAG, "BRIGHTNESS: ");
                    break;
                case FF_REW:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onFF_REWGesture(e1, e2, distanceX, distanceY);
                    }
                    hasFF_REW = true;
                    Log.d(TAG, "FF_REW: ");
                    break;
                case LONG_PRESS:
                    if (mVideoGestureListener != null) {
//                        mVideoGestureListener.onLongPress(e1, e2, distanceX, distanceY);
                    }
                    Log.d(TAG, "LONG_PRESS: ");
                    break;
            }
            return true;
        }


        @Override
        public boolean onContextClick(MotionEvent e) {
            Log.d(TAG, "onContextClick: ");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: ");
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onDoubleTapGesture(e);
            }
            return super.onDoubleTap(e);
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onLongPress(MotionEvent e) {
            isLongClick = true;
            mScrollMode = LONG_PRESS;
            Log.d(TAG, "onLongPress: ");
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onLongPress(e);
            }
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: ");
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onSingleTapGesture(e);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public void setVideoGestureListener(VideoGestureListener videoGestureListener) {
        mVideoGestureListener = videoGestureListener;
    }

    /**
     * 用于提供给外部实现的视频手势处理接口
     */

    public interface VideoGestureListener {
        /**
         * 亮度手势，手指在Layout左半部上下滑动时候调用
         */
        public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        /**
         * /音量手势，手指在Layout右半部上下滑动时候调用
         */
        public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        /**
         * /快进快退手势，手指在Layout左右滑动的时候调用
         */
        public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        /**
         * 单击手势，确认是单击的时候调用
         */
        public void onSingleTapGesture(MotionEvent e);

        /**
         * /双击手势，确认是双击的时候调用
         */
        public void onDoubleTapGesture(MotionEvent e);

        /**
         * /按下手势，第一根手指按下时候调用
         */
        public void onDown(MotionEvent e);

        /**
         * /快进快退执行后的松开时候调用
         */
        public void onEndFF_REW(MotionEvent e);

        /**
         *
         * onLongPress
         */
        void onLongPress(MotionEvent motionEvent);

        void onScroll(MotionEvent motionEvent, float f, float f2);

        /**
         * onThreeFingerDown
         * @param motionEvent
         */
        void mo34772a(MotionEvent motionEvent);

        /**
         * onSeekUp
         * @param i
         * @param i2
         */
        void mo33448a(int i, int i2);

        /**
         * onTouchUp
         */
        void onTouchUp(MotionEvent motionEvent);

    }


    private void vibrate() {
        if (!isLongClick) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }
    }
//————————————————
//    版权声明：本文为CSDN博主「张雨zy」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/yu540135101/article/details/103810310



    public interface InterfaceC20234a {
        /* renamed from: a */


        /* renamed from: a */


        /* renamed from: a */


        /**
         *
         * onEnterPositionState
         */
        boolean mo34773a();

        /**
         * onEnterLightState
         */
        boolean mo34771b();

        /**
         * onDoubleClick---
         * @param motionEvent
         * @return
         */
        boolean mo34770b(MotionEvent motionEvent);

        /**
         * onEnterVolumeState
         */
        boolean mo34769c();

        /**
         * onSingleClick---
         * @param motionEvent
         * @return
         */
        boolean mo34768c(MotionEvent motionEvent);





        /**
         * onDown
         */
        void mo34767f(MotionEvent motionEvent);
    }

}
