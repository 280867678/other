package com.example.floatdragview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.floatdragview.bin.DownloadTaskInfo;

public class DownloadCenterActivity extends AppCompatActivity {

    /* renamed from: a */
    DownloadCenterActivityFragment downloadCenterActivityFragment;  //f17924a

    /* renamed from: c */
    DownloadCenterDetailFragment downloadCenterDetailFragment; //f17925c

    /* renamed from: d */
    boolean f17926d;

    /* renamed from: g */
    private boolean f17927g = false;

    /* renamed from: h */
    private boolean f17928h = false;

    /* renamed from: i */
    private C4775b f17929i = new C4775b(this, (byte) 0);

    /* renamed from: j */
    private C4774a f17930j = new C4774a(this, (byte) 0);

    /* renamed from: k */
    private Bitmap f17931k = null;

    /* renamed from: l */
    private BroadcastReceiver f17932l = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!"ACTION_EXIT_PLAYER".equals(intent.getAction()) || DownloadCenterActivity.this.isFinishing()) {
                return;
            }
//            if (Build.VERSION.SDK_INT < 17 || !this.f17982a.isDestroyed()) {
//                AppPraiseDlg.m11689a((Context) this.f17982a, intent.getLongExtra("EXTRA_STAY_TIME", 0L));
//            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
//    @Override // com.xunlei.downloadprovider.app.BaseActivity
    /* renamed from: q_ */
    public final boolean mo2991q_() {
        return false;
    }


    private class C4774a extends BroadcastReceiver {
        private C4774a() {
        }

        /* synthetic */ C4774a(DownloadCenterActivity downloadCenterActivity, byte b) {
            this();
        }

        @SuppressLint("WrongConstant")
        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !"com.xunLei.downloadCenter.MoreOperate".equals(action)) {
                return;
            }
            try {
                DownloadTaskInfo m8549c = (DownloadTaskInfo) intent.getSerializableExtra("taskInfo");
                String stringExtra = intent.getStringExtra("from");
                if (m8549c != null) {
                    DownloadCenterActivity downloadCenterActivity = DownloadCenterActivity.this;
                    if (downloadCenterActivity.downloadCenterDetailFragment == null) {
                        downloadCenterActivity.downloadCenterDetailFragment = new DownloadCenterDetailFragment();
                        FragmentTransaction beginTransaction = downloadCenterActivity.getSupportFragmentManager().beginTransaction();
                        beginTransaction.replace(R.id.fragment_center_detail, downloadCenterActivity.downloadCenterDetailFragment);
                        beginTransaction.commitAllowingStateLoss();
                    }
                    if (downloadCenterActivity.downloadCenterDetailFragment != null) {
                        DownloadCenterDetailFragment downloadCenterDetailFragment = downloadCenterActivity.downloadCenterDetailFragment;
                        if (m8549c != null) {
                            DownloadCenterDetailFragment.C5196a c5196a = new DownloadCenterDetailFragment.C5196a((byte) 0);
                            c5196a.f19506c = m8549c;
                            c5196a.f19507d = stringExtra;
                            downloadCenterDetailFragment.f19485e = c5196a;
                            if (downloadCenterDetailFragment.isAdded()) {
                                downloadCenterDetailFragment.m9519a(c5196a, false);
                            }
                        }
                        downloadCenterActivity.downloadCenterActivityFragment.f17946b.setVisibility(4);
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    DownloadCenterActivity.this.getWindow().setStatusBarColor(DownloadCenterActivity.this.getResources().getColor(R.color.blue_status_bar));
                }
            } catch (Exception unused) {
            }
        }
    }

    /* renamed from: com.xunlei.downloadprovider.download.center.DownloadCenterActivity$b */
    /* loaded from: classes2.dex */
    private class C4775b extends BroadcastReceiver {
        private C4775b() {
        }

        /* synthetic */ C4775b(DownloadCenterActivity downloadCenterActivity, byte b) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !"ACTION_EXIT_PLAYER".equals(action) || DownloadCenterActivity.this.isFinishing()) {
                return;
            }
            if (Build.VERSION.SDK_INT < 17 || !DownloadCenterActivity.this.isDestroyed()) {
//                AppPraiseDlg.m11689a((Context) DownloadCenterActivity.this, intent.getLongExtra("EXTRA_STAY_TIME", 0L));
            }
        }
    }


    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public static void m10830a(Context context, long j, String str, Bundle bundle) {
//        WebsiteHelper.m1721a().m1706b();
        Intent xLIntent = new Intent(context, DownloadCenterActivity.class);
        boolean z = context instanceof Activity;
        if (!z) {
            xLIntent.setFlags(268435456);
        } else {
            xLIntent.setFlags(67108864);
        }
        if (bundle != null) {
            xLIntent.putExtras(bundle);
        }
//        if (DLCenterEntry.personal_my_collection.toString().equals(str)) {
//            xLIntent.putExtra("extra_key_jump_to_collection", true);
//        }
//        xLIntent.putExtra(MsgLogStore.f10614m, j);
        xLIntent.putExtra("from", str);
        context.startActivity(xLIntent);
        if ("alarmDialog".equals(str)) {
            if (z) {
                ((Activity) context).overridePendingTransition(R.anim.translate_alpha_in, R.anim.translate_alpha_out);
            }
        } else if (z) {
            ((Activity) context).overridePendingTransition(R.anim.translate_between_interface_right_in, R.anim.translate_between_interface_left_out);
        }
    }


    @Override // android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
