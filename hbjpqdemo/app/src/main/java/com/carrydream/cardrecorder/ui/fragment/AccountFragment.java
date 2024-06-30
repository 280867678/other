package com.carrydream.cardrecorder.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Order;
import com.carrydream.cardrecorder.Model.Plans;
import com.carrydream.cardrecorder.Model.Update;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.BaseFragment;
import com.carrydream.cardrecorder.base.BaseMessage;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.tool.CacheUtils;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.RxConstUtils;
import com.carrydream.cardrecorder.tool.RxTimeUtils;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.ui.Module.AccountModule;
import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter;
import com.carrydream.cardrecorder.ui.activity.AboutActivity;
import com.carrydream.cardrecorder.ui.activity.FeedbackActivity;
import com.carrydream.cardrecorder.ui.activity.HtmlActivity;
import com.carrydream.cardrecorder.ui.activity.LoginActivity;
import com.carrydream.cardrecorder.ui.activity.VipActivity;
//import com.carrydream.cardrecorder.ui.component.DaggerAccountComponent;
import com.carrydream.cardrecorder.ui.component.DaggerAccountComponent;
import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import com.carrydream.cardrecorder.ui.dialog.IsDialog;
import com.carrydream.cardrecorder.ui.dialog.UpdateDialog;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/* loaded from: classes.dex */
public class AccountFragment extends BaseFragment implements AccountContacts.View {
    @BindView(R.id.Exit)
    TextView Exit;
    @BindView(R.id.account)
    TextView account;
    @BindView(R.id.cache)
    LinearLayout cache;
    @BindView(R.id.cache_txt)
    TextView cache_txt;
    @BindView(R.id.login)
    RelativeLayout login;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.open_vip)
    TextView open_vip;
    @Inject
    AccountPresenter presenter;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.version)
    LinearLayout version;
    @BindView(R.id.version_code)
    TextView version_code;
    @BindView(R.id.version_red)
    View version_red;
    @BindView(R.id.vip)
    RelativeLayout vip;

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnOrder(BaseResult<Order> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnPlans(BaseResult<Plans> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<AccountContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    protected void lazyLoad() {
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    public int setLayoutId() {
        return R.layout.account_fragment;
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    protected void init() {
        DaggerAccountComponent.builder().appComponent(BaseApplication.getAppComponent()).accountModule(new AccountModule(this)).build().inject(this);
        getVersion(false);
        TextView textView = this.version_code;
        textView.setText("v " + Tool.getAppVersionName(getContext()));
    }

    @OnClick({R.id.version, R.id.login, R.id.vip, R.id.Exit, R.id.cache, R.id.about, R.id.feedback, R.id.service, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Exit /* 2131230728 */:
                IsDialog isDialog = new IsDialog(getActivity(), "您是否确认要退出登录？");
                isDialog.setNewListener(new IsDialog.NewSubmit() { // from class: com.carrydream.cardrecorder.ui.fragment.AccountFragment$$ExternalSyntheticLambda0
                    @Override // com.carrydream.cardrecorder.ui.dialog.IsDialog.NewSubmit
                    public final void yes(View view2) {
                        AccountFragment.this.m129xea318061(view2);
                    }
                });
                isDialog.show();
                return;
            case R.id.about /* 2131230741 */:
                startActivity(new Intent(getContext(), AboutActivity.class));
                return;
            case R.id.cache /* 2131230841 */:
                CacheUtils.clearAllCache(getContext());
                showCache();
                return;
            case R.id.feedback /* 2131230946 */:
//                if (DataUtils.getInstance().getUser() != null) {
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
//                    return;
//                } else {
//                    startActivity(new Intent(getContext(), LoginActivity.class));
//                    return;
//                }
            case R.id.help /* 2131230986 */:
                Intent intent = new Intent(getContext(), HtmlActivity.class);
                intent.putExtra("title", "帮助");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getHelp_h5());
                startActivity(intent);
                return;
            case R.id.login /* 2131231073 */:
                is_login();
                return;
            case R.id.service /* 2131231251 */:
                Tool.openQQ(getContext(), DataUtils.getInstance().getConfig().getCustomer_service_qq());
                return;
            case R.id.version /* 2131231387 */:
                getVersion(true);
                return;
            case R.id.vip /* 2131231399 */:
                is_login();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$0$com-carrydream-cardrecorder-ui-fragment-AccountFragment  reason: not valid java name */
    public /* synthetic */ void m129xea318061(View view) {
        this.presenter.logout();
        DataUtils.getInstance().setUser(null);
        DataUtils.getInstance().setInfo(null);
        setState();
    }

    public void is_login() {
//        if (DataUtils.getInstance().getUser() != null) {
            startActivity(new Intent(getContext(), VipActivity.class));
//        } else {
//            startActivity(new Intent(getContext(), LoginActivity.class));
//        }
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    public void onEventBase(BaseMessage<String> baseMessage) {
        super.onEventBase(baseMessage);
        if (1 == baseMessage.getCode()) {
            this.presenter.Info();
        }
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setState();
    }

    @SuppressLint("WrongConstant")
    public void setState() {
//        if (DataUtils.getInstance().getUser() != null) {
//            User user = DataUtils.getInstance().getUser();
//            this.account.setText(Tool.phone(user.getUser().getMobile()));
//            if (user.getUser().getIs_vip() == 1) {
//                this.name.setText("尊贵的会员");
//                Long valueOf = Long.valueOf(RxTimeUtils.getIntervalTime(RxTimeUtils.string2Date(user.getUser().getVip_end().toString()), new Date(), RxConstUtils.TimeUnit.DAY));
//                if (valueOf.longValue() > 3) {
//                    TextView textView = this.time;
//                    textView.setText("会员有效期至:" + user.getUser().getVip_end().toString());
//                    this.open_vip.setText("会员中心");
//                } else {
//                    TextView textView2 = this.time;
//                    textView2.setText("您的会员还有" + (valueOf.longValue() + 1) + "天即将到期");
//                    this.open_vip.setText("立即续费");
//                }
//                this.time.setVisibility(0);
//            } else {
//                TextView textView3 = this.name;
//                textView3.setText(getString(R.string.app_name) + "会员");
//                this.time.setVisibility(8);
//                this.open_vip.setText("立即开通");
//            }
//            this.Exit.setVisibility(0);
//            return;
//        }
        this.account.setText("登录/注册");
        this.Exit.setVisibility(8);
        TextView textView4 = this.name;
        textView4.setText(getString(R.string.app_name) + "会员");
        this.time.setVisibility(8);
        this.open_vip.setText("立即开通");
    }

    private void showCache() {
        try {
            this.cache_txt.setText(String.format("（%s)", CacheUtils.getTotalCacheSize(getContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVersion(final boolean z) {
        new Handler().postDelayed(new Runnable() { // from class: com.carrydream.cardrecorder.ui.fragment.AccountFragment$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AccountFragment.this.m128xfc74257b(z);
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$getVersion$1$com-carrydream-cardrecorder-ui-fragment-AccountFragment  reason: not valid java name */
    public /* synthetic */ void m128xfc74257b(boolean z) {
        AccountPresenter accountPresenter = this.presenter;
        accountPresenter.upgrade(Tool.getAppVersionNo(getContext()) + "", getContext().getPackageName(), getString(R.string.channel), z);
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnUpgrade(final BaseResult<Update> baseResult, boolean z) {
        if (baseResult.getStatus() == 200) {
            if (baseResult.getData().getIs_upgrade() == 1) {
                TextView textView = this.version_code;
                textView.setText(ExifInterface.GPS_MEASUREMENT_INTERRUPTED + Tool.getAppVersionName(getContext()) + "(有新版本)");
                this.version_red.setVisibility(0);
                UpdateDialog updateDialog = new UpdateDialog(getActivity(), baseResult.getData());
                updateDialog.setProtocol(new UpdateDialog.protocol() { // from class: com.carrydream.cardrecorder.ui.fragment.AccountFragment.1
                    @Override // com.carrydream.cardrecorder.ui.dialog.UpdateDialog.protocol
                    public void no() {
                    }

                    @Override // com.carrydream.cardrecorder.ui.dialog.UpdateDialog.protocol
                    public void yes() {
                        AccountFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(((Update) baseResult.getData()).getDownload_url())));
                    }
                });
                updateDialog.show();
                return;
            }
            this.version_red.setVisibility(8);
            if (z) {
                Toast.makeText(getContext(), "已是最新版本", 0).show();
            }
        }
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnLogOut(BaseResult<Object> baseResult) {
        baseResult.getStatus();
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnInfo(BaseResult<User> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setUser(baseResult.getData());
            setState();
        }
    }
}
