package cn.jzvd.demo.Tab_2_Custom.AGVideo.mediaplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.Jzvd;
import cn.jzvd.demo.R;
import cn.jzvd.demo.Tab_2_Custom.AGVideo.AGVideo;

/**
 * Created by MinhDV on 5/3/18.
 */
public class MediaExo extends JZMediaInterface implements Player.EventListener, VideoListener {
    private SimpleExoPlayer simpleExoPlayer;
    private Runnable callback;
    private String TAG = "MediaExo";
    private long previousSeek = 0;

    public MediaExo(Jzvd jzvd) {
        super(jzvd);
    }

    @Override
    public void start() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void prepare() {
        Log.e(TAG, "prepare" + "  jzvd.state:" + jzvd.state);
        Context context = jzvd.getContext();
        if (jzvd.state == Jzvd.STATE_PREPARING_CHANGE_URL || jzvd.state == Jzvd.STATE_AUTO_COMPLETE) {
            changeVideo();
        } else {
            release();
            mMediaHandlerThread = new HandlerThread("JZVD");
            mMediaHandlerThread.start();
            mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
            handler = new Handler();
            mMediaHandler.post(() -> {
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory();
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                        360000, 600000, 1000, 5000,
                        C.LENGTH_UNSET,
                        false);

                // 2. Create the player

                RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl);
                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
                String currUrl = "";
                if (jzvd.jzDataSource != null) {
                    Object currentUrl = jzvd.jzDataSource.getCurrentUrl();
                    if (currentUrl != null) {
                        currUrl = currentUrl.toString();
                    }
                }
                MediaSource videoSource;
                if (currUrl.contains(".m3u8")) {
                    videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(currUrl), handler, null);
                } else {
                    videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(currUrl));
                }
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.addVideoListener(this);
                    Log.e(TAG, "URL Link = " + currUrl);
                    simpleExoPlayer.addListener(this);
                }


                boolean isLoop = false;
                if (jzvd.jzDataSource != null) {
                    isLoop = jzvd.jzDataSource.looping;
                }
                if (simpleExoPlayer != null) {
                    if (isLoop) {
                        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                    } else {
                        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    }
                    simpleExoPlayer.prepare(videoSource);
                    simpleExoPlayer.setPlayWhenReady(true);
                }
                callback = new onBufferingUpdate();
                if (jzvd.textureView != null) {
                    SurfaceTexture surfaceTexture=jzvd.textureView.getSurfaceTexture();
                    if (surfaceTexture!=null){
                        simpleExoPlayer.setVideoSurface(new Surface(surfaceTexture));
                    }
                }
            });
        }

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        handler.post(() -> jzvd.onVideoSizeChanged(width, height));
    }

    @Override
    public void onRenderedFirstFrame() {
        Log.e(TAG, "onRenderedFirstFrame");
    }

    @Override
    public void pause() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public boolean isPlaying() {
        return simpleExoPlayer.getPlayWhenReady();
    }

    @Override
    public void seekTo(long time) {
        if (simpleExoPlayer==null){
            return;
        }
        if (time != previousSeek) {
            if (time >= simpleExoPlayer.getBufferedPosition()) {
                if (jzvd instanceof AGVideo) {
                    ((AGVideo) jzvd).showProgress();
                }
            }
            simpleExoPlayer.seekTo(time);
            previousSeek = time;
            jzvd.seekToInAdvance = time;

        }
    }

    @Override
    public void release() {
        if (mMediaHandler != null && mMediaHandlerThread != null && simpleExoPlayer != null) {//不知道有没有妖孽
            HandlerThread tmpHandlerThread = mMediaHandlerThread;
            SimpleExoPlayer tmpMediaPlayer = simpleExoPlayer;
            JZMediaInterface.SAVED_SURFACE = null;

            mMediaHandler.post(() -> {
                tmpMediaPlayer.release();//release就不能放到主线程里，界面会卡顿
                tmpHandlerThread.quit();
            });
            simpleExoPlayer = null;
        }
    }

    @Override
    public long getCurrentPosition() {
        if (simpleExoPlayer != null)
            return simpleExoPlayer.getCurrentPosition();
        else return 0;
    }

    @Override
    public long getDuration() {
        if (simpleExoPlayer != null)
            return simpleExoPlayer.getDuration();
        else return 0;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        simpleExoPlayer.setVolume(leftVolume);
        simpleExoPlayer.setVolume(rightVolume);
    }

    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
        simpleExoPlayer.setPlaybackParameters(playbackParameters);
    }

    @Override
    public void onTimelineChanged(final Timeline timeline, Object manifest, final int reason) {
        Log.e(TAG, "onTimelineChanged");
//        JZMediaPlayer.instance().mainThreadHandler.post(() -> {
//                if (reason == 0) {
//
//                    JzvdMgr.getCurrentJzvd().onInfo(reason, timeline.getPeriodCount());
//                }
//        });
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        Log.e(TAG, "onPlayerStateChanged" + playbackState + "/ready=" + String.valueOf(playWhenReady));
        handler.post(() -> {
            switch (playbackState) {
                case Player.STATE_IDLE: {
                }
                break;
                case Player.STATE_BUFFERING: {
                    if (jzvd instanceof AGVideo) {
                        ((AGVideo) jzvd).showProgress();
                    }
                    handler.post(callback);
                }
                break;
                case Player.STATE_READY: {
                    if (playWhenReady) {
                        jzvd.state = Jzvd.STATE_PREPARED;
                        jzvd.onStatePlaying();
                    } else {
                    }
                }
                break;
                case Player.STATE_ENDED: {
                    jzvd.onCompletion();
                }
                break;
            }
        });
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, "onPlayerError" + error.toString());
        handler.post(() -> jzvd.onError(1000, 1000));
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
        handler.post(() -> jzvd.onSeekComplete());
    }

    @Override
    public void setSurface(Surface surface) {
        simpleExoPlayer.setVideoSurface(surface);
    }

    @Override
    public int getVideoWidth() {


// 获取视频轨道的格式
        Format videoFormat = simpleExoPlayer.getVideoFormat();

// 检查视频格式是否有效
        if (videoFormat != null) {
            // 获取视频的宽度和高度
            int videoWidth = videoFormat.width;
            int videoHeight = videoFormat.height;
            // 打印输出视频尺寸
            System.out.println("视频尺寸：" + videoWidth + " x " + videoHeight);
            return videoWidth;

        } else {
            System.out.println("没有找到视频轨道或视频格式信息。");
            return 1080;
        }


//        return simpleExoPlayer.videos;
    }

    @Override
    public int getVideoHeight() {


// 获取视频轨道的格式
        Format videoFormat = simpleExoPlayer.getVideoFormat();

// 检查视频格式是否有效
        if (videoFormat != null) {
            // 获取视频的宽度和高度
            int videoWidth = videoFormat.width;
            int videoHeight = videoFormat.height;

            // 打印输出视频尺寸
            System.out.println("视频尺寸：" + videoWidth + " x " + videoHeight);
            return videoHeight;
        } else {
            System.out.println("没有找到视频轨道或视频格式信息。");
            return 720;
        }


//        return 0;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            jzvd.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void changeVideo() {
        Context context =jzvd.getContext();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
        String currUrl = jzvd.jzDataSource.getCurrentUrl().toString();
        MediaSource videoSource;
        if (currUrl.contains(".m3u8")) {
            videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(currUrl), handler, null);
        } else {
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(currUrl));
        }
        if (simpleExoPlayer != null) {
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }

    }

    private class onBufferingUpdate implements Runnable {
        @Override
        public void run() {
            if (simpleExoPlayer != null) {
                final int percent = simpleExoPlayer.getBufferedPercentage();
                handler.post(() -> jzvd.setBufferProgress(percent));
                if (percent < 100) {
                    handler.postDelayed(callback, 300);
                } else {
                    handler.removeCallbacks(callback);
                }
            }
        }
    }
}
