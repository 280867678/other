package yanzhikai.gesturetest.popup;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

import yanzhikai.gesturetest.R;
import yanzhikai.gesturetest.VideoGestureRelativeLayout;
import yanzhikai.gesturetest.util.VodSpeedRateHelper;

public class VodSpeedRateProcessor {


    private VodSpeedSelectPopupWindow f60060g;
    private VodSpeedRateController f60063j;
    public VodSpeedRate f60056c;
    public VodSpeedRate f60055b;
    private InterfaceC20424a f60064k;
    public interface InterfaceC20424a {
        void mo33405a(VodSpeedRate vodSpeedRate, VodSpeedRate vodSpeedRate2);
        boolean mo33407a();
        boolean mo33406a(VodSpeedRate vodSpeedRate);
        boolean mo33404b();
        Set<VodSpeedRate> mo33403c();
    }

    private VideoGestureRelativeLayout f60057d;
    private View.OnClickListener f60059f;
    public VodSpeedRateTextView f60054a;
    private View f60058e;


    public VodSpeedRateProcessor(VideoGestureRelativeLayout vodPlayerView, VodSpeedRateController vodSpeedRateController, View.OnClickListener onClickListener) {
        this.f60057d = vodPlayerView;
        this.f60063j = vodSpeedRateController;
        this.f60059f = onClickListener;
        m33414f();
    }

    private void m33414f() {
        if (this.f60054a == null) {
            this.f60054a = (VodSpeedRateTextView) this.f60057d.findViewById(R.id.vod_speed_rate_view);
            this.f60058e = this.f60057d.findViewById(R.id.vod_speed_rate_container);
            this.f60058e.setOnClickListener(new View.OnClickListener() { // from class: com.xunlei.downloadprovider.download.player.vip.speedrate.e.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
//                    f60057d.m34898c(7);
                    m33412g();
                    if (f60059f != null) {
                        f60059f.onClick(view);
                    }
                }
            });
            this.f60054a.getPaint().setAntiAlias(true);
        }
//        m33432a();
        this.f60054a.setRate(this.f60056c);
        this.f60058e.setVisibility(View.GONE);
    }


    public VodSpeedRate m33418d() {
        return this.f60056c;
    }





    public void m33412g() {
        f60060g = new VodSpeedSelectPopupWindow(f60057d.getContext(),this.f60063j);
        f60060g.m33390a(new VodSpeedSelectPopupWindow.InterfaceC20426a() {
            @Override
            public boolean mo33386a(VodSpeedRate vodSpeedRate) {
                boolean z = false;
                if (f60056c == vodSpeedRate) {
                    return false;
                }

                f60055b = vodSpeedRate;
//                VodSpeedRateReporter.m33401a(vodSpeedRate, m33410h());
                if (VodSpeedRate.isYearSVipRate(vodSpeedRate)) {
                    if (f60064k != null && f60064k.mo33406a(vodSpeedRate)) {
                        m33429a(vodSpeedRate, false);
                        z = true;
//                        AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                        return true;
                    }
//                    m33430a(vodSpeedRate, "ch_operation_bar");
//                    AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                    return true;
                } else if (VodSpeedRate.isSVipRate(vodSpeedRate)) {
                    if (f60064k != null && f60064k.mo33407a()) {
                        m33429a(vodSpeedRate, false);
                        z = true;
//                        AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                        return true;
                    }
//                    m33430a(vodSpeedRate, "ch_operation_bar");
//                    AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                    return true;
                } else {
                    if (!true) {
                        if (VodSpeedRate.isVipRate(vodSpeedRate)) {
                            if (f60064k != null && f60064k.mo33404b()) {
                                m33429a(vodSpeedRate, false);
                            } else {
//                                m33430a(vodSpeedRate, "bj_operation_bar");
//                                AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                                return true;
                            }
                        } else {
                            m33429a(vodSpeedRate, false);
                        }
                    } else {
                        m33429a(vodSpeedRate, false);
                    }
//                    z = true;
//                    AndroidXPanPlayerReport.m36220a(this.m33410h(), vodSpeedRate, this.f60063j, this.f60061h, z);
                    return true;
                }
            }
        });
    }


    public void m33429a(VodSpeedRate vodSpeedRate, boolean z) {
        if (this.f60054a == null) {
            return;
        }
        InterfaceC20424a interfaceC20424a = this.f60064k;
        if (interfaceC20424a != null) {
            interfaceC20424a.mo33405a(this.f60056c, vodSpeedRate);
        }
        boolean z2 = vodSpeedRate != this.f60056c;
        this.f60056c = vodSpeedRate;
        this.f60055b = vodSpeedRate;
        this.f60054a.setRate(vodSpeedRate);
        if (z || !z2) {
            return;
        }
        Toast.makeText(this.f60054a.getContext(),VodSpeedRateHelper.m33435a(this.f60054a.getContext(), vodSpeedRate),Toast.LENGTH_LONG).show();
    }





}
