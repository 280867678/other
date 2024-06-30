package com.mxtech.app;

import android.app.Dialog;
import android.content.DialogInterface;

/* loaded from: classes2.dex */
public abstract class AbsDialogPlatform implements DialogPlatform {
    protected final DialogRegistry dialogRegistry;

    public AbsDialogPlatform() {
        this.dialogRegistry = new DialogRegistry();
    }

    public AbsDialogPlatform(DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
    }

    @Override // com.mxtech.app.DialogPlatform
    public final DialogRegistry getDialogRegistry() {
        return this.dialogRegistry;
    }

    @Override // com.mxtech.app.DialogPlatform
    public final <T extends Dialog> T showDialog(T dlg) {
        return (T) showDialog(dlg, this.dialogRegistry, this.dialogRegistry);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final <T extends Dialog> T showDialog(T dlg, DialogInterface.OnDismissListener onDismissListener) {
        return (T) showDialog(dlg, this.dialogRegistry, onDismissListener);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(int resId) {
        showSimpleDialogMessage(getContext().getString(resId), this.dialogRegistry, this.dialogRegistry);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(int resId, DialogInterface.OnDismissListener onDismissListener) {
        showSimpleDialogMessage(getContext().getString(resId), this.dialogRegistry, onDismissListener);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(CharSequence text) {
        showSimpleDialogMessage(text, this.dialogRegistry, this.dialogRegistry);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(CharSequence text, DialogInterface.OnDismissListener onDismissListener) {
        showSimpleDialogMessage(text, this.dialogRegistry, onDismissListener);
    }
}
