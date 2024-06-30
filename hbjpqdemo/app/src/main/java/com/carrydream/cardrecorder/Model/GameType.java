package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class GameType {
    private boolean is_success;
    private String name;

    public GameType(String str, boolean z) {
        this.name = str;
        this.is_success = z;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isIs_success() {
        return this.is_success;
    }

    public void setIs_success(boolean z) {
        this.is_success = z;
    }
}
