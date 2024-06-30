package com.mxtech.videoplayer.subtitle.service;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import com.mxtech.os.AsyncTask2;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class SubtitleFinder extends Handler {
    private static final int MAX_DOWNLOADER_IDLE_TIME = 10000;
    private static final int MSG_DOWNLOAD_END = 2;
    private static final int MSG_DOWNLOAD_RESULT = 1;
    private static final int MSG_SHUTDOWN_DOWNLOADER = 0;
    private static final String TAG = App.TAG + ".SubtitleFinder";
    private Downloader _downloader;
    private IListener _listener;
    private final ArrayList<Searcher> _searchers = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface IListener {
        void onSubtitleDownloadEnded(SubtitleFinder subtitleFinder);

        void onSubtitleDownloadFailed(SubtitleFinder subtitleFinder, String str, File file, SubtitleEntry subtitleEntry, SubtitleService.SubtitleServiceException subtitleServiceException);

        void onSubtitleDownloaded(SubtitleFinder subtitleFinder, String str, File file, SubtitleEntry subtitleEntry);

        void onSubtitleSearchFailed(SubtitleFinder subtitleFinder, String str, SubtitleService.SubtitleServiceException subtitleServiceException);

        void onSubtitleSearched(SubtitleFinder subtitleFinder, String str, @Nullable SubtitleEntry[] subtitleEntryArr);
    }

    public SubtitleFinder() {
    }

    public SubtitleFinder(IListener listener) {
        this._listener = listener;
    }

    public final void setListener(IListener listener) {
        this._listener = listener;
    }

    public void search(Media[] medias, String[] sites, Locale[] locales, String queryText) {
        for (String site : sites) {
            new Searcher(site, locales, queryText).executeParallel(medias);
        }
    }

    public void download(File file, SubtitleEntry entry) {
        if (this._downloader == null) {
            this._downloader = new Downloader();
        } else {
            removeMessages(0);
        }
        this._downloader.download(file, entry);
    }

    public void cancel() {
        Iterator<Searcher> it = this._searchers.iterator();
        while (it.hasNext()) {
            Searcher searcher = it.next();
            searcher.cancel(true);
        }
        if (this._downloader != null) {
            this._downloader.interrupt();
            this._downloader = null;
        }
    }

    public final int getSearchesRunning() {
        return this._searchers.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SearchResult {
        @Nullable
        final SubtitleEntry[] entries;
        @Nullable
        final SubtitleService.SubtitleServiceException exception;

        SearchResult(SubtitleEntry[] entries) {
            this.entries = entries;
            this.exception = null;
        }

        SearchResult(SubtitleService.SubtitleServiceException exception) {
            this.entries = null;
            this.exception = exception;
        }
    }

    /* loaded from: classes2.dex */
    private class Searcher extends AsyncTask2<Media, Void, SearchResult> {
        private final Locale[] _locales;
        private final String _queryText;
        private final String _serviceName;

        Searcher(String serviceName, Locale[] locales, String queryText) {
            this._serviceName = serviceName;
            this._locales = locales;
            this._queryText = queryText;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            SubtitleFinder.this._searchers.add(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public SearchResult doInBackground(Media... params) {
            try {
                return new SearchResult(SubtitleFinder.this.newService(this._serviceName).search(params, this._locales, this._queryText));
            } catch (SubtitleService.SubtitleServiceException e) {
                return new SearchResult(e);
            } catch (Exception e2) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(SearchResult result) {
            if (SubtitleFinder.this._searchers.remove(this) && result != null) {
                if (result.exception != null) {
                    SubtitleFinder.this._listener.onSubtitleSearchFailed(SubtitleFinder.this, this._serviceName, result.exception);
                } else {
                    SubtitleFinder.this._listener.onSubtitleSearched(SubtitleFinder.this, this._serviceName, result.entries);
                }
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            SubtitleFinder.this._searchers.remove(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DownloadRequest {
        final Downloader downloader;
        final SubtitleEntry entry;
        final File file;

        DownloadRequest(Downloader downloader, File file, SubtitleEntry entry) {
            this.downloader = downloader;
            this.file = file;
            this.entry = entry;
        }
    }

    /* loaded from: classes2.dex */
    private class DownloadResult extends DownloadRequest {
        final SubtitleService.SubtitleServiceException exception;

        DownloadResult(SubtitleFinder subtitleFinder, DownloadRequest req) {
            this(req, null);
        }

        DownloadResult(DownloadRequest req, SubtitleService.SubtitleServiceException exception) {
            super(req.downloader, req.file, req.entry);
            this.exception = exception;
        }
    }

    /* loaded from: classes2.dex */
    private class Downloader extends Thread {
        private final ArrayList<DownloadRequest> _requests;

        Downloader() {
            super("SubtitleDownloader$Downloader");
            this._requests = new ArrayList<>();
            start();
        }

        void download(File file, SubtitleEntry entry) {
            DownloadRequest req = new DownloadRequest(this, file, entry);
            synchronized (this) {
                this._requests.add(req);
                notify();
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            DownloadRequest req;
            try {
                TreeMap<String, SubtitleService> services = new TreeMap<>();
                while (true) {
                    synchronized (this) {
                        while (this._requests.size() <= 0) {
                            SubtitleFinder.this.sendMessage(SubtitleFinder.this.obtainMessage(2, this));
                            wait();
                        }
                        req = this._requests.remove(0);
                    }
                    SubtitleService service = services.get(req.entry.service);
                    if (service == null) {
                        service = SubtitleFinder.this.newService(req.entry.service);
                        services.put(req.entry.service, service);
                    }
                    try {
                        service.get(req.file, req.entry.tag);
                        SubtitleFinder.this.sendMessage(SubtitleFinder.this.obtainMessage(1, new DownloadResult(SubtitleFinder.this, req)));
                    } catch (SubtitleService.SubtitleServiceException e) {
                        Log.e(SubtitleFinder.TAG, "", e);
                        SubtitleFinder.this.sendMessage(SubtitleFinder.this.obtainMessage(1, new DownloadResult(req, e)));
                    }
                }
            } catch (InterruptedException e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubtitleService newService(String name) {
        switch (name.hashCode()) {
            case 1781894087:
                if (name.equals("opensubtitles.org")) {
                }
                break;
        }
        return new OpenSubtitles();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                if (msg.obj == this._downloader) {
                    this._downloader.interrupt();
                    this._downloader = null;
                    return;
                }
                return;
            case 1:
                DownloadResult result = (DownloadResult) msg.obj;
                if (result.downloader == this._downloader) {
                    if (result.exception != null) {
                        this._listener.onSubtitleDownloadFailed(this, result.entry.service, result.file, result.entry, result.exception);
                        return;
                    } else {
                        this._listener.onSubtitleDownloaded(this, result.entry.service, result.file, result.entry);
                        return;
                    }
                }
                return;
            case 2:
                if (msg.obj == this._downloader) {
                    sendMessageDelayed(obtainMessage(0, this._downloader), 10000L);
                }
                this._listener.onSubtitleDownloadEnded(this);
                return;
            default:
                return;
        }
    }
}
