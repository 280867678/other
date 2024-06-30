package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.Library;
import com.mxtech.StringUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.app.Authorizer;
import com.mxtech.app.DialogRegistry;
import com.mxtech.os.Cpu;
import com.mxtech.os.Model;
import com.mxtech.text.ColorSizeSpan;
import com.mxtech.videoplayer.directory.MediaDirectoryService;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.widget.SleepTimer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public final class L {
    public static final String FFMPEG = "ffmpeg.mx";
    public static final int FLAG_BROKEN_OMX_VIDEO_FRAME_REORDER = 268435456;
    public static final int FLAG_EXYNOS = 256;
    public static final int FLAG_HAS_LOW_LATENCY_AUDIO = 536870912;
    public static final int FLAG_HTC = 65536;
    public static final int FLAG_HUMMINGBIRD = 128;
    public static final int FLAG_MSM7x30 = 16;
    public static final int FLAG_NO_OMX_DECODER = 262144;
    public static final int FLAG_OMAP3 = 32;
    public static final int FLAG_OMAP4 = 64;
    public static final int FLAG_OMAP4_ENHANCEMENT = 4;
    public static final int FLAG_QCOM = 1024;
    public static final int FLAG_QSD8K = 8;
    public static final int FLAG_SERVICE = 2;
    public static final int FLAG_TEGRA = 512;
    public static final int FLAG_TEGRA3 = 2048;
    public static final String LIBFFMPEG = "libffmpeg.mx.so";
    public static final int MODE_EDUCATIONAL = 200;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_NORMAL_FROYO = 1;
    public static final int MODE_PRO = 100;
    public static int THUMB_HEIGHT;
    public static int THUMB_WIDTH;
    private static CustomCodecVersionName _customCodecVersionName;
    private static Object _errorColorSpan;
    private static PackageInfo _packageInfo;
    private static ColorSizeSpan _secondaryColorSizeSpan;
    private static ForegroundColorSpan _secondaryColorSpan;
    private static RelativeSizeSpan _secondarySizeSpan;
    public static AudioManager audioManager;
    public static Authorizer authorizer;
    public static String codecPackageOrSystemLib;
    public static String customFFmpegPath;
    public static boolean hasOMXDecoder;
    public static KeyguardManager keyguardManager;
    public static int maxAudioVolumeLevel;
    public static int maxVolumeDisplayLevel;
    public static int mode;
    public static MediaObserver observer;
    public static SleepTimer sleepTimer;
    public static ThumbStorage thumbStorage;
    public static int volumeIncrementBy;
    public static int mainFlags = 0;
    public static final ThumbCache thumbCache = new ThumbCache();
    public static final MediaDirectoryService directoryService = new MediaDirectoryService();
    public static final List<String> simpleSnackbarMessages = new ArrayList();
    public static final StringBuilder sb = new StringBuilder();
    private static int FC_IGNORE_BROKEN = 2;
    private static final int[][] VLINED_BACKGROUND_STATES = {new int[]{16842912}, new int[]{16842912, 16842913}};
    private static final int[] DEFAULT_STATE = new int[0];

    public static native boolean hasOMXDecoder();

    public static native int native_init(Context context, int i, int i2, Object obj, String str, String str2, int i3, int i4, int i5);

    @SuppressLint({"NewApi"})
    public static void init(Context context, int mode2) {
        Resources res = context.getResources();
        THUMB_WIDTH = res.getDimensionPixelSize(R.dimen.thumb_width);
        THUMB_HEIGHT = res.getDimensionPixelSize(R.dimen.thumb_height);
        keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        audioManager = (AudioManager) context.getSystemService("audio");
        maxAudioVolumeLevel = audioManager.getStreamMaxVolume(3);
        if (maxAudioVolumeLevel > 40) {
            int bestBy = 1;
            int bestLevel = maxAudioVolumeLevel;
            for (int by = maxAudioVolumeLevel / 20; by > 0; by--) {
                if (maxAudioVolumeLevel % by == 0) {
                    int level = maxAudioVolumeLevel / by;
                    if (level > 40) {
                        break;
                    }
                    bestBy = by;
                    bestLevel = level;
                }
            }
            maxVolumeDisplayLevel = bestLevel;
            volumeIncrementBy = bestBy;
        } else {
            maxVolumeDisplayLevel = maxAudioVolumeLevel;
            volumeIncrementBy = 1;
        }
        mode = mode2;
        try {
            InputStream in = new FileInputStream("/system/build.prop");
            Properties props = new Properties();
            props.load(in);
            String enhancement = props.getProperty("com.ti.omap_enhancement");
            if ("true".equalsIgnoreCase(enhancement)) {
                mainFlags |= 4;
            }
            String platform = props.getProperty("ro.board.platform");
            if (platform != null && platform.length() > 0) {
                String platform2 = platform.toLowerCase(Locale.US);
                switch (platform2.charAt(0)) {
                    case 'a':
                        if (platform2.startsWith("apq")) {
                            mainFlags |= 1024;
                            break;
                        }
                        break;
                    case 'e':
                        if (platform2.startsWith("exynos")) {
                            mainFlags |= 256;
                            break;
                        }
                        break;
                    case 'm':
                        if (!platform2.equalsIgnoreCase("msm7x30")) {
                            if (platform2.startsWith("msm")) {
                                mainFlags |= 1024;
                                break;
                            }
                        } else {
                            mainFlags |= 1040;
                            break;
                        }
                        break;
                    case 'o':
                        if (!platform2.equalsIgnoreCase("omap3")) {
                            if (platform2.equalsIgnoreCase("omap4")) {
                                mainFlags |= 64;
                                if (Build.VERSION.SDK_INT >= 18 && Model.model == 30) {
                                    mainFlags |= 268435456;
                                    break;
                                }
                            }
                        } else {
                            mainFlags |= 32;
                            break;
                        }
                        break;
                    case 'q':
                        if (!platform2.equalsIgnoreCase("qsd8k")) {
                            if (platform2.startsWith("qsd")) {
                                mainFlags |= 1024;
                                break;
                            }
                        } else {
                            mainFlags |= 1032;
                            break;
                        }
                        break;
                    case 's':
                        if (!platform2.startsWith("s5pc1")) {
                            if (platform2.startsWith("s5pc2")) {
                                mainFlags |= 256;
                                break;
                            }
                        } else {
                            mainFlags |= 128;
                            break;
                        }
                        break;
                    case 't':
                        if (platform2.startsWith("tegra")) {
                            mainFlags |= 512;
                            if ((Cpu.features & 516) == 4 && Build.VERSION.SDK_INT < 23) {
                                mainFlags |= 2048;
                                break;
                            }
                        }
                        break;
                }
            }
            if (!App.prefs.contains(Key.OMX_DECODER)) {
                if ((mainFlags & 8) != 0) {
                    if (Model.model != 61 || Build.VERSION.RELEASE.compareTo("2.3.6") < 0) {
                        disableOMXDecoder();
                    }
                } else if (Build.VERSION.SDK_INT < 9) {
                    disableOMXDecoder();
                } else if (Model.model == 190) {
                    disableOMXDecoder();
                } else if (Model.model == 220) {
                    disableOMXDecoder();
                }
            }
            if (Model.manufacturer == 10040) {
                mainFlags |= 65536;
                if (!App.DEBUG && Build.VERSION.SDK_INT >= 18) {
                    mainFlags |= 262144;
                }
            } else if (!App.DEBUG && Model.model == 43) {
                mainFlags |= 262144;
            }
            in.close();
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
        P.init(context);
        observer = new MediaObserver();
    }

    public static boolean canShare(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("video/*");
        try {
            for (ResolveInfo resolved : pm.queryIntentActivities(intent, 0)) {
                String packageName = resolved.activityInfo.packageName;
                if (!DeviceUtils.ANDROID_TV_DEFAULT_INTENT_RECEIVER.equals(packageName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
            return false;
        }
    }

    public static boolean canSendMultipleFiles(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/octet-content");
        try {
            for (ResolveInfo resolved : pm.queryIntentActivities(intent, 0)) {
                String packageName = resolved.activityInfo.packageName;
                if (!DeviceUtils.ANDROID_TV_DEFAULT_INTENT_RECEIVER.equals(packageName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
            return false;
        }
    }

    private static void disableOMXDecoder() {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putBoolean(Key.OMX_DECODER, false);
        AppUtils.apply(editor);
    }

    public static String getCodecLibPath() {
        if (codecPackageOrSystemLib.startsWith(Library.PACKAGE_COMMON)) {
            try {
                PackageManager pm = App.context.getPackageManager();
                ApplicationInfo info = pm.getApplicationInfo(codecPackageOrSystemLib, 0);
                return AppUtils.getNativeLibraryDir(info);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(App.TAG, "", e);
                return null;
            }
        }
        return codecPackageOrSystemLib;
    }

    /* loaded from: classes.dex */
    public static class Codec {
        public final String archName;
        public final String code;
        public final String packageName;

        Codec(String packageName, String archName, String code) {
            this.packageName = packageName;
            this.archName = archName;
            this.code = code;
        }

        public String getArchId() {
            return this.packageName.substring("com.mxtech.ffmpeg.".length());
        }
    }

    public static Codec findCodec() {
        switch (Cpu.family) {
            case 1:
                if ((Cpu.features & 4) != 0) {
                    if ((mainFlags & 2048) == 0) {
                        return new Codec(CHK.CODEC, "ARMv7 NEON", "neon");
                    }
                    return new Codec("com.mxtech.ffmpeg.tegra3", "Tegra 3", "tegra3");
                } else if ((Cpu.features & 1) != 0) {
                    return new Codec("com.mxtech.ffmpeg.v7_vfpv3d16", "ARMv7", "tegra2");
                } else {
                    if ((Cpu.features & 8) != 0) {
                        Log.e(App.TAG, "ARMv6 family is no longer supported.");
                        return null;
                    }
                    Log.e(App.TAG, "ARMv5 family is no longer supported.");
                    return null;
                }
            case 2:
                if ((Cpu.features & 1) != 0 || Model.model == 1400) {
                    return new Codec("com.mxtech.ffmpeg.x86", "x86", "x86");
                }
                Log.e(App.TAG, "Unsupported x86 feature: " + Cpu.features);
                return null;
            case 3:
                Log.e(App.TAG, "MIPS family is no longer supported.");
                return null;
            case 4:
            default:
                Log.e(App.TAG, "Unknown CPU family: " + Cpu.family);
                return null;
            case 5:
                Log.e(App.TAG, "x86_64 is yet supported.");
                return null;
        }
    }

    public static String getSysArchId() {
        Codec codec = findCodec();
        return codec != null ? codec.getArchId() : "";
    }

    public static String getSysArchName() {
        Codec codec = findCodec();
        return codec != null ? codec.archName : "Unknown";
    }

    public static int getSysDecoderVersion() {
        switch (Build.VERSION.SDK_INT) {
            case 8:
                return 8;
            case 9:
            case 10:
                return 9;
            case 11:
            case 12:
            case 13:
                return 11;
            case 14:
            case 15:
            case 16:
            case 17:
                return 14;
            default:
                return 18;
        }
    }

    public static void updateFontConf(boolean forceRebuild) {
        int fcflags = App.prefs.getBoolean(Key.SSA_BROKEN_FONT_IGNORE, false) ? 0 | FC_IGNORE_BROKEN : 0;
        updateFontConf(forceRebuild, P.getFontFolder().getPath(), fcflags);
    }

    private static File getFontCacheDir_v0() {
        return new File(App.context.getCacheDir(), "font");
    }

    private static File getFontCacheDir() {
        File parent;
        if (Build.VERSION.SDK_INT < 21) {
            parent = App.context.getFilesDir();
        } else {
            parent = App.context.getNoBackupFilesDir();
        }
        return new File(parent, "font_cache");
    }

    public static void clearFontCache() {
        clearFontCache(getFontCacheDir());
    }

    public static void clearFontCache(File directory) {
        long begin = SystemClock.uptimeMillis();
        FileUtils.deleteRecursive(directory);
        sb.setLength(0);
        sb.append("Font cache cleared in ").append(SystemClock.uptimeMillis() - begin).append("ms");
        Log.d(App.TAG, sb.toString());
    }

    private static void appendAlias(StringBuilder b, String family, String[] prefers, String[] accepts, String[] defaults) {
        b.append("<alias>").append("<family>").append(family).append("</family>");
        if (prefers != null) {
            b.append("<prefer>");
            for (String prefer : prefers) {
                b.append("<family>").append(prefer).append("</family>");
            }
            b.append("</prefer>");
        }
        if (accepts != null) {
            b.append("<accept>");
            for (String accept : accepts) {
                b.append("<family>").append(accept).append("</family>");
            }
            b.append("</accept>");
        }
        if (defaults != null) {
            b.append("<default>");
            for (String d : defaults) {
                b.append("<family>").append(d).append("</family>");
            }
            b.append("</default>");
        }
        b.append("</alias>");
    }

    private static void updateFontConf(boolean forceRebuild, String fontDirPath, int fcflags) {
        if (App.prefs.getInt(Key.FONT_CACHE_VERSION, 0) < 1) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putInt(Key.FONT_CACHE_VERSION, 1);
            AppUtils.apply(editor);
            File oldDir = getFontCacheDir_v0();
            clearFontCache(oldDir);
            Log.i(App.TAG, "Removed old font cache directory " + oldDir);
        }
        File file = new File(App.context.getFilesDir(), "font.conf");
        File cacheDir = getFontCacheDir();
        if (forceRebuild || !checkFontConf(file, cacheDir, "/system/fonts", fontDirPath)) {
            cacheDir.mkdirs();
            sb.setLength(0);
            sb.append("<?xml version=\"1.0\"?>").append("<!DOCTYPE fontconfig SYSTEM \"fonts.dtd\">").append("<fontconfig>").append("<dir>/system/fonts</dir>").append("<dir>").append(TextUtils.htmlEncode(fontDirPath)).append("</dir>").append("<cachedir>").append(cacheDir.getPath()).append("</cachedir>");
            if ((FC_IGNORE_BROKEN & fcflags) != 0) {
                String[] BROKEN_FAMILIES = {"DFHSGothic-W7", "DFPHSGothic-W7", "DFGHSGothic-W7"};
                for (String family : BROKEN_FAMILIES) {
                    sb.append("<match target=\"pattern\">").append("<test qual=\"any\" name=\"family\">").append("<string>").append(family).append("</string>").append("</test>").append("<edit name=\"family\" mode=\"assign\" binding=\"same\">").append("<string>sans-serif</string>").append("</edit>").append("</match>");
                }
            }
            sb.append("<match>").append("<edit name=\"family\" mode=\"append_last\">").append("<string>sans-serif</string>").append("</edit>").append("</match>");
            appendAlias(sb, "serif", null, new String[]{"Times New Roman", "Times", "Droid Serif"}, null);
            appendAlias(sb, "sans-serif", null, new String[]{"Arial", "Roboto", "Droid Sans"}, null);
            appendAlias(sb, "monospace", null, new String[]{"Courier New", "Courier", "Droid Sans Mono"}, null);
            sb.append("</fontconfig>");
            try {
                Writer out = new FileWriter(file, false);
                try {
                    out.write(sb.toString());
                    out.flush();
                    out.close();
                    for (ActivityScreen a : ActivityRegistry.findByClass(ActivityScreen.class)) {
                        a.rebuildFonts();
                    }
                } catch (Throwable th) {
                    out.close();
                    throw th;
                }
            } catch (FileNotFoundException e) {
                Log.w(App.TAG, "", e);
            } catch (IOException e2) {
                Log.w(App.TAG, "", e2);
            }
        }
    }

    private static boolean checkFontConf(File fontConf, File cacheDir, String... fontDirs) {
        if (fontConf.length() == 0) {
            return false;
        }
        XmlPullParser p = Xml.newPullParser();
        boolean inFontConfig = false;
        boolean inDir = false;
        boolean inCacheDir = false;
        boolean cacheDirFound = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fontConf));
            ArrayList<String> dirs = new ArrayList<>(Arrays.asList(fontDirs));
            p.setInput(reader);
            int eventType = p.getEventType();
            while (eventType != 1) {
                switch (eventType) {
                    case 2:
                    case 3:
                        String name = p.getName();
                        char c = 65535;
                        switch (name.hashCode()) {
                            case -433484341:
                                if (name.equals("cachedir")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case -381495215:
                                if (name.equals("fontconfig")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 99469:
                                if (name.equals("dir")) {
                                    c = 1;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                if (p.getDepth() == 1) {
                                    inFontConfig = eventType == 2;
                                    break;
                                } else {
                                    continue;
                                }
                            case 1:
                                if (!inFontConfig) {
                                    continue;
                                } else if (p.getDepth() == 2) {
                                    inDir = eventType == 2;
                                    break;
                                } else {
                                    break;
                                }
                            case 2:
                                if (!inFontConfig) {
                                    continue;
                                } else if (p.getDepth() == 2) {
                                    inCacheDir = eventType == 2;
                                    break;
                                } else {
                                    break;
                                }
                            default:
                                continue;
                        }
                    case 4:
                        if (inDir) {
                            dirs.remove(p.getText());
                            break;
                        } else if (!inCacheDir) {
                            continue;
                        } else if (cacheDirFound) {
                            reader.close();
                            return false;
                        } else if (!cacheDir.getPath().equals(p.getText())) {
                            reader.close();
                            return false;
                        } else {
                            cacheDirFound = true;
                            break;
                        }
                }
                eventType = p.next();
            }
            boolean match = cacheDirFound && dirs.size() == 0;
            reader.close();
            return match;
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
            return false;
        }
    }

    public static String getDecoderAbbrText(Context context, byte decoder) {
        int resId;
        switch (decoder) {
            case 1:
                resId = R.string.decoder_abbr_hw;
                break;
            case 2:
                resId = R.string.decoder_abbr_sw;
                break;
            case 3:
            default:
                resId = R.string.decoder_abbr_default;
                break;
            case 4:
                resId = R.string.decoder_abbr_omx;
                break;
        }
        return context.getString(resId);
    }

    public static String getDecoderText(Context context, byte decoder) {
        int resId;
        switch (decoder) {
            case 1:
                resId = R.string.decoder_hw;
                break;
            case 2:
                resId = R.string.decoder_sw;
                break;
            case 3:
            default:
                return "";
            case 4:
                resId = R.string.decoder_omx;
                break;
        }
        return context.getString(resId);
    }

    public static long getCustomCodecChecksum(String filePath) throws IOException {
        FileUtils.Adler32Checksum adler32 = new FileUtils.Adler32Checksum();
        adler32.update(filePath);
        return adler32.getValue();
    }

    public static void removeCustomCodecChecksum(SharedPreferences.Editor edit) {
        edit.remove(Key.CUSTOM_CODEC_CHECKSUM);
        edit.remove(Key.CUSTOM_CODEC_libffmpeg_DATE);
        edit.remove(Key.CUSTOM_CODEC_libffmpeg_SIZE);
    }

    public static void unloadCustomCodec() {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.remove(Key.CUSTOM_CODEC_PATH);
        removeCustomCodecChecksum(editor);
        AppUtils.apply(editor);
        customFFmpegPath = null;
        deleteLoadedCustomCodecFiles();
    }

    public static void deleteLoadedCustomCodecFiles() {
        String filesDir = App.context.getFilesDir().getPath();
        new File(filesDir, "libavutil.mx.so").delete();
        new File(filesDir, "libavcodec.mx.so").delete();
        new File(filesDir, "libswresample.mx.so").delete();
        new File(filesDir, "libswscale.mx.so").delete();
        new File(filesDir, "libavformat.mx.so").delete();
        new File(filesDir, "libavutil.so").delete();
        new File(filesDir, "libavcodec.so").delete();
        new File(filesDir, "libswresample.so").delete();
        new File(filesDir, "libswscale.so").delete();
        new File(filesDir, "libavformat.so").delete();
    }

    public static boolean isUsingCustomCodec() {
        return App.prefs.contains(Key.CUSTOM_CODEC_CHECKSUM);
    }

    public static void restart(Activity activity, int messageId) {
        restart(activity, messageId, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void restart_l(Context context) {
        try {
            ComponentName name = new ComponentName(context, AppUtils.findActivityKindOf(context, ActivityMediaList.class));
            Intent intent = AppUtils.makeMainActivty(name);
            App.quit(intent);
        } catch (Exception e) {
            Log.e(App.TAG, "", e);
        }
    }

    public static void restart(final Activity activity, int messageId, final boolean allowCancel) {
        if (!activity.isFinishing()) {
            AlertDialog.Builder b = new AlertDialog.Builder(activity);
            b.setMessage(messageId);
            if (allowCancel) {
                b.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                b.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.L.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        L.restart_l(activity.getApplicationContext());
                    }
                });
            } else {
                b.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            }
            final DialogRegistry dialogRegistry = DialogRegistry.registryOf(activity);
            AlertDialog dlg = b.create();
            dlg.setCanceledOnTouchOutside(true);
            if (dialogRegistry != null) {
                dialogRegistry.register(dlg);
            }
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.L.2
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialog) {
                    if (DialogRegistry.this != null) {
                        DialogRegistry.this.unregister(dialog);
                    }
                    if (!allowCancel) {
                        L.restart_l(activity.getApplicationContext());
                    }
                }
            });
            dlg.show();
        }
    }

    public static AlertDialog createFileWriteFailureDialog(Context context, int messageResId) {
        return createFileWriteFailureDialog(context, context.getString(messageResId));
    }

    @SuppressLint({"InflateParams"})
    public static AlertDialog createFileWriteFailureDialog(Context context, CharSequence message) {
        AlertDialog dlg = new AlertDialog.Builder(context).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
        if (Build.VERSION.SDK_INT < 19) {
            dlg.setMessage(message);
        } else {
            ViewGroup content = (ViewGroup) dlg.getLayoutInflater().inflate(R.layout.file_write_failure, (ViewGroup) null);
            TextView text = (TextView) content.findViewById(R.id.text);
            TextView comment = (TextView) content.findViewById(R.id.comment);
            CharSequence commentText = context.getString(R.string.file_write_failure_kitkat);
            text.setText(message);
            comment.setText(commentText);
            dlg.setView(content);
        }
        return dlg;
    }

    public static AlertDialog alertFileWriteFailureMessage(Activity context, int resId) {
        return alertFileWriteFailureMessage(context, context.getString(resId), 0);
    }

    public static AlertDialog alertFileWriteFailureMessage(Activity context, CharSequence message) {
        return alertFileWriteFailureMessage(context, message, 0);
    }

    public static AlertDialog alertFileWriteFailureMessage(Activity context, int resId, int titleId) {
        return alertFileWriteFailureMessage(context, context.getString(resId), titleId);
    }

    public static AlertDialog alertFileWriteFailureMessage(Activity context, CharSequence message, int titleId) {
        AlertDialog dlg = createFileWriteFailureDialog(context, message);
        if (titleId != 0) {
            dlg.setTitle(titleId);
        }
        dlg.setCanceledOnTouchOutside(true);
        DialogRegistry dialogRegistry = DialogRegistry.registryOf(context);
        if (dialogRegistry != null) {
            dlg.setOnDismissListener(dialogRegistry);
            dialogRegistry.register(dlg);
        }
        dlg.show();
        return dlg;
    }

    public static PackageInfo getMyPackageInfo() {
        if (_packageInfo == null) {
            try {
                _packageInfo = App.context.getPackageManager().getPackageInfo(App.context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(App.TAG, "", e);
                throw new RuntimeException("Cannot read my package info.");
            }
        }
        return _packageInfo;
    }

    public static String getCustomCodecDisplayUrl() {
        String url = authorizer.getCustomCodecPage();
        return url != null ? url : App.context.getString(R.string.custom_codec_display_url);
    }

    public static String getCustomCodecRealUrl() {
        return StringUtils.getString_s(R.string.direct_download_url, "codecs", getSysArchId());
    }

    /* loaded from: classes.dex */
    public static class CustomCodecVersionName {
        public final String name;
        public final String version;

        public CustomCodecVersionName(String archCode, String version) {
            this.name = "libffmpeg.mx.so." + archCode + "." + version;
            this.version = version;
        }
    }

    public static CustomCodecVersionName getCustomCodecVersionName() throws IllegalStateException {
        if (_customCodecVersionName == null) {
            try {
                ApplicationInfo appInfo = App.context.getPackageManager().getApplicationInfo(App.context.getPackageName(), 128);
                return getCustomCodecVersionName(appInfo);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(App.TAG, "", e);
                throw new IllegalStateException();
            }
        }
        return _customCodecVersionName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CustomCodecVersionName getCustomCodecVersionName(ApplicationInfo appinfo) throws IllegalStateException {
        Codec codec = findCodec();
        if (codec == null) {
            throw new IllegalStateException();
        }
        if (_customCodecVersionName == null) {
            _customCodecVersionName = new CustomCodecVersionName(codec.code, appinfo.metaData.getString("custom_codec_version"));
        }
        return _customCodecVersionName;
    }

    public static String getDisplayName(String fileName) {
        return (P.list_fields & 16) != 0 ? fileName : FileUtils.stripExtension(fileName);
    }

    public static void colorizeVLinedBackground(View view, int colorAccent) {
        int[][] iArr;
        Drawable oldDrawable;
        Drawable background = view.getBackground();
        if (background != null && background.isStateful()) {
            int[] orgiginalState = background.getState();
            for (int[] state : VLINED_BACKGROUND_STATES) {
                background.setState(state);
                Drawable d = background.getCurrent();
                if (d instanceof LayerDrawable) {
                    LayerDrawable ld = (LayerDrawable) d;
                    Drawable newDrawable = new ColorDrawable(colorAccent);
                    if (Build.VERSION.SDK_INT < 14 && (oldDrawable = ld.findDrawableByLayerId(R.id.colorAccent)) != null) {
                        newDrawable.setBounds(oldDrawable.getBounds());
                        oldDrawable.setCallback(null);
                    }
                    if (ld.setDrawableByLayerId(R.id.colorAccent, newDrawable) && Build.VERSION.SDK_INT < 14) {
                        newDrawable.setCallback(ld);
                    }
                }
            }
            background.setState(orgiginalState);
        }
    }

    public static CharSequence getErrorColoredText(CharSequence text) {
        if (_errorColorSpan == null) {
            _errorColorSpan = new ForegroundColorSpan(App.context.getResources().getColor(R.color.red_alert));
        }
        SpannableString s = SpannableString.valueOf(text);
        s.setSpan(_errorColorSpan, 0, text.length(), 33);
        return s;
    }

    public static Object getSecondaryColorSpan() {
        if (_secondaryColorSpan == null) {
            _secondaryColorSpan = new ForegroundColorSpan(App.context.getResources().getColor(17170448));
        }
        return _secondaryColorSpan;
    }

    public static Object getSecondarySizeSpan() {
        if (_secondarySizeSpan == null) {
            _secondarySizeSpan = new RelativeSizeSpan(0.8f);
        }
        return _secondarySizeSpan;
    }

    public static Object getSecondaryColorSizeSpan() {
        if (_secondaryColorSizeSpan == null) {
            _secondaryColorSizeSpan = new ColorSizeSpan(App.context.getResources().getColor(17170448), 0.8f);
        }
        return _secondaryColorSizeSpan;
    }

    public static void localizeSettingsPath(String string, Appendable sb2) throws IOException {
        int closeBracket;
        int resId;
        int start = 0;
        while (true) {
            int openBracket = string.indexOf(91, start);
            if (openBracket >= 0 && (closeBracket = string.indexOf(93, openBracket)) > 0) {
                String tag = string.substring(openBracket + 1, closeBracket);
                char c = 65535;
                switch (tag.hashCode()) {
                    case -1901885695:
                        if (tag.equals("Player")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1850559411:
                        if (tag.equals("Resume")) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1087880156:
                        if (tag.equals("Decoder")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 2368702:
                        if (tag.equals("List")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 981404069:
                        if (tag.equals("Folders")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1499275331:
                        if (tag.equals("Settings")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        resId = R.string.settings;
                        break;
                    case 1:
                        resId = R.string.cfg_list;
                        break;
                    case 2:
                        resId = R.string.cfg_scan_root;
                        break;
                    case 3:
                        resId = R.string.cfg_player;
                        break;
                    case 4:
                        resId = R.string.resume_last;
                        break;
                    case 5:
                        resId = R.string.decoder;
                        break;
                    default:
                        sb2.append(string, start, openBracket + 1);
                        start = openBracket + 1;
                        continue;
                }
                sb2.append(string, start, openBracket + 1);
                sb2.append(App.context.getString(resId));
                start = closeBracket;
            }
        }
        sb2.append(string, start, string.length());
    }

    public static String localizeSettingsPath(String string) {
        try {
            StringBuilder sb2 = new StringBuilder();
            localizeSettingsPath(string, sb2);
            return sb2.toString();
        } catch (IOException e) {
            return string;
        }
    }

    public static void localizeSettingsPath(int resId, Appendable sb2) throws IOException {
        localizeSettingsPath(App.context.getString(resId), sb2);
    }

    public static void localizeSettingsPath(int resId, StringBuilder sb2) {
        try {
            localizeSettingsPath(App.context.getString(resId), sb2);
        } catch (IOException e) {
        }
    }

    public static void localizeSettingsPath(int resId, SpannableStringBuilder sb2) {
        try {
            localizeSettingsPath(App.context.getString(resId), sb2);
        } catch (IOException e) {
        }
    }

    public static String localizeSettingsPath(int resId) {
        return localizeSettingsPath(App.context.getString(resId));
    }
}
