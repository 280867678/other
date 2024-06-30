package com.carrydream.cardrecorder.retrofit;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.net.SocketTimeoutException;
import retrofit2.HttpException;

/* loaded from: classes.dex */
public abstract class BaseCallback<T> implements Observer<T> {
    public abstract void onFailure(String str);

    public abstract void onFinish();

    @Override // io.reactivex.Observer
    public void onSubscribe(Disposable disposable) {
    }

    public abstract void onSuccess(T t);

    @Override // io.reactivex.Observer
    public void onNext(T t) {
//        Log.e("BaseCallback onNext登录返回数据：",  t.toString());
//        Log.e("api.login登录返回数据：",t.getMessage()+"  :: "+t.getStatus()+ "  ::  "+t.getData());

        new Gson().toJson(t);
        onSuccess(t);
    }

    @Override // io.reactivex.Observer
    public void onError(Throwable th) {
        th.printStackTrace();
        if (th instanceof HttpException) {
            HttpException httpException = (HttpException) th;
            int code = httpException.code();
            String message = httpException.getMessage();
            if (code == 504) {
                message = "网络不给力-code" + code;
            } else if (code == 502 || code == 404) {
                message = "服务器异常，请稍后再试-code" + code;
            }
            onFailure(message);
            Log.e("onFailure", message);
        } else if (th instanceof SocketTimeoutException) {
            onFailure("网络异常,请检查您的网络!");
            Log.e("onFailure", "网络请求超时");
        } else if (th instanceof JsonSyntaxException) {
            onFailure("返回内容格式错误");
            Log.e("onFailure", "返回内容格式错误");
        } else {
            onFailure(th.getMessage());
            Log.e("onFailure", th.getMessage());
        }
        onFinish();
    }

    @Override // io.reactivex.Observer
    public void onComplete() {
        onFinish();
    }
}
