package com.mxtech.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/* loaded from: classes2.dex */
public final class DialogPlatformWrapper extends AbsDialogPlatform {
    private final DialogPlatform _platform;

    public DialogPlatformWrapper(DialogPlatform platform, DialogRegistry registry) {
        super(registry);
        this._platform = platform;
    }

    @Override // com.mxtech.app.DialogPlatform
    public Context getContext() {
        return this._platform.getContext();
    }

    @Override // com.mxtech.app.DialogPlatform
    public boolean isFinishing() {
        return this._platform.isFinishing();
    }

    @Override // com.mxtech.app.DialogPlatform
    public <T extends Dialog> T showDialog(T dlg, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener) {
        return (T) this._platform.showDialog(dlg, dialogRegistry, onDismissListener);
    }

    @Override // com.mxtech.app.DialogPlatform
    public void showSimpleDialogMessage(CharSequence text, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener) {
        this._platform.showSimpleDialogMessage(text, dialogRegistry, onDismissListener);
    }
}
