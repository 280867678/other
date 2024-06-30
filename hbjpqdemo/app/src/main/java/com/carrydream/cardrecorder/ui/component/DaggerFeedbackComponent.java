package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.ui.Module.FeedbackModule;
import com.carrydream.cardrecorder.ui.Module.FeedbackModule_ProvideFeedbackViewFactory;
import com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter;
import com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter_Factory;
import com.carrydream.cardrecorder.ui.activity.FeedbackActivity;
import com.carrydream.cardrecorder.ui.activity.FeedbackActivity_MembersInjector;
import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;

import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class DaggerFeedbackComponent implements FeedbackComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private MembersInjector<FeedbackActivity> feedbackActivityMembersInjector;
    private Provider<FeedbackPresenter> feedbackPresenterProvider;
    private Provider<Api> getApiProvider;
    private Provider<FeedbackContacts.View> provideFeedbackViewProvider;

    private DaggerFeedbackComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
//        this.getApiProvider = new Factory<Api>(builder) { // from class: com.carrydream.cardrecorder.ui.component.DaggerFeedbackComponent.1
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
        Factory<FeedbackContacts.View> create = FeedbackModule_ProvideFeedbackViewFactory.create(builder.feedbackModule);
        this.provideFeedbackViewProvider = create;
        Factory<FeedbackPresenter> create2 = FeedbackPresenter_Factory.create(this.getApiProvider, create);
        this.feedbackPresenterProvider = create2;
        this.feedbackActivityMembersInjector = FeedbackActivity_MembersInjector.create(create2);
    }

    @Override // com.carrydream.cardrecorder.ui.component.FeedbackComponent
    public void inject(FeedbackActivity feedbackActivity) {
        this.feedbackActivityMembersInjector.injectMembers(feedbackActivity);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private AppComponent appComponent;
        private FeedbackModule feedbackModule;

        private Builder() {
        }

        public FeedbackComponent build() {
            if (this.feedbackModule == null) {
                throw new IllegalStateException(FeedbackModule.class.getCanonicalName() + " must be set");
            } else if (this.appComponent == null) {
                throw new IllegalStateException(AppComponent.class.getCanonicalName() + " must be set");
            } else {
                return new DaggerFeedbackComponent(this);
            }
        }

        public Builder feedbackModule(FeedbackModule feedbackModule) {
            this.feedbackModule = (FeedbackModule) Preconditions.checkNotNull(feedbackModule);
            return this;
        }

        public Builder appComponent(AppComponent appComponent) {
            this.appComponent = (AppComponent) Preconditions.checkNotNull(appComponent);
            return this;
        }
    }
}
