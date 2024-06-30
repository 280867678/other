package com.mxtech.subtitle;

import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import com.mxtech.StringUtils;

/* loaded from: classes2.dex */
public final class PolishStylizer {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ItalicSpan extends StyleSpan {
        public ItalicSpan() {
            super(2);
        }
    }

    public static SpannableStringBuilder stylize(SpannableStringBuilder b) {
        onNewLine(b, 0);
        int lineStart = 0;
        while (true) {
            int lineStart2 = StringUtils.indexOf(b, 10, lineStart);
            if (lineStart2 >= 0) {
                lineStart = lineStart2 + 1;
                onNewLine(b, lineStart);
            } else {
                return b;
            }
        }
    }

    private static void onNewLine(SpannableStringBuilder b, int lineStart) {
        ItalicSpan[] italicSpanArr;
        if (lineStart < b.length()) {
            if (b.charAt(lineStart) == '/') {
                b.delete(lineStart, lineStart + 1);
                ItalicSpan[] spans = (ItalicSpan[]) b.getSpans(lineStart, lineStart, ItalicSpan.class);
                if (spans.length == 0) {
                    b.setSpan(new ItalicSpan(), lineStart, b.length(), 33);
                    return;
                }
                return;
            }
            for (ItalicSpan span : (ItalicSpan[]) b.getSpans(lineStart, lineStart, ItalicSpan.class)) {
                b.setSpan(span, b.getSpanStart(span), lineStart, 33);
            }
        }
    }
}
