package com.mxtech;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
//import android.os.Base64Utils;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import com.mxtech.app.MXApplication;
import com.mxtech.videoplayer.subtitle.SubView;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;

/* loaded from: classes.dex */
public final class Misc {
    public static final double PIx2 = 6.283185307179586d;

    public static <T> int compare(T left, T right, Comparator<? super T> comp) {
        return comp == null ? ((Comparable) left).compareTo(right) : comp.compare(left, right);
    }

    public static <T> boolean inRange(T pos, T begin, T end, Comparator<? super T> comp) {
        return comp == null ? ((Comparable) begin).compareTo(pos) <= 0 && ((Comparable) end).compareTo(pos) > 0 : comp.compare(begin, pos) <= 0 && comp.compare(end, pos) > 0;
    }

    public static int getPreferenceStringToInt(SharedPreferences pref, String key, int defaultValue) {
        String val = pref.getString(key, null);
        if (val != null) {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Log.e(MXApplication.TAG, "", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static int numberOfSetBits(int i) {
        int i2 = i - ((i >> 1) & 1431655765);
        int i3 = (i2 & 858993459) + ((i2 >> 2) & 858993459);
        return ((((i3 >> 4) + i3) & 252645135) * 16843009) >> 24;
    }

    public static <T> T[] merge(T[] first, T[] second) {
        T[] result = (T[]) ((Object[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length));
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> T[] append(T[] first, T second) {
        T[] result = (T[]) ((Object[]) Array.newInstance(first.getClass().getComponentType(), first.length + 1));
        System.arraycopy(first, 0, result, 0, first.length);
        result[first.length] = second;
        return result;
    }

    public static double canonicalizeRadian(double angle) {
        int numCirculation;
        if (angle < SubView.SUBTITLE_DRAG_GAP) {
            numCirculation = (int) ((angle - 3.141592653589793d) / 6.283185307179586d);
        } else {
            numCirculation = (int) ((angle + 3.141592653589793d) / 6.283185307179586d);
        }
        return angle - (numCirculation * 6.283185307179586d);
    }

    public static CharSequence toStateString(int[] states) {
        StringBuilder sb = new StringBuilder();
        for (int state : states) {
            switch (state) {
                case 16842908:
                    sb.append("focused ");
                    break;
                case 16842909:
                    sb.append("window_focused ");
                    break;
                case 16842910:
                    sb.append("enabled ");
                    break;
                case 16842911:
                    sb.append("checkable ");
                    break;
                case 16842912:
                    sb.append("checked ");
                    break;
                case 16842913:
                    sb.append("selected ");
                    break;
                case 16842914:
                    sb.append("active ");
                    break;
                case 16842915:
                    sb.append("single ");
                    break;
                case 16842916:
                    sb.append("first ");
                    break;
                case 16842917:
                    sb.append("middle ");
                    break;
                case 16842918:
                    sb.append("last ");
                    break;
                case 16842919:
                    sb.append("pressed ");
                    break;
                case 16842920:
                    sb.append("expanded ");
                    break;
                case 16842921:
                    sb.append("empty ");
                    break;
                case 16842922:
                    sb.append("above_anchor ");
                    break;
                case 16843324:
                    sb.append("long_pressable ");
                    break;
                case 16843518:
                    sb.append("activated ");
                    break;
                case 16843547:
                    sb.append("accelerated ");
                    break;
                case 16843597:
                    sb.append("multiline ");
                    break;
                case 16843623:
                    sb.append("hovered ");
                    break;
                case 16843624:
                    sb.append("drag_can_accept ");
                    break;
                case 16843625:
                    sb.append("drag_hovered ");
                    break;
            }
        }
        return sb;
    }

    @SuppressLint({"NewApi"})
    public static byte[] decodeBase64(String data) {
        return Build.VERSION.SDK_INT < 8 ? Base64Utils.decodeBase64(data) : Base64.decode(data, 0);
    }

    public static void appendDetails(StringBuilder sb, Object object) {
        if (object != null && object.getClass().isArray()) {
            sb.append('[');
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                appendDetails(sb, Array.get(object, i));
            }
            sb.append(']');
        } else if (object instanceof Collection) {
            sb.append('[');
            boolean first = true;
            for (Object element : (Collection) object) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                appendDetails(sb, element);
            }
            sb.append(']');
        } else {
            sb.append(object);
        }
    }

    public static Uri removeFragment(Uri uri) {
        String s = uri.toString();
        int indexFragmentMark = s.indexOf(35);
        if (indexFragmentMark >= 0) {
            return Uri.parse(s.substring(0, indexFragmentMark));
        }
        return uri;
    }
}
