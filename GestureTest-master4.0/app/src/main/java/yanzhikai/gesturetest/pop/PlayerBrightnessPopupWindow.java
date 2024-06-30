package yanzhikai.gesturetest.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.google.android.material.internal.ContextUtils;

import yanzhikai.gesturetest.R;


public class PlayerBrightnessPopupWindow extends PopupWindow {

    /* renamed from: a */
    private Context f67732a;

    /* renamed from: b */
    private View f67733b;

    /* renamed from: c */
    private ProgressBar f67734c;

    /* renamed from: d */
    private ImageView f67735d;

    /* renamed from: e */
    private int f67736e;

    public PlayerBrightnessPopupWindow(Context context, View view) {
        super(context);
        this.f67736e = 1;
        this.f67732a = context;
        this.f67733b = view;
        setBackgroundDrawable(new ColorDrawable(0));
    }

    /* renamed from: a */
    private static int m90581a(Context context) {
        @SuppressLint("RestrictedApi") Activity activity = ContextUtils.getActivity(context);
        if (activity == null) {
            return 0;
        }
        try {
            int i = (int) (activity.getWindow().getAttributes().screenBrightness * 255.0f);
            if (i < 0) {
                try {
                    return Settings.System.getInt(context.getContentResolver(), "screen_brightness");
                } catch (Settings.SettingNotFoundException unused) {
                    return i;
                }
            }
            return i;
        } catch (Exception unused2) {
            return 0;
        }
    }

    @SuppressLint("RestrictedApi")
    public void show() {
        Context context;
        int i;
        if (ContextUtils.getActivity(this.f67732a) == null) {
            return;
        }
        if (ScreenTool.isLandscape(ContextUtils.getActivity(this.f67732a))) {
            context = this.f67732a;
            i = R.layout.unused_res_a_res_0x7f030f59;
        } else {
            context = this.f67732a;
            i = R.layout.unused_res_a_res_0x7f030f58;
        }
        ViewGroup viewGroup = (ViewGroup) View.inflate(context, i, null);
        View view = this.f67733b;
        if (view != null && view.getParent() != null && viewGroup != null) {
            this.f67734c = (ProgressBar) viewGroup.findViewById(R.id.unused_res_a_res_0x7f0a1355);
            this.f67735d = (ImageView) viewGroup.findViewById(R.id.unused_res_a_res_0x7f0a28b9);
            setContentView(viewGroup);
            setWidth(-2);
            setHeight(-2);
            setOutsideTouchable(true);
            this.f67736e = m90581a(this.f67732a);
            this.f67734c.setMax(255);
            this.f67734c.setProgress(this.f67736e);
            if (ScreenTool.isLandscape(ContextUtils.getActivity(this.f67732a))) {
                super.showAtLocation(this.f67733b, 48, 0, UIUtils.dip2px(30.0f));
            } else {
                super.showAtLocation(this.f67733b, Gravity.CENTER, 0, 0);
            }
        }
    }

    public void updateBrightness(float f) {
        this.f67736e = m90581a(this.f67732a);
        int round = Math.round(10.2f);
        if (round == 0) {
            round = 1;
        }
        int max = Math.max(0, Math.min(255, Math.max(Math.min((int) (f * 5.0f * (1080.0f / this.f67733b.getHeight())), round), -round) + this.f67736e));
        @SuppressLint("RestrictedApi") Activity activity = ContextUtils.getActivity(this.f67732a);
        if (activity != null) {
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.screenBrightness = max / 255.0f;
            activity.getWindow().setAttributes(attributes);
        }
        ProgressBar progressBar = this.f67734c;
        if (progressBar != null && this.f67735d != null) {
            progressBar.setProgress(max);
            this.f67735d.setImageLevel((max * 100) / 255);
        }
    }





}

