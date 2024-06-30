package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.carrydream.cardrecorder.Model.ZdyPoker;
import com.carrydream.cardrecorder.tool.Tool;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class ZdyAdapter extends BaseQuickAdapter<ZdyPoker, BaseViewHolder> {
    Activity context;

    public ZdyAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, ZdyPoker zdyPoker) {
        baseViewHolder.setText(R.id.count, zdyPoker.getCount() + "");
        ((TextView) baseViewHolder.getView(R.id.count)).setTypeface(Typeface.createFromAsset(this.context.getAssets(), "D-DIN-Bold.woff.ttf"));
        Glide.with(this.context).load(Integer.valueOf(Tool.getIMG(zdyPoker.getName()))).into((ImageView) baseViewHolder.getView(R.id.img));
    }
}
