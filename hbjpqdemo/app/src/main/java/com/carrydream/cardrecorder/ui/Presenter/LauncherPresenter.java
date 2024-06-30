package com.carrydream.cardrecorder.ui.Presenter;

import android.util.Log;

import com.carrydream.cardrecorder.Model.Captcha;
import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Sms;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.retrofit.BaseCallback;
import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class LauncherPresenter implements LauncherContacts.Presenter {
    private Api api;
    private LauncherContacts.View view;

    @Override // com.carrydream.cardrecorder.base.BasePresenter
    public void onDestroy() {
    }

    @Override // com.carrydream.cardrecorder.base.BasePresenter
    public void start() {
    }

    @Override // com.carrydream.cardrecorder.base.BasePresenter
    public void stop() {
    }

    @Inject
    public LauncherPresenter(Api api, LauncherContacts.View view) {
        this.view = view;
        this.api = api;
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void captcha(String str) {
        this.api.captcha(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Captcha>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.1
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str2) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Captcha> baseResult) {
                LauncherPresenter.this.view.OnCaptcha(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void check(String str, String str2, String str3) {
        this.api.check(str2, str3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Sms>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.2
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str4) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Sms> baseResult) {
                LauncherPresenter.this.view.OnCheck(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void sms_code(String str) {
        this.api.sms_code(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Sms>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.3
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str2) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Sms> baseResult) {
                LauncherPresenter.this.view.OnSms_code(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void login(String str, String str2) {
        this.api.login(str, str2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Info>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.4
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str3) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Info> baseResult) {
//               String gson= new Gson().toJson(baseResult);

                Log.e("api.login登录返回数据：Message:",baseResult.getMessage()+"  Status::*  "+baseResult.getStatus()+ "  Data::*  "+baseResult.getData());
//                Info{token='107810|b7PxdVOp3mE9qtFX2ucl9jrUQBnE60yKhJfkLyDH', user=UserDTO{mobile='17779580850', package_tag='jpq_app', reg_city='中国-江西-宜春', reg_version_code=null, updated_at='2024-04-08 09:28:57', created_at='2024-04-08 09:28:57', id=3079}, location=LocationDTO{ip='182.110.142.226', province_code=36, province_name='江西', city=[中国, 江西, 宜春]}}
                LauncherPresenter.this.view.OnLogin(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void Info() {
        this.api.info().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<User>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.5
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<User> baseResult) {
                LauncherPresenter.this.view.OnInfo(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.Presenter
    public void conf() {
        this.api.conf().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Config>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter.6
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Config> baseResult) {
                LauncherPresenter.this.view.OnConf(baseResult);
            }
        });
    }
}
