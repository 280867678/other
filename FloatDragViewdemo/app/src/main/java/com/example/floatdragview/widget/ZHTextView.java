package com.example.floatdragview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.floatdragview.R;

import java.util.ArrayList;

public class ZHTextView extends androidx.appcompat.widget.AppCompatTextView {

    /* renamed from: a */
    private C8331b f31333a;

    /* renamed from: b */
    private CharSequence f31334b;

    /* renamed from: c */
    private boolean f31335c;

    /* renamed from: d */
    private float f31336d;

    public ZHTextView(Context context) {
        super(context);
        this.f31335c = true;
        this.f31336d = 0.0f;
        m1593a((AttributeSet) null, 0);
    }

    public ZHTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f31335c = true;
        this.f31336d = 0.0f;
        m1593a(attributeSet, 0);
    }

    public ZHTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f31335c = true;
        this.f31336d = 0.0f;
        m1593a(attributeSet, 0);
    }

//    @TargetApi(21)
//    public ZHTextView(Context context, AttributeSet attributeSet, int i, int i2) {
//        super(context, attributeSet, i, i2);
//        this.f31335c = true;
//        this.f31336d = 0.0f;
//        m1593a(attributeSet, i2);
//    }

    public float getTextIndentPadding() {
        return this.f31336d;
    }

    public void setTextIndentPadding(float f) {
        this.f31336d = f;
        if (Build.VERSION.SDK_INT < 18 || !isInLayout()) {
            requestLayout();
        }
        invalidate();
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:43:0x00ee
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    @Override // android.widget.TextView, android.view.View
    protected void onDraw(android.graphics.Canvas r25) {
        /*
            Method dump skipped, instructions count: 471
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xunlei.downloadprovider.xlui.widget.ZHTextView.onDraw(android.graphics.Canvas):void");
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        float f;
        int i3;
        int max = 0;
        if (!this.f31335c) {
            super.onMeasure(i, i2);
            return;
        }
        CharSequence charSequence = this.f31334b;
        TextPaint paint = getPaint();
        String valueOf = charSequence == null ? "" : String.valueOf(charSequence);
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int textIndentPadding = !TextUtils.isEmpty(valueOf) ? (int) (getTextIndentPadding() + paint.measureText(valueOf) + getPaddingLeft() + getPaddingRight()) : 0;
        if (mode != 1073741824) {
            size = mode == Integer.MIN_VALUE ? Math.min(textIndentPadding, size) : textIndentPadding;
        }
        m1592a(charSequence, (size - getPaddingLeft()) - getPaddingRight());
        int size2 = this.f31333a.f31339a.size();
        TextPaint paint2 = getPaint();
        int mode2 = View.MeasureSpec.getMode(i2);
        int size3 = View.MeasureSpec.getSize(i2);
        float ascent = paint2.ascent();
        float descent = paint2.descent();
        if (Build.VERSION.SDK_INT >= 16) {
            i3 = getMaxLines();
            f = getLineSpacingExtra();
        } else {
            f = 0.0f;
            i3 = 1;
        }
        if (mode2 != 1073741824) {
            float f2 = (-ascent) + descent;
            int paddingTop = (int) (getPaddingTop() + f2 + getPaddingBottom());
            int max2 = (int) ((f2 * Math.max(0, Math.min(size2, i3))) + ((max - 1) * f) + getPaddingTop() + getPaddingBottom());
            if (size3 == 0 || mode2 == Integer.MIN_VALUE) {
                size3 = Math.max(max2, paddingTop);
            } else {
                size3 = Math.min(max2, size3);
            }
        }
        setMeasuredDimension(size, size3);
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        this.f31334b = getText();
        if (Build.VERSION.SDK_INT >= 18 && !isInLayout()) {
            requestLayout();
        }
        invalidate();
    }

    /* renamed from: a */
    private void m1593a(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.ZHTextView, i, 0);
        this.f31336d = obtainStyledAttributes.getDimension(0, this.f31336d);
        obtainStyledAttributes.recycle();
        this.f31334b = getText();
        this.f31333a = new C8331b((byte) 0);
        this.f31333a.f31340b = getText();
        this.f31333a.f31342d = this.f31336d;
        if (this.f31336d > 0.0f && Build.VERSION.SDK_INT >= 18 && !isInLayout()) {
            requestLayout();
        }
        invalidate();
    }

    /* renamed from: a */
    private void m1592a(CharSequence charSequence, int i) {
        if (i <= 0 || charSequence == null) {
            return;
        }
        C8331b c8331b = this.f31333a;
        c8331b.f31339a.clear();
        c8331b.f31340b = null;
        this.f31333a.f31340b = charSequence;
        this.f31333a.f31341c = i;
        this.f31333a.f31342d = getTextIndentPadding();
        TextPaint paint = getPaint();
        int length = charSequence.length();
        int i2 = (int) (i - this.f31333a.f31342d);
        int i3 = i2 <= 0 ? i : i2;
        int i4 = 0;
        int i5 = 0;
        while (i4 < length) {
            int breakText = paint.breakText(charSequence, i4, length, true, i5 == 0 ? i3 : i, null);
            if (breakText <= 0) {
                return;
            }
            C8330a c8330a = new C8330a((byte) 0);
            c8330a.f31337a = i4;
            i4 += breakText;
            c8330a.f31338b = i4;
            this.f31333a.f31339a.add(c8330a);
            i5++;
        }
    }

    public int getCurrentLineNum() {
        if (this.f31333a != null) {
            return this.f31333a.f31339a.size();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.xunlei.downloadprovider.xlui.widget.ZHTextView$a */
    /* loaded from: classes2.dex */
    public static class C8330a {

        /* renamed from: a */
        public int f31337a;

        /* renamed from: b */
        public int f31338b;

        private C8330a() {
        }

        /* synthetic */ C8330a(byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.xunlei.downloadprovider.xlui.widget.ZHTextView$b */
    /* loaded from: classes2.dex */
    public static class C8331b {

        /* renamed from: a */
        public ArrayList<C8330a> f31339a;

        /* renamed from: b */
        public CharSequence f31340b;

        /* renamed from: c */
        public int f31341c;

        /* renamed from: d */
        public float f31342d;

        private C8331b() {
            this.f31339a = new ArrayList<>();
            this.f31340b = null;
        }

        /* synthetic */ C8331b(byte b) {
            this();
        }
    }
}
