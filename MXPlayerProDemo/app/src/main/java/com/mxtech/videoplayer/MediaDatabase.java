package com.mxtech.videoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.DBUtils;
import com.mxtech.FileUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.io.DocumentTreeRegistry;
import com.mxtech.media.MediaUtils;
import com.mxtech.videoplayer.directory.MediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.subtitle.SubView;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* loaded from: classes.dex */
public final class MediaDatabase implements Handler.Callback, ActivityRegistry.TerminationListener {
    public static final int CONTENT_STATES = 1;
    private static final String FILE_EXPIRE_TIME = "+7 DAY";
    private static final String FILE_NAME = "medias.db";
    private static final String HISTORY_EXPIRE_TIME = "+60 DAY";
    private static final int MSG_CONTENT_CHANGED = 2;
    private static final int MSG_RELEASE = 1;
    private static final int RELEASE_DELAY_MILLIS = 10000;
    private static final String STATE_EXPIRE_TIME = "+7 DAY";
    private static final long URL_EXPIRE_TIME_MS = 8640000000L;
    private static final int VERSION = 25;
    private static boolean _clearUnreferencedDirectoriesCalled;
    private static Opener _opener;
    private static int _refCount;
    private int _changes;
    private HashMap<String, Integer> _mediaDirs;
    private final Map<Thread, ThreadContext> _threadContexts = new HashMap();
    public SQLiteDatabase db;
    private static final String TAG = App.TAG + ".Database";
    private static final MediaDatabase _instance = new MediaDatabase();
    private static Handler _handler = new Handler(Looper.getMainLooper(), _instance);
    private static final Collection<Observer> _observers = new ArrayList();

    /* loaded from: classes2.dex */
    public static class FullPlaybackRecord {
        public long finishTime;
        public int id;
        public long lastWatchTime;
        public Uri uri;
    }

    /* loaded from: classes2.dex */
    public static class FullState extends State {
        public Uri uri;
    }

    /* loaded from: classes2.dex */
    public static class SearchRecord {
        public String query;
        public long time;
    }

    /* loaded from: classes.dex */
    public static class DocumentTreeStorage implements DocumentTreeRegistry.Storage {
        @Override // com.mxtech.io.DocumentTreeRegistry.Storage
        public List<DocumentTreeRegistry.Resolved> loadTrees() {
            MediaDatabase mdb;
            Cursor c;
            List<DocumentTreeRegistry.Resolved> list = new ArrayList<>();
            try {
                mdb = MediaDatabase.getInstance();
                c = mdb.db.rawQuery("SELECT Tree, Path FROM DocumentTrees", null);
                try {
                } finally {
                    c.close();
                }
            } catch (Exception e) {
                Log.e(MediaDatabase.TAG, "", e);
            }
            if (c.moveToFirst()) {
                do {
                    list.add(DocumentTreeRegistry.newResolvedOnLoading(Uri.parse(c.getString(0)), c.getString(1)));
                } while (c.moveToNext());
                mdb.release();
                return list;
            }
            mdb.release();
            return list;
        }

