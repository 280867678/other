package com.mxtech.widget;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.mxtech.FileUtils;
import com.mxtech.videoplayer.pro.R;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/* loaded from: classes2.dex */
public class FileChooser extends AlertDialog implements FileFilter, AdapterView.OnItemClickListener {
    public static final int FLAG_INPUT = 1;
    public static final int FLAG_OUTPUT = 2;
    private static String TAG = "MX.FileChooser";
    private File _dir;
    private String[] _extensions;
    private OnFileClickListener _fileClickListener;
    private EditText _fileName;
    private final int _flags;
    private String[] _names;
    private TextView _pathView;
    private String[] _prefixes;
    private File _selected;

    /* loaded from: classes2.dex */
    public interface OnFileClickListener {
        void onFileClicked(FileChooser fileChooser, File file);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Adapter extends ArrayAdapter<File> {
        public Adapter(Context context, File[] files) {
            super(context, R.layout.file_chooser_row, R.id.fileName, files);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            File file = getItem(position);
            Context context = getContext();
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.file_chooser_row, null);
            }
            ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
            TextView fileNameView = (TextView) convertView.findViewById(R.id.fileName);
            if (iconView != null && fileNameView != null) {
                String fileName = file.getName();
                if (fileName.equals("..")) {
                    iconView.setImageResource(R.drawable.uponelevel);
                    fileNameView.setText(R.string.parent_folder);
                } else {
                    if (file.isDirectory()) {
                        iconView.setImageResource(R.drawable.folder);
                    } else {
                        iconView.setImageResource(R.drawable.file);
                    }
                    fileNameView.setText(fileName);
                }
            }
            return convertView;
        }
    }

    public FileChooser(Context context) {
        super(context);
        this._flags = 1;
        init(R.layout.file_chooser_input);
    }

    public FileChooser(Context context, int flags) {
        super(context);
        this._flags = flags;
        init((flags & 2) != 0 ? R.layout.file_chooser_output : R.layout.file_chooser_input);
    }

    public FileChooser(Context context, int flags, int layoutResId) {
        super(context);
        this._flags = flags;
        init(layoutResId);
    }

    private void init(int layoutResId) {
        View l = getLayoutInflater().inflate(layoutResId, (ViewGroup) null);
        ((ListView) l.findViewById(R.id.fileList)).setOnItemClickListener(this);
        this._pathView = (TextView) l.findViewById(R.id.path);
        this._fileName = (EditText) l.findViewById(R.id.file_name);
        setView(l);
    }

    public void setOnFileClickListener(OnFileClickListener listener) {
        this._fileClickListener = listener;
    }

    public File getSelectedFile() {
        return this._selected;
    }

    public File getCurrentDirectory() {
        return this._dir;
    }

    public void setDirectory(File dir) {
        if (dir.exists()) {
            this._dir = dir;
        } else {
            this._dir = Environment.getExternalStorageDirectory();
        }
    }

    public void setDirectory(String dirPath) {
        setDirectory(new File(dirPath));
    }

    public void setOutputFilename(String filename) {
        if (this._fileName == null) {
            throw new IllegalStateException("file_name view not found.");
        }
        this._fileName.setText(filename);
    }

    public String getOutputFilename() {
        if (this._fileName == null) {
            throw new IllegalStateException("file_name view not found.");
        }
        return this._fileName.getText().toString();
    }

    public void setExtensions(String[] extensions) {
        this._extensions = extensions;
    }

    public void setPrefix(String[] prefixes) {
        this._prefixes = prefixes;
    }

    public void setFilenames(String[] names) {
        this._names = names;
    }

    private File list(File dir, int direction) {
        File[] files;
        if (dir != null) {
            if (this._names != null || this._extensions != null || this._prefixes != null) {
                files = FileUtils.listFiles(dir, this);
            } else {
                files = FileUtils.listFiles(dir);
            }
            if (files == null && direction != 0) {
                if (direction < 0) {
                    File parent = dir.getParentFile();
                    if (parent != null) {
                        Log.w(TAG, "Move to parent directory '" + parent + "' as '" + dir + "' is not accessible.");
                        return list(parent, direction);
                    }
                } else {
                    File primaryDir = Environment.getExternalStorageDirectory();
                    if (primaryDir.getParent().equalsIgnoreCase(dir.getPath())) {
                        Log.w(TAG, "Move to primary external storage directory '" + primaryDir + "' as '" + dir + "' is not accessible.");
                        return list(primaryDir, direction);
                    }
                }
            }
            if (files != null) {
                Arrays.sort(files, new Comparator<File>() { // from class: com.mxtech.widget.FileChooser.1
                    @Override // java.util.Comparator
                    public int compare(File left, File right) {
                        boolean leftDir = left.isDirectory();
                        boolean rightDir = right.isDirectory();
                        if (leftDir && !rightDir) {
                            return -1;
                        }
                        if (!leftDir && rightDir) {
                            return 1;
                        }
                        return left.getName().compareToIgnoreCase(right.getName());
                    }
                });
                if (dir.getParent() != null) {
                    File[] moreFiles = new File[files.length + 1];
                    System.arraycopy(files, 0, moreFiles, 1, files.length);
                    moreFiles[0] = new File(dir, "..");
                    files = moreFiles;
                }
                Adapter adapter = new Adapter(getContext(), files);
                ((ListView) findViewById(R.id.fileList)).setAdapter((ListAdapter) adapter);
                this._pathView.setText(dir.getPath());
                this._dir = dir;
                return dir;
            }
        }
        return null;
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        if (file.isDirectory()) {
            return true;
        }
        String filename = file.getName();
        if (this._names != null) {
            for (String name : this._names) {
                if (name.equals(filename)) {
                    return true;
                }
            }
        }
        if (this._extensions != null) {
            String ext = FileUtils.getExtension(filename);
            for (String x : this._extensions) {
                if (x.equalsIgnoreCase(ext)) {
                    return true;
                }
            }
        }
        if (this._prefixes != null) {
            for (String prefix : this._prefixes) {
                if (filename.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File dir;
        int direction;
        Adapter adapter = (Adapter) parent.getAdapter();
        File file = adapter.getItem(position);
        if (file.isDirectory()) {
            if (file.getName().equals("..")) {
                dir = this._dir.getParentFile();
                direction = -1;
            } else {
                dir = file;
                direction = 1;
            }
            list(dir, direction);
            return;
        }
        this._selected = file;
        if (this._fileClickListener != null) {
            this._fileClickListener.onFileClicked(this, file);
        }
        if ((this._flags & 1) != 0) {
            dismiss();
        } else {
            this._fileName.setText(file.getName());
        }
    }

    @Override // android.app.Dialog
    protected void onStart() {
        super.onStart();
        this._selected = null;
        list(this._dir, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatDialog, android.app.Dialog
    public void onStop() {
        super.onStop();
    }
}
