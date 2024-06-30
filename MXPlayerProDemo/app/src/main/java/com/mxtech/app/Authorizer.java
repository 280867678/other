package com.mxtech.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.mxtech.DeviceUtils;
import com.mxtech.IOUtils;
import com.mxtech.Library;
import com.mxtech.NumericUtils;
import com.mxtech.StringUtils;
import com.mxtech.net.NetUtils;
import com.mxtech.os.Cpu;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Tuner;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@TargetApi(9)
/* loaded from: classes.dex */
public final class Authorizer implements Runnable {
    public static final int AD_RESULT_GENERAL_ERROR = 1;
    public static final int AD_RESULT_NETWORK_ERROR = 2;
    public static final int AD_RESULT_SUCCESS = 0;
    private static final String AUTH_URL = "http://i.mxplayer.j2inter.com/auth2";
    public static final int CUSTOM_CODEC_ASK_ACTIVE = 2;
    public static final int CUSTOM_CODEC_ASK_NONE = 0;
    public static final int CUSTOM_CODEC_ASK_PASSIVE = 1;
    private static final int DEFAULT_INTERSTITIAL_INITIAL_LOAD_TERM = 180000;
    private static final int DEFAULT_INTERSTITIAL_RELOAD_TERM = 180000;
    public static final int FLAG_IGNORE_DO_NOT_INQUIRE = 1;
    public static final int FLAG_UP_TO_DATE = 2;
    private static final int HTTP_CONNECT_TIMEOUT = 5000;
    private static final int HTTP_SO_TIMEOUT = 8000;
    private static final String KEY_ANALYTICS_SAMPLING_RATE = "analytics_sampling_rate";
    private static final String KEY_BACKCOLOR = "backcolor";
    private static final String KEY_CUSTOM_CODEC_INTERACTIVE = "custom_codec_i.2";
    private static final String KEY_CUSTOM_CODEC_PAGE = "custom_codec_page";
    private static final String KEY_DONT_INQUIRE_UPDATE_FOR = "dont_inquire_update_for";
    private static final String KEY_INTERSTITIAL_INITIAL_LOAD_TERM = "interstitial_initial_load_term";
    private static final String KEY_INTERSTITIAL_RELOAD_TERM = "interstitial_reload_term";
    private static final String KEY_LAST_AUTH_TIME = "auth_time";
    private static final String KEY_LAST_AUTH_VERSION = "auth_version";
    private static final String KEY_LAST_COUNTRY = "last_country";
    private static final String KEY_LAST_LANGUAGE = "last_language";
    private static final String KEY_LATEST_VERSION = "latest_version";
    private static final String KEY_LATEST_VERSION_NAME = "latest_version_name";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MIN_VERSION = "min_version";
    private static final String KEY_NEXT_AUTH_TIME = "auth_time_next";
    private static final String KEY_REFRESH = "refresh";
    private static final String KEY_SCHEDULE = "schedule";
    private static final String KEY_TEXTCOLOR = "textcolor";
    private static final String KEY_UPGRADE_MESSAGE = "upgrade_message";
    private static final String KEY_UPGRADE_URL = "upgrade_url";
    private static final int LINE_AD_SCHEDULE = 5;
    private static final int LINE_ANALYTICS_SAMPLING_RATE = 13;
    private static final int LINE_BACKCOLOR = 3;
    private static final int LINE_CUSTOM_CODEC_INTERACTIVE = 12;
    private static final int LINE_LOCATION_ACCESS = 4;
    private static final int LINE_REFRESH_INTERVAL = 1;
    private static final int LINE_TEXTCOLOR = 2;
    private static final int LINE_UPGRADE_MESSAGE = 10;
    private static final int LINE_UPGRADE_URL = 11;
    private static final int LINE_VERSION = 0;
    public static final String PARAM_AD = "ad";
    public static final String PARAM_AD_CLICK = "ck";
    public static final String PARAM_AD_GENERAL_ERROR = "ge";
    public static final String PARAM_AD_NETWORK_ERROR = "ne";
    public static final String PARAM_AD_RECEPTION = "rc";
    public static final String PARAM_AD_REQUEST = "rq";
    public static final String PARAM_AD_VERIFY_FAILURE = "ax";
    public static final String PARAM_APP_VERIFY_FAILURE = "vx";
    public static final String PARAM_APP_VERIFY_SUCCESS = "vo";
    public static final String PARAM_BRAND = "br";
    public static final String PARAM_COUNTRY = "cn";
    public static final String PARAM_CPU_CORE = "Cc";
    public static final String PARAM_CPU_FAMILY = "Cm";
    public static final String PARAM_CPU_FEATURES = "Cf";
    public static final String PARAM_DEVICE = "dv";
    public static final String PARAM_HARDWARE = "hw";
    public static final String PARAM_HAS_KOREAN_APPS = "ka";
    public static final String PARAM_IS_ROAMING = "ir";
    public static final String PARAM_LANGUAGE = "la";
    public static final String PARAM_MANUFACTURER = "mf";
    public static final String PARAM_MODEL = "md";
    public static final String PARAM_NETWORK_COUNTRY = "nc";
    public static final String PARAM_OS = "os";
    public static final String PARAM_PRODUCT = "pr";
    public static final String PARAM_SCREEN_SIZE = "ss";
    public static final String PARAM_SERIAL = "sr";
    public static final String PARAM_SIM_COUNTRY = "sc";
    public static final String PARAM_TARGET_MARKET = "mk";
    private static final long PASSIVE_RETRY_INTERVAL = 600000;
    private static final String PREF_NAME = "a";
    private static final long RETRY_INTERVAL = 600000;
    private static final String TAG = "MX.Authorizer";
    private final Context _context;
    private int _dontInquireUpdateFor;
    private final SharedPreferences _prefs;
    private Ration[] _rations;
    public float analyticsSamplingRate;
    public Integer backColor;
    public int interstitial_initial_load_term;
    public int interstitial_reload_term;
    public int latestVersion;
    public String latestVersionName;
    public boolean locationAccess;
    public int minVersion;
    public int refreshInterval;
    public Integer textColor;
    public int version;
    private final ScheduledExecutorService _executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<ScheduledFuture<?>> _authSchedule = new AtomicReference<>();
    private boolean _firstCall = true;

