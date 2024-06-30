package com.example.videoplayer.ui;


import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
//import com.dyne.myktdemo.base.BaseFragment;
import com.example.videoplayer.R;
import com.example.videoplayer.base.BaseFragment;
import com.example.videoplayer.constants.Constants;
import com.example.videoplayer.utils.SpUtil;
import com.github.gzuliyujiang.filepicker.ExplorerConfig;
import com.github.gzuliyujiang.filepicker.FilePicker;
import com.github.gzuliyujiang.filepicker.annotation.ExplorerMode;
import com.github.gzuliyujiang.filepicker.contract.OnFilePickedListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
//import com.lxr.video_player.R;
//import com.lxr.video_player.constants.Constants;
//import com.lxr.video_player.constants.SimpleMessage;
//import com.lxr.video_player.databinding.FragmentSettingBinding;
//import com.lxr.video_player.utils.SpUtil;
//import com.shuyu.gsyvideoplayer.GSYVideoManager;
//import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
//import com.shuyu.gsyvideoplayer.player.PlayerFactory;
//import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;

import org.greenrobot.eventbus.EventBus;

//import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import java.io.File;

public class SettingFragment extends BaseFragment {
    private TextView rlPlayerManager;
    ;
    private RelativeLayout rlSubtitlePath;
    ;
    private TextView tvSubtitlePath;
    private RelativeLayout rlClearCache;
    ;
    private TextView tvPlayerManager;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView() {
        tvSubtitlePath = rootView.findViewById(R.id.tv_subtitle_path);
        rlPlayerManager = rootView.findViewById(R.id.tv_player_manager);
        tvPlayerManager = rootView.findViewById(R.id.tv_player_manager);
        rlClearCache = rootView.findViewById(R.id.rl_clear_cache);
        rlSubtitlePath = rootView.findViewById(R.id.rl_subtitle_path);

        tvSubtitlePath.setText(SpUtil.getString(Constants.K_DEFAULT_PATH_4_FIND_SUBTITLE));
        rlPlayerManager.setOnClickListener(v -> {
            new XPopup.Builder(requireContext())
                    .asCenterList(
                            null,
                            new String[]{
                                    getResources().getString(R.string.player_manager_ijk),
                                    getResources().getString(R.string.player_manager_exo),
                                    getResources().getString(R.string.player_manager_system)
                            },
                            (position, text) -> {
                                switch (position) {
                                    case 0:
//                                        PlayerFactory.setPlayManager(IjkPlayerManager.class);
                                        tvPlayerManager.setText(getResources().getString(R.string.player_manager_ijk));
                                        break;
                                    case 1:
//                                        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
                                        tvPlayerManager.setText(getResources().getString(R.string.player_manager_exo));
                                        break;
                                    case 2:
//                                        PlayerFactory.setPlayManager(SystemPlayerManager.class);
                                        tvPlayerManager.setText(getResources().getString(R.string.player_manager_system));
                                        break;
                                }
                            }
                    ).show();
        });

        rlClearCache.setOnClickListener(v -> {
            new XPopup.Builder(requireContext())
                    .asConfirm(
                            "提示",
                            "确认清理缓存?",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    SpUtil.clearAll();
                                    SPUtils.getInstance().clear();
//                                    GSYVideoManager.instance().clearAllDefaultCache(requireContext());
//                                    EventBus.getDefault().post(SimpleMessage.REFRESH);
                                }
                            }
                    ).show();
        });

        rlSubtitlePath.setOnClickListener(v -> {
            ExplorerConfig config = new ExplorerConfig(requireContext());
            config.setRootDir(new File(SpUtil.getString(Constants.K_DEFAULT_PATH_4_FIND_SUBTITLE)));
            config.setExplorerMode(ExplorerMode.DIRECTORY);
            config.setOnFilePickedListener(new OnFilePickedListener() {
                @Override
                public void onFilePicked(File file) {
                    tvSubtitlePath.setText(file.getAbsolutePath());
                    SpUtil.put(Constants.K_DEFAULT_PATH_4_FIND_SUBTITLE, file.getAbsolutePath());
                }
            });
            FilePicker picker = new FilePicker(requireActivity());
            picker.setExplorerConfig(config);
            picker.getOkView().setText("就用这个目录");
            picker.show();
        });
    }
}



