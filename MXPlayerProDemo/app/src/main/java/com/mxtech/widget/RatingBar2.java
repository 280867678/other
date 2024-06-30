package com.mxtech.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RatingBar;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class RatingBar2 extends RatingBar {
    public RatingBar2(Context context) {
        super(context);
        init(context, null, R.attr.ratingBarStyle);
    }

    public RatingBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.ratingBarStyle);
    }

    public RatingBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT < 23) {
            TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.colorAccent});
            int accentColor = a.getColor(0, 0);
            a.recycle();
            if (accentColor != 0) {
                getProgressDrawable().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override // android.widget.AbsSeekBar, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            int progress = getProgress();
            switch (keyCode) {
                case 21:
                    if (progress > 0) {
                        setProgress(progress - getKeyProgressIncrement());
                        return true;
                    }
                    break;
                case 22:
                    if (progress < getMax()) {
                        setProgress(getKeyProgressIncrement() + progress);
                        return true;
                    }
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
