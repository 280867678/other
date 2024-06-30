package com.mxtech.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import java.lang.reflect.Field;

@TargetApi(14)
/* loaded from: classes2.dex */
public class NumberPicker2 extends NumberPicker {
    private static final String TAG = "MX.NumberPicker2";

    public NumberPicker2(Context context) {
        super(context);
    }

    public NumberPicker2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPicker2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public NumberPicker2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.NumberPicker
    public void setFormatter(NumberPicker.Formatter formatter) {
        super.setFormatter(formatter);
        try {
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(this);
            inputText.setFilters(new InputFilter[0]);
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
    }
}
