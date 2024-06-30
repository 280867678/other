package com.mask.mediaprojection.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.mask.mediaprojection.interfaces.MediaRecorderCallback;
import com.mask.mediaprojection.interfaces.ScreenCaptureCallback;
import com.mask.mediaprojection.utils.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/* loaded from: classes2.dex */
public class MediaProjectionService extends Service {
    private static final int ID_MEDIA_PROJECTION = 10086;
    private DisplayMetrics displayMetrics;
    private ImageReader imageReader;
    private boolean isImageAvailable;
    private boolean isMediaRecorderEnable;
    private boolean isMediaRecording;
    private boolean isScreenCaptureEnable;
    private File mediaFile;
    private MediaProjection mediaProjection;
    private MediaProjectionManager mediaProjectionManager;
    private MediaRecorder mediaRecorder;
    private MediaRecorderCallback mediaRecorderCallback;
    private MediaProjectionNotificationEngine notificationEngine;
    private VirtualDisplay virtualDisplayImageReader;
    private VirtualDisplay virtualDisplayMediaRecorder;

    /* loaded from: classes2.dex */
    public class MediaProjectionBinder extends Binder {
        public MediaProjectionBinder() {
        }

        public MediaProjectionService getService() {
            return MediaProjectionService.this;
        }
    }

    public static void bindService(Context context, ServiceConnection serviceConnection) {
        context.bindService(new Intent(context, MediaProjectionService.class), serviceConnection, 1);
    }

    public static void unbindService(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new MediaProjectionBinder();
    }

    @Override // android.app.Service
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroy() {
        stopImageReader();
        stopMediaRecorder();
        MediaProjection mediaProjection = this.mediaProjection;
        if (mediaProjection != null) {
            mediaProjection.stop();
            this.mediaProjection = null;
        }
        if (this.mediaProjectionManager != null) {
            this.mediaProjectionManager = null;
        }
        stopForeground(true);
    }

    private void stopImageReader() {
        Log.e("stopImageReader", "结束 屏幕截图");
        this.isImageAvailable = false;
        ImageReader imageReader = this.imageReader;
        if (imageReader != null) {
            imageReader.close();
            this.imageReader = null;
        }
        VirtualDisplay virtualDisplay = this.virtualDisplayImageReader;
        if (virtualDisplay != null) {
            virtualDisplay.release();
            this.virtualDisplayImageReader = null;
        }
    }

    private void stopMediaRecorder() {
        stopRecording();
        VirtualDisplay virtualDisplay = this.virtualDisplayMediaRecorder;
        if (virtualDisplay != null) {
            virtualDisplay.release();
            this.virtualDisplayMediaRecorder = null;
        }
    }

    private void showNotification() {
        MediaProjectionNotificationEngine mediaProjectionNotificationEngine = this.notificationEngine;
        if (mediaProjectionNotificationEngine == null) {
            return;
        }
        startForeground(10086, mediaProjectionNotificationEngine.getNotification());
    }

