package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mxtech.DeviceUtils;
import com.mxtech.videoplayer.pro.R;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public final class TunerTabLayout extends LinearLayout implements View.OnClickListener {
    private static final int PADDING_LEFT = 8;
    private static final int PADDING_RIGHT = 8;
    private static final int PADDING_VERTICAL = 10;
    private Listener _listener;
    private int _paddingLeft;
    private int _paddingRight;
    private int _paddingVertical;
    private TextView _selected;
    private ColorStateList _tabColor;

    /* loaded from: classes.dex */
    public interface Listener {
        void onTabSelected(TextView textView, int i);
    }

    public TunerTabLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TunerTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @TargetApi(11)
    public TunerTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public TunerTabLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{R.attr.colorControlActivated, 16843282}, defStyleAttr, defStyleRes);
        int[] colors = {a.getColor(0, 0), a.getColor(1, 0)};
        int[][] states = {new int[]{16842913}, new int[0]};
        a.recycle();
        this._tabColor = new ColorStateList(states, colors);
    }

    public void setListener(Listener listener) {
        this._listener = listener;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this._paddingVertical = (int) DeviceUtils.DIPToPixel(10.0f);
        this._paddingLeft = (int) DeviceUtils.DIPToPixel(8.0f);
        this._paddingRight = (int) DeviceUtils.DIPToPixel(8.0f);
        TextView[] tabs = getTabs();
        int length = tabs.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            TextView tab = tabs[i];
            tab.setOnClickListener(this);
            tab.setTag(Integer.valueOf(i2));
            tab.setTextColor(this._tabColor);
            i++;
            i2++;
        }
    }

    public TextView[] getTabs() {
        ArrayList<TextView> coll = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v.getId() != -1 && (v instanceof TextView)) {
                coll.add((TextView) v);
            }
        }
        return (TextView[]) coll.toArray(new AppCompatTextView[coll.size()]);
    }

    public TextView getSelected() {
        return this._selected;
    }

    public int getSelectedIndex() {
        if (this._selected == null) {
            return -1;
        }
        return ((Integer) this._selected.getTag()).intValue();
    }

    public int getTabCount() {
        int tabCount = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v.getId() != -1 && (v instanceof TextView)) {
                tabCount++;
            }
        }
        return tabCount;
    }

    public void select(int index) {
        this._selected = null;
        TextView[] tabs = getTabs();
        int length = tabs.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            TextView v = tabs[i];
            int i3 = i2 + 1;
            if (i2 == index) {
                v.setSelected(true);
                v.setFocusable(true);
                v.requestFocus();
                this._selected = v;
            } else {
                v.setSelected(false);
                v.setFocusable(false);
            }
            v.setPadding(this._paddingLeft, this._paddingVertical, this._paddingRight, this._paddingVertical);
            i++;
            i2 = i3;
        }
        if (this._listener != null) {
            this._listener.onTabSelected(this._selected, index);
        }
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus && this._selected != null) {
            this._selected.requestFocus();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        if (index != null) {
            select(index.intValue());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        int index;
        int index2;
        switch (event.getKeyCode()) {
            case 19:
                if (event.getAction() != 0 || (index2 = getSelectedIndex()) <= 0) {
                    return true;
                }
                select(index2 - 1);
                return true;
            case 20:
                if (event.getAction() != 0 || (index = getSelectedIndex()) >= getTabCount() - 1) {
                    return true;
                }
                select(index + 1);
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
