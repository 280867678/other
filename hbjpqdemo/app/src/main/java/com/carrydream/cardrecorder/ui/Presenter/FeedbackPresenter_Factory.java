package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;
import dagger.internal.Factory;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class FeedbackPresenter_Factory implements Factory<FeedbackPresenter> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<Api> apiProvider;
    private final Provider<FeedbackContacts.View> viewProvider;

    public FeedbackPresenter_Factory(Provider<Api> provider, Provider<FeedbackContacts.View> provider2) {
        this.apiProvider = provider;
        this.viewProvider = provider2;
    }

    @Override // javax.inject.Provider
    public FeedbackPresenter get() {
        return new FeedbackPresenter(this.apiProvider.get(), this.viewProvider.get());
    }

    public static Factory<FeedbackPresenter> create(Provider<Api> provider, Provider<FeedbackContacts.View> provider2) {
        return new FeedbackPresenter_Factory(provider, provider2);
    }
}
