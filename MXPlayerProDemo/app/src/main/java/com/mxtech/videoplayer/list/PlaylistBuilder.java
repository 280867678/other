package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.mxtech.media.MediaUtils;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import java.io.File;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class PlaylistBuilder extends Builder {
    public static final String TAG = "MX.List.Builder/PL";
    private AsyncTask<Void, Void, String> _asyncReadTask;
    private String _text;
    final File file;
    final Uri uri;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaylistBuilder(File file, ActivityMediaList context, MediaListFragment content) {
        super(context, content, 1024);
        this.uri = Uri.fromFile(file);
        this.file = file;
        Log.e(TAG, "WRONG INCOMING " + file);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaylistBuilder(Uri uri, ActivityMediaList context, MediaListFragment content) {
        super(context, content, 1024);
        this.uri = uri;
        this.file = null;
        Log.e(TAG, "WRONG INCOMING " + uri);
    }

    @Override // com.mxtech.videoplayer.list.Builder
    Uri uri() {
        return this.uri;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public String title() {
        return MediaUtils.capitalizeWithDictionary(L.getDisplayName(this.file.getName()), this.content.helper.titleSb);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public CharSequence getMessage(int messageType) {
        if (messageType == 1) {
            return this.context.getString(R.string.play_list_empty);
        }
        if (messageType == 2) {
            return this.context.getString(R.string.play_list_failure);
        }
        return super.getMessage(messageType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public boolean isItemComplex() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public boolean prepareBuild() {
        if (this._asyncReadTask == null) {
            this._asyncReadTask = new AsyncTask2<Void, Void, String>() { // from class: com.mxtech.videoplayer.list.PlaylistBuilder.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public String doInBackground(Void... params) {
                    return null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(String text) {
                    if (PlaylistBuilder.this._asyncReadTask == this) {
                        PlaylistBuilder.this._text = text;
                        PlaylistBuilder.this.content.onBuildPrepared(PlaylistBuilder.this, PlaylistBuilder.this._text != null);
                    }
                }
            }.executeParallel(new Void[0]);
        }
        return super.prepareBuild();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public Entry[] build() {
        return new Entry[0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public void stop() {
        if (this._asyncReadTask != null) {
            this._asyncReadTask.cancel(true);
            this._asyncReadTask = null;
        }
        super.stop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public void deleteUi(Entry[] entries) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public void renameUi(Entry entry) {
    }
}
