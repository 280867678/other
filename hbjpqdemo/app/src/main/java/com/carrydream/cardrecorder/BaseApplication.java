package com.carrydream.cardrecorder;

import com.carrydream.cardrecorder.base.Constant;
import com.carrydream.cardrecorder.retrofit.ApiModule;
import com.carrydream.cardrecorder.retrofit.AppComponent;
import com.carrydream.cardrecorder.retrofit.ApplicationModule;
import com.carrydream.cardrecorder.retrofit.DaggerAppComponent;
import com.carrydream.cardrecorder.retrofit.RetrofitModule;
import com.carrydream.cardrecorder.tool.SpUtils;
import com.hjq.permissions.XXPermissions;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.commonsdk.UMConfigure;

/* loaded from: classes.dex */
public class BaseApplication extends BaseFrameApplication {
    public static IWXAPI MyWxApi;
    private static BaseApplication appContext;
    public static boolean is_debug;
    public AppComponent mAppComponent;

//    @Override // com.carrydream.cardrecorder.BaseFrameApplication
//    protected Class getCrashLauncherActivity() {
//        return MainActivity.class;
//    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.carrydream.cardrecorder.BaseFrameApplication
    public void uncaughtException(Thread thread, Throwable th) {
        super.uncaughtException(thread, th);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.carrydream.cardrecorder.BaseFrameApplication
    public void onInitData() {
        super.onInitData();
        appContext = this;
        is_debug = true;
        XXPermissions.setScopedStorage(true);
        SpUtils.getInstance().init(appContext);
        this.mAppComponent = DaggerAppComponent.builder().apiModule(new ApiModule()).applicationModule(new ApplicationModule(this)).retrofitModule(new RetrofitModule("https://api.console.aiyouxiba.com")).build();
        IWXAPI createWXAPI = WXAPIFactory.createWXAPI(this, Constant.GetWxId, true);
        MyWxApi = createWXAPI;
        createWXAPI.registerApp(Constant.GetWxId);
//        UMConfigure.init(appContext, "63be6ffc1d7d8539493ecdc0", getString(com.hb.aiyouxiba.R.string.channel), 1, null);
//        UMConfigure.setLogEnabled(true);
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public AppComponent getmAppComponent() {
        return this.mAppComponent;
    }

    public static BaseApplication getInstance() {
        return appContext;
    }

    public static AppComponent getAppComponent() {
        return getInstance().getmAppComponent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.carrydream.cardrecorder.BaseFrameApplication
    public void onInitDataThread() {
        super.onInitDataThread();
    }
}
