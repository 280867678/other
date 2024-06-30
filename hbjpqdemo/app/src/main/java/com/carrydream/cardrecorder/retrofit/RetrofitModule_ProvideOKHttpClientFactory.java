package com.carrydream.cardrecorder.retrofit;

import android.app.Application;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

/* loaded from: classes.dex */
public final class RetrofitModule_ProvideOKHttpClientFactory implements Factory<OkHttpClient> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<Application> appProvider;
    private final Provider<CommonInterceptor> commonInterceptorProvider;
    private final Provider<TimeHttpLoggingInterceptor> httpLoggingInterceptorProvider;
    private final RetrofitModule module;

    public RetrofitModule_ProvideOKHttpClientFactory(RetrofitModule retrofitModule, Provider<Application> provider, Provider<TimeHttpLoggingInterceptor> provider2, Provider<CommonInterceptor> provider3) {
        this.module = retrofitModule;
        this.appProvider = provider;
        this.httpLoggingInterceptorProvider = provider2;
        this.commonInterceptorProvider = provider3;
    }

    @Override // javax.inject.Provider
    public OkHttpClient get() {
        return (OkHttpClient) Preconditions.checkNotNull(this.module.provideOKHttpClient(this.appProvider.get(), this.httpLoggingInterceptorProvider.get(), this.commonInterceptorProvider.get()), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<OkHttpClient> create(RetrofitModule retrofitModule, Provider<Application> provider, Provider<TimeHttpLoggingInterceptor> provider2, Provider<CommonInterceptor> provider3) {
        return new RetrofitModule_ProvideOKHttpClientFactory(retrofitModule, provider, provider2, provider3);
    }

    public static OkHttpClient proxyProvideOKHttpClient(RetrofitModule retrofitModule, Application application, TimeHttpLoggingInterceptor timeHttpLoggingInterceptor, CommonInterceptor commonInterceptor) {
        return retrofitModule.provideOKHttpClient(application, timeHttpLoggingInterceptor, commonInterceptor);
    }
}
