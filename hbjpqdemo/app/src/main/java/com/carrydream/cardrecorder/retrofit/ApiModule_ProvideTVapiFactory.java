package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.base.Api;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
import retrofit2.Retrofit;

/* loaded from: classes.dex */
public final class ApiModule_ProvideTVapiFactory implements Factory<Api> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final ApiModule module;
    private final Provider<Retrofit> retrofitProvider;

    public ApiModule_ProvideTVapiFactory(ApiModule apiModule, Provider<Retrofit> provider) {
        this.module = apiModule;
        this.retrofitProvider = provider;
    }

    @Override // javax.inject.Provider
    public Api get() {
        return (Api) Preconditions.checkNotNull(this.module.provideTVapi(this.retrofitProvider.get()), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<Api> create(ApiModule apiModule, Provider<Retrofit> provider) {
        return new ApiModule_ProvideTVapiFactory(apiModule, provider);
    }

    public static Api proxyProvideTVapi(ApiModule apiModule, Retrofit retrofit) {
        return apiModule.provideTVapi(retrofit);
    }
}
