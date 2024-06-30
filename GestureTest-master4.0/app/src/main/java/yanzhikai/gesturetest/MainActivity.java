package yanzhikai.gesturetest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.ViewTarget;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import yanzhikai.gesturetest.glide.GlideRequest;
import yanzhikai.gesturetest.glide.WebPLoadHelper;
import yanzhikai.gesturetest.pop.PlayerBrightnessPopupWindow;
import yanzhikai.gesturetest.pop.PlayerVolumePopupWindow;
import yanzhikai.gesturetest.popup.VodSpeedRate;
import yanzhikai.gesturetest.popup.VodSpeedRateProcessor;
//import yanzhikai.gesturetest.util.GlideRequest;
import yanzhikai.gesturetest.util.ResourceExt;

public class MainActivity extends AppCompatActivity implements VideoGestureRelativeLayout.VideoGestureListener {
    private final String TAG = "gesturetestm";
    private VideoGestureRelativeLayout ly_VG;
    private ShowChangeLayout scl;
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    TextView myTextView;
    public static Context mContext;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ly_VG = (VideoGestureRelativeLayout) findViewById(R.id.ly_VG);
        this.mBrightnessPopWindow = new PlayerBrightnessPopupWindow(this, ly_VG);
        this.mVolumePopWindow = new PlayerVolumePopupWindow(this, this.ly_VG);


        myTextView = (TextView) findViewById(R.id.myTextView);
        mContext = this;
        ly_VG.setVideoGestureListener(this);
//        textView.setOnGenericMotionListener(this);
        imageView = findViewById(R.id.imageView);
//        Glide.with(MainActivity.this)
//                .load(R.drawable.speed_rate_icon_anim) // 使用本地drawable资源ID
//
//                .into(imageView);


        scl = (ShowChangeLayout) findViewById(R.id.scl);

