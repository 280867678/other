package com.mxtech.videoplayer.preference;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import com.mxtech.app.AppUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.list.MediaListFragment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import org.xmlrpc.android.IXMLRPCSerializer;

/* loaded from: classes2.dex */
public final class Serializer {
    private static final String ATTR_APPLICATION = "application";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_OPTION = "option";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_VERSION_CODE = "version-code";
    private static final String ATTR_VERSION_NAME = "version-name";
    private static final String ATTR_audioDecoder = "audioDecoder";
    private static final String ATTR_audioOffset = "audioOffset";
    private static final String ATTR_audioStream = "audioStream";
    private static final String ATTR_decoder = "decoder";
    private static final String ATTR_decodingOption = "decodingOption";
    private static final String ATTR_enabled = "enabled";
    private static final String ATTR_finishTime = "finishTime";
    private static final String ATTR_horzRatio = "horzRatio";
    private static final String ATTR_input = "input";
    private static final String ATTR_lastWatchTime = "lastWatchTime";
    private static final String ATTR_name = "name";
    private static final String ATTR_panX = "panX";
    private static final String ATTR_panY = "panY";
    private static final String ATTR_playbackSpeed = "playbackSpeed";
    private static final String ATTR_position = "position";
    private static final String ATTR_process = "process";
    private static final String ATTR_query = "query";
    private static final String ATTR_repeatA = "repeatA";
    private static final String ATTR_repeatB = "repeatB";
    private static final String ATTR_subtitleSpeed = "subtitleSpeed";
    private static final String ATTR_subtitleSync = "subtitleSync";
    private static final String ATTR_time = "time";
    private static final String ATTR_typename = "typename";
    private static final String ATTR_uri = "uri";
    private static final String ATTR_vertRatio = "vertRatio";
    private static final String ATTR_zoomHeight = "zoomHeight";
    private static final String ATTR_zoomWidth = "zoomWidth";
    private static final String COMPONENT_AUDIOPLAYER = "audio_player";
    private static final String COMPONENT_PLAYLINK = "play_link";
    public static final int DIRECT_OPEN_LOG = 8;
    public static final int MOVIE_TITLE_LOG = 32;
    public static final int PLAYBACK_STATES = 2;
    public static final int PREFERENCES = 1;
    private static final int PROC_DIRECT_OPEN_LOG = 6;
    private static final int PROC_FILE_EXTENSIONS = 2;
    private static final int PROC_MOVIE_TITLE_LOG = 8;
    private static final int PROC_NONE = 0;
    private static final int PROC_PLAYBACK_RECORDS = 4;
    private static final int PROC_PREFERENCES = 1;
    private static final int PROC_SEARCH_HISTORY = 5;
    private static final int PROC_SUBTITLE_QUERY_LOG = 7;
    private static final int PROC_VIDEO_STATES = 3;
    public static final int RECORDS = 62;
    public static final int SEARCH_HISTORY = 4;
    public static final int SUBTITLE_QUERY_LOG = 16;
    private static final String TAG_BOOLEAN = "bool";
    private static final String TAG_COMPONENT = "component";
    private static final String TAG_DIRECT_OPEN_LOG = "direct-open-log";
    private static final String TAG_EXTENSION = "ext";
    private static final String TAG_FILE_EXTENSIONS = "file-extentions";
    private static final String TAG_FLOAT = "float";
    private static final String TAG_HEAD = "mx-player";
    private static final String TAG_INTEGER = "int";
    private static final String TAG_LONG = "long";
    private static final String TAG_MOVIE_TITLE_LOG = "movie-title-log";
    private static final String TAG_PLAYBACK_RECORDS = "playback-records";
    private static final String TAG_PREFERENCES = "preferences";
    private static final String TAG_RECORD = "record";
    private static final String TAG_SEARCH_HISTORY = "search-history";
    private static final String TAG_STATE = "state";
    private static final String TAG_STRING = "string";
    private static final String TAG_SUBTITLE = "subtitle";
    private static final String TAG_SUBTITLE_QUERY_LOG = "subtitle-query-log";
    private static final String TAG_VIDEO_STATES = "video-states";
    private static final String TAG = App.TAG + ".Serializer";
    private static final String[] NON_EXPORTABLE_PREFS = {"drt", "drt_ts", "App Restrictions"};

    /* loaded from: classes2.dex */
    public static class ExportFileTestResult {
        String application;
        int contains;
        int version;
    }

