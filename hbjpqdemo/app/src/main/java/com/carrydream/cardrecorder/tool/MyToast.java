package com.carrydream.cardrecorder.tool;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.carrydream.cardrecorder.BaseApplication;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class MyToast {
    private static Toast mToast;

    public static void show(String str) {
        Toast toast = new Toast(BaseApplication.getInstance());
        mToast = toast;
        toast.setGravity(17, 0, 0);
        View inflate = View.inflate(BaseApplication.getInstance(), R.layout.my_toast_layout, null);
        ((TextView) inflate.findViewById(R.id.toast_tv)).setText(str);
        mToast.setView(inflate);
        mToast.setDuration(0);
        mToast.show();
    }
}
