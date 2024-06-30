package com.carrydream.cardrecorder.retrofit;

import android.app.Application;
import android.content.Context;
import com.carrydream.cardrecorder.BaseApplication;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
/* loaded from: classes.dex */
public class ApplicationModule {
    Application context;

    public ApplicationModule(Application application) {
        this.context = application;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public Application provideApplication() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public Context provideContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public BaseApplication provideCanBossAppContext() {
        return (BaseApplication) this.context;
    }
}