        //初始化获取音量属性
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);

        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
    }

    private float f59996F = 0.0f;

    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
        makeToast("设置进度为" + newProgress);
    }

    /**
     * onLongPress
     *
     * @param motionEvent
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {
        this.f59996F = 0.0f;
//        this.m33491as();
//        myTextView.setVisibility(View.VISIBLE);
        f59996F = 0.0f;
        m33491as();
    }

    /**
     * onScroll
     *
     * @param motionEvent
     * @param f
     * @param f2
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onScroll(MotionEvent motionEvent, float f, float f2) {
        Log.e("video_rate_log:VodSpeedRateController", "onScroll, distanceX : " + f + " distanceY : " + f2);
        if (f60014u != null) {
            return;
        }
        this.m33514a(f);
    }

    /**
     * onThreeFingerDown
     *
     * @param motionEvent
     */
    @Override
    public void onThreeFingerDown(MotionEvent motionEvent) {

    }


    private VodSpeedRate f59991A = VodSpeedRate.getVodSpeedRate(1.0f);
    ;
    private VodSpeedRate f60018y;


    public void m33514a(float f) {
        float m43909a = m43909a(30.0f);
        this.f59996F += f;

        VodSpeedRate vodSpeedRate = null;
        float f2 = this.f59996F;
        if (f2 > m43909a) {
            vodSpeedRate = m33489au();
//                Toast.makeText(mContext,"f2 > m43909a::::"+f2+"  **  "+m43909a,Toast.LENGTH_LONG).show();
            this.f59996F = 0.0f;
        } else if (f2 < (-m43909a)) {
            vodSpeedRate = m33488av();
//                Toast.makeText(mContext,"f2 < (-m43909a)::::"+f2+"  **  "+(-m43909a),Toast.LENGTH_LONG).show();
            this.f59996F = 0.0f;
        }
        if (vodSpeedRate != null) {
            m33472d(vodSpeedRate);
        }

    }

    public static int m43909a(float f) {
        return (int) (TypedValue.applyDimension(1, f, getResourcesk().getDisplayMetrics()) + 0.5f);
    }

    public static Resources getResourcesk() {
        return mContext.getResources();
    }


    /**
     * onSeekUp
     *
     * @param i
     * @param i2
     */
    @Override
    public void mo33448a(int i, int i2) {

    }

    /**
     * onTouchUp
     *
     * @param motionEvent
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onTouchUp(MotionEvent motionEvent) {
//        myTextView.setVisibility(View.GONE);

        Log.e("video_rate_log:VodSpeedRateController", "onTouchUp");
        m33492ar();
//        if (f59999I) {
//            m33493aq();
//        }
    }


    private boolean f59992B = false;
    ;
    private boolean f59999I = false;
    ;

    @SuppressLint("LongLogTag")
    public void m33492ar() {
        if (this.f59992B) {
            this.f59992B = false;
            this.f60002a.showSpeedRateRemindView(false);
            this.f60003b.showSpeedRateRemindView(false, null, null, this.f60011r);
//            AndroidXPanPlayerReport.m36200c(m33494ap(), this.f60018y, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
            Log.e("video_rate_log:VodSpeedRateController", "recover before speedrate mLongPressBeforeSpeedRate = " + this.f59991A.toString());
            m33481b(this.f59991A);


        }
    }

//    public void m33493aq() {
//        this.f59999I = false;
//        this.f60003b.setLongPressSpeedRateScrollTip("会员专属，开通会员即享倍速播放", this.f60009p);
////        VodSpeedRateProcessor vodSpeedRateProcessor = this.f60014u;
////        if (vodSpeedRateProcessor != null) {
////            vodSpeedRateProcessor.m33430a(VodSpeedRate.RATE_3_POINT, "ch_longpress");
////        }
//    }

    private PlayerBrightnessPopupWindow mBrightnessPopWindow;
    private PlayerVolumePopupWindow mVolumePopWindow;

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d(TAG, "onVolumeGesture: oldVolume " + oldVolume);
        int value = ly_VG.getHeight() / maxVolume;
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);

//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);


//        int newVolume = oldVolume;

        Log.d(TAG, "onVolumeGesture: value" + value);

        //另外一种调音量的方法，感觉体验不好，就没采用
//        if (distanceY > value){
//            newVolume = 1 + oldVolume;
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
//        }else if (distanceY < -value){
//            newVolume = oldVolume - 1;
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
//        }
        Log.d(TAG, "onVolumeGesture: newVolume " + newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
//        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
//        if (volumeProgress >= 50) {
//            scl.setImageResource(R.drawable.volume_higher_w);
//        } else if (volumeProgress > 0) {
//            scl.setImageResource(R.drawable.volume_lower_w);
//        } else {
//            scl.setImageResource(R.drawable.volume_off_w);
//        }
//        scl.setProgress(volumeProgress);
//        scl.show();


        if (!this.mVolumePopWindow.isShowing()) {
            this.mVolumePopWindow.show();
        }
        this.mVolumePopWindow.updateVolume(newVolume);


    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //这是直接设置系统亮度的方法
//        if (Math.abs(distanceY) > ly_VG.getHeight()/255){
//            if (distanceY > 0){
//                setBrightness(4);
//            }else {
//                setBrightness(-4);
//            }
//        }

        //下面这是设置当前APP亮度的方法
        Log.d(TAG, "onBrightnessGesture: old" + brightness);
        float newBrightness = (e1.getY() - e2.getY()) / ly_VG.getHeight();
        newBrightness += brightness;

        Log.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
//        mLayoutParams.screenBrightness = newBrightness;
//        mWindow.setAttributes(mLayoutParams);
//        scl.setProgress((int) (newBrightness * 100));
//        scl.setImageResource(R.drawable.brightness_w);
//        scl.show();


        float f3 = 0.0f + 0.0f - e2.getRawY();


        if (newBrightness > 0) {
            if (!mBrightnessPopWindow.isShowing()) {
                mBrightnessPopWindow.show();
            }
            mBrightnessPopWindow.updateBrightness(newBrightness);

            Log.e("DDDDDDDDDDD::","DDDDDDDD");
        } else {
            if (!mBrightnessPopWindow.isShowing()) {
                mBrightnessPopWindow.show();
            }
            mBrightnessPopWindow.updateBrightness(f3);
            Log.e("DDDDDDDDDDD::","XXXXXXXXXXX");
        }


    }

    //这是直接设置系统亮度的方法
    private void setBrightness(int brightness) {
        //要是有自动调节亮度，把它关掉
        mBrightnessHelper.offAutoBrightness();

        int oldBrightness = mBrightnessHelper.getBrightness();
        Log.d(TAG, "onBrightnessGesture: oldBrightness: " + oldBrightness);
        int newBrightness = oldBrightness + brightness;
        Log.d(TAG, "onBrightnessGesture: newBrightness: " + newBrightness);
        //设置亮度
        mBrightnessHelper.setSystemBrightness(newBrightness);
        //设置显示
        scl.setProgress((int) (Float.valueOf(newBrightness) / mBrightnessHelper.getMaxBrightness() * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();

    }


    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float offset = e2.getX() - e1.getX();
        Log.d(TAG, "onFF_REWGesture: offset " + offset);
        Log.d(TAG, "onFF_REWGesture: ly_VG.getWidth()" + ly_VG.getWidth());
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            scl.setImageResource(R.drawable.ff);
            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
            if (newProgress > 100) {
                newProgress = 100;
            }
        } else {
            scl.setImageResource(R.drawable.fr);
            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
            if (newProgress < 0) {
                newProgress = 0;
            }
        }

        scl.setProgress(newProgress);
        scl.show();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        Log.d(TAG, "onSingleTapGesture: ");
        makeToast("SingleTap");
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        Log.d(TAG, "onDoubleTapGesture: ");
        makeToast("DoubleTap");
    }

    private void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    private VodSpeedRateProcessor f60014u;


    @SuppressLint("LongLogTag")
    public void m33491as() {

//            m33473c(false);
//            boolean m33485ay = m33485ay();
//            XLLog.m43797b("video_rate_log:VodSpeedRateController", "onLongPress isMaxSpeedRate = " + m33485ay);
        VodSpeedRate m33484az = m33484az();
//            VodSpeedRate vodSpeedRate = this.f60018y;
        if (f60018y != null) {
            this.f59991A = f60018y;
        }
        if (this.f60018y == null) {
//                VodSpeedRateProcessor vodSpeedRateProcessor = this.f60014u;
            if (f60014u != null && f60014u.m33418d() != null) {
                this.f60018y = this.f60014u.m33418d();
            } else {
                this.f60018y = VodSpeedRate.RATE_1_POINT_0;
            }
        }
        this.f59992B = true;
//            String string = getContext().getString(R.string.vod_speed_long_press_default_tip);
        int i = this.f60011r;
//            this.f60000J = false;
//            VodSpeedRate vodSpeedRate2 = this.f60018y;
        if (f60018y != null && f60018y == m33484az) {
            Log.e("video_rate_log:VodSpeedRateController", "当前已经是最高倍速了");
        } else {
//                this.f60000J = true;
            m33475c(m33484az);
        }
//            AndroidXPanPlayerReport.m36219a(m33494ap(), m33484az, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
        this.f60003b.showSpeedRateRemindView(true, m33484az, "长按时，左右拖动可切换倍速", i);
        m33486ax();

    }

    @SuppressLint("LongLogTag")
    private void m33486ax() {
        Log.e("video_rate_log:VodSpeedRateController", "setHadLongPress");
//        this.f60006m.m43862a("longpressed", true);
    }


    private void m33475c(VodSpeedRate vodSpeedRate) {
        m33481b(vodSpeedRate);
        m33490at();
    }

    @SuppressLint("LongLogTag")
    public void m33481b(VodSpeedRate vodSpeedRate) {
        if (vodSpeedRate == null) {
            vodSpeedRate = this.f60014u.m33418d();
        }
        this.f60018y = vodSpeedRate;
//        VodSpeedRateHelper.m33434a(mo34978i(), this.f60018y);
//        if (mo35004I() != null) {
//            mo35004I().m33558a(this.f60018y);
//        }

        Log.e("m33481b::vodSpeedRate.getRateValue:::", String.valueOf(vodSpeedRate.getRateValue()));
//        Toast.makeText(MainActivity.this,"速率：：："+vodSpeedRate.getRateValue(),Toast.LENGTH_LONG).show();

        myTextView.setText(vodSpeedRate.getRateDescription());
    }

    @SuppressLint("WrongConstant")
    private void m33490at() {

        ((Vibrator) getSystemService("vibrator")).vibrate(200L);

    }


    public VodSpeedRate m33484az() {
//        if (m33500aC()) {
//            return VodSpeedRate.RATE_4_POINT;
//        }
//        if (m33501aB()) {
//            return VodSpeedRate.RATE_3_POINT;
//        }
//        if (m33502aA()) {
        return VodSpeedRate.RATE_2_POINT;
//        }
//        return VodSpeedRate.RATE_1_POINT_5;
    }

//    public boolean m33500aC() {
//        return true;
//    }
//
//    public boolean m33501aB() {
//        return true;
//    }
//
//    public boolean m33502aA() {
//        return true;
//    }


//    private VodSpeedRate m33489au() {
//        if (this.f60018y != null) {
//            int i = C204113.f60024a[this.f60018y.ordinal()];
//            if (i != 1) {
//                if (i == 2) {
//                    if (!m33502aA()) {
//                        m33492ar();
//                        VodSpeedRateProcessor vodSpeedRateProcessor = this.f60014u;
//                        if (vodSpeedRateProcessor != null) {
//                            vodSpeedRateProcessor.m33430a(VodSpeedRate.RATE_2_POINT, "bj_longpress");
//                            return null;
//                        }
//                        return null;
//                    }
//                    return VodSpeedRate.RATE_2_POINT;
//                } else if (i == 3) {
//                    if (!m33501aB()) {
//                        m33492ar();
//                        VodSpeedRateProcessor vodSpeedRateProcessor2 = this.f60014u;
//                        if (vodSpeedRateProcessor2 != null) {
//                            vodSpeedRateProcessor2.m33430a(VodSpeedRate.RATE_3_POINT, "ch_longpress");
//                            return null;
//                        }
//                        return null;
//                    }
//                    return VodSpeedRate.RATE_3_POINT;
//                } else if (i != 4) {
//                    return null;
//                } else {
//                    if (GlobalConfigure.m40886a().m40869i().m40631aB() && VasTypeHelper.m24604b()) {
//                        return VodSpeedRate.RATE_4_POINT;
//                    }
//                    if (!GlobalConfigure.m40886a().m40869i().m40631aB() || VasTypeHelper.m24604b()) {
//                        return null;
//                    }
//                    m33492ar();
//                    VodSpeedRateProcessor vodSpeedRateProcessor3 = this.f60014u;
//                    if (vodSpeedRateProcessor3 != null) {
//                        vodSpeedRateProcessor3.m33430a(VodSpeedRate.RATE_4_POINT, "ch_longpress");
//                        return null;
//                    }
//                    return null;
//                }
//            }
//            return VodSpeedRate.RATE_1_POINT_5;
//        }
//        return null;
//    }


    public static class C204113 {

        static final int[] f60024a = new int[VodSpeedRate.values().length];

        static {
            f60024a[VodSpeedRate.RATE_1_POINT_25.ordinal()] = 1;

            f60024a[VodSpeedRate.RATE_1_POINT_5.ordinal()] = 2;

            f60024a[VodSpeedRate.RATE_2_POINT.ordinal()] = 3;

            f60024a[VodSpeedRate.RATE_3_POINT.ordinal()] = 4;

            f60024a[VodSpeedRate.RATE_4_POINT.ordinal()] = 5;
        }
    }

    private VodSpeedRate m33489au() {
        if (this.f60018y != null) {
            int i = C204113.f60024a[this.f60018y.ordinal()];


            Log.e("m33489au()中 i 的值：", String.valueOf(i));

//            if(i == 1){
//                return VodSpeedRate.RATE_1_POINT_5;
//            }else if(i == 2){
//                return VodSpeedRate.RATE_2_POINT;
//            }
//            else if(i == 3){
//                return VodSpeedRate.RATE_3_POINT;
//            }
//            else if(i == 4){
//                return VodSpeedRate.RATE_4_POINT;
//            }
//            else {
//                return null;
//            }


            if (i != 1) {
                if (i == 2) {
//                    if (!m33502aA()) {
//                        m33492ar();
//                        VodSpeedRateProcessor vodSpeedRateProcessor = this.f60014u;
//                        if (vodSpeedRateProcessor != null) {
//                            vodSpeedRateProcessor.m33430a(VodSpeedRate.RATE_2_POINT, "bj_longpress");
//                            return null;
//                        }
//                        return null;
//                    }
                    return VodSpeedRate.RATE_2_POINT;
                } else if (i == 3) {
//                    if (!m33501aB()) {
//                        m33492ar();
//                        VodSpeedRateProcessor vodSpeedRateProcessor2 = this.f60014u;
//                        if (vodSpeedRateProcessor2 != null) {
//                            vodSpeedRateProcessor2.m33430a(VodSpeedRate.RATE_3_POINT, "ch_longpress");
//                            return null;
//                        }
//                        return null;
//                    }
                    return VodSpeedRate.RATE_3_POINT;
                } else if (i != 4) {
                    return null;
                } else {
//                    if (GlobalConfigure.m40886a().m40869i().m40631aB() && VasTypeHelper.m24604b()) {
                    return VodSpeedRate.RATE_4_POINT;
//                    }
//                    if (!GlobalConfigure.m40886a().m40869i().m40631aB() || VasTypeHelper.m24604b()) {
//                        return null;
//                    }
//                    m33492ar();
//                    VodSpeedRateProcessor vodSpeedRateProcessor3 = this.f60014u;
//                    if (vodSpeedRateProcessor3 != null) {
//                        vodSpeedRateProcessor3.m33430a(VodSpeedRate.RATE_4_POINT, "ch_longpress");
//                        return null;
//                    }
//                    return null;
                }
            }
            return VodSpeedRate.RATE_1_POINT_5;
        }
        return null;
    }


    private VodSpeedRate m33488av() {
        int i = C204113.f60024a[this.f60018y.ordinal()];
        Log.e("m33488av()0000中 i 的值：", String.valueOf(i) + " ::: " + f60018y.getRateValue() + "   ::::  " + this.f60018y.ordinal());

        if (this.f60018y == null || i == 1) {
            if (this.f60018y == null) {
                Log.e("m33488av()中 i 的值：", "this.f60018y == null");
            }
            if (i == 1) {
                Log.e("m33488av()中 i 的值：", String.valueOf(i) + " i == 1");
            }
            Log.e("m33488av()中 i 的值：", String.valueOf(i));
            return null;
        }

//        if (i == 3){
//            return VodSpeedRate.RATE_1_POINT_5;
//        }else if (i == 4){
//            return VodSpeedRate.RATE_2_POINT;
//        }else if (i == 5){
//            return VodSpeedRate.RATE_3_POINT;
//        }else{
//            return VodSpeedRate.RATE_1_POINT_25;
//        }
//        if(i == 2){
//            return VodSpeedRate.RATE_1_POINT_25;
//        }else if(i == 3){
//            return VodSpeedRate.RATE_1_POINT_5;
//        }
//        else if(i == 4){
//            return VodSpeedRate.RATE_2_POINT;
//        }
//        else if(i == 5){
//            return VodSpeedRate.RATE_3_POINT;
//        }
//        else if(i == 1){
//            return VodSpeedRate.RATE_0_POINT_5;
//        }else {
//            return null;
//        }


        if (i != 2) {
            if (i != 3) {
                if (i != 4) {
                    if (i != 5) {
                        return null;
                    }
                    return VodSpeedRate.RATE_3_POINT;
                }
                return VodSpeedRate.RATE_2_POINT;
            }
            return VodSpeedRate.RATE_1_POINT_5;
        }
        return VodSpeedRate.RATE_1_POINT_25;
    }


    private void m33472d(VodSpeedRate vodSpeedRate) {
        m33481b(vodSpeedRate);
        this.f60003b.showSpeedRateRemindView(true, vodSpeedRate, null, this.f60011r);
        m33490at();
//        AnchorController z = mo34960z();
//        if (z != null) {
//            z.m36097a(mo35242R().mo12156q(), 2, false);
//        }
//        AndroidXPanPlayerReport.m36205b(m33494ap(), this.f60018y, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
    }


    PressSpeedRate f60002a = new PressSpeedRate();
    ;

    SpeedRateScroll f60003b = new SpeedRateScroll();
    ;
    private int f60009p = ResourceExt.m5920a(R.color.ui_vip_gold_C37B);
    ;

    private int f60010q = ResourceExt.m5920a(R.color.ui_vip_gold_C37B_50);
    ;

    private int f60011r = ResourceExt.m5920a(R.color.media_player_text_white);
    ;

    private int f60012s = ResourceExt.m5920a(R.color.media_player_white_60);
    ;


    public class PressSpeedRate {

        /* renamed from: b */
        private View layoutSpeedRate;

        /* renamed from: c */
        private TextView speedRateText;

        /* renamed from: d */
        private ImageView speedRateIcon;

