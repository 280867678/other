package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.scope.ActivityScope;
import com.carrydream.cardrecorder.ui.Module.LauncherModule;
import com.carrydream.cardrecorder.ui.activity.LauncherActivity;
import com.carrydream.cardrecorder.ui.activity.LoginActivity;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {LauncherModule.class})
@ActivityScope
/* loaded from: classes.dex */
public interface LauncherComponent {
    void inject(LauncherActivity launcherActivity);

    void inject(LoginActivity loginActivity);
}
