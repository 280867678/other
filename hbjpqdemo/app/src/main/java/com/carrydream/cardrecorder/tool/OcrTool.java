package com.carrydream.cardrecorder.tool;

import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class OcrTool {
    private static OcrTool mInstance;
    private String[] Pokers = {ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
    private HashMap<String, Integer> landowner = new HashMap<>();
    private OnOcr onOcr;

    public void setOnOcr(OnOcr onOcr) {
        this.onOcr = onOcr;
    }

    private OcrTool() {
        for (String str : this.Pokers) {
            this.landowner.put(str, 4);
        }
        this.landowner.put("red_Joker", 1);
        this.landowner.put("black_Joker", 1);
    }

    public static synchronized OcrTool getInstance() {
        OcrTool ocrTool;
        synchronized (OcrTool.class) {
            if (mInstance == null) {
                mInstance = new OcrTool();
            }
            ocrTool = mInstance;
        }
        return ocrTool;
    }

    public void reset() {
        this.landowner.clear();
        for (String str : this.Pokers) {
            this.landowner.put(str, 4);
        }
        this.landowner.put("red_Joker", 1);
        this.landowner.put("black_Joker", 1);
    }

    public HashMap<String, Integer> getLandowner() {
        return this.landowner;
    }

    public void paly(String str) {
        Log.e("出牌", str.toString());
        String replaceAll = str.replaceAll("\\s*", "").replaceAll("[^(a-zA-Z0-9)]", "");
        ArrayList<String> arrayList = new ArrayList();
        for (int i = 0; i < replaceAll.length(); i++) {
            arrayList.add(replaceAll.charAt(i) + "");
        }
        for (String str2 : arrayList) {
            if (this.landowner.get(str2) != null) {
                int intValue = this.landowner.get(str2).intValue() - 1;
                HashMap<String, Integer> hashMap = this.landowner;
                if (intValue < 0) {
                    intValue = 0;
                }
                hashMap.put(str2, Integer.valueOf(intValue));
            }
        }
        this.onOcr.output(this.landowner);
    }
}
