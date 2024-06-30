package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.scope.ActivityScope;
import com.carrydream.cardrecorder.ui.Module.HomeModule;
import com.carrydream.cardrecorder.ui.fragment.HomeFragment;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {HomeModule.class})
@ActivityScope
/* loaded from: classes.dex */
public interface HomeComponent {
    void inject(HomeFragment homeFragment);
}
