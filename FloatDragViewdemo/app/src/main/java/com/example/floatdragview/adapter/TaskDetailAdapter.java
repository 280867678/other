package com.example.floatdragview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.floatdragview.ad.AbstractTaskDetailAdController;
import com.example.floatdragview.ad.DetailADViewHolder;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;
import com.example.floatdragview.util.DownloadCenterControl;
import com.example.floatdragview.util.MultiPartList;
import com.example.floatdragview.viewholder.DetailEmptyPlaceholderViewHolder;
import com.example.floatdragview.viewholder.DetailOpenBarViewHolder;
import com.example.floatdragview.viewholder.DetailShareBarViewHolder;
import com.example.floatdragview.viewholder.DetailTaskBasicInfoViewHolder;

import java.util.List;

public class TaskDetailAdapter extends RecyclerView.Adapter<TaskDetailViewHolder> {

    /* renamed from: m */
    private static final String f19556m = "ag";

    /* renamed from: a */
    protected InterfaceC5203a f19557a;

    /* renamed from: b */
    protected Context f19558b;

    /* renamed from: c */
    protected DownloadCenterControl f19559c;

    /* renamed from: h */
//    public TaskCommentAdapter f19564h;

    /* renamed from: i */
//    public DetailWebSourceInfoViewHolder f19565i;

    /* renamed from: j */
    public boolean f19566j;

    /* renamed from: k */
    public String f19567k;

    /* renamed from: n */
    private DetailTaskBasicInfoViewHolder f19569n;

    /* renamed from: d */
    AbstractTaskDetailAdController f19560d = null;

    /* renamed from: e */
//    protected C5083m f19561e = null;

    /* renamed from: f */
    public MultiPartList<TaskDetailItem> f19562f = new MultiPartList<>();

    /* renamed from: g */
    public boolean f19563g = false;

    /* renamed from: l */
    protected boolean f19568l = true;

    /* compiled from: TaskDetailAdapter.java */
    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.ag$a */
    /* loaded from: classes2.dex */
    public interface InterfaceC5203a extends BTSubTaskInfoItemEditListener {
        /* renamed from: a */
        void mo9106a(DownloadTaskInfo downloadTaskInfo);

        /* renamed from: a */
        void mo9105a(boolean z);

        /* renamed from: c */
        void mo9103c();

        /* renamed from: d */
        void mo9102d();
    }

    public TaskDetailAdapter(Context context) {
        this.f19558b = context;
//        this.f19564h = new TaskCommentAdapter(context, this);
    }

    /* renamed from: a */
    public final DownloadCenterControl m9454a() {
        return this.f19559c;
    }

    /* renamed from: a */
    public final void m9447a(boolean z) {
        this.f19568l = z;
    }

    /* renamed from: a */
    public final void m9452a(DownloadCenterControl downloadCenterControl) {
        this.f19559c = downloadCenterControl;
    }

    /* renamed from: a */
//    public final void m9451a(C5083m c5083m) {
//        this.f19561e = c5083m;
//    }

    /* renamed from: b */
    public final InterfaceC5203a m9446b() {
        return this.f19557a;
    }

    /* renamed from: a */
    public final void m9450a(InterfaceC5203a interfaceC5203a) {
        this.f19557a = interfaceC5203a;
    }

    @Override // android.support.p006v7.widget.RecyclerView.Adapter
    /* renamed from: a */
    public TaskDetailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Log.e("TaskDetailViewHolder:", String.valueOf(i));


