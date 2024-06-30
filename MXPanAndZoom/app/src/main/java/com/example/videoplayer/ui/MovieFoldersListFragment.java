package com.example.videoplayer.ui;


import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drake.brv.annotaion.AnimationType;

import com.example.videoplayer.R;
import com.example.videoplayer.adapter.HistoryListAdapter;
import com.example.videoplayer.adapter.ListAdapter;
import com.example.videoplayer.base.BaseFragment;
import com.example.videoplayer.entity.VideoFolder;
import com.example.videoplayer.entity.VideoInfo;
import com.example.videoplayer.utils.SpUtil;
import com.example.videoplayer.utils.Utils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.drake.brv.utils.divider;
//import com.drake.brv.utils.linear;
//import com.drake.brv.utils.models;
//import com.drake.brv.utils.setup;

public class MovieFoldersListFragment extends BaseFragment {

    private List<VideoFolder> folderList = new ArrayList<>();
    private RecyclerView rvPlayHistory;
    private RecyclerView rv;
    HistoryListAdapter historyListAdapter;
    ListAdapter listAdapter;
    List<VideoInfo> videoList;
//    List<VideoFolder> videoFolders;

    LinearLayout llPlayHistoryContainer;

    @Override
    public void initData() {
        super.initData();
        updateListData();
    }

    @Override
    public void initView() {


        rvPlayHistory = rootView.findViewById(R.id.rv_play_history);
        llPlayHistoryContainer = rootView.findViewById(R.id.ll_play_history_container);
        rv = rootView.findViewById(R.id.rv);
//        rvPlayHistory = rootView.findViewById(R.id.rv_play_history);
        historyListAdapter = new HistoryListAdapter(videoList, getContext());
        rvPlayHistory.setAdapter(historyListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPlayHistory.setLayoutManager(layoutManager);


//        rvPlayHistory.run(() -> {
//            linear(orientation = LinearLayoutManager.HORIZONTAL).setup(() -> {
//                addType<VideoInfo> (R.layout.item_play_history);
//                onBind(() -> {
//                    Glide.with(context).load(getModel < VideoInfo > ().path)
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .placeholder(R.drawable.iv_video)
//                            .centerCrop().into(findView(R.id.iv));
//
//                    // 总时长
//                    String duration = "";
//                    if (getModel < VideoInfo > ().duration.toInt() == 0) {
//                        long cacheDuration = SPUtils.getInstance().getLong(getModel < VideoInfo > ().id.toString(), 0L);
//                        if (cacheDuration != 0L) {
//                            duration = CommonUtil.stringForTime(cacheDuration);
//                            findView<ProgressBar> (R.id.progressBar).setMax(cacheDuration.toInt());
//                        }
//                    } else {
//                        duration = CommonUtil.stringForTime(getModel < VideoInfo > ().duration);
//                        findView<ProgressBar>
//                        (R.id.progressBar).setMax(getModel < VideoInfo > ().duration.toInt());
//                    }
//
//                    // 已播放进度时长
//                    long progressPlayed = SpUtil.getLong(getModel < VideoInfo > ().id.toString());
//                    if (progressPlayed != -0L && !duration.isEmpty()) {
//                        findView<TextView>
//                        (R.id.tv_duration).setText(CommonUtil.stringForTime(progressPlayed) + "/" + duration);
//                        findView<ProgressBar>
//                        (R.id.progressBar).setProgress(progressPlayed.toInt());
//                    } else {
//                        findView<TextView> (R.id.tv_duration).setText(duration);
//                    }
//                });
//                onClick(R.id.item, () -> {
//                    Intent intent = new Intent(this @MovieFoldersListFragment.getContext(),
//                    PlayerActivity.class);
//                    intent.putExtra("videoList", GsonUtils.toJson(models));
//                    intent.putExtra("position", modelPosition);
//                    startActivity(intent);
//                });
//            });
//        });


        listAdapter = new ListAdapter(folderList, getContext());
        rv.setAdapter(listAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager layoutManagers = new LinearLayoutManager(getContext());
        layoutManagers.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManagers);


//        rv.run(() -> {
//            linear().divider(R.drawable.divider).setup(() -> {
//                setAnimation(AnimationType.SLIDE_BOTTOM);
//                addType<VideoFolder> (R.layout.item_folder);
//                onBind(() -> {
//                    Glide.with(context)
//                            .load(getModel < VideoFolder > ().videoList.get(0).getPath())
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .placeholder(R.drawable.iv_video)
//                            .centerCrop().into(findView(R.id.iv));
//                });
//                onClick(R.id.item, () -> {
//                    Intent intent = new Intent(this @MovieFoldersListFragment.getContext(),
//                    LocalVideoListActivity.class);
//                    intent.putExtra("title", getModel < VideoFolder > ().getName());
//                    intent.putExtra("bucketDisplayName", getModel < VideoFolder > ().getVideoList().get(0).getBucketDisplayName());
//                    startActivity(intent);
//                });
//            });
//        });
        showPermissionTipPopup();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            if (XXPermissions.isGranted(getContext(),
                    Permission.MANAGE_EXTERNAL_STORAGE)) {
                updateListData();
            }
        }
    }

