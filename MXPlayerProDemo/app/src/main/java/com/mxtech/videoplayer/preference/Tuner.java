package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.mxtech.app.DialogRegistry;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.ScreenStyle;
import com.mxtech.videoplayer.preference.TunerControl;
import com.mxtech.videoplayer.preference.TunerNavigation;
import com.mxtech.videoplayer.preference.TunerScreen;
import com.mxtech.videoplayer.preference.TunerStyle;
import com.mxtech.videoplayer.preference.TunerSubtitleLayout;
import com.mxtech.videoplayer.preference.TunerSubtitleText;
import com.mxtech.videoplayer.preference.TunerTabLayout;

/* loaded from: classes.dex */
public final class Tuner extends AlertDialog implements TunerTabLayout.Listener, TunerClient, TabHost.OnTabChangeListener, Runnable, DialogInterface.OnDismissListener {
    public static final int CATEGORY_DISPLAY = 0;
    public static final int CATEGORY_SUBTITLE = 1;
    public static final int PANE_FIRST_DISPLAY = 0;
    public static final int PANE_FIRST_SUBTITLE = 4;
    public static final int PANE_NAVIGATION = 3;
    public static final int PANE_SCREEN = 1;
    public static final int PANE_STYLE = 0;
    public static final int PANE_SUBTITLE_LAYOUT = 5;
    public static final int PANE_SUBTITLE_TEXT = 4;
    public static final int PANE_TOUCH = 2;
    public static final String TAG_NAVIGATION = "3";
    public static final String TAG_SCREEN = "1";
    public static final String TAG_STYLE = "0";
    public static final String TAG_SUBTITLE_LAYOUT = "5";
    public static final String TAG_SUBTITLE_TEXT = "4";
    public static final String TAG_TOUCH = "2";
    private final View[] _contents;
    private final Listener _listener;
    private final TunerPane[] _panes;
    private HorizontalScrollView _scroller;
    private TabHost _tabHost;
    private boolean _tabInitialPositioned;
    private DialogInterface.OnDismissListener _userOnDismissListener;
    static final String TAG = App.TAG + ".Tuner";
    private static int _lastPane = -1;

    /* loaded from: classes.dex */
    public interface Listener {
        void onDisplayBatteryClockInTitleBar(Tuner tuner, boolean z);

        void onElapsedTimeShowAlwaysChanged(Tuner tuner, boolean z);

        void onOSDColorChanged(Tuner tuner, int i, int i2);

        void onOSDPlacementChanged(Tuner tuner, boolean z, boolean z2);

        void onSSABrokenFontIgnoreChanged(Tuner tuner, boolean z);

        void onSSAFontIgnoreChanged(Tuner tuner, boolean z);

        void onScreenBrightnessChanged(Tuner tuner, float f);

        void onScreenOrientationChanged(Tuner tuner, int i);

        void onScreenStyleChanged(Tuner tuner, ScreenStyle screenStyle, int i);

        void onShowLeftTimeChanged(Tuner tuner, boolean z);

        void onShowMoveButtons(Tuner tuner, boolean z);

        void onShowPrevNextButtons(Tuner tuner, boolean z);

        void onShowScreenRotationButtonChanged(Tuner tuner, boolean z);

        void onSoftButtonsVisibilityChanged(Tuner tuner, int i);

        void onStatusTextShowAlwaysChanged(Tuner tuner, boolean z);

        void onSubtitleAlignmentChanged(Tuner tuner, int i);

        void onSubtitleBkChanged(Tuner tuner, boolean z, int i);

        void onSubtitleBorderChanged(Tuner tuner, boolean z, int i, float f);

        void onSubtitleBottomPaddingChanged(Tuner tuner, int i);

        void onSubtitleOverlayFitChanged(Tuner tuner, boolean z);

        void onSubtitleScaleChanged(Tuner tuner, float f);

        void onSubtitleShadowChanged(Tuner tuner, boolean z);

        void onSubtitleTextColorChanged(Tuner tuner, int i);

        void onSubtitleTextSizeChanged(Tuner tuner, int i);

        void onSubtitleTypefaceChanged(Tuner tuner, String str, int i);

        void onSysStatusbarVisibilityChanged(Tuner tuner, int i);

        void onTouchActionChanged(Tuner tuner, int i);
    }

    public static int getPaneCategory(int pane) {
        return pane < 4 ? 0 : 1;
    }

