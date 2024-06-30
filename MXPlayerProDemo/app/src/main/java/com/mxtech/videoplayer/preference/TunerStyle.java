package com.mxtech.videoplayer.preference;

import afzkl.development.mColorPicker.views.ColorPanelView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import com.google.android.gms.location.places.Place;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mxtech.app.DialogRegistry;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.ScreenStyle;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.widget.ColorPickerDialog;

/* loaded from: classes2.dex */
public class TunerStyle extends AppCompatDialogPreference {
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane implements Runnable {
        private int _changesToNotify;
        private final Context _context;
        private final ColorPanelView _controlHighlightColorView;
        private final ColorPanelView _controlNormalColorView;
        private final DialogRegistry _dialogRegistry;
        private final CheckBox _frameBorderView;
        private final ColorPanelView _frameColorView;
        private final Tuner.Listener _listener;
        private final CheckBox _placeProgressBarBelowButtons;
        private final Spinner _presetSpinner;
        private final ColorPanelView _progressBarColorView;
        private final Spinner _progressBarStyleSpinner;
        private final CheckBox _putBackgroundOnScreenButtons;
        private final ScreenStyle _style;
        private final Tuner _tuner;

        public Pane(Context context, ScreenStyle style, @Nullable Tuner tuner, ViewGroup group, @Nullable Tuner.Listener listener, DialogRegistry dialogRegistry) {
            this._context = context;
            this._tuner = tuner;
            this._listener = listener;
            this._dialogRegistry = dialogRegistry;
            this._style = style;
            this._presetSpinner = (Spinner) group.findViewById(R.id.preset);
            this._presetSpinner.setSelection(this._style.getPreset());
            this._presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.1
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Pane.this.dirty || position != Pane.this._style.getPreset()) {
                        Pane.this.dirty = true;
                        Pane.this._style.setPreset(position);
                        Pane.this._style.reset();
                        Pane.this._frameColorView.setColor(Pane.this._style.defaultFrameColor);
                        Pane.this._frameBorderView.setChecked(Pane.this._style.defaultFrameBorder);
                        Pane.this._progressBarStyleSpinner.setSelection(Pane.this._style.defaultProgressBarStyle);
                        Pane.this._progressBarColorView.setColor(Pane.this._style.defaultProgressBarColor);
                        Pane.this._placeProgressBarBelowButtons.setChecked(Pane.this._style.defaultProgressBarPlacement == 1);
                        Pane.this._controlNormalColorView.setColor(Pane.this._style.defaultControlColorNormal);
                        Pane.this._controlHighlightColorView.setColor(Pane.this._style.defaultControlColorHighlight);
                        Pane.this._putBackgroundOnScreenButtons.setChecked(Pane.this._style.defaultOnScreenButtonBackground == 1);
                        Pane.this.notifyChanges(Place.TYPE_SUBLOCALITY);
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            this._frameColorView = (ColorPanelView) group.findViewById(R.id.frame_color);
            this._frameColorView.setColor(this._style.getFrameColor());
            this._frameColorView.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, Pane.this._style.defaultFrameColor, Pane.this._frameColorView.getColor(), 1);
                        picker.setTitle(R.string.frame_color);
                        picker.setCanceledOnTouchOutside(true);
                        picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                        Pane.this._dialogRegistry.register(picker);
                        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.2.1
                            @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                            public void onColorChanged(int color) {
                                Pane.this.dirty = true;
                                Pane.this._frameColorView.setColor(color);
                                Pane.this._style.setFrameColor(color);
                                Pane.this.notifyChanges(8);
                            }
                        });
                        picker.setOnDismissListener(Pane.this._dialogRegistry);
                        picker.show();
                    }
                }
            });
            this._frameBorderView = (CheckBox) group.findViewById(R.id.frame_border);
            this._frameBorderView.setChecked(this._style.getFrameBorder());
            this._frameBorderView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    Pane.this._style.setFrameBorder(isChecked);
                    Pane.this.notifyChanges(16);
                }
            });
            this._progressBarStyleSpinner = (Spinner) group.findViewById(R.id.progress_bar_style);
            this._progressBarStyleSpinner.setSelection(this._style.getProgressBarStyle());
            this._progressBarStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.4
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Pane.this.dirty || position != Pane.this._style.getProgressBarStyle()) {
                        Pane.this.dirty = true;
                        Pane.this._style.setProgressBarStyle(position);
                        Pane.this.notifyChanges(4);
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            this._progressBarColorView = (ColorPanelView) group.findViewById(R.id.progress_bar_color);
            this._progressBarColorView.setColor(this._style.getProgressBarColor());
            this._progressBarColorView.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, Pane.this._style.defaultProgressBarColor, Pane.this._progressBarColorView.getColor(), 0);
                        picker.setTitle(R.string.progress_bar_color);
                        picker.setCanceledOnTouchOutside(true);
                        picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                        Pane.this._dialogRegistry.register(picker);
                        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.5.1
                            @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                            public void onColorChanged(int color) {
                                Pane.this.dirty = true;
                                Pane.this._progressBarColorView.setColor(color);
                                Pane.this._style.setProgressBarColor(color);
                                Pane.this.notifyChanges(32);
                            }
                        });
                        picker.setOnDismissListener(Pane.this._dialogRegistry);
                        picker.show();
                    }
                }
            });
            this._controlNormalColorView = (ColorPanelView) group.findViewById(R.id.control_normal_color);
            this._controlNormalColorView.setColor(this._style.getControlColorNormal());
            this._controlNormalColorView.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.6
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, Pane.this._style.defaultControlColorNormal, Pane.this._controlNormalColorView.getColor(), 0);
                        picker.setTitle(R.string.control_normal_color);
                        picker.setCanceledOnTouchOutside(true);
                        picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                        Pane.this._dialogRegistry.register(picker);
                        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.6.1
                            @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                            public void onColorChanged(int color) {
                                Pane.this.dirty = true;
                                Pane.this._controlNormalColorView.setColor(color);
                                Pane.this._style.setControlColorNormal(color);
                                Pane.this.notifyChanges(64);
                            }
                        });
                        picker.setOnDismissListener(Pane.this._dialogRegistry);
                        picker.show();
                    }
                }
            });
            this._controlHighlightColorView = (ColorPanelView) group.findViewById(R.id.control_highlight_color);
            this._controlHighlightColorView.setColor(this._style.getControlColorHighlight());
            this._controlHighlightColorView.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.7
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, Pane.this._style.defaultControlColorHighlight, Pane.this._controlHighlightColorView.getColor(), 1);
                        picker.setTitle(R.string.control_highlight_color);
                        picker.setCanceledOnTouchOutside(true);
                        picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                        Pane.this._dialogRegistry.register(picker);
                        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.7.1
                            @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                            public void onColorChanged(int color) {
                                Pane.this.dirty = true;
                                Pane.this._controlHighlightColorView.setColor(color);
                                Pane.this._style.setControlColorHighlight(color);
                                Pane.this.notifyChanges(128);
                            }
                        });
                        picker.setOnDismissListener(Pane.this._dialogRegistry);
                        picker.show();
                    }
                }
            });
            this._placeProgressBarBelowButtons = (CheckBox) group.findViewById(R.id.place_progress_bar_below_buttons);
            this._placeProgressBarBelowButtons.setChecked(this._style.getProgressBarPlacement() == 1);
            this._placeProgressBarBelowButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.8
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    Pane.this._style.setProgressBarPlacement(isChecked ? 1 : 0);
                    Pane.this.notifyChanges(256);
                }
            });
            this._putBackgroundOnScreenButtons = (CheckBox) group.findViewById(R.id.put_background_on_on_screen_buttons);
            this._putBackgroundOnScreenButtons.setChecked(this._style.getOnScreenButtonBackground() == 1);
            this._putBackgroundOnScreenButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerStyle.Pane.9
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    Pane.this._style.setOnScreenButtonBackground(isChecked ? 1 : 0);
                    Pane.this.notifyChanges(512);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            return new View[]{this._presetSpinner};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyChanges(int changes) {
            if (this._listener != null) {
                this._changesToNotify |= changes;
                App.handler.removeCallbacks(this);
                App.handler.post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int changes = this._changesToNotify;
            this._changesToNotify = 0;
            this._listener.onScreenStyleChanged(this._tuner, this._style, changes);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            ScreenStyle.setInstance(this._style);
            this._style.flushChanges(editor);
        }
    }

    public TunerStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TunerStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public View onCreateDialogView() {
        ViewGroup v = (ViewGroup) super.onCreateDialogView();
        this._pane = new Pane(getContext(), new ScreenStyle(), null, v, null, this.dialogRegistry);
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
}
