package com.mxtech.videoplayer.subtitle.service;

import android.net.Uri;
import com.mxtech.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class DataSubtitle extends Subtitle {
    private final byte[] _data;
    private final String _filename;

    public DataSubtitle(Uri sourceUri, String filename, byte[] data) {
        super(sourceUri);
        this._filename = filename;
        this._data = data;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public String directory() {
        String path;
        String parent;
        return (!this._filename.equalsIgnoreCase(this.sourceUri.getLastPathSegment()) || (path = this.sourceUri.toString()) == null || (parent = FileUtils.getParentPath(path)) == null) ? this.sourceUri.toString() : parent;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public String filename() {
        return this._filename;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public InputStream content() {
        return new ByteArrayInputStream(this._data);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public int size() {
        return this._data.length;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.Subtitle
    public boolean exists() {
        return true;
    }
}
