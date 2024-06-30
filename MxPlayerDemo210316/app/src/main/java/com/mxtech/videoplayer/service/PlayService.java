package com.mxtech.videoplayer.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RemoteControlClient;
//import android.media.ThumbnailSafeUtils;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.media.TransportMediator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.flurry.android.FlurryAgent;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.drive.DriveFile;
//import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
//import com.mxtech.app.ActivityRegistry;
//import com.mxtech.app.MXApplication;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.JointPlayer;
//import com.mxtech.media.Playlist;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.SubStationAlphaMedia;
//import com.mxtech.videoplayer.ActivityMediaList;
//import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.App;
//import com.mxtech.videoplayer.IScreen;
import com.mxtech.videoplayer.L;
//import com.mxtech.videoplayer.MediaButtonReceiver;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.Player;
//import com.mxtech.videoplayer.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.pro.R;
//import com.mxtech.videoplayer.preference.P;
//import com.mxtech.videoplayer.widget.SleepTimer;

import java.io.File;
import java.util.List;
import java.util.Locale;

@TargetApi(14)
/* loaded from: classes.dex */
public final class PlayService extends Service implements Player.Client, OrderedSharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onSharedPreferenceChanged(OrderedSharedPreferences orderedSharedPreferences, String str) {

    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void load(Uri uri, byte b, int i) {

    }

    @Override
    public int mediaTimeToSubtitleTime(int i) {
        return 0;
    }

    @Override
    public void onAudioCodecUnsupported(FFPlayer fFPlayer, int i) {

    }

    @Override
    public void onAudioStreamChanged(JointPlayer jointPlayer, int i) {

    }

    @Override
    public void onBufferingUpdate(int i) {

    }

    @Override
    public void onCoverArtChanged() {

    }

    @Override
    public void onDurationKnown(int i) {

    }

    @Override
    public void onEmbeddedSubtitleAdded(ISubtitle iSubtitle) {

    }

    @Override
    public void onNetworkListingComplete() {

    }

    @Override
    public void onPresentingStateChanged(boolean z) {

    }

    @Override
    public void onRebootToChangeDisplay(int i) {

    }

    @Override
    public void onRemoteResourceLoaded(List<ISubtitle> list, Bitmap bitmap, Uri uri) {

    }

    @Override
    public void onRemoteResourceLoadingBegin(int i) {

    }

    @Override
    public void onRemoteResourceLoadingCanceled() {

    }

    @Override
    public void onSSAPrepared(SubStationAlphaMedia subStationAlphaMedia) {

    }

    @Override
    public void onSeekBegin(int i) {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onSetupFontCache(boolean z) {

    }

    @Override
    public void onStateChanged(int i, int i2) {

    }

    @Override
    public void onSubtitleClosed(ISubtitle iSubtitle) {

    }

    @Override
    public void onSubtitleInvalidated() {

    }

    @Override
    public void onSubtitlesClosed() {

    }

    @Override
    public void onTryNextDecoder(byte b, byte b2, boolean z) {

    }

    @Override
    public void onVideoDeviceChanged() {

    }

    @Override
    public void onVideoFilteringFailed(int i) {

    }

    @Override
    public void onVideoSizeChanged(int i, int i2) {

    }

    @Override
    public void update(Player player, int i) {

    }

    @Override
    public void updatePersistent(Uri uri, MediaDatabase.State state, List<ISubtitle> list) {

    }
    public static final int ART_WORK_SDK_INT = 21;
    private static final int CHANGE_ALBUM_ART = 4;
    private static final int CHANGE_COVER = 2;
    private static final int CHANGE_SOURCE = 1;
    private static final String CMD_BACKWARD = "rew";
    private static final String CMD_CLOSE = "close";
    private static final String CMD_FORWARD = "ff";
    private static final String CMD_NEXT = "next";
    private static final String CMD_PLAYPAUSE = "playpause";
    private static final String CMD_PREV = "prev";
    private static final int FIRST_START_AFTER = 500;
    private static final int FLAG_NO_LOAD_REMOTE_COVER = 268435456;
    private static final int NOTIFY_ID = 1;
    private static final String SCHEME = "cmd";
    private static final String TAG = "MX.PlayService";
    public static PlayService instance = null;
    public static final String kLauncher = "com.mxtech.videoplayer.service.PlayService";
//    private static ServiceStateHandler stateHandler;
    private Bitmap _albumArtSourceBitmap;
    private final Binding _binding = new Binding();
    private final BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() { // from class: com.mxtech.videoplayer.service.PlayService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (PlayService.this.pp != null) {
                String action = intent.getAction();
                if ("android.media.AUDIO_BECOMING_NOISY".equals(action)) {
                    if (App.prefs.getBoolean(Key.PAUSE_ON_HEADSET_DISCONNECTED, true)) {
                        PlayService.this.pp.pause(1);
                    }
                } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                    PlayService.this._headsetPlugged = intent.getIntExtra("state", 0) == 1;
//                    PlayService.this.updateStereoMode();
//                    if (PlayService.this._headsetPlugged) {
//                        PlayService.this.pp.resume();
//                    }
                }
            }
        }
    };
    private final IntentFilter _broadcastReceivingFilter = new IntentFilter();
    private boolean _foreground;
    private boolean _headsetPlugged;
    private Intent _intent;
    private int _naviMoveIntervalMillis;
    private boolean _needArtWork;
    private NotificationManager _nm;
    private Notification _noti;
    private int _orientation;
    private OrientationEventListener _orientationEventListener;
    private boolean _receivingMediaButtonEvents;
    private RemoteControlClient _remoteControlClient;
    private Bundle _savedStates;
    @Nullable
