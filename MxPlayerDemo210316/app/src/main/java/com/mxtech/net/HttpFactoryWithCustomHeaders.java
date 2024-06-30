package com.mxtech.net;

import android.net.Uri;
//import android.support.annotation.Nullable;

import androidx.annotation.Nullable;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class HttpFactoryWithCustomHeaders extends HttpFactory {
    private final Map<String, String> _headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    public HttpFactoryWithCustomHeaders() {
    }

    public HttpFactoryWithCustomHeaders(Map<String, String> headers) {
        this._headers.putAll(headers);
    }

    public HttpFactoryWithCustomHeaders(String userAgent) {
        this._headers.put("User-Agent", userAgent);
    }

    public synchronized void addHeader(String key, String value) {
        this._headers.put(key, value);
    }

    public synchronized void clearHeaders() {
        this._headers.clear();
    }

    @Override // com.mxtech.net.HttpFactory
    @Nullable
    public synchronized Map<String, String> getHttpHeaders(Uri uri) {
        Map<String, String> headers;
        headers = super.getHttpHeaders(uri);
        if (this._headers.size() > 0) {
            if (headers == null) {
                headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            }
            headers.putAll(this._headers);
        }
        return headers;
    }

    @Override // com.mxtech.net.HttpFactory
    protected void setHeaders(HttpURLConnection conn) {
        for (Map.Entry<String, String> entry : this._headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
