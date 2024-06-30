package com.mxtech.market;

import android.content.Context;

/* loaded from: classes.dex */
public interface INavigator {
    String id();

    String name(Context context);

    String productDetailUri(String str);
}
