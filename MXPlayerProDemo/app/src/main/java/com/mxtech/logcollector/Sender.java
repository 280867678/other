package com.mxtech.logcollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
//import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewConfiguration;
import android.widget.Toast;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.IOUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.os.Cpu;
import com.mxtech.os.SysInfo;
import com.mxtech.videoplayer.pro.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* loaded from: classes2.dex */
public class Sender {
    public static final int ITEM_APP_SETTINGS = 4;
    public static final int ITEM_SYS_LOG = 1;
    public static final int ITEM_SYS_SETTINGS = 2;
    public static final String LOG_COLLECTOR_PACKAGE_NAME = "com.mxtech.logcollector";
    private static final int MAX_ATTACH_FILE_SIZE = 20971520;
    private final Activity _activity;
    private final File _buildPropFile;
    private final Client _client;
    private final File _exportFile;
    private final File _logFile;
    private final File _logFileUncompressed;

    /* loaded from: classes.dex */
    public interface Client {
        boolean exportSettingsTo(File file);

        String getEmailRecipient();

        String getEmailSubject();

        void onCannotRunLogCollector(int i);
    }

    public Sender(Activity activity, Client client) {
        this._activity = activity;
        this._client = client;
        File dir = AppUtils.getExternalDir(activity);
        dir.mkdirs();
        this._logFile = new File(dir, "log.txt.gz");
        this._logFileUncompressed = new File(dir, "log.txt");
        this._buildPropFile = new File(dir, "build.prop");
        this._exportFile = new File(dir, "app_settings.xml");
        this._logFile.delete();
        this._buildPropFile.delete();
        this._exportFile.delete();
    }

