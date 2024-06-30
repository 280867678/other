package com.mxtech.videoplayer.directory;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.image.ImageScanner;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.MediaScanner;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/* loaded from: classes.dex */
public final class MediaDirectory {
    public static final int DEEP_COPY = 1;
    public static final int FLAG_DIRECTORIES = 16;
    public static final int FLAG_IMAGE_FILES = 4;
    public static final int FLAG_INCLUDE_EMPTY_DIRECTORIES = 64;
    public static final int FLAG_INCLUDE_ROOT = 128;
    public static final int FLAG_MEDIA_FILES = 1;
    public static final int FLAG_NO_FILES = 8;
    public static final int FLAG_ORDERED = 256;
    public static final int FLAG_SCAN_SUBDIRECTORIES = 32;
    public static final int FLAG_SUBTITLE_FILES = 2;
    private static final int MEDIACOL_DATA = 0;
    private boolean _dirty;
    private final SortedMap<String, MediaFile> _files;
    public static final String TAG = App.TAG + ".MediaDirectory";
    public static final FileFilter directoryFilter = new FileFilter() { // from class: com.mxtech.videoplayer.directory.MediaDirectory.1
        @Override // java.io.FileFilter
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };
    public static final FileFilter fileFilter = new FileFilter() { // from class: com.mxtech.videoplayer.directory.MediaDirectory.2
        @Override // java.io.FileFilter
        public boolean accept(File file) {
            return file.isFile();
        }
    };
    private static final String[] MEDIASTORE_PROJECTION = {"_data"};
    private static Comparator<String> CASE_INSENSITIVE_FILE_FIRST_ORDER = new Comparator<String>() { // from class: com.mxtech.videoplayer.directory.MediaDirectory.3
        @Override // java.util.Comparator
        public int compare(String left, String right) {
            return MediaDirectory.compareToIgnoreCaseFileFirst(left, right);
        }
    };

    public MediaDirectory() {
        this._files = new TreeMap(CASE_INSENSITIVE_FILE_FIRST_ORDER);
        this._dirty = true;
    }

    public MediaDirectory(boolean needBuilding) {
        this._files = new TreeMap(CASE_INSENSITIVE_FILE_FIRST_ORDER);
        this._dirty = needBuilding;
    }

    public MediaDirectory(MediaDirectory from) {
        this(from, 0);
    }

    public MediaDirectory(MediaDirectory from, int flags) {
        this._files = new TreeMap(CASE_INSENSITIVE_FILE_FIRST_ORDER);
        if ((flags & 1) != 0) {
            for (Map.Entry<String, MediaFile> entry : from._files.entrySet()) {
                this._files.put(entry.getKey(), new MediaFile(entry.getValue()));
            }
        } else {
            this._files.putAll(from._files);
        }
        this._dirty = from._dirty;
    }

    public boolean contentEquals(MediaDirectory other) {
        return this._files.equals(other._files);
    }

    private static String makeDirectoryPath(String path) {
        int len = path.length();
        if (len == 0) {
            return File.separator;
        }
        if (path.charAt(len - 1) != File.separatorChar) {
            return path + File.separatorChar;
        }
        return path;
    }

