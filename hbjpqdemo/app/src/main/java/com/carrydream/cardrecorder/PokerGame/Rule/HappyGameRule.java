package com.carrydream.cardrecorder.PokerGame.Rule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import com.carrydream.cardrecorder.Model.IRect;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.Tool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class HappyGameRule {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:273:0x07dd A[LOOP:9: B:271:0x07d7->B:273:0x07dd, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:277:0x07f0  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x0808  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x0819  */
    /* JADX WARN: Removed duplicated region for block: B:294:0x0821  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x083d  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x0899  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0174  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x01da A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01db  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        Rect rect;
        boolean z;
        String str;
        IRect iRect = null;
        int i2 = 0;
        Rect rect2;
        Iterator it;
        Object obj;
        String str2;
        Mat mat;
        boolean z2 = false;
        Iterator it2;
        Mat mat2 = null;
        Activity activity2 = null;
        int i3 = 0;
        ArrayList arrayList = null;
        ArrayList arrayList2 = null;
        Mat mat3;
        String str3;
        Mat mat4;
        Activity activity3 = activity;
        int i4 = i;
        Mat mat5 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat5);
        Mat clone = mat5.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect3 = new Rect();
        rect3.width = width;
        rect3.height = height;
        int i5 = 0;
        Integer num = 0;
        rect3.x = 0;
        rect3.y = (height / 2) - (rect3.height / 2);
        Mat mat6 = new Mat(clone, rect3);
        Mat clone2 = mat6.clone();
        Imgproc.cvtColor(mat6, mat6, 11);
        Mat mat7 = new Mat(mat5.rows(), mat5.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat6, mat7, 100, 255.0d, 0);
        ArrayList arrayList3 = new ArrayList();
        Imgproc.findContours(mat7, arrayList3, new Mat(), 1, 2);
        ArrayList arrayList4 = new ArrayList();
        int size = arrayList3.size();
        Rect rect4 = null;
        Mat mat8 = null;
        int i6 = 0;
        while (i6 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList3.get(i6));
            ArrayList arrayList5 = arrayList3;
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            Integer num2 = num;
            int i7 = size;
            Rect rect5 = rect4;
            if (boundingRect.height >= bitmap.getHeight() * 0.06d && ((i4 == 0 || f <= 5.5d) && f >= 0.3d)) {
                arrayList4.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i8 = boundingRect.width * boundingRect.height;
                mat4 = mat8;
                if (boundingRect.tl().y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i8 > i5) {
                    i5 = i8;
                    mat8 = new Mat(mat7, boundingRect);
                    rect4 = boundingRect;
                    i6++;
                    arrayList3 = arrayList5;
                    num = num2;
                    size = i7;
                }
            } else {
                mat4 = mat8;
            }
            mat8 = mat4;
            rect4 = rect5;
            i6++;
            arrayList3 = arrayList5;
            num = num2;
            size = i7;
        }
        Integer num3 = num;
        Rect rect6 = rect4;
        Mat mat9 = mat8;
        if (i4 != 0) {
            if ((i5 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                return;
            }
            rect = rect6;
            if (rect.height > bitmap.getHeight() * 0.55d) {
                z = true;
                if (i4 != 0) {
                    ArrayList<Rect> arrayList6 = new ArrayList();
                    arrayList6.addAll(arrayList4);
                    arrayList6.remove(rect);
                    for (Rect rect7 : arrayList6) {
                        double d = rect7.width * rect7.height;
                        double d2 = i5;
                        if (d > 0.95d * d2 && d < d2 * 1.05d) {
                            return;
                        }
                    }
                }
                mat6.release();
                Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(clone2, createBitmap);
                Tool.saveBitmapToFile(activity3, createBitmap, i4 + "_", "框");
                if (mat9 != null) {
                    return;
                }
                if (i4 == 1 && rect.br().x < bitmap.getWidth() * 0.5d) {
                    return;
                }
                if (i4 == 2 && rect.x > bitmap.getWidth() * 0.5d) {
                    return;
                }
                Bitmap createBitmap2 = Bitmap.createBitmap(mat9.cols(), mat9.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat9, createBitmap2);
                Tool.saveBitmapToFile(activity3, createBitmap2, i4 + "_", i4 + "Max");
                ArrayList arrayList7 = new ArrayList();
                ArrayList arrayList8 = new ArrayList();
                Iterator it3 = arrayList4.iterator();
                while (true) {
                    String str4 = "10";
                    String str5 = "王";
                    if (it3.hasNext()) {
                        Rect rect8 = (Rect) it3.next();
                        if (Tool.isExist(rect, rect8)) {
                            Mat mat10 = new Mat(mat7, rect8);
                            float f2 = (rect8.width * 1.0f) / rect8.height;
                            it2 = it3;
                            if (rect8.width > rect8.height) {
                                it3 = it2;
                            } else {
                                Utils.matToBitmap(mat10, Bitmap.createBitmap(mat10.cols(), mat10.rows(), Bitmap.Config.ARGB_8888));
                                ArrayList arrayList9 = arrayList8;
                                Mat mat11 = mat7;
                                z2 = z;
                                ArrayList<IRect> arrayList10 = arrayList7;
                                boolean z3 = rect8.br().x > rect.br().x - (((double) (rect8.width * 2)) * 0.8d);
                                if (rect8.br().y > rect.y + (rect.height * 0.6666d)) {
                                    z3 = true;
                                }
                                String predict = Tool.predict(mat10);
                                if (predict.equals("king")) {
                                    predict = "王";
                                }
                                if (rect8.width * rect8.height <= 5500 && (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect8.width * rect8.height >= 400)) {
                                    if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict) || "heart".equals(predict)) {
                                        str5 = predict;
                                        arrayList2 = arrayList9;
                                        mat2 = mat11;
                                        arrayList = arrayList10;
                                    } else if (!"club".equals(predict)) {
                                        predict.hashCode();
                                        char c = 65535;
                                        switch (predict.hashCode()) {
                                            case 56:
                                                if (predict.equals("8")) {
                                                    c = 0;
                                                    break;
                                                }
                                                break;
                                            case 74:
                                                if (predict.equals("J")) {
                                                    c = 1;
                                                    break;
                                                }
                                                break;
                                            case 75:
                                                if (predict.equals("K")) {
                                                    c = 2;
                                                    break;
                                                }
                                                break;
                                            case 87:
                                                if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                                    c = 3;
                                                    break;
                                                }
                                                break;
                                            case 1567:
                                                if (predict.equals("10")) {
                                                    c = 4;
                                                    break;
                                                }
                                                break;
                                            case 29579:
                                                if (predict.equals("王")) {
                                                    c = 5;
                                                    break;
                                                }
                                                break;
                                        }
                                        switch (c) {
                                            case 0:
                                                str3 = predict;
                                                mat2 = mat11;
                                                mat3 = mat10;
                                                if (f2 >= 0.5f && f2 <= 0.8f) {
                                                    str5 = str3;
                                                    arrayList2 = arrayList9;
                                                    arrayList2.add(str5);
                                                    IRect iRect2 = new IRect(str5, z3, rect8);
                                                    arrayList = arrayList10;
                                                    arrayList.add(iRect2);
                                                    Bitmap createBitmap3 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                    Utils.matToBitmap(mat3, createBitmap3);
                                                    StringBuilder sb = new StringBuilder();
                                                    i3 = i;
                                                    sb.append(i3);
                                                    sb.append("");
                                                    activity2 = activity;
                                                    Tool.saveBitmapToFile(activity2, createBitmap3, str5, sb.toString());
                                                    break;
                                                } else {
                                                    Log.e("比例不对", f2 + "=====" + str3);
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList9;
                                                    mat7 = mat2;
                                                    it3 = it2;
                                                    z = z2;
                                                    activity3 = activity;
                                                    i4 = i;
                                                    break;
                                                }
                                            case 1:
                                                mat2 = mat11;
                                                mat3 = mat10;
                                                Mat lately = Tool.lately(mat2, rect, rect8, arrayList4);
                                                if (lately == null) {
                                                    activity3 = activity;
                                                    i4 = i;
                                                    mat7 = mat2;
                                                    it3 = it2;
                                                    z = z2;
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList9;
                                                    break;
                                                } else {
                                                    Utils.matToBitmap(lately, Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888));
                                                    Tool.predict(lately);
                                                    if (!Tool.predict(lately).equals("O")) {
                                                        str5 = "J";
                                                    }
                                                    arrayList2 = arrayList9;
                                                    arrayList2.add(str5);
                                                    IRect iRect22 = new IRect(str5, z3, rect8);
                                                    arrayList = arrayList10;
                                                    arrayList.add(iRect22);
                                                    Bitmap createBitmap32 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                    Utils.matToBitmap(mat3, createBitmap32);
                                                    StringBuilder sb2 = new StringBuilder();
                                                    i3 = i;
                                                    sb2.append(i3);
                                                    sb2.append("");
                                                    activity2 = activity;
                                                    Tool.saveBitmapToFile(activity2, createBitmap32, str5, sb2.toString());
                                                    break;
                                                }
                                            case 2:
                                                mat2 = mat11;
                                                mat3 = mat10;
                                                str3 = predict;
                                                if (rect8.y >= rect.y + (rect8.width * 0.5d)) {
                                                    Mat lately2 = Tool.lately(mat2, rect, rect8, arrayList4);
                                                    if (lately2 != null) {
                                                        Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict2 = Tool.predict(lately2);
                                                        if (!"diamond".equals(predict2) && !"spade".equals(predict2) && !"heart".equals(predict2) && !"club".equals(predict2)) {
                                                        }
                                                    }
                                                    activity3 = activity;
                                                    i4 = i;
                                                    mat7 = mat2;
                                                    it3 = it2;
                                                    z = z2;
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList9;
                                                    break;
                                                }
                                                str5 = str3;
                                                arrayList2 = arrayList9;
                                                arrayList2.add(str5);
                                                IRect iRect222 = new IRect(str5, z3, rect8);
                                                arrayList = arrayList10;
                                                arrayList.add(iRect222);
                                                Bitmap createBitmap322 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                Utils.matToBitmap(mat3, createBitmap322);
                                                StringBuilder sb22 = new StringBuilder();
                                                i3 = i;
                                                sb22.append(i3);
                                                sb22.append("");
                                                activity2 = activity;
                                                Tool.saveBitmapToFile(activity2, createBitmap322, str5, sb22.toString());
                                                break;
                                            case 3:
                                                mat2 = mat11;
                                                if (f2 <= 0.55f) {
                                                    boolean z4 = false;
                                                    for (IRect iRect3 : arrayList10) {
                                                        if (iRect3.getName().equals("王") && rect8.x > iRect3.getRect().x - 3 && rect8.x < iRect3.getRect().x + 3) {
                                                            z4 = true;
                                                        }
                                                    }
                                                    if (!z4) {
                                                        mat3 = mat10;
                                                        arrayList2 = arrayList9;
                                                        arrayList2.add(str5);
                                                        IRect iRect2222 = new IRect(str5, z3, rect8);
                                                        arrayList = arrayList10;
                                                        arrayList.add(iRect2222);
                                                        Bitmap createBitmap3222 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                        Utils.matToBitmap(mat3, createBitmap3222);
                                                        StringBuilder sb222 = new StringBuilder();
                                                        i3 = i;
                                                        sb222.append(i3);
                                                        sb222.append("");
                                                        activity2 = activity;
                                                        Tool.saveBitmapToFile(activity2, createBitmap3222, str5, sb222.toString());
                                                        break;
                                                    }
                                                }
                                                activity3 = activity;
                                                mat7 = mat2;
                                                it3 = it2;
                                                z = z2;
                                                arrayList7 = arrayList10;
                                                arrayList8 = arrayList9;
                                                break;
                                            case 4:
                                                mat2 = mat11;
                                                if (f2 < 0.3f) {
                                                    activity3 = activity;
                                                    mat7 = mat2;
                                                    it3 = it2;
                                                    z = z2;
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList9;
                                                    break;
                                                } else {
                                                    str3 = predict;
                                                    mat3 = mat10;
                                                    str5 = str3;
                                                    arrayList2 = arrayList9;
                                                    arrayList2.add(str5);
                                                    IRect iRect22222 = new IRect(str5, z3, rect8);
                                                    arrayList = arrayList10;
                                                    arrayList.add(iRect22222);
                                                    Bitmap createBitmap32222 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                    Utils.matToBitmap(mat3, createBitmap32222);
                                                    StringBuilder sb2222 = new StringBuilder();
                                                    i3 = i;
                                                    sb2222.append(i3);
                                                    sb2222.append("");
                                                    activity2 = activity;
                                                    Tool.saveBitmapToFile(activity2, createBitmap32222, str5, sb2222.toString());
                                                    break;
                                                }
                                            case 5:
                                                mat2 = mat11;
                                                Mat lately3 = Tool.lately(mat2, rect, rect8, arrayList4);
                                                if (lately3 == null) {
                                                    activity3 = activity;
                                                    mat7 = mat2;
                                                    it3 = it2;
                                                    z = z2;
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList9;
                                                    break;
                                                } else {
                                                    Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                    if (!Tool.predict(lately3).equals("O")) {
                                                        str5 = "J";
                                                    }
                                                    mat3 = mat10;
                                                    arrayList2 = arrayList9;
                                                    arrayList2.add(str5);
                                                    IRect iRect222222 = new IRect(str5, z3, rect8);
                                                    arrayList = arrayList10;
                                                    arrayList.add(iRect222222);
                                                    Bitmap createBitmap322222 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                    Utils.matToBitmap(mat3, createBitmap322222);
                                                    StringBuilder sb22222 = new StringBuilder();
                                                    i3 = i;
                                                    sb22222.append(i3);
                                                    sb22222.append("");
                                                    activity2 = activity;
                                                    Tool.saveBitmapToFile(activity2, createBitmap322222, str5, sb22222.toString());
                                                    break;
                                                }
                                            default:
                                                if (f2 >= 0.5f && f2 <= 1.3f) {
                                                    if (i4 == 0 || ((rect8.y <= (rect8.height * 0.5d) + rect.y || z2) && rect8.height <= bitmap.getHeight() * 0.16d)) {
                                                        str3 = predict;
                                                        mat2 = mat11;
                                                        mat3 = mat10;
                                                        str5 = str3;
                                                        arrayList2 = arrayList9;
                                                        arrayList2.add(str5);
                                                        IRect iRect2222222 = new IRect(str5, z3, rect8);
                                                        arrayList = arrayList10;
                                                        arrayList.add(iRect2222222);
                                                        Bitmap createBitmap3222222 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                                        Utils.matToBitmap(mat3, createBitmap3222222);
                                                        StringBuilder sb222222 = new StringBuilder();
                                                        i3 = i;
                                                        sb222222.append(i3);
                                                        sb222222.append("");
                                                        activity2 = activity;
                                                        Tool.saveBitmapToFile(activity2, createBitmap3222222, str5, sb222222.toString());
                                                        break;
                                                    }
                                                } else {
                                                    Log.e("比例不对", f2 + "=====" + predict);
                                                }
                                                activity3 = activity;
                                                it3 = it2;
                                                mat7 = mat11;
                                                z = z2;
                                                arrayList7 = arrayList10;
                                                arrayList8 = arrayList9;
                                                break;
                                        }
                                    } else {
                                        str5 = predict;
                                        mat2 = mat11;
                                        arrayList = arrayList10;
                                        arrayList2 = arrayList9;
                                    }
                                    mat3 = mat10;
                                    Bitmap createBitmap32222222 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat3, createBitmap32222222);
                                    StringBuilder sb2222222 = new StringBuilder();
                                    i3 = i;
                                    sb2222222.append(i3);
                                    sb2222222.append("");
                                    activity2 = activity;
                                    Tool.saveBitmapToFile(activity2, createBitmap32222222, str5, sb2222222.toString());
                                } else {
                                    activity3 = activity;
                                    arrayList8 = arrayList9;
                                    it3 = it2;
                                    mat7 = mat11;
                                    z = z2;
                                    arrayList7 = arrayList10;
                                }
                            }
                        } else {
                            z2 = z;
                            it2 = it3;
                            mat2 = mat7;
                            activity2 = activity3;
                            i3 = i4;
                            arrayList = arrayList7;
                            arrayList2 = arrayList8;
                        }
                        arrayList7 = arrayList;
                        arrayList8 = arrayList2;
                        i4 = i3;
                        activity3 = activity2;
                        mat7 = mat2;
                        it3 = it2;
                        z = z2;
                    } else {
                        int i9 = i4;
                        ArrayList<String> arrayList11 = arrayList8;
                        Mat mat12 = mat7;
                        ArrayList<IRect> arrayList12 = arrayList7;
                        if (i9 != 0) {
                            HashMap<String, Integer> average = Tool.average(arrayList12);
                            int intValue = average.get("area").intValue();
                            int intValue2 = average.get("y").intValue();
                            Rect rect9 = rect;
                            Object obj2 = "王";
                            double d3 = intValue;
                            double d4 = 0.9d * d3;
                            double d5 = d3 * 1.1d;
                            double d6 = intValue2 - 10;
                            double d7 = intValue2 + 10;
                            if (intValue != 0) {
                                Iterator it4 = arrayList12.iterator();
                                while (it4.hasNext()) {
                                    IRect iRect4 = (IRect) it4.next();
                                    if (iRect4.isIs_right()) {
                                        it = it4;
                                        obj = obj2;
                                        int i10 = iRect4.getRect().height * iRect4.getRect().height;
                                        int i11 = iRect4.getRect().y;
                                        str2 = str4;
                                        mat = mat12;
                                        double d8 = i10;
                                        if (d8 >= d4 && d8 <= d5) {
                                            double d9 = i11;
                                            if (d9 <= d7 && d9 >= d6) {
                                            }
                                        }
                                        arrayList11.remove(iRect4.getName());
                                    } else {
                                        it = it4;
                                        obj = obj2;
                                        str2 = str4;
                                        mat = mat12;
                                    }
                                    it4 = it;
                                    obj2 = obj;
                                    str4 = str2;
                                    mat12 = mat;
                                }
                            }
                            Object obj3 = obj2;
                            String str6 = str4;
                            Mat mat13 = mat12;
                            if (arrayList11.size() >= 9) {
                                ArrayList arrayList13 = new ArrayList();
                                for (IRect iRect5 : arrayList12) {
                                    arrayList13.add(Integer.valueOf(iRect5.getRect().y));
                                }
                                if (Build.VERSION.SDK_INT >= 24) {
                                    int intValue3 = (int)Tool.mode(arrayList13).get(0);
                                    ArrayList<IRect> arrayList14 = new ArrayList();
                                    for (IRect iRect6 : arrayList12) {
                                        if (iRect6.getRect().y < intValue3 + 10 && iRect6.getRect().y > intValue3 - 10) {
                                            arrayList14.add(iRect6);
                                        }
                                    }
                                    if (arrayList14.size() < 9) {
                                        arrayList14.sort(new Comparator() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.HappyGameRule$$ExternalSyntheticLambda0
                                            @Override // java.util.Comparator
                                            public final int compare(Object obj4, Object obj5) {
                                                return HappyGameRule.lambda$Enable$0((IRect) obj4, (IRect) obj5);
                                            }
                                        });
                                        ArrayList arrayList15 = new ArrayList();
                                        Integer num4 = num3;
                                        for (IRect iRect7 : arrayList14) {
                                            int intValue4 = iRect7.getRect().x - num4.intValue();
                                            if (intValue4 != iRect7.getRect().x) {
                                                arrayList15.add(Integer.valueOf(intValue4));
                                            }
                                            num4 = Integer.valueOf(iRect7.getRect().x);
                                        }
                                        int intValue5 = ((Integer) Collections.max(arrayList15)).intValue();
                                        int i12 = 0;
//                                        while (true) {
                                            if (i12 >= arrayList15.size()) {
                                                i2 = 0;
                                            } else if (intValue5 == ((Integer) arrayList15.get(i12)).intValue()) {
                                                arrayList15.remove(i12);
                                                i2 = i12 + 1;
                                            } else {
                                                i12++;
                                            }
//                                        }
                                        IRect iRect80 = (IRect) arrayList14.get(i2 - 1);
                                        IRect iRect9 = (IRect) arrayList14.get(i2);
                                        if (iRect80.getName().equals(iRect9.getName())) {
                                            arrayList11.add(iRect80.getName());
                                        } else {
                                            str = str6;
                                            if (iRect80.getName().equals(str)) {
                                                rect2 = new Rect(iRect80.getRect().x + ((Integer) arrayList15.get(0)).intValue(), iRect9.getRect().y, iRect9.getRect().width, iRect9.getRect().height);
                                            } else {
                                                rect2 = new Rect(iRect80.getRect().x + ((Integer) arrayList15.get(0)).intValue(), iRect80.getRect().y, iRect80.getRect().width, iRect80.getRect().height);
                                            }
                                            Mat mat14 = new Mat(mat13, rect2);
                                            Bitmap createBitmap4 = Bitmap.createBitmap(mat14.cols(), mat14.rows(), Bitmap.Config.ARGB_8888);
                                            Utils.matToBitmap(mat14, createBitmap4);
                                            Tool.saveBitmapToFile(activity, createBitmap4, "预测", i9 + "");
                                            arrayList11.add(Tool.predict(mat14));
                                            HashMap hashMap = new HashMap();
                                            for (String str7 : arrayList11) {
                                                hashMap.put(str7, str7);
                                            }
                                            switch (arrayList11.size()) {
                                                case 1:
                                                    if (((IRect) arrayList12.get(0)).getRect().br().x > rect9.tl().x + (iRect.getRect().width * 3)) {
                                                        return;
                                                    }
                                                    break;
                                                case 2:
                                                    IRect iRect10 = (IRect) arrayList12.get(0);
                                                    IRect iRect11 = (IRect) arrayList12.get(1);
                                                    double d10 = rect9.tl().x + (iRect10.getRect().width * 3.5d);
                                                    if (hashMap.size() > 1) {
                                                        return;
                                                    }
                                                    if (!iRect10.getName().equals(str) && !iRect10.getName().equals(obj3) && (iRect10.getRect().br().x > d10 || iRect11.getRect().br().x > d10)) {
                                                        return;
                                                    }
                                                    break;
                                                case 3:
                                                    if (hashMap.size() > 1) {
                                                        return;
                                                    }
                                                    if (((String) arrayList11.get(0)).equals(obj3)) {
                                                        arrayList11.remove(obj3);
                                                        break;
                                                    }
                                                    break;
                                                case 4:
                                                    if (hashMap.size() >= 3) {
                                                        return;
                                                    }
                                                    break;
                                                case 5:
                                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                                        return;
                                                    }
                                                    break;
                                                case 6:
                                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                                        return;
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                            str = str6;
                            HashMap hashMap2 = new HashMap();
//                            while (r3.hasNext()) {
//                            }
//                            switch (arrayList11.size()) {
//                            }
                        } else if (arrayList11.size() < DataUtils.getInstance().getHand()) {
                            return;
                        } else {
                            ArrayList arrayList16 = new ArrayList();
                            for (IRect iRect12 : arrayList12) {
                                arrayList16.add(Integer.valueOf(iRect12.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                List<Integer> mode = Tool.mode(arrayList16);
                                for (Integer num5 : mode) {
                                    num3 = Integer.valueOf(num3.intValue() + num5.intValue());
                                }
                                int intValue6 = num3.intValue() / mode.size();
                                for (IRect iRect13 : arrayList12) {
                                    double d11 = intValue6;
                                    if (iRect13.getRect().y < d11 * 0.9d || iRect13.getRect().y > d11 * 1.1d) {
                                        arrayList11.remove(iRect13.getName());
                                    }
                                }
                            }
                        }
                        onOCRListener.play(i9, Tool.sort(arrayList11));
                        return;
                    }
                }
            }
        } else {
            rect = rect6;
        }
        z = false;
        if (i4 != 0) {
        }
        mat6.release();
        Bitmap createBitmap5 = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap5);
        Tool.saveBitmapToFile(activity3, createBitmap5, i4 + "_", "框");
        if (mat9 != null) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$Enable$0(IRect iRect, IRect iRect2) {
        return iRect.getRect().x - iRect2.getRect().x;
    }
}
