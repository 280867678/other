package com.mxtech.videoplayer.directory;

import android.net.Uri;
import com.mxtech.FileUtils;
import com.mxtech.videoplayer.L;
import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

/* loaded from: classes.dex */
public final class MediaFile {
    public static Comparator<MediaFile> CASE_INSENSITIVE_FILE_FIRST_ORDER = new Comparator<MediaFile>() { // from class: com.mxtech.videoplayer.directory.MediaFile.1
        @Override // java.util.Comparator
        public int compare(MediaFile left, MediaFile right) {
            return MediaDirectory.compareToIgnoreCaseFileFirst(left.isDirectory() ? left.dirPath : left.path, right.isDirectory() ? right.dirPath : right.path);
        }
    };
    public static final int DIRECTORY_MASK = 32;
    public static final int FILE_MASK = 16;
    public static final int HIDDEN_DIRECTORY = 33;
    public static final int IMAGE_FILE = 17;
    public static final int INVALIDATED_DIRECTORY = 32;
    public static final int MEDIA_FILE = 16;
    public static final int SUBTITLE_FILE = 18;
    public static final int VISIBLE_DIRECTORY = 34;
    private long _lastModifiedCached;
    private long _lengthCached;
    private String _name;
    private Uri _uri;
    public final File base;
    final String dirPath;
    public final String path;
    public int state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MediaFile newFile(File base, int type) {
        return new MediaFile(base, null, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MediaFile newDirectory(File dir, String dirPath, int state) {
        L.observer.monitorDirectory(dir);
        return new MediaFile(dir, dirPath, state);
    }

    private MediaFile(File file, String dirPath, int state) {
        this._lastModifiedCached = -1L;
        this._lengthCached = -1L;
        this.base = file;
        this.path = file.getPath();
        this.dirPath = dirPath;
        this.state = state;
    }

    public MediaFile(MediaFile from) {
        this._lastModifiedCached = -1L;
        this._lengthCached = -1L;
        this.base = from.base;
        this.path = from.path;
        this.dirPath = from.dirPath;
        this.state = from.state;
        this._name = from._name;
        this._uri = from._uri;
        this._lastModifiedCached = from._lastModifiedCached;
        this._lengthCached = from._lengthCached;
    }

    public File[] listFiles() {
        return FileUtils.listFiles(this.base);
    }

    public File[] listFiles(FileFilter filter) {
        return FileUtils.listFiles(this.base, filter);
    }

    public Uri uri() {
        if (this._uri == null) {
            this._uri = Uri.fromFile(this.base);
        }
        return this._uri;
    }

    public String name() {
        if (this._name == null) {
            this._name = this.base.getName();
        }
        return this._name;
    }

    public void clearCache() {
        this._lastModifiedCached = -1L;
        this._lengthCached = -1L;
    }

    public long lastModified() {
        if (this._lastModifiedCached < 0) {
            this._lastModifiedCached = this.base.lastModified();
        }
        return this._lastModifiedCached;
    }

    public long length() {
        if (this._lengthCached < 0) {
            this._lengthCached = this.base.length();
        }
        return this._lengthCached;
    }

    public boolean isDirectory() {
        return (this.state & 32) != 0;
    }

    public boolean isFile() {
        return (this.state & 16) != 0;
    }

    public String toString() {
        String stateString;
        switch (this.state) {
            case 16:
                stateString = "file/media";
                break;
            case 17:
                stateString = "file/image";
                break;
            case 18:
                stateString = "file/subtitle";
                break;
            case 32:
                stateString = "directory/invalidated";
                break;
            case 33:
                stateString = "directory/hidden";
                break;
            case 34:
                stateString = "directory/visible";
                break;
            default:
                stateString = "Unknown(" + Integer.toString(this.state) + ")";
                break;
        }
        return this.base.getPath() + " [" + stateString + "]";
    }

    public boolean equals(Object o) {
        if (o instanceof MediaFile) {
            MediaFile other = (MediaFile) o;
            return this.state == other.state && this.path.equals(other.path);
        }
        return false;
    }

    public int hashCode() {
        return this.path.hashCode() + this.state;
    }
}
