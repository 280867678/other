package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class VipTipsDialog extends Dialog {
    Activity context;
    private TextView no;
    protocol protocol;

    /* loaded from: classes.dex */
    public interface protocol {
        void no();

        void yes();
    }

    public void setProtocol(protocol protocolVar) {
        this.protocol = protocolVar;
    }

    public VipTipsDialog(Activity activity) {
        super(activity, R.style.MDialog);
        this.context = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.vip_dialog);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(17);
        window.setType(1000);
        window.setFlags(1024, 1024);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        window.setAttributes(attributes);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    private void initViews() {
        this.no = (TextView) findViewById(R.id.no);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.VipTipsDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipTipsDialog.this.m125x71aca5b4(view);
            }
        });
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.VipTipsDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipTipsDialog.this.m126xfee75735(view);
            }
        });
        this.no.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.VipTipsDialog$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VipTipsDialog.this.m127x8c2208b6(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-VipTipsDialog  reason: not valid java name */
    public /* synthetic */ void m125x71aca5b4(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-VipTipsDialog  reason: not valid java name */
    public /* synthetic */ void m126xfee75735(View view) {
        this.protocol.yes();
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$2$com-carrydream-cardrecorder-ui-dialog-VipTipsDialog  reason: not valid java name */
    public /* synthetic */ void m127x8c2208b6(View view) {
        this.protocol.no();
        dismiss();
    }
}
