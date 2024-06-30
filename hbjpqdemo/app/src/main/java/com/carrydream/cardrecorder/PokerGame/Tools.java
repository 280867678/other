package com.carrydream.cardrecorder.PokerGame;

import android.graphics.Bitmap;
import android.util.Log;
import com.carrydream.cardrecorder.tool.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class Tools {
    public static List<MatOfPoint> findContours(Bitmap bitmap) {
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
        mat2.clone();
        Imgproc.cvtColor(mat2, mat2, 11);
        Mat mat3 = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
        Imgproc.threshold(mat2, mat3, 100, 255.0d, 0);
        ArrayList arrayList = new ArrayList();
        Imgproc.findContours(mat3, arrayList, new Mat(), 1, 2);
        return arrayList;
    }

    public static boolean isSubList(List<String> list, List<String> list2) {
        if (list != null) {
            try {
                if (list.size() != 0) {
                    HashMap<String, Integer> hashMap = hashMap(list);
                    HashMap<String, Integer> hashMap2 = hashMap(list2);
                    for (String str : hashMap2.keySet()) {
                        Integer num = hashMap2.get(str);
                        if (hashMap.get(str) == null) {
                            return false;
                        }
                        Integer valueOf = Integer.valueOf(hashMap.get(str).intValue() - num.intValue());
                        if (valueOf.intValue() < 0) {
                            return false;
                        }
                        hashMap.put(str, valueOf);
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(list2);
                    for (String str2 : hashMap.keySet()) {
                        Integer num2 = hashMap.get(str2);
                        if (num2.intValue() != 0) {
                            for (int i = 0; i < num2.intValue(); i++) {
                                arrayList.add(str2);
                            }
                        }
                    }
                    return Tool.MySort(list).equals(Tool.MySort(arrayList));
                }
            } catch (Exception e) {
                Log.e("=======hjw=======", e.getMessage());
                return true;
            }
        }
        return false;
    }

    public static HashMap<String, Integer> hashMap(List<String> list) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String str : list) {
            hashMap.put(str, Integer.valueOf((hashMap.get(str) == null ? 0 : hashMap.get(str).intValue()) + 1));
        }
        return hashMap;
    }
}
