package com.mxtech.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.MXAppCompatDelegate;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.mxtech.app.PopupMenuHack;
import com.mxtech.os.Model;
import com.mxtech.widget.MXAutoCompleteTextView;
import com.mxtech.widget.MXMultiAutoCompleteTextView;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class MXAppCompatActivity extends AppCompatActivity implements PopupMenuHack.OptionsItemSelector, DialogPlatform {
    public static final String TAG = "MX.AppCompatActivity";
    private boolean _needToRecreate;
    private PopupMenuHack _popupMenuHack;
    private boolean _recreating;
    public final DialogRegistry dialogRegistry = new DialogRegistry();
    protected boolean foreground;
    private AppCompatDelegate mDelegate;
    protected boolean started;
    Themifier themifier;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistry.onCreated(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        this.started = true;
        ActivityRegistry.onStarted(this);
        super.onStart();
        if (this._needToRecreate) {
            recreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this.foreground = true;
        ActivityRegistry.onResumed(this);
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.foreground = false;
        ActivityRegistry.onPaused(this);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        this.started = false;
        ActivityRegistry.onStopped(this);
        if (this._popupMenuHack != null) {
            this._popupMenuHack.onActivityStop();
        }
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ActivityRegistry.onDestroyed(this);
        this.dialogRegistry.dismissAll();
    }

    @Override // android.app.Activity
    public void recreate() {
        if (!this._recreating) {
            this._needToRecreate = false;
            if (Build.VERSION.SDK_INT >= 11) {
                super.recreate();
                this._recreating = true;
            } else if (this.started) {
                finish();
                overridePendingTransition(0, 0);
                try {
                    Context app = getApplicationContext();
                    Intent i = new Intent("android.intent.action.VIEW", null, app, AppUtils.findActivityKindOf(app, getClass()));
                    i.addFlags(65536);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    this._recreating = true;
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            } else {
                this._needToRecreate = true;
            }
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        updatePopupMenuHack();
    }

    @Override // android.support.v7.app.AppCompatActivity
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        updatePopupMenuHack();
        AppCompatUtils.applyOverscanMargin(this);
    }

    private void updatePopupMenuHack() {
        if (Build.VERSION.SDK_INT == 17) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (this._popupMenuHack == null) {
                    this._popupMenuHack = new PopupMenuHack(this, this.dialogRegistry);
                    return;
                }
                return;
            }
            this._popupMenuHack = null;
        }
    }

    @Override // android.app.Activity
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (this._popupMenuHack == null || !this._popupMenuHack.onOptionsItemSelected(item)) {
            return onOptionsItemSelected2(item);
        }
        return true;
    }

    @Override // com.mxtech.app.PopupMenuHack.OptionsItemSelector
    public boolean onOptionsItemSelected2(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public final void colorizeMenuIcons(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            AppCompatUtils.colorizeMenuIcons(actionBar.getThemedContext(), menu);
        }
    }

    public final void colorizeDrawables(View view) {
        AppCompatUtils.colorizeDrawables(view.getContext(), view);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (workaround_LGMenuBug(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (workaround_LGMenuBug(keyCode)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public static boolean workaround_LGMenuBug(int keyCode) {
        if (keyCode == 82) {
            if (Model.manufacturer == 10060 && Build.VERSION.SDK_INT <= 16) {
                return true;
            }
            if (Model.manufacturer == 10000 && Build.VERSION.SDK_INT <= 10) {
                return true;
            }
        }
        return false;
    }

    @Override // com.mxtech.app.DialogPlatform
    public Context getContext() {
        return this;
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
    public <T extends Dialog> T showDialog(T dlg, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener) {
        dialogRegistry.register(dlg);
        if (onDismissListener != null) {
            dlg.setOnDismissListener(onDismissListener);
        }
        dlg.show();
        return dlg;
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(int resId) {
        showSimpleDialogMessage(getString(resId), this.dialogRegistry, this.dialogRegistry);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(CharSequence text) {
        showSimpleDialogMessage(text, this.dialogRegistry, this.dialogRegistry);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(int resId, DialogInterface.OnDismissListener onDismissListener) {
        showSimpleDialogMessage(getString(resId), this.dialogRegistry, onDismissListener);
    }

    @Override // com.mxtech.app.DialogPlatform
    public final void showSimpleDialogMessage(CharSequence text, DialogInterface.OnDismissListener onDismissListener) {
        showSimpleDialogMessage(text, this.dialogRegistry, onDismissListener);
    }

    @Override // com.mxtech.app.DialogPlatform
    public void showSimpleDialogMessage(CharSequence text, DialogRegistry dialogRegistry, DialogInterface.OnDismissListener onDismissListener) {
        showDialog(new AlertDialog.Builder(this).setMessage(text).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create(), dialogRegistry, onDismissListener);
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity, android.view.LayoutInflater.Factory
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View result = super.onCreateView(name, context, attrs);
        if (result == null) {
            char c = 65535;
            switch (name.hashCode()) {
                case -1946472170:
                    if (name.equals("RatingBar")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -1455429095:
                    if (name.equals("CheckedTextView")) {
                        c = 7;
                        break;
                    }
                    break;
                case -1346021293:
                    if (name.equals("MultiAutoCompleteTextView")) {
                        c = 2;
                        break;
                    }
                    break;
                case -938935918:
                    if (name.equals("TextView")) {
                        c = 0;
                        break;
                    }
                    break;
                case -339785223:
                    if (name.equals("Spinner")) {
                        c = 4;
                        break;
                    }
                    break;
                case 776382189:
                    if (name.equals("RadioButton")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1413872058:
                    if (name.equals("AutoCompleteTextView")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1601505219:
                    if (name.equals("CheckBox")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1666676343:
                    if (name.equals("EditText")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return new AppCompatTextView(this, attrs);
                case 1:
                    return new MXAutoCompleteTextView(this, attrs);
                case 2:
                    return new MXMultiAutoCompleteTextView(this, attrs);
                case 3:
                    return new AppCompatEditText(this, attrs);
                case 4:
                    return new AppCompatSpinner(this, attrs);
                case 5:
                    return new AppCompatCheckBox(this, attrs);
                case 6:
                    return new AppCompatRadioButton(this, attrs);
                case 7:
                    return new AppCompatCheckedTextView(this, attrs);
                case '\b':
                    return new AppCompatRatingBar(this, attrs);
                default:
                    return null;
            }
        }
        return result;
    }

    @Override // android.support.v7.app.AppCompatActivity
    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = MXAppCompatDelegate.create(this, this);
        }
        return this.mDelegate;
    }

    @Nullable
    public final Uri[] getExtraURIs(String key) {
        return AppUtils.getExtraURIs(getIntent(), key);
    }
}
