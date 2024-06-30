package com.mxtech.videoplayer.list;

import android.net.Uri;
import com.mxtech.media.service.IFFService;
import com.mxtech.videoplayer.MediaLoader;

/* loaded from: classes.dex */
public interface IListContainer extends MediaLoader.Client {
    @Override // com.mxtech.videoplayer.MediaLoader.Client
    IFFService getFFService(IFFService iFFService);

    void openUri(Uri uri);

    void rebuildAsync();

    void rebuildDelayed(int i);

    @Override // com.mxtech.videoplayer.MediaLoader.Client
    void releaseFFService(IFFService iFFService);

    void scheduleToRunOnServiceConnection(Runnable runnable);

    void setStatusText(CharSequence charSequence);
}
