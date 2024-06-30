package com.mxtech.license;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import com.mxtech.IOUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.license.ILicenseVerifier;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.ActivityScreen;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public final class MXVerifier extends AsyncTask2<Void, Void, Integer> implements ILicenseVerifier {
    public static final int CODE = 2;
    private Cipher _cipher;
    private final Context _context;
    private IvParameterSpec _iv;
    private Key _key;
    private final ILicenseVerifier.IListener _listener;
    private static String PASSWORD = "com.google.appengine.tools.development.DevAppEngineWebAppContext disableTransportGuarantee";
    private static byte[] IV_PARAM = {95, 48, 86, 100, 117, 51, 94, 66, 84, 71, 64, 95, 60, 75, 99, -9};
    private static int SALT_LENGTH = 256;
    private static int GARBAGE_LENGTH = 256;

    public MXVerifier(Context context, ILicenseVerifier.IListener listener) {
        this._context = context;
        this._listener = listener;
        executeParallel(new Void[0]);
    }

    @Override // com.mxtech.license.ILicenseVerifier
    public long getLastVerifyTime() {
        return AppUtils.getVerifiedTime(2);
    }

    @Override // com.mxtech.license.ILicenseVerifier
    public void close() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Integer doInBackground(Void... params) {
        return Integer.valueOf(verify());
    }

    private int verify() {
        int result = verify_l();
        AppUtils.updateMXLicenseResult(result);
        return result;
    }

    private int verify_l() {
        String server = null;
        String content = null;
        boolean secure = false;
        try {
            try {
                if (Build.VERSION.SDK_INT >= 8) {
                    try {
                        String plain = createContent(true);
                        content = encryptContent(plain);
                        server = "http://p.mxplayer.j2inter.com/auth2";
                        secure = true;
                    } catch (AccountsException e) {
                        return -100;
                    } catch (Throwable th) {
                    }
                }
                if (content == null) {
                    content = createContent(false);
                    server = "http://p.mxplayer.j2inter.com/auth";
                    secure = false;
                }
                try {
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    try {
                        conn.setDoOutput(true);
                        OutputStream out = conn.getOutputStream();
                        out.write(content.getBytes());
                        out.close();
                        int httpRespCode = conn.getResponseCode();
                        if (httpRespCode != 200) {
                            return ILicenseVerifier.E_NETWORK;
                        }
                        byte[] buffer = new byte[1024];
                        InputStream in = conn.getInputStream();
                        int bytes = IOUtils.readToEnd(in, buffer);
                        String result = new String(buffer, 0, bytes);
                        int parseResult = parseResult(result, secure);
                        in.close();
                        return parseResult;
                    } finally {
                        conn.disconnect();
                    }
                } catch (IOException e2) {
                    return ILicenseVerifier.E_NETWORK;
                } catch (Throwable th2) {
                    return -1;
                }
            } catch (AccountsException e3) {
                return -100;
            }
        } finally {
            clearCipher();
        }
    }

    @SuppressLint({"InlinedApi", "TrulyRandom"})
    private String encryptContent(String content) throws Exception {
        byte[] salt = new SecureRandom().generateSeed(SALT_LENGTH);
        byte[] password = PASSWORD.getBytes();
        byte[] saltCombined = new byte[salt.length + password.length];
        System.arraycopy(salt, 0, saltCombined, 0, salt.length);
        System.arraycopy(password, 0, saltCombined, salt.length, password.length);
        MessageDigest keyGen = MessageDigest.getInstance("SHA-1");
        keyGen.update(salt);
        keyGen.update(PASSWORD.getBytes());
        this._key = new SecretKeySpec(keyGen.digest(), 0, 16, "AES");
        this._iv = new IvParameterSpec(IV_PARAM);
        this._cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this._cipher.init(1, this._key, this._iv);
        byte[] encrypted = this._cipher.doFinal(content.getBytes());
        byte[] combined = new byte[salt.length + encrypted.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(encrypted, 0, combined, salt.length, encrypted.length);
        return Base64.encodeToString(combined, 2);
    }

    @SuppressLint({"NewApi"})
    private int parseResult(String result, boolean secure) throws Exception {
        if (secure) {
            try {
                byte[] raw = Base64.decode(result, 0);
                if (raw.length <= GARBAGE_LENGTH) {
                    return -100;
                }
                this._cipher.init(2, this._key, this._iv);
                byte[] decrypted = this._cipher.doFinal(raw, GARBAGE_LENGTH, raw.length - GARBAGE_LENGTH);
                result = new String(decrypted);
            } catch (Exception e) {
                return -100;
            }
        }
        return Integer.parseInt(result) == 0 ? 0 : -100;
    }

    private void clearCipher() {
        this._key = null;
        this._cipher = null;
        this._iv = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x00b5, code lost:
        r4.append(r14).append(';');
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00be, code lost:
        r10 = r10 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private String createContent(boolean secure) throws AccountsException {
        AccountManager manager = AccountManager.get(this._context);
        Account[] accs = manager.getAccounts();
        int numAccounts = 0;
        StringBuilder b = new StringBuilder();
        if (!secure) {
            b.append("acc=");
        }
        for (Account acc : accs) {
            if (acc.name.indexOf(64) > 0) {
                b.append(acc.name).append(';');
                numAccounts++;
            }
        }
        try {
            PackageManager pm = this._context.getPackageManager();
            String myPkgName = this._context.getPackageName();
            ApplicationInfo extInfo = pm.getApplicationInfo("com.mxtech.lproxy", 128);
            PackageInfo myPkgInfo = pm.getPackageInfo(myPkgName, 64);
            PackageInfo extPkgInfo = pm.getPackageInfo("com.mxtech.lproxy", 64);
            if (myPkgInfo.sharedUserId.equals(extPkgInfo.sharedUserId) && myPkgInfo.signatures[0].equals(extPkgInfo.signatures[0])) {
                String user = extInfo.metaData.getString(ActivityScreen.kEndByUser);
                String targetsText = extInfo.metaData.getString("target");
                String[] split = targetsText.split(";");
                int length = split.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String target = split[i];
                    if (myPkgName.equals(target)) {
                        break;
                    }
                    i++;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (numAccounts == 0) {
            throw new AccountsException();
        }
        if (secure) {
            b.append('\n');
        } else {
            b.append("&aid=");
        }
        String ANDROID_ID = Settings.Secure.getString(this._context.getContentResolver(), "android_id");
        if (ANDROID_ID == null || ANDROID_ID.length() == 0) {
            b.append("@null");
        } else {
            b.append(ANDROID_ID);
        }
        return b.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Integer result) {
        if (result.intValue() == 0) {
            this._listener.onLicensed(this);
        } else {
            this._listener.onLicenseCheckFailed(this, result.intValue(), 0);
        }
    }
}
