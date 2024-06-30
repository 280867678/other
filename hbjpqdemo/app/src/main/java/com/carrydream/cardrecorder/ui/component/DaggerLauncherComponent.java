package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.ui.Module.LauncherModule;
import com.carrydream.cardrecorder.ui.Module.LauncherModule_ProvideLauncherViewFactory;
import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter;
import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter_Factory;
import com.carrydream.cardrecorder.ui.activity.LauncherActivity;
import com.carrydream.cardrecorder.ui.activity.LauncherActivity_MembersInjector;
import com.carrydream.cardrecorder.ui.activity.LoginActivity;
import com.carrydream.cardrecorder.ui.activity.LoginActivity_MembersInjector;
import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;

import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class DaggerLauncherComponent implements LauncherComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Provider<Api> getApiProvider;
    private MembersInjector<LauncherActivity> launcherActivityMembersInjector;
    private Provider<LauncherPresenter> launcherPresenterProvider;
    private MembersInjector<LoginActivity> loginActivityMembersInjector;
    private Provider<LauncherContacts.View> provideLauncherViewProvider;

    private DaggerLauncherComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
//        this.getApiProvider = new Factory<Api>() { // from class: com.carrydream.cardrecorder.ui.component.DaggerLauncherComponent.1
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
                return  Preconditions.checkNotNull(builder.appComponent.getApi(), "Cannot return null from a non-@Nullable component method");
            }
        };


        Factory<LauncherContacts.View> create = LauncherModule_ProvideLauncherViewFactory.create(builder.launcherModule);
        this.provideLauncherViewProvider = create;
        Factory<LauncherPresenter> create2 = LauncherPresenter_Factory.create(this.getApiProvider, create);
        this.launcherPresenterProvider = create2;
        this.loginActivityMembersInjector = LoginActivity_MembersInjector.create(create2);
        this.launcherActivityMembersInjector = LauncherActivity_MembersInjector.create(this.launcherPresenterProvider);
    }

    @Override // com.carrydream.cardrecorder.ui.component.LauncherComponent
    public void inject(LoginActivity loginActivity) {
        this.loginActivityMembersInjector.injectMembers(loginActivity);
    }

    @Override // com.carrydream.cardrecorder.ui.component.LauncherComponent
    public void inject(LauncherActivity launcherActivity) {
        this.launcherActivityMembersInjector.injectMembers(launcherActivity);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private AppComponent appComponent;
        private LauncherModule launcherModule;

        private Builder() {
        }

        public LauncherComponent build() {
            if (this.launcherModule == null) {
                throw new IllegalStateException(LauncherModule.class.getCanonicalName() + " must be set");
            } else if (this.appComponent == null) {
                throw new IllegalStateException(AppComponent.class.getCanonicalName() + " must be set");
            } else {
                return new DaggerLauncherComponent(this);
            }
        }

        public Builder launcherModule(LauncherModule launcherModule) {
            this.launcherModule = (LauncherModule) Preconditions.checkNotNull(launcherModule);
            return this;
        }

        public Builder appComponent(AppComponent appComponent) {
            this.appComponent = (AppComponent) Preconditions.checkNotNull(appComponent);
            return this;
        }
    }
}
