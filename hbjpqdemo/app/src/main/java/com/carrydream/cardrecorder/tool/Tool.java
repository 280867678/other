package com.carrydream.cardrecorder.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import com.carrydream.cardrecorder.Model.IRect;
import com.carrydream.cardrecorder.Model.SortPoker;
import com.carrydream.cardrecorder.Model.ZdyPoker;
import com.carrydream.cardrecorder.datapool.DataPool;
import com.hb.aiyouxiba.R;
import com.mask.photo.utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class Tool {
    public static List<String> sort(List<String> list) {
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, ExifInterface.GPS_MEASUREMENT_2D, "王"};
        ArrayList<Integer> arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case 50:
                    if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                        c = 0;
                        break;
                    }
                    break;
                case 51:
                    if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                        c = 1;
                        break;
                    }
                    break;
                case 52:
                    if (str.equals("4")) {
                        c = 2;
                        break;
                    }
                    break;
                case 53:
                    if (str.equals("5")) {
                        c = 3;
                        break;
                    }
                    break;
                case 54:
                    if (str.equals("6")) {
                        c = 4;
                        break;
                    }
                    break;
                case 55:
                    if (str.equals("7")) {
                        c = 5;
                        break;
                    }
                    break;
                case 56:
                    if (str.equals("8")) {
                        c = 6;
                        break;
                    }
                    break;
                case 57:
                    if (str.equals("9")) {
                        c = 7;
                        break;
                    }
                    break;
                case 65:
                    if (str.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                        c = '\b';
                        break;
                    }
                    break;
                case 74:
                    if (str.equals("J")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 75:
                    if (str.equals("K")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 81:
                    if (str.equals("Q")) {
                        c = 11;
                        break;
                    }
                    break;
                case 1567:
                    if (str.equals("10")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 29579:
                    if (str.equals("王")) {
                        c = '\r';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    arrayList.add(12);
                    break;
                case 1:
                    arrayList.add(0);
                    break;
                case 2:
                    arrayList.add(1);
                    break;
                case 3:
                    arrayList.add(2);
                    break;
                case 4:
                    arrayList.add(3);
                    break;
                case 5:
                    arrayList.add(4);
                    break;
                case 6:
                    arrayList.add(5);
                    break;
                case 7:
                    arrayList.add(6);
                    break;
                case '\b':
                    arrayList.add(11);
                    break;
                case '\t':
                    arrayList.add(8);
                    break;
                case '\n':
                    arrayList.add(10);
                    break;
                case 11:
                    arrayList.add(9);
                    break;
                case '\f':
                    arrayList.add(7);
                    break;
                case '\r':
                    arrayList.add(13);
                    break;
            }
        }
        Collections.sort(arrayList);
        ArrayList<String> arrayList2 = new ArrayList();
        for (Integer num : arrayList) {
            arrayList2.add(strArr[num.intValue()]);
        }
        ArrayList<SortPoker> arrayList3 = new ArrayList();
        for (String str2 : arrayList2) {
            SortPoker sortPoker = new SortPoker(str2);
            SortPoker isExists = isExists(arrayList3, sortPoker);
            if (isExists == null) {
                ArrayList arrayList4 = new ArrayList();
                arrayList4.add(str2);
                sortPoker.setList(arrayList4);
                sortPoker.setNumber(1);
                arrayList3.add(sortPoker);
            } else {
                List<String> list2 = isExists.getList();
                list2.add(str2);
                isExists.setList(list2);
                isExists.setNumber(isExists.getNumber() + 1);
            }
        }
        Collections.sort(arrayList3, new Comparator() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda5
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Tool.lambda$sort$0((SortPoker) obj, (SortPoker) obj2);
            }
        });
        ArrayList arrayList5 = new ArrayList();
        for (SortPoker sortPoker2 : arrayList3) {
            arrayList5.addAll(sortPoker2.getList());
        }
        return arrayList5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$sort$0(SortPoker sortPoker, SortPoker sortPoker2) {
        return sortPoker2.getNumber() - sortPoker.getNumber();
    }

    public static SortPoker isExists(List<SortPoker> list, SortPoker sortPoker) {
        SortPoker sortPoker2 = null;
        for (SortPoker sortPoker3 : list) {
            if (sortPoker3.getName().equals(sortPoker.getName())) {
                sortPoker2 = sortPoker3;
            }
        }
        return sortPoker2;
    }

    public static void openQQ(Context context, String str) {
        if (checkApkExist(context, "com.tencent.mobileqq")) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + str + "&version=1")));
            return;
        }
        MyToast.show("本机未安装QQ应用");
    }

    public static boolean isMobileNO(String str) {
        if (!TextUtils.isEmpty(str) && str.length() == 11) {
            return Pattern.compile("^((1[3,5,7,8,9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$").matcher(str).matches();
        }
        return false;
    }

    public static void hideKeyboard(View view) {
        @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(View view) {
        @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
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

    public static boolean isWxIn(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        if (installedPackages != null) {
            for (int i = 0; i < installedPackages.size(); i++) {
                if (installedPackages.get(i).packageName.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkApkExist(Context context, String str) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        if (installedPackages != null) {
            for (int i = 0; i < installedPackages.size(); i++) {
                if (installedPackages.get(i).packageName.equals(str)) {
                    return true;
                }
            }
        }
        return false;
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

    public static String phone(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (i >= 3 && i <= 6) {
                sb.append('*');
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    public static Bitmap up_cropBitmap(Bitmap bitmap) {
        return Bitmap.createBitmap(bitmap, 100, 140, 280, 60, (Matrix) null, false);
    }

    public static Bitmap down_cropBitmap(Bitmap bitmap) {
        return Bitmap.createBitmap(bitmap, 227, 140, 280, 60, (Matrix) null, false);
    }

    public static Bitmap BinaryImage(Bitmap bitmap, int i) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        for (int i2 = 0; i2 < width; i2++) {
            for (int i3 = 0; i3 < height; i3++) {
                int pixel = copy.getPixel(i2, i3);
                int i4 = (-16777216) & pixel;
                int i5 = 255;
                if (((int) ((((16711680 & pixel) >> 16) * 0.3d) + (((65280 & pixel) >> 8) * 0.59d) + ((pixel & 255) * 0.11d))) <= i) {
                    i5 = 0;
                }
                copy.setPixel(i2, i3, (i5 << 16) | i4 | (i5 << 8) | i5);
            }
        }
        return copy;
    }

    public static Bitmap bitmap2Gray(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    public static void copyAssets(Context context, String str, String str2) {
        try {
            Log.e("hjw", "更新资源库");
            InputStream open = context.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(str2, false);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hjw", "拷贝出错：" + e.getLocalizedMessage());
        }
    }

    public static void unZip(File file, String str) throws RuntimeException {
        long currentTimeMillis = System.currentTimeMillis();
        if (!file.exists()) {
            throw new RuntimeException(file.getPath() + "所指文件不存在");
        }
        ZipFile zipFile = null;
        try {
            try {
                ZipFile zipFile2 = new ZipFile(file);
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile2.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry nextElement = entries.nextElement();
                        PrintStream printStream = System.out;
                        printStream.println("解压" + nextElement.getName());
                        if (nextElement.isDirectory()) {
                            new File(str + "/" + nextElement.getName()).mkdirs();
                        } else {
                            File file2 = new File(str + "/" + nextElement.getName());
                            if (!file2.getParentFile().exists()) {
                                file2.getParentFile().mkdirs();
                            }
                            file2.createNewFile();
                            InputStream inputStream = zipFile2.getInputStream(nextElement);
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = inputStream.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                            fileOutputStream.close();
                            inputStream.close();
                        }
                    }
                    long currentTimeMillis2 = System.currentTimeMillis();
                    PrintStream printStream2 = System.out;
                    printStream2.println("解压完成，耗时：" + (currentTimeMillis2 - currentTimeMillis) + " ms");
                    try {
                        zipFile2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e2) {
//                    e = e2;
                    Log.e("hjw---", e2.getMessage());
                    throw new RuntimeException("unzip error from ZipUtils", e2);
                } catch (Throwable th) {
                    th = th;
                    zipFile = zipFile2;
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e4) {
//                e = e4;
            }
        } catch (Throwable th2) {
//            th = th2;
        }
    }

    public static HashMap<String, Integer> initData() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String[] strArr = {"王", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
        for (int i = 0; i < 14; i++) {
            String str = strArr[i];
            hashMap.put(str, Integer.valueOf(str.equals("王") ? 2 : 4));
        }
        return hashMap;
    }

    public static HashMap<String, Integer> initData16() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
        for (int i = 0; i < 13; i++) {
            String str = strArr[i];
            str.hashCode();
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                hashMap.put(str, 1);
            } else if (str.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                hashMap.put(str, 3);
            } else {
                hashMap.put(str, 4);
            }
        }
        return hashMap;
    }

    public static HashMap<String, Integer> initData15() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
        for (int i = 0; i < 13; i++) {
            String str = strArr[i];
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case 50:
                    if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                        c = 0;
                        break;
                    }
                    break;
                case 65:
                    if (str.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                        c = 1;
                        break;
                    }
                    break;
                case 75:
                    if (str.equals("K")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    hashMap.put(str, 1);
                    break;
                case 1:
                    hashMap.put(str, 1);
                    break;
                case 2:
                    hashMap.put(str, 3);
                    break;
                default:
                    hashMap.put(str, 4);
                    break;
            }
        }
        return hashMap;
    }

    public static int getIMG(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("王", Integer.valueOf((int) R.mipmap.poker_icon_king));
        hashMap.put(ExifInterface.GPS_MEASUREMENT_2D, Integer.valueOf((int) R.mipmap.poker_icon_2));
        hashMap.put(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, Integer.valueOf((int) R.mipmap.poker_icon_a));
        hashMap.put("K", Integer.valueOf((int) R.mipmap.poker_icon_k));
        hashMap.put("Q", Integer.valueOf((int) R.mipmap.poker_icon_q));
        hashMap.put("J", Integer.valueOf((int) R.mipmap.poker_icon_j));
        hashMap.put("10", Integer.valueOf((int) R.mipmap.poker_icon_10));
        hashMap.put("9", Integer.valueOf((int) R.mipmap.poker_icon_9));
        hashMap.put("8", Integer.valueOf((int) R.mipmap.poker_icon_8));
        hashMap.put("7", Integer.valueOf((int) R.mipmap.poker_icon_7));
        hashMap.put("6", Integer.valueOf((int) R.mipmap.poker_icon_6));
        hashMap.put("5", Integer.valueOf((int) R.mipmap.poker_icon_5));
        hashMap.put("4", Integer.valueOf((int) R.mipmap.poker_icon_4));
        hashMap.put(ExifInterface.GPS_MEASUREMENT_3D, Integer.valueOf((int) R.mipmap.poker_icon_3));
        return ((Integer) hashMap.get(str)).intValue();
    }

    public static HashMap<String, Integer> initDataZdy(List<ZdyPoker> list) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (ZdyPoker zdyPoker : list) {
            hashMap.put(zdyPoker.getName(), Integer.valueOf(zdyPoker.getCount()));
        }
        return hashMap;
    }

    
    
    @SuppressLint("NewApi")
    public static List<ZdyPoker> initDataZdy(HashMap<String, Integer> hashMap) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            arrayList.add(new ZdyPoker(entry.getKey(), entry.getValue().intValue()));
        }
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, ExifInterface.GPS_MEASUREMENT_2D, "王"};
        final HashMap hashMap2 = new HashMap();
        for (int i = 0; i < 14; i++) {
            hashMap2.put(strArr[i], Integer.valueOf(i));
        }
        arrayList.sort(new Comparator() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Tool.lambda$initDataZdy$1(hashMap2, (ZdyPoker) obj, (ZdyPoker) obj2);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$initDataZdy$1(HashMap hashMap, ZdyPoker zdyPoker, ZdyPoker zdyPoker2) {
        return ((Integer) hashMap.get(zdyPoker2.getName())).intValue() - ((Integer) hashMap.get(zdyPoker.getName())).intValue();
    }

    
    public static String MySort(List<String> list, boolean z) {
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, ExifInterface.GPS_MEASUREMENT_2D, "王"};
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < 14; i++) {
            hashMap.put(strArr[i], Integer.valueOf(i));
        }
        list.sort(new Comparator() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Tool.lambda$MySort$2(hashMap, (String) obj, (String) obj2);
            }
        });
        if (z) {
            return list.get(0);
        }
        return list.get(list.size() - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$MySort$2(HashMap hashMap, String str, String str2) {
        return ((Integer) hashMap.get(str2)).intValue() - ((Integer) hashMap.get(str)).intValue();
    }

    
    public static List<String> MySort(List<String> list) {
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, ExifInterface.GPS_MEASUREMENT_2D, "王"};
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < 14; i++) {
            hashMap.put(strArr[i], Integer.valueOf(i));
        }
        list.sort(new Comparator() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda4
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Tool.lambda$MySort$3(hashMap, (String) obj, (String) obj2);
            }
        });
        return list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$MySort$3(HashMap hashMap, String str, String str2) {
        return ((Integer) hashMap.get(str2)).intValue() - ((Integer) hashMap.get(str)).intValue();
    }

    
    public static List<Integer> mode(List<Integer> list) {
        HashMap hashMap = new HashMap();
        Set<Map.Entry> entrySet = hashMap.entrySet();
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Integer num : list) {
            int intValue = num.intValue();
            hashMap.put(Integer.valueOf(intValue), Integer.valueOf(((Integer) hashMap.getOrDefault(Integer.valueOf(intValue), 0)).intValue() + 1));
        }
        hashMap.forEach(new BiConsumer() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Integer num2 = (Integer) obj;
                arrayList.add((Integer) obj2);
            }
        });
        Collections.sort(arrayList);
        int intValue2 = ((Integer) arrayList.get(arrayList.size() - 1)).intValue();
        for (Map.Entry entry : entrySet) {
            if (((Integer) entry.getValue()).intValue() == intValue2) {
                arrayList2.add((Integer) entry.getKey());
            }
        }
        return arrayList2;
    }

    public static boolean isExist(Rect rect, Rect rect2) {
        return rect2.x >= rect.x && rect2.br().x <= rect.br().x && rect2.y >= rect.y && rect2.br().y <= rect.br().y;
    }

    public static HashMap<String, Integer> average(List<IRect> list) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (IRect iRect : list) {
            if (iRect.isIs_right() || iRect.getName().equals("10") || iRect.getName().equals("王")) {
                i2++;
            } else {
                int i4 = iRect.getRect().height * iRect.getRect().height;
                int i5 = iRect.getRect().y;
                String name = iRect.getName();
                Log.e(name, i4 + "");
                i += i4;
                i3 += i5;
            }
        }
        int size = i == 0 ? 0 : i / (list.size() - i2);
        int size2 = i3 != 0 ? i3 / (list.size() - i2) : 0;
        hashMap.put("area", Integer.valueOf(size));
        hashMap.put("y", Integer.valueOf(size2));
        return hashMap;
    }

    public static String predict(Mat mat) {
        Mat mat2 = new Mat(40, 60, mat.type());
        Imgproc.resize(mat, mat2, new Size(40.0d, 60.0d));
        MatOfFloat matOfFloat = new MatOfFloat();
        DataPool.getHogDescriptor().compute(mat2, matOfFloat);
        return new File(DataPool.getPredicatedResult(DataPool.getkNearest().predict(matOfFloat.reshape(0, 1), new Mat()))).getParentFile().getName();
    }

    public static Mat lately(Mat mat, Rect rect, Rect rect2, List<Rect> list) {
        double d = Double.MAX_VALUE;
        Rect rect3 = null;
        for (int i = 0; i < list.size(); i++) {
            Rect rect4 = list.get(i);
            if (rect4.width >= 10 && ((rect2.x != rect4.x || rect2.y != rect4.y) && rect4.y >= rect2.y + rect2.height)) {
                double pow = Math.pow(Math.abs((rect2.x + (rect2.width * 0.5d)) - (rect4.x + (rect4.width * 0.5d))), 2.0d) + Math.pow(Math.abs((rect2.y + rect2.height) - rect4.y), 2.0d);
                if (pow < d) {
                    rect3 = rect4;
                    d = pow;
                }
            }
        }
        if (rect3 == null) {
            return null;
        }
        Mat mat2 = new Mat(mat, rect3);
        Utils.matToBitmap(mat2, Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888));
        return mat2;
    }

    public static Bitmap getBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, int i5) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false);
        return i5 == 0 ? createBitmap : BinaryImage(createBitmap, i5);
    }

    public static List<String> intersection(List<String> list, List<String> list2) {
        System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        for (String str : list) {
            if (list2.contains(str)) {
                arrayList.add(str);
            }
        }
        System.currentTimeMillis();
        return arrayList;
    }

    public static void saveBitmapToFile(Activity activity, Bitmap bitmap, String str, String str2) {
        saveBitmapToFile(activity, bitmap, Environment.DIRECTORY_PICTURES + "/" + str, FileUtils.getDateName(str2), new SaveBitmapCallback() { // from class: com.carrydream.cardrecorder.tool.Tool.1
            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onSuccess(File file) {
                super.onSuccess(file);
            }

            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onFail(Exception exc) {
                super.onFail(exc);
                exc.printStackTrace();
            }
        });
    }

    public static void saveBitmapData(Activity activity, Bitmap bitmap, String str, String str2) {
        saveBitmapToFile(activity, bitmap, "good_data/" + str, str2, new SaveBitmapCallback() { // from class: com.carrydream.cardrecorder.tool.Tool.2
            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onSuccess(File file) {
                super.onSuccess(file);
            }

            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onFail(Exception exc) {
                super.onFail(exc);
                exc.printStackTrace();
            }
        });
    }

    public static void saveBitmapToFile(final Activity activity, final Bitmap bitmap, final String str, final String str2, final SaveBitmapCallback saveBitmapCallback) {
        if (bitmap == null) {
            saveBitmapCallback.onFail(new NullPointerException("Bitmap不能为null"));
        } else {
            new Thread(new Runnable() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Tool.lambda$saveBitmapToFile$6(activity, str, str2, bitmap, saveBitmapCallback);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$saveBitmapToFile$6(Activity activity, String str, String str2, Bitmap bitmap, final SaveBitmapCallback saveBitmapCallback) {
        File file = new File(activity.getExternalCacheDir(), str);
        file.mkdirs();
        final File file2 = new File(file, str2 + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            activity.runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.tool.Tool.3
                @Override // java.lang.Runnable
                public void run() {
                    saveBitmapCallback.onSuccess(file2);
                }
            });
            fileOutputStream.close();
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.tool.Tool$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    saveBitmapCallback.onFail(e);
                }
            });
        }
    }

    public static Bitmap ChangeBitmap(Bitmap bitmap) {
        int[] iArr = new int[bitmap.getWidth() * bitmap.getHeight()];
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = 0;
        for (int i2 = 0; i2 < bitmap.getHeight(); i2++) {
            for (int i3 = 0; i3 < bitmap.getWidth(); i3++) {
                int pixel = bitmap.getPixel(i3, i2);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int alpha = Color.alpha(pixel);
                if (i3 < 2) {
                    red = 255;
                    green = 255;
                    blue = 255;
                }
                iArr[i] = Color.argb(alpha, red, green, blue);
                Log.i("imagecolor", "============" + iArr[i]);
                i++;
            }
        }
        return Bitmap.createBitmap(iArr, width, height, Bitmap.Config.ARGB_8888);
    }
}
