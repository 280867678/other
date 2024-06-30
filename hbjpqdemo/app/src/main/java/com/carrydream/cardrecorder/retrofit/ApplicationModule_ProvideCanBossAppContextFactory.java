package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.BaseApplication;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class ApplicationModule_ProvideCanBossAppContextFactory implements Factory<BaseApplication> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final ApplicationModule module;

    public ApplicationModule_ProvideCanBossAppContextFactory(ApplicationModule applicationModule) {
        this.module = applicationModule;
    }

    @Override // javax.inject.Provider
    public BaseApplication get() {
        return (BaseApplication) Preconditions.checkNotNull(this.module.provideCanBossAppContext(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<BaseApplication> create(ApplicationModule applicationModule) {
        return new ApplicationModule_ProvideCanBossAppContextFactory(applicationModule);
    }

    public static BaseApplication proxyProvideCanBossAppContext(ApplicationModule applicationModule) {
        return applicationModule.provideCanBossAppContext();
    }
}