//    private Screen _screen;
    private int _stereoMode;
//    private Tracker _tracker;
    private int _updateCount;
    @Nullable
    private Player pp;

    /* loaded from: classes.dex */
//    public interface Screen extends IScreen {
//        void onReturnPlayer(PlayService playService, Player player, Bundle bundle);
//    }

    /* loaded from: classes.dex */
    public class Binding extends Binder {
        public Binding() {
        }

        public PlayService getService() {
            return PlayService.this;
        }
    }

//    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
//    @Override // com.mxtech.videoplayer.MediaButtonReceiver.IReceiver
//    public void onMediaKeyReceived(KeyEvent event) {
//        if (this.pp != null) {
//            int action = event.getAction();
//            int keyCode = event.getKeyCode();
//            int repeatCount = event.getRepeatCount();
//            if (P.prevnext_to_rewff) {
//                if (keyCode == 87) {
//                    keyCode = 90;
//                } else if (keyCode == 88) {
//                    keyCode = 89;
//                }
//            }
//            if (action == 0) {
//                switch (keyCode) {
//                    case 89:
//                        seekToDelta(-this._naviMoveIntervalMillis);
//                        return;
//                    case 90:
//                        seekToDelta(this._naviMoveIntervalMillis);
//                        return;
//                    default:
//                        return;
//                }
//            } else if (action == 1 && repeatCount == 0) {
//                switch (keyCode) {
//                    case 79:
//                    case 85:
//                        break;
//                    case 86:
//                        clear(false);
//                        return;
//                    case 87:
//                        this.pp.next();
//                        return;
//                    case 88:
//                        this.pp.previous(false);
//                        return;
//                    case TransportMediator.KEYCODE_MEDIA_PLAY /* 126 */:
//                        if (!P.toggleOnMediaPlayButton) {
//                            this.pp.start();
//                            return;
//                        }
//                        break;
//                    case TransportMediator.KEYCODE_MEDIA_PAUSE /* 127 */:
//                        this.pp.pause();
//                        return;
//                    default:
//                        return;
//                }
//                this.pp.toggle();
//            }
//        }
//    }
//
//    /* JADX INFO: Access modifiers changed from: private */
//    /* loaded from: classes.dex */
//    public static class ServiceStateHandler extends Handler implements ActivityRegistry.StateChangeListener {
//        private static final int MSG_DELAYED_STOP = 1;
//        private static final int MSG_FIRST_START = 3;
//        private static final int MSG_NEXT_VIDEO = 2;
//        private boolean _started;
//
//        private ServiceStateHandler() {
//            this._started = false;
//        }
//
//        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
//        public void onActivityStateChanged(Activity activity, int state) {
//            if (activity instanceof ActivityScreen) {
//                update();
//            }
//        }
//
//        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
//        public void onActivityRemoved(Activity activity) {
//        }
//
//        @Override // com.mxtech.app.ActivityRegistry.StateChangeListener
//        public void onActivityCreated(Activity activity) {
//        }
//
//        @Override // android.os.Handler
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    update(false);
//                    return;
//                case 2:
//                    if (PlayService.instance != null) {
//                        PlayService.instance.nextAuto();
//                        return;
//                    }
//                    return;
//                case 3:
//                    PlayService.instance.firstStart((Uri) msg.obj);
//                    return;
//                default:
//                    return;
//            }
//        }
//
//        public void update() {
//            removeMessages(1);
//            update(true);
//        }
//
//        private void update(boolean delayStop) {
//            if (this._started) {
//                if (PlayService.instance != null && !PlayService.instance.hasPlayer() && !ActivityRegistry.hasVisibleActivity(ActivityScreen.class)) {
//                    if (delayStop) {
//                        sendEmptyMessageDelayed(1, 1000L);
//                        return;
//                    }
//                    PlayService.instance.stopSelf();
//                    this._started = false;
//                }
//            } else if (ActivityRegistry.hasVisibleActivity(ActivityScreen.class)) {
//                if (App.context.startService(new Intent(App.context, PlayService.class)) == null) {
//                    Log.e(PlayService.TAG, "Can't start the play service.");
//                } else {
//                    this._started = true;
//                }
//            }
//        }
//    }
//
//    public static void init() {
//        stateHandler = new ServiceStateHandler();
//        ActivityRegistry.registerStateListener(stateHandler);
//    }
//
//    public PlayService() {
//        this._broadcastReceivingFilter.addAction("android.media.AUDIO_BECOMING_NOISY");
//        this._broadcastReceivingFilter.addAction("android.intent.action.HEADSET_PLUG");
//    }

    @SuppressLint("WrongConstant")
    @Override // android.app.Service
    public void onCreate() {
        Log.v(TAG, "onCreate");
        instance = this;
        this._naviMoveIntervalMillis = App.prefs.getInt(Key.NAVI_MOVE_INTERVAL, 10) * 1000;
        this._nm = (NotificationManager) getSystemService("notification");
        this._needArtWork = App.prefs.getBoolean(Key.LOCK_SCREEN_ART_WORK, true);
        this._orientation = getResources().getConfiguration().orientation;
        this._stereoMode = App.prefs.getInt(Key.STEREO_MODE, 0);
        App.prefs.registerOnSharedPreferenceChangeListener(this);
//        stateHandler.update();
        super.onCreate();
    }

