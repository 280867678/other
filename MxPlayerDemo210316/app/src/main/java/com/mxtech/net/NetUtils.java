package com.mxtech.net;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public final class NetUtils {
    private static SSLSocketFactory _allTrustSocketFactory;

    public static InetAddress parseNumericAddress(String numericAddress) throws NoSuchMethodException, InvocationTargetException, Exception {
        return (InetAddress) InetAddress.class.getMethod("parseNumericAddress", String.class).invoke(null, numericAddress);
    }

    public static boolean isLocalAddress(String host) throws UnknownHostException {
        if (host == null || host.length() == 0 || "localhost".equals(host)) {
            return true;
        }
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                InetAddress addr = parseNumericAddress(host);
                if (!addr.isLoopbackAddress()) {
                    if (!addr.isSiteLocalAddress()) {
                        return false;
                    }
                }
                return true;
            }
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof IllegalArgumentException) {
                return false;
            }
        } catch (Exception e3) {
        }
        for (int i = host.length() - 1; i >= 0; i--) {
            char ch = host.charAt(i);
            if (ch != '.' && ('0' > ch || ch > '9')) {
                return false;
            }
        }
        String[] nums = host.split("\\.");
        if (nums.length == 4) {
            byte[] bytes = new byte[4];
            for (int i2 = 0; i2 < 4; i2++) {
                try {
                    bytes[i2] = (byte) Integer.parseInt(nums[i2]);
                } catch (Exception e4) {
                    return false;
                }
            }
            InetAddress addr2 = InetAddress.getByAddress(null, bytes);
            if (!addr2.isLoopbackAddress()) {
                if (!addr2.isSiteLocalAddress()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @SuppressLint({"TrulyRandom"})
    public static synchronized SSLSocketFactory getAllTrustSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLSocketFactory sSLSocketFactory;
        synchronized (NetUtils.class) {
            if (_allTrustSocketFactory == null) {
                TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.mxtech.net.NetUtils.1
                    @Override // javax.net.ssl.X509TrustManager
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override // javax.net.ssl.X509TrustManager
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override // javax.net.ssl.X509TrustManager
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }
                }};
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                _allTrustSocketFactory = sc.getSocketFactory();
            }
            sSLSocketFactory = _allTrustSocketFactory;
        }
        return sSLSocketFactory;
    }

    public static String getBasicAuthorization(String userInfo) {
        return "Basic " + Base64.encodeToString(userInfo.getBytes(), 2);
    }

    public static String makePostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(URLEncoder.encode(key, "UTF-8"));
            sb.append("=");
            sb.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
        }
        return sb.toString();
    }
}
