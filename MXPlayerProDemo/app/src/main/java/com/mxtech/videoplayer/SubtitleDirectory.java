package com.mxtech.videoplayer;

import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SubtitleDirectory implements FileFilter, OrderedSharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = App.TAG + ".SubtitleDirectory";
    private static SubtitleDirectory instance;
    private Map<File, String> _files;
    private int _refCount;

    public static SubtitleDirectory getInstance(boolean create) {
        if (instance == null) {
            if (create) {
                instance = new SubtitleDirectory();
            } else {
                return null;
            }
        }
        instance._refCount++;
        return instance;
    }

    public static SubtitleDirectory getInstance() {
        return getInstance(true);
    }

    public static void onLowMemory() {
        if (instance != null && instance._refCount == 0) {
            instance._files = null;
        }
    }

    SubtitleDirectory() {
        App.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public void close() {
        this._refCount--;
    }

    public void invalidate() {
        this._files = null;
    }

    public Map<File, String> list() {
        if (this._files != null) {
            return this._files;
        }
        if (P.subtitleFolder == null) {
            return null;
        }
        File[] files = FileUtils.listFiles(P.subtitleFolder, this);
        if (files != null) {
            this._files = new HashMap();
            for (File file : files) {
                this._files.put(file, file.getName());
            }
        }
        return this._files;
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        return file.isFile() && SubtitleFactory.getExtensionId(file) >= 0;
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case 70025845:
                if (key.equals(Key.SUBTITLE_FOLDER)) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Log.d(TAG, "Clear cached subtitle file list from user subtitle directory as directory changed.");
                this._files = null;
                return;
            default:
                return;
        }
    }
}
