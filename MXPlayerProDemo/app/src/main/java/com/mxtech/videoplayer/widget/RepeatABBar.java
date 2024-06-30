package com.mxtech.videoplayer.widget;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.mxtech.videoplayer.IPlayer;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class RepeatABBar implements IFloatingBar, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final ToggleButton _A;
    private final ToggleButton _B;
    private final ViewGroup _layout;
    private final IPlayer _player;

    public RepeatABBar(ViewGroup parent, LayoutInflater layoutInflater, IPlayer player) {
        this._player = player;
        this._layout = (ViewGroup) layoutInflater.inflate(R.layout.repeat_ab_bar, parent).findViewById(R.id.repeat_ab_bar);
        this._A = (ToggleButton) this._layout.findViewById(R.id.A);
        this._B = (ToggleButton) this._layout.findViewById(R.id.B);
        this._layout.findViewById(R.id.close).setOnClickListener(this);
        int pointA = this._player.getRepeatPointA();
        int pointB = this._player.getRepeatPointB();
        if (pointA >= 0) {
            this._A.setTextOn(DateUtils.formatElapsedTime(L.sb, (pointA + 500) / 1000));
            this._A.setChecked(true);
        }
        if (pointB >= 0) {
            this._B.setTextOn(DateUtils.formatElapsedTime(L.sb, (pointB + 500) / 1000));
            this._B.setChecked(true);
        }
        this._A.setOnCheckedChangeListener(this);
        this._B.setOnCheckedChangeListener(this);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == this._A) {
            if (isChecked) {
                int pointA = this._player.currentPosition();
                this._player.setRepeatPoints(pointA, this._player.getRepeatPointB());
                this._A.setTextOn(DateUtils.formatElapsedTime(L.sb, (pointA + 500) / 1000));
                return;
            }
            this._player.setRepeatPoints(-1, this._player.getRepeatPointB());
        } else if (isChecked) {
            int pointB = this._player.currentPosition();
            this._player.setRepeatPoints(this._player.getRepeatPointA(), pointB);
            this._B.setTextOn(DateUtils.formatElapsedTime(L.sb, (pointB + 500) / 1000));
        } else {
            this._player.setRepeatPoints(this._player.getRepeatPointA(), -1);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        this._player.showFloatingBar(this._layout.getId(), false);
    }

    @Override // com.mxtech.videoplayer.widget.IFloatingBar
    public ViewGroup getLayout() {
        return this._layout;
    }
}
