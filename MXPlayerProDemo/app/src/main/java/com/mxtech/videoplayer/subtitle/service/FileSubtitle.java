package com.mxtech.videoplayer.subtitle.service;

import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class FileSubtitle extends Subtitle {
    private final File _file;

    public FileSubtitle(Uri sourceUri, File file) {
        super(sourceUri);
        this._file = file;
    }

    public FileSubtitle(File file) {
        this(Uri.fromFile(file), file);
    }

    public FileSubtitle(Uri sourceUri) {
        this(sourceUri, new File(sourceUri.getPath()));
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public String filename() {
        return this._file.getName();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public String directory() {
        return this._file.getParent();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public InputStream content() throws FileNotFoundException {
        return new FileInputStream(this._file);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public int size() {
        return (int) this._file.length();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public boolean exists() {
        return this._file.exists();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public String toString() {
        return this._file.getPath();
    }
}
