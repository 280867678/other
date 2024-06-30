package com.mask.photo;

import android.app.Activity;
import android.text.TextUtils;
import com.mask.photo.interfaces.CompressCallback;
import com.mask.photo.utils.FileUtils;
import java.io.File;
import java.util.List;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/* loaded from: classes2.dex */
public class PhotoCompress {
    private PhotoCompress() {
    }

    /* loaded from: classes2.dex */
    public static class Option {
        private final Activity activity;
        private CompressCallback callback;
        private File dirFile;
        private int minSize;
        private List pathList;

        private Option(Activity activity) {
            this.minSize = 1024;
            this.activity = activity;
            this.dirFile = FileUtils.getCachePhotoCompressDir(activity);
        }

        public Option sourcePath(List list) {
            this.pathList = list;
            return this;
        }

        public Option minSize(int i) {
            this.minSize = i;
            return this;
        }

        public void start(CompressCallback compressCallback) {
            this.callback = compressCallback;
            start();
        }

        public void start() {
            Luban.with(this.activity).load(this.pathList).ignoreBy(this.minSize).setTargetDir(this.dirFile.getAbsolutePath()).setFocusAlpha(true).filter(new CompressionPredicate() { // from class: com.mask.photo.PhotoCompress.Option.3
                public boolean apply(String str) {
                    return (TextUtils.isEmpty(str) || str.toLowerCase().endsWith(".gif")) ? false : true;
                }
            }).setRenameListener(new OnRenameListener() { // from class: com.mask.photo.PhotoCompress.Option.2
                public String rename(String str) {
                    String str2;
                    String name = new File(str).getName();
                    int lastIndexOf = name.lastIndexOf(com.huantansheng.easyphotos.utils.file.FileUtils.HIDDEN_PREFIX);
                    if (lastIndexOf > 0) {
                        str2 = name.substring(lastIndexOf);
                        name = name.substring(0, lastIndexOf);
                    } else {
                        str2 = ".png";
                    }
                    return FileUtils.getDateName(name + "_compress") + str2;
                }
            }).setCompressListener(new OnCompressListener() { // from class: com.mask.photo.PhotoCompress.Option.1
                public void onStart() {
                    Option.this.dirFile.mkdirs();
                    if (Option.this.callback != null) {
                        Option.this.callback.onStart();
                    }
                }

                public void onSuccess(File file) {
                    if (Option.this.callback != null) {
                        Option.this.callback.onSuccess(file);
                    }
                }

                public void onError(Throwable th) {
                    if (Option.this.callback != null) {
                        Option.this.callback.onFail(th);
                    }
                }
            }).launch();
        }
    }

    public static Option with(Activity activity) {
        return new Option(activity);
    }
}
