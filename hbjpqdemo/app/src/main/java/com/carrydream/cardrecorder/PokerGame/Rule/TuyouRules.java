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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class TuyouRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:189:0x04ee  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x066f  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0201  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
        Iterator it;
        IRect iRect = null;
        Activity activity2 = null;
        ArrayList arrayList = null;
        Iterator it2 = null;
        ArrayList arrayList2 = null;
        boolean z2 = false;
        Object obj;
        String str = null;
        Mat mat;
        String str2;
        String str3;
        ArrayList arrayList3;
        Activity activity3 = activity;
        Mat mat2 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat2);
        Mat clone = mat2.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect = new Rect();
        rect.width = width;
        rect.height = height;
        rect.x = 0;
        rect.y = (height / 2) - (rect.height / 2);
        Mat mat3 = new Mat(clone, rect);
        Mat clone2 = mat3.clone();
        Imgproc.cvtColor(mat3, mat3, 11);
        Mat mat4 = new Mat(mat2.rows(), mat2.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat3, mat4, 100, 255.0d, 0);
        ArrayList arrayList4 = new ArrayList();
        Imgproc.findContours(mat4, arrayList4, new Mat(), 1, 2);
        ArrayList arrayList5 = new ArrayList();
        int size = arrayList4.size();
        Mat mat5 = null;
        Rect rect2 = null;
        int i2 = 0;
        int i3 = 0;
        while (i2 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList4.get(i2));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            ArrayList arrayList6 = arrayList4;
            int i4 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i == 0 || f <= 5.0f) && f >= 0.3d)) {
                arrayList5.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i5 = boundingRect.width * boundingRect.height;
                Point tl = boundingRect.tl();
                if (i != 0) {
                    arrayList3 = arrayList5;
                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i5 > i3) {
                        i3 = i5;
                        mat5 = new Mat(mat4, boundingRect);
                        rect2 = boundingRect;
                    }
                    i2++;
                    arrayList4 = arrayList6;
                    size = i4;
                    arrayList5 = arrayList3;
                } else if (i5 > i3) {
                    i3 = i5;
                    mat5 = new Mat(mat4, boundingRect);
                    rect2 = boundingRect;
                }
            }
            arrayList3 = arrayList5;
            i2++;
            arrayList4 = arrayList6;
            size = i4;
            arrayList5 = arrayList3;
        }
        ArrayList arrayList7 = arrayList5;
        mat3.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        Tool.saveBitmapToFile(activity3, createBitmap, i + "_", "框");
        if (mat5 == null) {
            return;
        }
        if (i != 1 || rect2.br().x >= bitmap.getWidth() * 0.5d) {
            if (i != 2 || rect2.x <= bitmap.getWidth() * 0.5d) {
                Bitmap createBitmap2 = Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat5, createBitmap2);
                Tool.saveBitmapToFile(activity3, createBitmap2, i + "_", i + "Max");
                if (i != 0) {
                    if ((i3 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                        return;
                    }
                    if (rect2.height > bitmap.getHeight() * 0.5d) {
                        z = true;
                        ArrayList<IRect> arrayList8 = new ArrayList();
                        ArrayList arrayList9 = new ArrayList();
                        it = arrayList7.iterator();
                        while (it.hasNext()) {
                            Rect rect3 = (Rect) it.next();
                            if (Tool.isExist(rect2, rect3)) {
                                Mat mat6 = new Mat(mat4, rect3);
                                float f2 = (rect3.width * 1.0f) / rect3.height;
                                Utils.matToBitmap(mat6, Bitmap.createBitmap(mat6.cols(), mat6.rows(), Bitmap.Config.ARGB_8888));
                                ArrayList arrayList10 = arrayList8;
                                ArrayList arrayList11 = arrayList9;
                                if (rect3.br().y <= rect2.y + (rect2.height * 0.65d) && rect2.br().y - rect3.br().y >= rect3.height) {
                                    String predict = Tool.predict(mat6);
                                    Iterator it3 = it;
                                    if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                                        if (predict.equals("king")) {
                                            predict = "王";
                                        }
                                        if (rect3.width * rect3.height <= 5500) {
                                            if (predict.equals("10")) {
                                                obj = "王";
                                            } else {
                                                obj = "王";
                                                if (!predict.equals(obj) && !predict.equals("J") && rect3.width * rect3.height < 400) {
                                                }
                                            }
                                            if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict)) {
                                                str = predict;
                                                mat = mat6;
                                                it2 = it3;
                                            } else {
                                                it2 = it3;
                                                if ("spade".equals(predict) || "heart".equals(predict)) {
                                                    str = predict;
                                                    mat = mat6;
                                                } else {
                                                    mat = mat6;
                                                    if ("club".equals(predict)) {
                                                        str = predict;
                                                    } else {
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
                                                                if (predict.equals(obj)) {
                                                                    c = 4;
                                                                    break;
                                                                }
                                                                break;
                                                        }
                                                        switch (c) {
                                                            case 0:
                                                                arrayList2 = arrayList7;
                                                                z2 = z;
                                                                Mat lately = lately(mat4, rect2, rect3, arrayList2);
                                                                if (lately == null) {
                                                                    activity3 = activity;
                                                                    it = it2;
                                                                    arrayList8 = arrayList10;
                                                                    z = z2;
                                                                    arrayList7 = arrayList2;
                                                                    break;
                                                                } else {
                                                                    Utils.matToBitmap(lately, Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888));
                                                                    String predict2 = Tool.predict(lately);
                                                                    if (!predict2.equals("O") && !predict2.equals("K")) {
                                                                        str2 = "J";
                                                                        arrayList = arrayList11;
                                                                        arrayList.add(str2);
                                                                        arrayList8 = arrayList10;
                                                                        arrayList8.add(new IRect(str2, false, rect3));
                                                                        Bitmap createBitmap3 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                        Utils.matToBitmap(mat, createBitmap3);
                                                                        activity2 = activity;
                                                                        Tool.saveBitmapToFile(activity2, createBitmap3, str2, i + "");
                                                                        break;
                                                                    }
                                                                    str2 = obj.toString();
                                                                    arrayList = arrayList11;
                                                                    arrayList.add(str2);
                                                                    arrayList8 = arrayList10;
                                                                    arrayList8.add(new IRect(str2, false, rect3));
                                                                    Bitmap createBitmap32 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                    Utils.matToBitmap(mat, createBitmap32);
                                                                    activity2 = activity;
                                                                    Tool.saveBitmapToFile(activity2, createBitmap32, str2, i + "");
                                                                }
                                                                break;
                                                            case 1:
                                                                arrayList2 = arrayList7;
                                                                z2 = z;
                                                                str3 = predict;
                                                                if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                                    Mat lately2 = lately(mat4, rect2, rect3, arrayList2);
                                                                    if (lately2 != null) {
                                                                        Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                                        String predict3 = Tool.predict(lately2);
                                                                        if (!"diamond".equals(predict3) && !"spade".equals(predict3) && !"heart".equals(predict3) && !"club".equals(predict3)) {
                                                                        }
                                                                    }
                                                                    activity3 = activity;
                                                                    it = it2;
                                                                    arrayList8 = arrayList10;
                                                                    z = z2;
                                                                    arrayList7 = arrayList2;
                                                                    break;
                                                                }
                                                                arrayList = arrayList11;
                                                                str2 = str3;
                                                                arrayList.add(str2);
                                                                arrayList8 = arrayList10;
                                                                arrayList8.add(new IRect(str2, false, rect3));
                                                                Bitmap createBitmap322 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                Utils.matToBitmap(mat, createBitmap322);
                                                                activity2 = activity;
                                                                Tool.saveBitmapToFile(activity2, createBitmap322, str2, i + "");
                                                                break;
                                                            case 2:
                                                                arrayList2 = arrayList7;
                                                                z2 = z;
                                                                str2 = obj.toString();
                                                                arrayList = arrayList11;
                                                                arrayList.add(str2);
                                                                arrayList8 = arrayList10;
                                                                arrayList8.add(new IRect(str2, false, rect3));
                                                                Bitmap createBitmap3222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                Utils.matToBitmap(mat, createBitmap3222);
                                                                activity2 = activity;
                                                                Tool.saveBitmapToFile(activity2, createBitmap3222, str2, i + "");
                                                                break;
                                                            case 3:
                                                                arrayList2 = arrayList7;
                                                                if (f2 >= 0.3f) {
                                                                    z2 = z;
                                                                    str3 = predict;
                                                                    arrayList = arrayList11;
                                                                    str2 = str3;
                                                                    arrayList.add(str2);
                                                                    arrayList8 = arrayList10;
                                                                    arrayList8.add(new IRect(str2, false, rect3));
                                                                    Bitmap createBitmap32222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                    Utils.matToBitmap(mat, createBitmap32222);
                                                                    activity2 = activity;
                                                                    Tool.saveBitmapToFile(activity2, createBitmap32222, str2, i + "");
                                                                    break;
                                                                } else {
                                                                    Log.e("比例不对10", f2 + "");
                                                                    activity3 = activity;
                                                                    arrayList7 = arrayList2;
                                                                    it = it2;
                                                                    break;
                                                                }
                                                            case 4:
                                                                arrayList2 = arrayList7;
                                                                Mat lately3 = lately(mat4, rect2, rect3, arrayList2);
                                                                if (lately3 == null) {
                                                                    activity3 = activity;
                                                                    arrayList7 = arrayList2;
                                                                    it = it2;
                                                                    break;
                                                                } else {
                                                                    Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                                    String predict4 = Tool.predict(lately3);
                                                                    str2 = (predict4.equals("O") || predict4.equals("K")) ? obj.toString() : "J";
                                                                    z2 = z;
                                                                    arrayList = arrayList11;
                                                                    arrayList.add(str2);
                                                                    arrayList8 = arrayList10;
                                                                    arrayList8.add(new IRect(str2, false, rect3));
                                                                    Bitmap createBitmap322222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                    Utils.matToBitmap(mat, createBitmap322222);
                                                                    activity2 = activity;
                                                                    Tool.saveBitmapToFile(activity2, createBitmap322222, str2, i + "");
                                                                    break;
                                                                }
                                                            default:
                                                                if (f2 < 0.5f || f2 > 1.0f) {
                                                                    Log.e("比例不对", f2 + "=====" + predict);
                                                                } else if (i == 0 || rect3.y <= (rect3.height * 0.5d) + rect2.y || z) {
                                                                    str3 = predict;
                                                                    arrayList2 = arrayList7;
                                                                    z2 = z;
                                                                    arrayList = arrayList11;
                                                                    str2 = str3;
                                                                    arrayList.add(str2);
                                                                    arrayList8 = arrayList10;
                                                                    arrayList8.add(new IRect(str2, false, rect3));
                                                                    Bitmap createBitmap3222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                    Utils.matToBitmap(mat, createBitmap3222222);
                                                                    activity2 = activity;
                                                                    Tool.saveBitmapToFile(activity2, createBitmap3222222, str2, i + "");
                                                                    break;
                                                                }
                                                                activity3 = activity;
                                                                it = it2;
                                                                break;
                                                        }
                                                        arrayList9 = arrayList11;
                                                    }
                                                }
                                            }
                                            arrayList8 = arrayList10;
                                            arrayList = arrayList11;
                                            arrayList2 = arrayList7;
                                            z2 = z;
                                            str2 = str;
                                            Bitmap createBitmap32222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                            Utils.matToBitmap(mat, createBitmap32222222);
                                            activity2 = activity;
                                            Tool.saveBitmapToFile(activity2, createBitmap32222222, str2, i + "");
                                        }
                                    }
                                    it = it3;
                                }
                                arrayList8 = arrayList10;
                                arrayList9 = arrayList11;
                            } else {
                                activity2 = activity3;
                                arrayList = arrayList9;
                                it2 = it;
                                arrayList2 = arrayList7;
                                z2 = z;
                            }
                            it = it2;
                            z = z2;
                            arrayList7 = arrayList2;
                            arrayList9 = arrayList;
                            activity3 = activity2;
                        }
                        ArrayList<String> arrayList12 = arrayList9;
                        if (i == 0) {
                            HashMap hashMap = new HashMap();
                            for (String str4 : arrayList12) {
                                hashMap.put(str4, str4);
                            }
                            switch (arrayList12.size()) {
                                case 1:
                                    if (((IRect) arrayList8.get(0)).getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList8.get(0);
                                    IRect iRect3 = (IRect) arrayList8.get(1);
                                    if (hashMap.size() > 1 && arrayList12.contains("王")) {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules$$ExternalSyntheticLambda0
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj2) {
                                                    return TuyouRules.lambda$Enable$0((String) obj2);
                                                }
                                            }).collect(Collectors.toList()));
                                            break;
                                        }
                                    } else if (Math.abs(iRect2.getRect().br().y - iRect3.getRect().br().y) > 4.0d) {
                                        return;
                                    }
                                    break;
                                case 3:
                                    if (hashMap.size() > 1) {
                                        if (!arrayList12.contains("王")) {
                                            return;
                                        }
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.TuyouRules$$ExternalSyntheticLambda1
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj2) {
                                                    return TuyouRules.lambda$Enable$1((String) obj2);
                                                }
                                            }).collect(Collectors.toList()));
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
                        } else {
                            Log.e("自己", arrayList12.toString());
                            if (arrayList12.size() < DataUtils.getInstance().getHand()) {
                                return;
                            }
                            ArrayList arrayList13 = new ArrayList();
                            for (IRect iRect4 : arrayList8) {
                                arrayList13.add(Integer.valueOf(iRect4.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList13);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue = num.intValue() / mode.size();
                                for (IRect iRect5 : arrayList8) {
                                    double d = intValue;
                                    if (iRect5.getRect().y < 0.9d * d || iRect5.getRect().y > d * 1.1d) {
                                        arrayList12.remove(iRect5.getName());
                                        Log.e("自己的牌里面差别较大的", iRect5.getName());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i, arrayList12.toString());
                        onOCRListener.play(i, Tool.sort(arrayList12));
                    }
                }
                z = false;
                ArrayList<IRect> arrayList82 = new ArrayList();
                ArrayList arrayList92 = new ArrayList();
                it = arrayList7.iterator();
                while (it.hasNext()) {
                }
                ArrayList<String> arrayList122 = arrayList92;
                if (i == 0) {
                }
                Log.e("出牌" + i, arrayList122.toString());
                onOCRListener.play(i, Tool.sort(arrayList122));
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

    public static Mat lately(Mat mat, Rect rect, Rect rect2, List<Rect> list) {
        double d = Double.MAX_VALUE;
        Rect rect3 = null;
        for (int i = 0; i < list.size(); i++) {
            Rect rect4 = list.get(i);
            if (rect4.width >= 10 && ((rect2.x != rect4.x || rect2.y != rect4.y) && rect4.y >= (rect2.y + rect2.height) - 10)) {
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
}
