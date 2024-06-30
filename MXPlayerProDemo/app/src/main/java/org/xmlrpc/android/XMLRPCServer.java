package org.xmlrpc.android;

import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.Socket;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes2.dex */
public class XMLRPCServer extends XMLRPCCommon {
    private static final String NEWLINES = "\n\n";
    private static final String RESPONSE = "HTTP/1.1 200 OK\nConnection: close\nContent-Type: text/xml\nContent-Length: ";
    private XMLRPCSerializer iXMLRPCSerializer = new XMLRPCSerializer();

    @Override // org.xmlrpc.android.XMLRPCCommon
    public /* bridge */ /* synthetic */ void setSerializer(IXMLRPCSerializer iXMLRPCSerializer) {
        super.setSerializer(iXMLRPCSerializer);
    }

    public MethodCall readMethodCall(Socket socket) throws IOException, XmlPullParserException {
        MethodCall methodCall = new MethodCall();
        InputStream inputStream = socket.getInputStream();
        XmlPullParser pullParser = xmlPullParserFromSocket(inputStream);
        pullParser.nextTag();
        pullParser.require(2, null, "methodCall");
        pullParser.nextTag();
        pullParser.require(2, null, "methodName");
        methodCall.setMethodName(pullParser.nextText());
        pullParser.nextTag();
        pullParser.require(2, null, "params");
        pullParser.nextTag();
        do {
            pullParser.require(2, null, "param");
            pullParser.nextTag();
            Object param = this.iXMLRPCSerializer.deserialize(pullParser);
            methodCall.params.add(param);
            pullParser.nextTag();
            pullParser.require(3, null, "param");
            pullParser.nextTag();
        } while (!pullParser.getName().equals("params"));
        return methodCall;
    }

    XmlPullParser xmlPullParserFromSocket(InputStream socketInputStream) throws IOException, XmlPullParserException {
        String line;
        String xmlRpcText = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(socketInputStream));
        do {
            line = br.readLine();
            if (line == null) {
                break;
            }
        } while (line.length() > 0);
        while (br.ready()) {
            xmlRpcText = xmlRpcText + br.readLine();
        }
        InputStream inputStream = new ByteArrayInputStream(xmlRpcText.getBytes("UTF-8"));
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        Reader streamReader = new InputStreamReader(inputStream);
        pullParser.setInput(streamReader);
        return pullParser;
    }

    public void respond(Socket socket, Object[] params) throws IOException {
        String content = methodResponse(params);
        String response = RESPONSE + content.length() + NEWLINES + content;
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
        socket.close();
        Log.d("XMLRPC", "response:" + response);
    }

    private String methodResponse(Object[] params) throws IllegalArgumentException, IllegalStateException, IOException {
        StringWriter bodyWriter = new StringWriter();
        this.serializer.setOutput(bodyWriter);
        this.serializer.startDocument(null, null);
        this.serializer.startTag(null, "methodResponse");
        serializeParams(params);
        this.serializer.endTag(null, "methodResponse");
        this.serializer.endDocument();
        return bodyWriter.toString();
    }
}
