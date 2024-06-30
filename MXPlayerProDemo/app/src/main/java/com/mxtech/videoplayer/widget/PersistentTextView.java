package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.AttributeSet;
import android.util.Log;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.AsyncSimpleAdapter;
import com.mxtech.widget.FilterTextProvider;
import com.mxtech.widget.MXAutoCompleteTextView;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class PersistentTextView extends MXAutoCompleteTextView implements FilterTextProvider {
    private static final int DEFAULT_MAX_CANDIDATES = 20;
    private static final String TAG = App.TAG + ".PersistentTextView";
    private AsyncSimpleAdapter _adapter;
    private boolean _autoSave;
    private boolean _dirty;
    private int _maxCandidates;
    private String _tableName;
    private int _threshold;
    private AsyncWriter _writingTask;

    public PersistentTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PersistentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PersistentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PersistentTextView, defStyleAttr, 0);
        this._tableName = a.getString(R.styleable.PersistentTextView_table);
        this._maxCandidates = a.getInt(R.styleable.PersistentTextView_maxCandidates, 20);
        setThreshold(a.getInt(R.styleable.PersistentTextView_completionThreshold, 2));
        if (a.getBoolean(R.styleable.PersistentTextView_autoSave, false)) {
            setAutoSave(true);
        }
        if (a.getBoolean(R.styleable.PersistentTextView_restoreLast, false)) {
            restoreLastText();
        }
        a.recycle();
        this._adapter = new AsyncSimpleAdapter(context, this, 17367050, "Input", 16908308);
        setAdapter(this._adapter);
    }

    public final void setTableName(String tableName) {
        this._tableName = tableName;
    }

    public final void setMaxCandidates(int maxCandidates) {
        if (maxCandidates != this._maxCandidates) {
            this._maxCandidates = maxCandidates;
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public final int getThreshold() {
        return this._threshold;
    }

    @Override // android.widget.AutoCompleteTextView
    public final void setThreshold(int threshold) {
        if (threshold < 0) {
            this._threshold = 0;
        } else {
            this._threshold = threshold;
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public boolean enoughToFilter() {
        return getText().length() >= this._threshold;
    }

    public String[] runQuery(CharSequence constraint) {
        MediaDatabase mdb;
        Cursor c;
        ArrayList<String> texts = new ArrayList<>();
        try {
            mdb = MediaDatabase.getInstance();
            c = mdb.querySimpleLog(this._tableName, "Input", constraint, this._maxCandidates);
            try {
            } finally {
                c.close();
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "", e);
        }
        if (c.moveToFirst()) {
            do {
                String text = c.getString(0);
                if (!texts.contains(text)) {
                    texts.add(text);
                }
            } while (c.moveToNext());
            mdb.release();
            return (String[]) texts.toArray(new String[texts.size()]);
        }
        mdb.release();
        return (String[]) texts.toArray(new String[texts.size()]);
    }

    public void restoreLastText() {
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            setText(mdb.getLastAccessedInput(this._tableName));
            mdb.release();
        } catch (SQLiteException e) {
            Log.e(TAG, "", e);
        }
    }

    public final void setAutoSave(boolean enable) {
        this._autoSave = enable;
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        this._dirty = true;
    }

    public void save() {
        if (this._dirty) {
            String text = getText().toString();
            if (text.length() != 0) {
                if (this._writingTask != null) {
                    if (!text.equals(this._writingTask.text)) {
                        this._writingTask.cancel(true);
                    } else {
                        return;
                    }
                }
                this._writingTask = new AsyncWriter(this._tableName, text);
                this._writingTask.executeParallel(new Void[0]);
            }
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this._dirty && this._autoSave) {
            save();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AsyncWriter extends AsyncTask2<Void, Void, Boolean> {
        final String tableName;
        final String text;

        AsyncWriter(String tableName, String text) {
            this.tableName = tableName;
            this.text = text;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                mdb.writeSimpleLog(this.tableName, this.text, null);
                mdb.release();
            } catch (Exception e) {
                Log.e(PersistentTextView.TAG, "", e);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (PersistentTextView.this._writingTask == this) {
                PersistentTextView.this._writingTask = null;
                if (result != null && result.booleanValue() && this.text.equals(PersistentTextView.this.getText().toString())) {
                    PersistentTextView.this._dirty = false;
                }
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            if (PersistentTextView.this._writingTask == this) {
                PersistentTextView.this._writingTask = null;
            }
        }
    }
}
