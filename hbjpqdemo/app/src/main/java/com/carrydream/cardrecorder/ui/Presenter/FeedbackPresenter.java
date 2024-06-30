package com.carrydream.cardrecorder.ui.Presenter;

import com.carrydream.cardrecorder.Model.Picture;
import com.carrydream.cardrecorder.Model.Pictures;
import com.carrydream.cardrecorder.base.Api;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.retrofit.BaseCallback;
import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/* loaded from: classes.dex */
public class FeedbackPresenter implements FeedbackContacts.Presenter {
    private Api api;
    private FeedbackContacts.View view;

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
    public FeedbackPresenter(Api api, FeedbackContacts.View view) {
        this.view = view;
        this.api = api;
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.Presenter
    public void feedback(String str, String str2, String[] strArr) {
        this.api.feedback(str, str2, strArr).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Object>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter.1
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str3) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Object> baseResult) {
                FeedbackPresenter.this.view.OnFeedback(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.Presenter
    public void images(List<String> list) {
        list.remove("add");
        this.api.images(getMultipartBody(list, "files[]")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Pictures>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter.2
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Pictures> baseResult) {
                FeedbackPresenter.this.view.OnImages(baseResult);
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.Presenter
    public void image(String str) {
        File file = new File(str);
        this.api.image(MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseCallback<BaseResult<Picture>>() { // from class: com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter.3
            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFailure(String str2) {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onFinish() {
            }

            @Override // com.carrydream.cardrecorder.retrofit.BaseCallback
            public void onSuccess(BaseResult<Picture> baseResult) {
                FeedbackPresenter.this.view.OnImage(baseResult);
            }
        });
    }

    public static List<MultipartBody.Part> getMultipartBody(List<String> list, String str) {
        ArrayList arrayList = new ArrayList();
        for (String str2 : list) {
            File file = new File(str2);
            arrayList.add(MultipartBody.Part.createFormData(str, file.getName(), MultipartBody.create(MediaType.parse("multipart/form-data"), file)));
        }
        return arrayList;
    }
}
