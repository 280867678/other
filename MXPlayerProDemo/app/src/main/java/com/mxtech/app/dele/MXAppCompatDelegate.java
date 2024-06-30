package com.mxtech.app.dele;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;

public class MXAppCompatDelegate {
    public static AppCompatDelegate create(Activity activity, AppCompatCallback callback) {
        return create(activity, activity.getWindow(), callback);
    }

    public static AppCompatDelegate create(Dialog dialog, AppCompatCallback callback) {
        return create(dialog.getContext(), dialog.getWindow(), callback);
    }

    private static AppCompatDelegate create(Context context, Window window, AppCompatCallback callback) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 23) {
            return new MXAppCompatDelegateImplV23(context, window, callback);
        }
        if (sdk >= 14) {
            return new MXAppCompatDelegateImplV14(context, window, callback);
        }
        if (sdk >= 11) {
            return new MXAppCompatDelegateImplV11(context, window, callback);
        }
        return new MXAppCompatDelegateImplV7(context, window, callback);
    }
}