    private String getKernelVersion() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            String readLine = reader.readLine();
            reader.close();
            return readLine;
        } catch (IOException e) {
            Log.e("MX.LogCollector", "", e);
            return "";
        }
    }

    private Map<String, String> readMemInfo() {
        Map<String, String> map = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"), 1024);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                int separator = line.indexOf(58);
                if (separator > 0) {
                    map.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e("MX.LogCollector", "", e);
        }
        return map;
    }

    private Properties readBuildProps() {
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream("/system/build.prop");
            props.load(in);
            in.close();
        } catch (Exception e) {
            Log.e("MX.LogCollector", "", e);
        }
        return props;
    }

    @SuppressLint({"NewApi"})
    private String getAdditionalInfo(boolean addUserExplain) {
        String arch;
        String features;
        PackageInfo pkg;
        String[] strArr;
        try {
            Map<String, String> info = SysInfo.readAll();
            arch = info.get("CPU architecture");
            if (arch == null) {
                String arch2 = info.get("model name");
                arch = arch2;
                if (arch == null) {
                    String arch3 = info.get("cpu model");
                    arch = arch3;
                }
            }
            features = info.get("Features");
            if (features == null) {
                String features2 = info.get("flags");
                features = features2;
            }
        } catch (IOException e) {
            Log.e("MX.LogCollector", "", e);
            arch = null;
            features = null;
        }
        Resources res = this._activity.getResources();
        Map<String, String> mem = readMemInfo();
        Properties buildProps = readBuildProps();
        StringBuilder b = new StringBuilder();
        PackageManager pm = this._activity.getPackageManager();
        try {
            pkg = pm.getPackageInfo(this._activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            pkg = null;
            Log.e(MXApplication.TAG, "", e2);
        }
        b.append("=========================\n").append(res.getString(R.string.application)).append(": ").append(this._activity.getString(pkg.applicationInfo.labelRes)).append(" (").append(pkg.versionName).append(")\n").append(res.getString(R.string.manufacturer)).append(": ").append(Build.MANUFACTURER).append('\n').append(res.getString(R.string.model)).append(": ").append(Build.MODEL).append('\n').append(res.getString(R.string.brand)).append(": ").append(Build.BRAND).append('\n').append(res.getString(R.string.version)).append(": ").append(Build.VERSION.RELEASE).append(" (").append(Build.VERSION.CODENAME).append(")\n").append(res.getString(R.string.build)).append(": ").append(Build.FINGERPRINT).append('\n').append(res.getString(R.string.kernel)).append(": ").append(getKernelVersion()).append('\n').append("CPU: ").append(Cpu.coreCount).append(" core(s) ").append(StringUtils.formatFrequency(SysInfo.getCPUClockFrequency(), 2)).append(" (family:").append(Cpu.family).append(" features:").append(Cpu.features).append(")\n").append(res.getString(R.string.cpu_arch)).append(": ").append(arch).append(" (os.arch: ").append(System.getProperty("os.arch")).append(")\n").append(res.getString(R.string.cpu_features)).append(": ").append(features).append('\n').append(res.getString(R.string.board_platform)).append(": ").append(buildProps.get("ro.board.platform")).append('\n').append(res.getString(R.string.abi)).append(": ");
        if (Build.VERSION.SDK_INT >= 21) {
            for (String abi : Build.SUPPORTED_ABIS) {
                b.append(abi).append(' ');
            }
        } else {
            b.append(Build.CPU_ABI).append(' ');
            if (Build.VERSION.SDK_INT >= 8) {
                b.append(Build.CPU_ABI2).append(' ');
            }
        }
        b.replace(b.length() - 1, b.length(), "\n");
        Configuration config = res.getConfiguration();
        DisplayMetrics metrics = new DisplayMetrics();
        Display disp = this._activity.getWindowManager().getDefaultDisplay();
        disp.getMetrics(metrics);
        b.append(res.getString(R.string.resolution)).append(": ").append(disp.getWidth()).append(" x ").append(disp.getHeight()).append('\n');
        if (Build.VERSION.SDK_INT >= 13) {
            b.append(res.getString(R.string.logical_resolution)).append(": ").append(config.screenWidthDp).append(" x ").append(config.screenHeightDp).append(" (smallest: ").append(config.smallestScreenWidthDp).append(")\n");
        }
        b.append(res.getString(R.string.is_tablet)).append(": ").append(DeviceUtils.isTabletFromDeviceConfig(res, Build.VERSION.SDK_INT)).append('\n');
        b.append(res.getString(R.string.screen_size)).append(": ").append(getScreenSizeName(config)).append('\n');
        b.append(res.getString(R.string.density)).append(": ").append(metrics.density).append(" (").append(metrics.densityDpi).append(")\n").append(res.getString(R.string.font_scale)).append(": ").append(config.fontScale).append('\n');
        if (Build.VERSION.SDK_INT >= 14) {
            b.append(res.getString(R.string.has_hard_keys)).append(": ").append(ViewConfiguration.get(this._activity).hasPermanentMenuKey()).append('\n');
        }
        b.append(res.getString(R.string.touch_screen)).append(": ").append(config.touchscreen != 1).append('\n');
        if (Build.VERSION.SDK_INT >= 13) {
            b.append("TV: ").append((config.uiMode & 15) == 4).append(" (uiMode:").append(config.uiMode).append(")\n");
        }
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) this._activity.getSystemService("activity");
        activityManager.getMemoryInfo(memInfo);
        b.append(res.getString(R.string.locale)).append(": ").append(Locale.getDefault()).append('\n').append(res.getString(R.string.mem_total)).append(": ").append(mem.get("MemTotal"));
        if (Build.VERSION.SDK_INT >= 16) {
            b.append(" (").append(memInfo.totalMem / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID).append("kB)\n");
        } else {
            b.append('\n');
        }
        b.append(res.getString(R.string.mem_free)).append(": ").append(mem.get("MemFree")).append('\n');
        b.append("=========================\n\n");
        if (addUserExplain) {
            b.append(res.getString(R.string.desc_error)).append("\n\n\n").append(res.getString(R.string.reproducing_step)).append("\n").append("1.\n").append("2.\n").append("3.\n").append("4.\n\n\n");
        }
        return b.toString();
    }

    private String getScreenSizeName(Configuration config) {
        switch (config.screenLayout & 15) {
            case 1:
                return "Small";
            case 2:
                return "Normal";
            case 3:
                return "Large";
            case 4:
                return "X Large";
            default:
                return "Unknown";
        }
    }

    private boolean openEMailClient(List<File> files) {
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.setType("application/octet-content");
        String recipient = this._client.getEmailRecipient();
        if (recipient != null) {
            intent.putExtra("android.intent.extra.EMAIL", new String[]{this._client.getEmailRecipient()});
        }
        intent.putExtra("android.intent.extra.SUBJECT", this._client.getEmailSubject());
        intent.putExtra("android.intent.extra.TEXT", getAdditionalInfo(true));
        if (files.size() > 0) {
            ArrayList<Uri> streams = new ArrayList<>(1);
            for (File file : files) {
                streams.add(Uri.fromFile(file));
            }
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", streams);
        }
        Intent chooser = Intent.createChooser(intent, this._activity.getString(R.string.sending_tools));
        chooser.addFlags(268435456);
        try {
            this._activity.startActivity(chooser);
            return true;
        } catch (Exception e) {
            Log.e(MXApplication.TAG, "", e);
            return false;
        }
    }

    private boolean saveToFile(List<File> files) {
        String state = Environment.getExternalStorageState();
        if (!"mounted".equals(state)) {
            DialogUtils.alert(this._activity, (CharSequence) StringUtils.getString_s(R.string.save_to_fail_failure, this._activity.getString(DeviceUtils.getStorageErrorMessageId(state))));
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US);
        File zipFile = new File(Environment.getExternalStorageDirectory(), "report_" + formatter.format(new Date()) + ".zip");
        try {
            Log.i("MX.LogCollector", "Saving report file to '" + zipFile + '\'');
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            byte[] buffer = new byte[4096];
            out.putNextEntry(new ZipEntry("sysinfo.txt"));
            out.write(getAdditionalInfo(false).getBytes());
            out.closeEntry();
            for (File file : files) {
                FileInputStream in = new FileInputStream(file);
                try {
                    out.putNextEntry(new ZipEntry(file.getName()));
                    IOUtils.transferStream(in, out, buffer);
                    out.closeEntry();
                    in.close();
                    file.delete();
                } catch (Throwable th) {
                    in.close();
                    throw th;
                }
            }
            out.close();
            Toast.makeText(this._activity, StringUtils.getString_s(R.string.save_to_fail_success, zipFile), 1).show();
        } catch (IOException e) {
            Log.e("MX.LogCollector", "", e);
            DialogUtils.alert(this._activity, (CharSequence) StringUtils.getString_s(R.string.save_to_fail_failure, this._activity.getString(R.string.error_io_error)));
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0044  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x002a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean sendLog(int items, boolean saveToFile) {
        OutputStream out;
        OutputStream zipped;
        List<File> attachments = new ArrayList<>();
        if ((items & 1) != 0) {
            int collectResult = Collector.collect(this._logFileUncompressed.getPath(), false, "threadtime", null);
            if (collectResult == 0) {
                if (saveToFile) {
                    attachments.add(this._logFileUncompressed);
                } else {
                    FileInputStream in = null;
                    OutputStream out2 = null;
                    OutputStream zipped2 = null;
                    try {
                        FileInputStream in2 = new FileInputStream(this._logFileUncompressed);
                        try {
                            out = new FileOutputStream(this._logFile);
                            try {
                                zipped = new GZIPOutputStream(new BufferedOutputStream(out));
                            } catch (Throwable th) {
                                th = th;
                                out2 = out;
                                in = in2;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            in = in2;
                        }
                        try {
                            long length = this._logFileUncompressed.length();
                            if (length > 20971520) {
                                in2.skip(length - 20971520);
                            }
                            byte[] buf = new byte[4096];
                            while (true) {
                                int read = in2.read(buf);
                                if (read < 0) {
                                    break;
                                }
                                zipped.write(buf, 0, read);
                            }
                            if (in2 != null) {
                                try {
                                    in2.close();
                                } catch (IOException e) {
                                    e = e;
                                    Log.e("MX.LogCollector", "", e);
                                    attachments.add(this._logFile);
                                    if ((items & 2) != 0) {
                                    }
                                    if ((items & 4) != 0) {
                                        attachments.add(this._exportFile);
                                    }
                                    if (saveToFile) {
                                    }
                                }
                            }
                            if (zipped != null) {
                                zipped.close();
                            } else if (out != null) {
                                out.close();
                            }
                            attachments.add(this._logFile);
                        } catch (Throwable th3) {
                            th = th3;
                            zipped2 = zipped;
                            out2 = out;
                            in = in2;
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e2) {
                                    e = e2;
                                    Log.e("MX.LogCollector", "", e);
                                    attachments.add(this._logFile);
                                    if ((items & 2) != 0) {
                                    }
                                    if ((items & 4) != 0) {
                                    }
                                    if (saveToFile) {
                                    }
                                }
                            }
                            if (zipped2 != null) {
                                zipped2.close();
                            } else if (out2 != null) {
                                out2.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                    }
                }
            } else {
                this._client.onCannotRunLogCollector(collectResult);
                return false;
            }
        }
        if ((items & 2) != 0) {
            try {
                FileUtils.copyFile("/system/build.prop", this._buildPropFile.getPath());
                attachments.add(this._buildPropFile);
            } catch (IOException e3) {
                Log.e("MX.LogCollector", "", e3);
            }
        }
        if ((items & 4) != 0 && this._client.exportSettingsTo(this._exportFile)) {
            attachments.add(this._exportFile);
        }
        if (saveToFile) {
            return saveToFile(attachments);
        }
        return openEMailClient(attachments);
    }
}
