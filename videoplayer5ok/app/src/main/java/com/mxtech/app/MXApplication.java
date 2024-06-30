package com.mxtech.app;

import android.app.Application;

public class MXApplication extends Application {
    public static OrderedSharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
