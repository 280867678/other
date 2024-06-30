package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import com.mxtech.DeviceUtils;
import com.mxtech.videoplayer.pro.R;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class PropertyDialog extends AlertDialog {
    private Set<View> _hiddenItems;
    protected final TableLayout layout;

    @SuppressLint({"InflateParams"})
    public PropertyDialog(Context context) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.property_dialog, (ViewGroup) null);
        this.layout = (TableLayout) view.findViewById(R.id.layout);
        super.setView(view);
    }

    public final int addRow(int nameResId, @Nullable CharSequence value) {
        return addRow((CharSequence) getContext().getString(nameResId), value, true);
    }

    public final int addRow(int nameResId, @Nullable CharSequence value, boolean visible) {
        return addRow(getContext().getString(nameResId), value, visible);
    }

    public final int addRow(CharSequence name, @Nullable CharSequence value) {
        return addRow(name, value, true);
    }

    public int addRow(CharSequence name, @Nullable CharSequence value, boolean visible) {
        if (value == null || value.length() <= 0) {
            return 0;
        }
        View row = getLayoutInflater().inflate(R.layout.property_dialog_row, (ViewGroup) this.layout, false);
        TextView nameColumn = (TextView) row.findViewById(R.id.name);
        TextView valueColumn = (TextView) row.findViewById(R.id.value);
        if (!visible) {
            row.setVisibility(8);
            if (this._hiddenItems == null) {
                this._hiddenItems = new HashSet();
            }
            this._hiddenItems.add(row);
        }
        nameColumn.setMaxWidth((DeviceUtils.smallestPixels * 3) / 10);
        nameColumn.setText(name);
        valueColumn.setText(value);
        this.layout.addView(row);
        return 1;
    }

    public final int addGroup(int stringId) {
        return addGroup((CharSequence) getContext().getString(stringId), this.layout.getChildCount(), true);
    }

    public final int addGroup(int stringId, int at) {
        return addGroup((CharSequence) getContext().getString(stringId), at, true);
    }

    public final int addGroup(int stringId, int at, boolean visible) {
        return addGroup(getContext().getString(stringId), at, visible);
    }

    public final int addGroup(CharSequence string) {
        return addGroup(string, this.layout.getChildCount(), true);
    }

    public final int addGroup(CharSequence string, boolean visible) {
        return addGroup(string, this.layout.getChildCount(), visible);
    }

    public final int addGroup(CharSequence string, int at) {
        return addGroup(string, at, true);
    }

    public int addGroup(CharSequence string, int at, boolean visible) {
        TextView column = (TextView) getLayoutInflater().inflate(R.layout.property_dialog_group, (ViewGroup) this.layout, false);
        column.setText(string);
        if (!visible) {
            column.setVisibility(8);
            if (this._hiddenItems == null) {
                this._hiddenItems = new HashSet();
            }
            this._hiddenItems.add(column);
        }
        this.layout.addView(column, at);
        return 1;
    }

    public void showHiddenItems(boolean show) {
        if (this._hiddenItems != null) {
            for (View view : this._hiddenItems) {
                view.setVisibility(show ? 0 : 8);
            }
        }
    }
}
