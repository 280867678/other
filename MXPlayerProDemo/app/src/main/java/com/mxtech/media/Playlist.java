package com.mxtech.media;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.list.MediaListFragment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class Playlist {
    private static final String TAG = App.TAG + ".Playlist";

    /* loaded from: classes.dex */
    public static final class Autogen {
        private static final long DAY = 86400000;
        private static final long HOUR = 3600000;
        private static final int KB = 1024;
        private static final long MINUTE = 60000;
        private static final long SECOND = 1000;
        private static final String TAG = Playlist.TAG + "/Autogen";
        private static boolean _garbageCollected = false;
        private static final String kDirName = "agpl";
        private static final String kHeadFourCC = "#EXTM3U";
        private static final String kHeadOption = "#EXTMXOPT:autogen=true,expand=true";
        private static final long kLifeTime = 604800000;
        private static final String kOptionStartFrom = ",start-from=";

        /* loaded from: classes.dex */
        public static class PL {
            public Uri[] playlist;
            @Nullable
            public Uri startUri;

            PL(@Nullable Uri startUri, Uri[] playlist) {
                this.startUri = startUri;
                this.playlist = playlist;
            }
        }

        @Nullable
        public static File create(Uri firstUri, Uri[] uris) {
            File file;
            File dir = dir();
            if (!_garbageCollected) {
                _garbageCollected = true;
                gc(dir);
            }
            String firstUriStr = firstUri.toString();
            int firstUriIndex = -1;
            int i = 0;
            int length = uris.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                Uri uri = uris[i2];
                if (firstUriStr.equals(uri.toString())) {
                    firstUriIndex = i;
                    break;
                }
                i++;
                i2++;
            }
            long time = System.currentTimeMillis();
            while (true) {
                long time2 = time + 1;
                file = new File(dir, Long.toHexString(time) + ".m3u");
                if (!file.exists()) {
                    try {
                        break;
                    } catch (IOException e) {
                        Log.e(TAG, "Can't write to " + file, e);
                        return null;
                    }
                }
                time = time2;
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(kHeadFourCC);
            writer.newLine();
            writer.write(kHeadOption);
            if (firstUriIndex >= 0) {
                writer.write(kOptionStartFrom);
                writer.write(Integer.toString(firstUriIndex));
            }
            writer.newLine();
            for (Uri uri2 : uris) {
                writer.write(uri2.toString());
                writer.newLine();
            }
            writer.close();
            return file;
        }

        @Nullable
        public static PL load(File file) {
            int startIndex;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String l0 = reader.readLine();
                if (l0 == null || !kHeadFourCC.equals(l0)) {
                    reader.close();
                    return null;
                }
                String l1 = reader.readLine();
                if (l1 == null || !l1.startsWith(kHeadOption)) {
                    reader.close();
                    return null;
                }
                if (l1.length() > kHeadOption.length() + kOptionStartFrom.length()) {
                    try {
                        startIndex = Integer.parseInt(l1.substring(kHeadOption.length() + kOptionStartFrom.length()));
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Can't parse 'start-from', start from the beginning.", e);
                        startIndex = Integer.MAX_VALUE;
                    }
                } else {
                    startIndex = Integer.MAX_VALUE;
                }
                ArrayList<Uri> uris = new ArrayList<>();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    uris.add(Uri.parse(line));
                }
                PL pl = new PL(startIndex < uris.size() ? uris.get(startIndex) : null, (Uri[]) uris.toArray(new Uri[uris.size()]));
                reader.close();
                return pl;
            } catch (IOException e2) {
                Log.e(TAG, "Can't read from " + file, e2);
                return null;
            }
        }

        public static boolean isAutoGenFile(Uri uri, boolean deepTest) {
            boolean z = false;
            String scheme = uri.getScheme();
            if (MediaListFragment.TYPE_FILE.equals(scheme)) {
                String name = uri.getLastPathSegment();
                if (name.endsWith(".m3u")) {
                    try {
                        Long.parseLong(name.substring(0, name.length() - 4), 16);
                        if (deepTest) {
                            File file = new File(uri.getPath());
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            String l0 = reader.readLine();
                            if (l0 != null && kHeadFourCC.equals(l0)) {
                                String l1 = reader.readLine();
                                if (l1 == null || !l1.startsWith(kHeadOption)) {
                                    reader.close();
                                } else {
                                    reader.close();
                                    z = true;
                                }
                            } else {
                                reader.close();
                            }
                        } else {
                            z = true;
                        }
                    } catch (Exception e) {
                    }
                }
            }
            return z;
        }

        public static void cleanup(Uri uri) {
            if (isAutoGenFile(uri, true)) {
                File file = new File(uri.getPath());
                file.delete();
            }
        }

        public static void gc() {
            gc(dir());
        }

        private static void gc(File dir) {
            File[] files = dir.listFiles();
            if (files != null) {
                long oldest = System.currentTimeMillis() - 604800000;
                for (File file : files) {
                    try {
                        String name = file.getName();
                        if (name.endsWith(".m3u")) {
                            long creationTime = Long.parseLong(FileUtils.stripExtension(name), 16);
                            if (creationTime < oldest) {
                                file.delete();
                            }
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        private static File dir() {
            return App.context.getDir(kDirName, 0);
        }
    }
}
