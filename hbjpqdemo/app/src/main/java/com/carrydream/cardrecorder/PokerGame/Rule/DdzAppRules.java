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
public class DdzAppRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Code restructure failed: missing block: B:115:0x039d, code lost:
        if ("club".equals(r3) == false) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x03e1, code lost:
        if (r4.height < (r3 - 5)) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x035e, code lost:
        if (isEnabled(r0, r4) != false) goto L63;
     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x03b9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, int i2, int i3, OnOCRListener onOCRListener) {
        Rect rect;
        Iterator it;
        Rect rect2;
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        Mat clone = mat.clone();
        int width = clone.width();
        int height = clone.height();
        Rect rect3 = new Rect();
        rect3.width = width;
        rect3.height = height;
        rect3.x = 0;
        rect3.y = (height / 2) - (rect3.height / 2);
        Mat mat2 = new Mat(clone, rect3);
        Mat clone2 = mat2.clone();
        Imgproc.cvtColor(mat2, mat2, 11);
        Mat mat3 = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat2, mat3, 100, 255.0d, 0);
        ArrayList<MatOfPoint> arrayList = new ArrayList();
        Imgproc.findContours(mat3, arrayList, new Mat(), 1, 2);
        ArrayList arrayList2 = new ArrayList();
        String str = "_";
        if (i != 0) {
            for (MatOfPoint matOfPoint : arrayList) {
                Rect boundingRect = Imgproc.boundingRect(matOfPoint);
                String str2 = str;
                if (boundingRect.tl().y >= i2 - 10 && boundingRect.br().y <= i3 + 10 && boundingRect.height >= bitmap.getHeight() * 0.05d) {
                    arrayList2.add(boundingRect);
                    Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    mat2.release();
                }
                str = str2;
            }
            Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap);
            Tool.saveBitmapToFile(activity, createBitmap, i + str, "框框");
            rect = null;
        } else {
            int size = arrayList.size();
            int i4 = 0;
            int i5 = 0;
            Mat mat4 = null;
            Rect rect4 = null;
            while (i4 < size) {
                Rect boundingRect2 = Imgproc.boundingRect((MatOfPoint) arrayList.get(i4));
                ArrayList arrayList3 = arrayList;
                float f = (boundingRect2.width * 1.0f) / boundingRect2.height;
                int i6 = size;
                Mat mat5 = mat4;
                Rect rect5 = rect4;
                if (boundingRect2.height >= bitmap.getHeight() * 0.05d && f >= 0.3d) {
                    arrayList2.add(boundingRect2);
                    Imgproc.rectangle(clone2, boundingRect2.tl(), boundingRect2.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                    int i7 = boundingRect2.width * boundingRect2.height;
                    boundingRect2.tl();
                    if (i7 > i5) {
                        mat4 = new Mat(mat3, boundingRect2);
                        i5 = i7;
                        rect4 = boundingRect2;
                        i4++;
                        arrayList = arrayList3;
                        size = i6;
                    }
                }
                mat4 = mat5;
                rect4 = rect5;
                i4++;
                arrayList = arrayList3;
                size = i6;
            }
            Mat mat6 = mat4;
            Rect rect6 = rect4;
            mat2.release();
            Bitmap createBitmap2 = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(clone2, createBitmap2);
            Tool.saveBitmapToFile(activity, createBitmap2, i + "_", "框");
            if (mat6 == null) {
                return;
            }
            Bitmap createBitmap3 = Bitmap.createBitmap(mat6.cols(), mat6.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat6, createBitmap3);
            Tool.saveBitmapToFile(activity, createBitmap3, i + "_", i + "Max");
            rect = rect6;
        }
        ArrayList<IRect> arrayList4 = new ArrayList();
        ArrayList<String> arrayList5 = new ArrayList();
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            Rect rect7 = (Rect) it2.next();
            if (i != 0 || Tool.isExist(rect, rect7)) {
                Mat mat7 = new Mat(mat3, rect7);
                float f2 = (rect7.width * 1.0f) / rect7.height;
                Utils.matToBitmap(mat7, Bitmap.createBitmap(mat7.cols(), mat7.rows(), Bitmap.Config.ARGB_8888));
                String predict = Tool.predict(mat7);
                if (predict.equals("king")) {
                    predict = "王";
                }
                if (!"X".equals(predict) && !"O".equals(predict) && !"diamond".equals(predict) && !"spade".equals(predict) && !"heart".equals(predict) && !"club".equals(predict)) {
                    predict.hashCode();
                    char c = 65535;
                    it = it2;
                    switch (predict.hashCode()) {
                        case 74:
                            rect2 = rect;
                            if (predict.equals("J")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 75:
                            rect2 = rect;
                            if (predict.equals("K")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 87:
                            rect2 = rect;
                            if (predict.equals(ExifInterface.LONGITUDE_WEST)) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1567:
                            rect2 = rect;
                            if (predict.equals("10")) {
                                c = 3;
                                break;
                            }
                            break;
                        case 29579:
                            if (predict.equals("王")) {
                                rect2 = rect;
                                c = 4;
                                break;
                            }
                        default:
                            rect2 = rect;
                            break;
                    }
                    switch (c) {
                        case 0:
                            Mat lately = Tool.lately(mat3, null, rect7, arrayList2);
                            if (lately != null || i == 0) {
                                predict = Tool.predict(lately).equals("O") ? "王" : "J";
                            }
                            if (i != 0) {
                                int i8 = i3 - i2;
                                int i9 = (int) (i8 * 0.6815d);
                                if (!predict.equals("王")) {
                                    if (rect7.width <= i9 + 5) {
                                        if (rect7.width >= i9 - 5) {
                                            if (rect7.height <= i8 + 5) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            arrayList5.add(predict);
                            arrayList4.add(new IRect(predict, false, rect7));
                            break;
                        case 1:
                            if (i == 0) {
                                Mat lately2 = Tool.lately(mat3, null, rect7, arrayList2);
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
                            }
                            if (i != 0) {
                            }
                            arrayList5.add(predict);
                            arrayList4.add(new IRect(predict, false, rect7));
                            break;
                        case 2:
                            break;
                        case 3:
                            if (f2 < 0.3f) {
                                Log.e("比例不对10", f2 + "");
                                break;
                            }
                            if (i != 0) {
                            }
                            arrayList5.add(predict);
                            arrayList4.add(new IRect(predict, false, rect7));
                            break;
                        case 4:
                            Mat lately3 = Tool.lately(mat3, null, rect7, arrayList2);
                            if (lately3 != null) {
                                Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                if (!Tool.predict(lately3).equals("O")) {
                                    predict = "J";
                                    if (i != 0) {
                                    }
                                    arrayList5.add(predict);
                                    arrayList4.add(new IRect(predict, false, rect7));
                                    break;
                                }
                                predict = "王";
                                if (i != 0) {
                                }
                                arrayList5.add(predict);
                                arrayList4.add(new IRect(predict, false, rect7));
                            }
                            break;
                        default:
                            if (f2 < 0.5f || f2 > 1.0f) {
                                Log.e("比例不对", f2 + "=====" + predict);
                                break;
                            }
                            if (i != 0) {
                            }
                            arrayList5.add(predict);
                            arrayList4.add(new IRect(predict, false, rect7));
                            break;
                    }
                } else {
                    it = it2;
                    rect2 = rect;
                }
                rect = rect2;
                it2 = it;
            }
        }
        if (i != 0) {
            Log.e("别人最后", arrayList5.toString());
            HashMap hashMap = new HashMap();
            for (String str3 : arrayList5) {
                hashMap.put(str3, str3);
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
            ArrayList arrayList6 = new ArrayList();
            for (IRect iRect3 : arrayList4) {
                arrayList6.add(Integer.valueOf(iRect3.getRect().y));
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Integer num = 0;
                List<Integer> mode = Tool.mode(arrayList6);
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
        Log.e("出牌" + i, arrayList5.toString());
        onOCRListener.play(i, Tool.sort(arrayList5));
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
