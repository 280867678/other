package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.base.Api;
import dagger.Component;
import javax.inject.Singleton;

@Component(modules = {ApplicationModule.class, RetrofitModule.class, ApiModule.class})
@Singleton
/* loaded from: classes.dex */
public interface AppComponent {
    Api getApi();
}
