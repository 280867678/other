package com.mxtech.videoplayer;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.Nullable;
//import android.support.v4.media.TransportMediator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//import com.flurry.android.FlurryAgent;
//import com.google.android.gms.actions.SearchIntents;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import com.mxtech.DeviceUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.app.Authorizer;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.media.service.FFService;
import com.mxtech.media.service.IFFService;
import com.mxtech.os.AsyncTask2;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.MediaButtonReceiver;
import com.mxtech.videoplayer.list.IListContainer;
import com.mxtech.videoplayer.list.ListHelper;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.DecoderPreferences;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class ActivityMediaList extends ActivityList implements IListContainer, Handler.Callback, ServiceConnection, MediaButtonReceiver.IReceiver, View.OnClickListener, View.OnLongClickListener {
    private static final int MSG_REBUILD = 100;
    public static final String TAG = ActivityList.TAG + ".Media";
    private boolean _bound;
    private boolean _connecting;
    private final Handler _handler = new Handler(this);
    private ListHelper _listHelper;
    private View _playLastButton;
    private String _playLastItemName;
    @Nullable
    private MenuItem _playLastMenuItem;
    private ScanTask _scanTask;
    private IFFService _service;
    private ArrayList<Runnable> _serviceConnRunnables;
    private int _serviceRefCount;
    private boolean _showPlayLastButton;
    private TextView _statusView;
    private Tracker _tracker;

    private void openIntent(Intent intent, boolean backupCurrent) {
        if (!isFinishing()) {
            String action = intent.getAction();
            Bundle args = new Bundle();
            if ("android.intent.action.SEARCH".equals(action)) {
                String query = intent.getStringExtra(SearchIntents.EXTRA_QUERY);
                if (query == null) {
                    query = "";
                }
                if (query.length() > 0) {
                    try {
                        MediaDatabase mdb = MediaDatabase.getInstance();
                        mdb.saveRecentQuery(query);
                        mdb.release();
                    } catch (SQLiteException e) {
                        Log.e(TAG, "", e);
                    }
                }
                ArrayList<String> extra = intent.getStringArrayListExtra("android.speech.extra.RESULTS");
                if (extra == null) {
                    args.putString(MediaListFragment.KEY_TYPE, MediaListFragment.TYPE_SEARCH);
                    args.putString(MediaListFragment.KEY_TARGET, query);
                } else {
                    args.putString(MediaListFragment.KEY_TYPE, MediaListFragment.TYPE_SEARCH_MULTI);
                    args.putStringArrayList(MediaListFragment.KEY_TARGET, extra);
                }
                MediaListFragment current = (MediaListFragment) getCurrentFragment();
                if (current != null && current.isVolatile()) {
                    current.reset(args);
                    return;
                }
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    args.putString(MediaListFragment.KEY_TYPE, MediaListFragment.TYPE_URI);
                    args.putParcelable(MediaListFragment.KEY_TARGET, uri);
                } else {
                    args.putString(MediaListFragment.KEY_TYPE, MediaListFragment.TYPE_ROOT);
                }
            }
            newFragment(args, backupCurrent);
        }
    }

    @Override // com.mxtech.videoplayer.list.IListContainer
    public void openUri(Uri uri) {
        if (!isFinishing()) {
            Bundle args = new Bundle();
            args.putString(MediaListFragment.KEY_TYPE, MediaListFragment.TYPE_URI);
            args.putParcelable(MediaListFragment.KEY_TARGET, uri);
            newFragment(args, true);
        }
    }

    public ListHelper getListHelper() {
        if (this._listHelper == null) {
            this._listHelper = new ListHelper(this, getLayoutInflater(), this._handler, this);
        }
        return this._listHelper;
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        updateHomeAsUp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle saved) {
        Intent intent = getIntent();
        Log.v(TAG, "onCreate(" + this + ") saved:" + saved + " intent:" + intent);
        super.onCreate(saved);
        this._tracker = ((App) App.context).getDefaultTracker();
        this._showPlayLastButton = App.prefs.getBoolean(Key.LIST_FLOATING_ACTION_BUTTON, DeviceUtils.hasTouchScreen);
        this._statusView = (TextView) findViewById(R.id.status);
        this._playLastButton = findViewById(R.id.play_last);
        if (((MXApplication) getApplication()).initInteractive(this)) {
            if (saved == null) {
                openIntent(intent, false);
            }
            if (!"android.intent.action.SEARCH".equals(intent.getAction()) && !isFinishing()) {
                App app = (App) getApplication();
                PackageInfo myPackage = L.getMyPackageInfo();
                Authorizer authorizer = L.authorizer;
                app.getClass();
                if (!authorizer.inquireUpdate(this, myPackage, 2, new App.VersionUpdateHandler(this))) {
                    Resources res = getResources();
                    if (res.getBoolean(R.bool.show_notice) && App.prefs.getInt(Key.NOTICED_VERSION, 0) < AppUtils.getVersion(myPackage.versionCode)) {
                        showNoticeDialog();
                    } else if (needOMXDecoderNotice()) {
                        notifyOMXDecoder();
                    }
                }
            }
        }
    }

    public void setPlayLastButton(String title) {
        this._playLastItemName = title;
        updatePlayLastMenuButton();
    }

    private void updatePlayLastMenuButtonState() {
        if (!this._showPlayLastButton) {
            this._playLastButton.setVisibility(8);
            this._playLastButton.setOnClickListener(null);
            this._playLastButton.setOnLongClickListener(null);
        }
        updatePlayLastMenuButton();
    }

    private void updatePlayLastMenuButton() {
        if (this._playLastItemName != null && getSplitToolbar() == null) {
            if (this._showPlayLastButton && this._playLastButton.getVisibility() != 0) {
                this._playLastButton.setVisibility(0);
                this._playLastButton.setOnClickListener(this);
                this._playLastButton.setOnLongClickListener(this);
            }
            if (this._playLastMenuItem != null) {
                this._playLastMenuItem.setEnabled(true);
                this._playLastMenuItem.setVisible(this._showPlayLastButton ? false : true);
                this._playLastMenuItem.setTitle(this._playLastItemName);
                return;
            }
            return;
        }
        if (this._showPlayLastButton && this._playLastButton.getVisibility() != 8) {
            this._playLastButton.setVisibility(8);
            this._playLastButton.setOnClickListener(null);
            this._playLastButton.setOnLongClickListener(null);
        }
        if (this._playLastMenuItem != null) {
            this._playLastMenuItem.setEnabled(false);
            this._playLastMenuItem.setVisible(false);
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity
    protected void onSplitToolbarAdded(Toolbar splitbar) {
        super.onSplitToolbarAdded(splitbar);
        updatePlayLastMenuButton();
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity
    protected void onSplitToolbarRemoved(Toolbar splitbar) {
        super.onSplitToolbarRemoved(splitbar);
        updatePlayLastMenuButton();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        MediaListFragment frag;
        if (v == this._playLastButton && (frag = (MediaListFragment) getCurrentFragment()) != null) {
            frag.playLast();
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (v != this._playLastButton || this._playLastItemName == null) {
            return false;
        }
        Toast toast = Toast.makeText(this, this._playLastItemName, 0);
        ViewUtils.positionToast(toast, this, v);
        toast.show();
        return true;
    }

    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case -2126866314:
                if (key.equals(Key.LIST_SELECTION_MODE)) {
                    c = 4;
                    break;
                }
                break;
            case -1301073427:
                if (key.equals(Key.MARK_LAST_PLAYED_MEDIA_FOR_EACH_FOLDERS)) {
                    c = 0;
                    break;
                }
                break;
            case -1269271051:
                if (key.equals(Key.LIST_VIEW)) {
                    c = 3;
                    break;
                }
                break;
            case -938226291:
                if (key.equals(Key.LIST_FIELDS)) {
                    c = 5;
                    break;
                }
                break;
            case -695276219:
                if (key.equals(Key.LIST_SORTS)) {
                    c = '\b';
                    break;
                }
                break;
            case -430050411:
                if (key.equals(Key.NEW_TAGGED_PERIOD)) {
                    c = 1;
                    break;
                }
                break;
            case 70025845:
                if (key.equals(Key.SUBTITLE_FOLDER)) {
                    c = 2;
                    break;
                }
                break;
            case 1354101259:
                if (key.equals(Key.LIST_LAST_MEDIA_TYPEFACE)) {
                    c = 6;
                    break;
                }
                break;
            case 1779678258:
                if (key.equals(Key.LIST_FLOATING_ACTION_BUTTON)) {
                    c = '\t';
                    break;
                }
                break;
            case 2030905492:
                if (key.equals(Key.LIST_DRAW_PLAYTIME_OVER_THUMBNAIL)) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
                rebuildAsync();
                return;
            case 3:
                clearBackStack();
                Intent intent = new Intent("android.intent.action.MAIN");
                setIntent(intent);
                openIntent(intent, false);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
                MediaListFragment frag = (MediaListFragment) getCurrentFragment();
                if (frag != null) {
                    frag.refresh(false);
                    return;
                }
                return;
            case '\b':
                MediaListFragment frag2 = (MediaListFragment) getCurrentFragment();
                if (frag2 != null) {
                    frag2.refresh(true);
                    return;
                }
                return;
            case '\t':
                this._showPlayLastButton = App.prefs.getBoolean(Key.LIST_FLOATING_ACTION_BUTTON, DeviceUtils.hasTouchScreen);
                updatePlayLastMenuButtonState();
                return;
            default:
                super.onSharedPreferenceChanged(prefs, key);
                return;
        }
    }

    @Override // com.mxtech.videoplayer.ActivityList, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("android.intent.action.SEARCH".equals(intent.getAction())) {
            openIntent(intent, true);
        }
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase
    protected void onNoticeDismissed(boolean byUser) {
        super.onNoticeDismissed(byUser);
        if (byUser && !isFinishing()) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putInt(Key.NOTICED_VERSION, AppUtils.getVersion(L.getMyPackageInfo().versionCode));
            AppUtils.apply(editor);
            if (needOMXDecoderNotice()) {
                notifyOMXDecoder();
            }
        }
    }

    private boolean needOMXDecoderNotice() {
        return L.hasOMXDecoder && !App.prefs.contains(Key.OMX_DECODER_NOTIFIED);
    }

    private void notifyOMXDecoder() {
        showDialog((ActivityMediaList) new AlertDialog.Builder(this).setTitle(R.string.hardware_acceleration).setMessage(L.localizeSettingsPath(R.string.notify_hardware_decoder)).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.ActivityMediaList.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putBoolean(Key.OMX_DECODER_NOTIFIED, true);
                AppUtils.apply(editor);
            }
        }).create());
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x009b -> B:25:0x007a). Please submit an issue!!! */
    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.videoplayer.ActivityThemed, com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        String codecPath;
        super.onStart();
        if (L.isUsingCustomCodec() && (codecPath = App.prefs.getString(Key.CUSTOM_CODEC_PATH, null)) != null) {
            File libffmpeg = new File(codecPath);
            if (libffmpeg.exists() && (libffmpeg.lastModified() != App.prefs.getLong(Key.CUSTOM_CODEC_libffmpeg_DATE, 0L) || libffmpeg.length() != App.prefs.getInt(Key.CUSTOM_CODEC_libffmpeg_SIZE, 0))) {
                try {
                    if (libffmpeg.length() != App.prefs.getInt(Key.CUSTOM_CODEC_libffmpeg_SIZE, 0) || L.getCustomCodecChecksum(codecPath) != App.prefs.getLong(Key.CUSTOM_CODEC_CHECKSUM, 0L)) {
                        SharedPreferences.Editor editor = App.prefs.edit();
                        editor.remove(Key.CUSTOM_CODEC_CHECKSUM);
                        editor.commit();
                        L.restart(this, R.string.restart_app_to_change_codec);
                    } else {
                        SharedPreferences.Editor editor2 = App.prefs.edit();
                        editor2.putLong(Key.CUSTOM_CODEC_libffmpeg_DATE, libffmpeg.lastModified());
                        AppUtils.apply(editor2);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Cannot get checksum from custom codec path.");
                }
            }
        }
        DecoderPreferences.checkCustomCodecFile(this);
        if (P.respectMediaButtons) {
            MediaButtonReceiver.registerReceiver(this, -100);
        }
    }

    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this._tracker.setScreenName("List");
        this._tracker.send(new HitBuilders.ScreenViewBuilder().build());
        FlurryAgent.onStartSession(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        FlurryAgent.onEndSession(this);
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase
    public View getSnackbarParent() {
        return this.topLayout;
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        MediaButtonReceiver.unregisterReceiver(this);
        this._handler.removeMessages(100);
        if (this._scanTask != null) {
            this._scanTask.cancel();
        }
    }

    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this._listHelper != null) {
            if (AppUtils.isQuitting()) {
                this._listHelper.close(true);
                MediaDatabase.releaseSingleton();
            } else {
                this._listHelper.close(false);
            }
        }
        synchronized (this) {
            unbindService();
        }
    }

    @Override // com.mxtech.videoplayer.MediaButtonReceiver.IReceiver
    public final void onMediaKeyReceived(KeyEvent event) {
        handleMediaKeyEvent(event);
    }

    private void handleMediaKeyEvent(KeyEvent event) {
        MediaListFragment frag;
        switch (event.getKeyCode()) {
            case 79:
            case 85:
            case TransportMediator.KEYCODE_MEDIA_PLAY /* 126 */:
                if (event.getAction() == 1 && (frag = (MediaListFragment) getCurrentFragment()) != null) {
                    frag.playLast();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.mxtech.videoplayer.ActivityList, android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.v(TAG, "KeyEvent: action=" + event.getAction() + " keyCode=" + keyCode + " repeat=" + event.getRepeatCount());
        if (keyCode == 102) {
            if (event.getAction() == 1) {
                playLast();
                return true;
            }
            return true;
        } else if (keyCode == 103) {
            if (event.getAction() == 1) {
                rescan(1);
                return true;
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ScanTask extends AsyncTask2<File, Void, Void> {
        private MediaScanner _scanner;

        private ScanTask() {
            this._scanner = new MediaScanner(P.copyVideoExts(), P.scanRoots);
        }

        void cancel() {
            this._scanner.interrupt();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(File... roots) {
            try {
                this._scanner.scan(roots);
                try {
                    MediaDatabase mdb = MediaDatabase.getInstance();
                    try {
                        mdb.db.beginTransaction();
                        mdb.resetVideoFiles(this._scanner);
                        mdb.db.setTransactionSuccessful();
                        mdb.db.endTransaction();
                        return null;
                    } finally {
                        mdb.release();
                    }
                } catch (SQLiteException e) {
                    Log.e(ActivityMediaList.TAG, "", e);
                    publishProgress(new Void[0]);
                    return null;
                }
            } catch (InterruptedException e2) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Void... values) {
            if (!ActivityMediaList.this.isFinishing()) {
                DialogUtils.alert(ActivityMediaList.this, R.string.error_database);
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            ActivityMediaList.this._scanTask = null;
            ActivityMediaList.this.stopSpin();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            ActivityMediaList.this._scanTask = null;
            ActivityMediaList.this.stopSpin();
            L.observer.resetAsync();
        }
    }

    @Override // com.mxtech.videoplayer.ActivityList
    protected boolean rescan(int from) {
        if (!canStartRescan()) {
            return false;
        }
        ArrayList<File> list = new ArrayList<>(P.scanRoots.size());
        for (Map.Entry<File, Boolean> root : P.scanRoots.entrySet()) {
            if (root.getValue().booleanValue()) {
                list.add(root.getKey());
            }
        }
        this._scanTask = new ScanTask();
        this._scanTask.executeParallel(list.toArray(new File[list.size()]));
        startSpin(from);
        return true;
    }

    @Override // com.mxtech.videoplayer.ActivityList
    protected boolean canStartRescan() {
        return this._scanTask == null && super.canStartRescan();
    }

    @Override // com.mxtech.videoplayer.ActivityList, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!super.onCreateOptionsMenu(menu)) {
            return false;
        }
        this._playLastMenuItem = menu.findItem(R.id.play_last);
        updatePlayLastMenuButtonState();
        return true;
    }

    @Override // com.mxtech.videoplayer.ActivityList, android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        ((App) getApplication()).updateHelpCommand(menu);
        return true;
    }

    @Override // com.mxtech.videoplayer.ActivityList, com.mxtech.app.MXAppCompatActivity, com.mxtech.app.PopupMenuHack.OptionsItemSelector
    public boolean onOptionsItemSelected2(MenuItem item) {
        if (isFinishing()) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.media_scan) {
            rescan(1);
            return true;
        } else if (id == R.id.play_last) {
            playLast();
            return true;
        } else {
            return super.onOptionsItemSelected2(item);
        }
    }

    private void playLast() {
        MediaListFragment frag = (MediaListFragment) getCurrentFragment();
        if (frag != null) {
            frag.playLast();
        }
    }

    public static void rebuildAll() {
        for (ActivityMediaList list : ActivityRegistry.findByClass(ActivityMediaList.class)) {
            list.rebuildAsync();
        }
    }

    @Override // com.mxtech.videoplayer.list.IListContainer
    public final void rebuildAsync() {
        rebuildDelayed(0);
    }

    @Override // com.mxtech.videoplayer.list.IListContainer
    public final void rebuildDelayed(int millis) {
        this._handler.removeMessages(100);
        this._handler.sendEmptyMessageDelayed(100, millis);
    }

    public final void cancelDelayedRebuild() {
        this._handler.removeMessages(100);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        MediaListFragment frag;
        if (msg.what == 100) {
            if (this._scanTask == null && this.started && (frag = (MediaListFragment) getCurrentFragment()) != null) {
                frag.build();
            }
            return true;
        }
        return false;
    }

    @Override // com.mxtech.videoplayer.list.IListContainer
    public void setStatusText(CharSequence text) {
        if (text != null && text.length() > 0) {
            this._statusView.setText(text);
            this._statusView.setVisibility(0);
            return;
        }
        this._statusView.setVisibility(8);
    }

    @Override // android.app.Activity
    public void onUserInteraction() {
        super.onUserInteraction();
        MediaListFragment frag = (MediaListFragment) getCurrentFragment();
        if (frag != null) {
            frag.onUserInteraction();
        }
    }

    @Override // com.mxtech.videoplayer.list.IListContainer, com.mxtech.videoplayer.MediaLoader.Client
    public final IFFService getFFService(IFFService service) {
        IFFService iFFService = null;
        synchronized (this) {
            if (service != null) {
                this._serviceRefCount--;
            }
            if (this._service != null) {
                if (this._service.asBinder().isBinderAlive()) {
                    this._serviceRefCount++;
                    iFFService = this._service;
                } else {
                    this._service = null;
                }
            }
            if (!this._connecting) {
                Intent i = new Intent(this, FFService.class);
                if (L.customFFmpegPath != null) {
                    i.putExtra(FFService.EXTRA_CUSTOM_FFMPEG_PATH, L.customFFmpegPath);
                }
                i.putExtra(FFService.EXTRA_CODEC_PACKAGE_OR_SYSTEM_LIB, L.codecPackageOrSystemLib);
                if (!bindService(i, this, 129)) {
                    Log.e(TAG, "FF Service binding failed.");
                    this._bound = false;
                } else {
                    this._bound = true;
                    this._connecting = true;
                }
            }
            if (Process.myPid() != Process.myTid()) {
                while (true) {
                    SystemClock.sleep(500L);
                    synchronized (this) {
                        if (!this._connecting) {
                            break;
                        }
                    }
                }
                if (this._service != null) {
                    this._serviceRefCount++;
                }
                iFFService = this._service;
            }
        }
        return iFFService;
    }

    @Override // com.mxtech.videoplayer.list.IListContainer, com.mxtech.videoplayer.MediaLoader.Client
    public final synchronized void releaseFFService(IFFService service) {
        int i = this._serviceRefCount - 1;
        this._serviceRefCount = i;
        if (i <= 0) {
            unbindService();
        }
    }

    private void unbindService() {
        if (this._bound) {
            this._service = null;
            this._bound = false;
            this._connecting = false;
            try {
                unbindService(this);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Unable to unbind from media service (already unbound)", e);
            }
        }
    }

    @Override // com.mxtech.videoplayer.list.IListContainer
    public final synchronized void scheduleToRunOnServiceConnection(Runnable r) {
        if (this._serviceConnRunnables == null) {
            this._serviceConnRunnables = new ArrayList<>();
        }
        this._serviceConnRunnables.add(r);
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName name, IBinder binder) {
        ArrayList<Runnable> runs;
        Log.i(TAG, "Connected to " + name);
        synchronized (this) {
            this._service = IFFService.Stub.asInterface(binder);
            this._connecting = false;
            runs = this._serviceConnRunnables;
            this._serviceConnRunnables = null;
        }
        if (runs != null) {
            Iterator<Runnable> it = runs.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                r.run();
            }
        }
    }

    @Override // android.content.ServiceConnection
    public final synchronized void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "Disconnected from " + name);
        this._service = null;
        this._connecting = false;
    }
}
