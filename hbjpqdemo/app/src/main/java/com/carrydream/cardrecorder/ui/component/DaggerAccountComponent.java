package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.ui.Module.AccountModule;
import com.carrydream.cardrecorder.ui.Module.AccountModule_ProvideAccountViewFactory;
import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter;
import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter_Factory;
import com.carrydream.cardrecorder.ui.activity.VipActivity;
import com.carrydream.cardrecorder.ui.activity.VipActivity_MembersInjector;
import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import com.carrydream.cardrecorder.ui.fragment.AccountFragment;
import com.carrydream.cardrecorder.ui.fragment.AccountFragment_MembersInjector;

import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class DaggerAccountComponent implements AccountComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private MembersInjector<AccountFragment> accountFragmentMembersInjector;
    private Provider<AccountPresenter> accountPresenterProvider;
    private Provider<Api> getApiProvider;
    private Provider<AccountContacts.View> provideAccountViewProvider;
    private MembersInjector<VipActivity> vipActivityMembersInjector;

    private DaggerAccountComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
//        this.getApiProvider = new Factory<Api>(builder) { // from class: com.carrydream.cardrecorder.ui.component.DaggerAccountComponent.1
//            private final AppComponent appComponent;
//            final /* synthetic */ Builder val$builder;
//
//            {
//                this.val$builder = builder;
//                this.appComponent = builder.appComponent;
//            }
//
//            @Override // javax.inject.Provider
//            public Api get() {
//                return (Api) Preconditions.checkNotNull(this.appComponent.getApi(), "Cannot return null from a non-@Nullable component method");
//            }
//        };

        this.getApiProvider = new Provider<Api>() {
            @Override
            public Api get() {
                return (Api) Preconditions.checkNotNull(builder.appComponent.getApi(), "Cannot return null from a non-@Nullable component method");
            }
        };

        Factory<AccountContacts.View> create = AccountModule_ProvideAccountViewFactory.create(builder.accountModule);
        this.provideAccountViewProvider = create;
        Factory<AccountPresenter> create2 = AccountPresenter_Factory.create(this.getApiProvider, create);
        this.accountPresenterProvider = create2;
        this.accountFragmentMembersInjector = AccountFragment_MembersInjector.create(create2);
        this.vipActivityMembersInjector = VipActivity_MembersInjector.create(this.accountPresenterProvider);
    }

    @Override // com.carrydream.cardrecorder.ui.component.AccountComponent
    public void inject(AccountFragment accountFragment) {
        this.accountFragmentMembersInjector.injectMembers(accountFragment);
    }

    @Override // com.carrydream.cardrecorder.ui.component.AccountComponent
    public void inject(VipActivity vipActivity) {
        this.vipActivityMembersInjector.injectMembers(vipActivity);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private AccountModule accountModule;
        private AppComponent appComponent;

        private Builder() {
        }

        public AccountComponent build() {
            if (this.accountModule == null) {
                throw new IllegalStateException(AccountModule.class.getCanonicalName() + " must be set");
            } else if (this.appComponent == null) {
                throw new IllegalStateException(AppComponent.class.getCanonicalName() + " must be set");
            } else {
                return new DaggerAccountComponent(this);
            }
        }

        public Builder accountModule(AccountModule accountModule) {
            this.accountModule = (AccountModule) Preconditions.checkNotNull(accountModule);
            return this;
        }

        public Builder appComponent(AppComponent appComponent) {
            this.appComponent = (AppComponent) Preconditions.checkNotNull(appComponent);
            return this;
        }
    }
}
