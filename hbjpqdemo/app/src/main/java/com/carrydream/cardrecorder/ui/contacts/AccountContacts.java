package com.carrydream.cardrecorder.ui.contacts;

import com.carrydream.cardrecorder.Model.Order;
import com.carrydream.cardrecorder.Model.Plans;
import com.carrydream.cardrecorder.Model.Update;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.BasePresenter;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.trello.rxlifecycle3.android.ActivityEvent;

/* loaded from: classes.dex */
public interface AccountContacts {

    /* loaded from: classes.dex */
    public interface Presenter extends BasePresenter {
        void Info();

        void logout();

        void order(String str);

        void plans();

        void upgrade(String str, String str2, String str3, boolean z);
    }

    /* loaded from: classes.dex */
    public interface View extends BaseView<Presenter, ActivityEvent> {
        void OnInfo(BaseResult<User> baseResult);

        void OnLogOut(BaseResult<Object> baseResult);

        void OnOrder(BaseResult<Order> baseResult);

        void OnPlans(BaseResult<Plans> baseResult);

        void OnUpgrade(BaseResult<Update> baseResult, boolean z);
    }
}
