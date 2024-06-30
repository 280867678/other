package com.mxtech.videoplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.image.ImageScanner;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class MediaScanner extends HashMap<File, List<File>> implements FileFilter {
    public static final int IMAGE_FILE = 2;
    private static final int MAX_DEPTH = 20;
    public static final String NOMEDIA = ".nomedia";
    private static final String RECYCLE_BIN = "$RECYCLE.BIN";
    public static final int SUBTITLE_FILE = 1;
    public static final String TAG = App.TAG + ".Scanner";
    private int _depth;
    private boolean _interrupted;
    private final SortedMap<File, Boolean> _scanRoots;
    private final Map<String, Integer> _videoExts;

    public MediaScanner(Map<String, Integer> videoExts, SortedMap<File, Boolean> scanRoots) {
        this._videoExts = videoExts;
        this._scanRoots = scanRoots;
    }

    public void scan(File[] dirs) throws InterruptedException {
        super.clear();
        long beginMs = SystemClock.uptimeMillis();
        Log.v(TAG, "------- [Scan Begin] -------");
        Log.v(TAG, "  Primary external stroage: " + Environment.getExternalStorageDirectory());
        for (Map.Entry<File, Boolean> entry : this._scanRoots.entrySet()) {
            Log.v(TAG, "  " + (entry.getValue().booleanValue() ? "+" : "-") + entry.getKey());
        }
        for (File dir : dirs) {
            doScan(dir);
        }
        Log.v(TAG, "------- [Scan End (" + (SystemClock.uptimeMillis() - beginMs) + "ms)] -------");
    }

    private void doScan(File dir) throws InterruptedException {
        Log.v(TAG, "Scan (depth:" + this._depth + ") " + dir.getPath());
        if (this._depth > 20) {
            Log.w(TAG, "Maximum depth is reached while scanning storage. final directory=" + dir.getPath());
        } else if (RECYCLE_BIN.equalsIgnoreCase(dir.getName())) {
            Log.i(TAG, "Skip $RECYCLE.BIN");
        } else {
            this._depth++;
            try {
                if (this._interrupted) {
                    Log.d(TAG, "Scan is interrupted at " + dir.getPath());
                    throw new InterruptedException();
                } else if (P.respectNomedia && new File(dir, NOMEDIA).exists()) {
                    Log.i(TAG, "Skip " + dir.getPath() + " as " + NOMEDIA + " found.");
                } else {
                    File[] files = FileUtils.listFiles(dir, this);
                    if (files == null) {
                        Log.w(TAG, "Listing failed on " + dir.getPath());
                        return;
                    }
                    ArrayList arrayList = null;
                    for (File file : files) {
                        if (file.isDirectory()) {
                            doScan(file);
                        } else {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(file);
                        }
                    }
                    if (arrayList != null) {
                        super.put(dir, arrayList);
                    }
                }
            } finally {
                this._depth--;
            }
        }
    }

    public void interrupt() {
        this._interrupted = true;
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        boolean support = false;
        if (!P.showHidden && file.isHidden()) {
            Log.i(TAG, "File is hidden: " + file.getPath());
        } else if (file.isDirectory()) {
            String filePath = file.getPath();
            if (this._scanRoots.containsKey(file)) {
                Log.i(TAG, "Scan directory later since it is on the folder list: " + filePath);
            } else {
                try {
                    if (FileUtils.isSymbolicLink(filePath)) {
                        File canonical = FileUtils.getCanonicalFile(file);
                        if (!FileUtils.isLocatedIn(this._scanRoots, canonical.getPath())) {
                            Log.d(TAG, "Directory is a symbolic link but recognized since source path is outside video folder. symbolic link: " + filePath + " --> " + canonical.getPath());
                            support = true;
                        } else {
                            Log.i(TAG, "Directory is skipped since it is a symbolic link: " + filePath + " --> " + canonical.getPath());
                        }
                    } else {
                        support = true;
                    }
                } catch (IOException e) {
                }
            }
        } else if (file.length() != 0) {
            String ext = FileUtils.getExtension(file);
            if (ext != null && this._videoExts.get(ext) != null) {
                support = true;
            }
            if (support) {
                Log.v(TAG, "Found media file " + file.getPath());
            }
        }
        return support;
    }

    public static boolean isFileLocatedInScanRoots(String path) {
        return FileUtils.isLocatedIn(P.scanRoots, path);
    }

    public static boolean isDirectoryLocatedInScanRoots(File dir) {
        String path = dir.getPath();
        if (FileUtils.isLocatedIn(P.scanRoots, path)) {
            File canonical = FileUtils.getCanonicalFile(dir);
            if (canonical.equals(dir)) {
                return true;
            }
            String canonPath = canonical.getPath();
            return !FileUtils.isLocatedIn(P.scanRoots, canonPath);
        }
        return false;
    }

    public static boolean isPathVisible(String path, Map<String, Boolean> nomediaCache) {
        if (P.showHidden || !FileUtils.isAnyHidden(path)) {
            return (P.respectNomedia && FileUtils.containsFileInHierarchy(path, NOMEDIA, nomediaCache)) ? false : true;
        }
        return false;
    }

    public static boolean isPathVisible(File file) {
        if (P.showHidden || !FileUtils.isAnyHidden(file.getPath())) {
            return (P.respectNomedia && FileUtils.containsFileInHierarchy(file, NOMEDIA)) ? false : true;
        }
        return false;
    }

    private static void addDirectoryFromMediaCursor(Cursor mediaCursor, Map<String, File> dirs) throws SQLiteException {
        String parentPath;
        if (mediaCursor.moveToFirst()) {
            do {
                if (!mediaCursor.isNull(0) && (parentPath = FileUtils.getParentPath(mediaCursor.getString(0))) != null && !dirs.containsKey(parentPath)) {
                    File parent = new File(parentPath);
                    if (isDirectoryLocatedInScanRoots(parent)) {
                        dirs.put(parentPath, parent);
                    }
                }
            } while (mediaCursor.moveToNext());
        }
    }

    public static String[] listVideos(MediaDatabase vdb) {
        Map.Entry<String, Integer>[] directories;
        Cursor mediaCursor;
        long begin = SystemClock.uptimeMillis();
        HashSet<String> allFiles = new HashSet<>();
        TreeMap<String, File> dirs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, Integer> entry : vdb.getDirectories(true)) {
            String path = entry.getKey();
            File dir = new File(path);
            if (isDirectoryLocatedInScanRoots(dir)) {
                dirs.put(path, dir);
            }
        }
        try {
            Cursor mediaCursor2 = App.cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, null, null, null);
            if (mediaCursor2 != null) {
                addDirectoryFromMediaCursor(mediaCursor2, dirs);
                mediaCursor2.close();
            }
            if (P.isAudioPlayer && (mediaCursor = App.cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, null, null, null)) != null) {
                addDirectoryFromMediaCursor(mediaCursor, dirs);
                mediaCursor.close();
            }
        } catch (Exception e) {
            Log.e(ActivityList.TAG, "", e);
        }
        Map<String, Boolean> nomediaCache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Iterator<File> it = dirs.values().iterator();
        while (it.hasNext()) {
            File dir2 = it.next();
            if (isPathVisible(dir2.getPath(), nomediaCache)) {
                File[] files = FileUtils.listFiles(dir2, P.mediaFileFilter);
                if (files != null) {
                    for (File file : files) {
                        allFiles.add(file.getPath());
                    }
                }
            } else {
                it.remove();
            }
        }
        Log.v(ActivityList.TAG, "Building video file list (" + (SystemClock.uptimeMillis() - begin) + "ms)");
        return (String[]) allFiles.toArray(new String[allFiles.size()]);
    }

    public static void getMediaFiles(File dir, Collection<File> mediaFiles, Collection<File> auxFiles, int auxFileTypes, boolean associatedFilesOnly) {
        Collection<File> thisAuxFiles;
        String ext;
        File[] files = FileUtils.listFiles(dir);
        if (files != null) {
            if (associatedFilesOnly && mediaFiles == auxFiles) {
                thisAuxFiles = null;
            } else {
                thisAuxFiles = auxFiles;
            }
            for (File file : files) {
                if (!file.isDirectory()) {
                    String path = file.getPath();
                    if ((P.showHidden || !file.isHidden()) && (ext = FileUtils.getExtension(path)) != null) {
                        if (P.isSupportedMediaExtension(ext)) {
                            mediaFiles.add(file);
                        } else if ((auxFileTypes & 1) != 0 && SubtitleFactory.isSupportedExtension(ext)) {
                            if (thisAuxFiles == null) {
                                thisAuxFiles = new ArrayList<>();
                            }
                            thisAuxFiles.add(file);
                        } else if ((auxFileTypes & 2) != 0 && ImageScanner.isSupportedExtension(ext)) {
                            if (thisAuxFiles == null) {
                                thisAuxFiles = new ArrayList<>();
                            }
                            thisAuxFiles.add(file);
                        }
                    }
                }
            }
            if (associatedFilesOnly && thisAuxFiles != null) {
                filterAssociatedFiles(mediaFiles, thisAuxFiles);
                auxFiles.addAll(thisAuxFiles);
            }
        }
    }

    public static void getMediaFilesRecursive(File dir, Collection<File> mediaFiles, Collection<File> auxFiles, int auxFileTypes, boolean associatedFilesOnly) {
        Collection<File> thisMediaFiles;
        Collection<File> thisAuxFiles;
        String ext;
        File[] files = FileUtils.listFiles(dir);
        if (files != null) {
            if (associatedFilesOnly) {
                thisMediaFiles = null;
                thisAuxFiles = null;
            } else {
                thisMediaFiles = mediaFiles;
                thisAuxFiles = auxFiles;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    if (isDirectoryLocatedInScanRoots(file) && isPathVisible(file)) {
                        getMediaFilesRecursive(file, mediaFiles, auxFiles, auxFileTypes, associatedFilesOnly);
                    }
                } else {
                    String path = file.getPath();
                    if ((P.showHidden || !file.isHidden()) && (ext = FileUtils.getExtension(path)) != null) {
                        if (P.isSupportedMediaExtension(ext)) {
                            if (thisMediaFiles == null) {
                                thisMediaFiles = new ArrayList<>();
                            }
                            thisMediaFiles.add(file);
                        } else if ((auxFileTypes & 1) != 0 && SubtitleFactory.isSupportedExtension(ext)) {
                            if (thisAuxFiles == null) {
                                thisAuxFiles = new ArrayList<>();
                            }
                            thisAuxFiles.add(file);
                        } else if ((auxFileTypes & 2) != 0 && ImageScanner.isSupportedExtension(ext)) {
                            if (thisAuxFiles == null) {
                                thisAuxFiles = new ArrayList<>();
                            }
                            thisAuxFiles.add(file);
                        }
                    }
                }
            }
            if (associatedFilesOnly && thisMediaFiles != null) {
                mediaFiles.addAll(thisMediaFiles);
                if (thisAuxFiles != null) {
                    filterAssociatedFiles(thisMediaFiles, thisAuxFiles);
                    auxFiles.addAll(thisAuxFiles);
                }
            }
        }
    }

    public static File getMediaFile(File auxFile, Collection<File> mediaFiles) {
        String auxPath = auxFile.getPath();
        if (SubtitleFactory.isSupportedFile(auxFile)) {
            for (File mediaFile : mediaFiles) {
                if (SubtitleFactory.isSubtitleOf(mediaFile.getPath(), auxPath, false)) {
                    return mediaFile;
                }
            }
        } else if (ImageScanner.isSupportedFile(auxFile)) {
            for (File mediaFile2 : mediaFiles) {
                if (FileUtils.equalsNoExtension(mediaFile2.getPath(), auxPath, true)) {
                    return mediaFile2;
                }
            }
        }
        return null;
    }

    public static void filterAssociatedFiles(Collection<File> mediaFiles, Collection<File> auxFiles) {
        Iterator<File> it = auxFiles.iterator();
        while (it.hasNext()) {
            if (getMediaFile(it.next(), mediaFiles) == null) {
                it.remove();
            }
        }
    }
}
