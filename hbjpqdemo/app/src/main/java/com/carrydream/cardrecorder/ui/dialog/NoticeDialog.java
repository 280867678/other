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
public class NoticeDialog extends Dialog {
    private TextView content;
    String contents;
    Activity context;
    private FrameLayout frameLayout;
    private TextView yes;

    /* loaded from: classes.dex */
    public interface NewSubmit {
        void yes(View view);
    }

    public NoticeDialog(Activity activity) {
        super(activity, R.style.MDialog);
        this.context = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.notice_dialog);
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
        TextView textView = (TextView) findViewById(R.id.yes);
        this.yes = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.NoticeDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NoticeDialog.this.m113x5beb7049(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-NoticeDialog  reason: not valid java name */
    public /* synthetic */ void m113x5beb7049(View view) {
        dismiss();
    }
}
