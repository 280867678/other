package com.carrydream.cardrecorder.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrydream.cardrecorder.adapter.ChoiceAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hb.aiyouxiba.R;
import java.util.ArrayList;
import java.util.Collections;

/* loaded from: classes.dex */
public class SelectHandNumberDialog extends Dialog {
    ChoiceAdapter choiceAdapter;
    Activity context;
    NumberSelect numberSelect;
    private RecyclerView recycler;
    int sum;
    String title;
    private TextView title_tv;

    /* loaded from: classes.dex */
    public interface NumberSelect {
        void Select(int i);
    }

    public void setNumberSelect(NumberSelect numberSelect) {
        this.numberSelect = numberSelect;
    }

    public SelectHandNumberDialog(Activity activity, int i, String str) {
        super(activity, R.style.MDialog);
        this.context = activity;
        this.sum = i;
        this.title = str;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.select_number_dialog);
        initBottom();
        initViews();
    }

    private void initBottom() {
        Window window = getWindow();
        window.setGravity(80);
        window.setType(1000);
        window.setFlags(1024, 1024);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -2;
        window.setAttributes(attributes);
        window.setWindowAnimations(R.style.dialogWindowAnim);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        this.recycler = (RecyclerView) findViewById(R.id.recycler);
        TextView textView = (TextView) findViewById(R.id.title);
        this.title_tv = textView;
        textView.setText(this.title);
        this.recycler.setLayoutManager(new LinearLayoutManager(this.context, 1, false));
        ChoiceAdapter choiceAdapter = new ChoiceAdapter(this.context, R.layout.game_item, "张");
        this.choiceAdapter = choiceAdapter;
        this.recycler.setAdapter(choiceAdapter);
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < this.sum) {
            i++;
            if (i >= 10) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        Collections.reverse(arrayList);
        this.choiceAdapter.setNewInstance(arrayList);
        this.choiceAdapter.addChildClickViewIds(R.id.item);
        this.choiceAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectHandNumberDialog$$ExternalSyntheticLambda1
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i2) {
                SelectHandNumberDialog.this.m117x4a7b8be5(baseQuickAdapter, view, i2);
            }
        });
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectHandNumberDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SelectHandNumberDialog.this.m118xdeb9fb84(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-SelectHandNumberDialog  reason: not valid java name */
    public /* synthetic */ void m117x4a7b8be5(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        Integer num = (Integer) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        this.numberSelect.Select(num.intValue());
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-SelectHandNumberDialog  reason: not valid java name */
    public /* synthetic */ void m118xdeb9fb84(View view) {
        dismiss();
    }
}
