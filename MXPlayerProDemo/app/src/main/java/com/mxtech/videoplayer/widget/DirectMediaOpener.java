package com.mxtech.videoplayer.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.app.AppUtils;
import com.mxtech.app.MXAppCompatActivity;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;

@TargetApi(11)
/* loaded from: classes.dex */
public class DirectMediaOpener implements DialogInterface.OnClickListener, TextView.OnEditorActionListener {
    private final MXAppCompatActivity _activity;
    private final AlertDialog _dialog;
    private final PersistentTextView _input;

    @SuppressLint({"InflateParams"})
    public DirectMediaOpener(MXAppCompatActivity activity) {
        String scheme;
        ClipData.Item item;
        CharSequence text;
        this._activity = activity;
        this._dialog = new AlertDialog.Builder(activity).setTitle(R.string.open_url).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).create();
        View layout = this._dialog.getLayoutInflater().inflate(R.layout.open_url, (ViewGroup) null);
        this._input = (PersistentTextView) layout.findViewById(16908291);
        TextViewUtils.makeClearable((ViewGroup) this._input.getParent(), this._input, (ImageView) layout.findViewById(R.id.clear_btn));
        this._input.setOnEditorActionListener(this);
        Uri uri = null;
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService("clipboard");
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0 && (uri = (item = clip.getItemAt(0)).getUri()) == null && (text = item.getText()) != null) {
            uri = Uri.parse(text.toString());
        }
        if (uri != null && (scheme = uri.getScheme()) != null && P.isSupportedProtocol(scheme)) {
            this._input.setText(uri.toString());
        }
        if (this._input.length() == 0) {
            this._input.restoreLastText();
        }
        this._dialog.setView(layout);
        activity.showDialog((MXAppCompatActivity) this._dialog);
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == 2 || actionId == 0) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            onClick(this._dialog, -1);
            this._dialog.dismiss();
            return true;
        }
        return false;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        Dialog dlg = (Dialog) dialog;
        TextView textView = (TextView) dlg.findViewById(16908291);
        String text = textView.getText().toString().trim();
        if (text.length() != 0) {
            this._input.save();
            if (text.indexOf("://") < 0) {
                text = "http://" + text;
            }
            open(Uri.parse(text));
        }
    }

    protected void open(Uri uri) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", uri, this._activity, AppUtils.findActivityKindOf(this._activity, ActivityScreen.class));
            this._activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
    }
}
