package com.mxtech.parser;

/* loaded from: classes2.dex */
public final class TimeParser implements IParser {
    @Override // com.mxtech.parser.IParser
    public int parseToInt(String source) throws NumberFormatException {
        return parse(source);
    }

    private static int parseIntTolerable(String s) throws NumberFormatException {
        if (s.length() > 0) {
            return Integer.parseInt(s);
        }
        return 0;
    }

    public static int parse(String source) throws NumberFormatException {
        int fraction;
        int millis = 0;
        int begin = 0;
        int end = source.indexOf(58);
        if (end > 0) {
            int millis2 = 0 + parseIntTolerable(source.substring(0, end));
            millis = millis2 * 60;
            begin = end + 1;
            int end2 = source.indexOf(58, begin);
            if (end2 > 0) {
                millis = (millis + parseIntTolerable(source.substring(begin, end2))) * 60;
                begin = end2 + 1;
            }
        }
        int time_fraction = source.indexOf(46, begin);
        if (time_fraction < 0) {
            time_fraction = source.indexOf(44, begin);
        }
        if (time_fraction < 0) {
            return (millis + parseIntTolerable(source.substring(begin))) * 1000;
        }
        int millis3 = (millis + parseIntTolerable(source.substring(begin, time_fraction))) * 1000;
        String fractionText = source.substring(time_fraction + 1);
        switch (fractionText.length()) {
            case 0:
                fraction = 0;
                break;
            case 1:
                fraction = parseIntTolerable(fractionText) * 100;
                break;
            case 2:
                fraction = parseIntTolerable(fractionText) * 10;
                break;
            case 3:
                fraction = parseIntTolerable(fractionText);
                break;
            default:
                fraction = parseIntTolerable(fractionText.substring(0, 3));
                break;
        }
        return millis3 + fraction;
    }
}
