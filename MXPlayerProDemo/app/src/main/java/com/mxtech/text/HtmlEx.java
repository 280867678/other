package com.mxtech.text;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import org.xml.sax.XMLReader;

/* loaded from: classes2.dex */
public final class HtmlEx {
    public static final int RUBY_REGULAR = 0;
    public static final int RUBY_SIDE_BY_SIDE = 1;
    private static final Handler _handler = new Handler();

    public static CharSequence fromHtml(String source, int rubyHandling) {
        _handler.rubyHandling = rubyHandling;
        return complete(Html2.fromHtml(source, null, _handler));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Ruby {
        private Ruby() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RT {
        private RT() {
        }
    }

    /* loaded from: classes2.dex */
    private static class Handler implements Html.TagHandler {
        int rubyHandling;

        private Handler() {
        }

        @Override // android.text.Html.TagHandler
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            int i = 0;
            if ("ruby".equalsIgnoreCase(tag)) {
                if (this.rubyHandling == 0) {
                    if (opening) {
                        output.setSpan(new Ruby(), output.length(), output.length(), 18);
                        return;
                    }
                    Ruby[] rubyArr = (Ruby[]) output.getSpans(output.length(), output.length(), Ruby.class);
                    int length = rubyArr.length;
                    while (i < length) {
                        Ruby ruby = rubyArr[i];
                        output.setSpan(ruby, output.getSpanStart(ruby), output.getSpanEnd(ruby), 33);
                        i++;
                    }
                }
            } else if ("rt".equalsIgnoreCase(tag)) {
                if (this.rubyHandling == 0) {
                    if (opening) {
                        output.setSpan(new RT(), output.length(), output.length(), 18);
                        return;
                    }
                    RT[] rtArr = (RT[]) output.getSpans(output.length(), output.length(), RT.class);
                    int length2 = rtArr.length;
                    while (i < length2) {
                        RT rt = rtArr[i];
                        output.setSpan(rt, output.getSpanStart(rt), output.getSpanEnd(rt), 33);
                        i++;
                    }
                } else if (opening) {
                    output.append('(');
                } else {
                    output.append(')');
                }
            }
        }
    }

    private static CharSequence complete(CharSequence output) {
        Editable s;
        RT[] rtArr;
        if ((output instanceof Spanned) && ((Spanned) output).nextSpanTransition(0, 1, Ruby.class) >= 0) {
            if (output instanceof Editable) {
                s = (Editable) output;
            } else {
                s = new SpannableStringBuilder(output);
            }
            for (RT rt : (RT[]) s.getSpans(0, s.length(), RT.class)) {
                int rtStart = s.getSpanStart(rt);
                int rtEnd = s.getSpanEnd(rt);
                Ruby[] rubies = (Ruby[]) s.getSpans(rtStart, rtStart, Ruby.class);
                if (rubies.length == 1) {
                    int rubyStart = s.getSpanStart(rubies[0]);
                    int rubyEnd = s.getSpanEnd(rubies[0]);
                    if (rubyEnd >= rtEnd) {
                        s.removeSpan(rt);
                        s.removeSpan(rubies[0]);
                        RubySpan rubySpan = new RubySpan(new SpannableStringBuilder(s, rtStart, rtEnd));
                        s.delete(rtStart, rtEnd);
                        s.setSpan(rubySpan, rubyStart, rubyEnd - (rtEnd - rtStart), 33);
                    }
                }
            }
            return s;
        }
        return output;
    }
}
