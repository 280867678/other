package com.carrydream.cardrecorder;

import android.app.Application;
import android.os.Handler;
import com.carrydream.cardrecorder.tool.LogUtil;

/* loaded from: classes.dex */
public abstract class BaseFrameApplication extends Application {
    public static BaseFrameApplication instance;
    private final Handler handler = new Handler();

//    protected abstract Class getCrashLauncherActivity();

    /* JADX INFO: Access modifiers changed from: protected */
    public void uncaughtException(Thread thread, Throwable th) {
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtil.i("BaseFrameApplication onCreate");
        onInitData();
        this.handler.post(new Runnable() { // from class: com.carrydream.cardrecorder.BaseFrameApplication.1
            @Override // java.lang.Runnable
            public void run() {
                BaseFrameApplication.this.onInitDataThread();
            }
        });
    }

    public static BaseFrameApplication getInstance() {
        return instance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onInitData() {
        LogUtil.i("BaseFrameApplication onInitData");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onInitDataThread() {
        LogUtil.i("BaseFrameApplication onInitDataThread");
    }
}
