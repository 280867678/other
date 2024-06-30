package com.mxtech.subtitle;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.Misc;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.MediaUtils;
import com.mxtech.nio.CharsetDetector;
import com.mxtech.nio.Decodable;
import com.mxtech.nio.StringDecoder;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class SubtitleFactory {
    public static final int EXT_IDX = 5;
    public static final int EXT_MPL = 6;
    public static final int EXT_PJS = 9;
    public static final int EXT_PSB = 8;
    public static final int EXT_SMI = 3;
    public static final int EXT_SRT = 0;
    public static final int EXT_SSA = 1;
    public static final int EXT_SUB = 2;
    public static final int EXT_TXT = 4;
    public static final int EXT_VTT = 7;
    public static final int MAX_FILE_SIZE = 20971520;
    public static final int NB_EXT = 10;
    public static final String[] ALL_EXTENSIONS = {"srt", "ssa", "ass", "sub", "smi", "txt", "idx", "mpl", "vtt", "psb", "sami", "pjs"};
    public static final String[] EXT_NAMES = {"SRT", "SSA", "SUB", "SMI", "TXT", "SUB", "MPL", "VTT", "PSB", PJSSubtitle.TYPENAME};
    private static final Class<?>[] AllParsers = {SubRipSubtitle.class, SubStationAlphaSubtitle.class, MicroDVDSubtitle.class, SAMIParser.class, SubViewer2Subtitle.class, VobSubtitle.class, MPL2Subtitle.class, WebVTTSubtitleBasic.class, TMPlayerSubtitle.class, PowerDivXSubtitle.class, PJSSubtitle.class};
    private static final Class<?>[] SRTParsers = {SubRipSubtitle.class};
    private static final Class<?>[] SSAParsers = {SubStationAlphaSubtitle.class};
    private static final Class<?>[] SUBParsers = {MicroDVDSubtitle.class, SubViewer2Subtitle.class};
    private static final Class<?>[] SMIParsers = {SAMIParser.class};
    private static final Class<?>[] TXTParsers = {MicroDVDSubtitle.class, SubRipSubtitle.class, TMPlayerSubtitle.class, MPL2Subtitle.class, PowerDivXSubtitle.class};
    private static final Class<?>[] IDXParsers = {VobSubtitle.class};
    private static final Class<?>[] MPLParsers = {MPL2Subtitle.class};
    private static final Class<?>[] VTTParsers = {WebVTTSubtitleBasic.class};
    private static final Class<?>[] PSBParsers = {PowerDivXSubtitle.class};
    private static final Class<?>[] PJSParsers = {PJSSubtitle.class};
    private static final Class<?>[][] ParsersForExt = {SRTParsers, SSAParsers, SUBParsers, SMIParsers, TXTParsers, IDXParsers, MPLParsers, VTTParsers, PSBParsers, PJSParsers};
    private static final Map<String, Class<?>> TypeParsers = new HashMap();

    static {
        TypeParsers.put(SubRipSubtitle.TYPENAME, SubRipSubtitle.class);
        TypeParsers.put(SubStationAlphaSubtitle.TYPENAME, SubStationAlphaSubtitle.class);
        TypeParsers.put(MicroDVDSubtitle.TYPENAME, MicroDVDSubtitle.class);
        TypeParsers.put(SAMISubtitle.TYPENAME, SAMIParser.class);
        TypeParsers.put(SubViewer2Subtitle.TYPENAME, SubViewer2Subtitle.class);
        TypeParsers.put(VobSubtitle.TYPENAME, VobSubtitle.class);
        TypeParsers.put(MPL2Subtitle.TYPENAME, MPL2Subtitle.class);
        TypeParsers.put(WebVTTSubtitleBasic.TYPENAME, WebVTTSubtitleBasic.class);
        TypeParsers.put(TMPlayerSubtitle.TYPENAME, TMPlayerSubtitle.class);
        TypeParsers.put(PowerDivXSubtitle.TYPENAME, PowerDivXSubtitle.class);
        TypeParsers.put(PJSSubtitle.TYPENAME, PJSSubtitle.class);
    }

    private static ISubtitle[] createSubtitle(Class<?> praser, Uri uri, String type, String name, String text, ISubtitleClient client) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return (ISubtitle[]) praser.getMethod("create", Uri.class, String.class, String.class, String.class, ISubtitleClient.class).invoke(null, uri, type, name, text, client);
    }

    public static ISubtitle[] create(String text, Uri uri, String name, ISubtitleClient client, String typename) {
        int lastDot;
        int extId;
        Set<Class<?>> done = null;
        if (typename != null) {
            Class<?> parser = TypeParsers.get(typename);
            if (parser != null) {
                try {
                    ISubtitle[] subs = createSubtitle(parser, uri, typename, name, text, client);
                    if (subs != null) {
                        return subs;
                    }
                } catch (Exception e) {
                    Log.e(ISubtitle.TAG, "", e);
                }
                done = new HashSet<>();
                done.add(parser);
            } else {
                Log.w(ISubtitle.TAG, "Cannot find subtitle parser for '" + typename + '\'');
            }
        }
        String filename = uri.getLastPathSegment();
        if (filename != null && (lastDot = filename.lastIndexOf(46)) > 0 && (extId = getExtensionId(filename, lastDot + 1)) >= 0) {
            Class<?>[] parsers = ParsersForExt[extId];
            for (Class<?> parser2 : parsers) {
                if (done == null || !done.contains(parser2)) {
                    try {
                        ISubtitle[] subs2 = createSubtitle(parser2, uri, null, name, text, client);
                        if (subs2 != null) {
                            return subs2;
                        }
                    } catch (Exception e2) {
                        Log.e(ISubtitle.TAG, "", e2);
                    }
                }
            }
            if (done == null) {
                done = new HashSet<>();
            }
            for (Class<?> parser3 : parsers) {
                done.add(parser3);
            }
        }
        Class<?>[] clsArr = AllParsers;
        int length = clsArr.length;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                Class<?> parser4 = clsArr[i2];
                if (done == null || !done.contains(parser4)) {
                    try {
                        ISubtitle[] subs3 = createSubtitle(parser4, uri, null, name, text, client);
                        if (subs3 != null) {
                            return subs3;
                        }
                    } catch (Exception e3) {
                        Log.e(ISubtitle.TAG, "", e3);
                    }
                }
                i = i2 + 1;
            } else {
                return new ISubtitle[0];
            }
        }
    }

    public static ISubtitle[] createFromFile(File file, String name, ISubtitleClient client, String mime) throws Throwable {
        long len = file.length();
        if (len == 0) {
            return new ISubtitle[0];
        }
        if (len > 20971520) {
            throw new IOException("file too large. size=" + len);
        }
        String text = decodeFile(file);
        return create(text, Uri.fromFile(file), name, client, mime);
    }

    public static int getExtensionId(File file) {
        return getExtensionId(file.getPath());
    }

    public static int getExtensionId(String path) {
        int lastDot = path.lastIndexOf(46);
        if (lastDot <= 0) {
            return -1;
        }
        return getExtensionId(path, lastDot + 1);
    }

    public static boolean isSupportedFile(File file) {
        return getExtensionId(file.getPath()) >= 0;
    }

    public static boolean isSupportedFile(String path) {
        return getExtensionId(path) >= 0;
    }

    public static boolean isSupportedExtension(String ext) {
        return getExtensionId(ext, 0) >= 0;
    }

    public static int getExtensionId(String path, int offset) {
        String expected;
        int id;
        int lenPath = path.length();
        if (lenPath > offset + 1) {
            switch (path.charAt(offset)) {
                case 'A':
                case 'a':
                    expected = "ASS";
                    id = 1;
                    break;
                case 'I':
                case 'i':
                    expected = "IDX";
                    id = 5;
                    break;
                case 'M':
                case 'm':
                    expected = "MPL";
                    id = 6;
                    break;
                case 'P':
                case 'p':
                    switch (path.charAt(offset + 1)) {
                        case 'J':
                        case 'j':
                            expected = PJSSubtitle.TYPENAME;
                            id = 9;
                            break;
                        case 'S':
                        case 's':
                            expected = "PSB";
                            id = 8;
                            break;
                        default:
                            return -1;
                    }
                case 'S':
                case 's':
                    switch (path.charAt(offset + 1)) {
                        case 'A':
                        case 'a':
                            expected = SAMISubtitle.TYPENAME;
                            id = 3;
                            break;
                        case 'M':
                        case 'm':
                            expected = "SMI";
                            id = 3;
                            break;
                        case 'R':
                        case 'r':
                            expected = "SRT";
                            id = 0;
                            break;
                        case 'S':
                        case 's':
                            expected = "SSA";
                            id = 1;
                            break;
                        case 'U':
                        case 'u':
                            expected = "SUB";
                            id = 2;
                            break;
                        default:
                            return -1;
                    }
                case 'T':
                case 't':
                    expected = "TXT";
                    id = 4;
                    break;
                case 'V':
                case 'v':
                    expected = "VTT";
                    id = 7;
                    break;
                default:
                    return -1;
            }
            int lenExpected = expected.length();
            if (lenPath == offset + lenExpected && expected.regionMatches(true, 0, path, offset, lenExpected)) {
                return id;
            }
        }
        return -1;
    }

    public static String decodeFile(File file) throws Exception {
        String makeSuitableString;
        if (P.subtitleCharset == null) {
            CharsetDetector det = new CharsetDetector();
            try {
                if (det.detectFile(file.getPath(), CharsetDetector.FLAG_IGNORE_MARKUP | CharsetDetector.FLAG_DECODE)) {
                    makeSuitableString = makeSuitableString(det);
                    return makeSuitableString;
                }
                throw new UnsupportedEncodingException();
            } finally {
                det.close();
            }
        }
        StringDecoder dec = new StringDecoder(P.subtitleCharset);
        try {
            if (dec.decodeFile(file.getPath())) {
                makeSuitableString = makeSuitableString(dec);
                return makeSuitableString;
            }
            throw new UnsupportedEncodingException();
        } finally {
            dec.close();
        }
    }

    public static String decode(byte[] input) throws Exception {
        String makeSuitableString;
        if (P.subtitleCharset == null) {
            CharsetDetector det = new CharsetDetector();
            try {
                if (det.detect(input, CharsetDetector.FLAG_IGNORE_MARKUP | CharsetDetector.FLAG_DECODE)) {
                    makeSuitableString = makeSuitableString(det);
                    return makeSuitableString;
                }
                throw new UnsupportedEncodingException();
            } finally {
                det.close();
            }
        }
        StringDecoder dec = new StringDecoder(P.subtitleCharset);
        try {
            if (dec.decode(input)) {
                makeSuitableString = makeSuitableString(dec);
                return makeSuitableString;
            }
            throw new UnsupportedEncodingException();
        } finally {
            dec.close();
        }
    }

    public static String makeSuitableString(Decodable dec) {
        dec.trim();
        dec.normalizeLineBreak();
        return dec.makeString();
    }

    public static boolean isSubtitleOf(String videoFileName, String subtitleFileName) {
        return isSubtitleOf(videoFileName, subtitleFileName, true);
    }

    public static boolean isSubtitleOf(String videoFileName, String subtitleFileName, boolean testExtension) {
        int subNameLen = subtitleFileName.length();
        int videoDot = videoFileName.lastIndexOf(46);
        if (videoDot < 0) {
            videoDot = videoFileName.length();
        }
        if (subNameLen >= videoDot + 2 && videoFileName.regionMatches(true, 0, subtitleFileName, 0, videoDot) && subtitleFileName.charAt(videoDot) == '.') {
            int lastDot = subtitleFileName.lastIndexOf(46);
            int secondDot = subtitleFileName.indexOf(46, videoDot + 1);
            if (secondDot < 0 || secondDot == lastDot) {
                return !testExtension || getExtensionId(subtitleFileName, lastDot + 1) >= 0;
            }
            return false;
        }
        return false;
    }

    /* loaded from: classes2.dex */
    private static class Filter implements FileFilter {
        private final String _mediaFilename;

        Filter(String mediaFilename) {
            this._mediaFilename = mediaFilename;
        }

        @Override // java.io.FileFilter
        public boolean accept(File file) {
            if (file.isFile()) {
                return SubtitleFactory.isSubtitleOf(this._mediaFilename, file.getName(), true);
            }
            return false;
        }
    }

    public static File[] scan(String mediaFilename, File... folders) {
        File[] listed;
        Filter filter = new Filter(mediaFilename);
        File[] files = null;
        for (File folder : folders) {
            if (folder != null && (listed = FileUtils.listFiles(folder, filter)) != null) {
                if (files == null || files.length == 0) {
                    files = listed;
                } else {
                    files = (File[]) Misc.merge(files, listed);
                }
            }
        }
        if (files != null) {
            return files;
        }
        File[] files2 = FileUtils.EMPTY_ARRAY;
        return files2;
    }

    public static File getAlternativeFile(File parent, String filename) {
        File[] files;
        String ext = FileUtils.getExtension(filename);
        if (!"SUB".equalsIgnoreCase(ext) || (files = FileUtils.listFiles(parent, new FileUtils.CaseInsensitiveFileFilter(FileUtils.stripExtension(filename) + ".IDX"))) == null || files.length <= 0) {
            return null;
        }
        return files[0];
    }

    public static boolean isEmbeddedSubtitle(Uri uri) {
        return FFPlayer.SUB_SCHEMA.equals(uri.getScheme());
    }

    public static CharSequence getDecorativeName(ISubtitle sub, @Nullable ISubtitle[] allSubs) {
        String name = sub.name();
        Uri uri = sub.uri();
        String desc = MediaUtils.getDecorativeLastPathSegment(uri);
        if (allSubs != null && (sub.flags() & 65536) == 0) {
            int length = allSubs.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                ISubtitle other = allSubs[i];
                if (sub == other || !name.equals(other.name()) || !TextUtils.equals(desc, MediaUtils.getDecorativeLastPathSegment(other.uri()))) {
                    i++;
                } else {
                    if (MediaUtils.isFileUri(uri)) {
                        desc = uri.getPath();
                    } else {
                        desc = uri.toString();
                    }
                    int descLen = desc.length();
                    int nameLen = name.length();
                    if (descLen > nameLen && desc.charAt((descLen - nameLen) - 1) == File.separatorChar && desc.endsWith(name)) {
                        desc = desc.substring(0, (descLen - nameLen) - 1);
                    }
                }
            }
        }
        if (desc == null || desc.length() <= 0 || name.equalsIgnoreCase(desc)) {
            return name;
        }
        SpannableStringBuilder sb = new SpannableStringBuilder(name);
        int len = sb.length();
        sb.setSpan(L.getSecondaryColorSizeSpan(), len, len, 18);
        sb.append((CharSequence) " (").append((CharSequence) desc).append(')');
        return sb;
    }
}
