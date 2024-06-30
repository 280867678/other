package com.example.floatdragview.adapter;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;

public abstract class TaskDetailViewHolder extends RecyclerView.ViewHolder {

    /* renamed from: d */
    protected DownloadTaskInfo f19672d;

    /* renamed from: e */
    protected TaskDetailItem f19673e;

    /* renamed from: f */
    protected TaskDetailAdapter f19674f;

    /* renamed from: g */
//    public DownloadCenterControl f19675g;

    /* renamed from: h */
//    protected C5083m f19676h;

    /* renamed from: a */
    public void mo9111a() {
    }

    /* renamed from: a */
    public abstract void mo9109a(TaskDetailItem taskDetailItem);

    /* renamed from: a */
    public void mo9108a(boolean z) {
    }

    public TaskDetailViewHolder(View view) {
        super(view);
    }

    /* renamed from: a */
//    public final void m9363a(C5083m c5083m) {
//        this.f19676h = c5083m;
//    }

    /* renamed from: a */
    public final void m9362a(TaskDetailAdapter taskDetailAdapter) {
        this.f19674f = taskDetailAdapter;
    }

    /* renamed from: b */
    public final TaskDetailItem m9361b() {
        return this.f19673e;
    }

    /* renamed from: c */
//    public final Activity m9359c() {
//        if (this.f19674f.m9454a() == null) {
//            return null;
//        }
//        return this.f19674f.m9454a().f18209a;
//    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    /* renamed from: d */
    public final DownloadTaskInfo m9358d() {
        return this.f19672d;
    }

    /* renamed from: b */
    public final void m9360b(TaskDetailItem taskDetailItem) {
        this.f19672d = taskDetailItem == null ? null : taskDetailItem.f19671d;
        this.f19673e = taskDetailItem;
    }
}
