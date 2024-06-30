package com.carrydream.cardrecorder.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.Update;
import com.hb.aiyouxiba.R;
//import com.umeng.analytics.pro.ai;
import java.util.Iterator;

/* loaded from: classes.dex */
public class UpdateDialog extends Dialog {
    Activity context;
    private TextView no;
    protocol protocol;
    Update update;
    private TextView update_tv;
    private TextView version_name;

    /* loaded from: classes.dex */
    public interface protocol {
        void no();

        void yes();
    }

    public void setProtocol(protocol protocolVar) {
        this.protocol = protocolVar;
    }

    public UpdateDialog(Activity activity, Update update) {
        super(activity, R.style.MDialog);
        this.context = activity;
        this.update = update;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.update_dialog);
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
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        this.update_tv = (TextView) findViewById(R.id.update);
        this.version_name = (TextView) findViewById(R.id.version_name);
        this.no = (TextView) findViewById(R.id.no);
        TextView textView = this.version_name;
        textView.setText("v" + this.update.getVersion_num());
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = this.update.getUpgrade_log().iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next() + "\n");
        }
        this.update_tv.setText(stringBuffer);
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.UpdateDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                UpdateDialog.this.m123x25e5197a(view);
            }
        });
        this.no.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.UpdateDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                UpdateDialog.this.m124x53bdb3d9(view);
            }
        });
        boolean z = false;
        if (this.update.getIs_force() == 0) {
            this.no.setVisibility(0);
            z = true;
        } else {
            this.no.setVisibility(8);
        }
        setCanceledOnTouchOutside(z);
        setCancelable(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-UpdateDialog  reason: not valid java name */
    public /* synthetic */ void m123x25e5197a(View view) {
        this.protocol.yes();
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-UpdateDialog  reason: not valid java name */
    public /* synthetic */ void m124x53bdb3d9(View view) {
        this.protocol.no();
        dismiss();
    }
}
