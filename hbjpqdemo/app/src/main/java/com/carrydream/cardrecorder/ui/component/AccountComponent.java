package com.carrydream.cardrecorder.ui.component;

import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.scope.ActivityScope;
import com.carrydream.cardrecorder.ui.Module.AccountModule;
import com.carrydream.cardrecorder.ui.activity.VipActivity;
import com.carrydream.cardrecorder.ui.fragment.AccountFragment;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {AccountModule.class})
@ActivityScope
/* loaded from: classes.dex */
public interface AccountComponent {
    void inject(VipActivity vipActivity);

    void inject(AccountFragment accountFragment);
}
