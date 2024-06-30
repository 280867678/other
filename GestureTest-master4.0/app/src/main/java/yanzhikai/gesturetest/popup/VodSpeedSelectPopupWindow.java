package yanzhikai.gesturetest.popup;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import yanzhikai.gesturetest.R;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.xunlei.downloadprovider.download.player.vip.speedrate.h */
/* loaded from: classes3.dex */
public final class VodSpeedSelectPopupWindow extends VodPlayerBasePopupWindow {

    /* renamed from: a */
//    private XLPlayerDataSource f60071a;

    /* renamed from: b */
    private View f60072b;

    /* renamed from: c */
    private VodSpeedRateController f60073c;

    /* renamed from: d */
    private InterfaceC20426a f60074d;

    /* renamed from: e */
    private View f60075e;

    /* renamed from: f */
    private LinearLayout f60076f;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: VodSpeedSelectPopupWindow.java */
    /* renamed from: com.xunlei.downloadprovider.download.player.vip.speedrate.h$a */
    /* loaded from: classes3.dex */
    public interface InterfaceC20426a {
        /* renamed from: a */
        boolean mo33386a(VodSpeedRate vodSpeedRate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VodSpeedSelectPopupWindow(Context context, VodSpeedRateController vodSpeedRateController) {
        super(context);
        this.f60072b = LayoutInflater.from(context).inflate(R.layout.vod_play_speed_select_popupwindow, (ViewGroup) null);
        this.f60073c = vodSpeedRateController;
        setContentView(this.f60072b);
        setWidth(context.getResources().getDimensionPixelSize(R.dimen.vod_player_speed_rate_popup_width));
        this.f60072b.setFocusable(true);
        this.f60072b.setFocusableInTouchMode(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m33390a(InterfaceC20426a interfaceC20426a) {
        this.f60074d = interfaceC20426a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m33391a(VodSpeedRate vodSpeedRate) {
        if (vodSpeedRate == null) {
            vodSpeedRate = VodSpeedRate.getDefaultRate();
        }
        View view = this.f60075e;
        if (view != null) {
            view.setSelected(false);
        }
        int childCount = this.f60076f.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.f60076f.getChildAt(i);
            if (childAt.getTag() != null && (childAt.getTag() instanceof VodSpeedRate) && ((VodSpeedRate) childAt.getTag()) == vodSpeedRate) {
                childAt.setSelected(true);
                this.f60075e = childAt;
                return;
            }
        }
    }

    /* renamed from: a */
    private void m33393a(Context context, View view, Set<VodSpeedRate> set) {
        VodSpeedRate[] values;
        this.f60076f = (LinearLayout) view.findViewById(R.id.vod_speed_rate_pop_container);
        this.f60076f.removeAllViews();
        for (final VodSpeedRate vodSpeedRate : VodSpeedRate.values()) {
            if ( VodSpeedRate.RATE_4_POINT != vodSpeedRate) {
                View inflate = LayoutInflater.from(context).inflate(R.layout.include_vod_speed_rate_item, (ViewGroup) null);
                TextView textView = (TextView) inflate.findViewById(R.id.vod_speed_rate_item_tv);
                ImageView imageView = (ImageView) inflate.findViewById(R.id.vod_speed_rate_vip_iv);
                ImageView imageView2 = (ImageView) inflate.findViewById(R.id.vod_speed_rate_year_vip_iv);
                if (set != null && !set.isEmpty() && set.contains(vodSpeedRate)) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.vod_speed_rate_trail_ic);
                } else if (VodSpeedRate.isYearSVipRate(vodSpeedRate)) {
                    imageView.setImageResource(R.drawable.vod_super_vip_round_ic);
                    imageView.setVisibility(View.VISIBLE);
                    imageView2.setImageResource(R.drawable.speed_rate_year_vip_ic);
                    imageView2.setVisibility(View.VISIBLE);
                } else if (VodSpeedRate.isVipRate(vodSpeedRate)) {
                    imageView.setImageResource(R.drawable.vod_vip_round_ic);
                    imageView.setVisibility(View.VISIBLE);
                } else if (VodSpeedRate.isSVipRate(vodSpeedRate)) {
                    imageView.setImageResource(R.drawable.vod_super_vip_round_ic);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
                textView.setText(vodSpeedRate.getRateDescription());
                inflate.setOnClickListener(new View.OnClickListener() { // from class: com.xunlei.downloadprovider.download.player.vip.speedrate.h.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        if (VodSpeedSelectPopupWindow.this.f60075e != null) {
                            VodSpeedSelectPopupWindow.this.f60075e.setSelected(false);
                        }
                        VodSpeedSelectPopupWindow.this.f60075e = view2;
                        view2.setSelected(true);
                        if (VodSpeedSelectPopupWindow.this.f60074d == null || !VodSpeedSelectPopupWindow.this.f60074d.mo33386a(vodSpeedRate)) {
                            return;
                        }
                        VodSpeedSelectPopupWindow.this.dismiss();
                    }
                });
                inflate.setTag(vodSpeedRate);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0);
                layoutParams.weight = 1.0f;
                layoutParams.gravity = Gravity.CENTER ; //17
                this.f60076f.addView(inflate, layoutParams);
            }
        }
    }

    /* renamed from: a */
    public void m33392a( Set<VodSpeedRate> set) {
//        this.f60071a = xLPlayerDataSource;
        m33393a(this.f75814k, this.f60072b, set);
    }
}

