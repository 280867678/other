package com.mxtech.videoplayer.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mxtech.os.Model;
import com.mxtech.preference.AppCompatDialogPreference;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import java.util.Locale;

/* loaded from: classes2.dex */
public class TunerNavigation extends AppCompatDialogPreference {
    private Pane _pane;

    /* loaded from: classes2.dex */
    public static class Pane extends TunerPane {
        public static final float CENTIMETER_PER_INCH = 2.54f;
        private static final int MAX_MOVE_INTERVAL = 60;
        private static final int MIN_MOVE_INTERVAL = 1;
        private final CheckBox _displaySeekingPosition;
        private final SeekBar _gestureSeekSpeed;
        private final TextView _gestureSeekSpeedText;
        private final boolean _inch;
        private final Tuner.Listener _listener;
        private final SeekBar _moveInterval;
        private final TextView _moveIntervalText;
        private final CheckBox _showMoveButtons;
        private final CheckBox _showPrevNextButtons;
        private final Tuner _tuner;

        public Pane(Context context, @Nullable Tuner tuner, ViewGroup group, @Nullable Tuner.Listener listener) {
            this._tuner = tuner;
            this._listener = listener;
            this._showMoveButtons = (CheckBox) group.findViewById(R.id.showMoveButtons);
            this._showPrevNextButtons = (CheckBox) group.findViewById(R.id.show_prev_next);
            this._displaySeekingPosition = (CheckBox) group.findViewById(R.id.display_seeking_position);
            this._moveInterval = (SeekBar) group.findViewById(R.id.moveInterval);
            this._moveIntervalText = (TextView) group.findViewById(R.id.moveIntervalText);
            int moveInterval = App.prefs.getInt(Key.NAVI_MOVE_INTERVAL, 10);
            this._moveIntervalText.setMinimumWidth(TextViewUtils.getNumberBounds(this._moveIntervalText).width() * 2);
            this._moveIntervalText.setText(Integer.toString(moveInterval));
            this._moveInterval.setMax(59);
            this._moveInterval.setKeyProgressIncrement(1);
            this._moveInterval.setProgress(moveInterval - 1);
            this._moveInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerNavigation.Pane.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Pane.this.dirty = true;
                    Pane.this._moveIntervalText.setText(Integer.toString(progress + 1));
                }
            });
            this._showMoveButtons.setChecked(P.getNaviShowMoveButtons());
            this._showMoveButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerNavigation.Pane.2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onShowMoveButtons(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._showPrevNextButtons.setChecked(App.prefs.getBoolean(Key.SHOW_PREV_NEXT, true));
            this._showPrevNextButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerNavigation.Pane.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                    if (Pane.this._listener != null) {
                        Pane.this._listener.onShowPrevNextButtons(Pane.this._tuner, isChecked);
                    }
                }
            });
            this._displaySeekingPosition.setChecked(App.prefs.getBoolean(Key.DISPLAY_SEEKING_POSITION, true));
            this._displaySeekingPosition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerNavigation.Pane.4
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Pane.this.dirty = true;
                }
            });
            this._gestureSeekSpeed = (SeekBar) group.findViewById(R.id.gesture_seek_speed);
            float speed = App.prefs.getFloat(Key.GESTURE_SEEK_SPEED, 10.0f);
            String country = Locale.getDefault().getCountry();
            TextView gestureSeekSpeedLabel = (TextView) group.findViewById(R.id.gesture_seek_speed_label);
            this._inch = "GB".equals(country) || "US".equals(country) || "CA".equals(country);
            if (this._inch) {
                gestureSeekSpeedLabel.setText(context.getString(R.string.gesture_seek_speed) + "\n(" + context.getString(R.string.second_abbr) + "/inch)");
            } else {
                gestureSeekSpeedLabel.setText(context.getString(R.string.gesture_seek_speed) + "\n(" + context.getString(R.string.second_abbr) + "/cm)");
            }
            this._gestureSeekSpeedText = (TextView) group.findViewById(R.id.gesture_seek_speed_text);
            this._gestureSeekSpeedText.setMinimumWidth(TextViewUtils.getNumberBounds(this._gestureSeekSpeedText).width() * 3);
            int progress = seekSpeedToProgress(speed);
            this._gestureSeekSpeed.setMax(seekProgressCount() - 1);
            this._gestureSeekSpeed.setKeyProgressIncrement(1);
            this._gestureSeekSpeed.setProgress(progress);
            setGestureSeekSpeedText(progress);
            this._gestureSeekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.mxtech.videoplayer.preference.TunerNavigation.Pane.5
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress2, boolean fromUser) {
                    Pane.this.setGestureSeekSpeedText(progress2);
                    if (fromUser) {
                        Pane.this.dirty = true;
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public View[] getTopmostFocusableViews() {
            return new View[]{this._gestureSeekSpeed};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGestureSeekSpeedText(int progress) {
            this._gestureSeekSpeedText.setText(Integer.toString(Math.round(seekProgressToSpeed(progress, false))));
        }

        private int seekProgressCount() {
            return 26;
        }

        private float seekProgressToSpeed(int progress, boolean normalize) {
            float speed;
            if (this._inch) {
                if (progress < 6) {
                    speed = (progress * 5) + 5;
                } else if (progress < 13) {
                    speed = ((progress - 6) * 10) + 40;
                } else if (progress < 17) {
                    speed = (((progress - 6) - 7) * 25) + 125;
                } else if (progress < 21) {
                    speed = ((((progress - 6) - 7) - 4) * 50) + 250;
                } else {
                    speed = (((((progress - 6) - 7) - 4) - 4) * 100) + 500;
                }
                if (normalize) {
                    return speed / 2.54f;
                }
                return speed;
            } else if (progress < 5) {
                float speed2 = (progress * 2) + 2;
                return speed2;
            } else if (progress < 11) {
                float speed3 = ((progress - 5) * 5) + 15;
                return speed3;
            } else if (progress < 17) {
                float speed4 = (((progress - 5) - 6) * 10) + 50;
                return speed4;
            } else if (progress < 22) {
                float speed5 = ((((progress - 5) - 6) - 6) * 20) + Model.KINDLE_FIRE;
                return speed5;
            } else {
                float speed6 = (((((progress - 5) - 6) - 6) - 5) * 50) + 250;
                return speed6;
            }
        }

        private int seekSpeedToProgress(float speed) {
            float position;
            if (this._inch) {
                float speed2 = speed * 2.54f;
                if (speed2 <= 30.0f) {
                    position = (speed2 - 5.0f) / 5.0f;
                } else if (speed2 <= 100.0f) {
                    position = 6.0f + ((speed2 - 40.0f) / 10.0f);
                } else if (speed2 <= 200.0f) {
                    position = 13.0f + ((speed2 - 125.0f) / 25.0f);
                } else if (speed2 <= 400.0f) {
                    position = 17.0f + ((speed2 - 250.0f) / 50.0f);
                } else {
                    position = 21.0f + ((speed2 - 500.0f) / 100.0f);
                }
            } else if (speed <= 10.0f) {
                position = (speed - 2.0f) / 2.0f;
            } else if (speed <= 40.0f) {
                position = 5.0f + ((speed - 15.0f) / 5.0f);
            } else if (speed <= 100.0f) {
                position = 11.0f + ((speed - 50.0f) / 10.0f);
            } else if (speed <= 200.0f) {
                position = 17.0f + ((speed - 120.0f) / 20.0f);
            } else {
                position = 22.0f + ((speed - 250.0f) / 50.0f);
            }
            return Math.round(position);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.mxtech.videoplayer.preference.TunerPane
        public void applyChanges(SharedPreferences.Editor editor) {
            editor.putBoolean(Key.NAVI_SHOW_MOVE_BUTTONS, this._showMoveButtons.isChecked());
            editor.putInt(Key.NAVI_MOVE_INTERVAL, this._moveInterval.getProgress() + 1);
            editor.putFloat(Key.GESTURE_SEEK_SPEED, seekProgressToSpeed(this._gestureSeekSpeed.getProgress(), true));
            editor.putBoolean(Key.SHOW_PREV_NEXT, this._showPrevNextButtons.isChecked());
            editor.putBoolean(Key.DISPLAY_SEEKING_POSITION, this._displaySeekingPosition.isChecked());
        }
    }

    public TunerNavigation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TunerNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.preference.AppCompatDialogPreference
    public View onCreateDialogView() {
        ViewGroup v = (ViewGroup) super.onCreateDialogView();
        this._pane = new Pane(getContext(), null, v, null);
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
