package com.example.floatdragview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;
import com.example.floatdragview.datasource.TaskSpeedCountInfo;
import com.example.floatdragview.util.DetailsPeriod;
import com.example.floatdragview.util.DipPixelUtil;
import com.example.floatdragview.util.HandlerUtil;
import com.example.floatdragview.widget.DownloadCenterBottomView;
import com.example.floatdragview.widget.DownloadCenterSelectFileTitleView;
import com.example.floatdragview.widget.FloatDragView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DownloadCenterDetailFragment extends DownloadDetailBasicFragment {

    /* renamed from: B */
    private DetailsPeriod f19477B;

    /* renamed from: a */
    TaskDetailFragment f19481a;

    /* renamed from: b */
    boolean f19482b;

    /* renamed from: d */
//    XLAlertDialog f19484d;

    /* renamed from: e */
    public C5196a f19485e;

    /* renamed from: k */
//    private TaskDetailActionSheetDialog f19488k;

    /* renamed from: l */
    private DownloadCenterSelectFileTitleView f19489l;

    /* renamed from: m */
    private DownloadCenterBottomView f19490m;

    /* renamed from: n */
    private boolean f19491n;

    /* renamed from: o */
    private TextView f19492o;

    /* renamed from: p */
    private View f19493p;

    /* renamed from: q */
    private ImageView f19494q;

    /* renamed from: r */
    private ImageView f19495r;

    /* renamed from: s */
    private ImageView f19496s;

    /* renamed from: t */
    private TaskSpeedCountInfo f19497t;

    /* renamed from: u */
    private FloatDragView f19498u;

    /* renamed from: w */
    private Animation f19500w;

    /* renamed from: x */
    private Animation f19501x;

    /* renamed from: y */
    private Animation f19502y;

    /* renamed from: z */
    private Animation f19503z;

    /* renamed from: c */
    String f19483c = "DownloadCenterDetailFragment";

    /* renamed from: v */
//    private DownloadCenterControl f19499v = new DownloadCenterControl();

    /* renamed from: A */
//    private AbstractTaskDetailAdView f19476A = null;

    /* renamed from: C */
    private boolean f19478C = false;

    /* renamed from: f */
    HandlerUtil.MessageListener f19486f = new HandlerUtil.MessageListener() {
        @Override
        public void handleMessage(Message message) {

        }
    };

    /* renamed from: g */
    HandlerUtil.StaticHandler f19487g = new HandlerUtil.StaticHandler(this.f19486f);

    /* renamed from: D */
    private final int f19479D = 1000;

    /* renamed from: E */
    private Runnable f19480E = new Runnable() {
        @Override
        public void run() {
            try {
                if (f19487g != null) {
                    f19487g.removeCallbacks(this);
                    f19487g.postDelayed(this, 1000L);
                }
                m9503f();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.DownloadCenterDetailFragment$b */
    /* loaded from: classes.dex */
    public interface InterfaceC5197b {
        /* renamed from: a */
        void mo9489a();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: o */
//    public static /* synthetic */ XLAlertDialog m9491o(DownloadCenterDetailFragment downloadCenterDetailFragment) {
//        downloadCenterDetailFragment.f19484d = null;
//        return null;
//    }
    public DownloadCenterDetailFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.support.p003v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
//        this.f19499v.f18209a = getActivity();
        View inflate = layoutInflater.inflate(R.layout.download_center_taskdetail_layout, viewGroup, false);
        this.f19493p = inflate.findViewById(R.id.status_bar);
        this.f19494q = (ImageView) inflate.findViewById(R.id.detail_title_right_icon);
//        this.f19494q.setOnClickListener(new View$OnClickListenerC5199aa(this));
        this.f19496s = (ImageView) inflate.findViewById(R.id.detail_title_share_icon);
//        this.f19496s.setOnClickListener(new View$OnClickListenerC5200ab(this));
        this.f19492o = (TextView) inflate.findViewById(R.id.task_detail_mask);
        this.f19492o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean z;
                z = f19491n;
                if (z || f19482b) {
                    return;
                }
                m9514a(true);
            }
        });
        this.f19498u = (FloatDragView) inflate.findViewById(R.id.drag_view);
        this.f19498u.setIdleY(DipPixelUtil.dip2px(92.0f));
        this.f19498u.setContentNestedScrollViewId(R.id.task_detail_recycler_view);
        this.f19495r = (ImageView) inflate.findViewById(R.id.close_btn);
//        this.f19495r.setOnClickListener(new View$OnClickListenerC5202ad(this));
        this.f19498u.setListener(new FloatDragView.InterfaceC5373a() {
            @Override
            public void mo9068a(float f) {
                boolean z;
                z = f19491n;
                if (z || f19482b) {
                    return;
                }
                if (f == -1.0d) {
                    m9500h();
                } else {
                    m9498i();
                }
            }

            @Override
            public void mo9067a(int i) {
                if (i != 10 || f19482b) {
                    return;
                }
                m9514a(true);
            }
        });
        this.f19489l =  inflate.findViewById(R.id.select_file_title);
        this.f19489l.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.f19489l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.f19489l.setSelectAllListener(new DownloadCenterSelectFileTitleView.InterfaceC4814b() {
            @Override
            public void mo5581a(boolean z) {

            }
        });
        this.f19489l.setShowListener(new DownloadCenterSelectFileTitleView.InterfaceC4813a() {
            @Override
            public void mo9056a() {

            }
        });
        this.f19490m =  inflate.findViewById(R.id.bottom_view);
        this.f19490m.setDeleteTasksListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.f19490m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(inflate.getContext(),"as",Toast.LENGTH_LONG);
            }
        });

