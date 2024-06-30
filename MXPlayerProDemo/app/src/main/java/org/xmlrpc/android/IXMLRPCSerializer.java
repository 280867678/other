package org.xmlrpc.android;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes2.dex */
public interface IXMLRPCSerializer {
    public static final String DATETIME_FORMAT = "yyyyMMdd'T'HH:mm:ss";
    public static final String TAG_DATA = "data";
    public static final String TAG_MEMBER = "member";
    public static final String TAG_NAME = "name";
    public static final String TAG_VALUE = "value";
    public static final String TYPE_ARRAY = "array";
    public static final String TYPE_BASE64 = "base64";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_DATE_TIME_ISO8601 = "dateTime.iso8601";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_I4 = "i4";
    public static final String TYPE_I8 = "i8";
    public static final String TYPE_INT = "int";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_STRUCT = "struct";

    Object deserialize(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException;

    void serialize(XmlSerializer xmlSerializer, Object obj) throws IOException;
}
