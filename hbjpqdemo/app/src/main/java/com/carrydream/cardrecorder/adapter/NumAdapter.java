package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.content.res.Resources;
import com.carrydream.cardrecorder.Model.Item;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class NumAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {
    Activity context;
    int select;

    public NumAdapter(Activity activity, int i) {
        super(i);
        this.select = 0;
        this.context = activity;
    }

    public void setSelect(int i) {
        this.select = i;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, Item item) {
        Resources resources;
        int i;
        baseViewHolder.setText(R.id.name, item.getName());
        if (this.select == getItemPosition(item)) {
            baseViewHolder.setBackgroundResource(R.id.item, R.drawable.bg6);
            baseViewHolder.setTextColor(R.id.name, this.context.getResources().getColor(R.color.white));
            return;
        }
        baseViewHolder.setBackgroundResource(R.id.item, R.drawable.bg16);
        if (item.isEnable()) {
            resources = this.context.getResources();
            i = R.color.C_303133;
        } else {
            resources = this.context.getResources();
            i = R.color.C_C8CACC;
        }
        baseViewHolder.setTextColor(R.id.name, resources.getColor(i));
    }
}
