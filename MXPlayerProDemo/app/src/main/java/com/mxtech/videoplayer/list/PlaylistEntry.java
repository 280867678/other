package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.view.View;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.MediaFile;
import java.io.File;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class PlaylistEntry extends ListableEntry {
    public PlaylistEntry(Uri uri, MediaListFragment content) {
        super(uri, null, content, 96);
    }

    public PlaylistEntry(MediaFile file, MediaListFragment content) {
        super(Uri.fromFile(file.base), file, content, 108);
    }

    public PlaylistEntry(Uri uri, MediaFile file, MediaListFragment content) {
        super(uri, file, content, 108);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.ListableEntry
    public CharSequence getContentsText() {
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getLayoutResourceId() {
        return R.layout.list_row_media;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void render(View view) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public File[] getAssociatedFiles(int fileTypes) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public boolean renameTo(String newName) {
        return false;
    }
}
