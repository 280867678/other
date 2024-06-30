package com.mxtech.videoplayer.list;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import com.mxtech.FileUtils;
import com.mxtech.media.MediaUtils;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class DirectoryBuilder extends LocalBuilder implements FileFilter {
    static final int HIERARCHY_FIXED = 1;
    static final int HIERARCHY_NONE = 0;
    static final int HIERARCHY_TOP_DIRS = 2;
    private String _commonRoot;
    private final MediaFile _directory;
    private final int _hierarchyMode;
    private int _numDirectories;
    private int _numOthers;
    private final boolean _primaryExternalStorage;

    @Override // com.mxtech.videoplayer.list.Builder, com.mxtech.videoplayer.MediaDatabase.Observer
    public /* bridge */ /* synthetic */ void onChanged(int i) {
        super.onChanged(i);
    }

    public DirectoryBuilder(MediaFile folder, int hierarchyMode, ActivityMediaList context, MediaListFragment content) {
        super(context, content, (hierarchyMode != 0 ? 2560 : 0) | 1036);
        this._directory = folder;
        this._hierarchyMode = hierarchyMode;
        if (folder != null) {
            this._primaryExternalStorage = folder.base.equals(content.helper.primaryExternalStorage);
        } else {
            this._primaryExternalStorage = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHierarchyMode() {
        return this._hierarchyMode;
    }

    @Override // com.mxtech.videoplayer.list.Builder
    Uri uri() {
        if (this._directory != null) {
            return this._directory.uri();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public String title() {
        if (this._primaryExternalStorage) {
            return this.content.helper.res.getString(R.string.internal_memory);
        }
        if (this._directory != null) {
            return MediaUtils.capitalizeWithDictionary(this._directory.name(), this.content.helper.titleSb);
        }
        return this.context.getString(P.isAudioPlayer ? R.string.title_media_list : R.string.title_video_list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public int getGroupCount() {
        int numGroups = 0;
        if (this._numDirectories > 0) {
            numGroups = 0 + 1;
        }
        if (this._numOthers > 0) {
            return numGroups + 1;
        }
        return numGroups;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public int getChildrenCount(int groupPosition) {
        if (this._numDirectories > 0) {
            if (groupPosition == 0) {
                return this._numDirectories;
            }
            return this._numOthers;
        }
        return this._numOthers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public CharSequence getGroupText(int groupPosition) {
        int resId;
        if (this._numDirectories > 0) {
            if (groupPosition == 0) {
                resId = R.string.title_folder_list;
            } else {
                resId = P.isAudioPlayer ? R.string.title_media_list : R.string.title_video_list;
            }
        } else {
            resId = P.isAudioPlayer ? R.string.title_media_list : R.string.title_video_list;
        }
        return this.context.getString(resId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public CharSequence getMessage(int messageType) {
        if (messageType == 1) {
            if (this._directory == null) {
                return getRootEmptyMessage();
            }
            return this.content.getEmptyStringWithStateMessage(P.isAudioPlayer ? R.string.no_media_in_this_folder : R.string.no_videos_in_this_folder);
        }
        return super.getMessage(messageType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public boolean isItemComplex() {
        return (P.list_fields & 198) != 0;
    }

    @Override // java.io.FileFilter
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public Entry[] build() throws SQLiteException {
        String path;
        Map<String, Entry> entryMap;
        Entry[] finalizeBuilding;
        this._commonRoot = null;
        this._numDirectories = 0;
        this._numOthers = 0;
        MediaDatabase mdb = MediaDatabase.getInstance();
        ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
        try {
            if (this._hierarchyMode == 2) {
                String[] topDirs = mdir.getTopDirectories();
                if (topDirs.length > 1) {
                    this.features &= -257;
                    Map<String, Entry> entryMap2 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    for (String dir : topDirs) {
                        createDirectoryEntries(mdb, entryMap2, FileUtils.getParentPath(dir), mdir.list(dir, 439));
                    }
                    finalizeBuilding = finalizeBuilding(entryMap2.values());
                    return finalizeBuilding;
                }
            }
            this.features |= 256;
            if ((this.features & 512) != 0) {
                path = this._directory != null ? this._directory.path : "/";
                MediaFile[] files = mdir.list(path, 439);
                int numFiles = files.length;
                if (numFiles < 2) {
                    finalizeBuilding = new Entry[0];
                } else {
                    if (this._directory == null) {
                        path = mdir.findCommonRoot();
                        this._commonRoot = path;
                        if (path == null) {
                            finalizeBuilding = new Entry[0];
                        }
                    }
                    if (files[0].path.equalsIgnoreCase(path)) {
                        ArrayList<MediaFile> directFilesMap = new ArrayList<>();
                        for (int i = 1; i < numFiles; i++) {
                            MediaFile file = files[i];
                            if (!file.isFile()) {
                                break;
                            }
                            directFilesMap.add(file);
                        }
                        entryMap = createFilesEntries((MediaFile[]) directFilesMap.toArray(new MediaFile[directFilesMap.size()]));
                    } else {
                        entryMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    }
                    createDirectoryEntries(mdb, entryMap, path, files);
                }
                return finalizeBuilding;
            }
            path = this._directory.path;
            entryMap = createFilesEntries(mdir.list(path, 7));
            updateFileEntriesFromDatabase(mdb, path, entryMap);
            Collection<Entry> entries = entryMap.values();
            if ((this.features & 512) != 0) {
                for (Entry entry : entries) {
                    if (entry instanceof DirectoryEntry) {
                        this._numDirectories++;
                    } else {
                        this._numOthers++;
                    }
                }
            }
            checkGlobalLastMedia(mdb);
            finalizeBuilding = finalizeBuilding(entries);
            return finalizeBuilding;
        } finally {
            mdb.release();
        }
    }

    private void updateFileEntriesFromDatabase(MediaDatabase mdb, String root, Map<String, Entry> entries) throws SQLiteException {
        Cursor cursor = mdb.db.rawQuery("SELECT Id, Size, NoThumbnail, Read, VideoTrackCount, AudioTrackCount, SubtitleTrackCount, SubtitleTrackTypes, Duration, FrameTime, Width, Height, Interlaced, LastWatchTime, FinishTime, FileTimeOverriden, FileName FROM VideoFile WHERE Directory=" + mdb.getDirectoryId(root), null);
        try {
            if (cursor.moveToFirst()) {
                mdb.db.beginTransaction();
                do {
                    updateFileEntryFromCursor(mdb, root + "/" + cursor.getString(16), cursor, entries);
                } while (cursor.moveToNext());
                mdb.db.setTransactionSuccessful();
                mdb.db.endTransaction();
            }
        } finally {
            cursor.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public void updateStatusText() {
        CharSequence text;
        if ((P.list_fields & 8) != 0) {
            if (this._commonRoot != null) {
                text = this._commonRoot;
            } else if (this._directory != null) {
                text = this._directory.path;
            } else {
                text = null;
            }
        } else if ((this.features & 512) != 0 && this._directory != null) {
            text = this.content.getPathToCurrentContent();
        } else {
            text = null;
        }
        this.content.container.setStatusText(text);
    }
}
