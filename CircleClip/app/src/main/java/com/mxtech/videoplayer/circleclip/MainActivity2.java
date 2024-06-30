package com.mxtech.videoplayer.circleclip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        CircleClipTapView circleClipTapView = findViewById(R.id.circleClipTapView);
        circleClipTapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 在触摸事件中更新裁剪动画的位置和状态
                float x = event.getX();
                float y = event.getY();
                Log.e("X:"+x,"  Y:"+y);


                circleClipTapView.m10036c(x, y);
                return true;
            }
        });



    }
}