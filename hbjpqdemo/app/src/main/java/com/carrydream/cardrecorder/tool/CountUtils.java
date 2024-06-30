package com.carrydream.cardrecorder.tool;

import android.os.CountDownTimer;
import android.widget.TextView;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class CountUtils extends CountDownTimer {
    private TextView mTextView;
    OnClickListener onClickListener;

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void start();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CountUtils(TextView textView, long j, long j2) {
        super(j, j2);
        this.mTextView = textView;
    }

    @Override // android.os.CountDownTimer
    public void onTick(long j) {
        this.mTextView.setClickable(false);
        TextView textView = this.mTextView;
        textView.setText((j / 1000) + "秒重新发送");
        this.mTextView.setTextColor(R.color.C_969799);
    }

    @Override // android.os.CountDownTimer
    public void onFinish() {
        this.mTextView.setText("获取短信验证码");
        this.mTextView.setTextColor(R.color.theme);
        this.mTextView.setClickable(true);
    }
}
