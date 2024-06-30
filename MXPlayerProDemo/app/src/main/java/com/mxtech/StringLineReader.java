package com.mxtech;

/* loaded from: classes2.dex */
public final class StringLineReader {
    private final int _end;
    private int _pos;
    private final String _source;

    public StringLineReader(String source) {
        this(source, 0, source.length());
    }

    public StringLineReader(String source, int start) {
        this(source, start, source.length());
    }

    public StringLineReader(String source, int start, int end) {
        this._source = source;
        this._pos = start;
        this._end = end;
    }

    public final String readLine() {
        if (this._pos == this._end) {
            return null;
        }
        for (int charPos = this._pos; charPos < this._end; charPos++) {
            char ch = this._source.charAt(charPos);
            if (ch == '\r') {
                String substring = this._source.substring(this._pos, charPos);
                this._pos = charPos + 1;
                if (this._pos < this._end && this._source.charAt(this._pos) == '\n') {
                    this._pos++;
                    return substring;
                }
                return substring;
            } else if (ch == '\n') {
                String substring2 = this._source.substring(this._pos, charPos);
                this._pos = charPos + 1;
                return substring2;
            }
        }
        String substring3 = this._source.substring(this._pos, this._end);
        this._pos = this._end;
        return substring3;
    }

    public final int nextPosition() {
        return this._pos;
    }
}
