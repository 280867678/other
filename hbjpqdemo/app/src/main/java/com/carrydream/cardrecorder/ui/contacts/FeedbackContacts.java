package com.carrydream.cardrecorder.ui.contacts;

import com.carrydream.cardrecorder.Model.Picture;
import com.carrydream.cardrecorder.Model.Pictures;
import com.carrydream.cardrecorder.base.BasePresenter;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.trello.rxlifecycle3.android.ActivityEvent;
import java.util.List;

/* loaded from: classes.dex */
public interface FeedbackContacts {

    /* loaded from: classes.dex */
    public interface Presenter extends BasePresenter {
        void feedback(String str, String str2, String[] strArr);

        void image(String str);

        void images(List<String> list);
    }

    /* loaded from: classes.dex */
    public interface View extends BaseView<Presenter, ActivityEvent> {
        void OnFeedback(BaseResult<Object> baseResult);

        void OnImage(BaseResult<Picture> baseResult);

        void OnImages(BaseResult<Pictures> baseResult);
    }
}