    private void build() {
        Map.Entry<String, Integer>[] directories;
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            addDirectoriesFromMediaStore();
            for (Map.Entry<String, Integer> pathId : mdb.getDirectories(true)) {
                String path = pathId.getKey();
                String dirPath = makeDirectoryPath(path);
                this._files.put(dirPath, MediaFile.newDirectory(new File(path), dirPath, 32));
            }
            mdb.release();
        } catch (SQLiteException e) {
            Log.e(TAG, "", e);
        }
    }

    private void addDirectoriesFromMediaStore(Uri baseContentUri, Cursor cursor) throws SQLiteException {
        String directory;
        if (cursor.moveToFirst()) {
            String prevDirectory = null;
            do {
                if (!cursor.isNull(0)) {
                    String filePath = cursor.getString(0);
                    if ((prevDirectory == null || !FileUtils.isDirectAscendantOf(filePath, prevDirectory)) && (directory = FileUtils.getParentPath(filePath)) != null) {
                        prevDirectory = directory;
                        String dirPath = makeDirectoryPath(directory);
                        this._files.put(dirPath, MediaFile.newDirectory(new File(directory), dirPath, 32));
                    }
                }
            } while (cursor.moveToNext());
        }
    }

    private void addDirectoriesFromMediaStore() {
        Cursor cursor;
        try {
            Cursor cursor2 = App.cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MEDIASTORE_PROJECTION, null, null, null);
            if (cursor2 != null) {
                addDirectoriesFromMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor2);
                cursor2.close();
            }
            if (P.isAudioPlayer && (cursor = App.cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIASTORE_PROJECTION, null, null, null)) != null) {
                addDirectoriesFromMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public MediaFile newFile(String path, File file, int fileState) {
        if (path == null) {
            path = file.getPath();
        } else if (file == null) {
            file = new File(path);
        }
        boolean isDirectory = (fileState & 32) != 0;
        if (isDirectory) {
            path = makeDirectoryPath(path);
        }
        MediaFile mfile = this._files.get(path);
        if (mfile == null) {
            if (isDirectory) {
                return MediaFile.newDirectory(file, path, fileState);
            }
            return MediaFile.newFile(file, fileState);
        }
        return mfile;
    }

    public static MediaFile newFileNoCached(String path, File file, int fileState) {
        if (path == null) {
            path = file.getPath();
        } else if (file == null) {
            file = new File(path);
        }
        boolean isDirectory = (fileState & 32) != 0;
        if (isDirectory) {
            path = makeDirectoryPath(path);
        }
        if (isDirectory) {
            return MediaFile.newDirectory(file, path, fileState);
        }
        return MediaFile.newFile(file, fileState);
    }

    private static void scanDirectory(MediaFile directory, Map<String, MediaFile> map) {
        String path;
        String ext;
        int type;
        File[] files = directory.listFiles(fileFilter);
        if (files != null) {
            for (File file : files) {
                if ((P.showHidden || !file.isHidden()) && (ext = FileUtils.getExtension((path = file.getPath()))) != null) {
                    if (P.isSupportedMediaExtension(ext)) {
                        type = 16;
                    } else if (SubtitleFactory.isSupportedExtension(ext)) {
                        type = 18;
                    } else if (ImageScanner.isSupportedExtension(ext)) {
                        type = 17;
                    }
                    if (!map.containsKey(path)) {
                        map.put(path, MediaFile.newFile(file, type));
                    }
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:122:0x01fe A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:126:0x012d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0140 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0153 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0166 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0187 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x018a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0038 A[SYNTHETIC] */
    @NonNull
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public MediaFile[] list(String root, int flags) {
        MediaFile[] result;
        boolean addDirectoryIfNonEmpty;
        MediaFile file;
        if (this._dirty) {
            this._dirty = false;
            build();
        }
        String root2 = makeDirectoryPath(root);
        int rootLen = root2.length();
        ArrayList<MediaFile> list = new ArrayList<>();
        ArrayList<MediaFile> rescans = null;
        MediaFile lastDirectory = null;
        for (Map.Entry<String, MediaFile> entry : this._files.tailMap(root2).entrySet()) {
            String path = entry.getKey();
            if ((flags & 32) != 0) {
                if (!StringUtils.startsWithIgnoreCase(path, root2)) {
                    if (rescans != null) {
                        Map<String, MediaFile> newMap = null;
                        Map<String, Boolean> nomediaCache = null;
                        Iterator<MediaFile> it = rescans.iterator();
                        while (it.hasNext()) {
                            MediaFile dir = it.next();
                            if (dir.base.exists()) {
                                if (nomediaCache == null) {
                                    nomediaCache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                                }
                                if (MediaScanner.isPathVisible(dir.path, nomediaCache) && MediaScanner.isDirectoryLocatedInScanRoots(dir.base)) {
                                    if (newMap == null) {
                                        newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                                    } else {
                                        newMap.clear();
                                    }
                                    dir.state = 34;
                                    boolean isRoot = dir.path.length() <= rootLen;
                                    if ((flags & 16) != 0 && (!isRoot ? (flags & 32) != 0 || FileUtils.isDirectAscendantOf(dir.path, root2) : (flags & 128) != 0)) {
                                        if ((flags & 64) != 0) {
                                            list.add(dir);
                                            addDirectoryIfNonEmpty = false;
                                        } else {
                                            addDirectoryIfNonEmpty = true;
                                        }
                                    } else {
                                        addDirectoryIfNonEmpty = false;
                                    }
                                    scanDirectory(dir, newMap);
                                    this._files.putAll(newMap);
                                    boolean filesInclusive = (flags & 8) == 0 && ((flags & 32) != 0 || isRoot);
                                    for (MediaFile file2 : newMap.values()) {
                                        switch (file2.state) {
                                            case 16:
                                                if ((flags & 1) == 0) {
                                                    break;
                                                } else {
                                                    if (addDirectoryIfNonEmpty) {
                                                        list.add(dir);
                                                        addDirectoryIfNonEmpty = false;
                                                    }
                                                    if (filesInclusive) {
                                                        list.add(file2);
                                                        break;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                            case 17:
                                                if ((flags & 4) == 0) {
                                                    break;
                                                } else {
                                                    if (addDirectoryIfNonEmpty) {
                                                        list.add(dir);
                                                        addDirectoryIfNonEmpty = false;
                                                    }
                                                    if (filesInclusive) {
                                                        list.add(file2);
                                                        break;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                            case 18:
                                                if ((flags & 2) == 0) {
                                                    break;
                                                } else {
                                                    if (addDirectoryIfNonEmpty) {
                                                        list.add(dir);
                                                        addDirectoryIfNonEmpty = false;
                                                    }
                                                    if (filesInclusive) {
                                                        list.add(file2);
                                                        break;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                        }
                                    }
                                } else {
                                    dir.state = 33;
                                }
                            }
                        }
                    }
                    result = (MediaFile[]) list.toArray(new MediaFile[list.size()]);
                    if ((flags & 256) != 0 && rescans != null) {
                        Arrays.sort(result, MediaFile.CASE_INSENSITIVE_FILE_FIRST_ORDER);
                    }
                    return result;
                }
                file = entry.getValue();
                switch (file.state) {
                    case 16:
                        if ((flags & 1) == 0) {
                            break;
                        } else {
                            if (lastDirectory != null) {
                                list.add(lastDirectory);
                                lastDirectory = null;
                            }
                            if ((flags & 8) == 0) {
                                list.add(file);
                                break;
                            } else {
                                break;
                            }
                        }
                    case 17:
                        if ((flags & 4) == 0) {
                            break;
                        } else {
                            if (lastDirectory != null) {
                                list.add(lastDirectory);
                                lastDirectory = null;
                            }
                            if ((flags & 8) == 0) {
                                list.add(file);
                                break;
                            } else {
                                break;
                            }
                        }
                    case 18:
                        if ((flags & 2) == 0) {
                            break;
                        } else {
                            if (lastDirectory != null) {
                                list.add(lastDirectory);
                                lastDirectory = null;
                            }
                            if ((flags & 8) == 0) {
                                list.add(file);
                                break;
                            } else {
                                break;
                            }
                        }
                    case 32:
                        lastDirectory = null;
                        if (rescans == null) {
                            rescans = new ArrayList<>();
                        }
                        rescans.add(file);
                        break;
                    case 33:
                        lastDirectory = null;
                        break;
                    case 34:
                        lastDirectory = null;
                        if ((flags & 16) != 0 && ((flags & 128) != 0 || path.length() > rootLen)) {
                            if ((flags & 64) != 0) {
                                list.add(file);
                                break;
                            } else {
                                lastDirectory = file;
                                break;
                            }
                        }
                        break;
                }
            } else {
                if (!FileUtils.isDirectAscendantOf(path, root2) && !path.equalsIgnoreCase(root2)) {
                    if (rescans != null) {
                    }
                    result = (MediaFile[]) list.toArray(new MediaFile[list.size()]);
                    if ((flags & 256) != 0) {
                        Arrays.sort(result, MediaFile.CASE_INSENSITIVE_FILE_FIRST_ORDER);
                    }
                    return result;
                }
                file = entry.getValue();
                switch (file.state) {
                    case 16:
                        break;
                    case 17:
                        break;
                    case 18:
                        break;
                    case 32:
                        break;
                    case 33:
                        break;
                    case 34:
                        break;
                }
            }
        }
        if (rescans != null) {
        }
        result = (MediaFile[]) list.toArray(new MediaFile[list.size()]);
        if ((flags & 256) != 0) {
        }
        return result;
    }

    private MediaFile nextDirectory(Iterator<MediaFile> it) {
        while (it.hasNext()) {
            MediaFile file = it.next();
            if (file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    @Nullable
    public String findCommonRoot() {
        String root = null;
        Iterator<MediaFile> it = this._files.values().iterator();
        MediaFile dir = nextDirectory(it);
        while (dir != null && it.hasNext()) {
            MediaFile file = it.next();
            if (file.isFile()) {
                if (file.state != 16) {
                    continue;
                } else if (root == null) {
                    root = dir.path;
                } else if (FileUtils.isLocatedIn(dir.path, root)) {
                    continue;
                } else {
                    root = FileUtils.getCommonParentIgnoreCase(root, dir.path);
                    if (root == null) {
                        break;
                    }
                    dir = nextDirectory(it);
                }
            } else {
                dir = file;
            }
        }
        return root;
    }

    private void addDirectoryNoDup(String dir, SortedSet<String> allDirs, Set<String> dirs) {
        boolean contains = false;
        SortedSet<String> coll = allDirs.tailSet(dir);
        Iterator<String> it = coll.iterator();
        while (it.hasNext() && FileUtils.isLocatedIn(it.next(), dir)) {
            contains = true;
            it.remove();
        }
        if (contains) {
            dirs.add(dir);
        }
    }

    @SuppressLint({"SdCardPath"})
    public String[] getTopDirectories() {
        MediaFile[] list;
        Set<String> topDirs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        TreeSet<String> allDirs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (MediaFile file : list("/", 57)) {
            allDirs.add(file.path);
        }
        File sdcardRoot = Environment.getExternalStorageDirectory();
        File sdcardRootParent = sdcardRoot.getParentFile();
        if (sdcardRootParent != null) {
            if (sdcardRootParent.getPath().equals("/")) {
                addDirectoryNoDup(sdcardRoot.getPath(), allDirs, topDirs);
            } else {
                File[] dirs = FileUtils.listFiles(sdcardRootParent, directoryFilter);
                if (dirs != null) {
                    for (File dir : dirs) {
                        addDirectoryNoDup(dir.getPath(), allDirs, topDirs);
                    }
                } else {
                    addDirectoryNoDup(sdcardRoot.getPath(), allDirs, topDirs);
                }
            }
        }
        File emulated = new File("/storage/emulated");
        File[] dirs2 = FileUtils.listFiles(emulated, directoryFilter);
        if (dirs2 != null) {
            for (File dir2 : dirs2) {
                addDirectoryNoDup(dir2.getPath(), allDirs, topDirs);
            }
        }
        File storage = new File("/storage");
        File[] dirs3 = FileUtils.listFiles(storage, directoryFilter);
        if (dirs3 != null) {
            for (File dir3 : dirs3) {
                addDirectoryNoDup(dir3.getPath(), allDirs, topDirs);
            }
        }
        File mnt = new File("/mnt");
        File[] dirs4 = FileUtils.listFiles(mnt, directoryFilter);
        if (dirs4 != null) {
            for (File dir4 : dirs4) {
                addDirectoryNoDup(dir4.getPath(), allDirs, topDirs);
            }
        }
        for (Map.Entry<File, Boolean> entry : P.scanRoots.entrySet()) {
            if (entry.getValue().booleanValue()) {
                File root = entry.getKey();
                if (root.isDirectory()) {
                    addDirectoryNoDup(root.getPath(), allDirs, topDirs);
                }
            }
        }
        return (String[]) topDirs.toArray(new String[topDirs.size()]);
    }

    public void clear() {
        this._files.clear();
        this._dirty = true;
    }

    public boolean renew(File file, boolean scanSubNewDirectories) {
        String path = file.getPath();
        if (file.exists()) {
            if (file.isDirectory()) {
                path = makeDirectoryPath(path);
            }
            MediaFile mediaFile = this._files.get(path);
            if (mediaFile != null) {
                if (file.isFile() != mediaFile.isFile()) {
                    invalidate(mediaFile);
                    addNewDirectoryOrFile(file, scanSubNewDirectories);
                    return true;
                } else if (mediaFile.isFile()) {
                    if (MediaScanner.isPathVisible(file) && MediaScanner.isFileLocatedInScanRoots(path)) {
                        mediaFile.clearCache();
                        return true;
                    }
                    invalidate(mediaFile);
                    return true;
                } else {
                    invalidate(mediaFile);
                    return true;
                }
            }
            return addNewDirectoryOrFile(file, scanSubNewDirectories);
        }
        MediaFile mediaFile2 = this._files.get(path);
        if (mediaFile2 != null || (mediaFile2 = this._files.get(makeDirectoryPath(path))) != null) {
            invalidate(mediaFile2);
            return true;
        }
        return false;
    }

    private void addSubDirectories(MediaFile mediaDir) {
        File[] subdirs = mediaDir.listFiles(directoryFilter);
        if (subdirs != null) {
            for (File subdir : subdirs) {
                String dirPath = makeDirectoryPath(subdir.getPath());
                MediaFile subMediaDir = MediaFile.newDirectory(subdir, dirPath, 32);
                this._files.put(dirPath, subMediaDir);
                addSubDirectories(subMediaDir);
            }
        }
    }

    private boolean addNewDirectoryOrFile(File file, boolean scanSubdirectories) {
        String ext;
        int type;
        String path = file.getPath();
        if (file.isDirectory()) {
            String dirPath = makeDirectoryPath(path);
            MediaFile mediaDir = MediaFile.newDirectory(file, dirPath, 32);
            this._files.put(dirPath, mediaDir);
            if (scanSubdirectories) {
                addSubDirectories(mediaDir);
                return true;
            }
            return true;
        } else if (MediaScanner.isPathVisible(file) && MediaScanner.isFileLocatedInScanRoots(path) && (ext = FileUtils.getExtension(path)) != null) {
            if (P.isSupportedMediaExtension(ext)) {
                type = 16;
            } else if (SubtitleFactory.isSupportedExtension(ext)) {
                type = 18;
            } else if (!ImageScanner.isSupportedExtension(ext)) {
                return false;
            } else {
                type = 17;
            }
            this._files.put(path, MediaFile.newFile(file, type));
            return true;
        } else {
            return false;
        }
    }

    public static int getFormat(String path) {
        String ext = FileUtils.getExtension(path);
        if (ext != null) {
            if (P.isSupportedMediaExtension(ext)) {
                return 16;
            }
            if (SubtitleFactory.isSupportedExtension(ext)) {
                return 18;
            }
            if (ImageScanner.isSupportedExtension(ext)) {
                return 17;
            }
        }
        return -1;
    }

    private void invalidate(MediaFile file) {
        switch (file.state) {
            case 16:
            case 17:
            case 18:
                this._files.remove(file.path);
                return;
            case 33:
                file.state = 32;
                return;
            case 34:
                String path = makeDirectoryPath(file.path);
                Iterator<Map.Entry<String, MediaFile>> it = this._files.tailMap(path).entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, MediaFile> entry = it.next();
                    String subPath = entry.getKey();
                    if (StringUtils.startsWithIgnoreCase(subPath, path)) {
                        MediaFile sub = entry.getValue();
                        if (sub.isFile()) {
                            it.remove();
                        } else {
                            sub.state = 32;
                        }
                    } else {
                        return;
                    }
                }
                return;
            default:
                return;
        }
    }

    public static int compareToIgnoreCaseFileFirst(@NonNull String path1, @NonNull String path2) {
        int c1;
        int c2;
        int result;
        int len1 = path1.length();
        int len2 = path2.length();
        int end = len1 < len2 ? len1 : len2;
        for (int i = 0; i < end; i++) {
            int c12 = path1.codePointAt(i);
            int c22 = path2.codePointAt(i);
            if (c12 != c22 && (result = (c1 = StringUtils.foldCase(c12)) - (c2 = StringUtils.foldCase(c22))) != 0) {
                boolean sub1 = path1.indexOf(File.separatorChar, i) >= 0;
                boolean sub2 = path2.indexOf(File.separatorChar, i) >= 0;
                if (sub1) {
                    if (sub2) {
                        if (c1 == File.separatorChar) {
                            result = -1;
                        } else if (c2 == File.separatorChar) {
                            result = 1;
                        }
                    } else {
                        result = 1;
                    }
                } else if (sub2) {
                    result = -1;
                }
                return result;
            }
        }
        return len1 - len2;
    }
}
