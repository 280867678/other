package com.example.videoplayer.entity;

import java.util.List;

/**
 * @Author      : Liu XiaoRan
 * @Email       : 592923276@qq.com
 * @Date        : on 2023/1/11 15:41.
 * @Description : 装载视频的文件夹对象
 */
public class VideoFolder {
    public String name;
    public List<VideoInfo> videoList;

    public VideoFolder(String name, List<VideoInfo> videoList) {
        this.name = name;
        this.videoList = videoList;
    }
}

