package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class PowerDialog extends Dialog {
    private TextView content;
    Activity context;
    private FrameLayout frameLayout;
    Power power;
    private TextView yes;

    /* loaded from: classes.dex */
    public interface Power {
        void open();
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public PowerDialog(Activity activity) {
        super(activity, R.style.MDialog);
        this.context = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.power_dialog);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(17);
        window.setType(1000);
        window.setFlags(1024, 1024);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
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
        TextView textView = (TextView) findViewById(R.id.yes);
        this.yes = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.PowerDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PowerDialog.this.m114xa91b8384(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-PowerDialog  reason: not valid java name */
    public /* synthetic */ void m114xa91b8384(View view) {
        this.power.open();
        dismiss();
    }
}
