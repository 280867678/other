package com.example.floatdragview.widget;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;
import android.widget.Scroller;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floatdragview.R;
import com.example.floatdragview.util.TaskDetailUtil;


/* loaded from: classes2.dex */
public class FloatDragView extends ViewGroup {

    /* renamed from: A */
    private int f20209A;

    /* renamed from: B */
    private boolean f20210B;

    /* renamed from: C */
    private int f20211C;

    /* renamed from: a */
    private final int f20212a;

    /* renamed from: b */
    private final int f20213b;

    /* renamed from: c */
    private final float f20214c;

    /* renamed from: d */
    private final int f20215d;

    /* renamed from: e */
    private float f20216e;

    /* renamed from: f */
    private int f20217f;

    /* renamed from: g */
    private float f20218g;

    /* renamed from: h */
    private float f20219h;

    /* renamed from: i */
    private Scroller f20220i;

    /* renamed from: j */
    private boolean f20221j;

    /* renamed from: k */
    private int f20222k;

    /* renamed from: l */
    private int f20223l;

    /* renamed from: m */
    private int f20224m;

    /* renamed from: n */
    private int f20225n;

    /* renamed from: o */
    private boolean f20226o;

    /* renamed from: p */
    private VelocityTracker f20227p;

    /* renamed from: q */
    private int f20228q;

    /* renamed from: r */
    private boolean f20229r;

    /* renamed from: s */
    private boolean f20230s;

    /* renamed from: t */
    private int f20231t;

    /* renamed from: u */
    private int f20232u;

    /* renamed from: v */
    private int f20233v;

    /* renamed from: w */
    private InterfaceC5373a f20234w;

    /* renamed from: x */
    private int f20235x;

    /* renamed from: y */
    private int f20236y;

    /* renamed from: z */
    private boolean f20237z;

    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.widget.FloatDragView$a */
    /* loaded from: classes2.dex */
    public interface InterfaceC5373a {
        /* renamed from: a */
        void mo9068a(float f);

        /* renamed from: a */
        void mo9067a(int i);
    }

    public FloatDragView(Context context) {
        super(context);
        this.f20212a = -10;
        this.f20213b = 800;
        this.f20214c = 2.5f;
        this.f20215d = 2;
        this.f20216e = 8.0f;
        this.f20218g = 0.0f;
        this.f20219h = 0.0f;
        this.f20221j = false;
        this.f20226o = false;
        this.f20229r = true;
        this.f20230s = false;
        this.f20236y = -1;
        this.f20210B = false;
        this.f20211C = 0;
        m9074a(context, null);
    }

