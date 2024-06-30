package org.xmlrpc.android;

/* loaded from: classes2.dex */
public class XMLRPCFault extends XMLRPCException {
    private static final long serialVersionUID = 5676562456612956519L;
    private int faultCode;
    private String faultString;

    public XMLRPCFault(String faultString, int faultCode) {
        super("XMLRPC Fault: " + faultString + " [code " + faultCode + "]");
        this.faultString = faultString;
        this.faultCode = faultCode;
    }

    public String getFaultString() {
        return this.faultString;
    }

    public int getFaultCode() {
        return this.faultCode;
    }
}
