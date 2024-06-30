package com.example.videoplayer.base;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;
import com.blankj.utilcode.util.LogUtils;
//import com.gyf.immersionbar.ktx.fitsTitleBar;
import com.gyf.immersionbar.ImmersionBar;
import com.example.videoplayer.constants.MessageEvent;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
//import com.lxr.video_player.constants.MessageEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public abstract class BaseActivity extends AppCompatActivity implements OnTitleBarListener {
    String TAG = getClass().getName();
//    protected T binding;

    protected LoadingPopupView loadingPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeInitView();


        setContentView(getLayoutId());

        EventBus.getDefault().register(this);

        initTitleStatusBar();
        initView();
        initListener();
    }

    /**
     * 由子类实现
     * @return 返回子类的布局id
     */
    public abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSimpleMessage(String simpleMessage) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {

    }

    /**
     * 布局初始化之前
     */
    protected void initBeforeInitView(){

    }

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    /**
     * 初始化标题栏和沉浸式状态栏
     */
    private void initTitleStatusBar(){
        TitleBar findTitleBar = findTitleBar(findViewById(Window.ID_ANDROID_CONTENT));
        if (findTitleBar != null) {
            findTitleBar.setOnTitleBarListener(this);
        }

//        ImmersionBar(immersionBar -> {
//            if (findTitleBar != null) {
//                return immersionBar.fitsTitleBar(findTitleBar);
//            }
//            return null;
//        });

        ImmersionBar.with(this).init();


    }


    @Override
    public void onLeftClick(TitleBar titleBar) {
        OnTitleBarListener.super.onLeftClick(titleBar);
        onBackPressed();
    }

    @Override
    public void onRightClick(TitleBar titleBar) {
        OnTitleBarListener.super.onRightClick(titleBar);
    }

    @Override
    public void onTitleClick(TitleBar titleBar) {
        OnTitleBarListener.super.onTitleClick(titleBar);
    }

    private TitleBar findTitleBar(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof TitleBar) {
                return (TitleBar) view;
            } else if (view instanceof ViewGroup) {
                TitleBar titleBar = findTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }

    /**
     * 打开等待框
     */
    protected void showLoading() {
        if (loadingPopup == null) {
            loadingPopup = (LoadingPopupView) new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .isLightNavigationBar(true)
                    .hasShadowBg(false)
                    .asLoading()
                    .show();
        }
        loadingPopup.show();
    }

    /**
     * 关闭等待框
     */
    protected void dismissLoading() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }
    }
}

