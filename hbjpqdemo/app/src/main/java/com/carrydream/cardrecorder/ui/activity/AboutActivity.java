package com.carrydream.cardrecorder.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.carrydream.cardrecorder.base.BaseActivity;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.Tool;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class AboutActivity extends BaseActivity {
    @BindView(R.id.version)
    TextView version;

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_about;
    }

    @OnClick({R.id.back, R.id.help_center, R.id.privacy_policy})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.help_center) {
            Intent intent = new Intent(this, HtmlActivity.class);
            intent.putExtra("title", "用户协议");
            intent.putExtra("url", DataUtils.getInstance().getConfig().getUser_agreement_h5());
            startActivity(intent);
        } else if (id != R.id.privacy_policy) {
        } else {
            Intent intent2 = new Intent(this, HtmlActivity.class);
            intent2.putExtra("title", "隐私政策");
            intent2.putExtra("url", DataUtils.getInstance().getConfig().getPrivacy_policy_h5());
            startActivity(intent2);
        }
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        TextView textView = this.version;
        textView.setText("v " + Tool.getAppVersionName(this));
    }
}
