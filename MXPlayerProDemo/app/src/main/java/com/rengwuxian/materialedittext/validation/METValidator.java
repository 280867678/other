package com.rengwuxian.materialedittext.validation;

import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public abstract class METValidator {
    protected String errorMessage;

    public abstract boolean isValid(@NonNull CharSequence charSequence, boolean z);

    public METValidator(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @NonNull
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
