package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class GameConfig {
    private int gray;
    private int grayscale;
    private double ratio;

    public GameConfig(int i, double d) {
        this.grayscale = i;
        this.ratio = d;
    }

    public int getGrayscale() {
        return this.grayscale;
    }

    public void setGrayscale(int i) {
        this.grayscale = i;
    }

    public double getRatio() {
        return this.ratio;
    }

    public void setRatio(double d) {
        this.ratio = d;
    }

    public int getGray() {
        return this.gray;
    }

    public void setGray(int i) {
        this.gray = i;
    }
}
