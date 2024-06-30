package com.mxtech.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import com.mxtech.ViewUtils;

/* loaded from: classes.dex */
public class MXMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    public MXMultiAutoCompleteTextView(Context context) {
        super(context);
    }

    public MXMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MXMultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void trim() {
        ViewUtils.trimTextView(this);
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 23 && MXAutoCompleteTextView.showSoftInputIfNecessary(this, true)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
