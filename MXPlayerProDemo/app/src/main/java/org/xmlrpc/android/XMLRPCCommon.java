package org.xmlrpc.android;

import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes2.dex */
class XMLRPCCommon {
    protected XmlSerializer serializer = Xml.newSerializer();
    protected IXMLRPCSerializer iXMLRPCSerializer = new XMLRPCSerializer();

    public void setSerializer(IXMLRPCSerializer serializer) {
        this.iXMLRPCSerializer = serializer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void serializeParams(Object[] params) throws IllegalArgumentException, IllegalStateException, IOException {
        if (params != null && params.length != 0) {
            this.serializer.startTag(null, "params");
            for (Object obj : params) {
                this.serializer.startTag(null, "param").startTag(null, IXMLRPCSerializer.TAG_VALUE);
                this.iXMLRPCSerializer.serialize(this.serializer, obj);
                this.serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE).endTag(null, "param");
            }
            this.serializer.endTag(null, "params");
        }
    }
}