    @SuppressLint({"InflateParams"})
    public Tuner(Context context, Listener listener, DialogRegistry dialogRegistry, int pane, int category) {
        super(context);
        this._contents = new View[6];
        this._panes = new TunerPane[6];
        super.setOnDismissListener(this);
        this._listener = listener;
        if (pane < 0) {
            if (category >= 0) {
                int lastCategory = _lastPane >= 0 ? getPaneCategory(_lastPane) : -1;
                if (lastCategory == category) {
                    pane = _lastPane;
                } else if (category == 0) {
                    pane = 0;
                } else if (category == 1) {
                    pane = 4;
                } else {
                    throw new IllegalArgumentException("Invalid category: " + category);
                }
            } else if (_lastPane >= 0) {
                pane = _lastPane;
            } else {
                pane = 0;
            }
        }
        Resources res = context.getResources();
        View l = getLayoutInflater().inflate(R.layout.tuner_frame, (ViewGroup) null);
        setView(l);
        this._contents[0] = l.findViewById(R.id.stylePane);
        this._contents[1] = l.findViewById(R.id.screenPane);
        this._contents[2] = l.findViewById(R.id.dragPane);
        this._contents[3] = l.findViewById(R.id.navigationPane);
        this._contents[4] = l.findViewById(R.id.subtitleTextPane);
        this._contents[5] = l.findViewById(R.id.subtitleLayoutPane);
        this._panes[0] = new TunerStyle.Pane(context, ScreenStyle.getInstance(), this, (ViewGroup) this._contents[0], this._listener, dialogRegistry);
        this._panes[1] = new TunerScreen.Pane(context, this, (ViewGroup) this._contents[1], this._listener, dialogRegistry);
        this._panes[2] = new TunerControl.Pane(context, this, (ViewGroup) this._contents[2], this, this._listener, dialogRegistry);
        this._panes[3] = new TunerNavigation.Pane(context, this, (ViewGroup) this._contents[3], this._listener);
        this._panes[4] = new TunerSubtitleText.Pane(context, this, (ViewGroup) this._contents[4], this._listener, dialogRegistry);
        this._panes[5] = new TunerSubtitleLayout.Pane(context, this, (ViewGroup) this._contents[5], this._listener, dialogRegistry);
        View v = l.findViewById(16908306);
        if (v instanceof TabHost) {
            this._tabHost = (TabHost) v;
            this._tabHost.setup();
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_STYLE).setContent(R.id.stylePane).setIndicator(res.getString(R.string.style)));
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_SCREEN).setContent(R.id.screenPane).setIndicator(res.getString(R.string.tune_screen_tab)));
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_TOUCH).setContent(R.id.dragPane).setIndicator(res.getString(R.string.cfg_tuner_drag)));
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_NAVIGATION).setContent(R.id.navigationPane).setIndicator(res.getString(R.string.tune_navigation_tab)));
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_SUBTITLE_TEXT).setContent(R.id.subtitleTextPane).setIndicator(res.getString(R.string.cfg_tuner_subtitle_text)));
            this._tabHost.addTab(this._tabHost.newTabSpec(TAG_SUBTITLE_LAYOUT).setContent(R.id.subtitleLayoutPane).setIndicator(res.getString(R.string.cfg_tuner_subtitle_layout)));
            TabWidget tabWidget = this._tabHost.getTabWidget();
            for (int i = 0; i < this._panes.length; i++) {
                View[] topViews = this._panes[i].getTopmostFocusableViews();
                if (topViews.length > 0) {
                    for (View view : topViews) {
                        view.setNextFocusUpId(16908307);
                    }
                    tabWidget.getChildTabViewAt(i).setNextFocusDownId(topViews[0].getId());
                }
            }
            this._tabHost.setCurrentTab(pane);
            this._tabHost.setOnTabChangedListener(this);
            this._scroller = (HorizontalScrollView) l.findViewById(R.id.scroller);
            return;
        }
        TunerTabLayout tabLayout = (TunerTabLayout) v;
        tabLayout.setListener(this);
        tabLayout.select(pane);
    }

    private void saveChanges() {
        TunerPane[] tunerPaneArr;
        TunerPane[] tunerPaneArr2;
        for (TunerPane pane : this._panes) {
            if (pane.dirty) {
                SharedPreferences.Editor editor = App.prefs.edit();
                for (TunerPane p2 : this._panes) {
                    if (p2.dirty) {
                        p2.applyChanges(editor);
                        p2.dirty = false;
                    }
                }
                editor.commit();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int valueToIndex(int value, int[] values, int defaultValue) {
        int i = 0;
        for (int o : values) {
            if (o != value) {
                i++;
            } else {
                return i;
            }
        }
        return defaultValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AlertDialog, android.support.v7.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.dimAmount = 0.3f;
        window.setAttributes(layout);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this._tabInitialPositioned && this._tabHost != null && this._scroller != null) {
            run();
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this._scroller != null) {
            this._scroller.removeCallbacks(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatDialog, android.app.Dialog
    public void onStop() {
        saveChanges();
        super.onStop();
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        saveChanges();
        return super.onSaveInstanceState();
    }

    @Override // java.lang.Runnable
    public void run() {
        int scrollerWidth = this._scroller.getWidth();
        if (scrollerWidth == 0) {
            this._scroller.postDelayed(this, 100L);
            return;
        }
        View tab = this._tabHost.getCurrentTabView();
        if (scrollerWidth < tab.getRight()) {
            this._scroller.scrollTo(tab.getLeft(), 0);
        }
        this._tabInitialPositioned = true;
    }

    @Override // com.mxtech.videoplayer.preference.TunerTabLayout.Listener
    public void onTabSelected(TextView tab, int index) {
        int i = 0;
        while (i < this._contents.length) {
            this._contents[i].setVisibility(index == i ? 0 : 4);
            i++;
        }
        _lastPane = index;
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String tabId) {
        _lastPane = Integer.parseInt(tabId);
    }

    @Override // com.mxtech.videoplayer.preference.TunerClient
    public void saveAndDismiss() {
        dismiss();
    }

    @Override // android.app.Dialog
    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this._userOnDismissListener = listener;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        ((TunerSubtitleText.Pane) this._panes[4]).onDismiss();
        if (this._userOnDismissListener != null) {
            this._userOnDismissListener.onDismiss(dialog);
        }
    }

    public void onLowMemory() {
        ((TunerSubtitleText.Pane) this._panes[4]).onLowMemory();
    }
}
