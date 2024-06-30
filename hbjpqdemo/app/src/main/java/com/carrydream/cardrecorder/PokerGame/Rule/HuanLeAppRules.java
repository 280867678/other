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
public class HuanLeAppRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:127:0x03ce  */
    /* JADX WARN: Removed duplicated region for block: B:239:0x044f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, int i2, int i3, OnOCRListener onOCRListener) {
        int i4;
        Rect rect;
        Iterator it;
        Mat mat;
        Rect rect2;
        String str;
        String str2;
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
        ArrayList<MatOfPoint> arrayList = new ArrayList();
        Imgproc.findContours(mat4, arrayList, new Mat(), 1, 2);
        ArrayList arrayList2 = new ArrayList();
        String str3 = "_";
        if (i != 0) {
            for (MatOfPoint matOfPoint : arrayList) {
                Rect boundingRect = Imgproc.boundingRect(matOfPoint);
                String str4 = str3;
                if (boundingRect.tl().y >= i2 - 10 && boundingRect.br().y <= i3 + 10 && boundingRect.height >= bitmap.getHeight() * 0.05d) {
                    arrayList2.add(boundingRect);
                    Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    mat3.release();
                }
                str3 = str4;
            }
            Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap);
            Tool.saveBitmapToFile(activity, createBitmap, i + str3, "框框");
            i4 = i;
            rect = null;
        } else {
            int size = arrayList.size();
            Mat mat5 = null;
            Rect rect4 = null;
            int i5 = 0;
            int i6 = 0;
            while (i5 < size) {
                Rect boundingRect2 = Imgproc.boundingRect((MatOfPoint) arrayList.get(i5));
                ArrayList arrayList3 = arrayList;
                float f = (boundingRect2.width * 1.0f) / boundingRect2.height;
                Mat mat6 = mat5;
                Rect rect5 = rect4;
                if (boundingRect2.height >= bitmap.getHeight() * 0.05d && f >= 0.3d) {
                    arrayList2.add(boundingRect2);
                    Imgproc.rectangle(clone2, boundingRect2.tl(), boundingRect2.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    int i7 = boundingRect2.width * boundingRect2.height;
                    boundingRect2.tl();
                    if (i7 > i6) {
                        i6 = i7;
                        mat5 = new Mat(mat4, boundingRect2);
                        rect4 = boundingRect2;
                        i5++;
                        arrayList = arrayList3;
                    }
                }
                mat5 = mat6;
                rect4 = rect5;
                i5++;
                arrayList = arrayList3;
            }
            Mat mat7 = mat5;
            Rect rect6 = rect4;
            mat3.release();
            Bitmap createBitmap2 = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap2);
            StringBuilder sb = new StringBuilder();
            i4 = i;
            sb.append(i4);
            sb.append("_");
            Tool.saveBitmapToFile(activity, createBitmap2, sb.toString(), "框");
            if (mat7 == null) {
                return;
            }
            Bitmap createBitmap3 = Bitmap.createBitmap(mat7.cols(), mat7.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat7, createBitmap3);
            Tool.saveBitmapToFile(activity, createBitmap3, i4 + "_", i4 + "Max");
            rect = rect6;
        }
        ArrayList<IRect> arrayList4 = new ArrayList();
        ArrayList<String> arrayList5 = new ArrayList();
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            Rect rect7 = (Rect) it2.next();
            if (i4 != 0 || Tool.isExist(rect, rect7)) {
                Mat mat8 = new Mat(mat4, rect7);
                float f2 = (rect7.width * 1.0f) / rect7.height;
                Utils.matToBitmap(mat8, Bitmap.createBitmap(mat8.cols(), mat8.rows(), Bitmap.Config.ARGB_8888));
                String predict = Tool.predict(mat8);
                if (predict.equals("king")) {
                    predict = "王";
                }
                if ("X".equals(predict) || "O".equals(predict)) {
                    it = it2;
                } else {
                    it = it2;
                    if (!"diamond".equals(predict)) {
                        rect2 = rect;
                        if ("spade".equals(predict) || "heart".equals(predict)) {
                            mat = mat8;
                        } else {
                            mat = mat8;
                            if (!"club".equals(predict)) {
                                predict.hashCode();
                                char c = 65535;
                                ArrayList arrayList6 = arrayList5;
                                switch (predict.hashCode()) {
                                    case 74:
                                        str = "王";
                                        if (predict.equals("J")) {
                                            c = 0;
                                            break;
                                        }
                                        break;
                                    case 75:
                                        str = "王";
                                        if (predict.equals("K")) {
                                            c = 1;
                                            break;
                                        }
                                        break;
                                    case 87:
                                        str = "王";
                                        if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                            c = 2;
                                            break;
                                        }
                                        break;
                                    case 1567:
                                        str = "王";
                                        if (predict.equals("10")) {
                                            c = 3;
                                            break;
                                        }
                                        break;
                                    case 29579:
                                        if (predict.equals("王")) {
                                            str = "王";
                                            c = 4;
                                            break;
                                        }
                                    default:
                                        str = "王";
                                        break;
                                }
                                switch (c) {
                                    case 0:
                                        Mat lately = Tool.lately(mat4, null, rect7, arrayList2);
                                        if (lately != null || i4 == 0) {
                                            predict = Tool.predict(lately).equals("O") ? str : "J";
                                        }
                                        if (i4 != 0) {
                                            if (rect7.tl().y <= i2 + 5 && rect7.y >= i2 - 5) {
                                                if (rect7.br().y > i3 + 5 || rect7.br().y < i3 - 5) {
                                                    str2 = str;
                                                    if (!predict.equals(str2) && !predict.equals("Q")) {
                                                        Log.e("标准b-y" + i3, predict + "---" + rect7.br().y);
                                                    }
                                                } else {
                                                    str2 = str;
                                                }
                                                if (rect7.height < i3 - i2 && !predict.equals(str2)) {
                                                }
                                            }
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        }
                                        arrayList5 = arrayList6;
                                        arrayList5.add(predict);
                                        arrayList4.add(new IRect(predict, false, rect7));
                                        Bitmap createBitmap4 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap4);
                                        Tool.saveBitmapToFile(activity, createBitmap4, predict, i4 + "");
                                        it2 = it;
                                        rect = rect2;
                                        break;
                                    case 1:
                                        if (i4 == 0) {
                                            Mat lately2 = Tool.lately(mat4, null, rect7, arrayList2);
                                            if (lately2 != null) {
                                                Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                String predict2 = Tool.predict(lately2);
                                                if (!"diamond".equals(predict2) && !"spade".equals(predict2) && !"heart".equals(predict2) && !"club".equals(predict2)) {
                                                }
                                            }
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        }
                                        if (i4 != 0) {
                                        }
                                        arrayList5 = arrayList6;
                                        arrayList5.add(predict);
                                        arrayList4.add(new IRect(predict, false, rect7));
                                        Bitmap createBitmap42 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap42);
                                        Tool.saveBitmapToFile(activity, createBitmap42, predict, i4 + "");
                                        it2 = it;
                                        rect = rect2;
                                        break;
                                    case 2:
                                        if (isEnabled(arrayList4, rect7)) {
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        } else {
                                            predict = str;
                                            if (i4 != 0) {
                                            }
                                            arrayList5 = arrayList6;
                                            arrayList5.add(predict);
                                            arrayList4.add(new IRect(predict, false, rect7));
                                            Bitmap createBitmap422 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                            Utils.matToBitmap(mat, createBitmap422);
                                            Tool.saveBitmapToFile(activity, createBitmap422, predict, i4 + "");
                                            it2 = it;
                                            rect = rect2;
                                            break;
                                        }
                                    case 3:
                                        if (f2 < 0.3f) {
                                            Log.e("比例不对10", f2 + "");
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        }
                                        if (i4 != 0) {
                                        }
                                        arrayList5 = arrayList6;
                                        arrayList5.add(predict);
                                        arrayList4.add(new IRect(predict, false, rect7));
                                        Bitmap createBitmap4222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap4222);
                                        Tool.saveBitmapToFile(activity, createBitmap4222, predict, i4 + "");
                                        it2 = it;
                                        rect = rect2;
                                        break;
                                    case 4:
                                        Mat lately3 = Tool.lately(mat4, null, rect7, arrayList2);
                                        if (lately3 == null) {
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        } else {
                                            Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                            predict = Tool.predict(lately3).equals("O") ? str : "J";
                                            if (i4 != 0) {
                                            }
                                            arrayList5 = arrayList6;
                                            arrayList5.add(predict);
                                            arrayList4.add(new IRect(predict, false, rect7));
                                            Bitmap createBitmap42222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                            Utils.matToBitmap(mat, createBitmap42222);
                                            Tool.saveBitmapToFile(activity, createBitmap42222, predict, i4 + "");
                                            it2 = it;
                                            rect = rect2;
                                            break;
                                        }
                                    default:
                                        if (f2 < 0.5f || f2 > 1.0f) {
                                            Log.e("比例不对", f2 + "=====" + predict);
                                            it2 = it;
                                            rect = rect2;
                                            arrayList5 = arrayList6;
                                            break;
                                        }
                                        if (i4 != 0) {
                                        }
                                        arrayList5 = arrayList6;
                                        arrayList5.add(predict);
                                        arrayList4.add(new IRect(predict, false, rect7));
                                        Bitmap createBitmap422222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap422222);
                                        Tool.saveBitmapToFile(activity, createBitmap422222, predict, i4 + "");
                                        it2 = it;
                                        rect = rect2;
                                        break;
                                }
                            }
                        }
                        Bitmap createBitmap4222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat, createBitmap4222222);
                        Tool.saveBitmapToFile(activity, createBitmap4222222, predict, i4 + "");
                        it2 = it;
                        rect = rect2;
                    }
                }
                mat = mat8;
                rect2 = rect;
                Bitmap createBitmap42222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, createBitmap42222222);
                Tool.saveBitmapToFile(activity, createBitmap42222222, predict, i4 + "");
                it2 = it;
                rect = rect2;
            }
        }
        if (i4 != 0) {
            Log.e("别人最后", arrayList5.toString());
            HashMap hashMap = new HashMap();
            for (String str5 : arrayList5) {
                hashMap.put(str5, str5);
            }
            int size2 = arrayList5.size();
            if (size2 == 2) {
                IRect iRect = (IRect) arrayList4.get(0);
                IRect iRect2 = (IRect) arrayList4.get(1);
                if (hashMap.size() > 1 || Math.abs(iRect.getRect().br().y - iRect2.getRect().br().y) > 4.0d) {
                    return;
                }
            } else if (size2 != 3) {
                if (size2 != 4) {
                    if (size2 == 5) {
                        if (hashMap.size() != 5 && hashMap.size() != 2) {
                            Log.e("不是顺子或者3带对", "不符合规则" + arrayList5.toString());
                            return;
                        }
                    } else if (size2 == 6 && hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList5.toString());
                        return;
                    }
                } else if (hashMap.size() >= 3) {
                    Log.e("不是炸或者3带1", "不符合规则" + arrayList5.toString());
                    return;
                }
            } else if (hashMap.size() > 1) {
                return;
            }
        } else {
            Log.e("自己", arrayList5.toString());
            if (arrayList5.size() < DataUtils.getInstance().getHand()) {
                return;
            }
            ArrayList arrayList7 = new ArrayList();
            for (IRect iRect3 : arrayList4) {
                arrayList7.add(Integer.valueOf(iRect3.getRect().y));
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Integer num = 0;
                List<Integer> mode = Tool.mode(arrayList7);
                for (Integer num2 : mode) {
                    num = Integer.valueOf(num.intValue() + num2.intValue());
                }
                int intValue = num.intValue() / mode.size();
                for (IRect iRect4 : arrayList4) {
                    double d = intValue;
                    if (iRect4.getRect().y < 0.9d * d || iRect4.getRect().y > d * 1.1d) {
                        arrayList5.remove(iRect4.getName());
                        Log.e("自己的牌里面差别较大的", iRect4.getName());
                    }
                }
            }
        }
        Log.e("出牌" + i4, arrayList5.toString());
        onOCRListener.play(i4, Tool.sort(arrayList5));
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
