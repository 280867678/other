package com.mxtech.preference;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class OrderedSharedPreferences extends Handler implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int MSG_REGISTER_CALLBACK = 0;
    private static final int MSG_UNREGISTER_CALLBACK = 1;
    private static final String TAG = "MX.OrderedSharedPreferences";
    @Nullable
    private ArrayList<WeakReference<OnSharedPreferenceChangeListener>> _listeners;
    private final SharedPreferences _prefs;
    private boolean _registered;

    /* loaded from: classes.dex */
    public interface OnSharedPreferenceChangeListener {
        void onSharedPreferenceChanged(OrderedSharedPreferences orderedSharedPreferences, String str);
    }

    public OrderedSharedPreferences(SharedPreferences prefs) {
        super(Looper.getMainLooper());
        this._prefs = prefs;
    }

    public boolean contains(String key) {
        return this._prefs.contains(key);
    }

    public SharedPreferences.Editor edit() {
        return this._prefs.edit();
    }

    public Map<String, ?> getAll() {
        return this._prefs.getAll();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this._prefs.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return this._prefs.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return this._prefs.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return this._prefs.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return this._prefs.getString(key, defValue);
    }

    @TargetApi(11)
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return this._prefs.getStringSet(key, defValues);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            sendMessage(obtainMessage(0, listener));
        } else {
            doRegisterOnSharedPreferenceChangeListener(listener);
        }
    }

    private void doRegisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<>();
        }
        Iterator<WeakReference<OnSharedPreferenceChangeListener>> it = this._listeners.iterator();
        while (it.hasNext()) {
            WeakReference<OnSharedPreferenceChangeListener> ref = it.next();
            OnSharedPreferenceChangeListener l = ref.get();
            if (l == null) {
                it.remove();
            } else if (l == listener) {
                return;
            }
        }
        this._listeners.add(new WeakReference<>(listener));
        if (!this._registered) {
            this._prefs.registerOnSharedPreferenceChangeListener(this);
            this._registered = true;
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            sendMessage(obtainMessage(1, listener));
        } else {
            doUnregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    private void doUnregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (this._listeners != null) {
            Iterator<WeakReference<OnSharedPreferenceChangeListener>> it = this._listeners.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WeakReference<OnSharedPreferenceChangeListener> ref = it.next();
                OnSharedPreferenceChangeListener l = ref.get();
                if (l == null) {
                    it.remove();
                } else if (l == listener) {
                    it.remove();
                    break;
                }
            }
            if (this._registered && this._listeners.size() == 0) {
                this._prefs.unregisterOnSharedPreferenceChangeListener(this);
                this._registered = false;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            doRegisterOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) msg.obj);
        } else if (msg.what == 1) {
            doUnregisterOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) msg.obj);
        }
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Iterator it = new ArrayList(this._listeners).iterator();
        while (it.hasNext()) {
            WeakReference<OnSharedPreferenceChangeListener> ref = (WeakReference) it.next();
            OnSharedPreferenceChangeListener l = ref.get();
            if (l == null) {
                this._listeners.remove(ref);
            } else {
                l.onSharedPreferenceChanged(this, key);
            }
        }
    }
}
