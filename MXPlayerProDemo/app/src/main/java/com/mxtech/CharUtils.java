package com.mxtech;

import com.flurry.android.Constants;

/* loaded from: classes2.dex */
public final class CharUtils {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static final boolean isHexaDigit(char ch) {
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return true;
            default:
                return false;
        }
    }

    public static final int parseHexaDigit(char ch) throws NumberFormatException {
        switch (ch) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
            default:
                throw new NumberFormatException();
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        int numBytes = bytes.length;
        char[] hexChars = new char[numBytes * 2];
        for (int j = 0; j < numBytes; j++) {
            int v = bytes[j] & Constants.UNKNOWN;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[(j * 2) + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToBytes(String string) throws NumberFormatException {
        char[] chars = string.toCharArray();
        int numChars = chars.length;
        if (numChars % 2 != 0) {
            throw new NumberFormatException();
        }
        byte[] bytes = new byte[numChars / 2];
        int k = 0;
        int i = 0;
        while (i < numChars) {
            int i2 = i + 1;
            int higher = parseHexaDigit(chars[i]);
            i = i2 + 1;
            int lower = parseHexaDigit(chars[i2]);
            bytes[k] = (byte) ((higher << 4) | lower);
            k++;
        }
        return bytes;
    }
}
