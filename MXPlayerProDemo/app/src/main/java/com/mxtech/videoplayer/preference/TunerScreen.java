package com.mxtech.videoplayer.preference;

import afzkl.development.mColorPicker.views.ColorPanelView;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mxtech.DeviceUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.os.Model;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.videoplayer.widget.BrightnessBar;
import com.mxtech.widget.ColorPickerDialog;

/* loaded from: classes2.dex */
public class TunerScreen extends AppCompatDialogPreference {
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane {
        private final CheckBox _autoHideSoftButtons;
        @Nullable
        private final SeekBar _brightness;
        @Nullable
        private final CheckBox _brightnessManual;
        @Nullable
        private final TextView _brightnessText;
        private final Context _context;
        private final DialogRegistry _dialogRegistry;
        private final CheckBox _elapsedTimeShowAlways;
        @Nullable
        private final Spinner _fullScreen;
        private final int _fullScreenInitialVisibility;
        @Nullable
        private final int[] _fullscreenValues;
        private final CheckBox _keepScreenOn;
        private final Tuner.Listener _listener;
        @Nullable
        private final Spinner _orientation;
        private final int[] _orientationValues;
        private final ColorPanelView _osdBackColor;
        private final CheckBox _osdBackground;
        private final CheckBox _osdBottom;
        private final ColorPanelView _osdTextColor;
        private final CheckBox _pauseIfObscured;
        private final CheckBox _screenRotationButton;
        private final CheckBox _showBatteryClockInTitleBar;
        private final CheckBox _showInterfaceAtTheStartup;
        private final CheckBox _showLeftTime;
        @Nullable
        private final Spinner _softButtons;
        private final int _softButtonsInitialVisibility;
        @Nullable
        private final int[] _softButtonsValues;
        private final CheckBox _statusBarShowAlways;
        private final CheckBox _statusTextShowAlways;
        private Toast _toast;
        private final Tuner _tuner;

        @SuppressLint({"InlinedApi"})
        public Pane(Context context, @Nullable Tuner tuner, ViewGroup group, @Nullable Tuner.Listener listener, DialogRegistry dialogRegistry) {
            this._context = context;
            this._tuner = tuner;
            this._listener = listener;
            this._dialogRegistry = dialogRegistry;
            if (DeviceUtils.isTV) {
                this._orientationValues = null;
                this._orientation = null;
                this._brightnessManual = null;
                this._brightness = null;
                this._brightnessText = null;
                group.findViewById(R.id.orientation_row).setVisibility(8);
                group.findViewById(R.id.brightness_row).setVisibility(8);
            } else {
                this._orientationValues = context.getResources().getIntArray(R.array.tune_orientation_values);
                this._orientation = (Spinner) group.findViewById(R.id.orientation);
                this._orientation.setSelection(Tuner.valueToIndex(P.screenOrientation, this._orientationValues, 0));
                this._orientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.1
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    @SuppressLint({"InlinedApi"})
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        boolean z = true;
                        int o = Pane.this._orientationValues[position];
                        if (Pane.this.dirty || o != P.screenOrientation) {
                            Pane.this.dirty = true;
                            if (Pane.this._listener != null) {
                                Pane.this._listener.onScreenOrientationChanged(Pane.this._tuner, o);
                            }
                            Pane.this._screenRotationButton.setEnabled((o == 4 || o == 10) ? false : false);
                        }
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                this._brightnessManual = (CheckBox) group.findViewById(R.id.brightnessEnable);
                this._brightness = (SeekBar) group.findViewById(R.id.brightness);
                this._brightnessText = (TextView) group.findViewById(R.id.brightnessText);
                this._brightnessManual.setChecked(!P.screenBrightnessAuto);
                this._brightnessManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.2
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton button, boolean checked) {
                        Pane.this.dirty = true;
                        if (Pane.this._listener != null) {
                            if (checked) {
                                Pane.this._listener.onScreenBrightnessChanged(Pane.this._tuner, Pane.this.getCurrentBrightness());
                            } else {
                                Pane.this._listener.onScreenBrightnessChanged(Pane.this._tuner, -1.0f);
                            }
                        }
                        if (Pane.this._toast == null) {
                            Pane.this._toast = Toast.makeText(Pane.this._context, "", 1);
                        }
                        if (!checked) {
                            Pane.this._toast.setText(R.string.alert_brightness_control);
                            Pane.this._toast.show();
                        } else if (Model.manufacturer == 10040) {
                            Pane.this._toast.setText(R.string.alert_brightness_control_on);
                            Pane.this._toast.show();
                        }
                    }
                });
                int maxBrightness = BrightnessBar.getMaxBrightness(context);
                int brightnessLevel = BrightnessBar.brightToLevel(maxBrightness, P.screenBrightness);
                this._brightnessText.setMinimumWidth(TextViewUtils.getNumberBounds(this._brightnessText).width() * 2);
                this._brightnessText.setText(Integer.toString(brightnessLevel));
                this._brightness.setMax(maxBrightness);
                this._brightness.setKeyProgressIncrement(1);
                this._brightness.setProgress(brightnessLevel);
                this._brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.3
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar, int level, boolean fromUser) {
                        Pane.this._brightnessText.setText(Integer.toString(level));
                        if (fromUser) {
                            Pane.this.dirty = true;
                            Pane.this._brightnessManual.setChecked(true);
                            if (Pane.this._listener != null) {
                                Pane.this._listener.onScreenBrightnessChanged(Pane.this._tuner, Pane.this.getCurrentBrightness());
                            }
                        }
                    }
                });
            }
            if (P.oldTablet || DeviceUtils.isTV) {
                this._fullScreen = null;
                this._fullScreenInitialVisibility = 0;
                this._fullscreenValues = null;
                group.findViewById(R.id.fullscreen_row).setVisibility(8);
            } else {
                this._fullScreenInitialVisibility = P.getFullScreen();
                this._fullscreenValues = context.getResources().getIntArray(R.array.three_states);
                this._fullScreen = (Spinner) group.findViewById(R.id.fullscreen);
                this._fullScreen.setSelection(Tuner.valueToIndex(this._fullScreenInitialVisibility, this._fullscreenValues, 0));
                this._fullScreen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.4
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int visibility = Pane.this._fullscreenValues[position];
                        if (Pane.this.dirty || visibility != Pane.this._fullScreenInitialVisibility) {
                            Pane.this.dirty = true;
                            if (Pane.this._listener != null) {
                                Pane.this._listener.onSysStatusbarVisibilityChanged(Pane.this._tuner, visibility);
                            }
                        }
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            this._statusTextShowAlways = (CheckBox) group.findViewById(R.id.alwaysShowStatusText);
            this._statusTextShowAlways.setChecked(App.prefs.getBoolean(Key.STATUS_TEXT_SHOW_ALWAYS, false));
            this._statusTextShowAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.5
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onStatusTextShowAlwaysChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._statusBarShowAlways = (CheckBox) group.findViewById(R.id.alwaysShowStatusBar);
            if (P.oldTablet) {
                this._statusBarShowAlways.setChecked(App.prefs.getBoolean(Key.STATUS_BAR_SHOW_ALWAYS, false));
                this._statusBarShowAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.6
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Pane.this.dirty = true;
                    }
                });
            } else {
                this._statusBarShowAlways.setVisibility(8);
            }
            this._elapsedTimeShowAlways = (CheckBox) group.findViewById(R.id.alwaysShowElapsedTime);
            this._elapsedTimeShowAlways.setChecked(P.elapsedTimeShowAlways);
            this._elapsedTimeShowAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.7
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onElapsedTimeShowAlwaysChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._osdBottom = (CheckBox) group.findViewById(R.id.osd_bottom);
            this._osdBottom.setChecked(App.prefs.getBoolean(Key.OSD_BOTTOM, false));
            this._osdBottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.8
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onOSDPlacementChanged(Pane.this._tuner, isChecked, Pane.this._osdBackground.isChecked());
                    }
                }
            });
            this._osdBackground = (CheckBox) group.findViewById(R.id.osd_background);
            this._osdBackground.setChecked(App.prefs.getBoolean(Key.OSD_BACKGROUND, true));
            this._osdBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.9
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onOSDPlacementChanged(Pane.this._tuner, Pane.this._osdBottom.isChecked(), isChecked);
                    }
                }
            });
            this._osdTextColor = (ColorPanelView) group.findViewById(R.id.osd_text_color);
            this._osdTextColor.setColor(App.prefs.getInt(Key.OSD_TEXT_COLOR, P.DEFAULT_OSD_TEXT_COLOR));
            this._osdTextColor.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.10
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                        if (activity == null || !activity.isFinishing()) {
                            ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, P.DEFAULT_OSD_TEXT_COLOR, Pane.this._osdTextColor.getColor(), 0);
                            picker.setTitle(R.string.text_color);
                            picker.setCanceledOnTouchOutside(true);
                            picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                            Pane.this._dialogRegistry.register(picker);
                            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.10.1
                                @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                                public void onColorChanged(int color) {
                                    Pane.this.dirty = true;
                                    Pane.this._osdTextColor.setColor(color);
                                    if (Pane.this._listener != null) {
                                        Pane.this._listener.onOSDColorChanged(Pane.this._tuner, color, Pane.this._osdBackColor.getColor());
                                    }
                                }
                            });
                            picker.setOnDismissListener(Pane.this._dialogRegistry);
                            picker.show();
                        }
                    }
                }
            });
            this._osdBackColor = (ColorPanelView) group.findViewById(R.id.osd_back_color);
            this._osdBackColor.setColor(App.prefs.getInt(Key.OSD_BACK_COLOR, P.DEFAULT_OSD_BACK_COLOR));
            this._osdBackColor.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.11
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!Pane.this._dialogRegistry.containsInstanceOf(ColorPickerDialog.class)) {
                        Activity activity = AppUtils.getActivityFrom(Pane.this._context);
                        if (activity == null || !activity.isFinishing()) {
                            ColorPickerDialog picker = new ColorPickerDialog(Pane.this._context, P.DEFAULT_OSD_BACK_COLOR, Pane.this._osdBackColor.getColor(), 1);
                            picker.setTitle(R.string.background_color);
                            picker.setCanceledOnTouchOutside(true);
                            picker.setButton(-1, Pane.this._context.getString(17039370), (DialogInterface.OnClickListener) null);
                            Pane.this._dialogRegistry.register(picker);
                            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.11.1
                                @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                                public void onColorChanged(int color) {
                                    Pane.this.dirty = true;
                                    Pane.this._osdBackColor.setColor(color);
                                    if (Pane.this._listener != null) {
                                        Pane.this._listener.onOSDColorChanged(Pane.this._tuner, Pane.this._osdTextColor.getColor(), color);
                                    }
                                }
                            });
                            picker.setOnDismissListener(Pane.this._dialogRegistry);
                            picker.show();
                        }
                    }
                }
            });
            if (DeviceUtils.isTV) {
                this._screenRotationButton = null;
                group.findViewById(R.id.screen_rotation_button).setVisibility(8);
            } else {
                this._screenRotationButton = (CheckBox) group.findViewById(R.id.screen_rotation_button);
                this._screenRotationButton.setChecked(App.prefs.getBoolean(Key.SCREEN_ROTATION_BUTTON, true));
                this._screenRotationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.12
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Pane.this.dirty = true;
                        if (Pane.this._listener != null) {
                            Pane.this._listener.onShowScreenRotationButtonChanged(Pane.this._tuner, isChecked);
                        }
                    }
                });
                this._screenRotationButton.setEnabled((P.screenOrientation == 4 || P.screenOrientation == 10) ? false : true);
            }
            this._autoHideSoftButtons = (CheckBox) group.findViewById(R.id.softButtons);
            if (!P.hasHandsetSoftMenuKey) {
                this._autoHideSoftButtons.setVisibility(8);
                group.findViewById(R.id.soft_buttons_row).setVisibility(8);
                this._softButtons = null;
                this._softButtonsInitialVisibility = 0;
                this._softButtonsValues = null;
            } else if (P.uiVersion < 19) {
                this._autoHideSoftButtons.setChecked(P.softButtonsVisibility == 2);
                this._autoHideSoftButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.13
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Pane.this.dirty = true;
                    }
                });
                group.findViewById(R.id.soft_buttons_row).setVisibility(8);
                this._softButtons = null;
                this._softButtonsInitialVisibility = 0;
                this._softButtonsValues = null;
            } else {
                this._autoHideSoftButtons.setVisibility(8);
                this._softButtonsValues = context.getResources().getIntArray(R.array.three_states);
                this._softButtonsInitialVisibility = P.softButtonsVisibility;
                this._softButtons = (Spinner) group.findViewById(R.id.soft_buttons);
                this._softButtons.setSelection(Tuner.valueToIndex(this._softButtonsInitialVisibility, this._softButtonsValues, 2));
                this._softButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.14
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int visibility = Pane.this._softButtonsValues[position];
                        if (Pane.this.dirty || visibility != Pane.this._softButtonsInitialVisibility) {
                            Pane.this.dirty = true;
                            if (!P.tabletFromConfig && visibility == 1 && Pane.this._fullScreen != null && Pane.this._fullscreenValues[Pane.this._fullScreen.getSelectedItemPosition()] != 0) {
                                if (Pane.this._toast == null) {
                                    Pane.this._toast = Toast.makeText(Pane.this._context, "", 1);
                                }
                                Pane.this._toast.setText(R.string.comment_soft_buttons_hiding);
                                Pane.this._toast.show();
                            }
                            if (Pane.this._listener != null) {
                                Pane.this._listener.onSoftButtonsVisibilityChanged(Pane.this._tuner, visibility);
                            }
                        }
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            this._keepScreenOn = (CheckBox) group.findViewById(R.id.keepScreenOn);
            this._keepScreenOn.setChecked(App.prefs.getBoolean(Key.KEEP_SCREEN_ON, false));
            this._keepScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.15
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._pauseIfObscured = (CheckBox) group.findViewById(R.id.pause_if_obscured);
            this._pauseIfObscured.setChecked(App.prefs.getBoolean(Key.PAUSE_IF_OBSTRUCTED, false));
            this._pauseIfObscured.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.16
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._showInterfaceAtTheStartup = (CheckBox) group.findViewById(R.id.show_interface_at_the_startup);
            this._showInterfaceAtTheStartup.setChecked(P.showInterfaceAtTheStartup);
            this._showInterfaceAtTheStartup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.17
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._showBatteryClockInTitleBar = (CheckBox) group.findViewById(R.id.battery_clock_in_title_bar);
            this._showBatteryClockInTitleBar.setChecked(P.getBatteryClockInTitleBar());
            this._showBatteryClockInTitleBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.18
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onDisplayBatteryClockInTitleBar(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._showLeftTime = (CheckBox) group.findViewById(R.id.showLeftTime);
            this._showLeftTime.setChecked(App.prefs.getBoolean(Key.SHOW_LEFT_TIME, false));
            this._showLeftTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerScreen.Pane.19
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onShowLeftTimeChanged(Pane.this._tuner, isChecked);
                    }
                }
            });
            View up = this._orientation;
            View up2 = null;
            if (this._fullScreen != null) {
                if (up != null) {
                    this._fullScreen.setNextFocusUpId(up.getId());
                }
                up = this._fullScreen;
            }
            if (this._softButtons != null) {
                if (up != null) {
                    this._softButtons.setNextFocusUpId(up.getId());
                }
                up = this._softButtons;
            }
            if (this._brightness != null) {
                if (up != null) {
                    int upId = up.getId();
                    this._brightnessManual.setNextFocusUpId(upId);
                    this._brightness.setNextFocusUpId(upId);
                }
                up = this._brightnessManual;
                up2 = this._brightness;
            }
            if (up != null) {
                this._elapsedTimeShowAlways.setNextFocusUpId(up.getId());
            }
            this._elapsedTimeShowAlways.setNextFocusDownId(R.id.osd_text_color);
            if (up2 != null) {
                this._statusTextShowAlways.setNextFocusUpId(up2.getId());
            } else if (up != null) {
                this._statusTextShowAlways.setNextFocusUpId(up.getId());
            }
            if (this._screenRotationButton != null) {
                this._screenRotationButton.setNextFocusUpId(R.id.osd_text_color);
            } else if (this._showBatteryClockInTitleBar != null) {
                this._showBatteryClockInTitleBar.setNextFocusUpId(R.id.osd_text_color);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            return this._orientation != null ? new View[]{this._orientation} : this._fullScreen != null ? new View[]{this._fullScreen} : this._softButtons != null ? new View[]{this._softButtons} : this._brightness != null ? new View[]{this._brightnessManual, this._brightness} : new View[]{this._elapsedTimeShowAlways, this._statusTextShowAlways};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getCurrentBrightness() {
            return (float) BrightnessBar.levelToBright(this._brightness.getMax(), this._brightness.getProgress());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            P.elapsedTimeShowAlways = this._elapsedTimeShowAlways.isChecked();
            if (this._fullScreen != null) {
                editor.putInt(Key.FULLSCREEN, this._fullscreenValues[this._fullScreen.getSelectedItemPosition()]);
            }
            if (this._softButtons != null) {
                editor.putInt(Key.SOFT_BUTTONS, this._softButtonsValues[this._softButtons.getSelectedItemPosition()]);
            } else if (this._autoHideSoftButtons.getVisibility() == 0) {
                editor.putInt(Key.SOFT_BUTTONS, this._autoHideSoftButtons.isChecked() ? 2 : 0);
            }
            if (this._brightness != null) {
                P.screenBrightnessAuto = this._brightnessManual.isChecked() ? false : true;
                P.screenBrightness = getCurrentBrightness();
                editor.putBoolean(Key.SCREEN_BRIGHTNESS_AUTO, P.screenBrightnessAuto);
                editor.putFloat(Key.SCREEN_BRIGHTNESS, P.screenBrightness);
            }
            editor.putBoolean(Key.STATUS_TEXT_SHOW_ALWAYS, this._statusTextShowAlways.isChecked());
            if (this._statusBarShowAlways.getVisibility() == 0) {
                editor.putBoolean(Key.STATUS_BAR_SHOW_ALWAYS, this._statusBarShowAlways.isChecked());
            }
            editor.putBoolean(Key.ELAPSED_TIME_SHOW_ALWAYS, P.elapsedTimeShowAlways);
            if (!DeviceUtils.isTV) {
                P.screenOrientation = this._orientationValues[this._orientation.getSelectedItemPosition()];
                editor.putInt(Key.SCREEN_ORIENTATION, P.screenOrientation);
                editor.putBoolean(Key.SCREEN_ROTATION_BUTTON, this._screenRotationButton.isChecked());
            }
            editor.putBoolean(Key.KEEP_SCREEN_ON, this._keepScreenOn.isChecked());
            editor.putBoolean(Key.PAUSE_IF_OBSTRUCTED, this._pauseIfObscured.isChecked());
            editor.putBoolean(Key.SHOW_INTERFACE_AT_THE_STARTUP, this._showInterfaceAtTheStartup.isChecked());
            if (this._showBatteryClockInTitleBar != null) {
                editor.putBoolean(Key.BATTERY_CLOCK_IN_TITLE_BAR, this._showBatteryClockInTitleBar.isChecked());
            }
            editor.putBoolean(Key.SHOW_LEFT_TIME, this._showLeftTime.isChecked());
            editor.putBoolean(Key.OSD_BOTTOM, this._osdBottom.isChecked());
            editor.putBoolean(Key.OSD_BACKGROUND, this._osdBackground.isChecked());
            editor.putInt(Key.OSD_TEXT_COLOR, this._osdTextColor.getColor());
            editor.putInt(Key.OSD_BACK_COLOR, this._osdBackColor.getColor());
        }
    }

    public TunerScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TunerScreen(Context context, AttributeSet attrs) {
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
