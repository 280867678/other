package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.Platform;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class PlatformAdapter extends BaseQuickAdapter<Platform, BaseViewHolder> {
    Activity context;

    public PlatformAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, Platform platform) {
        TextView textView = (TextView) baseViewHolder.getView(R.id.name);
        textView.setTextColor(this.context.getResources().getColor(R.color.C_303133));
        textView.setText(platform.getPlatform());
    }
}
