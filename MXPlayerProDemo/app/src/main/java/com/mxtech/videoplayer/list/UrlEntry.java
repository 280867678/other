package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.view.View;
import com.mxtech.videoplayer.pro.R;
import java.io.File;

/* loaded from: classes2.dex */
class UrlEntry extends PlayableEntry {
    /* JADX INFO: Access modifiers changed from: package-private */
    public UrlEntry(Uri uri, MediaListFragment content) {
        super(uri, content, 3932192);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getDisplayType(long unfinishedLastMediaWatchTime, long currentTime) {
        int type = this.lastWatchTime == unfinishedLastMediaWatchTime ? 1 : 0;
        if (this.finishTime >= 0) {
            return type | 4;
        }
        return type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void render(View view) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getLayoutResourceId() {
        return R.layout.list_row_media;
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
