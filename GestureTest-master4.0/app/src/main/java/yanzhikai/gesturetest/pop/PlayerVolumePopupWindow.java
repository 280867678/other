package yanzhikai.gesturetest.pop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.google.android.material.internal.ContextUtils;

import yanzhikai.gesturetest.R;

public class PlayerVolumePopupWindow extends PopupWindow {

    /* renamed from: a */
    private Context f67744a;

    /* renamed from: b */
    private View f67745b;

    /* renamed from: c */
    private ProgressBar f67746c;

    /* renamed from: d */
    private ImageView f67747d;

    /* renamed from: e */
    private int f67748e;

    public PlayerVolumePopupWindow(Context context, View view) {
        super(context);
        this.f67748e = 0;
        this.f67744a = context;
        this.f67745b = view;
        setBackgroundDrawable(new ColorDrawable(0));
    }

    @SuppressLint("RestrictedApi")
    public void show() {
        Context context;
        int i;
        if (ContextUtils.getActivity(this.f67744a) == null) {
            return;
        }
        if (ScreenTool.isLandscape(ContextUtils.getActivity(this.f67744a))) {
            context = this.f67744a;
            i = R.layout.unused_res_a_res_0x7f030f5c;
        } else {
            context = this.f67744a;
            i = R.layout.unused_res_a_res_0x7f030f5b;
        }
        ViewGroup viewGroup = (ViewGroup) View.inflate(context, i, null);
        View view = this.f67745b;
        if (view != null && view.getParent() != null && viewGroup != null) {
            this.f67746c = (ProgressBar) viewGroup.findViewById(R.id.unused_res_a_res_0x7f0a1361);
            this.f67747d = (ImageView) viewGroup.findViewById(R.id.unused_res_a_res_0x7f0a1360);
            setContentView(viewGroup);
            setWidth(-2);
            setHeight(-2);
            setOutsideTouchable(true);
            int maxVolume = Utility.getMaxVolume(this.f67744a);
            this.f67748e = Utility.getCurrentVolume(this.f67744a);
            this.f67746c.setMax(maxVolume);
            this.f67746c.setProgress(this.f67748e);
            if (ScreenTool.isLandscape(ContextUtils.getActivity(this.f67744a))) {
                super.showAtLocation(this.f67745b, 48, 0, UIUtils.dip2px(30.0f));
            } else {
                super.showAtLocation(this.f67745b, 17, 0, 0);
            }
        }
    }

    public void updateVolume(float f) {
        if (this.f67746c == null || this.f67747d == null) {
            return;
        }
        this.f67748e = Utility.getCurrentVolume(this.f67744a);
        int max = this.f67746c.getMax();
        int round = Math.round(max / 15.0f);
        if (round == 0) {
            round = 1;
        }
        int max2 = Math.max(0, Math.min(max, Math.max(Math.min((int) (f * (1080.0f / this.f67745b.getHeight())), round), -round) + this.f67748e));
        if (Utility.getCurrentVolume(this.f67744a) != max2) {
            Utility.setVolume(this.f67744a, max2);
        }
        this.f67746c.setProgress(max2);
        this.f67747d.setImageLevel((max2 * 100) / max);
    }
}

