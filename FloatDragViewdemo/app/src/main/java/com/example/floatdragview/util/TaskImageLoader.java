package com.example.floatdragview.util;

import android.widget.ImageView;

import com.example.floatdragview.R;
import com.example.floatdragview.bin.DownloadTaskInfo;

public class TaskImageLoader {

    private static TaskImageLoader f20895a;

    /* renamed from: a */
    public static TaskImageLoader m8492a() {
        if (f20895a == null) {
            synchronized (TaskImageLoader.class) {
                if (f20895a == null) {
                    f20895a = new TaskImageLoader();
                }
            }
        }
        return f20895a;
    }


    public static void m8484b(DownloadTaskInfo downloadTaskInfo5, ImageView imageView) {

        int fileIconResId = R.drawable.ic_dl_bt;
        imageView.setImageResource(fileIconResId);


    }
}
