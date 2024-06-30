package com.mxtech.license;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Base64;
import com.mxtech.app.AppUtils;
import com.mxtech.app.MXApplication;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
final class GoogleUtils {
    private static final int ERROR_CONTACTING_SERVER = 257;
    private static final int ERROR_INVALID_PACKAGE_NAME = 258;
    private static final int ERROR_NON_MATCHING_UID = 259;
    private static final int ERROR_NOT_MARKET_MANAGED = 3;
    private static final int ERROR_OVER_QUOTA = 5;
    private static final int ERROR_SERVER_FAILURE = 4;
    private static final int LICENSED = 0;
    private static final int LICENSED_OLD_KEY = 2;
    private static final int NOT_LICENSED = 1;

    GoogleUtils() {
    }

    private static int translateGoogleCode(int googleCode) {
        switch (googleCode) {
            case 0:
            case 2:
                return 0;
            case 1:
                return -100;
            case 3:
            case 258:
            case 259:
                return ILicenseVerifier.E_APPLICATION;
            case 4:
            case 5:
            case 257:
                return ILicenseVerifier.E_NETWORK;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getLicenseStatus(int code, Parcel parcel, long nonce) {
        if (code != 1) {
            return -100;
        }
        int responseCode = parcel.readInt();
        if (responseCode != 0 && responseCode != 2) {
            return translateGoogleCode(responseCode);
        }
        if (responseCode != 0 && responseCode != 2) {
            return translateGoogleCode(responseCode);
        }
        String signedData = parcel.readString();
        String signature = parcel.readString();
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publicKey = AppUtils.getSD(1);
            sig.initVerify(keyFactory.generatePublic(new X509EncodedKeySpec(publicKey)));
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature, 0))) {
                return -100;
            }
            try {
                ResponseData data = ResponseData.parse(signedData);
                if (data.responseCode == responseCode && data.nonce == nonce) {
                    String packageName = MXApplication.context.getPackageName();
                    if (!data.packageName.equals(packageName)) {
                        return -100;
                    }
                    PackageInfo pi = MXApplication.context.getPackageManager().getPackageInfo(packageName, 0);
                    if (data.versionCode == pi.versionCode && !TextUtils.isEmpty(data.userId)) {
                        return translateGoogleCode(responseCode);
                    }
                    return -100;
                }
                return -100;
            } catch (PackageManager.NameNotFoundException e) {
                return -1;
            } catch (NumberFormatException e2) {
                return -100;
            } catch (IllegalArgumentException e3) {
                return -100;
            } catch (Exception e4) {
                return -1;
            }
        } catch (Exception e5) {
            return -1;
        }
    }

    /* loaded from: classes2.dex */
    private static class ResponseData {
        public String extra;
        public long nonce;
        public String packageName;
        public int responseCode;
        public long timestamp;
        public String userId;
        public int versionCode;

        private ResponseData() {
        }

        public static ResponseData parse(String responseData) throws NumberFormatException {
            String mainData;
            String extraData;
            int index = responseData.indexOf(58);
            if (-1 == index) {
                mainData = responseData;
                extraData = "";
            } else {
                mainData = responseData.substring(0, index);
                extraData = index >= responseData.length() ? "" : responseData.substring(index + 1);
            }
            String[] fields = TextUtils.split(mainData, Pattern.quote("|"));
            if (fields.length < 6) {
                throw new IllegalArgumentException("Wrong number of fields.");
            }
            ResponseData data = new ResponseData();
            data.extra = extraData;
            data.responseCode = Integer.parseInt(fields[0]);
            data.nonce = Long.parseLong(fields[1]);
            data.packageName = fields[2];
            data.versionCode = Integer.parseInt(fields[3]);
            data.userId = fields[4];
            data.timestamp = Long.parseLong(fields[5]);
            return data;
        }

        public String toString() {
            return TextUtils.join("|", new Object[]{Integer.valueOf(this.responseCode), Long.valueOf(this.nonce), this.packageName, Integer.valueOf(this.versionCode), this.userId, Long.valueOf(this.timestamp)});
        }
    }
}
