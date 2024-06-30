package com.carrydream.cardrecorder.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.adapter.PlatformAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hb.aiyouxiba.R;
import java.util.List;

/* loaded from: classes.dex */
public class SelectPlatformDialog extends Dialog {
    Activity context;
    PlatformAdapter platformAdapter;
    PlatformSelect platformSelect;
    List<Platform> platforms;
    private RecyclerView recycler;

    /* loaded from: classes.dex */
    public interface PlatformSelect {
        void Select(Platform platform);
    }

    public void setPlatforSelect(PlatformSelect platformSelect) {
        this.platformSelect = platformSelect;
    }

    public SelectPlatformDialog(Activity activity, List<Platform> list) {
        super(activity, R.style.MDialog);
        this.context = activity;
        this.platforms = list;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.game_type_dialog);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        this.recycler = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, 1, false));
        PlatformAdapter platformAdapter = new PlatformAdapter(this.context, R.layout.game_item);
        this.platformAdapter = platformAdapter;
        this.recycler.setAdapter(platformAdapter);
        this.platformAdapter.setNewInstance(this.platforms);
        this.platformAdapter.addChildClickViewIds(R.id.item);
        this.platformAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectPlatformDialog$$ExternalSyntheticLambda1
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                SelectPlatformDialog.this.m121x2a13e0c0(baseQuickAdapter, view, i);
            }
        });
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectPlatformDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SelectPlatformDialog.this.m122x3017ac1f(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-SelectPlatformDialog  reason: not valid java name */
    public /* synthetic */ void m121x2a13e0c0(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        Platform platform = (Platform) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        this.platformSelect.Select(platform);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-SelectPlatformDialog  reason: not valid java name */
    public /* synthetic */ void m122x3017ac1f(View view) {
        dismiss();
    }
}
