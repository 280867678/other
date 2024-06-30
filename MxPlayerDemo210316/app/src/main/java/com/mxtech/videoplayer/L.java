package com.mxtech.videoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mxtech.FileUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.os.Cpu;
import com.mxtech.os.Model;
import com.mxtech.preference.P;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.pro.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class L {

    public static final String TAG = "MX";
    public static String codecPackageOrSystemLib = App.context.getPackageName();
    private static CustomCodecVersionName _customCodecVersionName;
    public static int mainFlags = 0;
    public static String customFFmpegPath;
    public static boolean hasOMXDecoder;
    public static final List<String> simpleSnackbarMessages = new ArrayList();




    public static String getCodecLibPath() {
//        if (codecPackageOrSystemLib.startsWith(Library.PACKAGE_COMMON)) {
//            try {
//                PackageManager pm = App.context.getPackageManager();
//                ApplicationInfo info = pm.getApplicationInfo(codecPackageOrSystemLib, 0);
//                return AppUtils.getNativeLibraryDir(info);
//            } catch (PackageManager.NameNotFoundException e) {
//                Log.e(TAG, "", e);
//                return null;
//            }
//        }
//        return codecPackageOrSystemLib;
//        return null;



        try {
            PackageManager pm = App.context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(codecPackageOrSystemLib, 0);
            return AppUtils.getNativeLibraryDir(info);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("L:", "", e);
            return null;
        }




    }


    public static native int getAvailableHWDecoders();

    public static native int getCurrentHWDecoder();

    public static native int getPreferredHWDecoder();

    private static native int loadHWDecoder(int i);

    public static native void native_init(Context context, int i, int i2, Object obj, String str, String str2, int i3, int i4, int i5);










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



    public static class CustomCodecVersionName {
        public final String name;
        public final String version;

        public CustomCodecVersionName(String archCode, String version) {
            this.name = "libffmpeg.mx.so." + archCode + "." + version;
            this.version = version;
        }
    }

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


    public static Codec findCodec() {
        switch (Cpu.family) {
            case 1:
                if ((Cpu.features & 4) != 0) {
                    if ((mainFlags & 2048) == 0) {
                        return new Codec("com.mxtech.ffmpeg.v7_neon", "ARMv7 NEON", "neon");
                    }
                    return new Codec("com.mxtech.ffmpeg.tegra3", "Tegra 3", "tegra3");
                } else if ((Cpu.features & 1) != 0) {
                    return new Codec("com.mxtech.ffmpeg.v7_vfpv3d16", "ARMv7", "tegra2");
                } else {
                    if ((Cpu.features & 8) != 0) {
                        Log.e(TAG, "ARMv6 family is no longer supported.");
                        return null;
                    }
                    Log.e(TAG, "ARMv5 family is no longer supported.");
                    return null;
                }
            case 2:
                if ((Cpu.features & 1) != 0 || Model.model == 1400) {
                    return new Codec("com.mxtech.ffmpeg.x86", "x86", "x86");
                }
                Log.e(TAG, "Unsupported x86 feature: " + Cpu.features);
                return null;
            case 3:
                Log.e(TAG, "MIPS family is no longer supported.");
                return null;
            case 4:
            default:
                Log.e(TAG, "Unknown CPU family: " + Cpu.family);
                return null;
            case 5:
                Log.e(TAG, "x86_64 is yet supported.");
                return null;
        }
    }







    public static long getCustomCodecChecksum(String filePath) throws IOException {
        FileUtils.Adler32Checksum adler32 = new FileUtils.Adler32Checksum();
        adler32.update(filePath);
        return adler32.getValue();
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


    public static void unloadCustomCodec() {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.remove(Key.CUSTOM_CODEC_PATH);
        removeCustomCodecChecksum(editor);
        AppUtils.apply(editor);
        customFFmpegPath = null;
        deleteLoadedCustomCodecFiles();
    }

    public static void removeCustomCodecChecksum(SharedPreferences.Editor edit) {
        edit.remove(Key.CUSTOM_CODEC_CHECKSUM);
        edit.remove(Key.CUSTOM_CODEC_libffmpeg_DATE);
        edit.remove(Key.CUSTOM_CODEC_libffmpeg_SIZE);
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















    public static void loadHWDecoder(boolean verbose) {
        int alternative;
        SharedPreferences.Editor editor = null;
        String message = null;
        if (P.useAlternativeOMXDecoder() && (alternative = getAlternativeHWDecoder()) != 0) {
            hasOMXDecoder = loadHWDecoder(alternative) != 0;
            if (!hasOMXDecoder) {
                if (0 == 0) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.OMX_DECODER_ALTERNATIVE);
                if (verbose) {
                    message = App.context.getString(R.string.omx_decoder_alt_init_failure);
                }
            } else {
                return;
            }
        }
        hasOMXDecoder = loadHWDecoder(3) != 0;
        if (!hasOMXDecoder) {
            if (verbose) {
                String message2 = App.context.getString(R.string.omx_decoder_init_failure);
                if (message != null) {
                    message = message + '\n' + message2;
                } else {
                    message = message2;
                }
            }
            if (App.prefs.contains(Key.OMX_DECODER)) {
                if (editor == null) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.OMX_DECODER);
            }
            if (App.prefs.contains(Key.OMX_DECODER_LOCAL)) {
                if (editor == null) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.OMX_DECODER_LOCAL);
            }
            if (App.prefs.contains(Key.OMX_DECODER_NETWORK)) {
                if (editor == null) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.OMX_DECODER_NETWORK);
            }
            if (App.prefs.contains(Key.TRY_HW_IF_OMX_FAILS)) {
                if (editor == null) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.TRY_HW_IF_OMX_FAILS);
            }
            if (App.prefs.contains(Key.OMX_VIDEO_CODECS)) {
                if (editor == null) {
                    editor = App.prefs.edit();
                }
                editor.remove(Key.OMX_VIDEO_CODECS);
            }
        }
        if (editor != null) {
            AppUtils.apply(editor);
        }
        if (message != null) {
            Toast.makeText(App.context, message, Toast.LENGTH_LONG).show();
        }
    }






















    private static int FC_IGNORE_BROKEN = 2;
    public static final StringBuilder sb = new StringBuilder();


    public static int getAlternativeHWDecoder() {
        return getAvailableHWDecoders() & (getPreferredHWDecoder() ^ (-1));
    }



    public static void updateFontConf(boolean forceRebuild) {
        int fcflags = App.prefs.getBoolean(Key.SSA_BROKEN_FONT_IGNORE, false) ? 0 | FC_IGNORE_BROKEN : 0;
        updateFontConf(forceRebuild, P.getFontFolder().getPath(), fcflags);
    }



    private static void updateFontConf(boolean forceRebuild, String fontDirPath, int fcflags) {
//        if (App.prefs.getInt(Key.FONT_CACHE_VERSION, 0) < 1) {
//            SharedPreferences.Editor editor = App.prefs.edit();
//            editor.putInt(Key.FONT_CACHE_VERSION, 1);
//            AppUtils.apply(editor);
//            File oldDir = getFontCacheDir_v0();
//            clearFontCache(oldDir);
//            Log.i(TAG, "Removed old font cache directory " + oldDir);
//        }
//        File file = new File(App.context.getFilesDir(), "font.conf");
//        File cacheDir = getFontCacheDir();
//        if (forceRebuild || !checkFontConf(file, cacheDir, "/system/fonts", fontDirPath)) {
//            cacheDir.mkdirs();
//            sb.setLength(0);
//            sb.append("<?xml version=\"1.0\"?>").append("<!DOCTYPE fontconfig SYSTEM \"fonts.dtd\">").append("<fontconfig>").append("<dir>/system/fonts</dir>").append("<dir>").append(TextUtils.htmlEncode(fontDirPath)).append("</dir>").append("<cachedir>").append(cacheDir.getPath()).append("</cachedir>");
//            if ((FC_IGNORE_BROKEN & fcflags) != 0) {
//                String[] BROKEN_FAMILIES = {"DFHSGothic-W7", "DFPHSGothic-W7", "DFGHSGothic-W7"};
//                for (String family : BROKEN_FAMILIES) {
//                    sb.append("<match target=\"pattern\">").append("<test qual=\"any\" name=\"family\">").append("<string>").append(family).append("</string>").append("</test>").append("<edit name=\"family\" mode=\"assign\" binding=\"same\">").append("<string>sans-serif</string>").append("</edit>").append("</match>");
//                }
//            }
//            sb.append("<match>").append("<edit name=\"family\" mode=\"append_last\">").append("<string>sans-serif</string>").append("</edit>").append("</match>");
//            appendAlias(sb, "serif", null, new String[]{"Times New Roman", "Times", "Droid Serif"}, null);
//            appendAlias(sb, "sans-serif", null, new String[]{"Arial", "Roboto", "Droid Sans"}, null);
//            appendAlias(sb, "monospace", null, new String[]{"Courier New", "Courier", "Droid Sans Mono"}, null);
//            sb.append("</fontconfig>");
//            try {
//                Writer out = new FileWriter(file, false);
//                try {
//                    out.write(sb.toString());
//                    out.flush();
//                    out.close();
//                    for (ActivityScreen a : ActivityRegistry.findByClass(ActivityScreen.class)) {
//                        a.rebuildFonts();
//                    }
//                } catch (Throwable th) {
//                    out.close();
//                    throw th;
//                }
//            } catch (FileNotFoundException e) {
//                Log.w(TAG, "", e);
//            } catch (IOException e2) {
//                Log.w(TAG, "", e2);
//            }
//        }
    }
    
    
    
    

}
