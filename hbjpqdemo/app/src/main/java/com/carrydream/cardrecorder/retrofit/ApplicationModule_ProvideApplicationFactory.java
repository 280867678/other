package com.carrydream.cardrecorder.retrofit;

import android.app.Application;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class ApplicationModule_ProvideApplicationFactory implements Factory<Application> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final ApplicationModule module;

    public ApplicationModule_ProvideApplicationFactory(ApplicationModule applicationModule) {
        this.module = applicationModule;
    }

    @Override // javax.inject.Provider
    public Application get() {
        return (Application) Preconditions.checkNotNull(this.module.provideApplication(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<Application> create(ApplicationModule applicationModule) {
        return new ApplicationModule_ProvideApplicationFactory(applicationModule);
    }

    public static Application proxyProvideApplication(ApplicationModule applicationModule) {
        return applicationModule.provideApplication();
    }
}
