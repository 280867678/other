package com.carrydream.cardrecorder.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.widget.FrameLayout;
import butterknife.BindView;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Captcha;
import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Sms;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.ui.Module.LauncherModule;
import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter;
//import com.carrydream.cardrecorder.ui.component.DaggerLauncherComponent;
import com.carrydream.cardrecorder.ui.component.DaggerLauncherComponent;
import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
import com.hb.aiyouxiba.R;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class LauncherActivity extends BaseActivity implements LauncherContacts.View {
    @Inject
    LauncherPresenter Presenter;
    @BindView(R.id.frameLayout)
    FrameLayout framelayout;

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnCaptcha(BaseResult<Captcha> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnCheck(BaseResult<Sms> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnLogin(BaseResult<Info> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnSms_code(BaseResult<Sms> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<LauncherContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return BaseView.CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_launcher;
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        DaggerLauncherComponent.builder().appComponent(BaseApplication.getAppComponent()).launcherModule(new LauncherModule(this)).build().inject(this);
        this.Presenter.conf();
        new Handler().postDelayed(new Runnable() { // from class: com.carrydream.cardrecorder.ui.activity.LauncherActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LauncherActivity.this.m100xc55dbbc5();
            }
        }, 1000L);
//        if (DataUtils.getInstance().getUser() != null) {
//            this.Presenter.Info();
//        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-ui-activity-LauncherActivity  reason: not valid java name */
    public /* synthetic */ void m100xc55dbbc5() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnConf(BaseResult<Config> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setConfig(baseResult.getData());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnInfo(BaseResult<User> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setUser(baseResult.getData());
        }
    }
}
