package yanzhikai.gesturetest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import yanzhikai.gesturetest.popup.VodSpeedRate;
import yanzhikai.gesturetest.popup.VodSpeedRateController;
import yanzhikai.gesturetest.popup.VodSpeedRateProcessor;
import yanzhikai.gesturetest.popup.VodSpeedSelectPopupWindow;

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
    TextView textView;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ly_VG = (VideoGestureRelativeLayout) findViewById(R.id.ly_VG);
        textView = (TextView)findViewById(R.id.textView);
        mContext=this;
        ly_VG.setVideoGestureListener(this);
//        textView.setOnGenericMotionListener(this);

        scl = (ShowChangeLayout) findViewById(R.id.scl);

        //初始化获取音量属性
        mAudioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);

        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
    }

    private float f59996F= 0.0f;

    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1){
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
        makeToast("设置进度为" + newProgress);
    }









    private VodSpeedRate f59991A = VodSpeedRate.getVodSpeedRate(1.0f);;
    private VodSpeedRate f60018y;


    /**
     * onLongPress
     *
     * @param motionEvent
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {
        this.f59996F = 0.0f;
//        this.m33491as();
        textView.setVisibility(View.VISIBLE);
        f59996F = 0.0f;
        m33491as();

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onScroll(MotionEvent motionEvent, float f, float f2) {
        Log.e("video_rate_log:VodSpeedRateController", "onScroll, distanceX : " + f + " distanceY : " + f2);
        if (f60014u != null ) {
            return;
        }
        this.m33514a(f);
    }


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
     * onThreeFingerDown
     *
     * @param motionEvent
     */
    @Override
    public void mo34772a(MotionEvent motionEvent) {

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
        textView.setVisibility(View.GONE);

        Log.e("video_rate_log:VodSpeedRateController", "onTouchUp");
        m33492ar();
        if (f59999I) {
            m33493aq();
        }
    }

    private boolean f59992B = false;;
    private boolean f59999I = false;;

    @SuppressLint("LongLogTag")
    public void m33492ar() {
        if (this.f59992B) {
            this.f59992B = false;
//            this.f60002a.m33442a(false);
//            this.f60003b.m33436a(false, null, null, this.f60011r);
//            AndroidXPanPlayerReport.m36200c(m33494ap(), this.f60018y, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
            Log.e("video_rate_log:VodSpeedRateController", "recover before speedrate mLongPressBeforeSpeedRate = " + this.f59991A.toString());
            m33481b(this.f59991A);


        }
    }
    public void m33493aq() {
        this.f59999I = false;
//        this.f60003b.m33437a(getContext().getString(R.string.vod_speed_long_press_open_vip_tip), this.f60009p);
//        VodSpeedRateProcessor vodSpeedRateProcessor = this.f60014u;
//        if (vodSpeedRateProcessor != null) {
//            vodSpeedRateProcessor.m33430a(VodSpeedRate.RATE_3_POINT, "ch_longpress");
//        }
    }




    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d(TAG, "onVolumeGesture: oldVolume " + oldVolume);
        int value = ly_VG.getHeight()/maxVolume ;
        int newVolume = (int) ((e1.getY() - e2.getY())/value + oldVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,AudioManager.FLAG_PLAY_SOUND);


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
        Log.d(TAG, "onVolumeGesture: newVolume "+ newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume/Float.valueOf(maxVolume) *100);
        if (volumeProgress >= 50){
            scl.setImageResource(R.drawable.volume_higher_w);
        }else if (volumeProgress > 0){
            scl.setImageResource(R.drawable.volume_lower_w);
        }else {
            scl.setImageResource(R.drawable.volume_off_w);
        }
        scl.setProgress(volumeProgress);
        scl.show();
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
        float newBrightness = (e1.getY() - e2.getY()) / ly_VG.getHeight() ;
        newBrightness += brightness;

        Log.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0){
            newBrightness = 0;
        }else if (newBrightness > 1){
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        scl.setProgress((int) (newBrightness * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();
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
        scl.setProgress((int) (Float.valueOf(newBrightness)/mBrightnessHelper.getMaxBrightness() * 100));
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
            newProgress = (int) (oldProgress + offset/ly_VG.getWidth() * 100);
            if (newProgress > 100){
                newProgress = 100;
            }
        }else {
            scl.setImageResource(R.drawable.fr);
            newProgress = (int) (oldProgress + offset/ly_VG.getWidth() * 100);
            if (newProgress < 0){
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

    private void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
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
//            int i = this.f60011r;
//            this.f60000J = false;
//            VodSpeedRate vodSpeedRate2 = this.f60018y;
            if (f60018y != null && f60018y == m33484az) {
                Log.e("video_rate_log:VodSpeedRateController", "当前已经是最高倍速了");
            } else {
//                this.f60000J = true;
                m33475c(m33484az);
            }
//            AndroidXPanPlayerReport.m36219a(m33494ap(), m33484az, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
//            this.f60003b.m33436a(true, m33484az, string, i);
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

        textView.setText(vodSpeedRate.getRateDescription());
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
//            return VodSpeedRate.RATE_2_POINT;
//        }
        return VodSpeedRate.RATE_1_POINT_5;
    }

    public boolean m33500aC() {
        return true;
    }

    public boolean m33501aB() {
        return true;
    }
    public boolean m33502aA() {
        return true;
    }






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
        Log.e("m33488av()0000中 i 的值：", String.valueOf(i) + " ::: "+ f60018y.getRateValue()+"   ::::  "+this.f60018y.ordinal());

        if (this.f60018y == null || i  == 1) {
            if (this.f60018y == null){
                Log.e("m33488av()中 i 的值：", "this.f60018y == null");
            }
            if (i == 1){
                Log.e("m33488av()中 i 的值：", String.valueOf(i) + " i == 1");
            }
            Log.e("m33488av()中 i 的值：", String.valueOf(i) );
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
//        this.f60003b.m33436a(true, vodSpeedRate, null, this.f60011r);
        m33490at();
//        AnchorController z = mo34960z();
//        if (z != null) {
//            z.m36097a(mo35242R().mo12156q(), 2, false);
//        }
//        AndroidXPanPlayerReport.m36205b(m33494ap(), this.f60018y, this, this.f58735h, this.f60000J, this.f59999I, m33485ay());
    }








}
