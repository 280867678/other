package com.mxtech.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.widget.EditText;
import com.mxtech.DeviceUtils;

/* loaded from: classes.dex */
public final class DialogUtils {

    /* loaded from: classes2.dex */
    public static class FinishOwnerOnDismiss implements DialogInterface.OnDismissListener {
        private final DialogRegistry registry;

        public FinishOwnerOnDismiss(DialogRegistry registry) {
            this.registry = registry;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this.registry != null) {
                this.registry.unregister(dialog);
            }
            if (dialog instanceof Dialog) {
                Dialog dlg = (Dialog) dialog;
                Activity owner = dlg.getOwnerActivity();
                if (owner != null) {
                    owner.finish();
                }
            }
        }
    }

    public static AlertDialog alert(Activity activity, CharSequence text) {
        return alert(activity, text, (CharSequence) null);
    }

    public static AlertDialog alert(Activity activity, int textId, int titleId) {
        return alert(activity, activity.getString(textId), activity.getString(titleId));
    }

    public static AlertDialog alert(Context context, CharSequence text) {
        return alert(context, text, (CharSequence) null);
    }

    public static AlertDialog alert(Context context, int textId) {
        return alert(context, context.getString(textId), (CharSequence) null);
    }

    public static AlertDialog alert(Context context, int textId, int titleId) {
        return alert(context, context.getString(textId), context.getString(titleId));
    }

    public static AlertDialog alert(Context context, CharSequence text, CharSequence title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(text);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(true);
        DialogRegistry registry = DialogRegistry.registryOf(context);
        if (registry != null) {
            dlg.setOnDismissListener(registry);
            registry.register(dlg);
        }
        dlg.show();
        return dlg;
    }

    public static AlertDialog alertAndFinish(Activity activity, int messageResId) {
        return alertAndFinish(activity, activity.getString(messageResId));
    }

    public static AlertDialog alertAndFinish(Activity activity, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOwnerActivity(activity);
        DialogRegistry registry = DialogRegistry.registryOf(activity);
        if (registry != null) {
            registry.register(dlg);
        }
        dlg.setOnDismissListener(new FinishOwnerOnDismiss(registry));
        dlg.show();
        return dlg;
    }

    public static AlertDialog showSimpleInputDialog(Context context, int textId, DialogInterface.OnClickListener positiveListener, int titleId) {
        return showSimpleInputDialog(context, context.getString(textId), positiveListener, titleId);
    }

    public static AlertDialog showSimpleInputDialog(Context context, CharSequence defaultText, DialogInterface.OnClickListener positiveListener, int titleId) {
        AlertDialog dlg = new AlertDialog.Builder(context).setTitle(titleId).setPositiveButton(17039370, positiveListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        int margin = (int) DeviceUtils.DIPToPixel(8.0f);
        EditText edit = new AppCompatEditText(dlg.getContext());
        edit.setSingleLine();
        edit.setImeOptions(2);
        edit.setText(defaultText);
        edit.setSelectAllOnFocus(true);
        edit.setId(16908291);
        dlg.setCanceledOnTouchOutside(true);
        DialogRegistry registry = DialogRegistry.registryOf(context);
        if (registry != null) {
            registry.register(dlg);
            dlg.setOnDismissListener(registry);
        }
        dlg.setView(edit, margin, margin, margin, margin);
        dlg.show();
        return dlg;
    }
}
