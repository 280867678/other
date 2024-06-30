package com.example.floatdragview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.floatdragview.R;

public class DownloadCenterSelectFileTitleView extends FrameLayout {

    /* renamed from: a */
    private TextView f18086a;

    /* renamed from: b */
    private TextView f18087b;

    /* renamed from: c */
    private TextView f18088c;

    /* renamed from: d */
    private TextView f18089d;

    /* renamed from: e */
    private InterfaceC4814b f18090e;

    /* renamed from: f */
    private InterfaceC4813a f18091f;

    /* renamed from: g */
    private boolean f18092g;

    /* renamed from: h */
    private Animation f18093h;

    /* renamed from: i */
    private Animation f18094i;

    /* renamed from: com.xunlei.downloadprovider.download.center.widget.DownloadCenterSelectFileTitleView$a */
    /* loaded from: classes2.dex */
    public interface InterfaceC4813a {
        /* renamed from: a */
        void mo9056a();
    }

    /* renamed from: com.xunlei.downloadprovider.download.center.widget.DownloadCenterSelectFileTitleView$b */
    /* loaded from: classes2.dex */
    public interface InterfaceC4814b {
        /* renamed from: a */
        void mo5581a(boolean z);
    }

    public DownloadCenterSelectFileTitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f18092g = true;
        this.f18093h = null;
        this.f18094i = null;
        m10750a(context);
    }

    public DownloadCenterSelectFileTitleView(Context context) {
        super(context);
        this.f18092g = true;
        this.f18093h = null;
        this.f18094i = null;
        m10750a(context);
    }

    public DownloadCenterSelectFileTitleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f18092g = true;
        this.f18093h = null;
        this.f18094i = null;
        m10750a(context);
    }

    /* renamed from: a */
    private void m10750a(Context context) {
        LayoutInflater.from(context).inflate(R.layout.download_center_select_file_view, this);
        this.f18086a = (TextView) findViewById(R.id.title);
        this.f18087b = (TextView) findViewById(R.id.cancel);
        this.f18088c = (TextView) findViewById(R.id.select_all);
        this.f18088c.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
//                DownloadCenterSelectFileTitleView.InterfaceC4814b interfaceC4814b;
//                TextView textView;
//                TextView textView2;
//                DownloadCenterSelectFileTitleView.InterfaceC4814b interfaceC4814b2;
//                interfaceC4814b = this.f18186a.f18090e;
                if (f18090e != null) {
//                    textView = this.f18186a.f18088c;
                    f18088c.setVisibility(8);
//                    textView2 = this.f18186a.f18089d;
                    f18089d.setVisibility(0);
//                    interfaceC4814b2 = this.f18186a.f18090e;
                    f18090e.mo5581a(true);
                }
            }
        });
        this.f18089d = (TextView) findViewById(R.id.un_select_all);
        this.f18089d.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
//                DownloadCenterSelectFileTitleView.InterfaceC4814b interfaceC4814b;
//                DownloadCenterSelectFileTitleView.InterfaceC4814b interfaceC4814b2;
//                TextView textView;
//                TextView textView2;
//                interfaceC4814b = this.f18187a.f18090e;
                if (f18090e != null) {
//                    interfaceC4814b2 = this.f18187a.f18090e;
                    f18090e.mo5581a(false);
//                    textView = this.f18187a.f18088c;
                    f18088c.setVisibility(0);
//                    textView2 = this.f18187a.f18089d;
                    f18089d.setVisibility(8);
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.f18093h = AnimationUtils.loadAnimation(context, R.anim.title_bar_top_in);
        this.f18094i = AnimationUtils.loadAnimation(context, R.anim.title_bar_top_out);
        this.f18093h.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                DownloadCenterSelectFileTitleView.InterfaceC4813a interfaceC4813a;
//                DownloadCenterSelectFileTitleView.InterfaceC4813a interfaceC4813a2;
                setAnimation(null);
//                interfaceC4813a = this.f18189a.f18091f;
                if (f18091f != null) {
//                    interfaceC4813a2 = this.f18189a.f18091f;
                    f18091f.mo9056a();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.f18094i.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(8);
                setAnimation(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setTitle(String str) {
        this.f18086a.setText(str);
    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public final void m10748a(boolean z) {
        if (z) {
            this.f18088c.setVisibility(0);
            this.f18089d.setVisibility(8);
            return;
        }
        this.f18088c.setVisibility(8);
        this.f18089d.setVisibility(0);
    }

    public void setCancelListener(View.OnClickListener onClickListener) {
        this.f18087b.setOnClickListener(onClickListener);
    }

    public void setShowListener(InterfaceC4813a interfaceC4813a) {
        this.f18091f = interfaceC4813a;
    }

    public void setSelectAllListener(InterfaceC4814b interfaceC4814b) {
        this.f18090e = interfaceC4814b;
    }

    /* renamed from: b */
    @SuppressLint("WrongConstant")
    public final void m10746b(boolean z) {
        this.f18086a.setText(getResources().getString(R.string.download_list_select_title));
        m10748a(true);
        setVisibility(0);
        if (z) {
            startAnimation(this.f18093h);
        }
    }

    /* renamed from: c */
    @SuppressLint("WrongConstant")
    public final void m10744c(boolean z) {
        if (z) {
            startAnimation(this.f18094i);
        } else {
            setVisibility(8);
        }
    }
}

