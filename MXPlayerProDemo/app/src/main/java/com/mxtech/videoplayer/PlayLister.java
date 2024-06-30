package com.mxtech.videoplayer;

import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.mxtech.ArrayUtils;
import com.mxtech.FileUtils;
import com.mxtech.IOUtils;
import com.mxtech.NumericUtils;
import com.mxtech.StringUtils;
import com.mxtech.image.ImageScanner;
import com.mxtech.media.MediaUtils;
import com.mxtech.net.HttpFactory;
import com.mxtech.net.NetUtils;
import com.mxtech.os.AsyncTask2;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.P;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class PlayLister {
    private static final int FLAG_LOOP_ALL = 1;
    private static final int FLAG_REGISTER_CURRENT = 2;
    private AccessTester _accessTester;
    private final Uri _container;
    private UriWrapper[] _contents;
    private final boolean _fixedList;
    private final HttpFactory _httpFactory;
    private Listener _listener;
    private Set<UriWrapper> _randomHistory;
    private Uri[] _remoteCovers;
    private RemoteLister _remoteListingTask;
    private boolean _remoteListingValid;
    private Uri[] _remoteSubtitles;
    public static final String TAG = App.TAG + ".PlayLister";
    private static int MAX_DIRECTORY_SIZE = 2097152;
    private final ArrayList<UriWrapper> _history = new ArrayList<>();
    private final Map<Uri, Boolean> _validatedResources = new HashMap();

    /* loaded from: classes.dex */
    public interface Listener {
        void onNetworkListingCompleted(PlayLister playLister);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class UriWrapper implements Comparable<UriWrapper> {
        public final String decoded;
        public final String name;
        public final Uri uri;

        public UriWrapper(Uri uri) {
            this.uri = MediaUtils.canonicalizeUri(uri);
            this.decoded = Uri.decode(uri.toString());
            this.name = FileUtils.getName(this.decoded);
        }

        public boolean equals(Object o) {
            if (o instanceof UriWrapper) {
                return this.decoded.equalsIgnoreCase(((UriWrapper) o).decoded);
            }
            return false;
        }

        public boolean equals(UriWrapper o) {
            return this.decoded.equalsIgnoreCase(o.decoded);
        }

        public int hashCode() {
            return this.decoded.hashCode();
        }

        @Override // java.lang.Comparable
        public int compareTo(UriWrapper another) {
            return StringUtils.compareToIgnoreCaseNumeric(this.name, another.name);
        }

        public String toString() {
            return this.uri.toString();
        }

        public static UriWrapper[] createArray(Uri[] from) {
            int size = from.length;
            UriWrapper[] to = new UriWrapper[size];
            for (int i = 0; i < size; i++) {
                to[i] = new UriWrapper(from[i]);
            }
            return to;
        }

        public static UriWrapper[] createArray(List<Uri> from) {
            int size = from.size();
            UriWrapper[] to = new UriWrapper[size];
            for (int i = 0; i < size; i++) {
                to[i] = new UriWrapper(from.get(i));
            }
            return to;
        }
    }

    private PlayLister(Uri source, Uri container, @Nullable Uri[] list, HttpFactory httpFactory) {
        this._httpFactory = httpFactory;
        this._container = container;
        if (list != null) {
            this._fixedList = true;
            this._contents = UriWrapper.createArray(list);
            return;
        }
        this._fixedList = false;
        this._contents = new UriWrapper[0];
        refreshList(true, source);
    }

    private static boolean uriEqual(Uri l, Uri r) {
        if (l == null) {
            return r == null;
        }
        return l.equals(r);
    }

    public static PlayLister create(PlayLister old, Uri initial, @Nullable Uri[] list, HttpFactory httpFactory) {
        Uri container = null;
        if (old != null) {
            if (old._fixedList && list != null && old._contents.length == list.length) {
                int count = list.length;
                for (int i = 0; i < count; i++) {
                    if (old._contents[i].equals(list[i])) {
                    }
                }
                return old;
            }
            container = getContainer(initial);
            if (!old._fixedList && list == null && uriEqual(old._container, container)) {
                return old;
            }
        }
        if (old != null) {
            old.close();
        }
        if (container == null) {
            container = getContainer(initial);
        }
        return new PlayLister(initial, container, list, httpFactory);
    }

    private static Uri getUpperOrSourceUri(Uri source) {
        String parentPath = FileUtils.getParentPath(source.toString());
        if (parentPath != null) {
            return Uri.parse(parentPath);
        }
        return source;
    }

    private static Uri getContainer(Uri source) {
        String path = source.getPath();
        if (path == null || path.length() == 0 || path.charAt(path.length() - 1) == '/') {
            return null;
        }
        String scheme = source.getScheme();
        if (scheme == null || scheme.equals(MediaListFragment.TYPE_FILE)) {
            File file = new File(source.getPath());
            if (!file.isDirectory()) {
                return getUpperOrSourceUri(source);
            }
            return source;
        } else if (scheme.equals("http") || scheme.equals("https")) {
            return getUpperOrSourceUri(source);
        } else {
            return source;
        }
    }

    public void setListener(Listener listener) {
        this._listener = listener;
    }

    private void getMediaStoreMedias(Cursor cursor, Set<UriWrapper> files) {
        if (cursor.moveToFirst()) {
            do {
                files.add(new UriWrapper(Uri.fromFile(new File(cursor.getString(0)))));
            } while (cursor.moveToNext());
        }
    }

    @NonNull
    private UriWrapper[] listMediaStoredVideos() {
        Cursor cursor;
        try {
            Set<UriWrapper> files = new HashSet<>();
            Cursor cursor2 = App.cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, null, null, null);
            if (cursor2 != null) {
                getMediaStoreMedias(cursor2, files);
                cursor2.close();
            }
            if (P.isAudioPlayer && (cursor = App.cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, null, null, null)) != null) {
                getMediaStoreMedias(cursor, files);
                cursor.close();
            }
            return (UriWrapper[]) files.toArray(new UriWrapper[files.size()]);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return new UriWrapper[0];
        }
    }

    @NonNull
    private UriWrapper[] filesToUris(@Nullable File[] files) {
        if (files == null) {
            return new UriWrapper[0];
        }
        int count = files.length;
        UriWrapper[] uris = new UriWrapper[count];
        for (int i = 0; i < count; i++) {
            uris[i] = new UriWrapper(Uri.fromFile(files[i]));
        }
        return uris;
    }

    public void close() {
        if (this._remoteListingTask != null) {
            this._remoteListingTask.cancel(true);
            this._remoteListingTask = null;
        }
        this._listener = null;
        if (this._accessTester != null) {
            this._accessTester.cancel(true);
            this._accessTester = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class NetworkReadResult {
        final boolean containsSource;
        final UriWrapper[] covers;
        final UriWrapper[] medias;
        final UriWrapper[] subtitles;

        NetworkReadResult(Uri source, Uri container, List<String> names) {
            List<Uri> medias = new ArrayList<>();
            List<Uri> subtitles = new ArrayList<>();
            List<Uri> covers = new ArrayList<>();
            String sourceName = source != null ? FileUtils.getName(source.toString()) : null;
            boolean containsSource = false;
            for (String name : names) {
                if (!containsSource && name.equalsIgnoreCase(sourceName)) {
                    containsSource = true;
                }
                String ext = FileUtils.getExtension(name);
                if (ext != null) {
                    if (P.isSupportedMediaExtension(ext)) {
                        medias.add(Uri.withAppendedPath(container, name));
                    } else if (SubtitleFactory.isSupportedExtension(ext)) {
                        subtitles.add(Uri.withAppendedPath(container, name));
                    } else if (ImageScanner.isSupportedExtension(ext)) {
                        covers.add(Uri.withAppendedPath(container, name));
                    }
                }
            }
            this.containsSource = containsSource;
            this.medias = UriWrapper.createArray(medias);
            this.subtitles = UriWrapper.createArray(subtitles);
            this.covers = UriWrapper.createArray(covers);
            ArrayUtils.safeSort(this.medias);
            ArrayUtils.safeSort(this.subtitles);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class RemoteLister extends AsyncTask2<Uri, Void, NetworkReadResult> {
        private RemoteLister() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public NetworkReadResult doInBackground(Uri... params) {
            Uri source = params[0];
            Uri container = params[1];
            String scheme = container.getScheme();
            if ("http".equals(scheme) || "https".equals(scheme)) {
                try {
                    HttpURLConnection conn = PlayLister.this._httpFactory.openGetConnection(container.toString());
                    long len = conn.getContentLength();
                    if (len >= 0) {
                        if (len > PlayLister.MAX_DIRECTORY_SIZE) {
                            Log.w(PlayLister.TAG, "Remote directory too large: " + len);
                            conn.disconnect();
                            return null;
                        }
                    } else {
                        len = PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
                    }
                    ByteArrayOutputStream contentTemp = new ByteArrayOutputStream((int) len);
                    InputStream s = conn.getInputStream();
                    IOUtils.transferStream(s, contentTemp, PlayLister.MAX_DIRECTORY_SIZE);
                    String content = contentTemp.toString();
                    s.close();
                    contentTemp.close();
                    List<String> names = new ArrayList<>();
                    Pattern pattern = Pattern.compile("href\\s*=\\s*\"(.+?)\"", 2);
                    Matcher m = pattern.matcher(content);
                    while (m.find()) {
                        names.add(m.group(1));
                    }
                    NetworkReadResult networkReadResult = new NetworkReadResult(source, container, names);
                    conn.disconnect();
                    return networkReadResult;
                } finally {
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(NetworkReadResult result) {
            int i = 0;
            if (PlayLister.this._remoteListingTask == this) {
                PlayLister.this._remoteListingTask = null;
                if (result != null) {
                    PlayLister.this._remoteListingValid = result.containsSource;
                    PlayLister.this._contents = result.medias;
                    PlayLister.this._remoteSubtitles = new Uri[result.subtitles.length];
                    UriWrapper[] uriWrapperArr = result.subtitles;
                    int length = uriWrapperArr.length;
                    int i2 = 0;
                    int i3 = 0;
                    while (i2 < length) {
                        UriWrapper uri = uriWrapperArr[i2];
                        PlayLister.this._remoteSubtitles[i3] = uri.uri;
                        i2++;
                        i3++;
                    }
                    PlayLister.this._remoteCovers = new Uri[result.covers.length];
                    UriWrapper[] uriWrapperArr2 = result.covers;
                    int length2 = uriWrapperArr2.length;
                    int i4 = 0;
                    while (i < length2) {
                        UriWrapper uri2 = uriWrapperArr2[i];
                        PlayLister.this._remoteCovers[i4] = uri2.uri;
                        i++;
                        i4++;
                    }
                }
                if (PlayLister.this._listener != null) {
                    PlayLister.this._listener.onNetworkListingCompleted(PlayLister.this);
                }
            }
        }
    }

    public boolean isRemoteListing() {
        return this._remoteListingTask != null;
    }

    public boolean isRemoteListValid() {
        return this._remoteListingValid;
    }

    public Uri[] getRemoteSubtitles() {
        return this._remoteSubtitles;
    }

    public Uri[] getRemoteCovers() {
        return this._remoteCovers;
    }

    private void refreshList(boolean allowNetworkAccess, Uri source) {
        if (!this._fixedList && this._container != null) {
            String scheme = this._container.getScheme();
            if (MediaListFragment.TYPE_FILE.equals(scheme)) {
                this._contents = filesToUris(FileUtils.listFiles(new File(this._container.getSchemeSpecificPart()), P.mediaFileFilter));
            } else if ("content".equals(scheme)) {
                if (MediaUtils.isMediaStoreVideoURI(this._container)) {
                    this._contents = listMediaStoredVideos();
                }
            } else if (allowNetworkAccess && source.getQuery() == null) {
                if (this._remoteListingTask == null) {
                    this._remoteListingTask = new RemoteLister();
                    this._remoteListingTask.executeParallel(source, this._container);
                    return;
                }
                return;
            }
            ArrayUtils.safeSort(this._contents);
        }
    }

    private int getIndex(UriWrapper find) {
        UriWrapper[] uriWrapperArr;
        int i = 0;
        for (UriWrapper entry : this._contents) {
            if (!entry.equals(find)) {
                i++;
            } else {
                return i;
            }
        }
        return -1;
    }

    public Uri getAndUpdate(Uri current, int direction) {
        return getAndUpdate(current, direction, (P.playerLooping == 9 ? 1 : 0) | 2);
    }

    public Uri get(Uri current, int direction) {
        return getAndUpdate(current, direction, P.playerLooping == 9 ? 1 : 0);
    }

    private Uri getAndUpdate(@Nullable Uri current, int direction, int flags) {
        if (current == null) {
            return null;
        }
        UriWrapper currentWr = new UriWrapper(current);
        int count = this._history.size();
        if (direction < 0) {
            if (count > 0) {
                return this._history.remove(count - 1).uri;
            }
        } else if ((flags & 2) != 0 && (count == 0 || !currentWr.equals(this._history.get(count - 1)))) {
            this._history.add(currentWr);
        }
        return get(currentWr, direction, flags);
    }

    public Uri get(@NonNull UriWrapper current, int direction, int flags) {
        int index;
        UriWrapper[] uriWrapperArr;
        int pickup;
        UriWrapper[] uriWrapperArr2;
        if (direction == 0) {
            if (this._contents.length == 0) {
                return getPredicted(current.uri, 1);
            }
            if (this._randomHistory == null) {
                this._randomHistory = new HashSet();
            }
            this._randomHistory.add(current);
            int total = 0;
            for (UriWrapper uri : this._contents) {
                if (!this._randomHistory.contains(uri)) {
                    total++;
                }
            }
            if (total == 0) {
                if ((flags & 1) == 0) {
                    return null;
                }
                this._randomHistory.clear();
                this._randomHistory.add(current);
                total = 0;
                for (UriWrapper uri2 : this._contents) {
                    if (!current.equals(uri2)) {
                        total++;
                    }
                }
                if (total == 0) {
                    return current.uri;
                }
            }
            int pickup2 = NumericUtils.ThreadLocalRandom.get().nextInt(total);
            index = 0;
            UriWrapper[] uriWrapperArr3 = this._contents;
            int length = uriWrapperArr3.length;
            int i = 0;
            int pickup3 = pickup2;
            while (i < length) {
                UriWrapper uri3 = uriWrapperArr3[i];
                if (!this._randomHistory.contains(uri3)) {
                    pickup = pickup3 - 1;
                    if (pickup3 == 0) {
                        break;
                    }
                } else {
                    pickup = pickup3;
                }
                index++;
                i++;
                pickup3 = pickup;
            }
        } else if (this._contents.length == 0) {
            return getPredicted(current.uri, direction);
        } else {
            int index2 = getIndex(current);
            if (index2 < 0) {
                Log.w(TAG, "Current video not found from the list. Current=" + current.decoded + " List=" + this._contents[0].decoded + " ... " + this._contents[this._contents.length - 1].decoded + " (" + this._contents.length + ')');
                return getPredicted(current.uri, direction);
            }
            index = index2 + direction;
            if (index < 0 || index >= this._contents.length) {
                if ((flags & 1) == 0) {
                    return null;
                }
                index = index < 0 ? this._contents.length - 1 : 0;
            }
        }
        return this._contents[index].uri;
    }

    public Uri getContainer() {
        return this._container;
    }

    public Uri getRandom() {
        if (this._contents.length > 0) {
            return this._contents[NumericUtils.ThreadLocalRandom.get().nextInt(this._contents.length)].uri;
        }
        return null;
    }

    public Uri getAt(int index) {
        if (index < this._contents.length) {
            return this._contents[index].uri;
        }
        return null;
    }

    private Uri getPredicted(Uri current, int direction) {
        Uri predicted = predict(current, direction);
        if (predicted == null) {
            return null;
        }
        Boolean isValid = this._validatedResources.get(predicted);
        if (isValid == null || !isValid.booleanValue()) {
            return null;
        }
        return predicted;
    }

    private Uri predict(Uri current, int direction) {
        List<String> pathSegs = current.getPathSegments();
        int segCount = pathSegs.size();
        if (segCount == 0) {
            return null;
        }
        boolean digitFound = false;
        boolean dotFound = false;
        StringBuilder name = new StringBuilder(pathSegs.get(segCount - 1));
        for (int i = name.length() - 1; i >= 0; i--) {
            char ch = name.charAt(i);
            if (dotFound) {
                if (Character.isDigit(ch)) {
                    digitFound = true;
                    char ch2 = (char) (ch + direction);
                    if (ch2 < '0') {
                        name.setCharAt(i, '9');
                    } else if (ch2 > '9') {
                        name.setCharAt(i, '0');
                    } else {
                        name.setCharAt(i, ch2);
                        StringBuilder b = new StringBuilder();
                        String scheme = current.getScheme();
                        String auth = current.getEncodedAuthority();
                        String query = current.getEncodedQuery();
                        String fragment = current.getEncodedFragment();
                        if (scheme != null) {
                            b.append(scheme).append("://");
                        }
                        if (auth != null) {
                            b.append(auth);
                        }
                        for (int k = 0; k < segCount - 1; k++) {
                            b.append('/').append(Uri.encode(pathSegs.get(k)));
                        }
                        b.append('/').append(Uri.encode(name.toString()));
                        if (query != null) {
                            b.append('?').append(query);
                        }
                        if (fragment != null) {
                            b.append('#').append(fragment);
                        }
                        return Uri.parse(b.toString());
                    }
                } else if (digitFound) {
                    break;
                }
            } else if (ch == '.') {
                dotFound = true;
            }
        }
        return null;
    }

    public void rename(Uri from, Uri to) {
        UriWrapper fromWr = new UriWrapper(from);
        UriWrapper toWr = new UriWrapper(to);
        int length = this._contents.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (!this._contents[i].equals(fromWr)) {
                i++;
            } else {
                this._contents[i] = toWr;
                break;
            }
        }
        int length2 = this._history.size();
        int i2 = 0;
        while (true) {
            if (i2 >= length2) {
                break;
            } else if (!this._history.get(i2).equals(fromWr)) {
                i2++;
            } else {
                this._history.set(i2, toWr);
                break;
            }
        }
        if (this._randomHistory != null && this._randomHistory.remove(fromWr)) {
            this._randomHistory.add(toWr);
        }
    }

    public void remove(Uri media) {
        UriWrapper mediaWr = new UriWrapper(media);
        int length = this._contents.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (!this._contents[i].equals(mediaWr)) {
                i++;
            } else {
                UriWrapper[] newContents = new UriWrapper[length - 1];
                if (i > 0) {
                    System.arraycopy(this._contents, 0, newContents, 0, i);
                }
                if (i + 1 < length) {
                    System.arraycopy(this._contents, i + 1, newContents, i, (length - i) - 1);
                }
                this._contents = newContents;
            }
        }
        this._history.remove(mediaWr);
        if (this._randomHistory != null) {
            this._randomHistory.remove(mediaWr);
        }
    }

    public void loadRemoteAdjascentUri(Uri current) {
        if (!isRemoteListing() && !isRemoteListValid() && this._accessTester == null) {
            this._validatedResources.put(current, true);
            String scheme = current.getScheme();
            String host = current.getHost();
            String query = current.getQuery();
            if (query == null) {
                if (("http".equals(scheme) || "https".equals(scheme)) && NetUtils.isLocalAddress(host)) {
                    Uri prev = predict(current, -1);
                    Uri next = predict(current, 1);
                    boolean testPrev = false;
                    boolean testNext = false;
                    if (prev != null && !this._validatedResources.containsKey(prev)) {
                        this._validatedResources.put(prev, null);
                        testPrev = true;
                    }
                    if (next != null && !this._validatedResources.containsKey(next)) {
                        this._validatedResources.put(next, null);
                        testNext = true;
                    }
                    if (testPrev || testNext) {
                        this._accessTester = new AccessTester();
                        if (testPrev && testNext) {
                            this._accessTester.executeParallel(prev, next);
                        } else if (testPrev) {
                            this._accessTester.executeParallel(prev);
                        } else {
                            this._accessTester.executeParallel(next);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AccessTester extends AsyncTask2<Uri, Void, Map<Uri, Boolean>> {
        private AccessTester() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Map<Uri, Boolean> doInBackground(Uri... params) {
            Map<Uri, Boolean> results = new HashMap<>();
            for (Uri uri : params) {
                if (isCancelled()) {
                    return null;
                }
                String scheme = uri.getScheme();
                results.put(uri, false);
                if ("http".equals(scheme) || "https".equals(scheme)) {
                    SystemClock.uptimeMillis();
                    try {
                        PlayLister.this._httpFactory.openHeadConnection(uri.toString()).disconnect();
                        results.put(uri, true);
                    } catch (Throwable th) {
                    }
                }
            }
            return results;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Map<Uri, Boolean> result) {
            if (PlayLister.this._accessTester == this) {
                PlayLister.this._accessTester = null;
                if (result != null) {
                    PlayLister.this._validatedResources.putAll(result);
                }
            }
        }
    }
}
