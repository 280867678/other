package com.carrydream.cardrecorder.ui.fragment;

import com.carrydream.cardrecorder.ui.Presenter.HomePresenter;

import java.util.Objects;

import javax.inject.Provider;

import dagger.MembersInjector;

/* loaded from: classes.dex */
public final class HomeFragment_MembersInjector implements MembersInjector<HomeFragment> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<HomePresenter> presenterProvider;

    public HomeFragment_MembersInjector(Provider<HomePresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<HomeFragment> create(Provider<HomePresenter> provider) {
        return new HomeFragment_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(HomeFragment homeFragment) {
        Objects.requireNonNull(homeFragment, "Cannot inject members into a null reference");
        homeFragment.presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(HomeFragment homeFragment, Provider<HomePresenter> provider) {
        homeFragment.presenter = provider.get();
    }
}
