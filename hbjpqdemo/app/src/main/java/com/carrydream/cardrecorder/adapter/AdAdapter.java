package com.carrydream.cardrecorder.adapter;

import android.app.Activity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class AdAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    Activity context;

    public AdAdapter(Activity activity, int i) {
        super(i);
        this.context = activity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, String str) {
        Glide.with(this.context).load(str).apply((BaseRequestOptions<?>) RequestOptions.bitmapTransform(new MultiTransformation(new CenterCrop(), new RoundedCorners(10)))).transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build())).into((ImageView) baseViewHolder.getView(R.id.cover));
    }
}
