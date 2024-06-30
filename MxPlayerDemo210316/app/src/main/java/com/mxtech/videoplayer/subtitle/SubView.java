package com.mxtech.videoplayer.subtitle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.SubtitleData;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mxtech.preference.P;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.preference.Key;

import java.util.ArrayList;
import java.util.Iterator;

public class SubView extends ViewSwitcher implements Handler.Callback{
    private Animation _animFadeOut;
    private Animation _animSlideInLeft;
    private Animation _animSlideInRight;
    private Animation _animSlideOutLeft;
    private Animation _animSlideOutRight;
    private final Handler _handler;
    private boolean _italicTag;
//    private IPlayer _player;
    private int _readFlags;
//    private Screen _screen;
    private int _slidingTargetPos;
    private double _speed;
    @Nullable
    private SubStationAlphaMedia _ssa;
    private boolean _subStationAlphaDirectRendering;
    @Nullable
//    private ISubtitleClient _subtitleClient;
//    private SubtitleOverlay _subtitleOverlay;
    private final ArrayList<SubtitleData> _subtitles;
    private int _sync;
    private int _updateLockCount;




    public SubView(Context context) {
        super(context);
        this._subtitles = new ArrayList<>();
        this._handler = new Handler(Looper.getMainLooper(), this);
        this._speed = 1.0d;
        this._slidingTargetPos = -1;
        this._readFlags = 256;
        this._subStationAlphaDirectRendering = true;
    }

    public SubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._subtitles = new ArrayList<>();
        this._handler = new Handler(Looper.getMainLooper(), this);
        this._speed = 1.0d;
        this._slidingTargetPos = -1;
        this._readFlags = 256;
        this._subStationAlphaDirectRendering = true;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }









    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
//        if (P.subtitleBkColorEnabled) {
//            setBackgroundColor(P.subtitleBkColor);
//        }
//        setEnableFadeOut(P.getSubtitleFadeout());
        addView(makeView(), new FrameLayout.LayoutParams(-1, -2));
        addView(makeView(), new FrameLayout.LayoutParams(-1, -2));
//        setTextColor(P.subtitleTextColor);
//        setTextSize(P.getSubtitleTextSizeSp());
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
//        for (int i = getChildCount() - 1; i >= 0; i--) {
//            StrokeView v = (StrokeView) getChildAt(i);
//            updateRubySpans(v, v.getText(), v.getPaddingLeft(), v.getPaddingRight(), left, right);
//        }
        super.setPadding(left, top, right, bottom);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
//        setSubtitlePadding(DeviceUtils.DIPToPixel(P.subtitleBottomPaddingDp));
//        updateRubySpans();
    }




    private View makeView() {
        Context context = getContext();
//        SubText v = new SubText(context);
//        float borderThickness = App.prefs.getFloat(Key.SUBTITLE_BORDER_THICKNESS, 0.08f);
//        v.enableStroke(P.getSubtitleBorderEnabled());
//        v.setBorderColor(P.subtitleBorderColor);
//        v.setBorderThickness(borderThickness, borderThickness);
//        v.setGravity(P.subtitleAlignment | 80);
//        v.setMinLines((11 > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT >= 14) ? 2 : 3);
//        v.setTypeface(P.getSubtitleFont());
//        v.setBold((P.subtitleTypefaceStyle & 1) != 0);
//        v.enableShadow(P.getSubtitleShadownEnabled());
//        int padding = (int) DeviceUtils.DIPToPixel(36.0f);
//        int i = (P.subtitleAlignment & 7) == 3 ? 0 : padding;
//        if ((P.subtitleAlignment & 7) == 5) {
//            padding = 0;
//        }

        TextView v = new TextView(context);
//        v.setPadding(i, 0, padding, 0);
        return v;
    }



    public void enableItalicTag(boolean enable) {
        this._italicTag = enable;
        refresh();
    }

    public void refresh() {
        if (this._updateLockCount <= 0) {
            updateVisibility();
            if (!this._handler.hasMessages(1)) {
                this._handler.sendEmptyMessage(1);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void updateVisibility() {
        boolean hasEnabled = false;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubtitleData e = it.next();
//            if (e.enabled) {
//                hasEnabled = true;
//                break;
//            }
        }
        setVisibility(hasEnabled ? 0 : 8);
    }



}
