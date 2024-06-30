package com.mxtech.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class AppCompatCheckBoxPreference extends AppCompatTwoStatePreference {
    public AppCompatCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public AppCompatCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public AppCompatCheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842895);
    }

    public AppCompatCheckBoxPreference(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppCompatCheckBoxPreference, defStyleAttr, defStyleRes);
        setSummaryOn(a.getString(R.styleable.AppCompatCheckBoxPreference_android_summaryOn));
        setSummaryOff(a.getString(R.styleable.AppCompatCheckBoxPreference_android_summaryOff));
        setDisableDependentsState(a.getBoolean(R.styleable.AppCompatCheckBoxPreference_android_disableDependentsState, false));
        a.recycle();
    }

    @Override // android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        View checkboxView = view.findViewById(16908289);
        if (checkboxView != null && (checkboxView instanceof Checkable)) {
            ((Checkable) checkboxView).setChecked(this.mChecked);
        }
        syncSummaryView(view);
    }
}
