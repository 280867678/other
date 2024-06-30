package com.mxtech.videoplayer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.Library;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.AppUtils;
import com.mxtech.app.Authorizer;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.io.DocumentTreeRegistry;
import com.mxtech.market.INavigator;
import com.mxtech.market.Market;
import com.mxtech.os.Cpu;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.service.PlayService;
import com.mxtech.videoplayer.subtitle.service.TitleSuggestionCache;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class App extends MXApplication {
    private static final int DATE_FORMAT = 655505;
    static final String FFMPEG_REQUIRED_VERSION = "ffmpeg_required_version";
    static final String FFMPEG_REQUIRED_VERSION_NAME = "ffmpeg_required_version_name";
    static final String PLAYER_REQUIRED_VERSION = "player_required_version";
    static final String PLAYER_REQUIRED_VERSION_NAME = "player_required_version_name";
    private static Intent _restartIntent;
    public static ContentResolver cr;
    public static boolean quitting;
    private Tracker _tracker;
    public static String TAG = "MX/J";
    public static boolean initialized = false;
    private static String[] SYS_DUMP_FILTER = {"libmx", ".mx.", "stagefright", "ffmpeg", "libav", "libsw"};
    public static final String TAG_ANALYTICS = TAG + ".Analytics";

    public abstract boolean isDirectLicensed();

    public abstract Boolean isLicenseVerified();

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXApplication
    public void onPreInit() {
        int i = 0;
        super.onPreInit();
        if (!DeviceUtils.getTVMode(getResources().getConfiguration())) {
            boolean forceTVMode = P.getTVMode();
            if (forceTVMode) {
                DeviceUtils.forceTVMode(true);
            }
        }
        System.setProperty("java.io.tmpdir", getDir("files", 0).getPath());
        cr = getContentResolver();
        String userLocale = prefs.getString(Key.USER_LOCALE, "");
        if (userLocale.length() > 0) {
            boolean supported = false;
            String[] stringArray = getResources().getStringArray(R.array.translated_locales);
            int length = stringArray.length;
            while (true) {
                if (i >= length) {
                    break;
                }
                String locale = stringArray[i];
                if (!locale.equals(userLocale)) {
                    i++;
                } else {
                    supported = true;
                    break;
                }
            }
            if (supported) {
                setCustomLocale(LocaleUtils.parse(userLocale));
                return;
            }
            Log.i(TAG, "User locale '" + userLocale + "' is removed as is not supported anymore.");
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(Key.USER_LOCALE);
            AppUtils.apply(editor);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXApplication
    public void onInit() {
        super.onInit();
        L.authorizer = new Authorizer(this, 0);
        String flurryApiKey = getString(R.string.flurry_api_key);
        FlurryAgent.setLogEnabled(DEBUG);
        FlurryAgent.init(this, flurryApiKey);
        Library.init(this, R.string.button_reset);
        ActivityScreen.static_init(this);
        L.thumbStorage = new ThumbStorage(this);
        if (Build.VERSION.SDK_INT >= 21) {
            DocumentTreeRegistry.init(new MediaDatabase.DocumentTreeStorage());
        }
    }

    @Override // com.mxtech.app.MXApplication
    public boolean onInitInteractive(Activity caller) {
        if (initialized) {
            return true;
        }
        if (!Cpu.classLoaded) {
            dumpLibs(AppUtils.getNativeLibraryDir());
            fatalMessage(caller, R.string.error_load_components, true);
            return false;
        }
        PlayService.init();
        L.updateFontConf(false);
        return new LibsLoader().load(caller, false);
    }

    @Override // com.mxtech.app.MXApplication, android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        P.onConfigurationChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodecInfo resolveCodec(Activity caller, PackageInfo myPackage, PackageManager pm, ApplicationInfo appInfo) {
        CodecInfo codecInfo;
        L.Codec codec = L.findCodec();
        if (codec == null) {
            fatalMessage(caller, R.string.error_unsupported_architecture, true);
            return null;
        }
        try {
            PackageInfo ffmpegPackageInfo = pm.getPackageInfo(codec.packageName, 0);
            int requiredFFmpegVersion = appInfo.metaData.getInt(FFMPEG_REQUIRED_VERSION);
            int installedVersion = ffmpegPackageInfo.versionCode;
            if (installedVersion < requiredFFmpegVersion) {
                openCodecPackage(caller, appInfo, codec, ffmpegPackageInfo, StringUtils.getString_s(R.string.error_codec_version, getString(17039370)), true);
                codecInfo = null;
            } else {
                ApplicationInfo codecInfo2 = pm.getApplicationInfo(codec.packageName, 128);
                int requiredPlayerVersion = codecInfo2.metaData.getInt(PLAYER_REQUIRED_VERSION);
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

    protected CodecInfo getAlternativeCodec(PackageInfo myPackage) {
        return null;
    }

    public static void fatalMessage(Activity caller, int resId, boolean closeActivity) {
        if (caller != null) {
            ActivityMessenger.showMessage(caller, resId);
            if (closeActivity) {
                caller.finish();
            }
        }
    }

    public void updateHelpCommand(Menu menu) {
    }

    public boolean handleHelpCommand(ActivityVPBase activity, int id) {
        boolean z = true;
        try {
            if (id == R.id.checkVersion) {
                new VersionChecker(activity);
            } else if (id == R.id.send_bug_report) {
                new ErrorReportSender(activity);
            } else if (id == R.id.whats_new) {
                activity.showNoticeDialog();
            } else if (id == R.id.features) {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.features_url))));
            } else if (id == R.id.faq) {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.faq_url))));
            } else if (id == R.id.about) {
                activity.startActivity(new Intent(this, ActivityAbout.class));
            } else {
                z = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return z;
    }

    /* loaded from: classes.dex */
    private class VersionChecker extends AppCompatProgressDialog implements Handler.Callback {
        private final Activity _activity;
        private final DialogRegistry _dialogRegistry;
        private final Handler _handler;

        VersionChecker(Activity activity) {
            super(activity);
            this._activity = activity;
            this._handler = new Handler(this);
            this._dialogRegistry = DialogRegistry.registryOf(activity);
            setCancelable(true);
            setProgressStyle(0);
            setMessage(App.this.getString(R.string.version_checking));
            if (this._dialogRegistry != null) {
                setOnDismissListener(this._dialogRegistry);
                this._dialogRegistry.register(this);
            }
            show();
            L.authorizer.authNow(this._handler, 100);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            if (msg.what != 100) {
                return false;
            }
            if (this._activity.isFinishing() || !(this._dialogRegistry == null || this._dialogRegistry.contains(this))) {
                return true;
            }
            dismiss();
            if (ActivityRegistry.getForegroundActivity() != this._activity) {
                return true;
            }
            if (msg.arg1 != 0) {
                DialogUtils.alert(this._activity, (CharSequence) App.this.getString(R.string.version_checking_failed));
                return true;
            }
            try {
                PackageInfo currentPackage = App.this.getPackageManager().getPackageInfo(App.this.getPackageName(), 0);
                if (!L.authorizer.inquireUpdate(this._activity, currentPackage, 3, new VersionUpdateHandler(this._activity))) {
                    DialogUtils.alert(this._activity, (CharSequence) App.this.getString(R.string.version_checking_uptodate));
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(App.TAG, "", e);
            }
            return true;
        }
    }

    /* loaded from: classes.dex */
    public class VersionUpdateHandler implements DialogInterface.OnClickListener {
        private final Activity _caller;

        public VersionUpdateHandler(Activity caller) {
            this._caller = caller;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            INavigator market;
            Uri uri;
            String uriString = L.authorizer.getUpgradeUrl();
            if (uriString != null) {
                uri = Uri.parse(uriString);
                market = Market.getNavigatorFromPackageUri(uri);
            } else if (App.this.isDirectLicensed()) {
                market = null;
                uri = Uri.parse(StringUtils.getString_s(R.string.direct_download_url, App.this.getPackageName(), L.getSysArchId()));
            } else {
                market = Market.getNavigator(App.this);
                uri = Uri.parse(market.productDetailUri(App.this.getPackageName()));
            }
            try {
                try {
                    this._caller.startActivity(new Intent("android.intent.action.VIEW", uri));
                } catch (ActivityNotFoundException e) {
                    if (market != null) {
                        try {
                            this._caller.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(StringUtils.getString_s(R.string.direct_download_url, App.this.getPackageName(), L.getSysArchId()))));
                        } catch (ActivityNotFoundException e2) {
                        }
                    }
                    if (!this._caller.isFinishing()) {
                        DialogUtils.alert(this._caller, (CharSequence) App.this.getString(R.string.cannot_open_downloader));
                    }
                }
            } catch (Exception e3) {
                Log.e(App.TAG, "", e3);
            }
        }
    }

    private void decorateLine(Spannable s, int begin, int end) {
        s.setSpan(new BulletSpan((int) DeviceUtils.DIPToPixel(4.0f)), begin, end, 33);
        s.setSpan(new RelativeSizeSpan(0.8f), begin, end, 33);
    }

    public final void openUpgradePage(Activity caller, PackageInfo myPackage, ApplicationInfo codecInfoMeta, String text, boolean closeCaller) {
        if (caller != null) {
            ArrayList<String> uris = new ArrayList<>(2);
            INavigator market = Market.getNavigator(this);
            uris.add(market.productDetailUri(myPackage.packageName));
            uris.add(StringUtils.getString_s(R.string.direct_download_url, myPackage.packageName, L.getSysArchId()));
            SpannableStringBuilder b = new SpannableStringBuilder(text);
            boolean haveAppendix = false;
            if (0 == 0) {
                haveAppendix = true;
                b.append((CharSequence) "\n\n");
            }
            int quoteStart = b.length();
            b.append((CharSequence) getString(R.string.installed_version)).append((CharSequence) ": ").append((CharSequence) myPackage.versionName).append('\n');
            decorateLine(b, quoteStart, b.length());
            CharSequence playerVersionName = toVersionName(codecInfoMeta.metaData.get(PLAYER_REQUIRED_VERSION_NAME));
            if (playerVersionName != null) {
                if (!haveAppendix) {
                    b.append((CharSequence) "\n\n");
                }
                int quoteStart2 = b.length();
                b.append((CharSequence) getString(R.string.required_version)).append((CharSequence) ": ").append(playerVersionName).append(' ').append((CharSequence) getString(R.string.or_later));
                decorateLine(b, quoteStart2, b.length());
            }
            ActivityMessenger.openUri(caller, getString(R.string.upgrade), b, null, (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
            if (closeCaller) {
                caller.finish();
            }
        }
    }

    public final void openBuyPage(Activity caller, String packageName, String text, boolean closeCaller) {
        if (caller != null) {
            ArrayList<String> uris = new ArrayList<>(2);
            INavigator market = Market.getNavigator(this);
            uris.add(market.productDetailUri(packageName));
            uris.add(getString(R.string.buy_url));
            ActivityMessenger.openUri(caller, null, text, null, (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
            if (closeCaller) {
                caller.finish();
            }
        }
    }

    public final void openBuyPageReadMore(Activity caller, String packageName, String text1, @Nullable String text2, boolean closeCaller) {
        if (caller != null) {
            ArrayList<String> uris = new ArrayList<>(2);
            INavigator market = Market.getNavigator(this);
            uris.add(market.productDetailUri(packageName));
            uris.add(getString(R.string.buy_url));
            StringBuilder sb = new StringBuilder(text1);
            sb.append(" {read_more}");
            if (text2 != null) {
                sb.append("\n\n").append(text2);
            }
            Locale locale = Locale.getDefault();
            ActivityMessenger.openUri(caller, null, sb, getString(R.string.license_failure_readmore_url, new Object[]{locale.getLanguage(), locale.getCountry()}), (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
            if (closeCaller) {
                caller.finish();
            }
        }
    }

    public final void openCodecPackage(Activity caller, ApplicationInfo appInfoMeta, L.Codec codec, PackageInfo packageInfo, String text, boolean closeActivity) {
        if (caller != null) {
            INavigator market = Market.getNavigator(this);
            ArrayList<String> uris = new ArrayList<>(2);
            uris.add(market.productDetailUri(codec.packageName));
            uris.add(StringUtils.getString_s(R.string.direct_download_url, codec.packageName, codec.getArchId()));
            SpannableStringBuilder b = new SpannableStringBuilder(text);
            b.append((CharSequence) "\n\n");
            int quoteStart = b.length();
            b.append((CharSequence) getString(R.string.type)).append((CharSequence) ": ").append((CharSequence) codec.archName).append('\n');
            decorateLine(b, quoteStart, b.length());
            if (packageInfo != null) {
                int quoteStart2 = b.length();
                b.append((CharSequence) getString(R.string.installed_version)).append((CharSequence) ": ").append((CharSequence) packageInfo.versionName).append('\n');
                decorateLine(b, quoteStart2, b.length());
            }
            int quoteStart3 = b.length();
            b.append((CharSequence) getString(R.string.required_version)).append((CharSequence) ": ").append((CharSequence) toVersionName(appInfoMeta.metaData.get(FFMPEG_REQUIRED_VERSION_NAME))).append(' ').append((CharSequence) getString(R.string.or_later));
            decorateLine(b, quoteStart3, b.length());
            ActivityMessenger.openUri(caller, getString(R.string.install_codec), b, null, (String[]) uris.toArray(new String[uris.size()]), getString(R.string.cannot_open_downloader));
            if (closeActivity) {
                caller.finish();
            }
        }
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
            Log.i(TAG, "\t" + filePath + ": size=" + file.length() + " date=" + DateUtils.formatDateTime(this, file.lastModified(), DATE_FORMAT));
        }
    }

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

    private String toVersionName(Object data) {
        return data instanceof String ? (String) data : data.toString();
    }

    public static void quit(Intent restartIntent) {
        quitting = true;
        if (_restartIntent == null) {
            _restartIntent = restartIntent;
        }
        if (PlayService.instance != null) {
            PlayService.instance.stopSelf();
        } else {
            AppUtils.quit(context, _restartIntent);
        }
    }

    public static void quit() {
        quit(null);
    }

    @Override // com.mxtech.app.MXApplication, android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        L.thumbCache.clear();
        SubtitleDirectory.onLowMemory();
        TitleSuggestionCache.onLowMemory();
        super.onLowMemory();
    }

    public synchronized Tracker getDefaultTracker() {
        if (this._tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            String trackerId = getString(R.string.analytics_tracker_id);
            this._tracker = analytics.newTracker(trackerId);
        }
        this._tracker.setSampleRate(L.authorizer.analyticsSamplingRate * 100.0f);
        return this._tracker;
    }

    public static CharSequence getFileDeletionFailureMessage(int numTotal, int numFailure) {
        StringBuilder sb = new StringBuilder();
        if (numFailure == 1) {
            sb.append(context.getString(R.string.file_deletion_failure_single));
        } else if (numFailure == numTotal) {
            sb.append(context.getString(R.string.file_deletion_failure_all));
        } else {
            sb.append(context.getString(R.string.file_deletion_failure_partial));
        }
        sb.append(' ').append(context.getString(R.string.check_read_only_mounting));
        return sb;
    }
}