//        this.f19476A = new TaskDetailThemeAdView(getContext());
//        this.f19476A.setTag("view_tag_value_top");
//        this.f19498u.addView(this.f19476A, 0);
        return inflate;
    }

    @Override // android.support.p003v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.f19481a = (TaskDetailFragment) getChildFragmentManager().findFragmentById(R.id.fragment_task_detail);
        TaskDetailFragment taskDetailFragment = this.f19481a;
//        DownloadCenterControl downloadCenterControl = this.f19499v;
//        taskDetailFragment.f19518e = downloadCenterControl;
//        if (taskDetailFragment.f19516c != null) {
//            taskDetailFragment.f19516c.m9452a(downloadCenterControl);
//            if (taskDetailFragment.isVisible() && taskDetailFragment.isAdded()) {
//                taskDetailFragment.f19516c.notifyDataSetChanged();
//            }
//        }
//        this.f19481a.f19519f = this;
        this.f19481a.f19524k = this.f19498u;
//        AbstractTaskDetailAdView abstractTaskDetailAdView = this.f19476A;
        this.f19481a.f19527n = new TaskDetailFragment.InterfaceC5198a() {
            @Override
            public void mo9366a(boolean z) {

            }
        };
        if (this.f19485e != null) {
            m9519a(this.f19485e, true);
        }
    }

    @Override // android.support.p003v4.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.f19477B != null) {
            this.f19477B.onStart();
        }
    }

    @Override // android.support.p003v4.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.f19477B != null) {
            this.f19477B.onStop();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public void m9500h() {
        this.f19494q.setImageResource(R.drawable.download_center_detail_more_gray_selector);
        this.f19496s.setImageResource(R.drawable.download_detail_share_icon_gray_selector);
        if (this.f19482b) {
            this.f19495r.setImageResource(R.drawable.commonui_nav_arrow_back_selector);
        } else {
            this.f19495r.setImageResource(R.drawable.download_center_detail_close_gray_selector);
        }
        this.f19493p.setBackgroundColor(Color.parseColor("#ffffff"));
        this.f19493p.setClickable(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public void m9498i() {
        this.f19494q.setImageResource(R.drawable.download_center_detail_more_selector);
        this.f19496s.setImageResource(R.drawable.download_detail_share_icon_selector);
        this.f19495r.setImageResource(R.drawable.download_center_detail_close_selector);
        this.f19493p.setBackgroundColor(Color.parseColor("#00000000"));
        this.f19493p.setClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
//    public final ArrayList<BTSubTaskItem> m9520a() {
//        ArrayList<BTSubTaskItem> arrayList = new ArrayList<>();
//        arrayList.addAll(this.f19481a.f19515b.m9467a());
//        return arrayList;
//    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m9510b(boolean z) {
        this.f19491n = z;
        if (z) {
            this.f19498u.setVisibilityState$2563266(true);
            this.f19498u.setScrollEnable(false);
            this.f19489l.m10746b(true);
            this.f19490m.m10755a();
            this.f19498u.cancelLongPress();
        } else {
            this.f19498u.setScrollEnable(true);
            this.f19490m.m10753a(true);
            this.f19489l.m10744c(true);
            m9500h();
        }
        if (this.f19481a != null) {
            TaskDetailFragment taskDetailFragment = this.f19481a;
            taskDetailFragment.f19520g = z;
            if (taskDetailFragment.f19516c != null) {
                taskDetailFragment.f19516c.f19563g = taskDetailFragment.f19520g;
//                if (TaskHelper.m8447e(taskDetailFragment.f19517d)) {
//                    if (z) {
//                        taskDetailFragment.f19516c.m9443e();
//                        taskDetailFragment.m9482a(false, false);
//                        taskDetailFragment.f19523j.m9412a(false);
//                        return;
//                    }
//                    ArrayList<TaskDetailItem> arrayList = taskDetailFragment.f19515b.f19534j;
//                    ArrayList<TaskDetailItem> arrayList2 = taskDetailFragment.f19515b.f19535k;
//                    taskDetailFragment.f19515b.m9466a(false);
//                    taskDetailFragment.f19516c.m9445b(arrayList, arrayList2);
//                    taskDetailFragment.f19523j.m9412a(true);
//                    taskDetailFragment.m9482a(false, false);
//                }
            }
        }
    }

    /* renamed from: a */
//    public final void m9515a(TaskWrapper taskWrapper, String str) {
//        C5196a c5196a = new C5196a((byte) 0);
//        c5196a.f19505b = taskWrapper;
//        c5196a.f19504a = true;
//        c5196a.f19507d = str;
//        m9519a(c5196a, false);
//    }

    /* renamed from: a */
    public final void m9519a(C5196a c5196a, boolean z) {
//        DownloadTaskInfo downloadTaskInfo = c5196a.f19506c;
        if (c5196a.f19504a) {
//            this.f19509i = c5196a.f19505b;
//            downloadTaskInfo = this.f19509i.f20884b;
            this.f19482b = true;
            this.f19498u.setIdleY(0);
            this.f19498u.setLockScroll(true);
            if (this.f19487g != null) {
                this.f19487g.removeCallbacks(this.f19480E);
                this.f19487g.postDelayed(this.f19480E, 1000L);
            }
        }
//        if (downloadTaskInfo == null) {
//            return;
//        }
//        this.f19510j = c5196a.f19507d;
//        this.f19478C = false;
//        if (TextUtils.isEmpty(downloadTaskInfo.mCreateOrigin)) {
//            DownloadTaskManager.m10252a();
//            TaskInfo m10204f = DownloadTaskManager.m10204f(downloadTaskInfo.getTaskId());
//            if (m10204f != null) {
//                downloadTaskInfo.mSniffKeyword = m10204f.mSniffKeyword;
//                downloadTaskInfo.mWebsiteName = m10204f.mWebsiteName;
//                downloadTaskInfo.mRefUrl = m10204f.mRefUrl;
//                downloadTaskInfo.mCreateOrigin = m10204f.mCreateOrigin;
//                downloadTaskInfo.mExtraInfo = m10204f.mExtraInfo;
//            }
//        }
//        this.f19508h = downloadTaskInfo;
//        DownloadTaskManager.m10252a();
//        this.f19497t = DownloadTaskManager.m10186m(downloadTaskInfo.getTaskId());
//        if (!DownloadsUtil.m10156f(downloadTaskInfo.getResourceGcid())) {
//            TaskListManager.m8543e().m8542e(downloadTaskInfo.getTaskId());
//        }
//        StringBuilder sb = new StringBuilder("showTaskDetail title:");
//        sb.append(downloadTaskInfo.mTitle);
//        sb.append(" gcid = ");
//        sb.append(downloadTaskInfo.getResourceGcid());
//        String str = this.f19510j;
//        if (TextUtils.isEmpty(str)) {
//            str = DispatchConstants.OTHER;
//        }
//        String str2 = str;
        this.f19477B = new DetailsPeriod();
        this.f19477B.onStart();
//        boolean m9414a = TaskCommentController.m9414a(downloadTaskInfo);
//        DLCenterReporter.m9604a(downloadTaskInfo.mTitle, str2, (downloadTaskInfo.getTaskStatus() == 2 || downloadTaskInfo.getTaskStatus() == 1) && !downloadTaskInfo.mHasVipChannelSpeedup, DownloadReporter.m10451a(downloadTaskInfo), TaskDetailFragment.m9485a(downloadTaskInfo), m9414a);
//        if (this.f19497t == null) {
//            this.f19497t = new TaskSpeedCountInfo();
//            this.f19497t.mTaskId = downloadTaskInfo.getTaskId();
//        }
//        if (this.f19481a != null) {
//            this.f19481a.f19526m = this.f19482b;
//            TaskDetailFragment taskDetailFragment = this.f19481a;
//            TaskSpeedCountInfo taskSpeedCountInfo = this.f19497t;
//            String str3 = this.f19510j;
//            taskDetailFragment.f19517d = downloadTaskInfo;
//            NormalTaskDetailDataSource normalTaskDetailDataSource = taskDetailFragment.f19514a;
//            if (normalTaskDetailDataSource.f19548a != null) {
//                normalTaskDetailDataSource.f19548a.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19549b != null) {
//                normalTaskDetailDataSource.f19549b.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19550c != null) {
//                normalTaskDetailDataSource.f19550c.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19554g != null) {
//                normalTaskDetailDataSource.f19554g.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19551d != null) {
//                normalTaskDetailDataSource.f19551d.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19552e != null) {
//                normalTaskDetailDataSource.f19552e.f19671d = downloadTaskInfo;
//            }
//            if (normalTaskDetailDataSource.f19553f != null) {
//                normalTaskDetailDataSource.f19553f.f19671d = downloadTaskInfo;
//            }
//            taskDetailFragment.f19515b.m9436a(downloadTaskInfo);
//            taskDetailFragment.f19515b.m9466a(false);
//            if (TaskCommentController.m9414a(downloadTaskInfo)) {
//                taskDetailFragment.f19523j.m9406b(downloadTaskInfo);
//            } else {
//                taskDetailFragment.f19523j.m9401d();
//                taskDetailFragment.f19521h.postDelayed(new RunnableC5208al(taskDetailFragment), 500L);
//            }
//            taskDetailFragment.f19523j.m9425a();
//            NormalTaskDetailDataSource normalTaskDetailDataSource2 = taskDetailFragment.f19514a;
//            normalTaskDetailDataSource2.f19555h = taskSpeedCountInfo;
//            if (normalTaskDetailDataSource2.f19548a != null) {
//                normalTaskDetailDataSource2.f19548a.f19670c = taskSpeedCountInfo;
//            }
//            BtTaskDetailDataSource btTaskDetailDataSource = taskDetailFragment.f19515b;
//            btTaskDetailDataSource.f19584h = taskSpeedCountInfo;
//            if (btTaskDetailDataSource.f19578b != null) {
//                btTaskDetailDataSource.f19578b.f19670c = taskSpeedCountInfo;
//            }
//            if (taskDetailFragment.m9479c() && taskDetailFragment.f19521h != null) {
//                taskDetailFragment.f19521h.scrollToPosition(0);
//            }
//            if (taskDetailFragment.f19516c != null) {
//                taskDetailFragment.f19516c.f19566j = true;
//                taskDetailFragment.f19516c.f19567k = str3;
//                taskDetailFragment.f19516c.notifyDataSetChanged();
//            }
//            taskDetailFragment.m9481b();
//            taskDetailFragment.f19525l = false;
//            if (TaskHelper.m8447e(taskDetailFragment.f19517d)) {
//                taskDetailFragment.m9487a();
//                taskDetailFragment.f19523j.f19604c = true;
//                taskDetailFragment.m9482a(true, false);
//            } else {
//                taskDetailFragment.f19523j.f19604c = false;
//            }
//            taskDetailFragment.f19522i.m11803a(true);
//            taskDetailFragment.f19522i.m11792c();
//        }
        if (this.f19482b) {
            m9500h();
            this.f19487g.post(new Runnable() {
                @Override
                public void run() {
                    m9497i();
                }
            });
            return;
        }
//        m9498i();
        if (z) {
            m9507c(false);
        } else {
            this.f19487g.post(new Runnable() {
                @Override
                public void run() {
                    m9507c(true);
                }
            });
        }
    }

    /* renamed from: b */
    public final void m9513b() {
        if (this.f19481a == null || !isVisible()) {
            return;
        }
        this.f19481a.m9477d();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    @SuppressLint("WrongConstant")
    public void m9507c(boolean z) {
//        if (this.f19508h == null) {
//            return;
//        }
        if (this.f19498u != null) {
            this.f19498u.m9077a();
        }
        View view = getView();
        if (view == null) {
            return;
        }
        this.f19493p.setVisibility(0);
        if (z) {
            view.setVisibility(0);
            this.f19500w = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_0_1);
            this.f19500w.setDuration(300L);
            this.f19500w.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    f19492o.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    TextView textView;
//                    TextView textView2;
//                    TaskDetailFragment taskDetailFragment;
//                    TaskDetailFragment unused;
//                    textView = this.f20168a.f19492o;
                    f19492o.setAnimation(null);
//                    textView2 = this.f20168a.f19492o;
                    f19492o.setClickable(true);
//                    taskDetailFragment = this.f20168a.f19481a;
//                    if (f19481a != null) {
//                        unused = this.f20168a.f19481a;
//                        TaskDetailFragment.m9475e();
//                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.f19492o.setAnimation(this.f19500w);
            this.f19492o.animate();
            this.f19501x = AnimationUtils.loadAnimation(getActivity(), R.anim.detail_bottom_in);
            this.f19501x.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    f19498u.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.f19498u.setAnimation(this.f19501x);
            this.f19498u.animate();
        } else {
            getView().setVisibility(0);
            if (this.f19481a != null) {
//                TaskDetailFragment.m9475e();
            }
        }
        this.f19489l.m10744c(false);
        this.f19490m.m10753a(false);
    }

//    @Override // com.xunlei.downloadprovider.download.taskdetails.TaskDetailListener
//    /* renamed from: a */
//    public final void mo9440a(List<BTSubTaskItem> list) {
//        m9511b(list);
//    }

    /* renamed from: b */
//    private void m9511b(List<BTSubTaskItem> list) {
//        if (CollectionUtil.m924a(list)) {
//            this.f19490m.m10752b();
//            this.f19489l.setTitle("请选择文件");
//        } else {
//            this.f19490m.m10751c();
//            DownloadCenterSelectFileTitleView downloadCenterSelectFileTitleView = this.f19489l;
//            downloadCenterSelectFileTitleView.setTitle("已选择" + list.size() + "个项目");
//        }
//        if (this.f19481a != null) {
//            this.f19489l.m10748a(!this.f19481a.f19515b.m9463d());
//        }
//    }

//    @Override // com.xunlei.downloadprovider.download.taskdetails.TaskDetailListener
//    /* renamed from: d */
//    public final void mo9439d() {
//        m9510b(true);
//        m9511b(m9520a());
//    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
//    public final void m9505e() {
//        if (this.f19484d != null) {
//            this.f19484d.dismiss();
//        }
//    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: f */
    public final void m9503f() {
        if (isAdded() && isVisible()) {
//            if (this.f19509i != null) {
//                this.f19509i.m8511i();
//            }
            m9513b();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.DownloadCenterDetailFragment$a */
    /* loaded from: classes2.dex */
    public static class C5196a {

        /* renamed from: a */
        boolean f19504a;

        /* renamed from: b */
//        TaskWrapper f19505b;

        /* renamed from: c */
        public DownloadTaskInfo f19506c;

        /* renamed from: d */
        public String f19507d;

        private C5196a() {
            this.f19504a = false;
        }

        public /* synthetic */ C5196a(byte b) {
            this();
        }
    }

    @Override // android.support.p003v4.app.Fragment
    public void onDestroy() {
//        if (this.f19487g != null) {
//            this.f19487g.removeCallbacks(this.f19480E);
//        }
//        this.f19487g.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /* renamed from: c */
    public final void m9509c() {
        if (this.f19491n) {
            m9510b(false);
        } else if (isVisible()) {
            m9514a(true);
        }
    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public final void m9514a(boolean z) {
        if (!this.f19482b) {
            if (z) {
                if (!this.f19478C) {
                    this.f19478C = true;
                }
            }
            if (this.f19491n) {
                m9510b(false);
            }
            if (z) {
                this.f19502y = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_1_0);
                this.f19502y.setDuration(300L);
                this.f19502y.setAnimationListener(new Animation.AnimationListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onAnimationStart(Animation animation) {
                        f19492o.setClickable(false);

                        f19493p.setVisibility(4);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        f19492o.setAnimation(null);
                        getView().setVisibility(8);
//                        textView2 = this.f20172a.f19492o;
                        f19492o.setClickable(true);
//                        taskDetailFragment = this.f20172a.f19481a;
                        if (f19481a != null) {
//                            taskDetailFragment2 = this.f20172a.f19481a;
                            f19481a.m9471g();
//                            taskDetailFragment3 = this.f20172a.f19481a;
//                            f19481a.m9473f();
                        }
                        f19508h = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                this.f19492o.startAnimation(this.f19502y);
                this.f19503z = AnimationUtils.loadAnimation(getActivity(), R.anim.detail_bottom_out);
                this.f19503z.setDuration(300L);
                this.f19503z.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        f19498u.setAnimation(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                this.f19498u.startAnimation(this.f19503z);
            } else {
                getView().setVisibility(8);
                if (f19481a != null) {
                    f19481a.m9471g();
//                    m9473f();
                }
            }
            this.f19489l.m10744c(false);
            this.f19490m.m10753a(false);
        }
        if (this.f19477B != null) {
            this.f19477B.onStop();
//            DLCenterReporter.m9622a(this.f19477B.getDuration());
            this.f19477B = null;
        }
        if (this.f19487g != null) {
            this.f19487g.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentActivity activity = getActivity();
                    if (activity instanceof DownloadCenterDetailFragment.InterfaceC5197b) {
                        ((DownloadCenterDetailFragment.InterfaceC5197b) activity).mo9489a();
                    }
                }
            }, 300L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
//    public static /* synthetic */ void m9508c(DownloadCenterDetailFragment downloadCenterDetailFragment) {
//        if (downloadCenterDetailFragment.f19488k != null) {
//            if (downloadCenterDetailFragment.f19488k.isShowing()) {
//                downloadCenterDetailFragment.f19488k.dismiss();
//            }
//            downloadCenterDetailFragment.f19488k = null;
//        }
//        downloadCenterDetailFragment.f19488k = new TaskDetailActionSheetDialog(downloadCenterDetailFragment.getActivity());
//        downloadCenterDetailFragment.f19488k.f20240c = new View$OnClickListenerC5237g(downloadCenterDetailFragment);
//        downloadCenterDetailFragment.f19488k.f20238a = new View$OnClickListenerC5275j(downloadCenterDetailFragment);
//        downloadCenterDetailFragment.f19488k.f20239b = new View$OnClickListenerC5276k(downloadCenterDetailFragment);
//        downloadCenterDetailFragment.f19488k.f20241d = new View$OnClickListenerC5277l(downloadCenterDetailFragment);
//        downloadCenterDetailFragment.f19488k.m9065a(downloadCenterDetailFragment.f19508h);
//        downloadCenterDetailFragment.f19488k.show();
//    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: i */
    @SuppressLint("WrongConstant")
    public /* synthetic */ void m9497i() {

        f19493p.setVisibility(0);
        getView().setVisibility(0);
        if (f19498u != null) {
            f19498u.m9077a();
            f19498u.setVisibilityState$2563266(false);
        }
        if (f19481a != null) {
//            m9475e();
        }
        f19489l.m10744c(false);
        f19490m.m10753a(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static /* synthetic */ void m9517a(DownloadCenterDetailFragment downloadCenterDetailFragment, int i, List list) {
//        for (int i2 = 0; i2 < list.size(); i2++) {
//            if (((BTSubTaskItem) list.get(i2)).mLocalFileName != null) {
//                File file = new File(((BTSubTaskItem) list.get(i2)).mLocalFileName);
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
//        List<BTSubTaskItem> b = downloadCenterDetailFragment.f19481a.f19515b.m9465b();
//        long[] jArr = new long[b.size()];
//        for (int i3 = 0; i3 < b.size(); i3++) {
//            jArr[i3] = b.get(i3).mBTSubIndex;
//        }
        if (i == 1) {
//            DownloadTaskManager.m10252a().m10219b(downloadCenterDetailFragment.f19508h.getTaskId());
            downloadCenterDetailFragment.m9514a(false);
            return;
        }
//        DownloadTaskManager.m10252a();
//        DownloadTaskManager.m10245a(downloadCenterDetailFragment.f19508h.getTaskId(), jArr);
        downloadCenterDetailFragment.m9510b(false);
    }

}