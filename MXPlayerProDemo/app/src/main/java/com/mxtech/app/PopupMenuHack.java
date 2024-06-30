package com.mxtech.app;

import android.view.MenuItem;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class PopupMenuHack {
    static final String TAG = "MX.PopupMenuHack";
    private final DialogRegistry _dialogRegistry;
    private final OptionsItemSelector _selector;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OptionsItemSelector {
        boolean onOptionsItemSelected2(MenuItem menuItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PopupMenuHack(OptionsItemSelector selector, DialogRegistry dialogRegistry) {
        this._selector = selector;
        this._dialogRegistry = dialogRegistry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onOptionsItemSelected(MenuItem item) {
        this._selector.onOptionsItemSelected2(item);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onActivityStop() {
        this._dialogRegistry.dismissAll();
    }
}
