package com.example.floatdragview;

import android.app.TaskInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.floatdragview.adapter.TaskDetailAdapter;
import com.example.floatdragview.animator.CustomItemAnimator;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.BtTaskDetailDataSource;
import com.example.floatdragview.datasource.NormalTaskDetailDataSource;
import com.example.floatdragview.datasource.TaskDetailItem;
import com.example.floatdragview.listener.TaskDetailListener;
import com.example.floatdragview.util.DownloadCenterControl;
import com.example.floatdragview.util.TaskDetailUtil;
import com.example.floatdragview.util.TaskHelper;
import com.example.floatdragview.widget.FloatDragView;

import java.util.ArrayList;


public class TaskDetailFragment extends Fragment {

    /* renamed from: c */
    TaskDetailAdapter f19516c;

    /* renamed from: d */
    DownloadTaskInfo f19517d;

    /* renamed from: e */
    DownloadCenterControl f19518e;

    /* renamed from: f */
    TaskDetailListener f19519f;

    /* renamed from: g */
    boolean f19520g;

    /* renamed from: h */
    RecyclerView f19521h;

    /* renamed from: j */
//    TaskCommentController f19523j;

    /* renamed from: k */
    FloatDragView f19524k;

    /* renamed from: l */
    boolean f19525l;

    /* renamed from: m */
    boolean f19526m;

    /* renamed from: r */
    private LinearLayoutManager f19531r;

    /* renamed from: a */
    final NormalTaskDetailDataSource f19514a = new NormalTaskDetailDataSource();

    /* renamed from: b */
    final BtTaskDetailDataSource f19515b = new BtTaskDetailDataSource();

    /* renamed from: p */
    private boolean f19529p = true;

    /* renamed from: q */
    private boolean f19530q = true;

    /* renamed from: i */
//    TaskDetailAdController f19522i = null;

    /* renamed from: s */
    private RecyclerView.OnScrollListener f19532s = new RecyclerView.OnScrollListener() {
        @Override // android.support.p006v7.widget.RecyclerView.OnScrollListener
        public final void onScrolled(RecyclerView recyclerView, int i, int i2) {
            super.onScrolled(recyclerView, i, i2);
//            m9482a(false, false);
            TaskDetailUtil.m8464a().f20945a = true;
            TaskDetailUtil.m8464a().f20946b = System.currentTimeMillis();
        }
    };

    /* renamed from: n */
    InterfaceC5198a f19527n = null;

    /* renamed from: t */
    private final int f19533t = 1;

    /* renamed from: o */
//    final MessageThread f19528o = new MessageThread("BTTaskDetail", new C5209am(this));

    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.TaskDetailFragment$a */
    /* loaded from: classes2.dex */
    public interface InterfaceC5198a {
        /* renamed from: a */
        void mo9366a(boolean z);
    }
    public TaskDetailFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.f19522i.m11804a(new C5207ak(this));



        f19517d = new DownloadTaskInfo();




