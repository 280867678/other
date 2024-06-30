package com.mxtech.media;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mxtech.videoplayer.App;

import java.io.File;
import java.util.List;

public class MediaUtils {


    public static String ffmpegPathFrom(Uri uri) {
        String scheme = uri.getScheme();
        return (scheme == null || "file".equals(scheme)) ? uri.getPath() : uri.toString();
    }

    public static boolean isMediaStoreUri(@NonNull Uri uri) {
        if ("content".equals(uri.getScheme()) && "media".equals(uri.getAuthority())) {
            List<String> segments = uri.getPathSegments();
            if (segments.size() != 4) {
                return false;
            }
            String type = segments.get(1);
            if (("video".equals(type) || "audio".equals(type)) && "media".equals(segments.get(2))) {
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


    @NonNull
    public static Uri translateContentUriToFileUri(@NonNull Uri uri) {
        try {
            Cursor cursor = App.cr.query(uri, new String[]{"_data"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    Uri fileUri = Uri.fromFile(new File(cursor.getString(0)));
                    Log.d("MX.MediaUtils", uri + " --> " + fileUri);
                    cursor.close();
                    return fileUri;
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MX", "", e);
        }
        return uri;
    }




}
