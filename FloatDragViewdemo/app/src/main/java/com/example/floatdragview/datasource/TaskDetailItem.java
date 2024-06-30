package com.example.floatdragview.datasource;

import com.example.floatdragview.bin.DownloadTaskInfo;

public final class TaskDetailItem {

    /* renamed from: a */
    public final int f19668a;

    /* renamed from: b */
    public long f19669b;

    /* renamed from: c */
    public Object f19670c;

    /* renamed from: d */
    public DownloadTaskInfo f19671d;

    public TaskDetailItem(int i, DownloadTaskInfo downloadTaskInfo, Object obj, long j) {
        this.f19669b = -1L;
        this.f19668a = i;
        this.f19670c = obj;
        this.f19669b = j;
        this.f19671d = downloadTaskInfo;
    }

    /* renamed from: a */
    public final <T> T m9364a(Class<T> cls) {
        if (this.f19670c == null) {
            return null;
        }
        try {
            return cls.cast(this.f19670c);
        } catch (ClassCastException unused) {
            return null;
        }
    }
}
