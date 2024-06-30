package com.mxtech.market;

import android.content.Context;
import android.net.Uri;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class GoogleAndroidMarketNavigator implements INavigator {
    public static boolean isBelongsTo(Uri uri) {
        return "market".equals(uri.getScheme());
    }

    @Override // com.mxtech.market.INavigator
    public String name(Context context) {
        return context.getString(R.string.android_market);
    }

    @Override // com.mxtech.market.INavigator
    public String id() {
        return Market.MARKET_GOOGLE;
    }

    @Override // com.mxtech.market.INavigator
    public String productDetailUri(String packageName) {
        return "market://details?id=" + Uri.encode(packageName);
    }
}
