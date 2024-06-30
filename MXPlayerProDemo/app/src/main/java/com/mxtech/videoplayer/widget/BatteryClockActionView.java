package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class BatteryClockActionView extends FrameLayout {
    private ImageView _batteryCharging;
    private TextView _text;

    public BatteryClockActionView(Context context) {
        this(context, null);
    }

    public BatteryClockActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.screen_status_action_view, this);
        this._text = (TextView) findViewById(R.id.statusText);
        this._batteryCharging = (ImageView) findViewById(R.id.batteryCharging);
        this._batteryCharging.setColorFilter(this._text.getTextColors().getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
    }

    public void setContent(CharSequence text, boolean charging) {
        this._text.setText(text);
        this._batteryCharging.setVisibility(charging ? 0 : 8);
    }
}
