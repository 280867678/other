package com.mxtech.videoplayer.pro;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.util.ArrayList;

@TargetApi(18)
/* loaded from: classes.dex */
public final class RestrictionReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 18) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean directLicensed = prefs.getBoolean("direct_manage", false);
            boolean gpsLicensed = prefs.getBoolean("gps_manage", false);
            ArrayList<RestrictionEntry> list = new ArrayList<>();
            if (directLicensed || gpsLicensed) {
                RestrictionEntry restriction = new RestrictionEntry("managed", true);
                restriction.setType(0);
                list.add(restriction);
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("android.intent.extra.restrictions_list", list);
            setResultExtras(bundle);
        }
    }
}