    /* loaded from: classes2.dex */
    public static class Ration {
        public final String adKey;
        public final char platform;
        public final int weight;

        Ration(char platform, int weight, String adKey) {
            this.platform = platform;
            this.weight = weight;
            this.adKey = adKey;
        }

        static Ration[] parse(String string) throws NumberFormatException {
            int slashIndex;
            String[] blocks = string.split(" ");
            ArrayList<Ration> ratios = new ArrayList<>(blocks.length);
            for (String block : blocks) {
                if (block.length() >= 4 && block.charAt(1) == ':' && (slashIndex = block.indexOf(47)) >= 3) {
                    ratios.add(new Ration(block.charAt(0), Integer.parseInt(block.substring(2, slashIndex)), block.substring(slashIndex + 1)));
                }
            }
            return (Ration[]) ratios.toArray(new Ration[ratios.size()]);
        }
    }

    public Authorizer(Context context, int firstRunDelay) {
        String country;
        this._context = context.getApplicationContext();
        this._prefs = context.getSharedPreferences(PREF_NAME, 0);
        try {
            String packageName = this._context.getPackageName();
            PackageInfo info = this._context.getPackageManager().getPackageInfo(packageName, 0);
            this.version = AppUtils.getVersion(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
            this.version = 0;
        }
        this.minVersion = this._prefs.getInt(KEY_MIN_VERSION, 0);
        this.latestVersion = this._prefs.getInt(KEY_LATEST_VERSION, 0);
        this.latestVersionName = this._prefs.getString(KEY_LATEST_VERSION_NAME, null);
        this.refreshInterval = this._prefs.getInt(KEY_REFRESH, -1);
        this.interstitial_initial_load_term = this._prefs.getInt(KEY_INTERSTITIAL_INITIAL_LOAD_TERM, 180000);
        this.interstitial_reload_term = this._prefs.getInt(KEY_INTERSTITIAL_RELOAD_TERM, 180000);
        this.analyticsSamplingRate = this._prefs.getFloat(KEY_ANALYTICS_SAMPLING_RATE, 1.0f);
        if (this._prefs.contains(KEY_TEXTCOLOR)) {
            this.textColor = Integer.valueOf(this._prefs.getInt(KEY_TEXTCOLOR, -1));
        }
        if (this._prefs.contains(KEY_BACKCOLOR)) {
            this.backColor = Integer.valueOf(this._prefs.getInt(KEY_BACKCOLOR, ViewCompat.MEASURED_STATE_MASK));
        }
        this.locationAccess = this._prefs.getBoolean(KEY_LOCATION, false);
        this._dontInquireUpdateFor = this._prefs.getInt(KEY_DONT_INQUIRE_UPDATE_FOR, 0);
        if (Library.advertise) {
            try {
                String schedule = this._prefs.getString(KEY_SCHEDULE, null);
                if (schedule != null) {
                    this._rations = Ration.parse(schedule);
                }
            } catch (NumberFormatException e2) {
            }
            Locale locale = Locale.getDefault();
            String country2 = DeviceUtils.getTelephonyCountry(this._context);
            if (country2 == null || country2.length() == 0) {
                country = locale.getCountry();
            } else {
                country = country2.toUpperCase(Locale.US);
            }
            String lastCountry = this._prefs.getString(KEY_LAST_COUNTRY, null);
            String lastLanguage = this._prefs.getString(KEY_LAST_LANGUAGE, null);
            if (!country.equals(lastCountry) || !locale.getLanguage().equals(lastLanguage)) {
                this._authSchedule.set(this._executor.schedule(this, firstRunDelay, TimeUnit.MILLISECONDS));
                return;
            }
        }
        if (this.version != this._prefs.getInt(KEY_LAST_AUTH_VERSION, 0)) {
            this._authSchedule.set(this._executor.schedule(this, firstRunDelay, TimeUnit.MILLISECONDS));
            return;
        }
        long nextAuthTime = this._prefs.getLong(KEY_NEXT_AUTH_TIME, 0L);
        long initialDelay = nextAuthTime - System.currentTimeMillis();
        this._authSchedule.set(this._executor.schedule(this, initialDelay < ((long) firstRunDelay) ? firstRunDelay : initialDelay, TimeUnit.MILLISECONDS));
    }

    public void close() {
        this._executor.shutdownNow();
    }

    @Nullable
    public String getPerNetworkKey(char adsNetwork) {
        Ration[] rationArr;
        if (this._rations != null) {
            for (Ration ration : this._rations) {
                if (ration.platform == adsNetwork) {
                    return ration.adKey;
                }
            }
        }
        return null;
    }

    public String getCustomCodecPage() {
        return this._prefs.getString(KEY_CUSTOM_CODEC_PAGE, null);
    }

    public String getUpgradeUrl() {
        return this._prefs.getString(KEY_UPGRADE_URL, null);
    }

    public int getCustomCodecInteractionMode() {
        return this._prefs.getInt(KEY_CUSTOM_CODEC_INTERACTIVE, 0);
    }

    public void postAsync(Runnable task) {
        this._executor.execute(task);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class InquireUpdateListener implements CompoundButton.OnCheckedChangeListener {
        Dialog dialog;

        private InquireUpdateListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if (isChecked) {
                Authorizer.this._dontInquireUpdateFor = Authorizer.this.latestVersion;
                SharedPreferences.Editor editor = Authorizer.this._prefs.edit();
                editor.putInt(Authorizer.KEY_DONT_INQUIRE_UPDATE_FOR, Authorizer.this.latestVersion);
                AppUtils.apply(editor);
                this.dialog.dismiss();
            }
        }
    }

    @SuppressLint({"InflateParams"})
    public boolean inquireUpdate(Activity context, PackageInfo currentPackage, int flags, DialogInterface.OnClickListener updateListener) {
        if ((flags & 1) == 0 && this.latestVersion == this._dontInquireUpdateFor) {
            return false;
        }
        if ((flags & 2) != 0) {
            if (this.version >= this.latestVersion) {
                return false;
            }
        } else if (this.version >= this.minVersion) {
            return false;
        }
        InquireUpdateListener listener = new InquireUpdateListener();
        AlertDialog dlg = new AlertDialog.Builder(context).setTitle(R.string.inquire_update_title).setPositiveButton(17039379, updateListener).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).create();
        ViewGroup content = (ViewGroup) dlg.getLayoutInflater().inflate(R.layout.inquire_update_content, (ViewGroup) null);
        TextView text = (TextView) content.findViewById(R.id.text);
        CheckBox denyBox = (CheckBox) content.findViewById(R.id.deny);
        String message = null;
        String messageFormat = this._prefs.getString(KEY_UPGRADE_MESSAGE, null);
        if (messageFormat != null) {
            try {
                message = String.format(Locale.getDefault(), messageFormat, currentPackage.versionName, this.latestVersionName);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        if (message == null) {
            message = StringUtils.getString_s(R.string.inquire_update_text, currentPackage.versionName, this.latestVersionName);
        }
        text.setText(message);
        if ((flags & 1) != 0) {
            denyBox.setVisibility(8);
        } else {
            denyBox.setOnCheckedChangeListener(listener);
        }
        dlg.setView(content);
        dlg.setCanceledOnTouchOutside(true);
        DialogRegistry dialogRegistry = DialogRegistry.registryOf(context);
        if (dialogRegistry != null) {
            dlg.setOnDismissListener(dialogRegistry);
            dialogRegistry.register(dlg);
        }
        listener.dialog = dlg;
        dlg.show();
        return true;
    }

    public Ration nextRation(Set<Character> exclusion) {
        Ration[] rationArr;
        Ration[] rationArr2;
        Ration[] rationArr3;
        if (this._rations == null) {
            return null;
        }
        int sum = 0;
        for (Ration ration : this._rations) {
            if (!exclusion.contains(Character.valueOf(ration.platform))) {
                sum += ration.weight;
            }
        }
        if (sum > 0) {
            int dart = NumericUtils.ThreadLocalRandom.get().nextInt(sum);
            for (Ration ration2 : this._rations) {
                if (!exclusion.contains(Character.valueOf(ration2.platform))) {
                    if (dart >= ration2.weight) {
                        dart -= ration2.weight;
                    } else {
                        return ration2;
                    }
                }
            }
        }
        for (Ration ration3 : this._rations) {
            if (!exclusion.contains(Character.valueOf(ration3.platform))) {
                return ration3;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean authenticate() throws Exception {
        URL url = new URL(AUTH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", this._context.getPackageName() + '/' + this.version);
            Locale locale = Locale.getDefault();
            String country = DeviceUtils.getTelephonyCountry(this._context);
            String country2 = (country == null || country.length() == 0) ? locale.getCountry() : country.toUpperCase(Locale.US);
            String language = locale.getLanguage();
            HashMap<String, String> params = new HashMap<>();
            params.put(PARAM_COUNTRY, country2);
            params.put(PARAM_LANGUAGE, language);
            try {
                TelephonyManager tm = (TelephonyManager) MXApplication.context.getSystemService("phone");
                String simCountry = tm.getSimCountryIso();
                String networkCountry = tm.getNetworkCountryIso();
                if (simCountry != null && simCountry.length() > 0) {
                    params.put(PARAM_SIM_COUNTRY, simCountry);
                }
                if (networkCountry != null && networkCountry.length() > 0) {
                    params.put(PARAM_NETWORK_COUNTRY, networkCountry);
                }
                if (tm.isNetworkRoaming()) {
                    params.put(PARAM_IS_ROAMING, Tuner.TAG_SCREEN);
                }
            } catch (Exception e) {
            }
            params.put(PARAM_OS, Integer.toString(Build.VERSION.SDK_INT));
            params.put(PARAM_CPU_FAMILY, Integer.toString(Cpu.family));
            params.put(PARAM_CPU_FEATURES, Long.toString(Cpu.features));
            params.put(PARAM_CPU_CORE, Integer.toString(Cpu.coreCount));
            Configuration cfg = this._context.getResources().getConfiguration();
            params.put(PARAM_TARGET_MARKET, this._context.getString(R.string.target_market));
            params.put(PARAM_SCREEN_SIZE, Integer.toString(cfg.screenLayout & 15));
            params.put(PARAM_BRAND, Build.BRAND);
            params.put(PARAM_DEVICE, Build.DEVICE);
            params.put(PARAM_MANUFACTURER, Build.MANUFACTURER);
            params.put(PARAM_MODEL, Build.MODEL);
            params.put(PARAM_PRODUCT, Build.PRODUCT);
            params.put(PARAM_HARDWARE, Build.HARDWARE);
            params.put(PARAM_SERIAL, Build.SERIAL);
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            os.write(NetUtils.makePostDataString(params));
            os.close();
            int status = conn.getResponseCode();
            if (status != 200) {
                return false;
            }
            ByteArrayOutputStream contentTemp = new ByteArrayOutputStream(512);
            InputStream is = conn.getInputStream();
            IOUtils.transferStream(is, contentTemp);
            String content = contentTemp.toString();
            is.close();
            contentTemp.close();
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i].trim();
            }
            if (lines.length < 1) {
                return false;
            }
            String[] versionBlocks = lines[0].split(" ");
            if (versionBlocks.length != 3) {
                return false;
            }
            this.minVersion = Integer.parseInt(versionBlocks[0]);
            this.latestVersion = Integer.parseInt(versionBlocks[1]);
            this.latestVersionName = versionBlocks[2].replace('_', ' ');
            SharedPreferences.Editor edit = this._prefs.edit();
            edit.putInt(KEY_MIN_VERSION, this.minVersion);
            edit.putInt(KEY_LATEST_VERSION, this.latestVersion);
            edit.putString(KEY_LATEST_VERSION_NAME, this.latestVersionName);
            if (Library.advertise) {
                if (lines.length < 6) {
                    edit.commit();
                    return false;
                }
                if (lines[1].length() == 0) {
                    this.refreshInterval = -1;
                    edit.remove(KEY_REFRESH);
                } else {
                    this.refreshInterval = Integer.parseInt(lines[1]);
                    edit.putInt(KEY_REFRESH, this.refreshInterval);
                }
                if (lines[2].length() == 0) {
                    this.textColor = null;
                    edit.remove(KEY_TEXTCOLOR);
                } else {
                    this.textColor = Integer.valueOf(Color.parseColor(lines[2]));
                    edit.putInt(KEY_TEXTCOLOR, this.textColor.intValue());
                }
                if (lines[3].length() == 0) {
                    this.backColor = null;
                    edit.remove(KEY_BACKCOLOR);
                } else {
                    this.backColor = Integer.valueOf(Color.parseColor(lines[3]));
                    edit.putInt(KEY_BACKCOLOR, this.backColor.intValue());
                }
                this.locationAccess = Boolean.parseBoolean(lines[4]);
                edit.putBoolean(KEY_LOCATION, this.locationAccess);
                final Ration[] rations = Ration.parse(lines[5]);
                edit.putString(KEY_SCHEDULE, lines[5]);
                MXApplication.handler.post(new Runnable() { // from class: com.mxtech.app.Authorizer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Authorizer.this._rations = rations;
                    }
                });
            }
            long now = System.currentTimeMillis();
            if (lines.length > 6) {
                edit.putLong(KEY_NEXT_AUTH_TIME, (Integer.parseInt(lines[6]) * 1000) + now);
            }
            if (lines.length > 8 && Library.advertise) {
                this.interstitial_initial_load_term = Integer.parseInt(lines[7]) * 1000;
                this.interstitial_reload_term = Integer.parseInt(lines[8]) * 1000;
                edit.putInt(KEY_INTERSTITIAL_INITIAL_LOAD_TERM, this.interstitial_initial_load_term);
                edit.putInt(KEY_INTERSTITIAL_RELOAD_TERM, this.interstitial_reload_term);
            }
            if (lines.length > 9) {
                edit.putString(KEY_CUSTOM_CODEC_PAGE, lines[9]);
            }
            if (lines.length <= 10 || lines[10].length() <= 0) {
                edit.remove(KEY_UPGRADE_MESSAGE);
            } else {
                edit.putString(KEY_UPGRADE_MESSAGE, lines[10].replace('|', '\n'));
            }
            if (lines.length <= 11 || lines[11].length() <= 0) {
                edit.remove(KEY_UPGRADE_URL);
            } else {
                edit.putString(KEY_UPGRADE_URL, lines[11]);
            }
            int customCodecInteraction = 0;
            if (lines.length > 12 && lines[12].length() > 0) {
                char ch = lines[12].charAt(0);
                if (ch == 't') {
                    customCodecInteraction = 2;
                } else if (ch == 'p') {
                    customCodecInteraction = 1;
                }
            }
            if (customCodecInteraction == 0) {
                edit.remove(KEY_CUSTOM_CODEC_INTERACTIVE);
            } else {
                edit.putInt(KEY_CUSTOM_CODEC_INTERACTIVE, customCodecInteraction);
            }
            if (lines.length > 13 && lines[13].length() > 0) {
                try {
                    this.analyticsSamplingRate = Float.parseFloat(lines[13]);
                    edit.putFloat(KEY_ANALYTICS_SAMPLING_RATE, this.analyticsSamplingRate);
                } catch (Exception e2) {
                }
            }
            edit.putString(KEY_LAST_COUNTRY, country2);
            edit.putString(KEY_LAST_LANGUAGE, language);
            edit.putLong(KEY_LAST_AUTH_TIME, now);
            edit.putInt(KEY_LAST_AUTH_VERSION, this.version);
            edit.commit();
            conn.disconnect();
            return true;
        } finally {
            conn.disconnect();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            if (!this._firstCall && !ActivityRegistry.hasForegroundActivity() && (!Library.advertise || this._rations != null)) {
                this._authSchedule.set(this._executor.schedule(this, 600000L, TimeUnit.MILLISECONDS));
                return;
            }
            if (this._firstCall || Library.advertise || DeviceUtils.isWifiActive(this._context)) {
                this._firstCall = false;
                try {
                    if (authenticate()) {
                        long authAfter = this._prefs.getLong(KEY_NEXT_AUTH_TIME, 0L) - System.currentTimeMillis();
                        this._authSchedule.set(this._executor.schedule(this, authAfter, TimeUnit.MILLISECONDS));
                        return;
                    }
                } catch (Exception e) {
                }
            }
            AtomicReference<ScheduledFuture<?>> atomicReference = this._authSchedule;
            ScheduledExecutorService scheduledExecutorService = this._executor;
            if (Library.advertise) {
            }
            atomicReference.set(scheduledExecutorService.schedule(this, 600000L, TimeUnit.MILLISECONDS));
        } catch (RejectedExecutionException e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AuthNow implements Runnable {
        private Handler _handler;
        private final int _what;

        AuthNow(Handler handler, int what) {
            this._handler = handler;
            this._what = what;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                try {
                    SystemClock.uptimeMillis();
                    if (Authorizer.this.authenticate()) {
                        long authAfter = Authorizer.this._prefs.getLong(Authorizer.KEY_NEXT_AUTH_TIME, 0L) - System.currentTimeMillis();
                        Authorizer.this._authSchedule.set(Authorizer.this._executor.schedule(this, authAfter, TimeUnit.MILLISECONDS));
                        Message msg = this._handler.obtainMessage(this._what);
                        msg.arg1 = 0;
                        this._handler.sendMessage(msg);
                        return;
                    }
                } finally {
                    this._handler = null;
                }
            } catch (Exception e) {
            }
            AtomicReference atomicReference = Authorizer.this._authSchedule;
            ScheduledExecutorService scheduledExecutorService = Authorizer.this._executor;
            if (Library.advertise) {
            }
            atomicReference.set(scheduledExecutorService.schedule(this, 600000L, TimeUnit.MILLISECONDS));
            Message msg2 = this._handler.obtainMessage(this._what);
            msg2.arg1 = -1;
            this._handler.sendMessage(msg2);
        }
    }

    public void authNow(Handler handler, int what) {
        ScheduledFuture<?> future = this._authSchedule.get();
        if (future != null) {
            future.cancel(false);
        }
        this._executor.execute(new AuthNow(handler, what));
    }
}