//        private PressSpeedRate() {
//        }

        /* renamed from: a */
        @SuppressLint("LongLogTag")
        public void showSpeedRateRemindView(boolean z) {
            Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView");
            if (z) {
                if (this.layoutSpeedRate == null) {
//                    if (this.f58730c != null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.stub_speed_rate);
                    if (viewStub == null) {
                        Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView stub is null");
                        return;
                    }
                    View inflate = viewStub.inflate();
                    this.layoutSpeedRate = inflate.findViewById(R.id.layout_speed_rate);
                    this.speedRateText = (TextView) inflate.findViewById(R.id.speed_rate_text);
                    this.speedRateIcon = (ImageView) inflate.findViewById(R.id.speed_rate_icon);
//                    } else {
//                        XLLog.m43797b("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView mPlayerRootView is null");
//                        return;
//                    }
                }
//                View view = this.layoutSpeedRate;
                if (this.layoutSpeedRate != null) {
                    this.layoutSpeedRate.setVisibility(View.VISIBLE);
//                    m33443a(this.speedRateText);
                    m33511a(this.speedRateIcon, true);
                    return;
                }
                return;
            }
            Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView 隐藏view");
            if (this.layoutSpeedRate != null) {
                Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView set GONE");
                this.layoutSpeedRate.setVisibility(View.GONE);
                m33511a(this.speedRateIcon, false);
            }
        }

        /* renamed from: a */
