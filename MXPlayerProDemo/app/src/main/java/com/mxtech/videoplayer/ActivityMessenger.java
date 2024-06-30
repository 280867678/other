package com.mxtech.videoplayer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXAppCompatActivity;

/* loaded from: classes.dex */
public final class ActivityMessenger extends ActivityThemed implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private static final String EXTRA_FAIL_MESSAGE = "fail_message";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_PACKAGE_URIS = "package_uris";
    private static final String EXTRA_READMORE_URL = "readmore_url";
    private static final String EXTRA_TITLE = "title";
    public static final String TAG_READMORE = "{read_more}";

    public static void openUri(Activity caller, CharSequence title, CharSequence message, @Nullable String readMoreUrl, String[] packageUris, String failMessage) {
        Intent i = new Intent(caller, ActivityMessenger.class);
        if (title != null) {
            i.putExtra("title", title);
        }
        i.putExtra(EXTRA_MESSAGE, message);
        if (readMoreUrl != null) {
            i.putExtra(EXTRA_READMORE_URL, readMoreUrl);
        }
        i.putExtra(EXTRA_PACKAGE_URIS, packageUris);
        i.putExtra(EXTRA_FAIL_MESSAGE, failMessage);
        try {
            caller.startActivity(i);
        } catch (Exception e) {
            Log.e(MXAppCompatActivity.TAG, "", e);
        }
    }

    public static void showMessage(Activity caller, int messageId) {
        showMessage(caller, caller.getString(messageId), (CharSequence) null);
    }

    public static void showMessage(Activity caller, int messageId, CharSequence title) {
        showMessage(caller, caller.getString(messageId), title);
    }

    public static void showMessage(Activity caller, CharSequence message) {
        showMessage(caller, message, (CharSequence) null);
    }

    public static void showMessage(Activity caller, CharSequence message, CharSequence title) {
        Intent i = new Intent(caller, ActivityMessenger.class);
        if (title != null) {
            i.putExtra("title", title);
        }
        i.putExtra(EXTRA_MESSAGE, message);
        try {
            caller.startActivity(i);
        } catch (Exception e) {
            Log.e(MXAppCompatActivity.TAG, "", e);
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    protected void onCreate(Bundle saved) {
        String s;
        int readMoreIndex;
        super.onCreate(saved, 0);
        if (!isFinishing()) {
            Intent i = getIntent();
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            boolean needLikify = false;
            CharSequence title = i.getCharSequenceExtra("title");
            if (title == null) {
                title = getTitle();
            }
            b.setTitle(title);
            CharSequence message = i.getCharSequenceExtra(EXTRA_MESSAGE);
            String readMoreUrl = i.getStringExtra(EXTRA_READMORE_URL);
            if (readMoreUrl != null && (readMoreIndex = (s = message.toString()).indexOf(TAG_READMORE)) >= 0) {
                SpannableStringBuilder ssb = new SpannableStringBuilder(s);
                String readMore = getString(R.string.read_more);
                ssb.replace(readMoreIndex, TAG_READMORE.length() + readMoreIndex, (CharSequence) readMore);
                ssb.setSpan(new URLSpan(readMoreUrl), readMoreIndex, readMore.length() + readMoreIndex, 33);
                message = ssb;
                needLikify = true;
            }
            b.setMessage(message);
            if (i.hasExtra(EXTRA_PACKAGE_URIS)) {
                b.setPositiveButton(17039370, this);
                b.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            } else {
                b.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            }
            AlertDialog dialog = (AlertDialog) showDialog((ActivityMessenger) b.create(), (DialogInterface.OnDismissListener) this);
            if (needLikify) {
                View messageView = dialog.findViewById(16908299);
                if (messageView instanceof TextView) {
                    ((TextView) messageView).setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        String[] stringArrayExtra;
        if (!isFinishing()) {
            Intent activityIntent = getIntent();
            Intent i = new Intent("android.intent.action.VIEW");
            for (String uriString : activityIntent.getStringArrayExtra(EXTRA_PACKAGE_URIS)) {
                try {
                    i.setData(Uri.parse(uriString));
                    startActivity(i);
                    finish();
                    return;
                } catch (Exception e) {
                }
            }
            DialogUtils.alertAndFinish(this, activityIntent.getStringExtra(EXTRA_FAIL_MESSAGE));
            dialog.dismiss();
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this.dialogRegistry.unregister(dialog);
        if (this.dialogRegistry.size() == 0) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (!ActivityRegistry.hasActivity()) {
            System.exit(0);
        }
    }
}
