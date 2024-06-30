package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import com.mxtech.ArrayUtils;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.widget.OrderedChooser;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class LocaleMultiSelector {
    public static final int FLAG_IGNORE_COUNTRY_COODE = 1;
    private Locale[] _allLocalesOrdered;
    private final Context _context;
    @Nullable
    private Locale[] _defaults;
    @Nullable
    private OrderedChooser _dialog;
    private int _flags;

    public LocaleMultiSelector(Context context) {
        this._context = context;
    }

    public void setFlags(int flags) {
        this._flags = flags;
    }

    public void addFlags(int flags) {
        this._flags |= flags;
    }

    public void setDefaultLocales(Locale[] locales) {
        this._defaults = locales;
    }

    public AlertDialog create() {
        TreeMap<String, Locale> localeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Locale[] allLocales = P.getNativeLocales();
        String[] allNames = P.getNativeLocaleNames();
        int numLocales = allLocales.length;
        if ((this._flags & 1) != 0) {
            ArrayList<Locale> locales = new ArrayList<>(numLocales);
            ArrayList<String> names = new ArrayList<>(numLocales);
            for (int i = 0; i < numLocales; i++) {
                Locale locale = allLocales[i];
                if (locale.toString().length() == locale.getLanguage().length()) {
                    locales.add(locale);
                    names.add(allNames[i]);
                }
            }
            numLocales = locales.size();
            allLocales = (Locale[]) locales.toArray(new Locale[numLocales]);
            allNames = (String[]) names.toArray(new String[numLocales]);
        }
        for (int i2 = 0; i2 < numLocales; i2++) {
            localeMap.put(allNames[i2], allLocales[i2]);
        }
        ArrayList<Integer> checkedItems = new ArrayList<>();
        int numLocales2 = localeMap.size();
        CharSequence[] names2 = new CharSequence[numLocales2];
        this._allLocalesOrdered = new Locale[numLocales2];
        int i3 = 0;
        for (Map.Entry<String, Locale> entry : localeMap.entrySet()) {
            Locale locale2 = entry.getValue();
            this._allLocalesOrdered[i3] = locale2;
            names2[i3] = entry.getKey();
            if (this._defaults != null) {
                Locale[] localeArr = this._defaults;
                int length = localeArr.length;
                int i4 = 0;
                while (true) {
                    if (i4 < length) {
                        Locale d = localeArr[i4];
                        if (!locale2.equals(d)) {
                            i4++;
                        } else {
                            checkedItems.add(Integer.valueOf(i3));
                            break;
                        }
                    }
                }
            }
            i3++;
        }
        this._dialog = new OrderedChooser(this._context, names2, ArrayUtils.toPrimitive(checkedItems));
        return this._dialog;
    }

    public Locale[] getSelected() {
        if (this._dialog != null) {
            ArrayList<Locale> locales = new ArrayList<>();
            for (Integer pos : this._dialog.getCheckedItems()) {
                locales.add(this._allLocalesOrdered[pos.intValue()]);
            }
            return (Locale[]) locales.toArray(new Locale[locales.size()]);
        } else if (this._defaults != null) {
            return this._defaults;
        } else {
            return new Locale[0];
        }
    }

    @Nullable
    public AlertDialog getDialog() {
        return this._dialog;
    }
}
