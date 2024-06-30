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
public class JJRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Code restructure failed: missing block: B:147:0x03c1, code lost:
        if (r0.equals("club") == false) goto L129;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0440, code lost:
        if ("club".equals(r0) == false) goto L174;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x0470, code lost:
        if (r0.equals("club") == false) goto L129;
     */
    /* JADX WARN: Removed duplicated region for block: B:283:0x049a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0201  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
//        Log.e("不是炸或者3带1  Enable", "不符合规则" + i);
        boolean z;
        Iterator it;
        IRect iRect = null;
        boolean z2 = false;
        ArrayList arrayList = null;
        Iterator it2;
        ArrayList arrayList2;
        ArrayList arrayList3;
        ArrayList arrayList4;
        ArrayList arrayList5;
        int i2 = i;
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        Mat clone = mat.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect = new Rect();
        rect.width = width;
        rect.height = height;
        rect.x = 0;
        rect.y = (height / 2) - (rect.height / 2);
        Mat mat2 = new Mat(clone, rect);
        Mat clone2 = mat2.clone();
        Imgproc.cvtColor(mat2, mat2, 11);
        Mat mat3 = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat2, mat3, 100, 255.0d, 0);
        ArrayList arrayList6 = new ArrayList();
        Imgproc.findContours(mat3, arrayList6, new Mat(), 1, 2);
        ArrayList arrayList7 = new ArrayList();
        int size = arrayList6.size();
        Mat mat4 = null;
        Rect rect2 = null;
        int i3 = 0;
        int i4 = 0;
        while (i3 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList6.get(i3));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            ArrayList arrayList8 = arrayList6;
            int i5 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i2 == 0 || f <= 5.0f) && f >= 0.3d)) {
                arrayList7.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i6 = boundingRect.width * boundingRect.height;
                Point tl = boundingRect.tl();
                if (i2 != 0) {
                    arrayList5 = arrayList7;
                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i6 > i4) {
                        i4 = i6;
                        mat4 = new Mat(mat3, boundingRect);
                        rect2 = boundingRect;
                    }
                    i3++;

                    arrayList7 = arrayList5;
                } else if (i6 > i4) {
                    i4 = i6;
                    mat4 = new Mat(mat3, boundingRect);
                    rect2 = boundingRect;
                }
            }
            arrayList5 = arrayList7;
            i3++;
            arrayList6 = arrayList8;
            size = i5;
            arrayList7 = arrayList5;
        }
        ArrayList arrayList9 = arrayList7;
        mat2.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        Tool.saveBitmapToFile(activity, createBitmap, i2 + "_", "框");
        if (mat4 == null) {
            return;
        }
        if (i2 == 1 && rect2.br().x < bitmap.getWidth() * 0.5d) {
            return;
        }
        if (i2 == 2 && rect2.x > bitmap.getWidth() * 0.5d) {
            return;
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(mat4.cols(), mat4.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat4, createBitmap2);
        Tool.saveBitmapToFile(activity, createBitmap2, i2 + "_", i2 + "Max");
        if (i2 != 0) {
            if ((i4 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                return;
            }
            if (rect2.height > bitmap.getHeight() * 0.5d) {
                z = true;
                ArrayList<IRect> arrayList10 = new ArrayList();
                ArrayList arrayList11 = new ArrayList();
                it = arrayList9.iterator();
                while (true) {
                    String str = "王";
                    if (!it.hasNext()) {
                        Rect rect3 = (Rect) it.next();
                        if (Tool.isExist(rect2, rect3)) {
                            Mat mat5 = new Mat(mat3, rect3);
                            float f2 = (rect3.width * 1.0f) / rect3.height;
                            Utils.matToBitmap(mat5, Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888));
                            if (i2 != 0) {
                                arrayList3 = arrayList10;
                                arrayList4 = arrayList11;
                                if (rect3.br().y > rect2.y + (rect2.height * 0.65d) || rect2.br().y - rect3.br().y < rect3.height) {

                                }
                            } else {
                                arrayList3 = arrayList10;
                                arrayList4 = arrayList11;
                            }
                            String predict = Tool.predict(mat5);
                            it2 = it;
                            if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                                if (predict.equals("king")) {
                                    predict = "王";
                                }
                                if (rect3.width * rect3.height <= 5500 && (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect3.width * rect3.height >= 400)) {
                                    if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict) || "heart".equals(predict) || "club".equals(predict)) {

                                        z2 = z;
                                        arrayList = arrayList4;
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
                                                Mat lately = Tool.lately(mat3, rect2, rect3, arrayList2);
                                                if (lately != null) {
                                                    String predict2 = Tool.predict(lately);
                                                    if (!predict2.equals("diamond")) {
                                                        if (!predict2.equals("spade")) {
                                                            if (!predict2.equals("heart")) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    str = "J";
                                                    arrayList = arrayList4;
                                                    arrayList.add(str);
                                                    arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));

                                                    arrayList9 = arrayList2;

                                                    break;
                                                }

                                                arrayList9 = arrayList2;

                                                break;
                                            case 1:
                                                z2 = z;
                                                arrayList2 = arrayList9;
                                                if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                    Mat lately2 = Tool.lately(mat3, rect2, rect3, arrayList2);
                                                    if (lately2 != null) {
                                                        Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict3 = Tool.predict(lately2);
                                                        if (!"diamond".equals(predict3)) {
                                                            if (!"spade".equals(predict3)) {
                                                                if (!"heart".equals(predict3)) {
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    arrayList9 = arrayList2;

                                                    break;
                                                }
                                                str = predict;
                                                arrayList = arrayList4;
                                                arrayList.add(str);
                                                arrayList10 = arrayList3;
                                                arrayList10.add(new IRect(str, false, rect3));

                                                arrayList9 = arrayList2;

                                                break;
                                            case 2:
                                                z2 = z;
                                                arrayList2 = arrayList9;
                                                arrayList = arrayList4;
                                                arrayList.add(str);
                                                arrayList10 = arrayList3;
                                                arrayList10.add(new IRect(str, false, rect3));

                                                arrayList9 = arrayList2;

                                                break;
                                            case 3:
                                                z2 = z;
                                                arrayList2 = arrayList9;
                                                if (f2 < 0.3f) {
                                                    Log.e("比例不对10", f2 + "");

                                                    arrayList9 = arrayList2;

                                                    break;
                                                }
                                                str = predict;
                                                arrayList = arrayList4;
                                                arrayList.add(str);
                                                arrayList10 = arrayList3;
                                                arrayList10.add(new IRect(str, false, rect3));

                                                arrayList9 = arrayList2;

                                                break;
                                            case 4:
                                                arrayList2 = arrayList9;
                                                Mat lately3 = Tool.lately(mat3, rect2, rect3, arrayList2);
                                                if (lately3 != null) {
                                                    z2 = z;
                                                    Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                    String predict4 = Tool.predict(lately3);
                                                    if (!predict4.equals("diamond")) {
                                                        if (!predict4.equals("spade")) {
                                                            if (!predict4.equals("heart")) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    str = "J";
                                                    arrayList = arrayList4;
                                                    arrayList.add(str);
                                                    arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));

                                                    arrayList9 = arrayList2;


                                                    break;
                                                } else {

                                                    arrayList9 = arrayList2;

                                                }
                                            default:
                                                if (f2 >= 0.5f && f2 <= 1.0f) {
                                                    if (i2 != 0 && rect3.y > (rect3.height * 0.5d) + rect2.y && !z) {
                                                        break;
                                                    } else {
                                                        z2 = z;
                                                        arrayList2 = arrayList9;
                                                        str = predict;
                                                        arrayList = arrayList4;
                                                        arrayList.add(str);
                                                        arrayList10 = arrayList3;
                                                        arrayList10.add(new IRect(str, false, rect3));

                                                        arrayList9 = arrayList2;

                                                        break;
                                                    }
                                                } else {
                                                    Log.e("比例不对", f2 + "=====" + predict);
                                                    break;
                                                }
                                        }

                                    }
                                }
                            }
                            arrayList10 = arrayList3;

                        } else {
                            z2 = z;
                            arrayList = arrayList11;
                            it2 = it;
                        }
                        arrayList2 = arrayList9;
                        i2 = i;
                        arrayList9 = arrayList2;
                        it = it2;
                        arrayList11 = arrayList;
                        z = z2;
                    } else {
                        ArrayList<String> arrayList12 = arrayList11;
                        if (i != 0) {
                            HashMap hashMap = new HashMap();
                            for (String str2 : arrayList12) {
                                hashMap.put(str2, str2);
                            }
                            switch (arrayList12.size()) {
                                case 1:
                                    if (((IRect) arrayList10.get(0)).getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList10.get(0);
                                    IRect iRect3 = (IRect) arrayList10.get(1);
                                    if (hashMap.size() > 1 && arrayList12.contains("王")) {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.JJRules$$ExternalSyntheticLambda0
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj) {
                                                    return JJRules.lambda$Enable$0((String) obj);
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
                                            arrayList12.removeAll((List) arrayList12.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.JJRules$$ExternalSyntheticLambda1
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj) {
                                                    return JJRules.lambda$Enable$1((String) obj);
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
                            for (IRect iRect4 : arrayList10) {
                                arrayList13.add(Integer.valueOf(iRect4.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList13);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue = num.intValue() / mode.size();
                                for (IRect iRect5 : arrayList10) {
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
                        return;
                    }
                }
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
