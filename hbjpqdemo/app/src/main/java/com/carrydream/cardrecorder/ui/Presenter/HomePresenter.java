package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.retrofit.BaseCallback;
import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class HomePresenter implements HomeContacts.Presenter {
    private Api api;
    private HomeContacts.View view;

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
    public HomePresenter(Api api, HomeContacts.View view) {
        this.view = view;
        this.api = api;
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.HomeContacts.Presenter
    public void support() {
        this.api.support().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<List<Platform>>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.HomePresenter.1
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<List<Platform>> baseResult) {
                HomePresenter.this.view.OnSupport(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.HomeContacts.Presenter
    public void report(final int i, final String str) {
        this.api.report(i, str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Object>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.HomePresenter.2
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str2) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Object> baseResult) {
                HomePresenter.this.view.OnReport(baseResult, i, str);
            }
        });
    }
}
