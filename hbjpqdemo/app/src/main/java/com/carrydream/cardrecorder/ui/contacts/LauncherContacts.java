package com.carrydream.cardrecorder.ui.contacts;

import com.carrydream.cardrecorder.Model.Captcha;
import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Sms;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.BasePresenter;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.trello.rxlifecycle3.android.ActivityEvent;

/* loaded from: classes.dex */
public interface LauncherContacts {

    /* loaded from: classes.dex */
    public interface Presenter extends BasePresenter {
        void Info();

        void captcha(String str);

        void check(String str, String str2, String str3);

        void conf();

        void login(String str, String str2);

        void sms_code(String str);
    }

    /* loaded from: classes.dex */
    public interface View extends BaseView<Presenter, ActivityEvent> {
        void OnCaptcha(BaseResult<Captcha> baseResult);

        void OnCheck(BaseResult<Sms> baseResult);

        void OnConf(BaseResult<Config> baseResult);

        void OnInfo(BaseResult<User> baseResult);

        void OnLogin(BaseResult<Info> baseResult);

        void OnSms_code(BaseResult<Sms> baseResult);
    }
}
