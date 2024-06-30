package com.mxtech.text;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class TextViewUtils {
    public static Rect getNumberBounds(TextView v) {
        Rect bounds = new Rect();
        Rect maxBounds = new Rect();
        Paint textPaint = v.getPaint();
        char[] buf = {'0'};
        int i = 0;
        while (i < 10) {
            textPaint.getTextBounds(buf, 0, 1, bounds);
            maxBounds.union(bounds);
            i++;
            buf[0] = (char) (buf[0] + 1);
        }
        return maxBounds;
    }

    public static Rect getBounds(TextView v, String text) {
        Rect bounds = new Rect();
        v.getPaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    public static void makeClearable(@Nullable ViewGroup parent, TextView view, ImageView clearButton) {
        new TextViewClearable(parent, view, clearButton);
    }

    /* loaded from: classes2.dex */
    private static class TextViewClearable implements TextWatcher, View.OnClickListener {
        private final ImageView _clearButton;
        @Nullable
        private final ViewGroup _parent;
        private final TextView _view;

        private TextViewClearable(ViewGroup parent, TextView view, ImageView clearButton) {
            this._parent = parent;
            this._view = view;
            this._clearButton = clearButton;
            view.addTextChangedListener(this);
            updateButtonVisibility(view.getText());
            clearButton.setOnClickListener(this);
            themify();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateButtonVisibility(s);
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            this._view.setText("");
        }

        private void updateButtonVisibility(CharSequence s) {
            this._clearButton.setVisibility(s.length() > 0 ? 0 : 4);
        }

        private void themify() {
            Drawable background;
            TypedArray a = this._view.getContext().obtainStyledAttributes(R.styleable.TextViewClearable);
            int colorButton = a.getColor(R.styleable.TextViewClearable_android_textColorPrimary, ViewCompat.MEASURED_STATE_MASK);
            int colorAccent = a.getColor(R.styleable.TextViewClearable_colorAccent, ViewCompat.MEASURED_STATE_MASK);
            a.recycle();
            if (this._parent != null && (background = this._parent.getBackground()) != null) {
                background.setColorFilter(colorAccent, PorterDuff.Mode.SRC_IN);
            }
            Drawable buttonDrawable = this._clearButton.getDrawable();
            if (buttonDrawable != null) {
                buttonDrawable.setColorFilter(colorButton, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
