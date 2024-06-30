package com.mxtech.videoplayer.pro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.mxtech.license.LicenseManager;
import com.mxtech.videoplayer.L;

/* loaded from: classes.dex */
public final class App extends com.mxtech.videoplayer.App {
    static final String KEY_DIRECT_LICENSED = "direct_manage";
    static final String KEY_GPS_LICENSED = "gps_manage";
    static final String KEY_LICENSED = "managed";
    static final int REQUEST_GET_ACCOUNTS = 2;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.App, com.mxtech.app.MXApplication
    public void onInit() {
        super.onInit();
        L.init(this, 100);
    }

    @Override // com.mxtech.videoplayer.App, com.mxtech.app.MXApplication
    public boolean onInitInteractive(Activity caller) {
        if (!super.onInitInteractive(caller)) {
            return false;
        }
        Licensor.check(this);
        return true;
    }

    @Override // com.mxtech.videoplayer.App
    public boolean isDirectLicensed() {
        return prefs.getBoolean(KEY_DIRECT_LICENSED, false);
    }

    @Override // com.mxtech.videoplayer.App
    public Boolean isLicenseVerified() {
        return Boolean.valueOf(Licensor.isLicenseVerified());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(18)
    public static boolean isLicenseVerifiedFromOtherProfile() {
        Bundle restrictions;
        UserManager userManager = (UserManager) context.getSystemService(com.mxtech.videoplayer.ActivityScreen.kEndByUser);
        return (userManager == null || (restrictions = userManager.getApplicationRestrictions(context.getPackageName())) == null || !restrictions.containsKey(KEY_LICENSED)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needRequestGetAccountsPermission() {
        String[] strArr;
        if (Build.VERSION.SDK_INT < 23 || Licensor.isLicenseVerified() || LicenseManager.isCheckingLicense()) {
            return false;
        }
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), 4096);
            if (info.requestedPermissions != null) {
                for (String permission : info.requestedPermissions) {
                    if ("android.permission.GET_ACCOUNTS".equals(permission)) {
                        return checkSelfPermission("android.permission.GET_ACCOUNTS") == -1;
                    }
                }
                return false;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }
}
