package com.carrydream.cardrecorder.datapool;

import android.util.Log;
import com.carrydream.cardrecorder.Model.MlData;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.ml.KNearest;
import org.opencv.ml.TrainData;
import org.opencv.objdetect.HOGDescriptor;

/* loaded from: classes.dex */
public class DataPool {
    static HOGDescriptor hogDescriptor;
    static KNearest kNearest;
    static HashMap<String, MlData> mlDataHashMap = new HashMap<>();
    static HashMap<Integer, String> n2f = new HashMap<>();

    static {
        kNearest = null;
        Size size = new Size(40.0d, 60.0d);
        Size size2 = new Size(20.0d, 30.0d);
        Size size3 = new Size(size2.width / 2.0d, size2.height / 2.0d);
        hogDescriptor = new HOGDescriptor(size, size2, size3, size3, 4);
        KNearest create = KNearest.create();
        kNearest = create;
        create.setDefaultK(3);
        kNearest.setIsClassifier(true);
    }

    public static void addMlData(String str, MlData mlData) {
        mlDataHashMap.put(str, mlData);
    }

    public static int str2Int(String str) {
        int size = n2f.size();
        n2f.put(Integer.valueOf(size), str);
        return size;
    }

    public static String getPredicatedResult(float f) {
        return n2f.get(Integer.valueOf((int) f));
    }

    public static void init() {
        Log.e("hjw", "------init ml------");
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int i2 = 0;
        for (Map.Entry<String, MlData> entry : mlDataHashMap.entrySet()) {
            i2 += entry.getValue().getDatas().size();
            if (i == 0) {
                i = entry.getValue().getAllMatdata().get(0).toList().size();
            }
        }
        Mat mat = new Mat(0, i, 5);
        for (Map.Entry<String, MlData> entry2 : mlDataHashMap.entrySet()) {
            entry2.getKey();
            for (Map.Entry<File, MatOfFloat> entry3 : entry2.getValue().getDatas().entrySet()) {
                arrayList.add(Integer.valueOf(str2Int(entry3.getKey().getAbsolutePath())));
                MatOfFloat value = entry3.getValue();
                mat.push_back(value.reshape(value.channels(), 1));
            }
        }
        Log.e("hjw", "data size:" + i2 + "  sample rows:" + mat.rows());
        Mat mat2 = new Mat(mat.rows(), 1, 5);
        int rows = mat.rows();
        for (int i3 = 0; i3 < rows; i3++) {
            mat2.put(i3, 0, ((Integer) arrayList.get(i3)).intValue());
        }
        kNearest.train(TrainData.create(mat, 0, mat2));
        Log.e("hjw", "------finish init------");
    }

    public static KNearest getkNearest() {
        return kNearest;
    }

    public static void setkNearest(KNearest kNearest2) {
        kNearest = kNearest2;
    }

    public static void clear() {
        mlDataHashMap.clear();
        n2f.clear();
    }

    public static MlData getDataByLabel(String str) {
        return mlDataHashMap.get(str);
    }

    public static HOGDescriptor getHogDescriptor() {
        return hogDescriptor;
    }
}
