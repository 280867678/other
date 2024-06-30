package com.mxtech.database;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

/* loaded from: classes2.dex */
public class DataSetObservable2 extends DataSetObservable {
    private static final String TAG = "MX.DataSetObservable2";

    @Override // android.database.Observable
    public void registerObserver(DataSetObserver observer) {
        synchronized (this.mObservers) {
            if (this.mObservers.contains(observer)) {
                Log.w(TAG, observer.toString() + " is already registered.");
            } else {
                super.registerObserver((DataSetObservable2) observer);
            }
        }
    }

    @Override // android.database.Observable
    public void unregisterObserver(DataSetObserver observer) {
        synchronized (this.mObservers) {
            if (!this.mObservers.contains(observer)) {
                Log.w(TAG, observer.toString() + " is not yet registered.");
            } else {
                super.unregisterObserver((DataSetObservable2) observer);
            }
        }
    }
}
