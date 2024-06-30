package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import com.carrydream.cardrecorder.Model.Item;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class AddAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {
    Activity context;
    int select;

    public AddAdapter(Activity activity, int i) {
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
        baseViewHolder.setText(R.id.name, item.getName());
        baseViewHolder.setBackgroundResource(R.id.item, R.drawable.bg6);
        baseViewHolder.setTextColor(R.id.name, this.context.getResources().getColor(R.color.white));
    }
}
