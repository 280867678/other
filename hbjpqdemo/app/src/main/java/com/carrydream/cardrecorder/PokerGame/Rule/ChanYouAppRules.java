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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class ChanYouAppRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:123:0x03b9  */
    /* JADX WARN: Removed duplicated region for block: B:219:0x03e4 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, int i2, int i3, int i4, OnOCRListener onOCRListener) {
        Rect rect;
        Iterator it;
        Mat mat;
        Rect rect2;
        ArrayList arrayList;
        Mat mat2 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat2);
        Mat clone = mat2.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect3 = new Rect();
        rect3.width = width;
        rect3.height = height;
        rect3.x = 0;
        rect3.y = (height / 2) - (rect3.height / 2);
        Mat mat3 = new Mat(clone, rect3);
        Mat clone2 = mat3.clone();
        Imgproc.cvtColor(mat3, mat3, 11);
        Mat mat4 = new Mat(mat2.rows(), mat2.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat3, mat4, 100, 255.0d, 0);
        ArrayList<MatOfPoint> arrayList2 = new ArrayList();
        Imgproc.findContours(mat4, arrayList2, new Mat(), 1, 2);
        ArrayList arrayList3 = new ArrayList();
        String str = "_";
        if (i != 0) {
            for (MatOfPoint matOfPoint : arrayList2) {
                Rect boundingRect = Imgproc.boundingRect(matOfPoint);
                String str2 = str;
                if (boundingRect.tl().y >= i2 - 5 && boundingRect.br().y <= i4 + 5 && boundingRect.height >= bitmap.getHeight() * 0.05d) {
                    arrayList3.add(boundingRect);
                    Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    mat3.release();
                }
                str = str2;
            }
            Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap);
            Tool.saveBitmapToFile(activity, createBitmap, i + str, "框框");
            rect = null;
        } else {
            int size = arrayList2.size();
            Mat mat5 = null;
            rect = null;
            int i5 = 0;
            int i6 = 0;
            while (i5 < size) {
                Rect boundingRect2 = Imgproc.boundingRect((MatOfPoint) arrayList2.get(i5));
                ArrayList arrayList4 = arrayList2;
                float f = (boundingRect2.width * 1.0f) / boundingRect2.height;
                int i7 = size;
                Mat mat6 = mat5;
                if (boundingRect2.height >= bitmap.getHeight() * 0.05d && f >= 0.3d) {
                    arrayList3.add(boundingRect2);
                    Imgproc.rectangle(clone2, boundingRect2.tl(), boundingRect2.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    int i8 = boundingRect2.width * boundingRect2.height;
                    boundingRect2.tl();
                    if (i8 > i6) {
                        i6 = i8;
                        mat5 = new Mat(mat4, boundingRect2);
                        rect = boundingRect2;
                        i5++;
                        size = i7;
                        arrayList2 = arrayList4;
                    }
                }
                mat5 = mat6;
                i5++;
                size = i7;
                arrayList2 = arrayList4;
            }
            Mat mat7 = mat5;
            mat3.release();
            Bitmap createBitmap2 = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap2);
            Tool.saveBitmapToFile(activity, createBitmap2, i + "_", "框");
            if (mat7 == null) {
                return;
            }
            Bitmap createBitmap3 = Bitmap.createBitmap(mat7.cols(), mat7.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat7, createBitmap3);
            Tool.saveBitmapToFile(activity, createBitmap3, i + "_", i + "Max");
        }
        ArrayList<IRect> arrayList5 = new ArrayList();
        ArrayList<String> arrayList6 = new ArrayList();
        Iterator it2 = arrayList3.iterator();
        while (it2.hasNext()) {
            Rect rect4 = (Rect) it2.next();
            if (i != 0 || Tool.isExist(rect, rect4)) {
                Mat mat8 = new Mat(mat4, rect4);
                float f2 = (rect4.width * 1.0f) / rect4.height;
                Utils.matToBitmap(mat8, Bitmap.createBitmap(mat8.cols(), mat8.rows(), Bitmap.Config.ARGB_8888));
                String predict = Tool.predict(mat8);
                if (predict.equals("king")) {
                    predict = "王";
                }
                if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict)) {
                    it = it2;
                } else {
                    it = it2;
                    if (!"spade".equals(predict)) {
                        rect2 = rect;
                        if (!"heart".equals(predict) && !"club".equals(predict)) {
                            predict.hashCode();
                            char c = 65535;
                            mat = mat8;
                            String str3 = "J";
                            switch (predict.hashCode()) {
                                case 74:
                                    arrayList = arrayList6;
                                    if (predict.equals("J")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 75:
                                    arrayList = arrayList6;
                                    if (predict.equals("K")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                                case 87:
                                    arrayList = arrayList6;
                                    if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 1567:
                                    arrayList = arrayList6;
                                    if (predict.equals("10")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 29579:
                                    if (predict.equals("王")) {
                                        arrayList = arrayList6;
                                        c = 4;
                                        break;
                                    }
                                default:
                                    arrayList = arrayList6;
                                    break;
                            }
                            switch (c) {
                                case 0:
                                    Mat lately = Tool.lately(mat4, null, rect4, arrayList3);
                                    if (lately != null && Tool.predict(lately).equals("O")) {
                                        str3 = "王";
                                    }
                                    predict = str3;
                                    if (i == 0) {
                                        int i9 = i3 - i2;
                                        int i10 = (int) (i9 * 0.661d);
                                        if (!predict.equals("王")) {
                                            if (rect4.width <= i10 + 5 && rect4.width >= i10 - 5 && rect4.height <= i9 + 5 && rect4.height >= i9 - 5) {
                                            }
                                            it2 = it;
                                            rect = rect2;
                                            arrayList6 = arrayList;
                                            break;
                                        }
                                    }
                                    arrayList6 = arrayList;
                                    arrayList6.add(predict);
                                    arrayList5.add(new IRect(predict, false, rect4));
                                    Bitmap createBitmap4 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat, createBitmap4);
                                    Tool.saveBitmapToFile(activity, createBitmap4, predict, i + "");
                                    it2 = it;
                                    rect = rect2;
                                    break;
                                case 1:
                                    if (i == 0) {
                                        Mat lately2 = Tool.lately(mat4, null, rect4, arrayList3);
                                        if (lately2 != null) {
                                            Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                            String predict2 = Tool.predict(lately2);
                                            if (!"diamond".equals(predict2) && !"spade".equals(predict2) && !"heart".equals(predict2) && !"club".equals(predict2)) {
                                            }
                                        }
                                        it2 = it;
                                        rect = rect2;
                                        arrayList6 = arrayList;
                                        break;
                                    }
                                    if (i == 0) {
                                    }
                                    arrayList6 = arrayList;
                                    arrayList6.add(predict);
                                    arrayList5.add(new IRect(predict, false, rect4));
                                    Bitmap createBitmap42 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat, createBitmap42);
                                    Tool.saveBitmapToFile(activity, createBitmap42, predict, i + "");
                                    it2 = it;
                                    rect = rect2;
                                    break;
                                case 2:
                                    if (isEnabled(arrayList5, rect4)) {
                                        it2 = it;
                                        rect = rect2;
                                        arrayList6 = arrayList;
                                        break;
                                    } else {
                                        predict = "王";
                                        if (i == 0) {
                                        }
                                        arrayList6 = arrayList;
                                        arrayList6.add(predict);
                                        arrayList5.add(new IRect(predict, false, rect4));
                                        Bitmap createBitmap422 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap422);
                                        Tool.saveBitmapToFile(activity, createBitmap422, predict, i + "");
                                        it2 = it;
                                        rect = rect2;
                                        break;
                                    }
                                case 3:
                                    if (f2 < 0.3f) {
                                        Log.e("比例不对10", f2 + "");
                                        it2 = it;
                                        rect = rect2;
                                        arrayList6 = arrayList;
                                        break;
                                    }
                                    if (i == 0) {
                                    }
                                    arrayList6 = arrayList;
                                    arrayList6.add(predict);
                                    arrayList5.add(new IRect(predict, false, rect4));
                                    Bitmap createBitmap4222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat, createBitmap4222);
                                    Tool.saveBitmapToFile(activity, createBitmap4222, predict, i + "");
                                    it2 = it;
                                    rect = rect2;
                                    break;
                                case 4:
                                    Mat lately3 = Tool.lately(mat4, null, rect4, arrayList3);
                                    if (lately3 != null) {
                                        Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                        if (Tool.predict(lately3).equals("O")) {
                                            str3 = "王";
                                        }
                                    }
                                    predict = str3;
                                    if (i == 0) {
                                    }
                                    arrayList6 = arrayList;
                                    arrayList6.add(predict);
                                    arrayList5.add(new IRect(predict, false, rect4));
                                    Bitmap createBitmap42222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat, createBitmap42222);
                                    Tool.saveBitmapToFile(activity, createBitmap42222, predict, i + "");
                                    it2 = it;
                                    rect = rect2;
                                    break;
                                default:
                                    if (f2 < 0.5f || f2 > 1.0f) {
                                        Log.e("比例不对", f2 + "=====" + predict);
                                        it2 = it;
                                        rect = rect2;
                                        arrayList6 = arrayList;
                                        break;
                                    }
                                    if (i == 0) {
                                    }
                                    arrayList6 = arrayList;
                                    arrayList6.add(predict);
                                    arrayList5.add(new IRect(predict, false, rect4));
                                    Bitmap createBitmap422222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat, createBitmap422222);
                                    Tool.saveBitmapToFile(activity, createBitmap422222, predict, i + "");
                                    it2 = it;
                                    rect = rect2;
                                    break;
                            }
                        } else {
                            mat = mat8;
                            Bitmap createBitmap4222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(mat, createBitmap4222222);
                            Tool.saveBitmapToFile(activity, createBitmap4222222, predict, i + "");
                            it2 = it;
                            rect = rect2;
                        }
                    }
                }
                mat = mat8;
                rect2 = rect;
                Bitmap createBitmap42222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, createBitmap42222222);
                Tool.saveBitmapToFile(activity, createBitmap42222222, predict, i + "");
                it2 = it;
                rect = rect2;
            }
        }
        if (i != 0) {
            Log.e("别人最后", arrayList6.toString());
            HashMap hashMap = new HashMap();
            for (String str4 : arrayList6) {
                hashMap.put(str4, str4);
            }
            int size2 = arrayList6.size();
            if (size2 == 2) {
                IRect iRect = (IRect) arrayList5.get(0);
                IRect iRect2 = (IRect) arrayList5.get(1);
                if (hashMap.size() > 1 || Math.abs(iRect.getRect().br().y - iRect2.getRect().br().y) > 4.0d) {
                    return;
                }
            } else if (size2 != 3) {
                if (size2 != 4) {
                    if (size2 == 5) {
                        if (hashMap.size() != 5 && hashMap.size() != 2) {
                            Log.e("不是顺子或者3带对", "不符合规则" + arrayList6.toString());
                            return;
                        }
                    } else if (size2 == 6 && hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList6.toString());
                        return;
                    }
                } else if (hashMap.size() >= 3) {
                    Log.e("不是炸或者3带1", "不符合规则" + arrayList6.toString());
                    return;
                }
            } else if (hashMap.size() > 1) {
                return;
            }
        } else {
            Log.e("自己", arrayList6.toString());
            if (arrayList6.size() < DataUtils.getInstance().getHand()) {
                return;
            }
            ArrayList arrayList7 = new ArrayList();
            for (IRect iRect3 : arrayList5) {
                arrayList7.add(Integer.valueOf(iRect3.getRect().y));
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Integer num = 0;
                List<Integer> mode = Tool.mode(arrayList7);
                for (Integer num2 : mode) {
                    num = Integer.valueOf(num.intValue() + num2.intValue());
                }
                int intValue = num.intValue() / mode.size();
                for (IRect iRect4 : arrayList5) {
                    double d = intValue;
                    if (iRect4.getRect().y < 0.9d * d || iRect4.getRect().y > d * 1.1d) {
                        arrayList6.remove(iRect4.getName());
                        Log.e("自己的牌里面差别较大的", iRect4.getName());
                    }
                }
            }
        }
        Log.e("出牌" + i, arrayList6.toString());
        onOCRListener.play(i, Tool.sort(arrayList6));
    }

    public static boolean isEnabled(List<IRect> list, Rect rect) {
        boolean z = false;
        for (IRect iRect : list) {
            if (iRect.getName().equals("王") && rect.x > iRect.getRect().x - 5 && rect.x < iRect.getRect().x + 5) {
                z = true;
            }
        }
        Log.e("isEnabled", z + "");
        return z;
    }
}
