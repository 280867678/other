package com.mxtech.market;

import android.content.Context;
import android.net.Uri;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class AmazonAppstoreNavigator implements INavigator {
    public static final String URL_PREFIX = "com.amazon";

    public static boolean isBelongsTo(Uri uri) {
        return "amzn".equals(uri.getScheme());
    }

    @Override // com.mxtech.market.INavigator
    public String name(Context context) {
        return context.getString(R.string.amazon_appstore);
    }

    @Override // com.mxtech.market.INavigator
    public String id() {
        return Market.MARKET_AMAZON;
    }

    @Override // com.mxtech.market.INavigator
    public String productDetailUri(String packageName) {
        return "amzn://apps/android?p=" + Uri.encode(packageName);
    }
}
