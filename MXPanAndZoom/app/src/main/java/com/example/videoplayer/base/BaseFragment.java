package com.example.videoplayer.base;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.LogUtils;
//import com.gyf.immersionbar.ktx.fitsTitleBar;
//import com.gyf.immersionbar.ktx.immersionBar;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseFragment extends Fragment implements OnTitleBarListener {
    private String TAG = getClass().getName();
    protected LoadingPopupView loadingPopup;
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initTitleStatusBar();
        initData();
        initListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String simpleMessage) {
    }

    public abstract int getLayoutId();

    public void initView() {
    }

    public void initData() {
    }

    public void initListener() {
    }

    protected void showLoading() {
        if (loadingPopup == null) {
            loadingPopup = (LoadingPopupView) new XPopup.Builder(getContext())
                    .dismissOnBackPressed(false)
                    .isLightNavigationBar(true)
                    .hasShadowBg(false)
                    .asLoading()
                    .show();
        }
        loadingPopup.show();
    }

    protected void dismissLoading() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }
    }

    private void initTitleStatusBar() {
        View findTitleBar = findTitleBar((ViewGroup) rootView);
        if (findTitleBar instanceof TitleBar) {
            ((TitleBar) findTitleBar).setOnTitleBarListener(this);
        }

//        immersionBar(immersionBar -> {
//            if (findTitleBar != null) {
//                fitsTitleBar(findTitleBar);
//            }
//        });

        ImmersionBar.with(this).init();
    }

    private View findTitleBar(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof TitleBar || view instanceof Toolbar) {
                return view;
            } else if (view instanceof ViewGroup) {
                View titleBar = findTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }

    @Override
    public void onLeftClick(TitleBar titleBar) {
        OnTitleBarListener.super.onLeftClick(titleBar);
    }

    @Override
    public void onRightClick(TitleBar titleBar) {
        OnTitleBarListener.super.onRightClick(titleBar);
    }

    @Override
    public void onTitleClick(TitleBar titleBar) {
        OnTitleBarListener.super.onTitleClick(titleBar);
    }
}

