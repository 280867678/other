package com.carrydream.cardrecorder.retrofit;

import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.Tool;
//import com.google.common.net.HttpHeaders;
import com.hb.aiyouxiba.R;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/* loaded from: classes.dex */
public class CommonInterceptor implements Interceptor {
    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        if (DataUtils.getInstance().getInfo() != null) {
            Request.Builder newBuilder = request.newBuilder();
            Request.Builder header = newBuilder.header("Authorization", "Bearer " + DataUtils.getInstance().getInfo().getToken() + "");
            StringBuilder sb = new StringBuilder();
            sb.append(Tool.getAppVersionNo(BaseApplication.getInstance()));
            sb.append("");
            request = header.header("version-code", sb.toString()).header("package-tag", BaseApplication.getInstance().getString(R.string.channel)).build();
        }
        request.headers();
        return chain.proceed(request);
    }
}
