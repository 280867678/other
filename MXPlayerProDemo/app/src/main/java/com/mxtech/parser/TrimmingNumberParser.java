package com.mxtech.parser;

/* loaded from: classes2.dex */
public final class TrimmingNumberParser implements IParser {
    @Override // com.mxtech.parser.IParser
    public final int parseToInt(String source) throws NumberFormatException {
        return Integer.parseInt(source.trim());
    }
}
