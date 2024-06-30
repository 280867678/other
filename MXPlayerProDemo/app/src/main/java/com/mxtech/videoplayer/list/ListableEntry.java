package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.mxtech.FileUtils;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class ListableEntry extends Entry {
    int count;
    int numFinished;
    int numNew;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract CharSequence getContentsText();

    public ListableEntry(Uri uri, MediaFile file, MediaListFragment content, int features) {
        super(uri, file, content, features);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int type(@Nullable List<Uri> uris) {
        Uri[] items = getItems();
        if (uris != null) {
            uris.addAll(Arrays.asList(items));
        }
        int numItems = items.length;
        if (numItems == 0) {
            return 0;
        }
        SortedSet<String> audioExts = P.getDefaultAudioExtensions();
        int numAudio = 0;
        int numVideo = 0;
        int numUnknown = 0;
        for (Uri item : items) {
            String ext = FileUtils.getExtension(item.toString());
            if (ext != null) {
                if (audioExts.contains(ext)) {
                    numAudio++;
                } else {
                    numVideo++;
                }
            } else {
                numUnknown++;
            }
        }
        if (numAudio == numItems) {
            return 1;
        }
        if (numVideo == numItems) {
            return 2;
        }
        return numUnknown != numItems ? 3 : 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getDisplayType(long unfinishedLastMediaWatchTime, long currentTime) {
        int type = this.lastWatchTime == unfinishedLastMediaWatchTime ? 1 : 0;
        if (this.numNew > 0) {
            return type | 2;
        }
        if (this.count == this.numFinished) {
            return type | 4;
        }
        return type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void open() {
        this.content.container.openUri(this.uri);
    }
}
