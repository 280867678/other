package com.mxtech.videoplayer.widget;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaButtonReceiver;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public final class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener, OrderedSharedPreferences.OnSharedPreferenceChangeListener, Runnable {
    private static final int RETRY_FOCUS_ACQUISITION_AFTER = 1000;
    private final String TAG;
    private final Client _client;
    private boolean _focused;
    private Handler _handler;
    private boolean _needed = App.prefs.getBoolean(Key.AUDIO_FOCUS, true);
    private boolean _registered;

    /* loaded from: classes.dex */
    public interface Client extends MediaButtonReceiver.IReceiver {
        boolean needAudioFocus(AudioFocusManager audioFocusManager);

        void onGainAudioFocus(AudioFocusManager audioFocusManager);

        void onLoseAudioFocus(AudioFocusManager audioFocusManager, boolean z);
    }

    public AudioFocusManager(Client client, String from) {
        this.TAG = App.TAG + ".AudioFocus/" + from;
        this._client = client;
        App.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case 772840239:
                if (key.equals(Key.AUDIO_FOCUS)) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this._needed = App.prefs.getBoolean(Key.AUDIO_FOCUS, true);
                update();
                return;
            default:
                return;
        }
    }

    public boolean canStartPlayback() {
        if (this._needed) {
            return this._focused;
        }
        return true;
    }

    public void update() {
        if (this._client.needAudioFocus(this)) {
            if (this._needed) {
                requestAudioFocus();
                if (P.respectMediaButtons) {
                    if (this._focused) {
                        MediaButtonReceiver.registerReceiver(this._client, 0);
                        return;
                    }
                    return;
                }
                MediaButtonReceiver.unregisterReceiver(this._client);
                return;
            }
            abandonAudioFocus();
            if (P.respectMediaButtons) {
                MediaButtonReceiver.registerReceiver(this._client, 0);
                return;
            } else {
                MediaButtonReceiver.unregisterReceiver(this._client);
                return;
            }
        }
        abandonAudioFocus();
        MediaButtonReceiver.unregisterReceiver(this._client);
    }

    private void requestAudioFocus() {
        if (!this._focused) {
            int status = L.audioManager.requestAudioFocus(this, 3, 1);
            if (status == 1) {
                this._registered = true;
                this._focused = true;
                Log.d(this.TAG, "Request granted.");
                this._client.onGainAudioFocus(this);
                return;
            }
            Log.e(this.TAG, "Request failed. (status:" + status + ")");
            if (this._handler == null) {
                this._handler = new Handler();
            } else {
                this._handler.removeCallbacks(this);
            }
            this._handler.postDelayed(this, 1000L);
        }
    }

    private void abandonAudioFocus() {
        if (this._registered) {
            int status = L.audioManager.abandonAudioFocus(this);
            if (status == 1) {
                Log.d(this.TAG, "Abandon granted.");
                this._registered = false;
                this._focused = false;
                if (this._handler != null) {
                    this._handler.removeCallbacks(this);
                }
                this._client.onLoseAudioFocus(this, true);
                return;
            }
            Log.e(this.TAG, "Abandon failed. (status:" + status + ")");
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        update();
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case IMediaPlayer.EFAIL /* -3 */:
                Log.v(this.TAG, "Audio focus --> Loss (transient/can duck) : ignores.");
                return;
            case -2:
                Log.v(this.TAG, "Audio focus --> Loss (transient)");
                onLoss(true);
                return;
            case -1:
                Log.v(this.TAG, "Audio focus --> Loss");
                onLoss(false);
                return;
            case 0:
            default:
                Log.w(this.TAG, "Audio focus -?- Unknown change " + focusChange);
                return;
            case 1:
                Log.v(this.TAG, "Audio focus <-- Gain");
                onGain();
                return;
            case 2:
                Log.w(this.TAG, "Audio focus <-- Gain (transient) : unexpected.");
                onGain();
                return;
            case 3:
                Log.w(this.TAG, "Audio focus <-- Gain (transient/may duck) : unexpected, ignores.");
                return;
            case 4:
                Log.w(this.TAG, "Audio focus <-- Gain (transient/exclusive) : unexpected.");
                onGain();
                return;
        }
    }

    private void onGain() {
        this._focused = true;
        this._client.onGainAudioFocus(this);
        if (P.respectMediaButtons) {
            MediaButtonReceiver.registerReceiver(this._client, 0);
        }
    }

    private void onLoss(boolean temporary) {
        this._focused = false;
        this._client.onLoseAudioFocus(this, temporary);
        MediaButtonReceiver.unregisterReceiver(this._client);
    }
}
