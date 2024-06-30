package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.widget.ListView2;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class LocaleSingleSelector implements DialogInterface.OnClickListener, View.OnKeyListener {
    public static final int FLAG_SHOW_AUTO_DETECT = 1;
    private final Context _context;
    @Nullable
    private Locale _current;
    @Nullable
    private Locale _default;
    private int _flags;
    private Locale[] _orderedLocales;

    public LocaleSingleSelector(Context context) {
        this._context = context;
    }

    public void setFlags(int flags) {
        this._flags = flags;
    }

    public void setDefaultLocale(Locale locale) {
        this._default = locale;
        this._current = locale;
    }

    public Locale getLocale() {
        return this._current;
    }

    public AlertDialog create() {
        CharSequence[] names;
        int i;
        int checked;
        TreeMap<String, Locale> localeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Locale[] allLocales = P.getNativeLocales();
        String[] allNames = P.getNativeLocaleNames();
        int numLocales = allLocales.length;
        for (int i2 = 0; i2 < numLocales; i2++) {
            localeMap.put(allNames[i2], allLocales[i2]);
        }
        boolean foundCurrent = false;
        if ((this._flags & 1) != 0) {
            names = new CharSequence[numLocales + 1];
            this._orderedLocales = new Locale[numLocales + 1];
            names[0] = this._context.getString(R.string.auto_detect);
            i = 1;
            checked = 0;
        } else {
            names = new CharSequence[numLocales];
            this._orderedLocales = new Locale[numLocales];
            i = 0;
            checked = -1;
        }
        for (Map.Entry<String, Locale> entry : localeMap.entrySet()) {
            Locale locale = entry.getValue();
            this._orderedLocales[i] = locale;
            names[i] = entry.getKey();
            if (!foundCurrent && locale.equals(this._default)) {
                checked = i;
                foundCurrent = true;
            }
            i++;
        }
        AlertDialog dialog = new AlertDialog.Builder(this._context).setSingleChoiceItems(names, checked, this).create();
        dialog.getListView().setOnKeyListener(this);
        return dialog;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == 21) {
            if (event.getAction() == 0) {
                ListView2.moveListViewToTop((ListView) v);
                return true;
            }
        } else if (keyCode == 22 && event.getAction() == 0) {
            ListView listView = (ListView) v;
            listView.setSelection(listView.getCount() - 1);
            return true;
        }
        return false;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which >= 0 && which < this._orderedLocales.length) {
            this._current = this._orderedLocales[which];
        } else {
            this._current = null;
        }
        dialog.dismiss();
    }
}
