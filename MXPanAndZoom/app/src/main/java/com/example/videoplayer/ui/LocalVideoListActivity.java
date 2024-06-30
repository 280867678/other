package com.example.videoplayer.ui;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import com.example.videoplayer.R;
//
//public class LocalVideoListActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_local_video_list);
//    }
//}





import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drake.brv.annotaion.AnimationType;
import com.drake.brv.utils.BRV;
//import com.drake.brv.utils.DividerFactory;
//import com.drake.brv.utils.LinearFactory;
//import com.drake.brv.utils.ModelFactory;
//import com.drake.brv.utils.ViewSetup;
//import com.dyne.myktdemo.base.BaseActivity;
import com.example.videoplayer.R;
import com.example.videoplayer.base.BaseActivity;
import com.example.videoplayer.entity.VideoInfo;
import com.hjq.bar.TitleBar;
import com.lihang.ShadowLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
//import com.lxr.video_player.R;
//import com.lxr.video_player.constants.SimpleMessage;
//import com.lxr.video_player.databinding.ActivityVideoListBinding;
//import com.lxr.video_player.entity.VideoInfo;
//import com.lxr.video_player.utils.SpUtil;
//import com.lxr.video_player.utils.Utils;
//import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author : Liu XiaoRan
 * @Email : 592923276@qq.com
 * @Date : on 2023/1/9 16:25.
 * @Description :
 */
public class LocalVideoListActivity extends BaseActivity {
    TitleBar titleBar;
    /**
     * 当前文件夹id,由文件夹列表传进
     */
    private String bucketDisplayName = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_video_list;
    }

    @Override
    public void initView() {
        titleBar = findViewById(R.id.title_bar);

        titleBar.setLeftTitle(getIntent().getStringExtra("title"));
        bucketDisplayName = getIntent().getStringExtra("bucketDisplayName");



//        BRV.linear().divider(DividerFactory.create(R.drawable.divider)).setup(binding.rv)
//                .anim(AnimationType.SLIDE_BOTTOM)
//                .models(ModelFactory.create(VideoInfo.class, R.layout.item_video))
//                .onBind((ViewSetup.OnBind<VideoInfo>) (holder, model, position) -> {
//                    Glide.with(holder.getContext()).load(model.getPath())
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .placeholder(R.drawable.iv_video)
//                            .centerCrop().into(holder.findView(R.id.iv));
//                    // 总时长
//                    String duration = "";
//                    if (model.getDuration().intValue() == 0) { // 缺失时长/缩略图的,从缓存取(如果有)
//                        Long cacheDuration = SPUtils.getInstance().getLong(model.getId().toString(), 0L);
//                        if (cacheDuration != 0L) { // 有缓存
//                            duration = CommonUtil.stringForTime(cacheDuration);
//                            holder.findView(R.id.progressBar).setMax(cacheDuration.intValue());
//                        }
//                    } else { // 正常视频
//                        duration = CommonUtil.stringForTime(model.getDuration());
//                        holder.findView(R.id.progressBar).setMax(model.getDuration().intValue());
//                    }
//                    // 已播放进度时长
//                    Long progressPlayed = SpUtil.getLong(model.getId().toString());
//                    if (progressPlayed != -0L && !duration.isEmpty()) { // 有进度且有时长都显示
//                        holder.findView(R.id.tv_duration).setText(CommonUtil.stringForTime(progressPlayed) + "/" + duration);
//                        holder.findView(R.id.progressBar).setProgress(progressPlayed.intValue());
//                    } else { // 没有进度(也包括没时长,此时是空串,不耽误)
//                        holder.findView(R.id.tv_duration).setText(duration);
//                    }
//                })
//                // 监听编辑(切换)模式,当前页面的backPress方法也实现了关闭方法,也会触发
//                .onToggle((position, toggleMode, end) -> {
//                    // 刷新列表,item的选择按钮根据开关显隐,所以要刷新
//                    VideoInfo model = BRV.getModel(binding.rv, position);
//                    model.setCheckBoxVisibility(toggleMode);
//                    // 数据变化.通知ui变化
//                    model.notifyChange();
//                    if (end) { // 列表遍历结束
//                        // ...
//                    }
//                });
    }
}