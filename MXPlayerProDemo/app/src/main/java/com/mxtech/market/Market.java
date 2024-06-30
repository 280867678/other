package com.mxtech.market;

import android.content.Context;
import android.net.Uri;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class Market {
    public static final String MARKET_AMAZON = "amazon_appstore";
    public static final String MARKET_GOOGLE = "android_market";
    public static final String MARKET_SAMSUNG = "samsung_apps";
    private static INavigator _navigator;

    public static INavigator getNavigator(Context context) {
        if (_navigator == null) {
            String targetMarket = context.getString(R.string.target_market);
            if (targetMarket.equals(AmazonAppstoreNavigator.URL_PREFIX)) {
                _navigator = new AmazonAppstoreNavigator();
            } else {
                _navigator = new GoogleAndroidMarketNavigator();
            }
        }
        return _navigator;
    }

    public static INavigator getNavigatorFromPackageUri(Uri uri) {
        if (GoogleAndroidMarketNavigator.isBelongsTo(uri)) {
            return new GoogleAndroidMarketNavigator();
        }
        if (AmazonAppstoreNavigator.isBelongsTo(uri)) {
            return new AmazonAppstoreNavigator();
        }
        if (SamsungAppsNavigator.isBelongsTo(uri)) {
            return new SamsungAppsNavigator();
        }
        return null;
    }
}
