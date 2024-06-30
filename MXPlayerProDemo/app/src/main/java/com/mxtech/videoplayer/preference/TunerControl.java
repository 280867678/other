package com.mxtech.videoplayer.preference;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.mxtech.DeviceUtils;
import com.mxtech.ExclusiveOnCheckedChangeListener;
import com.mxtech.ViewUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.videoplayer.pro.R;

import java.text.NumberFormat;
import java.util.Locale;

/* loaded from: classes2.dex */
public class TunerControl extends AppCompatDialogPreference {
    private static final int FIVE_SECOND_STEPS = 10;
    private static final int HALF_SECOND_STEPS = 8;
    private static final int INTERFACE_AUDIO_HIDE_DELAY_STEP_HALF_SECOND = 5000;
    private static final int INTERFACE_AUDIO_HIDE_DELAY_STEP_ONE_SECOND = 10000;
    private static final int MAX_INTERFACE_AUDIO_HIDE_DELAY = 60000;
    private static final int MIN_INTERFACE_AUDIO_HIDE_DELAY = 1000;
    private static final int ONE_SECOND_STEPS = 5;
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane {
        private final DialogRegistry _dialogRegistry;
        @Nullable
        private CheckBox _gestureBrightness;
        @Nullable
        private CheckBox _gestureDoubleTapPlayPause;
        @Nullable
        private CheckBox _gestureDoubleTapZoom;
        @Nullable
        private CheckBox _gesturePan;
        @Nullable
        private CheckBox _gesturePlaybackSpeed;
        @Nullable
        private CheckBox _gestureSeek;
        @Nullable
        private CheckBox _gestureSubtitleScroll;
        @Nullable
        private CheckBox _gestureSubtitleUpdown;
        @Nullable
        private CheckBox _gestureSubtitleZoom;
        @Nullable
        private CheckBox _gestureVolume;
        @Nullable
        private CheckBox _gestureZoom;
        @Nullable
        private CheckBox _gestureZoomAndPan;
        private CheckBox _interfaceAutoHide;
        private SeekBar _interfaceAutoHideDelay;
        private TextView _interfaceAutoHideDelayText;
        @Nullable
        private Spinner _keyUpdownAction;
        @Nullable
        private final int[] _keyUpdownActionValues;
        private final Tuner.Listener _listener;
        @Nullable
        private AppCompatSpinner _lockModes;
        @Nullable
        private CheckBox _lockShowInterface;
        private final String _secAbbrev;
        private CompoundButton.OnCheckedChangeListener _simpleCheckChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Pane.this.dirty = true;
            }
        };
        @Nullable
        private Spinner _touchAction;
        @Nullable
        private final int[] _touchActionValues;
        private final Tuner _tuner;
        @Nullable
        private Spinner _wheelAction;
        @Nullable
        private final int[] _wheelActionValues;

        /* loaded from: classes2.dex */
        private class LockModesHandler implements AdapterView.OnItemSelectedListener {
            private final Context _context;
            private final int _initialMode;
            private final TunerClient _tunerClient;

            LockModesHandler(Context context, TunerClient tunerClient) {
                this._context = context;
                this._tunerClient = tunerClient;
                Resources res = context.getResources();
                String lock = res.getString(R.string.lock);
                String kidsLock = res.getString(R.string.kids_lock);
                String touchEffect = res.getString(R.string.touch_effects);
                CharSequence[] lockTexts = {lock, kidsLock, kidsLock + " (+" + touchEffect + ')'};
                this._initialMode = App.prefs.getInt(Key.LOCK_MODE, 0);
                ViewUtils.setSpinnerEntries(Pane.this._lockModes, lockTexts);
                Pane.this._lockModes.setSelection(this._initialMode);
                Pane.this._lockModes.setOnItemSelectedListener(this);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Pane.this.dirty || position != this._initialMode) {
                    Activity activity = AppUtils.getActivityFrom(this._context);
                    if (activity == null || !activity.isFinishing()) {
                        Pane.this.dirty = true;
                    }
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }

        public Pane(Context context, @Nullable Tuner tuner, ViewGroup group, TunerClient tunerClient, @Nullable Tuner.Listener listener, DialogRegistry dialogRegistry) {
            Resources res = context.getResources();
            this._tuner = tuner;
            this._listener = listener;
            this._dialogRegistry = dialogRegistry;
            this._touchAction = (Spinner) group.findViewById(R.id.touch_action);
            this._touchActionValues = res.getIntArray(R.array.tune_touch_action_option_values);
            this._touchAction.setSelection(Tuner.valueToIndex(P.playbackTouchAction, this._touchActionValues, 0));
            this._touchAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.2
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int action = Pane.this._touchActionValues[position];
                    if (Pane.this.dirty || action != P.playbackTouchAction) {
                        Pane.this.dirty = true;
                        if (Pane.this._listener != null) {
                            Pane.this._listener.onTouchActionChanged(Pane.this._tuner, Pane.this._touchActionValues[position]);
                        }
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            if (DeviceUtils.hasTouchScreen) {
                this._gestureSeek = (CheckBox) group.findViewById(R.id.video_seeking);
                this._gestureZoom = (CheckBox) group.findViewById(R.id.zoom);
                this._gesturePan = (CheckBox) group.findViewById(R.id.pan);
                this._gestureZoomAndPan = (CheckBox) group.findViewById(R.id.zoom_and_pan);
                this._gestureVolume = (CheckBox) group.findViewById(R.id.volume);
                this._gestureBrightness = (CheckBox) group.findViewById(R.id.brightness);
                this._gestureDoubleTapPlayPause = (CheckBox) group.findViewById(R.id.double_tap_play_pause);
                this._gestureDoubleTapZoom = (CheckBox) group.findViewById(R.id.double_tap_zoom);
                this._gesturePlaybackSpeed = (CheckBox) group.findViewById(R.id.playback_speed);
                this._gestureSubtitleScroll = (CheckBox) group.findViewById(R.id.subtitle_scroll);
                this._gestureSubtitleUpdown = (CheckBox) group.findViewById(R.id.subtitle_updown);
                this._gestureSubtitleZoom = (CheckBox) group.findViewById(R.id.subtitle_zoom);
                CompoundButton.OnCheckedChangeListener zoomPanCheckChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.3
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Pane.this.dirty = true;
                        Pane.this.updateZoomPanStates();
                    }
                };
                int gestures = P.getGestures();
                this._gestureZoom.setChecked((gestures & 1) != 0);
                this._gestureZoom.setOnCheckedChangeListener(zoomPanCheckChangeListener);
                this._gesturePan.setChecked((gestures & 2) != 0);
                this._gesturePan.setOnCheckedChangeListener(zoomPanCheckChangeListener);
                this._gestureZoomAndPan.setChecked((gestures & 4) != 0);
                this._gestureZoomAndPan.setOnCheckedChangeListener(zoomPanCheckChangeListener);
                updateZoomPanStates();
                this._gestureSeek.setChecked((gestures & 8) != 0);
                this._gestureSeek.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                if (DeviceUtils.isTV) {
                    this._gestureVolume.setEnabled(false);
                    this._gestureVolume.setFocusable(false);
                } else {
                    this._gestureVolume.setChecked((gestures & 16) != 0);
                    this._gestureVolume.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                }
                this._gestureBrightness.setChecked((gestures & 32) != 0);
                this._gestureBrightness.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                this._gesturePlaybackSpeed.setChecked((gestures & 2048) != 0);
                this._gesturePlaybackSpeed.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                this._gestureSubtitleScroll.setChecked((gestures & 128) != 0);
                this._gestureSubtitleScroll.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                this._gestureSubtitleUpdown.setChecked((gestures & 512) != 0);
                this._gestureSubtitleUpdown.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                this._gestureSubtitleZoom.setChecked((gestures & 256) != 0);
                this._gestureSubtitleZoom.setOnCheckedChangeListener(this._simpleCheckChangeListener);
                ExclusiveOnCheckedChangeListener _doubleTapOnCheckedChangeListener = new ExclusiveOnCheckedChangeListener(this._gestureDoubleTapPlayPause, this._gestureDoubleTapZoom) { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.4
                    @Override // com.mxtech.ExclusiveOnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        super.onCheckedChanged(buttonView, isChecked);
                        Pane.this.dirty = true;
                    }
                };
                String doubleTapText = res.getString(R.string.double_tap);
                this._gestureDoubleTapPlayPause.setChecked((gestures & 64) != 0);
                this._gestureDoubleTapPlayPause.setOnCheckedChangeListener(_doubleTapOnCheckedChangeListener);
                this._gestureDoubleTapPlayPause.setText(doubleTapText + " (" + (res.getString(R.string.play) + '/' + res.getString(R.string.pause)).toLowerCase(Locale.getDefault()) + ')');
                this._gestureDoubleTapZoom.setChecked((gestures & 1024) != 0);
                this._gestureDoubleTapZoom.setOnCheckedChangeListener(_doubleTapOnCheckedChangeListener);
                this._gestureDoubleTapZoom.setText(doubleTapText + " (" + res.getString(R.string.zoom_short).toLowerCase(Locale.getDefault()) + ')');
            } else {
                group.findViewById(R.id.gestures_text).setVisibility(8);
                group.findViewById(R.id.gestures_group).setVisibility(8);
            }
            this._lockModes = (AppCompatSpinner) group.findViewById(R.id.lock_mode);
            new LockModesHandler(context, tunerClient);
            Configuration config = res.getConfiguration();
            if (config.navigation == 2 || config.keyboard != 1) {
                this._keyUpdownAction = (Spinner) group.findViewById(R.id.key_updown_action);
                this._keyUpdownActionValues = res.getIntArray(R.array.key_updown_action_values);
                this._keyUpdownAction.setSelection(Tuner.valueToIndex(P.playbackKeyUpdownAction, this._keyUpdownActionValues, 1));
                this._keyUpdownAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.5
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int action = Pane.this._keyUpdownActionValues[position];
                        if (Pane.this.dirty || action != P.playbackKeyUpdownAction) {
                            Pane.this.dirty = true;
                        }
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                group.findViewById(R.id.keyboard_action_row).setVisibility(8);
                this._keyUpdownActionValues = null;
            }
            if (P.hasWheelHeuristic) {
                this._wheelAction = (Spinner) group.findViewById(R.id.wheel_action);
                this._wheelActionValues = res.getIntArray(R.array.tune_wheel_action_values);
                this._wheelAction.setSelection(Tuner.valueToIndex(P.playbackWheelAction, this._wheelActionValues, 0));
                this._wheelAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.6
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int action = Pane.this._wheelActionValues[position];
                        if (Pane.this.dirty || action != P.playbackWheelAction) {
                            Pane.this.dirty = true;
                        }
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                group.findViewById(R.id.wheel_action_row).setVisibility(8);
                this._wheelActionValues = null;
            }
            this._lockShowInterface = (CheckBox) group.findViewById(R.id.lock_show_interface);
            this._lockShowInterface.setChecked(App.prefs.getBoolean(Key.LOCK_SHOW_INTERFACE, false));
            this._lockShowInterface.setOnCheckedChangeListener(this._simpleCheckChangeListener);
            L.sb.setLength(0);
            String secAbbreNoSpace = context.getString(R.string.second_abbr);
            this._secAbbrev = L.sb.append(' ').append(secAbbreNoSpace).toString();
            this._interfaceAutoHide = (CheckBox) group.findViewById(R.id.interface_auto_hide);
            this._interfaceAutoHide.setChecked(P.interfaceAutoHide);
            this._interfaceAutoHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.7
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    Pane.this.updateInterfaceAutoHideDelayText();
                }
            });
            this._interfaceAutoHideDelay = (SeekBar) group.findViewById(R.id.interface_auto_hide_delay);
            int delay = App.prefs.getInt(Key.INTERFACE_AUTO_HIDE_DELAY, 2000);
            this._interfaceAutoHideDelay.setMax(getInterfaceAutoHideDelayStepCount());
            this._interfaceAutoHideDelay.setKeyProgressIncrement(1);
            this._interfaceAutoHideDelay.setProgress(getInterfaceAutoHideDelayStep(delay));
            this._interfaceAutoHideDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerControl.Pane.8
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Pane.this.dirty = true;
                    Pane.this._interfaceAutoHide.setChecked(true);
                    Pane.this.updateInterfaceAutoHideDelayText();
                }
            });
            this._interfaceAutoHideDelayText = (TextView) group.findViewById(R.id.interface_auto_hide_delay_text);
            this._interfaceAutoHideDelayText.setMinimumWidth((TextViewUtils.getNumberBounds(this._interfaceAutoHideDelayText).width() * 4) + TextViewUtils.getBounds(this._interfaceAutoHideDelayText, secAbbreNoSpace).width());
            updateInterfaceAutoHideDelayText();
        }

        private int getInterfaceAutoHideDelayStepCount() {
            return 23;
        }

        private int getInterfaceAutoHideDelayStep(int delayMs) {
            if (delayMs <= 5000) {
                return (delayMs + IMediaPlayer.TYPE_ATTACHED_PIC) / 500;
            }
            if (delayMs <= 10000) {
                return ((delayMs + FitnessStatusCodes.SUCCESS_NO_DATA_SOURCES) / 1000) + 8;
            }
            return ((delayMs - 10000) / 5000) + 13;
        }

        private int getInterfaceAutoHideDelayTime(int step) {
            if (step <= 8) {
                return (step * 500) + 1000;
            }
            int step2 = step - 8;
            if (step2 <= 5) {
                return (step2 * 1000) + 5000;
            }
            return ((step2 - 5) * 5000) + 10000;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            View view;
            if (this._touchAction != null) {
                view = this._touchAction;
            } else if (this._keyUpdownAction != null) {
                view = this._keyUpdownAction;
            } else if (this._wheelAction != null) {
                view = this._wheelAction;
            } else {
                view = this._interfaceAutoHideDelay;
            }
            return new View[]{view};
        }

        private String formatInterfaceAutoHideDelay(int millis) {
            NumberFormat fmt = NumberFormat.getNumberInstance();
            fmt.setMinimumFractionDigits(1);
            L.sb.setLength(0);
            L.sb.append(fmt.format(millis / 1000.0d)).append(this._secAbbrev);
            return L.sb.toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateZoomPanStates() {
            if (this._gestureZoomAndPan.isChecked()) {
                this._gestureZoom.setEnabled(false);
                this._gesturePan.setEnabled(false);
                return;
            }
            this._gestureZoom.setEnabled(true);
            this._gesturePan.setEnabled(true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateInterfaceAutoHideDelayText() {
            this._interfaceAutoHideDelayText.setText(formatInterfaceAutoHideDelay(getInterfaceAutoHideDelayTime(this._interfaceAutoHideDelay.getProgress())));
            if (this._interfaceAutoHide.isChecked()) {
                this._interfaceAutoHideDelayText.setEnabled(true);
                this._interfaceAutoHideDelayText.setPaintFlags(this._interfaceAutoHideDelayText.getPaintFlags() & (-17));
                return;
            }
            this._interfaceAutoHideDelayText.setEnabled(false);
            this._interfaceAutoHideDelayText.setPaintFlags(this._interfaceAutoHideDelayText.getPaintFlags() | 16);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            if (this._touchAction != null) {
                P.playbackTouchAction = this._touchActionValues[this._touchAction.getSelectedItemPosition()];
                editor.putInt(Key.PLAYBACK_TOUCH_ACTION, P.playbackTouchAction);
            }
            if (this._keyUpdownAction != null) {
                P.playbackKeyUpdownAction = this._keyUpdownActionValues[this._keyUpdownAction.getSelectedItemPosition()];
                editor.putInt(Key.PLAYBACK_KEY_UPDOWN_ACTION, P.playbackKeyUpdownAction);
            }
            if (this._wheelAction != null) {
                P.playbackWheelAction = this._wheelActionValues[this._wheelAction.getSelectedItemPosition()];
                editor.putInt(Key.PLAYBACK_WHEEL_ACTION, P.playbackWheelAction);
            }
            if (this._lockModes != null) {
                editor.putInt(Key.LOCK_MODE, this._lockModes.getSelectedItemPosition());
            }
            if (this._gestureZoom != null) {
                int gestures = 0;
                if (this._gestureZoom.isChecked()) {
                    gestures = 0 | 1;
                }
                if (this._gesturePan.isChecked()) {
                    gestures |= 2;
                }
                if (this._gestureZoomAndPan.isChecked()) {
                    gestures |= 4;
                }
                if (this._gestureSeek.isChecked()) {
                    gestures |= 8;
                }
                if (this._gestureVolume.isChecked()) {
                    gestures |= 16;
                }
                if (this._gestureBrightness.isChecked()) {
                    gestures |= 32;
                }
                if (this._gestureDoubleTapPlayPause.isChecked()) {
                    gestures |= 64;
                } else if (this._gestureDoubleTapZoom.isChecked()) {
                    gestures |= 1024;
                }
                if (this._gesturePlaybackSpeed.isChecked()) {
                    gestures |= 2048;
                }
                if (this._gestureSubtitleScroll.isChecked()) {
                    gestures |= 128;
                }
                if (this._gestureSubtitleUpdown.isChecked()) {
                    gestures |= 512;
                }
                if (this._gestureSubtitleZoom.isChecked()) {
                    gestures |= 256;
                }
                editor.putInt(Key.GESTURES, gestures);
            }
            if (this._lockShowInterface != null) {
                editor.putBoolean(Key.LOCK_SHOW_INTERFACE, this._lockShowInterface.isChecked());
            }
            editor.putBoolean(Key.INTERFACE_AUTO_HIDE, this._interfaceAutoHide.isChecked());
            editor.putInt(Key.INTERFACE_AUTO_HIDE_DELAY, getInterfaceAutoHideDelayTime(this._interfaceAutoHideDelay.getProgress()));
        }
    }

    public TunerControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TunerControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public View onCreateDialogView() {
        ViewGroup v = (ViewGroup) super.onCreateDialogView();
        this._pane = new Pane(getContext(), null, v, null, null, this.dialogRegistry);
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
