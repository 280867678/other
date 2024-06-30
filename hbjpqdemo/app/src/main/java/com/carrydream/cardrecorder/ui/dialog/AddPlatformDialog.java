package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.carrydream.cardrecorder.tool.MyToast;
import com.carrydream.cardrecorder.tool.Tool;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class AddPlatformDialog extends Dialog {
    Activity context;
    Edit edit;
    EditText editText;
    TextView no;
    TextView yes;

    /* loaded from: classes.dex */
    public interface Edit {
        void add(String str);
    }

    public void setEditListener(Edit edit) {
        this.edit = edit;
    }

    public AddPlatformDialog(Activity activity) {
        super(activity, R.style.MDialog);
        this.context = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.add_platform);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(17);
        window.setType(1000);
        window.setFlags(1024, 1024);
        setCanceledOnTouchOutside(false);
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
        this.editText = (EditText) findViewById(R.id.edit);
        this.no = (TextView) findViewById(R.id.no);
        this.yes = (TextView) findViewById(R.id.yes);
        findViewById(R.id.item).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.AddPlatformDialog$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                Tool.hideKeyboard(view);
            }
        });
        this.yes.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.AddPlatformDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddPlatformDialog.this.m105xe6ea7b34(view);
            }
        });
        this.no.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.AddPlatformDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddPlatformDialog.this.m106xc7e8435(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-AddPlatformDialog  reason: not valid java name */
    public /* synthetic */ void m105xe6ea7b34(View view) {
        if (TextUtils.isEmpty(this.editText.getText().toString())) {
            MyToast.show("输入游戏完整名称");
            return;
        }
        this.edit.add(this.editText.getText().toString());
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$2$com-carrydream-cardrecorder-ui-dialog-AddPlatformDialog  reason: not valid java name */
    public /* synthetic */ void m106xc7e8435(View view) {
        dismiss();
    }
}