//    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
//    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
//        char c = 65535;
//        switch (key.hashCode()) {
//            case -1171355494:
//                if (key.equals(Key.NAVI_MOVE_INTERVAL)) {
//                    c = 2;
//                    break;
//                }
//                break;
//            case -1154108890:
//                if (key.equals(Key.MEDIA_BUTTONS)) {
//                    c = 0;
//                    break;
//                }
//                break;
//            case -151612022:
//                if (key.equals(Key.STEREO_MODE)) {
//                    c = 3;
//                    break;
//                }
//                break;
//            case 249780371:
//                if (key.equals(Key.LOCK_SCREEN_ART_WORK)) {
//                    c = 4;
//                    break;
//                }
//                break;
//            case 1615331941:
//                if (key.equals(Key.OMX_DECODER_ALTERNATIVE)) {
//                    c = 1;
//                    break;
//                }
//                break;
//        }
//        switch (c) {
//            case 0:
//                updateMediaButtonEventsReception();
//                return;
//            case 1:
//                clear(false);
//                return;
//            case 2:
//                this._naviMoveIntervalMillis = App.prefs.getInt(Key.NAVI_MOVE_INTERVAL, 10) * 1000;
//                return;
//            case 3:
//                this._stereoMode = App.prefs.getInt(Key.STEREO_MODE, 0);
//                updateStereoMode();
//                return;
//            case 4:
//                this._needArtWork = App.prefs.getBoolean(Key.LOCK_SCREEN_ART_WORK, true);
//                updateLockScreenArtwork();
//                return;
//            default:
//                return;
//        }
//    }
//
//    /* JADX INFO: Access modifiers changed from: private */
//    public void updateStereoMode() {
//        if (this.pp != null && this.pp.mp() != null) {
//            if (this._stereoMode == 99) {
//                this.pp.setStereoMode(ActivityScreen.resolveAutoReverseStereoMode(this, this._headsetPlugged, ((WindowManager) getSystemService("window")).getDefaultDisplay()));
//                if (this._orientationEventListener == null) {
//                    this._orientationEventListener = new OrientationEventListener(this) { // from class: com.mxtech.videoplayer.service.PlayService.2
//                        @Override // android.view.OrientationEventListener
//                        public void onOrientationChanged(int orientation) {
//                            PlayService.this.updateStereoMode();
//                        }
//                    };
//                    this._orientationEventListener.enable();
//                    return;
//                }
//                return;
//            }
//            this.pp.setStereoMode(this._stereoMode);
//        }
//        if (this._orientationEventListener != null) {
//            this._orientationEventListener.disable();
//        }
//    }
//
//    private void updateMediaButtonEventsReception() {
//        if (P.respectMediaButtons && this.pp != null) {
//            if (!this._receivingMediaButtonEvents) {
//                this._receivingMediaButtonEvents = MediaButtonReceiver.registerReceiver(this, 0);
//            }
//        } else if (this._receivingMediaButtonEvents) {
//            MediaButtonReceiver.unregisterReceiver(this);
//            this._receivingMediaButtonEvents = false;
//        }
//    }
//
//    private void seekToDelta(int delta) {
//        if (this.pp.isInPlaybackState()) {
//            this.pp.seekTo(this.pp.getCurrentPosition() + delta, this.pp.getShortSeekTimeout());
//        }
//    }

    @SuppressLint("WrongConstant")
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri uri;
        if (this.pp != null && intent != null && (uri = intent.getData()) != null && SCHEME.equals(uri.getScheme())) {
            String ssp = uri.getSchemeSpecificPart();
            if ("close".equals(ssp)) {
                this.pp.pause();
//                clear(false);
                return 2;
            } else if (CMD_PLAYPAUSE.equals(ssp)) {
                this.pp.toggle();
                return 2;
            } else if (CMD_NEXT.equals(ssp)) {
                this.pp.next();
                return 2;
            } else if (CMD_PREV.equals(ssp)) {
                this.pp.previous();
                return 2;
            } else if (CMD_BACKWARD.equals(ssp)) {
//                seekToDelta(-this._naviMoveIntervalMillis);
                return 2;
            } else if (CMD_FORWARD.equals(ssp)) {
//                seekToDelta(this._naviMoveIntervalMillis);
                return 2;
            } else {
                return 2;
            }
        }
        return 2;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "Bind");
        return this._binding;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "Unbind");
        return super.onUnbind(intent);
    }

