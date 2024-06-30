package com.carrydream.cardrecorder.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.carrydream.cardrecorder.base.BaseActivity;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;

/* loaded from: classes.dex */
public class HtmlActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    String title;
    @BindView(R.id.tv_title)
    TextView tv_title;
    String url;
    @BindView(R.id.web)
    WebView webView;

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected int getLayouId() {
        return R.layout.activity_detial;
    }

    @Override // com.carrydream.cardrecorder.base.BaseActivity
    protected void init() {
        this.back.setOnClickListener(new View.OnClickListener() { // from class: com.carrydream.cardrecorder.ui.activity.HtmlActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                HtmlActivity.this.m99xe5e563d0(view);
            }
        });
        this.title = getIntent().getStringExtra("title");
        String stringExtra = getIntent().getStringExtra("url");
        this.url = stringExtra;
        try {
            this.webView.loadUrl(stringExtra);
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.setWebViewClient(new WebViewClient());
            this.webView.setDownloadListener(new MWebViewDownLoadListener());
        } catch (Exception e) {
            Log.e("hjw", e.getMessage());
        }
        this.tv_title.setText(this.title);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$init$0$com-carrydream-cardrecorder-ui-activity-HtmlActivity  reason: not valid java name */
    public /* synthetic */ void m99xe5e563d0(View view) {
        finish();
    }

    /* loaded from: classes.dex */
    private class MWebViewDownLoadListener implements DownloadListener {
        private MWebViewDownLoadListener() {
        }

        @Override // android.webkit.DownloadListener
        public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
            Log.e("hjw", str);
            HtmlActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        }
    }
}
