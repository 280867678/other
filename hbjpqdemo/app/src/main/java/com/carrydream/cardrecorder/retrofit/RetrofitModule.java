package com.carrydream.cardrecorder.retrofit;

import android.app.Application;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.retrofit.TimeHttpLoggingInterceptor;
import dagger.Module;
import dagger.Provides;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
/* loaded from: classes.dex */
public class RetrofitModule {
    private String baseUrl;

    public RetrofitModule(String str) {
        this.baseUrl = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, BaseApplication baseApplication) {
        return new Retrofit.Builder().baseUrl(this.baseUrl).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public OkHttpClient provideOKHttpClient(Application application, TimeHttpLoggingInterceptor timeHttpLoggingInterceptor, CommonInterceptor commonInterceptor) {
        return new OkHttpClient.Builder().proxy(Proxy.NO_PROXY).addInterceptor(timeHttpLoggingInterceptor).addInterceptor(commonInterceptor).retryOnConnectionFailure(true).hostnameVerifier(SSLSocketClient.getHostnameVerifier()).connectTimeout(10L, TimeUnit.SECONDS).readTimeout(10L, TimeUnit.SECONDS).writeTimeout(8L, TimeUnit.SECONDS).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public TimeHttpLoggingInterceptor provideHttpLoggingInterceptor() {
        TimeHttpLoggingInterceptor timeHttpLoggingInterceptor = new TimeHttpLoggingInterceptor(new TimeHttpLoggingInterceptor.Logger() { // from class: com.carrydream.cardrecorder.retrofit.RetrofitModule.1
//            public final Logger logger = LoggerFactory.getLogger(getClass());

            @Override // com.carrydream.cardrecorder.retrofit.TimeHttpLoggingInterceptor.Logger
            public void log(String str) {
//                this.logger.info(str);
            }
        });
        timeHttpLoggingInterceptor.setLevel(TimeHttpLoggingInterceptor.Level.BODY);
        return timeHttpLoggingInterceptor;
    }
}