//    @Override // android.app.Service
//    public void onDestroy() {
//        Log.v(TAG, "onDestroy");
//        App.prefs.unregisterOnSharedPreferenceChangeListener(this);
//        clear(false);
//        instance = null;
//        if (App.quitting) {
//            App.quit();
//        }
//        super.onDestroy();
//    }

//    public boolean hasPlayer() {
//        return this.pp != null;
//    }
//
//    public void unlinkScreen(Screen screen) {
//        if (this._screen == screen) {
//            this._screen = null;
//        }
//    }
//
//    public void transferPlayer(@Nullable Screen screen, Player player, Class screenClass, Intent activityIntent, Bundle savedStates) {
//        if (this.pp != null) {
//            throw new IllegalStateException("PP already exists.");
//        }
//        Log.d(TAG, "Transfer Player(" + player + ") from " + screen);
//        this.pp = player;
//        this._screen = screen;
//        this._savedStates = savedStates;
//        this.pp.setClient(this);
//        this._intent = new Intent(activityIntent).setPackage(getPackageName()).setClass(getApplicationContext(), screenClass).setFlags(DriveFile.MODE_READ_WRITE).putExtra(ActivityScreen.EXTRA_LAUNCHER, kLauncher);
//        updateNotification(5);
//        registerReceiver(this._broadcastReceiver, this._broadcastReceivingFilter);
//        updateStereoMode();
//        updateMediaButtonEventsReception();
//        stateHandler.sendMessageDelayed(stateHandler.obtainMessage(3, this.pp.getUri()), this.pp.getState() == 4 ? FIRST_START_AFTER : 0);
//        this._tracker = ((App) App.context).getDefaultTracker();
//        this._tracker.setScreenName("BackgroundPlay");
//        this._tracker.send(new HitBuilders.ScreenViewBuilder().build());
//        FlurryAgent.onStartSession(this);
//    }
//
//    /* JADX INFO: Access modifiers changed from: private */
//    public void firstStart(@Nullable Uri mediaUri) {
//        if (this.pp != null && mediaUri != null && mediaUri.equals(this.pp.getUri())) {
//            if (this.pp.getState() == 6) {
//                onPlaybackComplete();
//            } else if (this.pp.isInPlaybackState() && isPlayable()) {
//                this.pp.resume();
//            } else if (!this.pp.skipCurrent()) {
//                clear(true);
//            }
//        }
//    }
//
//    /* loaded from: classes.dex */
//    public static class ReturnData {
//        @NonNull
//        public final Intent intent;
//        public final Player player;
//        public final Bundle savedStates;
//
//        public ReturnData(@NonNull Intent intent, Player player, Bundle savedStates) {
//            this.intent = intent;
//            this.player = player;
//            this.savedStates = savedStates;
//        }
//
//        public void close() {
//            this.player.close();
//            Playlist.Autogen.cleanup(this.intent);
//        }
//    }
//
//    @Nullable
//    public Uri playUri() {
//        if (this.pp != null) {
//            return this.pp.getUri();
//        }
//        return null;
//    }
//
//    @Nullable
//    public ReturnData returnPlayer(@Nullable Screen caller) {
//        if (this.pp == null || this._intent == null) {
//            return null;
//        }
//        Log.d(TAG, "Return " + this.pp + " to " + caller + " original screen " + this._screen + ".");
//        ReturnData returnData = new ReturnData(this._intent, this.pp, this._savedStates);
//        this.pp.cancelRemoteLoading();
//        Screen screen = this._screen;
//        this.pp = null;
//        this._screen = null;
//        this._savedStates = null;
//        this._intent = null;
//        this._noti = null;
//        if (this._orientationEventListener != null) {
//            this._orientationEventListener.disable();
//            this._orientationEventListener = null;
//        }
//        updateMediaButtonEventsReception();
//        updateNotification(4);
//        if (screen != null && screen != caller) {
//            Log.d(TAG, "Finish previous screen " + screen);
//            screen.finish();
//        }
//        try {
//            unregisterReceiver(this._broadcastReceiver);
//        } catch (IllegalArgumentException e) {
//            Log.e(TAG, "", e);
//        }
//        stateHandler.update();
//        FlurryAgent.onEndSession(this);
//        return returnData;
//    }
//
//    private void clear(boolean finishScreen) {
//        if (this.pp != null) {
//            this.pp.save();
//        }
//        Screen screen = this._screen;
//        ReturnData ret = returnPlayer(finishScreen ? null : this._screen);
//        if (ret != null) {
//            if (finishScreen || screen == null) {
//                ret.close();
//                ActivityMediaList.rebuildAll();
//                return;
//            }
//            screen.onReturnPlayer(this, ret.player, ret.savedStates);
//        }
//    }
//
//    @SuppressLint({"InlinedApi", "NewApi"})
//    private Notification buildNotification(int changes) {
//        if (this._noti == null) {
//            this._noti = new Notification();
//            this._noti.icon = R.drawable.ic_background_play;
//            this._noti.contentView = new RemoteViews(getPackageName(), R.layout.playservice_notification);
//            if (Build.VERSION.SDK_INT < 21) {
//                this._noti.audioStreamType = 3;
//            } else {
//                this._noti.audioAttributes = new AudioAttributes.Builder().setUsage(1).setLegacyStreamType(3).setContentType(2).build();
//                this._noti.color = getResources().getColor(R.color.playback_noti_primary_text_color);
//            }
//            if (Build.VERSION.SDK_INT >= 16) {
//                this._noti.priority = 1;
//                if (Build.VERSION.SDK_INT >= 21) {
//                    this._noti.category = "transport";
//                    this._noti.visibility = 1;
//                }
//                this._noti.bigContentView = new RemoteViews(getPackageName(), R.layout.playservice_notification_big);
//            }
//            PendingIntent closeIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, "close", null), this, getClass()), 0);
//            PendingIntent prevIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, CMD_PREV, null), this, getClass()), 0);
//            PendingIntent nextIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, CMD_NEXT, null), this, getClass()), 0);
//            PendingIntent backwardIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, CMD_BACKWARD, null), this, getClass()), 0);
//            PendingIntent forwardIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, CMD_FORWARD, null), this, getClass()), 0);
//            PendingIntent playpauseIntent = PendingIntent.getService(this, 0, new Intent(null, Uri.fromParts(SCHEME, CMD_PLAYPAUSE, null), this, getClass()), 0);
//            if (Build.VERSION.SDK_INT >= 11) {
//                if (this._noti.contentView != null) {
//                    this._noti.contentView.setOnClickPendingIntent(R.id.close, closeIntent);
//                    this._noti.contentView.setOnClickPendingIntent(R.id.prev, prevIntent);
//                    this._noti.contentView.setOnClickPendingIntent(R.id.next, nextIntent);
//                    this._noti.contentView.setOnClickPendingIntent(R.id.backward, backwardIntent);
//                    this._noti.contentView.setOnClickPendingIntent(R.id.forward, forwardIntent);
//                    this._noti.contentView.setOnClickPendingIntent(R.id.playpause, playpauseIntent);
//                }
//                if (Build.VERSION.SDK_INT >= 16 && this._noti.bigContentView != null) {
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.close, closeIntent);
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.prev, prevIntent);
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.next, nextIntent);
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.backward, backwardIntent);
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.forward, forwardIntent);
//                    this._noti.bigContentView.setOnClickPendingIntent(R.id.playpause, playpauseIntent);
//                }
//            }
//            this._noti.deleteIntent = closeIntent;
//            changes = -1;
//        }
//        if (this._noti.contentView != null) {
//            updateViews(this._noti.contentView, changes, false);
//        }
//        if (Build.VERSION.SDK_INT >= 16 && this._noti.bigContentView != null) {
//            updateViews(this._noti.bigContentView, changes, true);
//        }
//        this._updateCount = 0;
//        return this._noti;
//    }
//
//    @SuppressLint({"NewApi"})
//    private void updateViews(RemoteViews views, int changes, boolean bigView) {
//        if (this.pp.isInPlaybackState()) {
//            DateUtils.formatElapsedTime(L.sb, this.pp.getDuration() / 1000);
//            L.sb.insert(0, "%s / ");
//            views.setChronometer(R.id.progress, SystemClock.elapsedRealtime() - this.pp.getCurrentPosition(), L.sb.toString(), this.pp.getState() == 5);
//        } else {
//            views.setChronometer(R.id.progress, SystemClock.elapsedRealtime(), "--:--:--", false);
//        }
//        views.setViewVisibility(R.id.sleep_timer, L.sleepTimer != null ? 0 : 8);
//        if (Build.VERSION.SDK_INT >= 11) {
//            views.setImageViewResource(R.id.playpause, this.pp.shouldPresentPlaying() ? R.drawable.ic_button_pause : R.drawable.ic_button_play);
//            if (!bigView) {
//                if (Build.VERSION.SDK_INT < 21) {
//                    views.setViewVisibility(R.id.prev, DeviceUtils.getScreenWidthDp(getResources()) >= FIRST_START_AFTER ? 0 : 8);
//                } else {
//                    views.setViewVisibility(R.id.close, 8);
//                }
//            }
//        }
//        if ((changes & 1) != 0) {
//            String title = this._intent.getStringExtra("title");
//            if (title == null) {
//                title = this.pp.getTitle();
//            }
//            views.setTextViewText(R.id.title, title);
//            setCover(views, R.id.cover, (changes & 268435456) == 0, bigView);
//            this._noti.contentIntent = PendingIntent.getActivity(this, 0, this._intent, 0);
//        } else if ((changes & 2) != 0) {
//            setCover(views, R.id.cover, (changes & 268435456) == 0, bigView);
//        }
//    }
//
//    @TargetApi(14)
//    private void setArtWork() {
//        if (this._remoteControlClient == null) {
//            Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
//            mediaButtonIntent.setComponent(MediaButtonReceiver.getComponentName());
//            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
//            this._remoteControlClient = new RemoteControlClient(mediaPendingIntent);
//            AudioManager audioManager = (AudioManager) getSystemService("audio");
//            audioManager.registerRemoteControlClient(this._remoteControlClient);
//        }
//        Bitmap newAlbumArt = this.pp.getCover(this._orientation);
//        if (newAlbumArt != this._albumArtSourceBitmap) {
//            this._albumArtSourceBitmap = newAlbumArt;
//            RemoteControlClient.MetadataEditor editor = this._remoteControlClient.editMetadata(true);
//            Bitmap copied = null;
//            if (newAlbumArt != null) {
//                try {
//                    Bitmap.Config config = newAlbumArt.getConfig();
//                    if (config == null) {
//                        config = Bitmap.Config.ARGB_8888;
//                    }
//                    copied = newAlbumArt.copy(config, true);
//                } catch (OutOfMemoryError e) {
//                    this._albumArtSourceBitmap = null;
//                    Log.e(TAG, "", e);
//                    this._needArtWork = false;
//                    clearArtWork(true);
//                    return;
//                }
//            }
//            editor.putBitmap(100, copied);
//            editor.apply();
//        }
//    }
//
//    @TargetApi(14)
//    private void clearArtWork(boolean reset) {
//        if (this._remoteControlClient != null) {
//            RemoteControlClient.MetadataEditor editor = this._remoteControlClient.editMetadata(true);
//            editor.putBitmap(100, (Bitmap) null);
//            editor.apply();
//            if (reset) {
//                AudioManager audioManager = (AudioManager) getSystemService("audio");
//                audioManager.unregisterRemoteControlClient(this._remoteControlClient);
//                this._remoteControlClient = null;
//            }
//        }
//        this._albumArtSourceBitmap = null;
//    }
//
//    private void updateLockScreenArtwork() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            boolean reset = !this._needArtWork || this.pp == null;
//            if (!reset && this.pp.isInPlaybackState()) {
//                setArtWork();
//            } else {
//                clearArtWork(reset);
//            }
//        }
//    }
//
//    /* JADX WARN: Code restructure failed: missing block: B:15:0x0047, code lost:
//        if (r10.pp.isRemoteLoading() == false) goto L22;
//     */
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    private void setCover(RemoteViews views, int id, boolean loadRemoteCover, boolean bigView) {
//        Bitmap cover;
//        Resources res = getResources();
//        Bitmap cover2 = null;
//        try {
//            int coverWidth = res.getDimensionPixelSize(R.dimen.noti_cover_width);
//            int coverHeight = res.getDimensionPixelSize(R.dimen.noti_cover_height);
//            File mediaFile = this.pp.getFile();
//            if (this.pp.hasCover()) {
//                cover2 = this.pp.getCover(coverWidth, coverHeight);
//            } else if (this.pp.isInPlaybackState()) {
//                if (loadRemoteCover) {
//                    this.pp.loadRemoteCovers();
//                }
//                if (this.pp.loadLocalCovers() != null) {
//                    cover2 = this.pp.getCover(coverWidth, coverHeight);
//                } else if (mediaFile != null) {
//                    try {
//                        MediaDatabase mdb = MediaDatabase.getInstance();
//                        try {
//                            int fileId = mdb.getFileID(mediaFile);
//                            if (fileId != 0) {
//                                cover2 = L.thumbStorage.read(fileId, mediaFile);
//                            }
//                        } finally {
//                            mdb.release();
//                        }
//                    } catch (SQLiteException e) {
//                        Log.e(MXApplication.TAG, "", e);
//                    }
//                }
//            }
//            if (cover2 != null && (cover = ThumbnailSafeUtils.extractThumbnail(cover2, coverWidth, coverHeight, 0)) != null) {
//                views.setImageViewBitmap(id, cover);
//                return;
//            }
//        } catch (OutOfMemoryError e2) {
//            Log.e(TAG, "", e2);
//        }
//        views.setImageViewResource(id, R.drawable.ic_music_box_grey600_24dp);
//        if (Build.VERSION.SDK_INT >= 8) {
//            views.setInt(id, "setBackgroundColor", res.getColor(R.color.playback_noti_image_background_color));
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onCoverArtChanged() {
//        updateNotification(6);
//    }

    private void updateNotification(int changes) {
//        try {
//            if (this.pp == null) {
//                if (this._foreground) {
//                    stopForeground(true);
//                    this._foreground = false;
//                } else {
//                    this._nm.cancel(1);
//                }
//            } else if (this.pp.isInValidState()) {
//                Notification noti = buildNotification(changes);
//                if (this.pp.shouldPresentPlaying()) {
//                    if (!this._foreground) {
//                        startForeground(1, this._noti);
//                        this._foreground = true;
//                    }
//                } else if (this._foreground) {
//                    stopForeground(false);
//                    this._foreground = false;
//                }
//                this._nm.notify(1, noti);
//            } else if (this._foreground) {
//                stopForeground(false);
//                this._foreground = false;
//            }
//            if ((changes & 4) != 0) {
//                updateLockScreenArtwork();
//            }
//        } catch (RuntimeException e) {
//            Log.e(TAG, "", e);
//        }
    }

//    @Override // com.mxtech.videoplayer.Player.Client
//    public void load(Uri uri, byte requestedDecoder, int videoFlags) {
//        String ext;
//        Log.d(TAG, "Load Next");
//        ActivityScreen.arrangeExtras(this._intent, uri);
//        if (requestedDecoder == 0) {
//            requestedDecoder = this._intent.getByteExtra(ActivityScreen.EXTRA_DECODE_MODE, (byte) 0);
//        }
//        if (requestedDecoder == 0 && Build.VERSION.SDK_INT >= 21 && !P.isOMXDecoderEnabled() && (ext = FileUtils.getExtension(uri.toString())) != null && (requestedDecoder = (byte) P.getDecoder(ext, 0)) == 0) {
//            requestedDecoder = 2;
//        }
//        this.pp.setDataSource(uri, requestedDecoder, 0, 0);
//        this.pp.prepare();
//        this.pp.start();
//        updateNotification(1);
//    }
//
////    @Override // com.mxtech.videoplayer.widget.SleepTimer.ISleepable
////    public void onSleepTimerChanged(SleepTimer sleepTimer) {
////        if (this.pp != null && this.pp.isInValidState()) {
////            buildNotification(0);
////        }
////    }
////
////    @Override // com.mxtech.videoplayer.widget.SleepTimer.ISleepable
////    public boolean onSleepTimerFired(SleepTimer sleepTimer, boolean completeCurrentPlayback) {
////        if (completeCurrentPlayback && this.pp != null && this.pp.isInPlaybackState()) {
////            return false;
////        }
////        if (this.pp != null) {
////            this.pp.pause();
////            if (this.pp.isInValidState()) {
////                buildNotification(0);
////            }
////        }
////        return true;
////    }
//
    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.pp != null && this._orientation != newConfig.orientation) {
            this._orientation = newConfig.orientation;
            updateNotification(4);
        }
    }