        @Override // com.mxtech.io.DocumentTreeRegistry.Storage
        public void writeTree(Uri treeUri, String path) {
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                SQLiteDatabase db = mdb.db;
                SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE INTO DocumentTrees (Tree, Path) VALUES (?,?)");
                try {
                    stmt.bindString(1, treeUri.toString());
                    stmt.bindString(2, path);
                    stmt.execute();
                    mdb.release();
                } finally {
                    stmt.close();
                }
            } catch (Exception e) {
                Log.e(MediaDatabase.TAG, "", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ThreadContext {
        private SQLiteStatement _stmtDirectoryId;
        private SQLiteStatement _stmtQueryPosition;
        private SQLiteStatement _stmtSelectMediaId;

        private ThreadContext() {
        }

        void clear() {
            if (this._stmtQueryPosition != null) {
                this._stmtQueryPosition.close();
                this._stmtQueryPosition = null;
            }
            if (this._stmtSelectMediaId != null) {
                this._stmtSelectMediaId.close();
                this._stmtSelectMediaId = null;
            }
            if (this._stmtDirectoryId != null) {
                this._stmtDirectoryId.close();
                this._stmtDirectoryId = null;
            }
        }

        SQLiteStatement getQueuryPositionStatement(SQLiteDatabase db) {
            if (this._stmtQueryPosition == null) {
                this._stmtQueryPosition = db.compileStatement("SELECT Position FROM VideoStates WHERE Uri=?");
            }
            return this._stmtQueryPosition;
        }

        SQLiteStatement getQueryDirectoryIdStatement(SQLiteDatabase db) {
            if (this._stmtDirectoryId == null) {
                this._stmtDirectoryId = db.compileStatement("SELECT Id FROM VideoDirectory WHERE Path=?");
            }
            return this._stmtDirectoryId;
        }

        SQLiteStatement getQueryMediaIdStatement(SQLiteDatabase db) {
            if (this._stmtSelectMediaId == null) {
                this._stmtSelectMediaId = db.compileStatement("SELECT Id FROM VideoFile WHERE Directory=? AND FileName=?");
            }
            return this._stmtSelectMediaId;
        }
    }

    /* loaded from: classes.dex */
    public static class State {
        public static final short NO_AUDIO_STREAM = -100;
        public byte audioDecoder;
        public int audioOffset;
        public byte decoder;
        public int decodingOption;
        public float horzRatio;
        public short panX;
        public short panY;
        public int position;
        public int process;
        public int subtitleOffset;
        public Subtitle[] subtitles;
        public float vertRatio;
        public short zoomHeight;
        public short zoomWidth;
        public short audioStream = -1;
        public double subtitleSpeed = 1.0d;
        public double playbackSpeed = SubView.SUBTITLE_DRAG_GAP;
        public int repeatA = -1;
        public int repeatB = -1;

        /* loaded from: classes.dex */
        public static class Subtitle {
            public boolean enabled;
            public String name;
            public String typename;
            public Uri uri;

            public Subtitle(Uri uri, String name, String typename, boolean enabled) {
                this.uri = uri;
                this.name = name;
                this.typename = typename;
                this.enabled = enabled;
            }

            public Subtitle() {
            }
        }

        public boolean hasSubtitles() {
            return this.subtitles != null && this.subtitles.length > 0;
        }

        public boolean containsEnabledSubtitle() {
            Subtitle[] subtitleArr;
            if (this.subtitles != null) {
                for (Subtitle sub : this.subtitles) {
                    if (sub.enabled) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public boolean canResume() {
            return this.position > 0;
        }

        public void startOver() {
            this.position = 0;
            this.repeatA = -1;
            this.repeatB = -1;
        }

        public String toString() {
            return super.toString() + " [Position=" + this.position + " decoder=" + ((int) this.decoder) + " decodeOption=" + this.decodingOption + " audioDecoder=" + ((int) this.audioDecoder) + " audioStream=" + ((int) this.audioStream) + " audioOffset=" + this.audioOffset + " subtitle-offset=" + this.subtitleOffset + " subtitle-speed=" + this.subtitleSpeed + " playback-speed=" + this.playbackSpeed + " ratio=" + this.horzRatio + ":" + this.vertRatio + " zoom=" + ((int) this.zoomWidth) + 'x' + ((int) this.zoomHeight) + " pan=(" + ((int) this.panX) + ", " + ((int) this.panY) + ") process=" + this.process + " subtitle-count=" + (this.subtitles != null ? this.subtitles.length : 0) + " repeat=" + this.repeatA + " ~ " + this.repeatB + ']';
        }
    }

    public static synchronized MediaDatabase getInstance() throws SQLiteException {
        MediaDatabase mediaDatabase;
        synchronized (MediaDatabase.class) {
            if (_refCount == 0) {
                if (_instance.db != null) {
                    _handler.removeMessages(1);
                } else {
                    Log.v(TAG, "Open database.");
                    if (_opener == null) {
                        _opener = new Opener(App.context);
                    }
                    _instance.db = _opener.getWritableDatabase();
                }
            }
            _refCount++;
            mediaDatabase = _instance;
        }
        return mediaDatabase;
    }

    MediaDatabase() {
        ActivityRegistry.registerTerminationListener(this);
    }

    public void release() {
        synchronized (MediaDatabase.class) {
            int i = _refCount - 1;
            _refCount = i;
            if (i == 0) {
                _handler.sendEmptyMessageDelayed(1, 10000L);
            }
        }
    }

    private void release_l() {
        Log.v(TAG, "Release database. thread-contexts:" + this._threadContexts.size() + ", reference-count:" + _refCount);
        for (ThreadContext context : this._threadContexts.values()) {
            context.clear();
        }
        this._threadContexts.clear();
        _opener.close();
        this.db = null;
    }

    public static synchronized void releaseSingleton() {
        synchronized (MediaDatabase.class) {
            Log.d(TAG, "Release singleton. ref-count: " + _refCount + " _instance.db: " + _instance.db);
            if (_refCount == 0 && _instance.db != null) {
                _handler.removeMessages(1);
                _instance.release_l();
            }
        }
    }

    @Override // com.mxtech.app.ActivityRegistry.TerminationListener
    public void onProcessMayTerminating() {
        synchronized (MediaDatabase.class) {
            if (_handler.hasMessages(1)) {
                _handler.removeMessages(1);
                release_l();
            }
        }
    }

    private ThreadContext context() {
        ThreadContext context;
        synchronized (MediaDatabase.class) {
            Thread currentThread = Thread.currentThread();
            context = this._threadContexts.get(currentThread);
            if (context == null) {
                context = new ThreadContext();
                this._threadContexts.put(currentThread, context);
            }
        }
        return context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Opener extends SQLiteOpenHelper {
        Opener(Context packageContext) {
            super(packageContext, MediaDatabase.FILE_NAME, (SQLiteDatabase.CursorFactory) null, 25);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            long begin = SystemClock.uptimeMillis();
            db.execSQL("CREATE TABLE VideoStates(Id\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Uri\t\t\tTEXT\t\tNOT NULL UNIQUE,Position\t\tINT\t\t\tNOT NULL,DecodeMode\t\tTINYINT\t\tNOT NULL,DecodeOption\tINT\t\t\tNOT NULL DEFAULT 0,AudioDecoder\tTINYINT\t\tNOT NULL,AudioStream\tSMALLINT\tNOT NULL,AudioOffset    INT\t\t\tNOT NULL DEFAULT 0,SubtitleSync\tINT\t\t\tNOT NULL,SubtitleSpeed\tDOUBLE\t\tNOT NULL DEFAULT 1,PlaybackSpeed\tDOUBLE\t\tNOT NULL DEFAULT 0,WidthRatio\t\tFLOAT\t\tNOT NULL DEFAULT 0,HeightRatio\tFLOAT\t\tNOT NULL DEFAULT 0,ZoomWidth\t\tSMALLINT\tNOT NULL DEFAULT 0,ZoomHeight\t\tSMALLINT\tNOT NULL DEFAULT 0,PanX\t\t\tSMALLINT\tNOT NULL DEFAULT 0,PanY\t\t\tSMALLINT\tNOT NULL DEFAULT 0,Process\t\tINT\t\t\tNOT NULL DEFAULT 0,RepeatA\t\tINT\t\t\tNOT NULL DEFAULT -1,RepeatB\t\tINT\t\t\tNOT NULL DEFAULT -1,ExpireAt\t\tINTEGER)");
            db.execSQL("CREATE INDEX VideoStates_ExpireAt ON VideoStates (ExpireAt)");
            db.execSQL("CREATE TABLE SubtitleStates(Id\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Video\t\t\tINT\t\t\tNOT NULL,Uri\t\t\tTEXT\t\tNOT NULL,Name\t\t\tTEXT,Typename\t\tTEXT,Enabled\t\tTINYINT\t\tNOT NULL,UNIQUE (Video, Uri))");
            db.execSQL("CREATE TABLE VideoDirectory(Id\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Path\t\t\tTEXT COLLATE NOCASE NOT NULL UNIQUE)");
            db.execSQL("CREATE TABLE VideoFile(Id\t\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Directory\t\t\tINTEGER\t\tNOT NULL,FileName\t\t\tTEXT COLLATE NOCASE\tNOT NULL,Size\t\t\t\tBIGINT\t\tNOT NULL,LastModified\t\tBIGINT\t\tNOT NULL,NoThumbnail\t\tTINYINT\t\tNOT NULL DEFAULT 0,FileTimeOverriden \tBIGINT,Read\t\t\t\tTINYINT\t\tNOT NULL DEFAULT 0,VideoTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,AudioTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,SubtitleTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,SubtitleTrackTypes INT\t\t\tNOT NULL DEFAULT 0,Duration\t\t\tINT\t\t\tNOT NULL DEFAULT 0,FrameTime\t\t\tINT\t\t\tNOT NULL DEFAULT 0,Width\t\t\t\tINT\t\t\tNOT NULL DEFAULT 0,Height\t\t\t\tINT\t\t\tNOT NULL DEFAULT 0,Interlaced\t\t\tTINYINT,LastWatchTime\t\tINTEGER,FinishTime\t\t\tINTEGER,ExpireAt\t\t\tINTEGER,UNIQUE (Directory, FileName))");
            db.execSQL("CREATE INDEX VideoFile_LastWatchTime ON VideoFile (LastWatchTime)");
            db.execSQL("CREATE INDEX VideoFile_ExpireAt ON VideoFile (ExpireAt)");
            db.execSQL("CREATE TABLE SearchHistory(Id\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Query\t\t\tTEXT\t\tNOT NULL UNIQUE,Time\t\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
            db.execSQL("CREATE INDEX SearchHistory_Time ON SearchHistory (Time DESC)");
            db.execSQL("CREATE TABLE DirectOpenLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
            db.execSQL("CREATE INDEX DirectOpenLog_Time ON DirectOpenLog (Time DESC)");
            db.execSQL("CREATE TABLE DocumentTrees(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Tree\t\tTEXT\t\tNOT NULL UNIQUE,Path\t\tTEXT\t\tNOT NULL)");
            db.execSQL("CREATE TABLE SubtitleQueryLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
            db.execSQL("CREATE INDEX SubtitleQueryLog_Time ON SubtitleQueryLog (Time DESC)");
            db.execSQL("CREATE TABLE MovieTitleLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
            db.execSQL("CREATE INDEX MovieTitleLog_Time ON MovieTitleLog (Time DESC)");
            Log.w(MediaDatabase.TAG, "Created video database at " + db.getPath() + ". (" + (SystemClock.uptimeMillis() - begin) + "ms)");
            L.thumbStorage.clear();
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            long begin = SystemClock.uptimeMillis();
            if (oldVersion < 2) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN DecodeOption INT NOT NULL DEFAULT 0");
                    db.execSQL("UPDATE VideoStates SET DecodeOption=1 WHERE DecodeMode=2");
                } catch (SQLiteException e) {
                    Log.w(MediaDatabase.TAG, "", e);
                }
            }
            if (oldVersion < 3) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN ZoomWidth SMALLINT NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN ZoomHeight SMALLINT NOT NULL DEFAULT 0");
                } catch (SQLiteException e2) {
                    Log.w(MediaDatabase.TAG, "", e2);
                }
            }
            if (oldVersion < 4) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN Process INT NOT NULL DEFAULT 0");
                } catch (SQLiteException e3) {
                    Log.w(MediaDatabase.TAG, "", e3);
                }
            }
            if (oldVersion < 5) {
                try {
                    db.execSQL("DROP TABLE IF EXISTS VideoDirectory_");
                    db.execSQL("CREATE TABLE VideoDirectory_(Id\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Path\t\t\tTEXT COLLATE NOCASE NOT NULL UNIQUE)");
                    db.execSQL("INSERT OR IGNORE INTO VideoDirectory_ SELECT * FROM VideoDirectory");
                    db.execSQL("DROP TABLE VideoDirectory");
                    db.execSQL("ALTER TABLE VideoDirectory_ RENAME TO VideoDirectory");
                } catch (SQLiteException e4) {
                    Log.w(MediaDatabase.TAG, "", e4);
                }
            }
            if (oldVersion < 6) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN WidthRatio FLOAT NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN HeightRatio FLOAT NOT NULL DEFAULT 0");
                    db.execSQL("DROP TABLE IF EXISTS VideoFile_");
                    db.execSQL("CREATE TABLE VideoFile_(Id\t\t\t\t\tINTEGER \tNOT NULL PRIMARY KEY AUTOINCREMENT,Directory\t\t\tINTEGER\t\tNOT NULL,FileName\t\t\tTEXT COLLATE NOCASE\tNOT NULL,Size\t\t\t\tBIGINT\t\tNOT NULL,LastModified\t\tBIGINT\t\tNOT NULL,NoThumbnail\t\tTINYINT\t\tNOT NULL DEFAULT 0,Read\t\t\t\tTINYINT\t\tNOT NULL DEFAULT 0,VideoTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,AudioTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,SubtitleTrackCount\tTINYINT\t\tNOT NULL DEFAULT 0,Duration\t\t\tINT\t\t\tNOT NULL DEFAULT 0,FrameTime\t\t\tINT\t\t\tNOT NULL DEFAULT 0,LastWatchTime\t\tINTEGER,FinishTime\t\t\tINTEGER,UNIQUE (Directory, FileName))");
                    db.execSQL("INSERT OR IGNORE INTO VideoFile_ (Id, Directory, FileName, Size, LastModified, LastWatchTime, FinishTime) SELECT Id, Directory, FileName, Size, LastModified, LastWatchTime, FinishTime FROM VideoFile");
                    db.execSQL("DROP TABLE VideoFile");
                    db.execSQL("ALTER TABLE VideoFile_ RENAME TO VideoFile");
                } catch (SQLiteException e5) {
                    Log.w(MediaDatabase.TAG, "", e5);
                }
            }
            if (oldVersion < 7) {
                try {
                    db.execSQL("CREATE TABLE DirectOpenLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT\t\tNOT NULL)");
                } catch (SQLiteException e6) {
                    Log.w(MediaDatabase.TAG, "", e6);
                }
            }
            if (oldVersion < 10) {
                try {
                    db.execSQL("DROP TABLE IF EXISTS DirectOpenLog_");
                    db.execSQL("CREATE TABLE DirectOpenLog_(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
                    db.execSQL("INSERT OR IGNORE INTO DirectOpenLog_ (Id, Input) SELECT Id, Input FROM DirectOpenLog");
                    db.execSQL("DROP TABLE DirectOpenLog");
                    db.execSQL("ALTER TABLE DirectOpenLog_ RENAME TO DirectOpenLog");
                    db.execSQL("CREATE INDEX DirectOpenLog_Time ON DirectOpenLog (Time DESC)");
                } catch (SQLiteException e7) {
                    Log.w(MediaDatabase.TAG, "", e7);
                }
            }
            if (oldVersion < 11) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN AudioDecoder TINYINT NOT NULL DEFAULT 0");
                } catch (SQLiteException e8) {
                    Log.w(MediaDatabase.TAG, "", e8);
                }
            }
            if (oldVersion < 12) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN PanX SMALLINT NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN PanY SMALLINT NOT NULL DEFAULT 0");
                } catch (SQLiteException e9) {
                    Log.w(MediaDatabase.TAG, "", e9);
                }
            }
            if (oldVersion < 13) {
                try {
                    db.execSQL("ALTER TABLE SubtitleStates ADD COLUMN Typename TEXT");
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN SubtitleTrackTypes INT NOT NULL DEFAULT 0");
                } catch (SQLiteException e10) {
                    Log.w(MediaDatabase.TAG, "", e10);
                }
            }
            if (oldVersion < 14) {
                try {
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN ExpireAt INTEGER");
                    db.execSQL("CREATE INDEX VideoFile_ExpireAt ON VideoFile (ExpireAt)");
                } catch (SQLiteException e11) {
                    Log.w(MediaDatabase.TAG, "", e11);
                }
            }
            if (oldVersion < 15) {
                try {
                    db.execSQL("ALTER TABLE SubtitleStates ADD COLUMN Name TEXT");
                } catch (SQLiteException e12) {
                    Log.w(MediaDatabase.TAG, "", e12);
                }
            }
            if (oldVersion < 16) {
                try {
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN Width INT NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN Height INT NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN Interlaced TINYINT");
                    db.execSQL("UPDATE VideoFile SET Read=0");
                } catch (SQLiteException e13) {
                    Log.w(MediaDatabase.TAG, "", e13);
                }
            }
            if (oldVersion < 17) {
                try {
                    db.execSQL("ALTER TABLE VideoFile ADD COLUMN FileTimeOverriden BIGINT");
                } catch (SQLiteException e14) {
                    Log.w(MediaDatabase.TAG, "", e14);
                }
            }
            if (oldVersion < 18) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN SubtitleSpeed DOUBLE NOT NULL DEFAULT 1");
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN RepeatA INT NOT NULL DEFAULT -1");
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN RepeatB INT NOT NULL DEFAULT -1");
                } catch (SQLiteException e15) {
                    Log.w(MediaDatabase.TAG, "", e15);
                }
            }
            if (oldVersion < 19) {
                try {
                    db.execSQL("CREATE TABLE DocumentTrees(Id\t\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Tree\t\t\tTEXT\t\tNOT NULL UNIQUE,Path\t\t\tTEXT\t\tNOT NULL)");
                } catch (SQLiteException e16) {
                    Log.w(MediaDatabase.TAG, "", e16);
                }
            }
            if (oldVersion < 20) {
                try {
                    db.execSQL("CREATE TABLE SubtitleQueryLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
                    db.execSQL("CREATE INDEX SubtitleQueryLog_Time ON SubtitleQueryLog (Time DESC)");
                } catch (SQLiteException e17) {
                    Log.w(MediaDatabase.TAG, "", e17);
                }
            }
            if (oldVersion < 21) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN PlaybackSpeed DOUBLE NOT NULL DEFAULT 0");
                } catch (SQLiteException e18) {
                    Log.w(MediaDatabase.TAG, "", e18);
                }
            }
            if (oldVersion < 23) {
                try {
                    db.execSQL("ALTER TABLE VideoStates ADD COLUMN AudioOffset INT NOT NULL DEFAULT 0");
                } catch (SQLiteException e19) {
                    Log.w(MediaDatabase.TAG, "", e19);
                }
            }
            if (oldVersion < 25) {
                try {
                    db.execSQL("CREATE TABLE MovieTitleLog(Id\t\t\tINTEGER\t\tNOT NULL PRIMARY KEY AUTOINCREMENT,Input\t\tTEXT COLLATE NOCASE\tNOT NULL UNIQUE,Time\t\tINTEGER\t\tNOT NULL DEFAULT CURRENT_TIMESTAMP)");
                    db.execSQL("CREATE INDEX MovieTitleLog_Time ON MovieTitleLog (Time DESC)");
                } catch (SQLiteException e20) {
                    Log.w(MediaDatabase.TAG, "", e20);
                }
            }
            Log.w(MediaDatabase.TAG, "Upgraded media database " + oldVersion + " --> " + newVersion + " at " + db.getPath() + ". (" + (SystemClock.uptimeMillis() - begin) + "ms)");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(MediaDatabase.TAG, "Media database silently downgrading " + oldVersion + " <-- " + newVersion);
        }
    }

    public void setCaseSensitiveLike(boolean caseSensitive) {
        this.db.execSQL("PRAGMA case_sensitive_like=" + (caseSensitive ? '1' : '0'));
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0049 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void deleteUnreferencedThumbs() {
        File[] files = FileUtils.listFiles(L.thumbStorage.cacheDir);
        if (files != null) {
            HashSet<String> leftFiles = new HashSet<>();
            for (File file : files) {
                leftFiles.add(file.getName());
            }
            try {
                Cursor c = this.db.rawQuery("SELECT Id || '+' || Size || '+' || LastModified FROM VideoFile WHERE Directory != 0", null);
                if (c.moveToFirst()) {
                    do {
                        leftFiles.remove(c.getString(0));
                    } while (c.moveToNext());
                    c.close();
                }
                c.close();
            } finally {
                Iterator<String> it = leftFiles.iterator();
                while (it.hasNext()) {
                    String fileName = it.next();
                    if (!new File(L.thumbStorage.cacheDir, fileName).delete()) {
                        Log.e(TAG, "Failed to delete unexpected thumbnail cache '" + fileName);
                    }
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void shrink(MediaDirectory mdir) throws SQLiteException {
        HashMap<String, File> leftThumbs;
        File[] thumbs = FileUtils.listFiles(L.thumbStorage.cacheDir);
        if (thumbs != null) {
            leftThumbs = new HashMap<>(thumbs.length);
            for (File thumb : thumbs) {
                leftThumbs.put(thumb.getName(), thumb);
            }
        } else {
            leftThumbs = new HashMap<>();
        }
        this.db.beginTransaction();
        Set<String> mediaFiles = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        MediaFile[] files = mdir.list("/", 33);
        for (MediaFile file : files) {
            mediaFiles.add(file.path);
        }
        try {
            Cursor c = this.db.rawQuery("SELECT f.Id || '+' || f.Size || '+' || f.LastModified, d.Path || '/' || f.FileName, f.Id, strftime('%s',f.expireAt), f.Size FROM VideoDirectory d INNER JOIN VideoFile f ON d.Id = f.Directory", null);
            if (c.moveToFirst()) {
                do {
                    String thumbFilename = c.getString(0);
                    String path = c.getString(1);
                    if (mediaFiles.contains(path) || new File(path).exists()) {
                        leftThumbs.remove(thumbFilename);
                        if (!c.isNull(3)) {
                            this.db.execSQL("UPDATE VideoFile SET ExpireAt=NULL WHERE Id=" + c.getString(2));
                        }
                    } else if (c.isNull(3)) {
                        this.db.execSQL("UPDATE VideoFile SET ExpireAt=DATETIME('NOW', '+7 DAY') WHERE Id=" + c.getString(2));
                        leftThumbs.remove(thumbFilename);
                    } else {
                        long expireAt = c.getLong(3);
                        if (expireAt <= System.currentTimeMillis() / 1000) {
                            this.db.execSQL("DELETE FROM VideoFile WHERE Id=" + c.getString(2));
                        } else {
                            leftThumbs.remove(thumbFilename);
                        }
                    }
                } while (c.moveToNext());
                c.close();
                this.db.execSQL("DELETE FROM VideoDirectory WHERE Id IN (SELECT d.Id FROM VideoDirectory d LEFT JOIN VideoFile f ON d.Id = f.Directory WHERE f.Id IS NULL)");
                this.db.setTransactionSuccessful();
                this.db.endTransaction();
                for (File thumb2 : leftThumbs.values()) {
                    if (!thumb2.delete()) {
                        Log.e(TAG, "Failed to delete unreferenced thumb '" + thumb2 + '\'');
                    }
                }
            }
            c.close();
            this.db.execSQL("DELETE FROM VideoDirectory WHERE Id IN (SELECT d.Id FROM VideoDirectory d LEFT JOIN VideoFile f ON d.Id = f.Directory WHERE f.Id IS NULL)");
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
            while (r13.hasNext()) {
            }
        } catch (Throwable th) {
            this.db.endTransaction();
            throw th;
        }
    }

    public void writeState(Uri uri, State state) throws SQLiteException {
        SQLiteStatement stmtVideo;
        State.Subtitle[] subtitleArr;
        long begin = SystemClock.uptimeMillis();
        _deleteState(this.db, uri);
        if (MediaListFragment.TYPE_FILE.equals(uri.getScheme())) {
            stmtVideo = this.db.compileStatement("INSERT INTO VideoStates (Uri, Position, DecodeMode, DecodeOption, AudioDecoder, AudioStream, AudioOffset, SubtitleSync, SubtitleSpeed, PlaybackSpeed, WidthRatio, HeightRatio, ZoomWidth, ZoomHeight, PanX, PanY, Process, RepeatA, RepeatB) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        } else {
            stmtVideo = this.db.compileStatement("INSERT INTO VideoStates (Uri, Position, DecodeMode, DecodeOption, AudioDecoder, AudioStream, AudioOffset, SubtitleSync, SubtitleSpeed, PlaybackSpeed, WidthRatio, HeightRatio, ZoomWidth, ZoomHeight, PanX, PanY, Process, RepeatA, RepeatB, ExpireAt) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,DATETIME('NOW', '+7 DAY'))");
        }
        int i = 0 + 1;
        try {
            stmtVideo.bindString(i, uri.toString());
            int i2 = i + 1;
            stmtVideo.bindLong(i2, state.position);
            int i3 = i2 + 1;
            stmtVideo.bindLong(i3, state.decoder);
            int i4 = i3 + 1;
            stmtVideo.bindLong(i4, state.decodingOption);
            int i5 = i4 + 1;
            stmtVideo.bindLong(i5, state.audioDecoder);
            int i6 = i5 + 1;
            stmtVideo.bindLong(i6, state.audioStream);
            int i7 = i6 + 1;
            stmtVideo.bindLong(i7, state.audioOffset);
            int i8 = i7 + 1;
            stmtVideo.bindLong(i8, state.subtitleOffset);
            int i9 = i8 + 1;
            stmtVideo.bindDouble(i9, state.subtitleSpeed);
            int i10 = i9 + 1;
            stmtVideo.bindDouble(i10, state.playbackSpeed);
            int i11 = i10 + 1;
            stmtVideo.bindDouble(i11, state.horzRatio);
            int i12 = i11 + 1;
            stmtVideo.bindDouble(i12, state.vertRatio);
            int i13 = i12 + 1;
            stmtVideo.bindLong(i13, state.zoomWidth);
            int i14 = i13 + 1;
            stmtVideo.bindLong(i14, state.zoomHeight);
            int i15 = i14 + 1;
            stmtVideo.bindLong(i15, state.panX);
            int i16 = i15 + 1;
            stmtVideo.bindLong(i16, state.panY);
            int i17 = i16 + 1;
            stmtVideo.bindLong(i17, state.process);
            int i18 = i17 + 1;
            stmtVideo.bindLong(i18, state.repeatA);
            stmtVideo.bindLong(i18 + 1, state.repeatB);
            long rowId = stmtVideo.executeInsert();
            if (state.subtitles != null && state.subtitles.length > 0) {
                SQLiteStatement stmtSubtitle = this.db.compileStatement("INSERT INTO SubtitleStates (Video, Uri, Name, Typename, Enabled) VALUES (?,?,?,?,?)");
                for (State.Subtitle s : state.subtitles) {
                    stmtSubtitle.bindLong(1, rowId);
                    stmtSubtitle.bindString(2, s.uri.toString());
                    if (s.name != null) {
                        stmtSubtitle.bindString(3, s.name);
                    }
                    if (s.typename != null) {
                        stmtSubtitle.bindString(4, s.typename);
                    }
                    stmtSubtitle.bindLong(5, s.enabled ? 1L : 0L);
                    try {
                        stmtSubtitle.execute();
                    } catch (SQLiteConstraintException e) {
                    }
                }
                stmtSubtitle.close();
            }
            stmtVideo.close();
            Log.v(TAG, "State saved for '" + uri + "': " + state + " (" + (SystemClock.uptimeMillis() - begin) + "ms)");
        } catch (Throwable th) {
            stmtVideo.close();
            throw th;
        }
    }

    public State readState(Uri uri) throws SQLiteException {
        long begin = SystemClock.uptimeMillis();
        Cursor c = this.db.rawQuery("SELECT Id, Position, DecodeMode, DecodeOption, AudioDecoder, AudioStream, AudioOffset, SubtitleSync, SubtitleSpeed, PlaybackSpeed, WidthRatio, HeightRatio, ZoomWidth, ZoomHeight, PanX, PanY, Process, RepeatA, RepeatB FROM VideoStates WHERE Uri=" + DatabaseUtils.sqlEscapeString(uri.toString()), null);
        try {
            if (c.moveToFirst()) {
                int i = 0 + 1;
                long rowId = c.getLong(0);
                State state = new State();
                int i2 = i + 1;
                state.position = c.getInt(i);
                int i3 = i2 + 1;
                state.decoder = (byte) c.getShort(i2);
                int i4 = i3 + 1;
                state.decodingOption = c.getInt(i3);
                int i5 = i4 + 1;
                state.audioDecoder = (byte) c.getShort(i4);
                int i6 = i5 + 1;
                state.audioStream = c.getShort(i5);
                int i7 = i6 + 1;
                state.audioOffset = c.getInt(i6);
                int i8 = i7 + 1;
                state.subtitleOffset = c.getInt(i7);
                int i9 = i8 + 1;
                state.subtitleSpeed = c.getDouble(i8);
                int i10 = i9 + 1;
                state.playbackSpeed = c.getDouble(i9);
                int i11 = i10 + 1;
                state.horzRatio = c.getFloat(i10);
                int i12 = i11 + 1;
                state.vertRatio = c.getFloat(i11);
                int i13 = i12 + 1;
                state.zoomWidth = c.getShort(i12);
                int i14 = i13 + 1;
                state.zoomHeight = c.getShort(i13);
                int i15 = i14 + 1;
                state.panX = c.getShort(i14);
                int i16 = i15 + 1;
                state.panY = c.getShort(i15);
                int i17 = i16 + 1;
                state.process = c.getInt(i16);
                int i18 = i17 + 1;
                state.repeatA = c.getInt(i17);
                int i19 = i18 + 1;
                state.repeatB = c.getInt(i18);
                Cursor subCursor = this.db.rawQuery("SELECT Uri, Name, Typename, Enabled FROM SubtitleStates WHERE Video=" + rowId, null);
                if (subCursor.moveToNext()) {
                    ArrayList<State.Subtitle> subtitles = new ArrayList<>();
                    do {
                        State.Subtitle subtitle = new State.Subtitle();
                        subtitle.uri = Uri.parse(subCursor.getString(0));
                        if (!subCursor.isNull(1)) {
                            subtitle.name = subCursor.getString(1);
                        }
                        if (!subCursor.isNull(2)) {
                            subtitle.typename = subCursor.getString(2);
                        }
                        subtitle.enabled = subCursor.getLong(3) != 0;
                        subtitles.add(subtitle);
                    } while (subCursor.moveToNext());
                    state.subtitles = (State.Subtitle[]) subtitles.toArray(new State.Subtitle[subtitles.size()]);
                }
                subCursor.close();
                Log.v(TAG, "States read: " + uri + " (" + (SystemClock.uptimeMillis() - begin) + "ms)");
                return state;
            }
            c.close();
            Log.v(TAG, "States not found: " + uri + " (" + (SystemClock.uptimeMillis() - begin) + "ms)");
            return null;
        } finally {
            c.close();
        }
    }

    public List<FullState> readAllState() throws SQLiteException {
        Cursor c = this.db.rawQuery("SELECT Id, Uri, Position, DecodeMode, DecodeOption, AudioDecoder, AudioStream, AudioOffset, SubtitleSync, SubtitleSpeed, PlaybackSpeed, WidthRatio, HeightRatio, ZoomWidth, ZoomHeight, PanX, PanY, Process, RepeatA, RepeatB FROM VideoStates", null);
        List<FullState> states = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                do {
                    int i = 0 + 1;
                    long rowId = c.getLong(0);
                    FullState state = new FullState();
                    int i2 = i + 1;
                    state.uri = Uri.parse(c.getString(i));
                    int i3 = i2 + 1;
                    state.position = c.getInt(i2);
                    int i4 = i3 + 1;
                    state.decoder = (byte) c.getShort(i3);
                    int i5 = i4 + 1;
                    state.decodingOption = c.getInt(i4);
                    int i6 = i5 + 1;
                    state.audioDecoder = (byte) c.getShort(i5);
                    int i7 = i6 + 1;
                    state.audioStream = c.getShort(i6);
                    int i8 = i7 + 1;
                    state.audioOffset = c.getInt(i7);
                    int i9 = i8 + 1;
                    state.subtitleOffset = c.getInt(i8);
                    int i10 = i9 + 1;
                    state.subtitleSpeed = c.getDouble(i9);
                    int i11 = i10 + 1;
                    state.playbackSpeed = c.getDouble(i10);
                    int i12 = i11 + 1;
                    state.horzRatio = c.getFloat(i11);
                    int i13 = i12 + 1;
                    state.vertRatio = c.getFloat(i12);
                    int i14 = i13 + 1;
                    state.zoomWidth = c.getShort(i13);
                    int i15 = i14 + 1;
                    state.zoomHeight = c.getShort(i14);
                    int i16 = i15 + 1;
                    state.panX = c.getShort(i15);
                    int i17 = i16 + 1;
                    state.panY = c.getShort(i16);
                    int i18 = i17 + 1;
                    state.process = c.getInt(i17);
                    int i19 = i18 + 1;
                    state.repeatA = c.getInt(i18);
                    int i20 = i19 + 1;
                    state.repeatB = c.getInt(i19);
                    Cursor subCursor = this.db.rawQuery("SELECT Uri, Name, Typename, Enabled FROM SubtitleStates WHERE Video=" + rowId, null);
                    if (subCursor.moveToNext()) {
                        ArrayList<State.Subtitle> subtitles = new ArrayList<>();
                        do {
                            State.Subtitle subtitle = new State.Subtitle();
                            subtitle.uri = Uri.parse(subCursor.getString(0));
                            if (!subCursor.isNull(1)) {
                                subtitle.name = subCursor.getString(1);
                            }
                            if (!subCursor.isNull(2)) {
                                subtitle.typename = subCursor.getString(2);
                            }
                            subtitle.enabled = subCursor.getLong(3) != 0;
                            subtitles.add(subtitle);
                        } while (subCursor.moveToNext());
                        state.subtitles = (State.Subtitle[]) subtitles.toArray(new State.Subtitle[subtitles.size()]);
                    }
                    subCursor.close();
                    states.add(state);
                } while (c.moveToNext());
                return states;
            }
            return states;
        } finally {
            c.close();
        }
    }

    public void clearExpired(MediaDirectory mdir) throws SQLiteException {
        this.db.execSQL("DELETE FROM SubtitleStates WHERE Video IN (SELECT Id FROM VideoStates WHERE ExpireAt < CURRENT_TIMESTAMP)");
        this.db.execSQL("DELETE FROM VideoStates WHERE ExpireAt < CURRENT_TIMESTAMP");
        this.db.execSQL("DELETE FROM VideoFile WHERE Directory = 0 AND ExpireAt < CURRENT_TIMESTAMP");
        shrink(mdir);
    }

    public List<FullPlaybackRecord> readAllPlaybackRecords() {
        List<FullPlaybackRecord> records = new ArrayList<>();
        Cursor c = this.db.rawQuery("SELECT f.id, d.Path || '/' || f.FileName, f.LastWatchTime, f.FinishTime FROM VideoDirectory d INNER JOIN VideoFile f ON d.Id = f.Directory WHERE f.LastWatchTime IS NOT NULL OR f.FinishTime IS NOT NULL", null);
        try {
            if (c.moveToFirst()) {
                do {
                    FullPlaybackRecord record = new FullPlaybackRecord();
                    record.id = c.getInt(0);
                    record.uri = Uri.fromFile(new File(c.getString(1)));
                    record.lastWatchTime = c.isNull(2) ? 0L : c.getLong(2);
                    record.finishTime = c.isNull(3) ? 0L : c.getLong(3);
                    records.add(record);
                } while (c.moveToNext());
                return records;
            }
            return records;
        } finally {
            c.close();
        }
    }

    public int getLastVideoId(int directory) throws SQLiteException {
        int i = 0;
        Cursor cursor = directory != 0 ? this.db.rawQuery("SELECT Id FROM VideoFile WHERE Directory=" + directory + " AND LastWatchTime IS NOT NULL ORDER BY LastWatchTime DESC LIMIT 1", null) : this.db.rawQuery("SELECT Id FROM VideoFile WHERE LastWatchTime IS NOT NULL ORDER BY LastWatchTime DESC LIMIT 1", null);
        try {
            if (cursor.moveToFirst()) {
                i = cursor.getInt(0);
            }
            return i;
        } finally {
            cursor.close();
        }
    }

    public int getNotFinishedLastVideo(int directory) throws SQLiteException {
        int i = 0;
        Cursor cursor = directory != 0 ? this.db.rawQuery("SELECT Id FROM VideoFile WHERE Directory=" + directory + " AND LastWatchTime IS NOT NULL AND (FinishTime IS NULL OR (FinishTime < LastWatchTime)) ORDER BY LastWatchTime DESC LIMIT 1", null) : this.db.rawQuery("SELECT Id FROM VideoFile WHERE LastWatchTime IS NOT NULL AND (FinishTime IS NULL OR (FinishTime < LastWatchTime)) ORDER BY LastWatchTime DESC LIMIT 1", null);
        try {
            if (cursor.moveToFirst()) {
                i = cursor.getInt(0);
            }
            return i;
        } finally {
            cursor.close();
        }
    }

    public Uri getLastVideoUri() throws SQLiteException {
        Uri uri = null;
        Cursor c = this.db.rawQuery("SELECT d.Path, f.FileName FROM VideoFile f LEFT JOIN VideoDirectory d ON f.Directory = d.Id WHERE f.LastWatchTime IS NOT NULL ORDER BY f.LastWatchTime DESC LIMIT 1", null);
        try {
            if (c.moveToFirst()) {
                if (c.isNull(0)) {
                    uri = Uri.parse(c.getString(1));
                } else {
                    uri = Uri.fromFile(new File(c.getString(0), c.getString(1)));
                }
            }
            return uri;
        } finally {
            c.close();
        }
    }

    public FullPlaybackRecord getLastVideoInfo() throws SQLiteException {
        FullPlaybackRecord info = null;
        Cursor c = this.db.rawQuery("SELECT f.id, d.Path, f.FileName, f.LastWatchTime, f.FinishTime FROM VideoFile f LEFT JOIN VideoDirectory d ON f.Directory = d.Id WHERE f.LastWatchTime IS NOT NULL ORDER BY f.LastWatchTime DESC LIMIT 1", null);
        try {
            if (c.moveToFirst()) {
                info = new FullPlaybackRecord();
                info.id = c.getInt(0);
                if (c.isNull(1)) {
                    info.uri = Uri.parse(c.getString(2));
                } else {
                    info.uri = Uri.fromFile(new File(c.getString(1), c.getString(2)));
                }
                if (!c.isNull(3)) {
                    info.lastWatchTime = c.getLong(3);
                }
                if (!c.isNull(4)) {
                    info.finishTime = c.getLong(4);
                }
            }
            return info;
        } finally {
            c.close();
        }
    }

    public void deleteState(Uri videoUri) throws SQLiteException {
        _deleteState(this.db, videoUri);
    }

    private void _deleteState(SQLiteDatabase db, Uri videoUri) throws SQLiteException {
        StringBuilder b = new StringBuilder("Uri=");
        DatabaseUtils.appendEscapedSQLString(b, videoUri.toString());
        _deleteState(db, b.toString());
    }

    private void _deleteState(SQLiteDatabase db, String where) throws SQLiteException {
        Cursor cursor = db.rawQuery("SELECT Id FROM VideoStates WHERE " + where, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long rowId = cursor.getLong(0);
                    db.execSQL("DELETE FROM SubtitleStates WHERE Video=" + rowId);
                    db.execSQL("DELETE FROM VideoStates WHERE Id=" + rowId);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    public Integer getPosition(Uri uri) throws SQLiteException {
        SQLiteStatement stmtQueryPosition = context().getQueuryPositionStatement(this.db);
        try {
            stmtQueryPosition.bindString(1, uri.toString());
            return Integer.valueOf((int) stmtQueryPosition.simpleQueryForLong());
        } catch (SQLiteDoneException e) {
            return null;
        } finally {
            stmtQueryPosition.clearBindings();
        }
    }

    public Map.Entry<String, Integer>[] getDirectories(boolean reload) throws SQLiteException {
        return getDirectories(reload, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0044 A[Catch: all -> 0x0071, TRY_ENTER, TryCatch #1 {all -> 0x0074, blocks: (B:6:0x001c, B:8:0x002b, B:11:0x0043, B:12:0x0044, B:14:0x0048, B:17:0x0052, B:27:0x0079, B:28:0x008d, B:20:0x006b), top: B:44:0x001c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Map.Entry<String, Integer>[] getDirectories(boolean reload, String root) throws SQLiteException {
        Map.Entry<String, Integer>[] entryArr;
        Cursor cursor;
        if (reload) {
            if (root != null) {
                StringBuilder sb = new StringBuilder("SELECT Id, Path FROM VideoDirectory WHERE ");
                DBUtils.appendPathFilter(sb, "Path", root, true);
                cursor = this.db.rawQuery(sb.toString(), null);
            } else {
                cursor = this.db.rawQuery("SELECT Id, Path FROM VideoDirectory", null);
            }
            try {
                int count = cursor.getCount();
                int i = 0;
                int[] ids = new int[count];
                String[] dirPaths = new String[count];
                if (cursor.moveToFirst()) {
                    do {
                        ids[i] = cursor.getInt(0);
                        dirPaths[i] = cursor.getString(1);
                        i++;
                    } while (cursor.moveToNext());
                    synchronized (MediaDatabase.class) {
                        if (this._mediaDirs == null) {
                            this._mediaDirs = new HashMap<>();
                        } else {
                            this._mediaDirs.clear();
                        }
                        for (int i2 = 0; i2 < count; i2++) {
                            this._mediaDirs.put(dirPaths[i2], Integer.valueOf(ids[i2]));
                        }
                        entryArr = (Map.Entry[]) this._mediaDirs.entrySet().toArray(new Map.Entry[this._mediaDirs.size()]);
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            synchronized (MediaDatabase.class) {
                if (this._mediaDirs == null) {
                    entryArr = new Map.Entry[0];
                } else {
                    entryArr = (Map.Entry[]) this._mediaDirs.entrySet().toArray(new Map.Entry[this._mediaDirs.size()]);
                }
            }
        }
        return entryArr;
    }

    public int getDirectoryId(String dirPath) throws SQLiteException {
        try {
            return getDirectoryIdNoCreate(dirPath);
        } catch (SQLiteDoneException e) {
            ContentValues content = new ContentValues(1);
            content.put("Path", dirPath);
            int id = (int) this.db.insertOrThrow("VideoDirectory", null, content);
            synchronized (MediaDatabase.class) {
                this._mediaDirs.put(dirPath, Integer.valueOf(id));
                return id;
            }
        }
    }

    public int getDirectoryIdNoCreate(String dirPath) throws SQLiteException, SQLiteDoneException {
        int id;
        synchronized (MediaDatabase.class) {
            if (this._mediaDirs == null) {
                this._mediaDirs = new HashMap<>();
            } else {
                Integer id2 = this._mediaDirs.get(dirPath);
                if (id2 != null) {
                    id = id2.intValue();
                }
            }
            SQLiteStatement stmtDirectoryID = context().getQueryDirectoryIdStatement(this.db);
            try {
                stmtDirectoryID.bindString(1, dirPath);
                id = (int) stmtDirectoryID.simpleQueryForLong();
                synchronized (MediaDatabase.class) {
                    this._mediaDirs.put(dirPath, Integer.valueOf(id));
                }
            } finally {
                stmtDirectoryID.clearBindings();
            }
        }
        return id;
    }

    public int getFileIDOrThrow(int directory, String fileName) throws SQLiteException, SQLiteDoneException {
        SQLiteStatement stmt = context().getQueryMediaIdStatement(this.db);
        try {
            stmt.bindLong(1, directory);
            stmt.bindString(2, fileName);
            return (int) stmt.simpleQueryForLong();
        } finally {
            stmt.clearBindings();
        }
    }

    public int getFileIDOrThrow(File file) throws SQLiteException, SQLiteDoneException {
        return getFileIDOrThrow(getDirectoryIdNoCreate(file.getParent()), file.getName());
    }

    public int getFileID(File file) throws SQLiteException {
        try {
            return getFileIDOrThrow(getDirectoryIdNoCreate(file.getParent()), file.getName());
        } catch (SQLiteDoneException e) {
            return 0;
        }
    }

    public Uri getUri(int mediaId) throws SQLiteException {
        Uri uri = null;
        Cursor c = this.db.rawQuery("SELECT d.Path, f.FileName FROM VideoFile f LEFT JOIN VideoDirectory d ON f.Directory = d.Id WHERE f.Id=" + mediaId, null);
        try {
            if (c.moveToFirst()) {
                uri = makeUri(c.getString(0), c.getString(1));
            }
            return uri;
        } finally {
            c.close();
        }
    }

    public int upsertVideoFile(File file, ContentValues values) throws SQLiteException {
        return upsertVideoFile(getDirectoryId(file.getParent()), file, values, false);
    }

    public int upsertVideoFile(File file, ContentValues values, boolean silent) throws SQLiteException {
        return upsertVideoFile(getDirectoryId(file.getParent()), file, values, silent);
    }

    public int upsertVideoFile(int directory, File file, ContentValues values) throws SQLiteException {
        return upsertVideoFile(directory, file, values, false);
    }

    public int upsertVideoFile(int directory, File file, ContentValues values, boolean silent) throws SQLiteException {
        int fileId;
        try {
            fileId = getFileIDOrThrow(directory, file.getName());
            this.db.update("VideoFile", values, "Id=" + fileId, null);
        } catch (SQLiteDoneException e) {
            ContentValues values2 = new ContentValues(values);
            values2.put("Directory", Integer.valueOf(directory));
            values2.put("FileName", file.getName());
            values2.put("Size", Long.valueOf(file.length()));
            values2.put("LastModified", Long.valueOf(file.lastModified()));
            fileId = (int) this.db.insertOrThrow("VideoFile", null, values2);
        }
        if (!silent) {
            notifyChange(1);
        }
        return fileId;
    }

    public int upsertUri(Uri uri, ContentValues values) throws SQLiteException {
        return upsertUri(uri, values, false);
    }

    public int upsertUri(Uri uri, ContentValues values, boolean silent) throws SQLiteException {
        File file = MediaUtils.fileFromUri(uri);
        if (file != null) {
            return upsertVideoFile(file, values, silent);
        }
        return 0;
    }

    private String likeReplaceCommand(String table, String key, String from, String to) {
        StringBuilder b = new StringBuilder("UPDATE OR REPLACE ");
        b.append(table).append(" SET ").append(key).append('=');
        DatabaseUtils.appendEscapedSQLString(b, to);
        b.append(" || SUBSTR(").append(key).append(',').append(from.length() + 1).append(") WHERE ").append(key).append(" LIKE '");
        DBUtils.appendEscapedLikeString(b, from).append("%' ESCAPE '\\'");
        return b.toString();
    }

    private String replaceCommand(String table, String key, String from, String to) {
        StringBuilder b = new StringBuilder("UPDATE OR REPLACE ");
        b.append(table).append(" SET ").append(key).append('=');
        DatabaseUtils.appendEscapedSQLString(b, to);
        b.append(" WHERE ").append(key).append('=');
        DatabaseUtils.appendEscapedSQLString(b, from);
        return b.toString();
    }

    public void renameDirectory(File from, File to) throws SQLiteException {
        String fromUri = Uri.fromFile(from).toString() + '/';
        String toUri = Uri.fromFile(to).toString() + '/';
        this.db.beginTransaction();
        try {
            setCaseSensitiveLike(true);
            this.db.execSQL(likeReplaceCommand("VideoStates", "Uri", fromUri, toUri));
            this.db.execSQL(likeReplaceCommand("SubtitleStates", "Uri", fromUri, toUri));
            this.db.execSQL(likeReplaceCommand("VideoDirectory", "Path", from.getPath(), to.getPath()));
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
            setCaseSensitiveLike(false);
        }
    }

    public void renameFile(File from, File to) throws SQLiteException {
        String fromUri = Uri.fromFile(from).toString();
        String toUri = Uri.fromFile(to).toString();
        this.db.beginTransaction();
        try {
            this.db.execSQL(replaceCommand("VideoStates", "Uri", fromUri, toUri));
            try {
                int dirID = getDirectoryIdNoCreate(from.getParent());
                this.db.execSQL(replaceCommand("VideoFile", "FileName", from.getName(), to.getName()) + " AND Directory=" + dirID);
            } catch (SQLiteDoneException e) {
            }
            this.db.setTransactionSuccessful();
            notifyChange(1);
        } finally {
            this.db.endTransaction();
        }
    }

    public void renameSubFile(File from, File to) throws SQLiteException {
        String fromUri = Uri.fromFile(from).toString();
        String toUri = Uri.fromFile(to).toString();
        setCaseSensitiveLike(true);
        try {
            this.db.execSQL(likeReplaceCommand("SubtitleStates", "Uri", fromUri, toUri));
        } finally {
            setCaseSensitiveLike(false);
        }
    }

    public void deleteDirectory(String path) throws SQLiteException {
        try {
            int dirID = getDirectoryIdNoCreate(path);
            Cursor c = this.db.rawQuery("SELECT Id, Size, LastModified FROM VideoFile WHERE Directory=" + dirID, null);
            if (c.moveToFirst()) {
                do {
                    L.thumbStorage.delete(c.getInt(0), c.getLong(1), c.getLong(2));
                } while (c.moveToNext());
                c.close();
                this.db.beginTransaction();
                Uri uri = Uri.fromFile(new File(path));
                String uriString = uri.toString();
                StringBuilder uriCond = new StringBuilder("Uri LIKE '");
                DBUtils.appendEscapedLikeString(uriCond, uriString).append("/%' ESCAPE '\\' AND SUBSTR(Uri,").append(uriString.length() + 2).append(") NOT LIKE '%/%'");
                setCaseSensitiveLike(true);
                this.db.execSQL("DELETE FROM SubtitleStates WHERE Video IN (SELECT Id FROM VideoStates WHERE " + uriCond.toString() + ')');
                this.db.execSQL("DELETE FROM VideoStates WHERE " + uriCond.toString());
                this.db.execSQL("DELETE FROM VideoDirectory WHERE Id=" + dirID);
                this.db.execSQL("DELETE FROM VideoFile WHERE Directory=" + dirID);
                this.db.setTransactionSuccessful();
                notifyChange(1);
                this.db.endTransaction();
                setCaseSensitiveLike(false);
            }
            c.close();
            this.db.beginTransaction();
            Uri uri2 = Uri.fromFile(new File(path));
            String uriString2 = uri2.toString();
            StringBuilder uriCond2 = new StringBuilder("Uri LIKE '");
            DBUtils.appendEscapedLikeString(uriCond2, uriString2).append("/%' ESCAPE '\\' AND SUBSTR(Uri,").append(uriString2.length() + 2).append(") NOT LIKE '%/%'");
            setCaseSensitiveLike(true);
            this.db.execSQL("DELETE FROM SubtitleStates WHERE Video IN (SELECT Id FROM VideoStates WHERE " + uriCond2.toString() + ')');
            this.db.execSQL("DELETE FROM VideoStates WHERE " + uriCond2.toString());
            this.db.execSQL("DELETE FROM VideoDirectory WHERE Id=" + dirID);
            this.db.execSQL("DELETE FROM VideoFile WHERE Directory=" + dirID);
            this.db.setTransactionSuccessful();
            notifyChange(1);
            this.db.endTransaction();
            setCaseSensitiveLike(false);
        } catch (SQLiteDoneException e) {
        }
    }

    public void clearUnreferencedDirectories(boolean onlyOnce) {
        if (!onlyOnce || !_clearUnreferencedDirectoriesCalled) {
            this.db.execSQL("DELETE FROM VideoDirectory WHERE Id IN (SELECT d.Id FROM VideoDirectory d LEFT JOIN VideoFile f ON d.Id = f.Directory WHERE f.Id IS NULL)");
            _clearUnreferencedDirectoriesCalled = true;
        }
    }

    public void deleteThumbnail(File file) throws SQLiteException {
        try {
            int id = getFileIDOrThrow(getDirectoryIdNoCreate(file.getParent()), file.getName());
            L.thumbStorage.delete(id, file);
            this.db.execSQL("UPDATE VideoFile SET NoThumbnail=0 WHERE Id=" + id);
        } catch (SQLiteDoneException e) {
        }
    }

    public void deleteFile(int id, File file) throws SQLiteException {
        try {
            this.db.beginTransaction();
            StringBuilder b = new StringBuilder("Uri=");
            DatabaseUtils.appendEscapedSQLString(b, Uri.fromFile(file).toString());
            String whereUri = b.toString();
            _deleteState(this.db, whereUri);
            this.db.execSQL("DELETE FROM VideoFile WHERE Id=" + id);
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
        } catch (SQLiteDoneException e) {
        }
        L.thumbStorage.delete(id, file);
    }

    public void resetVideoFiles(Map<File, List<File>> files) throws SQLiteException {
        long dirID;
        SQLiteStatement stmtQueryDirID = null;
        SQLiteStatement stmtInsertDir = null;
        SQLiteStatement stmtInsertFile = null;
        this.db.beginTransaction();
        try {
            stmtQueryDirID = this.db.compileStatement("SELECT Id FROM VideoDirectory WHERE Path=?");
            stmtInsertDir = this.db.compileStatement("INSERT INTO VideoDirectory (Path) VALUES (?)");
            stmtInsertFile = this.db.compileStatement("INSERT OR IGNORE INTO VideoFile (Directory, FileName, Size, LastModified) VALUES (?,?,?,?)");
            for (Map.Entry<File, List<File>> dir : files.entrySet()) {
                stmtQueryDirID.bindString(1, dir.getKey().getPath());
                try {
                    dirID = stmtQueryDirID.simpleQueryForLong();
                } catch (SQLiteDoneException e) {
                    stmtInsertDir.bindString(1, dir.getKey().getPath());
                    dirID = stmtInsertDir.executeInsert();
                }
                for (File file : dir.getValue()) {
                    stmtInsertFile.bindLong(1, dirID);
                    stmtInsertFile.bindString(2, file.getName());
                    stmtInsertFile.bindLong(3, file.length());
                    stmtInsertFile.bindLong(4, file.lastModified());
                    stmtInsertFile.execute();
                }
            }
            this.db.setTransactionSuccessful();
            notifyChange(1);
        } finally {
            this.db.endTransaction();
            if (stmtQueryDirID != null) {
                stmtQueryDirID.close();
            }
            if (stmtInsertDir != null) {
                stmtInsertDir.close();
            }
            if (stmtInsertFile != null) {
                stmtInsertFile.close();
            }
        }
    }

    public int getFrameTime(File file) {
        Cursor c;
        try {
            c = this.db.rawQuery("SELECT FrameTime FROM VideoFile WHERE Id=" + getFileIDOrThrow(file), null);
        } catch (SQLiteDoneException e) {
        }
        if (c.moveToFirst() && !c.isNull(0)) {
            int i = c.getInt(0);
            c.close();
            return i;
        }
        c.close();
        return -1;
    }

    public Cursor querySearchHistory(String[] projection, String limit) {
        StringBuilder b = new StringBuilder("SELECT ");
        int length = projection.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            String proj = projection[i];
            int i3 = i2 + 1;
            if (i2 > 0) {
                b.append(',');
            }
            b.append(proj);
            i++;
            i2 = i3;
        }
        b.append(" FROM SearchHistory ORDER BY Time DESC");
        if (limit != null) {
            b.append(" LIMIT ").append(limit);
        }
        return this.db.rawQuery(b.toString(), null);
    }

    public List<SearchRecord> readAllSearchRecords() {
        List<SearchRecord> records = new ArrayList<>();
        Cursor c = this.db.rawQuery("SELECT Query, strftime('%s',Time) FROM SearchHistory", null);
        try {
            if (c.moveToFirst()) {
                do {
                    SearchRecord record = new SearchRecord();
                    record.query = c.getString(0);
                    record.time = c.getLong(1) * 1000;
                    records.add(record);
                } while (c.moveToNext());
                return records;
            }
            return records;
        } finally {
            c.close();
        }
    }

    public void saveRecentQuery(String query) {
        saveRecentQuery(query, null);
    }

    public void saveRecentQuery(String query, Timestamp time) {
        this.db.beginTransaction();
        try {
            if (time != null) {
                this.db.execSQL("REPLACE INTO SearchHistory (Query, Time) VALUES (" + DatabaseUtils.sqlEscapeString(query) + ",'" + time + "')");
            } else {
                this.db.execSQL("REPLACE INTO SearchHistory (Query) VALUES (" + DatabaseUtils.sqlEscapeString(query) + ')');
            }
            this.db.execSQL("DELETE FROM SearchHistory WHERE DATETIME(Time,'+60 DAY') < CURRENT_TIMESTAMP");
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
        }
    }

    public void clearCachedInfo(boolean corruptedOnly) {
        String cmdText = corruptedOnly ? "UPDATE VideoFile SET NoThumbnail=0, Read=0, VideoTrackCount=0, AudioTrackCount=0, SubtitleTrackCount=0, SubtitleTrackTypes=0, Duration=0, FrameTime=0, Width=0, Height=0, Interlaced=NULL WHERE NoThumbnail=1 OR (Read=1 AND (VideoTrackCount+AudioTrackCount=0 OR Duration<=0))" : "UPDATE VideoFile SET NoThumbnail=0, Read=0, VideoTrackCount=0, AudioTrackCount=0, SubtitleTrackCount=0, SubtitleTrackTypes=0, Duration=0, FrameTime=0, Width=0, Height=0, Interlaced=NULL";
        this.db.execSQL(cmdText);
    }

    public void clearHistory() {
        this.db.beginTransaction();
        try {
            this.db.execSQL("DELETE FROM VideoStates");
            this.db.execSQL("DELETE FROM SubtitleStates");
            this.db.execSQL("UPDATE VideoFile SET LastWatchTime=NULL, FinishTime=NULL");
            this.db.execSQL("DELETE FROM SearchHistory");
            this.db.execSQL("DELETE FROM DirectOpenLog");
            this.db.execSQL("DELETE FROM SubtitleQueryLog");
            this.db.execSQL("DELETE FROM MovieTitleLog");
            this.db.setTransactionSuccessful();
            notifyChange(1);
        } finally {
            this.db.endTransaction();
        }
    }

    /* loaded from: classes2.dex */
    public static class SimpleLog {
        public final String input;
        public final long time;

        public SimpleLog(String input, long time) {
            this.input = input;
            this.time = time;
        }
    }

    public String getLastDirectOpenUri() {
        return getLastAccessedInput("DirectOpenLog");
    }

    public List<SimpleLog> readAllDirectOpenLogs() {
        return readAllSimpleLogs("DirectOpenLog");
    }

    public void writeDirectOpenLog(String input) {
        writeSimpleLog("DirectOpenLog", input, null);
    }

    public void writeDirectOpenLog(String input, Timestamp time) {
        writeSimpleLog("DirectOpenLog", input, time);
    }

    public List<SimpleLog> readAllSubtitleQueryLogs() {
        return readAllSimpleLogs("SubtitleQueryLog");
    }

    public void writeSubtitleQueryLog(String input, @Nullable Timestamp time) {
        writeSimpleLog("SubtitleQueryLog", input, time);
    }

    public List<SimpleLog> readAllMovieTitleLogs() {
        return readAllSimpleLogs("MovieTitleLog");
    }

    public void writeMovieTitleLog(String input, @Nullable Timestamp time) {
        writeSimpleLog("MovieTitleLog", input, time);
    }

    public String getLastAccessedInput(String table) {
        SQLiteStatement stmt = this.db.compileStatement("SELECT Input FROM " + table + " ORDER BY Id DESC LIMIT 1");
        try {
            return stmt.simpleQueryForString();
        } catch (SQLiteDoneException e) {
            return null;
        } finally {
            stmt.close();
        }
    }

    public List<SimpleLog> readAllSimpleLogs(String table) {
        List<SimpleLog> records = new ArrayList<>();
        Cursor c = this.db.rawQuery("SELECT Input, strftime('%s',Time) FROM " + table, null);
        try {
            if (c.moveToFirst()) {
                do {
                    SimpleLog record = new SimpleLog(c.getString(0), c.getLong(1) * 1000);
                    records.add(record);
                } while (c.moveToNext());
                return records;
            }
            return records;
        } finally {
            c.close();
        }
    }

    public Cursor querySimpleLog(String table, String columns, CharSequence startsWith, int limit) {
        return (startsWith == null || startsWith.length() == 0) ? this.db.rawQuery("SELECT " + columns + " FROM " + table + " ORDER BY Id DESC LIMIT " + limit, null) : this.db.rawQuery("SELECT " + columns + " FROM " + table + " WHERE Input COLLATE NOCASE LIKE '" + ((Object) DBUtils.stripLikeCtrlCharacters(startsWith)) + "%' ORDER BY Id DESC LIMIT " + limit, null);
    }

    public void writeSimpleLog(String table, String input, Timestamp time) {
        this.db.beginTransaction();
        try {
            if (time != null) {
                this.db.execSQL("REPLACE INTO " + table + " (Input, Time) VALUES (" + DatabaseUtils.sqlEscapeString(input) + ",'" + time + "')");
            } else {
                this.db.execSQL("REPLACE INTO " + table + " (Input) VALUES (" + DatabaseUtils.sqlEscapeString(input) + ")");
            }
            this.db.execSQL("DELETE FROM " + table + " WHERE DATETIME(Time,'" + HISTORY_EXPIRE_TIME + "') < CURRENT_TIMESTAMP");
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Observer {
        public final int types;

        public abstract void onChanged(int i);

        public Observer(int types) {
            this.types = types;
        }
    }

    public static void registerObserver(Observer observer) {
        _observers.add(observer);
    }

    public static void unregisterObserver(Observer observer) {
        _observers.remove(observer);
    }

    public void notifyChange(int changes) {
        if (_observers.size() > 0) {
            synchronized (MediaDatabase.class) {
                if (this._changes == 0) {
                    _handler.sendEmptyMessage(2);
                }
                this._changes |= changes;
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        int changes;
        if (msg.what == 1) {
            synchronized (MediaDatabase.class) {
                if (_refCount == 0) {
                    release_l();
                }
            }
        } else if (msg.what == 2) {
            synchronized (MediaDatabase.class) {
                changes = this._changes;
                this._changes = 0;
            }
            for (Observer ob : _observers) {
                if ((ob.types & changes) != 0) {
                    ob.onChanged(changes);
                }
            }
        }
        return false;
    }

    public static Uri makeUri(@Nullable String directory, String filename) {
        return directory == null ? Uri.parse(filename) : Uri.parse(directory + File.separatorChar + filename);
    }
}
