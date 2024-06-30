package com.example.floatdragview.datasource;

import android.util.LruCache;

import com.example.floatdragview.bin.DownloadTaskInfo;

import java.util.Iterator;
import java.util.List;

public final class BtTaskDetailDataSource  {

    /* renamed from: b */
    public TaskDetailItem f19578b;

    /* renamed from: c */
    public TaskDetailItem f19579c;

    /* renamed from: d */
    public TaskDetailItem f19580d;

    /* renamed from: e */
    public TaskDetailItem f19581e;

    /* renamed from: f */
    public TaskDetailItem f19582f;

    /* renamed from: g */
    public TaskDetailItem f19583g;

    /* renamed from: h */
    public TaskSpeedCountInfo f19584h;

    /* renamed from: a */
//    final BTSubTaskLoader f19577a = new BTSubTaskLoader();

    /* renamed from: i */
//    LruCache<String, BTSubTaskItem> f19585i = new LruCache<>(100);

    /* renamed from: a */
    public final void m9436a(DownloadTaskInfo downloadTaskInfo) {
        if (this.f19578b != null) {
            this.f19578b.f19671d = downloadTaskInfo;
        }
        if (this.f19579c != null) {
            this.f19579c.f19671d = downloadTaskInfo;
        }
        if (this.f19580d != null) {
            this.f19580d.f19671d = downloadTaskInfo;
        }
//        if (this.f19536l != null && !this.f19536l.isEmpty()) {
//            Iterator<TaskDetailItem> it = this.f19536l.iterator();
//            while (it.hasNext()) {
//                it.next().f19671d = downloadTaskInfo;
//            }
//        }
        if (this.f19581e != null) {
            this.f19581e.f19671d = downloadTaskInfo;
        }
        if (this.f19582f != null) {
            this.f19582f.f19671d = downloadTaskInfo;
        }
        if (this.f19583g != null) {
            this.f19583g.f19671d = downloadTaskInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
//    public static boolean m9437a(long j, List<BTSubTaskItem> list) {
//        for (BTSubTaskItem bTSubTaskItem : list) {
//            if (bTSubTaskItem.mTaskId == j) {
//                return bTSubTaskItem.isSelected();
//            }
//        }
//        return false;
//    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static String m9438a(long j, long j2) {
        return j + "_" + j2;
    }
}

