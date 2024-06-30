package com.mxtech.videoplayer.preference;

import afzkl.development.mColorPicker.views.ColorPanelView;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.widget.ColorPickerDialog;

/* loaded from: classes2.dex */
public class TunerSubtitleLayout extends AppCompatDialogPreference {
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane {
        private final Spinner _alignment;
        private final ColorPanelView _bkColor;
        private final CheckBox _bkColorEnabled;
        private final SeekBar _bottomPadding;
        private final TextView _bottomPaddingText;
        private final Context _context;
        private final DialogRegistry _dialogRegistry;
        private final CheckBox _fitOverlayToVideo;
        private final Tuner.Listener _listener;
        private final Tuner _tuner;

        public Pane(Context context, @Nullable Tuner tuner, ViewGroup group, @Nullable Tuner.Listener listener, DialogRegistry dialogRegistry) {
            this._context = context;
            this._tuner = tuner;
            this._listener = listener;
            this._dialogRegistry = dialogRegistry;
            this._alignment = (Spinner) group.findViewById(R.id.subtitleAlignment);
            this._bottomPadding = (SeekBar) group.findViewById(R.id.subtitleBottomPadding);
            this._bottomPaddingText = (TextView) group.findViewById(R.id.subtitleBottomPaddingText);
            this._bkColorEnabled = (CheckBox) group.findViewById(R.id.subtitleBackground);
            this._bkColor = (ColorPanelView) group.findViewById(R.id.subtitleBackgroundColor);
            this._fitOverlayToVideo = (CheckBox) group.findViewById(R.id.fit_subtitle_overlay_to_video);
            this._alignment.setSelection(gravityToIndex(P.subtitleAlignment));
            this._alignment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.1
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int gravity = Pane.this.indexToGravity(position);
                    if (Pane.this.dirty || gravity != P.subtitleAlignment) {
                        Pane.this.dirty = true;
                        if (Pane.this._listener != null) {
                            Pane.this._listener.onSubtitleAlignmentChanged(Pane.this._tuner, gravity);
                        }
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            this._bottomPaddingText.setMinimumWidth(TextViewUtils.getNumberBounds(this._bottomPaddingText).width() * 2);
            this._bottomPaddingText.setText(Integer.toString(P.subtitleBottomPaddingDp));
            this._bottomPadding.setMax(P.MAX_BOTTOM_PADDING);
            this._bottomPadding.setKeyProgressIncrement(1);
            this._bottomPadding.setProgress(P.subtitleBottomPaddingDp);
            this._bottomPadding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.2
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
                        Pane.this._listener.onSubtitleBottomPaddingChanged(Pane.this._tuner, progress);
                    }
                    Pane.this._bottomPaddingText.setText(Integer.toString(progress));
                }
            });
            this._bkColorEnabled.setChecked(P.subtitleBkColorEnabled);
            this._bkColorEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleBkChanged(Pane.this._tuner, isChecked, Pane.this._bkColor.getColor());
                    }
                }
            });
            this._bkColor.setColor(P.subtitleBkColor);
            this._bkColor.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                        if (activity == null || !activity.isFinishing()) {
                            ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, 0, Pane.this._bkColor.getColor(), 1);
                            picker.setTitle(R.string.background_color);
                            picker.setCanceledOnTouchOutside(true);
                            picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                            Pane.this._dialogRegistry.register(picker);
                            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.4.1
                                @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                                public void onColorChanged(int color) {
                                    Pane.this.dirty = true;
                                    boolean enabled = Color.alpha(color) != 0;
                                    Pane.this._bkColorEnabled.setChecked(enabled);
                                    Pane.this._bkColor.setColor(color);
                                    if (Pane.this._listener != null) {
                                        Pane.this._listener.onSubtitleBkChanged(Pane.this._tuner, enabled, color);
                                    }
                                }
                            });
                            picker.setOnDismissListener(Pane.this._dialogRegistry);
                            picker.show();
                        }
                    }
                }
            });
            this._fitOverlayToVideo.setChecked(App.prefs.getBoolean(Key.SUBTITLE_FIT_OVERLAY_TO_VIDEO, true));
            this._fitOverlayToVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerSubtitleLayout.Pane.5
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onSubtitleOverlayFitChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            return new View[]{this._alignment};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int indexToGravity(int index) {
            switch (index) {
                case 0:
                    return 3;
                case 1:
                default:
                    return 1;
                case 2:
                    return 5;
            }
        }

        private int gravityToIndex(int gravity) {
            switch (gravity) {
                case 3:
                    return 0;
                case 4:
                default:
                    return 1;
                case 5:
                    return 2;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            P.subtitleAlignment = indexToGravity(this._alignment.getSelectedItemPosition());
            P.subtitleBottomPaddingDp = this._bottomPadding.getProgress();
            P.subtitleBkColor = this._bkColor.getColor();
            P.subtitleBkColorEnabled = Color.alpha(P.subtitleBkColor) != 0 && this._bkColorEnabled.isChecked();
            editor.putInt(Key.SUBTITLE_ALIGNMENT, P.subtitleAlignment);
            editor.putInt(Key.SUBTITLE_BOTTOM_PADDING, P.subtitleBottomPaddingDp);
            editor.putBoolean(Key.SUBTITLE_BKCOLOR_ENABLED, P.subtitleBkColorEnabled);
            editor.putInt(Key.SUBTITLE_BKCOLOR, P.subtitleBkColor);
            editor.putBoolean(Key.SUBTITLE_FIT_OVERLAY_TO_VIDEO, this._fitOverlayToVideo.isChecked());
        }
    }

    public TunerSubtitleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TunerSubtitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
}
