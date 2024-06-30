package com.carrydream.cardrecorder.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/* loaded from: classes.dex */
public class SpUtils {
    private static volatile SpUtils mInstance;
    private Context mComtext;
    private SharedPreferences mPref;

    private SpUtils() {
    }

    public static SpUtils getInstance() {
        if (mInstance == null) {
            synchronized (SpUtils.class) {
                if (mInstance == null) {
                    mInstance = new SpUtils();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (this.mComtext == null) {
            this.mComtext = context;
            this.mPref = PreferenceManager.getDefaultSharedPreferences(context);
            return;
        }
        Log.e("hjw", "未初始化lib");
    }

    public void putString(String str, String str2) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public void putLong(String str, long j) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.putLong(str, j);
        edit.commit();
    }

    public void putInt(String str, int i) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.putInt(str, i);
        edit.commit();
    }

    public void putFloat(String str, float f) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.putFloat(str, f);
        edit.commit();
    }

    public void putBoolean(String str, boolean z) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public String getString(String str) {
        return this.mPref.getString(str, "");
    }

    public String getString(String str, String str2) {
        return this.mPref.getString(str, str2);
    }

    public long getLong(String str) {
        return this.mPref.getLong(str, 0L);
    }

    public long getLong(String str, long j) {
        return this.mPref.getLong(str, j);
    }

    public int getInt(String str) {
        return this.mPref.getInt(str, 0);
    }

    public int getInt(String str, int i) {
        return this.mPref.getInt(str, i);
    }

    public float getFloat(String str) {
        return this.mPref.getFloat(str, 0.0f);
    }

    public float getFloat(String str, float f) {
        return this.mPref.getFloat(str, f);
    }

    public Boolean getBoolean(String str) {
        return Boolean.valueOf(this.mPref.getBoolean(str, false));
    }

    public Boolean getBoolean(String str, boolean z) {
        return Boolean.valueOf(this.mPref.getBoolean(str, z));
    }

    public void remove(String str) {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.remove(str);
        edit.commit();
    }

    public void clear() {
        SharedPreferences.Editor edit = this.mPref.edit();
        edit.clear();
        edit.commit();
    }
}
