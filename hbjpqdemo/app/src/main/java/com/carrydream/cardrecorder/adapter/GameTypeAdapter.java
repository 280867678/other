package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.GameType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class GameTypeAdapter extends BaseQuickAdapter<GameType, BaseViewHolder> {
    Activity context;

    public GameTypeAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, GameType gameType) {
        TextView textView = (TextView) baseViewHolder.getView(R.id.name);
        if (gameType.isIs_success()) {
            textView.setTextColor(this.context.getResources().getColor(R.color.C_303133));
            textView.setText(gameType.getName());
            return;
        }
        textView.setTextColor(this.context.getResources().getColor(R.color.C_C8CACC));
        textView.setText(gameType.getName() + "（正在研发中）");
    }
}
