package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.mxtech.FileUtils;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.os.AsyncTask2;
import com.mxtech.preference.MXPreferenceActivity;
import com.mxtech.videoplayer.ActivityThemed;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.MediaScanner;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.FileChooser;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class MediaDirectorySelector extends ActivityThemed {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.scan_root_selector);
        if (((MXApplication) getApplication()).initInteractive(this)) {
            new Selector(this, this.dialogRegistry, getWindow().getDecorView());
        }
    }

    @SuppressLint({"NewApi"})
    /* loaded from: classes.dex */
    public static class Fragment extends PreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        @SuppressLint({"NewApi"})
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.scan_root_selector, container, false);
            MXPreferenceActivity activity = (MXPreferenceActivity) getActivity();
            new Selector(activity, activity.getDialogRegistry(), view);
            return view;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Selector extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnClickListener {
        private final Activity _activity;
        private final DialogRegistry _dialogRegistry;
        private final ColorStateList _disableColor;
        private final ColorStateList _enableColor;
        private final Button _hideButton;
        private int _lastHideButtonTextId = R.string.hide;
        private final LayoutInflater _layoutInflater;
        private final ListView _listView;
        private final View _removeButton;
        private AppCompatProgressDialog _scanProgress;
        private ScanTask _scanTask;

        Selector(Activity activity, DialogRegistry dialogRegistry, View layout) {
            Drawable d;
            this._activity = activity;
            this._dialogRegistry = dialogRegistry;
            this._layoutInflater = (LayoutInflater) activity.getSystemService("layout_inflater");
            this._listView = (ListView) layout.findViewById(16908298);
            this._hideButton = (Button) layout.findViewById(R.id.hide);
            this._removeButton = layout.findViewById(R.id.remove);
            Button addButton = (Button) layout.findViewById(R.id.add);
            View resetButton = layout.findViewById(R.id.reset);
            this._hideButton.setOnClickListener(this);
            this._removeButton.setOnClickListener(this);
            addButton.setOnClickListener(this);
            resetButton.setOnClickListener(this);
            this._listView.setOnItemClickListener(this);
            if ((resetButton instanceof ImageView) && (d = ((ImageView) resetButton).getDrawable()) != null) {
                GraphicUtils.safeMutate(d).setColorFilter(addButton.getTextColors().getDefaultColor(), PorterDuff.Mode.SRC_IN);
            }
            updateButtonsState();
            TypedArray a = activity.obtainStyledAttributes(new int[]{16842806, 16843282});
            this._enableColor = a.getColorStateList(0);
            this._disableColor = a.getColorStateList(1);
            a.recycle();
            this._listView.setAdapter((ListAdapter) this);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return P.scanRoots.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = this._layoutInflater.inflate(R.layout.scan_root_selector_item, parent, false);
            }
            int i = 0;
            for (Map.Entry<File, Boolean> entry : P.scanRoots.entrySet()) {
                int i2 = i + 1;
                if (i == position) {
                    TextView tv = (TextView) v;
                    tv.setText(entry.getKey().getPath());
                    if (entry.getValue().booleanValue()) {
                        tv.setPaintFlags(tv.getPaintFlags() & (-17));
                        tv.setTextColor(this._enableColor);
                    } else {
                        tv.setPaintFlags(tv.getPaintFlags() | 16);
                        tv.setTextColor(this._disableColor);
                    }
                    return v;
                }
                i = i2;
            }
            return null;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateButtonsState();
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            boolean newVisible;
            if (!this._activity.isFinishing() && this._dialogRegistry.size() <= 0) {
                int id = v.getId();
                if (id == R.id.add) {
                    FileChooser chooser = new FileChooser(this._activity);
                    chooser.setCanceledOnTouchOutside(true);
                    chooser.setTitle(R.string.choose_video_scan_root);
                    chooser.setDirectory(Environment.getExternalStorageDirectory());
                    chooser.setButton(-1, this._activity.getString(17039370), new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.MediaDirectorySelector.Selector.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (Selector.this._activity.isFinishing()) {
                                    return;
                                }
                                if (which == -1 && Selector.this._scanProgress == null && Selector.this._scanTask == null) {
                                    FileChooser chooser2 = (FileChooser) dialog;
                                    File dir = chooser2.getCurrentDirectory();
                                    Boolean visible = P.scanRoots.get(dir);
                                    if (visible == null || !visible.booleanValue()) {
                                        Selector.this._scanProgress = new AppCompatProgressDialog(Selector.this._activity);
                                        Selector.this._scanProgress.setProgressStyle(0);
                                        Selector.this._scanProgress.setMessage(Selector.this._activity.getString(R.string.alert_scanning_folder));
                                        Selector.this._scanProgress.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.preference.MediaDirectorySelector.Selector.1.1
                                            @Override // android.content.DialogInterface.OnDismissListener
                                            public void onDismiss(DialogInterface dialog2) {
                                                Selector.this._dialogRegistry.unregister(dialog2);
                                                Selector.this._scanProgress = null;
                                                if (Selector.this._scanTask != null) {
                                                    Selector.this._scanTask.cancel();
                                                }
                                            }
                                        });
                                        Selector.this._dialogRegistry.register(Selector.this._scanProgress);
                                        Selector.this._scanProgress.show();
                                        Selector.this._scanTask = new ScanTask(Selector.this, dir);
                                        Selector.this._scanTask.executeParallel(new Void[0]);
                                    }
                                }
                            } finally {
                                dialog.dismiss();
                            }
                        }
                    });
                    chooser.setOnDismissListener(this._dialogRegistry);
                    chooser.setExtensions(new String[0]);
                    this._dialogRegistry.register(chooser);
                    chooser.show();
                } else if (id == R.id.hide) {
                    SparseBooleanArray ids = this._listView.getCheckedItemPositions();
                    int i = 0;
                    int numVisible = 0;
                    int numHidden = 0;
                    Set<Map.Entry<File, Boolean>> set = P.scanRoots.entrySet();
                    for (Map.Entry<File, Boolean> root : set) {
                        int i2 = i + 1;
                        if (ids.get(i)) {
                            if (root.getValue().booleanValue()) {
                                numVisible++;
                            } else {
                                numHidden++;
                            }
                        }
                        i = i2;
                    }
                    if (numVisible + numHidden > 0) {
                        if (numVisible == 0) {
                            newVisible = true;
                        } else {
                            newVisible = false;
                        }
                        int i3 = 0;
                        for (Map.Entry<File, Boolean> root2 : set) {
                            int i4 = i3 + 1;
                            if (ids.get(i3)) {
                                root2.setValue(Boolean.valueOf(newVisible));
                            }
                            i3 = i4;
                        }
                        setRoots(P.scanRoots);
                        return;
                    }
                    FileChooser chooser2 = new FileChooser(this._activity);
                    chooser2.setCanceledOnTouchOutside(true);
                    chooser2.setTitle(R.string.choose_folder_to_hide);
                    chooser2.setDirectory(Environment.getExternalStorageDirectory());
                    chooser2.setButton(-1, this._activity.getString(17039370), new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.MediaDirectorySelector.Selector.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            if (!Selector.this._activity.isFinishing() && which == -1) {
                                FileChooser chooser3 = (FileChooser) dialog;
                                File dir = chooser3.getCurrentDirectory();
                                Boolean visible = P.scanRoots.get(dir);
                                if (visible == null || visible.booleanValue()) {
                                    P.scanRoots.put(dir, false);
                                    Selector.this.setRoots(P.scanRoots);
                                }
                            }
                        }
                    });
                    chooser2.setOnDismissListener(this._dialogRegistry);
                    chooser2.setExtensions(new String[0]);
                    this._dialogRegistry.register(chooser2);
                    chooser2.show();
                } else if (id == R.id.remove) {
                    SparseBooleanArray ids2 = this._listView.getCheckedItemPositions();
                    int i5 = 0;
                    Iterator<Map.Entry<File, Boolean>> it = P.scanRoots.entrySet().iterator();
                    while (it.hasNext()) {
                        it.next();
                        int i6 = i5 + 1;
                        if (ids2.get(i5)) {
                            it.remove();
                            i5 = i6;
                        } else {
                            i5 = i6;
                        }
                    }
                    setRoots(P.scanRoots);
                } else if (id == R.id.reset) {
                    AlertDialog.Builder b = new AlertDialog.Builder(this._activity);
                    b.setTitle(R.string.menu_revert_to_default);
                    b.setMessage(R.string.inquire_revert_video_file_extension);
                    b.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.MediaDirectorySelector.Selector.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Selector.this.setRoots(null);
                            dialog.dismiss();
                        }
                    });
                    b.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
                    AlertDialog dlg = b.create();
                    dlg.setCanceledOnTouchOutside(true);
                    this._dialogRegistry.register(dlg);
                    dlg.setOnDismissListener(this._dialogRegistry);
                    dlg.show();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRoots(SortedMap<File, Boolean> roots) {
            P.setScanRoots(roots);
            for (int i = this._listView.getCount() - 1; i >= 0; i--) {
                this._listView.setItemChecked(i, false);
            }
            notifyDataSetChanged();
            updateButtonsState();
        }

        private void updateButtonsState() {
            SparseBooleanArray ids = this._listView.getCheckedItemPositions();
            int i = 0;
            int numHidden = 0;
            int numVisible = 0;
            for (Map.Entry<File, Boolean> root : P.scanRoots.entrySet()) {
                int i2 = i + 1;
                if (ids.get(i)) {
                    if (root.getValue().booleanValue()) {
                        numVisible++;
                    } else {
                        numHidden++;
                    }
                }
                i = i2;
            }
            int newTextId = (numVisible != 0 || numHidden <= 0) ? R.string.hide : R.string.show;
            if (newTextId != this._lastHideButtonTextId) {
                this._lastHideButtonTextId = newTextId;
                this._hideButton.setText(newTextId);
            }
            boolean enableRemove = numVisible + numHidden > 0;
            this._removeButton.setEnabled(enableRemove);
            this._removeButton.setFocusable(enableRemove);
        }
    }

    /* loaded from: classes.dex */
    private static class ScanTask extends AsyncTask2<Void, Void, Boolean> {
        private final TreeMap<File, Boolean> _newRoots = new TreeMap<>(FileUtils.CASE_INSENSITIVE_ORDER);
        private final File _root;
        private final MediaScanner _scanner;
        private final Selector _selector;

        ScanTask(Selector selector, File root) {
            this._selector = selector;
            this._root = root;
            this._newRoots.putAll(P.scanRoots);
            this._newRoots.put(root, true);
            this._scanner = new MediaScanner(P.copyVideoExts(), this._newRoots);
        }

        void cancel() {
            this._scanner.interrupt();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            long begin = SystemClock.uptimeMillis();
            try {
                this._scanner.scan(new File[]{this._root});
                try {
                    MediaDatabase mdb = MediaDatabase.getInstance();
                    try {
                        mdb.resetVideoFiles(this._scanner);
                        Log.v(App.TAG, "Folder scan completed. (" + (SystemClock.uptimeMillis() - begin) + "ms)");
                        return true;
                    } finally {
                        mdb.release();
                    }
                } catch (SQLiteException e) {
                    Log.e(App.TAG, "", e);
                    publishProgress(new Void[0]);
                    return null;
                }
            } catch (InterruptedException e2) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Void... values) {
            if (!this._selector._activity.isFinishing()) {
                DialogUtils.alert(this._selector._activity, R.string.error_database);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            this._selector._scanTask = null;
            if (this._selector._scanProgress != null) {
                this._selector._scanProgress.dismiss();
            }
            if (result != null && result.booleanValue()) {
                this._selector.setRoots(this._newRoots);
            }
        }
    }
}
