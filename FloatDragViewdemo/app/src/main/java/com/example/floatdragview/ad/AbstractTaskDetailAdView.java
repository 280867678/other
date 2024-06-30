package com.example.floatdragview.ad;

import android.app.Dialog;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.floatdragview.TaskDetailFragment;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.util.DipPixelUtil;
import com.example.floatdragview.util.TaskHelper;

public abstract class AbstractTaskDetailAdView extends FrameLayout {

    /* renamed from: c */
    private static final String f16566c = "a";

    /* renamed from: a */
    protected Context f16567a;

    /* renamed from: b */
//    protected BaseAdapterModel f16568b;

    /* renamed from: d */
//    private Dialog f16569d;

    /* renamed from: e */
    private AbstractTaskDetailAdController f16570e;

    /* renamed from: f */
    private DownloadTaskInfo f16571f;

    /* renamed from: a */
    public abstract void mo11771a(@NonNull BaseAdapterModel baseAdapterModel);

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: b */
    public void mo11768b() {
    }

    /* renamed from: d */
    public abstract void mo11765d();

    public abstract String getAdUIStyle();

    public AbstractTaskDetailAdView(Context context) {
        super(context);
        this.f16567a = null;
//        this.f16568b = null;
//        this.f16569d = null;
        this.f16570e = null;
        this.f16571f = null;
        this.f16567a = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: a */
    public final void m11784a() {
        mo11768b();
        mo11766c();
        mo11765d();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: c */
    public void mo11766c() {
//        DialogInterface$OnClickListenerC4463b dialogInterface$OnClickListenerC4463b = new DialogInterface$OnClickListenerC4463b(this);
//        DialogInterface$OnClickListenerC4464c dialogInterface$OnClickListenerC4464c = new DialogInterface$OnClickListenerC4464c(this);
//        if (this.f16569d == null) {
//            this.f16569d = new Dialog(this.f16567a);
//            this.f16569d.setTitle("温馨提示");
//            this.f16569d.setMessage("当前为移动网络，开始下载/安装应用？");
//            this.f16569d.setConfirmButtonText("确认");
//            this.f16569d.setCancelButtonText("取消");
//        }
//        this.f16569d.setOnClickConfirmButtonListener(dialogInterface$OnClickListenerC4463b);
//        this.f16569d.setOnClickCancelButtonListener(dialogInterface$OnClickListenerC4464c);
    }

    public void setDownloadTaskInfo(DownloadTaskInfo downloadTaskInfo) {
        this.f16571f = downloadTaskInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
//    public int getBottomMarginWhileShow() {
//        if (this.f16571f == null || !TaskHelper.m8447e(this.f16571f) || TaskDetailFragment.m9485a(this.f16571f)) {
//            return 0;
//        }
//        return DipPixelUtil.dip2px(5.0f);
//    }

    /* JADX INFO: Access modifiers changed from: protected */
//    public int getBottomMarginWhileHide() {
//        if (this.f16571f == null || !TaskHelper.m8447e(this.f16571f)) {
//            return 0;
//        }
//        return DipPixelUtil.dip2px(5.0f);
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    /* renamed from: b */
//    public final void m11783b(@NonNull BaseAdapterModel baseAdapterModel) {
//        if (this.f16570e != null) {
//            this.f16570e.mo11794a(this, baseAdapterModel);
//        }
//    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: c */
//    public final void m11782c(BaseAdapterModel baseAdapterModel) {
//        if (this.f16570e == null || baseAdapterModel == null) {
//            return;
//        }
//        this.f16570e.mo11793b(this, baseAdapterModel);
//    }

    public void setAdController(AbstractTaskDetailAdController abstractTaskDetailAdController) {
        this.f16570e = abstractTaskDetailAdController;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: e */
//    public final void m11781e() {
//        if (this.f16569d != null) {
//            this.f16569d.show();
//        }
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    /* renamed from: f */
//    public final void m11780f() {
//        if (this.f16569d != null) {
//            this.f16569d.dismiss();
//        }
//    }


    public int getBottomMarginWhileHide() {
        if (this.f16571f == null ) {
            return 0;
        }
        return DipPixelUtil.dip2px(5.0f);
    }

}

