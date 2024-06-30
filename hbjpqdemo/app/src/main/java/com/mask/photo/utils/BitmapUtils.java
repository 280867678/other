package com.mask.photo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import androidx.exifinterface.media.ExifInterface;
//import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.mask.photo.interfaces.PuzzleCallback;
import com.mask.photo.interfaces.SaveBitmapCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class BitmapUtils {
    public static void recycle(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

//    public static Bitmap getBitmap(View view) {
//        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        view.draw(new Canvas(createBitmap));
//        return createBitmap;
//    }
//
//    public static Bitmap getBitmap(File file) {
//        ExifInterface exifInterface = null;
//        if (file == null) {
//            return null;
//        }
//        Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
//        try {
//            exifInterface = new ExifInterface(file);
//        } catch (Exception unused) {
//        }
//        return exifInterface == null ? decodeFile : adjustOrientation(decodeFile, exifInterface);
//    }

//    public static Bitmap getBitmap(Context context, Uri uri) {
//        Bitmap bitmap;
//        if (uri == null) {
//            return null;
//        }
//        try {
//            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
//            bitmap = BitmapFactory.decodeStream(openInputStream);
//            if (openInputStream != null) {
//                try {
//                    openInputStream.close();
//                } catch (Exception unused) {
//                }
//            }
//        } catch (Exception unused2) {
//            bitmap = null;
//        }
//        try {
//            InputStream openInputStream2 = context.getContentResolver().openInputStream(uri);
//            r0 = openInputStream2 != null ? new ExifInterface(openInputStream2) : null;
//            if (openInputStream2 != null) {
//                openInputStream2.close();
//            }
//        } catch (Exception unused3) {
//        }
//        return r0 == null ? bitmap : adjustOrientation(bitmap, r0);
//    }

//    public static Bitmap adjustOrientation(Bitmap bitmap, ExifInterface exifInterface) {
//        if (exifInterface == null) {
//            return bitmap;
//        }
//        int i = 0;
//        switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)) {
//            case 3:
//            case 4:
//                i = SubsamplingScaleImageView.ORIENTATION_180;
//                break;
//            case 5:
//            case 6:
//                i = 90;
//                break;
//            case 7:
//            case 8:
//                i = SubsamplingScaleImageView.ORIENTATION_270;
//                break;
//        }
//        if (i == 0) {
//            return bitmap;
//        }
//        Matrix matrix = new Matrix();
//        matrix.setRotate(i);
//        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        recycle(bitmap);
//        return createBitmap;
//    }

    public static void saveBitmapToFile(final Activity activity, final Bitmap bitmap, final String str, final SaveBitmapCallback saveBitmapCallback) {
        if (bitmap == null) {
            saveBitmapCallback.onFail(new NullPointerException("Bitmap不能为null"));
        } else {
            new Thread(new Runnable() { // from class: com.mask.photo.utils.BitmapUtils.1
                @Override // java.lang.Runnable
                public void run() {
                    File cachePhotoDir = FileUtils.getCachePhotoDir(activity);
                    cachePhotoDir.mkdirs();
                    final File file = new File(cachePhotoDir, FileUtils.getDateName(str) + ".png");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        activity.runOnUiThread(new Runnable() { // from class: com.mask.photo.utils.BitmapUtils.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                saveBitmapCallback.onSuccess(file);
                            }
                        });
                        fileOutputStream.close();
                    } catch (Exception e) {
                        activity.runOnUiThread(new Runnable() { // from class: com.mask.photo.utils.BitmapUtils.1.2
                            @Override // java.lang.Runnable
                            public void run() {
                                saveBitmapCallback.onFail(e);
                            }
                        });
                    }
                }
            }).start();
        }
    }

//    public static void addWatermark(Bitmap bitmap, float f, float f2, String str, int i, float f3, float f4, float f5, boolean z) {
//        Rect rect;
//        if (bitmap == null) {
//            return;
//        }
//        float width = bitmap.getWidth();
//        float height = bitmap.getHeight();
//        if (width <= 0.0f || height <= 0.0f || TextUtils.isEmpty(str)) {
//            return;
//        }
//        float min = Math.min(width / f, height / f2);
//        float f6 = f3 * min;
//        float f7 = f4 * min;
//        float f8 = f5 * min;
//        Canvas canvas = new Canvas(bitmap);
//        TextPaint textPaint = new TextPaint();
//        textPaint.setAntiAlias(true);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setTextSize(f6);
//        textPaint.setColor(i);
//        textPaint.getTextBounds(str, 0, str.length(), new Rect());
//        if (z) {
//            textPaint.setTextAlign(Paint.Align.LEFT);
//        } else {
//            textPaint.setTextAlign(Paint.Align.RIGHT);
//            f7 = width - f7;
//        }
//        canvas.drawText(str, f7, ((height - f8) - rect.height()) - rect.top, textPaint);
//    }
//
//    public static void puzzleFile(final List<File> list, final PuzzleCallback puzzleCallback) {
//        if (list == null || list.isEmpty()) {
//            return;
//        }
//        new Thread(new Runnable() { // from class: com.mask.photo.utils.BitmapUtils.2
//            @Override // java.lang.Runnable
//            public void run() {
//                ArrayList arrayList = new ArrayList();
//                for (File file : list) {
//                    Bitmap bitmap = BitmapUtils.getBitmap(file);
//                    if (bitmap != null) {
//                        arrayList.add(bitmap);
//                    }
//                }
//                puzzleCallback.onSuccess(BitmapUtils.puzzleBitmap(arrayList));
//            }
//        }).start();
//    }
//
//    public static void puzzleUri(final Context context, final List<Uri> list, final PuzzleCallback puzzleCallback) {
//        if (list == null || list.isEmpty()) {
//            return;
//        }
//        new Thread(new Runnable() { // from class: com.mask.photo.utils.BitmapUtils.3
//            @Override // java.lang.Runnable
//            public void run() {
//                ArrayList arrayList = new ArrayList();
//                for (Uri uri : list) {
//                    Bitmap bitmap = BitmapUtils.getBitmap(context, uri);
//                    if (bitmap != null) {
//                        arrayList.add(bitmap);
//                    }
//                }
//                puzzleCallback.onSuccess(BitmapUtils.puzzleBitmap(arrayList));
//            }
//        }).start();
//    }

//    public static Bitmap puzzleBitmap(List<Bitmap> list) {
//        if (list == null || list.isEmpty()) {
//            return null;
//        }
//        int i = 0;
//        int i2 = 0;
//        int i3 = 0;
//        for (Bitmap bitmap : list) {
//            i2 = Math.max(i2, bitmap.getWidth());
//            i3 += bitmap.getHeight();
//        }
//        Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(createBitmap);
//        for (Bitmap bitmap2 : list) {
//            int width = bitmap2.getWidth();
//            int height = bitmap2.getHeight();
//            canvas.drawBitmap(bitmap2, (i2 - width) / 2.0f, i, (Paint) null);
//            i += height;
//        }
//        return createBitmap;
//    }
}
