package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.base.Api;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit2.Retrofit;

@Module
/* loaded from: classes.dex */
public class ApiModule {
    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public Api provideTVapi(Retrofit retrofit) {
        return (Api) retrofit.create(Api.class);
    }
}
