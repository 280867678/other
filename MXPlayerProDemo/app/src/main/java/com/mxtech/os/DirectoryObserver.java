package com.mxtech.os;

import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.mxtech.FileUtils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class DirectoryObserver implements FileFilter, Handler.Callback {
    public static final int MONITOR_SUB_DIRECTORIES = 2;
    public static final int MONITOR_TRANSIENT = 4;
    private static final int MSG_CHANGED = 2;
    private static final int MSG_DELETED = 3;
    private static final int MSG_DIRECTORY_DELETED = 7;
    private static final int MSG_DIRECTORY_MOVED = 6;
    private static final int MSG_INFLATE_SUBDIRECOTRY = 1;
    private static final int MSG_MOVED_IN = 4;
    private static final int MSG_MOVED_OUT = 5;
    public static final String TAG = "MX.DirectoryObserver";
    private final List<Listener> _listeners = new ArrayList();
    private final Map<String, Observer> _observers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    protected final Handler handler = new Handler(Looper.getMainLooper(), this);

    /* loaded from: classes.dex */
    public interface Listener {
        void onChanged(DirectoryObserver directoryObserver, File file);

        void onDeleted(DirectoryObserver directoryObserver, File file);

        void onDirectoryDeleted(DirectoryObserver directoryObserver, File file);

        void onDirectoryMovedOut(DirectoryObserver directoryObserver, File file);

        void onMovedIn(DirectoryObserver directoryObserver, File file);

        void onMovedOut(DirectoryObserver directoryObserver, File file);

        void onWatchingNewDirectory(DirectoryObserver directoryObserver, File file);
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                File subDir = (File) msg.obj;
                if (accept(subDir)) {
                    monitorDirectory(subDir, msg.arg1, false);
                }
                return true;
            case 2:
                for (Listener listener : this._listeners) {
                    listener.onChanged(this, (File) msg.obj);
                }
                return true;
            case 3:
                for (Listener listener2 : this._listeners) {
                    listener2.onDeleted(this, (File) msg.obj);
                }
                return true;
            case 4:
                for (Listener listener3 : this._listeners) {
                    listener3.onMovedIn(this, (File) msg.obj);
                }
                return true;
            case 5:
                for (Listener listener4 : this._listeners) {
                    listener4.onMovedOut(this, (File) msg.obj);
                }
                return true;
            case 6:
                for (Listener listener5 : this._listeners) {
                    listener5.onDirectoryMovedOut(this, ((Observer) msg.obj).directory);
                }
                return true;
            case 7:
                for (Listener listener6 : this._listeners) {
                    listener6.onDirectoryDeleted(this, ((Observer) msg.obj).directory);
                }
                return true;
            default:
                return false;
        }
    }

    public final void registerListener(Listener listener) {
        this._listeners.add(listener);
    }

    public final void unregisterListener(Listener listener) {
        this._listeners.remove(listener);
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        return file.isDirectory();
    }

    public void monitorDirectory(File directory) {
        monitorDirectory(directory, 0, false);
    }

    public final synchronized void monitorDirectory(File directory, int monitoring, boolean scanSubDirectories) {
        File parent = directory.getParentFile();
        if (parent != null) {
            doMonitorDirectory_l(parent, monitoring & (-3));
        }
        doMonitorDirectory_l(directory, monitoring);
        if (scanSubDirectories) {
            doMonitorSubdirectories_l(directory, monitoring);
        }
    }

    private void doMonitorDirectory_l(File directory, int monitoring) {
        String path = directory.getPath();
        Observer ob = this._observers.get(path);
        if (ob == null) {
            ob = new Observer(directory, monitoring);
            this._observers.put(path, ob);
        }
        ob.startWatching();
    }

    private void doMonitorSubdirectories_l(File parent, int monitoring) {
        File[] subDirs = FileUtils.listFiles(parent, this);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                doMonitorDirectory_l(subDir, monitoring);
                doMonitorSubdirectories_l(subDir, monitoring);
            }
        }
    }

    public final synchronized void clear() {
        for (FileObserver ob : this._observers.values()) {
            ob.stopWatching();
        }
        this._observers.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Observer extends FileObserver {
        final File directory;
        final int monitoring;

        Observer(File directory, int monitoring) {
            super(directory.getPath(), ((monitoring & 2) != 0 ? 256 : 0) | 512 | 64 | 128 | 1024 | 2048 | 8);
            this.directory = directory;
            this.monitoring = monitoring;
        }

        @Override // android.os.FileObserver
        public void onEvent(int event, String relPath) {
            File file = relPath != null ? new File(this.directory, relPath) : this.directory;
            if ((event & 512) != 0) {
                DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(3, file));
            } else if ((event & 8) != 0) {
                DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(2, file));
            } else if ((event & 128) != 0) {
                DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(4, file));
            } else if ((event & 64) != 0) {
                DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(5, file));
            } else if ((event & 3072) != 0) {
                stopWatching();
                synchronized (DirectoryObserver.this) {
                    DirectoryObserver.this._observers.remove(this.directory.getPath());
                }
                if ((event & 1024) != 0) {
                    DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(7, this));
                } else {
                    DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(6, this));
                }
            } else if ((event & 256) != 0 && (this.monitoring & 2) != 0 && file.isDirectory()) {
                DirectoryObserver.this.handler.sendMessage(DirectoryObserver.this.handler.obtainMessage(1, this.monitoring, 0, file));
            }
        }
    }
}
