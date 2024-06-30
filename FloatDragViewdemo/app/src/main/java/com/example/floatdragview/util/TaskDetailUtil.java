package com.example.floatdragview.util;

public class TaskDetailUtil {

    /* renamed from: c */
    private static TaskDetailUtil f20944c;

    /* renamed from: a */
    public boolean f20945a;

    /* renamed from: b */
    public long f20946b;

    private TaskDetailUtil() {
    }

    /* renamed from: a */
    public static TaskDetailUtil m8464a() {
        if (f20944c == null) {
            synchronized (TaskDetailUtil.class) {
                if (f20944c == null) {
                    f20944c = new TaskDetailUtil();
                }
            }
        }
        return f20944c;
    }
}
