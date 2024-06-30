package com.mxtech.videoplayer.list;

import android.database.sqlite.SQLiteException;
import android.net.Uri;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class AllDirectoriesBuilder extends LocalBuilder {
    @Override // com.mxtech.videoplayer.list.Builder, com.mxtech.videoplayer.MediaDatabase.Observer
    public /* bridge */ /* synthetic */ void onChanged(int i) {
        super.onChanged(i);
    }

    public AllDirectoriesBuilder(ActivityMediaList context, MediaListFragment content) {
        super(context, content, 12);
    }

    @Override // com.mxtech.videoplayer.list.Builder
    Uri uri() {
        return null;
    }

    @Override // com.mxtech.videoplayer.list.Builder
    String title() {
        return this.context.getString(R.string.title_folder_list);
    }

    @Override // com.mxtech.videoplayer.list.Builder
    CharSequence getMessage(int messageType) {
        return messageType == 1 ? getRootEmptyMessage() : super.getMessage(messageType);
    }

    @Override // com.mxtech.videoplayer.list.Builder
    boolean isItemComplex() {
        return (P.list_fields & 10) != 0;
    }

    @Override // com.mxtech.videoplayer.list.Builder
    Entry[] build() throws SQLiteException {
        MediaDatabase mdb = MediaDatabase.getInstance();
        try {
            ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
            List<Entry> entries = new ArrayList<>();
            MediaFile[] files = mdir.list("/", 433);
            int numFiles = files.length;
            long currentTime = System.currentTimeMillis();
            int i = 0;
            while (i < numFiles) {
                MediaFile file = files[i];
                if (file.isDirectory()) {
                    DirectoryEntry entry = this.content.newDirectoryEntry(file, false);
                    i = updateDirectoryEntry(mdb, entry, file.path, files, i + 1, currentTime);
                    entry.numDirectItems = entry.count;
                    entries.add(entry);
                } else {
                    i++;
                }
            }
            return finalizeBuilding(entries);
        } finally {
            mdb.release();
        }
    }
}
