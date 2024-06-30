package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.ui.activity.HtmlActivity;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class AgreementDialog extends Dialog {
    private TextView agreement;
    Activity context;
    private FrameLayout frameLayout;
    protocol protocol;
    private TextView yes;

    /* loaded from: classes.dex */
    public interface protocol {
        void no();

        void yes();
    }

    public void setProtocol(protocol protocolVar) {
        this.protocol = protocolVar;
    }

    public AgreementDialog(Activity activity) {
        super(activity, R.style.MDialog);
        this.context = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.agreement_dialog);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(17);
        window.setType(1000);
        window.setFlags(1024, 1024);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -2;
        window.setAttributes(attributes);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    private void initViews() {
        this.frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        this.agreement = (TextView) findViewById(R.id.agreement);
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.AgreementDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AgreementDialog.this.m107xc92c7149(view);
            }
        });
        findViewById(R.id.no).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.AgreementDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AgreementDialog.this.m108xf280c68a(view);
            }
        });
        setText();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-AgreementDialog  reason: not valid java name */
    public /* synthetic */ void m107xc92c7149(View view) {
        this.protocol.yes();
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-AgreementDialog  reason: not valid java name */
    public /* synthetic */ void m108xf280c68a(View view) {
        this.protocol.no();
        dismiss();
    }

    public void setText() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "    我们非常注重您的个人信息和隐私保护，为了更好的保证您的个人权益，请您认真阅读用户协议和隐私政策的全部内容，同意并接受全部条款后开始使用我们的产品和服务。\n\n    我们会根据您使用的功能需求，收集必要的用户信息，可能涉及设备及储存权限、账户等信息，未经您同意，我们不会从三方获取、共享或者对外提供您的信息。");
        ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.dialog.AgreementDialog.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(AgreementDialog.this.context, HtmlActivity.class);
                intent.putExtra("title", "用户协议");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getUser_agreement_h5());
                AgreementDialog.this.context.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() { // from class: com.carrydream.cardrecorder.ui.dialog.AgreementDialog.2
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Intent intent = new Intent(AgreementDialog.this.context, HtmlActivity.class);
                intent.putExtra("title", "隐私政策");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getPrivacy_policy_h5());
                AgreementDialog.this.context.startActivity(intent);
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, 42, 46, 33);
        spannableStringBuilder.setSpan(clickableSpan2, 47, 51, 33);
        this.agreement.setText(spannableStringBuilder);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3D90FE"));
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(Color.parseColor("#3D90FE"));
        spannableStringBuilder.setSpan(foregroundColorSpan, 42, 46, 33);
        spannableStringBuilder.setSpan(foregroundColorSpan2, 47, 51, 33);
        this.agreement.setMovementMethod(LinkMovementMethod.getInstance());
        this.agreement.setText(spannableStringBuilder);
        this.agreement.setHighlightColor(Color.parseColor("#00000000"));
    }
}
