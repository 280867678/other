package com.mxtech.videoplayer.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.mxtech.DeviceUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.preference.ColorPickerPreference;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.ColorPickerDialog;
import java.util.Arrays;

/* loaded from: classes2.dex */
public final class ListThemePreference extends ColorPickerPreference implements CompoundButton.OnCheckedChangeListener {
    private NameColorPair[] _nameColorMap;
    private CheckBox _optColorizeStatusBar;
    private CheckBox _optLastMediaItalicTypeface;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class NameColorPair {
        final int[] color;
        final String name;

        /* JADX INFO: Access modifiers changed from: package-private */
        public NameColorPair(String name, int colorUp) {
            this(name, colorUp, 0);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public NameColorPair(String name, int colorUp, int colorDown) {
            this.name = name;
            this.color = new int[]{colorUp, colorDown};
        }
    }

    public ListThemePreference(Context context) {
        super(context);
    }

    public ListThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListThemePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // com.mxtech.preference.ColorPickerPreference
    protected void onDialogCreated(ColorPickerDialog dialog) {
        ViewGroup footer = dialog.getFooterHolder();
        View layout = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.opt_colorize_status_bar, footer);
        this._optLastMediaItalicTypeface = (CheckBox) layout.findViewById(R.id.last_media_italic_typeface);
        this._optLastMediaItalicTypeface.setChecked((P.list_last_media_typeface & 2) != 0);
        this._optLastMediaItalicTypeface.setOnCheckedChangeListener(this);
        this._optColorizeStatusBar = (CheckBox) layout.findViewById(R.id.colorize_notification_bar);
        if (Build.VERSION.SDK_INT < 21 || DeviceUtils.isTV) {
            this._optColorizeStatusBar.setVisibility(8);
            return;
        }
        this._optColorizeStatusBar.setChecked(App.prefs.getBoolean(Key.LIST_COLORIZE_NOTIFICATION_BAR, false));
        this._optColorizeStatusBar.setOnCheckedChangeListener(this);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = App.prefs.edit();
        if (buttonView == this._optColorizeStatusBar) {
            editor.putBoolean(Key.LIST_COLORIZE_NOTIFICATION_BAR, isChecked);
        } else if (buttonView == this._optLastMediaItalicTypeface) {
            if (isChecked) {
                P.list_last_media_typeface |= 2;
            } else {
                P.list_last_media_typeface &= -3;
            }
            editor.putInt(Key.LIST_LAST_MEDIA_TYPEFACE, P.list_last_media_typeface);
        }
        AppUtils.apply(editor);
    }

    private NameColorPair[] getNameColorMapping() {
        if (this._nameColorMap == null) {
            this._nameColorMap = getAllThemes();
        }
        return this._nameColorMap;
    }

    @Override // com.mxtech.preference.ColorPickerPreference
    protected int[] parseColor(String value) {
        return parseColor(getNameColorMapping(), value);
    }

    @Override // com.mxtech.preference.ColorPickerPreference
    protected String toColorString(int[] color) {
        return toColorString(getNameColorMapping(), color);
    }

    static int[] parseColor(NameColorPair[] mapping, String value) {
        for (NameColorPair pair : mapping) {
            if (pair.name.equals(value)) {
                return pair.color;
            }
        }
        return DEFAULT_COLOR;
    }

    static String toColorString(NameColorPair[] mapping, int[] color) {
        for (NameColorPair pair : mapping) {
            if (Arrays.equals(pair.color, color)) {
                return pair.name;
            }
        }
        return "white";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    static NameColorPair[] getAllThemes() {
        boolean z;
        int upColor;
        int downColor;
        String[] names = App.context.getResources().getStringArray(R.array.list_theme_values);
        NameColorPair[] map = new NameColorPair[names.length];
        for (int i = 0; i < names.length; i++) {
            TypedArray a = App.context.obtainStyledAttributes(P.getThemeStyleId(names[i]), R.styleable.ListThemePreference);
            String str = names[i];
            switch (str.hashCode()) {
                case -1852469876:
                    if (str.equals("dark_gray")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 93818879:
                    if (str.equals("black")) {
                        z = false;
                        break;
                    }
                    z = true;
                    break;
                default:
                    z = true;
                    break;
            }
            switch (z) {
                case false:
                    downColor = a.getColor(R.styleable.ListThemePreference_android_colorBackground, 0);
                    upColor = downColor;
                    break;
                case true:
                    upColor = a.getColor(R.styleable.ListThemePreference_colorPrimary, -1);
                    downColor = a.getColor(R.styleable.ListThemePreference_android_colorBackground, 0);
                    break;
                default:
                    if (a.getColor(R.styleable.ListThemePreference_android_colorBackground, 0) == -16777216) {
                        upColor = a.getColor(R.styleable.ListThemePreference_colorAccent, 0);
                        downColor = ViewCompat.MEASURED_STATE_MASK;
                        break;
                    } else {
                        downColor = a.getColor(R.styleable.ListThemePreference_colorPrimary, -1);
                        upColor = downColor;
                        break;
                    }
            }
            map[i] = new NameColorPair(names[i], upColor, downColor);
            a.recycle();
        }
        return map;
    }
}
