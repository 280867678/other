package com.mxtech.videoplayer.list;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import com.mxtech.StringUtils;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class FilesBuilder extends LocalBuilder {
    private Object _filter;

    @Override // com.mxtech.videoplayer.list.Builder, com.mxtech.videoplayer.MediaDatabase.Observer
    public /* bridge */ /* synthetic */ void onChanged(int i) {
        super.onChanged(i);
    }

    public FilesBuilder(ActivityMediaList context, MediaListFragment content) {
        super(context, content, 1036);
    }

    public FilesBuilder(Object filter, ActivityMediaList context, MediaListFragment content) {
        super(context, content, 13);
        this._filter = filter;
    }

    @Override // com.mxtech.videoplayer.list.Builder
    Uri uri() {
        if (this._filter instanceof String) {
            return Uri.fromParts("filter", (String) this._filter, null);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public String title() {
        if (this._filter instanceof String) {
            return StringUtils.getString_s(R.string.title_search_result, (String) this._filter);
        }
        if (this._filter == null) {
            return this.context.getString(P.isAudioPlayer ? R.string.title_media_list : R.string.title_video_list);
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public CharSequence getMessage(int messageType) {
        if (messageType == 1) {
            if (this._filter != null) {
                return this.content.getEmptyStringWithStateMessage(R.string.no_videos_in_search_result);
            }
            return getRootEmptyMessage();
        }
        return super.getMessage(messageType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public boolean isItemComplex() {
        return (P.list_fields & 198) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public Entry[] build() throws SQLiteException {
        ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
        MediaDatabase mdb = MediaDatabase.getInstance();
        try {
            MediaFile[] files = mdir.list("/", 39);
            if (this._filter != null) {
                files = filter(files);
            }
            Map<String, Entry> entries = createFilesEntries(files);
            updateFromDatabase(mdb, entries);
            if (this._filter != null) {
                checkGlobalLastMedia(mdb);
            }
            return finalizeBuilding(entries.values());
        } finally {
            mdb.release();
        }
    }

    private MediaFile[] filter(MediaFile[] files) {
        if (this._filter instanceof List) {
            ArrayList<String> matches = (ArrayList) this._filter;
            if (matches.size() != 0) {
                Iterator<String> it = matches.iterator();
                while (it.hasNext()) {
                    String match = it.next();
                    for (MediaFile file : files) {
                        if (file.state == 16 && StringUtils.indexOfIgnoreCase(file.name(), match) >= 0) {
                            this._filter = match;
                        }
                    }
                }
                this._filter = matches.get(0);
                return new MediaFile[0];
            }
            return files;
        }
        if (this._filter instanceof String) {
            ArrayList<MediaFile> filtered = new ArrayList<>();
            String str = (String) this._filter;
            for (MediaFile file2 : files) {
                if (StringUtils.indexOfIgnoreCase(file2.name(), str) >= 0) {
                    filtered.add(file2);
                }
            }
            return (MediaFile[]) filtered.toArray(new MediaFile[filtered.size()]);
        }
        return files;
    }

    private void updateFromDatabase(MediaDatabase mdb, Map<String, Entry> entries) throws SQLiteException {
        Cursor cursor = mdb.db.rawQuery("SELECT f.Id, f.Size, f.NoThumbnail, f.Read, f.VideoTrackCount, f.AudioTrackCount, f.SubtitleTrackCount, f.SubtitleTrackTypes, f.Duration, f.FrameTime, f.Width, f.Height, f.Interlaced, f.LastWatchTime, f.FinishTime, f.FileTimeOverriden, d.Path || '/' || f.FileName FROM VideoDirectory d INNER JOIN VideoFile f ON d.Id = f.Directory", null);
        try {
            if (cursor.moveToFirst()) {
                mdb.db.beginTransaction();
                do {
                    updateFileEntryFromCursor(mdb, cursor.getString(16), cursor, entries);
                } while (cursor.moveToNext());
                mdb.db.setTransactionSuccessful();
                mdb.db.endTransaction();
            }
        } finally {
            cursor.close();
        }
    }
}
