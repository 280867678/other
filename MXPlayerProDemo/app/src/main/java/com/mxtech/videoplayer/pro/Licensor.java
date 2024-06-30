package com.mxtech.videoplayer.pro;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.license.ILicenseVerifier;
import com.mxtech.license.LicenseManager;
import com.mxtech.license.MXVerifier;
import com.mxtech.videoplayer.ActivityVPBase;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Licensor implements ILicenseVerifier.IListener {
    Licensor() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void check(Context context) {
        if (!LicenseManager.isCheckingLicense()) {
            LicenseManager.checkLicense(context, new Licensor());
        }
    }

    @Override // com.mxtech.license.ILicenseVerifier.IListener
    public void onLicensed(ILicenseVerifier verifier) {
        verifier.close();
        boolean direct = verifier instanceof MXVerifier;
        SharedPreferences.Editor editor = App.prefs.edit();
        if (direct) {
            editor.putBoolean("direct_manage", true);
            editor.remove("gps_manage");
        } else {
            editor.putBoolean("gps_manage", true);
            editor.remove("direct_manage");
        }
        editor.apply();
    }

    @Override // com.mxtech.license.ILicenseVerifier.IListener
    public void onLicenseCheckFailed(ILicenseVerifier verifier, int reason, int extra) {
        int stringId;
        verifier.close();
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.remove("gps_manage");
        editor.remove("direct_manage");
        editor.apply();
        if (Build.VERSION.SDK_INT < 18 || !App.isLicenseVerifiedFromOtherProfile()) {
            if (Build.VERSION.SDK_INT >= 23 && ((App) App.context).needRequestGetAccountsPermission()) {
                Activity activity = ActivityRegistry.getForegroundActivity();
                if (activity instanceof ActivityVPBase) {
                    ((ActivityVPBase) activity).showSnackbar();
                }
            } else if (reason == -100) {
                ActivityRegistry.finishAllActivities();
                Activity fore = ActivityRegistry.getForegroundActivity();
                if (fore != null) {
                    switch (reason) {
                        case ILicenseVerifier.E_APPLICATION /* -101 */:
                            stringId = R.string.lic_appid_format;
                            break;
                        case -100:
                            stringId = R.string.lic_deny_open_market;
                            break;
                        default:
                            stringId = R.string.lic_general;
                            break;
                    }
                    String text = App.context.getString(stringId);
                    if (extra != 0) {
                        text = text + " (" + extra + ')';
                    }
                    if (reason == -100) {
                        ((App) App.context).openBuyPageReadMore(fore, App.context.getPackageName(), text, App.context.getString(R.string.lic_open_purchase_page), true);
                    } else {
                        ((App) App.context).openBuyPageReadMore(fore, App.context.getPackageName(), text, null, true);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLicenseVerified() {
        if (App.native_initialized) {
            return LicenseManager.isLicenseVerified(App.context) || (Build.VERSION.SDK_INT >= 18 && App.isLicenseVerifiedFromOtherProfile());
        }
        return false;
    }
}
