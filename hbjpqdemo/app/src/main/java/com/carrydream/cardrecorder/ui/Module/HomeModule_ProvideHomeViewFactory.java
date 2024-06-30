package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class HomeModule_ProvideHomeViewFactory implements Factory<HomeContacts.View> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final HomeModule module;

    public HomeModule_ProvideHomeViewFactory(HomeModule homeModule) {
        this.module = homeModule;
    }

    @Override // javax.inject.Provider
    public HomeContacts.View get() {
        return (HomeContacts.View) Preconditions.checkNotNull(this.module.provideHomeView(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<HomeContacts.View> create(HomeModule homeModule) {
        return new HomeModule_ProvideHomeViewFactory(homeModule);
    }

    public static HomeContacts.View proxyProvideHomeView(HomeModule homeModule) {
        return homeModule.provideHomeView();
    }
}
