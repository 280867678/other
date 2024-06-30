package com.example.videoplayer.ui;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.videoplayer.R;
import com.example.videoplayer.base.BaseActivity;
import com.example.videoplayer.entity.VideoInfo;
import com.example.videoplayer.utils.Utils;

import java.util.List;


public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        List<VideoInfo> list = Utils.getVideoList();
        for (int i=0;i<list.size();i++){
            VideoInfo videoInfo = list.get(i);
            Log.e("视频："+i+"：", "id："+String.valueOf(videoInfo.id));
            Log.e("视频："+i+"：","文件路径："+videoInfo.path);
            Log.e("视频："+i+"：","大小："+videoInfo.size);
            Log.e("视频："+i+"：","视频名称，不带后缀："+videoInfo.displayName);
            Log.e("视频："+i+"：","视频标题，带后缀："+videoInfo.title);
            Log.e("视频："+i+"：","时长，部分视频损坏/其他原因没有："+videoInfo.duration);
            Log.e("视频："+i+"：","分辨率："+videoInfo.resolution);
            Log.e("视频："+i+"：","是否私密："+videoInfo.isPrivate);
            Log.e("视频："+i+"：","装载（文件夹）id："+videoInfo.bucketId);
            Log.e("视频："+i+"：","装载文件夹名称："+videoInfo.bucketDisplayName);
            Log.e("视频："+i+"：","缩略图："+videoInfo.thumbnail);
            Log.e("视频："+i+"：","书签，上次播放的位置："+videoInfo.bookmark);
        }





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 500);
    }
}
