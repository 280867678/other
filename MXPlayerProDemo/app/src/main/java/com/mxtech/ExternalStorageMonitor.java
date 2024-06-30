package com.mxtech;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import com.mxtech.videoplayer.list.MediaListFragment;

/* loaded from: classes2.dex */
public final class ExternalStorageMonitor extends BroadcastReceiver {
    private final Activity _activity;
    private final boolean _allowReadonly;
    private final Listener _listener;
    private boolean _receiving;
    public String lastState;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onStorageErrorReceived(String str);
    }

    public ExternalStorageMonitor(Activity activity, boolean allowReadonly, Listener listener) {
        this._activity = activity;
        this._allowReadonly = allowReadonly;
        this._listener = listener;
    }

    public boolean check() {
        return check(Environment.getExternalStorageState());
    }

    private boolean check(String state) {
        return "mounted".equals(state) || (this._allowReadonly && "mounted_ro".equals(state));
    }

    public boolean start() {
        this.lastState = Environment.getExternalStorageState();
        if (check(this.lastState)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addAction("android.intent.action.MEDIA_SHARED");
            filter.addDataScheme(MediaListFragment.TYPE_FILE);
            this._activity.registerReceiver(this, filter);
            this._receiving = true;
            return true;
        }
        return false;
    }

    public void stop() {
        if (this._receiving) {
            this._activity.unregisterReceiver(this);
            this._receiving = false;
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.lastState = Environment.getExternalStorageState();
        if (!check(this.lastState)) {
            this._listener.onStorageErrorReceived(this.lastState);
        }
    }
}
