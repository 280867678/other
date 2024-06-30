package com.carrydream.cardrecorder.tool;

import org.greenrobot.eventbus.EventBus;

/* loaded from: classes.dex */
public class EventUtil {
    public static final int OPEN_ = 2;
    public static final int UPDATE = 0;
    public static final int VIP_UPDATE = 1;

    public static void register(Object obj) {
        if (EventBus.getDefault().isRegistered(obj)) {
            return;
        }
        EventBus.getDefault().register(obj);
    }

    public static void unregister(Object obj) {
        if (EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    public static void post(Object obj) {
        EventBus.getDefault().post(obj);
    }
}
