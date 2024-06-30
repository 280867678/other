package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.ui.Module.HomeModule;
import com.carrydream.cardrecorder.ui.Module.HomeModule_ProvideHomeViewFactory;
import com.carrydream.cardrecorder.ui.Presenter.HomePresenter;
import com.carrydream.cardrecorder.ui.Presenter.HomePresenter_Factory;
import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import com.carrydream.cardrecorder.ui.fragment.HomeFragment;
import com.carrydream.cardrecorder.ui.fragment.HomeFragment_MembersInjector;

import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class DaggerHomeComponent implements HomeComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Provider<Api> getApiProvider;
    private MembersInjector<HomeFragment> homeFragmentMembersInjector;
    private Provider<HomePresenter> homePresenterProvider;
    private Provider<HomeContacts.View> provideHomeViewProvider;

    private DaggerHomeComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
//        this.getApiProvider = new Factory<Api>(builder) { // from class: com.carrydream.cardrecorder.ui.component.DaggerHomeComponent.1
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
        Factory<HomeContacts.View> create = HomeModule_ProvideHomeViewFactory.create(builder.homeModule);
        this.provideHomeViewProvider = create;
        Factory<HomePresenter> create2 = HomePresenter_Factory.create(this.getApiProvider, create);
        this.homePresenterProvider = create2;
        this.homeFragmentMembersInjector = HomeFragment_MembersInjector.create(create2);
    }

    @Override // com.carrydream.cardrecorder.ui.component.HomeComponent
    public void inject(HomeFragment homeFragment) {
        this.homeFragmentMembersInjector.injectMembers(homeFragment);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private AppComponent appComponent;
        private HomeModule homeModule;

        private Builder() {
        }

        public HomeComponent build() {
            if (this.homeModule == null) {
                throw new IllegalStateException(HomeModule.class.getCanonicalName() + " must be set");
            } else if (this.appComponent == null) {
                throw new IllegalStateException(AppComponent.class.getCanonicalName() + " must be set");
            } else {
                return new DaggerHomeComponent(this);
            }
        }

        public Builder homeModule(HomeModule homeModule) {
            this.homeModule = (HomeModule) Preconditions.checkNotNull(homeModule);
            return this;
        }

        public Builder appComponent(AppComponent appComponent) {
            this.appComponent = (AppComponent) Preconditions.checkNotNull(appComponent);
            return this;
        }
    }
}
