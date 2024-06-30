package com.example.floatdragview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.floatdragview.R;

public class DownloadCenterBottomView extends FrameLayout {

    /* renamed from: a */
    private TextView f18073a;

    /* renamed from: b */
    private TextView f18074b;

    /* renamed from: c */
    private View f18075c;

    /* renamed from: d */
    private ImageView f18076d;

    /* renamed from: e */
    private View f18077e;

    /* renamed from: f */
    private TextView f18078f;

    /* renamed from: g */
    private ImageView f18079g;

    /* renamed from: h */
    private View f18080h;

    /* renamed from: i */
    private TextView f18081i;

    /* renamed from: j */
    private ImageView f18082j;

    /* renamed from: k */
    private Animation f18083k;

    /* renamed from: l */
    private Animation f18084l;

    /* renamed from: m */
    private boolean f18085m;

    public DownloadCenterBottomView(Context context) {
        super(context);
        m10754a(context);
    }

    public DownloadCenterBottomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m10754a(context);
    }

    public DownloadCenterBottomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m10754a(context);
    }

    /* renamed from: a */
    private void m10754a(Context context) {
        LayoutInflater.from(context).inflate(R.layout.download_center_bottom_view, this);
        this.f18073a = (TextView) findViewById(R.id.download_center_bottom_view_title);
        this.f18074b = (TextView) findViewById(R.id.start_task);
        this.f18076d = (ImageView) findViewById(R.id.icon_start);
        this.f18078f = (TextView) findViewById(R.id.pause_tasks);
        this.f18079g = (ImageView) findViewById(R.id.icon_pause);
        this.f18081i = (TextView) findViewById(R.id.delete_tasks);
        this.f18082j = (ImageView) findViewById(R.id.icon_delete);
        this.f18075c = findViewById(R.id.start_contain);
        this.f18077e = findViewById(R.id.pause_contain);
        this.f18080h = findViewById(R.id.delete_contain);
        this.f18083k = AnimationUtils.loadAnimation(context, R.anim.delete_bottom_in);
        this.f18084l = AnimationUtils.loadAnimation(context, R.anim.delete_bottom_out);
        this.f18083k.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationEnd(Animation animation) {
                View findViewById = ((View) getParent()).findViewById(R.id.expand_view);
                if (findViewById != null) {
                    findViewById.setVisibility(0);
                }
                setAnimation(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.f18084l.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                View findViewById = ((View) getParent()).findViewById(R.id.expand_view);
                if (findViewById != null) {
                    findViewById.setVisibility(8);
                }
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(8);
                setAnimation(null);
                View findViewById = ((View) getParent()).findViewById(R.id.expand_view);
                if (findViewById != null) {
                    findViewById.setVisibility(8);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /* renamed from: a */
    public final void m10755a() {
        this.f18085m = true;
        setVisibility(0);
        startAnimation(this.f18083k);
        m10752b();
    }

    /* renamed from: a */
    public final void m10753a(boolean z) {
        this.f18085m = false;
        if (z) {
            startAnimation(this.f18084l);
        } else {
            setVisibility(8);
        }
    }

    public void setTitle(String str) {
        this.f18073a.setText(str);
    }

    public void setDeleteTasksListener(View.OnClickListener onClickListener) {
        this.f18080h.setOnClickListener(onClickListener);
    }

    public void setPauseTasksListener(View.OnClickListener onClickListener) {
        this.f18077e.setOnClickListener(onClickListener);
    }

    public void setStartTasksListener(View.OnClickListener onClickListener) {
        this.f18075c.setOnClickListener(onClickListener);
    }

    /* renamed from: b */
    public final void m10752b() {
        this.f18075c.setClickable(false);
        this.f18080h.setClickable(false);
        this.f18077e.setClickable(false);
        this.f18076d.setEnabled(false);
        this.f18079g.setEnabled(false);
        this.f18082j.setEnabled(false);
        this.f18074b.setTextColor(getResources().getColor(R.color.download_list_bottom_disable));
        this.f18078f.setTextColor(getResources().getColor(R.color.download_list_bottom_disable));
        this.f18081i.setTextColor(getResources().getColor(R.color.download_list_bottom_disable));
    }

    /* renamed from: c */
    public final void m10751c() {
        this.f18075c.setClickable(true);
        this.f18077e.setClickable(true);
        this.f18080h.setClickable(true);
        this.f18076d.setEnabled(true);
        this.f18079g.setEnabled(true);
        this.f18082j.setEnabled(true);
        this.f18074b.setTextColor(getResources().getColor(R.color.download_list_bottom_enable));
        this.f18078f.setTextColor(getResources().getColor(R.color.download_list_bottom_enable));
        this.f18081i.setTextColor(getResources().getColor(R.color.download_list_bottom_enable));
    }
}
