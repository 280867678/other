package com.mxtech.market;

import android.content.Context;
import android.net.Uri;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class SamsungAppsNavigator implements INavigator {
    public static boolean isBelongsTo(Uri uri) {
        return "samsungapps".equals(uri.getScheme());
    }

    @Override // com.mxtech.market.INavigator
    public String name(Context context) {
        return context.getString(R.string.samsung_apps);
    }

    @Override // com.mxtech.market.INavigator
    public String id() {
        return Market.MARKET_SAMSUNG;
    }

    @Override // com.mxtech.market.INavigator
    public String productDetailUri(String packageName) {
        return "samsungapps://ProductDetail/" + Uri.encode(packageName);
    }
}
