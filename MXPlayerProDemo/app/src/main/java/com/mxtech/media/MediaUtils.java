package com.mxtech.media;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import com.mxtech.DBUtils;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogUtils;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class MediaUtils {
    public static final int FAIL = -1;
    public static final int FORMAT_PRECISE = 0;
    public static final int FORMAT_ROUGH = 1;
    public static final int OK = 0;
    public static final int OK_UPDATED = 1;
    private static final String TAG = App.TAG + ".MediaUtils";
    private static final String[] CAPS_DICTIONARY = {"sdcard"};

    public static boolean isMediaStoreVideoURI(Uri uri) {
        if ("content".equals(uri.getScheme()) && "media".equals(uri.getAuthority())) {
            List<String> segments = uri.getPathSegments();
            if (segments.size() == 4 && "video".equals(segments.get(1)) && "media".equals(segments.get(2))) {
                try {
                    Long.parseLong(segments.get(3));
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    public static File fileFromUri(Uri uri) {
        if (isFileUri(uri)) {
            return new File(uri.getPath());
        }
        if (isMediaStoreVideoURI(uri)) {
            try {
                Cursor cursor = MediaStore.Video.query(App.cr, uri, new String[]{"_data"});
                if (cursor != null) {
                    if (cursor.moveToFirst() && !cursor.isNull(0)) {
                        File file = new File(cursor.getString(0));
                        cursor.close();
                        return file;
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(App.TAG, "", e);
            }
        }
        return null;
    }

    private static int getMediaFileUris(Uri contentUri, String path, ArrayList<Uri> mediaUris) throws Exception {
        StringBuilder selection = new StringBuilder();
        selection.append("_data").append('=');
        DatabaseUtils.appendEscapedSQLString(selection, path);
        int numUris = 0;
        Cursor cursor = App.cr.query(contentUri, new String[]{"_id"}, selection.toString(), null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        mediaUris.add(Uri.withAppendedPath(contentUri, cursor.getString(0)));
                        numUris++;
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return numUris;
    }

    public static Uri[] getMediaUris(Uri uri, boolean includeAudios) {
        try {
            if (isFileUri(uri)) {
                ArrayList<Uri> mediaUris = new ArrayList<>();
                String path = uri.getPath();
                getMediaFileUris(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, path, mediaUris);
                if (includeAudios) {
                    getMediaFileUris(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, path, mediaUris);
                }
                if (mediaUris.size() > 0) {
                    return (Uri[]) mediaUris.toArray(new Uri[mediaUris.size()]);
                }
            } else if (isMediaStoreVideoURI(uri)) {
                return new Uri[]{uri};
            }
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
        return null;
    }

    public static String getDecorativeName(String path) {
        int lastSeparator = path.lastIndexOf(File.separatorChar);
        if (lastSeparator >= 0 && lastSeparator < path.length() - 1) {
            return path.substring(lastSeparator + 1);
        }
        return path;
    }

    @Nullable
    public static String getDecorativeLastPathSegment(Uri uri) {
        String name = uri.getLastPathSegment();
        if (name != null) {
            return getDecorativeName(name);
        }
        return name;
    }

    public static String retrieveTitle(Uri uri, boolean includeExtension, StringBuilder sb) {
        String stripped;
        DocumentFile doc;
        String name = null;
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(App.context, uri) && (doc = DocumentFile.fromSingleUri(App.context, uri)) != null) {
            name = doc.getName();
        }
        if (name == null) {
            name = getDecorativeLastPathSegment(uri);
        }
        if (name != null) {
            if (!includeExtension && (stripped = FileUtils.stripExtension(name)) != null) {
                name = stripped;
            }
            if (sb != null) {
                return capitalizeWithDictionary(name, sb);
            }
            return capitalizeWithDictionary(name);
        }
        return uri.toString();
    }

    public static String capitalizeWithDictionary(String s, StringBuilder sb) {
        String[] strArr;
        for (String dicWord : CAPS_DICTIONARY) {
            if (dicWord.equalsIgnoreCase(s)) {
                return dicWord;
            }
        }
        return StringUtils.capitalize(s, sb);
    }

    public static String capitalizeWithDictionary(String s) {
        String[] strArr;
        for (String dicWord : CAPS_DICTIONARY) {
            if (dicWord.equalsIgnoreCase(s)) {
                return dicWord;
            }
        }
        return StringUtils.capitalize(s);
    }

    public static void pauseMusicService(Context context) {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");
        context.sendBroadcast(intent);
    }

    public static boolean isFileUri(Uri uri) {
        String scheme = uri.getScheme();
        return scheme == null || MediaListFragment.TYPE_FILE.equals(scheme);
    }

    public static String ffmpegPathFrom(Uri uri) {
        String scheme = uri.getScheme();
        return (scheme == null || MediaListFragment.TYPE_FILE.equals(scheme)) ? uri.getPath() : uri.toString();
    }

    public static float getVolumeFromLevel(int level, int max) {
        int max2 = max + 1;
        return 1.0f - ((float) (Math.log(max2 - level) / Math.log(max2)));
    }

    public static int renameMediaTo(final ActivityVPBase activity, File file, File newFile, @Nullable File[] subFiles, @Nullable File coverFile) {
        Log.i(App.TAG, "Renaming media file: " + file.getPath() + " --> " + newFile.getPath() + " (Subtitle files:" + (subFiles != null ? subFiles.length : 0) + ")");
        if (activity.isFinishing()) {
            return -1;
        }
        if (newFile.exists()) {
            DialogUtils.alert(activity, activity.getString(R.string.edit_error_rename_file_fail) + ' ' + activity.getString(R.string.error_rename_duplicates), activity.getString(R.string.edit_rename_to));
            return -1;
        }
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            if (!FileUtils.renameTo(activity, file, newFile)) {
                activity.handleFileWritingFailure(file, 1, 1, new ActivityVPBase.FileOperationSink() { // from class: com.mxtech.media.MediaUtils.1
                    @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
                    public void onFileOperationRetry() {
                        ActivityVPBase.this.defaultFileOperationRetry();
                    }

                    @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
                    public void onFileOperationFailed(int numTotal, int numFailed) {
                        L.alertFileWriteFailureMessage(ActivityVPBase.this, R.string.edit_error_rename_file_fail, R.string.edit_rename_to);
                    }
                });
                mdb.release();
                return -1;
            }
            String newName = newFile.getName();
            Uri[] mediaUris = getMediaUris(Uri.fromFile(file), true);
            SQLiteDatabase db = mdb.db;
            db.beginTransaction();
            File directory = file.getParentFile();
            String direcotryPath = directory.getPath();
            mdb.renameFile(file, newFile);
            if (coverFile != null) {
                FileUtils.renameTo(activity, coverFile, new File(directory, newName + '.' + FileUtils.getExtension(coverFile)));
            }
            if (subFiles != null) {
                for (int i = 0; i < subFiles.length; i++) {
                    File oldSub = subFiles[i];
                    String subFolder = oldSub.getParent();
                    if (direcotryPath.equalsIgnoreCase(subFolder)) {
                        String ext = FileUtils.getExtension(oldSub);
                        File newSub = new File(directory, newName + '.' + ext);
                        if (!newSub.exists() && FileUtils.renameTo(activity, oldSub, newSub)) {
                            subFiles[i] = newSub;
                            mdb.renameSubFile(oldSub, newSub);
                        }
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            if (mediaUris != null) {
                ContentValues values = new ContentValues(3);
                values.put("_data", newFile.getPath());
                values.put("_display_name", newName);
                values.put("title", newName);
                try {
                    for (Uri uri : mediaUris) {
                        App.cr.update(uri, values, null, null);
                    }
                    mdb.release();
                    return 1;
                } catch (Exception e) {
                    Log.e(App.TAG, "", e);
                }
            }
            mdb.release();
            return 0;
        } catch (SQLiteException e2) {
            Log.e(App.TAG, "", e2);
            return -1;
        }
    }

    private static void renameDirectoryInMediaStore(Uri contentUri, String oldDir, String newDir) throws Exception {
        ContentValues values = new ContentValues(1);
        Cursor cursor = App.cr.query(contentUri, new String[]{"_id", "_data"}, DBUtils.pathFilterString("_data", oldDir, true), null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    StringBuilder sb = new StringBuilder();
                    do {
                        if (!cursor.isNull(1)) {
                            String path = cursor.getString(1);
                            sb.setLength(0);
                            sb.append(newDir);
                            sb.append(path.substring(oldDir.length()));
                            values.put("_data", sb.toString());
                            sb.setLength(0);
                            sb.append("_id").append('=').append(cursor.getString(0));
                            App.cr.update(contentUri, values, sb.toString(), null);
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    public static void renameDirectoryInStorages(File oldDir, File newDir) {
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            mdb.renameDirectory(oldDir, newDir);
            String oldPath = oldDir.getPath();
            String newPath = newDir.getPath();
            try {
                renameDirectoryInMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, oldPath, newPath);
                renameDirectoryInMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, oldPath, newPath);
            } catch (Exception e) {
                Log.e(App.TAG, "", e);
            }
            TreeMap<File, Boolean> newRoots = null;
            for (Map.Entry<File, Boolean> scanRoot : P.scanRoots.entrySet()) {
                File file = scanRoot.getKey();
                String path = file.getPath();
                if (FileUtils.isLocatedIn(path, oldPath)) {
                    if (newRoots == null) {
                        newRoots = new TreeMap<>(FileUtils.CASE_INSENSITIVE_ORDER);
                        newRoots.putAll(P.scanRoots);
                    }
                    String newScanRoot = newPath + path.substring(oldPath.length());
                    newRoots.remove(file);
                    newRoots.put(new File(newScanRoot), scanRoot.getValue());
                }
            }
            if (newRoots != null) {
                P.setScanRoots(newRoots);
            }
            mdb.release();
        } catch (Exception e2) {
            Log.e(App.TAG, "", e2);
        }
    }

    public static boolean delete(MediaDatabase mdb, int dirId, File file) {
        if (FileUtils.delete(App.cr, file)) {
            Log.d(App.TAG, file + " is deleted from file system.");
            String ext = FileUtils.getExtension(file);
            if (ext == null || !P.isSupportedMediaExtension(ext)) {
                return true;
            }
            try {
                Uri[] uris = getMediaUris(Uri.fromFile(file), true);
                if (uris != null) {
                    for (Uri uri : uris) {
                        if (App.cr.delete(uri, null, null) > 0) {
                            Log.d(App.TAG, uri + " is removed from media store.");
                        } else {
                            Log.w(App.TAG, uri + " is NOT removed from media store.");
                        }
                    }
                }
                if (dirId == 0) {
                    try {
                        dirId = mdb.getDirectoryIdNoCreate(file.getParent());
                    } catch (SQLiteDoneException e) {
                        return true;
                    }
                }
                int fileId = mdb.getFileIDOrThrow(dirId, file.getName());
                mdb.deleteFile(fileId, file);
                return true;
            } catch (Exception e2) {
                Log.e(App.TAG, "", e2);
                return true;
            }
        }
        Log.w(App.TAG, file + " is NOT deleted from file system.");
        return false;
    }

    public static Uri canonicalizeUri(Uri uri) {
        File file = fileFromUri(uri);
        if (file != null) {
            Uri canonUri = Uri.fromFile(file);
            Log.v(TAG, "Canonicalize URI " + uri + " -> " + canonUri);
            return canonUri;
        }
        return uri;
    }

    public static String formatFrameTime(int frameTimeNs, int type) {
        double frameRate = 1.0E9f / frameTimeNs;
        int frameRateNearest = (int) Math.round(frameRate);
        double fraction = Math.abs(frameRateNearest - frameRate);
        return type == 0 ? fraction < 0.01d ? Integer.toString(frameRateNearest) : String.format(Locale.US, "%.5f", Double.valueOf(frameRate)) : fraction < 0.01d ? Integer.toString(frameRateNearest) : String.format(Locale.US, "%.2f", Double.valueOf(frameRate));
    }

    public static double frameTimeToFrameRate(int frameTimeNs) {
        double frameRate = 1.0E9f / frameTimeNs;
        int frameRateNearest = (int) Math.round(frameRate);
        double fraction = Math.abs(frameRateNearest - frameRate);
        if (fraction < 0.01d) {
            return frameRateNearest;
        }
        return frameRate;
    }
}
