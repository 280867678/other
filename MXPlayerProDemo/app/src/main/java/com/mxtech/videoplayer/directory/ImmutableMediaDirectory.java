package com.mxtech.videoplayer.directory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import java.util.Arrays;

/* loaded from: classes.dex */
public class ImmutableMediaDirectory {
    private final MediaDirectory _copied;
    private final String[] _topDirs;

    public ImmutableMediaDirectory() {
        this._copied = null;
        this._topDirs = new String[0];
    }

    public ImmutableMediaDirectory(MediaDirectory source, String[] topDirs) {
        this._topDirs = topDirs;
        this._copied = new MediaDirectory(source, 1);
    }

    public boolean contentEquals(MediaDirectory mdir, String[] topDirs) {
        return this._copied != null && Arrays.equals(this._topDirs, topDirs) && this._copied.contentEquals(mdir);
    }

    public boolean isDummy() {
        return this._copied == null;
    }

    @Nullable
    public String findCommonRoot() {
        if (this._copied != null) {
            return this._copied.findCommonRoot();
        }
        return null;
    }

    @NonNull
    public MediaFile[] list(String root, int flags) {
        return this._copied != null ? this._copied.list(root, flags) : new MediaFile[0];
    }

    @NonNull
    public String[] getTopDirectories() {
        return this._topDirs;
    }

    public MediaFile newFile(String path, File file, int fileState) {
        return this._copied != null ? this._copied.newFile(path, file, fileState) : MediaDirectory.newFileNoCached(path, file, fileState);
    }
}
