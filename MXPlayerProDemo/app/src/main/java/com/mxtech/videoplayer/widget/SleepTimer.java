package com.mxtech.videoplayer.widget;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.videoplayer.service.PlayService;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class SleepTimer extends AlertDialog implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, Runnable {
    private static final String TAG = App.TAG + ".SleepTimer";
    @Nullable
    private ImageView _backspaceButton;
    private final View _decorView;
    private TextView _hourView;
    private TextView _minute0View;
    private TextView _minute1View;
    @Nullable
    private Button _startButton;
    public boolean finishLastMedia;
    public boolean fired;

    @SuppressLint({"InflateParams", "NewApi"})
    public SleepTimer(ActivityVPBase activity) {
        super(activity);
        this._decorView = activity.getWindow().getDecorView();
        View layout = activity.getLayoutInflater().inflate(R.layout.sleep_timer, (ViewGroup) null);
        int mins = App.prefs.getInt(Key.SLEEP_TIMER_TIME, 0) / 60;
        this._hourView = (TextView) layout.findViewById(R.id.hour);
        this._minute1View = (TextView) layout.findViewById(R.id.minute1);
        this._minute0View = (TextView) layout.findViewById(R.id.minute0);
        this._backspaceButton = (ImageView) layout.findViewById(R.id.backspace);
        setButtonOnClickListener(layout, R.id.backspace);
        setButtonOnClickListener(layout, R.id.key_0);
        setButtonOnClickListener(layout, R.id.key_1);
        setButtonOnClickListener(layout, R.id.key_2);
        setButtonOnClickListener(layout, R.id.key_3);
        setButtonOnClickListener(layout, R.id.key_4);
        setButtonOnClickListener(layout, R.id.key_5);
        setButtonOnClickListener(layout, R.id.key_6);
        setButtonOnClickListener(layout, R.id.key_7);
        setButtonOnClickListener(layout, R.id.key_8);
        setButtonOnClickListener(layout, R.id.key_9);
        setButtonOnClickListener(layout, R.id.dec);
        setButtonOnClickListener(layout, R.id.inc);
        this.finishLastMedia = App.prefs.getBoolean(Key.SLEEP_TIMER_FINISH_LAST_MEDIA, false);
        CheckBox finishLastMediaBox = (CheckBox) layout.findViewById(R.id.finish_last_media);
        finishLastMediaBox.setChecked(this.finishLastMedia);
        finishLastMediaBox.setOnCheckedChangeListener(this);
        setMins(mins);
        if (this._backspaceButton != null) {
            this._backspaceButton.setEnabled(mins > 0);
        }
        setTitle(R.string.sleep_timer);
        setView(layout);
        setButton(-1, activity.getString(R.string.start), this);
        setButton(-2, activity.getString(R.string.stop), this);
        setOnShowListener(this);
        activity.showDialog((ActivityVPBase) this);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        this._startButton = ((AlertDialog) dialog).getButton(-1);
        this._startButton.setEnabled(getMins() > 0);
    }

    private void setButtonOnClickListener(View layout, int buttonId) {
        View button = layout.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backspace) {
            backspace();
        } else if (id == R.id.key_0) {
            key(0);
        } else if (id == R.id.key_1) {
            key(1);
        } else if (id == R.id.key_2) {
            key(2);
        } else if (id == R.id.key_3) {
            key(3);
        } else if (id == R.id.key_4) {
            key(4);
        } else if (id == R.id.key_5) {
            key(5);
        } else if (id == R.id.key_6) {
            key(6);
        } else if (id == R.id.key_7) {
            key(7);
        } else if (id == R.id.key_8) {
            key(8);
        } else if (id == R.id.key_9) {
            key(9);
        } else if (id == R.id.dec) {
            changeMin(-1);
        } else if (id == R.id.inc) {
            changeMin(1);
        }
    }

    @Override // android.support.v7.app.AlertDialog, android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                key(keyCode - 7);
                return true;
            case 67:
                backspace();
                return true;
            case 69:
                changeMin(-1);
                return true;
            case 81:
                changeMin(1);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void changeMin(int delta) {
        int oldMins = getMins();
        int newMins = oldMins + delta;
        if (newMins < 0) {
            newMins = 0;
        }
        if (oldMins != newMins) {
            setMins(newMins);
            onTimeChanged();
        }
    }

    private void backspace() {
        this._minute0View.setText(this._minute1View.getText());
        this._minute1View.setText(this._hourView.getText());
        this._hourView.setText(Tuner.TAG_STYLE);
        onTimeChanged();
    }

    private void key(int number) {
        this._hourView.setText(this._minute1View.getText());
        this._minute1View.setText(this._minute0View.getText());
        this._minute0View.setText(Integer.toString(number));
        onTimeChanged();
    }

    private void onTimeChanged() {
        int mins = getMins();
        if (this._startButton != null) {
            this._startButton.setEnabled(mins > 0);
        }
        if (this._backspaceButton != null) {
            this._backspaceButton.setEnabled(mins > 0);
        }
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putInt(Key.SLEEP_TIMER_TIME, mins * 60);
        AppUtils.apply(editor);
    }

    private int getMins() {
        return (Integer.parseInt(this._hourView.getText().toString()) * 60) + (Integer.parseInt(this._minute1View.getText().toString()) * 10) + Integer.parseInt(this._minute0View.getText().toString());
    }

    private void setMins(int mins) {
        this._hourView.setText(Integer.toString(mins / 60));
        this._minute1View.setText(Integer.toString((mins % 60) / 10));
        this._minute0View.setText(Integer.toString((mins % 60) % 10));
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        int mins = getMins();
        if (L.sleepTimer != null) {
            L.sleepTimer.clear();
        }
        if (which == -1 && mins > 0) {
            L.sleepTimer = this;
            this._decorView.postDelayed(this, mins * 60 * 1000);
            this.fired = false;
        }
        notifyTimerChanged();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.finishLastMedia = isChecked;
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putBoolean(Key.SLEEP_TIMER_FINISH_LAST_MEDIA, isChecked);
        AppUtils.apply(editor);
        notifyTimerChanged();
    }

    private void notifyTimerChanged() {
        for (ActivityScreen activity : ActivityRegistry.findByClass(ActivityScreen.class)) {
            activity.onSleepTimerChanged(this);
        }
        if (PlayService.instance != null) {
            PlayService.instance.onSleepTimerChanged(this);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        if (L.sleepTimer == this) {
            this.fired = true;
            if (!this.finishLastMedia) {
                L.sleepTimer = null;
                for (ActivityScreen activity : ActivityRegistry.findByClass(ActivityScreen.class)) {
                    activity.onSleepTimerFired(this);
                }
                if (PlayService.instance != null) {
                    PlayService.instance.onSleepTimerFired(this);
                }
            }
        }
    }

    public void clear() {
        this._decorView.removeCallbacks(this);
        L.sleepTimer = null;
    }
}
