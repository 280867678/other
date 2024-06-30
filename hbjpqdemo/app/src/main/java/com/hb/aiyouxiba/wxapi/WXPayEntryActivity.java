package com.hb.aiyouxiba.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.base.BaseMessage;
import com.carrydream.cardrecorder.tool.EventUtil;
import com.carrydream.cardrecorder.tool.MyToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/* loaded from: classes2.dex */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BaseApplication.MyWxApi.handleIntent(getIntent(), this);
    }

    @Override // com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
    public void onReq(BaseReq baseReq) {
        Log.e("微信支付", "onReq");
    }

    @Override // com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
    public void onResp(BaseResp baseResp) {
        Log.e("微信支付", "onReq" + baseResp.errCode);
        if (baseResp.getType() == 5) {
            int i = baseResp.errCode;
            if (i == 0) {
                MyToast.show("微信支付成功");
                EventUtil.post(new BaseMessage(false, 1));
            } else if (i == -1) {
                MyToast.show("微信支付失败");
                EventUtil.post(new BaseMessage(false, 1));
            } else if (i == -2) {
                MyToast.show("微信支付取消");
                EventUtil.post(new BaseMessage(false, 1));
            }
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        EventUtil.register(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        BaseApplication.MyWxApi.handleIntent(intent, this);
    }
}
