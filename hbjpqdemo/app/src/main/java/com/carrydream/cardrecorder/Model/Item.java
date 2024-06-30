package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class Item {
    private boolean enable;
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean z) {
        this.enable = z;
    }

    public Item(String str, boolean z) {
        this.name = str;
        this.enable = z;
    }

    public Item(String str) {
        this.name = str;
        this.enable = true;
    }
}
