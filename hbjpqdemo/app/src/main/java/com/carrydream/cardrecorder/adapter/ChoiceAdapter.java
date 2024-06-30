package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.text.TextUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class ChoiceAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    Activity context;
    String quantifier;

    public ChoiceAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    public ChoiceAdapter(Activity activity, int i, String str) {
        super(i);
        this.context = activity;
        this.quantifier = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, Integer num) {
        StringBuilder sb = new StringBuilder();
        sb.append(num);
        sb.append("  ");
        sb.append(TextUtils.isEmpty(this.quantifier) ? "" : this.quantifier);
        baseViewHolder.setText(R.id.name, sb.toString());
    }
}
