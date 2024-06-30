package com.mxtech.videoplayer;

import android.app.Application;
import android.content.Context;

public class APP extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context= this;
    }
}
