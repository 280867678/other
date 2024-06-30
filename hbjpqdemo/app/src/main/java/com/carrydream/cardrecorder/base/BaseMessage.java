package com.carrydream.cardrecorder.base;

import android.content.Context;

/* loaded from: classes.dex */
public class BaseMessage<T> {
    private T Data;
    private int code;
    private Context context;
    private boolean fristFlag;

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BaseMessage(boolean z, int i, T t) {
        this.code = i;
        this.fristFlag = z;
        this.Data = t;
    }

    public BaseMessage(Context context, int i, T t) {
        this.code = i;
        this.context = context;
        this.Data = t;
    }

    public BaseMessage(boolean z, int i) {
        this.code = i;
        this.fristFlag = z;
    }

    public int getCode() {
        return this.code;
    }

    public boolean isFristFlag() {
        return this.fristFlag;
    }

    public void setFristFlag(boolean z) {
        this.fristFlag = z;
    }

    public void setCode(int i) {
        this.code = i;
    }

    public T getData() {
        return this.Data;
    }

    public void setData(T t) {
        this.Data = t;
    }

    public String toString() {
        return "BaseMessage{code=" + this.code + ", fristFlag=" + this.fristFlag + ", Data=" + this.Data + '}';
    }
}
