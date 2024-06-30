package com.mxtech.widget;

import com.rengwuxian.materialedittext.validation.METValidator;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class RegexpMETValidator extends METValidator {
    private final Pattern _pattern;

    public RegexpMETValidator(String errorMessage, Pattern pattern) {
        super(errorMessage);
        this._pattern = pattern;
    }

    @Override // com.rengwuxian.materialedittext.validation.METValidator
    public boolean isValid(CharSequence text, boolean isEmpty) {
        return this._pattern.matcher(text).matches();
    }
}
