package com.carrydream.cardrecorder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.carrydream.cardrecorder.Model.MlData;
import com.carrydream.cardrecorder.PokerGame.CustomPoker;
import com.carrydream.cardrecorder.PokerGame.Poker;
import com.carrydream.cardrecorder.adapter.AdAdapter;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.datapool.DataPool;
import com.carrydream.cardrecorder.tool.DensityUtils;
import com.carrydream.cardrecorder.tool.GlideEngine;
import com.carrydream.cardrecorder.tool.LogUtil;
import com.carrydream.cardrecorder.tool.NotificationHelper;
import com.carrydream.cardrecorder.tool.OnOcr;
import com.carrydream.cardrecorder.tool.SaveBitmapCallback;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.ui.activity.HomeActivity;
import com.hb.aiyouxiba.R;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.mask.mediaprojection.interfaces.MediaRecorderCallback;
import com.mask.mediaprojection.interfaces.ScreenCaptureCallback;
import com.mask.mediaprojection.utils.MediaProjectionHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/* loaded from: classes.dex */
public class MainActivity extends BaseActivity {
    static final String CHINESE_LANGUAGE = "eng";
    public static final int REQUEST_MEDIA_PROJECTION = 10001;
    public static String mldata = "good_data";
    int Screen;
    Button add1;
    Button add2;
    AdAdapter appAdpter;
    Bitmap bitmap1;
    Bitmap bitmap2;
    Button contrast1;
    Button contrast2;
    Poker customPoker;
    ImageView image;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    TextView king_d;
    TextView king_x;
    TextView last;
    private MediaProjection mMediaProjection;
    OrientationEventListener mOrientationListener;
    MediaProjectionManager mProjectionManager;
    public String mldataPath;
    TextView next;
    Poker pokerTool;
    RecyclerView recycler;
    TimerTask task;
    TextView text;
    TextView text_10;
    TextView text_2;
    TextView text_3;
    TextView text_4;
    TextView text_5;
    TextView text_6;
    TextView text_7;
    TextView text_8;
    TextView text_9;
    TextView text_A;
    TextView text_J;
    TextView text_K;
    TextView text_Q;
    Timer timer;
    String url1;
    String url2;
    public static String targetpath = "pork";
    public static String dataPath = Environment.getExternalStorageDirectory() + File.separator + targetpath;
    int REQUEST_CODE = 2000;
    List<String> list = new ArrayList();
    String FILE_NAME = "tessdata";
    String LANGUAGE_NAME = "chi_sim.traineddata";
    ArrayList<MatOfPoint> targetAreas = new ArrayList<>();
    ArrayList<Rect> rects = new ArrayList<>();
    private ArrayList<String> cardTypes = new ArrayList<>();
    int orientation_ = 0;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) { // from class: com.carrydream.cardrecorder.MainActivity.1
        @Override // org.opencv.android.BaseLoaderCallback, org.opencv.android.LoaderCallbackInterface
        public void onManagerConnected(int i) {
            if (i == 0) {
                Log.i("hjw", "OpenCV 加载  成功");
            } else {
                super.onManagerConnected(i);
            }
        }
    };

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_main;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        MediaProjectionHelper.getInstance().createVirtualDisplay(i, i2, intent, true, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.carrydream.cardrecorder.base.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        MediaProjectionHelper.getInstance().stopService(this);
        super.onDestroy();
    }

    /* loaded from: classes.dex */
    class MyBroadcastReceive extends BroadcastReceiver {
        MyBroadcastReceive() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.e("getType:", String.valueOf(getWindowManager().getDefaultDisplay().getRotation()));
            if (String.valueOf(getWindowManager().getDefaultDisplay().getRotation()).equals("1")) {
                Log.e("getType:", "stopTime");
                stopTime();
            } else {
                Log.e("getType:", "startTime");
                startTime();
            }
        }
    }


    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        initData();
        this.pokerTool = new CustomPoker().setData(Tool.initData());
        this.mldataPath = getExternalCacheDir() + File.separator + mldata;
        this.recycler = (RecyclerView) findViewById(R.id.recycler);
        this.appAdpter = new AdAdapter(this, R.layout.item);
        this.recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.recycler.setAdapter(this.appAdpter);

        this.add1 = (Button) findViewById(R.id.add1);
        this.add2 = (Button) findViewById(R.id.add2);
        MyBroadcastReceive myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(myBroadcastReceive, intentFilter);
        if (!OpenCVLoader.initDebug()) {
            Log.e("hjw", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, this.mLoaderCallback);
        } else {
            Log.e("hjw", "OpenCV library found inside package. Using it!");
        }
        new MlThread(this.mldataPath).start();
        this.contrast1 = (Button) findViewById(R.id.contrast1);
        this.contrast2 = (Button) findViewById(R.id.contrast2);
        this.text = (TextView) findViewById(R.id.text);
        this.image = (ImageView) findViewById(R.id.image);
        this.image1 = (ImageView) findViewById(R.id.image1);
        this.image2 = (ImageView) findViewById(R.id.image2);
        this.image3 = (ImageView) findViewById(R.id.image3);
        this.contrast1.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                m44lambda$init$0$comcarrydreamcardrecorderMainActivity(view);
            }
        });
        this.add1.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.MainActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                m45lambda$init$1$comcarrydreamcardrecorderMainActivity(view);
            }
        });
        this.pokerTool.setOnOcr(new OnOcr() { // from class: com.carrydream.cardrecorder.MainActivity.2
            @Override // com.carrydream.cardrecorder.tool.OnOcr
            public void NewRound(HashMap<String, Integer> hashMap) {
                Log.e("新牌局回调", "--------------");
                if (king_d != null) {
                    king_d.setText(hashMap.get("king").toString());
                    text_3.setText(hashMap.get(ExifInterface.GPS_MEASUREMENT_3D).toString());
                    text_4.setText(hashMap.get("4").toString());
                    text_5.setText(hashMap.get("5").toString());
                    text_6.setText(hashMap.get("6").toString());
                    text_7.setText(hashMap.get("7").toString());
                    text_8.setText(hashMap.get("8").toString());
                    text_9.setText(hashMap.get("9").toString());
                    text_10.setText(hashMap.get("10").toString());
                    text_J.setText(hashMap.get("J").toString());
                    text_Q.setText(hashMap.get("Q").toString());
                    text_K.setText(hashMap.get("K").toString());
                    text_A.setText(hashMap.get(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS).toString());
                }
                if (next != null && last != null) {
                    runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.MainActivity.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            next.setText("");
                            last.setText("");
                        }
                    });
                } else {
                    Log.e("新牌局回调", "next_last==null");
                }
            }

            @Override // com.carrydream.cardrecorder.tool.OnOcr
            public void output(HashMap<String, Integer> hashMap) {
                Log.e("剩余的牌————————", hashMap.toString());
                if (king_d != null) {
                    king_d.setText(hashMap.get("king").toString());
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.set(hashMap, mainActivity.text_2, ExifInterface.GPS_MEASUREMENT_2D);
                    MainActivity mainActivity2 = MainActivity.this;
                    mainActivity2.set(hashMap, mainActivity2.text_3, ExifInterface.GPS_MEASUREMENT_3D);
                    MainActivity mainActivity3 = MainActivity.this;
                    mainActivity3.set(hashMap, mainActivity3.text_4, "4");
                    MainActivity mainActivity4 = MainActivity.this;
                    mainActivity4.set(hashMap, mainActivity4.text_5, "5");
                    MainActivity mainActivity5 = MainActivity.this;
                    mainActivity5.set(hashMap, mainActivity5.text_6, "6");
                    MainActivity mainActivity6 = MainActivity.this;
                    mainActivity6.set(hashMap, mainActivity6.text_7, "7");
                    MainActivity mainActivity7 = MainActivity.this;
                    mainActivity7.set(hashMap, mainActivity7.text_8, "8");
                    MainActivity mainActivity8 = MainActivity.this;
                    mainActivity8.set(hashMap, mainActivity8.text_9, "9");
                    MainActivity mainActivity9 = MainActivity.this;
                    mainActivity9.set(hashMap, mainActivity9.text_10, "10");
                    MainActivity mainActivity10 = MainActivity.this;
                    mainActivity10.set(hashMap, mainActivity10.text_J, "J");
                    MainActivity mainActivity11 = MainActivity.this;
                    mainActivity11.set(hashMap, mainActivity11.text_Q, "Q");
                    MainActivity mainActivity12 = MainActivity.this;
                    mainActivity12.set(hashMap, mainActivity12.text_K, "K");
                    MainActivity mainActivity13 = MainActivity.this;
                    mainActivity13.set(hashMap, mainActivity13.text_A, ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
                }
            }

            @Override // com.carrydream.cardrecorder.tool.OnOcr
            public void next(final List<List<String>> list) {
                Log.e("下家", list.toString());
                if (next != null) {
                    runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.MainActivity.2.2
                        @Override // java.lang.Runnable
                        public void run() {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (List<String> list2 : list) {
                                for (String str : list2) {
                                    stringBuffer.append(str);
                                }
                                stringBuffer.append(",");
                            }
                            next.setText(stringBuffer);
                        }
                    });
                }
            }

            @Override // com.carrydream.cardrecorder.tool.OnOcr
            public void last(final List<List<String>> list) {
                Log.e("上家", list.toString());
                if (last != null) {
                    runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.MainActivity.2.3
                        @Override // java.lang.Runnable
                        public void run() {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (List<String> list2 : list) {
                                for (String str : list2) {
                                    stringBuffer.append(str);
                                }
                                stringBuffer.append(",");
                            }
                            last.setText(stringBuffer);
                        }
                    });
                }
            }
        });
        this.add2.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.MainActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                m46lambda$init$2$comcarrydreamcardrecorderMainActivity(view);
            }
        });
        this.contrast2.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.MainActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick(view);
            }
        });

    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-MainActivity  reason: not valid java name */
    public /* synthetic */ void m44lambda$init$0$comcarrydreamcardrecorderMainActivity(View view) {
        new MlThread(this.mldataPath).start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$1$com-carrydream-cardrecorder-MainActivity  reason: not valid java name */
    public /* synthetic */ void m45lambda$init$1$comcarrydreamcardrecorderMainActivity(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$2$com-carrydream-cardrecorder-MainActivity  reason: not valid java name */
    public /* synthetic */ void m46lambda$init$2$comcarrydreamcardrecorderMainActivity(View view) {
        EasyPhotos.createAlbum((FragmentActivity) this, true, true, GlideEngine.getInstance()).start(new SelectCallback() { // from class: com.carrydream.cardrecorder.MainActivity.3
            @Override // com.huantansheng.easyphotos.callback.SelectCallback
            public void onCancel() {
            }

            @Override // com.huantansheng.easyphotos.callback.SelectCallback
            public void onResult(ArrayList<Photo> arrayList, boolean z) {
                File file = new File(arrayList.get(0).path);
                url2 = file.getAbsolutePath();
                Log.e("hjw", file.getAbsolutePath());
                Glide.with((FragmentActivity) MainActivity.this).load(file).into(image2);
            }
        });
    }

    public void set(HashMap<String, Integer> hashMap, TextView textView, String str) {
        int intValue = hashMap.get(str).intValue();
        textView.setText(intValue + "");
        if (intValue >= 4) {
            textView.setTextColor(getResources().getColor(R.color.red));
        } else {
            textView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTime() {
        this.timer = new Timer();
        this.task  = new TimerTask() { // from class: com.carrydream.cardrecorder.MainActivity.4
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                try {
                    Log.e("hjw", "______________________");

                    Log.e("timerTask doScreenCapture", "______________________");

                    doScreenCapture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        this.timer.schedule(task, 100, 500);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTime() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
        }
        this.timer = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:78:0x0316, code lost:
        if (r3.equals("O") != false) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0318, code lost:
        r11 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x031b, code lost:
        r11 = "J";
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0356, code lost:
        if ("club".equals(r2) == false) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0378, code lost:
        if (predict(r3).equals("O") != false) goto L75;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Bitmap FindMax(Bitmap bitmap, int i) {
        Bitmap bitmap2;
        Iterator<Rect> it;
        ArrayList arrayList;
        String str;
        Rect lately1 = null;
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
        ArrayList arrayList2 = new ArrayList();
        Imgproc.findContours(mat3, arrayList2, new Mat(), 3, 2);
        bitmap.getHeight();
        ArrayList arrayList3 = new ArrayList();
        int size = arrayList2.size();
        Rect rect2 = null;
        int i2 = 0;
        Mat mat4 = null;
        int i3 = 0;
        while (i2 < size) {
            MatOfPoint matOfPoint = (MatOfPoint) arrayList2.get(i2);
            Rect boundingRect = Imgproc.boundingRect(matOfPoint);
            arrayList3.add(boundingRect);
            ArrayList arrayList4 = arrayList2;
            Imgproc.rectangle(clone2, boundingRect.tl(), boundingRect.br(), new Scalar(0.0d, 0.0d, 255.0d, 255.0d), 1);
            int i4 = boundingRect.width * boundingRect.height;
            int i5 = size;
            ArrayList arrayList5 = arrayList3;
            if (boundingRect.tl().y < bitmap.getHeight() * 0.5d && boundingRect.br().y > bitmap.getHeight() * 0.5d && i4 > i3) {
                mat4 = new Mat(mat3, boundingRect);
                rect2 = Imgproc.boundingRect(matOfPoint);
                i3 = i4;
            }
            i2++;
            arrayList3 = arrayList5;
            size = i5;
            arrayList2 = arrayList4;
        }
        ArrayList arrayList6 = arrayList3;
        mat2.release();
        Bitmap createBitmap = Bitmap.createBitmap(clone2.cols(), clone2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clone2, createBitmap);
        saveBitmapToFile(createBitmap, i + "_", "Max");
        if (mat4 == null) {
            return null;
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(mat4.cols(), mat4.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat4, createBitmap2);
        saveBitmapToFile(createBitmap2, i + "_", i + "Max");
        float f = 1.0f;
        if (i != 0) {
            float height2 = (i3 * 1.0f) / (bitmap.getHeight() * bitmap.getWidth());
            if (height2 < 0.0568d) {
                Log.e("面积比", height2 + "");
                return null;
            }
        }
        ArrayList arrayList7 = new ArrayList();
        new ArrayList();
        Iterator<Rect> it2 = arrayList6.iterator();
        while (it2.hasNext()) {
            Rect next = it2.next();
            if (isExist(rect2, next)) {
                Mat mat5 = new Mat(mat3, next);
                float f2 = (next.width * f) / next.height;
                if (next.width >= 10 && next.height >= 15 && next.width <= 90 && next.height <= 90) {
                    Utils.matToBitmap(mat5, Bitmap.createBitmap(mat5.cols(), mat5.rows(), Bitmap.Config.ARGB_8888));
                    String predict = predict(mat5);
                    if (!"X".equals(predict) && !"O".equals(predict) && !"diamond".equals(predict) && !"spade".equals(predict) && !"heart".equals(predict) && !"club".equals(predict)) {
                        predict.hashCode();
                        char c = 65535;
                        it = it2;
                        bitmap2 = createBitmap2;
                        switch (predict.hashCode()) {
                            case 74:
                                str = "king";
                                if (predict.equals("J")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 75:
                                str = "king";
                                if (predict.equals("K")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 1567:
                                str = "king";
                                if (predict.equals("10")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 3292055:
                                if (predict.equals("king")) {
                                    str = "king";
                                    c = 3;
                                    break;
                                }
                            default:
                                str = "king";
                                break;
                        }
                        switch (c) {
                            case 0:
                                arrayList = arrayList6;
                                Mat lately = lately(mat3, rect2, next, arrayList);
                                Utils.matToBitmap(lately, Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888));
                                break;
                            case 1:
                                arrayList = arrayList6;
                                Mat lately2 = lately(mat3, rect2, next, arrayList);
                                Utils.matToBitmap(lately2, Bitmap.createBitmap(lately2.cols(), lately2.rows(), Bitmap.Config.ARGB_8888));
                                String predict2 = predict(lately2);
                                Log.e("k的下面是", predict2);
                                if (!"diamond".equals(predict2)) {
                                    if (!"spade".equals(predict2)) {
                                        if (!"heart".equals(predict2)) {
                                            break;
                                        }
                                    }
                                }
                                arrayList7.add(predict);
                                arrayList6 = arrayList;
                                createBitmap2 = bitmap2;
                                f = 1.0f;
                                it2 = it;
                                break;
                            case 2:
                                arrayList = arrayList6;
                                arrayList7.add(predict);
                                arrayList6 = arrayList;
                                createBitmap2 = bitmap2;
                                f = 1.0f;
                                it2 = it;
                                break;
                            case 3:
                                arrayList = arrayList6;
                                Mat lately3 = lately(mat3, rect2, next, arrayList);
                                Bitmap createBitmap3 = Bitmap.createBitmap(lately3.cols(), lately3.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(lately3, createBitmap3);
                                String predict3 = predict(lately3);
                                saveBitmapToFile(createBitmap3, "我", predict3);
                                break;
                            default:
                                if (next.height - next.width < 4 || f2 < 0.58f) {
                                    it2 = it;
                                    createBitmap2 = bitmap2;
                                    f = 1.0f;
                                    break;
                                } else {
                                    arrayList = arrayList6;
                                    Mat mat6 = new Mat(mat3, lately1(next, arrayList));
                                    String predict4 = predict(mat6);
                                    Log.e(predict + "的下面是", predict4 + "_" + next.toString() + "_" + lately1.toString());
                                    Bitmap createBitmap4 = Bitmap.createBitmap(mat6.cols(), mat6.rows(), Bitmap.Config.ARGB_8888);
                                    Utils.matToBitmap(mat6, createBitmap4);
                                    saveBitmapToFile(createBitmap4, "下面", predict + "_" + predict4);
                                    arrayList7.add(predict);
                                    arrayList6 = arrayList;
                                    createBitmap2 = bitmap2;
                                    f = 1.0f;
                                    it2 = it;
                                    break;
                                }
                        }
                    }
                }
            }
            bitmap2 = createBitmap2;
            it = it2;
            arrayList = arrayList6;
            arrayList6 = arrayList;
            createBitmap2 = bitmap2;
            f = 1.0f;
            it2 = it;
        }
        Bitmap bitmap3 = createBitmap2;
        Log.e("出牌" + i, arrayList7.toString());
        this.pokerTool.play(i, Tool.sort(arrayList7));
        return bitmap3;
    }

    public String FindColor(String str, Mat mat, Rect rect, Rect rect2, List<Rect> list) {
        Mat lately = lately(mat, rect, rect2, list);
        String predict = predict(lately);
        Bitmap createBitmap = Bitmap.createBitmap(lately.cols(), lately.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(lately, createBitmap);
        if ("diamond".equals(predict) || "spade".equals(predict) || "heart".equals(predict) || "club".equals(predict)) {
            saveBitmapToFile(createBitmap, "花色", predict + "_" + str);
            return predict;
        }
        saveBitmapToFile(createBitmap, "不是花色", predict);
        return "不是花色" + predict;
    }

    public boolean isExist(Rect rect, Rect rect2) {
        return rect2.x > rect.x && rect2.x + rect2.width < rect.x + rect.width && rect2.y > rect.y && rect2.y + rect2.height < rect.y + rect.height;
    }

    private String predict(Mat mat) {
        Mat mat2 = new Mat(40, 60, mat.type());
        Imgproc.resize(mat, mat2, new Size(40.0d, 60.0d));
        MatOfFloat matOfFloat = new MatOfFloat();
        DataPool.getHogDescriptor().compute(mat2, matOfFloat);
        return new File(DataPool.getPredicatedResult(DataPool.getkNearest().predict(matOfFloat.reshape(0, 1), new Mat()))).getParentFile().getName();
    }

    private Mat lately(Mat mat, Rect rect, Rect rect2, List<Rect> list) {
        Rect rect3 = null;
        double d = Double.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            Rect rect4 = list.get(i);
            if (rect4.width >= 10 && ((rect2.x != rect4.x || rect4.y != rect4.y) && rect4.y >= rect2.y + rect2.height)) {
                double pow = Math.pow(Math.abs((rect2.x + (rect2.width * 0.5d)) - (rect4.x + (rect4.width * 0.5d))), 2.0d) + Math.pow(Math.abs((rect2.y + rect2.height) - rect4.y), 2.0d);
                if (pow < d) {
                    rect3 = rect4;
                    d = pow;
                }
            }
        }
        Mat mat2 = new Mat(mat, rect3);
        Bitmap createBitmap = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat2, createBitmap);
        saveBitmapToFile(createBitmap, "最近的", rect2.toString());
        return mat2;
    }

    private Rect lately1(Rect rect, List<Rect> list) {
        Rect rect2 = null;
        double d = Double.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            Rect rect3 = list.get(i);
            if (rect3.width >= 10 && ((rect.x != rect3.x || rect.y != rect3.y) && rect3.y >= rect.y + rect.height)) {
                double pow = Math.pow(Math.abs((rect.x + (rect.width * 0.5d)) - (rect3.x + (rect3.width * 0.5d))), 2.0d) + Math.pow(Math.abs((rect.y + rect.height) - rect3.y), 2.0d);
                if (pow < d) {
                    Log.e("小于", pow + "<" + pow + rect3.toString());
                    rect2 = rect3;
                    d = pow;
                }
            }
        }
        return rect2;
    }

    private void initData() {
        MediaProjectionHelper.getInstance().setNotificationEngine(new MediaProjectionNotificationEngine() { // from class: com.carrydream.cardrecorder.MainActivity$$ExternalSyntheticLambda4
            @Override // com.mask.mediaprojection.interfaces.MediaProjectionNotificationEngine
            public final Notification getNotification() {
                return m47lambda$initData$3$comcarrydreamcardrecorderMainActivity();
            }
        });
        languageIsExists();
        if (Build.VERSION.SDK_INT >= 29) {
            LogUtil.i("Environment.isExternalStorageLegacy: " + Environment.isExternalStorageLegacy());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initData$3$com-carrydream-cardrecorder-MainActivity  reason: not valid java name */
    public /* synthetic */ Notification m47lambda$initData$3$comcarrydreamcardrecorderMainActivity() {
        String string = getString(R.string.app_name);
        return NotificationHelper.getInstance().createSystem().setOngoing(true).setTicker(string).setContentText(string).setDefaults(-1).build();
    }

    private void doServiceStart() {
        MediaProjectionHelper.getInstance().startService(this);
    }

    private void doServiceStop() {
        MediaProjectionHelper.getInstance().stopService(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScreenCapture() {
        MediaProjectionHelper.getInstance().capture(new ScreenCaptureCallback() { // from class: com.carrydream.cardrecorder.MainActivity.5
            @Override // com.mask.mediaprojection.interfaces.ScreenCaptureCallback
            public void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);

                Log.e("TAG", "ScreenCapture onSuccess");


                getBitmap(bitmap, 0, bitmap.getHeight() / 2, bitmap.getWidth(), (bitmap.getHeight() / 2) - DensityUtils.dp2px(MainActivity.this, 40.0f));
                int width = (bitmap.getWidth() / 2) / 4;
                int height = (bitmap.getHeight() / 2) / 4;
                Bitmap bitmap2 = getBitmap(bitmap, bitmap.getWidth() / 2, height, (bitmap.getWidth() / 2) - width, (bitmap.getHeight() / 2) - height);
                getBitmap(bitmap, (bitmap.getWidth() / 2) / 4, height, (bitmap.getWidth() / 2) - width, (bitmap.getHeight() / 2) - height);
                FindMax(bitmap2, 1);
            }

            @Override // com.mask.mediaprojection.interfaces.ScreenCaptureCallback
            public void onFail() {
                super.onFail();
                Log.e("TAG", "ScreenCapture onFail");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return Tool.BinaryImage(Bitmap.createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false), 175);
    }

    private void doMediaRecorderStart() {
        MediaProjectionHelper.getInstance().startMediaRecorder(new MediaRecorderCallback() { // from class: com.carrydream.cardrecorder.MainActivity.6
            @Override // com.mask.mediaprojection.interfaces.MediaRecorderCallback
            public void onSuccess(File file) {
                super.onSuccess(file);
                LogUtil.i("MediaRecorder onSuccess: " + file.getAbsolutePath());
            }

            @Override // com.mask.mediaprojection.interfaces.MediaRecorderCallback
            public void onFail() {
                super.onFail();
                LogUtil.e("MediaRecorder onFail");
            }
        });
    }

    private void doMediaRecorderStop() {
        MediaProjectionHelper.getInstance().stopMediaRecorder();
    }

    private void saveBitmapToFile(Bitmap bitmap, String str) {
        saveBitmapToFile(bitmap, str, ExifInterface.GPS_MEASUREMENT_3D);
    }

    private void saveBitmapToFile(Bitmap bitmap, final String str, String str2) {
        Tool.saveBitmapToFile(this, bitmap, str, str2, new SaveBitmapCallback() { // from class: com.carrydream.cardrecorder.MainActivity.7
            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onSuccess(File file) {
                super.onSuccess(file);
                Log.i("结果", str + "------------" + file.getAbsolutePath());
            }

            @Override // com.carrydream.cardrecorder.tool.SaveBitmapCallback
            public void onFail(Exception exc) {
                super.onFail(exc);
                LogUtil.e("Save onError");
                exc.printStackTrace();
            }
        });
    }

    private void languageIsExists() {
        File file = new File(getExternalFilesDir(this.FILE_NAME), this.LANGUAGE_NAME);
        if (file.exists()) {
            return;
        }
        copyToSdCard(this.LANGUAGE_NAME, file);
    }

    private void copyToSdCard(String str, File file) {
        try {
            InputStream open = getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("hjw", e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClick(View view) {
        doServiceStart();
        EasyFloat.with(this).setLayout(R.layout.layout_pop)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_BOTTOM)
                .setTag("tag1").setDragEnable(true)
                .hasEditText(false)
                .setLocation(0, DensityUtils.getScreenWidth(this) - DensityUtils.dp2px(this, 40.0f))
                .setMatchParent(false, false)
                .setAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() { // from class: com.carrydream.cardrecorder.MainActivity.8
            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void createdResult(boolean z, String str, View view2) {
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void show(View view2) {
                Log.e("show", "show");
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void hide(View view2) {
                Log.e("hide", "hide");
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void dismiss() {
                Log.e("dismiss", "dismiss");
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void touchEvent(View view2, MotionEvent motionEvent) {
                Log.e("touchEvent", "touchEvent");
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void drag(View view2, MotionEvent motionEvent) {
                Log.e("drag", "drag");
            }

            @Override // com.lzf.easyfloat.interfaces.OnFloatCallbacks
            public void dragEnd(View view2) {
                Log.e("dragEnd", "dragEnd");
            }
        }).show();
    }

    /* loaded from: classes.dex */
    class MlThread extends Thread {
        String path;

        public MlThread(String str) {
            this.path = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.e("hjw", "拷贝样本数据到sdcard");
            File file = new File(getExternalCacheDir(), "/good_data.zip");
            if (!file.exists()) {
                Tool.copyAssets(MainActivity.this, "good_data.zip", file.getAbsolutePath());
            }
            Tool.unZip(file, getExternalCacheDir() + File.separator);
            runOnUiThread(new Runnable() { // from class: com.carrydream.cardrecorder.MainActivity.MlThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.e("hjw", "解压完成");
                }
            });
            File[] listFiles = new File(this.path).listFiles();
            DataPool.clear();
            for (File file2 : listFiles) {
                if (!file2.isFile()) {
                    MlData createMlDataFromDirectory = MlData.createMlDataFromDirectory(file2, MainActivity.this);
                    if (createMlDataFromDirectory == null) {
                        Log.i("hjw", "ret:" + createMlDataFromDirectory.toString());
                    } else {
                        DataPool.addMlData(createMlDataFromDirectory.getLabel(), createMlDataFromDirectory);
                    }
                }
            }
            DataPool.init();
        }
    }
}
