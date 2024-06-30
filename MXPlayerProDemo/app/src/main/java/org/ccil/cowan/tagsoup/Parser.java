package org.ccil.cowan.tagsoup;

import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* loaded from: classes2.dex */
public class Parser implements XMLReader {
    public static final String schemaProperty = "http://www.ccil.org/~cowan/tagsoup/properties/schema";

    @Override // org.xml.sax.XMLReader
    public native ContentHandler getContentHandler();

    @Override // org.xml.sax.XMLReader
    public native DTDHandler getDTDHandler();

    @Override // org.xml.sax.XMLReader
    public native EntityResolver getEntityResolver();

    @Override // org.xml.sax.XMLReader
    public native ErrorHandler getErrorHandler();

    @Override // org.xml.sax.XMLReader
    public native boolean getFeature(String str) throws SAXNotRecognizedException, SAXNotSupportedException;

    @Override // org.xml.sax.XMLReader
    public native Object getProperty(String str) throws SAXNotRecognizedException, SAXNotSupportedException;

    @Override // org.xml.sax.XMLReader
    public native void parse(String str) throws IOException, SAXException;

    @Override // org.xml.sax.XMLReader
    public native void parse(InputSource inputSource) throws IOException, SAXException;

    @Override // org.xml.sax.XMLReader
    public native void setContentHandler(ContentHandler contentHandler);

    @Override // org.xml.sax.XMLReader
    public native void setDTDHandler(DTDHandler dTDHandler);

    @Override // org.xml.sax.XMLReader
    public native void setEntityResolver(EntityResolver entityResolver);

    @Override // org.xml.sax.XMLReader
    public native void setErrorHandler(ErrorHandler errorHandler);

    @Override // org.xml.sax.XMLReader
    public native void setFeature(String str, boolean z) throws SAXNotRecognizedException, SAXNotSupportedException;

    @Override // org.xml.sax.XMLReader
    public native void setProperty(String str, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException;
}
