package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import dagger.internal.Factory;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class HomePresenter_Factory implements Factory<HomePresenter> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<Api> apiProvider;
    private final Provider<HomeContacts.View> viewProvider;

    public HomePresenter_Factory(Provider<Api> provider, Provider<HomeContacts.View> provider2) {
        this.apiProvider = provider;
        this.viewProvider = provider2;
    }

    @Override // javax.inject.Provider
    public HomePresenter get() {
        return new HomePresenter(this.apiProvider.get(), this.viewProvider.get());
    }

    public static Factory<HomePresenter> create(Provider<Api> provider, Provider<HomeContacts.View> provider2) {
        return new HomePresenter_Factory(provider, provider2);
    }
}
