package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class Captcha {
    private String img;
    private String key;
    private boolean sensitive;

    public boolean isSensitive() {
        return this.sensitive;
    }

    public void setSensitive(boolean z) {
        this.sensitive = z;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String str) {
        this.img = str;
    }
}