        DetailTaskBasicInfoViewHolder detailTaskBasicInfoViewHolder = null;
//        detailTaskBasicInfoViewHolder = null;
        if (1 == i) {
            this.f19569n = new DetailTaskBasicInfoViewHolder(DetailTaskBasicInfoViewHolder.m9327a(this.f19558b));
            DetailTaskBasicInfoViewHolder detailTaskBasicInfoViewHolder2 = this.f19569n;
            InterfaceC5203a interfaceC5203a = this.f19557a;
            if (detailTaskBasicInfoViewHolder2.f19735a != null) {
                detailTaskBasicInfoViewHolder2.f19735a.setRefreshListener(interfaceC5203a);
            }

            if (f19569n != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                f19569n.m9362a(this);
            }


            return this.f19569n;
        } else if (3 == i) {
            DetailOpenBarViewHolder detailOpenBarViewHolder = new DetailOpenBarViewHolder(DetailOpenBarViewHolder.m9338a(this.f19558b));
            detailOpenBarViewHolder.m9362a(this);

            if (detailOpenBarViewHolder != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                detailOpenBarViewHolder.m9362a(this);
            }


            return  detailOpenBarViewHolder;
        } else if (4 == i) {
            DetailShareBarViewHolder detailOpenBarViewHolder = new DetailShareBarViewHolder(DetailShareBarViewHolder.m9331a(this.f19558b, viewGroup));
            if (detailOpenBarViewHolder != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                detailOpenBarViewHolder.m9362a(this);
            }


            return detailOpenBarViewHolder;
        }
//        else if (5 == i) {
////            this.f19565i = new DetailWebSourceInfoViewHolder(DetailWebSourceInfoViewHolder.m9326a(this.f19558b));
////            detailTaskBasicInfoViewHolder = this.f19565i;
//        }
        else if (i == 0) {
            DetailBtTaskSubFileViewHolder detailBtTaskSubFileViewHolders = new DetailBtTaskSubFileViewHolder(DetailBtTaskSubFileViewHolder.m9357a(this.f19558b, viewGroup));
            detailBtTaskSubFileViewHolders.f19679b = this.f19557a;
            if (detailBtTaskSubFileViewHolders != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                detailBtTaskSubFileViewHolders.m9362a(this);
            }
            return detailBtTaskSubFileViewHolders;
//            return detailBtTaskSubFileViewHolders;
        } else if (7 == i) {
            DetailEmptyPlaceholderViewHolder detailEmptyPlaceholderViewHolder = new DetailEmptyPlaceholderViewHolder(DetailEmptyPlaceholderViewHolder.m9339a(this.f19558b, viewGroup));
            if (detailEmptyPlaceholderViewHolder != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                detailEmptyPlaceholderViewHolder.m9362a(this);
            }
            return detailEmptyPlaceholderViewHolder;
        }
        else if (8 == i || 9 == i || 10 == i) {
            View m9365a = DetailADViewHolder.m9365a(this.f19558b, i, this.f19560d);
            if (m9365a != null) {
                DetailADViewHolder detailADViewHolder = new DetailADViewHolder(m9365a);
                detailADViewHolder.f19666a = this.f19560d;

                if (detailTaskBasicInfoViewHolder != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
                    detailTaskBasicInfoViewHolder.m9362a(this);
                }
                return detailADViewHolder;
            }
        }
//        else if (i >= 110) {
////            detailTaskBasicInfoViewHolder = new TaskCommentViewHolder(this.f19564h.f19586a.onCreateViewHolder(viewGroup, i - 110));
//        }
        if (detailTaskBasicInfoViewHolder != null) {
//            detailTaskBasicInfoViewHolder.f19675g = this.f19559c;
//            detailTaskBasicInfoViewHolder.m9363a(this.f19561e);
            detailTaskBasicInfoViewHolder.m9362a(this);
        }
        return detailTaskBasicInfoViewHolder;
    }

    /* renamed from: c */
