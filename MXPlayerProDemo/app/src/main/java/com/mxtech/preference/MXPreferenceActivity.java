package com.mxtech.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppCompatUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.MXAppCompatActivity;
import com.mxtech.app.Themifier;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.MXAutoCompleteTextView;
import com.mxtech.widget.MXMultiAutoCompleteTextView;

/* loaded from: classes.dex */
public class MXPreferenceActivity extends PreferenceActivity implements ViewTreeObserver.OnGlobalLayoutListener, DialogPlatform {
    private static final String TAG = "MX.MXPreferenceActivity";
    private ColorFilter _accentColorFilter;
    private ColorFilter _actionBarNormalColorFilter;
    private ColorFilter _normalColorFilter;
    public DialogRegistry dialogRegistry = new DialogRegistry();
    protected boolean started;
    public Themifier themifier;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistry.onCreated(this);
    }

    @Override // android.preference.PreferenceActivity
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        cleanseDisposedPreferences(preferenceScreen);
        super.setPreferenceScreen(preferenceScreen);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStart() {
        this.started = true;
        ActivityRegistry.onStarted(this);
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        ActivityRegistry.onResumed(this);
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onPause() {
        ActivityRegistry.onPaused(this);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.PreferenceActivity, android.app.Activity
    public void onStop() {
        this.started = false;
        ActivityRegistry.onStopped(this);
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.PreferenceActivity, android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ActivityRegistry.onDestroyed(this);
        this.dialogRegistry.dismissAll();
    }

    @Override // android.preference.PreferenceActivity, android.app.ListActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        ListView listView;
        super.onContentChanged();
        if (11 <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < 21 && (listView = getListView()) != null) {
            listView.setDivider(null);
            listView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    @Nullable
    public final ColorFilter getAccentColorFilter() {
        if (this._accentColorFilter == null) {
            TypedArray a = obtainStyledAttributes(new int[]{R.attr.colorControlActivated});
            int color = a.getColor(0, 0);
            if (color != 0) {
                this._accentColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            a.recycle();
        }
        return this._accentColorFilter;
    }

    @Nullable
    public final ColorFilter getNormalColorFilter() {
        if (this._normalColorFilter == null) {
            TypedArray a = obtainStyledAttributes(new int[]{R.attr.colorControlNormal});
            int color = a.getColor(0, 0);
            if (color != 0) {
                this._normalColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            a.recycle();
        }
        return this._normalColorFilter;
    }

    @Nullable
    public final ColorFilter getActionBarNormalColorFilter() {
        if (this._actionBarNormalColorFilter == null) {
            TypedArray a = obtainStyledAttributes(new int[]{R.attr.actionBarTheme});
            int actionBarTheme = a.getResourceId(0, 0);
            a.recycle();
            if (actionBarTheme != 0) {
                TypedArray a2 = obtainStyledAttributes(actionBarTheme, new int[]{R.attr.colorControlNormal});
                int color = a2.getColor(0, 0);
                a2.recycle();
                if (color != 0) {
                    this._actionBarNormalColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }
        return this._actionBarNormalColorFilter;
    }

    public final void themifyImageViews(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Drawable d = imageView.getDrawable();
            if (d != null) {
                GraphicUtils.safeMutate(d).setColorFilter(getAccentColorFilter());
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int numChild = group.getChildCount();
            for (int i = 0; i < numChild; i++) {
                themifyImageViews(group.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        ListView listView = getListView();
        if (listView != null) {
            themifyImageViews(listView);
        }
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void setSupportActionBar(Toolbar toolbar) {
        Drawable icon;
        AppCompatUtils.applyOverscanMargin(this);
        ColorFilter cf = getActionBarNormalColorFilter();
        if (cf != null && (icon = toolbar.getNavigationIcon()) != null) {
            icon.mutate().setColorFilter(cf);
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (MXAppCompatActivity.workaround_LGMenuBug(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (MXAppCompatActivity.workaround_LGMenuBug(keyCode)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public Themifier getThemifier() {
        return this.themifier;
    }

    protected PreferenceGroup findPreferenceParent(Preference preference) {
        return findPreferenceParent(getPreferenceScreen(), preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static PreferenceGroup findPreferenceParent(PreferenceGroup root, Preference preference) {
        PreferenceGroup parent;
        int numPreferences = root.getPreferenceCount();
        for (int i = 0; i < numPreferences; i++) {
            Preference p = root.getPreference(i);
            if (p != preference) {
                if ((p instanceof PreferenceGroup) && (parent = findPreferenceParent((PreferenceGroup) p, preference)) != null) {
                    return parent;
                }
            } else {
                return root;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void cleanseDisposedPreferences(PreferenceGroup group) {
        for (int i = group.getPreferenceCount() - 1; i >= 0; i--) {
            Preference p = group.getPreference(i);
            if ((p instanceof AppCompatPreference) && ((AppCompatPreference) p).disposed) {
                group.removePreference(p);
            } else if (p instanceof PreferenceGroup) {
                cleanseDisposedPreferences((PreferenceGroup) p);
            }
        }
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

    @Override // android.app.Activity, android.view.LayoutInflater.Factory
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
}
