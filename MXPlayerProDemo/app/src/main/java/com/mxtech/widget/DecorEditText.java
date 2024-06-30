package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.videoplayer.pro.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class DecorEditText extends MaterialEditText {
    private boolean _mandatory;
    private Map<Integer, String> _messageMap;

    public DecorEditText(Context context) {
        super(context);
    }

    public DecorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DecorEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    public final void setConstraint(int minLength, int maxLength) {
        setConstraint(minLength, maxLength, (Pattern) null, (String) null);
    }

    public final void setConstraint(int minLength, int maxLength, @Nullable String pattern, int patternFailMessageId) {
        setConstraint(minLength, maxLength, pattern != null ? Pattern.compile(pattern) : null, pattern != null ? getContext().getString(patternFailMessageId) : null);
    }

    public final void setConstraint(int minLength, int maxLength, @Nullable String pattern, @Nullable String patternFailMessage) {
        setConstraint(minLength, maxLength, pattern != null ? Pattern.compile(pattern) : null, patternFailMessage);
    }

    public void setConstraint(int minLength, int maxLength, @Nullable Pattern pattern, @Nullable String patternFailMessage) {
        if (minLength > 0) {
            this._mandatory = true;
            if (minLength > 1) {
                setMinCharacters(minLength);
            }
        }
        if (maxLength < Integer.MAX_VALUE) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            setMaxCharacters(maxLength);
        }
        if (pattern != null) {
            addValidator(new RegexpMETValidator(patternFailMessage, pattern));
        }
    }

    @SuppressLint({"UseSparseArrays"})
    public final void setMessage(int id, String text) {
        if (this._messageMap == null) {
            this._messageMap = new HashMap();
        }
    }

    public final String getMessage(int id) {
        String message;
        return (this._messageMap == null || (message = this._messageMap.get(Integer.valueOf(id))) == null) ? getContext().getString(id) : message;
    }

    public final String getMessage(int id, Object... args) {
        String format = getMessage(id);
        try {
            return String.format(format, args);
        } catch (Exception e) {
            Log.w(MXApplication.TAG, "", e);
            return format;
        }
    }

    public final void setError(int resId) {
        super.setError(getContext().getString(resId));
    }

    public final void trim() {
        ViewUtils.trimTextView(this);
    }

    @Override // com.rengwuxian.materialedittext.MaterialEditText
    public boolean validate() {
        int len = length();
        if (this._mandatory && len == 0) {
            setError(getMessage(R.string.field_empty));
            return false;
        }
        int minChars = getMinCharacters();
        if (len < minChars) {
            setError(getMessage(R.string.field_too_short, StringUtils.getQuantityString_s(R.plurals.count_letters, minChars, Integer.valueOf(minChars))));
            return false;
        }
        return super.validate();
    }
}
