package com.carrydream.cardrecorder.ui.fragment;

import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter;

import java.util.Objects;

import javax.inject.Provider;

import dagger.MembersInjector;

/* loaded from: classes.dex */
public final class AccountFragment_MembersInjector implements MembersInjector<AccountFragment> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<AccountPresenter> presenterProvider;

    public AccountFragment_MembersInjector(Provider<AccountPresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<AccountFragment> create(Provider<AccountPresenter> provider) {
        return new AccountFragment_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(AccountFragment accountFragment) {
        Objects.requireNonNull(accountFragment, "Cannot inject members into a null reference");
        accountFragment.presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(AccountFragment accountFragment, Provider<AccountPresenter> provider) {
        accountFragment.presenter = provider.get();
    }
}
