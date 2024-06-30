package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.BaseApplication;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/* loaded from: classes.dex */
public final class RetrofitModule_ProvideRetrofitFactory implements Factory<Retrofit> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<BaseApplication> baseAppContextProvider;
    private final Provider<OkHttpClient> clientProvider;
    private final RetrofitModule module;

    public RetrofitModule_ProvideRetrofitFactory(RetrofitModule retrofitModule, Provider<OkHttpClient> provider, Provider<BaseApplication> provider2) {
        this.module = retrofitModule;
        this.clientProvider = provider;
        this.baseAppContextProvider = provider2;
    }

    @Override // javax.inject.Provider
    public Retrofit get() {
        return (Retrofit) Preconditions.checkNotNull(this.module.provideRetrofit(this.clientProvider.get(), this.baseAppContextProvider.get()), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<Retrofit> create(RetrofitModule retrofitModule, Provider<OkHttpClient> provider, Provider<BaseApplication> provider2) {
        return new RetrofitModule_ProvideRetrofitFactory(retrofitModule, provider, provider2);
    }

    public static Retrofit proxyProvideRetrofit(RetrofitModule retrofitModule, OkHttpClient okHttpClient, BaseApplication baseApplication) {
        return retrofitModule.provideRetrofit(okHttpClient, baseApplication);
    }
}
