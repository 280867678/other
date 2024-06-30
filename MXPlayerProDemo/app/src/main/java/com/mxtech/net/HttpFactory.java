package com.mxtech.net;

import android.annotation.TargetApi;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
//import com.google.android.gms.fitness.FitnessStatusCodes;
//import com.google.android.gms.wallet.WalletConstants;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@TargetApi(8)
/* loaded from: classes.dex */
public class HttpFactory {
    private static final int FLAG_HEAD_ONLY = 1;
    private static final int MAX_REDIRECTION_DEPTH = 2;
    private static final String[] METHOD_GET = {"GET"};
    private static final String[] METHOD_HEAD = {"HEAD", "GET"};
    private static final String TAG = "MX.HttpFactory";
    private static Set<String> _headMethodNotSupportedHosts;
    private static HttpFactory _singleton;

    public static synchronized HttpFactory getInstance() {
        HttpFactory httpFactory;
        synchronized (HttpFactory.class) {
            if (_singleton == null) {
                _singleton = new HttpFactory();
            }
            httpFactory = _singleton;
        }
        return httpFactory;
    }

    private static String getHostPort(URL url) {
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = url.getDefaultPort();
        }
        return host + ":" + port;
    }

    private static synchronized void setHeadMethodNotSupported(URL url) {
        synchronized (HttpFactory.class) {
            if (_headMethodNotSupportedHosts == null) {
                _headMethodNotSupportedHosts = new TreeSet(String.CASE_INSENSITIVE_ORDER);
            }
            _headMethodNotSupportedHosts.add(getHostPort(url));
        }
    }

    private static synchronized boolean isHeadMethodSupported(URL url) {
        boolean z = true;
        synchronized (HttpFactory.class) {
            if (_headMethodNotSupportedHosts != null) {
                if (_headMethodNotSupportedHosts.contains(getHostPort(url))) {
                    z = false;
                }
            }
        }
        return z;
    }

    @Nullable
    public Map<String, String> getHttpHeaders(Uri uri) {
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            headers.put("Authorization", NetUtils.getBasicAuthorization(userInfo));
            return headers;
        }
        return null;
    }

    protected void setHeaders(HttpURLConnection conn) {
    }

    public final HttpURLConnection openGetConnection(String urlStr) throws IOException {
        return doOpenGetConnection(urlStr, null, null, 0, 0);
    }

    public final HttpURLConnection openHeadConnection(String urlStr) throws IOException {
        return doOpenGetConnection(urlStr, null, null, 0, 1);
    }

    public final HttpURLConnection openGetConnection(String urlStr, @Nullable Map<String, String> headers) throws IOException {
        return doOpenGetConnection(urlStr, headers, null, 0, 0);
    }

    private HttpURLConnection doOpenGetConnection(String urlStr, @Nullable Map<String, String> headers, @Nullable String cookies, int redirectionDepth, int flags) throws IOException {
        String[] requestMethods;
        HttpURLConnection connToReturn;
        int status = 0;
        URL url = new URL(urlStr);
        if ((flags & 1) == 0) {
            requestMethods = METHOD_GET;
        } else if (isHeadMethodSupported(url)) {
            requestMethods = METHOD_HEAD;
        } else {
            requestMethods = METHOD_GET;
            flags &= -2;
        }
        for (String method : requestMethods) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true);
                String userInfo = url.getUserInfo();
                if (userInfo != null) {
                    conn.setRequestProperty("Authorization", NetUtils.getBasicAuthorization(userInfo));
                }
                setHeaders(conn);
                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (cookies != null) {
                    conn.setRequestProperty("Cookie", cookies);
                }
                conn.setRequestMethod(method);
                status = conn.getResponseCode();
                if (status < 400 && (flags & 1) != 0 && !"HEAD".equals(method)) {
                    setHeadMethodNotSupported(url);
                }
                switch (status) {
                    case 200:
                    case 201:
                    case 202:
                    case 203:
                    case 204:
                    case 205:
                    case 206:
                        connToReturn = conn;
                        conn = null;
                        return connToReturn;
                    case 301:
                    case 302:
                    case 303:
                    case 307:
                    case 308:
                        if (redirectionDepth >= 2) {
                            throw new HttpServerException(status, "Too many redirections (" + (redirectionDepth + 1) + ") occurred.");
                        }
                        String newUrl = conn.getHeaderField("Location");
                        String newCookies = conn.getHeaderField("Set-Cookie");
                        if (newUrl == null) {
                            throw new HttpServerException(status, "Returned redirection status code " + status + " but `Location` is not speficied.");
                        }
                        Log.d(TAG, "Redirecting " + url + " --> " + newUrl);
                        conn.disconnect();
                        HttpURLConnection conn2 = null;
                        connToReturn = doOpenGetConnection(newUrl, headers, newCookies, redirectionDepth + 1, flags);
                        if (0 != 0) {
                            conn2.disconnect();
                        }
                        return connToReturn;
                    case 401:
                    case 403:
                    case 404 /* 404 */:
                        if ((flags & 1) != 0 && !"HEAD".equals(method)) {
                            setHeadMethodNotSupported(url);
                        }
                        throw new HttpServerException(status);
                    default:
                        if (conn != null) {
                            conn.disconnect();
                        }
                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        throw new HttpServerException(status);
    }
}
