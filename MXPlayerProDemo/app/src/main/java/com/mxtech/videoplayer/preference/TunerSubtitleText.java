package com.mxtech.videoplayer.preference;

import afzkl.development.mColorPicker.views.ColorPanelView;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mxtech.FileUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.collection.CollUtils;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.text.TextViewUtils;
import com.mxtech.text.TypefaceSpan;
import com.mxtech.text.TypefaceUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.widget.ColorPickerDialog;
import com.mxtech.widget.FileChooser;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public final class TunerSubtitleText extends AppCompatDialogPreference {
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane {
        public static final int SCALE_UNIT = 5;
        private static final int TYPE_COMMAND_OPEN = 1;
        private static final int TYPE_SYSTEM_FONT = 2;
        private static final int TYPE_USER_FONT = 3;
        private final CheckBox _bold;
        private final ColorPanelView _borderColor;
        private final CheckBox _borderEnabled;
        private final SeekBar _borderThickness;
        private final TextView _borderThicknessText;
        private final Context _context;
        private final DialogRegistry _dialogRegistry;
        private final CheckBox _fadeOutEnabled;
        private final TypefaceLoadingThread _fontLoadingThread = new TypefaceLoadingThread();
        private final TypefaceSelector _fontSelector;
        private final Tuner.Listener _listener;
        private final SeekBar _scale;
        private final TextView _scaleText;
        private final CheckBox _shadowEnabled;
        private final CheckBox _ssaBrokenFontIgnore;
        private final CheckBox _ssaDirectRendering;
        private final CheckBox _ssaFontIgnore;
        private final ColorPanelView _textColor;
        private final SeekBar _textSize;
        private final TextView _textSizeText;
        private final Tuner _tuner;
        private final Spinner _typeface;

        public Pane(Context context, @Nullable Tuner tuner, ViewGroup group, @Nullable Tuner.Listener listener, DialogRegistry dialogRegistry) {
            this._context = context;
            this._tuner = tuner;
            this._listener = listener;
            this._dialogRegistry = dialogRegistry;
            this._typeface = (Spinner) group.findViewById(R.id.subtitleTypeface);
            this._textSize = (SeekBar) group.findViewById(R.id.subtitleTextSize);
            this._textSizeText = (TextView) group.findViewById(R.id.subtitleTextSizeText);
            this._textColor = (ColorPanelView) group.findViewById(R.id.subtitleTextColor);
            this._shadowEnabled = (CheckBox) group.findViewById(R.id.subtitleShadow);
            this._borderEnabled = (CheckBox) group.findViewById(R.id.subtitleBorder);
            this._borderColor = (ColorPanelView) group.findViewById(R.id.subtitleBorderColor);
            this._bold = (CheckBox) group.findViewById(R.id.subtitleBold);
            this._fontSelector = new TypefaceSelector(this._context, P.getFontFolder());
            this._typeface.setAdapter((SpinnerAdapter) this._fontSelector);
            this._typeface.setSelection(this._fontSelector.getCurrent());
            this._typeface.setOnItemSelectedListener(this._fontSelector);
            this._textSizeText.setMinimumWidth(TextViewUtils.getNumberBounds(this._textSizeText).width() * 2);
            int size = Math.round(P.getSubtitleTextSize());
            this._textSizeText.setText(Integer.toString(size));
            this._textSize.setMax(44);
            this._textSize.setKeyProgressIncrement(1);
            this._textSize.setProgress(size - 16);
            this._textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleTextSizeChanged(Pane.this._tuner, progress + 16);
                    }
                    Pane.this._textSizeText.setText(Integer.toString(progress + 16));
                }
            });
            this._textColor.setColor(P.subtitleTextColor);
            this._textColor.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                        if (activity == null || !activity.isFinishing()) {
                            ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, -1, Pane.this._textColor.getColor(), 0);
                            picker.setTitle(R.string.text_color);
                            picker.setCanceledOnTouchOutside(true);
                            picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                            Pane.this._dialogRegistry.register(picker);
                            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.2.1
                                @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                                public void onColorChanged(int color) {
                                    Pane.this.dirty = true;
                                    Pane.this._textColor.setColor(color);
                                    if (Pane.this._listener != null) {
                                        Pane.this._listener.onSubtitleTextColorChanged(Pane.this._tuner, color);
                                    }
                                }
                            });
                            picker.setOnDismissListener(Pane.this._dialogRegistry);
                            picker.show();
                        }
                    }
                }
            });
            this._shadowEnabled.setChecked(P.getSubtitleShadownEnabled());
            this._shadowEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleShadowChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._fadeOutEnabled = (CheckBox) group.findViewById(R.id.subtitle_fadeout);
            this._fadeOutEnabled.setChecked(P.getSubtitleFadeout());
            this._fadeOutEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.4
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._borderEnabled.setChecked(P.getSubtitleBorderEnabled());
            this._borderEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.5
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    Pane.this.notifyBorderChanged();
                }
            });
            this._borderColor.setColor(P.subtitleBorderColor);
            this._borderColor.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.6
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                        if (activity == null || !activity.isFinishing()) {
                            ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, ViewCompat.MEASURED_STATE_MASK, Pane.this._borderColor.getColor(), 0);
                            picker.setTitle(R.string.border_color);
                            picker.setCanceledOnTouchOutside(true);
                            picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                            Pane.this._dialogRegistry.register(picker);
                            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.6.1
                                @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                                public void onColorChanged(int color) {
                                    Pane.this.dirty = true;
                                    Pane.this._borderEnabled.setChecked(true);
                                    Pane.this._borderColor.setColor(color);
                                    Pane.this.notifyBorderChanged();
                                }
                            });
                            picker.setOnDismissListener(Pane.this._dialogRegistry);
                            picker.show();
                        }
                    }
                }
            });
            this._borderThickness = (SeekBar) group.findViewById(R.id.border_thickness);
            this._borderThicknessText = (TextView) group.findViewById(R.id.border_thickness_text);
            this._borderThicknessText.setMinimumWidth((TextViewUtils.getNumberBounds(this._borderThicknessText).width() * 3) + TextViewUtils.getBounds(this._borderThicknessText, "%").width());
            float thickness = App.prefs.getFloat(Key.SUBTITLE_BORDER_THICKNESS, 0.08f);
            this._borderThicknessText.setText(Integer.toString(Math.round((thickness * 100.0f) / 0.1f)) + '%');
            this._borderThickness.setMax(Math.round(25.0f));
            this._borderThickness.setKeyProgressIncrement(1);
            this._borderThickness.setProgress(Math.round((thickness - 0.05f) / 0.01f));
            this._borderThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.7
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Pane.this.dirty = true;
                    float thickness2 = 0.05f + (progress * 0.01f);
                    Pane.this._borderThicknessText.setText(Integer.toString(Math.round((100.0f * thickness2) / 0.1f)) + '%');
                    Pane.this.notifyBorderChanged();
                }
            });
            this._bold.setChecked((P.subtitleTypefaceStyle & 1) != 0);
            this._bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.8
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleTypefaceChanged(Pane.this._tuner, Pane.this._fontSelector.getCurrentItem().name, Pane.this.getCurrentTypefaceStyle());
                    }
                }
            });
            this._scale = (SeekBar) group.findViewById(R.id.subtitle_scale);
            this._scaleText = (TextView) group.findViewById(R.id.subtitle_scale_text);
            this._scaleText.setMinimumWidth((TextViewUtils.getNumberBounds(this._scaleText).width() * 3) + TextViewUtils.getBounds(this._scaleText, "%").width());
            this._scaleText.setText(Integer.toString(Math.round(P.subtitleScale * 100.0f)) + '%');
            this._scale.setMax(Math.round(70.0f));
            this._scale.setKeyProgressIncrement(1);
            this._scale.setProgress(Math.round(((P.subtitleScale - 0.5f) * 100.0f) / 5.0f));
            this._scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.9
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Pane.this.dirty = true;
                    float scale = P.canonicalizeSubtitleScale(0.5f + ((progress / 100.0f) * 5.0f));
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleScaleChanged(Pane.this._tuner, scale);
                    }
                    Pane.this._scaleText.setText(Integer.toString(Math.round(scale * 100.0f)) + '%');
                }
            });
            this._ssaDirectRendering = (CheckBox) group.findViewById(R.id.improve_ssa_rendering);
            this._ssaDirectRendering.setChecked(P.improve_ssa_rendering);
            this._ssaDirectRendering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.10
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._ssaFontIgnore = (CheckBox) group.findViewById(R.id.ignore_ssa_fonts);
            this._ssaFontIgnore.setChecked(App.prefs.getBoolean(Key.SSA_FONT_IGNORE, false));
            this._ssaFontIgnore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.11
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSSAFontIgnoreChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._ssaBrokenFontIgnore = (CheckBox) group.findViewById(R.id.ignore_broken_ssa_fonts);
            this._ssaBrokenFontIgnore.setChecked(App.prefs.getBoolean(Key.SSA_BROKEN_FONT_IGNORE, false));
            this._ssaBrokenFontIgnore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleText.Pane.12
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSSABrokenFontIgnoreChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            return new View[]{this._typeface};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getCurrentTypefaceStyle() {
            return this._bold.isChecked() ? P.subtitleTypefaceStyle | 1 : P.subtitleTypefaceStyle & (-2);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            P.subtitleFontNameOrPath = this._fontSelector.getCurrentItem().name;
            P.subtitleTypefaceStyle = getCurrentTypefaceStyle();
            P.subtitleTextColor = this._textColor.getColor();
            P.subtitleBorderColor = this._borderColor.getColor();
            editor.putString(Key.SUBTITLE_FONT_NAME, P.subtitleFontNameOrPath);
            editor.putInt(Key.SUBTITLE_FONT_STYLE, P.subtitleTypefaceStyle);
            editor.putFloat(Key.SUBTITLE_TEXT_SIZE, this._textSize.getProgress() + 16);
            editor.putInt(Key.SUBTITLE_TEXT_COLOR, P.subtitleTextColor);
            editor.putBoolean(Key.SUBTITLE_SHADOW_ENABLED, this._shadowEnabled.isChecked());
            editor.putBoolean(Key.SUBTITLE_BORDER_ENABLED, this._borderEnabled.isChecked());
            editor.putInt(Key.SUBTITLE_BORDER_COLOR, P.subtitleBorderColor);
            editor.putString(Key.FONT_FOLDER, this._fontSelector.fontDir.getPath());
            editor.putFloat(Key.SUBTITLE_BORDER_THICKNESS, 0.05f + (this._borderThickness.getProgress() * 0.01f));
            editor.putFloat(Key.SUBTITLE_SCALE, 0.5f + ((this._scale.getProgress() / 100.0f) * 5.0f));
            editor.putBoolean(Key.IMPROVE_SSA_RENDERING, this._ssaDirectRendering.isChecked());
            editor.putBoolean(Key.SSA_FONT_IGNORE, this._ssaFontIgnore.isChecked());
            editor.putBoolean(Key.SSA_BROKEN_FONT_IGNORE, this._ssaBrokenFontIgnore.isChecked());
            boolean fadeout = this._fadeOutEnabled.isChecked();
            if (P.getSubtitleFadeout() != fadeout) {
                editor.putBoolean(Key.SUBTITLE_FADEOUT, fadeout);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Entry implements Comparable<Entry> {
            final String name;
            int pendingCount;
            CharSequence stylizedText;
            String text;
            final int type;

            Entry(Context context, String name, int type) throws RuntimeException {
                this.name = name;
                this.type = type;
                if (type == 1) {
                    this.text = context.getString(R.string.font_open);
                } else if (type == 2) {
                    if (name == null) {
                        this.text = context.getString(R.string.font_default);
                    } else if (name.equals("monospace")) {
                        this.text = context.getString(R.string.font_mono);
                    } else if (name.equals("sans-serif")) {
                        this.text = context.getString(R.string.font_sanserif);
                    } else if (name.equals("serif")) {
                        this.text = context.getString(R.string.font_serif);
                    }
                } else {
                    this.text = FileUtils.getName(name);
                }
            }

            boolean isFont() {
                return this.type == 2 || this.type == 3;
            }

            void createStylizedText() {
                try {
                    Typeface typeface = TypefaceUtils.createTypeface(this.name, 0);
                    this.stylizedText = new SpannableString(this.text);
                    ((SpannableString) this.stylizedText).setSpan(new TypefaceSpan(typeface), 0, this.text.length(), 33);
                } catch (RuntimeException e) {
                    Log.e(Tuner.TAG, "Stylizing font " + this, e);
                    this.stylizedText = this.text;
                }
            }

            @Override // java.lang.Comparable
            public int compareTo(Entry entry) {
                if (this.type == 1) {
                    if (entry.type == 1) {
                        return 0;
                    }
                    return -1;
                } else if (entry.type != 1) {
                    return this.text.compareToIgnoreCase(entry.text);
                } else {
                    return 1;
                }
            }

            public String toString() {
                return this.name + " (" + this.text + ") [" + this.type + ']';
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class TypefaceLoadingThread extends HandlerThread implements Handler.Callback {
            final Handler handler;

            public TypefaceLoadingThread() {
                super("Typeface loader");
                start();
                this.handler = new Handler(getLooper(), this);
            }

            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                Entry entry = (Entry) msg.obj;
                entry.createStylizedText();
                Pane.this._fontSelector.queue(entry);
                return true;
            }

            public boolean queue(Entry entry) {
                return this.handler.sendMessage(this.handler.obtainMessage(0, entry));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class TypefaceSelector extends BaseAdapter implements AdapterView.OnItemSelectedListener, FileFilter, DialogInterface.OnClickListener, FileChooser.OnFileClickListener, Handler.Callback {
            private static final int TYPEFACE_CACHE_CAPACITY_LOWMEMORY = 16;
            private static final int TYPEFACE_CACHE_CAPACITY_PER_100MB = 100;
            private int _current;
            private final int _dropDownLayoutId;
            private ViewGroup _dropdownView;
            private LinkedList<Entry> _fontLoadPendingEntries;
            private boolean _fontLoading;
            private final LayoutInflater _layoutInflater;
            private LruCache<Entry, SpannableString> _stylizedTextCache;
            public File fontDir;
            final Handler handler = new Handler(this);
            private final ArrayList<Entry> _items = new ArrayList<>();

            TypefaceSelector(Context context, File fontDir) {
                this._layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
                this.fontDir = fontDir;
                build(P.subtitleFontNameOrPath);
                long maxMemory = Runtime.getRuntime().maxMemory();
                int capacity = (int) ((100 * maxMemory) / 104857600);
                this._stylizedTextCache = new LruCache<>(capacity < 16 ? 16 : capacity);
                if (Build.VERSION.SDK_INT < 11) {
                    TypedArray a = context.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
                    this._dropDownLayoutId = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, R.layout.select_dialog_singlechoice_material);
                    a.recycle();
                    return;
                }
                this._dropDownLayoutId = R.layout.support_simple_spinner_dropdown_item;
            }

            @Override // java.io.FileFilter
            public boolean accept(File file) {
                String ext;
                if (file.isFile() && (ext = FileUtils.getExtension(file)) != null) {
                    return "ttf".equalsIgnoreCase(ext) || "ttc".equalsIgnoreCase(ext) || "otf".equalsIgnoreCase(ext);
                }
                return false;
            }

            private void build(String currentTypeface) {
                this._items.clear();
                this._current = -1;
                this._items.add(new Entry(Pane.this._context, null, 1));
                File[] files = FileUtils.listFiles(this.fontDir, this);
                if (files != null) {
                    for (File file : files) {
                        String path = file.getPath();
                        try {
                            this._items.add(new Entry(Pane.this._context, path, 3));
                        } catch (RuntimeException e) {
                            Log.i(Tuner.TAG, path, e);
                        }
                    }
                }
                this._items.add(new Entry(Pane.this._context, null, 2));
                this._items.add(new Entry(Pane.this._context, "monospace", 2));
                this._items.add(new Entry(Pane.this._context, "serif", 2));
                this._items.add(new Entry(Pane.this._context, "sans-serif", 2));
                Collections.sort(this._items);
                int i = 0;
                Iterator<Entry> it = this._items.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Entry entry = it.next();
                    if (entry.isFont() && TextUtils.equals(currentTypeface, entry.name)) {
                        this._current = i;
                        break;
                    }
                    i++;
                }
                if (this._current < 0) {
                    int i2 = 0;
                    Iterator<Entry> it2 = this._items.iterator();
                    while (it2.hasNext()) {
                        Entry entry2 = it2.next();
                        if (entry2.type == 2 && entry2.name == null) {
                            this._current = i2;
                            return;
                        }
                        i2++;
                    }
                }
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return this._items.size();
            }

            @Override // android.widget.Adapter
            public Object getItem(int position) {
                return this._items.get(position);
            }

            @Override // android.widget.Adapter
            public long getItemId(int position) {
                return position;
            }

            private void fillView(View v, int position, boolean asyncLoadTypeface) {
                if (position < this._items.size()) {
                    Entry entry = this._items.get(position);
                    TextView tv = (TextView) v.findViewById(16908308);
                    tv.setText(getText(entry, asyncLoadTypeface));
                    v.setTag(entry);
                }
            }

            private CharSequence getText(Entry entry, boolean asyncLoadTypeface) {
                if (entry.stylizedText != null) {
                    return entry.stylizedText;
                }
                if (entry.type != 1) {
                    SpannableString s = this._stylizedTextCache.get(entry);
                    if (s == null) {
                        if (entry.pendingCount == 0) {
                            if (asyncLoadTypeface) {
                                if (!this._fontLoading) {
                                    this._fontLoading = Pane.this._fontLoadingThread.queue(entry);
                                } else {
                                    if (this._fontLoadPendingEntries == null) {
                                        this._fontLoadPendingEntries = new LinkedList<>();
                                    }
                                    this._fontLoadPendingEntries.add(entry);
                                    entry.pendingCount++;
                                }
                            } else {
                                entry.createStylizedText();
                                CharSequence stylizedText = entry.stylizedText;
                                if (entry.stylizedText instanceof SpannableString) {
                                    this._stylizedTextCache.put(entry, (SpannableString) entry.stylizedText);
                                    entry.stylizedText = null;
                                }
                                return stylizedText;
                            }
                        }
                    } else {
                        return s;
                    }
                }
                return entry.text;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void queue(Entry entry) {
                this.handler.sendMessage(this.handler.obtainMessage(0, entry));
            }

            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                Entry entry;
                this._fontLoading = false;
                Entry entry2 = (Entry) msg.obj;
                View v = this._dropdownView.findViewWithTag(msg.obj);
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(16908308);
                    tv.setText(getText(entry2, false));
                }
                if (entry2.stylizedText instanceof SpannableString) {
                    this._stylizedTextCache.put(entry2, (SpannableString) entry2.stylizedText);
                    entry2.stylizedText = null;
                }
                if (this._fontLoadPendingEntries != null) {
                    do {
                        entry = this._fontLoadPendingEntries.poll();
                        if (entry != null) {
                            entry.pendingCount--;
                        } else {
                            return true;
                        }
                    } while (this._dropdownView.findViewWithTag(entry) == null);
                    this._fontLoading = Pane.this._fontLoadingThread.queue(entry);
                    return true;
                }
                return true;
            }

            void onLowMemory() {
                if (this._stylizedTextCache.maxSize() > 16) {
                    this._stylizedTextCache = CollUtils.resizeLruCache(this._stylizedTextCache, 16);
                    Log.i(Tuner.TAG, "Reducing stylized text cache size to " + this._stylizedTextCache.maxSize());
                }
            }

            @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
            public View getDropDownView(int position, View v, ViewGroup parent) {
                this._dropdownView = parent;
                if (v == null) {
                    v = this._layoutInflater.inflate(this._dropDownLayoutId, parent, false);
                }
                fillView(v, position, true);
                return v;
            }

            @Override // android.widget.Adapter
            public View getView(int position, View v, ViewGroup parent) {
                if (v == null) {
                    v = this._layoutInflater.inflate(17367048, parent, false);
                }
                fillView(v, position, false);
                return v;
            }

            int getCurrent() {
                return this._current;
            }

            Entry getCurrentItem() {
                return this._items.get(this._current);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = this._items.get(position);
                if (entry.type != 1) {
                    changeFont(entry, position);
                    return;
                }
                try {
                    if (Pane.this._dialogRegistry.containsInstanceOf(FileChooser.class)) {
                        return;
                    }
                    Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                    if (activity == null || !activity.isFinishing()) {
                        FileChooser chooser = new FileChooser(Pane.this._context);
                        chooser.setCanceledOnTouchOutside(true);
                        chooser.setTitle(R.string.font_browse_title);
                        chooser.setExtensions(new String[]{"ttf", "ttc", "otf"});
                        chooser.setDirectory(this.fontDir.exists() ? this.fontDir : Environment.getExternalStorageDirectory());
                        chooser.setButton(-1, Pane.this._context.getString(17039370), this);
                        chooser.setButton(-2, Pane.this._context.getString(17039360), (DialogInterface.OnClickListener) null);
                        chooser.setOnDismissListener(Pane.this._dialogRegistry);
                        chooser.setOnFileClickListener(this);
                        Pane.this._dialogRegistry.register(chooser);
                        chooser.show();
                    }
                } finally {
                    Pane.this._typeface.setSelection(this._current);
                }
            }

            private void changeFont(Entry entry, int position) {
                if (this._current != position) {
                    Pane.this.dirty = true;
                    this._current = position;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleTypefaceChanged(Pane.this._tuner, entry.name, Pane.this.getCurrentTypefaceStyle());
                    }
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }

            private void changeFontFolder(File folder) {
                this.fontDir = folder;
                build(this._items.get(this._current).name);
                notifyDataSetChanged();
                Pane.this._typeface.setSelection(this._current);
                SharedPreferences.Editor editor = App.prefs.edit();
                Pane.this.applyChanges(editor);
                Pane.this.dirty = !editor.commit();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                changeFontFolder(((FileChooser) dialog).getCurrentDirectory());
            }

            @Override // com.mxtech.widget.FileChooser.OnFileClickListener
            public void onFileClicked(FileChooser chooser, File file) {
                changeFontFolder(chooser.getCurrentDirectory());
                String path = file.getPath();
                int i = 0;
                Iterator<Entry> it = this._items.iterator();
                while (it.hasNext()) {
                    Entry entry = it.next();
                    if (path.equals(entry.name)) {
                        Pane.this._typeface.setSelection(i);
                        return;
                    }
                    i++;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyBorderChanged() {
            if (this._listener != null) {
                this._listener.onSubtitleBorderChanged(this._tuner, this._borderEnabled.isChecked(), this._borderColor.getColor(), 0.05f + (this._borderThickness.getProgress() * 0.01f));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onDismiss() {
            this._fontLoadingThread.quit();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onLowMemory() {
            this._fontSelector.onLowMemory();
        }
    }

    public TunerSubtitleText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TunerSubtitleText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public View onCreateDialogView() {
        ViewGroup v = (ViewGroup) super.onCreateDialogView();
        this._pane = new Pane(getContext(), null, v, null, this.dialogRegistry);
        return v;
    }

    @Override // com.mxtech.preference.AppCompatDialogPreference, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1 && this._pane.dirty) {
            SharedPreferences.Editor editor = App.prefs.edit();
            this._pane.applyChanges(editor);
            this._pane.dirty = !editor.commit();
        }
        super.onClick(dialog, which);
    }

    @Override // com.mxtech.preference.AppCompatDialogPreference, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this._pane.onDismiss();
        super.onDismiss(dialog);
    }

    public void onLowMemory() {
        if (this._pane != null) {
            this._pane.onLowMemory();
        }
    }
}
