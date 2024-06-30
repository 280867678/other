package com.example.floatdragview.ad;

import android.content.Context;

import androidx.annotation.NonNull;

public abstract class AbstractTaskDetailAdController {

    /* renamed from: a */
    public static boolean f16546a = false;

    /* renamed from: b */
    protected final Context f16547b;

    /* renamed from: c */
    protected InterfaceC4459a f16548c = null;

    /* renamed from: d */
    protected boolean f16549d = false;

    /* compiled from: AbstractTaskDetailAdController.java */
    /* renamed from: com.xunlei.downloadprovider.ad.taskdetail.a$a */
    /* loaded from: classes2.dex */
    public interface InterfaceC4459a {
        /* renamed from: a */
        void mo9441a();
    }

    /* renamed from: a */
    public abstract void mo11795a(AbstractTaskDetailAdView abstractTaskDetailAdView);

    /* renamed from: a */
    public abstract void mo11794a(@NonNull AbstractTaskDetailAdView abstractTaskDetailAdView, @NonNull BaseAdapterModel baseAdapterModel);

    /* renamed from: b */
    public abstract void mo11793b(@NonNull AbstractTaskDetailAdView abstractTaskDetailAdView, @NonNull BaseAdapterModel baseAdapterModel);

    public AbstractTaskDetailAdController(Context context) {
        this.f16547b = context;
    }

    /* renamed from: a */
    public final void m11804a(InterfaceC4459a interfaceC4459a) {
        this.f16548c = interfaceC4459a;
    }

    /* renamed from: a */
    public final boolean m11805a() {
        return this.f16549d;
    }

    /* renamed from: a */
    public final void m11803a(boolean z) {
        this.f16549d = z;
    }

    /* renamed from: b */
//    public static boolean m11802b() {
//        return (DownloadCenterConfig.m10878e() || !GlobalConfigure.m11083a().f17566p.m11221i() || f16546a) ? false : true;
//    }
}

