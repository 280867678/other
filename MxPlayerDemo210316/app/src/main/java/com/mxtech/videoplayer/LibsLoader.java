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
//import com.mxtech.IOUtils;
import com.mxtech.IOUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.os.Cpu;
import com.mxtech.videoplayer.pro.R;
//import com.mxtech.app.MXApplication;
//import com.mxtech.os.Cpu;
//import com.mxtech.videoplayer.CHK;
//import com.mxtech.videoplayer.preference.Key;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class LibsLoader {
    public static final String TAG = "MX";
    private Handler throw_away_handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"HandlerLeak"})
    public boolean load(Activity caller, boolean forceCodecPackage) {
        File ffmpeg;
        String codecPath = null;
        try {


            PackageManager pm = App.context.getPackageManager();
            String packageName = App.context.getPackageName();
            @SuppressLint("WrongConstant") ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 128);
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            String libPath = AppUtils.getNativeLibraryDir(appInfo);
            App.CodecInfo codecInfo = null;
            if (!forceCodecPackage) {
                L.Codec codec = L.findCodec();
                if (codec != null && ("com.mxtech.ffmpeg.v7_neon".equals(codec.packageName) || ("com.mxtech.ffmpeg.tegra3".equals("com.mxtech.ffmpeg.v7_neon") && "com.mxtech.ffmpeg.v7_neon".equals(codec.packageName)))) {
                    codecPath = libPath;
                    L.codecPackageOrSystemLib = packageName;
                } else {
                    Log.w(TAG, "Required codec: " + (codec != null ? codec.packageName : "null") + ", Embedded codec: " + "com.mxtech.ffmpeg.v7_neon");
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
                Log.e(TAG, "Library missed: " + codecPath + "/" + mxutil.getName() + '=' + mxutil.length());
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
                String customCodecPath = App.prefs.getString("custom_codec", null);
                if (customCodecPath != null) {

                    String filesDir = App.context.getFilesDir().getPath();
                    L.CustomCodecVersionName cc = L.getCustomCodecVersionName(appInfo);
                    if (cc.name.equals(App.prefs.getString("custom_codec.libname", null))) {
                        File libffmpeg = new File(filesDir, "libffmpeg.mx.so");
                        if (!App.prefs.contains("custom_codec_checksum")) {
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

                                in = zip.getInputStream(ffmpegEntry);
                                OutputStream out2 = new FileOutputStream(libffmpeg);

                                IOUtils.transferStream(in, out2, buf);
                                if (out2 != null) {
                                    out2.close();
                                }
                                if (in != null) {
                                    in.close();
                                }
                                zip.close();


                            } else {
                                FileUtils.copyFile(customCodecPath, libffmpeg.getPath(), buf);
                            }
                            SharedPreferences.Editor editor = App.prefs.edit();
                            editor.putLong("custom_codec_checksum", L.getCustomCodecChecksum(customCodecPath));
                            editor.putLong("custom_codec.date.libffmpeg", srcFile.lastModified());
                            editor.putInt("custom_codec.size.libffmpeg", (int) srcFile.length());
                            AppUtils.apply(editor);
                        }
                        AppUtils.loadLibrary(filesDir, "ffmpeg.mx");
                        L.customFFmpegPath = filesDir;
                    } else {
                        L.unloadCustomCodec();
                        L.simpleSnackbarMessages.add(App.context.getString(R.string.obsolete_custom_codec));
                    }

                }
                if (L.customFFmpegPath == null) {
                    ffmpeg = new File(codecPath, "libffmpeg.mx.so");
                } else {
                    ffmpeg = null;
                }
                File mxvp = new File(codecPath, "libmxvp.so");
                File ft2 = new File(codecPath, "libft2.mx.so");
                File mxass = new File(codecPath, "libmxass.so");
                int sysdecVersion = L.getSysDecoderVersion();
                File mxsysdec = new File(codecPath, "libmxsysdec." + sysdecVersion + ".so");
                if ((ffmpeg != null && ffmpeg.length() == 0) || mxvp.length() == 0 || ft2.length() == 0 || mxass.length() == 0) {
                    Log.e(TAG, "Libraries are missed:" + (ffmpeg != null ? ffmpeg.getName() + '=' + ffmpeg.length() + ' ' : "") + mxvp.getName() + '=' + mxvp.length() + ' ' + ft2.getName() + '=' + ft2.length() + ' ' + mxass.getName() + '=' + mxass.length() + ' ' + mxsysdec.getName() + '=' + mxsysdec.length() + " (codec path:" + codecPath + ')');
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
                        AppUtils.loadLibrary(codecPath, "ffmpeg.mx");
                    }
                    AppUtils.loadLibrary(codecPath, "mxvp");
                    if (pm.hasSystemFeature("android.hardware.audio.low_latency")) {
                        L.mainFlags |= 536870912;
                    }
                    int outputSampleRate = 0;
                    int outputFramesPerBuffer = 0;
                    if (Build.VERSION.SDK_INT >= 17) {

                        @SuppressLint("WrongConstant") AudioManager am = (AudioManager) App.context.getSystemService("audio");
                        String sampleRate = am.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
                        if (sampleRate != null) {
                            outputSampleRate = Integer.parseInt(sampleRate);
                        }
                        String framesPerBuffer = am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
                        if (framesPerBuffer != null) {
                            outputFramesPerBuffer = Integer.parseInt(framesPerBuffer);
                        }

                    }

                    L.native_init(App.context, L.mainFlags, Build.VERSION.SDK_INT, codecInfo != null ? codecInfo.packageInfo : null, App.context.getFilesDir().getPath(), App.context.getFileStreamPath("stats").getPath(), Cpu.coreCount, outputSampleRate, outputFramesPerBuffer);
                    App.native_initialized = true;
                    App.initialized = true;
                    L.loadHWDecoder(false);
                    return true;

                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    private static boolean testSize(File ffmpeg, File mxvp, File ft2, File mxass, File mxsysdec, int sysdecVersion) {
        long mxsysdecSize;
        if (ffmpeg != null && ffmpeg.length() != CHK.SIZE.ffmpeg) {
            Log.e(TAG, "ffmpeg file size mismatch. expected=9815160 installed=" + ffmpeg.length());
            return false;
        } else if (mxvp != null && mxvp.length() != CHK.SIZE.mxvp) {
            Log.e(TAG, "mxvp file size mismatch. expected=972688 installed=" + mxvp.length());
            return false;
        } else if (ft2 != null && ft2.length() != CHK.SIZE.ft2) {
            Log.e(TAG, "ft2 file size mismatch. expected=574972 installed=" + ft2.length());
            return false;
        } else if (mxass != null && mxass.length() != CHK.SIZE.mxass) {
            Log.e(TAG, "mxass file size mismatch. expected=493216 installed=" + mxass.length());
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
                    mxsysdecSize = 177808;
                    break;
                case 18:
                    mxsysdecSize = 177808;
                    break;
                case 21:
                    mxsysdecSize = CHK.SIZE.mxsysdec_21;
                    break;
            }
            if (mxsysdec != null && mxsysdec.length() != mxsysdecSize) {
                Log.e(TAG, "mxsysdec." + sysdecVersion + " file size mismatch. expected=" + mxsysdecSize + " installed=" + mxsysdec.length());
                return false;
            }
            return true;
        }
    }

    private static boolean testSize(File mxutil) {
        if (mxutil == null || mxutil.length() == CHK.SIZE.mxutil) {
            return true;
        }
        Log.e(TAG, "mxutil file size mismatch. expected=959240 installed=" + mxutil.length());
        return false;
    }
}
