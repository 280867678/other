package com.mxtech.videoplayer.subtitle.service;

import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.mxtech.IOUtils;
import com.mxtech.net.HttpFactory;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public final class Media implements Comparable<Media> {
    private final int _durationMs;
    private final File _file;
    private final int _frameTimeNs;
    @Nullable
    private final HttpFactory _httpFactory;
    private String _opensubtitles_moviehash;
    private boolean _opensubtitles_moviehash_http_read_failed;
    private long _size;
    public final String displayName;
    public final String fileName;
    @Nullable
    public final String title;
    public final Uri uri;

    public Media(Uri uri, @Nullable HttpFactory httpFactory, String fileName, @Nullable File file, @Nullable String displayName, @Nullable String title, int durationMs, int frameTimeNs, long size, String opensubtitles_moviehash) {
        this.uri = uri;
        this.fileName = fileName;
        this.displayName = displayName == null ? fileName : displayName;
        this.title = title;
        this._httpFactory = httpFactory;
        this._file = file;
        this._durationMs = durationMs;
        this._frameTimeNs = frameTimeNs;
        this._size = size;
        this._opensubtitles_moviehash = opensubtitles_moviehash;
    }

    public Media(Uri uri, @Nullable HttpFactory httpFactory, String fileName, @Nullable File file, String displayName, @Nullable String title, int durationMs, int frameTimeNs) {
        this(uri, httpFactory, fileName, file, displayName, title, durationMs, frameTimeNs, 0L, null);
    }

    @Nullable
    public File file() {
        return this._file;
    }

    public int durationMs() {
        return this._durationMs;
    }

    public int frameTimeNs() {
        return this._frameTimeNs;
    }

    public long size() {
        if (this._size == 0 && this._file != null) {
            this._size = this._file.length();
        }
        return this._size;
    }

    @Nullable
    public synchronized String openSubtitlesMovieHash() {
        String str;
        long begin;
        if (this._opensubtitles_moviehash == null) {
            if (this._file != null) {
                try {
                    this._opensubtitles_moviehash = OpenSubtitles.computeHash(this._file);
                } catch (IOException ex) {
                    Log.e(OpenSubtitles.TAG, "IO exception occurred while calculating MovieHash from " + this._file, ex);
                }
            } else {
                String scheme = this.uri.getScheme();
                if (this._opensubtitles_moviehash_http_read_failed || this._httpFactory == null || !("http".equals(scheme) || "https".equals(scheme))) {
                    Log.w(OpenSubtitles.TAG, "Can't get MovieHash from " + this.uri);
                } else {
                    if (P.log_opensubtitles) {
                        begin = SystemClock.uptimeMillis();
                        Log.d(OpenSubtitles.TAG, "Retrieving MovieHash begin from " + this.uri);
                    } else {
                        begin = 0;
                    }
                    if (P.log_opensubtitles) {
                        Log.d(OpenSubtitles.TAG, "Reading head chunk.");
                    }
                    HttpURLConnection conn = this._httpFactory.openGetConnection(this.uri.toString());
                    this._size = conn.getContentLength();
                    if (this._size < 0) {
                        Log.w(OpenSubtitles.TAG, "Content length is unknown.");
                        this._size = 0L;
                        str = null;
                        if (conn != null) {
                            try {
                                conn.disconnect();
                            } catch (Throwable th) {
                            }
                        }
                    } else if (this._size == 0) {
                        Log.w(OpenSubtitles.TAG, "Content length is zero.");
                        str = null;
                        if (conn != null) {
                            try {
                                conn.disconnect();
                            } catch (Throwable th2) {
                            }
                        }
                    } else {
                        InputStream content = conn.getInputStream();
                        int chunkSize = OpenSubtitles.getHashChunkSize(this._size);
                        byte[] headChunk = new byte[chunkSize];
                        IOUtils.readFully(content, headChunk);
                        content.close();
                        conn.disconnect();
                        if (P.log_opensubtitles) {
                            Log.d(OpenSubtitles.TAG, "Reading tail chunk.");
                        }
                        ArrayMap<String, String> rangeHeader = new ArrayMap<>(1);
                        rangeHeader.put("Range", "bytes=" + (this._size - chunkSize) + "-");
                        HttpURLConnection conn2 = this._httpFactory.openGetConnection(this.uri.toString(), rangeHeader);
                        int status = conn2.getResponseCode();
                        if (status != 206) {
                            Log.w(OpenSubtitles.TAG, "Request failed or can't set range. status: " + status);
                            str = null;
                            if (conn2 != null) {
                                try {
                                    conn2.disconnect();
                                } catch (Throwable th3) {
                                }
                            }
                        } else {
                            InputStream content2 = conn2.getInputStream();
                            byte[] tailChunk = new byte[chunkSize];
                            IOUtils.readFully(content2, tailChunk);
                            content2.close();
                            conn2.disconnect();
                            HttpURLConnection conn3 = null;
                            this._opensubtitles_moviehash = OpenSubtitles.computeHash(this._size, ByteBuffer.wrap(headChunk), ByteBuffer.wrap(tailChunk));
                            if (P.log_opensubtitles) {
                                Log.d(OpenSubtitles.TAG, "Retrieving MovieHash ended. (" + (SystemClock.uptimeMillis() - begin) + "ms)");
                            }
                            if (0 != 0) {
                                try {
                                    conn3.disconnect();
                                } catch (Throwable th4) {
                                }
                            }
                        }
                    }
                }
            }
        }
        str = this._opensubtitles_moviehash;
        return str;
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public String toString() {
        return this.uri.toString();
    }

    public boolean equals(Object o) {
        return (o instanceof Media) && this.uri.equals(((Media) o).uri);
    }

    @Override // java.lang.Comparable
    public int compareTo(Media another) {
        return this.uri.compareTo(another.uri);
    }
}
