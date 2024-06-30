package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import dagger.internal.Factory;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class AccountPresenter_Factory implements Factory<AccountPresenter> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<Api> apiProvider;
    private final Provider<AccountContacts.View> viewProvider;

    public AccountPresenter_Factory(Provider<Api> provider, Provider<AccountContacts.View> provider2) {
        this.apiProvider = provider;
        this.viewProvider = provider2;
    }

    @Override // javax.inject.Provider
    public AccountPresenter get() {
        return new AccountPresenter(this.apiProvider.get(), this.viewProvider.get());
    }

    public static Factory<AccountPresenter> create(Provider<Api> provider, Provider<AccountContacts.View> provider2) {
        return new AccountPresenter_Factory(provider, provider2);
    }
}
