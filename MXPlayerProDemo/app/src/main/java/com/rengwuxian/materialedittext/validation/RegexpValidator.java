package com.rengwuxian.materialedittext.validation;

import android.support.annotation.NonNull;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class RegexpValidator extends METValidator {
    private Pattern pattern;

    public RegexpValidator(@NonNull String errorMessage, @NonNull String regex) {
        super(errorMessage);
        this.pattern = Pattern.compile(regex);
    }

    @Override // com.rengwuxian.materialedittext.validation.METValidator
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return this.pattern.matcher(text).matches();
    }
}
