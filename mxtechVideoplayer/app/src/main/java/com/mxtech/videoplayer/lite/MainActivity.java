package com.mxtech.videoplayer.lite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mxtech.media.FFPlayer;
import com.mxtech.subtitle.ISubtitleClient;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.videoplayer.pro.R;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ISubtitleClient {

    private Uri _uri;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    private File _file;

//    file:///storage/emulated/0/sddman/dowload/file/气垫传奇/气垫传奇.1080p.BD中英双字[最新电影www.dygangs.me].mp4
//    file:///storage/emulated/0/sddman/dowload/file/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87.1080p.BD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97%5B%E6%9C%80%E6%96%B0%E7%94%B5%E5%BD%B1www.dygangs.me%5D.mp4
//    file:///storage/emulated/0/sddman/dowload/file/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87.1080p.BD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97%5B%E6%9C%80%E6%96%B0%E7%94%B5%E5%BD%B1www.dygangs.me%5D.mp4
//    file:///storage/emulated/0/sddman/dowload/file/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87.1080p.BD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97%5B%E6%9C%80%E6%96%B0%E7%94%B5%E5%BD%B1www.dygangs.me%5D.mp4
//    file:///storage/emulated/0/sddman/dowload/file/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87.1080p.BD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97%5B%E6%9C%80%E6%96%B0%E7%94%B5%E5%BD%B1www.dygangs.me%5D.mp4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        surfaceView = findViewById(R.id.sv);
        surfaceHolder = surfaceView.getHolder();



        Log.e("Uri::",Uri.parse("https://hnzy.bfvvs.com/play/rb2p5XJe/index.m3u8").toString());

        try {
            FFPlayer secondary2 = new FFPlayer(null, getAudioCreationFlag(), false, this);

//            this._file = fileFromUri(Uri.parse("https://hnzy.bfvvs.com/play/rb2p5XJe/index.m3u8"));
//            this._uri = Uri.fromFile(this._file);
            secondary2.setDataSource(getApplicationContext(), Uri.parse("file:///storage/emulated/0/sddman/dowload/file/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87/%E6%B0%94%E5%9E%AB%E4%BC%A0%E5%A5%87.1080p.BD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97%5B%E6%9C%80%E6%96%B0%E7%94%B5%E5%BD%B1www.dygangs.me%5D.mp4"), null);
Log.e("Uri::",Uri.parse("https://hnzy.bfvvs.com/play/rb2p5XJe/index.m3u8").toString());

            if (surfaceHolder != null) {
//                secondary2.setCoreLimit(P.getCoreLimit());
                secondary2.setDisplay(surfaceHolder, 4);  //(this._decoder == 4 ? 32 : 64) | videoFlags
                secondary2.setScreenOnWhilePlaying(true);
            }
            secondary2.setVolume(0.0f, 0.0f);
            secondary2.forceAudioTimeSync(true);



            secondary2.setAudioStreamType(3);
            secondary2.prepareAsync();
            secondary2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getAudioCreationFlag() {
        return 1024;
    }


    @Override
    public int frameTime() {
        return 0;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public SubStationAlphaMedia getSubStationAlphaMedia(int i, FFPlayer fFPlayer) {
        return null;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int mediaDuration() {
        return 0;
    }

    @Override
    public int mediaHeight() {
        return 0;
    }

    @Override
    public Uri mediaUri() {
        return null;
    }

    @Override
    public int mediaWidth() {
        return 0;
    }

    @Override
    public void onSubtitleInvalidated() {

    }

    @Override
    public void registerMediaListener(IMediaListener iMediaListener) {

    }

    @Override
    public void setupFonts(boolean z) {

    }

    @Override
    public void unregisterMediaListener(IMediaListener iMediaListener) {

    }




































    public static File fileFromUri(Uri uri) {
        if (isFileUri(uri)) {
            return new File(uri.getPath());
        }
//        if (isMediaStoreVideoURI(uri)) {
//            try {
//                Cursor cursor = MediaStore.Video.query(App.cr, uri, new String[]{"_data"});
//                if (cursor != null) {
//                    if (cursor.moveToFirst() && !cursor.isNull(0)) {
//                        File file = new File(cursor.getString(0));
//                        cursor.close();
//                        return file;
//                    }
//                    cursor.close();
//                }
//            } catch (Exception e) {
//                Log.e("MX/J", "", e);
//            }
//        }
        return null;
    }

    public static boolean isFileUri(Uri uri) {
        String scheme = uri.getScheme();
        return scheme == null || "file".equals(scheme);
    }


}