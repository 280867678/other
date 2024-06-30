package com.example.floatdragview.ad;

import android.content.Context;
import android.view.View;

import com.example.floatdragview.adapter.TaskDetailViewHolder;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;

public class DetailADViewHolder extends TaskDetailViewHolder {

    /* renamed from: b */
    private static final String f19665b = "a";

    /* renamed from: a */
    public AbstractTaskDetailAdController f19666a;

    /* renamed from: c */
    private AbstractTaskDetailAdView f19667c;

    /* renamed from: a */
    public static View m9365a(Context context, int i, AbstractTaskDetailAdController abstractTaskDetailAdController) {
        AbstractTaskDetailAdView taskDetailStyleDownloadListAdView;
        switch (i) {
            case 8:
                taskDetailStyleDownloadListAdView = new TaskDetailStyleDownloadListAdView(context);
                break;
            case 9:
//                taskDetailStyleDownloadListAdView = TaskDetailBannerAdViewFactory.m11776a(context);
//                break;
            case 10:
                taskDetailStyleDownloadListAdView = new TaskDetailEmptyAdView(context);
                break;
            default:
                taskDetailStyleDownloadListAdView = null;
                break;
        }
        if (taskDetailStyleDownloadListAdView != null) {
            taskDetailStyleDownloadListAdView.setAdController(abstractTaskDetailAdController);
        }
        return taskDetailStyleDownloadListAdView;
    }

    public DetailADViewHolder(View view) {
        super(view);
        this.f19666a = null;
        this.f19667c = null;
        if (view instanceof AbstractTaskDetailAdView) {
            this.f19667c = (AbstractTaskDetailAdView) view;
        }
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9109a(TaskDetailItem taskDetailItem) {
        m9360b(taskDetailItem);
        DownloadTaskInfo downloadTaskInfo = taskDetailItem.f19671d;
        if (this.f19666a != null && this.f19667c != null) {
            this.f19667c.setDownloadTaskInfo(downloadTaskInfo);
            this.f19666a.mo11795a(this.f19667c);
            return;
        }
        StringBuilder sb = new StringBuilder("onBindData,Both AbstractTaskDetailAd and AbstractTaskDetailAdView can not be null: (AbstractTaskDetailAd == null): ");
        sb.append(this.f19666a == null);
        sb.append(" (AbstractTaskDetailAdView==null): ");
        sb.append(this.f19667c == null);
    }
}

