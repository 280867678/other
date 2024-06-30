package com.carrydream.cardrecorder.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Order;
import com.carrydream.cardrecorder.Model.Plans;
import com.carrydream.cardrecorder.Model.Update;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.adapter.VipAdapter;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.tool.WxPay;
import com.carrydream.cardrecorder.ui.Module.AccountModule;
import com.carrydream.cardrecorder.ui.Presenter.AccountPresenter;
//import com.carrydream.cardrecorder.ui.component.DaggerAccountComponent;
import com.carrydream.cardrecorder.ui.component.DaggerAccountComponent;
import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import java.text.DecimalFormat;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class VipActivity extends BaseActivity implements AccountContacts.View {
    VipAdapter adapter;
    @BindView(R.id.agreement)
    TextView agreement;
    @BindView(R.id.day_price)
    TextView day_price;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.open_tips)
    ImageView open_tips;
    @BindView(R.id.open_vip)
    TextView open_vip;
    Plans.PlansDTO plansDTO;
    @Inject
    AccountPresenter presenter;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.service)
    TextView service;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.txt_tips)
    TextView txt_tips;

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnLogOut(BaseResult<Object> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnUpgrade(BaseResult<Update> baseResult, boolean z) {
    }

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<AccountContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return BaseView.CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_vip;
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        DaggerAccountComponent.builder().appComponent(BaseApplication.getAppComponent()).accountModule(new AccountModule(this)).build().inject(this);
        this.presenter.plans();
        this.recycler.setLayoutManager(new LinearLayoutManager(this, 0, false));
        VipAdapter vipAdapter = new VipAdapter(this, R.layout.vip_item);
        this.adapter = vipAdapter;
        vipAdapter.addChildClickViewIds(R.id.item);
        this.adapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity$$ExternalSyntheticLambda3
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                VipActivity.this.m101x851afb96(baseQuickAdapter, view, i);
            }
        });
        this.recycler.setAdapter(this.adapter);
//        this.name.setText(Tool.phone(DataUtils.getInstance().getUser().getUser().getMobile()));
        this.iv_back.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipActivity.this.m102x1255ad17(view);
            }
        });
        this.open_vip.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipActivity.this.m103x9f905e98(view);
            }
        });
        setText();
        this.service.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipActivity.this.m104x2ccb1019(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-ui-activity-VipActivity  reason: not valid java name */
    public /* synthetic */ void m101x851afb96(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        this.plansDTO = this.adapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        this.adapter.setSelect(i);
        double parseDouble = Double.parseDouble(this.plansDTO.getPrice() + "") / this.plansDTO.getDay_count();
        TextView textView = this.price;
        textView.setText("¥" + this.plansDTO.getPrice());
        TextView textView2 = this.day_price;
        textView2.setText("低至" + new DecimalFormat("0.0").format(parseDouble) + "元/天");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$1$com-carrydream-cardrecorder-ui-activity-VipActivity  reason: not valid java name */
    public /* synthetic */ void m102x1255ad17(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$2$com-carrydream-cardrecorder-ui-activity-VipActivity  reason: not valid java name */
    @SuppressLint("WrongConstant")
    public /* synthetic */ void m103x9f905e98(View view) {
        if (Tool.isWxIn(this)) {
            AccountPresenter accountPresenter = this.presenter;
            accountPresenter.order(this.plansDTO.getId() + "");
            return;
        }
        Toast.makeText(this, "请先安装微信哦!", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$3$com-carrydream-cardrecorder-ui-activity-VipActivity  reason: not valid java name */
    public /* synthetic */ void m104x2ccb1019(View view) {
        Tool.openQQ(this, DataUtils.getInstance().getConfig().getCustomer_service_qq());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
//        if (DataUtils.getInstance().getUser().getUser().getIs_vip() == 0) {
//            this.open_tips.setImageResource(R.mipmap.personal_icon_vip_nor);
//            this.txt_tips.setText("待开通");
//            this.txt_tips.setTextColor(getResources().getColor(R.color.C_969799));
//            this.time.setText("开通VIP，尊享专属权益");
//            this.time.setTextColor(getResources().getColor(R.color.C_C8CACC));
//            this.open_vip.setText("立即开通");
//            return;
//        }
        this.open_tips.setImageResource(R.mipmap.personal_icon_vip_sel);
        this.txt_tips.setText("已开通");
        this.txt_tips.setTextColor(getResources().getColor(R.color.c_ffe8d3));
        TextView textView = this.time;
//        textView.setText("会员有效期至:" + DataUtils.getInstance().getUser().getUser().getVip_end().toString());
        this.time.setTextColor(getResources().getColor(R.color.c_ffe8d3));
        this.open_vip.setText("立即续费");
    }

    public void setText() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "*开通即同意《VIP会员服务协议》|《用户服务协议》");
        ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(VipActivity.this, HtmlActivity.class);
                intent.putExtra("title", "VIP会员服务协议");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getMembership_agreement_h5());
                VipActivity.this.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.activity.VipActivity.2
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(VipActivity.this, HtmlActivity.class);
                intent.putExtra("title", "用户服务协议");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getUser_agreement_h5());
                VipActivity.this.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, 6, 17, 33);
        spannableStringBuilder.setSpan(clickableSpan2, 18, 26, 33);
        this.agreement.setText(spannableStringBuilder);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#75513B"));
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(Color.parseColor("#75513B"));
        spannableStringBuilder.setSpan(foregroundColorSpan, 6, 17, 33);
        spannableStringBuilder.setSpan(foregroundColorSpan2, 18, 26, 33);
        this.agreement.setMovementMethod(LinkMovementMethod.getInstance());
        this.agreement.setText(spannableStringBuilder);
        this.agreement.setHighlightColor(Color.parseColor("#00000000"));
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnPlans(BaseResult<Plans> baseResult) {
        if (baseResult.getStatus() == 200) {
            this.adapter.setNewInstance(baseResult.getData().getPlans());
            this.plansDTO = baseResult.getData().getPlans().get(0);
            TextView textView = this.price;
            textView.setText("¥" + this.plansDTO.getPrice());
            double parseDouble = Double.parseDouble(this.plansDTO.getPrice() + "") / this.plansDTO.getDay_count();
            TextView textView2 = this.day_price;
            textView2.setText("低至" + new DecimalFormat("0.0").format(parseDouble) + "元/天");
        }
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnOrder(BaseResult<Order> baseResult) {
        Log.e("微信支付", "OnOrder: " + baseResult.getData().toString());
        if (baseResult.getStatus() == 200 && baseResult.getData().getCode() == 1) {
            WxPay.Pay(baseResult.getData());
        }
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.AccountContacts.View
    public void OnInfo(BaseResult<User> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setUser(baseResult.getData());
            onResume();
        }
    }
}
