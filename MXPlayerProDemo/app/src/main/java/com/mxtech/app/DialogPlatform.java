package com.mxtech.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/* loaded from: classes.dex */
public interface DialogPlatform {
    Context getContext();

    DialogRegistry getDialogRegistry();

    boolean isFinishing();

    <T extends Dialog> T showDialog(T t);

    <T extends Dialog> T showDialog(T t, DialogInterface.OnDismissListener onDismissListener);

    <T extends Dialog> T showDialog(T t, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener);

    void showSimpleDialogMessage(int i);

    void showSimpleDialogMessage(int i, DialogInterface.OnDismissListener onDismissListener);

    void showSimpleDialogMessage(CharSequence charSequence);

    void showSimpleDialogMessage(CharSequence charSequence, DialogInterface.OnDismissListener onDismissListener);

    void showSimpleDialogMessage(CharSequence charSequence, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener);
}
