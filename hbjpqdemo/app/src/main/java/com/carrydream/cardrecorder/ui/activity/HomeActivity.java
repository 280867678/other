package com.carrydream.cardrecorder.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import com.carrydream.cardrecorder.Model.MlData;
import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.PokerGame.CustomPoker;
import com.carrydream.cardrecorder.PokerGame.Poker;
import com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules;
import com.carrydream.cardrecorder.PokerGame.Rule.ClassicsRules;
import com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules;
import com.carrydream.cardrecorder.PokerGame.Rule.HappyGameRule;
import com.carrydream.cardrecorder.PokerGame.Rule.HuanLeAppRules;
import com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules;
import com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules;
import com.carrydream.cardrecorder.PokerGame.Rule.JJRules;
import com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules;
import com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules;
import com.carrydream.cardrecorder.adapter.TabFragmentPagerAdapter;
import com.carrydream.cardrecorder.adapter.WindowAdapter;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.base.BaseMessage;
import com.carrydream.cardrecorder.datapool.DataPool;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.DensityUtils;
import com.carrydream.cardrecorder.tool.GlideEngine;
import com.carrydream.cardrecorder.tool.MyToast;
import com.carrydream.cardrecorder.tool.NotificationHelper;
import com.carrydream.cardrecorder.tool.OnOcr;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.ui.activity.HomeActivity;
import com.carrydream.cardrecorder.ui.dialog.AgreementDialog;
import com.carrydream.cardrecorder.ui.dialog.NoticeDialog;
import com.carrydream.cardrecorder.ui.dialog.PowerDialog;
import com.carrydream.cardrecorder.ui.dialog.VipTipsDialog;
import com.carrydream.cardrecorder.ui.fragment.AccountFragment;
import com.carrydream.cardrecorder.ui.fragment.HomeFragment;
import com.carrydream.cardrecorder.ui.fragment.AccountFragment;
import com.carrydream.cardrecorder.ui.fragment.HomeFragment;
import com.carrydream.cardrecorder.widget.NoScrollViewPager;
import com.hb.aiyouxiba.R;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.mask.mediaprojection.interfaces.ScreenCaptureCallback;
import com.mask.mediaprojection.utils.MediaProjectionHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

/* loaded from: classes.dex */
public class HomeActivity extends BaseActivity {
    public static int PageChange = 0;
    public static String mldata = "good_data";
    Poker customPoker;
    TextView last;
    public String mldataPath;
    TextView next;


    RecyclerView recycler;



