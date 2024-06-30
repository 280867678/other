package com.carrydream.cardrecorder.ui.activity;

import com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter;
import dagger.MembersInjector;
import java.util.Objects;
import javax.inject.Provider;

/* loaded from: classes.dex */
public final class FeedbackActivity_MembersInjector implements MembersInjector<FeedbackActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<FeedbackPresenter> presenterProvider;

    public FeedbackActivity_MembersInjector(Provider<FeedbackPresenter> provider) {
        this.presenterProvider = provider;
    }

    public static MembersInjector<FeedbackActivity> create(Provider<FeedbackPresenter> provider) {
        return new FeedbackActivity_MembersInjector(provider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(FeedbackActivity feedbackActivity) {
        Objects.requireNonNull(feedbackActivity, "Cannot inject members into a null reference");
        feedbackActivity.presenter = this.presenterProvider.get();
    }

    public static void injectPresenter(FeedbackActivity feedbackActivity, Provider<FeedbackPresenter> provider) {
        feedbackActivity.presenter = provider.get();
    }
}
