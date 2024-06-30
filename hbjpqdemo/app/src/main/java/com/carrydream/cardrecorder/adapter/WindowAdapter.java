package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.ZdyPoker;
import com.carrydream.cardrecorder.tool.Tool;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class WindowAdapter extends BaseQuickAdapter<ZdyPoker, BaseViewHolder> {
    Activity context;

    public WindowAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, ZdyPoker zdyPoker) {
        baseViewHolder.setText(R.id.count, zdyPoker.getCount() + "");
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.img);
        TextView textView = (TextView) baseViewHolder.getView(R.id.count);
        textView.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "D-DIN-Bold.woff.ttf"));
        if (zdyPoker.getCount() == 0) {
            textView.setTextColor(this.context.getResources().getColor(R.color.C_969799));
        } else if (zdyPoker.getCount() >= 4) {
            textView.setTextColor(this.context.getResources().getColor(R.color.C_fd8353));
        } else {
            textView.setTextColor(this.context.getResources().getColor(R.color.c_feffdf));
        }
        imageView.setImageResource(Tool.getIMG(zdyPoker.getName()));
    }
}
