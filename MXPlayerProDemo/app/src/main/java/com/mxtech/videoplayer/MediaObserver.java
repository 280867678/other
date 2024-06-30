package com.mxtech.videoplayer;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Message;
import android.provider.MediaStore;
import com.mxtech.FileUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.os.DirectoryObserver;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.directory.MediaDirectory;
import com.mxtech.videoplayer.directory.MediaDirectoryService;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class MediaObserver extends DirectoryObserver implements ActivityRegistry.StateChangeListener, DirectoryObserver.Listener, OrderedSharedPreferences.OnSharedPreferenceChangeListener, Runnable {
    public static final String TAG = App.TAG + ".MediaObserver";
    private final ContentObserver _contentObserver;
    private boolean _enabled;
    private boolean _hasPendingResetRequest;
    private boolean _needReset;
    private final MediaDirectoryService.Accessor _resetAccessor = new MediaDirectoryService.Accessor() { // from class: com.mxtech.videoplayer.MediaObserver.2
        @Override // com.mxtech.videoplayer.directory.MediaDirectoryService.Accessor
        public void run(MediaDirectoryService.MockController mockController, MediaDirectory mdir, Message message) {
            mdir.clear();
            mockController.invalidateMockDirectory();
        }
    };

    public MediaObserver() {
        ActivityRegistry.registerStateListener(this);
        this._contentObserver = new ContentObserver(this.handler) { // from class: com.mxtech.videoplayer.MediaObserver.1
            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return false;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                MediaObserver.this.handler.removeCallbacks(MediaObserver.this);
                MediaObserver.this.handler.post(MediaObserver.this);
                MediaObserver.this._hasPendingResetRequest = true;
            }
        };
        super.registerListener(this);
        App.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        this._hasPendingResetRequest = false;
        resetAsync();
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case -527677737:
                if (key.equals(Key.SCAN_ROOTS)) {
                    c = 2;
                    break;
                }
                break;
            case 214489388:
                if (key.equals(Key.SHOW_HIDDEN)) {
                    c = 1;
                    break;
                }
                break;
            case 327429450:
                if (key.equals(Key.RESPECT_NOMEDIA)) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
                resetAsync();
                return;
            default:
                return;
        }
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onChanged(DirectoryObserver observer, File file) {
        renew(file, false, false);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onDeleted(DirectoryObserver observer, File file) {
        renew(file, false, false);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onMovedIn(DirectoryObserver observer, File file) {
        renew(file, true, false);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onMovedOut(DirectoryObserver observer, File file) {
        renew(file, false, false);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onDirectoryMovedOut(DirectoryObserver observer, File dir) {
        renew(dir, false, true);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onDirectoryDeleted(DirectoryObserver observer, File dir) {
        renew(dir, false, true);
    }

    @Override // com.mxtech.os.DirectoryObserver.Listener
    public void onWatchingNewDirectory(DirectoryObserver observer, File dir) {
        renew(dir, false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RenewAccessor implements MediaDirectoryService.Accessor {
        private final File file;
        private final boolean scanNewSubDirectories;

        RenewAccessor(File file, boolean scanNewSubDirectories) {
            this.file = file;
            this.scanNewSubDirectories = scanNewSubDirectories;
        }

        @Override // com.mxtech.videoplayer.directory.MediaDirectoryService.Accessor
        public void run(MediaDirectoryService.MockController mockController, MediaDirectory mdir, Message message) {
            if (mdir.renew(this.file, this.scanNewSubDirectories)) {
                mockController.invalidateMockDirectory();
            }
        }
    }

    private void renew(File file, boolean scanNewSubDirectories, boolean forceDirectory) {
        String ext;
        SubtitleDirectory sdir;
        boolean isDirectory = forceDirectory || file.isDirectory();
        if (isDirectory || MediaDirectory.getFormat(file.getPath()) >= 0) {
            L.directoryService.requestModify(new RenewAccessor(file, scanNewSubDirectories));
        }
        if (P.subtitleFolder != null) {
            boolean refreshSubtitleDirectory = false;
            if (isDirectory) {
                if (FileUtils.equalsIgnoreCase(file, P.subtitleFolder)) {
                    refreshSubtitleDirectory = true;
                }
            } else if (FileUtils.isDirectAscendantOf(file.getPath(), P.subtitleFolder.getPath()) && (ext = FileUtils.getExtension(file)) != null && SubtitleFactory.isSupportedExtension(ext)) {
                refreshSubtitleDirectory = true;
            }
            if (refreshSubtitleDirectory && (sdir = SubtitleDirectory.getInstance(false)) != null) {
                sdir.invalidate();
                sdir.close();
            }
        }
    }

    @Override // com.mxtech.os.DirectoryObserver
    public void monitorDirectory(File directory) {
        super.monitorDirectory(directory, 4, false);
    }

    private void enable(boolean enabled) {
        if (this._enabled != enabled) {
            this._enabled = enabled;
            if (enabled) {
                App.cr.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, this._contentObserver);
                if (P.isAudioPlayer) {
                    App.cr.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this._contentObserver);
                }
                if (P.scanRoots != null) {
                    Map<String, Boolean> nomediaCache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    for (Map.Entry<File, Boolean> entry : P.scanRoots.entrySet()) {
                        if (entry.getValue().booleanValue()) {
                            File dir = entry.getKey();
                            if (MediaScanner.isPathVisible(dir.getPath(), nomediaCache)) {
                                monitorDirectory(dir);
                            }
                        }
                    }
                }
                if (P.subtitleFolder != null) {
                    monitorDirectory(P.subtitleFolder);
                }
                L.directoryService.requestModify(this._resetAccessor);
                return;
            }
            App.cr.unregisterContentObserver(this._contentObserver);
            SubtitleDirectory sdir = SubtitleDirectory.getInstance(false);
            if (sdir != null) {
                sdir.invalidate();
                sdir.close();
            }
            super.clear();
        }
    }

    private void reset() {
        if (!this._hasPendingResetRequest) {
            this._needReset = false;
            if (this._enabled) {
                enable(false);
                enable(true);
                return;
            }
            super.clear();
        }
    }

    public void resetAsync() {
        if (ActivityRegistry.hasVisibleActivity(ActivityMediaList.class)) {
            reset();
        } else {
            this._needReset = true;
        }
    }

    @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
    public void onActivityStateChanged(Activity activity, int state) {
        if (state == 1 || state == 16) {
            enable(ActivityRegistry.hasVisibleActivity(ActivityMediaList.class));
        }
        if (this._needReset && state == 1 && (activity instanceof ActivityMediaList)) {
            reset();
        }
    }

    @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
    public void onActivityCreated(Activity activity) {
    }

    @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
    public void onActivityRemoved(Activity activity) {
    }
}