//        @SuppressLint("LongLogTag")
//        private void m33443a(TextView textView) {
//            boolean m33497aF = true;
//            String format = m33497aF ? "会员x2.0 " : String.format(Locale.CHINA, "x%.1f ", Float.valueOf(m33484az().getRateValue()));
//            SpannableString spannableString = new SpannableString(format + "倍速中");
//            Context context = textView.getContext();
//            if (m33497aF) {
//                spannableString.setSpan(new ForegroundColorSpan(ResourceExt.m5919a(context, R.color.ui_vip_gold_C37B)), 0, format.length(), 33);
//                spannableString.setSpan(new ForegroundColorSpan(ResourceExt.m5919a(context, R.color.ui_base_white)), format.length(), spannableString.length(), 33);
//            } else {
//                spannableString.setSpan(new ForegroundColorSpan(ResourceExt.m5919a(context, R.color.ui_base_white)), 0, format.length(), 33);
//                spannableString.setSpan(new ForegroundColorSpan(ResourceExt.m5919a(context, R.color.ui_base_white)), format.length(), spannableString.length(), 33);
//            }
//            Log.e("video_rate_log:VodSpeedRateController", "rateText = " + format);
//            if (textView != null) {
//                textView.setText(spannableString);
//            }
//        }
    }

    @SuppressLint("LongLogTag")
    public void m33511a(ImageView imageView, boolean z) {
        Log.e("video_rate_log:VodSpeedRateController", "showIconAnimation isPlay = " + z);
        if (z) {
            m33482b(imageView);
        } else {
            m33512a(imageView);
        }
    }

    //        private GlideRequest<Drawable> f60004k;
    private ViewTarget<ImageView, Drawable> f60008o;

    @SuppressLint("ResourceType")
    private void m33482b(ImageView imageView) {
//        if (this.f60004k == null) {
//            this.f60004k = new WebPLoadHelper().m13358a(imageView.getContext(), R.drawable.speed_rate_icon_anim, null, 0, -1);
//            f60004k = new RequestOptions().transform()
//                    .placeholder(R.drawable.speed_rate_icon_anim).error(R.drawable.speedp_try_inject_thunder).centerCrop();
//————————————————
//            版权声明：本文为CSDN博主「牛八少爷」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//            原文链接：https://blog.csdn.net/niuba123456/article/details/86333826


//        }


        Glide.with(MainActivity.this)
                .load(R.drawable.speed_rate_icon_anim) // 使用本地drawable资源ID

                .into(imageView);
//        this.f60008o = this.f60004k.into(imageView);
//        this.f60008o.onStart();


//        Glide.with(MainActivity.mContext)
//                .load(R.drawable.speed_rate_icon_anim) // 使用本地drawable资源ID
//                .asGif()
//                .into(imageView);


//        RequestOptions options = new RequestOptions()
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.DATA);
//        Glide.with(MainActivity.mContext)
//                .asGif()
//                .load(R.drawable.speed_rate_icon_anim)
//                .apply(options)
//                .into(imageView);

    }

    private void m33512a(ImageView imageView) {
//        ViewTarget<ImageView, Drawable> viewTarget;
//        if (imageView == null || this.f60008o == null) {
//            return;
//        }
//        this.f60008o.onStop();
        imageView.setImageDrawable(null);

//        RequestOptions options = new RequestOptions()
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.DATA);
//        Glide.with(MainActivity.mContext)
//                .asGif()
//                .load(R.drawable.speed_rate_icon_anim)
//                .apply(options)
//                .into(imageView);
    }


    public class SpeedRateScroll {

        /* renamed from: b */
        private View layoutSpeedRateScroll;

        /* renamed from: c */
        private TextView speedRateText0;

        /* renamed from: d */
        private ImageView speedRateIcon0;

        /* renamed from: e */
//        private ImageView f60039e;

        /* renamed from: f */
        private TextView speedRateText1;

        /* renamed from: g */
        private ImageView speedRateIcon1;

        /* renamed from: h */
//        private ImageView f60042h;

        /* renamed from: i */
        private TextView speedRateText2;

        /* renamed from: j */
        private ImageView speedRateIcon2;

        /* renamed from: k */
//        private ImageView f60045k;

        /* renamed from: l */
        private TextView speedRateText3;

        /* renamed from: m */
        private ImageView speedRateIcon3;

        /* renamed from: n */
//        private ImageView f60048n;

        /* renamed from: o */
        private TextView speedRateText4;

        /* renamed from: p */
        private ImageView speedRateIcon4;

        /* renamed from: q */
//        private ImageView f60051q;

        /* renamed from: r */
//        private ImageView f60052r;

        /* renamed from: s */
        private TextView longPressSpeedRateScrollTip;

//        private SpeedRateScroll() {
//        }

        /* renamed from: a */
        private ImageView m33438a(VodSpeedRate vodSpeedRate) {
            int i = C204113.f60024a[vodSpeedRate.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                return null;
                            }
                            return this.speedRateIcon4;
                        }
                        return this.speedRateIcon3;
                    }
                    return this.speedRateIcon2;
                }
                return this.speedRateIcon1;
            }
            return this.speedRateIcon0;
        }

        /* renamed from: a */
