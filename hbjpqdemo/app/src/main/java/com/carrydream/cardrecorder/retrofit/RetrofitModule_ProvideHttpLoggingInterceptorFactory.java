package com.carrydream.cardrecorder.retrofit;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class RetrofitModule_ProvideHttpLoggingInterceptorFactory implements Factory<TimeHttpLoggingInterceptor> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final RetrofitModule module;

    public RetrofitModule_ProvideHttpLoggingInterceptorFactory(RetrofitModule retrofitModule) {
        this.module = retrofitModule;
    }

    @Override // javax.inject.Provider
    public TimeHttpLoggingInterceptor get() {
        return (TimeHttpLoggingInterceptor) Preconditions.checkNotNull(this.module.provideHttpLoggingInterceptor(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<TimeHttpLoggingInterceptor> create(RetrofitModule retrofitModule) {
        return new RetrofitModule_ProvideHttpLoggingInterceptorFactory(retrofitModule);
    }

    public static TimeHttpLoggingInterceptor proxyProvideHttpLoggingInterceptor(RetrofitModule retrofitModule) {
        return retrofitModule.provideHttpLoggingInterceptor();
    }
}
