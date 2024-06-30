package com.mxtech.videoplayer.subtitle.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Base64OutputStream;
import android.util.Log;
import com.google.android.gms.actions.SearchIntents;
import com.mxtech.CharUtils;
import com.mxtech.IOUtils;
import com.mxtech.LocaleUtils;
import com.mxtech.NumericUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.crypto.UsernamePasswordSaver;
import com.mxtech.media.MediaUtils;
import com.mxtech.net.HttpFactory;
import com.mxtech.net.HttpServerException;
import com.mxtech.net.NetUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.preference.Tuner;
import com.mxtech.videoplayer.subtitle.SubView;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.widget.DecorEditText;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlrpc.android.IXMLRPCSerializer;
import org.xmlrpc.android.XMLRPCClient2;
import org.xmlrpc.android.XMLRPCException;

/* loaded from: classes2.dex */
public final class OpenSubtitles extends SubtitleService {
    private static final int HASH_CHUNK_SIZE = 65536;
    private static final int IDLE_TIME = 60000;
    private static final int KEEP_ALIVE_INTERVAL = 840000;
    private static final int MAX_LANGUAGE_TEST_BYTES = 262144;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 1;
    private static final int MIN_USERNAME_LENGTH = 2;
    public static final String NAME = "opensubtitles.org";
    public static final boolean NEED_LOGIN_FOR_RATING = true;
    private static final int NUM_RETRY = 2;
    private static final int OS_ERROR_DOWNLOAD_LIMIT_REACHED = 407;
    private static final int OS_ERROR_EMAIL_EXIST = 418;
    private static final int OS_ERROR_NO_SESSION = 406;
    private static final int OS_ERROR_OTHER = 410;
    private static final int OS_ERROR_SUBTITLE_INVALID = 402;
    private static final int OS_ERROR_UNAUTHORIZED = 401;
    private static final int OS_ERROR_USERNAME_EXIST = 417;
    private static final int RESULT_LIMIT = 100;
    private static final String SERVER = "http://api.opensubtitles.org/xml-rpc";
    private static final String SERVER_SECURE = "https://api.opensubtitles.org/xml-rpc";
    private static final int SUPPORTED_FEATURES;
    private static URL URL_SERVER = null;
    private static URL URL_SERVER_SECURE = null;
    private static final String USERNAME_PATTERN = "^[\\w-]+$";
    private static final String USER_AGENT = "MX Player v1";
    private static OrderedSharedPreferences.OnSharedPreferenceChangeListener _credentialChangeListener;
    private static ScheduledExecutorService _executor;
    private static boolean _hasKeepAliveSchedule;
    private static int _lastHttpStatus;
    private static long _lastMethodCallTime;
    private static String _token;
    private static String _tokenToLogout;
    private MediaContext _mediaContext;
    private SubtitleContext _subtitleContext;
    public static final String TAG = App.TAG + ".OpenSubtitles";
    private static int EXECUTE_NEED_LOGIN = 1;
    private static int EXECUTE_SECURE = 2;
    private static int _lastOSStatus = -1;

    static {
        try {
            URL_SERVER_SECURE = new URL(SERVER_SECURE);
            URL_SERVER = new URL(SERVER);
        } catch (MalformedURLException e) {
            Log.e(TAG, "", e);
        }
        int features = 375;
        if (Build.VERSION.SDK_INT >= 8) {
            features = 375 | 128;
        }
        SUPPORTED_FEATURES = features;
    }

