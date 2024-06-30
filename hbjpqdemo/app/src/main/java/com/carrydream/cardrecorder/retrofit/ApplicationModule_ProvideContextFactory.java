package com.carrydream.cardrecorder.retrofit;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class ApplicationModule_ProvideContextFactory implements Factory<Context> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final ApplicationModule module;

    public ApplicationModule_ProvideContextFactory(ApplicationModule applicationModule) {
        this.module = applicationModule;
    }

    @Override // javax.inject.Provider
    public Context get() {
        return (Context) Preconditions.checkNotNull(this.module.provideContext(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<Context> create(ApplicationModule applicationModule) {
        return new ApplicationModule_ProvideContextFactory(applicationModule);
    }

    public static Context proxyProvideContext(ApplicationModule applicationModule) {
        return applicationModule.provideContext();
    }
}
