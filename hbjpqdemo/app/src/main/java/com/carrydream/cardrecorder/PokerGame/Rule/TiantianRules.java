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
public class TiantianRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:256:0x0415 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0193  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
        Iterator it;
        IRect iRect = null;
        boolean z2 = false;
        ArrayList arrayList = null;
        Iterator it2 = null;
        ArrayList arrayList2 = null;
        ArrayList arrayList3 = null;
        String str;
        Mat mat;
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
                mat = clone2;
                if (boundingRect.tl().y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i5 > i3) {
                    i3 = i5;
                    mat5 = new Mat(mat4, boundingRect);
                    rect2 = boundingRect;
                }
            } else {
                mat = clone2;
            }
            i2++;
            clone2 = mat;
            arrayList4 = arrayList6;
            size = i4;
        }
        mat3.release();
        if (mat5 == null) {
            return;
        }
        if (i == 1 && rect2.br().x < bitmap.getWidth() * 0.5d) {
            return;
        }
        if (i == 2 && rect2.x > bitmap.getWidth() * 0.5d) {
            return;
        }
        if (i != 0) {
            if ((i3 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                return;
            }
            if (rect2.height > bitmap.getHeight() * 0.5d) {
                z = true;
                ArrayList arrayList7 = new ArrayList();
                ArrayList arrayList8 = new ArrayList();
                it = arrayList5.iterator();
                while (true) {
                    String str2 = "王";
                    if (!it.hasNext()) {
                        Rect rect3 = (Rect) it.next();
                        if (Tool.isExist(rect2, rect3)) {
                            Mat mat6 = new Mat(mat4, rect3);
                            float f2 = (rect3.width * 1.0f) / rect3.height;
                            ArrayList arrayList9 = arrayList5;
                            ArrayList arrayList10 = arrayList7;
                            ArrayList arrayList11 = arrayList8;
                            if (rect3.br().y <= rect2.y + (rect2.height * 0.65d) && rect2.br().y - rect3.br().y >= rect3.height) {
                                String predict = Tool.predict(mat6);
                                Iterator it3 = it;
                                if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                                    if (predict.equals("king")) {
                                        predict = "王";
                                    }
                                    if (rect3.width * rect3.height <= 5500 && (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect3.width * rect3.height >= 400)) {
                                        if (!"X".equals(predict) && !"O".equals(predict) && !"diamond".equals(predict) && !"spade".equals(predict) && !"heart".equals(predict) && !"club".equals(predict)) {
                                            predict.hashCode();
                                            char c = 65535;
                                            it2 = it3;
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
                                                    arrayList2 = arrayList9;
                                                    Mat lately = Tool.lately(mat4, rect2, rect3, arrayList2);
                                                    if (lately == null) {
                                                        arrayList5 = arrayList2;
                                                        z = z2;
                                                        arrayList7 = arrayList10;
                                                        arrayList8 = arrayList11;
                                                        it = it2;
                                                        break;
                                                    } else {
                                                        Utils.matToBitmap(lately, Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict2 = Tool.predict(lately);
                                                        if (!predict2.equals("O") && !predict2.equals("K")) {
                                                            str2 = "J";
                                                        }
                                                        arrayList = arrayList11;
                                                        arrayList.add(str2);
                                                        arrayList3 = arrayList10;
                                                        arrayList3.add(new IRect(str2, false, rect3));
                                                        break;
                                                    }
                                                case 1:
                                                    arrayList2 = arrayList9;
                                                    z2 = z;
                                                    str = predict;
                                                    if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                        Mat lately2 = Tool.lately(mat4, rect2, rect3, arrayList2);
                                                        if (lately2 != null) {
                                                            Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                            String predict3 = Tool.predict(lately2);
                                                            if (!"diamond".equals(predict3) && !"spade".equals(predict3) && !"heart".equals(predict3) && !"club".equals(predict3)) {
                                                            }
                                                        }
                                                        arrayList5 = arrayList2;
                                                        z = z2;
                                                        arrayList7 = arrayList10;
                                                        arrayList8 = arrayList11;
                                                        it = it2;
                                                        break;
                                                    }
                                                    str2 = str;
                                                    arrayList = arrayList11;
                                                    arrayList.add(str2);
                                                    arrayList3 = arrayList10;
                                                    arrayList3.add(new IRect(str2, false, rect3));
                                                    break;
                                                case 2:
                                                    arrayList2 = arrayList9;
                                                    z2 = z;
                                                    arrayList = arrayList11;
                                                    arrayList.add(str2);
                                                    arrayList3 = arrayList10;
                                                    arrayList3.add(new IRect(str2, false, rect3));
                                                    break;
                                                case 3:
                                                    arrayList2 = arrayList9;
                                                    if (f2 >= 0.3f) {
                                                        z2 = z;
                                                        str = predict;
                                                        str2 = str;
                                                        arrayList = arrayList11;
                                                        arrayList.add(str2);
                                                        arrayList3 = arrayList10;
                                                        arrayList3.add(new IRect(str2, false, rect3));
                                                        break;
                                                    } else {
                                                        Log.e("比例不对10", f2 + "");
                                                        arrayList5 = arrayList2;
                                                        arrayList7 = arrayList10;
                                                        arrayList8 = arrayList11;
                                                        it = it2;
                                                        break;
                                                    }
                                                case 4:
                                                    arrayList2 = arrayList9;
                                                    Mat lately3 = Tool.lately(mat4, rect2, rect3, arrayList2);
                                                    if (lately3 == null) {
                                                        arrayList5 = arrayList2;
                                                        arrayList7 = arrayList10;
                                                        arrayList8 = arrayList11;
                                                        it = it2;
                                                        break;
                                                    } else {
                                                        Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict4 = Tool.predict(lately3);
                                                        if (!predict4.equals("O") && !predict4.equals("K")) {
                                                            str2 = "J";
                                                        }
                                                        z2 = z;
                                                        arrayList = arrayList11;
                                                        arrayList.add(str2);
                                                        arrayList3 = arrayList10;
                                                        arrayList3.add(new IRect(str2, false, rect3));
                                                        break;
                                                    }
                                                default:
                                                    if (f2 < 0.5f || f2 > 1.0f) {
                                                        Log.e("比例不对", f2 + "=====" + predict);
                                                    } else if (i == 0 || rect3.y <= (rect3.height * 0.5d) + rect2.y || z) {
                                                        z2 = z;
                                                        arrayList2 = arrayList9;
                                                        str = predict;
                                                        str2 = str;
                                                        arrayList = arrayList11;
                                                        arrayList.add(str2);
                                                        arrayList3 = arrayList10;
                                                        arrayList3.add(new IRect(str2, false, rect3));
                                                        break;
                                                    }
                                                    arrayList5 = arrayList9;
                                                    arrayList7 = arrayList10;
                                                    arrayList8 = arrayList11;
                                                    it = it2;
                                                    break;
                                            }
                                        } else {
                                            z2 = z;
                                            it2 = it3;
                                            arrayList2 = arrayList9;
                                            arrayList3 = arrayList10;
                                            arrayList = arrayList11;
                                        }
                                    }
                                }
                                it = it3;
                            }
                            arrayList5 = arrayList9;
                            arrayList7 = arrayList10;
                            arrayList8 = arrayList11;
                        } else {
                            z2 = z;
                            arrayList = arrayList8;
                            it2 = it;
                            arrayList2 = arrayList5;
                            arrayList3 = arrayList7;
                        }
                        arrayList5 = arrayList2;
                        arrayList7 = arrayList3;
                        it = it2;
                        arrayList8 = arrayList;
                        z = z2;
                    } else {
                        ArrayList<IRect> arrayList12 = arrayList7;
                        ArrayList<String> arrayList13 = arrayList8;
                        if (i != 0) {
                            HashMap hashMap = new HashMap();
                            for (String str3 : arrayList13) {
                                hashMap.put(str3, str3);
                            }
                            switch (arrayList13.size()) {
                                case 1:
                                    if (((IRect) arrayList12.get(0)).getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList12.get(0);
                                    IRect iRect3 = (IRect) arrayList12.get(1);
                                    if (hashMap.size() > 1 && arrayList13.contains("王")) {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList13.removeAll((List) arrayList13.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules$$ExternalSyntheticLambda0
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj) {
                                                    return TiantianRules.lambda$Enable$0((String) obj);
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
                                        if (!arrayList13.contains("王")) {
                                            return;
                                        }
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList13.removeAll((List) arrayList13.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.TiantianRules$$ExternalSyntheticLambda1
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj) {
                                                    return TiantianRules.lambda$Enable$1((String) obj);
                                                }
                                            }).collect(Collectors.toList()));
                                            break;
                                        }
                                    }
                                    break;
                                case 4:
                                    if (hashMap.size() >= 3) {
                                        Log.e("不是炸或者3带1", "不符合规则" + arrayList13.toString());
                                        return;
                                    }
                                    break;
                                case 5:
                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者3带对", "不符合规则" + arrayList13.toString());
                                        return;
                                    }
                                    break;
                                case 6:
                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList13.toString());
                                        return;
                                    }
                                    break;
                            }
                        } else if (arrayList13.size() < DataUtils.getInstance().getHand()) {
                            return;
                        } else {
                            ArrayList arrayList14 = new ArrayList();
                            for (IRect iRect4 : arrayList12) {
                                arrayList14.add(Integer.valueOf(iRect4.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList14);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue = num.intValue() / mode.size();
                                for (IRect iRect5 : arrayList12) {
                                    double d = intValue;
                                    if (iRect5.getRect().y < 0.9d * d || iRect5.getRect().y > d * 1.1d) {
                                        arrayList13.remove(iRect5.getName());
                                        Log.e("自己的牌里面差别较大的", iRect5.getName());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i, arrayList13.toString());
                        onOCRListener.play(i, Tool.sort(arrayList13));
                        return;
                    }
                }
            }
        }
        z = false;
        ArrayList arrayList72 = new ArrayList();
        ArrayList arrayList82 = new ArrayList();
        it = arrayList5.iterator();
        while (true) {
            String str22 = "王";
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
