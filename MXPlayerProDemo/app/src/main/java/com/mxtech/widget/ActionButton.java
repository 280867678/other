package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.mxtech.ViewUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes2.dex */
public class ActionButton extends ImageButton implements View.OnLongClickListener {
    private CharSequence _title;

    public ActionButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint({"NewApi"})
    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionButton, defStyleAttr, defStyleRes);
        this._title = a.getString(R.styleable.ActionButton_android_title);
        a.recycle();
        if (this._title != null) {
            setOnLongClickListener(this);
        }
    }

    public void setTitle(CharSequence title) {
        this._title = title;
    }

    public void setTitle(int resId) {
        this._title = getContext().getString(resId);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        Toast toast = Toast.makeText(getContext(), this._title, 0);
        ViewUtils.positionToast(toast, this);
        toast.show();
        return true;
    }
}