//        private void m33441a() {
//            if (m35620ab()) {
////                ImageView imageView = this.f60039e;
////                if (this.f60039e != null) {
////                    this.f60039e.setVisibility(View.GONE);
////                }
////                ImageView imageView2 = this.f60042h;
////                if (this.f60042h != null) {
////                    this.f60042h.setVisibility(View.GONE);
////                }
////                ImageView imageView3 = this.f60045k;
////                if (this.f60045k != null) {
////                    this.f60045k.setVisibility(View.GONE);
////                }
////                ImageView imageView4 = this.f60048n;
////                if (this.f60048n != null) {
////                    this.f60048n.setVisibility(View.GONE);
////                }
////                ImageView imageView5 = this.f60051q;
////                if (this.f60051q != null) {
////                    this.f60051q.setVisibility(View.GONE);
////                }
////                ImageView imageView6 = this.f60052r;
////                if (this.f60052r != null) {
////                    this.f60052r.setVisibility(View.GONE);
////                    return;
////                }
//                return;
//            }
////            ImageView imageView7 = this.f60039e;
////            if (imageView7 != null) {
////                imageView7.setVisibility(View.GONE);
////            }
////            ImageView imageView8 = this.f60042h;
////            if (imageView8 != null) {
////                imageView8.setVisibility(View.GONE);
////            }
////            Set m33499aD = m33499aD();
////            if (this.f60045k != null) {
////                if (m33499aD != null && m33499aD.contains(VodSpeedRate.RATE_2_POINT)) {
////                    this.f60045k.setImageResource(R.drawable.vod_speed_rate_trail_ic);
////                } else {
////                    this.f60045k.setImageResource(R.drawable.vod_vip_round_ic);
////                }
////            }
////            if (this.f60048n != null) {
////                if (m33499aD != null && m33499aD.contains(VodSpeedRate.RATE_3_POINT)) {
////                    this.f60048n.setImageResource(R.drawable.vod_speed_rate_trail_ic);
////                } else {
////                    this.f60048n.setImageResource(R.drawable.vod_super_vip_round_ic);
////                }
////            }
////            if (this.f60051q != null) {
////                if (m33499aD != null && m33499aD.contains(VodSpeedRate.RATE_4_POINT)) {
////                    this.f60051q.setImageResource(R.drawable.vod_speed_rate_trail_ic);
////                } else {
////                    this.f60051q.setImageResource(R.drawable.vod_super_vip_round_ic);
////                }
////            }
//        }

        /* renamed from: a */
