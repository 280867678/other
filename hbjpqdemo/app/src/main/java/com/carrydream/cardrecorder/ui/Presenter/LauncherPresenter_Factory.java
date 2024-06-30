package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
import dagger.internal.Factory;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class LauncherPresenter_Factory implements Factory<LauncherPresenter> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<Api> apiProvider;
    private final Provider<LauncherContacts.View> viewProvider;

    public LauncherPresenter_Factory(Provider<Api> provider, Provider<LauncherContacts.View> provider2) {
        this.apiProvider = provider;
        this.viewProvider = provider2;
    }

    @Override // javax.inject.Provider
    public LauncherPresenter get() {
        return new LauncherPresenter(this.apiProvider.get(), this.viewProvider.get());
    }

    public static Factory<LauncherPresenter> create(Provider<Api> provider, Provider<LauncherContacts.View> provider2) {
        return new LauncherPresenter_Factory(provider, provider2);
    }
}
