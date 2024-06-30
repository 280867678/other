package com.mxtech.videoplayer;

import android.app.Application;
import android.content.Context;

import com.mxtech.app.MXApplication;

public  class APP extends MXApplication {


    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context= this;
    }
}