    private void createImageReader() {
        int i = this.displayMetrics.widthPixels;
        int i2 = this.displayMetrics.heightPixels;
        int i3 = this.displayMetrics.densityDpi;
        Log.e("createImageReader=====", i + "-" + i2 + "-" + i3);
        ImageReader newInstance = ImageReader.newInstance(i2, i, 1, 2);
        this.imageReader = newInstance;
        newInstance.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { // from class: com.mask.mediaprojection.service.MediaProjectionService$$ExternalSyntheticLambda0
            @Override // android.media.ImageReader.OnImageAvailableListener
            public final void onImageAvailable(ImageReader imageReader) {
                MediaProjectionService.this.m283x31749216(imageReader);
            }
        }, null);
        this.virtualDisplayImageReader = this.mediaProjection.createVirtualDisplay("ScreenCapture", i2, i, i3, 16, this.imageReader.getSurface(), null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$createImageReader$0$com-mask-mediaprojection-service-MediaProjectionService  reason: not valid java name */
    public /* synthetic */ void m283x31749216(ImageReader imageReader) {
        this.isImageAvailable = true;
    }

    private void createMediaRecorder() {
        int i = this.displayMetrics.widthPixels;
        int i2 = this.displayMetrics.heightPixels;
        int i3 = this.displayMetrics.densityDpi;
        File cacheMovieDir = FileUtils.getCacheMovieDir(this);
        cacheMovieDir.mkdirs();
        this.mediaFile = new File(cacheMovieDir, FileUtils.getDateName("MediaRecorder") + ".mp4");
        MediaRecorder mediaRecorder = new MediaRecorder();
        this.mediaRecorder = mediaRecorder;
        mediaRecorder.setVideoSource(2);
        this.mediaRecorder.setOutputFormat(2);
        this.mediaRecorder.setOutputFile(this.mediaFile.getAbsolutePath());
        this.mediaRecorder.setVideoEncoder(2);
        this.mediaRecorder.setVideoSize(i, i2);
        this.mediaRecorder.setVideoFrameRate(30);
        this.mediaRecorder.setVideoEncodingBitRate(i * 5 * i2);
        this.mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() { // from class: com.mask.mediaprojection.service.MediaProjectionService.1
            @Override // android.media.MediaRecorder.OnErrorListener
            public void onError(MediaRecorder mediaRecorder2, int i4, int i5) {
                if (MediaProjectionService.this.mediaRecorderCallback != null) {
                    MediaProjectionService.this.mediaRecorderCallback.onFail();
                }
            }
        });
        try {
            this.mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        VirtualDisplay virtualDisplay = this.virtualDisplayMediaRecorder;
        if (virtualDisplay == null) {
            this.virtualDisplayMediaRecorder = this.mediaProjection.createVirtualDisplay("MediaRecorder", i, i2, i3, 16, this.mediaRecorder.getSurface(), null, null);
        } else {
            virtualDisplay.setSurface(this.mediaRecorder.getSurface());
        }
    }

    public void setNotificationEngine(MediaProjectionNotificationEngine mediaProjectionNotificationEngine) {
        this.notificationEngine = mediaProjectionNotificationEngine;
    }

    public void createVirtualDisplay(int i, Intent intent, DisplayMetrics displayMetrics, boolean z, boolean z2) {
        this.displayMetrics = displayMetrics;
        Log.e("createVirtualDisplay", displayMetrics.widthPixels + "===" + displayMetrics.heightPixels);
        this.isScreenCaptureEnable = z;
        this.isMediaRecorderEnable = z2;
        if (intent == null) {
            stopSelf();
            return;
        }
        showNotification();
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService("media_projection");
        this.mediaProjectionManager = mediaProjectionManager;
        if (mediaProjectionManager == null) {
            stopSelf();
            return;
        }
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(i, intent);
        this.mediaProjection = mediaProjection;
        if (mediaProjection == null) {
            stopSelf();
        } else if (z) {
            createImageReader();
        }
    }

    public void capture(DisplayMetrics displayMetrics, ScreenCaptureCallback screenCaptureCallback) {
        if (!this.isScreenCaptureEnable) {
            screenCaptureCallback.onFail();
            return;
        }
        ImageReader imageReader = this.imageReader;
        if (imageReader == null) {
            screenCaptureCallback.onFail();
        } else if (!this.isImageAvailable) {
            screenCaptureCallback.onFail();
        } else {
            Image acquireLatestImage = imageReader.acquireLatestImage();
            if (acquireLatestImage == null) {
                screenCaptureCallback.onFail();
                return;
            }
            int width = acquireLatestImage.getWidth();
            int height = acquireLatestImage.getHeight();
            Image.Plane[] planes = acquireLatestImage.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            Bitmap createBitmap = Bitmap.createBitmap(((planes[0].getRowStride() - (pixelStride * width)) / pixelStride) + width, height, Bitmap.Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(buffer);
            Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, width, height);
            acquireLatestImage.close();
            screenCaptureCallback.onSuccess(createBitmap2);
        }
    }

    public static Bitmap image_ARGB8888_2_bitmap(DisplayMetrics displayMetrics, Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride() - (pixelStride * width);
        Bitmap createBitmap = Bitmap.createBitmap(displayMetrics, width, height, Bitmap.Config.ARGB_8888);
        int i = 0;
        for (int i2 = 0; i2 < height; i2++) {
            for (int i3 = 0; i3 < width; i3++) {
                createBitmap.setPixel(i3, i2, ((buffer.get(i) & 255) << 16) | 0 | ((buffer.get(i + 1) & 255) << 8) | (buffer.get(i + 2) & 255) | ((buffer.get(i + 3) & 255) << 24));
                i += pixelStride;
            }
            i += rowStride;
        }
        return createBitmap;
    }

    public static final Bitmap rotate(Bitmap bitmap, float f) {
        if (f == 0.0f || bitmap == null) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != createBitmap) {
            bitmap.recycle();
            return createBitmap;
        }
        return bitmap;
    }

    private Bitmap cropBitmap(Bitmap bitmap, int i, int i2) {
        int doubleValue = (int) (txDouble(i, i2).doubleValue() * i);
        return scaleBitmap(Bitmap.createBitmap(bitmap, 0, (i2 - doubleValue) / 2, i, doubleValue, (Matrix) null, false), i2, i);
    }

    public static Double txfloat(int i, int i2) {
        return Double.valueOf(Double.parseDouble(new DecimalFormat("0.00").format(i / i2)));
    }

    public static Double txDouble(int i, int i2) {
        return Double.valueOf(Double.parseDouble(new DecimalFormat("0.000000").format(i / i2)));
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(i / width, i2 / height);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        createBitmap.getHeight();
        createBitmap.getWidth();
        return createBitmap;
    }

    public void startRecording(MediaRecorderCallback mediaRecorderCallback) {
        this.mediaRecorderCallback = mediaRecorderCallback;
        if (!this.isMediaRecorderEnable) {
            if (mediaRecorderCallback != null) {
                mediaRecorderCallback.onFail();
            }
        } else if (this.isMediaRecording) {
            if (mediaRecorderCallback != null) {
                mediaRecorderCallback.onFail();
            }
        } else {
            createMediaRecorder();
            this.mediaRecorder.start();
            this.isMediaRecording = true;
        }
    }

    public void stopRecording() {
        MediaRecorderCallback mediaRecorderCallback;
        if (!this.isMediaRecorderEnable && (mediaRecorderCallback = this.mediaRecorderCallback) != null) {
            mediaRecorderCallback.onFail();
        }
        MediaRecorder mediaRecorder = this.mediaRecorder;
        if (mediaRecorder == null) {
            MediaRecorderCallback mediaRecorderCallback2 = this.mediaRecorderCallback;
            if (mediaRecorderCallback2 != null) {
                mediaRecorderCallback2.onFail();
            }
        } else if (!this.isMediaRecording) {
            MediaRecorderCallback mediaRecorderCallback3 = this.mediaRecorderCallback;
            if (mediaRecorderCallback3 != null) {
                mediaRecorderCallback3.onFail();
            }
        } else {
            mediaRecorder.stop();
            this.mediaRecorder.reset();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
            MediaRecorderCallback mediaRecorderCallback4 = this.mediaRecorderCallback;
            if (mediaRecorderCallback4 != null) {
                mediaRecorderCallback4.onSuccess(this.mediaFile);
            }
            this.mediaFile = null;
            this.isMediaRecording = false;
            this.mediaRecorderCallback = null;
        }
    }
}
