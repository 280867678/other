package com.mask.mediaprojection.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.mask.mediaprojection.interfaces.MediaRecorderCallback;
import com.mask.mediaprojection.interfaces.ScreenCaptureCallback;
import com.mask.mediaprojection.service.MediaProjectionService;

/* loaded from: classes2.dex */
public class MediaProjectionHelper {
    public static final int REQUEST_CODE = 10086;
    private DisplayMetrics displayMetrics;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjectionService mediaProjectionService;
    private MediaProjectionNotificationEngine notificationEngine;
    private ServiceConnection serviceConnection;

    /* loaded from: classes2.dex */
    private static class InstanceHolder {
        private static final MediaProjectionHelper instance = new MediaProjectionHelper();

        private InstanceHolder() {
        }
    }

    public static MediaProjectionHelper getInstance() {
        return InstanceHolder.instance;
    }

    private MediaProjectionHelper() {
    }

    public void setNotificationEngine(MediaProjectionNotificationEngine mediaProjectionNotificationEngine) {
        this.notificationEngine = mediaProjectionNotificationEngine;
    }

    public boolean startService(Activity activity) {
        MediaProjectionManager mediaProjectionManager = this.mediaProjectionManager;
        if (mediaProjectionManager != null) {
            Log.e("mediaProjectionManager", mediaProjectionManager.toString());
            return false;
        }
        @SuppressLint("WrongConstant") MediaProjectionManager mediaProjectionManager2 = (MediaProjectionManager) activity.getSystemService("media_projection");
        this.mediaProjectionManager = mediaProjectionManager2;
        if (mediaProjectionManager2 != null) {
            activity.startActivityForResult(mediaProjectionManager2.createScreenCaptureIntent(), REQUEST_CODE);
        }
        this.displayMetrics = new DisplayMetrics();
        Log.e("displayMetrics", this.displayMetrics.widthPixels + "==" + this.displayMetrics.heightPixels);
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(this.displayMetrics);
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.mask.mediaprojection.utils.MediaProjectionHelper.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder instanceof MediaProjectionService.MediaProjectionBinder) {
                    MediaProjectionHelper.this.mediaProjectionService = ((MediaProjectionService.MediaProjectionBinder) iBinder).getService();
                    MediaProjectionHelper.this.mediaProjectionService.setNotificationEngine(MediaProjectionHelper.this.notificationEngine);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                MediaProjectionHelper.this.mediaProjectionService = null;
            }
        };
        this.serviceConnection = serviceConnection;
        MediaProjectionService.bindService(activity, serviceConnection);
        return true;
    }

    public void stopService(Context context) {
        this.mediaProjectionService = null;
        ServiceConnection serviceConnection = this.serviceConnection;
        if (serviceConnection != null) {
            MediaProjectionService.unbindService(context, serviceConnection);
            this.serviceConnection = null;
        }
        this.displayMetrics = null;
        this.mediaProjectionManager = null;
        Log.e("stopService", "mediaProjectionManager");
    }

    public void createVirtualDisplay(int i, int i2, Intent intent, boolean z, boolean z2) {
        MediaProjectionService mediaProjectionService = this.mediaProjectionService;
        if (mediaProjectionService != null && i == 10086 && i2 == -1) {
            mediaProjectionService.createVirtualDisplay(i2, intent, this.displayMetrics, z, z2);
        }
    }

    public void capture(ScreenCaptureCallback screenCaptureCallback) {
        MediaProjectionService mediaProjectionService = this.mediaProjectionService;
        if (mediaProjectionService == null) {
            screenCaptureCallback.onFail();
        } else {
            mediaProjectionService.capture(this.displayMetrics, screenCaptureCallback);
        }
    }

    public void startMediaRecorder(MediaRecorderCallback mediaRecorderCallback) {
        MediaProjectionService mediaProjectionService = this.mediaProjectionService;
        if (mediaProjectionService == null) {
            mediaRecorderCallback.onFail();
        } else {
            mediaProjectionService.startRecording(mediaRecorderCallback);
        }
    }

    public void stopMediaRecorder() {
        MediaProjectionService mediaProjectionService = this.mediaProjectionService;
        if (mediaProjectionService == null) {
            return;
        }
        mediaProjectionService.stopRecording();
    }
}