    private void showPermissionTipPopup() {
        if (getContext() != null) {
            if (!XXPermissions.isGranted(getContext(),
                    Permission.MANAGE_EXTERNAL_STORAGE)) {
                new XPopup.Builder(getContext())
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asConfirm(
                                "提示",
                                "为了播放视频、音频、获取字幕,我们需要访问您设备文件的权限",
                                "就不给",
                                "好哒",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        getPermission2getData();
                                    }
                                },
                                new OnCancelListener() {
                                    @Override
                                    public void onCancel() {
                                        ActivityUtils.finishAllActivities(true);
                                    }
                                },
                                false
                        )
                        .show();
            } else {
                updateListData();
            }
        }
    }

    private void getPermission2getData() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            ToastUtils.showLong("部分权限未正常授予,请授权");
                            return;
                        }
                        updateListData();
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            ToastUtils.showLong("读写文件权限被永久拒绝，请手动授权");
                            XXPermissions.startPermissionActivity(MovieFoldersListFragment.this,
                                    permissions);
                        } else {
                            showPermissionTipPopup();
                            ToastUtils.showShort("获取权限失败");
                        }
                    }
                });
    }


    private void updateListData() {
        folderList.clear();
        List<VideoInfo> videoList = Utils.getVideoList();
//        Map<String, List<VideoInfo>> groupByBucketIdMap = videoList.stream().collect(Collectors.groupingBy(VideoInfo::getBucketDisplayName));
        Map<String, List<VideoInfo>> groupByBucketIdMap = new HashMap<>();
        for (VideoInfo video : videoList) {
            String bucketDisplayName = video.getBucketDisplayName();
            if (groupByBucketIdMap.containsKey(bucketDisplayName)) {
                groupByBucketIdMap.get(bucketDisplayName).add(video);
            } else {
                List<VideoInfo> newList = new ArrayList<>();
                newList.add(video);
                groupByBucketIdMap.put(bucketDisplayName, newList);
            }
        }

        this.videoList = videoList;

        for (Map.Entry<String, List<VideoInfo>> entry : groupByBucketIdMap.entrySet()) {
            folderList.add(new VideoFolder(entry.getKey(), entry.getValue()));
        }
//        rv.models = folderList;

//        List<VideoInfo> mutablePlayHistoryList = videoList.stream()
//                .filter(video -> SpUtil.getLong(String.valueOf(video.getId())) != 0L &&
//                        (SPUtils.getInstance().getLong(String.valueOf(video.getId()), 0L) != 0L || video.getDuration() != 0L))
//                .collect(Collectors.toList());
        List<VideoInfo> mutablePlayHistoryList = new ArrayList<>();
        for (VideoInfo video : videoList) {
            long spUtilValue = SpUtil.getLong(String.valueOf(video.getId()));
            long spUtilsValue = SPUtils.getInstance().getLong(String.valueOf(video.getId()), 0L);

            if (spUtilValue != 0L && (spUtilsValue != 0L || video.getDuration() != 0L)) {
                mutablePlayHistoryList.add(video);
            }
        }


        if (mutablePlayHistoryList.size() != 0) {
//            llPlayHistoryContainer.isVisible(true);
            llPlayHistoryContainer.setVisibility(View.VISIBLE);

//            rvPlayHistory.models = mutablePlayHistoryList;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onEvent(String simpleMessage) {
        if ("refresh".equals(simpleMessage)) {
            updateListData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_movie_folders_list;
    }
}