//    public TaskDetailViewHolder mo9194c() {
//        return this.f19569n;
//    }

    @Override // android.support.p006v7.widget.RecyclerView.Adapter
    /* renamed from: a */
    public void onBindViewHolder(TaskDetailViewHolder taskDetailViewHolder, int i) {
        TaskDetailItem taskDetailItem = null;
        if (taskDetailViewHolder != null) {
            new StringBuilder("onBindViewHolder ").append(taskDetailViewHolder.getClass().getSimpleName());
        }
        int m9462a = this.f19562f.m9462a();
        if (i < m9462a) {
            taskDetailItem = this.f19562f.m9461a(i);
        } else {
//            taskDetailItem = this.f19564h.f19587b.get(i - m9462a);
        }
//        taskDetailViewHolder.f19675g = this.f19559c;
        taskDetailViewHolder.mo9109a(taskDetailItem);
//        if (taskDetailViewHolder instanceof DetailTaskBasicInfoViewHolder) {
//            DetailTaskBasicInfoViewHolder detailTaskBasicInfoViewHolder = (DetailTaskBasicInfoViewHolder) taskDetailViewHolder;
//            if ((detailTaskBasicInfoViewHolder.f19735a != null ? detailTaskBasicInfoViewHolder.f19735a.getIsInCollapedState() : true) != this.f19568l) {
//                new StringBuilder("onBindViewHolder, mIsTaskDetailCollaped : ").append(this.f19568l);
//                boolean z = this.f19568l;
//                if (detailTaskBasicInfoViewHolder.f19735a != null) {
//                    detailTaskBasicInfoViewHolder.f19735a.setNeedFold(z);
//                    detailTaskBasicInfoViewHolder.f19735a.setCollaped(z);
//                }
//            }
//        }
    }

    @Override // android.support.p006v7.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return m9453a(i).f19668a;
    }

    /* renamed from: d */
    public final int m9444d() {
        return this.f19562f.m9462a();
    }

    /* renamed from: a */
    public final void m9448a(List<TaskDetailItem> list, List<TaskDetailItem> list2) {
        MultiPartList<TaskDetailItem> multiPartList = this.f19562f;
        multiPartList.f19538a.clear();
        multiPartList.f19539b.clear();
        multiPartList.f19540c.clear();
        multiPartList.f19541d.clear();
        this.f19562f.m9460a(list);
        this.f19562f.m9456d(list2);
        notifyDataSetChanged();
    }

    /* renamed from: b */
    public final void m9445b(List<TaskDetailItem> list, List<TaskDetailItem> list2) {
        this.f19562f.f19538a.clear();
        this.f19562f.f19541d.clear();
        this.f19562f.m9460a(list);
        this.f19562f.m9456d(list2);
        notifyDataSetChanged();
    }

    /* renamed from: e */
    public final void m9443e() {
        this.f19562f.f19538a.clear();
        this.f19562f.f19541d.clear();
        notifyDataSetChanged();
    }

    /* renamed from: a */
    public final void m9449a(TaskDetailItem taskDetailItem) {
        if (this.f19562f != null) {
            MultiPartList<TaskDetailItem> multiPartList = this.f19562f;
            if (taskDetailItem != null) {
                if (multiPartList.f19538a.contains(taskDetailItem)) {
                    multiPartList.f19538a.remove(taskDetailItem);
                } else if (multiPartList.f19539b.contains(taskDetailItem)) {
                    multiPartList.f19539b.remove(taskDetailItem);
                } else if (multiPartList.f19541d.contains(taskDetailItem)) {
                    multiPartList.f19541d.remove(taskDetailItem);
                }
            }
        }
        notifyDataSetChanged();
    }

    /* renamed from: a */
    public void mo9197a(List<TaskDetailItem> list) {
        this.f19562f.m9459b();
        this.f19562f.m9458b(list);
        this.f19562f.m9457c(list);
        this.f19562f.f19542e = false;
        notifyDataSetChanged();
    }

    /* renamed from: f */
    public final void m9442f() {
        if (this.f19562f == null) {
            return;
        }
        notifyItemRangeChanged(0, this.f19562f.m9462a());
    }

    /* renamed from: a */
    public final TaskDetailItem m9453a(int i) {
        int m9462a = this.f19562f.m9462a();
        if (i < m9462a) {
            return this.f19562f.m9461a(i);
        }
        return this.f19562f.m9461a(0);
    }

    @Override // android.support.p006v7.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        if (i < this.f19562f.m9462a() && this.f19562f.m9461a(i) != null) {
            TaskDetailItem m9461a = this.f19562f.m9461a(i);
            long j = m9461a.f19669b;
            if (j == -1 || j > 2147483647L) {
                return -1L;
            }
            return (m9461a.f19668a << 32) | (j & 2147483647L);
        }
        return i;
    }

    @Override // android.support.p006v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.f19563g) {
            return this.f19562f.m9462a();
        }
        return this.f19562f.m9462a() ;
    }











}
