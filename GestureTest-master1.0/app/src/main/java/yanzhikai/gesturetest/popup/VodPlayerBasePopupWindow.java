package yanzhikai.gesturetest.popup;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DimenRes;
import android.view.View;
import android.widget.PopupWindow;

import yanzhikai.gesturetest.R;
import yanzhikai.gesturetest.util.MethodCompat;
import yanzhikai.gesturetest.util.StatusBarUtil;


/* renamed from: com.xunlei.downloadprovider.vod.player.a */
/* loaded from: classes6.dex */
public class VodPlayerBasePopupWindow extends PopupWindow {

    /* renamed from: a */
    private String f75806a;

    /* renamed from: b */
    private InterfaceC24839a f75807b;

    /* renamed from: c */
    private PopupWindow.OnDismissListener f75808c;

    /* renamed from: d */
    private Handler f75809d;

    /* renamed from: e */
    private int f75810e;

    /* renamed from: f */
    private boolean f75811f;



    /* renamed from: k */
    protected Context f75814k;

    /* compiled from: VodPlayerBasePopupWindow.java */
    /* renamed from: com.xunlei.downloadprovider.vod.player.a$a */
    /* loaded from: classes6.dex */
    public interface InterfaceC24839a {
        /* renamed from: a */
        void mo12040a();

        /* renamed from: b */
        void mo12039b();
    }

    public VodPlayerBasePopupWindow(Context context) {
        super(context);
        this.f75806a = VodPlayerBasePopupWindow.class.getSimpleName();
        this.f75809d = new Handler(Looper.getMainLooper());
        this.f75810e = 0;
        this.f75811f = true;

        this.f75814k = context;
        super.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.xunlei.downloadprovider.vod.player.a.1
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {

                if (VodPlayerBasePopupWindow.this.f75807b != null) {
                    VodPlayerBasePopupWindow.this.f75807b.mo12040a();
                }
                if (VodPlayerBasePopupWindow.this.f75808c != null) {
                    VodPlayerBasePopupWindow.this.f75808c.onDismiss();
                }
                VodPlayerBasePopupWindow.this.f75809d.postDelayed(new Runnable() { // from class: com.xunlei.downloadprovider.vod.player.a.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (VodPlayerBasePopupWindow.this.f75807b != null) {
                            VodPlayerBasePopupWindow.this.f75807b.mo12039b();
                        }
                    }
                }, VodPlayerBasePopupWindow.this.f75814k.getResources().getInteger(R.integer.vod_player_control_menu_right_in_out_duration));
            }
        });
    }



    @Override // android.widget.PopupWindow
    public void setContentView(View view) {
        super.setContentView(view);
        setBackgroundDrawable(new BitmapDrawable());
        int dimensionPixelSize = this.f75814k.getResources().getDimensionPixelSize(R.dimen.vod_player_popup_menu_width);
        setFocusable(true);
        setWidth(dimensionPixelSize);
        setHeight(-1);
        setAnimationStyle(R.style.vod_player_menu_anim);
    }

    @Override // android.widget.PopupWindow
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.f75808c = onDismissListener;
    }

    /* renamed from: a */
    public void m12905a(InterfaceC24839a interfaceC24839a) {
        this.f75807b = interfaceC24839a;
    }

    /* renamed from: b */
    public void m12900b(boolean z) {
        if (z) {
            int i = this.f75810e;
            if (i == 0) {
                i = this.f75814k.getResources().getDimensionPixelSize(R.dimen.vod_player_popup_menu_width);
            }
            setWidth(i);
            setHeight(-1);
            setAnimationStyle(R.style.vod_player_menu_anim);
            return;
        }
        int dimensionPixelSize = this.f75814k.getResources().getDimensionPixelSize(R.dimen.vod_player_popup_menu_default_height);
        setWidth(-1);
        setHeight(dimensionPixelSize);
        setAnimationStyle(R.style.vod_player_menu_anim1);
    }

    /* renamed from: b */
    public void m12902b(@DimenRes int i) {
        this.f75810e = this.f75814k.getResources().getDimensionPixelSize(i);
    }

    /* renamed from: c */
    public void m12899c(int i) {
        this.f75810e = i;
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        mo12891a(view, i, i2, i3, true);
    }

    /* renamed from: a */
    public void mo12891a(View view, int i, int i2, int i3, boolean z) {
        Context context = this.f75814k;
        if (context == null || view == null) {
            return;
        }
        if (!(context instanceof Activity) || MethodCompat.m43025b((Activity) context)) {
            if (!this.f75811f || (StatusBarUtil.m5905b(this.f75814k) && 80 == i)) {
                super.showAtLocation(view, i, i2, i3);
            } else if (Build.VERSION.SDK_INT == 24) {
                super.showAtLocation(view, i, i2, i3);
                StatusBarUtil.m5910a(getContentView());
            } else {
                setFocusable(false);
                update();
                super.showAtLocation(view, i, i2, i3);
                if (z) {
                    StatusBarUtil.m5910a(getContentView());
                }
                setFocusable(true);
                update();
            }
        }
    }

    /* renamed from: c */
    public void m12897c(boolean z) {
        this.f75811f = z;
    }
}

