package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class LauncherModule_ProvideLauncherViewFactory implements Factory<LauncherContacts.View> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final LauncherModule module;

    public LauncherModule_ProvideLauncherViewFactory(LauncherModule launcherModule) {
        this.module = launcherModule;
    }

    @Override // javax.inject.Provider
    public LauncherContacts.View get() {
        return (LauncherContacts.View) Preconditions.checkNotNull(this.module.provideLauncherView(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<LauncherContacts.View> create(LauncherModule launcherModule) {
        return new LauncherModule_ProvideLauncherViewFactory(launcherModule);
    }

    public static LauncherContacts.View proxyProvideLauncherView(LauncherModule launcherModule) {
        return launcherModule.provideLauncherView();
    }
}
