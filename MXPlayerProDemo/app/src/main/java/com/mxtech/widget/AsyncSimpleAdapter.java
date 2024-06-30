package com.mxtech.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class AsyncSimpleAdapter extends BaseAdapter implements Filterable {
    private final String _from;
    private FilterTextProvider _textProvider;
    private String[] _texts;
    private final int _to;
    private int mDropDownResource;
    private SimpleFilter mFilter;
    private final LayoutInflater mInflater;
    private int mResource;

    public AsyncSimpleAdapter(Context context, FilterTextProvider textProvider, int resource, String from, int to) {
        this.mDropDownResource = resource;
        this.mResource = resource;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this._from = from;
        this._to = to;
        this._textProvider = textProvider;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this._texts != null) {
            return this._texts.length;
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (this._texts != null) {
            return this._texts[position];
        }
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(this.mInflater, position, convertView, parent, this.mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView, ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }
        bindView(position, v);
        return v;
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(this.mInflater, position, convertView, parent, this.mDropDownResource);
    }

    private void bindView(int position, View view) {
        View v;
        if (this._texts != null && this._texts.length > position && (v = view.findViewById(this._to)) != null) {
            String data = this._texts[position];
            if (v instanceof TextView) {
                ((TextView) v).setText(data);
                return;
            }
            throw new IllegalStateException(v.getClass().getName() + " is not a view that can be bounds by this SimpleAdapter");
        }
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new SimpleFilter();
        }
        return this.mFilter;
    }

    /* loaded from: classes2.dex */
    private class SimpleFilter extends Filter {
        private SimpleFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults results = new Filter.FilterResults();
            String[] texts = AsyncSimpleAdapter.this._textProvider.runQuery(constraint);
            if (texts != null) {
                results.count = texts.length;
                results.values = texts;
            }
            return results;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            AsyncSimpleAdapter.this._texts = (String[]) results.values;
            if (results.count > 0) {
                AsyncSimpleAdapter.this.notifyDataSetChanged();
            } else {
                AsyncSimpleAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
