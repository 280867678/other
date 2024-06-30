package org.xmlrpc.android;

import android.annotation.SuppressLint;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes2.dex */
public final class XMLRPCClient2 extends XMLRPCCommon implements HostnameVerifier {
    private static final String TAG = "XMLRPCClient2";
    private final HttpURLConnection _conn;
    private final String _hostname;
    private boolean _sendGZip;
    public int lastStatusCode = -1;

    @Override // org.xmlrpc.android.XMLRPCCommon
    public /* bridge */ /* synthetic */ void setSerializer(IXMLRPCSerializer iXMLRPCSerializer) {
        super.setSerializer(iXMLRPCSerializer);
    }

    @SuppressLint({"TrulyRandom"})
    public XMLRPCClient2(URL url) throws IOException {
        this._hostname = url.getHost();
        this._conn = (HttpURLConnection) url.openConnection();
        this._conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        this._conn.addRequestProperty("Accept-Encoding", "gzip");
        this._conn.setDoOutput(true);
        if (this._conn instanceof HttpsURLConnection) {
            HttpsURLConnection secureConn = (HttpsURLConnection) this._conn;
            secureConn.setHostnameVerifier(this);
        }
    }

    @Override // javax.net.ssl.HostnameVerifier
    public boolean verify(String hostname, SSLSession session) {
        Log.d(TAG, "Verify SSL hostname - " + hostname + " (genuine: " + this._hostname + ")");
        return this._hostname.equals(hostname);
    }

    public void setUserAgent(String userAgent) {
        this._conn.setRequestProperty("User-Agent", userAgent);
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this._conn.setRequestProperty("Accept-Language", acceptLanguage);
    }

    public void setSendGZipped(boolean gzipped) {
        this._sendGZip = gzipped;
    }

    public boolean isSecure() {
        return this._conn instanceof HttpsURLConnection;
    }

    public void setSSLSocketFactory(SSLSocketFactory factory) {
        if (this._conn instanceof HttpsURLConnection) {
            HttpsURLConnection secureConn = (HttpsURLConnection) this._conn;
            secureConn.setSSLSocketFactory(factory);
        }
    }

    public void setAuthentication(String user, String password) {
    }

    public Object call(String method, Object... params) throws XMLRPCException, IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {
        boolean pushZipped;
        String body = methodCall(method, params);
        byte[] bytes = body.getBytes("UTF-8");
        if (this._sendGZip && bytes.length > 200) {
            pushZipped = true;
            this._conn.setRequestProperty("Content-Encoding", "gzip");
        } else {
            pushZipped = false;
        }
        OutputStream out = this._conn.getOutputStream();
        if (pushZipped) {
            out = new GZIPOutputStream(out);
        }
        out.write(bytes);
        out.close();
        this.lastStatusCode = this._conn.getResponseCode();
        if (this.lastStatusCode >= 300) {
            throw new IOException("HTTP status code: " + this.lastStatusCode);
        }
        InputStream in = this._conn.getInputStream();
        if ("gzip".equalsIgnoreCase(this._conn.getContentEncoding())) {
            in = new GZIPInputStream(in);
        }
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        Reader reader = new InputStreamReader(in);
        try {
            pullParser.setInput(reader);
            pullParser.nextTag();
            pullParser.require(2, null, "methodResponse");
            pullParser.nextTag();
            String tag = pullParser.getName();
            if (tag.equals("params")) {
                pullParser.nextTag();
                pullParser.require(2, null, "param");
                pullParser.nextTag();
                return this.iXMLRPCSerializer.deserialize(pullParser);
            } else if (tag.equals("fault")) {
                pullParser.nextTag();
                Map<String, Object> map = (Map) this.iXMLRPCSerializer.deserialize(pullParser);
                String faultString = (String) map.get("faultString");
                int faultCode = ((Integer) map.get("faultCode")).intValue();
                throw new XMLRPCFault(faultString, faultCode);
            } else {
                throw new XMLRPCException("Bad tag <" + tag + "> in XMLRPC response - neither <params> nor <fault>");
            }
        } finally {
            reader.close();
        }
    }

    public void close() {
        this._conn.disconnect();
    }

    private String methodCall(String method, Object[] params) throws IllegalArgumentException, IllegalStateException, IOException {
        StringWriter bodyWriter = new StringWriter();
        this.serializer.setOutput(bodyWriter);
        this.serializer.startDocument(null, null);
        this.serializer.startTag(null, "methodCall");
        this.serializer.startTag(null, "methodName").text(method).endTag(null, "methodName");
        serializeParams(params);
        this.serializer.endTag(null, "methodCall");
        this.serializer.endDocument();
        return bodyWriter.toString();
    }
}
