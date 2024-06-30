package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class AccountModule_ProvideAccountViewFactory implements Factory<AccountContacts.View> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final AccountModule module;

    public AccountModule_ProvideAccountViewFactory(AccountModule accountModule) {
        this.module = accountModule;
    }

    @Override // javax.inject.Provider
    public AccountContacts.View get() {
        return (AccountContacts.View) Preconditions.checkNotNull(this.module.provideAccountView(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<AccountContacts.View> create(AccountModule accountModule) {
        return new AccountModule_ProvideAccountViewFactory(accountModule);
    }

    public static AccountContacts.View proxyProvideAccountView(AccountModule accountModule) {
        return accountModule.provideAccountView();
    }
}
