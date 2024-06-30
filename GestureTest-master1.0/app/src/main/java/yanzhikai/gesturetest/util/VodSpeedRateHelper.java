package yanzhikai.gesturetest.util;

import android.content.Context;
import android.text.TextUtils;

import yanzhikai.gesturetest.R;
import yanzhikai.gesturetest.popup.VodSpeedRate;


/* renamed from: com.xunlei.downloadprovider.download.player.vip.speedrate.d */
/* loaded from: classes3.dex */
public final class VodSpeedRateHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static String m33435a(Context context, VodSpeedRate vodSpeedRate) {
        String valueOf = vodSpeedRate == null ? "" : String.valueOf(vodSpeedRate.getRateValue());
        return !TextUtils.isEmpty(valueOf) ? context.getResources().getString(R.string.vod_play_speed_change_toast, valueOf) : "";
    }

    /* renamed from: a */
    public static int m33433a(VodSpeedRate vodSpeedRate) {
        if (vodSpeedRate == null) {
            vodSpeedRate = VodSpeedRate.getDefaultRate();
        }
        return (int) (vodSpeedRate.getRateValue() * 100.0f);
    }

    /* renamed from: a */
//    public static void m33434a(VodPlayerController vodPlayerController, VodSpeedRate vodSpeedRate) {
//        if (vodPlayerController == null || vodSpeedRate == null) {
//            return;
//        }
//        vodPlayerController.m35238a(104, String.valueOf(m33433a(vodSpeedRate)), true);
//    }
}
