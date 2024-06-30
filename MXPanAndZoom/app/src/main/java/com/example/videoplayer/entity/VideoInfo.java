package com.example.videoplayer.entity;



import android.graphics.Bitmap;
import androidx.databinding.BaseObservable;

/**
 * @Author      : Liu XiaoRan
 * @Email       : 592923276@qq.com
 * @Date        : on 2023/1/9 09:42.
 * @Description : 视频信息
 */
public class VideoInfo extends BaseObservable {
    public boolean checked = false;
    public boolean checkBoxVisibility = false; // 选择框的可见性,跟随列表的编辑模式开关

    public int id = 0; // 视频id
    public String path = null; // 文件路径
    public long size = 0; // 大小
    public String displayName = null; // 视频名称，不带后缀
    public String title = null; // 视频标题，带后缀
    public long duration = 0; // 时长，部分视频损坏/其他原因没有
    public String resolution = null; // 分辨率
    public int isPrivate = 0; // 是否私密
    public String bucketId = null; // 装载（文件夹）id
    public String bucketDisplayName = null; // 装载文件夹名称
    public Bitmap thumbnail = null; // 缩略图
    public String bookmark = null; // 书签，上次播放的位置

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketDisplayName() {
        return bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName) {
        this.bucketDisplayName = bucketDisplayName;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }
}

