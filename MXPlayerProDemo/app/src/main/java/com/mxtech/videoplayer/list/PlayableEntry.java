package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class PlayableEntry extends Entry {
    int durationMs;
    long fileTimeOverriden;
    long finishTime;
    int frameTimeNs;
    int height;
    Boolean interlaced;
    int width;

    public PlayableEntry(Uri uri, MediaListFragment content, int features) {
        super(uri, null, content, features | 64);
        this.finishTime = -1L;
    }

    public PlayableEntry(Uri uri, MediaFile file, MediaListFragment content, int features) {
        super(uri, file, content, features | 64);
        this.finishTime = -1L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int type(@Nullable List<Uri> uris) {
        if (uris != null) {
            uris.add(this.uri);
        }
        if (this.ext != null) {
            if (P.getDefaultAudioExtensions().contains(this.ext)) {
                return 1;
            }
            return 2;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasResolution() {
        return this.width > 0 && this.height > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void open() {
        this.content.play(this.uri);
    }
}
