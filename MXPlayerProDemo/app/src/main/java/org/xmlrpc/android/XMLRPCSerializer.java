package org.xmlrpc.android;

import android.util.Base64;
import com.mxtech.videoplayer.preference.Tuner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes2.dex */
class XMLRPCSerializer implements IXMLRPCSerializer {
    static SimpleDateFormat dateFormat = new SimpleDateFormat(IXMLRPCSerializer.DATETIME_FORMAT);

    @Override // org.xmlrpc.android.IXMLRPCSerializer
    public void serialize(XmlSerializer serializer, Object object) throws IOException {
        if ((object instanceof Integer) || (object instanceof Short) || (object instanceof Byte)) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_I4).text(object.toString()).endTag(null, IXMLRPCSerializer.TYPE_I4);
        } else if (object instanceof Long) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_I8).text(object.toString()).endTag(null, IXMLRPCSerializer.TYPE_I8);
        } else if ((object instanceof Double) || (object instanceof Float)) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_DOUBLE).text(object.toString()).endTag(null, IXMLRPCSerializer.TYPE_DOUBLE);
        } else if (object instanceof Boolean) {
            Boolean bool = (Boolean) object;
            String boolStr = bool.booleanValue() ? Tuner.TAG_SCREEN : Tuner.TAG_STYLE;
            serializer.startTag(null, IXMLRPCSerializer.TYPE_BOOLEAN).text(boolStr).endTag(null, IXMLRPCSerializer.TYPE_BOOLEAN);
        } else if (object instanceof String) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_STRING).text(object.toString()).endTag(null, IXMLRPCSerializer.TYPE_STRING);
        } else if ((object instanceof Date) || (object instanceof Calendar)) {
            String dateStr = dateFormat.format(object);
            serializer.startTag(null, IXMLRPCSerializer.TYPE_DATE_TIME_ISO8601).text(dateStr).endTag(null, IXMLRPCSerializer.TYPE_DATE_TIME_ISO8601);
        } else if (object instanceof byte[]) {
            String value = Base64.encodeToString((byte[]) object, 2);
            serializer.startTag(null, IXMLRPCSerializer.TYPE_BASE64).text(value).endTag(null, IXMLRPCSerializer.TYPE_BASE64);
        } else if (object instanceof List) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_ARRAY).startTag(null, IXMLRPCSerializer.TAG_DATA);
            List<Object> list = (List) object;
            for (Object o : list) {
                serializer.startTag(null, IXMLRPCSerializer.TAG_VALUE);
                serialize(serializer, o);
                serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE);
            }
            serializer.endTag(null, IXMLRPCSerializer.TAG_DATA).endTag(null, IXMLRPCSerializer.TYPE_ARRAY);
        } else if (object instanceof Object[]) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_ARRAY).startTag(null, IXMLRPCSerializer.TAG_DATA);
            Object[] objects = (Object[]) object;
            for (Object o2 : objects) {
                serializer.startTag(null, IXMLRPCSerializer.TAG_VALUE);
                serialize(serializer, o2);
                serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE);
            }
            serializer.endTag(null, IXMLRPCSerializer.TAG_DATA).endTag(null, IXMLRPCSerializer.TYPE_ARRAY);
        } else if (object instanceof Map) {
            serializer.startTag(null, IXMLRPCSerializer.TYPE_STRUCT);
            Map<String, Object> map = (Map) object;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value2 = entry.getValue();
                serializer.startTag(null, IXMLRPCSerializer.TAG_MEMBER);
                serializer.startTag(null, IXMLRPCSerializer.TAG_NAME).text(key).endTag(null, IXMLRPCSerializer.TAG_NAME);
                serializer.startTag(null, IXMLRPCSerializer.TAG_VALUE);
                serialize(serializer, value2);
                serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE);
                serializer.endTag(null, IXMLRPCSerializer.TAG_MEMBER);
            }
            serializer.endTag(null, IXMLRPCSerializer.TYPE_STRUCT);
        } else if (object instanceof XMLRPCSerializable) {
            XMLRPCSerializable serializable = (XMLRPCSerializable) object;
            serialize(serializer, serializable.getSerializable());
        } else {
            throw new IOException("Cannot serialize " + object);
        }
    }

    @Override // org.xmlrpc.android.IXMLRPCSerializer
    public Object deserialize(XmlPullParser parser) throws XmlPullParserException, IOException {
        Object text;
        parser.require(2, null, IXMLRPCSerializer.TAG_VALUE);
        if (parser.isEmptyElementTag()) {
            return "";
        }
        boolean hasType = true;
        String typeNodeName = null;
        try {
            parser.nextTag();
            typeNodeName = parser.getName();
            if (typeNodeName.equals(IXMLRPCSerializer.TAG_VALUE)) {
                if (parser.getEventType() == 3) {
                    return "";
                }
            }
        } catch (XmlPullParserException e) {
            hasType = false;
        }
        if (hasType) {
            if (typeNodeName.equals(IXMLRPCSerializer.TYPE_INT) || typeNodeName.equals(IXMLRPCSerializer.TYPE_I4)) {
                String value = parser.nextText();
                text = Integer.valueOf(Integer.parseInt(value));
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_I8)) {
                String value2 = parser.nextText();
                text = Long.valueOf(Long.parseLong(value2));
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_DOUBLE)) {
                String value3 = parser.nextText();
                text = Double.valueOf(Double.parseDouble(value3));
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_BOOLEAN)) {
                String value4 = parser.nextText();
                text = value4.equals(Tuner.TAG_SCREEN) ? Boolean.TRUE : Boolean.FALSE;
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_STRING)) {
                text = parser.nextText();
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_DATE_TIME_ISO8601)) {
                String value5 = parser.nextText();
                try {
                    text = dateFormat.parseObject(value5);
                } catch (ParseException e2) {
                    throw new IOException("Cannot deserialize dateTime " + value5);
                }
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_BASE64)) {
                String value6 = parser.nextText();
                BufferedReader reader = new BufferedReader(new StringReader(value6));
                StringBuffer sb = new StringBuffer();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                text = Base64.decode(sb.toString(), 2);
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_ARRAY)) {
                parser.nextTag();
                parser.require(2, null, IXMLRPCSerializer.TAG_DATA);
                parser.nextTag();
                List<Object> list = new ArrayList<>();
                while (parser.getName().equals(IXMLRPCSerializer.TAG_VALUE)) {
                    list.add(deserialize(parser));
                    parser.nextTag();
                }
                parser.require(3, null, IXMLRPCSerializer.TAG_DATA);
                parser.nextTag();
                parser.require(3, null, IXMLRPCSerializer.TYPE_ARRAY);
                text = list.toArray();
            } else if (typeNodeName.equals(IXMLRPCSerializer.TYPE_STRUCT)) {
                parser.nextTag();
                Map<String, Object> map = new HashMap<>();
                while (parser.getName().equals(IXMLRPCSerializer.TAG_MEMBER)) {
                    String memberName = null;
                    Object obj = null;
                    while (true) {
                        parser.nextTag();
                        String name = parser.getName();
                        if (name.equals(IXMLRPCSerializer.TAG_NAME)) {
                            memberName = parser.nextText();
                        } else if (!name.equals(IXMLRPCSerializer.TAG_VALUE)) {
                            break;
                        } else {
                            obj = deserialize(parser);
                        }
                    }
                    if (memberName != null && obj != null) {
                        map.put(memberName, obj);
                    }
                    parser.require(3, null, IXMLRPCSerializer.TAG_MEMBER);
                    parser.nextTag();
                }
                parser.require(3, null, IXMLRPCSerializer.TYPE_STRUCT);
                text = map;
            } else {
                throw new IOException("Cannot deserialize " + parser.getName());
            }
        } else {
            text = parser.getText();
        }
        parser.nextTag();
        parser.require(3, null, IXMLRPCSerializer.TAG_VALUE);
        return text;
    }
}
