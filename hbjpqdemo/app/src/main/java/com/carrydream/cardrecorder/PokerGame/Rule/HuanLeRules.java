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
public class HuanLeRules {

    /* loaded from: classes.dex */
    public interface OnOCRListener {
        void play(int i, List<String> list);
    }

    /* JADX WARN: Removed duplicated region for block: B:197:0x050d  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x0697  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0201  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void Enable(Activity activity, Bitmap bitmap, int i, OnOCRListener onOCRListener) {
        boolean z;
        Iterator it;
        int i2;
        IRect iRect = null;
        Activity activity2 = null;
        int i3 = 0;
        boolean z2 = false;
        ArrayList arrayList = null;
        ArrayList arrayList2 = null;
        Iterator it2 = null;
        ArrayList arrayList3 = null;
        ArrayList arrayList4;
        ArrayList arrayList5;
        String str;
        Object obj;
        String str2 = null;
        Mat mat = null;
        String str3;
        String str4;
        String str5;
        ArrayList arrayList6;
        Activity activity3 = activity;
        int i4 = i;
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
        ArrayList arrayList7 = new ArrayList();
        Imgproc.findContours(mat4, arrayList7, new Mat(), 1, 2);
        ArrayList arrayList8 = new ArrayList();
        int size = arrayList7.size();
        Mat mat5 = null;
        Rect rect2 = null;
        int i5 = 0;
        int i6 = 0;
        while (i5 < size) {
            Rect boundingRect = Imgproc.boundingRect((MatOfPoint) arrayList7.get(i5));
            float f = (boundingRect.width * 1.0f) / boundingRect.height;
            ArrayList arrayList9 = arrayList7;
            int i7 = size;
            if (boundingRect.height >= bitmap.getHeight() * 0.05d && ((i4 == 0 || f <= 5.0f) && f >= 0.3d)) {
                arrayList8.add(boundingRect);
                Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
                int i8 = boundingRect.width * boundingRect.height;
                Point tl = boundingRect.tl();
                if (i4 != 0) {
                    arrayList6 = arrayList8;
                    if (tl.y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i8 > i6) {
                        i6 = i8;
                        mat5 = new Mat(mat4, boundingRect);
                        rect2 = boundingRect;
                    }
                    i5++;
                    arrayList7 = arrayList9;
                    size = i7;
                    arrayList8 = arrayList6;
                } else if (i8 > i6) {
                    i6 = i8;
                    mat5 = new Mat(mat4, boundingRect);
                    rect2 = boundingRect;
                }
            }
            arrayList6 = arrayList8;
            i5++;
            arrayList7 = arrayList9;
            size = i7;
            arrayList8 = arrayList6;
        }
        ArrayList arrayList10 = arrayList8;
        mat3.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        Tool.saveBitmapToFile(activity3, createBitmap, i4 + "_", "框");
        if (mat5 == null) {
            return;
        }
        if (i4 != 1 || rect2.br().x >= bitmap.getWidth() * 0.5d) {
            if (i4 != 2 || rect2.x <= bitmap.getWidth() * 0.5d) {
                Bitmap createBitmap2 = Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat5, createBitmap2);
                Tool.saveBitmapToFile(activity3, createBitmap2, i4 + "_", i4 + "Max");
                if (i4 != 0) {
                    if ((i6 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth()) < 0.05d) {
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
                                Mat mat6 = new Mat(mat4, rect3);
                                float f2 = (rect3.width * 1.0f) / rect3.height;
                                Utils.matToBitmap(mat6, Bitmap.createBitmap(mat6.cols(), mat6.rows(), Bitmap.Config.ARGB_8888));
                                if (i4 != 0) {
                                    str = "王";
                                    arrayList4 = arrayList11;
                                    arrayList5 = arrayList12;
                                    if (rect3.br().y <= rect2.y + (rect2.height * 0.65d)) {
                                        if (rect2.br().y - rect3.br().y < rect3.height) {
                                        }
                                    }
                                    arrayList11 = arrayList4;
                                    arrayList12 = arrayList5;
                                } else {
                                    arrayList4 = arrayList11;
                                    arrayList5 = arrayList12;
                                    str = "王";
                                }
                                String predict = Tool.predict(mat6);
                                Iterator it3 = it;
                                if (rect3.br().x <= rect2.br().x - ((rect3.width * 2) * 0.8d) || predict.equals("10") || predict.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                                    if (predict.equals("king")) {
                                        predict = str;
                                    }
                                    if (rect3.width * rect3.height <= 5500) {
                                        if (predict.equals("10")) {
                                            obj = str;
                                        } else {
                                            obj = str;
                                            if (!predict.equals(obj) && !predict.equals("J") && rect3.width * rect3.height < 400) {
                                            }
                                        }
                                        if ("X".equals(predict) || "O".equals(predict) || "diamond".equals(predict) || "spade".equals(predict)) {
                                            z2 = z;
                                            str2 = predict;
                                            mat = mat6;
                                            it2 = it3;
                                        } else {
                                            it2 = it3;
                                            if (!"heart".equals(predict) && !"club".equals(predict)) {
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
                                                        z2 = z;
                                                        mat = mat6;
                                                        arrayList3 = arrayList10;
                                                        Mat lately = Tool.lately(mat4, rect2, rect3, arrayList3);
                                                        if (lately == null) {
                                                            activity3 = activity;
                                                            i4 = i;
                                                            arrayList10 = arrayList3;
                                                            it = it2;
                                                            arrayList11 = arrayList4;
                                                            arrayList12 = arrayList5;
                                                            z = z2;
                                                            break;
                                                        } else {
                                                            String predict2 = Tool.predict(lately);
                                                            str4 = (predict2.equals("diamond") || predict2.equals("spade") || predict2.equals("heart") || predict2.equals("club")) ? "J" : obj.toString();
                                                            arrayList2 = arrayList5;
                                                            arrayList2.add(str4);
                                                            arrayList = arrayList4;
                                                            arrayList.add(new IRect(str4, false, rect3));
                                                            str3 = str4;
                                                            Bitmap createBitmap3 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                            Utils.matToBitmap(mat, createBitmap3);
                                                            StringBuilder sb = new StringBuilder();
                                                            i3 = i;
                                                            sb.append(i3);
                                                            sb.append("");
                                                            activity2 = activity;
                                                            Tool.saveBitmapToFile(activity2, createBitmap3, str3, sb.toString());
                                                            break;
                                                        }
                                                    case 1:
                                                        z2 = z;
                                                        arrayList3 = arrayList10;
                                                        str5 = predict;
                                                        mat = mat6;
                                                        if (rect3.y >= rect2.y + (rect3.width * 0.5d)) {
                                                            Mat lately2 = Tool.lately(mat4, rect2, rect3, arrayList3);
                                                            if (lately2 != null) {
                                                                Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                                                String predict3 = Tool.predict(lately2);
                                                                if (!"diamond".equals(predict3) && !"spade".equals(predict3) && !"heart".equals(predict3) && !"club".equals(predict3)) {
                                                                }
                                                            }
                                                            activity3 = activity;
                                                            i4 = i;
                                                            arrayList10 = arrayList3;
                                                            it = it2;
                                                            arrayList11 = arrayList4;
                                                            arrayList12 = arrayList5;
                                                            z = z2;
                                                            break;
                                                        }
                                                        str4 = str5;
                                                        arrayList2 = arrayList5;
                                                        arrayList2.add(str4);
                                                        arrayList = arrayList4;
                                                        arrayList.add(new IRect(str4, false, rect3));
                                                        str3 = str4;
                                                        Bitmap createBitmap32 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                        Utils.matToBitmap(mat, createBitmap32);
                                                        StringBuilder sb2 = new StringBuilder();
                                                        i3 = i;
                                                        sb2.append(i3);
                                                        sb2.append("");
                                                        activity2 = activity;
                                                        Tool.saveBitmapToFile(activity2, createBitmap32, str3, sb2.toString());
                                                        break;
                                                    case 2:
                                                        z2 = z;
                                                        arrayList3 = arrayList10;
                                                        str4 = obj.toString();
                                                        mat = mat6;
                                                        arrayList2 = arrayList5;
                                                        arrayList2.add(str4);
                                                        arrayList = arrayList4;
                                                        arrayList.add(new IRect(str4, false, rect3));
                                                        str3 = str4;
                                                        Bitmap createBitmap322 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                        Utils.matToBitmap(mat, createBitmap322);
                                                        StringBuilder sb22 = new StringBuilder();
                                                        i3 = i;
                                                        sb22.append(i3);
                                                        sb22.append("");
                                                        activity2 = activity;
                                                        Tool.saveBitmapToFile(activity2, createBitmap322, str3, sb22.toString());
                                                        break;
                                                    case 3:
                                                        z2 = z;
                                                        arrayList3 = arrayList10;
                                                        if (f2 >= 0.3f) {
                                                            str5 = predict;
                                                            mat = mat6;
                                                            str4 = str5;
                                                            arrayList2 = arrayList5;
                                                            arrayList2.add(str4);
                                                            arrayList = arrayList4;
                                                            arrayList.add(new IRect(str4, false, rect3));
                                                            str3 = str4;
                                                            Bitmap createBitmap3222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                            Utils.matToBitmap(mat, createBitmap3222);
                                                            StringBuilder sb222 = new StringBuilder();
                                                            i3 = i;
                                                            sb222.append(i3);
                                                            sb222.append("");
                                                            activity2 = activity;
                                                            Tool.saveBitmapToFile(activity2, createBitmap3222, str3, sb222.toString());
                                                            break;
                                                        } else {
                                                            Log.e("比例不对10", f2 + "");
                                                            activity3 = activity;
                                                            arrayList10 = arrayList3;
                                                            it = it2;
                                                            arrayList11 = arrayList4;
                                                            arrayList12 = arrayList5;
                                                            z = z2;
                                                            break;
                                                        }
                                                    case 4:
                                                        arrayList3 = arrayList10;
                                                        Mat lately3 = Tool.lately(mat4, rect2, rect3, arrayList3);
                                                        if (lately3 == null) {
                                                            activity3 = activity;
                                                            arrayList10 = arrayList3;
                                                            it = it2;
                                                            arrayList11 = arrayList4;
                                                            arrayList12 = arrayList5;
                                                            break;
                                                        } else {
                                                            z2 = z;
                                                            Utils.matToBitmap(lately3, Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888));
                                                            String predict4 = Tool.predict(lately3);
                                                            if (predict4.equals("diamond") || predict4.equals("spade") || predict4.equals("heart") || predict4.equals("club")) {
                                                                str4 = "J";
                                                                mat = mat6;
                                                                arrayList2 = arrayList5;
                                                                arrayList2.add(str4);
                                                                arrayList = arrayList4;
                                                                arrayList.add(new IRect(str4, false, rect3));
                                                                str3 = str4;
                                                                Bitmap createBitmap32222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                                Utils.matToBitmap(mat, createBitmap32222);
                                                                StringBuilder sb2222 = new StringBuilder();
                                                                i3 = i;
                                                                sb2222.append(i3);
                                                                sb2222.append("");
                                                                activity2 = activity;
                                                                Tool.saveBitmapToFile(activity2, createBitmap32222, str3, sb2222.toString());
                                                                break;
                                                            }
                                                            str4 = obj.toString();
                                                            mat = mat6;
                                                            arrayList2 = arrayList5;
                                                            arrayList2.add(str4);
                                                            arrayList = arrayList4;
                                                            arrayList.add(new IRect(str4, false, rect3));
                                                            str3 = str4;
                                                            Bitmap createBitmap322222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                            Utils.matToBitmap(mat, createBitmap322222);
                                                            StringBuilder sb22222 = new StringBuilder();
                                                            i3 = i;
                                                            sb22222.append(i3);
                                                            sb22222.append("");
                                                            activity2 = activity;
                                                            Tool.saveBitmapToFile(activity2, createBitmap322222, str3, sb22222.toString());
                                                        }
                                                        break;
                                                    default:
                                                        if (f2 < 0.5f || f2 > 1.0f) {
                                                            Log.e("比例不对", f2 + "=====" + predict);
                                                        } else if (i4 == 0 || rect3.y <= (rect3.height * 0.5d) + rect2.y || z) {
                                                            z2 = z;
                                                            str5 = predict;
                                                            mat = mat6;
                                                            arrayList3 = arrayList10;
                                                            str4 = str5;
                                                            arrayList2 = arrayList5;
                                                            arrayList2.add(str4);
                                                            arrayList = arrayList4;
                                                            arrayList.add(new IRect(str4, false, rect3));
                                                            str3 = str4;
                                                            Bitmap createBitmap3222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                                            Utils.matToBitmap(mat, createBitmap3222222);
                                                            StringBuilder sb222222 = new StringBuilder();
                                                            i3 = i;
                                                            sb222222.append(i3);
                                                            sb222222.append("");
                                                            activity2 = activity;
                                                            Tool.saveBitmapToFile(activity2, createBitmap3222222, str3, sb222222.toString());
                                                            break;
                                                        }
                                                        activity3 = activity;
                                                        it = it2;
                                                        arrayList11 = arrayList4;
                                                        arrayList12 = arrayList5;
                                                        break;
                                                }
                                            } else {
                                                z2 = z;
                                                str2 = predict;
                                                mat = mat6;
                                            }
                                        }
                                        arrayList = arrayList4;
                                        arrayList2 = arrayList5;
                                        arrayList3 = arrayList10;
                                        str3 = str2;
                                        Bitmap createBitmap32222222 = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, createBitmap32222222);
                                        StringBuilder sb2222222 = new StringBuilder();
                                        i3 = i;
                                        sb2222222.append(i3);
                                        sb2222222.append("");
                                        activity2 = activity;
                                        Tool.saveBitmapToFile(activity2, createBitmap32222222, str3, sb2222222.toString());
                                    }
                                }
                                it = it3;
                                arrayList11 = arrayList4;
                                arrayList12 = arrayList5;
                            } else {
                                activity2 = activity3;
                                i3 = i4;
                                z2 = z;
                                arrayList = arrayList11;
                                arrayList2 = arrayList12;
                                it2 = it;
                                arrayList3 = arrayList10;
                            }
                            arrayList11 = arrayList;
                            arrayList10 = arrayList3;
                            i4 = i3;
                            it = it2;
                            z = z2;
                            arrayList12 = arrayList2;
                            activity3 = activity2;
                        }
                        i2 = i4;
                        ArrayList<IRect> arrayList13 = arrayList11;
                        ArrayList<String> arrayList14 = arrayList12;
                        if (i2 == 0) {
                            Log.e("别人最后", arrayList14.toString());
                            HashMap hashMap = new HashMap();
                            for (String str6 : arrayList14) {
                                hashMap.put(str6, str6);
                            }
                            switch (arrayList14.size()) {
                                case 1:
                                    if (((IRect) arrayList13.get(0)).getRect().br().x > rect2.tl().x + (iRect.getRect().width * 3)) {
                                        Log.e("===", "不符合单张牌的规则" + iRect.getRect().br().x + "=" + (iRect.getRect().width * 2) + "=" + iRect.getName());
                                        return;
                                    }
                                    break;
                                case 2:
                                    IRect iRect2 = (IRect) arrayList13.get(0);
                                    IRect iRect3 = (IRect) arrayList13.get(1);
                                    if (hashMap.size() > 1 && arrayList14.contains("王")) {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList14.removeAll((List) arrayList14.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules$$ExternalSyntheticLambda0
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj2) {
                                                    return HuanLeRules.lambda$Enable$0((String) obj2);
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
                                        if (!arrayList14.contains("王")) {
                                            return;
                                        }
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            arrayList14.removeAll((List) arrayList14.stream().filter(new Predicate() { // from class: com.carrydream.cardrecorder.PokerGame.Rule.HuanLeRules$$ExternalSyntheticLambda1
                                                @Override // java.util.function.Predicate
                                                public final boolean test(Object obj2) {
                                                    return HuanLeRules.lambda$Enable$1((String) obj2);
                                                }
                                            }).collect(Collectors.toList()));
                                            break;
                                        }
                                    }
                                    break;
                                case 4:
                                    if (hashMap.size() >= 3) {
                                        Log.e("不是炸或者3带1", "不符合规则" + arrayList14.toString());
                                        return;
                                    }
                                    break;
                                case 5:
                                    if (hashMap.size() != 5 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者3带对", "不符合规则" + arrayList14.toString());
                                        return;
                                    }
                                    break;
                                case 6:
                                    if (hashMap.size() != 6 && hashMap.size() != 3 && hashMap.size() != 2) {
                                        Log.e("不是顺子或者炸带2", "不符合规则" + arrayList14.toString());
                                        return;
                                    }
                                    break;
                            }
                        } else {
                            Log.e("自己", arrayList14.toString());
                            if (arrayList14.size() < DataUtils.getInstance().getHand()) {
                                return;
                            }
                            ArrayList arrayList15 = new ArrayList();
                            for (IRect iRect4 : arrayList13) {
                                arrayList15.add(Integer.valueOf(iRect4.getRect().y));
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                Integer num = 0;
                                List<Integer> mode = Tool.mode(arrayList15);
                                for (Integer num2 : mode) {
                                    num = Integer.valueOf(num.intValue() + num2.intValue());
                                }
                                int intValue = num.intValue() / mode.size();
                                for (IRect iRect5 : arrayList13) {
                                    double d = intValue;
                                    if (iRect5.getRect().y < 0.9d * d || iRect5.getRect().y > d * 1.1d) {
                                        arrayList14.remove(iRect5.getName());
                                        Log.e("自己的牌里面差别较大的", iRect5.getName());
                                    }
                                }
                            }
                        }
                        Log.e("出牌" + i2, arrayList14.toString());
                        onOCRListener.play(i2, Tool.sort(arrayList14));
                    }
                }
                z = false;
                ArrayList arrayList112 = new ArrayList();
                ArrayList arrayList122 = new ArrayList();
                it = arrayList10.iterator();
//                while (it.hasNext()) {
//                }
                i2 = i4;
                ArrayList<IRect> arrayList132 = arrayList112;
                ArrayList<String> arrayList142 = arrayList122;
                if (i2 == 0) {
                }
                Log.e("出牌" + i2, arrayList142.toString());
                onOCRListener.play(i2, Tool.sort(arrayList142));
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
