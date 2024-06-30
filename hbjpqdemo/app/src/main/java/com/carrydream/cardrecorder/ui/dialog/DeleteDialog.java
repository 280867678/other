package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.carrydream.cardrecorder.Model.Item;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class DeleteDialog extends Dialog {
    private TextView content;
    Item contents;
    Activity context;
    NewSubmit newSubmit;
    private TextView no;
    private TextView yes;

    /* loaded from: classes.dex */
    public interface NewSubmit {
        void delete(Item item);
    }

    public void setNewListener(NewSubmit newSubmit) {
        this.newSubmit = newSubmit;
    }

    public DeleteDialog(Activity activity, Item item) {
        super(activity, R.style.MDialog);
        this.context = activity;
        this.contents = item;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.delete_layout);
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
        attributes.height = -2;
        window.setAttributes(attributes);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    private void initViews() {
        this.content = (TextView) findViewById(R.id.content);
        this.no = (TextView) findViewById(R.id.no);
        this.yes = (TextView) findViewById(R.id.yes);
        TextView textView = this.content;
        textView.setText("是否删除【" + this.contents.getName() + "】？");
        this.yes.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.DeleteDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DeleteDialog.this.m109x30a2b4dc(view);
            }
        });
        this.no.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.DeleteDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DeleteDialog.this.m110x5e7b4f3b(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-DeleteDialog  reason: not valid java name */
    public /* synthetic */ void m109x30a2b4dc(View view) {
        this.newSubmit.delete(this.contents);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-DeleteDialog  reason: not valid java name */
    public /* synthetic */ void m110x5e7b4f3b(View view) {
        dismiss();
    }
}
