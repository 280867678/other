package com.carrydream.cardrecorder.ui.activity;

import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter;
import dagger.MembersInjector;
import java.util.Objects;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class LauncherActivity_MembersInjector implements MembersInjector<LauncherActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<LauncherPresenter> presenterProvider;

    public LauncherActivity_MembersInjector(Provider<LauncherPresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<LauncherActivity> create(Provider<LauncherPresenter> provider) {
        return new LauncherActivity_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(LauncherActivity launcherActivity) {
        Objects.requireNonNull(launcherActivity, "Cannot inject members into a null reference");
        launcherActivity.Presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(LauncherActivity launcherActivity, Provider<LauncherPresenter> provider) {
        launcherActivity.Presenter = provider.get();
    }
}
