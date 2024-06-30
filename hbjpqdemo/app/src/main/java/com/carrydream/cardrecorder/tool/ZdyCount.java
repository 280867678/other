package com.carrydream.cardrecorder.tool;

import androidx.exifinterface.media.ExifInterface;
import com.carrydream.cardrecorder.Model.ZdyPoker;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ZdyCount {
    public static List<ZdyPoker> getCount(int i) {
        ArrayList arrayList = new ArrayList();
        String[] strArr = {"王", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", ExifInterface.GPS_MEASUREMENT_3D};
        for (int i2 = 0; i2 < 14; i2++) {
            if (strArr[i2].equals("王")) {
                arrayList.add(new ZdyPoker(strArr[i2], i * 2));
            } else {
                arrayList.add(new ZdyPoker(strArr[i2], i * 4));
            }
        }
        return arrayList;
    }
}
