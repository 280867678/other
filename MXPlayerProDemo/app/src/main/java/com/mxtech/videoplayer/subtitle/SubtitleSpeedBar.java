package com.mxtech.videoplayer.subtitle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ZoomButton;
import com.mxtech.videoplayer.IPlayer;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.widget.IFloatingBar;
import java.text.NumberFormat;
import java.util.Locale;

/* loaded from: classes.dex */
public class SubtitleSpeedBar implements IFloatingBar, View.OnClickListener, TextWatcher {
    static final double SUBTITLE_SPEED_UNIT = 0.001d;
    private final NumberFormat _formatter;
    private final ViewGroup _layout;
    private final IPlayer _player;
    private final TextView _text;
    private final SubView theView;

    public SubtitleSpeedBar(ViewGroup parent, LayoutInflater layoutInflater, SubView subView, IPlayer player) {
        this.theView = subView;
        this._player = player;
        this._layout = (ViewGroup) layoutInflater.inflate(R.layout.subtitle_speed_bar, parent).findViewById(R.id.subtitle_speed_bar);
        this._text = (TextView) this._layout.findViewById(R.id.text);
        this._text.addTextChangedListener(this);
        ZoomButton decrease = (ZoomButton) this._layout.findViewById(R.id.dec);
        ZoomButton increase = (ZoomButton) this._layout.findViewById(R.id.inc);
        increase.setZoomSpeed(20L);
        decrease.setZoomSpeed(20L);
        increase.setOnClickListener(this);
        decrease.setOnClickListener(this);
        this._layout.findViewById(R.id.close).setOnClickListener(this);
        this._formatter = NumberFormat.getInstance(Locale.getDefault());
        this._formatter.setMinimumFractionDigits(1);
        updateSpeedText();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dec) {
            changeSpeed(-0.001d);
        } else if (id == R.id.inc) {
            changeSpeed(SUBTITLE_SPEED_UNIT);
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
            this.theView.setSpeed(Double.parseDouble(s.toString()) / 100.0d);
        } catch (NumberFormatException e) {
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    private void changeSpeed(double delta) {
        setSpeed(this.theView.getSpeed() + delta);
    }

    private void setSpeed(double speed) {
        this.theView.setSpeed(speed);
        updateSpeedText();
    }

    private void updateSpeedText() {
        this._text.setText(this._formatter.format(this.theView.getSpeed() * 100.0d));
    }

    @Override // com.mxtech.videoplayer.widget.IFloatingBar
    public ViewGroup getLayout() {
        return this._layout;
    }
}
