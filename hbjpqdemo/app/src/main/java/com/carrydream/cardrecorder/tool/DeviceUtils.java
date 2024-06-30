package com.carrydream.cardrecorder.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import androidx.core.app.ActivityCompat;
//import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hjq.permissions.Permission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class DeviceUtils {
    public static void callPhone(Context context, String str) {
    }

    public static Bitmap captureWithoutStatusBar(Activity activity) {
        return null;
    }

    public static String getPhoneStatus(Context context) {
        return null;
    }

    public static void sendSms(Context context, String str, String str2) {
    }

    @SuppressLint("WrongConstant")
    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
    }

    @SuppressLint("WrongConstant")
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    public static int getScreenWidths(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeights(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getSystemModel() {
        return Build.MODEL;
    }

    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static String getUniqueSerialNumber() {
        String str = Build.MODEL;
        String str2 = Build.MANUFACTURER;
        Log.d("详细序列号", str2 + "-" + str + "-" + getSerialNumber());
        return str2 + "-" + str + "-" + getSerialNumber();
    }

    public static String getIMEI(Context context) {
        if (isPhone(context)) {
            return getDeviceIdIMEI(context);
        }
        return getAndroidId(context);
    }

    public static String getIMSI(Context context) {
        return getSubscriberId(context);
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static String getDeviceIdIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static String getDeviceSoftwareVersion(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceSoftwareVersion();
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static String getLine1Number(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getLine1Number();
    }

    @SuppressLint("WrongConstant")
    public static String getNetworkCountryIso(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getNetworkCountryIso();
    }

    @SuppressLint("WrongConstant")
    public static String getNetworkOperator(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getNetworkOperator();
    }

    @SuppressLint("WrongConstant")
    public static String getNetworkOperatorName(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName();
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getNetworkType();
    }

    @SuppressLint("WrongConstant")
    public static int getPhoneType(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getPhoneType();
    }

    @SuppressLint("WrongConstant")
    public static String getSimCountryIso(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimCountryIso();
    }

    @SuppressLint("WrongConstant")
    public static String getSimOperator(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimOperator();
    }

    @SuppressLint("WrongConstant")
    public static String getSimOperatorName(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimOperatorName();
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static String getSimSerialNumber(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimSerialNumber();
    }

    @SuppressLint("WrongConstant")
    public static int getSimState(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimState();
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static String getSubscriberId(Context context) {
        return ActivityCompat.checkSelfPermission(context, Permission.READ_PHONE_STATE) != 0 ? "" : ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
    }

    @SuppressLint("MissingPermission")
    public static String getVoiceMailNumber(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (ActivityCompat.checkSelfPermission(context, Permission.READ_PHONE_STATE) != 0) {
            return null;
        }
        return telephonyManager.getVoiceMailNumber();
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getBuildBrandModel() {
        return Build.MODEL;
    }

    public static String getBuildBrand() {
        return Build.BRAND;
    }

    public static String getBuildMANUFACTURER() {
        return Build.MANUFACTURER;
    }

    public static String getSerialNumber() {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", String.class).invoke(cls, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppVersionName(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        return packageInfo.versionName;
    }

    public static int getAppVersionNo(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        return packageInfo.versionCode;
    }

    @SuppressLint("WrongConstant")
    public static boolean checkPermission(Context context, String str) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                if (((Integer) Class.forName("android.content.Context").getMethod("checkSelfPermission", String.class).invoke(context, str)).intValue() == 0) {
                    return true;
                }
            } catch (Exception unused) {
            }
        } else if (context.getPackageManager().checkPermission(str, context.getPackageName()) == 0) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0084 A[Catch: Exception -> 0x0098, TryCatch #5 {Exception -> 0x0098, blocks: (B:3:0x0001, B:5:0x0016, B:8:0x001c, B:10:0x0024, B:13:0x0036, B:17:0x003e, B:20:0x0043, B:47:0x0072, B:50:0x007e, B:52:0x0084, B:53:0x008e, B:16:0x003b, B:26:0x004b, B:29:0x0050, B:31:0x0055, B:35:0x005d, B:34:0x005a, B:37:0x005f, B:40:0x0064, B:42:0x0069, B:45:0x006e), top: B:67:0x0001, inners: #0, #1, #2, #3, #6, #8, #10 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String getDeviceInfo(Context context) {
        FileReader fileReader;
        BufferedReader bufferedReader;
        String str = null;
        try {
            JSONObject jSONObject = new JSONObject();
            @SuppressLint({"MissingPermission", "WrongConstant"}) String deviceId = checkPermission(context, Permission.READ_PHONE_STATE) ? ((TelephonyManager) context.getSystemService("phone")).getDeviceId() : null;
            try {
                fileReader = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException unused) {
                fileReader = new FileReader("/sys/class/net/eth0/address");
            }
            try {
                bufferedReader = new BufferedReader(fileReader, 1024);
            } catch (Throwable th) {
                th = th;
                bufferedReader = null;
            }
            try {
                str = bufferedReader.readLine();
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bufferedReader.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (IOException unused3) {
                try {
                    fileReader.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                str = null;
                jSONObject.put("mac", str);
                if (TextUtils.isEmpty(deviceId)) {
                }
                if (TextUtils.isEmpty(deviceId)) {
                }
                jSONObject.put("device_id", deviceId);
                return jSONObject.toString();
            } catch (Throwable th2) {
//                th = th2;
                try {
                    fileReader.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
//                throw th;
            }
            jSONObject.put("mac", str);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = str;
            }
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), "android_id");
            }
            jSONObject.put("device_id", deviceId);
            return jSONObject.toString();
        } catch (Exception e7) {
            e7.printStackTrace();
            return null;
        }
    }

    public static void ThroughArray(HashMap hashMap) {
        for (Object entry : hashMap.entrySet()) {
            Object key = ((Map.Entry)entry).getKey();
            Object value = ((Map.Entry)entry).getValue();
            Log.d("MSG_AUTH_COMPLETE", key + "： " + value);
        }
    }

    public static String getMacAddress(Context context) {
        String macAddress;
        @SuppressLint("WrongConstant") WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        if (connectionInfo == null || (macAddress = connectionInfo.getMacAddress()) == null) {
            return null;
        }
        return macAddress.replace(":", "");
    }

    public static String getMacAddress() {
        String str;
        try {
            str = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address").getInputStream())).readLine().replace(":", "");
        } catch (IOException e) {
            e.printStackTrace();
            str = null;
        }
        return str == null ? "" : str;
    }

    @SuppressLint("WrongConstant")
    public static boolean isPhone(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getPhoneType() != 0;
    }

    public static void dial(Context context, String str) {
        context.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + str)));
    }

    public static List<HashMap<String, String>> getAllContactInfo(Context context) {
        SystemClock.sleep(3000L);
        ArrayList arrayList = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        Uri parse = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri.parse("content://com.android.contacts/data");
        contentResolver.query(parse, new String[]{"contact_id"}, null, null, null).close();
        return arrayList;
    }

    public static void getContantNum() {
        Log.i("tips", "U should copy the following code.");
    }

    public static void getAllSMS(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"}, null, null, null);
        query.getCount();
        XmlSerializer newSerializer = Xml.newSerializer();
        try {
            newSerializer.setOutput(new FileOutputStream(new File("/mnt/sdcard/backupsms.xml")), "utf-8");
            int i = 1;
            newSerializer.startDocument("utf-8", true);
            newSerializer.startTag(null, "smss");
            while (query.moveToNext()) {
                SystemClock.sleep(1000L);
                newSerializer.startTag(null, "sms");
                newSerializer.startTag(null, "address");
                String string = query.getString(0);
                newSerializer.text(string);
                newSerializer.endTag(null, "address");
                newSerializer.startTag(null, "date");
                String string2 = query.getString(i);
                newSerializer.text(string2);
                newSerializer.endTag(null, "date");
                newSerializer.startTag(null, "type");
                String string3 = query.getString(2);
                newSerializer.text(string3);
                newSerializer.endTag(null, "type");
                newSerializer.startTag(null, "body");
                String string4 = query.getString(3);
                newSerializer.text(string4);
                newSerializer.endTag(null, "body");
                newSerializer.endTag(null, "sms");
                PrintStream printStream = System.out;
                printStream.println("address:" + string + "   date:" + string2 + "  type:" + string3 + "  body:" + string4);
                i = 1;
            }
            newSerializer.endTag(null, "smss");
            newSerializer.endDocument();
            newSerializer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(0);
    }

    @SuppressLint("WrongConstant")
    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(1);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static int getScreenRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation != 1) {
            if (rotation != 2) {
                if (rotation != 3) {
                    return 0;
                }
                return 270;
            }
            return 180;
        }
        return 90;
    }

    public static Bitmap captureWithStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap createBitmap = Bitmap.createBitmap(decorView.getDrawingCache(), 0, 0, getScreenWidth(activity), getScreenHeight(activity));
        decorView.destroyDrawingCache();
        return createBitmap;
    }

    @SuppressLint("WrongConstant")
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @SuppressLint("WrongConstant")
    public static boolean isScreenLock(Context context) {
        return ((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    public static void noScreenshots(Activity activity) {
        activity.getWindow().addFlags(8192);
    }

    public static Boolean isDebug(Context context) {
        return Boolean.valueOf((context.getApplicationInfo() == null || (context.getApplicationInfo().flags & 2) == 0) ? false : true);
    }

    public static boolean isWXInstall(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            if (installedPackages.get(i).packageName.equals("com.tencent.mm")) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public static void copyText(Context context, String str) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.valueOf(str)));
    }

    public static boolean isConnected(Context context) {
        @SuppressLint({"MissingPermission", "WrongConstant"}) NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
