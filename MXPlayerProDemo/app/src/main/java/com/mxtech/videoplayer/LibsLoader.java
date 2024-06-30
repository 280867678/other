package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.IOUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.media.FFPlayer;
import com.mxtech.os.Cpu;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.CHK;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.preference.Key;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class LibsLoader {
    private static final int ERROR_UNSATISFIED_LINK_ERROR = -1;
    private static final int NO_ERROR = 0;
    private Handler throw_away_handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"HandlerLeak"})
    public boolean load(Activity caller, boolean forceCodecPackage) {
        File ffmpeg;
        String codecPath = null;
        try {
            PackageManager pm = App.context.getPackageManager();
            String packageName = App.context.getPackageName();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 128);
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            String libPath = AppUtils.getNativeLibraryDir(appInfo);
            App.CodecInfo codecInfo = null;
            if (!forceCodecPackage) {
                L.Codec codec = L.findCodec();
                if (codec != null && (CHK.CODEC.equals(codec.packageName) || ("com.mxtech.ffmpeg.tegra3".equals(CHK.CODEC) && CHK.CODEC.equals(codec.packageName)))) {
                    codecPath = libPath;
                    L.codecPackageOrSystemLib = packageName;
                } else {
                    Log.w(App.TAG, "Required codec: " + (codec != null ? codec.packageName : "null") + ", Embedded codec: " + CHK.CODEC);
                }
            }
            if (codecPath == null) {
                codecInfo = ((App) App.context).resolveCodec(caller, packageInfo, pm, appInfo);
                if (codecInfo == null) {
                    return false;
                }
                codecPath = codecInfo.libaryPath;
                L.codecPackageOrSystemLib = codecInfo.alternative ? codecInfo.libaryPath : codecInfo.packageInfo.packageName;
            }
            File mxutil = new File(codecPath, "libmxutil.so");
            if (mxutil.length() == 0) {
                Log.e(App.TAG, "Library missed: " + codecPath + "/" + mxutil.getName() + '=' + mxutil.length());
                if (codecPath == libPath) {
                    return load(caller, true);
                }
                ((App) App.context).dumpLibs(codecPath);
                App.fatalMessage(caller, codecPath == libPath ? R.string.error_reinstall_app : R.string.error_reinstall_codec, true);
                return false;
            } else if (codecPath == libPath && !testSize(mxutil)) {
                ((App) App.context).dumpLibs(codecPath);
                return load(caller, true);
            } else {
                AppUtils.loadLibrary(codecPath, "mxutil");
                String customCodecPath = App.prefs.getString(Key.CUSTOM_CODEC_PATH, null);
                if (customCodecPath != null) {
                    try {
                        String filesDir = App.context.getFilesDir().getPath();
                        L.CustomCodecVersionName cc = L.getCustomCodecVersionName(appInfo);
                        if (cc.name.equals(App.prefs.getString(Key.CUSTOM_CODEC_LIBNAME, null))) {
                            File libffmpeg = new File(filesDir, L.LIBFFMPEG);
                            if (!App.prefs.contains(Key.CUSTOM_CODEC_CHECKSUM)) {
                                byte[] buf = new byte[8192];
                                File srcFile = new File(customCodecPath);
                                if ("zip".equalsIgnoreCase(FileUtils.getExtension(customCodecPath))) {
                                    ZipFile zip = new ZipFile(srcFile);
                                    ZipEntry ffmpegEntry = null;
                                    Iterator it = Collections.list(zip.entries()).iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            break;
                                        }
                                        ZipEntry entry = (ZipEntry) it.next();
                                        if (!entry.isDirectory() && entry.getSize() > 0 && cc.name.equalsIgnoreCase(FileUtils.getName(entry.getName()))) {
                                            ffmpegEntry = entry;
                                            break;
                                        }
                                    }
                                    if (ffmpegEntry == null) {
                                        throw new IllegalStateException();
                                    }
                                    InputStream in = null;
                                    OutputStream out = null;
                                    try {
                                        in = zip.getInputStream(ffmpegEntry);
                                        OutputStream out2 = new FileOutputStream(libffmpeg);
                                        try {
                                            IOUtils.transferStream(in, out2, buf);
                                            if (out2 != null) {
                                                out2.close();
                                            }
                                            if (in != null) {
                                                in.close();
                                            }
                                            zip.close();
                                        } catch (Throwable th) {
                                            th = th;
                                            out = out2;
                                            if (out != null) {
                                                out.close();
                                            }
                                            if (in != null) {
                                                in.close();
                                            }
                                            throw th;
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                    }
                                } else {
                                    FileUtils.copyFile(customCodecPath, libffmpeg.getPath(), buf);
                                }
                                SharedPreferences.Editor editor = App.prefs.edit();
                                editor.putLong(Key.CUSTOM_CODEC_CHECKSUM, L.getCustomCodecChecksum(customCodecPath));
                                editor.putLong(Key.CUSTOM_CODEC_libffmpeg_DATE, srcFile.lastModified());
                                editor.putInt(Key.CUSTOM_CODEC_libffmpeg_SIZE, (int) srcFile.length());
                                AppUtils.apply(editor);
                            }
                            AppUtils.loadLibrary(filesDir, L.FFMPEG);
                            L.customFFmpegPath = filesDir;
                        } else {
                            L.unloadCustomCodec();
                            L.simpleSnackbarMessages.add(App.context.getString(R.string.obsolete_custom_codec));
                        }
                    } catch (Throwable e) {
                        Log.e(App.TAG, "", e);
                        L.unloadCustomCodec();
                        L.simpleSnackbarMessages.add(App.context.getString(R.string.abandon_custom_codec));
                    }
                }
                if (L.customFFmpegPath == null) {
                    ffmpeg = new File(codecPath, L.LIBFFMPEG);
                } else {
                    ffmpeg = null;
                }
                File mxvp = new File(codecPath, "libmxvp.so");
                File ft2 = new File(codecPath, "libft2.mx.so");
                File mxass = new File(codecPath, "libmxass.so");
                int sysdecVersion = L.getSysDecoderVersion();
                File mxsysdec = new File(codecPath, "libmxsysdec." + sysdecVersion + ".so");
                if ((ffmpeg != null && ffmpeg.length() == 0) || mxvp.length() == 0 || ft2.length() == 0 || mxass.length() == 0) {
                    Log.e(App.TAG, "Libraries are missed:" + (ffmpeg != null ? ffmpeg.getName() + '=' + ffmpeg.length() + ' ' : "") + mxvp.getName() + '=' + mxvp.length() + ' ' + ft2.getName() + '=' + ft2.length() + ' ' + mxass.getName() + '=' + mxass.length() + ' ' + mxsysdec.getName() + '=' + mxsysdec.length() + " (codec path:" + codecPath + ')');
                    if (codecPath == libPath) {
                        return load(caller, true);
                    }
                    ((App) App.context).dumpLibs(codecPath);
                    App.fatalMessage(caller, codecPath == libPath ? R.string.error_reinstall_app : R.string.error_reinstall_codec, true);
                    return false;
                } else if (codecPath == libPath && !testSize(ffmpeg, mxvp, ft2, mxass, mxsysdec, sysdecVersion)) {
                    ((App) App.context).dumpLibs(codecPath);
                    return load(caller, true);
                } else {
                    if (ffmpeg != null) {
                        AppUtils.loadLibrary(codecPath, L.FFMPEG);
                    }
                    AppUtils.loadLibrary(codecPath, "mxvp");
                    if (pm.hasSystemFeature("android.hardware.audio.low_latency")) {
                        L.mainFlags |= 536870912;
                    }
                    int outputSampleRate = 0;
                    int outputFramesPerBuffer = 0;
                    if (Build.VERSION.SDK_INT >= 17) {
                        try {
                            AudioManager am = (AudioManager) App.context.getSystemService("audio");
                            String sampleRate = am.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
                            if (sampleRate != null) {
                                outputSampleRate = Integer.parseInt(sampleRate);
                            }
                            String framesPerBuffer = am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
                            if (framesPerBuffer != null) {
                                outputFramesPerBuffer = Integer.parseInt(framesPerBuffer);
                            }
                        } catch (Throwable e2) {
                            Log.e(App.TAG, "", e2);
                        }
                    }
                    int status = L.native_init(App.context, L.mainFlags, Build.VERSION.SDK_INT, codecInfo != null ? codecInfo.packageInfo : null, App.context.getFilesDir().getPath(), App.context.getFileStreamPath("stats").getPath(), Cpu.coreCount, outputSampleRate, outputFramesPerBuffer);
                    switch (status) {
                        case -1:
                            App.fatalMessage(caller, R.string.error_load_components, true);
                            return false;
                        case 0:
                        default:
                            FFPlayer.native_static_init();
                            L.hasOMXDecoder = L.hasOMXDecoder();
                            if (!L.hasOMXDecoder) {
                                SharedPreferences.Editor editor2 = null;
                                if (App.prefs.contains(Key.OMX_DECODER)) {
                                    if (0 == 0) {
                                        editor2 = App.prefs.edit();
                                    }
                                    editor2.remove(Key.OMX_DECODER);
                                }
                                if (App.prefs.contains(Key.OMX_DECODER_LOCAL)) {
                                    if (editor2 == null) {
                                        editor2 = App.prefs.edit();
                                    }
                                    editor2.remove(Key.OMX_DECODER_LOCAL);
                                }
                                if (App.prefs.contains(Key.OMX_DECODER_NETWORK)) {
                                    if (editor2 == null) {
                                        editor2 = App.prefs.edit();
                                    }
                                    editor2.remove(Key.OMX_DECODER_NETWORK);
                                }
                                if (App.prefs.contains(Key.TRY_HW_IF_OMX_FAILS)) {
                                    if (editor2 == null) {
                                        editor2 = App.prefs.edit();
                                    }
                                    editor2.remove(Key.TRY_HW_IF_OMX_FAILS);
                                }
                                if (App.prefs.contains(Key.OMX_VIDEO_CODECS)) {
                                    if (editor2 == null) {
                                        editor2 = App.prefs.edit();
                                    }
                                    editor2.remove(Key.OMX_VIDEO_CODECS);
                                }
                                if (editor2 != null) {
                                    AppUtils.apply(editor2);
                                }
                            }
                            App.native_initialized = true;
                            App.initialized = true;
                            return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e3) {
            Log.e(App.TAG, "", e3);
            App.fatalMessage(caller, R.string.error_unexpected, true);
            return false;
        } catch (Exception e4) {
            Log.e(App.TAG, "", e4);
            ((App) App.context).dumpLibs(null);
            App.fatalMessage(caller, R.string.error_load_components, true);
            return false;
        } catch (UnsatisfiedLinkError e5) {
            Log.e(App.TAG, "", e5);
            ((App) App.context).dumpLibs(null);
            App.fatalMessage(caller, R.string.error_load_components, true);
            if (L.customFFmpegPath != null) {
                L.unloadCustomCodec();
                L.simpleSnackbarMessages.add(App.context.getString(R.string.abandon_custom_codec));
            }
            return false;
        }
    }

    private static boolean testSize(File ffmpeg, File mxvp, File ft2, File mxass, File mxsysdec, int sysdecVersion) {
        long mxsysdecSize;
        if (ffmpeg != null && ffmpeg.length() != CHK.SIZE.ffmpeg) {
            Log.e(App.TAG, "ffmpeg file size mismatch. expected=9362216 installed=" + ffmpeg.length());
            return false;
        } else if (mxvp != null && mxvp.length() != CHK.SIZE.mxvp) {
            Log.e(App.TAG, "mxvp file size mismatch. expected=853960 installed=" + mxvp.length());
            return false;
        } else if (ft2 != null && ft2.length() != CHK.SIZE.ft2) {
            Log.e(App.TAG, "ft2 file size mismatch. expected=554504 installed=" + ft2.length());
            return false;
        } else if (mxass != null && mxass.length() != CHK.SIZE.mxass) {
            Log.e(App.TAG, "mxass file size mismatch. expected=452204 installed=" + mxass.length());
            return false;
        } else {
            switch (sysdecVersion) {
                case 8:
                    mxsysdecSize = 0;
                    break;
                case 9:
                    mxsysdecSize = 0;
                    break;
                case 10:
                case 12:
                case 13:
                case 15:
                case 16:
                case 17:
                case 19:
                case 20:
                default:
                    throw new IllegalArgumentException("Invalid sysdec version.");
                case 11:
                    mxsysdecSize = 0;
                    break;
                case 14:
                    mxsysdecSize = 177876;
                    break;
                case 18:
                    mxsysdecSize = 177876;
                    break;
                case 21:
                    mxsysdecSize = CHK.SIZE.mxsysdec_21;
                    break;
            }
            if (mxsysdec != null && mxsysdec.length() != mxsysdecSize) {
                Log.e(App.TAG, "mxsysdec." + sysdecVersion + " file size mismatch. expected=" + mxsysdecSize + " installed=" + mxsysdec.length());
                return false;
            }
            return true;
        }
    }

    private static boolean testSize(File mxutil) {
        if (mxutil == null || mxutil.length() == CHK.SIZE.mxutil) {
            return true;
        }
        Log.e(App.TAG, "mxutil file size mismatch. expected=868704 installed=" + mxutil.length());
        return false;
    }
}
