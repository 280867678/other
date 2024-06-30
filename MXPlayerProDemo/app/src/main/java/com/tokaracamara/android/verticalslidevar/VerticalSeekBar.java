package com.tokaracamara.android.verticalslidevar;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes2.dex */
public class VerticalSeekBar extends AbsVerticalSeekBar {
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    /* loaded from: classes.dex */
    public interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar verticalSeekBar, int i, boolean z);

        void onStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onStopTrackingTouch(VerticalSeekBar verticalSeekBar);
    }

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 16842875);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.tokaracamara.android.verticalslidevar.AbsVerticalSeekBar, com.tokaracamara.android.verticalslidevar.VerticalProgressBar
    public void onProgressRefresh(float scale, boolean fromUser) {
        super.onProgressRefresh(scale, fromUser);
        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), fromUser);
        }
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        this.mOnSeekBarChangeListener = l;
    }

    @Override // com.tokaracamara.android.verticalslidevar.AbsVerticalSeekBar
    void onStartTrackingTouch() {
        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    @Override // com.tokaracamara.android.verticalslidevar.AbsVerticalSeekBar
    void onStopTrackingTouch() {
        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }
}
