package com.carrydream.cardrecorder.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import com.carrydream.cardrecorder.tool.EventUtil;
import com.gyf.immersionbar.ImmersionBar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* loaded from: classes.dex */
public abstract class BaseFragment extends Fragment {
    public Map<String, Object> device;
    protected View rootView;
    private boolean isOk = false;
    private boolean isFirst = true;
    public List<Long> ads = new ArrayList();

    protected abstract void init();

    protected abstract void lazyLoad();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBase(BaseMessage<String> baseMessage) {
    }

    protected void refreshLoad() {
    }

    protected abstract int setLayoutId();

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(setLayoutId(), viewGroup, false);
        this.rootView = inflate;
        ButterKnife.bind(this, inflate);
        ImmersionBar.with(this).transparentStatusBar().barAlpha(0.0f).keyboardEnable(false).init();
        EventUtil.register(this);
        init();
        this.isOk = true;
        return this.rootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        initLoadData();
    }

    private void initLoadData() {
        if (this.isOk && this.isFirst) {
            lazyLoad();
            this.isFirst = false;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void showDialogSafety(Activity activity, Dialog dialog) {
        try {
            if (activity.isFinishing() || dialog == null || dialog.isShowing()) {
                return;
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception unused) {
        }
    }
}
