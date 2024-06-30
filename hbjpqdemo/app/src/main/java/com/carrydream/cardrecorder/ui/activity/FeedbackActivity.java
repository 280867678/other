package com.carrydream.cardrecorder.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.Picture;
import com.carrydream.cardrecorder.Model.Pictures;
import com.carrydream.cardrecorder.adapter.FeedbackAdapter;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.base.BaseView;
import com.carrydream.cardrecorder.tool.DensityUtils;
import com.carrydream.cardrecorder.tool.GlideEngine;
import com.carrydream.cardrecorder.tool.MyToast;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.ui.Module.FeedbackModule;
import com.carrydream.cardrecorder.ui.Presenter.FeedbackPresenter;
//import com.carrydream.cardrecorder.ui.component.DaggerFeedbackComponent;
//import com.carrydream.cardrecorder.ui.component.DaggerFeedbackComponent;
import com.carrydream.cardrecorder.ui.component.DaggerFeedbackComponent;
import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hb.aiyouxiba.R;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class FeedbackActivity extends BaseActivity implements FeedbackContacts.View {
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.bg)
    LinearLayout bg;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.contact_edit)
    EditText contact_edit;
    FeedbackAdapter feedbackAdapter;
    @BindView(R.id.edit)
    EditText mEditText;
    @Inject
    FeedbackPresenter presenter;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<FeedbackContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return BaseView.CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_feedback;
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        DaggerFeedbackComponent.builder().appComponent(BaseApplication.getAppComponent()).feedbackModule(new FeedbackModule(this)).build().inject(this);
        this.back.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FeedbackActivity.this.m52x6400e2aa(view);
            }
        });
        this.bg.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FeedbackActivity.this.m53xf0edf9c9(view);
            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity.1
            private int editEnd;
            private int editStart;
            private CharSequence temp;

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                this.temp = charSequence;
            }

            @SuppressLint("WrongConstant")
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                this.editStart = FeedbackActivity.this.mEditText.getSelectionStart();
                this.editEnd = FeedbackActivity.this.mEditText.getSelectionEnd();
                TextView textView = FeedbackActivity.this.amount;
                textView.setText(this.temp.length() + "/200");
                FeedbackActivity.this.comment.setSelected(true);
                if (this.temp.length() > 0) {
                    FeedbackActivity.this.comment.setSelected(true);
                } else {
                    FeedbackActivity.this.comment.setSelected(false);
                }
                if (this.temp.length() >= 200) {
                    Toast.makeText(FeedbackActivity.this, "您输入的字数已经超过了限制!", 0).show();
                }
            }
        });
        TextView textView = this.amount;
        textView.setText(this.mEditText.length() + "/200");
        this.comment.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FeedbackActivity.this.m54x7ddb10e8(view);
            }
        });
        final ArrayList arrayList = new ArrayList();
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        this.recycler.setLayoutManager(gridLayoutManager);
        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(this, R.layout.feedback_item);
        this.feedbackAdapter = feedbackAdapter;
        feedbackAdapter.addChildClickViewIds(R.id.item);
        this.feedbackAdapter.addChildClickViewIds(R.id.delete);
        this.feedbackAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity$$ExternalSyntheticLambda3
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                FeedbackActivity.this.m55xac82807(arrayList, baseQuickAdapter, view, i);
            }
        });
        this.recycler.setAdapter(this.feedbackAdapter);
        arrayList.add("add");
        this.feedbackAdapter.setNewInstance(arrayList);
        this.recycler.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                layoutParams.getSpanIndex();
                rect.top = DensityUtils.dp2px(FeedbackActivity.this, 12.0f);
                if (spanSize != gridLayoutManager.getSpanCount()) {
                    if (spanSize == 0) {
                        rect.left = DensityUtils.dp2px(FeedbackActivity.this, 0.0f);
                        rect.right = DensityUtils.dp2px(FeedbackActivity.this, 6.0f);
                    } else if (spanSize == 2) {
                        rect.left = DensityUtils.dp2px(FeedbackActivity.this, 6.0f);
                        rect.right = DensityUtils.dp2px(FeedbackActivity.this, 0.0f);
                    } else {
                        rect.left = DensityUtils.dp2px(FeedbackActivity.this, 6.0f);
                        rect.right = DensityUtils.dp2px(FeedbackActivity.this, 6.0f);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-ui-activity-FeedbackActivity  reason: not valid java name */
    public /* synthetic */ void m52x6400e2aa(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$1$com-carrydream-cardrecorder-ui-activity-FeedbackActivity  reason: not valid java name */
    public /* synthetic */ void m53xf0edf9c9(View view) {
        Tool.hideKeyboard(this.bg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$2$com-carrydream-cardrecorder-ui-activity-FeedbackActivity  reason: not valid java name */
    @SuppressLint("WrongConstant")
    public /* synthetic */ void m54x7ddb10e8(View view) {
        if (this.mEditText.getText().toString().equals("")) {
            Toast.makeText(this, "请填写反馈内容!", 0).show();
        } else if (this.feedbackAdapter.getData().size() > 1) {
            this.presenter.images(this.feedbackAdapter.getData());
        } else {
            this.presenter.feedback(this.mEditText.getText().toString(), this.contact_edit.getText().toString(), new String[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$3$com-carrydream-cardrecorder-ui-activity-FeedbackActivity  reason: not valid java name */
    public /* synthetic */ void m55xac82807(final List list, BaseQuickAdapter baseQuickAdapter, View view, int i) {
        String str = (String) baseQuickAdapter.getItem(i);
        int id = view.getId();
        if (id != R.id.delete) {
            if (id != R.id.item) {
                return;
            }
            EasyPhotos.createAlbum((FragmentActivity) this, true, true, GlideEngine.getInstance()).setCount(1).start(new SelectCallback() { // from class: com.carrydream.cardrecorder.ui.activity.FeedbackActivity.2
                @Override // com.huantansheng.easyphotos.callback.SelectCallback
                public void onCancel() {
                }

                @Override // com.huantansheng.easyphotos.callback.SelectCallback
                public void onResult(ArrayList<Photo> arrayList, boolean z) {
                    Iterator<Photo> it = arrayList.iterator();
                    while (it.hasNext()) {
                        List list2 = list;
                        list2.add(list2.size() - 1, it.next().path);
                    }
                    if (list.size() == 4) {
                        list.remove("add");
                    }
                    FeedbackActivity.this.feedbackAdapter.setNewInstance(list);
                    FeedbackActivity.this.feedbackAdapter.notifyDataSetChanged();
                }
            });
            return;
        }
        this.feedbackAdapter.removeAt(i);
        if (this.feedbackAdapter.getData().contains("add")) {
            return;
        }
        this.feedbackAdapter.addData( "add");
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.View
    public void OnFeedback(BaseResult<Object> baseResult) {
        if (baseResult.getStatus() == 200) {
            finish();
            MyToast.show("反馈成功!");
        }
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.View
    public void OnImages(BaseResult<Pictures> baseResult) {
        if (baseResult.getStatus() == 200) {
            List<String> urls = baseResult.getData().getUrls();
            this.presenter.feedback(this.mEditText.getText().toString(), this.contact_edit.getText().toString(), (String[]) urls.toArray(new String[urls.size()]));
        }
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.FeedbackContacts.View
    public void OnImage(BaseResult<Picture> baseResult) {
        baseResult.getStatus();
    }
}
