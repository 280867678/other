package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class IsDialog extends Dialog {
    private TextView content;
    String contents;
    Activity context;
    private FrameLayout frameLayout;
    NewSubmit newSubmit;
    private TextView no;
    String tv_no;
    String tv_yes;
    private TextView yes;

    /* loaded from: classes.dex */
    public interface NewSubmit {
        void yes(View view);
    }

    public void setNewListener(NewSubmit newSubmit) {
        this.newSubmit = newSubmit;
    }

    public IsDialog(Activity activity, String str) {
        super(activity, R.style.MDialog);
        this.tv_yes = "";
        this.tv_no = "";
        this.context = activity;
        this.contents = str;
    }

    public IsDialog(Activity activity, String str, String str2) {
        super(activity, R.style.MDialog);
        this.tv_no = "";
        this.context = activity;
        this.contents = str;
        this.tv_yes = str2;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.is_item);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(17);
        window.setType(1000);
        window.setFlags(1024, 1024);
        window.setLayout(-1, -1);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        window.setAttributes(attributes);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    private void initViews() {
        this.frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        this.content = (TextView) findViewById(R.id.content);
        this.no = (TextView) findViewById(R.id.no);
        this.yes = (TextView) findViewById(R.id.yes);
        this.content.setText(this.contents);
        this.yes.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.IsDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IsDialog.this.m111x886cd9b(view);
            }
        });
        if (!TextUtils.isEmpty(this.tv_yes)) {
            this.yes.setText(this.tv_yes);
        }
        this.no.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.IsDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IsDialog.this.m112x42516f7a(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-IsDialog  reason: not valid java name */
    public /* synthetic */ void m111x886cd9b(View view) {
        this.newSubmit.yes(view);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-IsDialog  reason: not valid java name */
    public /* synthetic */ void m112x42516f7a(View view) {
        dismiss();
    }
}
