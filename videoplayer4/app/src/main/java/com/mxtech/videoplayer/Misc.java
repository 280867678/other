package com.mxtech.videoplayer;

import com.mxtech.videoplayer.subtitle.SubView;

public class Misc {


    public static double canonicalizeRadian(double angle) {
        int numCirculation;
        if (angle < 0.0d) {
            numCirculation = (int) ((angle - 3.141592653589793d) / 6.283185307179586d);
        } else {
            numCirculation = (int) ((angle + 3.141592653589793d) / 6.283185307179586d);
        }
        return angle - (numCirculation * 6.283185307179586d);
    }




}
