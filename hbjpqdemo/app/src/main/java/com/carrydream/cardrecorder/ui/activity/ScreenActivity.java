package com.carrydream.cardrecorder.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.carrydream.cardrecorder.tool.EventUtil;
import com.carrydream.cardrecorder.tool.MyToast;
import com.mask.mediaprojection.utils.MediaProjectionHelper;

/* loaded from: classes.dex */
public class ScreenActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().setDimAmount(0.0f);
        doServiceStart();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        EventUtil.register(this);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            MediaProjectionHelper.getInstance().createVirtualDisplay(i, i2, intent, true, true);
            return;
        }
        MyToast.show("授权取消");
        MediaProjectionHelper.getInstance().stopService(this);
    }

    private boolean doServiceStart() {
        return MediaProjectionHelper.getInstance().startService(this);
    }

    private void doServiceStop() {
        MediaProjectionHelper.getInstance().stopService(this);
    }
}
