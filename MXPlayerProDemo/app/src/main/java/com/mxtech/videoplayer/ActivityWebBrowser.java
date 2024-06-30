package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.mxtech.DeviceUtils;
import com.mxtech.ViewUtils;
import com.mxtech.WebViewUtils;
import com.mxtech.app.MXApplication;

@SuppressLint({"SetJavaScriptEnabled"})
/* loaded from: classes.dex */
public class ActivityWebBrowser extends ActivityThemed {
    private static final String TAG = App.TAG + ".WebBrowser";
    private WebView _wv;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateComponentState() {
        PackageManager pm = App.context.getPackageManager();
        String selfPackageName = App.context.getPackageName();
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://google.com"));
        boolean selfRegistered = false;
        boolean hasReceiver = false;
        try {
            for (ResolveInfo resolved : pm.queryIntentActivities(intent, 65536)) {
                String packageName = resolved.activityInfo.packageName;
                if (selfPackageName.equals(packageName)) {
                    selfRegistered = true;
                } else if (!DeviceUtils.ANDROID_TV_DEFAULT_INTENT_RECEIVER.equals(packageName)) {
                    hasReceiver = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        if (hasReceiver) {
            if (selfRegistered) {
                pm.setComponentEnabledSetting(new ComponentName(App.context, ActivityWebBrowser.class), 2, 1);
            }
        } else if (!selfRegistered) {
            pm.setComponentEnabledSetting(new ComponentName(App.context, ActivityWebBrowser.class), 1, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    @SuppressLint({"NewApi"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.about);
        this._wv = (WebView) findViewById(R.id.content);
        if (((MXApplication) getApplication()).initInteractive(this)) {
            WebSettings settings = this._wv.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            this._wv.setWebChromeClient(new WebChromeClient() { // from class: com.mxtech.videoplayer.ActivityWebBrowser.1
                @Override // android.webkit.WebChromeClient
                public void onProgressChanged(WebView view, int progress) {
                    if (progress == 100) {
                        ActivityWebBrowser.this.setTitle(view.getTitle());
                    } else {
                        ActivityWebBrowser.this.setTitle(view.getTitle() + " (" + progress + "%)");
                    }
                }

                @Override // android.webkit.WebChromeClient
                public void onCloseWindow(WebView window) {
                    ActivityWebBrowser.this.finish();
                }
            });
            this._wv.setWebViewClient(new WebViewClient() { // from class: com.mxtech.videoplayer.ActivityWebBrowser.2
                @Override // android.webkit.WebViewClient
                public void onPageFinished(WebView view, String url) {
                    ActivityWebBrowser.this.setTitle(view.getTitle());
                }

                @Override // android.webkit.WebViewClient
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(ActivityWebBrowser.this, description, 0).show();
                }

                @Override // android.webkit.WebViewClient
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Uri uri = Uri.parse(url);
                    String scheme = uri.getScheme();
                    if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                        return false;
                    }
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", uri);
                        ActivityWebBrowser.this.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Log.e(ActivityWebBrowser.TAG, "", e);
                        return false;
                    }
                }
            });
            if (savedInstanceState == null) {
                Intent intent = getIntent();
                String uri = intent.getDataString();
                if (uri != null) {
                    this._wv.loadUrl(uri);
                }
            }
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        WebViewUtils.onResume(this._wv);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityThemed, com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        WebViewUtils.enableTimers(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        WebViewUtils.enableTimers(false);
        WebViewUtils.onPause(this._wv);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ViewUtils.removeFromParent(this._wv);
        this._wv.destroy();
        this._wv = null;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"NewApi"})
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle states) {
        super.onSaveInstanceState(states);
        this._wv.saveState(states);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle states) {
        super.onRestoreInstanceState(states);
        this._wv.restoreState(states);
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public final void onBackPressed() {
        if (this.actionMode != null) {
            this.actionMode.finish();
        } else if (this._wv.canGoBack()) {
            this._wv.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
