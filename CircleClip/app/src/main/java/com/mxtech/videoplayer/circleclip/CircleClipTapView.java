package com.mxtech.videoplayer.circleclip;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/* loaded from: classes.dex */
public class CircleClipTapView extends View {

    /* renamed from: A */
    public float f11651A;

    /* renamed from: B */
    public int f11652B;

    /* renamed from: C */
    public int f11653C;

    /* renamed from: D */
    public ValueAnimator f11654D;

    /* renamed from: E */
    public float f11655E;

    /* renamed from: r */
    public Context f11656r;

    /* renamed from: s */
    public Paint f11657s;

    /* renamed from: t */
    public Paint f11658t;

    /* renamed from: u */
    public int f11659u;

    /* renamed from: v */
    public int f11660v;

    /* renamed from: w */
    public Path f11661w;

    /* renamed from: x */
    public boolean f11662x;

    /* renamed from: y */
    public float f11663y;

    /* renamed from: z */
    public float f11664z;

    /* renamed from: com.mxtech.videoplayer.widget.CircleClipTapView$a */
    /* loaded from: classes.dex */
    public class C1780a implements ValueAnimator.AnimatorUpdateListener {
        public C1780a() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            CircleClipTapView circleClipTapView = CircleClipTapView.this;
            int i = circleClipTapView.f11652B;
            circleClipTapView.f11651A = ((circleClipTapView.f11653C - i) * floatValue) + i;
            circleClipTapView.invalidate();
        }
    }

    public CircleClipTapView(Context context) {
        super(context);
        m10038a(context);
    }

    private final ValueAnimator getCircleAnimator() {
        if (this.f11654D == null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new C1780a());
            ofFloat.addListener(new C1781b());
            this.f11654D = ofFloat;
            ofFloat.setDuration(650L);
        }
        return this.f11654D;
    }

    /* renamed from: a */
    public final void m10038a(Context context) {
        this.f11657s = new Paint();
        this.f11658t = new Paint();
        this.f11661w = new Path();
        this.f11662x = true;
        this.f11656r = context;
        this.f11658t.setStyle(Paint.Style.FILL);
        this.f11658t.setAntiAlias(true);
        this.f11658t.setColor(Color.parseColor("#1A000000"));
        this.f11657s.setStyle(Paint.Style.FILL);
        this.f11657s.setAntiAlias(true);
        this.f11657s.setColor(Color.parseColor("#33000000"));
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.f11659u = displayMetrics.widthPixels;
        this.f11660v = displayMetrics.heightPixels;
        float f = displayMetrics.density;
        this.f11652B = (int) (30.0f * f);
        this.f11653C = (int) (f * 400.0f);
        m10037b();
        this.f11654D = getCircleAnimator();
        this.f11655E = displayMetrics.density * 80.0f;
    }

    /* renamed from: b */
    public final void m10037b() {
        float f;
        int i;
        float f2 = this.f11659u * 0.33f;
        this.f11661w.reset();
        boolean z = this.f11662x;
        if (z) {
            f = 0.0f;
        } else {
            f = this.f11659u;
        }
        if (z) {
            i = 1;
        } else {
            i = -1;
        }
        this.f11661w.moveTo(f, 0.0f);
        float f3 = i;
        this.f11661w.lineTo(((f2 - this.f11655E) * f3) + f, 0.0f);
        Path path = this.f11661w;
        float f4 = this.f11655E;
        int i2 = this.f11660v;
        path.quadTo(((f2 + f4) * f3) + f, i2 / 2.0f, m8530a(f2, f4, f3, f), i2);
        this.f11661w.lineTo(f, this.f11660v);
        this.f11661w.close();
        invalidate();
    }

    /* renamed from: c */
    public final void m10036c(float f, float f2) {
        boolean z;
        this.f11663y = f;
        this.f11664z = f2;
        if (f <= this.f11656r.getResources().getDisplayMetrics().widthPixels / 2) {
            z = true;
        } else {
            z = false;
        }
        if (this.f11662x != z) {
            this.f11662x = z;
            m10037b();
        }
        ValueAnimator valueAnimator = this.f11654D;
        if (valueAnimator != null) {
            valueAnimator.end();
        }
        setVisibility(VISIBLE);
        getCircleAnimator().start();
    }

    @Override // android.view.View
    public final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            canvas.clipPath(this.f11661w);
        }
        if (canvas != null) {
            canvas.drawPath(this.f11661w, this.f11657s);
        }
        if (this.f11659u >= this.f11660v && canvas != null) {
            canvas.drawCircle(this.f11663y, this.f11664z, this.f11651A, this.f11658t);
        }
    }

    @Override // android.view.View
    public final void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.f11659u = i;
        this.f11660v = i2;
        m10037b();
    }

    public CircleClipTapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m10038a(context);
    }

    public CircleClipTapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m10038a(context);
    }

    /* renamed from: com.mxtech.videoplayer.widget.CircleClipTapView$b */
    /* loaded from: classes.dex */
    public class C1781b implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator) {
        }
    }





    public static float m8530a(float f, float f2, float f3, float f4) {
        return ((f - f2) * f3) + f4;
    }



}
