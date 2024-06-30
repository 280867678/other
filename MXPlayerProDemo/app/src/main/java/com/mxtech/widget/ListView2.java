package com.mxtech.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListView;

/* loaded from: classes.dex */
public class ListView2 extends ListView {
    private boolean _dpadLeftUp;

    public ListView2(Context context) {
        super(context);
        this._dpadLeftUp = true;
    }

    public ListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._dpadLeftUp = true;
    }

    public ListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._dpadLeftUp = true;
    }

    @TargetApi(21)
    public ListView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._dpadLeftUp = true;
    }

    public void enableDPadLeftUp(boolean enable) {
        this._dpadLeftUp = enable;
    }

    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this._dpadLeftUp && event.getAction() == 0 && event.getKeyCode() == 21) {
            moveListViewToTop(this);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void moveListViewToTop(ListView listView) {
        listView.setSelectionFromTop(0, listView.getHeight() / 2);
    }
}
