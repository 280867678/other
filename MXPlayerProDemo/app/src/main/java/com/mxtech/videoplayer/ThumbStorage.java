package com.mxtech.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.crypto.CipherInputStream;
import com.mxtech.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class ThumbStorage {
    public static final String TAG = App.TAG + ".ThumbStorage";
    public static final BitmapFactory.Options decodingOptions = null;
    final File cacheDir;

    public ThumbStorage(Context context) {
        this.cacheDir = new File(AppUtils.getExternalCacheDir(context), "image");
    }

    public void clear() {
        File[] files = FileUtils.listFiles(this.cacheDir);
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public void delete(int fileId, long size, long lastModified) {
        getFile(fileId, size, lastModified).delete();
    }

    public void delete(int fileId, File file) {
        getFile(fileId, file).delete();
    }

    public void write(int fileId, File srcFile, Bitmap thumb) {
        write(fileId, srcFile, thumb, null);
    }

    public void write(int fileId, File srcFile, Bitmap thumb, @Nullable byte[] iobuffer) {
        File file = getFile(fileId, srcFile);
        this.cacheDir.mkdirs();
        try {
            CipherOutputStream out = iobuffer != null ? new CipherOutputStream(new FileOutputStream(file), 2, fileId, iobuffer) : new CipherOutputStream(new FileOutputStream(file), 2, fileId);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    public Bitmap read(int fileId, File srcFile) {
        return read(fileId, srcFile, null);
    }

    public Bitmap read(int fileId, File srcFile, @Nullable byte[] iobuffer) {
        File file = getFile(fileId, srcFile);
        if (file.length() == 0) {
            return null;
        }
        try {
            InputStream in = iobuffer != null ? new CipherInputStream(new FileInputStream(file), 2, fileId, iobuffer) : new CipherInputStream(new FileInputStream(file), 2, fileId);
            Bitmap thumb = BitmapFactory.decodeStream(in, null, decodingOptions);
            if (thumb != null) {
                in.close();
                return thumb;
            }
            in.close();
            InputStream in2 = new FileInputStream(file);
            Bitmap thumb2 = BitmapFactory.decodeStream(in2, null, decodingOptions);
            in2.close();
            return thumb2;
        } catch (Throwable e) {
            Log.e(TAG, "Can't load cached thumb from " + file.getPath(), e);
            return null;
        }
    }

    private File getFile(int fileId, long size, long lastModified) {
        StringBuilder b = new StringBuilder();
        b.append(fileId).append('+').append(size).append('+').append(lastModified);
        return new File(this.cacheDir, b.toString());
    }

    private File getFile(int fileId, File file) {
        return getFile(fileId, file.length(), file.lastModified());
    }
}