    public FloatDragView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f20212a = -10;
        this.f20213b = 800;
        this.f20214c = 2.5f;
        this.f20215d = 2;
        this.f20216e = 8.0f;
        this.f20218g = 0.0f;
        this.f20219h = 0.0f;
        this.f20221j = false;
        this.f20226o = false;
        this.f20229r = true;
        this.f20230s = false;
        this.f20236y = -1;
        this.f20210B = false;
        this.f20211C = 0;
        m9074a(context, attributeSet);
    }

    public FloatDragView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f20212a = -10;
        this.f20213b = 800;
        this.f20214c = 2.5f;
        this.f20215d = 2;
        this.f20216e = 8.0f;
        this.f20218g = 0.0f;
        this.f20219h = 0.0f;
        this.f20221j = false;
        this.f20226o = false;
        this.f20229r = true;
        this.f20230s = false;
        this.f20236y = -1;
        this.f20210B = false;
        this.f20211C = 0;
        m9074a(context, attributeSet);
    }

    @TargetApi(21)
    public FloatDragView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.f20212a = -10;
        this.f20213b = 800;
        this.f20214c = 2.5f;
        this.f20215d = 2;
        this.f20216e = 8.0f;
        this.f20218g = 0.0f;
        this.f20219h = 0.0f;
        this.f20221j = false;
        this.f20226o = false;
        this.f20229r = true;
        this.f20230s = false;
        this.f20236y = -1;
        this.f20210B = false;
        this.f20211C = 0;
        m9074a(context, attributeSet);
    }

    /* renamed from: a */
    @SuppressLint("ResourceType")
    private void m9074a(Context context, AttributeSet attributeSet) {
        this.f20216e = ViewConfiguration.get(context).getScaledTouchSlop();
        this.f20220i = new Scroller(context, new LinearInterpolator());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BLContainer);
        this.f20231t = obtainStyledAttributes.getDimensionPixelOffset(0, 0);
        this.f20235x = obtainStyledAttributes.getDimensionPixelOffset(1, 0);
        this.f20232u = obtainStyledAttributes.getInt(2, 11);
        obtainStyledAttributes.recycle();
    }

    public void setLockScroll(boolean z) {
        this.f20230s = z;
    }

    @IdRes
    public int getContentNestedScrollViewId() {
        return this.f20236y;
    }

    public void setContentNestedScrollViewId(@IdRes int i) {
        this.f20236y = i;
        invalidate();
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.f20220i.computeScrollOffset()) {
            scrollBy(0, this.f20220i.getCurrY() - this.f20217f);
            this.f20217f = getScrollY();
            postInvalidate();
        } else if (this.f20226o) {
            this.f20226o = false;
            if (this.f20220i.getCurrY() > 0) {
                if (this.f20234w != null) {
                    this.f20234w.mo9067a(12);
                }
            } else if (this.f20220i.getCurrY() < 0) {
                if (this.f20234w != null) {
                    this.f20234w.mo9067a(10);
                }
            } else if (this.f20234w != null) {
                this.f20234w.mo9067a(11);
            }
            this.f20217f = 0;
        }
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        StringBuilder sb = new StringBuilder("l: ");
        sb.append(i);
        sb.append(" t: ");
        sb.append(i2);
        sb.append(" oldl: ");
        sb.append(i3);
        sb.append(" oldt: ");
        sb.append(i4);
        Log.e("FloatDragView onScrollChanged:",sb.toString());
        if (this.f20234w != null) {
            int i5 = (this.f20222k - (this.f20231t == 0 ? this.f20222k - this.f20225n : this.f20231t)) - this.f20225n;
            if (i2 > 0) {
                this.f20234w.mo9068a((-i2) / this.f20231t);
            } else if (i2 < 0) {
                this.f20234w.mo9068a((-i2) / i5);
            } else {
                this.f20234w.mo9068a(0.0f);
            }
            TaskDetailUtil.m8464a().f20945a = true;
            TaskDetailUtil.m8464a().f20946b = System.currentTimeMillis();
        }
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        super.scrollBy(i, i2);
        float scrollY = getScrollY();
        float f = this.f20222k / 2;
        if (this.f20234w == null || i2 == 0) {
            return;
        }
        if (this.f20231t == 0) {
            if (scrollY > f - this.f20225n) {
                this.f20218g = (f - scrollY) / this.f20225n;
                if (this.f20218g >= 0.0f) {
                }
            } else if (scrollY < (-(f - this.f20225n))) {
                this.f20219h = (scrollY + f) / this.f20225n;
                if (this.f20219h >= 0.0f) {
                }
            } else if (this.f20218g != 0.0f && this.f20218g != 1.0f) {
                this.f20218g = 1.0f;
            } else if (this.f20219h == 0.0f || this.f20219h == 1.0f) {
            } else {
                this.f20219h = 1.0f;
            }
        } else if (scrollY > this.f20231t - this.f20225n) {
            this.f20218g = (this.f20231t - scrollY) / this.f20225n;
            if (this.f20218g >= 0.0f) {
            }
        } else if (scrollY < (-(((f - this.f20231t) + f) - this.f20225n))) {
            this.f20219h = (scrollY + ((f - this.f20231t) + f)) / this.f20225n;
            if (this.f20219h >= 0.0f) {
            }
        } else if (this.f20218g != 0.0f && this.f20218g != 1.0f) {
            this.f20218g = 1.0f;
        } else if (this.f20219h == 0.0f || this.f20219h == 1.0f) {
        } else {
            this.f20219h = 1.0f;
        }
    }

    public void setScrollEnable(boolean z) {
        this.f20229r = z;
    }

    /* renamed from: b */
    private void m9072b() {
        int scrollY = getScrollY();
        this.f20227p.computeCurrentVelocity(1);
        float yVelocity = this.f20227p.getYVelocity();
        if (Math.abs(yVelocity) > 0.1d) {
            if (this.f20232u == 10 && yVelocity < 0.0f) {
                m9069c(yVelocity);
                return;
            } else if (this.f20232u == 11 && yVelocity < 0.0f) {
                m9076a(yVelocity);
                return;
            } else if (this.f20232u == 11 && yVelocity > 0.0f) {
                m9071b(yVelocity);
                return;
            } else if (this.f20232u == 12 && yVelocity > 0.0f && scrollY > this.f20224m) {
                m9069c(yVelocity);
                return;
            }
        }
        if (scrollY > this.f20223l) {
            m9076a(yVelocity);
        } else if (scrollY < this.f20224m) {
            m9071b(yVelocity);
        } else if (scrollY > 0) {
            m9069c(yVelocity);
        } else {
            this.f20232u = 11;
            m9069c(yVelocity);
        }
    }

    /* renamed from: a */
    private void m9076a(float f) {
        int i;
        if (Math.abs(f) < 2.5f) {
            f = 2.5f;
        }
        int scrollY = getScrollY();
        if (this.f20231t == 0) {
            i = (this.f20222k / 2) - scrollY;
        } else {
            i = this.f20231t - scrollY;
        }
        int i2 = i;
        this.f20233v = this.f20232u;
        this.f20232u = 12;
        this.f20217f = scrollY;
        int abs = 2 * ((int) Math.abs(i2 / f));
        StringBuilder sb = new StringBuilder("playFixAnimationUp scrollY: ");
        sb.append(scrollY);
        sb.append(" scrollDest: ");
        sb.append(i2);
        Log.e("FloatDragView m9076a:",sb.toString());
        this.f20220i.startScroll(0, scrollY, 0, i2, abs);
        invalidate();
    }

    /* renamed from: b */
    private void m9071b(float f) {
        int i;
        if (Math.abs(f) < 2.5f) {
            f = 2.5f;
        }
        int scrollY = getScrollY();
        this.f20233v = this.f20232u;
        this.f20232u = 10;
        if (this.f20231t == 0) {
            i = ((-this.f20222k) / 2) - scrollY;
        } else {
            i = (((-this.f20222k) / 2) - ((this.f20222k / 2) - this.f20231t)) - scrollY;
        }
        this.f20217f = scrollY;
        int abs = (int) (0.2f * Math.abs(i - (this.f20225n / f)));
        StringBuilder sb = new StringBuilder("playFixAnimationDown scrollY: ");
        sb.append(scrollY);
        sb.append(" scrollDest - mTitleHeight: ");
        sb.append(i - this.f20225n);
        Log.e("FloatDragView m9071b:",sb.toString());
        this.f20220i.startScroll(0, scrollY, 0, i - this.f20225n, abs);
        invalidate();
    }

    /* renamed from: c */
    private void m9069c(float f) {
        if (Math.abs(f) < 2.5f) {
            f = 2.5f;
        }
        int scrollY = getScrollY();
        this.f20233v = this.f20232u;
        this.f20232u = 11;
        int i = -scrollY;
        this.f20217f = scrollY;
        int abs = 2 * ((int) Math.abs(i / f));
        StringBuilder sb = new StringBuilder("playFixAnimationIdle scrollY: ");
        sb.append(scrollY);
        sb.append(" scrollDest: ");
        sb.append(i);
        Log.e("FloatDragView m9069c:",sb.toString());
        this.f20220i.startScroll(0, scrollY, 0, i, abs);
        invalidate();
    }

    public void setIdleY(int i) {
        this.f20231t = i;
        this.f20222k = 0;
        invalidate();
    }

    public final void setVisibilityState$2563266(boolean z) {
        this.f20233v = this.f20232u;
        this.f20232u = 12;
        this.f20217f = getScrollY();
        if (z) {
            this.f20226o = true;
            this.f20220i.startScroll(0, getScrollY(), 0, this.f20231t + (-this.f20217f), 800);
        } else {
            scrollTo(0, this.f20231t);
        }
        invalidate();
        requestLayout();
    }

    public void setListener(InterfaceC5373a interfaceC5373a) {
        this.f20234w = interfaceC5373a;
    }

    /* renamed from: a */
    public final void m9077a() {
        scrollTo(0, 0);
        this.f20209A = 0;
        this.f20233v = 11;
        this.f20232u = 11;
    }

    @SuppressLint("WrongConstant")
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            super.onMeasure(i, i2);
            return;
        }
        int i3 = 0;
        int i4 = 0;
        while (true) {
            boolean z = true;
            int i5 = childCount - 1;
            if (i3 < i5) {
                View childAt = getChildAt(i3);
                if (i3 != 0 || !"view_tag_value_top".equals(childAt.getTag())) {
                    z = false;
                }
                this.f20210B = z;
                if (childAt.getVisibility() != 0) {
                    this.f20211C = 0;
                }
                if (childAt.getVisibility() != 8) {
                    measureChild(childAt, i, i2);
                    i4 += childAt.getMeasuredHeight();
                    if (i3 == 0 && "view_tag_value_top".equals(childAt.getTag())) {
                        this.f20211C = childAt.getMeasuredHeight();
                    }
                }
                i3++;
            } else {
                View childAt2 = getChildAt(i5);
                int size = (View.MeasureSpec.getSize(i2) - i4) + getTopHeight();
                measureChild(childAt2, i, View.MeasureSpec.makeMeasureSpec(size, 1073741824));
                StringBuilder sb = new StringBuilder("onMeasure:");
                sb.append(size);
                sb.append(",");
                sb.append(childAt2.getMeasuredHeight());
                Log.e("FloatDragView onMeasure:",sb.toString());
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                return;
            }
        }
    }

    private int getTopHeight() {
        if (this.f20210B) {
            return this.f20211C;
        }
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int childCount = getChildCount();
        if (childCount > 0) {
            int topHeight = (this.f20231t + i2) - getTopHeight();

            i5 = childCount - 1;
            for (int i6=0;i6<i5;i6++){
                View childAt = getChildAt(i6);
                if (childAt.getVisibility() != GONE) {
                    int measuredHeight = childAt.getMeasuredHeight() + topHeight;
                    childAt.layout(i, topHeight, i3, measuredHeight);
                    topHeight = measuredHeight;
                }
            }

//            int i6 = 0;
//            while (true) {
//                i5 = childCount - 1;
//                if (i6 >= i5) {
//                    break;
//                }
//                View childAt = getChildAt(i6);
//                if (childAt.getVisibility() != GONE) {
//                    int measuredHeight = childAt.getMeasuredHeight() + topHeight;
//                    childAt.layout(i, topHeight, i3, measuredHeight);
//                    topHeight = measuredHeight;
//                }
//                i6++;
//            }
            if (this.f20222k == 0 || this.f20223l == 0 || this.f20224m == 0) {
                if (this.f20231t == 0) {
                    this.f20223l = (i4 - i2) / 4;
                    this.f20224m = -this.f20223l;
                } else {
                    this.f20223l = this.f20231t / 2;
                    this.f20224m = ((-(i4 - i2)) / 2) + this.f20231t;
                }
                this.f20222k = getMeasuredHeight() - topHeight;
            }
            View childAt2 = getChildAt(i5);
            childAt2.layout(i, topHeight, i3, Math.max(i4, childAt2.getMeasuredHeight() + topHeight));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x003e, code lost:
        if (r4.getTop() < r3.getPaddingTop()) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0062, code lost:
        if (r0.getTop() < r3.getPaddingTop()) goto L20;
     */
    /* renamed from: c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */

    /**
     * errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
     * @return
     */
    private boolean m9070c() {
        View childAt;
        if (getChildCount() == 0 || (childAt = getChildAt(getChildCount() - 1)) == null) {
            return false;
        }
        View contentNestedScrollView = getContentNestedScrollView();
        if (contentNestedScrollView instanceof ListView) {
            ListView listView = (ListView) contentNestedScrollView;
            boolean z = listView.getFirstVisiblePosition() > 0;
            if (z || getChildCount() <= 0) {
                return z;
            }
        } else if (!(contentNestedScrollView instanceof RecyclerView)) {
            return childAt.getScrollY() < 0;
        } else {
            RecyclerView recyclerView = (RecyclerView) contentNestedScrollView;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (recyclerView.getChildCount() > 0) {
                View findViewByPosition = layoutManager.findViewByPosition(0);
                if (findViewByPosition != null) {
                    return true;
                }
                return true;
            }
            return false;
        }

//        return false;
        return false;
    }

    @Nullable
    private View getContentNestedScrollView() {
        if (this.f20236y > 0) {
            return findViewById(this.f20236y);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00e0  */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    /**
     * errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        RecyclerView recyclerView;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 && this.f20237z) {
            this.f20237z = false;
            onTouchEvent(motionEvent);
            View contentNestedScrollView = getContentNestedScrollView();
            if (contentNestedScrollView instanceof ListView) {
                ListView listView = (ListView) contentNestedScrollView;
                if (listView != null) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setAction(3);
                    listView.onTouchEvent(obtain);
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View childAt = listView.getChildAt(i);
                        if (childAt != null) {
                            childAt.setPressed(false);
                        }
                    }
                }
            } else if ((contentNestedScrollView instanceof RecyclerView) && (recyclerView = (RecyclerView) contentNestedScrollView) != null) {
                MotionEvent obtain2 = MotionEvent.obtain(motionEvent);
                obtain2.setAction(3);
                recyclerView.onTouchEvent(obtain2);
                for (int i2 = 0; i2 < recyclerView.getChildCount(); i2++) {
                    View childAt2 = recyclerView.getChildAt(i2);
                    if (childAt2 != null) {
                        childAt2.setPressed(false);
                    }
                }
            }
            return true;
        } else if (this.f20237z) {
            onTouchEvent(motionEvent);
            return true;
        } else {
            if (getScrollY() == this.f20231t && m9073a(motionEvent) && actionMasked == 2) {
                int historySize = motionEvent.getHistorySize();
                int y = (int) (motionEvent.getY() - this.f20209A);
                if (historySize > 0) {
                    if (((int) (motionEvent.getY() - ((int) motionEvent.getHistoricalY(0)))) > this.f20216e && !m9070c()) {
                        z = true;
                    }
                } else if (y > this.f20216e) {
                    z = !m9070c();
                }
                if (!z) {
                    if (onInterceptTouchEvent(motionEvent)) {
                        if (!this.f20220i.isFinished()) {
                            this.f20220i.forceFinished(true);
                            return true;
                        }
                        this.f20228q = motionEvent.getPointerId(0);
                        this.f20209A = (int) motionEvent.getY();
                        this.f20237z = true;
                        onTouchEvent(motionEvent);
                        return true;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            return super.dispatchTouchEvent(motionEvent);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!this.f20229r || this.f20230s || m9070c()) {
            return false;
        }
        if (this.f20221j) {
            return true;
        }
        if (actionMasked == 2 && this.f20221j) {
            return true;
        }
        if (this.f20232u == 12 && m9073a(motionEvent) && !m9073a(motionEvent)) {
            return false;
        }
        if (getScrollY() == this.f20231t && m9073a(motionEvent) && actionMasked == 2) {
            return motionEvent.getHistorySize() > 0 ? ((float) ((int) (motionEvent.getY() - ((float) ((int) motionEvent.getHistoricalY(0)))))) > this.f20216e && !m9070c() : ((float) ((int) (motionEvent.getY() - ((float) this.f20209A)))) > this.f20216e && !m9070c();
        }
        if (actionMasked != 5) {
            switch (actionMasked) {
                case 0:
                    if (m9073a(motionEvent)) {
                        if (!this.f20220i.isFinished()) {
                            this.f20220i.forceFinished(true);
                            return true;
                        }
                        this.f20228q = motionEvent.getPointerId(0);
                        this.f20209A = (int) motionEvent.getY();
                        break;
                    }
                    break;
                case 1:
                    this.f20228q = -10;
                    if (this.f20221j) {
                        return true;
                    }
                    break;
                case 2:
                    int abs = Math.abs((int) (motionEvent.getY() - this.f20209A));
                    if (m9073a(motionEvent) && abs > this.f20216e) {
                        this.f20221j = true;
                        return true;
                    }
                    this.f20221j = false;
                    break;
            }
        } else if (m9073a(motionEvent) && this.f20228q == -10) {
            this.f20228q = motionEvent.getPointerId(motionEvent.getActionIndex());
            this.f20209A = (int) motionEvent.getY();
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((m9073a(motionEvent) || this.f20221j) && this.f20229r && !this.f20230s) {
            switch (motionEvent.getAction() & 255) {
                case 0:
                    this.f20209A = (int) motionEvent.getY();
                    this.f20228q = motionEvent.getPointerId(0);
                    break;
                case 1:
                    this.f20226o = true;
                    this.f20221j = false;
                    m9072b();
                    this.f20227p.clear();
                    this.f20228q = -10;
                    break;
                case 2:
                    this.f20221j = true;
                    int historySize = motionEvent.getHistorySize();
                    if (historySize == 0) {
                        int y = (int) motionEvent.getY();
                        this.f20209A = y;
                        m9075a(this.f20209A - y);
                    } else {
                        for (int i = 0; i < historySize; i++) {
                            int historicalY = (int) motionEvent.getHistoricalY(i);
                            this.f20209A = historicalY;
                            m9075a(this.f20209A - historicalY);
                        }
                    }
                    this.f20227p.addMovement(motionEvent);
                    break;
                case 5:
                    if (this.f20228q == -10) {
                        if (!this.f20220i.isFinished()) {
                            this.f20220i.forceFinished(true);
                        }
                        this.f20228q = motionEvent.getPointerId(motionEvent.getActionIndex());
                        this.f20209A = (int) motionEvent.getY();
                        break;
                    }
                    break;
                case 6:
                    if (this.f20228q != -10 && motionEvent.getPointerId(motionEvent.getActionIndex()) == this.f20228q) {
                        this.f20226o = true;
                        this.f20221j = false;
                        m9072b();
                        this.f20227p.clear();
                        this.f20228q = -10;
                        break;
                    }
                    break;
            }
            return true;
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.f20227p = VelocityTracker.obtain();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f20227p.recycle();
    }

    /* renamed from: a */
    private boolean m9073a(MotionEvent motionEvent) {
        View firstVisibleChild = getFirstVisibleChild();
        if (firstVisibleChild == null) {
            return false;
        }
        int x = (int) motionEvent.getX();
        return x > 0 && x < firstVisibleChild.getRight() && ((int) motionEvent.getY()) > firstVisibleChild.getTop() - getScrollY();
    }

    private View getFirstVisibleChild() {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.getVisibility() == VISIBLE) {
                    return childAt;
                }
            }
        }
        return null;
    }

    /* renamed from: a */
    private void m9075a(int i) {
        if (this.f20231t == 0) {
            if (getScrollY() + i > this.f20222k / 2) {
                i = (this.f20222k / 2) - getScrollY();
            } else if (getScrollY() + i < (-this.f20222k) / 2) {
                i = ((-this.f20222k) / 2) - getScrollY();
            }
        } else if (getScrollY() + i > this.f20231t) {
            i = this.f20231t - getScrollY();
        } else if (getScrollY() + i < ((-this.f20222k) / 2) - ((this.f20222k / 2) - this.f20231t)) {
            i = (((-this.f20222k) / 2) - ((this.f20222k / 2) - this.f20231t)) - getScrollY();
        }
        scrollBy(0, i);
    }

    public int getVisibilityState() {
        return this.f20232u;
    }
}

