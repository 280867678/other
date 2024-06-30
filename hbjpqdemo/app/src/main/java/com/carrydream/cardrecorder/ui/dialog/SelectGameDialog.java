package com.carrydream.cardrecorder.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrydream.cardrecorder.Model.GameType;
import com.carrydream.cardrecorder.adapter.GameTypeAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hb.aiyouxiba.R;
import java.util.List;

/* loaded from: classes.dex */
public class SelectGameDialog extends Dialog {
    Activity context;
    GameSelect gameSelect;
    GameTypeAdapter gameTypeAdapter;
    List<GameType> gameTypes;
    private RecyclerView recycler;

    /* loaded from: classes.dex */
    public interface GameSelect {
        void Select(GameType gameType);
    }

    public void setGameSelect(GameSelect gameSelect) {
        this.gameSelect = gameSelect;
    }

    public SelectGameDialog(Activity activity, List<GameType> list) {
        super(activity, R.style.MDialog);
        this.context = activity;
        this.gameTypes = list;
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

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        this.recycler = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, 1, false));
        GameTypeAdapter gameTypeAdapter = new GameTypeAdapter(this.context, R.layout.game_item);
        this.gameTypeAdapter = gameTypeAdapter;
        this.recycler.setAdapter(gameTypeAdapter);
        this.gameTypeAdapter.setNewInstance(this.gameTypes);
        this.gameTypeAdapter.addChildClickViewIds(R.id.item);
        this.gameTypeAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectGameDialog$$ExternalSyntheticLambda1
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                SelectGameDialog.this.m115xd050437f(baseQuickAdapter, view, i);
            }
        });
        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.dialog.SelectGameDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SelectGameDialog.this.m116xd186965e(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$0$com-carrydream-cardrecorder-ui-dialog-SelectGameDialog  reason: not valid java name */
    public /* synthetic */ void m115xd050437f(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        GameType gameType = (GameType) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        if (gameType.isIs_success()) {
            this.gameSelect.Select(gameType);
            dismiss();
            return;
        }
        Toast.makeText(this.context, "（正在研发中）", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initViews$1$com-carrydream-cardrecorder-ui-dialog-SelectGameDialog  reason: not valid java name */
    public /* synthetic */ void m116xd186965e(View view) {
        dismiss();
    }
}
