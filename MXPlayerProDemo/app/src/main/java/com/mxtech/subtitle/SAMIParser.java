package com.mxtech.subtitle;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlrpc.android.IXMLRPCSerializer;

/* loaded from: classes2.dex */
public class SAMIParser {
    private static final String TAG = ISubtitle.TAG + ".SAMI";
    private final String _defaultName;
    private int _index;
    private final Uri _source;
    private final List<SAMISubtitle> _tracks = new ArrayList();

    public native void parse(String str);

    public static ISubtitle[] create(Uri uri, String type, String name, String text, ISubtitleClient client) {
        SAMIParser parser = new SAMIParser(uri, name);
        parser.parse(text);
        if (parser._tracks.size() == 0) {
            return null;
        }
        Iterator<SAMISubtitle> it = parser._tracks.iterator();
        while (it.hasNext()) {
            SAMISubtitle sub = it.next();
            if (sub.isEmpty()) {
                it.remove();
            }
        }
        return (ISubtitle[]) parser._tracks.toArray(new ISubtitle[parser._tracks.size()]);
    }

    private SAMIParser(Uri source, String defaultName) {
        this._source = source;
        this._defaultName = defaultName;
    }

    private void parseCSS(String source) {
        Pattern outerPattern = Pattern.compile("\\.([-_A-Za-z]+)\\s*\\{([^}]+)\\}");
        Pattern innerPattern = Pattern.compile("([-_A-Za-z]+)\\s*\\:\\s*([^; \\t\\n]+)");
        Matcher outerMatch = outerPattern.matcher(source);
        while (outerMatch.find()) {
            String name = null;
            String locale = null;
            Matcher innerMatch = innerPattern.matcher(outerMatch.group(2));
            while (innerMatch.find()) {
                String attrbName = innerMatch.group(1);
                if (attrbName.equalsIgnoreCase(IXMLRPCSerializer.TAG_NAME)) {
                    name = new String(innerMatch.group(2));
                } else if (attrbName.equalsIgnoreCase("lang")) {
                    locale = new String(innerMatch.group(2));
                }
            }
            if (name == null) {
                name = this._defaultName;
            }
            String css = new String(outerMatch.group(1));
            List<SAMISubtitle> list = this._tracks;
            int i = this._index;
            this._index = i + 1;
            list.add(new SAMISubtitle(i, this._source, name, locale, css));
        }
    }

    private void append(String css, int begin, String text) {
        SAMISubtitle track = null;
        Iterator<SAMISubtitle> it = this._tracks.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SAMISubtitle s = it.next();
            if (css.equalsIgnoreCase(s.clazz)) {
                track = s;
                break;
            }
        }
        if (track == null) {
            int i = this._index;
            this._index = i + 1;
            track = new SAMISubtitle(i, this._source, this._defaultName, null, css);
            this._tracks.add(track);
        }
        track.append(begin, text);
    }
}
