package com.mxtech.preference;

import afzkl.development.mColorPicker.views.ColorPanelView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import com.mxtech.app.AppUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.ColorPickerDialog;
import java.util.Arrays;

/* loaded from: classes2.dex */
public class ColorPickerPreference extends AppCompatPreference implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    public static final int[] DEFAULT_COLOR = {0, 0};
    private int[] _color;
    private int[][] _colors;
    private int[] _defaultColor;
    private ColorPickerDialog _dialog;
    private String _dialogTitle;
    private ColorPanelView _previewView;
    private boolean _selectOpacity;

    public ColorPickerPreference(Context context) {
        super(context);
        this._selectOpacity = false;
        init(context, null, 0, 0);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._selectOpacity = false;
        init(context, attrs, 0, 0);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._selectOpacity = false;
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"NewApi"})
    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._selectOpacity = false;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setWidgetLayoutResource(R.layout.color_picker_preference_widget);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference, defStyleAttr, defStyleRes);
        try {
            CharSequence[] values = a.getTextArray(R.styleable.ColorPickerPreference_android_entryValues);
            if (values != null) {
                this._colors = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    this._colors[i] = parseColor(String.valueOf(values[i]));
                }
            }
            this._selectOpacity = a.getBoolean(R.styleable.ColorPickerPreference_selectOpacity, false);
            String defaultColor = a.getString(R.styleable.ColorPickerPreference_android_defaultValue);
            if (defaultColor != null) {
                this._defaultColor = parseColor(defaultColor);
            }
            this._dialogTitle = a.getString(R.styleable.ColorPickerPreference_android_dialogTitle);
            if (this._dialogTitle == null) {
                this._dialogTitle = a.getString(R.styleable.ColorPickerPreference_android_title);
            }
        } finally {
            a.recycle();
        }
    }

    protected int[] parseColor(String value) {
        return new int[]{Color.parseColor(value), 0};
    }

    protected final int[] parseColor(String value, int[] defaultValue) {
        int[] color = parseColor(value);
        return color != null ? color : defaultValue;
    }

    protected String toColorString(int[] color) {
        return String.format("#%08X", Integer.valueOf(color[0]));
    }

    @Override // android.preference.Preference
    protected final Object onGetDefaultValue(TypedArray a, int index) {
        return parseColor(a.getString(index), DEFAULT_COLOR);
    }

    @Override // android.preference.Preference
    protected final void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int[] color;
        if (restoreValue) {
            String colorStr = getPersistedString(null);
            if (colorStr != null) {
                color = parseColor(colorStr);
            } else if (this._defaultColor != null) {
                color = this._defaultColor;
            } else {
                color = DEFAULT_COLOR;
            }
        } else if (this._defaultColor != null) {
            color = this._defaultColor;
        } else {
            color = DEFAULT_COLOR;
        }
        setColor(color);
    }

    @Override // android.preference.Preference
    protected final void onBindView(View view) {
        super.onBindView(view);
        this._previewView = (ColorPanelView) view.findViewById(R.id.color_preview);
        this._previewView.setColor(this._color);
    }

    private void setColor(int[] color) {
        this._color = color;
        persistString(toColorString(color));
        if (this._previewView != null) {
            this._previewView.setColor(this._color);
        }
        notifyChanged();
    }

    @Override // android.preference.Preference
    protected void onClick() {
        if (this._dialog == null || !this._dialog.isShowing()) {
            showDialog(null);
        }
    }

    protected final void showDialog(Bundle state) {
        int[] defaultColor;
        Context context = getContext();
        Activity activity = AppUtils.getActivityFrom(context);
        if (activity == null || !activity.isFinishing()) {
            int flags = 0;
            if (this._defaultColor != null) {
                defaultColor = this._defaultColor;
            } else {
                flags = 0 | 2;
                defaultColor = DEFAULT_COLOR;
            }
            if (this._selectOpacity) {
                flags |= 1;
            }
            this._dialog = new ColorPickerDialog(context, defaultColor, this._color, this._colors, flags);
            if (this._dialogTitle != null) {
                this._dialog.setTitle(this._dialogTitle);
            }
            if (this._colors != null) {
                this._dialog.setOnDismissListener(this);
            } else {
                this._dialog.setButton(-1, MXApplication.context.getString(17039370), this);
                this._dialog.setButton(-2, MXApplication.context.getString(17039360), (DialogInterface.OnClickListener) null);
            }
            onDialogCreated(this._dialog);
            if (state != null) {
                this._dialog.onRestoreInstanceState(state);
            }
            this._dialog.setCanceledOnTouchOutside(true);
            this._dialog.show();
        }
    }

    protected void onDialogCreated(ColorPickerDialog dialog) {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        int[] color = ((ColorPickerDialog) dialog).getColors();
        if (!Arrays.equals(this._color, color) && callChangeListener(color)) {
            setColor(color);
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        int[] color = ((ColorPickerDialog) dialog).getColors();
        if (!Arrays.equals(this._color, color) && callChangeListener(color)) {
            setColor(color);
        }
    }

    @Override // android.preference.Preference
    protected boolean callChangeListener(Object newValue) {
        if (newValue instanceof int[]) {
            newValue = toColorString((int[]) newValue);
        }
        return super.callChangeListener(newValue);
    }

    public final void setSelectOpacity(boolean enable) {
        this._selectOpacity = enable;
    }

    @Override // android.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (this._dialog == null || !this._dialog.isShowing()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.dialogBundle = this._dialog.onSaveInstanceState();
        return myState;
    }

    @Override // android.preference.Preference
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        showDialog(myState.dialogBundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.mxtech.preference.ColorPickerPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            this.dialogBundle = source.readBundle();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }
}
