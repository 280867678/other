package com.carrydream.cardrecorder.retrofit;

import android.app.Application;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.base.Api;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/* loaded from: classes.dex */
public final class DaggerAppComponent implements AppComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Provider<Application> provideApplicationProvider;
    private Provider<BaseApplication> provideCanBossAppContextProvider;
    private Provider<TimeHttpLoggingInterceptor> provideHttpLoggingInterceptorProvider;
    private Provider<OkHttpClient> provideOKHttpClientProvider;
    private Provider<Retrofit> provideRetrofitProvider;
    private Provider<Api> provideTVapiProvider;

    private DaggerAppComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
        this.provideApplicationProvider = DoubleCheck.provider(ApplicationModule_ProvideApplicationFactory.create(builder.applicationModule));
        this.provideHttpLoggingInterceptorProvider = DoubleCheck.provider(RetrofitModule_ProvideHttpLoggingInterceptorFactory.create(builder.retrofitModule));
        this.provideOKHttpClientProvider = DoubleCheck.provider(RetrofitModule_ProvideOKHttpClientFactory.create(builder.retrofitModule, this.provideApplicationProvider, this.provideHttpLoggingInterceptorProvider, CommonInterceptor_Factory.create()));
        this.provideCanBossAppContextProvider = DoubleCheck.provider(ApplicationModule_ProvideCanBossAppContextFactory.create(builder.applicationModule));
        this.provideRetrofitProvider = DoubleCheck.provider(RetrofitModule_ProvideRetrofitFactory.create(builder.retrofitModule, this.provideOKHttpClientProvider, this.provideCanBossAppContextProvider));
        this.provideTVapiProvider = DoubleCheck.provider(ApiModule_ProvideTVapiFactory.create(builder.apiModule, this.provideRetrofitProvider));
    }

    @Override // com.carrydream.cardrecorder.retrofit.AppComponent
    public Api getApi() {
        return this.provideTVapiProvider.get();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ApiModule apiModule;
        private ApplicationModule applicationModule;
        private RetrofitModule retrofitModule;

        private Builder() {
        }

        public AppComponent build() {
            if (this.applicationModule == null) {
                throw new IllegalStateException(ApplicationModule.class.getCanonicalName() + " must be set");
            } else if (this.retrofitModule == null) {
                throw new IllegalStateException(RetrofitModule.class.getCanonicalName() + " must be set");
            } else {
                if (this.apiModule == null) {
                    this.apiModule = new ApiModule();
                }
                return new DaggerAppComponent(this);
            }
        }

        public Builder applicationModule(ApplicationModule applicationModule) {
            this.applicationModule = (ApplicationModule) Preconditions.checkNotNull(applicationModule);
            return this;
        }

        public Builder retrofitModule(RetrofitModule retrofitModule) {
            this.retrofitModule = (RetrofitModule) Preconditions.checkNotNull(retrofitModule);
            return this;
        }

        public Builder apiModule(ApiModule apiModule) {
            this.apiModule = (ApiModule) Preconditions.checkNotNull(apiModule);
            return this;
        }
    }
}
