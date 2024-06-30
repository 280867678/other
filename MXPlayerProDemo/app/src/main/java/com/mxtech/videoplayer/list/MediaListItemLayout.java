package com.mxtech.videoplayer.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.widget.CheckableRelativeLayout;

/* loaded from: classes2.dex */
public class MediaListItemLayout extends CheckableRelativeLayout {
    private boolean _hasTouchDownPosition;
    private float _lastTouchDownX;
    private int _paddingLeft;
    private int _paddingLeftNoThumb;
    private int[] _viewLocation;

    public MediaListItemLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MediaListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MediaListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public MediaListItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaListItemLayout, defStyleAttr, defStyleRes);
        this._paddingLeft = a.getDimensionPixelSize(R.styleable.MediaListItemLayout_android_paddingLeft, 0);
        this._paddingLeftNoThumb = a.getDimensionPixelSize(R.styleable.MediaListItemLayout_paddingLeftNoThumb, 0);
        a.recycle();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0) {
            this._lastTouchDownX = ev.getRawX();
            this._hasTouchDownPosition = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        this._hasTouchDownPosition = false;
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent event) {
        this._hasTouchDownPosition = false;
        return super.dispatchTrackballEvent(event);
    }

    public boolean isIconAreaTouched() {
        int viewWidth;
        if (this._hasTouchDownPosition) {
            View view = findViewById(R.id.thumb);
            if (view != null) {
                viewWidth = view.getWidth();
            } else {
                viewWidth = 0;
            }
            if (viewWidth == 0 && (view = findViewById(R.id.icon)) != null) {
                viewWidth = view.getWidth();
            }
            if (viewWidth > 0) {
                if (this._viewLocation == null) {
                    this._viewLocation = new int[2];
                }
                view.getLocationOnScreen(this._viewLocation);
                return this._lastTouchDownX < ((float) (this._viewLocation[0] + viewWidth));
            }
            return false;
        }
        return false;
    }

    public void updatePadding() {
        setPadding((P.list_fields & 1) != 0 ? this._paddingLeft : this._paddingLeftNoThumb, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
}
