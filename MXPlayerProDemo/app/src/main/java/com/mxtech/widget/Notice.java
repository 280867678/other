package com.mxtech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mxtech.IOUtils;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.WebViewUtils;
import com.mxtech.app.MXApplication;
import com.mxtech.market.Market;
import com.mxtech.videoplayer.pro.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class Notice {
    private static final int MAX_NOTICE_SIZE = 32768;
    private static final String PSEUDO_METADATA_STORE = "__store__";
    public static final String TAG = "MX.Notice";

    public static NoticeDialog createDialog(Context context, int noticeResId) {
        try {
            return createDialog(context, noticeResId, context.getPackageManager().getPackageInfo(context.getPackageName(), 128));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MXApplication.TAG, "", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isMatch(String condition, String packageName, @NonNull Bundle metadata) {
        String[] fragments = condition.split(":");
        if (fragments.length == 0 || !fragments[0].equals(packageName)) {
            return false;
        }
        for (int i = 1; i < fragments.length; i++) {
            String meta = fragments[i];
            String[] pair = meta.split("=");
            if (pair.length == 2 && !pair[1].equals(metadata.get(pair[0]))) {
                return false;
            }
        }
        return true;
    }

    /* loaded from: classes.dex */
    public static class NoticeDialog extends AlertDialog implements DialogInterface.OnDismissListener {
        private final String _html;
        private DialogInterface.OnDismissListener _nextOnDismissListener;
        private WebView _webView;

        @SuppressLint({"InflateParams"})
        public NoticeDialog(Context context, int noticeResId, PackageInfo pkg) throws IOException {
            super(context);
            Bundle metadata;
            Context context2 = getContext();
            byte[] buffer = new byte[32768];
            Resources res = context2.getResources();
            InputStream input = res.openRawResource(noticeResId);
            int bytes = IOUtils.readToEnd(input, buffer);
            input.close();
            String label = res.getString(pkg.applicationInfo.labelRes);
            String html = new String(buffer, 0, bytes);
            if (pkg.applicationInfo.metaData == null) {
                metadata = new Bundle();
            } else {
                metadata = new Bundle(pkg.applicationInfo.metaData);
            }
            metadata.putString(Notice.PSEUDO_METADATA_STORE, Market.getNavigator(context2).id());
            StringBuffer s = new StringBuffer(html.length());
            Pattern p = Pattern.compile("<%(.+?)\\r?\\n(.+?)\\r?\\n%>", 32);
            Matcher m = p.matcher(html);
            while (m.find()) {
                if (Notice.isMatch(m.group(1), pkg.packageName, metadata)) {
                    m.appendReplacement(s, m.group(2));
                } else {
                    m.appendReplacement(s, "");
                }
            }
            m.appendTail(s);
            String html2 = s.toString();
            HashMap<String, String> map = new HashMap<>();
            TypedArray a = context2.obtainStyledAttributes(new int[]{16842806, R.attr.colorAccent});
            int primaryColor = a.getColor(0, -1) & ViewCompat.MEASURED_SIZE_MASK;
            int hilightColor = a.getColor(1, primaryColor) & ViewCompat.MEASURED_SIZE_MASK;
            map.put("primary_color", String.format(Locale.US, "#%06x", Integer.valueOf(primaryColor)));
            map.put("highlight_color", String.format(Locale.US, "#%06x", Integer.valueOf(hilightColor)));
            this._html = StringUtils.format(html2, map, false);
            a.recycle();
            setTitle(StringUtils.getString_s(R.string.title_notice, label, pkg.versionName));
            setCanceledOnTouchOutside(true);
            View content = getLayoutInflater().inflate(R.layout.notice, (ViewGroup) null);
            this._webView = (WebView) content.findViewById(R.id.content);
            this._webView.loadData(this._html, "text/html", "utf-8");
            this._webView.setBackgroundColor(0);
            this._webView.setWebViewClient(new WebViewClient() { // from class: com.mxtech.widget.Notice.NoticeDialog.1
                @Override // android.webkit.WebViewClient
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        intent.addFlags(268435456);
                        view.getContext().startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Log.e(Notice.TAG, "", e);
                        return true;
                    }
                }
            });
            setView(content);
            super.setOnDismissListener(this);
        }

        @Override // android.app.Dialog
        protected void onStart() {
            super.onStart();
            WebViewUtils.enableTimers(true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.support.v7.app.AppCompatDialog, android.app.Dialog
        public void onStop() {
            super.onStop();
            WebViewUtils.enableTimers(false);
        }

        @Override // android.app.Dialog
        public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
            this._nextOnDismissListener = listener;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this._webView != null) {
                ViewUtils.removeFromParent(this._webView);
                this._webView.destroy();
                this._webView = null;
            }
            if (this._nextOnDismissListener != null) {
                this._nextOnDismissListener.onDismiss(dialog);
            }
        }
    }

    public static NoticeDialog createDialog(Context context, int noticeResId, PackageInfo pkg) {
        try {
            return new NoticeDialog(context, noticeResId, pkg);
        } catch (IOException e) {
            Log.e(MXApplication.TAG, "", e);
            return null;
        }
    }
}
