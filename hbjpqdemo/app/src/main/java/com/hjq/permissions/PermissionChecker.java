package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
final class PermissionChecker {
    PermissionChecker() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean checkActivityStatus(Activity activity, boolean z) {
        if (activity == null) {
            if (z) {
                throw new IllegalArgumentException("The instance of the Context must be an Activity object");
            }
            return false;
        } else if (activity.isFinishing()) {
            if (z) {
                throw new IllegalStateException("The Activity has been finishing, Please manually determine the status of the Activity");
            }
            return false;
        } else if (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed()) {
            return true;
        } else {
            if (z) {
                throw new IllegalStateException("The Activity has been destroyed, Please manually determine the status of the Activity");
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean checkPermissionArgument(List<String> list, boolean z) {
        if (list == null || list.isEmpty()) {
            if (z) {
                throw new IllegalArgumentException("The requested permission cannot be empty");
            }
            return false;
        }
        if (z) {
            ArrayList arrayList = new ArrayList();
            Field[] declaredFields = Permission.class.getDeclaredFields();
            if (declaredFields.length == 0) {
                return true;
            }
            for (Field field : declaredFields) {
                if (String.class.equals(field.getType())) {
                    try {
                        arrayList.add((String) field.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (String str : list) {
                if (!arrayList.contains(str)) {
                    throw new IllegalArgumentException("The " + str + " is not a dangerous permission or special permission");
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkStoragePermission(Context context, List<String> list, boolean z) {
        int findApkPathCookie;
        if ((list.contains(Permission.MANAGE_EXTERNAL_STORAGE) || list.contains(Permission.READ_EXTERNAL_STORAGE) || list.contains(Permission.WRITE_EXTERNAL_STORAGE)) && (findApkPathCookie = PermissionUtils.findApkPathCookie(context)) != 0) {
            try {
                XmlResourceParser openXmlResourceParser = context.getAssets().openXmlResourceParser(findApkPathCookie, "AndroidManifest.xml");
                while (true) {
                    if (openXmlResourceParser.getEventType() == 1) {
                        break;
                    } else if (openXmlResourceParser.getEventType() == 2 && "application".equals(openXmlResourceParser.getName())) {
                        int i = context.getApplicationInfo().targetSdkVersion;
                        boolean attributeBooleanValue = openXmlResourceParser.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "requestLegacyExternalStorage", false);
                        if (i >= 29 && !attributeBooleanValue && (list.contains(Permission.MANAGE_EXTERNAL_STORAGE) || !z)) {
                            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" attribute in the manifest file");
                        }
                        if (i >= 30 && !list.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !z) {
                            throw new IllegalArgumentException("Please adapt the scoped storage, or use the MANAGE_EXTERNAL_STORAGE permission");
                        }
                    } else {
                        openXmlResourceParser.next();
                    }
                }
                openXmlResourceParser.close();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkLocationPermission(List<String> list) {
        if (list.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (list.contains(Permission.ACCESS_COARSE_LOCATION) && !list.contains(Permission.ACCESS_FINE_LOCATION)) {
                throw new IllegalArgumentException("The application for background location permissions must include precise location permissions");
            }
            for (String str : list) {
                if (!Permission.ACCESS_FINE_LOCATION.equals(str) && !Permission.ACCESS_COARSE_LOCATION.equals(str) && !Permission.ACCESS_BACKGROUND_LOCATION.equals(str)) {
                    throw new IllegalArgumentException("Because it includes background location permissions, do not apply for permissions unrelated to location");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkTargetSdkVersion(Context context, List<String> list) {
        int i;
        if (list.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            i = 30;
        } else if (list.contains(Permission.ACCEPT_HANDOVER)) {
            i = 28;
        } else if (list.contains(Permission.ACCESS_BACKGROUND_LOCATION) || list.contains(Permission.ACTIVITY_RECOGNITION) || list.contains(Permission.ACCESS_MEDIA_LOCATION)) {
            i = 29;
        } else {
            i = (list.contains(Permission.REQUEST_INSTALL_PACKAGES) || list.contains(Permission.ANSWER_PHONE_CALLS) || list.contains(Permission.READ_PHONE_NUMBERS)) ? 26 : 23;
        }
        if (context.getApplicationInfo().targetSdkVersion >= i) {
            return;
        }
        throw new RuntimeException("The targetSdkVersion SDK must be " + i + " or more");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void optimizeDeprecatedPermission(List<String> list) {
        if (list.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            if (list.contains(Permission.READ_EXTERNAL_STORAGE) || list.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                throw new IllegalArgumentException("If you have applied for MANAGE_EXTERNAL_STORAGE permissions, do not apply for the READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions");
            }
            if (!PermissionUtils.isAndroid11()) {
                list.add(Permission.READ_EXTERNAL_STORAGE);
                list.add(Permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (!PermissionUtils.isAndroid8() && list.contains(Permission.READ_PHONE_NUMBERS) && !list.contains(Permission.READ_PHONE_STATE)) {
            list.add(Permission.READ_PHONE_STATE);
        }
        if (PermissionUtils.isAndroid10() || !list.contains(Permission.ACTIVITY_RECOGNITION) || list.contains(Permission.BODY_SENSORS)) {
            return;
        }
        list.add(Permission.BODY_SENSORS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkPermissionManifest(Context context, List<String> list) {
        List<String> manifestPermissions = PermissionUtils.getManifestPermissions(context);
        if (manifestPermissions == null || manifestPermissions.isEmpty()) {
            throw new ManifestRegisterException();
        }
        int i = Build.VERSION.SDK_INT >= 24 ? context.getApplicationInfo().minSdkVersion : 23;
        for (String str : list) {
            if (i < 30 && Permission.MANAGE_EXTERNAL_STORAGE.equals(str)) {
                if (!manifestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)) {
                    throw new ManifestRegisterException(Permission.READ_EXTERNAL_STORAGE);
                }
                if (!manifestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                    throw new ManifestRegisterException(Permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            if (i < 29 && Permission.ACTIVITY_RECOGNITION.equals(str) && !manifestPermissions.contains(Permission.BODY_SENSORS)) {
                throw new ManifestRegisterException(Permission.BODY_SENSORS);
            }
            if (i < 26 && Permission.READ_PHONE_NUMBERS.equals(str) && !manifestPermissions.contains(Permission.READ_PHONE_STATE)) {
                throw new ManifestRegisterException(Permission.READ_PHONE_STATE);
            }
            if (!Permission.NOTIFICATION_SERVICE.equals(str) && !manifestPermissions.contains(str)) {
                throw new ManifestRegisterException(str);
            }
        }
    }
}
