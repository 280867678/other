package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MediaButtonReceiver extends BroadcastReceiver {
    public static final int PRIORITY_HIGH = 100;
    public static final int PRIORITY_LOW = -100;
    public static final int PRIORITY_NORMAL = 0;
    private static ComponentName _component_name;
    private static boolean _receivingMediaButtonEvent;
    public static final String TAG = App.TAG + ".MediaButtonReceiver";
    private static final ArrayList<Entry> _receivers = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface IReceiver {
        void onMediaKeyReceived(KeyEvent keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Entry implements Comparable<Entry> {
        final int priority;
        final IReceiver receiver;

        Entry(IReceiver receiver, int priority) {
            this.receiver = receiver;
            this.priority = priority;
        }

        @Override // java.lang.Comparable
        public int compareTo(Entry another) {
            return this.priority - another.priority;
        }
    }

    public static ComponentName getComponentName() {
        if (_component_name == null) {
            _component_name = new ComponentName(App.context, MediaButtonReceiver.class.getName());
        }
        return _component_name;
    }

    @SuppressLint({"NewApi"})
    public static boolean registerReceiver(IReceiver receiver, int priority) {
        if (Build.VERSION.SDK_INT >= 8) {
            remove(receiver);
            _receivers.add(new Entry(receiver, priority));
            Collections.sort(_receivers);
            if (!_receivingMediaButtonEvent) {
                try {
                    L.audioManager.registerMediaButtonEventReceiver(getComponentName());
                    _receivingMediaButtonEvent = true;
                } catch (Throwable e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return _receivingMediaButtonEvent;
    }

    @SuppressLint({"NewApi"})
    public static void unregisterReceiver(IReceiver receiver) {
        if (Build.VERSION.SDK_INT >= 8) {
            remove(receiver);
            if (_receivingMediaButtonEvent && _receivers.size() == 0) {
                L.audioManager.unregisterMediaButtonEventReceiver(getComponentName());
                _receivingMediaButtonEvent = false;
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void unregisterAllReceivers() {
        if (Build.VERSION.SDK_INT >= 8) {
            _receivers.clear();
            if (_receivingMediaButtonEvent) {
                L.audioManager.unregisterMediaButtonEventReceiver(getComponentName());
                _receivingMediaButtonEvent = false;
            }
        }
    }

    private static void remove(IReceiver receiver) {
        Iterator<Entry> it = _receivers.iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            if (entry.receiver == receiver) {
                it.remove();
                return;
            }
        }
    }

    public static boolean isMediaKey(int keyCode) {
        switch (keyCode) {
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case TransportMediator.KEYCODE_MEDIA_PLAY /* 126 */:
            case TransportMediator.KEYCODE_MEDIA_PAUSE /* 127 */:
            case 128:
            case 129:
            case 130:
            case 222:
                return true;
            default:
                return false;
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction()) && _receivers.size() != 0) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            if (event == null) {
                Log.w(TAG, "android.intent.action.MEDIA_BUTTON came without key event.");
                return;
            }
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            int repeatCount = event.getRepeatCount();
            L.sb.setLength(0);
            L.sb.append("KeyEvent(ACTION_MEDIA_BUTTON): action=").append(action).append(" keyCode=").append(keyCode).append(" repeat=").append(repeatCount);
            Log.v(TAG, L.sb.toString());
            _receivers.get(_receivers.size() - 1).receiver.onMediaKeyReceived(event);
            if (isOrderedBroadcast()) {
                abortBroadcast();
            }
        }
    }
}
