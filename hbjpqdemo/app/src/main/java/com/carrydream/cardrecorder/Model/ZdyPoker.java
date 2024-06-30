package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class ZdyPoker {
    private int count;
    private String name;
    private int sum;

    public ZdyPoker(String str, int i) {
        this.name = str;
        this.count = i;
        this.sum = i;
    }

    public int getSum() {
        return this.sum;
    }

    public void setSum(int i) {
        this.sum = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public String toString() {
        return "ZdyPoker{name='" + this.name + "', count=" + this.count + ", sum=" + this.sum + '}';
    }
}
