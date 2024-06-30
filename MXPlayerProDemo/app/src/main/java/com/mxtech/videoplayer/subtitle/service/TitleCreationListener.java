package com.mxtech.videoplayer.subtitle.service;

import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public interface TitleCreationListener {
    void onNewTitle(MovieCandidate movieCandidate);

    void onNewTitles(List<MovieCandidate> list);
}
