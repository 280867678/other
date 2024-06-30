package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.MXApplication;
import com.mxtech.collection.SimpleMapEntry;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.preference.MXPreferenceActivity;
import com.mxtech.videoplayer.ActivityThemed;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public final class FileExtensionSelector extends ActivityThemed {
    private Selector _selector;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.video_file);
        if (((MXApplication) getApplication()).initInteractive(this)) {
            this._selector = new Selector(this, this.dialogRegistry, getWindow().getDecorView());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (this._selector != null) {
            this._selector.onPause();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this._selector != null) {
            this._selector.onDestroy();
        }
    }

    @SuppressLint({"NewApi"})
    /* loaded from: classes.dex */
    public static class Fragment extends PreferenceFragment {
        private Selector _selector;

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        @SuppressLint({"NewApi"})
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.video_file, container, false);
            MXPreferenceActivity activity = (MXPreferenceActivity) getActivity();
            this._selector = new Selector(activity, activity.getDialogRegistry(), view);
            return view;
        }

        @Override // android.app.Fragment
        public void onPause() {
            super.onPause();
            if (this._selector != null) {
                this._selector.onPause();
            }
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            if (this._selector != null) {
                this._selector.onDestroy();
            }
        }
    }

    /* loaded from: classes.dex */
    private static class Selector extends BaseAdapter implements Comparator<Map.Entry<String, Integer>>, AdapterView.OnItemClickListener, View.OnClickListener {
        private final Activity _activity;
        private DialogInterface.OnClickListener _addListener;
        private final Button _changeButton;
        private DialogInterface.OnClickListener _changeListener;
        private final DialogRegistry _dialogRegistry;
        private boolean _dirty;
        private ArrayList<Map.Entry<String, Integer>> _exts;
        private final LayoutInflater _layoutInflater;
        private final ListView _listView;
        private boolean _promptMessage;
        private final Button _removeButton;

        Selector(Activity activity, DialogRegistry dialogRegistry, View layout) {
            Drawable d;
            this._activity = activity;
            this._dialogRegistry = dialogRegistry;
            this._layoutInflater = (LayoutInflater) activity.getSystemService("layout_inflater");
            this._listView = (ListView) layout.findViewById(16908298);
            this._removeButton = (Button) layout.findViewById(R.id.remove);
            this._changeButton = (Button) layout.findViewById(R.id.change);
            Button addButton = (Button) layout.findViewById(R.id.add);
            View resetButton = layout.findViewById(R.id.reset);
            this._removeButton.setOnClickListener(this);
            this._changeButton.setOnClickListener(this);
            addButton.setOnClickListener(this);
            resetButton.setOnClickListener(this);
            this._listView.setOnItemClickListener(this);
            this._exts = new ArrayList<>(P.getAllVideoExtensions().entrySet());
            Collections.sort(this._exts, this);
            if ((resetButton instanceof ImageView) && (d = ((ImageView) resetButton).getDrawable()) != null) {
                GraphicUtils.safeMutate(d).setColorFilter(addButton.getTextColors().getDefaultColor(), PorterDuff.Mode.SRC_IN);
            }
            updateButtonsState();
            this._listView.setAdapter((ListAdapter) this);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this._exts.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this._exts.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = this._layoutInflater.inflate(R.layout.video_file_item, parent, false);
            }
            if (position < this._exts.size()) {
                TextView extView = (TextView) v.findViewById(R.id.ext);
                TextView modeView = (TextView) v.findViewById(R.id.mode);
                Map.Entry<String, Integer> entry = this._exts.get(position);
                extView.setText(entry.getKey().toUpperCase(Locale.US));
                modeView.setText(L.getDecoderAbbrText(this._activity, (byte) entry.getValue().intValue()));
            }
            return v;
        }

        @Override // java.util.Comparator
        public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
            return left.getKey().compareToIgnoreCase(right.getKey());
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateButtonsState();
        }

        private void updateButtonsState() {
            int checkedCount = this._listView.getCheckItemIds().length;
            boolean enable = checkedCount > 0;
            this._removeButton.setEnabled(enable);
            this._removeButton.setFocusable(enable);
            this._changeButton.setEnabled(enable);
            this._changeButton.setFocusable(enable);
        }

        @Override // android.view.View.OnClickListener
        @SuppressLint({"InflateParams"})
        public void onClick(View v) {
            if (!this._activity.isFinishing() && this._dialogRegistry.size() <= 0) {
                int id = v.getId();
                if (id == R.id.add) {
                    if (this._addListener == null) {
                        this._addListener = new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.FileExtensionSelector.Selector.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                EditText edit = (EditText) ((Dialog) dialog).findViewById(16908291);
                                String text = edit.getText().toString();
                                if (text.length() != 0) {
                                    SimpleMapEntry simpleMapEntry = new SimpleMapEntry(text, 0);
                                    int index = Collections.binarySearch(Selector.this._exts, simpleMapEntry, Selector.this);
                                    if (index >= 0) {
                                        Selector.this._listView.setSelection(index);
                                        return;
                                    }
                                    Selector.this._exts.add((-index) - 1, simpleMapEntry);
                                    Selector.this.refreshList();
                                    Selector.this._listView.setSelection(Collections.binarySearch(Selector.this._exts, simpleMapEntry, Selector.this));
                                }
                            }
                        };
                    }
                    AlertDialog dlg = new AlertDialog.Builder(this._activity).setTitle(R.string.add).setMessage(R.string.prompt_video_file_extension).setPositiveButton(17039370, this._addListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                    dlg.setView(dlg.getLayoutInflater().inflate(R.layout.video_file_ext_add, (ViewGroup) null));
                    dlg.setCanceledOnTouchOutside(true);
                    this._dialogRegistry.register(dlg);
                    dlg.setOnDismissListener(this._dialogRegistry);
                    dlg.show();
                } else if (id == R.id.remove) {
                    SparseBooleanArray ids = this._listView.getCheckedItemPositions();
                    if (ids != null) {
                        for (int i = ids.size() - 1; i >= 0; i--) {
                            if (ids.valueAt(i)) {
                                this._exts.remove(ids.keyAt(i));
                            }
                        }
                        refreshList();
                    }
                } else if (id == R.id.change) {
                    if (this._changeListener == null) {
                        this._changeListener = new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.FileExtensionSelector.Selector.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                int mode = Selector.this._activity.getResources().getIntArray(P.isOMXDecoderVisible() ? R.array.decoder_choice_value : R.array.decoder_choice_value_noomx)[which];
                                SparseBooleanArray ids2 = Selector.this._listView.getCheckedItemPositions();
                                for (int i2 = ids2.size() - 1; i2 >= 0; i2--) {
                                    if (ids2.valueAt(i2)) {
                                        ((Map.Entry) Selector.this._exts.get(ids2.keyAt(i2))).setValue(Integer.valueOf(mode));
                                    }
                                }
                                Selector.this.refreshList();
                                dialog.dismiss();
                            }
                        };
                    }
                    AlertDialog.Builder b = new AlertDialog.Builder(this._activity);
                    b.setTitle(R.string.decoder_selector_title);
                    b.setSingleChoiceItems(P.isOMXDecoderVisible() ? R.array.decoder_choice : R.array.decoder_choice_noomx, -1, this._changeListener);
                    AlertDialog dlg2 = b.create();
                    dlg2.setCanceledOnTouchOutside(true);
                    this._dialogRegistry.register(dlg2);
                    dlg2.setOnDismissListener(this._dialogRegistry);
                    dlg2.show();
                } else if (id == R.id.reset) {
                    AlertDialog.Builder b2 = new AlertDialog.Builder(this._activity);
                    b2.setTitle(R.string.menu_revert_to_default);
                    b2.setMessage(R.string.inquire_revert_video_file_extension);
                    b2.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.FileExtensionSelector.Selector.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            P.resetVideoExtensionList(null);
                            Selector.this._exts = new ArrayList(P.getAllVideoExtensions().entrySet());
                            Collections.sort(Selector.this._exts, Selector.this);
                            Selector.this.refreshList();
                            Selector.this._dirty = false;
                            dialog.dismiss();
                        }
                    });
                    b2.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
                    AlertDialog dlg3 = b2.create();
                    dlg3.setCanceledOnTouchOutside(true);
                    this._dialogRegistry.register(dlg3);
                    dlg3.setOnDismissListener(this._dialogRegistry);
                    dlg3.show();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void refreshList() {
            for (int i = this._listView.getCount() - 1; i >= 0; i--) {
                this._listView.setItemChecked(i, false);
            }
            notifyDataSetChanged();
            this._dirty = true;
            this._promptMessage = true;
            updateButtonsState();
        }

        void onPause() {
            if (this._dirty) {
                HashMap<String, Integer> map = new HashMap<>(this._exts.size());
                Iterator<Map.Entry<String, Integer>> it = this._exts.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> entry = it.next();
                    map.put(entry.getKey(), entry.getValue());
                }
                this._dirty = !P.resetVideoExtensionList(map);
            }
        }

        void onDestroy() {
            if (this._promptMessage) {
                Toast.makeText(this._activity, R.string.alert_rescan, 0).show();
            }
        }
    }
}