    public static boolean exportTo(File file, int contains) {
        MediaDatabase.State.Subtitle[] subtitleArr;
        int i;
        String tag;
        long begin = SystemClock.uptimeMillis();
        PackageInfo myPackage = L.getMyPackageInfo();
        XmlSerializer s = Xml.newSerializer();
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(file));
            s.setOutput(w);
            s.startDocument("UTF-8", true);
            s.startTag("", TAG_HEAD);
            s.attribute("", ATTR_APPLICATION, App.context.getString(R.string.app_name_base));
            s.attribute("", ATTR_VERSION_CODE, Integer.toString(AppUtils.getVersion(myPackage.versionCode)));
            s.attribute("", ATTR_VERSION_NAME, myPackage.versionName);
            if ((contains & 1) != 0) {
                s.startTag("", TAG_PREFERENCES);
                for (Map.Entry<String, ?> entry : App.prefs.getAll().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null && key.length() != 0 && key.charAt(0) != '.') {
                        String[] strArr = NON_EXPORTABLE_PREFS;
                        int length = strArr.length;
                        while (true) {
                            if (i < length) {
                                String skip = strArr[i];
                                i = skip.equals(key) ? 0 : i + 1;
                            } else {
                                if (value instanceof Boolean) {
                                    tag = TAG_BOOLEAN;
                                } else if (value instanceof Integer) {
                                    tag = "int";
                                } else if (value instanceof String) {
                                    tag = "string";
                                } else if (value instanceof Float) {
                                    tag = TAG_FLOAT;
                                } else if (value instanceof Long) {
                                    tag = TAG_LONG;
                                } else {
                                    Log.w(TAG, "Unknown preference type - key:" + key + " value:" + value + " type:" + (value != null ? value.getClass() : ""));
                                }
                                s.startTag("", tag);
                                s.attribute("", IXMLRPCSerializer.TAG_NAME, key);
                                s.attribute("", "value", value.toString());
                                s.endTag("", tag);
                            }
                        }
                    }
                }
                s.startTag("", TAG_COMPONENT);
                s.attribute("", IXMLRPCSerializer.TAG_NAME, COMPONENT_PLAYLINK);
                s.attribute("", "value", Boolean.toString(P.isPlayLink()));
                s.endTag("", TAG_COMPONENT);
                s.startTag("", TAG_COMPONENT);
                s.attribute("", IXMLRPCSerializer.TAG_NAME, "audio_player");
                s.attribute("", "value", Boolean.toString(P.isAudioPlayer));
                s.endTag("", TAG_COMPONENT);
                s.endTag("", TAG_PREFERENCES);
                SharedPreferences prefs = App.context.getSharedPreferences("video_exts", 0);
                Map<String, ?> extsMap = prefs.getAll();
                if (extsMap.size() > 0) {
                    s.startTag("", TAG_FILE_EXTENSIONS);
                    for (Map.Entry<String, ?> entry2 : extsMap.entrySet()) {
                        Object value2 = entry2.getValue();
                        if (value2 instanceof Integer) {
                            s.startTag("", TAG_EXTENSION);
                            s.attribute("", IXMLRPCSerializer.TAG_NAME, entry2.getKey());
                            s.attribute("", ATTR_OPTION, value2.toString());
                            s.endTag("", TAG_EXTENSION);
                        }
                    }
                    s.endTag("", TAG_FILE_EXTENSIONS);
                }
            }
            if ((contains & 62) != 0) {
                MediaDatabase mdb = MediaDatabase.getInstance();
                if ((contains & 2) != 0) {
                    try {
                        s.startTag("", TAG_VIDEO_STATES);
                        for (MediaDatabase.FullState state : mdb.readAllState()) {
                            s.startTag("", TAG_STATE);
                            s.attribute("", "uri", state.uri.toString());
                            s.attribute("", "position", Integer.toString(state.position));
                            s.attribute("", ATTR_decoder, Byte.toString(state.decoder));
                            s.attribute("", ATTR_decodingOption, Integer.toString(state.decodingOption));
                            s.attribute("", ATTR_audioDecoder, Byte.toString(state.audioDecoder));
                            s.attribute("", ATTR_audioStream, Short.toString(state.audioStream));
                            s.attribute("", ATTR_audioOffset, Integer.toString(state.audioOffset));
                            s.attribute("", ATTR_subtitleSync, Integer.toString(state.subtitleOffset));
                            s.attribute("", ATTR_subtitleSpeed, Double.toString(state.subtitleSpeed));
                            s.attribute("", ATTR_playbackSpeed, Double.toString(state.playbackSpeed));
                            s.attribute("", ATTR_horzRatio, Float.toString(state.horzRatio));
                            s.attribute("", ATTR_vertRatio, Float.toString(state.vertRatio));
                            s.attribute("", ATTR_zoomWidth, Short.toString(state.zoomWidth));
                            s.attribute("", ATTR_zoomHeight, Short.toString(state.zoomHeight));
                            s.attribute("", ATTR_panX, Short.toString(state.panX));
                            s.attribute("", ATTR_panY, Short.toString(state.panY));
                            s.attribute("", ATTR_process, Integer.toString(state.process));
                            s.attribute("", ATTR_repeatA, Integer.toString(state.repeatA));
                            s.attribute("", ATTR_repeatB, Integer.toString(state.repeatB));
                            if (state.subtitles != null) {
                                for (MediaDatabase.State.Subtitle subtitle : state.subtitles) {
                                    s.startTag("", TAG_SUBTITLE);
                                    s.attribute("", "uri", subtitle.uri.toString());
                                    if (subtitle.name != null) {
                                        s.attribute("", IXMLRPCSerializer.TAG_NAME, subtitle.name);
                                    }
                                    if (subtitle.typename != null) {
                                        s.attribute("", ATTR_typename, subtitle.typename);
                                    }
                                    s.attribute("", ATTR_enabled, Boolean.toString(subtitle.enabled));
                                    s.endTag("", TAG_SUBTITLE);
                                }
                            }
                            s.endTag("", TAG_STATE);
                        }
                        s.endTag("", TAG_VIDEO_STATES);
                        s.startTag("", TAG_PLAYBACK_RECORDS);
                        for (MediaDatabase.FullPlaybackRecord record : mdb.readAllPlaybackRecords()) {
                            s.startTag("", TAG_RECORD);
                            s.attribute("", "uri", record.uri.toString());
                            s.attribute("", ATTR_lastWatchTime, Long.toString(record.lastWatchTime));
                            s.attribute("", ATTR_finishTime, Long.toString(record.finishTime));
                            s.endTag("", TAG_RECORD);
                        }
                        s.endTag("", TAG_PLAYBACK_RECORDS);
                    } finally {
                        mdb.release();
                    }
                }
                if ((contains & 4) != 0) {
                    s.startTag("", TAG_SEARCH_HISTORY);
                    for (MediaDatabase.SearchRecord record2 : mdb.readAllSearchRecords()) {
                        s.startTag("", TAG_RECORD);
                        s.attribute("", "query", record2.query);
                        s.attribute("", ATTR_time, Long.toString(record2.time));
                        s.endTag("", TAG_RECORD);
                    }
                    s.endTag("", TAG_SEARCH_HISTORY);
                }
                if ((contains & 8) != 0) {
                    s.startTag("", TAG_DIRECT_OPEN_LOG);
                    for (MediaDatabase.SimpleLog record3 : mdb.readAllDirectOpenLogs()) {
                        s.startTag("", TAG_RECORD);
                        s.attribute("", ATTR_input, record3.input);
                        s.attribute("", ATTR_time, Long.toString(record3.time));
                        s.endTag("", TAG_RECORD);
                    }
                    s.endTag("", TAG_DIRECT_OPEN_LOG);
                }
                if ((contains & 16) != 0) {
                    s.startTag("", TAG_SUBTITLE_QUERY_LOG);
                    for (MediaDatabase.SimpleLog record4 : mdb.readAllSubtitleQueryLogs()) {
                        s.startTag("", TAG_RECORD);
                        s.attribute("", ATTR_input, record4.input);
                        s.attribute("", ATTR_time, Long.toString(record4.time));
                        s.endTag("", TAG_RECORD);
                    }
                    s.endTag("", TAG_SUBTITLE_QUERY_LOG);
                }
                if ((contains & 32) != 0) {
                    s.startTag("", TAG_MOVIE_TITLE_LOG);
                    for (MediaDatabase.SimpleLog record5 : mdb.readAllMovieTitleLogs()) {
                        s.startTag("", TAG_RECORD);
                        s.attribute("", ATTR_input, record5.input);
                        s.attribute("", ATTR_time, Long.toString(record5.time));
                        s.endTag("", TAG_RECORD);
                    }
                    s.endTag("", TAG_MOVIE_TITLE_LOG);
                }
            }
            s.endTag("", TAG_HEAD);
            s.endDocument();
            Log.d(TAG, "Export finished. file:" + file + " material:" + contains + " (" + (SystemClock.uptimeMillis() - begin) + "ms)");
            w.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public static ExportFileTestResult testExportedFile(File file) {
        ExportFileTestResult result;
        XmlPullParser p = Xml.newPullParser();
        int depth = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                p.setInput(reader);
                int eventType = p.getEventType();
                ExportFileTestResult result2 = null;
                while (true) {
                    try {
                        if (eventType != 1) {
                            switch (eventType) {
                                case 2:
                                    try {
                                        String tagName = p.getName();
                                        if (depth == 0) {
                                            if (TAG_HEAD.equals(tagName)) {
                                                result = new ExportFileTestResult();
                                                int numAttr = p.getAttributeCount();
                                                for (int i = 0; i < numAttr; i++) {
                                                    String attrName = p.getAttributeName(i);
                                                    if (ATTR_APPLICATION.equals(attrName)) {
                                                        result.application = p.getAttributeValue(i);
                                                    } else if (ATTR_VERSION_CODE.equals(attrName)) {
                                                        result.version = Integer.parseInt(p.getAttributeValue(i));
                                                    }
                                                }
                                                depth++;
                                                break;
                                            }
                                            result = result2;
                                            depth++;
                                        } else {
                                            if (depth == 1) {
                                                if (result2 == null) {
                                                    reader.close();
                                                    result2 = null;
                                                    break;
                                                } else {
                                                    char c = 65535;
                                                    switch (tagName.hashCode()) {
                                                        case -1421331564:
                                                            if (tagName.equals(TAG_VIDEO_STATES)) {
                                                                c = 2;
                                                                break;
                                                            }
                                                            break;
                                                        case -634850170:
                                                            if (tagName.equals(TAG_FILE_EXTENSIONS)) {
                                                                c = 1;
                                                                break;
                                                            }
                                                            break;
                                                        case -159185553:
                                                            if (tagName.equals(TAG_SEARCH_HISTORY)) {
                                                                c = 4;
                                                                break;
                                                            }
                                                            break;
                                                        case 884360869:
                                                            if (tagName.equals(TAG_DIRECT_OPEN_LOG)) {
                                                                c = 5;
                                                                break;
                                                            }
                                                            break;
                                                        case 975146128:
                                                            if (tagName.equals(TAG_PLAYBACK_RECORDS)) {
                                                                c = 3;
                                                                break;
                                                            }
                                                            break;
                                                        case 1981119818:
                                                            if (tagName.equals(TAG_SUBTITLE_QUERY_LOG)) {
                                                                c = 6;
                                                                break;
                                                            }
                                                            break;
                                                        case 1989861112:
                                                            if (tagName.equals(TAG_PREFERENCES)) {
                                                                c = 0;
                                                                break;
                                                            }
                                                            break;
                                                        case 2040345586:
                                                            if (tagName.equals(TAG_MOVIE_TITLE_LOG)) {
                                                                c = 7;
                                                                break;
                                                            }
                                                            break;
                                                    }
                                                    switch (c) {
                                                        case 0:
                                                        case 1:
                                                            result2.contains |= 1;
                                                            result = result2;
                                                            break;
                                                        case 2:
                                                        case 3:
                                                            result2.contains |= 2;
                                                            result = result2;
                                                            break;
                                                        case 4:
                                                            result2.contains |= 4;
                                                            result = result2;
                                                            break;
                                                        case 5:
                                                            result2.contains |= 8;
                                                            result = result2;
                                                            break;
                                                        case 6:
                                                            result2.contains |= 16;
                                                            result = result2;
                                                            break;
                                                        case 7:
                                                            result2.contains |= 32;
                                                        default:
                                                            result = result2;
                                                            break;
                                                    }
                                                    depth++;
                                                }
                                            }
                                            result = result2;
                                            depth++;
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        reader.close();
                                        throw th;
                                    }
                                case 3:
                                    depth--;
                                    result = result2;
                                    break;
                                default:
                                    result = result2;
                                    break;
                            }
                            eventType = p.next();
                            result2 = result;
                        } else {
                            reader.close();
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.e(TAG, "", e);
                        return null;
                    }
                }
                return result2;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:210:0x042f A[Catch: Exception -> 0x0439, all -> 0x08fd, TryCatch #5 {Exception -> 0x0439, blocks: (B:3:0x0014, B:208:0x042a, B:210:0x042f, B:211:0x0438), top: B:415:0x0014 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean importFrom(File file, int contains) {
        boolean z;
        String scheme;
        XmlPullParser p = Xml.newPullParser();
        int depth = 0;
        int proc = 0;
        boolean headFound = false;
        SharedPreferences.Editor edit = null;
        MediaDatabase mdb = null;
        MediaDatabase.State mediaState = null;
        List<MediaDatabase.State.Subtitle> mediaSubtitles = null;
        Uri mediaUri = null;
        boolean audioPlayer = false;
        boolean playLink = true;
        try {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                try {
                    p.setInput(reader);
                    int eventType = p.getEventType();
                    while (true) {
                        List<MediaDatabase.State.Subtitle> mediaSubtitles2 = mediaSubtitles;
                        MediaDatabase.State mediaState2 = mediaState;
                        try {
                            if (eventType != 1) {
                                switch (eventType) {
                                    case 2:
                                        try {
                                            String tag = p.getName();
                                            if (depth == 0) {
                                                if (TAG_HEAD.equals(tag)) {
                                                    headFound = true;
                                                    if ((contains & 62) != 0) {
                                                        mdb = MediaDatabase.getInstance();
                                                        mdb.db.beginTransaction();
                                                        mediaSubtitles = mediaSubtitles2;
                                                        mediaState = mediaState2;
                                                        depth++;
                                                        break;
                                                    }
                                                }
                                                mediaSubtitles = mediaSubtitles2;
                                                mediaState = mediaState2;
                                                depth++;
                                            } else {
                                                if (depth == 1) {
                                                    if (!headFound) {
                                                        z = false;
                                                        reader.close();
                                                        if (mdb != null) {
                                                            mdb.db.endTransaction();
                                                        }
                                                        if (mdb != null) {
                                                            mdb.release();
                                                        }
                                                        break;
                                                    } else {
                                                        proc = 0;
                                                        char c = 65535;
                                                        switch (tag.hashCode()) {
                                                            case -1421331564:
                                                                if (tag.equals(TAG_VIDEO_STATES)) {
                                                                    c = 2;
                                                                    break;
                                                                }
                                                                break;
                                                            case -634850170:
                                                                if (tag.equals(TAG_FILE_EXTENSIONS)) {
                                                                    c = 1;
                                                                    break;
                                                                }
                                                                break;
                                                            case -159185553:
                                                                if (tag.equals(TAG_SEARCH_HISTORY)) {
                                                                    c = 4;
                                                                    break;
                                                                }
                                                                break;
                                                            case 884360869:
                                                                if (tag.equals(TAG_DIRECT_OPEN_LOG)) {
                                                                    c = 5;
                                                                    break;
                                                                }
                                                                break;
                                                            case 975146128:
                                                                if (tag.equals(TAG_PLAYBACK_RECORDS)) {
                                                                    c = 3;
                                                                    break;
                                                                }
                                                                break;
                                                            case 1981119818:
                                                                if (tag.equals(TAG_SUBTITLE_QUERY_LOG)) {
                                                                    c = 6;
                                                                    break;
                                                                }
                                                                break;
                                                            case 1989861112:
                                                                if (tag.equals(TAG_PREFERENCES)) {
                                                                    c = 0;
                                                                    break;
                                                                }
                                                                break;
                                                            case 2040345586:
                                                                if (tag.equals(TAG_MOVIE_TITLE_LOG)) {
                                                                    c = 7;
                                                                    break;
                                                                }
                                                                break;
                                                        }
                                                        switch (c) {
                                                            case 0:
                                                                if ((contains & 1) != 0) {
                                                                    proc = 1;
                                                                    edit = App.prefs.edit();
                                                                    break;
                                                                }
                                                                break;
                                                            case 1:
                                                                if ((contains & 1) != 0) {
                                                                    proc = 2;
                                                                    edit = App.context.getSharedPreferences("video_exts", 0).edit();
                                                                    break;
                                                                }
                                                                break;
                                                            case 2:
                                                                if ((contains & 2) != 0) {
                                                                    proc = 3;
                                                                    break;
                                                                }
                                                                break;
                                                            case 3:
                                                                if ((contains & 2) != 0) {
                                                                    proc = 4;
                                                                    break;
                                                                }
                                                                break;
                                                            case 4:
                                                                if ((contains & 4) != 0) {
                                                                    proc = 5;
                                                                    break;
                                                                }
                                                                break;
                                                            case 5:
                                                                if ((contains & 8) != 0) {
                                                                    proc = 6;
                                                                    break;
                                                                }
                                                                break;
                                                            case 6:
                                                                if ((contains & 16) != 0) {
                                                                    proc = 7;
                                                                    break;
                                                                }
                                                                break;
                                                            case 7:
                                                                if ((contains & 32) != 0) {
                                                                    proc = 8;
                                                                    break;
                                                                }
                                                                break;
                                                        }
                                                        mediaSubtitles = mediaSubtitles2;
                                                        mediaState = mediaState2;
                                                    }
                                                } else if (depth == 2) {
                                                    switch (proc) {
                                                        case 1:
                                                            String value = null;
                                                            String str = null;
                                                            for (int i = p.getAttributeCount() - 1; i >= 0; i--) {
                                                                String attr = p.getAttributeName(i);
                                                                if (IXMLRPCSerializer.TAG_NAME.equals(attr)) {
                                                                    str = p.getAttributeValue(i);
                                                                } else if ("value".equals(attr)) {
                                                                    value = p.getAttributeValue(i);
                                                                }
                                                            }
                                                            if (str != null && value != null) {
                                                                String tag2 = p.getName();
                                                                if (TAG_BOOLEAN.equals(tag2)) {
                                                                    edit.putBoolean(str, Boolean.parseBoolean(value));
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                } else if ("int".equals(tag2)) {
                                                                    edit.putInt(str, Integer.parseInt(value));
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                } else if (TAG_LONG.equals(tag2)) {
                                                                    edit.putLong(str, Long.parseLong(value));
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                } else if (TAG_FLOAT.equals(tag2)) {
                                                                    edit.putFloat(str, Float.parseFloat(value));
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                } else if ("string".equals(tag2)) {
                                                                    edit.putString(str, value);
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                } else if (TAG_COMPONENT.equals(tag2)) {
                                                                    if (COMPONENT_PLAYLINK.equals(str)) {
                                                                        playLink = Boolean.parseBoolean(value);
                                                                        mediaState = mediaState2;
                                                                        break;
                                                                    } else if ("audio_player".equals(str)) {
                                                                        audioPlayer = Boolean.parseBoolean(value);
                                                                        mediaState = mediaState2;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 2:
                                                            if (TAG_EXTENSION.equals(p.getName())) {
                                                                String value2 = null;
                                                                String str2 = null;
                                                                for (int i2 = p.getAttributeCount() - 1; i2 >= 0; i2--) {
                                                                    String attr2 = p.getAttributeName(i2);
                                                                    if (IXMLRPCSerializer.TAG_NAME.equals(attr2)) {
                                                                        str2 = p.getAttributeValue(i2);
                                                                    } else if (ATTR_OPTION.equals(attr2)) {
                                                                        value2 = p.getAttributeValue(i2);
                                                                    }
                                                                }
                                                                if (str2 != null && value2 != null) {
                                                                    edit.putInt(str2, Integer.parseInt(value2));
                                                                    mediaState = mediaState2;
                                                                    break;
                                                                }
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 3:
                                                            if (TAG_STATE.equals(p.getName())) {
                                                                mediaUri = null;
                                                                mediaState = new MediaDatabase.State();
                                                                try {
                                                                    for (int i3 = p.getAttributeCount() - 1; i3 >= 0; i3--) {
                                                                        String attr3 = p.getAttributeName(i3);
                                                                        String value3 = p.getAttributeValue(i3);
                                                                        char c2 = 65535;
                                                                        switch (attr3.hashCode()) {
                                                                            case -2044522797:
                                                                                if (attr3.equals(ATTR_subtitleSync)) {
                                                                                    c2 = 7;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -1836772480:
                                                                                if (attr3.equals(ATTR_decodingOption)) {
                                                                                    c2 = 3;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -1445557316:
                                                                                if (attr3.equals(ATTR_horzRatio)) {
                                                                                    c2 = '\n';
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -1400125094:
                                                                                if (attr3.equals(ATTR_vertRatio)) {
                                                                                    c2 = 11;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -973916454:
                                                                                if (attr3.equals(ATTR_zoomHeight)) {
                                                                                    c2 = '\r';
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -930295060:
                                                                                if (attr3.equals(ATTR_playbackSpeed)) {
                                                                                    c2 = '\t';
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -309518737:
                                                                                if (attr3.equals(ATTR_process)) {
                                                                                    c2 = 16;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case -43922546:
                                                                                if (attr3.equals(ATTR_audioDecoder)) {
                                                                                    c2 = 4;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 116076:
                                                                                if (attr3.equals("uri")) {
                                                                                    c2 = 0;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 3433307:
                                                                                if (attr3.equals(ATTR_panX)) {
                                                                                    c2 = 14;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 3433308:
                                                                                if (attr3.equals(ATTR_panY)) {
                                                                                    c2 = 15;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 314520585:
                                                                                if (attr3.equals(ATTR_audioOffset)) {
                                                                                    c2 = 6;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 442310390:
                                                                                if (attr3.equals(ATTR_audioStream)) {
                                                                                    c2 = 5;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 747804969:
                                                                                if (attr3.equals("position")) {
                                                                                    c2 = 1;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 1044026127:
                                                                                if (attr3.equals(ATTR_subtitleSpeed)) {
                                                                                    c2 = '\b';
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 1094288902:
                                                                                if (attr3.equals(ATTR_repeatA)) {
                                                                                    c2 = 17;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 1094288903:
                                                                                if (attr3.equals(ATTR_repeatB)) {
                                                                                    c2 = 18;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 1542433860:
                                                                                if (attr3.equals(ATTR_decoder)) {
                                                                                    c2 = 2;
                                                                                    break;
                                                                                }
                                                                                break;
                                                                            case 1645118899:
                                                                                if (attr3.equals(ATTR_zoomWidth)) {
                                                                                    c2 = '\f';
                                                                                    break;
                                                                                }
                                                                                break;
                                                                        }
                                                                        switch (c2) {
                                                                            case 0:
                                                                                mediaUri = Uri.parse(value3);
                                                                                break;
                                                                            case 1:
                                                                                mediaState.position = Integer.parseInt(value3);
                                                                                break;
                                                                            case 2:
                                                                                mediaState.decoder = Byte.parseByte(value3);
                                                                                break;
                                                                            case 3:
                                                                                mediaState.decodingOption = Integer.parseInt(value3);
                                                                                break;
                                                                            case 4:
                                                                                mediaState.audioDecoder = Byte.parseByte(value3);
                                                                                break;
                                                                            case 5:
                                                                                mediaState.audioStream = Short.parseShort(value3);
                                                                                break;
                                                                            case 6:
                                                                                mediaState.audioOffset = Integer.parseInt(value3);
                                                                                break;
                                                                            case 7:
                                                                                mediaState.subtitleOffset = Integer.parseInt(value3);
                                                                                break;
                                                                            case '\b':
                                                                                mediaState.subtitleSpeed = Double.parseDouble(value3);
                                                                                break;
                                                                            case '\t':
                                                                                mediaState.playbackSpeed = Double.parseDouble(value3);
                                                                                break;
                                                                            case '\n':
                                                                                mediaState.horzRatio = Float.parseFloat(value3);
                                                                                break;
                                                                            case 11:
                                                                                mediaState.vertRatio = Float.parseFloat(value3);
                                                                                break;
                                                                            case '\f':
                                                                                mediaState.zoomWidth = Short.parseShort(value3);
                                                                                break;
                                                                            case '\r':
                                                                                mediaState.zoomHeight = Short.parseShort(value3);
                                                                                break;
                                                                            case 14:
                                                                                mediaState.panX = Short.parseShort(value3);
                                                                                break;
                                                                            case 15:
                                                                                mediaState.panY = Short.parseShort(value3);
                                                                                break;
                                                                            case 16:
                                                                                mediaState.process = Integer.parseInt(value3);
                                                                                break;
                                                                            case 17:
                                                                                mediaState.repeatA = Integer.parseInt(value3);
                                                                                break;
                                                                            case 18:
                                                                                mediaState.repeatB = Integer.parseInt(value3);
                                                                                break;
                                                                        }
                                                                    }
                                                                    if (mediaUri == null) {
                                                                        mediaState = null;
                                                                        break;
                                                                    }
                                                                } catch (Throwable th) {
                                                                    th = th;
                                                                    reader.close();
                                                                    if (mdb != null) {
                                                                        mdb.db.endTransaction();
                                                                    }
                                                                    throw th;
                                                                }
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 4:
                                                            if (TAG_RECORD.equals(p.getName())) {
                                                                Uri uri = null;
                                                                long lastWatchTime = 0;
                                                                long finishTime = 0;
                                                                for (int i4 = p.getAttributeCount() - 1; i4 >= 0; i4--) {
                                                                    String attr4 = p.getAttributeName(i4);
                                                                    String value4 = p.getAttributeValue(i4);
                                                                    if ("uri".equals(attr4)) {
                                                                        uri = Uri.parse(value4);
                                                                    } else if (ATTR_lastWatchTime.equals(attr4)) {
                                                                        lastWatchTime = Long.parseLong(value4);
                                                                    } else if (ATTR_finishTime.equals(attr4)) {
                                                                        finishTime = Long.parseLong(value4);
                                                                    }
                                                                }
                                                                if (uri != null && ((scheme = uri.getScheme()) == null || MediaListFragment.TYPE_FILE.equals(scheme))) {
                                                                    File mediaFile = new File(uri.getPath());
                                                                    if (mediaFile.exists()) {
                                                                        ContentValues filesInfo = new ContentValues(2);
                                                                        filesInfo.put("LastWatchTime", lastWatchTime != 0 ? Long.valueOf(lastWatchTime) : null);
                                                                        filesInfo.put("FinishTime", finishTime != 0 ? Long.valueOf(finishTime) : null);
                                                                        mdb.upsertVideoFile(mediaFile, filesInfo);
                                                                    }
                                                                }
                                                                mediaState = mediaState2;
                                                                break;
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 5:
                                                            if (TAG_RECORD.equals(p.getName())) {
                                                                String query = null;
                                                                Timestamp time = null;
                                                                for (int i5 = p.getAttributeCount() - 1; i5 >= 0; i5--) {
                                                                    String attr5 = p.getAttributeName(i5);
                                                                    String value5 = p.getAttributeValue(i5);
                                                                    if ("query".equals(attr5)) {
                                                                        query = value5;
                                                                    } else if (ATTR_time.equals(attr5)) {
                                                                        time = new Timestamp(Long.parseLong(value5));
                                                                    }
                                                                }
                                                                if (query != null) {
                                                                    mdb.saveRecentQuery(query, time);
                                                                }
                                                                mediaState = mediaState2;
                                                                break;
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 6:
                                                            if (TAG_RECORD.equals(p.getName())) {
                                                                String input = null;
                                                                Timestamp time2 = null;
                                                                for (int i6 = p.getAttributeCount() - 1; i6 >= 0; i6--) {
                                                                    String attr6 = p.getAttributeName(i6);
                                                                    String value6 = p.getAttributeValue(i6);
                                                                    if (ATTR_input.equals(attr6)) {
                                                                        input = value6;
                                                                    } else if (ATTR_time.equals(attr6)) {
                                                                        time2 = new Timestamp(Long.parseLong(value6));
                                                                    }
                                                                }
                                                                if (input != null) {
                                                                    mdb.writeDirectOpenLog(input, time2);
                                                                }
                                                                mediaState = mediaState2;
                                                                break;
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 7:
                                                            if (TAG_RECORD.equals(p.getName())) {
                                                                String input2 = null;
                                                                Timestamp time3 = null;
                                                                for (int i7 = p.getAttributeCount() - 1; i7 >= 0; i7--) {
                                                                    String attr7 = p.getAttributeName(i7);
                                                                    String value7 = p.getAttributeValue(i7);
                                                                    if (ATTR_input.equals(attr7)) {
                                                                        input2 = value7;
                                                                    } else if (ATTR_time.equals(attr7)) {
                                                                        time3 = new Timestamp(Long.parseLong(value7));
                                                                    }
                                                                }
                                                                if (input2 != null) {
                                                                    mdb.writeSubtitleQueryLog(input2, time3);
                                                                }
                                                                mediaState = mediaState2;
                                                                break;
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        case 8:
                                                            if (TAG_RECORD.equals(p.getName())) {
                                                                String input3 = null;
                                                                Timestamp time4 = null;
                                                                for (int i8 = p.getAttributeCount() - 1; i8 >= 0; i8--) {
                                                                    String attr8 = p.getAttributeName(i8);
                                                                    String value8 = p.getAttributeValue(i8);
                                                                    if (ATTR_input.equals(attr8)) {
                                                                        input3 = value8;
                                                                    } else if (ATTR_time.equals(attr8)) {
                                                                        time4 = new Timestamp(Long.parseLong(value8));
                                                                    }
                                                                }
                                                                if (input3 != null) {
                                                                    mdb.writeMovieTitleLog(input3, time4);
                                                                }
                                                            }
                                                            mediaState = mediaState2;
                                                            break;
                                                        default:
                                                            mediaState = mediaState2;
                                                            break;
                                                    }
                                                    mediaSubtitles = mediaSubtitles2;
                                                } else {
                                                    if (depth == 3 && mediaState2 != null && TAG_SUBTITLE.equals(p.getName())) {
                                                        mediaSubtitles = mediaSubtitles2 == null ? new ArrayList<>() : mediaSubtitles2;
                                                        try {
                                                            MediaDatabase.State.Subtitle sub = new MediaDatabase.State.Subtitle();
                                                            for (int i9 = p.getAttributeCount() - 1; i9 >= 0; i9--) {
                                                                String attr9 = p.getAttributeName(i9);
                                                                String value9 = p.getAttributeValue(i9);
                                                                if ("uri".equals(attr9)) {
                                                                    sub.uri = Uri.parse(value9);
                                                                } else if (IXMLRPCSerializer.TAG_NAME.equals(attr9)) {
                                                                    sub.name = value9;
                                                                } else if (ATTR_typename.equals(attr9)) {
                                                                    sub.typename = value9;
                                                                } else if (ATTR_enabled.equals(attr9)) {
                                                                    sub.enabled = Boolean.parseBoolean(value9);
                                                                }
                                                            }
                                                            if (sub.uri != null) {
                                                                mediaSubtitles.add(sub);
                                                            }
                                                            mediaState = mediaState2;
                                                        } catch (Throwable th2) {
                                                            th = th2;
                                                            reader.close();
                                                            if (mdb != null) {
                                                            }
                                                            throw th;
                                                        }
                                                    }
                                                    mediaSubtitles = mediaSubtitles2;
                                                    mediaState = mediaState2;
                                                }
                                                depth++;
                                            }
                                        } catch (Throwable th3) {
                                            th = th3;
                                        }
                                        break;
                                    case 3:
                                        depth--;
                                        if (depth == 1) {
                                            if (edit != null) {
                                                edit.commit();
                                                edit = null;
                                            }
                                            if (proc == 1) {
                                                PackageManager pm = App.context.getPackageManager();
                                                SharedPreferences.Editor edit2 = App.prefs.edit();
                                                pm.setComponentEnabledSetting(P.webDelegateName, playLink ? 1 : 2, 1);
                                                pm.setComponentEnabledSetting(P.localAudioDelegateName, audioPlayer ? 1 : 2, 1);
                                                pm.setComponentEnabledSetting(P.webAudioDelegateName, (playLink && audioPlayer) ? 1 : 2, 1);
                                                int myVersion = AppUtils.getVersion(L.getMyPackageInfo().versionCode);
                                                int noticedVersion = App.prefs.getInt(Key.NOTICED_VERSION, 0);
                                                if (noticedVersion > myVersion) {
                                                    edit2.putInt(Key.NOTICED_VERSION, myVersion);
                                                }
                                                L.removeCustomCodecChecksum(edit2);
                                                L.deleteLoadedCustomCodecFiles();
                                                edit2.commit();
                                                edit = null;
                                                mediaSubtitles = mediaSubtitles2;
                                                mediaState = mediaState2;
                                                break;
                                            }
                                        } else if (depth == 2) {
                                            if (mediaUri == null || mediaState2 == null) {
                                                mediaState = mediaState2;
                                            } else {
                                                if (mediaSubtitles2 != null) {
                                                    mediaState2.subtitles = (MediaDatabase.State.Subtitle[]) mediaSubtitles2.toArray(new MediaDatabase.State.Subtitle[mediaSubtitles2.size()]);
                                                }
                                                mdb.writeState(mediaUri, mediaState2);
                                                mediaUri = null;
                                                mediaState = null;
                                            }
                                            mediaSubtitles = null;
                                            break;
                                        }
                                    default:
                                        mediaSubtitles = mediaSubtitles2;
                                        mediaState = mediaState2;
                                        break;
                                }
                                eventType = p.next();
                            } else {
                                if (mdb != null) {
                                    mdb.db.setTransactionSuccessful();
                                }
                                z = true;
                                reader.close();
                                if (mdb != null) {
                                    mdb.db.endTransaction();
                                }
                                if (mdb != null) {
                                    mdb.release();
                                }
                            }
                        } catch (Exception e) {
                            e = e;
                            Log.e(TAG, "", e);
                            z = false;
                            if (mdb != null) {
                                mdb.release();
                            }
                            return z;
                        } catch (Throwable th4) {
                            th = th4;
                            if (mdb != null) {
                                mdb.release();
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
            } catch (Throwable th6) {
                th = th6;
            }
        } catch (Exception e2) {
            e = e2;
        }
        return z;
    }
}
