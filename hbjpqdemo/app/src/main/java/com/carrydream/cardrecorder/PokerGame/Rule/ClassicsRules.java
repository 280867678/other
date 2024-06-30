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
public class ClassicsRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Code restructure failed: missing block: B:166:0x0430, code lost:
        if ("club".equals(r0) == false) goto L174;
     */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0487 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0201  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
//        Iterator it;
//        boolean z2 = false;
//        ArrayList arrayList = null;
//        Iterator it2 = null;
//        ArrayList arrayList2;
//        ArrayList arrayList3;
//        ArrayList arrayList4;
//        ArrayList arrayList5;
//        int i2 = i;
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
        List<MatOfPoint> listMatOfPoint = new ArrayList();
        Imgproc.findContours(mat3, listMatOfPoint, new Mat(), 1, 2);
        List<Rect> listRect = new ArrayList();
//        int size = arrayList6.size();
        Mat mat4 = null;
        Rect rect2 = null;
//        int i3 = 0;
//        int i4 = 0;

        Log.e("i3  116:", String.valueOf(listMatOfPoint.size()));


        for (int k = 0; k <listMatOfPoint.size(); k ++){
            Rect boundingRect = Imgproc.boundingRect(listMatOfPoint.get(k));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
//            ArrayList arrayList8 = arrayList6;
//            int i5 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i == 0 || f <= 5.0f) && f >= 0.3d)) {
                listRect.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i6 = boundingRect.width * boundingRect.height;
                Point tl = boundingRect.tl();
                if (i != 0) {
//                    arrayList5 = arrayList7;
                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i6 > 0) {
//                        i4 = i6;
                        mat4 = new Mat(mat3, boundingRect);
                        rect2 = boundingRect;
                    }
//                    i3++;
//                    arrayList6 = arrayList8;
//                    size = i5;
//                    arrayList7 = arrayList5;
                } else if ((boundingRect.width * boundingRect.height) > 0) {
//                    i4 = i6;
                    mat4 = new Mat(mat3, boundingRect);
                    rect2 = boundingRect;
                }
            }
//            arrayList5 = arrayList7;
//            i3++;
//            arrayList6 = arrayList8;
//            size = i5;
//            arrayList7 = arrayList5;
        }

