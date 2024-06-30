package com.mxtech.videoplayer.circleclip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener,View.OnSystemUiVisibilityChangeListener ,Handler.Callback{
    public TapSeekLayout f9392K4;
    public TopLayout f9541v0;
    public UILayout f9546w0;
    public static final StringBuilder f9686w = new StringBuilder();
    public static final String f9329L4 = DateUtils.formatElapsedTime(f9686w, 0);
    public RelativeLayout f9401N0;
    public Lock f9547w1;
    public int f9552x1;
    public int f9557y1;
    public boolean f9463c3;
    public SurfaceView f9489j1;
    public static KeyguardManager f9675l;
    public int f9482h2;
    public final Handler f9521r0 = new Handler(this);
    public int f9358E0;
    public int[] f9530s4;
    public int[] f9535t4;
    public int f9492j4 = 0;
    public int f9496k4 = 0;
    public long f9500l4;
    public boolean f9562z1;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f9675l= (KeyguardManager) getSystemService("keyguard");
        int m4043n = 1;
        this.f9358E0 = m4043n;
        this.f9530s4 = new int[2];
        this.f9535t4 = new int[2];


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.systemWindowFittable);
        this.f9401N0 = relativeLayout;
        if (relativeLayout != null && Build.VERSION.SDK_INT < 16) {
            ViewGroup viewGroup = (ViewGroup) relativeLayout.getParent();
            int childCount = viewGroup.getChildCount();
            int i2 = 0;
            while (true) {
                if (i2 >= childCount) {
                    break;
                } else if (viewGroup.getChildAt(i2) == this.f9401N0) {
                    viewGroup.removeViewAt(i2);
                    int childCount2 = this.f9401N0.getChildCount();
                    int i3 = 0;
                    while (i3 < childCount2) {
                        View childAt = this.f9401N0.getChildAt(0);
                        this.f9401N0.removeViewAt(0);
                        viewGroup.addView(childAt, i2);
                        i3++;
                        i2++;
                    }
                    this.f9401N0 = null;
                } else {
                    i2++;
                }
            }
        }

        this.f9541v0 = (TopLayout) findViewById(R.id.top_layout);
        this.f9541v0.f9566r = this;
        this.f9541v0.setOnSystemUiVisibilityChangeListener(this);
        this.f9546w0 = (UILayout) findViewById(R.id.ui_layout);
        this.f9546w0.f9568s = this;
        this.f9546w0.setOnTouchListener(this);


    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        m10941O3(v, event, this.f9358E0);








//        TapSeekLayout m10963H32 = m10963H3();
////                C1586o c1586o = this.f9556y0;
////                m10963H32.getClass();
////                int m10362O = c1586o.m10362O();
////                int i5 = c1586o.f10844L;
////                c1586o.m10293z0(Math.min(m10362O + sa2.f24313D0, i5), 6000);
//        if (m10963H32.f11751w != 1 ) {
//            m10963H32.f11752x = 20;
//            m10963H32.f11750v = 0;
//        }
//        m10963H32.f11748t.setVisibility(0);
//        m10963H32.f11746r.setVisibility(8);
//        m10963H32.m10012b();
//        m10963H32.f11753y.m10036c(10, 20);
//        m10963H32.f11751w = 1;



        TapSeekLayout m10963H33 = m10963H3();