//        public void setLongPressSpeedRateScrollTip(String str, int i) {
////            TextView textView = this.longPressSpeedRateScrollTip;
//            if (this.longPressSpeedRateScrollTip != null) {
//                this.longPressSpeedRateScrollTip.setText(str);
//                this.longPressSpeedRateScrollTip.setTextColor(i);
//            }
//        }

        /* renamed from: a */
        @SuppressLint("LongLogTag")
        public void showSpeedRateRemindView(boolean z, VodSpeedRate vodSpeedRate, String str, int i) {
            Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView");
            if (z) {
                if (this.layoutSpeedRateScroll == null) {
//                    if (VodSpeedRateController.this.f58730c != null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.stub_speed_rate_scroll);
                    if (viewStub == null) {
                        Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView stub is null");
                        return;
                    }
                    View inflate = viewStub.inflate();
                    this.layoutSpeedRateScroll = inflate.findViewById(R.id.layout_speed_rate_scroll);
                    this.speedRateText0 = (TextView) inflate.findViewById(R.id.speed_rate_text_0);
                    this.speedRateIcon0 = (ImageView) inflate.findViewById(R.id.speed_rate_icon_0);
//                        this.f60039e = (ImageView) inflate.findViewById(R.id.speed_rate_tip_0);
                    this.speedRateText1 = (TextView) inflate.findViewById(R.id.speed_rate_text_1);
                    this.speedRateIcon1 = (ImageView) inflate.findViewById(R.id.speed_rate_icon_1);
//                        this.f60042h = (ImageView) inflate.findViewById(R.id.speed_rate_tip_1);
                    this.speedRateText2 = (TextView) inflate.findViewById(R.id.speed_rate_text_2);
                    this.speedRateIcon2 = (ImageView) inflate.findViewById(R.id.speed_rate_icon_2);
//                        this.f60045k = (ImageView) inflate.findViewById(R.id.speed_rate_tip_2);
                    this.speedRateText3 = (TextView) inflate.findViewById(R.id.speed_rate_text_3);
                    this.speedRateIcon3 = (ImageView) inflate.findViewById(R.id.speed_rate_icon_3);
//                        this.f60048n = (ImageView) inflate.findViewById(R.id.speed_rate_tip_3);
                    this.speedRateText4 = (TextView) inflate.findViewById(R.id.speed_rate_text_4);
                    this.speedRateIcon4 = (ImageView) inflate.findViewById(R.id.speed_rate_icon_4);
//                        this.f60051q = (ImageView) inflate.findViewById(R.id.speed_rate_tip_4);
//                        this.f60052r = (ImageView) inflate.findViewById(R.id.speed_rate_year_tip_4);
//                        if (GlobalConfigure.m40886a().m40869i().m40631aB()) {
                    this.speedRateText4.setVisibility(View.VISIBLE);
                    this.speedRateIcon4.setVisibility(View.VISIBLE);
//                            this.f60051q.setVisibility(View.VISIBLE);
//                            this.f60052r.setVisibility(View.VISIBLE);
//                        } else {
//                            this.speedRateText4.setVisibility(8);
//                            this.speedRateIcon4.setVisibility(8);
//                            this.f60051q.setVisibility(8);
//                            this.f60052r.setVisibility(8);
//                        }
                    this.longPressSpeedRateScrollTip = (TextView) inflate.findViewById(R.id.long_press_speed_rate_scroll_tip);
                    m33439a(this.speedRateText0, VodSpeedRate.RATE_1_POINT_25, false);
                    m33439a(this.speedRateText1, VodSpeedRate.RATE_1_POINT_5, false);
                    m33439a(this.speedRateText2, VodSpeedRate.RATE_2_POINT, false);
                    m33439a(this.speedRateText3, VodSpeedRate.RATE_3_POINT, false);
                    m33439a(this.speedRateText4, VodSpeedRate.RATE_4_POINT, false);
//                    } else {
//                        Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView mPlayerRootView is null");
//                        return;
//                    }
                }
//                View view = this.layoutSpeedRateScroll;
                if (this.layoutSpeedRateScroll != null) {
                    this.layoutSpeedRateScroll.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(str)) {
                        if (!f59999I) {
                            this.longPressSpeedRateScrollTip.setText("长按时，左右拖动可切换倍速");
                        }
                    } else {
                        this.longPressSpeedRateScrollTip.setText(str);
                    }
                    this.longPressSpeedRateScrollTip.setTextColor(i);
//                    m33441a();
                    m33440a(this.speedRateText0, VodSpeedRate.RATE_1_POINT_25, vodSpeedRate);
                    m33440a(this.speedRateText1, VodSpeedRate.RATE_1_POINT_5, vodSpeedRate);
                    m33440a(this.speedRateText2, VodSpeedRate.RATE_2_POINT, vodSpeedRate);
                    m33440a(this.speedRateText3, VodSpeedRate.RATE_3_POINT, vodSpeedRate);
                    m33440a(this.speedRateText4, VodSpeedRate.RATE_4_POINT, vodSpeedRate);
                    m33511a(this.speedRateIcon0, false);
                    m33511a(this.speedRateIcon1, false);
                    m33511a(this.speedRateIcon2, false);
                    m33511a(this.speedRateIcon3, false);
                    m33511a(this.speedRateIcon4, false);
                    ImageView m33438a = m33438a(vodSpeedRate);
                    if (m33438a != null) {
                        m33511a(m33438a, true);
                        return;
                    }
                    return;
                }
                return;
            }
            Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView 隐藏view");
            if (this.layoutSpeedRateScroll != null) {
                Log.e("video_rate_log:VodSpeedRateController", "showSpeedRateRemindView set GONE");
                this.layoutSpeedRateScroll.setVisibility(View.GONE);
                m33511a(this.speedRateIcon0, false);
                m33511a(this.speedRateIcon1, false);
                m33511a(this.speedRateIcon2, false);
                m33511a(this.speedRateIcon3, false);
                m33511a(this.speedRateIcon4, false);
            }
        }

        /* renamed from: a */
        private void m33440a(TextView textView, VodSpeedRate vodSpeedRate, VodSpeedRate vodSpeedRate2) {
            if (vodSpeedRate == vodSpeedRate2) {
                if (vodSpeedRate == VodSpeedRate.RATE_1_POINT_25 || vodSpeedRate == VodSpeedRate.RATE_1_POINT_5) {
                    textView.setTextColor(f60011r);
                } else {
                    textView.setTextColor(f60009p);
                }
            } else if (vodSpeedRate == VodSpeedRate.RATE_1_POINT_25 || vodSpeedRate == VodSpeedRate.RATE_1_POINT_5) {
                textView.setTextColor(f60012s);
            } else {
                textView.setTextColor(f60010q);
            }
        }

        /* renamed from: a */
        @SuppressLint("LongLogTag")
        private void m33439a(TextView textView, VodSpeedRate vodSpeedRate, boolean z) {
            String format;
            if (vodSpeedRate == VodSpeedRate.RATE_1_POINT_25) {
                format = String.format(Locale.CHINA, "x%.2f ", Float.valueOf(vodSpeedRate.getRateValue()));
            } else {
                format = String.format(Locale.CHINA, "x%.1f ", Float.valueOf(vodSpeedRate.getRateValue()));
            }
            Log.e("video_rate_log:VodSpeedRateController", "rateText = " + format);
            if (textView != null) {
                textView.setText(format);
            }
        }
    }


    protected int f58731d = 0;

//    public boolean m35620ab() {
//        return true;//this.f58731d == 2;
//    }
//
//    public void mo33223b(int i) {
//        this.f58731d = i;
//    }
//
//    public Set<VodSpeedRate> m33499aD() {
//        HashSet hashSet = new HashSet();
//        hashSet.add(VodSpeedRate.RATE_2_POINT);
//        hashSet.add(VodSpeedRate.RATE_3_POINT);
//        return hashSet;
//
//    }


}