        this.f19516c = new TaskDetailAdapter(getContext());
        this.f19516c.setHasStableIds(false);
        this.f19516c.m9452a(this.f19518e);
//        this.f19516c.f19560d = this.f19522i;
        this.f19516c.m9450a(new TaskDetailAdapter.InterfaceC5203a() {
            @Override // com.xunlei.downloadprovider.download.taskdetails.BTSubTaskInfoItemEditListener
            /* renamed from: a */
            public final void mo9107a() {
//                TaskDetailListener taskDetailListener;
//                TaskDetailListener taskDetailListener2;
//                BtTaskDetailDataSource btTaskDetailDataSource;
//                taskDetailListener = this.f19572a.f19519f;
                if (f19519f != null) {
//                    taskDetailListener2 = this.f19572a.f19519f;
//                    btTaskDetailDataSource = this.f19572a.f19515b;
//                    f19519f.mo9440a(f19515b.m9467a());
                }
            }

            @Override // com.xunlei.downloadprovider.download.taskdetails.BTSubTaskInfoItemEditListener
            /* renamed from: b */
            public final void mo9104b() {
//                TaskDetailListener taskDetailListener;
//                TaskDetailListener taskDetailListener2;
//                taskDetailListener = this.f19572a.f19519f;
                if (f19519f != null) {
//                    taskDetailListener2 = this.f19572a.f19519f;
                    f19519f.mo9439d();
                }
            }

            @Override // com.xunlei.downloadprovider.download.taskdetails.TaskDetailAdapter.InterfaceC5203a
            /* renamed from: a */
            public final void mo9106a(DownloadTaskInfo downloadTaskInfo) {
                TaskDetailFragment.m9483a(TaskDetailFragment.this, downloadTaskInfo);
            }

            @Override // com.xunlei.downloadprovider.download.taskdetails.TaskDetailAdapter.InterfaceC5203a
            /* renamed from: c */
            public final void mo9103c() {
//                DownloadTaskInfo downloadTaskInfo;
//                DownloadTaskInfo downloadTaskInfo2;
//                DownloadTaskInfo downloadTaskInfo3;
//                DownloadTaskInfo downloadTaskInfo4;
//                downloadTaskInfo = this.f19572a.f19517d;
                if (f19517d == null) {
                    return;
                }
//                BrowserUtil.m2052a();
//                FragmentActivity activity = getActivity();
//                downloadTaskInfo2 = this.f19572a.f19517d;
//                String str = f19517d.mRefUrl;
//                downloadTaskInfo3 = this.f19572a.f19517d;
//                String str2 = downloadTaskInfo3.mSniffKeyword;
//                downloadTaskInfo4 = this.f19572a.f19517d;
//                BrowserUtil.m2049a(activity, str, str2, downloadTaskInfo4.mWebsiteName, BrowserFrom.DL_CENTER_DETAIL);
            }

            @Override
            public void mo9102d() {

            }

            @Override // com.xunlei.downloadprovider.download.taskdetails.TaskDetailAdapter.InterfaceC5203a
            /* renamed from: a */
            public final void mo9105a(boolean z) {
//                TaskDetailAdapter taskDetailAdapter;
//                taskDetailAdapter = this.f19572a.f19516c;
                f19516c.m9447a(z);
            }
        });
        this.f19516c.f19563g = this.f19520g;
        this.f19514a.f19548a = new TaskDetailItem(1, this.f19517d, null, -1L);
        this.f19515b.f19578b = this.f19514a.f19548a;
        this.f19514a.f19549b = new TaskDetailItem(3, this.f19517d, null, -1L);
        this.f19514a.f19550c = new TaskDetailItem(4, this.f19517d, null, -1L);
        this.f19515b.f19579c = this.f19514a.f19550c;
        this.f19514a.f19551d = new TaskDetailItem(8, this.f19517d, null, 0L);
        this.f19514a.f19552e = new TaskDetailItem(9, this.f19517d, null, 0L);
        this.f19514a.f19553f = new TaskDetailItem(10, null, null, 0L);
        this.f19515b.f19581e = new TaskDetailItem(8, this.f19517d, null, 0L);
        this.f19515b.f19582f = new TaskDetailItem(9, this.f19517d, null, 0L);
        this.f19515b.f19583g = new TaskDetailItem(10, null, null, 0L);
        this.f19514a.f19554g = new TaskDetailItem(5, this.f19517d, null, -1L);
        this.f19515b.f19580d = this.f19514a.f19554g;

//        this.f19514a.f19548a = new TaskDetailItem(0, this.f19517d, null, -1L);



//        m9468i();
//        m9469h();
//        TaskHelper.m8447e(this.f19517d);

        this.f19514a.f19534j.add(this.f19514a.f19548a);
//        this.f19514a.f19535k.add(this.f19514a.f19548a);

        this.f19514a.f19534j.add(this.f19514a.f19549b);
//        this.f19514a.f19535k.add(this.f19514a.f19549b);

        this.f19514a.f19534j.add(this.f19514a.f19550c);
//        this.f19514a.f19535k.add(this.f19514a.f19550c);

        this.f19514a.f19534j.add(this.f19514a.f19551d);
//        this.f19514a.f19535k.add(this.f19514a.f19551d);

        this.f19514a.f19534j.add(this.f19514a.f19552e);
//        this.f19514a.f19535k.add(this.f19514a.f19552e);

        this.f19514a.f19534j.add(this.f19514a.f19552e);
//        this.f19514a.f19535k.add(this.f19514a.f19552e);







        this.f19514a.taskDetailItem = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem0 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem1 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem2 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem3 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem4 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem5 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem6 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem7 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem8 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem9 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem10 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem11 = new TaskDetailItem(0, this.f19517d, null, -1L);
        this.f19514a.taskDetailItem12 = new TaskDetailItem(0, this.f19517d, null, -1L);

