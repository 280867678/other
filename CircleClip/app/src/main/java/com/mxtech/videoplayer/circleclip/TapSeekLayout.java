package com.mxtech.videoplayer.circleclip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


/* loaded from: classes.dex */
public class TapSeekLayout extends FrameLayout {

    /* renamed from: r */
    public View f11746r;

    /* renamed from: s */
    public TextView f11747s;

    /* renamed from: t */
    public View f11748t;

    /* renamed from: u */
    public TextView f11749u;

    /* renamed from: v */
    public int f11750v;

    /* renamed from: w */
    public int f11751w;

    /* renamed from: x */
    public long f11752x;

    /* renamed from: y */
    public CircleClipTapView f11753y;

    /* renamed from: z */
    public final RunnableC1790a f11754z;

    /* renamed from: com.mxtech.videoplayer.widget.TapSeekLayout$a */
    /* loaded from: classes.dex */
    public class RunnableC1790a implements Runnable {
        public RunnableC1790a() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            TapSeekLayout tapSeekLayout = TapSeekLayout.this;
            tapSeekLayout.f11746r.setVisibility(GONE);
            tapSeekLayout.f11748t.setVisibility(GONE);
            tapSeekLayout.f11753y.setVisibility(GONE);
            tapSeekLayout.f11750v = 0;
        }
    }

    public TapSeekLayout(Context context) {
        super(context);
        this.f11750v = 0;
        this.f11754z = new RunnableC1790a();
        m10013a(context);
    }

    /* renamed from: a */
    public final void m10013a(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tap_seek_layout, (ViewGroup) this, true);
        View findViewById = findViewById(R.id.tap_left_view);
        this.f11746r = findViewById;
        findViewById.setVisibility(GONE);
        this.f11747s = (TextView) findViewById(R.id.tap_left_seek_time);
        this.f11748t = findViewById(R.id.tap_right_view);
        this.f11749u = (TextView) findViewById(R.id.tap_right_seek_time);
        this.f11748t.setVisibility(GONE);
        CircleClipTapView circleClipTapView = (CircleClipTapView) findViewById(R.id.circle_clip_tap_view);
        this.f11753y = circleClipTapView;
        circleClipTapView.setVisibility(GONE);
    }

    /* renamed from: b */
    public final void m10012b() {
        this.f11750v = (0 / 1000) + this.f11750v;
        TextView textView = this.f11747s;
        textView.setText(this.f11750v + "s");
        TextView textView2 = this.f11749u;
        textView2.setText(this.f11750v + "s");
        RunnableC1790a runnableC1790a = this.f11754z;
        removeCallbacks(runnableC1790a);
        postDelayed(runnableC1790a, 1000L);
    }

    public TapSeekLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f11750v = 0;
        this.f11754z = new RunnableC1790a();
        m10013a(context);
    }

    public TapSeekLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f11750v = 0;
        this.f11754z = new RunnableC1790a();
        m10013a(context);
    }
}
