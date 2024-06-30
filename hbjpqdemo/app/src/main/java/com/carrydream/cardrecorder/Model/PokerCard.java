package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class PokerCard {
    private String color;
    private String name;

    public PokerCard(String str, String str2) {
        this.name = str;
        this.color = str2;
    }

    public String toString() {
        return "PokerCard{name='" + this.name + "', color='" + this.color + "'}";
    }
}
