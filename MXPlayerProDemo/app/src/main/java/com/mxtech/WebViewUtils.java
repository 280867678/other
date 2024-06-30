package com.mxtech;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.mxtech.app.MXApplication;
import java.util.List;

/* loaded from: classes.dex */
public class WebViewUtils {
    private static final String TAG = "MX.WebViewUtils";
    private static TimerController _timerController;

    private static void callOnResume(WebView wv) throws Exception {
        if (Build.VERSION.SDK_INT >= 11) {
            wv.onResume();
        } else {
            WebView.class.getMethod("onResume", null).invoke(wv, null);
        }
    }

    private static void callOnPause(WebView wv) throws Exception {
        if (Build.VERSION.SDK_INT >= 11) {
            wv.onPause();
        } else {
            WebView.class.getMethod("onPause", null).invoke(wv, null);
        }
    }

    public static void onResume(WebView wv) {
        try {
            callOnResume(wv);
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
    }

    public static void onPause(WebView wv) {
        try {
            callOnPause(wv);
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
    }

    public static void onResumeAllWebViews(ViewGroup v) {
        if (v instanceof WebView) {
            try {
                callOnResume((WebView) v);
            } catch (Exception e) {
                Log.w(TAG, "", e);
                return;
            }
        }
        for (int i = v.getChildCount() - 1; i >= 0; i--) {
            View child = v.getChildAt(i);
            if (child instanceof ViewGroup) {
                onResumeAllWebViews((ViewGroup) child);
            }
        }
    }

    public static void onPauseAll(ViewGroup v, List<WebView> list) {
        if (v instanceof WebView) {
            try {
                WebView wv = (WebView) v;
                callOnPause(wv);
                list.add(wv);
            } catch (Exception e) {
                Log.w(TAG, "", e);
                return;
            }
        }
        for (int i = v.getChildCount() - 1; i >= 0; i--) {
            View child = v.getChildAt(i);
            if (child instanceof ViewGroup) {
                onPauseAll((ViewGroup) child, list);
            }
        }
    }

    public static void onResumeAll(List<WebView> list) {
        for (WebView wv : list) {
            onResume(wv);
        }
        list.clear();
    }

    /* loaded from: classes2.dex */
    private static class TimerController extends Handler {
        private static final int DESTROY_DELAY = 10000;
        private static final int MSG_DESTROY = 1;
        private int _refCounter;
        @Nullable
        private WebView _tinyWebView;

        private TimerController() {
        }

        void enable() {
            int i = this._refCounter + 1;
            this._refCounter = i;
            if (i == 1) {
                if (this._tinyWebView == null) {
                    try {
                        this._tinyWebView = new WebView(MXApplication.context);
                    } catch (Exception e) {
                        Log.e(WebViewUtils.TAG, "", e);
                    }
                }
                if (this._tinyWebView != null) {
                    this._tinyWebView.resumeTimers();
                }
                removeMessages(1);
            }
        }

        void disable() {
            int i = this._refCounter - 1;
            this._refCounter = i;
            if (i == 0) {
                if (this._tinyWebView != null) {
                    this._tinyWebView.pauseTimers();
                }
                sendEmptyMessageDelayed(1, 10000L);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1 && this._tinyWebView != null) {
                this._tinyWebView.destroy();
                this._tinyWebView = null;
            }
        }
    }

    public static synchronized void enableTimers(boolean enable) {
        synchronized (WebViewUtils.class) {
            if (_timerController == null) {
                _timerController = new TimerController();
            }
            if (enable) {
                _timerController.enable();
            } else {
                _timerController.disable();
            }
        }
    }
}
