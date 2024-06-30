package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.mxtech.database.DataSetObservable2;
import com.mxtech.videoplayer.pro.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public class OrderedChooser extends AlertDialog implements ListAdapter, SectionIndexer, AdapterView.OnItemClickListener, DialogInterface.OnShowListener, View.OnClickListener {
    private final ArrayList<Integer> _checkedItems;
    private TextView _header;
    private int _itemLayoutId;
    private Object[] _itemSpans;
    private CharSequence[] _items;
    private ListView _listView;
    private final DataSetObservable _observable;
    private Object[] _sections;

    public OrderedChooser(Context context, CharSequence[] items, int[] checkedItems) {
        super(context);
        this._observable = new DataSetObservable2();
        this._checkedItems = new ArrayList<>();
        init(context, items, checkedItems, 0);
    }

    public OrderedChooser(Context context, CharSequence[] items, int[] checkedItems, int theme) {
        super(context, theme);
        this._observable = new DataSetObservable2();
        this._checkedItems = new ArrayList<>();
        init(context, items, checkedItems, theme);
    }

    @SuppressLint({"InflateParams"})
    private void init(Context context, CharSequence[] items, @Nullable int[] checkedItems, int theme) {
        this._items = items;
        this._itemSpans = new Object[items.length];
        View layout = getLayoutInflater().inflate(R.layout.ordered_chooser, (ViewGroup) null);
        this._header = (TextView) layout.findViewById(R.id.header);
        this._listView = (ListView) layout.findViewById(16908298);
        this._listView.setChoiceMode(2);
        this._listView.setAdapter((ListAdapter) this);
        this._listView.setOnItemClickListener(this);
        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.OrderedChooser, 0, theme);
        this._itemLayoutId = a.getResourceId(R.styleable.OrderedChooser_multiChoiceItemLayout, R.layout.select_dialog_multichoice_material);
        a.recycle();
        this._header.setMovementMethod(LinkMovementMethod.getInstance());
        if (checkedItems != null) {
            for (int pos : checkedItems) {
                this._listView.setItemChecked(pos, true);
                this._checkedItems.add(Integer.valueOf(pos));
            }
        }
        ArrayList<Object> sections = new ArrayList<>();
        char leadingCharacter = 0;
        for (CharSequence item : items) {
            char ch = item.charAt(0);
            if (ch != leadingCharacter) {
                leadingCharacter = ch;
                sections.add(Character.valueOf(ch));
            }
        }
        this._sections = sections.toArray(new Object[sections.size()]);
        setButton(-3, context.getString(R.string.clear), (DialogInterface.OnClickListener) null);
        setOnShowListener(this);
        updateHeaderText();
        setView(layout);
    }

    public void setEnableIndexer(boolean enable) {
        this._listView.setFastScrollEnabled(enable);
    }

    public Collection<Integer> getCheckedItems() {
        return this._checkedItems;
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Button button = getButton(-3);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        this._listView.clearChoices();
        int numItems = this._listView.getCount();
        for (int i = 0; i < numItems; i++) {
            this._listView.setItemChecked(i, false);
        }
        this._checkedItems.clear();
        updateHeaderText();
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        return this._sections;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int sectionIndex) {
        CharSequence[] charSequenceArr;
        char ch = ((Character) this._sections[sectionIndex]).charValue();
        int i = 0;
        for (CharSequence item : this._items) {
            if (item.charAt(0) == ch) {
                break;
            }
            i++;
        }
        return i;
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        Object[] objArr;
        char ch = this._items[position].charAt(0);
        int i = 0;
        for (Object section : this._sections) {
            if (((Character) section).charValue() == ch) {
                break;
            }
            i++;
        }
        return i;
    }

    @Override // android.widget.Adapter
    public void registerDataSetObserver(DataSetObserver observer) {
        this._observable.registerObserver(observer);
    }

    @Override // android.widget.Adapter
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this._observable.unregisterObserver(observer);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this._items.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return Integer.valueOf(position);
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
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = getLayoutInflater().inflate(this._itemLayoutId, parent, false);
        }
        CheckedTextView textView = (CheckedTextView) view.findViewById(16908308);
        textView.setText(this._items[position]);
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
        return this._items.length == 0;
    }

    @Override // android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return true;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this._listView.isItemChecked(position)) {
            if (!this._checkedItems.contains(Integer.valueOf(position))) {
                this._checkedItems.add(Integer.valueOf(position));
            }
        } else {
            this._checkedItems.remove(Integer.valueOf(position));
        }
        updateHeaderText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeaderText() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        boolean first = true;
        Iterator<Integer> it = this._checkedItems.iterator();
        while (it.hasNext()) {
            final Integer pos = it.next();
            if (first) {
                first = false;
            } else {
                sb.append((CharSequence) ", ");
            }
            int spanStart = sb.length();
            sb.append(this._items[pos.intValue()]);
            int spanEnd = sb.length();
            if (this._itemSpans[pos.intValue()] == null) {
                this._itemSpans[pos.intValue()] = new ClickableSpan() { // from class: com.mxtech.widget.OrderedChooser.1
                    @Override // android.text.style.ClickableSpan
                    public void onClick(View widget) {
                        OrderedChooser.this._listView.setItemChecked(pos.intValue(), false);
                        OrderedChooser.this._checkedItems.remove(pos);
                        OrderedChooser.this.updateHeaderText();
                    }
                };
            }
            sb.setSpan(this._itemSpans[pos.intValue()], spanStart, spanEnd, 33);
        }
        this._header.setText(sb);
    }
}
