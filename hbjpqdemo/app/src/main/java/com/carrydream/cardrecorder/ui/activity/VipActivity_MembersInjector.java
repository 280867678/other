package com.carrydream.cardrecorder.ui.activity;

import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter;
import dagger.MembersInjector;
import java.util.Objects;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class VipActivity_MembersInjector implements MembersInjector<VipActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<AccountPresenter> presenterProvider;

    public VipActivity_MembersInjector(Provider<AccountPresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<VipActivity> create(Provider<AccountPresenter> provider) {
        return new VipActivity_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(VipActivity vipActivity) {
        Objects.requireNonNull(vipActivity, "Cannot inject members into a null reference");
        vipActivity.presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(VipActivity vipActivity, Provider<AccountPresenter> provider) {
        vipActivity.presenter = provider.get();
    }
}
