package com.carrydream.cardrecorder.Model;

import java.util.List;

/* loaded from: classes.dex */
public class SortPoker {
    private List<String> list;
    private String name;
    private int number;

    public SortPoker(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int i) {
        this.number = i;
    }

    public List<String> getList() {
        return this.list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String toString() {
        return "SortPoker{name='" + this.name + "', number=" + this.number + '}';
    }
}
