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
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class HuanleTuyouLeRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:186:0x04db  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0629  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0204  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
        Iterator it;
        int i2;
        boolean z2 = false;
        ArrayList arrayList = null;
        Iterator it2 = null;
        Mat mat = null;
        ArrayList arrayList2 = null;
        ArrayList arrayList3 = null;
        Mat mat2;
        ArrayList arrayList4;
        ArrayList arrayList5;
        ArrayList arrayList6;
        int i3 = i;
        Mat mat3 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat3);
        Mat clone = mat3.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect = new Rect();
        rect.width = width;
        rect.height = height;
        rect.x = 0;
        rect.y = (height / 2) - (rect.height / 2);
        Mat mat4 = new Mat(clone, rect);
        Mat clone2 = mat4.clone();
        Imgproc.cvtColor(mat4, mat4, 11);
        Mat mat5 = new Mat(mat3.rows(), mat3.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat4, mat5, 100, 255.0d, 0);
        ArrayList arrayList7 = new ArrayList();
        Imgproc.findContours(mat5, arrayList7, new Mat(), 1, 2);
        ArrayList arrayList8 = new ArrayList();
        int size = arrayList7.size();
        Mat mat6 = null;
        Rect rect2 = null;
        int i4 = 0;
        int i5 = 0;
        while (i4 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList7.get(i4));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            ArrayList arrayList9 = arrayList7;
            int i6 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i3 == 0 || f <= 5.0f) && f >= 0.3d)) {
                arrayList8.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i7 = boundingRect.width * boundingRect.height;
                Point tl = boundingRect.tl();
                if (i3 != 0) {
                    arrayList6 = arrayList8;
                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i7 > i5) {
                        i5 = i7;
                        mat6 = new Mat(mat5, boundingRect);
                        rect2 = boundingRect;
                    }
                    i4++;
                    arrayList7 = arrayList9;
                    size = i6;
                    arrayList8 = arrayList6;
                } else if (i7 > i5) {
                    i5 = i7;
                    mat6 = new Mat(mat5, boundingRect);
                    rect2 = boundingRect;
                }
            }
            arrayList6 = arrayList8;
            i4++;
            arrayList7 = arrayList9;
            size = i6;
            arrayList8 = arrayList6;
        }
        ArrayList arrayList10 = arrayList8;
        mat4.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        Tool.saveBitmapToFile(activity, createBitmap, i3 + "_", "框");
        if (mat6 == null) {
            return;
        }
        if (i3 != 1 || rect2.br().x >= bitmap.getWidth() * 0.7d) {
            if (i3 != 2 || rect2.x <= bitmap.getWidth() * 0.3d) {
                Bitmap createBitmap2 = Bitmap.createBitmap(mat6.cols(), mat6.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat6, createBitmap2);
                Tool.saveBitmapToFile(activity, createBitmap2, i3 + "_", i3 + "Max");
                if (i3 != 0) {
                    if ((i5 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
                        return;
                    }
                    if (rect2.height > bitmap.getHeight() * 0.5d) {
                        z = true;
                        ArrayList arrayList11 = new ArrayList();
                        ArrayList arrayList12 = new ArrayList();
                        it = arrayList10.iterator();
                        while (it.hasNext()) {
                            Rect rect3 = (Rect) it.next();
                            if (Tool.isExist(rect2, rect3)) {
                                Mat mat7 = new Mat(mat5, rect3);
                                float f2 = (rect3.width * 1.0f) / rect3.height;
                                Utils.matToBitmap(mat7, Bitmap.createBitmap(mat7.cols(), mat7.rows(), Bitmap.Config.ARGB_8888));
                                if (i3 != 0) {
                                    mat2 = mat5;
                                    if (rect3.br().y <= rect2.y + (rect2.height * 0.65d)) {
                                        if (rect2.br().y - rect3.br().y < rect3.height) {
                                        }
                                    }
                                    mat5 = mat2;
                                } else {
                                    mat2 = mat5;
                                }
                                String predict = Tool.predict(mat7);
                                if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                                    String str = "王";
                                    if (predict.equals("king")) {
                                        predict = "王";
                                    }
                                    if (rect3.width * rect3.height <= 5500 && (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect3.width * rect3.height >= 400)) {
                                        if (!"X".equals(predict) && !"O".equals(predict) && !"diamond".equals(predict) && !"spade".equals(predict) && !"heart".equals(predict) && !"club".equals(predict)) {
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
                                                    arrayList4 = arrayList11;
                                                    arrayList5 = arrayList12;
                                                    mat = mat2;
                                                    arrayList2 = arrayList10;
                                                    it2 = it;
                                                    Mat lately = Tool.lately(mat, rect2, rect3, arrayList2);
                                                    if (lately == null) {
                                                        i3 = i;
                                                        arrayList10 = arrayList2;
                                                        mat5 = mat;
                                                        it = it2;
                                                        arrayList11 = arrayList4;
                                                        arrayList12 = arrayList5;
                                                        z = z2;
                                                        break;
                                                    } else {
                                                        String predict2 = Tool.predict(lately);
                                                        if (predict2.equals("diamond") || predict2.equals("spade") || predict2.equals("heart") || predict2.equals("club")) {
                                                            str = "J";
                                                        }
                                                        arrayList = arrayList5;
                                                        arrayList.add(str);
                                                        arrayList3 = arrayList4;
                                                        arrayList3.add(new IRect(str, false, rect3));
                                                        break;
                                                    }
                                                case 1:
                                                    z2 = z;
                                                    mat = mat2;
                                                    arrayList2 = arrayList10;
                                                    it2 = it;
                                                    arrayList4 = arrayList11;
                                                    arrayList5 = arrayList12;
                                                    if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                        Mat lately2 = Tool.lately(mat, rect2, rect3, arrayList2);
                                                        if (lately2 != null) {
                                                            Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                            String predict3 = Tool.predict(lately2);
                                                            if (!"diamond".equals(predict3) && !"spade".equals(predict3) && !"heart".equals(predict3) && !"club".equals(predict3)) {
                                                            }
                                                        }
                                                        i3 = i;
                                                        arrayList10 = arrayList2;
                                                        mat5 = mat;
                                                        it = it2;
                                                        arrayList11 = arrayList4;
                                                        arrayList12 = arrayList5;
                                                        z = z2;
                                                        break;
                                                    }
                                                    str = predict;
                                                    arrayList = arrayList5;
                                                    arrayList.add(str);
                                                    arrayList3 = arrayList4;
                                                    arrayList3.add(new IRect(str, false, rect3));
                                                    break;
                                                case 2:
                                                    z2 = z;
                                                    mat = mat2;
                                                    arrayList2 = arrayList10;
                                                    it2 = it;
                                                    arrayList4 = arrayList11;
                                                    arrayList = arrayList12;
                                                    arrayList.add(str);
                                                    arrayList3 = arrayList4;
                                                    arrayList3.add(new IRect(str, false, rect3));
                                                    break;
                                                case 3:
                                                    z2 = z;
                                                    mat = mat2;
                                                    arrayList2 = arrayList10;
                                                    it2 = it;
                                                    if (f2 >= 0.3f) {
                                                        arrayList4 = arrayList11;
                                                        arrayList5 = arrayList12;
                                                        str = predict;
                                                        arrayList = arrayList5;
                                                        arrayList.add(str);
                                                        arrayList3 = arrayList4;
                                                        arrayList3.add(new IRect(str, false, rect3));
                                                        break;
                                                    } else {
                                                        Log.e("比例不对10", f2 + "");
                                                        arrayList10 = arrayList2;
                                                        mat5 = mat;
                                                        it = it2;
                                                        z = z2;
                                                        break;
                                                    }
                                                case 4:
                                                    mat = mat2;
                                                    ArrayList arrayList13 = arrayList10;
                                                    Mat lately3 = Tool.lately(mat, rect2, rect3, arrayList13);
                                                    if (lately3 == null) {
                                                        arrayList10 = arrayList13;
                                                        mat5 = mat;
                                                        break;
                                                    } else {
                                                        z2 = z;
                                                        it2 = it;
                                                        Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict4 = Tool.predict(lately3);
                                                        str = (predict4.equals("diamond") || predict4.equals("spade") || predict4.equals("heart") || predict4.equals("club")) ? "J" : "J";
                                                        arrayList4 = arrayList11;
                                                        arrayList = arrayList12;
                                                        arrayList2 = arrayList13;
                                                        arrayList.add(str);
                                                        arrayList3 = arrayList4;
                                                        arrayList3.add(new IRect(str, false, rect3));
                                                        break;
                                                    }
                                                default:
                                                    if (f2 >= 0.5f && f2 <= 1.0f) {
                                                        if (i3 != 0 && rect3.y > rect3.height + rect2.y && !z) {
                                                            break;
                                                        } else {
                                                            z2 = z;
                                                            arrayList4 = arrayList11;
                                                            arrayList5 = arrayList12;
                                                            mat = mat2;
                                                            arrayList2 = arrayList10;
                                                            it2 = it;
                                                            str = predict;
                                                            arrayList = arrayList5;
                                                            arrayList.add(str);
                                                            arrayList3 = arrayList4;
                                                            arrayList3.add(new IRect(str, false, rect3));
                                                            break;
                                                        }
                                                    } else {
                                                        Log.e("比例不对", f2 + "=====" + predict);
                                                        break;
                                                    }
                                            }
                                        } else {
                                            z2 = z;
                                            arrayList = arrayList12;
                                            mat = mat2;
                                            arrayList2 = arrayList10;
                                            arrayList3 = arrayList11;
                                            it2 = it;
                                        }
                                    }
                                }
                                mat5 = mat2;
                            } else {
                                z2 = z;
                                arrayList = arrayList12;
                                it2 = it;
                                mat = mat5;
                                arrayList2 = arrayList10;
                                arrayList3 = arrayList11;
                            }
                            arrayList10 = arrayList2;
                            arrayList11 = arrayList3;
                            mat5 = mat;
                            it = it2;
                            z = z2;
                            arrayList12 = arrayList;
                            i3 = i;
                        }
                        ArrayList<IRect> arrayList14 = arrayList11;
                        i2 = i3;
                        ArrayList<String> arrayList15 = arrayList12;
                        if (i2 == 0) {
                            HashMap hashMap = new HashMap();
                            for (String str2 : arrayList15) {
                                hashMap.put(str2, str2);
                            }
                            switch (arrayList15.size()) {
                                case 1:
                                    IRect iRect = (IRect) arrayList14.get(0);
                                    if (iRect.getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    } else if (iRect.getRect().y > rect2.y + iRect.getRect().height) {
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList14.get(0);
                                    IRect iRect3 = (IRect) arrayList14.get(1);
                                    if (hashMap.size() > 1 || Math.abs(iRect2.getRect().br().y - iRect3.getRect().br().y) > 4.0d) {
                                        return;
                                    }
                                    break;
                                case 3:
                                    if (hashMap.size() > 1) {
                                        return;
                                    }
                                    break;
                                case 4:
                                    if (hashMap.size() >= 3) {
                                        Log.e("不是炸或者3带1", "不符合规则" + arrayList15.toString());
                                        return;
                                    }
                                    break;
                                case 5:
                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者3带对", "不符合规则" + arrayList15.toString());
                                        return;
                                    }
                                    break;
                                case 6:
                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList15.toString());
                                        return;
                                    }
                                    break;
                            }
                        } else {
                            Log.e("自己", arrayList15.toString());
                            if (arrayList15.size() < DataUtils.getInstance().getHand()) {
                                return;
                            }
                            ArrayList arrayList16 = new ArrayList();
                            for (IRect iRect4 : arrayList14) {
                                arrayList16.add(Integer.valueOf(iRect4.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList16);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue = num.intValue() / mode.size();
                                for (IRect iRect5 : arrayList14) {
                                    double d = intValue;
                                    if (iRect5.getRect().y < 0.9d * d || iRect5.getRect().y > d * 1.1d) {
                                        arrayList15.remove(iRect5.getName());
                                        Log.e("自己的牌里面差别较大的", iRect5.getName());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i2, arrayList15.toString());
                        onOCRListener.play(i2, Tool.sort(arrayList15));
                    }
                }
                z = false;
                ArrayList arrayList112 = new ArrayList();
                ArrayList arrayList122 = new ArrayList();
                it = arrayList10.iterator();
                while (it.hasNext()) {
                }
                ArrayList<IRect> arrayList142 = arrayList112;
                i2 = i3;
                ArrayList<String> arrayList152 = arrayList122;
                if (i2 == 0) {
                }
                Log.e("出牌" + i2, arrayList152.toString());
                onOCRListener.play(i2, Tool.sort(arrayList152));
            }
        }
    }
}
