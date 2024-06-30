package com.example.floatdragview.util;

import android.os.Bundle;

import java.io.Serializable;

public class DetailsPeriod implements Serializable {
    static final long serialVersionUID = 0;
    private long mBirth = -1;
    private long mDuration = 0;
    private long mSessionStart = -1;

    /* JADX WARN: Removed duplicated region for block: B:12:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0012  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static DetailsPeriod createOrRestore(Bundle bundle) {
        DetailsPeriod detailsPeriod;
        if (bundle != null) {
                detailsPeriod = (DetailsPeriod) bundle.getSerializable(DetailsPeriod.class.getName());
            return detailsPeriod;
        }else {
            return new DetailsPeriod();
        }

    }

    public String toString() {
        return "DetailsPeriod{mBirth=" + this.mBirth + ", mDuration=" + this.mDuration + ", mSessionStart=" + this.mSessionStart + '}';
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getLifetime() {
        return System.currentTimeMillis() - this.mBirth;
    }

    public void onStart() {
        long currentTimeMillis = System.currentTimeMillis();
        this.mSessionStart = currentTimeMillis;
        if (this.mBirth == -1) {
            this.mBirth = currentTimeMillis;
        }
    }

    public void onStop() {
        if (this.mSessionStart != -1) {
            this.mDuration += System.currentTimeMillis() - this.mSessionStart;
            this.mSessionStart = -1L;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        onStop();
        if (bundle != null) {
            bundle.putSerializable(DetailsPeriod.class.getName(), this);
        }
    }
}