        this.f19514a.f19534j.add(this.f19514a.taskDetailItem);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem0);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem1);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem2);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem3);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem4);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem5);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem6);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem7);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem8);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem9);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem10);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem11);
        this.f19514a.f19534j.add(this.f19514a.taskDetailItem12);











        ArrayList<TaskDetailItem> arrayList = this.f19514a.f19534j;
        ArrayList<TaskDetailItem> arrayList2 = this.f19514a.f19535k;
        arrayList2.add(new TaskDetailItem(7, new DownloadTaskInfo(), 80, 0L));
        this.f19516c.m9448a(arrayList, arrayList2);
//        this.f19523j = new TaskCommentController(getContext(), this.f19516c.f19564h);
        this.f19529p = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_task_detail, container, false);
//        this.f19530q = RegionConfigure.m11059a().m11058b();
        this.f19521h = (RecyclerView) inflate.findViewById(R.id.task_detail_recycler_view);
        this.f19531r = new LinearLayoutManager(getActivity());
        this.f19521h.setLayoutManager(this.f19531r);
        this.f19521h.setAdapter(this.f19516c);
        this.f19521h.setItemAnimator(new CustomItemAnimator());
//        this.f19523j.m9424a(inflate, this.f19521h);
        this.f19521h.setOnScrollListener(this.f19532s);
        return inflate;
    }


    @Override // android.support.p003v4.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.f19529p) {
//            m9487a();
            if (this.f19516c != null) {
                this.f19516c.notifyDataSetChanged();
            }
        }
        this.f19529p = false;
//        if (this.f19528o != null) {
//            try {
//                if (this.f19528o.isAlive()) {
//                    return;
//                }
//                this.f19528o.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override // android.support.p003v4.app.Fragment
    public void onDestroy() {
//        if (this.f19528o != null && this.f19528o.isAlive()) {
//            try {
//                this.f19528o.f18724a.sendEmptyMessage(1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (this.f19523j != null) {
//            this.f19523j.m9405c();
//            this.f19523j.f19603b = null;
//        }
//        m9481b();
        super.onDestroy();
//        this.f19522i.m11789f();
    }



    public final void m9481b() {
        if (isAdded()) {
            getLoaderManager().destroyLoader(10);
//            this.f19515b.f19537m.clear();
        }
    }

    public final void m9471g() {
        m9481b();
        if (this.f19516c != null) {
            this.f19516c.m9447a(true);
        }
    }



    public final void m9477d() {
        if (isVisible() && isAdded() && isVisible() && isAdded() && this.f19517d != null && this.f19516c != null) {
            this.f19516c.m9442f();
        }
    }

    public static /* synthetic */ void m9483a(TaskDetailFragment taskDetailFragment, DownloadTaskInfo downloadTaskInfo) {
//        if (downloadTaskInfo != null) {
//            if (TaskHelper.m8449d((TaskInfo) downloadTaskInfo)) {
//                if (TaskHelper.m8445g(downloadTaskInfo)) {
//                    DLCenterReporter.m9613a("finish_install", downloadTaskInfo);
//                } else if (TaskHelper.m8461a((TaskInfo) downloadTaskInfo)) {
//                    DLCenterReporter.m9613a("finish_play", downloadTaskInfo);
//                } else {
//                    DLCenterReporter.m9613a("finish_open", downloadTaskInfo);
//                }
//                if (TaskHelper.m8450c(downloadTaskInfo)) {
//                    taskDetailFragment.f19518e.m10702a(downloadTaskInfo, "", "download_detail");
//                    return;
//                } else {
//                    taskDetailFragment.f19518e.m10697b(downloadTaskInfo);
//                    return;
//                }
//            }
////            DLCenterReporter.m9613a("dl_bxbb", downloadTaskInfo);
//            if (downloadTaskInfo.getTaskStatus() == 4) {
//                CooperationController.m11286a().f17433e = true;
//                if (!AbstractC8585b.m963e(taskDetailFragment.getContext())) {
//                    taskDetailFragment.f19518e.m10695c(downloadTaskInfo);
//                }
//            }
//            C5083m.m9934a(taskDetailFragment.getContext(), downloadTaskInfo, null, "download_detail");
//        }
    }


}