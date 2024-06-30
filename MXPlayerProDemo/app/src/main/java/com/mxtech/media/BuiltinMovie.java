package com.mxtech.media;

import android.annotation.SuppressLint;
import android.database.Cursor;
//import android.media.MediaMetadataSafeRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
//import com.google.android.gms.plus.PlusShare;
import com.mxtech.app.dele.MediaMetadataSafeRetriever;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.preference.Tuner;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({"InlinedApi"})
/* loaded from: classes2.dex */
public class BuiltinMovie implements IMediaInfo {
    public static final String[] PROJECTION_ID = {"_id"};
    private final boolean _isMediaStoreURI;
    private MediaMetadataSafeRetriever _meta;
    private boolean _metaTried;
    private final Uri _uri;
    private int _width = -1;
    private int _height = -1;

    public BuiltinMovie(Uri uri) {
        this._uri = uri;
        this._isMediaStoreURI = MediaUtils.isMediaStoreVideoURI(uri);
    }

    @Override // com.mxtech.media.IMediaInfo
    public void close() {
        if (this._meta != null) {
            this._meta.release();
            this._meta = null;
        }
    }

    private String readMediaStore(String column) {
        try {
            Cursor cursor = MediaStore.Video.query(App.cr, this._uri, new String[]{column});
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    String string = cursor.getString(0);
                    cursor.close();
                    return string;
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
        return null;
    }

    private boolean createMetaRetriever() {
        String path;
        if (this._metaTried) {
            return false;
        }
        this._metaTried = true;
        if (!MediaUtils.isFileUri(this._uri) || (path = this._uri.getPath()) == null) {
            return false;
        }
        try {
            this._meta = new MediaMetadataSafeRetriever();
            this._meta.setDataSource(path);
            return true;
        } catch (Exception e) {
            if (this._meta != null) {
                this._meta.release();
                this._meta = null;
                return false;
            }
            return false;
        }
    }

    private String readMetadata(int key) {
        if (this._meta != null || createMetaRetriever()) {
            return this._meta.extractMetadata(key);
        }
        return null;
    }

    private String readInfo(String column, int key) {
        if (column != null && this._isMediaStoreURI) {
            return readMediaStore(column);
        }
        if (key >= 0) {
            return readMetadata(key);
        }
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String format() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String description() {
        return readInfo("description", -1);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String album() {
        return readInfo("album", 1);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String artist() {
        String value = readInfo("artist", 2);
        if ("<unknown>".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String composer() {
        return readInfo(null, 4);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String genre() {
        return readInfo(null, 6);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String title() {
        return readInfo("title", 7);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String mimeType() {
        return readInfo("mime_type", 12);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String albumArtist() {
        return readInfo(null, 13);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String copyright() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String encoder() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String encoded_by() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String performer() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String publisher() {
        return null;
    }

    @Override // com.mxtech.media.IMediaInfo
    public String year() {
        String value = readInfo(null, 8);
        if (Tuner.TAG_STYLE.equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    @Override // com.mxtech.media.IMediaInfo
    public Locale[] locales() {
        return new Locale[0];
    }

    @Override // com.mxtech.media.IMediaInfo
    public String displayLocales() {
        return readInfo("language", -1);
    }

    @Override // com.mxtech.media.IMediaInfo
    public int duration() {
        try {
            return Integer.parseInt(readInfo(ActivityScreen.EXTRA_DURATION, 9));
        } catch (NumberFormatException e) {
            Log.w(App.TAG, "", e);
            return 0;
        }
    }

    private void readResolution() {
        this._width = 0;
        this._height = 0;
        String res = readInfo("resolution", -1);
        if (res != null) {
            Pattern pattern = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)");
            Matcher m = pattern.matcher(res);
            if (m.find()) {
                try {
                    this._width = Integer.parseInt(m.group(1));
                    this._height = Integer.parseInt(m.group(2));
                } catch (NumberFormatException e) {
                    Log.w(App.TAG, "", e);
                }
            }
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int width() {
        if (this._width < 0) {
            readResolution();
        }
        return this._width;
    }

    @Override // com.mxtech.media.IMediaInfo
    public int height() {
        if (this._height < 0) {
            readResolution();
        }
        return this._height;
    }

    @Override // com.mxtech.media.IMediaInfo
    public int displayWidth() {
        return width();
    }

    @Override // com.mxtech.media.IMediaInfo
    public int displayHeight() {
        return height();
    }
}
