package com.mxtech.videoplayer.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ZoomButton;
import com.mxtech.videoplayer.IPlayer;
import com.mxtech.videoplayer.pro.R;
import java.text.NumberFormat;
import java.util.Locale;

/* loaded from: classes.dex */
public class PlaybackSpeedBar implements IFloatingBar, View.OnClickListener, TextWatcher {
    public static final double MAX_SPEED = 4.0d;
    public static final double MIN_SPEED = 0.25d;
    public static final double SPEED_UNIT = 0.05d;
    private final ZoomButton _decButton;
    private final NumberFormat _formatter;
    private final ZoomButton _incButton;
    private final ViewGroup _layout;
    private final IPlayer _player;
    private final TextView _text;

    public PlaybackSpeedBar(ViewGroup parent, LayoutInflater layoutInflater, IPlayer player) {
        this._player = player;
        this._layout = (ViewGroup) layoutInflater.inflate(R.layout.playback_speed_bar, parent).findViewById(R.id.playback_speed_bar);
        this._text = (TextView) this._layout.findViewById(R.id.text);
        this._decButton = (ZoomButton) this._layout.findViewById(R.id.dec);
        this._incButton = (ZoomButton) this._layout.findViewById(R.id.inc);
        this._incButton.setZoomSpeed(20L);
        this._decButton.setZoomSpeed(20L);
        this._incButton.setOnClickListener(this);
        this._decButton.setOnClickListener(this);
        this._layout.findViewById(R.id.close).setOnClickListener(this);
        this._formatter = NumberFormat.getInstance(Locale.getDefault());
        this._formatter.setMinimumFractionDigits(0);
        updateSpeedText();
        this._text.addTextChangedListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dec) {
            changeSpeed(-0.05d);
        } else if (id == R.id.inc) {
            changeSpeed(0.05d);
        } else if (id == R.id.close) {
            this._player.showFloatingBar(this._layout.getId(), false);
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            this._player.setSpeed(getValidSpeed(Double.parseDouble(s.toString()) / 100.0d));
        } catch (NumberFormatException e) {
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    public static double getValidSpeed(double value) {
        if (value < 0.25d) {
            return 0.25d;
        }
        if (value > 4.0d) {
            return 4.0d;
        }
        return value;
    }

    private void changeSpeed(double delta) {
        setSpeed(this._player.getSpeed() + delta);
    }

    private void setSpeed(double speed) {
        double speed2 = getValidSpeed(speed);
        this._player.setSpeed(speed2);
        updateSpeedText(speed2);
    }

    public void updateSpeedText() {
        updateSpeedText(this._player.getSpeed());
    }

    public void updateSpeedText(double speed) {
        this._text.setText(this._formatter.format(100.0d * speed));
    }

    @Override // com.mxtech.videoplayer.widget.IFloatingBar
    public ViewGroup getLayout() {
        return this._layout;
    }
}
