package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.media.MediaUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.videoplayer.preference.Key;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressLint({"Registered"})
/* loaded from: classes.dex */
public class MediaSearchSuggestionProvider extends ContentProvider implements OrderedSharedPreferences.OnSharedPreferenceChangeListener {
    private static final int CACHE_EXPIRE_TIME = 60000;
    private long _cachedTime;
    private SortedSet<String> _fileNameCache;
    private static final String TAG = App.TAG + ".SSP";
    private static final String[] COLUMNS = {"_id", "suggest_text_1", "suggest_intent_query"};
    private static final String[] VIDEODB_PROJECTIONS = {"Id AS _id", "Query AS suggest_text_1", "Query AS suggest_intent_query"};

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Context context = getContext().getApplicationContext();
        if (context instanceof MXApplication) {
            ((MXApplication) context).init();
            if (((MXApplication) context).initInteractive(null)) {
                App.prefs.registerOnSharedPreferenceChangeListener(this);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case -527677737:
                if (key.equals(Key.SCAN_ROOTS)) {
                    c = 0;
                    break;
                }
                break;
            case 214489388:
                if (key.equals(Key.SHOW_HIDDEN)) {
                    c = 1;
                    break;
                }
                break;
            case 327429450:
                if (key.equals(Key.RESPECT_NOMEDIA)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
                this._fileNameCache = null;
                return;
            default:
                return;
        }
    }

    @Override // android.content.ContentProvider, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        this._fileNameCache = null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String[] listVideos;
        if (selectionArgs == null || selectionArgs.length == 0) {
            return null;
        }
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            String limitText = uri.getQueryParameter("limit");
            String query = selectionArgs[0];
            if (query.length() == 0) {
                Cursor querySearchHistory = mdb.querySearchHistory(VIDEODB_PROJECTIONS, limitText);
                mdb.release();
                return querySearchHistory;
            }
            int limit = Integer.MAX_VALUE;
            if (limitText != null) {
                try {
                    limit = Integer.parseInt(limitText);
                } catch (NumberFormatException e) {
                }
            }
            if (this._fileNameCache == null || this._cachedTime + 60000 < SystemClock.uptimeMillis()) {
                this._fileNameCache = new TreeSet(String.CASE_INSENSITIVE_ORDER);
                for (String path : MediaScanner.listVideos(mdb)) {
                    this._fileNameCache.add(FileUtils.getName(path));
                }
                this._cachedTime = SystemClock.uptimeMillis();
            }
            MatrixCursor dest = new MatrixCursor(COLUMNS);
            int i = 0;
            for (String filename : this._fileNameCache) {
                i++;
                if (StringUtils.containsIgnoreCase(filename, query)) {
                    MatrixCursor.RowBuilder r = dest.newRow();
                    String text = MediaUtils.capitalizeWithDictionary(filename, L.sb);
                    r.add(Integer.valueOf(i));
                    r.add(text);
                    r.add(text);
                    if (dest.getCount() == limit) {
                        break;
                    }
                }
            }
            mdb.release();
            return dest;
        } catch (SQLiteException e2) {
            Log.e(App.TAG, "", e2);
            return null;
        }
    }
}
