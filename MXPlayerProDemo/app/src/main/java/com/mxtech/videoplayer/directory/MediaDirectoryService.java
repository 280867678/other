package com.mxtech.videoplayer.directory;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import com.mxtech.videoplayer.App;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaDirectoryService {
    private static final String BUNDLE_KEY_STRING = "MediaDirectoryService:string";
    private static final String TAG = App.TAG + ".MediaDirectoryService";
    private static final int TERMINATE_THREAD_AFTER = 10000;
    private long _lastRequestTime;
    private int _numQueuedRequests;
    private ServiceThread _thread;
    private final List<OnMockDirectoryChangeListener> _listeners = new ArrayList();
    private final MediaDirectory _mdir = new MediaDirectory();
    private ImmutableMediaDirectory _immutable = new ImmutableMediaDirectory();
    private final Runnable _threadTerminator = new Runnable() { // from class: com.mxtech.videoplayer.directory.MediaDirectoryService.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (MediaDirectoryService.this) {
                if (MediaDirectoryService.this._thread != null && MediaDirectoryService.this._numQueuedRequests == 0 && SystemClock.uptimeMillis() >= MediaDirectoryService.this._lastRequestTime + 10000) {
                    MediaDirectoryService.this._thread.quit();
                    MediaDirectoryService.this._thread = null;
                }
            }
        }
    };
    private final Runnable _listenerCaller = new Runnable() { // from class: com.mxtech.videoplayer.directory.MediaDirectoryService.2
        @Override // java.lang.Runnable
        public void run() {
            ImmutableMediaDirectory immutable;
            synchronized (MediaDirectoryService.this) {
                immutable = MediaDirectoryService.this._immutable;
            }
            for (int i = 0; i < MediaDirectoryService.this._listeners.size(); i++) {
                ((OnMockDirectoryChangeListener) MediaDirectoryService.this._listeners.get(i)).onMockDirectoryChanged(immutable);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface Accessor {
        void run(MockController mockController, MediaDirectory mediaDirectory, Message message);
    }

    /* loaded from: classes.dex */
    public interface MockController {
        void invalidateMockDirectory();
    }

    /* loaded from: classes.dex */
    public interface OnMockDirectoryChangeListener {
        void onMockDirectoryChanged(ImmutableMediaDirectory immutableMediaDirectory);
    }

    static /* synthetic */ int access$106(MediaDirectoryService x0) {
        int i = x0._numQueuedRequests - 1;
        x0._numQueuedRequests = i;
        return i;
    }

    public void requestModify(Accessor accessor) {
        requestModify(accessor, Message.obtain());
    }

    public void requestModify(Accessor accessor, int arg1, int arg2) {
        Message m = Message.obtain();
        m.arg1 = arg1;
        m.arg2 = arg2;
        requestModify(accessor, m);
    }

    public void requestModify(Accessor accessor, int arg1, int arg2, String string) {
        Message m = Message.obtain();
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.getData().putString(BUNDLE_KEY_STRING, string);
        requestModify(accessor, m);
    }

    public synchronized void requestModify(Accessor accessor, Message message) {
        if (this._thread == null) {
            this._thread = new ServiceThread();
        }
        this._numQueuedRequests++;
        this._lastRequestTime = SystemClock.uptimeMillis();
        this._thread.request(accessor, message);
    }

    public static String getString(Message msg) {
        return msg.getData().getString(BUNDLE_KEY_STRING);
    }

    @NonNull
    public synchronized ImmutableMediaDirectory getMediaDirectory() {
        return this._immutable;
    }

    public void registerMockDirectoryChangeListener(OnMockDirectoryChangeListener listener) {
        this._listeners.add(listener);
    }

    public void unregisterMockDirectoryChangeListener(OnMockDirectoryChangeListener listener) {
        this._listeners.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ServiceThread extends HandlerThread implements Handler.Callback, MockController {
        private final Handler _handler;
        private boolean _invalidated;

        ServiceThread() {
            super(MediaDirectoryService.TAG);
            start();
            this._handler = new Handler(getLooper(), this);
        }

        void request(Accessor accessor, Message message) {
            message.what = 0;
            message.setTarget(this._handler);
            message.obj = accessor;
            this._handler.sendMessage(message);
        }

        @Override // com.mxtech.videoplayer.directory.MediaDirectoryService.MockController
        public void invalidateMockDirectory() {
            this._invalidated = true;
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            ImmutableMediaDirectory newImmutable = null;
            ((Accessor) msg.obj).run(this, MediaDirectoryService.this._mdir, msg);
            if (MediaDirectoryService.this._numQueuedRequests == 1 && this._invalidated) {
                this._invalidated = false;
                MediaDirectoryService.this._mdir.list("/", 32);
                if (MediaDirectoryService.this._numQueuedRequests == 1) {
                    String[] topDirs = MediaDirectoryService.this._mdir.getTopDirectories();
                    if (MediaDirectoryService.this._numQueuedRequests == 1) {
                        newImmutable = new ImmutableMediaDirectory(MediaDirectoryService.this._mdir, topDirs);
                    }
                }
            }
            synchronized (MediaDirectoryService.this) {
                if (MediaDirectoryService.access$106(MediaDirectoryService.this) == 0) {
                    if (newImmutable != null) {
                        MediaDirectoryService.this._immutable = newImmutable;
                        App.handler.post(MediaDirectoryService.this._listenerCaller);
                    }
                    App.handler.postDelayed(MediaDirectoryService.this._threadTerminator, 10000L);
                }
            }
            return true;
        }
    }
}
