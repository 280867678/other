package com.mxtech.videoplayer.audio;

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
public class AudioSyncBar implements IFloatingBar, View.OnClickListener, TextWatcher {
    private static final int SYNC_UNIT = 10;
    private final NumberFormat _formatter;
    private final ViewGroup _layout;
    private final IPlayer _player;
    private final TextView _text;

    public AudioSyncBar(ViewGroup parent, LayoutInflater layoutInflater, IPlayer player) {
        this._player = player;
        this._layout = (ViewGroup) layoutInflater.inflate(R.layout.audio_sync_bar, parent).findViewById(R.id.audio_sync_bar);
        this._text = (TextView) this._layout.findViewById(R.id.text);
        this._text.addTextChangedListener(this);
        ZoomButton backward = (ZoomButton) this._layout.findViewById(R.id.backward);
        ZoomButton forward = (ZoomButton) this._layout.findViewById(R.id.forward);
        forward.setZoomSpeed(20L);
        backward.setZoomSpeed(20L);
        forward.setOnClickListener(this);
        backward.setOnClickListener(this);
        this._layout.findViewById(R.id.close).setOnClickListener(this);
        this._formatter = NumberFormat.getInstance(Locale.getDefault());
        this._formatter.setMinimumFractionDigits(2);
        this._formatter.setMaximumFractionDigits(2);
        updateSyncText();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backward) {
            changeSync(-10);
        } else if (id == R.id.forward) {
            changeSync(10);
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
            this._player.setAudioSync((int) Math.round(Double.parseDouble(s.toString()) * 1000.0d));
        } catch (NumberFormatException e) {
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    private void changeSync(int deltaMs) {
        this._player.setAudioSync(this._player.getAudioSync() + deltaMs);
        updateSyncText();
    }

    private void updateSyncText() {
        this._text.setText(this._formatter.format(this._player.getAudioSync() / 1000.0d));
    }

    @Override // com.mxtech.videoplayer.widget.IFloatingBar
    public ViewGroup getLayout() {
        return this._layout;
    }
}
