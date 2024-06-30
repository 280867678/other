package com.carrydream.cardrecorder.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Captcha;
import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Sms;
import com.carrydream.cardrecorder.Model.User;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.carrydream.cardrecorder.tool.Base64Utils;
import com.carrydream.cardrecorder.tool.CountUtils;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.ui.Module.LauncherModule;
import com.carrydream.cardrecorder.ui.Presenter.LauncherPresenter;
//import com.carrydream.cardrecorder.ui.component.DaggerLauncherComponent;
import com.carrydream.cardrecorder.ui.component.DaggerLauncherComponent;
import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
//import com.google.android.gms.common.internal.ImagesContract;
//import com.google.firebase.firestore.util.ExponentialBackoff;
import com.hb.aiyouxiba.R;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class LoginActivity extends BaseActivity implements LauncherContacts.View {
    @Inject
    LauncherPresenter Presenter;
    @BindView(R.id.agreement_lay)
    LinearLayout agreement_lay;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.code_img)
    EditText code_img;
    @BindView(R.id.code_img_x)
    ImageView code_img_x;
    @BindView(R.id.code_x)
    ImageView code_x;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.img_code)
    ImageView img_code;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    String key;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.phone_x)
    ImageView phone_x;
    @BindView(R.id.select)
    ImageView select;
    @BindView(R.id.send)
    TextView send;

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnConf(BaseResult<Config> baseResult) {
    }

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<LauncherContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return BaseView.CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_login;
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        DaggerLauncherComponent.builder().appComponent(BaseApplication.getAppComponent()).launcherModule(new LauncherModule(this)).build().inject(this);
        getData();
        this.code.addTextChangedListener(new TextWatcher() { // from class: com.carrydream.cardrecorder.ui.activity.LoginActivity.1
            private CharSequence temp;

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                this.temp = charSequence;
            }

            @SuppressLint("WrongConstant")
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (this.temp.length() > 0) {
                    LoginActivity.this.login.setSelected(true);
                    LoginActivity.this.code_x.setVisibility(0);
                } else {
                    LoginActivity.this.login.setSelected(false);
                    LoginActivity.this.code_x.setVisibility(8);
                }
                if (this.temp.length() == 4) {
                    Tool.hideKeyboard(LoginActivity.this.code);
                }
            }
        });
        this.phone.addTextChangedListener(new TextWatcher() { // from class: com.carrydream.cardrecorder.ui.activity.LoginActivity.2
            private CharSequence temp;

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                this.temp = charSequence;
            }

            @SuppressLint("WrongConstant")
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (this.temp.length() > 0) {
                    LoginActivity.this.phone_x.setVisibility(0);
                } else {
                    LoginActivity.this.phone_x.setVisibility(8);
                }
                if (this.temp.length() == 11) {
                    LoginActivity.this.code_img.setFocusable(true);
                    LoginActivity.this.code_img.requestFocus();
                }
            }
        });
        this.code_img.addTextChangedListener(new TextWatcher() { // from class: com.carrydream.cardrecorder.ui.activity.LoginActivity.3
            private CharSequence temp;

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                this.temp = charSequence;
            }

            @SuppressLint("WrongConstant")
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (this.temp.length() > 0) {
                    LoginActivity.this.code_img_x.setVisibility(0);
                } else {
                    LoginActivity.this.code_img_x.setVisibility(8);
                }
                if (this.temp.length() == 4) {
                    LoginActivity.this.code.setFocusable(true);
                    LoginActivity.this.code.requestFocus();
                }
            }
        });
        setText();
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.select, R.id.iv_back, R.id.img_code, R.id.send, R.id.login, R.id.code_x, R.id.code_img_x, R.id.phone_x, R.id.layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.code_img_x /* 2131230867 */:
                this.code_img.setText("");
                this.code_img_x.setVisibility(8);
                return;
            case R.id.code_x /* 2131230868 */:
                this.code.setText("");
                this.code_x.setVisibility(8);
                return;
            case R.id.img_code /* 2131231006 */:
                getData();
                return;
            case R.id.iv_back /* 2131231022 */:
                finish();
                return;
            case R.id.layout /* 2131231054 */:
                Tool.hideKeyboard(this.phone);
                return;
            case R.id.login /* 2131231073 */:
                if (TextUtils.isEmpty(this.phone.getText())) {
                    Toast.makeText(this, "请输入手机号码", 0).show();
                    return;
                } else if (!Tool.isMobileNO(this.phone.getText().toString())) {
                    Toast.makeText(this, "手机号错误，请重新输入", 0).show();
                    this.phone.setFocusable(true);
                    this.phone.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(this.code.getText())) {
                    Toast.makeText(this, "请输入验证码", 0).show();
                    return;
                } else {
                    Tool.hideKeyboard(this.send);
                    if (!this.select.isSelected()) {
                        this.agreement_lay.startAnimation(shakeAnimation(2));
                        Toast.makeText(this, "请先同意用户协议,隐私政策", 0).show();
                        return;
                    }
                    this.Presenter.login(this.phone.getText().toString(), this.code.getText().toString());
                    return;
                }
            case R.id.phone_x /* 2131231191 */:
                this.phone.setText("");
                this.phone_x.setVisibility(8);
                return;
            case R.id.select /* 2131231246 */:
                ImageView imageView = this.select;
                imageView.setSelected(!imageView.isSelected());
                this.agreement_lay.setAnimation(shakeAnimation(2));
                return;
            case R.id.send /* 2131231250 */:
                if (TextUtils.isEmpty(this.phone.getText())) {
                    Toast.makeText(this, "请输入手机号码", 0).show();
                    return;
                } else if (!Tool.isMobileNO(this.phone.getText().toString())) {
                    Toast.makeText(this, "手机号错误，请重新输入", 0).show();
                    this.phone.setFocusable(true);
                    this.phone.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(this.code_img.getText())) {
                    Toast.makeText(this, "请输入图形验证码", 0).show();
                    return;
                } else {
                    Tool.hideKeyboard(this.send);
                    this.Presenter.check(this.phone.getText().toString(), this.key, this.code_img.getText().toString());
                    return;
                }
            default:
                return;
        }
    }

    public Animation shakeAnimation(int i) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 50.0f, 0.0f, 0.0f);
        translateAnimation.setInterpolator(new CycleInterpolator(i));
        translateAnimation.setRepeatCount(1);
        translateAnimation.setDuration(300L);
        return translateAnimation;
    }

    public void setText() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "我已经阅读并同意 用户协议 和 隐私政策");
        ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.activity.LoginActivity.4
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HtmlActivity.class);
                intent.putExtra("title", "用户协议");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getUser_agreement_h5());
                LoginActivity.this.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.activity.LoginActivity.5
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HtmlActivity.class);
                intent.putExtra("title", "隐私政策");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getPrivacy_policy_h5());
                LoginActivity.this.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, 9, 13, 33);
        spannableStringBuilder.setSpan(clickableSpan2, 16, 20, 33);
        this.content.setText(spannableStringBuilder);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3D90FE"));
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(Color.parseColor("#3D90FE"));
        spannableStringBuilder.setSpan(foregroundColorSpan, 9, 13, 33);
        spannableStringBuilder.setSpan(foregroundColorSpan2, 16, 20, 33);
        this.content.setMovementMethod(LinkMovementMethod.getInstance());
        this.content.setText(spannableStringBuilder);
        this.content.setHighlightColor(Color.parseColor("#00000000"));
    }

    public void getData() {
        this.Presenter.captcha(this.phone.getText().toString());
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnCaptcha(BaseResult<Captcha> baseResult) {
        if (baseResult.getStatus() == 200) {
            this.img_code.setImageBitmap(Base64Utils.stringToBitmap(baseResult.getData().getImg()));
            this.key = baseResult.getData().getKey();
        }
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnCheck(BaseResult<Sms> baseResult) {
        if (baseResult.getStatus() == 200) {
            if (baseResult.getData().getCode() == 1) {
                this.Presenter.sms_code(this.phone.getText().toString());
            }
            getData();
            Toast.makeText(this, baseResult.getData().getMsg(), 0).show();
            return;
        }
        Toast.makeText(this, baseResult.getMessage(), 0).show();
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnSms_code(BaseResult<Sms> baseResult) {
        if (baseResult.getStatus() == 200) {
            if (baseResult.getData().getCode() == 1) {
                new CountUtils(this.send, 60000, 1000L).start();
            }
            Toast.makeText(this, baseResult.getMessage(), 0).show();
            return;
        }
        Toast.makeText(this, baseResult.getMessage(), 0).show();
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnLogin(BaseResult<Info> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setInfo(baseResult.getData());
            this.Presenter.Info();
            return;
        }
        Toast.makeText(this, baseResult.getMessage(), 0).show();
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.LauncherContacts.View
    public void OnInfo(BaseResult<User> baseResult) {
        if (baseResult.getStatus() == 200) {
            DataUtils.getInstance().setUser(baseResult.getData());
            finish();
            return;
        }
        Toast.makeText(this, baseResult.getMessage(), 0).show();
    }
}
