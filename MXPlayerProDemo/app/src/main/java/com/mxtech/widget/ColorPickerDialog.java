package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.mxtech.DeviceUtils;
import com.mxtech.Library;
import com.mxtech.database.DataSetObservable2;
import com.mxtech.graphics.SemiCircleDrawable;
import com.mxtech.videoplayer.pro.R;

@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public final class ColorPickerDialog extends AlertDialog implements ListAdapter, AdapterView.OnItemClickListener, ColorPicker.OnColorChangedListener, DialogInterface.OnShowListener {
    public static final int FLAG_NO_DEFAULT_COLOR = 2;
    public static final int FLAG_SELECT_OPACITY = 1;
    private static final String TAG = "MX.ColorPickerDialog";
    @Nullable
    private EditText _alpha;
    @Nullable
    private EditText _blue;
    private int _borderColor;
    private int _borderWidth;
    private boolean _changeColorFromTextInput;
    private int[][] _colors;
    private int _currentColorIndex;
    @Nullable
    private EditText _green;
    private GridView _grid;
    private DataSetObservable _gridObserver;
    private OnColorChangedListener _onColorChangedListener;
    private OpacityBar _opacityBar;
    private ColorPicker _picker;
    @Nullable
    private EditText _red;
    private SaturationBar _saturationBar;
    private View _topLayout;
    private ValueBar _valueBar;

    /* loaded from: classes2.dex */
    public interface OnColorChangedListener {
        void onColorChanged(ColorPickerDialog colorPickerDialog, int[] iArr);
    }

    public ColorPickerDialog(Context context, int defaultColor, int currentColor, int flags) {
        super(context);
        init(context, flags, new int[]{defaultColor, 0}, new int[]{currentColor, 0}, null);
    }

    public ColorPickerDialog(Context context, int[] defaultColor, int[] currentColor, int[][] colors, int flags) {
        super(context);
        init(context, flags, defaultColor, currentColor, colors);
    }

    public ColorPickerDialog(Context context, int[] defaultColor, int[] currentColor, int[][] colors, int flags, int theme) {
        super(context, theme);
        init(context, flags, defaultColor, currentColor, colors);
    }

    @SuppressLint({"InflateParams"})
    private void init(Context context, int flags, int[] defaultColor, int[] currentColor, int[][] colors) {
        LayoutInflater inflater = getLayoutInflater();
        if (colors != null) {
            this._colors = colors;
            this._topLayout = inflater.inflate(R.layout.color_collection, (ViewGroup) null);
            this._gridObserver = new DataSetObservable2();
            this._grid = (GridView) this._topLayout.findViewById(R.id.color_grid);
            this._grid.setAdapter((ListAdapter) this);
            int i = 0;
            while (true) {
                if (i >= colors.length) {
                    break;
                } else if (colors[i] != currentColor) {
                    i++;
                } else {
                    this._currentColorIndex = i;
                    this._grid.setSelection(i);
                    break;
                }
            }
            this._grid.setOnItemClickListener(this);
            TypedArray a = context.obtainStyledAttributes(R.styleable.ColorPickerDialog);
            this._borderColor = a.getColor(R.styleable.ColorPickerDialog_borderColor, -2004318072);
            a.recycle();
            this._borderWidth = context.getResources().getDimensionPixelSize(R.dimen.border_width);
        } else {
            this._topLayout = inflater.inflate(R.layout.color_picker, (ViewGroup) null);
            this._picker = (ColorPicker) this._topLayout.findViewById(R.id.picker);
            this._valueBar = (ValueBar) this._topLayout.findViewById(R.id.value_bar);
            this._saturationBar = (SaturationBar) this._topLayout.findViewById(R.id.saturation_bar);
            this._opacityBar = (OpacityBar) this._topLayout.findViewById(R.id.opacity_bar);
            this._red = (EditText) this._topLayout.findViewById(R.id.red);
            this._green = (EditText) this._topLayout.findViewById(R.id.green);
            this._blue = (EditText) this._topLayout.findViewById(R.id.blue);
            if ((flags & 1) != 0) {
                this._picker.addOpacityBar(this._opacityBar);
                this._alpha = (EditText) this._topLayout.findViewById(R.id.alpha);
            } else {
                this._blue.setNextFocusDownId(-1);
                this._blue.setNextFocusRightId(-1);
                this._opacityBar.setVisibility(8);
                this._topLayout.findViewById(R.id.alpha_label).setVisibility(8);
                this._topLayout.findViewById(R.id.alpha).setVisibility(8);
            }
            this._picker.addValueBar(this._valueBar);
            this._picker.addSaturationBar(this._saturationBar);
            if (defaultColor[0] == 0) {
                defaultColor[0] = Color.argb(0, 1, 1, 1);
            }
            if ((flags & 2) != 0) {
                this._picker.setShowOldCenterColor(false);
            } else {
                this._picker.setShowOldCenterColor(true);
                this._picker.setOldCenterColor(defaultColor[0]);
                if (!DeviceUtils.hasTouchScreen) {
                    setButton(-3, context.getString(Library.defaultResId), (DialogInterface.OnClickListener) null);
                    setOnShowListener(this);
                }
            }
            this._picker.setOnColorChangedListener(this);
            this._red.addTextChangedListener(new TextWatcher() { // from class: com.mxtech.widget.ColorPickerDialog.1
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    int oldRed = Color.red(ColorPickerDialog.this._picker.getColor());
                    int newRed = ColorPickerDialog.this.getControlColor(s);
                    if (oldRed != newRed) {
                        ColorPickerDialog.this.setPickerColor((ColorPickerDialog.this._picker.getColor() & (-16711681)) | (newRed << 16), true);
                    }
                }
            });
            this._green.addTextChangedListener(new TextWatcher() { // from class: com.mxtech.widget.ColorPickerDialog.2
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    int oldGreen = Color.green(ColorPickerDialog.this._picker.getColor());
                    int newGreen = ColorPickerDialog.this.getControlColor(s);
                    if (oldGreen != newGreen) {
                        ColorPickerDialog.this.setPickerColor((ColorPickerDialog.this._picker.getColor() & (-65281)) | (newGreen << 8), true);
                    }
                }
            });
            this._blue.addTextChangedListener(new TextWatcher() { // from class: com.mxtech.widget.ColorPickerDialog.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    int oldBlue = Color.blue(ColorPickerDialog.this._picker.getColor());
                    int newBlue = ColorPickerDialog.this.getControlColor(s);
                    if (oldBlue != newBlue) {
                        ColorPickerDialog.this.setPickerColor((ColorPickerDialog.this._picker.getColor() & InputDeviceCompat.SOURCE_ANY) | newBlue, true);
                    }
                }
            });
            if (this._alpha != null) {
                this._alpha.addTextChangedListener(new TextWatcher() { // from class: com.mxtech.widget.ColorPickerDialog.4
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        int oldAlpha = Color.alpha(ColorPickerDialog.this._picker.getColor());
                        int newAlpha = ColorPickerDialog.this.getControlColor(s);
                        if (oldAlpha != newAlpha) {
                            ColorPickerDialog.this.setPickerColor((ColorPickerDialog.this._picker.getColor() & ViewCompat.MEASURED_SIZE_MASK) | (newAlpha << 24), true);
                        }
                    }
                });
            }
            setPickerColor(currentColor[0], false);
        }
        setView(this._topLayout);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Button button = getButton(-3);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.widget.ColorPickerDialog.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ColorPickerDialog.this.setPickerColor(ColorPickerDialog.this._picker.getOldCenterColor(), false);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPickerColor(int color, boolean fromTextInput) {
        this._changeColorFromTextInput = fromTextInput;
        this._picker.setColor(color);
        this._changeColorFromTextInput = false;
    }

    public final ViewGroup getFooterHolder() {
        return (ViewGroup) this._topLayout.findViewById(R.id.footer);
    }

    public void setFooter(View footer) {
        ViewGroup footerHolder = (ViewGroup) this._topLayout.findViewById(R.id.footer);
        if (footerHolder != null) {
            footerHolder.addView(footer);
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this._onColorChangedListener = listener;
    }

    public void setOnColorChangedListener(final ColorPicker.OnColorChangedListener listener) {
        this._onColorChangedListener = new OnColorChangedListener() { // from class: com.mxtech.widget.ColorPickerDialog.6
            @Override // com.mxtech.widget.ColorPickerDialog.OnColorChangedListener
            public void onColorChanged(ColorPickerDialog dialog, int[] color) {
                listener.onColorChanged(color[0]);
            }
        };
    }

    public int[] getColors() {
        return this._picker != null ? new int[]{this._picker.getColor(), 0} : this._colors[this._currentColorIndex];
    }

    public int getColor() {
        return this._picker != null ? this._picker.getColor() : this._colors[this._currentColorIndex][0];
    }

    @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
    public void onColorChanged(int color) {
        int newAlpha;
        int newBlue;
        int newGreen;
        int newRed;
        if (!this._changeColorFromTextInput) {
            if (this._red != null && (newRed = Color.red(color)) != getControlColor(this._red)) {
                this._red.setText(Integer.toString(newRed));
            }
            if (this._green != null && (newGreen = Color.green(color)) != getControlColor(this._green)) {
                this._green.setText(Integer.toString(newGreen));
            }
            if (this._blue != null && (newBlue = Color.blue(color)) != getControlColor(this._blue)) {
                this._blue.setText(Integer.toString(newBlue));
            }
            if (this._alpha != null && (newAlpha = Color.alpha(color)) != getControlColor(this._alpha)) {
                this._alpha.setText(Integer.toString(newAlpha));
            }
        }
        if (this._onColorChangedListener != null) {
            this._onColorChangedListener.onColorChanged(this, new int[]{color, 0});
        }
    }

    private int getControlColor(TextView tv) {
        return getColor(tv, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getControlColor(Editable s) {
        return getColor(null, s);
    }

    private int getColor(@Nullable TextView tv, @Nullable Editable s) {
        CharSequence text = s != null ? s : tv.getText();
        if (text.length() == 0) {
            return 0;
        }
        try {
            int color = Integer.parseInt(text.toString());
            if (color > 255) {
                if (s == null) {
                    return 255;
                }
                s.replace(0, s.length(), "255");
                return 255;
            }
            return color;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this._currentColorIndex = position;
        if (this._onColorChangedListener != null) {
            this._onColorChangedListener.onColorChanged(this, this._colors[position]);
        }
        dismiss();
    }

    @Override // android.widget.Adapter
    public void registerDataSetObserver(DataSetObserver observer) {
        this._gridObserver.registerObserver(observer);
    }

    @Override // android.widget.Adapter
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this._gridObserver.unregisterObserver(observer);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this._colors.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this._colors[position];
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view;
        if (convertView == null) {
            view = (ImageView) getLayoutInflater().inflate(R.layout.color_collection_item, parent, false);
        } else {
            view = (ImageView) convertView;
        }
        setColorViewValue(view.findViewById(R.id.color_view), this._colors[position]);
        if (position == this._currentColorIndex) {
            view.setBackgroundColor(1714664933);
        } else {
            view.setBackgroundDrawable(null);
        }
        return view;
    }

    @Override // android.widget.Adapter
    public int getItemViewType(int position) {
        return 0;
    }

    @Override // android.widget.Adapter
    public int getViewTypeCount() {
        return 1;
    }

    @Override // android.widget.Adapter
    public boolean isEmpty() {
        return false;
    }

    @Override // android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return true;
    }

    private void setColorViewValue(View view, int[] color) {
        SemiCircleDrawable d;
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Drawable current = imageView.getDrawable();
            if (current instanceof SemiCircleDrawable) {
                d = (SemiCircleDrawable) current;
            } else {
                d = new SemiCircleDrawable();
            }
            d.setBounds(0, 0, view.getWidth(), view.getHeight());
            if (color[1] != 0) {
                d.setColors(color[0], color[1]);
            } else {
                d.setColor(color[0]);
            }
            d.setStroke(this._borderWidth, this._borderColor);
            imageView.setImageDrawable(d);
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(color[0]);
        }
    }
}
