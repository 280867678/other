package com.mxtech.net;

import java.io.IOException;

/* loaded from: classes.dex */
public class HttpServerException extends IOException {
    public final int statusCode;

    public HttpServerException(int statusCode) {
        super("HTTP request failed with " + statusCode);
        this.statusCode = statusCode;
    }

    public HttpServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
