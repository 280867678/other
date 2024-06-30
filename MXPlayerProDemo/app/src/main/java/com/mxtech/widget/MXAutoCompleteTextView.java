package com.mxtech.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import com.mxtech.DeviceUtils;
import com.mxtech.ViewUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class MXAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    private static final String TAG = "MX.AutoCompleteTextView";
    private boolean _noLineFeed;

    public MXAutoCompleteTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MXAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MXAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MXAutoCompleteTextView, defStyleAttr, 0);
        this._noLineFeed = a.getBoolean(R.styleable.MXAutoCompleteTextView_noLineFeed, false);
        if (this._noLineFeed) {
            addTextChangedListener(new TextWatcher() { // from class: com.mxtech.widget.MXAutoCompleteTextView.1
                private int _depth;
                private boolean _singleLinefeedInserted;

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    this._depth++;
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (this._depth == 1) {
                        if (count == 1 && s.charAt(start) == '\n') {
                            this._singleLinefeedInserted = true;
                        } else {
                            this._singleLinefeedInserted = false;
                        }
                    }
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    if (this._depth == 1) {
                        for (int i = s.length() - 1; i >= 0; i--) {
                            char ch = s.charAt(i);
                            if (ch == '\n' || ch == '\r') {
                                s.delete(i, i + 1);
                            }
                        }
                        if (this._singleLinefeedInserted) {
                            MXAutoCompleteTextView.this.onEditorAction(6);
                        }
                    }
                    this._depth--;
                }
            });
        }
        a.recycle();
    }

    public void trim() {
        ViewUtils.trimTextView(this);
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 23 && showSoftInputIfNecessary(this, true)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean showSoftInputIfNecessary(AutoCompleteTextView view, boolean userRequest) {
        if (!DeviceUtils.hasQwertKeyboard && !view.isPopupShowing()) {
            Context context = view.getContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService("input_method");
            return imm.showSoftInput(view, userRequest ? 2 : 1);
        }
        return false;
    }
}
