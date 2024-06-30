package com.mxtech.app;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

public class OrderedSharedPreferences extends Handler implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final SharedPreferences _prefs;


    public OrderedSharedPreferences(SharedPreferences prefs) {
        super(Looper.getMainLooper());
        this._prefs = prefs;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {

    }



    public boolean getBoolean(String key, boolean defValue) {
        return this._prefs.getBoolean(key, defValue);
    }


    public String getString(String key, String defValue) {
        return this._prefs.getString(key, defValue);
    }



}
