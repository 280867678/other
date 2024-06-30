package com.example.floatdragview.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.floatdragview.R;
import com.example.floatdragview.adapter.TaskDetailViewHolder;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;

public final class DetailShareBarViewHolder extends TaskDetailViewHolder implements View.OnClickListener {

    /* renamed from: a */
    private DownloadTaskInfo f19722a;

    /* renamed from: b */
    private View f19723b;

    /* renamed from: c */
    private View f19724c;

    /* renamed from: i */
    private View f19725i;

    /* renamed from: j */
    private View f19726j;

    /* renamed from: k */
    private View f19727k;

    /* renamed from: l */
    private View f19728l;

    /* renamed from: m */
    private RelativeLayout f19729m;

    /* renamed from: n */
    private boolean f19730n;

    /* renamed from: o */
    private View.OnClickListener f19731o;

    /* renamed from: p */
//    private ShareListener f19732p;

    public DetailShareBarViewHolder(View view) {
        super(view);
        this.f19730n = true;
//        this.f19731o = new View$OnClickListenerC5249n(this);
//        this.f19732p = new C5250o(this);
//        this.f19730n = RegionConfigure.m11059a().m11057c();
        this.f19723b = view;
        this.f19729m =  this.f19723b.findViewById(R.id.operate_container);
        this.f19724c = this.f19723b.findViewById(R.id.detail_share_weixin_btn);
        this.f19724c.setOnClickListener(this);
        this.f19725i = this.f19723b.findViewById(R.id.detail_share_qq_btn);
        this.f19725i.setOnClickListener(this);
        this.f19726j = this.f19723b.findViewById(R.id.detail_share_qzone_btn);
        this.f19726j.setOnClickListener(this);
        this.f19727k = this.f19723b.findViewById(R.id.container);
        this.f19728l = this.f19723b.findViewById(R.id.share_btn_container);
        if (this.f19730n) {
            this.f19728l.setVisibility(0);
        } else {
            this.f19728l.setVisibility(8);
        }
    }

    /* renamed from: a */
    public static View m9331a(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.layout_task_detail_share_bar, viewGroup, false);
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9109a(TaskDetailItem taskDetailItem) {
        m9360b(taskDetailItem);
        DownloadTaskInfo downloadTaskInfo = taskDetailItem.f19671d;
        this.f19722a = downloadTaskInfo;
//        this.f19729m.m9309a(downloadTaskInfo, this.f19675g, this.f19674f);
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        if (this.f19722a == null) {
            return;
        }
        String str = "download_detail_bar";
        if (this.f19674f != null && "home_collect_card".equals(this.f19674f.f19567k)) {
            str = "home_collect_detail_bar";
        }
//        ShareOperationType shareOperationType = null;
        switch (view.getId()) {
            case R.id.detail_share_weixin_btn /* 2131887825 */:
//                shareOperationType = ShareOperationType.WEIXIN;
                break;
            case R.id.detail_share_qq_btn /* 2131887827 */:
//                shareOperationType = ShareOperationType.QQ;
                break;
            case R.id.detail_share_qzone_btn /* 2131887829 */:
            case R.id.textView6 /* 2131887830 */:
//                shareOperationType = ShareOperationType.QZONE;
                break;
        }
//        if (shareOperationType != null) {
//            DLCenterReporter.m9590b(shareOperationType.getReportShareTo(), str, this.f19722a.mGCID, this.f19722a.mTitle, this.f19722a.getTaskDownloadUrl());
//            ShareDownloadTaskInfo m8409a = ShareInfoCreator.m8409a(str, this.f19722a, CooperationHelper.m11304a().m11303a(7), "", "");
//            ShareHelper.m8419a();
//            ShareHelper.m8416a((Activity) this.itemView.getContext(), shareOperationType, m8409a, this.f19732p);
//        }
    }
}
