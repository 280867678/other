package com.carrydream.cardrecorder.Model;

import android.app.Activity;
import android.util.Log;

import com.carrydream.cardrecorder.datapool.DataPool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class MlData {
    public static final int UNITHEIGHT = 60;
    public static final int UNITWIDTH = 40;
    public String label;
    public HashMap<File, MatOfFloat> datas = new HashMap<>();
    public ArrayList<MatOfFloat> listDatas = new ArrayList<>();

    public MlData(String str) {
        this.label = str;
    }

    public void putMlData(File file, MatOfFloat matOfFloat) {
        this.datas.put(file, matOfFloat);
        this.listDatas.add(matOfFloat);
    }

    public ArrayList<MatOfFloat> getAllMatdata() {
        return this.listDatas;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public HashMap<File, MatOfFloat> getDatas() {
        return this.datas;
    }

    public static MlData createMlDataFromDirectory(File file, Activity activity) {
        File[] listFiles;
        MatOfFloat createMlDataFromFile;
        MlData mlData = new MlData(file.getName());
        if (file != null && file.exists() && file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                createMlDataFromFile = createMlDataFromFile(file2, activity);

                if (file2 != null && file2.exists() && ((file2.getAbsolutePath().endsWith(".png") || file2.getAbsolutePath().endsWith(".PNG")) && createMlDataFromFile  != null)) {
                    mlData.putMlData(file2, createMlDataFromFile);
                }
            }
        } else {
            Log.e("hjw", "study data must be a Directory");
        }
        return mlData;
    }

    private static MatOfFloat createMlDataFromFile(File file, Activity activity) {
        try {
            Log.e("createMlDataFromFile：文件到底是哪个：：：", file.getAbsolutePath());

            Mat imread = Imgcodecs.imread(file.getAbsolutePath());
            Mat mat = new Mat(40, 60, imread.type());
            Imgproc.resize(imread, mat, new Size(40.0d, 60.0d));
            MatOfFloat matOfFloat = new MatOfFloat();
            DataPool.getHogDescriptor().compute(mat, matOfFloat);
            return matOfFloat;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "label:" + this.label + " datasize:" + this.datas.size();
    }
}
