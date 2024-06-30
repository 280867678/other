package com.mxtech.videoplayer.preference;

import com.mxtech.videoplayer.APP;

public class P {

    public static int getColorFormat() {
        return parseColorFormat(APP.prefs.getString("color_format", "rgb565"));
    }


    private static int parseColorFormat(String format) {
        if ("rgb565".equals(format)) {
            return 4;
        }
        if ("rgbx8888".equals(format)) {
            return 2;
        }
        return 842094169;
    }

}
