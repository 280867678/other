package com.example.floatdragview.ad;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.floatdragview.R;

class TaskDetailStyleDownloadListAdView extends AbstractTaskDetailAdView {

    /* renamed from: k */
    private static final String f16587k = "j";

    /* renamed from: c */
    protected ViewGroup f16588c;

    /* renamed from: d */
    protected ImageView f16589d;

    /* renamed from: e */
    protected TextView f16590e;

    /* renamed from: f */
    protected RatingBar f16591f;

    /* renamed from: g */
    protected TextView f16592g;

    /* renamed from: h */
    protected View f16593h;

    /* renamed from: i */
    protected TextView f16594i;

    /* renamed from: j */
    protected TextView f16595j;

    /* renamed from: l */
    private View f16596l;

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    public String getAdUIStyle() {
        return "100";
    }

    public TaskDetailStyleDownloadListAdView(Context context) {
        super(context);
        this.f16596l = null;
        m11784a();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: b */
    public final void mo11768b() {
        super.mo11768b();
        this.f16596l = LayoutInflater.from(this.f16567a).inflate(R.layout.layout_task_detail_ad_style_download_list, this);
        this.f16596l.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        this.f16588c = (ViewGroup) this.f16596l.findViewById(R.id.parent_view);
        this.f16589d = (ImageView) this.f16596l.findViewById(R.id.iconImageView);
        this.f16590e = (TextView) this.f16596l.findViewById(R.id.titleTextView);
        this.f16593h = this.f16596l.findViewById(R.id.closeButton);
        this.f16594i = (TextView) this.f16596l.findViewById(R.id.actionButton);
        this.f16595j = (TextView) this.f16596l.findViewById(R.id.tagView);
        this.f16591f = (RatingBar) this.f16596l.findViewById(R.id.score_rb);
        this.f16592g = (TextView) this.f16596l.findViewById(R.id.install_count);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: c */
    public final void mo11766c() {
        super.mo11766c();
//        setOnClickListener(new View$OnClickListenerC4467k(this));
//        this.f16593h.setOnClickListener(new View$OnClickListenerC4468l(this));
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: a */
    public final void mo11771a(@NonNull BaseAdapterModel baseAdapterModel) {
        String string;
//        this.f16568b = baseAdapterModel;
//        m11773a(getBottomMarginWhileShow());
        this.f16590e.setText("this.f16568b.mo12332m()");
//        this.f16591f.setRating("this.f16568b.mo12328q()");
        TextView textView = this.f16594i;
//        BaseAdapterModel baseAdapterModel2 = this.f16568b;
//        if (baseAdapterModel2.mo12321x() == null || baseAdapterModel2.mo12321x().trim().equals("")) {
//            if (baseAdapterModel2.mo12324u() != 2) {
//                string = BrothersApplication.getApplicationInstance().getString(R.string.ad_web_action_name_short);
//            } else {
//                int mo12356D = baseAdapterModel2.mo12356D();
//                Application applicationInstance = BrothersApplication.getApplicationInstance();
//                String string2 = BrothersApplication.getApplicationInstance().getString(R.string.ad_app_status_undownload_short);
//                switch (mo12356D) {
//                    case 1:
//                        string = applicationInstance.getString(R.string.ad_app_status_undownload_short);
//                        break;
//                    case 2:
//                        string = applicationInstance.getString(R.string.ad_app_status_downloading_short);
//                        break;
//                    case 3:
//                        string = applicationInstance.getString(R.string.ad_app_status_uninstall_short);
//                        break;
//                    case 4:
//                        string = applicationInstance.getString(R.string.ad_app_status_intalled_short);
//                        break;
//                    default:
//                        string = string2;
//                        break;
//                }
//            }
//        } else {
//            string = baseAdapterModel2.mo12321x().trim();
//        }
//        textView.setText(string);
//        String m12169a = DownloadListADUtils.m12169a(this.f16568b.mo12331n());
//        if (TextUtils.isEmpty(m12169a) || m12169a.equals("0")) {
//            this.f16592g.setVisibility(8);
//        } else {
//            this.f16592g.setText(m12169a);
//            this.f16592g.setVisibility(0);
//        }
//        this.f16595j.setText(CommonUtil.m12252a(this.f16568b, (int) R.string.choiceness_ad_source_guanggao));
//        Context context = getContext();
//        String mo12334k = this.f16568b.mo12334k();
//        ADGlideBuilders.m12266b(context, mo12334k).placeholder((int) R.drawable.task_detail_image_ad_default_icon).error((int) R.drawable.task_detail_image_ad_default_icon).fallback((int) R.drawable.task_detail_image_ad_default_icon).listener((RequestListener<? super String, Bitmap>) null).into(this.f16589d);
//        setLayoutVisibility(0);
//        m11783b(this.f16568b);
    }

    /* renamed from: a */
    private void m11773a(int i) {
        ViewGroup.LayoutParams layoutParams = this.f16596l.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = i;
        }
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: d */
    public final void mo11765d() {
//        this.f16568b = null;
        m11773a(getBottomMarginWhileHide());
        setLayoutVisibility(8);
        this.f16589d.setImageResource(R.drawable.download_ad_background);
    }

    private void setLayoutVisibility(int i) {
        this.f16588c.setVisibility(i);
    }
}

