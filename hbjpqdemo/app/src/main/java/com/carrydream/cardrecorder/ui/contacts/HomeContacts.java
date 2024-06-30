package com.carrydream.cardrecorder.ui.contacts;

import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.base.BasePresenter;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.trello.rxlifecycle3.android.ActivityEvent;
import java.util.List;

/* loaded from: classes.dex */
public interface HomeContacts {

    /* loaded from: classes.dex */
    public interface Presenter extends BasePresenter {
        void report(int i, String str);

        void support();
    }

    /* loaded from: classes.dex */
    public interface View extends BaseView<Presenter, ActivityEvent> {
        void OnReport(BaseResult<Object> baseResult, int i, String str);

        void OnSupport(BaseResult<List<Platform>> baseResult);
    }
}
