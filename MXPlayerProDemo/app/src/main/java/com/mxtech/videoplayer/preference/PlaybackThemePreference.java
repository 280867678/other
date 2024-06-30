package com.mxtech.videoplayer.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import com.mxtech.preference.ColorPickerPreference;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.ListThemePreference;

/* loaded from: classes2.dex */
public final class PlaybackThemePreference extends ColorPickerPreference {
    private ListThemePreference.NameColorPair[] _nameColorMap;

    public PlaybackThemePreference(Context context) {
        super(context);
    }

    public PlaybackThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaybackThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlaybackThemePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ListThemePreference.NameColorPair[] getNameColorMapping() {
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

    static int[] parseColor(ListThemePreference.NameColorPair[] mapping, String value) {
        for (ListThemePreference.NameColorPair pair : mapping) {
            if (pair.name.equals(value)) {
                return pair.color;
            }
        }
        return DEFAULT_COLOR;
    }

    static String toColorString(ListThemePreference.NameColorPair[] mapping, int[] color) {
        for (ListThemePreference.NameColorPair pair : mapping) {
            if (pair.color[0] == color[0] && pair.color[1] == color[1]) {
                return pair.name;
            }
        }
        return "white";
    }

    static ListThemePreference.NameColorPair[] getAllThemes() {
        String[] names = App.context.getResources().getStringArray(R.array.playback_theme_values);
        ListThemePreference.NameColorPair[] map = new ListThemePreference.NameColorPair[names.length];
        for (int i = 0; i < names.length; i++) {
            String theme = names[i];
            TypedArray a = App.context.obtainStyledAttributes(P.getPlaybackThemeStyleId(theme), R.styleable.PlaybackThemePreference);
            int colorPrimary = a.getColor(R.styleable.PlaybackThemePreference_colorPrimary, 0);
            int colorAccent = a.getColor(R.styleable.PlaybackThemePreference_colorAccent, 0);
            a.recycle();
            char c = 65535;
            switch (theme.hashCode()) {
                case -1852469876:
                    if (theme.equals("dark_gray")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1852277025:
                    if (theme.equals("dark_navy")) {
                        c = 3;
                        break;
                    }
                    break;
                case 93818879:
                    if (theme.equals("black")) {
                        c = 1;
                        break;
                    }
                    break;
                case 113101865:
                    if (theme.equals("white")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                case 3:
                    map[i] = new ListThemePreference.NameColorPair(theme, colorPrimary);
                    break;
                default:
                    if (colorPrimary == -16777216) {
                        map[i] = new ListThemePreference.NameColorPair(theme, ViewCompat.MEASURED_STATE_MASK, colorAccent);
                        break;
                    } else {
                        map[i] = new ListThemePreference.NameColorPair(theme, colorAccent);
                        break;
                    }
            }
        }
        return map;
    }
}
