package com.mxtech.videoplayer.subtitle.service;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.subtitle.service.MoviePicker;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SubtitleUploader {
    private static final String TAG = App.TAG + ".SubtitleUploader";
    private static final int TYPE_CRITICAL_ERROR = 3;
    private static final int TYPE_ITEM_ERROR = 1;
    private static final int TYPE_NOERROR = 0;
    private static final int TYPE_SERVICE_ERROR = 2;
    private static final int kDelayFailure = 3000;
    private static final int kDelaySuccess = 2000;
    private final List<SubtitleServiceManager.UploadItem> _allItems;
    private final List<Media> _allMedia;
    private final Client _client;
    private final DialogPlatform _dialogPlatform;
    private boolean _finshed;
    private List<SubtitleServiceManager.UploadItem> _items;
    private Media _media;
    private int _mediaIndex = -1;
    private MovieSearcher _movieSearcher;
    private SubtitleService _service;
    private int _serviceIndex;
    private final List<SubtitleService> _services;
    private AsyncTask2<Void, Object, Void> _uploadTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface Client {
        void onUploadingCompleted(SubtitleUploader subtitleUploader);

        void setProgressMessage(SubtitleUploader subtitleUploader, CharSequence charSequence);

        void showToast(SubtitleUploader subtitleUploader, CharSequence charSequence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SubtitleUploader(DialogPlatform dialogPlatform, SubtitleService[] services, List<SubtitleServiceManager.UploadItem> items, Client listener) {
        this._dialogPlatform = dialogPlatform;
        this._services = new ArrayList(Arrays.asList(services));
        this._allItems = items;
        this._client = listener;
        this._serviceIndex = services.length - 1;
        TreeSet<Media> uniqueMedias = new TreeSet<>();
        for (SubtitleServiceManager.UploadItem item : items) {
            uniqueMedias.add(item.sub.media);
        }
        this._allMedia = new ArrayList(uniqueMedias);
        next();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        if (this._movieSearcher != null) {
            this._movieSearcher.cancel();
        }
        if (this._uploadTask != null) {
            this._uploadTask.cancel(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean next() {
        int numServices = this._services.size();
        int i = this._serviceIndex + 1;
        this._serviceIndex = i;
        if (i >= numServices) {
            if (numServices != 0) {
                int i2 = this._mediaIndex + 1;
                this._mediaIndex = i2;
                if (i2 < this._allMedia.size()) {
                    this._media = this._allMedia.get(this._mediaIndex);
                    this._items = getItems(this._media);
                    this._serviceIndex = 0;
                }
            }
            finish();
            return false;
        }
        this._service = this._services.get(this._serviceIndex);
        new MovieSearcher();
        return true;
    }

    private boolean reloadMedia() {
        if (this._mediaIndex >= this._allMedia.size()) {
            finish();
            return false;
        }
        this._media = this._allMedia.get(this._mediaIndex);
        this._items = getItems(this._media);
        return true;
    }

    private List<SubtitleServiceManager.UploadItem> getItems(Media media) {
        List<SubtitleServiceManager.UploadItem> items = new ArrayList<>();
        for (SubtitleServiceManager.UploadItem item : this._allItems) {
            if (item.sub.media.equals(media)) {
                items.add(item);
            }
        }
        return items;
    }

    private boolean reloadService() {
        if (this._serviceIndex >= this._services.size()) {
            finish();
            return false;
        }
        this._service = this._services.get(this._serviceIndex);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadFor(final long movieId) {
        if (this._items.size() > 0) {
            new AsyncTask2<Void, Object, Void>() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleUploader.1
                @Override // android.os.AsyncTask
                protected void onPreExecute() {
                    SubtitleUploader.this._uploadTask = this;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... params) {
                    try {
                        String serviceName = SubtitleUploader.this._service.name();
                        String done = App.context.getString(R.string.abc_action_mode_done);
                        for (SubtitleServiceManager.UploadItem item : SubtitleUploader.this._items) {
                            if (!isCancelled()) {
                                CharSequence desc = StringUtils.getStringItalic_s(R.string.uploading_progress, item.filename, serviceName);
                                publishProgress(new Object[]{desc});
                                try {
                                } catch (SubtitleService.SubtitleServiceException e) {
                                    Log.w(SubtitleUploader.TAG, "", e);
                                    publishProgress(new Object[]{TextUtils.concat(desc, " ", L.getErrorColoredText(SubtitleServiceManager.getErrorMessage(e, serviceName, SubtitleUploader.this._media.fileName, item.filename)))});
                                    SystemClock.sleep(3000L);
                                    int type = SubtitleUploader.getExceptionType(e);
                                    if (type == 2 || type == 3) {
                                        return null;
                                    }
                                }
                                if (SubtitleUploader.this._items.size() <= 1 || !SubtitleUploader.this._service.exist(item.sub.media, item.sub.subtitle)) {
                                    SubtitleUploader.this._service.upload(movieId, item.sub.media, item.sub.subtitle, item.locale);
                                    publishProgress(new Object[]{TextUtils.concat(desc, " - ", done)});
                                    SystemClock.sleep(2000L);
                                } else {
                                    throw new SubtitleService.SubtitleAlreadyExistException();
                                    break;
                                }
                            } else {
                                return null;
                            }
                        }
                        return null;
                    } catch (Exception e2) {
                        Log.w(SubtitleUploader.TAG, "", e2);
                        return null;
                    }
                }

                @Override // android.os.AsyncTask
                protected void onProgressUpdate(Object... values) {
                    SubtitleUploader.this._client.setProgressMessage(SubtitleUploader.this, (CharSequence) values[0]);
                }

                @Override // android.os.AsyncTask
                protected void onCancelled() {
                    SubtitleUploader.this._uploadTask = null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Void result) {
                    SubtitleUploader.this._uploadTask = null;
                    SubtitleUploader.this.next();
                }
            }.executeParallel(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MovieSearcher extends AsyncTask2<Void, CharSequence, Object> implements MoviePicker.Listener {
        @Nullable
        private MoviePicker _picker;

        MovieSearcher() {
            SubtitleUploader.this._movieSearcher = this;
            executeParallel(new Void[0]);
        }

        void cancel() {
            super.cancel(true);
            if (this._picker != null) {
                this._picker.cancel();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Object doInBackground(Void... params) {
            SubtitleServiceManager.UploadItem singleItem = null;
            CharSequence desc = SubtitleUploader.this._dialogPlatform.getContext().getString(R.string.retrieving_movie_info);
            SubtitleUploader.this._client.setProgressMessage(SubtitleUploader.this, desc);
            try {
                if (SubtitleUploader.this._items.size() == 1) {
                    SubtitleServiceManager.UploadItem singleItem2 = (SubtitleServiceManager.UploadItem) SubtitleUploader.this._items.get(0);
                    if (SubtitleUploader.this._service.exist(SubtitleUploader.this._media, singleItem2.sub.subtitle)) {
                        throw new SubtitleService.SubtitleAlreadyExistException();
                    }
                }
                return SubtitleUploader.this._service.searchMovies(SubtitleUploader.this._media);
            } catch (SubtitleService.SubtitleServiceException e) {
                Log.w(SubtitleUploader.TAG, "", e);
                CharSequence[] charSequenceArr = new CharSequence[1];
                CharSequence[] charSequenceArr2 = new CharSequence[3];
                charSequenceArr2[0] = desc;
                charSequenceArr2[1] = " ";
                charSequenceArr2[2] = L.getErrorColoredText(SubtitleServiceManager.getErrorMessage(e, SubtitleUploader.this._service.name(), SubtitleUploader.this._media.fileName, 0 != 0 ? singleItem.filename : null));
                charSequenceArr[0] = TextUtils.concat(charSequenceArr2);
                publishProgress(charSequenceArr);
                SystemClock.sleep(3000L);
                return e;
            } catch (Exception e2) {
                Log.w(SubtitleUploader.TAG, "", e2);
                return e2;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(CharSequence... values) {
            SubtitleUploader.this._client.setProgressMessage(SubtitleUploader.this, values[0]);
        }

        @Override // android.os.AsyncTask
        protected void onPostExecute(Object result) {
            if (SubtitleUploader.this._movieSearcher == this) {
                SubtitleUploader.this._movieSearcher = null;
                if (result instanceof List) {
                    if (!SubtitleUploader.this._dialogPlatform.isFinishing()) {
                        this._picker = new MoviePicker(SubtitleUploader.this._service, SubtitleUploader.this._dialogPlatform, SubtitleUploader.this._media, (List) result, this);
                    }
                } else if (result instanceof SubtitleService.SubtitleServiceException) {
                    SubtitleUploader.this.onException((SubtitleService.SubtitleServiceException) result);
                } else {
                    SubtitleUploader.this.finish();
                }
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            if (SubtitleUploader.this._movieSearcher == this) {
                SubtitleUploader.this._movieSearcher = null;
                SubtitleUploader.this.finish();
            }
        }

        @Override // com.mxtech.videoplayer.subtitle.service.MoviePicker.Listener
        public void onTitleSelected(MoviePicker moviePicker, long movieId, String title, int year, int season, int episode) {
            SubtitleUploader.this.uploadFor(movieId);
        }

        @Override // com.mxtech.videoplayer.subtitle.service.MoviePicker.Listener
        public void onCancelled(MoviePicker moviePicker) {
            SubtitleUploader.this.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getExceptionType(SubtitleService.SubtitleServiceException e) {
        if (e instanceof SubtitleService.SubtitleAlreadyExistException) {
            return 0;
        }
        if (e instanceof SubtitleService.LocalException) {
            return 1;
        }
        if (e instanceof SubtitleService.ServerException) {
            return 2;
        }
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onException(SubtitleService.SubtitleServiceException e) {
        int type = getExceptionType(e);
        switch (type) {
            case 0:
                next();
                return;
            case 1:
                this._allMedia.remove(this._mediaIndex);
                reloadMedia();
                return;
            case 2:
                this._services.remove(this._serviceIndex);
                reloadService();
                return;
            case 3:
                finish();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish() {
        if (!this._finshed) {
            this._finshed = true;
            this._client.onUploadingCompleted(this);
        }
    }
}