//        while (i3 < size) {
//            Rect boundingRect = Imgproc.boundingRect(arrayList6.get(i3));
//            float f = (boundingRect.width * 1.0f) / boundingRect.height;
////            ArrayList arrayList8 = arrayList6;
//            int i5 = size;
//            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i == 0 || f <= 5.0f) && f >= 0.3d)) {
//                arrayList7.add(boundingRect);
//                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
//                int i6 = boundingRect.width * boundingRect.height;
//                Point tl = boundingRect.tl();
//                if (i != 0) {
////                    arrayList5 = arrayList7;
//                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i6 > i4) {
//                        i4 = i6;
//                        mat4 = new Mat(mat3, boundingRect);
//                        rect2 = boundingRect;
//                    }
//                    i3++;
////                    arrayList6 = arrayList8;
//                    size = i5;
////                    arrayList7 = arrayList5;
//                } else if (i6 > i4) {
//                    i4 = i6;
//                    mat4 = new Mat(mat3, boundingRect);
//                    rect2 = boundingRect;
//                }
//            }
////            arrayList5 = arrayList7;
//            i3++;
////            arrayList6 = arrayList8;
//            size = i5;
////            arrayList7 = arrayList5;
//        }
//        ArrayList arrayList9 = arrayList7;
        mat2.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        Tool.saveBitmapToFile(activity, createBitmap, i + "_", "框");
        Log.e("i  116:", String.valueOf(i));
        if (mat4 == null) {
//            Log.e("i  1120:", String.valueOf(i));
            return;
        }

        if (i == 1 && rect2.br().x < bitmap.getWidth() * 0.5d) {
            return;
        }
        if (i == 2 && rect2.x > bitmap.getWidth() * 0.5d) {
            return;
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(mat4.cols(), mat4.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat4, createBitmap2);
        Tool.saveBitmapToFile(activity, createBitmap2, i + "_", i + "Max");
        Log.e("i  169:", String.valueOf(i));
        if (i != 0) {
//            if ((0 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
//                return;
//            }
            Log.e("isExist :", String.valueOf(rect2.height > bitmap.getHeight() * 0.5d));
            if (rect2.height > bitmap.getHeight() * 0.5d) {
                z = true;
                List<IRect> arrayList10 = new ArrayList();
                List<String> arrayList11 = new ArrayList();
//                it = arrayList7.iterator();
//                for (Rect it:arrayList7) {
//                    if (Tool.isExist(rect2, it)) {
//                        Mat mat5 = new Mat(mat3, it);
//                        float f2 = (it.width * 1.0f) / it.height;
//                        Utils.matToBitmap(mat5, Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888));
////                        if (i != 0) {
////                            arrayList3 = arrayList10;
////                            arrayList4 = arrayList11;
////                            if (it.br().y > rect2.y + (rect2.height * 0.65d) || rect2.br().y - it.br().y < it.height) {
////                                arrayList10 = arrayList3;
////                                arrayList11 = arrayList4;
////                            }
////                        } else {
////                            arrayList3 = arrayList10;
////                            arrayList4 = arrayList11;
////                        }
//                        String predict = Tool.predict(mat5);
////                        Iterator it3 = it;
//                        if (it.br().x <= rect2.br().x - ((it.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS) || predict.equals("K") || predict.equals("Q")) {
//                            if (predict.equals("king")) {
//                                predict = "王";
//                            }
//                            if (predict.equals("10") || predict.equals("王") || predict.equals("J") || it.width * it.height >= 400) {
//                                if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict) || "heart".equals(predict)) {
//                                    arrayList10 = arrayList3;
//                                    z2 = z;
//                                    it2 = it3;
//                                } else {
//                                    it2 = it3;
//                                    if (!"club".equals(predict)) {
//                                        predict.hashCode();
//                                        char c = 65535;
//                                        switch (predict.hashCode()) {
//                                            case 74:
//                                                if (predict.equals("J")) {
//                                                    c = 0;
//                                                    break;
//                                                }
//                                                break;
//                                            case 75:
//                                                if (predict.equals("K")) {
//                                                    c = 1;
//                                                    break;
//                                                }
//                                                break;
//                                            case 87:
//                                                if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
//                                                    c = 2;
//                                                    break;
//                                                }
//                                                break;
//                                            case 1567:
//                                                if (predict.equals("10")) {
//                                                    c = 3;
//                                                    break;
//                                                }
//                                                break;
//                                            case 29579:
//                                                if (predict.equals("王")) {
//                                                    c = 4;
//                                                    break;
//                                                }
//                                                break;
//                                        }
//                                        switch (c) {
//                                            case 0:
//                                                z2 = z;
////                                                    arrayList2 = arrayList9;
//                                                Mat lately = Tool.lately(mat3, rect2, it, arrayList7);
//                                                if (lately != null) {
//                                                    if (!Tool.predict(lately).equals("O")) {
//                                                        str = "J";
//                                                    }
//                                                    arrayList = arrayList4;
//                                                    arrayList.add(str);
//                                                    arrayList10 = arrayList3;
//                                                    arrayList10.add(new IRect(str, false, it));
//                                                    i2 = i;
////                                                        arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                    z = z2;
//                                                    break;
//                                                }
//                                                arrayList10 = arrayList3;
//                                                i2 = i;
////                                                    arrayList9 = arrayList2;
//                                                arrayList11 = arrayList4;
//                                                it = it2;
//                                                z = z2;
//                                            case 1:
////                                                    arrayList2 = arrayList9;
//                                                z2 = z;
//                                                if (it.y >= rect2.y + (it.width * 0.5d)) {
//                                                    Mat lately2 = Tool.lately(mat3, rect2, it, arrayList7);
//                                                    if (lately2 != null) {
//                                                        Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
//                                                        String predict2 = Tool.predict(lately2);
//                                                        if (!"diamond".equals(predict2)) {
//                                                            if (!"spade".equals(predict2)) {
//                                                                if (!"heart".equals(predict2)) {
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                    arrayList10 = arrayList3;
//                                                    i2 = i;
////                                                        arrayList9 = arrayList2;
//                                                    arrayList11 = arrayList4;
//                                                    it = it2;
//                                                    z = z2;
//                                                    break;
//                                                }
//                                                str = predict;
//                                                arrayList = arrayList4;
//                                                arrayList.add(str);
//                                                arrayList10 = arrayList3;
//                                                arrayList10.add(new IRect(str, false, it));
//                                                i2 = i;
////                                                    arrayList9 = arrayList2;
//                                                it = it2;
//                                                arrayList11 = arrayList;
//                                                z = z2;
//                                            case 2:
////                                                    arrayList2 = arrayList9;
//                                                z2 = z;
//                                                arrayList = arrayList4;
//                                                arrayList.add(str);
//                                                arrayList10 = arrayList3;
//                                                arrayList10.add(new IRect(str, false, it));
//                                                i2 = i;
////                                                    arrayList9 = arrayList2;
//                                                it = it2;
//                                                arrayList11 = arrayList;
//                                                z = z2;
//                                                break;
//                                            case 3:
////                                                    arrayList2 = arrayList9;
//                                                if (f2 >= 0.3f) {
//                                                    z2 = z;
//                                                    str = predict;
//                                                    arrayList = arrayList4;
//                                                    arrayList.add(str);
//                                                    arrayList10 = arrayList3;
//                                                    arrayList10.add(new IRect(str, false, it));
//                                                    i2 = i;
////                                                        arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                    z = z2;
//                                                    break;
//                                                } else {
//                                                    Log.e("比例不对10", f2 + "");
//                                                    arrayList10 = arrayList3;
////                                                        arrayList9 = arrayList2;
//                                                    arrayList11 = arrayList4;
//                                                    it = it2;
//                                                    break;
//                                                }
//                                            case 4:
////                                                    arrayList2 = arrayList9;
//                                                Mat lately3 = Tool.lately(mat3, rect2, it, arrayList7);
//                                                if (lately3 == null) {
//                                                    arrayList10 = arrayList3;
////                                                        arrayList9 = arrayList2;
//                                                    arrayList11 = arrayList4;
//                                                    it = it2;
//                                                    break;
//                                                } else {
//                                                    Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
//                                                    if (!Tool.predict(lately3).equals("O")) {
//                                                        str = "J";
//                                                    }
//                                                    z2 = z;
//                                                    arrayList = arrayList4;
//                                                    arrayList.add(str);
//                                                    arrayList10 = arrayList3;
//                                                    arrayList10.add(new IRect(str, false, it));
//                                                    i2 = i;
////                                                        arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                    z = z2;
//                                                    break;
//                                                }
//                                            default:
//                                                if (f2 < 0.5f || f2 > 1.0f) {
//                                                    Log.e("比例不对", f2 + "=====" + predict);
//                                                } else if (i2 == 0 || it.y <= (it.height * 0.5d) + rect2.y || z) {
//                                                    z2 = z;
////                                                        arrayList2 = arrayList9;
//                                                    str = predict;
//                                                    arrayList = arrayList4;
//                                                    arrayList.add(str);
//                                                    arrayList10 = arrayList3;
//                                                    arrayList10.add(new IRect(str, false, it));
//                                                    i2 = i;
////                                                        arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                    z = z2;
//                                                    break;
//                                                }
//                                                arrayList10 = arrayList3;
//                                                arrayList11 = arrayList4;
//                                                it = it2;
//                                                break;
//                                        }
//                                    } else {
//                                        arrayList10 = arrayList3;
//                                        z2 = z;
//                                    }
//                                }
//                                arrayList = arrayList4;
//                            }
//                        }
//                        arrayList10 = arrayList3;
//                        it = it3;
//                        arrayList11 = arrayList4;
//                    }
//                }


                for (Rect it : listRect) {
                    String str = "王";
                    if (it != null) {
                        Rect rect3 = it;

                        if (Tool.isExist(rect2, rect3)) {
                            Mat mat5 = new Mat(mat3, rect3);
                            float f2 = (rect3.width * 1.0f) / rect3.height;
                            Utils.matToBitmap(mat5, Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888));
//                            if (i != 0) {
//                                arrayList3 = arrayList10;
//                                arrayList4 = arrayList11;
//                                if (rect3.br().y > rect2.y + (rect2.height * 0.65d) || rect2.br().y - rect3.br().y < rect3.height) {
//                                    arrayList10 = arrayList3;
//                                    arrayList11 = arrayList4;
//                                }
//                            } else {
//                                arrayList3 = arrayList10;
//                                arrayList4 = arrayList11;
//                            }
                            String predict = Tool.predict(mat5);
                            Log.e("predict :", String.valueOf(predict));
//                            Iterator it3 = it;
                            if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS) || predict.equals("K") || predict.equals("Q")) {
                                if (predict.equals("king")) {
                                    predict = "王";
                                }
                                if (predict.equals("10") || predict.equals("王") || predict.equals("J") || rect3.width * rect3.height >= 400) {
                                    if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict) || "heart".equals(predict)) {
//                                        arrayList10 = arrayList3;
//                                        z2 = z;
//                                        it2 = it3;
                                    } else {
//                                        it2 = it3;

                                        char c = 65535;
                                        Log.e("predict 395:", predict);
                                        if (!"club".equals(predict)) {
                                            if (predict.equals("J")) {
                                                c = 0;
                                            }
                                            if (predict.equals("K")) {
                                                c = 1;
                                            }
                                            if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                                c = 2;
                                            }
                                            if (predict.equals("10")) {
                                                c = 3;
                                            }
                                            if (predict.equals("王")) {
                                                c = 4;
                                            }

                                        }
                                        switch (c) {
                                            case 0:
//                                                z2 = z;
//                                                    arrayList2 = arrayList9;
                                                Mat lately = Tool.lately(mat3, rect2, rect3, listRect);
                                                if (lately != null) {
                                                    if (!Tool.predict(lately).equals("O")) {
                                                        str = "J";
                                                    }
//                                                        arrayList = arrayList4;
                                                    Log.e("arrayList11 424:",str);
                                                    arrayList11.add(str);
//                                                        arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));
//                                                        i2 = i;
//                                                        arrayList9 = arrayList2;
//                                                        it = it2;
//                                                        arrayList11 = arrayList;
//                                                    z = z2;
                                                    break;
                                                }
//                                                    arrayList10 = arrayList3;
//                                                    i2 = i;
//                                                    arrayList9 = arrayList2;
//                                                    arrayList11 = arrayList4;
//                                                    it = it2;
//                                                z = z2;
                                            case 1:
//                                                    arrayList2 = arrayList9;
//                                                z2 = z;
                                                if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                    Mat lately2 = Tool.lately(mat3, rect2, rect3, listRect);
                                                    if (lately2 != null) {
                                                        Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                        String predict2 = Tool.predict(lately2);
                                                        if (!"diamond".equals(predict2)) {
                                                            if (!"spade".equals(predict2)) {
                                                                if (!"heart".equals(predict2)) {
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
//                                                        arrayList10 = arrayList3;
//                                                        i2 = i;
//                                                        arrayList9 = arrayList2;
//                                                        arrayList11 = arrayList4;
//                                                        it = it2;
//                                                    z = z2;
                                                    break;
                                                }
                                                str = predict;
//                                                    arrayList = arrayList4;
                                                arrayList11.add(str);
                                                Log.e("arrayList11 468:",str);
//                                                    arrayList10 = arrayList3;
                                                arrayList10.add(new IRect(str, false, rect3));
//                                                    i2 = i;
//                                                    arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                z = z2;
                                            case 2:
//                                                    arrayList2 = arrayList9;
//                                                z2 = z;
//                                                    arrayList = arrayList4;
                                                arrayList11.add(str);
                                                Log.e("arrayList11 481:",str);
//                                                    arrayList10 = arrayList3;
                                                arrayList10.add(new IRect(str, false, rect3));
//                                                    i2 = i;
//                                                    arrayList9 = arrayList2;
//                                                    it = it2;
//                                                    arrayList11 = arrayList;
//                                                z = z2;
                                                break;
                                            case 3:
//                                                    arrayList2 = arrayList9;
                                                if (f2 >= 0.3f) {
//                                                    z2 = z;
                                                    str = predict;
//                                                        arrayList = arrayList4;
                                                    arrayList11.add(str);
                                                    Log.e("arrayList11 497:",str);
//                                                        arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));
//                                                        i2 = i;
//                                                        arrayList9 = arrayList2;
//                                                        it = it2;
//                                                        arrayList11 = arrayList;
//                                                    z = z2;
                                                    break;
                                                } else {
                                                    Log.e("比例不对10", f2 + "");
//                                                        arrayList10 = arrayList3;
//                                                        arrayList9 = arrayList2;
//                                                        arrayList11 = arrayList4;
//                                                        it = it2;
                                                    break;
                                                }
                                            case 4:
//                                                    arrayList2 = arrayList9;
                                                Mat lately3 = Tool.lately(mat3, rect2, rect3, listRect);
                                                if (lately3 == null) {
//                                                        arrayList10 = arrayList3;
//                                                        arrayList9 = arrayList2;
//                                                        arrayList11 = arrayList4;
//                                                        it = it2;
                                                    break;
                                                } else {
                                                    Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                    if (!Tool.predict(lately3).equals("O")) {
                                                        str = "J";
                                                    }
//                                                    z2 = z;
//                                                        arrayList = arrayList4;
                                                    arrayList11.add(str);
                                                    Log.e("arrayList11 531:",str);
//                                                        arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));
//                                                        i2 = i;
//                                                        arrayList9 = arrayList2;
//                                                        it = it2;
//                                                        arrayList11 = arrayList;
//                                                    z = z2;
                                                    break;
                                                }
                                            default:
                                                if (f2 < 0.5f || f2 > 1.0f) {
                                                    Log.e("比例不对", f2 + "=====" + predict);
                                                } else if (i == 0 || rect3.y <= (rect3.height * 0.5d) + rect2.y || z) {
//                                                    z2 = z;
//                                                        arrayList2 = arrayList9;
                                                    str = predict;
//                                                        arrayList = arrayList4;
                                                    arrayList11.add(str);
                                                    Log.e("arrayList11 550:",str);
//                                                        arrayList10 = arrayList3;
                                                    arrayList10.add(new IRect(str, false, rect3));
//                                                        i2 = i;
//                                                        arrayList9 = arrayList2;
//                                                        it = it2;
//                                                        arrayList11 = arrayList;
//                                                    z = z2;
                                                    break;
                                                }
//                                                    arrayList10 = arrayList3;
//                                                    arrayList11 = arrayList4;
//                                                    it = it2;
                                                break;
                                        }

//                                 else{
//                                            arrayList10 = arrayList3;
//                                    z2 = z;
//                                        }
                                    }
//                                    arrayList = arrayList4;
                                }
                            }
//                            arrayList10 = arrayList3;
//                            it = it3;
//                            arrayList11 = arrayList4;
                        } else {
//                    z2 = z;
//                            arrayList = arrayList11;
//                            it2 = it;
                        }
//                        arrayList2 = arrayList9;
//                        i2 = i;
//                        arrayList9 = arrayList2;
//                        it = it2;
//                        arrayList11 = arrayList;
//                z = z2;
                        onOCRListener.play(i, Tool.sort(arrayList11));
                    } else {
//                        ArrayList<String> arrayList12 = arrayList11;

                        Log.e("最后", arrayList11.toString());
                        if (i != 0) {
                            Log.e("最后", arrayList11.toString());
                            HashMap hashMap = new HashMap();
                            for (String str2 : arrayList11) {
                                hashMap.put(str2, str2);
                            }
                            switch (arrayList11.size()) {
                                case 1:
                                    IRect iRect = (IRect) arrayList10.get(0);
                                    if (iRect.getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    } else if (iRect.getRect().y > rect2.y + iRect.getRect().height) {
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList10.get(0);
                                    IRect iRect3 = (IRect) arrayList10.get(1);
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
                                        Log.e("不是炸或者3带1", "不符合规则" + arrayList11.toString());
                                        return;
                                    }
                                    break;
                                case 5:
                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者3带对", "不符合规则" + arrayList11.toString());
                                        return;
                                    }
                                    break;
                                case 6:
                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList11.toString());
                                        return;
                                    }
                                    break;
                            }
                        } else {
                            Log.e("自己", arrayList11.toString());
                            if (arrayList11.size() < DataUtils.getInstance().getHand()) {
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
                                        if (!iRect5.getName().equals("王")) {
                                            arrayList11.remove(iRect5.getName());
                                        }
                                        Log.e("自己的牌里面差别较大的", iRect5.toString());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i, arrayList11.toString());
                        onOCRListener.play(i, Tool.sort(arrayList11));
                        return;
                    }
                }
            }
        }
//        z = false;
//        ArrayList<IRect> arrayList102 = new ArrayList();
//        ArrayList arrayList112 = new ArrayList();
//        it = arrayList7.iterator();
//        while (true) {
//            String str3 = "王";
//            if (!it.hasNext()) {
//            }
//        }
    }
}
