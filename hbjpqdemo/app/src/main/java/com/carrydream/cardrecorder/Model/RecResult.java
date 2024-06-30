package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class RecResult {
    private String filePath;
    private String resultLabel;
    private int startX;

    public RecResult(String str, int i, String str2) {
        this.resultLabel = str;
        this.startX = i;
        this.filePath = str2;
    }

    public String getResultLabel() {
        return this.resultLabel;
    }

    public void setResultLabel(String str) {
        this.resultLabel = str;
    }

    public int getStartX() {
        return this.startX;
    }

    public void setStartX(int i) {
        this.startX = i;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String toString() {
        return "RecResult{resultLabel='" + this.resultLabel + "', startX=" + this.startX + ", filePath='" + this.filePath + "'}";
    }
}
