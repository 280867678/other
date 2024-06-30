package com.carrydream.cardrecorder.ui.activity;

import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter;
import dagger.MembersInjector;
import java.util.Objects;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class LoginActivity_MembersInjector implements MembersInjector<LoginActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<LauncherPresenter> presenterProvider;

    public LoginActivity_MembersInjector(Provider<LauncherPresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<LoginActivity> create(Provider<LauncherPresenter> provider) {
        return new LoginActivity_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(LoginActivity loginActivity) {
        Objects.requireNonNull(loginActivity, "Cannot inject members into a null reference");
        loginActivity.Presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(LoginActivity loginActivity, Provider<LauncherPresenter> provider) {
        loginActivity.Presenter = provider.get();
    }
}