//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onRemoteResourceLoadingBegin(int loadings) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onRemoteResourceLoadingCanceled() {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onRemoteResourceLoaded(List<ISubtitle> subs, Bitmap cover, Uri uri) {
//        updateNotification(268435462);
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSetupFontCache(boolean on) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onPresentingStateChanged(boolean presentPlaying) {
//        if (this.pp != null) {
//            updateNotification(0);
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onStateChanged(int newState, int flags) {
//        if (this.pp != null) {
//            switch (newState) {
//                case -1:
//                    if (!this.pp.skipCurrent()) {
//                        clear(true);
//                        return;
//                    }
//                    return;
//                case 0:
//                case 1:
//                case 2:
//                default:
//                    return;
//                case 3:
//                    if (isPlayable()) {
//                        this.pp.save();
//                        ActivityMediaList.rebuildAll();
//                        updateNotification(6);
//                        updateStereoMode();
//                        return;
//                    } else if (!this.pp.skipCurrent()) {
//                        clear(true);
//                        return;
//                    } else {
//                        return;
//                    }
//                case 4:
//                case 5:
//                    updateNotification(0);
//                    return;
//                case 6:
//                    onPlaybackComplete();
//                    return;
//            }
//        }
//    }
//
//    private void onPlaybackComplete() {
//        this.pp.save();
//        stateHandler.sendEmptyMessageDelayed(2, 10L);
//    }
//
//    private boolean isPlayable() {
//        return this.pp.countAudioTracks() > 0 || this.pp.getDecoder() == 1;
//    }
//
//    /* JADX INFO: Access modifiers changed from: private */
//    public void nextAuto() {
//        if (this.pp != null) {
//            if (!this.pp.isInPlaybackState() || !this.pp.nextAuto()) {
//                clear(true);
//            }
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onTryNextDecoder(byte failedDecoder, byte nextDecoder, boolean initialError) {
//        if (this.pp != null) {
//            this.pp.prepare();
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onVideoDeviceChanged() {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void update(Player player, int position) {
//        if (this.pp != null) {
//            int i = this._updateCount + 1;
//            this._updateCount = i;
//            if (i > 40) {
//                updateNotification(0);
//            }
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSeekBegin(int pos) {
//        updateNotification(0);
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSeekComplete() {
//        this._updateCount = 40;
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onDurationKnown(int duration) {
//        updateNotification(0);
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onVideoSizeChanged(int width, int height) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onAudioStreamChanged(JointPlayer mp, int streamIndex) {
//        if (streamIndex == -1) {
//            ActivityScreen.checkCodec(this, mp);
//        }
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onAudioCodecUnsupported(FFPlayer ff, int audioStreamIndex) {
//        ActivityScreen.notifyUnsupportedCodec(this, getString(R.string.audio).toLowerCase(Locale.getDefault()), ff.getStreamCodec(audioStreamIndex));
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public int mediaTimeToSubtitleTime(int timeMs) {
//        return timeMs;
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onBufferingUpdate(int percent) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSSAPrepared(SubStationAlphaMedia ssa) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onVideoFilteringFailed(int type) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onEmbeddedSubtitleAdded(ISubtitle subtitle) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSubtitleInvalidated() {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public boolean canStart() {
//        return true;
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void updatePersistent(Uri uri, MediaDatabase.State save, List<ISubtitle> subs) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSubtitleClosed(ISubtitle subtitle) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onSubtitlesClosed() {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onRebootToChangeDisplay(int setDisplayFlags) {
//    }
//
//    @Override // com.mxtech.videoplayer.Player.Client
//    public void onNetworkListingComplete() {
//    }
}
