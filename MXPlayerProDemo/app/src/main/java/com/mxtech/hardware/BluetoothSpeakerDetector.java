package com.mxtech.hardware;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import com.mxtech.app.MXApplication;

@TargetApi(11)
/* loaded from: classes2.dex */
public final class BluetoothSpeakerDetector extends BroadcastReceiver implements BluetoothProfile.ServiceListener {
    private static final String TAG = "MX.BTSpeakerDetector";
    private BluetoothA2dp _A2DPProfile;
    private final BluetoothAdapter _adapter;
    private boolean _closed;
    private boolean _lastConnected;
    private OnBluetoothSpeakerStateListener _listener;

    /* loaded from: classes.dex */
    public interface OnBluetoothSpeakerStateListener {
        void onBluetoothSpeakerConnected(BluetoothSpeakerDetector bluetoothSpeakerDetector);

        void onBluetoothSpeakerDisconnected(BluetoothSpeakerDetector bluetoothSpeakerDetector);
    }

    public BluetoothSpeakerDetector() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        MXApplication.context.registerReceiver(this, filter);
        this._adapter = BluetoothAdapter.getDefaultAdapter();
        this._adapter.getProfileProxy(MXApplication.context, this, 2);
    }

    public void close() {
        this._listener = null;
        MXApplication.context.unregisterReceiver(this);
        if (this._A2DPProfile != null) {
            this._adapter.closeProfileProxy(2, this._A2DPProfile);
        }
        this._closed = true;
    }

    public void setOnBluetoothSpeakerStateListener(OnBluetoothSpeakerStateListener listener) {
        this._listener = listener;
    }

    public boolean isBluetoothSpeakerUsed() {
        if (this._adapter.getState() != 12) {
            return false;
        }
        if (this._A2DPProfile != null) {
            int numConnectedDevices = this._A2DPProfile.getConnectedDevices().size();
            return numConnectedDevices > 0;
        }
        AudioManager audioManager = (AudioManager) MXApplication.context.getSystemService("audio");
        boolean on = audioManager.isBluetoothA2dpOn();
        return on;
    }

    @Override // android.bluetooth.BluetoothProfile.ServiceListener
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (this._closed) {
            this._adapter.closeProfileProxy(profile, proxy);
            return;
        }
        if (profile == 2) {
            this._A2DPProfile = (BluetoothA2dp) proxy;
        }
        invokeListener();
    }

    @Override // android.bluetooth.BluetoothProfile.ServiceListener
    public void onServiceDisconnected(int profile) {
        if (profile == 2) {
            this._A2DPProfile = null;
        }
        invokeListener();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(action)) {
            invokeListener();
        }
    }

    private void invokeListener() {
        invokeListener(isBluetoothSpeakerUsed());
    }

    private void invokeListener(boolean connected) {
        if (this._lastConnected != connected) {
            this._lastConnected = connected;
            if (this._listener != null) {
                if (connected) {
                    this._listener.onBluetoothSpeakerConnected(this);
                } else {
                    this._listener.onBluetoothSpeakerDisconnected(this);
                }
            }
        }
    }
}
