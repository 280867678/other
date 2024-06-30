package com.carrydream.cardrecorder.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.carrydream.cardrecorder.BaseApplication;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class NotificationHelper {
    private static final String CHANNEL_ID_OTHER = "other";
    private static final String CHANNEL_ID_SYSTEM = "system";
    private static final int CHANNEL_IMPORTANCE_OTHER = 1;
    private static final int CHANNEL_IMPORTANCE_SYSTEM = 4;
    private static final String CHANNEL_NAME_OTHER = "其他消息";
    private static final String CHANNEL_NAME_SYSTEM = "系统通知";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InstanceHolder {
        private static final NotificationHelper instance = new NotificationHelper();

        private InstanceHolder() {
        }
    }

    public static NotificationHelper getInstance() {
        return InstanceHolder.instance;
    }

    private NotificationHelper() {
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        createChannel(CHANNEL_ID_OTHER, CHANNEL_NAME_OTHER, 1, false);
        createChannel(CHANNEL_ID_SYSTEM, CHANNEL_NAME_SYSTEM, 4, true);
    }

    private void createChannel(String str, String str2, int i, boolean z) {
        NotificationChannel notificationChannel = new NotificationChannel(str, str2, i);
        notificationChannel.setShowBadge(z);
        NotificationManager notificationManager = (NotificationManager) BaseApplication.getInstance().getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public NotificationCompat.Builder create(String str) {
        BaseApplication baseApplication = BaseApplication.getInstance();
        return new NotificationCompat.Builder(baseApplication, str).setContentTitle(baseApplication.getString(R.string.app_name)).setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(baseApplication.getResources(), R.mipmap.ic_launcher));
    }

    public NotificationCompat.Builder createOther() {
        return create(CHANNEL_ID_OTHER);
    }

    public NotificationCompat.Builder createSystem() {
        return create(CHANNEL_ID_SYSTEM);
    }

    public void show(int i, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) BaseApplication.getInstance().getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.notify(i, notification);
        }
    }
}
