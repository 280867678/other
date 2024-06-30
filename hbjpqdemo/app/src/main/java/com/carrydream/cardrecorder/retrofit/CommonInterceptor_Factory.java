package com.carrydream.cardrecorder.retrofit;

import dagger.internal.Factory;

/* loaded from: classes.dex */
public final class CommonInterceptor_Factory implements Factory<CommonInterceptor> {
    private static final CommonInterceptor_Factory INSTANCE = new CommonInterceptor_Factory();

    @Override // javax.inject.Provider
    public CommonInterceptor get() {
        return new CommonInterceptor();
    }

    public static Factory<CommonInterceptor> create() {
        return INSTANCE;
    }
}
