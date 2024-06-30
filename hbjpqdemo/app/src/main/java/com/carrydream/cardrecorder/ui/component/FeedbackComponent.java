package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.scope.ActivityScope;
import com.carrydream.cardrecorder.ui.Module.FeedbackModule;
import com.carrydream.cardrecorder.ui.activity.FeedbackActivity;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {FeedbackModule.class})
@ActivityScope
/* loaded from: classes.dex */
public interface FeedbackComponent {
    void inject(FeedbackActivity feedbackActivity);
}
