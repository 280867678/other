package com.mxtech;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;
import com.mxtech.app.MXApplication;
import com.mxtech.io.DocumentTreeRegistry;
import com.mxtech.videoplayer.list.MediaListFragment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.zip.Adler32;

/* loaded from: classes.dex */
public final class FileUtils {
    private static final String TAG = "MX.FileUtils";
    private static Uri _filesUri = null;
    private static final String kDirectoryWritableTestingContent = "Testing if directory is writable. Random: 8BhIgMOe4gLWrC10SqeyjB6fQ0Vsv9wBTL3iCPLKGKWZET2R33GbN9SyClEr6H83l1ZvuJKec5H8M6NukEzGp8s7PLHNlJNEjxbH8MDyZfepXDnrbvN9IC7HQDeq85ag4RyMFVfYzPhy";
    public static final File[] EMPTY_ARRAY = new File[0];
    public static Comparator<File> CASE_INSENSITIVE_ORDER = new Comparator<File>() { // from class: com.mxtech.FileUtils.1
        @Override // java.util.Comparator
        public int compare(File left, File right) {
            return left.getPath().compareToIgnoreCase(right.getPath());
        }
    };

    public static native boolean isSymbolicLink(String str) throws IOException;

    private static native String[] listImpl(String str);

    @NonNull
    public static File changeName(File file, String newNameNoExt) {
        String newName;
        String oldName = file.getName();
        int lastDotIndex = oldName.lastIndexOf(46);
        if (lastDotIndex > 0) {
            newName = newNameNoExt + '.' + oldName.substring(lastDotIndex + 1);
        } else {
            newName = newNameNoExt;
        }
        File parent = file.getParentFile();
        if (parent != null) {
            return new File(parent, newName);
        }
        return new File('/' + newName);
    }

    public static String stripExtension(File file) {
        return stripExtension(file.getPath());
    }

    public static String stripExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(46);
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    public static String getName(String filePath) {
        int lastSeparator = filePath.lastIndexOf(File.separatorChar);
        if (lastSeparator >= 0) {
            return filePath.substring(lastSeparator + 1);
        }
        return filePath;
    }