//                C1586o c1586o2 = this.f9556y0;
//                m10963H33.getClass();
//                int max = Math.max(c1586o2.m10362O() - sa2.f24313D0, 0);
//                c1586o2.m10293z0(max, 6000);
        if (m10963H33.f11751w != 0 ) {
            m10963H33.f11750v = 0;
        }
        m10963H33.f11746r.setVisibility(0);
        m10963H33.f11748t.setVisibility(0);
        m10963H33.m10012b();
        m10963H33.f11753y.m10036c(1053, 1683);
        m10963H33.f11751w = 10;



        return true;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }


    public static class TopLayout extends RelativeLayout implements Runnable {

        /* renamed from: r */
        public MainActivity f9566r;

        public TopLayout(Context context) {
            super(context);
        }

        @Override // android.view.View
        public final void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            if (this.f9566r != null) {
                post(this);
            }
        }


        @Override
        public final void run() {
            boolean z;
            boolean z2;
            boolean z3;
            MainActivity activityScreen = this.f9566r;
            String str = MainActivity.f9329L4;
            if (!activityScreen.isFinishing()) {
                int width = activityScreen.f9541v0.getWidth();
                int height = activityScreen.f9541v0.getHeight();
                if (activityScreen.f9401N0 != null && activityScreen.f9547w1 != null) {
                    if (width > activityScreen.f9552x1 || height > activityScreen.f9557y1) {
                        activityScreen.f9552x1 = width;
                        activityScreen.f9557y1 = height;
                    } else {
                        return;
                    }
                }
                boolean z4 = true;
                if (f9675l.inKeyguardRestrictedInputMode()) {
                    activityScreen.f9463c3 = true;
                    return;
                }
                SurfaceView surfaceView = activityScreen.f9489j1;
                if (surfaceView != null && ((RelativeLayout.LayoutParams) surfaceView.getLayoutParams()).getRules()[13] == 0) {
                    int width2 = activityScreen.f9489j1.getWidth();
                    int height2 = activityScreen.f9489j1.getHeight();
                    if (width2 > 4 && height2 > 4) {
                        if (width > height) {
                            z = true;
                        } else {
                            z = false;
                        }
                        if (width2 > height2) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if (z == z2) {
                            if (width == height) {
                                z3 = true;
                            } else {
                                z3 = false;
                            }
                            if (width2 != height2) {
                                z4 = false;
                            }
                        }
                        activityScreen.f9482h2 -= 4;
                        activityScreen.m10859i6();
                        activityScreen.f9482h2 += 4;
                        Handler handler = activityScreen.f9521r0;
                        handler.removeMessages(12);
                        handler.sendEmptyMessage(12);
                        return;
                    }
                    activityScreen.m10859i6();
                    return;
                }
                activityScreen.m10859i6();
            }
        }

        public TopLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public TopLayout(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }
    }

    /* loaded from: classes.dex */
    public static class UILayout extends RelativeLayout {

        /* renamed from: r */
//        public PlaybackController f9567r;

        /* renamed from: s */
        public MainActivity f9568s;

        public UILayout(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        public final boolean dispatchTouchEvent(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1 || action == 3) {
//                    this.f9567r.m10024h();
                }
            } else {
//                this.f9567r.m10028d();
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public final void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            MainActivity activityScreen = this.f9568s;
            if (activityScreen != null) {
                String str = MainActivity.f9329L4;
                activityScreen.getClass();
            }
        }

//        public void setController(PlaybackController playbackController) {
//            this.f9567r = playbackController;
//        }

        public UILayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public UILayout(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }
    }





    public final TapSeekLayout m10963H3() {
        if (this.f9392K4 == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.tap_seek_view_stub);
            if (viewStub != null && viewStub.getParent() != null) {
                this.f9392K4 = (TapSeekLayout) viewStub.inflate();
            } else {
                this.f9392K4 = (TapSeekLayout) findViewById(R.id.double_tap_seek_container);
            }
        }
        return this.f9392K4;
    }







    public final void m10859i6() {
        this.f9463c3 = false;
//        m10807v6();
//        Zoom zoom = this.f9474f2;
//        IScreen iScreen = zoom.f4246C;
//        C1586o mo10168G = iScreen.mo10168G();
//        if (mo10168G.m10345Y()) {
//            double d = mo10168G.f10852P;
//            double d2 = mo10168G.f10854Q;
//            double mo10144g = iScreen.mo10144g();
//            double mo10146e = iScreen.mo10146e();
//            if (d > 0.0d && d2 > 0.0d && mo10144g > 0.0d && mo10146e > 0.0d) {
//                double d3 = (mo10144g * d2) / d;
//                if (d3 <= mo10146e) {
//                    zoom.f4255u = mo10144g;
//                    zoom.f4256v = d3;
//                } else {
//                    zoom.f4255u = (mo10146e * d) / d2;
//                    zoom.f4256v = mo10146e;
//                }
//                if (d3 >= mo10146e) {
//                    zoom.f4257w = mo10144g;
//                    zoom.f4258x = d3;
//                } else {
//                    zoom.f4257w = (mo10146e * d) / d2;
//                    zoom.f4258x = mo10146e;
//                }
//                double d4 = d * 0.25d;
//                zoom.f4259y = d4;
//                zoom.f4260z = 0.25d * d2;
//                double d5 = zoom.f4255u;
//                if (d4 > d5) {
//                    zoom.f4259y = d5;
//                    zoom.f4260z = zoom.f4256v;
//                }
//                double max = ((Math.max(mo10146e, d2) * 2.0d) * d) / d2;
//                zoom.f4244A = max;
//                double d6 = zoom.f4257w;
//                if (max < d6) {
//                    zoom.f4244A = d6;
//                }
//            }
//        }
//        zoom.m12917b();
//        if (this.f9505n1 != null && !this.f9368G0) {
//            m10826r4();
//            this.f9505n1.requestLayout();
//        }
    }









    public final void m10941O3(View view, MotionEvent motionEvent, int i) {

        Point point;
        boolean z;
        boolean z2;
        boolean z3;
        float f;
        int i2;
        SurfaceView surfaceView;
        boolean z4;
        boolean z5;
//        ISubtitle firstVisibleSubtitle;
        boolean z6;
        boolean z7;
//        ScreenVerticalBar screenVerticalBar;
        float f2;
        int i3;
        boolean z8;
        boolean z9;
        double d;
        double d2;
        int i4;
        boolean z10;
        boolean z11;
        float abs;
        float m12912g;
        float f3;
        double d3;
        double m12912g2;
        double m12913f;
        int i5;
        int action = motionEvent.getAction();
        int[] iArr = this.f9530s4;
        view.getLocationOnScreen(iArr);
        Handler handler = this.f9521r0;
        if (action != 0) {
//            if (action != 1) {
//                if (action != 2) {
//                    if (action != 3) {
//                        if (action != 5) {
//                            if (action != 6) {
//                                if (action != 261) {
//                                    if (action != 262) {
//                                        return;
//                                    }
//                                }
//                            }
//                            if (motionEvent.getPointerCount() == 2 && (this.f9492j4 & 16) != 0) {
//                                if (((65280 & action) >> 8) == 1) {
//                                    i5 = 0;
//                                } else {
//                                    i5 = 1;
//                                }
//                                this.f9476f4 = ((int) motionEvent.getX(i5)) + iArr[0];
//                                this.f9480g4 = ((int) motionEvent.getY(i5)) + iArr[1];
//                                this.f9520q4 = this.f9482h2;
//                                this.f9525r4 = this.f9486i2;
//                                return;
//                            }
//                            return;
//                        }
//                        if (motionEvent.getPointerCount() == 2) {
//                            float x = motionEvent.getX(0) + iArr[0];
//                            float x2 = motionEvent.getX(1) + iArr[0];
//                            float y = motionEvent.getY(0) + iArr[1];
//                            float y2 = motionEvent.getY(1) + iArr[1];
//                            int i6 = this.f9492j4;
//                            if ((i6 & 16) != 0) {
//                                SurfaceView surfaceView2 = this.f9489j1;
//                                if (surfaceView2 != null) {
//                                    C1586o c1586o = this.f9556y0;
//                                    int i7 = c1586o.f10852P;
//                                    int i8 = c1586o.f10854Q;
//                                    if (i7 > 0 && i8 > 0) {
//                                        this.f9476f4 = (int) x;
//                                        this.f9480g4 = (int) y;
//                                        this.f9484h4 = (int) x2;
//                                        this.f9488i4 = (int) y2;
//                                        this.f9520q4 = this.f9482h2;
//                                        this.f9525r4 = this.f9486i2;
//                                        this.f9512o4 = surfaceView2.getWidth();
//                                        this.f9516p4 = this.f9489j1.getHeight();
//                                        return;
//                                    }
//                                    return;
//                                }
//                                return;
//                            } else if (i6 != 36 && i6 != 37) {
//                                if (i6 == 3) {
//                                    this.f9476f4 = (int) x;
//                                    this.f9480g4 = (int) y;
//                                    this.f9484h4 = (int) x2;
//                                    this.f9488i4 = (int) y2;
//                                    this.f9508n4 = this.f9556y0.m10352T();
//                                    return;
//                                } else if (i6 == 0) {
//                                    this.f9484h4 = (int) x2;
//                                    this.f9488i4 = (int) y2;
//                                    return;
//                                } else {
//                                    return;
//                                }
//                            } else {
//                                this.f9476f4 = (int) x;
//                                this.f9480g4 = (int) y;
//                                this.f9484h4 = (int) x2;
//                                this.f9488i4 = (int) y2;
//                                this.f9512o4 = (int) Math.abs(x - x2);
//                                int abs2 = (int) Math.abs(y - y2);
//                                this.f9516p4 = abs2;
//                                if (this.f9512o4 > 0 && abs2 > 0) {
//                                    if (this.f9492j4 == 37) {
//                                        this.f9555x4 = sa2.f24427w;
//                                        return;
//                                    } else {
//                                        this.f9550w4 = this.f9522r1.getTextSize() / DeviceUtils.f29173c;
//                                        return;
//                                    }
//                                }
//                                return;
//                            }
//                        }
//                        return;
//                    }
//                } else {
//                    int i9 = this.f9492j4;
//                    if (i9 != 0) {
//                        if (i9 != 1) {
//                            if (i9 != 2) {
//                                if (i9 != 3) {
//                                    if (i9 != 34) {
//                                        if (i9 != 36 && i9 != 37) {
//                                            switch (i9) {
//                                                case 17:
//                                                case 18:
//                                                case 19:
//                                                    m10914V2();
//                                                    if (this.f9556y0.m10344Z() && this.f9489j1 != null && (i3 = this.f9556y0.f10852P) > 0) {
//                                                        Zoom zoom = this.f9474f2;
//                                                        double d4 = zoom.f4253s;
//                                                        double d5 = zoom.f4254t;
//                                                        int i10 = this.f9492j4;
//                                                        if (i10 == 17) {
//                                                            z8 = true;
//                                                        } else {
//                                                            z8 = false;
//                                                        }
//                                                        if (i10 == 19) {
//                                                            z9 = true;
//                                                        } else {
//                                                            z9 = false;
//                                                        }
//                                                        if ((z9 | z8) && motionEvent.getPointerCount() >= 2) {
//                                                            float abs3 = Math.abs(motionEvent.getX(0) - motionEvent.getX(1));
//                                                            float abs4 = Math.abs(motionEvent.getY(0) - motionEvent.getY(1));
//                                                            if (abs3 > abs4) {
//                                                                m12912g = (this.f9512o4 + abs3) - Math.abs(this.f9476f4 - this.f9484h4);
//                                                                Zoom zoom2 = this.f9474f2;
//                                                                abs = (float) (m12912g / (zoom2.m12912g() / zoom2.m12913f()));
//                                                            } else {
//                                                                abs = (this.f9516p4 + abs4) - Math.abs(this.f9480g4 - this.f9488i4);
//                                                                Zoom zoom3 = this.f9474f2;
//                                                                m12912g = (float) (abs * (zoom3.m12912g() / zoom3.m12913f()));
//                                                            }
//                                                            double d6 = m12912g;
//                                                            Zoom zoom4 = this.f9474f2;
//                                                            double d7 = zoom4.f4259y;
//                                                            if (d6 < d7) {
//                                                                f3 = (float) d7;
//                                                                d3 = f3;
//                                                                m12912g2 = zoom4.m12912g();
//                                                                m12913f = zoom4.m12913f();
//                                                            } else {
//                                                                double d8 = zoom4.f4244A;
//                                                                if (d6 > d8) {
//                                                                    f3 = (float) d8;
//                                                                    d3 = f3;
//                                                                    m12912g2 = zoom4.m12912g();
//                                                                    m12913f = zoom4.m12913f();
//                                                                }
//                                                                d = d4;
//                                                                if (((int) d) == ((int) m12912g) || ((int) d5) != ((int) abs)) {
//                                                                    d2 = m12912g;
//                                                                    d5 = abs;
//                                                                    StringBuilder sb = C1368L.f9686w;
//                                                                    sb.setLength(0);
//                                                                    sb.append((((int) d2) * 100) / i3);
//                                                                    sb.append('%');
//                                                                    this.f9474f2.f4246C.mo10121y1(sb.toString());
//                                                                    i4 = this.f9492j4;
//                                                                    if (i4 == 18) {
//                                                                        z10 = true;
//                                                                    } else {
//                                                                        z10 = false;
//                                                                    }
//                                                                    if (i4 == 19) {
//                                                                        z11 = true;
//                                                                    } else {
//                                                                        z11 = false;
//                                                                    }
//                                                                    if (z11 | z10) {
//                                                                        this.f9482h2 = this.f9520q4 + ((int) ((motionEvent.getX(0) + iArr[0]) - this.f9476f4));
//                                                                        this.f9486i2 = this.f9525r4 + ((int) ((motionEvent.getY(0) + iArr[1]) - this.f9480g4));
//                                                                    }
//                                                                    this.f9474f2.m12910i(d2, d5);
//                                                                    return;
//                                                                }
//                                                            }
//                                                            double d9 = m12912g2 / m12913f;
//                                                            m12912g = f3;
//                                                            abs = (float) (d3 / d9);
//                                                            d = d4;
//                                                            if (((int) d) == ((int) m12912g)) {
//                                                            }
//                                                            d2 = m12912g;
//                                                            d5 = abs;
//                                                            StringBuilder sb2 = C1368L.f9686w;
//                                                            sb2.setLength(0);
//                                                            sb2.append((((int) d2) * 100) / i3);
//                                                            sb2.append('%');
//                                                            this.f9474f2.f4246C.mo10121y1(sb2.toString());
//                                                            i4 = this.f9492j4;
//                                                            if (i4 == 18) {
//                                                            }
//                                                            if (i4 == 19) {
//                                                            }
//                                                            if (z11 | z10) {
//                                                            }
//                                                            this.f9474f2.m12910i(d2, d5);
//                                                            return;
//                                                        }
//                                                        d = d4;
//                                                        d2 = d;
//                                                        i4 = this.f9492j4;
//                                                        if (i4 == 18) {
//                                                        }
//                                                        if (i4 == 19) {
//                                                        }
//                                                        if (z11 | z10) {
//                                                        }
//                                                        this.f9474f2.m12910i(d2, d5);
//                                                        return;
//                                                    }
//                                                    return;
//                                                default:
//                                                    return;
//                                            }
//                                        }
//                                        m10914V2();
//                                        if (motionEvent.getPointerCount() >= 2) {
//                                            float abs5 = Math.abs(motionEvent.getX(0) - motionEvent.getX(1));
//                                            float abs6 = Math.abs(motionEvent.getY(0) - motionEvent.getY(1));
//                                            if (abs5 > abs6) {
//                                                f2 = abs5 / this.f9512o4;
//                                            } else {
//                                                f2 = abs6 / this.f9516p4;
//                                            }
//                                            StringBuilder sb3 = C1368L.f9686w;
//                                            sb3.setLength(0);
//                                            if (this.f9492j4 == 36) {
//                                                float f4 = this.f9550w4 * f2;
//                                                if (f4 < 16.0f) {
//                                                    f4 = 16.0f;
//                                                } else if (f4 > 60.0f) {
//                                                    f4 = 60.0f;
//                                                }
//                                                this.f9522r1.setTextSize(f4);
//                                                sb3.append(' ');
//                                                sb3.append(Math.round(f4));
//                                            } else {
//                                                float m4061b = sa2.m4061b(this.f9555x4 * f2);
//                                                m10837o5(m4061b);
//                                                sb3.append(' ');
//                                                sb3.append(Math.round(m4061b * 100.0f));
//                                                sb3.append('%');
//                                            }
//                                            if (this.f9560y4 == null) {
//                                                this.f9560y4 = getResources().getDrawable(R.drawable.supreme_text_size);
//                                            }
//                                            m10833p5(sb3.toString(), this.f9560y4, false, true);
//                                            return;
//                                        }
//                                        return;
//                                    }
//                                    m10914V2();
//                                    float y3 = (((this.f9545v4 + this.f9480g4) - motionEvent.getY()) - iArr[1]) / DeviceUtils.f29172b;
//                                    if (this.f27128T == 1) {
//                                        y3 = (DeviceUtils.f29175e * y3) / DeviceUtils.f29176f;
//                                    }
//                                    if (y3 < 0.0f) {
//                                        y3 = 0.0f;
//                                    } else {
//                                        float f5 = sa2.f24436z;
//                                        if (y3 > f5) {
//                                            y3 = f5;
//                                        }
//                                    }
//                                    sa2.f24371d0 = Math.round(y3);
//                                    this.f9522r1.setSubtitlePadding(y3 * DeviceUtils.f29172b);
//                                    if (this.f9565z4 == null) {
//                                        this.f9565z4 = getResources().getDrawable(R.drawable.supreme_updown);
//                                    }
//                                    StringBuilder sb4 = C1368L.f9686w;
//                                    sb4.setLength(0);
//                                    sb4.append(' ');
//                                    sb4.append(sa2.f24371d0);
//                                    m10833p5(sb4.toString(), this.f9565z4, false, true);
//                                    return;
//                                }
//                                m10914V2();
//                                if (motionEvent.getPointerCount() >= 2) {
//                                    double m1363c = PlaybackSpeedBar.m1363c((((int) ((((this.f9480g4 - motionEvent.getY()) - iArr[1]) / DeviceUtils.f29172b) * 0.004f * 30.0f)) * 0.05d) + this.f9508n4);
//                                    if (this.f9556y0.m10352T() != m1363c) {
//                                        m10979B5(m1363c);
//                                        JointPlayer jointPlayer = this.f9556y0.f10860W;
//                                        if (jointPlayer != null && (jointPlayer.mo11375B() & 8) != 0) {
//                                            m10851k6(m1363c);
//                                            return;
//                                        }
//                                        return;
//                                    }
//                                    return;
//                                }
//                                return;
//                            }
//                            m10914V2();
//                            this.f9504m4.setValue((int) (this.f9508n4 + ((((this.f9480g4 - motionEvent.getY()) - iArr[1]) / DeviceUtils.f29172b) * screenVerticalBar.getMax() * 0.004f)));
//                            return;
//                        }
//                        m10914V2();
//                        if (this.f9556y0.m10319m()) {
//                            int x3 = (int) (((((motionEvent.getX() + iArr[0]) - this.f9476f4) / (DeviceUtils.f29171a * 39.3701d)) * this.f9363F0) + this.f9559y3);
//                            int i11 = this.f9556y0.f10844L;
//                            if (x3 < 0) {
//                                x3 = 0;
//                            } else if (x3 >= i11) {
//                                x3 = i11 - 1;
//                            }
//                            m10829q5(x3, false);
//                            if (this.f9378I0) {
//                                m10852k5(x3, x3 - this.f9559y3, null);
//                            }
//                            if (this.f9556y0.m10344Z() && mo10169F()) {
//                                m10950L4(x3);
//                                return;
//                            }
//                            return;
//                        }
//                        return;
//                    }
//                    int pointerCount = motionEvent.getPointerCount();
//                    int[] iArr2 = this.f9535t4;
//                    if (pointerCount == 1) {
//                        int x4 = ((int) motionEvent.getX()) + iArr[0];
//                        int y4 = ((int) motionEvent.getY()) + iArr[1];
//                        int m10798y3 = m10798y3(this.f9476f4, this.f9480g4, x4, y4, DeviceUtils.f29174d);
//                        if (m10798y3 != 1 && m10798y3 != 2) {
//                            if (m10798y3 == 3 || m10798y3 == 4) {
//                                this.f9476f4 = x4;
//                                if (this.f9556y0.m10319m() && this.f9556y0.m10344Z()) {
//                                    if ((i & SkinViewInflater.FLAG_SWITCH_THUMB) != 0 && this.f9522r1.getVisibility() != 8) {
//                                        this.f9522r1.getLocationOnScreen(iArr2);
//                                        if (y4 >= iArr2[1]) {
//                                            this.f9492j4 = 33;
//                                            this.f9522r1.f11505s++;
//                                            return;
//                                        }
//                                    }
//                                    if ((i & 8) != 0) {
//                                        this.f9556y0.m10316n0(7);
//                                        this.f9492j4 = 1;
//                                        mo10136n0();
//                                        this.f9405O0.m10028d();
//                                        if (!m10893a4()) {
//                                            this.f9405O0.m10026f(1, 0, false);
//                                            return;
//                                        }
//                                        return;
//                                    }
//                                    return;
//                                }
//                                return;
//                            }
//                            return;
//                        }
//                        this.f9480g4 = y4;
//                        if ((i & SkinViewInflater.FLAG_ANDROID_FOREGROUND) != 0 && this.f9540u4) {
//                            this.f9492j4 = 34;
//                            this.f9545v4 = this.f9522r1.getPaddingBottom();
//                            return;
//                        }
//                        if (sa2.m4073Q() && (i & 32) != 0) {
//                            z6 = true;
//                        } else {
//                            z6 = false;
//                        }
//                        if ((i & 16) != 0) {
//                            z7 = true;
//                        } else {
//                            z7 = false;
//                        }
//                        if (z6 && z7) {
//                            if (this.f9476f4 < this.f9546w0.getWidth() / 2) {
//                                this.f9504m4 = m10806w3();
//                            } else {
//                                this.f9504m4 = m10978C3();
//                            }
//                        } else if (z6) {
//                            this.f9504m4 = m10806w3();
//                        } else if (z7) {
//                            this.f9504m4 = m10978C3();
//                        } else {
//                            return;
//                        }
//                        ScreenVerticalBar screenVerticalBar2 = this.f9504m4;
//                        if (screenVerticalBar2 != null) {
//                            this.f9492j4 = 2;
//                            screenVerticalBar2.m10020a();
//                            this.f9504m4.m10018c();
//                            m10929R3();
//                            this.f9508n4 = this.f9504m4.getCurrent();
//                            if (z7 && m10861i4()) {
//                                sa2.f24405o1 = -1;
//                                m10871f6(this.f9449Z0, false, false);
//                                return;
//                            }
//                            return;
//                        }
//                        return;
//                    } else if (pointerCount >= 2) {
//                        float x5 = motionEvent.getX(0) + iArr[0];
//                        float x6 = motionEvent.getX(1) + iArr[0];
//                        float y5 = motionEvent.getY(0) + iArr[1];
//                        float y6 = motionEvent.getY(1) + iArr[1];
//                        float f6 = x5 - this.f9476f4;
//                        float f7 = y5 - this.f9480g4;
//                        float f8 = x6 - this.f9484h4;
//                        float f9 = y6 - this.f9488i4;
//                        if (Math.sqrt((f9 * f9) + (f8 * f8)) + Math.sqrt((f7 * f7) + (f6 * f6)) >= DeviceUtils.f29174d) {
//                            int m10798y32 = m10798y3(this.f9476f4, this.f9480g4, x5, y5, 0.0d);
//                            int m10798y33 = m10798y3(this.f9484h4, this.f9488i4, x6, y6, 0.0d);
//                            if ((i & SkinViewInflater.FLAG_DRAWABLE_TINT) != 0 && m10869g4(m10798y32, m10798y33, 1, 2)) {
//                                this.f9492j4 = 3;
//                                this.f9480g4 = (int) y5;
//                                double m10352T = this.f9556y0.m10352T();
//                                this.f9508n4 = m10352T;
//                                m10979B5(m10352T);
//                                return;
//                            }
//                            if ((i & SkinViewInflater.FLAG_SWITCH_TRACK) != 0 && this.f9522r1.getVisibility() != 8) {
//                                if (m10798y32 != m10798y33 && ((m10798y32 == 0 || m10798y32 == 4 || m10798y32 == 3) && (m10798y33 == 0 || m10798y33 == 4 || m10798y33 == 3))) {
//                                    z5 = true;
//                                } else {
//                                    z5 = false;
//                                }
//                                if (z5 && (firstVisibleSubtitle = this.f9522r1.getFirstVisibleSubtitle()) != null) {
//                                    this.f9522r1.getLocationOnScreen(iArr2);
//                                    float f10 = iArr2[1];
//                                    if (y5 >= f10 && y6 >= f10) {
//                                        this.f9476f4 = (int) x5;
//                                        this.f9480g4 = (int) y5;
//                                        this.f9484h4 = (int) x6;
//                                        this.f9488i4 = (int) y6;
//                                        this.f9512o4 = (int) Math.abs(x5 - x6);
//                                        int abs7 = (int) Math.abs(y5 - y6);
//                                        this.f9516p4 = abs7;
//                                        if (this.f9512o4 > 0 && abs7 > 0) {
//                                            if ((firstVisibleSubtitle.mo8440k() & 5242880) != 0) {
//                                                this.f9555x4 = sa2.f24427w;
//                                                this.f9492j4 = 37;
//                                            } else {
//                                                this.f9550w4 = this.f9522r1.getTextSize() / DeviceUtils.f29173c;
//                                                this.f9492j4 = 36;
//                                            }
//                                            this.f9522r1.f11505s++;
//                                            return;
//                                        }
//                                        return;
//                                    }
//                                }
//                            }
//                            if (this.f9556y0.m10344Z() && (i2 = i & 7) != 0 && (surfaceView = this.f9489j1) != null) {
//                                C1586o c1586o2 = this.f9556y0;
//                                int i12 = c1586o2.f10852P;
//                                int i13 = c1586o2.f10854Q;
//                                if (i12 > 0 && i13 > 0) {
//                                    this.f9476f4 = (int) x5;
//                                    this.f9480g4 = (int) y5;
//                                    this.f9484h4 = (int) x6;
//                                    this.f9488i4 = (int) y6;
//                                    this.f9520q4 = this.f9482h2;
//                                    this.f9525r4 = this.f9486i2;
//                                    this.f9512o4 = surfaceView.getWidth();
//                                    this.f9516p4 = this.f9489j1.getHeight();
//                                    if ((i & 4) != 0) {
//                                        this.f9492j4 = 19;
//                                        return;
//                                    } else if (i2 == 3) {
//                                        if (Math.abs(v64.m2533l(Math.atan2(f7, f6) - Math.atan2(f9, f8))) < 0.6283185307179586d) {
//                                            z4 = true;
//                                        } else {
//                                            z4 = false;
//                                        }
//                                        if (z4) {
//                                            this.f9492j4 = 18;
//                                            return;
//                                        } else if (!m10869g4(m10798y32, m10798y33, 1, 2) && !m10869g4(m10798y32, m10798y33, 4, 3)) {
//                                            this.f9492j4 = 17;
//                                            return;
//                                        } else {
//                                            return;
//                                        }
//                                    } else if ((i & 1) != 0) {
//                                        if (!m10869g4(m10798y32, m10798y33, 1, 2) && !m10869g4(m10798y32, m10798y33, 4, 3)) {
//                                            this.f9492j4 = 17;
//                                            return;
//                                        }
//                                        return;
//                                    } else {
//                                        this.f9492j4 = 18;
//                                        return;
//                                    }
//                                }
//                                return;
//                            }
//                            return;
//                        }
//                        return;
//                    } else {
//                        return;
//                    }
//                }
//            }
//            if (m10974D4()) {
//                this.f9492j4 = 0;
//                return;
//            }
            int i14 = this.f9492j4;
            if (i14 != 0) {
//                if (i14 != 1) {
//                    if (i14 != 2) {
//                        if (i14 != 3) {
//                            if (i14 != 33) {
//                                if (i14 != 34) {
//                                    if (i14 == 36 || i14 == 37) {
//                                        SubView subView = this.f9522r1;
//                                        int i15 = subView.f11505s - 1;
//                                        subView.f11505s = i15;
//                                        if (i15 < 0) {
//                                            subView.f11505s = 0;
//                                        }
//                                        mo10121y1(null);
//                                        if (action == 1) {
//                                            SharedPreferences.Editor m6109d = MXApplication.f30299C.m6109d();
//                                            if (this.f9492j4 == 36) {
//                                                m6109d.putFloat("subtitle_text_size.2", this.f9522r1.getTextSize() / DeviceUtils.f29173c);
//                                            } else {
//                                                m6109d.putFloat("subtitle_scale", sa2.f24427w);
//                                            }
//                                            m6109d.apply();
//                                        }
//                                    } else {
//                                        switch (i14) {
//                                        }
//                                    }
//                                } else {
//                                    mo10121y1(null);
//                                    if (action == 1) {
//                                        SharedPreferences.Editor m6109d2 = MXApplication.f30299C.m6109d();
//                                        m6109d2.putInt("subtitle_bottom_padding.2", sa2.f24371d0);
//                                        m6109d2.apply();
//                                    }
//                                }
//                            } else {
//                                SubView subView2 = this.f9522r1;
//                                int i16 = subView2.f11505s - 1;
//                                subView2.f11505s = i16;
//                                if (i16 < 0) {
//                                    z3 = false;
//                                    subView2.f11505s = 0;
//                                } else {
//                                    z3 = false;
//                                }
//                                r3 = z3;
//                                if (action == 1) {
//                                    double x7 = (motionEvent.getX() + iArr[z3 ? 1 : 0]) - this.f9476f4;
//                                    if (x7 >= SubView.f11489N) {
//                                        this.f9522r1.m10101p(-1);
//                                    } else if (x7 <= (-f)) {
//                                        this.f9522r1.m10101p(1);
//                                    }
//                                }
//                                this.f9496k4 = this.f9492j4;
//                                this.f9492j4 = r3;
//                                if (!this.f9540u4) {
//                                    this.f9540u4 = r3;
//                                    this.f9522r1.setTextBackColor(r3);
//                                    SubView subView3 = this.f9522r1;
//                                    int i17 = subView3.f11505s - 1;
//                                    subView3.f11505s = i17;
//                                    if (i17 < 0) {
//                                        subView3.f11505s = r3;
//                                        return;
//                                    }
//                                    return;
//                                }
//                                return;
//                            }
//                        }
//                        mo10121y1(null);
//                    } else {
//                        ScreenVerticalBar screenVerticalBar3 = this.f9504m4;
//                        int i18 = screenVerticalBar3.f11723u - 1;
//                        screenVerticalBar3.f11723u = i18;
//                        if (i18 == 0) {
//                            MXApplication.f30298B.postDelayed(screenVerticalBar3.f11724v, 1000L);
//                            IScreen iScreen = screenVerticalBar3.f11721s;
//                            if (iScreen != null && iScreen.mo10156T1() == null) {
//                                screenVerticalBar3.f11721s.mo10121y1(null);
//                            }
//                        }
//                        ScreenVerticalBar screenVerticalBar4 = this.f9504m4;
//                        if (screenVerticalBar4.getVisibility() == 0) {
//                            screenVerticalBar4.setVisibility(4);
//                            screenVerticalBar4.startAnimation(screenVerticalBar4.f11722t);
//                        }
//                        this.f9504m4 = null;
//                    }
//                } else {
//                    this.f9405O0.m10024h();
//                    if ((this.f9405O0.getVisibleParts() & 5) == 1) {
//                        z = true;
//                    } else {
//                        z = false;
//                    }
//                    if (z) {
//                        z2 = false;
//                        m10925S3(0);
//                    } else {
//                        z2 = false;
//                    }
//                    m10833p5(null, null, z2, true);
//                    mo10138m1();
//                }
//                r3 = 0;
//                this.f9496k4 = this.f9492j4;
//                this.f9492j4 = r3;
//                if (!this.f9540u4) {
//                }
            } else {
                if (action == 1) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    if (view instanceof UILayout) {
                        int i19 = i & 5184;
//                        RunnableC1330b runnableC1330b = this.f9352C4;
                        if (i19 != 0 && this.f9496k4 == 0) {
                            if (uptimeMillis < this.f9500l4 + 300) {
                                if ((i & 1024) != 0) {
//                                    Zoom zoom5 = this.f9474f2;
//                                    if (zoom5.f4253s != 0.0d && zoom5.f4254t != 0.0d) {
//                                        this.f9486i2 = 0;
//                                        this.f9482h2 = 0;
//                                        this.f9490j2 = false;
//                                        zoom5.m12918a();
//                                    } else {
//                                        int x8 = ((int) motionEvent.getX(0)) + iArr[0];
//                                        int y7 = ((int) motionEvent.getY(0)) + iArr[1];
//                                        int width = this.f9541v0.getWidth();
//                                        int height = this.f9541v0.getHeight();
//                                        Zoom zoom6 = this.f9474f2;
//                                        int i20 = zoom6.f4252r;
//                                        IScreen iScreen2 = zoom6.f4246C;
//                                        if (i20 != 0) {
//                                            if (i20 != 2) {
//                                                if (i20 != 3) {
//                                                    point = new Point((int) zoom6.f4255u, (int) zoom6.f4256v);
//                                                } else {
//                                                    point = new Point((int) zoom6.f4257w, (int) zoom6.f4258x);
//                                                }
//                                            } else {
//                                                C1586o mo10168G = iScreen2.mo10168G();
//                                                point = new Point(mo10168G.f10852P, mo10168G.f10854Q);
//                                            }
//                                        } else {
//                                            point = new Point(iScreen2.mo10144g(), iScreen2.mo10146e());
//                                        }
//                                        int i21 = point.x * 2;
//                                        int i22 = point.y * 2;
//                                        int i23 = (-(x8 - (width / 2))) * 2;
//                                        this.f9482h2 = i23;
//                                        int i24 = (-(y7 - (height / 2))) * 2;
//                                        this.f9486i2 = i24;
//                                        int i25 = (width - i21) / 2;
//                                        int i26 = (height - i22) / 2;
//                                        int i27 = i23 + i25;
//                                        int i28 = i24 + i26;
//                                        if (i27 > 0) {
//                                            this.f9482h2 = -i25;
//                                        } else if (i27 + i21 < width) {
//                                            this.f9482h2 = i25;
//                                        }
//                                        if (i28 > 0) {
//                                            this.f9486i2 = -i26;
//                                        } else if (i28 + i22 < height) {
//                                            this.f9486i2 = i26;
//                                        }
//                                        this.f9490j2 = true;
//                                        this.f9474f2.m12910i(i21, i22);
//                                    }
//                                    m10903X5();
                                } else {
                                    m10951L3(i);
                                }
//                                handler.removeCallbacks(runnableC1330b);
                            } else {
                                this.f9500l4 = uptimeMillis;
//                                handler.removeCallbacks(runnableC1330b);
//                                handler.postDelayed(runnableC1330b, 300L);
                            }
                        } else {
//                            handler.post(runnableC1330b);
                        }
                    } else if (this.f9562z1) {
//                        r3 = 0;
//                        this.f9405O0.m10026f(3, 0, false);
//                        this.f9405O0.m10029c();
//                        this.f9496k4 = this.f9492j4;
//                        this.f9492j4 = r3;
//                        if (!this.f9540u4) {
//                        }
                    }
                }

//                this.f9496k4 = this.f9492j4;
//                this.f9492j4 = 0;
//                if (!this.f9540u4) {
//                }
            }
        } else if (this.f9492j4 == 0) {
            this.f9476f4 = ((int) motionEvent.getX()) + iArr[0];
            this.f9480g4 = ((int) motionEvent.getY()) + iArr[1];
//            if ((i & SkinViewInflater.FLAG_ANDROID_FOREGROUND) != 0 && this.f9522r1.getVisibility() != 8 && m10877e4(this.f9476f4, this.f9480g4, SubView.f11490O)) {
//                this.f9540u4 = true;
//                this.f9522r1.setTextBackColor(1625152989);
//                this.f9522r1.f11505s++;
//            } else if (motionEvent.getPointerCount() <= 1 && (i & SkinViewInflater.FLAG_ANDROID_DRAWABLELEFT) != 0) {
//                JointPlayer jointPlayer2 = this.f9556y0.f10860W;
//                if ((jointPlayer2 == null || (jointPlayer2.mo11375B() & 8) != 0) && this.f9556y0.mo10337d()) {
//                    this.f9347B4 = false;
//                    handler.removeMessages(14);
//                    handler.sendEmptyMessageDelayed(14, ViewConfiguration.getLongPressTimeout());
//                }
//            }
        }
    }


    public int f9476f4;
    public int f9480g4;
    @SuppressLint("WrongConstant")
    public final void m10951L3(int i) {
        boolean z;
        boolean z2;
        char c = 0;
        int i2 = this.f9476f4;
        int i3 = this.f9480g4;
        int width = this.f9541v0.getWidth();
        if ((i & 64) != 0) {
            z = true;
        } else {
            z = false;
        }
        if ((i & 4096) != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        if (!z || z2) {
            if (z || !z2) {
                if (z && z2) {
                    int i4 = width / 3;
                    if (i2 >= i4) {
                    }
                    c = 1;
                } else {
                    c = 65535;
                }
            }
            if (c != 2) {
//                if (this.f9556y0.mo10337d()) {
//                    String string = getString(R.string.paused);
//                    Object obj = ContextCompat.f21129a;
//                    m10833p5(string, ContextCompat.C2953c.m5820b(this, R.drawable.ic_button_pause), true, false);
//                    mo10145e0();
//                }
//                this.f9556y0.m10363N0(true);
                TapSeekLayout m10963H3 = m10963H3();
                m10963H3.f11751w = 2;
                m10963H3.f11750v = 0;
                return;
            } else if (c == 0) {
                TapSeekLayout m10963H32 = m10963H3();
//                C1586o c1586o = this.f9556y0;
//                m10963H32.getClass();
//                int m10362O = c1586o.m10362O();
//                int i5 = c1586o.f10844L;
//                c1586o.m10293z0(Math.min(m10362O + sa2.f24313D0, i5), 6000);
                if (m10963H32.f11751w != 1 ) {
                    m10963H32.f11752x = 20;
                    m10963H32.f11750v = 0;
                }
                m10963H32.f11748t.setVisibility(0);
                m10963H32.f11746r.setVisibility(8);
                m10963H32.m10012b();
                m10963H32.f11753y.m10036c(i2, i3);
                m10963H32.f11751w = 1;
//                String str = td3.f25469a;
                return;
            } else if (c == 1) {
                TapSeekLayout m10963H33 = m10963H3();
//                C1586o c1586o2 = this.f9556y0;
//                m10963H33.getClass();
//                int max = Math.max(c1586o2.m10362O() - sa2.f24313D0, 0);
//                c1586o2.m10293z0(max, 6000);
                if (m10963H33.f11751w != 0 ) {
                    m10963H33.f11750v = 0;
                }
                m10963H33.f11746r.setVisibility(0);
                m10963H33.f11748t.setVisibility(8);
                m10963H33.m10012b();
                m10963H33.f11753y.m10036c(i2, i3);
                m10963H33.f11751w = 0;
//                String str2 = td3.f25469a;
                return;
            } else {
                return;
            }
        }
        c = 2;
        if (c != 2) {
        }
    }

}