package com.carrydream.cardrecorder.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.Plans;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class VipAdapter extends BaseQuickAdapter<Plans.PlansDTO, BaseViewHolder> {
    Activity context;
    int select;

    public VipAdapter(Activity activity, int i) {
        super(i);
        this.select = 0;
        this.context = activity;
    }

    public void setSelect(int i) {
        this.select = i;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, Plans.PlansDTO plansDTO) {
        BaseViewHolder text = baseViewHolder.setText(R.id.name, "VIP " + plansDTO.getDay_count() + "天");
        StringBuilder sb = new StringBuilder();
        sb.append(plansDTO.getPrice());
        sb.append("");
        BaseViewHolder text2 = text.setText(R.id.price, sb.toString());
        text2.setText(R.id.original_price, "¥" + plansDTO.getOriginal_price());
        TextView textView = (TextView) baseViewHolder.getView(R.id.intro);
        ((TextView) baseViewHolder.getView(R.id.original_price)).getPaint().setFlags(16);
        ((TextView) baseViewHolder.getView(R.id.price)).setTypeface(Typeface.createFromAsset(this.context.getAssets(), "D-DIN-Bold.woff.ttf"));
        if (this.select == getItemPosition(plansDTO)) {
            baseViewHolder.setBackgroundResource(R.id.item, R.drawable.bg14);
        } else {
            baseViewHolder.setBackgroundResource(R.id.item, R.drawable.bg15);
        }
        if (plansDTO.getIntro() != null && !plansDTO.getIntro().equals("")) {
            textView.setVisibility(0);
            textView.setText(plansDTO.getIntro());
            return;
        }
        textView.setVisibility(4);
    }
}
