package com.mxtech;

public class ViewUtils {


    public static CharSequence toSystemUiString(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0) != 0) {
            sb.append("VISIBLE ");
        }
        if ((flags & 4) != 0) {
            sb.append("FULLSCREEN ");
        }
        if ((flags & 1) != 0) {
            sb.append("LOW_PROFILE ");
        }
        if ((flags & 2) != 0) {
            sb.append("HIDE_NAVIGATION ");
        }
        if ((flags & 2048) != 0) {
            sb.append("IMMERSIVE ");
        }
        if ((flags & 4096) != 0) {
            sb.append("IMMERSIVE_STICKY ");
        }
        if ((flags & 1024) != 0) {
            sb.append("LAYOUT_FULLSCREEN ");
        }
        if ((flags & 512) != 0) {
            sb.append("LAYOUT_HIDE_NAVIGATION ");
        }
        if ((flags & 256) != 0) {
            sb.append("LAYOUT_STABLE ");
        }
        return sb;
    }




}
