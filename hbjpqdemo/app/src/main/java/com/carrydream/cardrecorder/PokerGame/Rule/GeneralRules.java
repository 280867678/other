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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class GeneralRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:304:0x0534 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x023b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
        Iterator it;
        Rect rect;
        String str;
        IRect iRect = null;
        Iterator it2;
        Rect rect2;
        String str2;
        boolean z2 = false;
        Iterator it3;
        ArrayList arrayList = null;
        Activity activity2 = null;
        ArrayList arrayList2 = null;
        int i2 = 0;
        Mat mat = null;
        Mat mat2;
        boolean z3;
        Mat mat3;
        Activity activity3 = activity;
        int i3 = i;
        Mat mat4 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat4);
        Mat clone = mat4.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect3 = new Rect();
        rect3.width = width;
        rect3.height = height;
        rect3.x = 0;
        rect3.y = (height / 2) - (rect3.height / 2);
        Mat mat5 = new Mat(clone, rect3);
        Mat clone2 = mat5.clone();
        Imgproc.cvtColor(mat5, mat5, 11);
        Mat mat6 = new Mat(mat4.rows(), mat4.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat5, mat6, 100, 255.0d, 0);
        ArrayList arrayList3 = new ArrayList();
        Imgproc.findContours(mat6, arrayList3, new Mat(), 1, 2);
        ArrayList arrayList4 = new ArrayList();
        int size = arrayList3.size();
        Rect rect4 = null;
        Mat mat7 = null;
        int i4 = 0;
        int i5 = 0;
        while (i4 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList3.get(i4));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            ArrayList arrayList5 = arrayList3;
            int i6 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.06d && ((i3 == 0 || f <= 5.5d) && f >= 0.3d)) {
                arrayList4.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i7 = boundingRect.width * boundingRect.height;
                mat3 = clone2;
                if (boundingRect.tl().y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i7 > i5) {
                    i5 = i7;
                    mat7 = new Mat(mat6, boundingRect);
                    rect4 = boundingRect;
                }
            } else {
                mat3 = clone2;
            }
            i4++;
            clone2 = mat3;
            arrayList3 = arrayList5;
            size = i6;
        }
        Mat mat8 = clone2;
        if (i3 != 0) {
            ArrayList arrayList6 = new ArrayList();
            arrayList6.addAll(arrayList4);
            arrayList6.remove(rect4);
            Iterator it4 = arrayList6.iterator();
            while (it4.hasNext()) {
                Rect rect5 = (Rect) it4.next();
                double d = rect5.width * rect5.height;
                Iterator it5 = it4;
                double d2 = i5;
                if (d > d2 * 0.95d && d < d2 * 1.05d) {
                    return;
                }
                it4 = it5;
            }
        }
        mat5.release();
        Bitmap createBitmap = Bitmap.createBitmap(mat8.cols(), mat8.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat8, createBitmap);
        Tool.saveBitmapToFile(activity3, createBitmap, i3 + "_", "框");
        if (mat7 == null) {
            return;
        }
        if (i3 == 1 && rect4.br().x < bitmap.getWidth() * 0.5d) {
            return;
        }
        if (i3 == 2 && rect4.x > bitmap.getWidth() * 0.5d) {
            return;
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(mat7.cols(), mat7.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat7, createBitmap2);
        Tool.saveBitmapToFile(activity3, createBitmap2, i3 + "_", i3 + "Max");
        if (i3 != 0) {
            if ((i5 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                return;
            }
            if (rect4.height > bitmap.getHeight() * 0.55d) {
                z = true;
                ArrayList<IRect> arrayList7 = new ArrayList();
                ArrayList arrayList8 = new ArrayList();
                it = arrayList4.iterator();
                while (true) {
                    String str3 = "王";
                    if (!it.hasNext()) {
                        Rect rect6 = (Rect) it.next();
                        if (Tool.isExist(rect4, rect6)) {
                            Mat mat9 = new Mat(mat6, rect6);
                            float f2 = (rect6.width * 1.0f) / rect6.height;
                            Utils.matToBitmap(mat9, Bitmap.createBitmap(mat9.cols(), mat9.rows(), Bitmap.Config.ARGB_8888));
                            ArrayList arrayList9 = arrayList8;
                            Mat mat10 = mat6;
                            it3 = it;
                            ArrayList arrayList10 = arrayList4;
                            ArrayList arrayList11 = arrayList7;
                            boolean z4 = rect6.br().y > ((double) rect4.y) + (((double) rect4.height) * 0.6666d) ? true : rect6.br().x > rect4.br().x - (((double) (rect6.width * 2)) * 0.8d);
                            if (rect4.br().y - rect6.br().y >= rect6.height) {
                                String predict = Tool.predict(mat9);
                                if (predict.equals("king")) {
                                    predict = "王";
                                }
                                if (rect6.width * rect6.height <= 5500 && (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect6.width * rect6.height >= 400)) {
                                    if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict)) {
                                        z2 = z;
                                        arrayList7 = arrayList11;
                                        mat2 = mat9;
                                    } else {
                                        mat2 = mat9;
                                        if ("heart".equals(predict)) {
                                            z2 = z;
                                            arrayList7 = arrayList11;
                                        } else {
                                            if (!"club".equals(predict)) {
                                                predict.hashCode();
                                                char c = 65535;
                                                switch (predict.hashCode()) {
                                                    case 74:
                                                        if (predict.equals("J")) {
                                                            c = 0;
                                                            break;
                                                        }
                                                        break;
                                                    case 75:
                                                        if (predict.equals("K")) {
                                                            c = 1;
                                                            break;
                                                        }
                                                        break;
                                                    case 87:
                                                        if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                                            c = 2;
                                                            break;
                                                        }
                                                        break;
                                                    case 1567:
                                                        if (predict.equals("10")) {
                                                            c = 3;
                                                            break;
                                                        }
                                                        break;
                                                    case 29579:
                                                        if (predict.equals("王")) {
                                                            c = 4;
                                                            break;
                                                        }
                                                        break;
                                                }
                                                switch (c) {
                                                    case 0:
                                                        z2 = z;
                                                        mat = mat10;
                                                        arrayList = arrayList10;
                                                        z3 = z4;
                                                        Mat lately = Tool.lately(mat, rect4, rect6, arrayList);
                                                        if (lately != null) {
                                                            Utils.matToBitmap(lately, Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888));
                                                            Tool.predict(lately);
                                                            if (!Tool.predict(lately).equals("O")) {
                                                                str3 = "J";
                                                            }
                                                            arrayList2 = arrayList9;
                                                            arrayList2.add(str3);
                                                            arrayList7 = arrayList11;
                                                            arrayList7.add(new IRect(str3, z3, rect6));
                                                            predict = str3;
                                                            break;
                                                        } else {
                                                            activity3 = activity;
                                                            mat6 = mat;
                                                            arrayList4 = arrayList;
                                                            z = z2;
                                                            arrayList8 = arrayList9;
                                                            it = it3;
                                                            arrayList7 = arrayList11;
                                                            i3 = i;
                                                        }
                                                    case 1:
                                                        mat = mat10;
                                                        arrayList = arrayList10;
                                                        z2 = z;
                                                        z3 = z4;
                                                        if (rect6.y >= rect4.y + (rect6.width * 0.5d)) {
                                                            Mat lately2 = Tool.lately(mat, rect4, rect6, arrayList);
                                                            if (lately2 != null) {
                                                                Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                                String predict2 = Tool.predict(lately2);
                                                                if (!"diamond".equals(predict2) && !"spade".equals(predict2) && !"heart".equals(predict2) && !"club".equals(predict2)) {
                                                                }
                                                            }
                                                            activity3 = activity;
                                                            mat6 = mat;
                                                            arrayList4 = arrayList;
                                                            z = z2;
                                                            arrayList8 = arrayList9;
                                                            it = it3;
                                                            arrayList7 = arrayList11;
                                                            i3 = i;
                                                        }
                                                        str3 = predict;
                                                        arrayList2 = arrayList9;
                                                        arrayList2.add(str3);
                                                        arrayList7 = arrayList11;
                                                        arrayList7.add(new IRect(str3, z3, rect6));
                                                        predict = str3;
                                                        break;
                                                    case 2:
                                                        mat = mat10;
                                                        arrayList = arrayList10;
                                                        z2 = z;
                                                        z3 = z4;
                                                        arrayList2 = arrayList9;
                                                        arrayList2.add(str3);
                                                        arrayList7 = arrayList11;
                                                        arrayList7.add(new IRect(str3, z3, rect6));
                                                        predict = str3;
                                                        break;
                                                    case 3:
                                                        mat = mat10;
                                                        arrayList = arrayList10;
                                                        if (f2 >= 0.3f) {
                                                            z2 = z;
                                                            z3 = z4;
                                                            str3 = predict;
                                                            arrayList2 = arrayList9;
                                                            arrayList2.add(str3);
                                                            arrayList7 = arrayList11;
                                                            arrayList7.add(new IRect(str3, z3, rect6));
                                                            predict = str3;
                                                            break;
                                                        } else {
                                                            Log.e("比例不对10", f2 + "");
                                                            activity3 = activity;
                                                            mat6 = mat;
                                                            arrayList4 = arrayList;
                                                            arrayList8 = arrayList9;
                                                            it = it3;
                                                            arrayList7 = arrayList11;
                                                            i3 = i;
                                                        }
                                                    case 4:
                                                        mat = mat10;
                                                        arrayList = arrayList10;
                                                        Mat lately3 = Tool.lately(mat, rect4, rect6, arrayList);
                                                        if (lately3 != null) {
                                                            Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                            if (!Tool.predict(lately3).equals("O")) {
                                                                str3 = "J";
                                                            }
                                                            z2 = z;
                                                            z3 = z4;
                                                            arrayList2 = arrayList9;
                                                            arrayList2.add(str3);
                                                            arrayList7 = arrayList11;
                                                            arrayList7.add(new IRect(str3, z3, rect6));
                                                            predict = str3;
                                                            break;
                                                        } else {
                                                            activity3 = activity;
                                                            mat6 = mat;
                                                            arrayList4 = arrayList;
                                                            arrayList8 = arrayList9;
                                                            it = it3;
                                                            arrayList7 = arrayList11;
                                                            i3 = i;
                                                        }
                                                    default:
                                                        if (f2 < 0.5f || f2 > 1.0f) {
                                                            Log.e("比例不对", f2 + "=====" + predict);
                                                        } else if (i3 == 0 || rect6.y <= (rect6.height * 0.5d) + rect4.y || z) {
                                                            z2 = z;
                                                            mat = mat10;
                                                            arrayList = arrayList10;
                                                            z3 = z4;
                                                            str3 = predict;
                                                            arrayList2 = arrayList9;
                                                            arrayList2.add(str3);
                                                            arrayList7 = arrayList11;
                                                            arrayList7.add(new IRect(str3, z3, rect6));
                                                            predict = str3;
                                                            break;
                                                        }
                                                        activity3 = activity;
                                                        i3 = i;
                                                        mat6 = mat10;
                                                        arrayList8 = arrayList9;
                                                        arrayList4 = arrayList10;
                                                        it = it3;
                                                        arrayList7 = arrayList11;
                                                        break;
                                                }
                                            } else {
                                                z2 = z;
                                                mat = mat10;
                                                arrayList2 = arrayList9;
                                                arrayList = arrayList10;
                                                arrayList7 = arrayList11;
                                            }
                                            Bitmap createBitmap3 = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
                                            Utils.matToBitmap(mat2, createBitmap3);
                                            StringBuilder sb = new StringBuilder();
                                            i2 = i;
                                            sb.append(i2);
                                            sb.append("");
                                            activity2 = activity;
                                            Tool.saveBitmapToFile(activity2, createBitmap3, predict, sb.toString());
                                        }
                                    }
                                    mat = mat10;
                                    arrayList2 = arrayList9;
                                    arrayList = arrayList10;
                                    Bitmap createBitmap32 = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat2, createBitmap32);
                                    StringBuilder sb2 = new StringBuilder();
                                    i2 = i;
                                    sb2.append(i2);
                                    sb2.append("");
                                    activity2 = activity;
                                    Tool.saveBitmapToFile(activity2, createBitmap32, predict, sb2.toString());
                                }
                            }
                            arrayList7 = arrayList11;
                            mat6 = mat10;
                            arrayList8 = arrayList9;
                            arrayList4 = arrayList10;
                            it = it3;
                        } else {
                            z2 = z;
                            it3 = it;
                            arrayList = arrayList4;
                            activity2 = activity3;
                            arrayList2 = arrayList8;
                            Mat mat11 = mat6;
                            i2 = i3;
                            mat = mat11;
                        }
                        arrayList8 = arrayList2;
                        arrayList4 = arrayList;
                        activity3 = activity2;
                        z = z2;
                        it = it3;
                        int i8 = i2;
                        mat6 = mat;
                        i3 = i8;
                    } else {
                        ArrayList<String> arrayList12 = arrayList8;
                        if (i3 != 0) {
                            HashMap<String, Integer> average = Tool.average(arrayList7);
                            int intValue = average.get("area").intValue();
                            int intValue2 = average.get("y").intValue();
                            double d3 = intValue;
                            double d4 = 0.95d * d3;
                            double d5 = d3 * 1.05d;
                            double d6 = intValue2 - 10;
                            double d7 = intValue2 + 10;
                            if (intValue != 0) {
                                ArrayList arrayList13 = new ArrayList();
                                Iterator it6 = arrayList7.iterator();
                                while (it6.hasNext()) {
                                    IRect iRect2 = (IRect) it6.next();
                                    if (iRect2.isIs_right()) {
                                        it2 = it6;
                                        int i9 = iRect2.getRect().height * iRect2.getRect().height;
                                        int i10 = iRect2.getRect().y;
                                        rect2 = rect4;
                                        str2 = str3;
                                        double d8 = i9;
                                        if (d8 >= d4 && d8 <= d5) {
                                            double d9 = i10;
                                            if (d9 <= d7 && d9 >= d6) {
                                            }
                                        }
                                        arrayList12.remove(iRect2.getName());
                                        arrayList13.add(iRect2);
                                    } else {
                                        it2 = it6;
                                        rect2 = rect4;
                                        str2 = str3;
                                    }
                                    it6 = it2;
                                    rect4 = rect2;
                                    str3 = str2;
                                }
                                rect = rect4;
                                str = str3;
                                arrayList7.removeAll(arrayList13);
                            } else {
                                rect = rect4;
                                str = "王";
                            }
                            HashMap hashMap = new HashMap();
                            for (String str4 : arrayList12) {
                                hashMap.put(str4, str4);
                            }
                            switch (arrayList12.size()) {
                                case 1:
                                    if (((IRect) arrayList7.get(0)).getRect().br().x > rect.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    }
                                    break;
                                case 2:
                                    String str5 = str;
                                    IRect iRect3 = (IRect) arrayList7.get(0);
                                    IRect iRect4 = (IRect) arrayList7.get(1);
                                    if (hashMap.size() > 1) {
                                        if (arrayList12.contains(str5)) {
                                            if (Build.VERSION.SDK_INT >= 24) {
                                                arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules$$ExternalSyntheticLambda0
                                                    @Override // java.util.function.Predicate
                                                    public final boolean test(Object obj) {
                                                        return GeneralRules.lambda$Enable$0((String) obj);
                                                    }
                                                }).collect(Collectors.toList()));
                                                break;
                                            }
                                        } else if (iRect3.getRect().y != iRect4.getRect().y) {
                                            return;
                                        }
                                    }
                                    break;
                                case 3:
                                    if (hashMap.size() > 1) {
                                        if (!arrayList12.contains(str)) {
                                            return;
                                        }
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.GeneralRules$$ExternalSyntheticLambda1
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj) {
                                                    return GeneralRules.lambda$Enable$1((String) obj);
                                                }
                                            }).collect(Collectors.toList()));
                                            break;
                                        }
                                    } else {
                                        String str6 = str;
                                        if (((String) arrayList12.get(0)).equals(str6)) {
                                            arrayList12.remove(str6);
                                            break;
                                        }
                                    }
                                    break;
                                case 4:
                                    if (hashMap.size() >= 3) {
                                        Log.e("不是炸或者3带1", "不符合规则" + arrayList12.toString());
                                        return;
                                    }
                                    break;
                                case 5:
                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者3带对", "不符合规则" + arrayList12.toString());
                                        return;
                                    }
                                    break;
                                case 6:
                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList12.toString());
                                        return;
                                    }
                                    break;
                            }
                        } else if (arrayList12.size() < DataUtils.getInstance().getHand()) {
                            return;
                        } else {
                            ArrayList arrayList14 = new ArrayList();
                            for (IRect iRect5 : arrayList7) {
                                arrayList14.add(Integer.valueOf(iRect5.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList14);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue3 = num.intValue() / mode.size();
                                for (IRect iRect6 : arrayList7) {
                                    double d10 = intValue3;
                                    if (iRect6.getRect().y < 0.9d * d10 || iRect6.getRect().y > d10 * 1.1d) {
                                        arrayList12.remove(iRect6.getName());
                                        Log.e("自己的牌里面差别较大的", iRect6.getName());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i, arrayList12.toString());
                        onOCRListener.play(i, Tool.sort(arrayList12));
                        return;
                    }
                }
            }
        }
        z = false;
        ArrayList<IRect> arrayList72 = new ArrayList();
        ArrayList arrayList82 = new ArrayList();
        it = arrayList4.iterator();
        while (true) {
            String str32 = "王";
            if (!it.hasNext()) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$Enable$0(String str) {
        return !str.equals("王");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$Enable$1(String str) {
        return !str.equals("王");
    }
}