//        ShareHelper.m8419a();
//        ShareHelper.m8417a(this, i, i2, intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_center);
        this.downloadCenterActivityFragment = (DownloadCenterActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        long longExtra = getIntent().getLongExtra(MsgLogStore.f10614m, -1L);
//        String stringExtra2 = getIntent().getStringExtra("gcid");
//        boolean booleanExtra = getIntent().getBooleanExtra("extra_key_should_open_detailpage", false);
//        this.f17926d = getIntent().getBooleanExtra("extra_key_jump_to_collection", false);
        if (this.downloadCenterActivityFragment == null) {
            this.downloadCenterActivityFragment = new DownloadCenterActivityFragment();
            Bundle arguments = this.downloadCenterActivityFragment.getArguments();
            if (arguments == null) {
                arguments = new Bundle(5);
                this.downloadCenterActivityFragment.setArguments(arguments);
            }
//            arguments.putLong(MsgLogStore.f10614m, longExtra);
//            arguments.putString("gcid", stringExtra2);
//            arguments.putString("from", getIntent().getStringExtra("from"));
//            arguments.putBoolean("extra_key_should_open_detailpage", booleanExtra);
//            arguments.putBoolean("extra_key_jump_to_collection", this.f17926d);
        }
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.fragment_container, this.downloadCenterActivityFragment);
        beginTransaction.commitAllowingStateLoss();
        if (!this.f17928h) {
            this.f17928h = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.xunLei.downloadCenter.MoreOperate");
            registerReceiver(this.f17930j, intentFilter);
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("ACTION_EXIT_PLAYER");
        LocalBroadcastManager.getInstance(this).registerReceiver(this.f17929i, intentFilter2);
//        if (booleanExtra) {
//            DownloadTaskManager.m10252a();
//            DownloadTaskInfo m8549c = new DownloadTaskInfo();
//            if (m8549c != null) {
//                DownloadCenterControl.m10710a(this, m8549c, "");
//            }
//        }
//        m10828c(getIntent());
    }



    @Override // android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.downloadCenterActivityFragment != null && this.downloadCenterActivityFragment.f17947c) {
            this.downloadCenterActivityFragment.m10827a();
        } else if (this.downloadCenterDetailFragment != null && this.downloadCenterDetailFragment.isVisible()) {
            this.downloadCenterDetailFragment.m9509c();
        } else {
            try {
                super.onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override // android.app.Activity
    public void finish() {
        if (this.f17927g) {
//            ActivityManager.RunningTaskInfo runningTaskInfo = ((ActivityManager) BrothersApplication.getApplicationInstance().getSystemService("activity")).getRunningTasks(1).get(0);
//            if (runningTaskInfo.baseActivity.equals(runningTaskInfo.topActivity)) {
//                MainTabActivity.m8379b(this, "thunder", null);
//            }
        }
        super.finish();
        overridePendingTransition(R.anim.translate_between_interface_left_in, R.anim.translate_between_interface_right_out);
    }


    @Override // android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
//        DownloadTaskInfo m8549c;
        super.onNewIntent(intent);

        Log.e("DownloadCenterActivity:","onNewIntent");
//        m10620a(intent);
//        m10829b(intent);
//        boolean booleanExtra = intent.getBooleanExtra("extra_key_should_open_detailpage", false);
        DownloadCenterActivityFragment downloadCenterActivityFragment = (DownloadCenterActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        long longExtra = intent.getLongExtra(MsgLogStore.f10614m, -1L);
//        Bundle arguments = downloadCenterActivityFragment.getArguments();
//        if (arguments == null) {
//            arguments = new Bundle(9);
//            downloadCenterActivityFragment.setArguments(arguments);
//        }
//        arguments.putLong(MsgLogStore.f10614m, longExtra);
//        arguments.putBoolean("extra_key_should_open_detailpage", false);
        if ("alarmDialog".equals(intent.getStringExtra("from")) && this.downloadCenterDetailFragment != null && this.downloadCenterDetailFragment.isAdded()) {
            this.downloadCenterDetailFragment.m9514a(true);
        }
//        if (booleanExtra && (m8549c = TaskListManager.m8543e().m8549c(longExtra)) != null) {
//            DownloadCenterControl.m10710a(this, m8549c, "");
//        }
//        m10828c(intent);
    }

    @Override // com.xunlei.downloadprovider.download.create.ThunderTaskActivity, com.xunlei.downloadprovider.app.BaseActivity, android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
//        if (DownloadService.m3173a() == null) {
//            DownloadService.m3172a((DownloadService.InterfaceC7694c) null);
//        } else {
//            DownloadTaskManager.m10252a();
//            DownloadTaskManager.m10182p();
//        }
//        LoginHelper.m6932a();
//        if (UserLoginImpl.m6779c()) {
//            DownloadTaskManager.m10252a().m10236a(LoginHelper.m6932a());
//        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xunlei.downloadprovider.download.create.ThunderTaskActivity, com.xunlei.downloadprovider.app.BaseActivity, android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.f17930j);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.f17929i);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.f17932l);
//        TaskDownloadCardViewHolder.hasReportDl_Try_Show = false;
//        FreeTrialRedPacketHelper.m8905a().m8896d();
//        FeedContentListHelper.m8743a();
//        FeedContentListHelper.m8739b();
    }

    @Override // com.xunlei.downloadprovider.download.create.ThunderTaskActivity, android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // com.xunlei.downloadprovider.download.create.ThunderTaskActivity, com.xunlei.downloadprovider.app.BaseActivity, android.support.p003v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

}