    public static int getCapabilities(@Nullable Media media) {
        return media == null ? 0 : 7;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public String name() {
        return "opensubtitles.org";
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public int getFeatures() {
        return SUPPORTED_FEATURES;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public void setupInputs(Context context, DecorEditText usernameInput, DecorEditText passwordInput) {
        usernameInput.setConstraint(2, 20, USERNAME_PATTERN, R.string.opensubtitles_username_characters);
        passwordInput.setConstraint(1, 64);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized Map execute(int flags, String method, Object... params) throws SubtitleService.SubtitleServiceException {
        Map execute_l;
        synchronized (OpenSubtitles.class) {
            execute_l = execute_l(2, flags, method, params);
        }
        return execute_l;
    }

    private static Map execute_l(int numRetry, int flags, String method, Object... params) throws SubtitleService.SubtitleServiceException {
        try {
            boolean needLogin = (EXECUTE_NEED_LOGIN & flags) != 0;
            if (!needLogin || login_l()) {
                Object[] args = new Object[params.length + 1];
                args[0] = needLogin ? _token : "";
                System.arraycopy(params, 0, args, 1, params.length);
                Object result = call_l((EXECUTE_SECURE & flags) != 0, 2, method, args);
                _lastMethodCallTime = SystemClock.uptimeMillis();
                if (result instanceof Map) {
                    Map results = (Map) result;
                    if (checkStatus_l(results)) {
                        return results;
                    }
                    if (needLogin && (_lastOSStatus == 406 || _lastOSStatus == OS_ERROR_UNAUTHORIZED)) {
                        _token = null;
                        if (numRetry > 0) {
                            return execute_l(numRetry - 1, flags, method, params);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        if (_lastOSStatus > 0) {
            if (_lastOSStatus < 300) {
                throw new SubtitleService.SubtitleServiceException();
            }
            switch (_lastOSStatus) {
                case OS_ERROR_UNAUTHORIZED /* 401 */:
                    throw new SubtitleService.UnauthorizedException();
                case 402:
                    throw new SubtitleService.SubtitleFormatUnrecognized();
                case OS_ERROR_DOWNLOAD_LIMIT_REACHED /* 407 */:
                    throw new SubtitleService.DownloadLimitReachedException();
                case OS_ERROR_USERNAME_EXIST /* 417 */:
                    throw new SubtitleService.UsernameExistException();
                case OS_ERROR_EMAIL_EXIST /* 418 */:
                    throw new SubtitleService.EmailAlreadyUsedException();
                default:
                    throw new SubtitleService.ServerException();
            }
        } else if (_lastHttpStatus > 0) {
            throw new SubtitleService.ServerException();
        } else {
            throw new SubtitleService.NetworkException();
        }
    }

    private static boolean login_l() throws KeyManagementException, NoSuchAlgorithmException, IllegalArgumentException, IllegalStateException, IOException, XMLRPCException, XmlPullParserException {
        UsernamePasswordSaver.Info info;
        if (_tokenToLogout != null) {
            call_l(false, 2, "LogOut", _tokenToLogout);
            _tokenToLogout = null;
        }
        if (_token == null) {
            String lang = LocaleUtils.fixLanguageCode(Locale.getDefault().getLanguage());
            String encryptedCred = App.prefs.getString(Key.CREDENTIAL_OPENSUBTITLES, null);
            String username = "";
            String password = "";
            if (encryptedCred != null && (info = UsernamePasswordSaver.decrypt(encryptedCred)) != null) {
                username = info.username;
                password = info.password;
            }
            Object result = call_l(true, 2, "LogIn", username, password, lang, USER_AGENT);
            if (!(result instanceof Map)) {
                return false;
            }
            Map results = (Map) result;
            if (!checkStatus_l(results)) {
                return false;
            }
            _token = (String) results.get("token");
            if (_token == null) {
                return false;
            }
            if (_executor == null) {
                _executor = Executors.newSingleThreadScheduledExecutor();
            }
            if (!_hasKeepAliveSchedule) {
                _executor.schedule(new Runnable() { // from class: com.mxtech.videoplayer.subtitle.service.OpenSubtitles.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            synchronized (OpenSubtitles.class) {
                                boolean unused = OpenSubtitles._hasKeepAliveSchedule = false;
                                if (OpenSubtitles._token != null) {
                                    if (!ActivityRegistry.hasForegroundActivity() || OpenSubtitles._lastMethodCallTime + 60000 < SystemClock.uptimeMillis()) {
                                        OpenSubtitles.call_l(false, 2, "LogOut", OpenSubtitles._token);
                                        String unused2 = OpenSubtitles._token = null;
                                    } else {
                                        Object result2 = OpenSubtitles.call_l(false, 2, "NoOperation", OpenSubtitles._token);
                                        if (result2 instanceof Map) {
                                            if (OpenSubtitles.checkStatus_l((Map) result2)) {
                                                OpenSubtitles._executor.schedule(this, 840000L, TimeUnit.MILLISECONDS);
                                                boolean unused3 = OpenSubtitles._hasKeepAliveSchedule = true;
                                            } else {
                                                String unused4 = OpenSubtitles._token = null;
                                            }
                                        } else {
                                            String unused5 = OpenSubtitles._token = null;
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            Log.e(OpenSubtitles.TAG, "", e);
                        }
                    }
                }, 840000L, TimeUnit.MILLISECONDS);
                _hasKeepAliveSchedule = true;
            }
            if (_credentialChangeListener == null) {
                _credentialChangeListener = new OrderedSharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.mxtech.videoplayer.subtitle.service.OpenSubtitles.2
                    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
                    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
                        if (key.equals(Key.CREDENTIAL_OPENSUBTITLES)) {
                            String unused = OpenSubtitles._tokenToLogout = OpenSubtitles._token;
                            String unused2 = OpenSubtitles._token = null;
                        }
                    }
                };
                App.prefs.registerOnSharedPreferenceChangeListener(_credentialChangeListener);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object call_l(boolean secure, int numRetry, String method, Object... args) throws KeyManagementException, NoSuchAlgorithmException, IOException, IllegalArgumentException, IllegalStateException, XMLRPCException, XmlPullParserException {
        Object result;
        XMLRPCClient2 client = getRpcClient(secure);
        try {
            try {
                _lastOSStatus = -1;
                _lastHttpStatus = 0;
                if (P.log_opensubtitles) {
                    Log.d(TAG, method + " -->> " + Arrays.toString(args) + (secure ? " (secure)" : ""));
                }
                result = client.call(method, args);
                if (P.log_opensubtitles) {
                    Log.d(TAG, method + " <<-- " + result + " (" + (secure ? " HTTPS" : " HTTP") + " response code: " + client.lastStatusCode + ")");
                }
                _lastHttpStatus = client.lastStatusCode;
            } catch (IOException e) {
                Log.w(TAG, "", e);
                _lastHttpStatus = client.lastStatusCode;
                if (numRetry <= 0 || client.lastStatusCode < 500) {
                    throw e;
                }
                client.close();
                XMLRPCClient2 client2 = null;
                if (P.log_opensubtitles) {
                    Log.w(TAG, "Retry RPC call since numRetry is " + numRetry, e);
                }
                result = call_l(secure, numRetry - 1, method, args);
                if (0 != 0) {
                    client2.close();
                }
            } catch (XmlPullParserException e2) {
                Log.w(TAG, "", e2);
                if (numRetry <= 0) {
                    throw e2;
                }
                if (P.log_opensubtitles) {
                    Log.w(TAG, "Retry RPC call since numRetry is " + numRetry, e2);
                }
                result = call_l(secure, numRetry - 1, method, args);
                if (client != null) {
                    client.close();
                }
            }
            return result;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private static XMLRPCClient2 getRpcClient() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        return getRpcClient(false);
    }

    private static XMLRPCClient2 getRpcClient(boolean secure) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        XMLRPCClient2 client = new XMLRPCClient2(secure ? URL_SERVER_SECURE : URL_SERVER);
        client.setUserAgent(USER_AGENT);
        client.setAcceptLanguage(LocaleUtils.getHttpLanguageCode(Locale.getDefault()));
        client.setSendGZipped(true);
        if (secure) {
            client.setSSLSocketFactory(NetUtils.getAllTrustSSLSocketFactory());
        }
        return client;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean checkStatus_l(Map results) {
        String statusText = (String) results.get("status");
        if (statusText != null) {
            _lastOSStatus = (int) NumericUtils.parseHeadingLong(statusText);
            return 200 <= _lastOSStatus && _lastOSStatus < 300;
        }
        return false;
    }

    /* loaded from: classes2.dex */
    private static class RequestEntry {
        final String hash;
        final Media media;
        final String tag;

        static RequestEntry fromHash(Media media, String hash) {
            return new RequestEntry(media, hash, null);
        }

        static RequestEntry fromTag(Media media, String tag) {
            return new RequestEntry(media, null, tag);
        }

        RequestEntry(Media media, String hash, String tag) {
            this.media = media;
            this.hash = hash;
            this.tag = tag;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:68:0x02f2  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x041b  */
    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public SubtitleEntry[] search(Media[] medias, Locale[] locales, String queryText) throws SubtitleService.SubtitleServiceException {
        String langId;
        String langs = buildLanguageString(locales);
        ArrayList<RequestEntry> requested = new ArrayList<>();
        ArrayList<HashMap<String, String>> reqs = new ArrayList<>();
        for (Media media : medias) {
            String hash = media.openSubtitlesMovieHash();
            if (hash != null) {
                HashMap<String, String> reqByFile = new HashMap<>();
                reqByFile.put("sublanguageid", langs);
                reqByFile.put("moviebytesize", Long.toString(media.size()));
                reqByFile.put("moviehash", hash);
                if (P.log_opensubtitles) {
                    Log.v(TAG, "Query #" + requested.size() + ": " + media.uri + " (by file) --> " + reqByFile);
                }
                reqs.add(reqByFile);
                requested.add(RequestEntry.fromHash(media, hash));
            }
            HashMap<String, String> reqByTag = new HashMap<>();
            reqByTag.put("tag", media.fileName);
            reqByTag.put("sublanguageid", langs);
            if (P.log_opensubtitles) {
                Log.v(TAG, "Query #" + requested.size() + ": " + media.uri + " (by tag) --> " + reqByTag);
            }
            reqs.add(reqByTag);
            requested.add(RequestEntry.fromTag(media, media.fileName));
        }
        if (queryText.length() > 0) {
            HashMap<String, String> reqByQuery = new HashMap<>();
            reqByQuery.put("sublanguageid", langs);
            reqByQuery.put(SearchIntents.EXTRA_QUERY, queryText);
            if (P.log_opensubtitles) {
                Log.v(TAG, "Query #" + requested.size() + ":  ? (by query) --> " + reqByQuery);
            }
            reqs.add(reqByQuery);
        }
        HashMap<String, String> options = new HashMap<>();
        options.put("limit", Integer.toString(100));
        Map results = execute(EXECUTE_NEED_LOGIN, "SearchSubtitles", reqs, options);
        Object data = results.get(IXMLRPCSerializer.TAG_DATA);
        if (!(data instanceof Object[])) {
            return new SubtitleEntry[0];
        }
        int i = 0;
        Object[] datas = (Object[]) data;
        ArrayList<SubtitleEntry> entries = new ArrayList<>(datas.length);
        int length = datas.length;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            int i4 = i;
            if (i3 < length) {
                Object dataEntry = datas[i3];
                if (P.log_opensubtitles) {
                    i = i4 + 1;
                    Log.v(TAG, "data[" + i4 + "] <-- " + dataEntry);
                } else {
                    i = i4;
                }
                Map r = (Map) dataEntry;
                String downloadLink = (String) r.get("SubDownloadLink");
                if (downloadLink != null) {
                    Iterator<SubtitleEntry> it = entries.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (downloadLink.equals(it.next().tag)) {
                                break;
                            }
                        } else {
                            String matchedBy = (String) r.get("MatchedBy");
                            if (matchedBy == null) {
                                if (P.log_opensubtitles) {
                                    Log.w(TAG, "No 'MatchedBy' returned.");
                                }
                            } else {
                                SubtitleEntry entry = new SubtitleEntry();
                                char c = 65535;
                                switch (matchedBy.hashCode()) {
                                    case 114586:
                                        if (matchedBy.equals("tag")) {
                                            c = 1;
                                            break;
                                        }
                                        break;
                                    case 1188161598:
                                        if (matchedBy.equals("moviehash")) {
                                            c = 0;
                                            break;
                                        }
                                        break;
                                    case 1331988540:
                                        if (matchedBy.equals("fulltext")) {
                                            c = 2;
                                            break;
                                        }
                                        break;
                                }
                                switch (c) {
                                    case 0:
                                        Iterator<RequestEntry> it2 = requested.iterator();
                                        while (true) {
                                            if (it2.hasNext()) {
                                                RequestEntry req = it2.next();
                                                if (req.hash != null && req.hash.equalsIgnoreCase((String) r.get("MovieHash"))) {
                                                    entry.media = req.media;
                                                }
                                            }
                                        }
                                        if (entry.media != null && entry.queryText == null) {
                                            if (P.log_opensubtitles) {
                                                Log.w(TAG, "No matching requestModify found");
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            entry.service = "opensubtitles.org";
                                            entry.fileName = (String) r.get("SubFileName");
                                            entry.size = (int) getInteger(r, "SubSize", 0L);
                                            entry.rating = getDecimal(r, "SubRating", SubView.SUBTITLE_DRAG_GAP);
                                            entry.tag = downloadLink;
                                            langId = (String) r.get("SubLanguageID");
                                            if (langId != null) {
                                                entry.locale = parseLanguageString(langId);
                                            }
                                            entries.add(entry);
                                            break;
                                        }
                                        break;
                                    case 1:
                                        int queryNumber = (int) getInteger(r, "QueryNumber", -1L);
                                        if (queryNumber >= 0 && queryNumber < requested.size()) {
                                            entry.media = requested.get(queryNumber).media;
                                        } else {
                                            Log.w(TAG, "Unknown 'QueryNumber' " + queryNumber + " among total " + requested.size() + " moviehash/tag requests.");
                                        }
                                        if (entry.media != null) {
                                            break;
                                        }
                                        entry.service = "opensubtitles.org";
                                        entry.fileName = (String) r.get("SubFileName");
                                        entry.size = (int) getInteger(r, "SubSize", 0L);
                                        entry.rating = getDecimal(r, "SubRating", SubView.SUBTITLE_DRAG_GAP);
                                        entry.tag = downloadLink;
                                        langId = (String) r.get("SubLanguageID");
                                        if (langId != null) {
                                        }
                                        entries.add(entry);
                                        break;
                                    case 2:
                                        int queryNumber2 = (int) getInteger(r, "QueryNumber", -1L);
                                        if (queryNumber2 == requested.size()) {
                                            entry.queryText = queryText;
                                        } else {
                                            Log.w(TAG, "Unknown 'QueryNumber' " + queryNumber2 + " for total " + requested.size() + "+1 requests.");
                                        }
                                        if (entry.media != null) {
                                        }
                                        entry.service = "opensubtitles.org";
                                        entry.fileName = (String) r.get("SubFileName");
                                        entry.size = (int) getInteger(r, "SubSize", 0L);
                                        entry.rating = getDecimal(r, "SubRating", SubView.SUBTITLE_DRAG_GAP);
                                        entry.tag = downloadLink;
                                        langId = (String) r.get("SubLanguageID");
                                        if (langId != null) {
                                        }
                                        entries.add(entry);
                                        break;
                                    default:
                                        if (P.log_opensubtitles) {
                                            Log.w(TAG, "Unknown 'MatchedBy' = " + matchedBy);
                                            break;
                                        } else {
                                            break;
                                        }
                                }
                            }
                        }
                    }
                }
                i2 = i3 + 1;
            } else {
                return (SubtitleEntry[]) entries.toArray(new SubtitleEntry[entries.size()]);
            }
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public List<MovieCandidate> searchMovies(Media media) throws SubtitleService.SubtitleServiceException {
        return getMediaContext(media).searchMovies();
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public List<MovieCandidate> searchMovies(String title) throws SubtitleService.SubtitleServiceException {
        Object[] objArr;
        Map resultMap = execute(EXECUTE_NEED_LOGIN, "SearchMoviesOnIMDB", title);
        List<MovieCandidate> candidates = new ArrayList<>();
        Object data = resultMap.get(IXMLRPCSerializer.TAG_DATA);
        if (data instanceof Object[]) {
            for (Object entry : (Object[]) data) {
                if (entry instanceof Map) {
                    Map entryMap = (Map) entry;
                    Object IMDbId = entryMap.get("id");
                    String givenTitle = (String) entryMap.get("title");
                    if (IMDbId != null && givenTitle != null) {
                        long id = getInteger(IMDbId);
                        MovieCandidate movie = new MovieCandidate(id, givenTitle, 0, 0, -1);
                        if (!candidates.contains(movie)) {
                            candidates.add(movie);
                        }
                    }
                }
            }
        }
        return candidates;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public List<String> searchMoviesStartsWith(String text) throws SubtitleService.SubtitleServiceException {
        Object[] objArr;
        Map resultMap = execute(EXECUTE_NEED_LOGIN, "QuickSuggest", text);
        List<String> candidates = new ArrayList<>();
        Object data = resultMap.get(IXMLRPCSerializer.TAG_DATA);
        if (data instanceof Object[]) {
            for (Object entry : (Object[]) data) {
                if (entry instanceof Map) {
                    Map entryMap = (Map) entry;
                    String title = (String) entryMap.get("MovieName");
                    if (title != null) {
                        if (!candidates.contains(title)) {
                            candidates.add(title);
                        }
                        if (P.log_opensubtitles) {
                            Log.v(TAG, "QuickSuggest: " + title);
                        }
                    }
                }
            }
        }
        return candidates;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public long registerMovie(Media media, String title, int year, int season, int episode) throws SubtitleService.SubtitleServiceException {
        StringBuilder sb = null;
        if (season > 0) {
            if (0 == 0) {
                sb = new StringBuilder().append(title).append(' ');
            }
            sb.append(String.format("S%02d", Integer.valueOf(season)));
        }
        if (episode >= 0) {
            if (sb == null) {
                sb = new StringBuilder().append(title).append(' ');
            }
            sb.append(String.format("E%02d", Integer.valueOf(episode)));
        }
        if (sb != null) {
            title = sb.toString();
        }
        MediaContext context = getMediaContext(media);
        long id = context.registerMovie(title, year);
        if (id == 0) {
            throw new SubtitleService.ServerDataFormatException("New movie id returned 0.");
        }
        context.registerFile();
        return id;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public void upload(long movieId, Media movie, Subtitle subtitle, @Nullable Locale subtitleLocale) throws SubtitleService.SubtitleServiceException {
        String lang;
        MediaContext mediaContext = getMediaContext(movie);
        SubtitleContext subtitleContext = getSubtitleContext(subtitle);
        mediaContext.id = movieId;
        ArrayMap<String, String> baseInfo = new ArrayMap<>(2);
        Map<String, String> cd1 = new HashMap<>();
        baseInfo.put("idmovieimdb", Long.toString(movieId));
        if (subtitleLocale != null && (lang = getLanguageString(subtitleLocale)) != null) {
            baseInfo.put("sublanguageid", lang);
        }
        mediaContext.putParameters(cd1);
        subtitleContext.putParameters(cd1);
        subtitleContext.putContent(cd1);
        ArrayMap<String, Map<String, String>> req = new ArrayMap<>(2);
        req.put("baseinfo", baseInfo);
        req.put("cd1", cd1);
        execute(EXECUTE_NEED_LOGIN, "UploadSubtitles", req);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public boolean exist(Media movie, Subtitle subtitle) throws SubtitleService.SubtitleServiceException {
        MediaContext mediaContext = getMediaContext(movie);
        SubtitleContext subtitleContext = getSubtitleContext(subtitle);
        return checkExistence(mediaContext, subtitleContext);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public void rate(Media media, Subtitle subtitle, int rating, @Nullable String comment) throws SubtitleService.SubtitleServiceException {
        MediaContext mediaContext = getMediaContext(media);
        SubtitleContext subtitleContext = getSubtitleContext(subtitle);
        if (subtitleContext.id == 0 && (!checkExistence(mediaContext, subtitleContext) || subtitleContext.id == 0)) {
            throw new SubtitleService.SubtitleNotFoundException();
        }
        HashMap<String, String> req = new HashMap<>();
        req.put("idsubtitle", Long.toString(subtitleContext.id));
        req.put("score", Integer.toString(rating < 1 ? 1 : rating));
        execute(EXECUTE_NEED_LOGIN, "SubtitlesVote", req);
        if (comment != null && comment.length() > 0) {
            req.clear();
            req.put("idsubtitle", Long.toString(subtitleContext.id));
            req.put("comment", comment);
            if (rating == 0) {
                req.put("badsubtitle", Tuner.TAG_SCREEN);
            }
            execute(EXECUTE_NEED_LOGIN, "AddComment", req);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getInteger(Map map, String key, long defaultValue) {
        return getInteger(map.get(key), defaultValue);
    }

    private static long getInteger(@Nullable Object value, long defaultValue) {
        if (value != null) {
            if (value instanceof Long) {
                return ((Long) value).longValue();
            }
            if (value instanceof Integer) {
                return ((Integer) value).intValue();
            }
            if (value instanceof Short) {
                return ((Short) value).shortValue();
            }
            if ((value instanceof String) && ((String) value).length() > 0) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    if (P.log_opensubtitles) {
                        Log.e(TAG, "", e);
                        return defaultValue;
                    }
                    return defaultValue;
                }
            }
            return defaultValue;
        }
        return defaultValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getInteger(Map map, String key) throws SubtitleService.ServerDataFormatException {
        return getInteger(map.get(key));
    }

    private static long getInteger(@Nullable Object value) throws SubtitleService.ServerDataFormatException {
        if (value != null) {
            if (value instanceof Long) {
                return ((Long) value).longValue();
            }
            if (value instanceof Integer) {
                return ((Integer) value).intValue();
            }
            if (value instanceof Short) {
                return ((Short) value).shortValue();
            }
            if ((value instanceof String) && ((String) value).length() > 0) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    if (P.log_opensubtitles) {
                        Log.e(TAG, "", e);
                    }
                }
            }
        }
        throw new SubtitleService.ServerDataFormatException("Invalid number: " + value);
    }

    private static double getDecimal(Map map, String key, double defaultValue) {
        return getDecimal(map.get(key), defaultValue);
    }

    private static double getDecimal(@Nullable Object value, double defaultValue) {
        if (value != null) {
            if (value instanceof Float) {
                return ((Float) value).floatValue();
            }
            if (value instanceof Double) {
                return ((Double) value).doubleValue();
            }
            if (value instanceof String) {
                if (((String) value).length() > 0) {
                    try {
                        return Double.parseDouble((String) value);
                    } catch (NumberFormatException e) {
                        if (P.log_opensubtitles) {
                            Log.e(TAG, "", e);
                            return defaultValue;
                        }
                        return defaultValue;
                    }
                }
                return defaultValue;
            } else if (value instanceof Long) {
                return ((Long) value).longValue();
            } else {
                if (value instanceof Integer) {
                    return ((Integer) value).intValue();
                }
                if (value instanceof Short) {
                    return ((Short) value).shortValue();
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public void get(File file, Object tag) throws SubtitleService.SubtitleServiceException {
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            try {
                try {
                    conn = HttpFactory.getInstance().openGetConnection((String) tag);
                    InputStream in2 = conn.getInputStream();
                    try {
                        InputStream in3 = new GZIPInputStream(in2);
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            try {
                                IOUtils.transferStream(in3, out);
                                out.flush();
                                if (in3 != null) {
                                    try {
                                        in3.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, "", e);
                                        return;
                                    }
                                }
                                if (conn != null) {
                                    conn.disconnect();
                                }
                            } finally {
                                out.close();
                            }
                        } catch (ZipException e2) {
                            e = e2;
                            Log.e(TAG, "", e);
                            throw new SubtitleService.ServerDataFormatException(e);
                        } catch (IOException e3) {
                            e = e3;
                            Log.e(TAG, "", e);
                            throw new SubtitleService.SubtitleFileWriteException(e);
                        } catch (Throwable th) {
                            th = th;
                            in = in3;
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e4) {
                                    Log.e(TAG, "", e4);
                                    throw th;
                                }
                            }
                            if (conn != null) {
                                conn.disconnect();
                            }
                            throw th;
                        }
                    } catch (ZipException e5) {
                        e = e5;
                    } catch (IOException e6) {
                        e = e6;
                    }
                } catch (HttpServerException e7) {
                    Log.e(TAG, "", e7);
                    throw new SubtitleService.ServerException(e7);
                } catch (IOException e8) {
                    Log.e(TAG, "", e8);
                    throw new SubtitleService.NetworkException(e8);
                }
            } catch (MalformedURLException e9) {
                Log.e(TAG, "", e9);
                throw new SubtitleService.ServerDataFormatException(e9);
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public String getRegisterUrl() {
        return "http://www.opensubtitles.org/" + LocaleUtils.fixLanguageCode(Locale.getDefault().getLanguage()) + "/newuser";
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public String getInfoUrl() {
        return "http://www.opensubtitles.org/" + LocaleUtils.fixLanguageCode(Locale.getDefault().getLanguage()) + "/faq";
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public void createAccount(String emailAddress, String username, String password) throws SubtitleService.SubtitleServiceException {
        execute(EXECUTE_SECURE, "UserRegister", emailAddress, password, username);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getHashChunkSize(long fileSize) {
        return (int) Math.min(65536L, fileSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String computeHash(File file) throws IOException {
        long size = file.length();
        long chunkSize = getHashChunkSize(size);
        FileInputStream is = new FileInputStream(file);
        try {
            FileChannel fileChannel = is.getChannel();
            String computeHash = computeHash(size, fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, chunkSize), fileChannel.map(FileChannel.MapMode.READ_ONLY, size - chunkSize, chunkSize));
            fileChannel.close();
            return computeHash;
        } finally {
            is.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String computeHash(long fileSize, ByteBuffer headChunk, ByteBuffer tailChunk) {
        long begin = P.log_opensubtitles ? SystemClock.uptimeMillis() : 0L;
        try {
            long head = computeHashForChunk(headChunk);
            long tail = computeHashForChunk(tailChunk);
            return String.format("%016x", Long.valueOf(fileSize + head + tail));
        } finally {
            if (P.log_opensubtitles) {
                Log.v(TAG, "Hash calculated in " + (SystemClock.uptimeMillis() - begin) + "ms.");
            }
        }
    }

    static String computeHash(InputStream stream, long length) throws IOException {
        int chunkSizeForFile = (int) Math.min(65536L, length);
        byte[] chunkBytes = new byte[(int) Math.min(131072L, length)];
        DataInputStream in = new DataInputStream(stream);
        try {
            in.readFully(chunkBytes, 0, chunkSizeForFile);
            long position = chunkSizeForFile;
            long tailChunkPosition = length - chunkSizeForFile;
            while (position < tailChunkPosition) {
                position += in.skip(tailChunkPosition - position);
                if (position < 0) {
                    break;
                }
            }
            in.readFully(chunkBytes, chunkSizeForFile, chunkBytes.length - chunkSizeForFile);
            long head = computeHashForChunk(ByteBuffer.wrap(chunkBytes, 0, chunkSizeForFile));
            long tail = computeHashForChunk(ByteBuffer.wrap(chunkBytes, chunkBytes.length - chunkSizeForFile, chunkSizeForFile));
            return String.format("%016x", Long.valueOf(length + head + tail));
        } finally {
            in.close();
        }
    }

    private static long computeHashForChunk(ByteBuffer buffer) {
        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        long hash = 0;
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }
        return hash;
    }

    private static String buildLanguageString(Locale[] locales) {
        ArrayList<String> langs = new ArrayList<>();
        for (Locale locale : locales) {
            String lang = getLanguageString(locale);
            if (lang != null && !langs.contains(lang)) {
                langs.add(lang);
            }
        }
        return TextUtils.join(",", langs);
    }

    private static String getLanguageString(Locale locale) {
        String locale2 = locale.toString();
        char c = 65535;
        switch (locale2.hashCode()) {
            case 106983531:
                if (locale2.equals("pt_BR")) {
                    c = 0;
                    break;
                }
                break;
            case 115861276:
                if (locale2.equals("zh_CN")) {
                    c = 1;
                    break;
                }
                break;
            case 115861812:
                if (locale2.equals("zh_TW")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "pob";
            case 1:
                return "chi";
            case 2:
                return "zht";
            default:
                String lang = locale.getLanguage();
                if (lang.length() != 2) {
                    return null;
                }
                if (lang.equals("zh")) {
                    return "zht";
                }
                return LocaleUtils.convert2To3B(lang);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Locale parseLanguageString(String s) {
        char c = 65535;
        switch (s.hashCode()) {
            case 98468:
                if (s.equals("chi")) {
                    c = 1;
                    break;
                }
                break;
            case 111171:
                if (s.equals("pob")) {
                    c = 0;
                    break;
                }
                break;
            case 120567:
                if (s.equals("zhe")) {
                    c = 4;
                    break;
                }
                break;
            case 120582:
                if (s.equals("zht")) {
                    c = 2;
                    break;
                }
                break;
            case 746330349:
                if (s.equals("chinese")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new Locale("pt", "BR");
            case 1:
                return new Locale("zh", "CN");
            case 2:
                return new Locale("zh", "TW");
            case 3:
                Log.w(TAG, "Unexpected language code - " + s);
                break;
            case 4:
                break;
            default:
                return LocaleUtils.parse(s, 2);
        }
        return new Locale("zh");
    }

    private MediaContext getMediaContext(Media media) {
        if (this._mediaContext != null && media.equals(this._mediaContext._media)) {
            return this._mediaContext;
        }
        MediaContext mediaContext = new MediaContext(media);
        this._mediaContext = mediaContext;
        return mediaContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MediaContext {
        private final Media _media;
        public long id;

        MediaContext(Media media) {
            this._media = media;
        }

        void putParameters(Map<String, String> req) {
            String hash = this._media.openSubtitlesMovieHash();
            long size = this._media.size();
            req.put("moviefilename", this._media.fileName);
            if (hash != null) {
                req.put("moviehash", hash);
            }
            if (size > 0) {
                req.put("moviebytesize", Long.toString(size));
            }
            if (this._media.durationMs() > 0) {
                req.put("movietimems", Integer.toString(this._media.durationMs()));
            }
            if (this._media.frameTimeNs() > 0) {
                req.put("moviefps", Double.toString(MediaUtils.frameTimeToFrameRate(this._media.frameTimeNs())));
            }
        }

        long registerMovie(String title, int year) throws SubtitleService.SubtitleServiceException {
            Map<String, String> req = new ArrayMap<>(2);
            req.put("moviename", title);
            req.put("movieyear", Integer.toString(year));
            Map result = OpenSubtitles.execute(OpenSubtitles.EXECUTE_NEED_LOGIN, "InsertMovie", req);
            long integer = OpenSubtitles.getInteger(result, "id");
            this.id = integer;
            return integer;
        }

        void registerFile() throws IllegalStateException, SubtitleService.SubtitleServiceException {
            if (this.id == 0) {
                throw new IllegalStateException();
            }
            Map<String, String> req = new HashMap<>();
            putParameters(req);
            req.put("imdbid", Long.toString(this.id));
            OpenSubtitles.execute(OpenSubtitles.EXECUTE_NEED_LOGIN, "InsertMovieHash", new Object[]{req});
        }

        List<MovieCandidate> searchMovies() throws SubtitleService.SubtitleServiceException {
            String hash = this._media.openSubtitlesMovieHash();
            if (hash != null) {
                List<MovieCandidate> candidates = new ArrayList<>();
                Map result = OpenSubtitles.execute(OpenSubtitles.EXECUTE_NEED_LOGIN, "CheckMovieHash2", new Object[]{hash});
                Object data = result.get(IXMLRPCSerializer.TAG_DATA);
                if (data instanceof Map) {
                    Map dataMap = (Map) data;
                    for (Object entryObj : dataMap.entrySet()) {
                        Map.Entry entry = (Map.Entry) entryObj;
                        if (OpenSubtitles.this.equalHash(hash, entry.getKey())) {
                            Object value = entry.getValue();
                            if (value instanceof Object[]) {
                                Object[] objArr = (Object[]) value;
                                int length = objArr.length;
                                int i = 0;
                                while (true) {
                                    int i2 = i;
                                    if (i2 < length) {
                                        Object item = objArr[i2];
                                        if (item instanceof Map) {
                                            Map itemMap = (Map) item;
                                            String givenTitle = (String) itemMap.get("MovieName");
                                            long IMDbId = OpenSubtitles.getInteger(itemMap, "MovieImdbID", 0L);
                                            if (IMDbId != 0 && givenTitle != null) {
                                                int episode = (int) OpenSubtitles.getInteger(itemMap, "SeriesEpisode", -1L);
                                                if (episode == 0) {
                                                    episode = -1;
                                                }
                                                candidates.add(new MovieCandidate(IMDbId, givenTitle, (int) OpenSubtitles.getInteger(itemMap, "MovieYear", 0L), (int) OpenSubtitles.getInteger(itemMap, "SeriesSeason", 0L), episode));
                                            }
                                        }
                                        i = i2 + 1;
                                    }
                                }
                            }
                        }
                    }
                    return candidates;
                }
                return candidates;
            }
            return OpenSubtitles.this.searchMovies(this._media.fileName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean equalHash(String hash1, Object hash2) {
        if (hash2 instanceof String) {
            return hash1.equalsIgnoreCase((String) hash2);
        }
        try {
            return Long.parseLong(hash1) == getInteger(hash2, 0L);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private SubtitleContext getSubtitleContext(Subtitle subtitle) throws SubtitleService.SubtitleFileReadException, SubtitleService.SubtitleFileEmptyException, SubtitleService.SubtitleFileTooLargeException {
        if (this._subtitleContext != null && subtitle.equals(this._subtitleContext._subtitle)) {
            return this._subtitleContext;
        }
        SubtitleContext subtitleContext = new SubtitleContext(subtitle);
        this._subtitleContext = subtitleContext;
        return subtitleContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SubtitleContext {
        private final byte[] _buffer = new byte[8192];
        private String _hash;
        private final Subtitle _subtitle;
        public long id;

        SubtitleContext(Subtitle subtitle) throws SubtitleService.SubtitleFileReadException, SubtitleService.SubtitleFileEmptyException, SubtitleService.SubtitleFileTooLargeException {
            if (!subtitle.exists()) {
                throw new SubtitleService.SubtitleFileReadException();
            }
            int size = subtitle.size();
            if (size == 0) {
                throw new SubtitleService.SubtitleFileEmptyException();
            }
            if (size > 20971520) {
                throw new SubtitleService.SubtitleFileTooLargeException();
            }
            this._subtitle = subtitle;
        }

        void putParameters(Map<String, String> req) throws SubtitleService.SubtitleFileReadException, SubtitleService.LocalException {
            if (this._hash == null) {
                try {
                    InputStream fs = this._subtitle.content();
                    int totalRead = 0;
                    try {
                        MessageDigest digester = MessageDigest.getInstance("MD5");
                        digester.reset();
                        while (true) {
                            int numRead = fs.read(this._buffer);
                            if (numRead < 0) {
                                break;
                            }
                            totalRead += numRead;
                            digester.update(this._buffer, 0, numRead);
                        }
                        if (totalRead == 0) {
                            throw new SubtitleService.SubtitleFileReadException("Can't read subtitle " + this._subtitle);
                        }
                        this._hash = CharUtils.bytesToHexString(digester.digest()).toLowerCase(Locale.US);
                    } finally {
                        fs.close();
                    }
                } catch (IOException e) {
                    throw new SubtitleService.SubtitleFileReadException(e);
                } catch (NoSuchAlgorithmException e2) {
                    throw new SubtitleService.LocalException(e2);
                }
            }
            req.put("subhash", this._hash);
            req.put("subfilename", this._subtitle.filename());
        }

        @TargetApi(8)
        void putContent(Map<String, String> req) throws SubtitleService.SubtitleFileReadException {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream((this._subtitle.size() * 2) / 3);
                Base64OutputStream out = new Base64OutputStream(byteOut, 3);
                int totalRead = 0;
                InputStream fs = this._subtitle.content();
                while (true) {
                    int numRead = fs.read(this._buffer);
                    if (numRead < 0) {
                        break;
                    }
                    totalRead += numRead;
                    out.write(this._buffer, 0, numRead);
                }
                if (totalRead == 0) {
                    throw new SubtitleService.SubtitleFileReadException("Can't read subtitle " + this._subtitle);
                }
                fs.close();
                out.close();
                req.put("subcontent", byteOut.toString("ASCII"));
            } catch (IOException e) {
                throw new SubtitleService.SubtitleFileReadException(e);
            }
        }
    }

    private boolean checkExistence(MediaContext mediaContext, SubtitleContext subtitleContext) throws SubtitleService.SubtitleServiceException {
        Object IMDbId;
        Object obj;
        ArrayMap<String, Map<String, String>> req = new ArrayMap<>(1);
        HashMap<String, String> cd1 = new HashMap<>();
        mediaContext.putParameters(cd1);
        subtitleContext.putParameters(cd1);
        req.put("cd1", cd1);
        Map result = execute(EXECUTE_NEED_LOGIN, "TryUploadSubtitles", req);
        long alreadyInDb = getInteger(result, "alreadyindb", 0L);
        boolean exist = alreadyInDb == 1;
        Object data = result.get(IXMLRPCSerializer.TAG_DATA);
        if (data != null && !(data instanceof Boolean)) {
            if (data instanceof Map) {
                Map dataMap = (Map) data;
                IMDbId = dataMap.get("IDMovieImdb");
                obj = dataMap.get("IDSubtitle");
            } else if ((data instanceof Object[]) && ((Object[]) data).length > 0) {
                Map dataMap2 = (Map) ((Object[]) data)[0];
                IMDbId = dataMap2.get("IDMovieImdb");
                obj = dataMap2.get("IDSubtitle");
            } else {
                IMDbId = null;
                obj = null;
            }
            if (IMDbId != null) {
                mediaContext.id = getInteger(IMDbId, 0L);
            }
            if (obj != null) {
                subtitleContext.id = getInteger(obj, 0L);
            }
        }
        return exist;
    }

    @TargetApi(8)
    public static Locale[] detectLanguage(Subtitle[] subtitles) throws SubtitleService.SubtitleServiceException {
        byte[] buffer = new byte[4096];
        int numSubs = subtitles.length;
        ArrayList<String> texts = new ArrayList<>(numSubs);
        Locale[] locales = new Locale[numSubs];
        String[] digests = new String[numSubs];
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(174762);
            for (int i = 0; i < numSubs; i++) {
                try {
                    digester.reset();
                    byteOut.reset();
                    InputStream in = subtitles[i].content();
                    Base64OutputStream out = new Base64OutputStream(byteOut, 3);
                    int total = 0;
                    do {
                        try {
                            int read = in.read(buffer);
                            if (read < 0) {
                                break;
                            }
                            total += read;
                            digester.update(buffer, 0, read);
                            out.write(buffer, 0, read);
                        } catch (Throwable th) {
                            in.close();
                            throw th;
                            break;
                        }
                    } while (total < 262144);
                    if (total != 0) {
                        digests[i] = CharUtils.bytesToHexString(digester.digest()).toLowerCase(Locale.US);
                        out.close();
                        texts.add(byteOut.toString("ASCII"));
                        if (P.log_opensubtitles) {
                            Log.d(TAG, subtitles[i].sourceUri + " ==MD5==> " + digests[i]);
                        }
                        in.close();
                    } else {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (texts.size() > 0) {
                Map result = execute(EXECUTE_NEED_LOGIN, "DetectLanguage", texts);
                Object data = result.get(IXMLRPCSerializer.TAG_DATA);
                if (data instanceof Map) {
                    Map dataMap = (Map) data;
                    for (Object entryObj : dataMap.entrySet()) {
                        Map.Entry<String, String> entry = (Map.Entry) entryObj;
                        String digest = entry.getKey();
                        String lang = entry.getValue();
                        for (int i2 = 0; i2 < numSubs; i2++) {
                            if (digests[i2].equalsIgnoreCase(digest)) {
                                locales[i2] = parseLanguageString(lang);
                            }
                        }
                    }
                }
            }
            return locales;
        } catch (NoSuchAlgorithmException e2) {
            Log.e(TAG, "", e2);
            throw new SubtitleService.LocalException(e2);
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleService
    public boolean isSupported(String filename) {
        int id = SubtitleFactory.getExtensionId(filename);
        switch (id) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 7:
                return true;
            case 5:
            default:
                return false;
        }
    }
}