    public static String getExtension(File file) {
        return getExtension(file.getPath());
    }

    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(46);
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return null;
    }

    /* loaded from: classes2.dex */
    public static class FileNameComparator implements Comparator<File> {
        private final Comparator<String> _nameComparator;

        public FileNameComparator() {
            this._nameComparator = null;
        }

        public FileNameComparator(Comparator<String> comparator) {
            this._nameComparator = comparator;
        }

        @Override // java.util.Comparator
        public int compare(File f1, File f2) {
            return this._nameComparator != null ? this._nameComparator.compare(f1.getName(), f2.getName()) : f1.getName().compareTo(f2.getName());
        }
    }

    /* loaded from: classes2.dex */
    public static class CaseInsensitiveFileFilter implements FileFilter {
        private final String _name;

        public CaseInsensitiveFileFilter(String name) {
            this._name = name;
        }

        @Override // java.io.FileFilter
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return false;
            }
            return this._name.equalsIgnoreCase(file.getName());
        }
    }

    public static boolean isLocatedIn(String child, String parent) {
        int parentLen;
        return child.startsWith(parent) && ((parentLen = parent.length()) == 0 || parent.charAt(parentLen + (-1)) == File.separatorChar || child.length() == parentLen || child.charAt(parentLen) == File.separatorChar);
    }

    public static boolean isLocatedIn(Collection<File> roots, String path) {
        for (File root : roots) {
            if (isLocatedIn(path, root.getPath())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLocatedIn(SortedMap<File, Boolean> roots, String filePath) {
        boolean visible = false;
        for (Map.Entry<File, Boolean> root : roots.entrySet()) {
            if (isLocatedIn(filePath, root.getKey().getPath())) {
                visible = root.getValue().booleanValue();
            }
        }
        return visible;
    }

    public static boolean containsFileInHierarchy(File dir, String fileName) {
        while (dir != null) {
            if (new File(dir, fileName).exists()) {
                return true;
            }
            dir = dir.getParentFile();
        }
        return false;
    }

    public static boolean containsFileInHierarchy(String dir, String fileName, Map<String, Boolean> cache) {
        String path;
        int len = dir.length();
        if (len > 0 && dir.charAt(len - 1) != File.separatorChar) {
            Boolean exists = cache.get(dir);
            if (exists == null) {
                exists = Boolean.valueOf(new File(dir, fileName).exists());
                cache.put(dir, exists);
            }
            if (exists.booleanValue()) {
                return true;
            }
        }
        int separatorIndex = 0;
        while (true) {
            int separatorIndex2 = dir.indexOf(File.separatorChar, separatorIndex);
            if (separatorIndex2 < 0) {
                return false;
            }
            if (separatorIndex2 == 0) {
                path = File.separator;
            } else {
                path = dir.substring(0, separatorIndex2);
            }
            Boolean exists2 = cache.get(path);
            if (exists2 == null) {
                exists2 = Boolean.valueOf(new File(path, fileName).exists());
                cache.put(path, exists2);
            }
            if (exists2.booleanValue()) {
                return true;
            }
            separatorIndex = separatorIndex2 + 1;
        }
    }

    public static boolean isAnyHidden(String path) {
        int start = 0;
        while (true) {
            int dotAt = path.indexOf(46, start);
            if (dotAt >= 0) {
                if (dotAt == 0 || path.charAt(dotAt - 1) == File.separatorChar) {
                    break;
                }
                start = dotAt + 1;
            } else {
                return false;
            }
        }
        return true;
    }

    public static File getCanonicalFile(File file) {
        if (Build.VERSION.SDK_INT > 7) {
            try {
                return file.getCanonicalFile();
            } catch (IOException e) {
                Log.e(TAG, file.getPath(), e);
                return file;
            }
        }
        return file;
    }

    public static String getCanonicalOrAbsolutePath(File file) {
        if (Build.VERSION.SDK_INT > 7) {
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                Log.e(TAG, file.getPath(), e);
            }
        }
        return file.getAbsolutePath();
    }

    public static boolean equalsIgnoreCaseOrNot(String s1, String s2) {
        return Build.VERSION.SDK_INT > 7 ? s1.equals(s2) : s1.equalsIgnoreCase(s2);
    }

    public static boolean equalsIgnoreCase(File f1, File f2) {
        if (f1 == null) {
            return f2 == null;
        } else if (f2 != null) {
            return f1.getPath().equalsIgnoreCase(f2.getPath());
        } else {
            return false;
        }
    }

    public static boolean equals(@Nullable File f1, @Nullable File f2) {
        if (f1 == null) {
            if (f2 == null) {
                return true;
            }
            return false;
        }
        return f1.equals(f2);
    }

    public static boolean equalsNoExtension(String path1, String path2, boolean ignoreCase) {
        int lastDot1 = path1.lastIndexOf(46);
        int lastDot2 = path2.lastIndexOf(46);
        if (lastDot1 != lastDot2) {
            return false;
        }
        return path1.regionMatches(ignoreCase, 0, path2, 0, lastDot1);
    }

    public static void copyFile(String from, String to) throws IOException {
        copyFile(from, to, new byte[8192]);
    }

    public static void copyFile(String from, String to, byte[] buf) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            InputStream in2 = new FileInputStream(from);
            try {
                OutputStream out2 = new FileOutputStream(to);
                try {
                    IOUtils.transferStream(in2, out2, buf);
                    if (in2 != null) {
                        in2.close();
                    }
                    if (out2 != null) {
                        out2.close();
                    }
                } catch (Throwable th) {
                    th = th;
                    out = out2;
                    in = in2;
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                in = in2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    /* loaded from: classes.dex */
    public static class Adler32Checksum {
        private final Adler32 a = new Adler32();
        private final byte[] buff = new byte[8192];

        public void reset() {
            this.a.reset();
        }

        public void update(String filePath) throws IOException {
            InputStream in = new FileInputStream(filePath);
            while (true) {
                try {
                    int bytes = in.read(this.buff);
                    if (bytes >= 0) {
                        this.a.update(this.buff, 0, bytes);
                    } else {
                        return;
                    }
                } finally {
                    in.close();
                }
            }
        }

        public long getValue() {
            return this.a.getValue();
        }

        public long get(String filePath) throws IOException {
            this.a.reset();
            update(filePath);
            return this.a.getValue();
        }
    }

    public static long adler32(String filePath) throws IOException {
        Adler32Checksum ck = new Adler32Checksum();
        ck.update(filePath);
        return ck.getValue();
    }

    private static File[] filenamesToFiles(File parent, String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        File[] result = new File[count];
        for (int i = 0; i < count; i++) {
            result[i] = new File(parent, filenames[i]);
        }
        return result;
    }

    public static String[] list(File file) {
        return MXApplication.native_initialized ? listImpl(file.getPath()) : file.list();
    }

    public static String[] list(File file, FilenameFilter filter) {
        if (MXApplication.native_initialized) {
            String[] filenames = listImpl(file.getPath());
            if (filter == null || filenames == null) {
                return filenames;
            }
            List<String> result = new ArrayList<>(filenames.length);
            for (String filename : filenames) {
                if (filter.accept(file, filename)) {
                    result.add(filename);
                }
            }
            return (String[]) result.toArray(new String[result.size()]);
        }
        return file.list(filter);
    }

    public static File[] listFiles(File file) {
        return MXApplication.native_initialized ? filenamesToFiles(file, list(file)) : file.listFiles();
    }

    public static File[] listFiles(File file, FilenameFilter filter) {
        return MXApplication.native_initialized ? filenamesToFiles(file, list(file, filter)) : file.listFiles(filter);
    }

    public static File[] listFiles(File file, FileFilter filter) {
        if (MXApplication.native_initialized) {
            File[] files = listFiles(file);
            if (filter == null || files == null) {
                return files;
            }
            List<File> result = new ArrayList<>(files.length);
            for (File f : files) {
                if (filter.accept(f)) {
                    result.add(f);
                }
            }
            return (File[]) result.toArray(new File[result.size()]);
        }
        return file.listFiles(filter);
    }

    @SuppressLint({"NewApi"})
    public static boolean delete(ContentResolver cr, File file) {
        String[] files;
        DocumentFile doc;
        if (file.delete()) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 21 || (doc = DocumentTreeRegistry.fromFile(file)) == null || !doc.delete()) {
            if (Build.VERSION.SDK_INT >= 19 && file.exists()) {
                if (file.isDirectory() && ((files = list(file)) == null || files.length > 0)) {
                    return false;
                }
                if (_filesUri == null) {
                    _filesUri = MediaStore.Files.getContentUri("external");
                }
                String[] selectionArgs = {file.getPath()};
                try {
                    cr.delete(_filesUri, "_data=?", selectionArgs);
                    if (file.exists()) {
                        ContentValues values = new ContentValues();
                        values.put("_data", file.getPath());
                        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        cr.delete(_filesUri, "_data=?", selectionArgs);
                    }
                    return !file.exists();
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "", e);
                }
            }
            return false;
        }
        return true;
    }

    public static boolean renameTo(Context context, File oldPath, File newPath) {
        DocumentFile doc;
        if (oldPath.renameTo(newPath)) {
            return true;
        }
        return Build.VERSION.SDK_INT >= 21 && TextUtils.equals(oldPath.getParent(), newPath.getParent()) && (doc = DocumentTreeRegistry.fromFile(oldPath)) != null && doc.renameTo(newPath.getName());
    }

    public static String getDirectAscendantPath(String parent, String path) {
        int searchFrom;
        int parentLen = parent.length();
        if (parentLen != 0) {
            if (path.length() <= parentLen || !path.regionMatches(true, 0, parent, 0, parentLen)) {
                return null;
            }
            if (parent.charAt(parentLen - 1) == File.separatorChar) {
                searchFrom = parentLen;
            } else if (path.charAt(parentLen) != File.separatorChar) {
                return null;
            } else {
                searchFrom = parentLen + 1;
            }
            int nextSep = path.indexOf(File.separatorChar, searchFrom);
            if (nextSep >= 0) {
                return path.substring(0, nextSep);
            }
            return path;
        }
        return path;
    }

    public static boolean isDirectAscendantOf(String path, String parent) {
        int searchFrom;
        int parentLen = parent.length();
        if (parentLen == 0) {
            return true;
        }
        int pathLen = path.length();
        if (pathLen > parentLen && path.regionMatches(true, 0, parent, 0, parentLen)) {
            if (parent.charAt(parentLen - 1) == File.separatorChar) {
                searchFrom = parentLen;
            } else if (path.charAt(parentLen) != File.separatorChar) {
                return false;
            } else {
                searchFrom = parentLen + 1;
            }
            int nextSep = path.indexOf(File.separatorChar, searchFrom);
            if (nextSep < 0 || nextSep == pathLen - 1) {
                return true;
            }
        }
        return false;
    }

    public static File fromUri(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null || MediaListFragment.TYPE_FILE.equals(scheme)) {
            return new File(uri.getPath());
        }
        return null;
    }

    public static String getParentPath(String path) {
        int lastSeparator = path.lastIndexOf(File.separatorChar);
        if (lastSeparator > 0) {
            return path.substring(0, lastSeparator);
        }
        if (lastSeparator == 0) {
            return File.separator;
        }
        return null;
    }

    public static String getCommonParent(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int len = len1 < len2 ? len1 : len2;
        for (int i = 0; i < len; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                int lastSep1 = s1.lastIndexOf(File.separatorChar, i);
                int lastSep2 = s2.lastIndexOf(File.separatorChar, i);
                int lastSep = lastSep1 < lastSep2 ? lastSep1 : lastSep2;
                if (lastSep < 0) {
                    return null;
                }
                if (lastSep == 0) {
                    return File.separator;
                }
                return s1.substring(0, lastSep);
            }
        }
        return s1;
    }

    public static String getCommonParentIgnoreCase(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int len = len1 < len2 ? len1 : len2;
        for (int i = 0; i < len; i++) {
            int ch1 = Character.toLowerCase(s1.codePointAt(i));
            int ch2 = Character.toLowerCase(s2.codePointAt(i));
            if (ch1 != ch2) {
                int lastSep1 = s1.lastIndexOf(File.separatorChar, i);
                int lastSep2 = s2.lastIndexOf(File.separatorChar, i);
                int lastSep = lastSep1 < lastSep2 ? lastSep1 : lastSep2;
                if (lastSep < 0) {
                    return null;
                }
                if (lastSep == 0) {
                    return File.separator;
                }
                return s1.substring(0, lastSep);
            }
        }
        return s1;
    }

    public static boolean checkDirectoryWritable(File directory) {
        if (directory.canWrite()) {
            File file = null;
            try {
                file = File.createTempFile(".mxvp", null, directory);
                FileWriter writer = new FileWriter(file);
                writer.write(kDirectoryWritableTestingContent);
                writer.flush();
                writer.close();
                BufferedReader reader = new BufferedReader(new FileReader(file), kDirectoryWritableTestingContent.length());
                boolean equals = kDirectoryWritableTestingContent.equals(reader.readLine());
                reader.close();
                if (file != null) {
                    file.delete();
                    return equals;
                }
                return equals;
            } catch (IOException e) {
                if (file != null) {
                    file.delete();
                }
                return false;
            } catch (Throwable th) {
                if (file != null) {
                    file.delete();
                }
                throw th;
            }
        }
        return false;
    }

    public static void deleteRecursive(File file) {
        File[] listFiles;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        boolean deleted = file.delete();
        Log.d(TAG, file + " is" + (deleted ? "" : " NOT") + " deleted.");
    }
}
