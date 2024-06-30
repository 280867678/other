package com.carrydream.cardrecorder.retrofit;

import android.util.Log;
import com.bumptech.glide.load.Key;
//import com.google.common.net.HttpHeaders;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

/* loaded from: classes.dex */
public class TimeHttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName(Key.STRING_CHARSET_NAME);
    private volatile Level level;
    private final Logger logger;

    /* loaded from: classes.dex */
    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    /* loaded from: classes.dex */
    public interface Logger {
        public static final Logger DEFAULT = new Logger() { // from class: com.carrydream.cardrecorder.retrofit.TimeHttpLoggingInterceptor.Logger.1
            @Override // com.carrydream.cardrecorder.retrofit.TimeHttpLoggingInterceptor.Logger
            public void log(String str) {
                Platform.get().log(4, str, null);
            }
        };

        void log(String str);
    }

    public TimeHttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public TimeHttpLoggingInterceptor(Logger logger) {
        this.level = Level.NONE;
        this.logger = logger;
    }

    public TimeHttpLoggingInterceptor setLevel(Level level) {
        Objects.requireNonNull(level, "level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        String str;
        String str2;
        Level level = this.level;
        Request request = chain.request();
        if (level == Level.NONE) {
            long nanoTime = System.nanoTime();
            Response proceed = chain.proceed(request);
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime);
            return proceed;
        }
        boolean z = level == Level.BODY;
        boolean z2 = z || level == Level.HEADERS;
        RequestBody body = request.body();
        boolean z3 = body != null;
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        StringBuilder sb = new StringBuilder();
        sb.append("--> ");
        sb.append(request.method());
        sb.append(' ');
        sb.append(request.url());
        sb.append(' ');
        sb.append(protocol);
        if (!z2 && z3) {
            body.contentLength();
        }
        if (z2) {
            if (z3) {
                if (body.contentType() != null) {
                    Log.d("httpRequest: ", "Content-Type: " + body.contentType());
                }
                if (body.contentLength() != -1) {
                    Log.d("httpRequest: ", "Content-Length: " + body.contentLength());
                }
            }
            Headers headers = request.headers();
            int size = headers.size();
            int i = 0;
            while (i < size) {
                String name = headers.name(i);
                int i2 = size;
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.d("httpRequest: ", name + ": " + headers.value(i));
                }
                i++;
                size = i2;
            }
            if (!z || !z3) {
                Log.d("httpRequest: ", "--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Log.d("httpRequest: ", "--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                Charset charset = UTF8;
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                }
                Log.d("httpRequest: ", "");
                if (isPlaintext(buffer)) {
                    Log.d("httpRequest: ", buffer.readString(charset));
                    Log.d("httpRequest: ", "--> END " + request.method() + " (" + body.contentLength() + "-byte body)");
                } else {
                    Log.d("httpRequest: ", "--> END " + request.method() + " (binary " + body.contentLength() + "-byte body omitted)");
                }
            }
        }
        long nanoTime2 = System.nanoTime();
        try {
            Response proceed2 = chain.proceed(request);
            long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime2);
            ResponseBody body2 = proceed2.body();
            long contentLength = body2.contentLength();
            if (contentLength != -1) {
                str = contentLength + "-byte";
            } else {
                str = "unknown-length";
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("<-- ");
            sb2.append(proceed2.code());
            sb2.append(' ');
            sb2.append(proceed2.message());
            sb2.append(' ');
            sb2.append(proceed2.request().url());
            sb2.append(" (");
            sb2.append(millis);
            sb2.append("ms");
            if (z2) {
                str2 = "";
            } else {
                str2 = ", " + str + " body";
            }
            sb2.append(str2);
            sb2.append(')');
            Log.d("httpRequest: ", sb2.toString());
            if (z2) {
                if (!z || !okhttp3.internal.http.HttpHeaders.hasBody(proceed2)) {
                    Log.d("httpRequest: ", "<-- END HTTP");
                } else if (bodyEncoded(proceed2.headers())) {
                    Log.d("httpRequest: ", "<-- END HTTP (encoded body omitted)");
                } else {
                    BufferedSource source = body2.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer2 = source.buffer();
                    Charset charset2 = UTF8;
                    MediaType contentType2 = body2.contentType();
                    if (contentType2 != null) {
                        try {
                            charset2 = contentType2.charset(charset2);
                        } catch (UnsupportedCharsetException unused) {
                            Log.d("httpRequest: ", "Couldn't decode the response body; charset is likely malformed.");
                            Log.d("httpRequest: ", "<-- END HTTP");
                            return proceed2;
                        }
                    }
                    if (!isPlaintext(buffer2)) {
                        Log.d("httpRequest: ", "<-- END HTTP (binary " + buffer2.size() + "-byte body omitted)");
                        return proceed2;
                    }
                    if (contentLength != 0) {
                        buffer2.clone().readString(charset2);
                    }
                    Log.d("httpRequest: ", "<-- END HTTP (" + buffer2.size() + "-byte body)");
                }
            }
            return proceed2;
        } catch (Exception e) {
            Log.d("httpRequest: ", "<-- HTTP FAILED: " + e.getMessage());
            throw e;
        }
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer buffer2 = new Buffer();
            buffer.copyTo(buffer2, 0L, buffer.size() < 64 ? buffer.size() : 64L);
            for (int i = 0; i < 16; i++) {
                if (buffer2.exhausted()) {
                    return true;
                }
                int readUtf8CodePoint = buffer2.readUtf8CodePoint();
                if (Character.isISOControl(readUtf8CodePoint) && !Character.isWhitespace(readUtf8CodePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException unused) {
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String str = headers.get("Content-Encoding");
        return (str == null || str.equalsIgnoreCase("identity")) ? false : true;
    }
}
