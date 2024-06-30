package com.example.floatdragview.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.floatdragview.R;
import com.example.floatdragview.adapter.TaskDetailViewHolder;
import com.example.floatdragview.datasource.TaskDetailItem;

public final class DetailEmptyPlaceholderViewHolder extends TaskDetailViewHolder {
    /* renamed from: a */
    public static View m9339a(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.layout_task_empty, viewGroup, false);
    }

    public DetailEmptyPlaceholderViewHolder(View view) {
        super(view);
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9109a(TaskDetailItem taskDetailItem) {
        m9360b(taskDetailItem);
        Integer num = (Integer) taskDetailItem.m9364a(Integer.class);
        if (num != null) {
            this.itemView.setPadding(0, 0, 0, num.intValue());
        } else {
            this.itemView.setPadding(0, 0, 0, 0);
        }
    }
}
