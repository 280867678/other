package com.mxtech.crypto;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.Nullable;

import android.util.Base64;
import com.flurry.android.Constants;
import com.mxtech.CharUtils;
import com.mxtech.IOUtils;
import com.mxtech.NumericUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/* loaded from: classes2.dex */
public class UsernamePasswordSaver {
    private static final String CHARSET = "UTF-8";
    private static final String DIGEST_ALGORITHM = "SHA-1";
    private static final int FIELD_BYTES = 256;
    public static final int FIELD_LENGTH = 64;
    private static final int MESSAGE_LENGTH = 522;
    private static final int SALT_SIZE = 8;

    /* loaded from: classes2.dex */
    public static class Info {
        public final String password;
        public final String username;

        public Info(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @SuppressLint({"NewApi", "TrulyRandom"})
    public static String encrypt(String username, String password) {
        String bytesToHexString;
        try {
            byte[] plainText = new byte[MESSAGE_LENGTH];
            NumericUtils.ThreadLocalRandom.get().nextBytes(plainText);
            byte[] passwordBytes = password.getBytes(CHARSET);
            int passwordBytesLen = passwordBytes.length;
            plainText[8] = (byte) passwordBytesLen;
            System.arraycopy(passwordBytes, 0, plainText, 9, passwordBytesLen);
            byte[] usernameBytes = username.getBytes(CHARSET);
            int usernameBytesLen = usernameBytes.length;
            plainText[265] = (byte) usernameBytesLen;
            System.arraycopy(usernameBytes, 0, plainText, 266, usernameBytesLen);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CipherOutputStream ciperOut = new CipherOutputStream(out, 2, plainText, 0, 8, 514);
            ciperOut.write(plainText, 8, plainText.length - 8);
            ciperOut.close();
            byte[] encrypted = out.toByteArray();
            MessageDigest keyGen = MessageDigest.getInstance(DIGEST_ALGORITHM);
            keyGen.update(plainText, 0, 8);
            keyGen.update(encrypted);
            byte[] digest = keyGen.digest();
            byte[] cipherText = new byte[encrypted.length + 8 + digest.length];
            System.arraycopy(plainText, 0, cipherText, 0, 8);
            System.arraycopy(encrypted, 0, cipherText, 8, encrypted.length);
            System.arraycopy(digest, 0, cipherText, encrypted.length + 8, digest.length);
            if (Build.VERSION.SDK_INT >= 8) {
                bytesToHexString = Base64.encodeToString(cipherText, 3);
            } else {
                bytesToHexString = CharUtils.bytesToHexString(cipherText);
            }
            return bytesToHexString;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    public static Info decrypt(String text) {
        byte[] cipherText;
        try {
            if (Build.VERSION.SDK_INT >= 8) {
                cipherText = Base64.decode(text, 0);
            } else {
                cipherText = CharUtils.hexStringToBytes(text);
            }
            if (cipherText.length <= MESSAGE_LENGTH) {
                return null;
            }
            MessageDigest keyGen = MessageDigest.getInstance(DIGEST_ALGORITHM);
            keyGen.update(cipherText, 0, MESSAGE_LENGTH);
            byte[] digest = keyGen.digest();
            int digestLength = digest.length;
            if (digestLength != cipherText.length - 522) {
                return null;
            }
            int i = 0;
            int k = MESSAGE_LENGTH;
            while (i < digestLength) {
                if (digest[i] == cipherText[k]) {
                    i++;
                    k++;
                } else {
                    return null;
                }
            }
            ByteArrayInputStream in = new ByteArrayInputStream(cipherText, 8, 514);
            CipherInputStream ciperIn = new CipherInputStream(in, 2, cipherText, 0, 8, 514);
            byte[] plainText = new byte[514];
            IOUtils.readFully(ciperIn, plainText);
            ciperIn.close();
            int passwordBytesLen = plainText[0] & Constants.UNKNOWN;
            String password = new String(plainText, 1, passwordBytesLen, CHARSET);
            int usernameLen = plainText[257] & Constants.UNKNOWN;
            String username = new String(plainText, (int) 258, usernameLen, CHARSET);
            return new Info(username, password);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean equals(@Nullable Info info, @Nullable String username, @Nullable String password) {
        boolean z = false;
        if (info != null) {
            return info.username.equals(username) && info.password.equals(password);
        }
        if (username == null || username.length() == 0) {
            z = true;
        }
        return z;
    }
}
