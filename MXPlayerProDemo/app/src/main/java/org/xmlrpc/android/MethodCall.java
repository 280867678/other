package org.xmlrpc.android;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class MethodCall {
    private static final int TOPIC = 1;
    String methodName;
    ArrayList<Object> params = new ArrayList<>();

    public String getMethodName() {
        return this.methodName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ArrayList<Object> getParams() {
        return this.params;
    }

    void setParams(ArrayList<Object> params) {
        this.params = params;
    }

    public String getTopic() {
        return (String) this.params.get(1);
    }
}
