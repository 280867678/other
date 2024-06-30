package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class Platform {
    GameConfig config;
    private int id;
    private String platform;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public GameConfig getConfig() {
        return this.config;
    }

    public void setConfig(GameConfig gameConfig) {
        this.config = gameConfig;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public String toString() {
        return "Platform{id=" + this.id + ", config=" + this.config + ", platform='" + this.platform + "'}";
    }
}
