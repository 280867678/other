package com.mxtech.os;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/* loaded from: classes.dex */
public abstract class AsyncTask2<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    @SafeVarargs
    @SuppressLint({"NewApi"})
    public final AsyncTask<Params, Progress, Result> executeParallel(Params... params) {
        return Build.VERSION.SDK_INT < 11 ? execute(params) : executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
}
