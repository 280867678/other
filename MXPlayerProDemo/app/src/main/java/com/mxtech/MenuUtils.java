package com.mxtech;

import android.support.v7.view.menu.MenuItemImpl;
import android.view.MenuItem;

/* loaded from: classes.dex */
public class MenuUtils {
    public static boolean isActionButton(MenuItem item) {
        return ((MenuItemImpl) item).isActionButton();
    }

    public static boolean isInOverflow(MenuItem item) {
        return !isActionButton(item);
    }
}
