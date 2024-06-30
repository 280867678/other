package com.carrydream.cardrecorder.Model;

import org.opencv.core.Rect;

/* loaded from: classes.dex */
public class IRect {
    private boolean is_right;
    private String name;
    private Rect rect;

    public IRect(String str, Rect rect) {
        this.name = str;
        this.rect = rect;
        this.is_right = false;
    }

    public IRect(String str, boolean z, Rect rect) {
        this.name = str;
        this.is_right = z;
        this.rect = rect;
    }

    public String getName() {
        return this.name;
    }

    public boolean isIs_right() {
        return this.is_right;
    }

    public void setIs_right(boolean z) {
        this.is_right = z;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Rect getRect() {
        return this.rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String toString() {
        return "IRect{name='" + this.name + "', is_right=" + this.is_right + ", rect=" + this.rect + '}';
    }
}
