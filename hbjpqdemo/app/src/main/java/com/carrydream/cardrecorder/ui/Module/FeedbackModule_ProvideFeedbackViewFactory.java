package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* loaded from: classes.dex */
public final class FeedbackModule_ProvideFeedbackViewFactory implements Factory<FeedbackContacts.View> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final FeedbackModule module;

    public FeedbackModule_ProvideFeedbackViewFactory(FeedbackModule feedbackModule) {
        this.module = feedbackModule;
    }

    @Override // javax.inject.Provider
    public FeedbackContacts.View get() {
        return (FeedbackContacts.View) Preconditions.checkNotNull(this.module.provideFeedbackView(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<FeedbackContacts.View> create(FeedbackModule feedbackModule) {
        return new FeedbackModule_ProvideFeedbackViewFactory(feedbackModule);
    }

    public static FeedbackContacts.View proxyProvideFeedbackView(FeedbackModule feedbackModule) {
        return feedbackModule.provideFeedbackView();
    }
}
