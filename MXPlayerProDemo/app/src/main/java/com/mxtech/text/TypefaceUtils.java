package com.mxtech.text;

import android.graphics.Typeface;
import java.io.File;

/* loaded from: classes2.dex */
public class TypefaceUtils {
    public static Typeface createTypeface(String name, int style) throws RuntimeException {
        if (name == null || name.length() == 0) {
            return Typeface.defaultFromStyle(style);
        }
        if (name.charAt(0) != File.separatorChar) {
            if (style == 0) {
                if (name.equals("monospace")) {
                    return Typeface.MONOSPACE;
                }
                if (name.equals("sans-serif")) {
                    return Typeface.SANS_SERIF;
                }
                if (name.equals("serif")) {
                    return Typeface.SERIF;
                }
            }
            return Typeface.create(name, style);
        }
        return Typeface.createFromFile(name);
    }
}
