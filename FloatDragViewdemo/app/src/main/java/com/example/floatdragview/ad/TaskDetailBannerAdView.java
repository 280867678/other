package com.example.floatdragview.ad;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.floatdragview.R;

public abstract class TaskDetailBannerAdView extends AbstractTaskDetailAdView {

    /* renamed from: e */
    private static final String f16574e = "d";

    /* renamed from: c */
    protected ImageView f16575c;

    /* renamed from: d */
    protected TextView f16576d;

    /* renamed from: f */
    private View f16577f;

    /* renamed from: g */
    private ViewGroup f16578g;

    /* renamed from: h */
//    private RequestListener<String, Bitmap> f16579h;

    protected abstract int getLayoutResourceId();

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: i */
    public void mo11774i() {
    }

    public TaskDetailBannerAdView(Context context) {
        super(context);
        this.f16577f = null;
        this.f16578g = null;
        this.f16575c = null;
        this.f16576d = null;
//        this.f16579h = new C4466f(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: b */
    public void mo11768b() {
        super.mo11768b();
        this.f16577f = LayoutInflater.from(this.f16567a).inflate(getLayoutResourceId(), this);
        this.f16577f.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        setBackgroundResource(R.color.common_content_bkg_color);
        this.f16578g = (ViewGroup) this.f16577f.findViewById(R.id.parent_view);
        this.f16575c = (ImageView) this.f16577f.findViewById(R.id.iv_banner);
        this.f16576d = (TextView) this.f16577f.findViewById(R.id.tv_title);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: c */
    public final void mo11766c() {
        super.mo11766c();
//        setOnClickListener(new View$OnClickListenerC4465e(this));
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: a */
    public final void mo11771a(@NonNull BaseAdapterModel baseAdapterModel) {
//        this.f16568b = baseAdapterModel;
        mo11775g();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: g */
    public void mo11775g() {
        this.f16576d.setText("this.f16568b.mo12332m()");
//        Glide.with(getContext()).load(this.f16568b.mo12333l()).asBitmap().listener((RequestListener<? super String, TranscodeType>) this.f16579h).into(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: h */
    public final void m11778h() {
//        m11779a(getBottomMarginWhileShow());
        setLayoutVisibility(0);
//        m11783b(this.f16568b);
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: d */
    public final void mo11765d() {
//        this.f16568b = null;
        m11779a(getBottomMarginWhileHide());
        setLayoutVisibility(8);
    }

    protected void setLayoutVisibility(int i) {
        this.f16578g.setVisibility(i);
    }

    /* renamed from: a */
    private void m11779a(int i) {
        ViewGroup.LayoutParams layoutParams = this.f16577f.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = i;
        }
    }
}

