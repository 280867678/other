package com.example.videoplayer;

import android.app.Application;
import android.content.Context;

import com.example.videoplayer.utils.SpUtil;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.mmkv.MMKV;
import com.drake.brv.utils.BRV;
import com.lxj.xpopup.XPopup;
import me.jessyan.autosize.AutoSizeConfig;
import com.example.videoplayer.constants.Constants;

public class APP extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context= this;

        MMKV.initialize(this);
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new MaterialHeader((Context) context));
//        BRV.INSTANCE.setModelId(BR.m);
//        BRV.INSTANCE.getModelId();


        AutoSizeConfig.getInstance().setExcludeFontScale(true);
        XPopup.setPrimaryColor(R.color.colorPrimary);
        initCacheConfig();


    }

    private void initCacheConfig() {
        if (SpUtil.getString(Constants.K_DEFAULT_PATH_4_FIND_SUBTITLE).isEmpty()) {
            SpUtil.put(Constants.K_DEFAULT_PATH_4_FIND_SUBTITLE, Constants.V_DEFAULT_PATH_4_FIND_SUBTITLE);
        }
    }


}