    @BindView(R.id.tab_img_1)
    ImageView tab_img_1;
    @BindView(R.id.tab_img_2)
    ImageView tab_img_2;
    @BindView(R.id.tab_txt_1)
    TextView tab_txt_1;
    @BindView(R.id.tab_txt_2)
    TextView tab_txt_2;
    TimerTask task;
    Timer timer;
    @BindView(R.id.viewpage)
    NoScrollViewPager viewpage;
    WindowAdapter windowAdapter;
    List<Fragment> fragments = new ArrayList();
    List<ImageView> imageViews = new ArrayList();
    List<TextView> textViews = new ArrayList();
    String FILE_NAME = "tessdata";
    String LANGUAGE_NAME = "chi_sim.traineddata";
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.8
        @Override // org.opencv.android.BaseLoaderCallback, org.opencv.android.LoaderCallbackInterface
        public void onManagerConnected(int i) {
            if (i == 0) {
                Log.i("hjw", "OpenCV 加载  成功");
            } else {
                super.onManagerConnected(i);
            }
        }
    };

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_home;
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {

        initWidget();

        MediaProjectionHelper.getInstance().setNotificationEngine(new MediaProjectionNotificationEngine() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda30
            @Override // com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine
            public final Notification getNotification() {
                return m56xef8edd04();
            }
        });
        Log.e("HomeActivity：", "init");
        initReceive();
        initDataOpenCV();
        initDialog();
        if (DataUtils.getInstance().getPoker() == null) {
            DataUtils.getInstance().setPoker(Tool.initData());
            DataUtils.getInstance().setHand(17);
        }
        this.customPoker = new CustomPoker().setData(DataUtils.getInstance().getPoker());
        setOnOcr();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ Notification m56xef8edd04() {
        String string = getString(R.string.app_name);
        return NotificationHelper.getInstance().createSystem().setOngoing(true).setTicker(string).setContentText(string).setDefaults(-1).build();
    }

    private void initWidget() {
        this.imageViews.add(this.tab_img_1);
        this.imageViews.add(this.tab_img_2);
        this.textViews.add(this.tab_txt_1);
        this.textViews.add(this.tab_txt_2);
        this.fragments.add(new HomeFragment());
        this.fragments.add(new AccountFragment());
        this.viewpage.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), this.fragments));
        this.viewpage.setCurrentItem(PageChange);
        this.viewpage.setEnabled(false);
        this.viewpage.setOffscreenPageLimit(this.fragments.size());
        this.viewpage.setOnPageChangeListener(new MyPagerChangeListener());
        this.imageViews.get(0).setSelected(true);
        this.textViews.get(0).setSelected(true);
    }

    @OnClick({R.id.tab1, R.id.tab2, R.id.start, R.id.linearLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linearLayout /* 2131231062 */:
                EasyPhotos.createAlbum((FragmentActivity) this, true, true, GlideEngine.getInstance()).start(new SelectCallback() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.1
                    @Override // com.huantansheng.easyphotos.callback.SelectCallback
                    public void onCancel() {
                    }

                    @Override // com.huantansheng.easyphotos.callback.SelectCallback
                    public void onResult(ArrayList<Photo> arrayList, boolean z) {
                        partition(BitmapFactory.decodeFile(new File(arrayList.get(0).path).getAbsolutePath()), DataUtils.getInstance().getGamePlatform());
                    }
                });
                return;
            case R.id.start /* 2131231279 */:
//                if (DataUtils.getInstance().getUser() == null) {
//                    startActivity(new Intent(this, LoginActivity.class));
//                    return;
//                }
//                else if (DataUtils.getInstance().getUser().getUser().getIs_vip() == 0) {
//                    VipTipsDialog vipTipsDialog = new VipTipsDialog(this);
//                    vipTipsDialog.setProtocol(new VipTipsDialog.protocol() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.2
//                        @Override // com.carrydream.cardrecorder.ui.dialog.VipTipsDialog.protocol
//                        public void no() {
//                        }
//
//                        @Override // com.carrydream.cardrecorder.ui.dialog.VipTipsDialog.protocol
//                        public void yes() {
//                            startActivity(new Intent(HomeActivity.this, VipActivity.class));
//                        }
//                    });
//                    vipTipsDialog.show();
//                    return;
//                }
//                else
                    if (!XXPermissions.isGranted(this, Permission.SYSTEM_ALERT_WINDOW)) {
                    PowerDialog powerDialog = new PowerDialog(this);
                    powerDialog.setPower(new PowerDialog.Power() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda29
                        @Override // com.carrydream.cardrecorder.ui.dialog.PowerDialog.Power
                        public final void open() {
                            m58x1deb830a();
                        }
                    });
                    powerDialog.show();
                    return;
                } else {
                    boolean doServiceStart = doServiceStart();
                    Log.e("开启服务：", String.valueOf(doServiceStart));
                    return;
                }
            case R.id.tab1 /* 2131231291 */:
                PageChange = 0;
                this.viewpage.setCurrentItem(0);
                return;
            case R.id.tab2 /* 2131231292 */:
                PageChange = 1;
                this.viewpage.setCurrentItem(1);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$1$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m58x1deb830a() {
        if (doServiceStart()) {
            return;
        }
        EasyFloat();
    }

    private void initReceive() {
        Log.e("HomeActivity：", "initReceive");
        MyBroadcastReceive myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(myBroadcastReceive, intentFilter);
    }

    private void initDataOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.e("hjw", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, this.mLoaderCallback);
        } else {
            Log.e("hjw", "OpenCV library found inside package. Using it!");
        }
        this.mldataPath = getExternalCacheDir() + File.separator + mldata;

        Log.e("路径打印：", this.mldataPath);

        new MlThread(this.mldataPath).start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.carrydream.cardrecorder.ui.activity.HomeActivity$3  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass3 implements OnOcr {
        AnonymousClass3() {
        }

        
        @Override // com.carrydream.cardrecorder.tool.OnOcr
        public void NewRound(HashMap<String, Integer> hashMap) {
            if (windowAdapter != null) {
                windowAdapter.setNewInstance(Tool.initDataZdy(hashMap));
                windowAdapter.notifyDataSetChanged();
            }
            if (next == null || last == null) {
                return;
            }
            runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HomeActivity.AnonymousClass3.this.m95xcab439f5();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$NewRound$0$com-carrydream-cardrecorder-ui-activity-HomeActivity$3  reason: not valid java name */
        public /* synthetic */ void m95xcab439f5() {
            next.setText("");
            last.setText("");
        }

        
        @Override // com.carrydream.cardrecorder.tool.OnOcr
        public void output(HashMap<String, Integer> hashMap) {
            if (windowAdapter != null) {
                windowAdapter.setNewInstance(Tool.initDataZdy(hashMap));
                windowAdapter.notifyDataSetChanged();
            }
        }

        @Override // com.carrydream.cardrecorder.tool.OnOcr
        public void next(final List<List<String>> list) {
            if (next != null) {
                runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$3$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        HomeActivity.AnonymousClass3.this.m97xb152feef(list);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$next$1$com-carrydream-cardrecorder-ui-activity-HomeActivity$3  reason: not valid java name */
        public /* synthetic */ void m97xb152feef(List list) {
            StringBuffer stringBuffer = new StringBuffer();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                for (Object str : (List) it.next()) {
                    stringBuffer.append(str.toString());
                }
                stringBuffer.append(",");
            }
            next.setText(stringBuffer);
        }

        @Override // com.carrydream.cardrecorder.tool.OnOcr
        public void last(final List<List<String>> list) {
            if (last != null) {
                runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$3$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        HomeActivity.AnonymousClass3.this.m96xd201e38b(list);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$last$2$com-carrydream-cardrecorder-ui-activity-HomeActivity$3  reason: not valid java name */
        public /* synthetic */ void m96xd201e38b(List list) {
            StringBuffer stringBuffer = new StringBuffer();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                for (Object str : (List) it.next()) {
                    stringBuffer.append(str.toString());
                }
                stringBuffer.append(",");
            }
            last.setText(stringBuffer);
        }
    }

    private void setOnOcr() {
        this.customPoker.setOnOcr(new AnonymousClass3());
    }

    private void initDialog() {
        new Handler().postDelayed(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                m57xd493c19a();
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initDialog$2$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m57xd493c19a() {
        if (!DataUtils.getInstance().getProtocol()) {
            AgreementDialog agreementDialog = new AgreementDialog(this);
            agreementDialog.setProtocol(new AgreementDialog.protocol() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.4
                @Override // com.carrydream.cardrecorder.ui.dialog.AgreementDialog.protocol
                public void yes() {
                    DataUtils.getInstance().setProtocol(true);
                    new NoticeDialog(HomeActivity.this).show();
                }

                @Override // com.carrydream.cardrecorder.ui.dialog.AgreementDialog.protocol
                public void no() {
                    finish();
                }
            });
            agreementDialog.show();
            return;
        }
        new NoticeDialog(this).show();
    }

    
    @Override // com.carrydream.cardrecorder.base.BaseActivity
    public void onEventBase(BaseMessage<String> baseMessage) {
        super.onEventBase(baseMessage);
        if (baseMessage.getCode() == 0) {
            this.customPoker = new CustomPoker().setData(DataUtils.getInstance().getPoker());
            setOnOcr();
            WindowAdapter windowAdapter = this.windowAdapter;
            if (windowAdapter != null) {
                windowAdapter.setNewInstance(Tool.initDataZdy(DataUtils.getInstance().getPoker()));
                this.windowAdapter.notifyDataSetChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            MediaProjectionHelper.getInstance().createVirtualDisplay(i, i2, intent, true, true);
            EasyFloat();
            return;
        }
        MyToast.show("授权取消");
        MediaProjectionHelper.getInstance().stopService(this);
    }

    /* loaded from: classes.dex */
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        public MyPagerChangeListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            if (i == 0) {
                for (ImageView imageView : imageViews) {
                    if (imageView.getId() == tab_img_1.getId()) {
                        imageView.setSelected(true);
                    } else {
                        imageView.setSelected(false);
                    }
                }
                for (TextView textView : textViews) {
                    if (textView.getId() == tab_txt_1.getId()) {
                        textView.setSelected(true);
                    } else {
                        textView.setSelected(false);
                    }
                }
            } else if (i == 1) {
                for (ImageView imageView2 : imageViews) {
                    if (imageView2.getId() == tab_img_2.getId()) {
                        imageView2.setSelected(true);
                    } else {
                        imageView2.setSelected(false);
                    }
                }
                for (TextView textView2 : textViews) {
                    if (textView2.getId() == tab_txt_2.getId()) {
                        textView2.setSelected(true);
                    } else {
                        textView2.setSelected(false);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyBroadcastReceive extends BroadcastReceiver {
        MyBroadcastReceive() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.e("getType:", String.valueOf(getWindowManager().getDefaultDisplay().getRotation()) + isAppOnForeground());
            if (String.valueOf(getWindowManager().getDefaultDisplay().getRotation()).equals("0")) {
                Log.e("startTime", "______________________stopTime");
                stopTime();
            } else {
                Log.e("startTime", "______________________startTime");
                startTime();
            }
        }
    }

    private boolean doServiceStart() {
        return MediaProjectionHelper.getInstance().startService(this);
    }

    private void doServiceStop() {
        MediaProjectionHelper.getInstance().stopService(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTime() {
        Log.e("startTime", "______________________111");
        if (isAppOnForeground()) {
            return;
        }
        Log.e("startTime", "______________________222");
        this.timer = new Timer();
        this.task  = new TimerTask() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.5
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                try {
                    Log.e("hjw", "______________________");
                    doScreenCapture();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", e.getMessage());
                }
            }
        };

        this.timer.schedule(task, 1000L, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTime() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
        }
        this.timer = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScreenCapture() {
        Log.e("doScreenCapture", "ScreenCapture onSuccess");
        MediaProjectionHelper.getInstance().capture(new ScreenCaptureCallback() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.6
            @Override // com.mask.mediaprojection.interfaces.ScreenCaptureCallback
            public void onBitmap(Bitmap bitmap, boolean z) {
                super.onBitmap(bitmap, z);
            }

            @Override // com.mask.mediaprojection.interfaces.ScreenCaptureCallback
            public void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
                Log.e("TAG", "ScreenCapture onSuccess");
                Tool.saveBitmapToFile(HomeActivity.this, bitmap, "图片大小", "图片");
                partition(bitmap, DataUtils.getInstance().getGamePlatform());
            }

            @Override // com.mask.mediaprojection.interfaces.ScreenCaptureCallback
            public void onFail() {
                super.onFail();
                Log.e("TAG", "ScreenCapture onFail");
            }
        });
    }

    public void partition(Bitmap bitmap, Platform platform) {
        Bitmap bitmap2 = getBitmap(bitmap, 0, bitmap.getHeight() / 2, bitmap.getWidth(), (bitmap.getHeight() / 2) - DensityUtils.dp2px(this, 40.0f), platform.getConfig().getGray());
        int width = bitmap.getWidth() / 2;
        int height = bitmap.getHeight() / 2;
        int i = height / 4;
        int ratio = (int) (width * platform.getConfig().getRatio());
        int i2 = width - ratio;
        int i3 = height - i;
        Bitmap bitmap3 = getBitmap(bitmap, ratio, i, i2, i3, platform.getConfig().getGrayscale());
        Bitmap bitmap4 = getBitmap(bitmap, width - 20, i, i2 + 20, i3, platform.getConfig().getGrayscale());
        switch (platform.getId()) {
            case 1:
                HuanLeRules.Enable(this, bitmap2, 0, new HuanLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda9
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m79xee332a61(i4, list);
                    }
                });
                HuanLeRules.Enable(this, bitmap4, 1, new HuanLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda10
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m89x84ea900(i4, list);
                    }
                });
                HuanLeRules.Enable(this, bitmap3, 2, new HuanLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda12
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m90x226a279f(i4, list);
                    }
                });
                return;
            case 2:
                TiantianRules.Enable(this, bitmap2, 0, new TiantianRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda23
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m91x3c85a63e(i4, list);
                    }
                });
                TiantianRules.Enable(this, bitmap4, 1, new TiantianRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda24
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m92x56a124dd(i4, list);
                    }
                });
                TiantianRules.Enable(this, bitmap3, 2, new TiantianRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda25
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m93x70bca37c(i4, list);
                    }
                });
                return;
            case 3:
            default:
                GeneralRules.Enable(this, bitmap2, 0, new GeneralRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda38
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m86xd92158a3(i4, list);
                    }
                });
                GeneralRules.Enable(this, bitmap4, 1, new GeneralRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda1
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m87xf33cd742(i4, list);
                    }
                });
                GeneralRules.Enable(this, bitmap3, 2, new GeneralRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda2
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m88xd5855e1(i4, list);
                    }
                });
                return;
            case 4:
                HappyGameRule.Enable(this, bitmap2, 0, new HappyGameRule.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda5
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HappyGameRule.OnOCRListener
                    public final void play(int i4, List list) {
                        m94x8ad8221b(i4, list);
                    }
                });
                HappyGameRule.Enable(this, bitmap4, 1, new HappyGameRule.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda3
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HappyGameRule.OnOCRListener
                    public final void play(int i4, List list) {
                        m59xe9d3b667(i4, list);
                    }
                });
                HappyGameRule.Enable(this, bitmap3, 2, new HappyGameRule.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda4
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HappyGameRule.OnOCRListener
                    public final void play(int i4, List list) {
                        m60x3ef3506(i4, list);
                    }
                });
                return;
            case 5:
                JJRules.Enable(this, bitmap2, 0, new JJRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda19
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.JJRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m61x1e0ab3a5(i4, list);
                    }
                });
                JJRules.Enable(this, bitmap4, 1, new JJRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda20
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.JJRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m62x38263244(i4, list);
                    }
                });
                JJRules.Enable(this, bitmap3, 2, new JJRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda21
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.JJRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m63x5241b0e3(i4, list);
                    }
                });
                return;
            case 6:
                TuyouRules.Enable(this, bitmap2, 0, new TuyouRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda26
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m64x6c5d2f82(i4, list);
                    }
                });
                TuyouRules.Enable(this, bitmap4, 1, new TuyouRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda27
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m65x8678ae21(i4, list);
                    }
                });
                TuyouRules.Enable(this, bitmap3, 2, new TuyouRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda28
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m66xa0942cc0(i4, list);
                    }
                });
                return;
            case 7:
                HuanleTuyouLeRules.Enable(this, bitmap2, 0, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda13
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m67xbaafab5f(i4, list);
                    }
                });
                HuanleTuyouLeRules.Enable(this, bitmap4, 1, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda14
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m68xd4cb29fe(i4, list);
                    }
                });
                HuanleTuyouLeRules.Enable(this, bitmap3, 2, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda15
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m69x13280ba8(i4, list);
                    }
                });
                return;
            case 8:
                ClassicsRules.Enable(this, bitmap2, 0, new ClassicsRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda35
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ClassicsRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m70x2d438a47(i4, list);
                    }
                });
                ClassicsRules.Enable(this, bitmap4, 1, new ClassicsRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda36
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ClassicsRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m71x475f08e6(i4, list);
                    }
                });
                ClassicsRules.Enable(this, bitmap3, 2, new ClassicsRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda37
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ClassicsRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m72x617a8785(i4, list);
                    }
                });
                return;
            case 9:
                int height2 = ((int) (bitmap.getHeight() * 0.2584d)) - i;
                int height3 = ((int) (bitmap.getHeight() * 0.3092d)) - i;
                HuanLeAppRules.Enable(this, bitmap2, 0, 0, 0, new HuanLeAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda6
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m73x7b960624(i4, list);
                    }
                });
                HuanLeAppRules.Enable(this, bitmap4, 1, height2, height3, new HuanLeAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda7
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m74x95b184c3(i4, list);
                    }
                });
                HuanLeAppRules.Enable(this, bitmap3, 2, height2, height3, new HuanLeAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda8
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanLeAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m75xafcd0362(i4, list);
                    }
                });
                return;
            case 10:
                int height4 = ((int) (bitmap.getHeight() * 0.2101d)) - i;
                int height5 = ((int) (bitmap.getHeight() * 0.2648d)) - i;
                int height6 = ((int) (bitmap.getHeight() * 0.3287d)) - i;
                ChanYouAppRules.Enable(this, bitmap2, 0, 0, 0, 0, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda0
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m76xc9e88201(i4, list);
                    }
                });
                ChanYouAppRules.Enable(this, bitmap4, 1, height4, height5, height6, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda11
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m77xe40400a0(i4, list);
                    }
                });
                ChanYouAppRules.Enable(this, bitmap3, 2, height4, height5, height6, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda22
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m78xfe1f7f3f(i4, list);
                    }
                });
                return;
            case 11:
                HuanleTuyouLeRules.Enable(this, bitmap2, 0, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda16
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m80x3c7c60e9(i4, list);
                    }
                });
                HuanleTuyouLeRules.Enable(this, bitmap4, 1, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda17
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m81x5697df88(i4, list);
                    }
                });
                HuanleTuyouLeRules.Enable(this, bitmap3, 2, new HuanleTuyouLeRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda18
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.HuanleTuyouLeRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m82x70b35e27(i4, list);
                    }
                });
                return;
            case 12:
                int height7 = ((int) (bitmap.getHeight() * 0.3092d)) - i;
                int height8 = ((int) (bitmap.getHeight() * 0.3564d)) - i;
                int height9 = ((int) (bitmap.getHeight() * 0.4166d)) - i;
                ChanYouAppRules.Enable(this, bitmap2, 0, 0, 0, height9, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda32
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m83x8acedcc6(i4, list);
                    }
                });
                ChanYouAppRules.Enable(this, bitmap4, 1, height7, height8, height9, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda33
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m84xa4ea5b65(i4, list);
                    }
                });
                ChanYouAppRules.Enable(this, bitmap3, 2, height7, height8, height9, new ChanYouAppRules.OnOCRListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$$ExternalSyntheticLambda34
                    @Override // com.carrydream.cardrecorder.PokerGame.Rule.ChanYouAppRules.OnOCRListener
                    public final void play(int i4, List list) {
                        m85xbf05da04(i4, list);
                    }
                });
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$3$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m79xee332a61(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$4$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m89x84ea900(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$5$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m90x226a279f(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$6$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m91x3c85a63e(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$7$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m92x56a124dd(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$8$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m93x70bca37c(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$9$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m94x8ad8221b(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$10$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m59xe9d3b667(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$11$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m60x3ef3506(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$12$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m61x1e0ab3a5(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$13$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m62x38263244(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$14$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m63x5241b0e3(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$15$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m64x6c5d2f82(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$16$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m65x8678ae21(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$17$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m66xa0942cc0(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$18$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m67xbaafab5f(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$19$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m68xd4cb29fe(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$20$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m69x13280ba8(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$21$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m70x2d438a47(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$22$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m71x475f08e6(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$23$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m72x617a8785(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$24$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m73x7b960624(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$25$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m74x95b184c3(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$26$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m75xafcd0362(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$27$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m76xc9e88201(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$28$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m77xe40400a0(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$29$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m78xfe1f7f3f(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$30$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m80x3c7c60e9(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$31$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m81x5697df88(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$32$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m82x70b35e27(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$33$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m83x8acedcc6(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$34$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m84xa4ea5b65(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$35$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m85xbf05da04(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$36$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m86xd92158a3(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$37$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m87xf33cd742(int i, List list) {
        this.customPoker.play(i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$partition$38$com-carrydream-cardrecorder-ui-activity-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m88xd5855e1(int i, List list) {
        this.customPoker.play(i, list);
    }

    private Bitmap getBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return Tool.BinaryImage(Bitmap.createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false), 160);
    }

    private Bitmap getBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, int i5) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false);
        return i5 == 0 ? createBitmap : Tool.BinaryImage(createBitmap, i5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MlThread extends Thread {
        String path;

        public MlThread(String str) {
            this.path = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.e("hjw", "拷贝样本数据到sdcard");
            File file = new File(getExternalCacheDir(), "/good_data.zip");
            Tool.copyAssets(HomeActivity.this, "good_data.zip", file.getAbsolutePath());
            Tool.unZip(file, getExternalCacheDir() + File.separator);
            runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$MlThread$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Log.e("hjw", "解压完成");
                }
            });
            File[] listFiles = new File(this.path).listFiles();
            DataPool.clear();
            for (File file2 : listFiles) {
                if (!file2.isFile()) {
                    Log.e("file2路径打印：", file2.toString());
                    if(file2.toString().equals("/storage/emulated/0/Android/data/com.hb.aiyouxiba.demo/cache/good_data/X/0_20221202_182828704.png")){
                        continue;
                    }
                    MlData createMlDataFromDirectory = MlData.createMlDataFromDirectory(file2, HomeActivity.this);
                    if (createMlDataFromDirectory == null) {
                        Log.i("hjw", "ret:" + createMlDataFromDirectory.toString());
                    } else {
                        DataPool.addMlData(createMlDataFromDirectory.getLabel(), createMlDataFromDirectory);
                    }
                }
            }
            DataPool.init();

        }
    }

    private void languageIsExists() {
        File file = new File(getCacheDir(), this.LANGUAGE_NAME);
        if (file.exists()) {
            return;
        }
        copyToSdCard(this.LANGUAGE_NAME, file);
    }

    private void copyToSdCard(String str, File file) {
        try {
            InputStream open = getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("hjw", e.toString());
        }
    }

    private void EasyFloat() {
        EasyFloat.with(this)
                .setLayout(R.layout.layout_pop)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_BOTTOM)
                .setTag("tag1")
                .setDragEnable(true)
                .hasEditText(false)
                .setLocation(0, DensityUtils.getScreenWidth(this) - DensityUtils.dp2px(this, 40.0f))
                .setMatchParent(false, false)
                .setAnimator(null)
                .registerCallbacks(new AnonymousClass7()).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.carrydream.cardrecorder.ui.activity.HomeActivity$7  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass7 implements OnFloatCallbacks {
        AnonymousClass7() {
        }

        
        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void createdResult(boolean z, String str, View view) {
            if (z) {
                final TextView textView = (TextView) view.findViewById(R.id.floatingWindow);
                final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.out1);
                final LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.out);
                recycler = (RecyclerView) view.findViewById(R.id.recycler);
                next = (TextView) view.findViewById(R.id.next);
                last = (TextView) view.findViewById(R.id.last);
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 14);
                recycler.setLayoutManager(gridLayoutManager);
                windowAdapter = new WindowAdapter(HomeActivity.this, R.layout.win_item);
                recycler.setAdapter(windowAdapter);
                windowAdapter.setNewInstance(Tool.initDataZdy(DataUtils.getInstance().getPoker()));
                recycler.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity.7.1
                    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                    public void getItemOffsets(Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view2.getLayoutParams();
                        int spanSize = layoutParams.getSpanSize();
                        layoutParams.getSpanIndex();
                        if (spanSize != gridLayoutManager.getSpanCount()) {
                            if (spanSize == 0) {
                                rect.left = DensityUtils.dp2px(HomeActivity.this, 4.0f);
                                rect.right = DensityUtils.dp2px(HomeActivity.this, 2.0f);
                            } else if (spanSize == 13) {
                                rect.left = DensityUtils.dp2px(HomeActivity.this, 2.0f);
                                rect.right = DensityUtils.dp2px(HomeActivity.this, 4.0f);
                            } else {
                                rect.left = DensityUtils.dp2px(HomeActivity.this, 2.0f);
                                rect.right = DensityUtils.dp2px(HomeActivity.this, 2.0f);
                            }
                        }
                    }
                });
                textView.setSelected(true);
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.HomeActivity$7$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                       m98xdc991dbc(textView, linearLayout2, linearLayout, view2);
                    }
                });
            }else {
                Log.e("EasyFloat创建失败！","1174行Home Activity");
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$createdResult$0$com-carrydream-cardrecorder-ui-activity-HomeActivity$7  reason: not valid java name */
        @SuppressLint("WrongConstant")
        public /* synthetic */ void m98xdc991dbc(TextView textView, LinearLayout linearLayout, LinearLayout linearLayout2, View view) {
            doScreenCapture();
            if (textView.isSelected()) {
                linearLayout.setVisibility(8);
                linearLayout2.setVisibility(8);
                textView.setText("显示");
                textView.setSelected(false);
                return;
            }
            textView.setText("隐藏");
            textView.setSelected(true);
            linearLayout.setVisibility(0);
            linearLayout2.setVisibility(0);
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void show(View view) {
            Log.e("show", "show");
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void hide(View view) {
            Log.e("hide", "hide");
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void dismiss() {
            Log.e("dismiss", "dismiss");
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void touchEvent(View view, MotionEvent motionEvent) {
            Log.e("touchEvent", "touchEvent");
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void drag(View view, MotionEvent motionEvent) {
            Log.e("drag", "drag");
        }

        @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
        public void dragEnd(View view) {
            Log.e("dragEnd", "dragEnd");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.carrydream.cardrecorder.base.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        doServiceStop();
        super.onDestroy();
    }
}
