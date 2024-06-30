package com.mxtech.videoplayer.list;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import java.util.Collection;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class Builder extends MediaDatabase.Observer {
    protected static final int COL_AUDIO_TRACK_COUNT = 5;
    protected static final int COL_DURATION = 8;
    protected static final int COL_FILETIME_OVERRIDEN = 15;
    protected static final int COL_FINISHTIME = 14;
    protected static final int COL_FRAME_TIME = 9;
    protected static final int COL_HEIGHT = 11;
    protected static final int COL_ID = 0;
    protected static final int COL_INTERLACED = 12;
    protected static final int COL_LASTWATCHTIME = 13;
    protected static final int COL_NOTHUMBNAIL = 2;
    protected static final int COL_READ = 3;
    protected static final int COL_SIZE = 1;
    protected static final int COL_SUBTITLE_TRACK_COUNT = 6;
    protected static final int COL_SUBTITLE_TRACK_TYPES = 7;
    protected static final int COL_VIDEO_TRACK_COUNT = 4;
    protected static final int COL_WIDTH = 10;
    static final int FEATURE_CAN_CONTAIN_THUMB = 1024;
    static final int FEATURE_CAN_DELETE = 8;
    static final int FEATURE_CAN_RENAME = 4;
    static final int FEATURE_GROUP_ITEMS = 2048;
    static final int FEATURE_HIERARCHICAL = 512;
    static final int FEATURE_HOMOGENOUS_LOCATION = 256;
    static final int FEATURE_VOLATILE = 1;
    static final int MSG_EMPTY = 1;
    static final int MSG_ERROR = 2;
    static final int MSG_LOADING = 0;
    protected static final int NB_COLUMNS = 16;
    static final String SCHEME_FILE = "file";
    static final String SCHEME_FILTER = "filter";
    static final String TAG = "MX.List.Builder";
    private long _lastMediaFinishTime;
    protected final MediaListFragment content;
    protected final ActivityMediaList context;
    int features;
    Uri lastMediaUri;
    long lastMediaWatchTime;
    boolean titleContainsExtension;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Entry[] build() throws SQLiteException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void deleteUi(Entry[] entryArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isItemComplex();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void renameUi(Entry entry);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String title();

    abstract Uri uri();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Builder(ActivityMediaList context, MediaListFragment content, int features) {
        super(1);
        this.features = features;
        this.content = content;
        this.context = context;
        MediaDatabase.registerObserver(this);
    }

    @Override // com.mxtech.videoplayer.MediaDatabase.Observer
    public void onChanged(int changes) {
        this.content.container.rebuildAsync();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getGroupCount() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getGroupText(int groupPosition) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getMessage(int messageType) {
        if (messageType == 0) {
            return this.context.getString(R.string.loading);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean prepareBuild() {
        this.lastMediaUri = null;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetTitle(Entry[] entries) {
        this.titleContainsExtension = (P.list_fields & 16) != 0;
        for (Entry entry : entries) {
            entry.title = entry.getTitle();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Entry[] finalizeBuilding(Collection<Entry> entries) {
        long unfinishedLastMediaWatchTime = (this.lastMediaUri == null || this.lastMediaWatchTime <= this._lastMediaFinishTime) ? Long.MIN_VALUE : this.lastMediaWatchTime;
        this.titleContainsExtension = (P.list_fields & 16) != 0;
        long currentTime = System.currentTimeMillis();
        for (Entry entry : entries) {
            entry.displayType = entry.getDisplayType(unfinishedLastMediaWatchTime, currentTime);
            entry.title = entry.getTitle();
        }
        return (Entry[]) entries.toArray(new Entry[entries.size()]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void checkGlobalLastMedia(MediaDatabase mdb) throws SQLiteException {
        if (!App.prefs.getBoolean(Key.MARK_LAST_PLAYED_MEDIA_FOR_EACH_FOLDERS, true)) {
            useGlobalLastMedia(mdb);
        }
    }

    protected final void useGlobalLastMedia(MediaDatabase mdb) throws SQLiteException {
        MediaDatabase.FullPlaybackRecord info = mdb.getLastVideoInfo();
        if (info != null) {
            this.lastMediaUri = info.uri;
            this.lastMediaWatchTime = info.lastWatchTime;
            this._lastMediaFinishTime = info.finishTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void updateLastMedia(Uri uri, long lastWatchTime, long finishTime) {
        if (lastWatchTime > 0) {
            if (this.lastMediaUri == null || this.lastMediaWatchTime < lastWatchTime) {
                this.lastMediaUri = uri;
                this.lastMediaWatchTime = lastWatchTime;
                this._lastMediaFinishTime = finishTime;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateStatusText() {
        this.content.container.setStatusText(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        MediaDatabase.unregisterObserver(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int updateFileEntryFromCursor(MediaDatabase mdb, String path, Cursor c, Map<String, Entry> entries) throws SQLiteException {
        Boolean valueOf;
        Entry entry = entries.get(path);
        if (entry instanceof FileEntry) {
            long fileSize = entry.file.length();
            if (fileSize != c.getLong(1)) {
                if (fileSize == 0) {
                    return 0;
                }
                Log.d(TAG, "Deleting video file record for '" + path + "' as file size altered.");
                mdb.deleteFile(c.getInt(0), entry.file.base);
                return 1;
            }
            FileEntry fileEntry = (FileEntry) entry;
            fileEntry.infoRead = c.getInt(3) != 0;
            fileEntry.noThumbnail = c.getInt(2) != 0;
            fileEntry.videoTrackCount = (byte) c.getInt(4);
            fileEntry.audioTrackCount = (byte) c.getInt(5);
            fileEntry.subtitleTrackCount = (byte) c.getInt(6);
            fileEntry.subtitleTrackTypes = c.getInt(7);
            fileEntry.lastWatchTime = c.isNull(13) ? -1L : c.getLong(13);
            fileEntry.finishTime = c.isNull(14) ? -1L : c.getLong(14);
            fileEntry.fileTimeOverriden = c.isNull(15) ? 0L : c.getLong(15);
            if (fileEntry.durationMs <= 0) {
                fileEntry.durationMs = c.getInt(8);
            }
            fileEntry.frameTimeNs = c.getInt(9);
            fileEntry.width = c.getInt(10);
            fileEntry.height = c.getInt(11);
            if (c.isNull(12)) {
                valueOf = null;
            } else {
                valueOf = Boolean.valueOf(c.getInt(12) == 1);
            }
            fileEntry.interlaced = valueOf;
            updateLastMedia(entry.uri, fileEntry.lastWatchTime, fileEntry.finishTime);
        }
        return 0;
    }
}
