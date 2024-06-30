package com.carrydream.cardrecorder.tool;

import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Order;
import com.tencent.mm.opensdk.modelpay.PayReq;

/* loaded from: classes.dex */
public class WxPay {
    public static void Pay(Order order) {
        PayReq payReq = new PayReq();
        payReq.appId = order.getParams().getAppid();
        payReq.partnerId = order.getParams().getPartnerid();
        payReq.prepayId = order.getParams().getPrepayid();
        payReq.packageValue = order.getParams().getPackageX();
        payReq.nonceStr = order.getParams().getNoncestr();
        payReq.timeStamp = order.getParams().getTimestamp();
        payReq.sign = order.getParams().getSign();
        BaseApplication.MyWxApi.sendReq(payReq);
    }
}
