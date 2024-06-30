package com.example.videoplayer.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.videoplayer.R;
import com.example.videoplayer.entity.VideoFolder;
import com.example.videoplayer.ui.LocalVideoListActivity;
import com.example.videoplayer.ui.PlayerActivity;
import com.example.videoplayer.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHoder> {
    private List<VideoFolder> list = new ArrayList<>();
    private Context context;

    public ListAdapter(List<VideoFolder> videoList, Context context) {
        this.list = videoList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folders, parent, false);
        ViewHoder myViewHoder = new ViewHoder(view);
        return myViewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, @SuppressLint("RecyclerView") int position) {
        VideoFolder news = list.get(position);
        Glide.with(context).load(news.videoList.get(0).path)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.iv_video)
                .centerCrop().into(holder.imageView);
//        // 总时长
//        String duration = "";
//        if (Integer.parseInt(String.valueOf(news.duration)) == 0) { // 缺失时长/缩略图的,从缓存取(如果有)
//            long cacheDuration =
//                    SPUtils.getInstance().getLong(String.valueOf(news.id), 0L);
//            if (cacheDuration != 0L) { // 有缓存
//                duration = CommonUtil.stringForTime(cacheDuration);
//                holder.progressBar.setMax((int) cacheDuration);
//            }
//        } else { // 正常视频
//            duration = CommonUtil.stringForTime(news.duration);
//            holder.progressBar.setMax((int) news.duration);
//        }
//        // 已播放进度时长
//        long progressPlayed = SpUtil.getLong(String.valueOf(news.id));
//        if (progressPlayed != -0L && !duration.isEmpty()) { // 有进度且有时长都显示
//            holder.textView.setText(CommonUtil.stringForTime(progressPlayed) + "/" + duration);
//            holder.progressBar.setProgress((int) progressPlayed);
//        } else { // 没有进度(也包括没时长,此时是空串,不耽误)
//
//            holder.textView.setText(duration);
//        }


//        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, LocalVideoListActivity.class);
//                intent.putExtra("title", news.name);
//                intent.putExtra("bucketDisplayName", news.videoList.get(0).bucketDisplayName);
//                context.startActivity(intent);
//            }
//        });


    }

//    onClick(R.id.item) {
//        val intent = Intent(this@MovieFoldersListFragment.context, PlayerActivity::class.java)
//        intent.putExtra("videoList", GsonUtils.toJson(models))
//        intent.putExtra("position", modelPosition)
//        startActivity(intent)
//    }


//    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHoder extends RecyclerView.ViewHolder {
        ImageView imageView;
//        ProgressBar progressBar;
//        TextView textView;
        ConstraintLayout constraintLayout;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
//            progressBar = itemView.findViewById(R.id.progressBar);
//            textView = itemView.findViewById(R.id.tv_duration);
            constraintLayout = itemView.findViewById(R.id.item);
        }
    }

}
