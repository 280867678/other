package com.example.floatdragview.ad;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class TaskDetailEmptyAdView extends AbstractTaskDetailAdView {

    /* renamed from: c */
    private static final String f16586c = "i";

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: a */
    public final void mo11771a(@NonNull BaseAdapterModel baseAdapterModel) {
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    public String getAdUIStyle() {
        return "-1";
    }

    public TaskDetailEmptyAdView(Context context) {
        super(context);
        setLayoutParams(new FrameLayout.LayoutParams(0, 0));
        mo11765d();
    }

    @Override // com.xunlei.downloadprovider.p340ad.taskdetail.view.AbstractTaskDetailAdView
    /* renamed from: d */
    public final void mo11765d() {
        int bottomMarginWhileHide = getBottomMarginWhileHide();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = bottomMarginWhileHide;
        }
    }
}
