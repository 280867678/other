package com.carrydream.cardrecorder.tool;

import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public interface OnOcr {
    void NewRound(HashMap<String, Integer> hashMap);

    void last(List<List<String>> list);

    void next(List<List<String>> list);

    void output(HashMap<String, Integer> hashMap);
}
