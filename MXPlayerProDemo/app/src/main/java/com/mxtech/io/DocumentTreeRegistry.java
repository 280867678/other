package com.mxtech.io;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import com.mxtech.app.MXApplication;
import java.io.File;
import java.util.List;

@TargetApi(21)
/* loaded from: classes.dex */
public class DocumentTreeRegistry {
    private static final String TAG = "MX.DocumentTreeRegistry";
    private static Storage _storage;
    private static List<Resolved> _trees;

    /* loaded from: classes.dex */
    public interface Storage {
        List<Resolved> loadTrees();

        void writeTree(Uri uri, String str);
    }

    /* loaded from: classes2.dex */
    public static class Resolved {
        private boolean _exist;
        final DocumentFile doc;
        String path;

        Resolved(DocumentFile doc, String path) {
            this.doc = doc;
            this.path = path;
        }

        Resolved(DocumentFile doc, String path, boolean exist) {
            this(doc, path);
            this._exist = exist;
        }

        boolean exists() {
            String realPath;
            if (!this._exist && (realPath = DocumentTreeRegistry.getPathFromDocumentFile(this.doc)) != null) {
                this.path = realPath;
                this._exist = true;
            }
            return this._exist;
        }
    }

    public static void init(Storage storage) {
        _storage = storage;
        _trees = storage.loadTrees();
    }

    public static void register(Uri treeUri) {
        Log.d(TAG, "Register " + treeUri);
        DocumentFile tree = DocumentFile.fromTreeUri(MXApplication.context, treeUri);
        String path = getPathFromDocumentFile(tree);
        if (path != null) {
            int pathLen = path.length();
            if (pathLen > 0 && path.charAt(pathLen - 1) == File.separatorChar) {
                path = path.substring(0, pathLen - 1);
            }
            _trees.add(new Resolved(tree, path, true));
            requestPersistablePermission(treeUri);
            _storage.writeTree(treeUri, path);
        }
    }

    public static Resolved newResolvedOnLoading(Uri treeUri, String path) {
        requestPersistablePermission(treeUri);
        return new Resolved(DocumentFile.fromTreeUri(MXApplication.context, treeUri), path);
    }

    private static void requestPersistablePermission(Uri treeUri) {
        ContentResolver cr = MXApplication.context.getContentResolver();
        try {
            cr.takePersistableUriPermission(treeUri, 3);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    @Nullable
    public static DocumentFile fromFile(File file) {
        return fromPath(file.getAbsolutePath());
    }

    @Nullable
    public static DocumentFile fromPath(String path) {
        DocumentFile doc;
        int pathLen = path.length();
        if (pathLen > 0 && path.charAt(pathLen - 1) == File.separatorChar) {
            pathLen--;
            path = path.substring(0, pathLen);
        }
        for (Resolved entry : _trees) {
            if (entry.exists() && (doc = fromPath(entry.doc, entry.path, path, pathLen)) != null) {
                return doc;
            }
        }
        Log.v(TAG, path + " ==> X ");
        return null;
    }

    @Nullable
    private static DocumentFile fromPath(DocumentFile tree, String treePath, String path, int pathLen) {
        int treeLen = treePath.length();
        if (path.startsWith(treePath)) {
            if (treeLen == pathLen) {
                Log.v(TAG, path + " ==> " + tree.getUri());
                return tree;
            } else if (path.charAt(treeLen) == File.separatorChar) {
                int nameStart = treeLen + 1;
                while (true) {
                    int nameEnd = path.indexOf(File.separatorChar, nameStart);
                    if (nameEnd < 0) {
                        DocumentFile doc = tree.findFile(path.substring(nameStart, pathLen));
                        if (doc != null) {
                            Log.v(TAG, path + " ==> " + doc.getUri());
                            return doc;
                        }
                    } else {
                        tree = tree.findFile(path.substring(nameStart, nameEnd));
                        if (tree == null) {
                            break;
                        }
                        nameStart = nameEnd + 1;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getPathFromDocumentFile(DocumentFile doc) {
        return getPathFromDocumentFile(doc, new StringBuilder());
    }

    @Nullable
    public static String getPathFromDocumentFile(DocumentFile doc, StringBuilder sb) {
        Uri docUri = doc.getUri();
        sb.setLength(0);
        while (doc != null) {
            String name = doc.getName();
            if (name == null) {
                Log.w(TAG, "Can't acquire name for document file " + docUri);
                return null;
            } else if (name.equals(File.separator)) {
                break;
            } else {
                sb.insert(0, name).insert(0, File.separatorChar);
                doc = doc.getParentFile();
            }
        }
        sb.insert(0, "/storage");
        Log.d(TAG, docUri + " ==> " + ((Object) sb));
        return sb.toString();
    }

    @Nullable
    public static File getFileFromDocumentFile(DocumentFile doc) {
        String path = getPathFromDocumentFile(doc, new StringBuilder());
        if (path != null) {
            return new File(path);
        }
        return null;
    }

    @Nullable
    public static File getFileFromDocumentFile(DocumentFile doc, StringBuilder sb) {
        String path = getPathFromDocumentFile(doc, sb);
        if (path != null) {
            return new File(path);
        }
        return null;
    }
}
