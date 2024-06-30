package com.carrydream.cardrecorder.ui.Presenter;

import android.util.Log;
import com.carrydream.cardrecorder.Model.Order;
import com.carrydream.cardrecorder.Model.Plans;
import com.carrydream.cardrecorder.Model.Update;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.retrofit.BaseCallback;
import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class AccountPresenter implements AccountContacts.Presenter {
    private String TAG = "微信支付";
    private Api api;
    private AccountContacts.View view;

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
    public AccountPresenter(Api api, AccountContacts.View view) {
        this.view = view;
        this.api = api;
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.Presenter
    public void upgrade(String str, String str2, String str3, final boolean z) {
        this.api.upgrade(str, str2, str3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Update>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.AccountPresenter.1
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str4) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Update> baseResult) {
                AccountPresenter.this.view.OnUpgrade(baseResult, z);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.Presenter
    public void plans() {
        this.api.plans().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Plans>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.AccountPresenter.2
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Plans> baseResult) {
                AccountPresenter.this.view.OnPlans(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.Presenter
    public void order(String str) {
        this.api.order(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Order>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.AccountPresenter.3
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Order> baseResult) {
                AccountPresenter.this.view.OnOrder(baseResult);
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str2) {
                String str3 = AccountPresenter.this.TAG;
                Log.e(str3, "onFailure: " + str2);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.Presenter
    public void logout() {
        this.api.logout().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Object>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.AccountPresenter.4
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Object> baseResult) {
                AccountPresenter.this.view.OnLogOut(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.Presenter
    public void Info() {
        this.api.info().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<User>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.AccountPresenter.5
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<User> baseResult) {
                AccountPresenter.this.view.OnInfo(baseResult);
            }
        });
    }
}
