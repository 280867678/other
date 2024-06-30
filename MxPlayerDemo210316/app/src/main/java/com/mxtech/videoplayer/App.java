package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.mxtech.FileUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.os.Cpu;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.text.StringUtils;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.service.PlayService;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class App extends Application {

    public static Context context;
    public static OrderedSharedPreferences prefs;
    public static final String TAG = "MX";
    public static boolean native_initialized;
    public static boolean initialized = false;
    private boolean _preInitCalled = false;
    private boolean _initCalled = false;
    public static Handler handler;
    private Locale _backupLocale;
    public static ContentResolver cr;

    public final void init() {
        if (!this._preInitCalled) {
            this._preInitCalled = true;
            onPreInit();
        }
        if (!this._initCalled) {
            this._initCalled = true;
            onInit();
        }
    }

    public void onPreInit() {
        context = this;
        handler = new Handler();
        if (prefs == null) {
            prefs = new OrderedSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        }
        if (Build.VERSION.SDK_INT >= 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void onInit() {
        this._backupLocale = getResources().getConfiguration().locale;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        cr = getContentResolver();
        if (prefs == null) {
            prefs = new OrderedSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        }
    }





//    protected boolean onInitInteractive(Activity caller) {
//        return true;
//    }
//
//    public final boolean initInteractive(Activity caller) {
//        return onInitInteractive(caller);
//    }

    public final boolean initInteractive(Activity caller) {
        return onInitInteractive(caller);
    }

    public boolean onInitInteractive(Activity caller) {
//        return true;
//        if (initialized) {
//            return true;
//        }
//        if (!Cpu.classLoaded) {
//            dumpLibs(AppUtils.getNativeLibraryDir());
//            fatalMessage(caller, R.string.error_load_components, true);
//            return false;
//        }
////        PlayService.init();
//        L.updateFontConf(false);
        return new LibsLoader().load(caller, false);
    }




























    public static class CodecInfo {
        final boolean alternative;
        final String libaryPath;
        final PackageInfo packageInfo;

        public CodecInfo(PackageInfo packageInfo, String libraryPath, boolean alternative) {
            this.packageInfo = packageInfo;
            this.libaryPath = libraryPath;
            this.alternative = alternative;
        }
    }
















    @SuppressLint("ResourceType")
    public CodecInfo resolveCodec(Activity caller, PackageInfo myPackage, PackageManager pm, ApplicationInfo appInfo) {
        CodecInfo codecInfo;
        L.Codec codec = L.findCodec();
        if (codec == null) {
            fatalMessage(caller, R.string.error_unsupported_architecture, true);
            return null;
        }
        try {
            PackageInfo ffmpegPackageInfo = pm.getPackageInfo(codec.packageName, 0);
            int requiredFFmpegVersion = appInfo.metaData.getInt("ffmpeg_required_version_name");
            int installedVersion = ffmpegPackageInfo.versionCode;
            if (installedVersion < requiredFFmpegVersion) {
                openCodecPackage(caller, appInfo, codec, ffmpegPackageInfo, StringUtils.getString_s(R.string.error_codec_version, getString(17039370)), true);
                codecInfo = null;
            } else {
                ApplicationInfo codecInfo2 = pm.getApplicationInfo(codec.packageName, 128);
                int requiredPlayerVersion = codecInfo2.metaData.getInt("player_required_version");
                if (AppUtils.getVersion(myPackage.versionCode) < requiredPlayerVersion) {
                    openUpgradePage(caller, myPackage, codecInfo2, StringUtils.getString_s(R.string.error_player_version, getString(17039370)), true);
                    codecInfo = null;
                } else {
                    codecInfo = new CodecInfo(ffmpegPackageInfo, AppUtils.getNativeLibraryDir(codecInfo2), false);
                }
            }
            return codecInfo;
        } catch (PackageManager.NameNotFoundException e) {
            CodecInfo alt = getAlternativeCodec(myPackage);
            if (alt == null) {
                openCodecPackage(caller, appInfo, codec, null, StringUtils.getString_s(R.string.error_codec_not_found, getString(17039370)), true);
                return null;
            }
            return alt;
        }
    }


    @SuppressLint("ResourceType")
    public static void fatalMessage(Activity caller, int resId, boolean closeActivity) {
        if (caller != null) {
//            ActivityMessenger.showMessage(caller, resId);
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setMessage(caller.getString(resId));
            b.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
//            AlertDialog dialog = (AlertDialog) showDialog( b.create(), (DialogInterface.OnDismissListener) this);
//            if (needLikify) {
//                View messageView = dialog.findViewById(16908299);
//                if (messageView instanceof TextView) {
//                    ((TextView) messageView).setMovementMethod(LinkMovementMethod.getInstance());
//                }
//            }

            Toast.makeText(context, caller.getString(resId), Toast.LENGTH_SHORT).show();

            if (closeActivity) {
                caller.finish();
            }
        }
    }



    public final void openCodecPackage(Activity caller, ApplicationInfo appInfoMeta, L.Codec codec, PackageInfo packageInfo, String text, boolean closeActivity) {
//        if (caller != null) {
//            INavigator market = Market.getNavigator(this);
//            ArrayList<String> uris = new ArrayList<>(2);
//            uris.add(market.productDetailUri(codec.packageName));
//            uris.add(StringUtils.getString_s(R.string.direct_download_url, codec.packageName, codec.getArchId()));
//            SpannableStringBuilder b = new SpannableStringBuilder(text);
//            b.append((CharSequence) "\n\n");
//            int quoteStart = b.length();
//            b.append((CharSequence) getString(R.string.type)).append((CharSequence) ": ").append((CharSequence) codec.archName).append('\n');
//            decorateLine(b, quoteStart, b.length());
//            if (packageInfo != null) {
//                int quoteStart2 = b.length();
//                b.append((CharSequence) getString(R.string.installed_version)).append((CharSequence) ": ").append((CharSequence) packageInfo.versionName).append('\n');
//                decorateLine(b, quoteStart2, b.length());
//            }
//            int quoteStart3 = b.length();
//            b.append((CharSequence) getString(R.string.required_version)).append((CharSequence) ": ").append((CharSequence) toVersionName(appInfoMeta.metaData.get(FFMPEG_REQUIRED_VERSION_NAME))).append(' ').append((CharSequence) getString(R.string.or_later));
//            decorateLine(b, quoteStart3, b.length());
//            ActivityMessenger.openUri(caller, getString(R.string.install_codec), b, null, (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
//            if (closeActivity) {
//                caller.finish();
//            }
//        }


        Toast.makeText(context, "打开URLopenCodecPackage", Toast.LENGTH_SHORT).show();
    }


    public final void openUpgradePage(Activity caller, PackageInfo myPackage, ApplicationInfo codecInfoMeta, String text, boolean closeCaller) {
//        if (caller != null) {
//            ArrayList<String> uris = new ArrayList<>(2);
//            INavigator market = Market.getNavigator(this);
//            uris.add(market.productDetailUri(myPackage.packageName));
//            uris.add(StringUtils.getString_s(R.string.direct_download_url, myPackage.packageName, L.getSysArchId()));
//            SpannableStringBuilder b = new SpannableStringBuilder(text);
//            boolean haveAppendix = false;
//            if (0 == 0) {
//                haveAppendix = true;
//                b.append((CharSequence) "\n\n");
//            }
//            int quoteStart = b.length();
//            b.append((CharSequence) getString(R.string.installed_version)).append((CharSequence) ": ").append((CharSequence) myPackage.versionName).append('\n');
//            decorateLine(b, quoteStart, b.length());
//            CharSequence playerVersionName = toVersionName(codecInfoMeta.metaData.get(PLAYER_REQUIRED_VERSION_NAME));
//            if (playerVersionName != null) {
//                if (!haveAppendix) {
//                    b.append((CharSequence) "\n\n");
//                }
//                int quoteStart2 = b.length();
//                b.append((CharSequence) getString(R.string.required_version)).append((CharSequence) ": ").append(playerVersionName).append(' ').append((CharSequence) getString(R.string.or_later));
//                decorateLine(b, quoteStart2, b.length());
//            }
//            ActivityMessenger.openUri(caller, getString(R.string.upgrade), b, null, (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
//            if (closeCaller) {
//                caller.finish();
//            }
//        }


        Toast.makeText(context, "打开URLopenUpgradePage", Toast.LENGTH_SHORT).show();
    }

    private static final String[] kEssentialCodecFiles = {"libloader.mx.so", "libffmpeg.mx.so", "libft2.mx.so", "libmxass.so", "libmxutil.so", "libmxvp.so"};
    protected CodecInfo getAlternativeCodec(PackageInfo myPackageInfo) {
        String[] split;
        String[] strArr;
        String libPaths = System.getProperty("java.library.path");
        if (libPaths != null) {
            for (String path : libPaths.split(":")) {
                for (String name : kEssentialCodecFiles) {
                    File file = new File(path, name);
                    if (!file.exists() || file.length() == 0) {
                    }
                }
                return new CodecInfo(myPackageInfo, path, true);
            }
        }
        return null;
    }


    private static String[] SYS_DUMP_FILTER = {"libmx", ".mx.", "stagefright", "ffmpeg", "libav", "libsw"};
    public final void dumpLibs(String codecLib) {
        String[] split;
        Log.i(TAG, "Dump system libraries =========");
        String paths = System.getProperty("java.library.path");
        Log.i(TAG, "java.library.path: " + paths);
        if (paths != null) {
            for (String path : paths.split(File.pathSeparator)) {
                dumpPath(path, SYS_DUMP_FILTER);
            }
        }
        String libPath = AppUtils.getNativeLibraryDir();
        dumpPath(libPath);
        if (codecLib == null) {
            Log.i(TAG, "codec path is not specified.");
        } else if (!libPath.equals(codecLib)) {
            dumpPath(codecLib);
        }
        Log.i(TAG, "Dump End =========");
    }

    private void dumpPath(String path) {
        dumpPath(path, null);
    }



    private void dumpPath(String path, @Nullable String[] filter) {
        Log.i(TAG, "Dump '" + path + "' =========");
        File[] files = FileUtils.listFiles(new File(path));
        if (files == null) {
            Log.i(TAG, path + " is not exists.");
            return;
        }
        for (File file : files) {
            String filePath = file.getPath();
            if (filter != null) {
                for (String f : filter) {
                    if (!filePath.contains(f)) {
                    }
                }
            }
            Log.i(TAG, "\t" + filePath + ": size=" + file.length() + " date=" + DateUtils.formatDateTime(this, file.lastModified(), 655505));
        }
    }
    
    
    
    
    
    
}
