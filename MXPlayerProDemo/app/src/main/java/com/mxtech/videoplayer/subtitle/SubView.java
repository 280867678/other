package com.mxtech.videoplayer.subtitle;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.mxtech.DeviceUtils;
import com.mxtech.StringUtils;
import com.mxtech.subtitle.Frame;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.ISubtitleClient;
import com.mxtech.subtitle.PolishStylizer;
import com.mxtech.subtitle.SubStationAlphaMedia;
import com.mxtech.text.RubySpan;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.IPlayer;
import com.mxtech.videoplayer.Player;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.SubtitleOverlay;
import com.mxtech.widget.StrokeView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class SubView extends ViewSwitcher implements Handler.Callback, Animation.AnimationListener, SubtitleOverlay.Listener {
    private static final int FADE_OUT_PLAY_THRESHOLD = 1000;
    private static final int HORIZ_PADDING = 36;
    private static final int MSG_REFRESH = 1;
    private static final int MSG_SLIDE_END = 2;
    public static final double SUBTITLE_DRAG_GAP = 0.0d;
    public static float SUBTITLE_DRAG_GAP_px = 0.0f;
    public static final double SUBTITLE_SCROLL_MINIMUM_MOVE = 0.006d;
    public static float SUBTITLE_SCROLL_MINIMUM_MOVE_px;
    public static final String TAG = App.TAG + ".SubView";
    private Animation _animFadeOut;
    private Animation _animSlideInLeft;
    private Animation _animSlideInRight;
    private Animation _animSlideOutLeft;
    private Animation _animSlideOutRight;
    private Client _client;
    private final Handler _handler;
    private boolean _italicTag;
    private IPlayer _player;
    private int _readFlags;
    private int _slidingTargetPos;
    private double _speed;
    @Nullable
    private SubStationAlphaMedia _ssa;
    private boolean _subStationAlphaDirectRendering;
    @Nullable
    private ISubtitleClient _subtitleClient;
    private SubtitleOverlay _subtitleOverlay;
    private final ArrayList<SubtitleData> _subtitles;
    private int _sync;
    private int _updateLockCount;

    /* loaded from: classes.dex */
    public interface Client {
        boolean attachSubtitleToMediaPlayer(ISubtitle iSubtitle);

        SubtitleOverlay createSubtitleOverlay(SubView subView);

        void detachSubtitleFromMediaPlayer(ISubtitle iSubtitle);

        int getIntrinsicOffset();

        void onSubtitleVisibilityChanged(SubView subView, ISubtitle iSubtitle);

        void removeSubtitleOverlay(SubView subView, SubtitleOverlay subtitleOverlay);
    }

    /* loaded from: classes2.dex */
    public static final class SubtitleData {
        public boolean enabled;
        public int flags;
        public boolean needRendering;
        public final ISubtitle subtitle;

        public SubtitleData(ISubtitle subtitle) {
            this.subtitle = subtitle;
            this.flags = subtitle.flags();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        SUBTITLE_SCROLL_MINIMUM_MOVE_px = (float) DeviceUtils.MeterToPixel(0.006d);
        SUBTITLE_DRAG_GAP_px = (float) DeviceUtils.MeterToPixel(SUBTITLE_DRAG_GAP);
        if (P.subtitleBkColorEnabled) {
            setBackgroundColor(P.subtitleBkColor);
        }
        setEnableFadeOut(P.getSubtitleFadeout());
        addView(makeView(), new FrameLayout.LayoutParams(-1, -2));
        addView(makeView(), new FrameLayout.LayoutParams(-1, -2));
        setTextColor(P.subtitleTextColor);
        setTextSize(P.getSubtitleTextSize());
    }

    public void setEnableFadeOut(boolean enable) {
        if (enable) {
            this._animFadeOut = AnimationUtils.loadAnimation(getContext(), 17432577);
            this._animFadeOut.setAnimationListener(this);
        } else {
            this._animFadeOut = null;
        }
        setOutAnimation(this._animFadeOut);
    }

    public boolean isPositionOnText(float x, float y, float tolerance) {
        return ((StrokeView) getCurrentView()).isPositionOnText(getPaddingTop() + x, getPaddingLeft() + y, tolerance);
    }

    public boolean isPositionOnTextBound(float x, float y, float tolerance) {
        return ((StrokeView) getCurrentView()).isPositionOnTextBound(getPaddingTop() + x, getPaddingLeft() + y, tolerance);
    }

    public void setText(CharSequence text, TextView.BufferType bufferType) {
        StrokeView current = (StrokeView) getCurrentView();
        if (getVisibility() == 0 && current.getText().length() > 0 && (text == null || text.length() == 0)) {
            StrokeView next = (StrokeView) getNextView();
            setStrokeViewText(next, text, bufferType);
            showNext();
            return;
        }
        setStrokeViewText(current, text, bufferType);
    }

    public float getTextSize() {
        return ((StrokeView) getCurrentView()).getTextSize();
    }

    /* loaded from: classes2.dex */
    private static class TextBackColorSpan extends BackgroundColorSpan {
        TextBackColorSpan(int color) {
            super(color);
        }
    }

    public void setTextBackColor(int color) {
        TextBackColorSpan[] textBackColorSpanArr;
        TextBackColorSpan[] textBackColorSpanArr2;
        CharSequence text = ((StrokeView) getCurrentView()).getText();
        if (((-16777216) & color) == 0) {
            if (text instanceof Spannable) {
                SpannableString s = new SpannableString(text);
                for (TextBackColorSpan span : (TextBackColorSpan[]) s.getSpans(0, s.length(), TextBackColorSpan.class)) {
                    s.removeSpan(span);
                }
                setText(s, TextView.BufferType.SPANNABLE);
                return;
            }
            return;
        }
        SpannableString s2 = new SpannableString(text);
        int len = s2.length();
        for (TextBackColorSpan span2 : (TextBackColorSpan[]) s2.getSpans(0, len, TextBackColorSpan.class)) {
            s2.removeSpan(span2);
        }
        s2.setSpan(new TextBackColorSpan(color), 0, len, 18);
        setText(s2, TextView.BufferType.SPANNABLE);
    }

    public void setTextSize(float size) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).setTextSize(2, size);
        }
    }

    public final void setTypeface(Typeface typeface, int style) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            StrokeView v = (StrokeView) getChildAt(i);
            v.setTypeface(typeface);
            v.setBold((style & 1) != 0);
        }
    }

    public final void setTextColor(int color) {
        ColorStateList colors = ColorStateList.valueOf(color);
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).setTextColor(colors);
        }
    }

    public final void setTextColor(ColorStateList colors) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).setTextColor(colors);
        }
    }

    public final void enableShadow(boolean enable) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).enableShadow(enable);
        }
    }

    public final void enableBorder(boolean enable) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).enableStroke(enable);
        }
    }

    public final void setBorderColor(int color) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).setBorderColor(color);
        }
    }

    public final void setBorderThickness(float normalThickness, float boldThickness) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((StrokeView) getChildAt(i)).setBorderThickness(normalThickness, boldThickness);
        }
    }

    public final void setGravity(int gravity) {
        int padding = (int) DeviceUtils.DIPToPixel(36.0f);
        for (int i = getChildCount() - 1; i >= 0; i--) {
            StrokeView v = (StrokeView) getChildAt(i);
            v.setGravity(gravity);
            int leftPadding = (gravity & 7) == 3 ? 0 : padding;
            int rightPadding = (gravity & 7) == 5 ? 0 : padding;
            updateRubySpans(v, v.getText(), leftPadding, rightPadding, getPaddingLeft(), getPaddingRight());
            v.setPadding(leftPadding, 0, rightPadding, 0);
        }
    }

    public void setSubtitlePadding(float bottom) {
        if (this._player.getOrientation() == 1) {
            bottom = (DeviceUtils.largestPixels * bottom) / DeviceUtils.smallestPixels;
        }
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), Math.round(bottom));
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            StrokeView v = (StrokeView) getChildAt(i);
            updateRubySpans(v, v.getText(), v.getPaddingLeft(), v.getPaddingRight(), left, right);
        }
        super.setPadding(left, top, right, bottom);
    }

    private View makeView() {
        Context context = getContext();
        SubText v = new SubText(context);
        float borderThickness = App.prefs.getFloat(Key.SUBTITLE_BORDER_THICKNESS, 0.08f);
        v.enableStroke(P.getSubtitleBorderEnabled());
        v.setBorderColor(P.subtitleBorderColor);
        v.setBorderThickness(borderThickness, borderThickness);
        v.setGravity(P.subtitleAlignment | 80);
        v.setMinLines((11 > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT >= 14) ? 2 : 3);
        v.setTypeface(P.getSubtitleFont());
        v.setBold((P.subtitleTypefaceStyle & 1) != 0);
        v.enableShadow(P.getSubtitleShadownEnabled());
        int padding = (int) DeviceUtils.DIPToPixel(36.0f);
        int i = (P.subtitleAlignment & 7) == 3 ? 0 : padding;
        if ((P.subtitleAlignment & 7) == 5) {
            padding = 0;
        }
        v.setPadding(i, 0, padding, 0);
        return v;
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationRepeat(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationStart(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationEnd(Animation animation) {
        setStrokeViewText((StrokeView) getNextView(), "", TextView.BufferType.NORMAL);
        onFrameAnimationEnd(null);
    }

    @Override // com.mxtech.videoplayer.subtitle.SubtitleOverlay.Listener
    public void onFrameAnimationEnd(SubtitleOverlay overlay) {
        if (this._slidingTargetPos >= 0 && !this._handler.hasMessages(2)) {
            this._handler.sendEmptyMessage(2);
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.SubtitleOverlay.Listener
    public void onSizeChanged(SubtitleOverlay overlay, int width, int height) {
        this._player.setSubtitleCanvasSize(width, height);
        refresh();
    }

    private void setStrokeViewText(StrokeView v, CharSequence text, TextView.BufferType type) {
        updateRubySpans(v, text);
        v.setText(text, type);
    }

    private void updateRubySpans(StrokeView v, CharSequence text, int viewPaddingLeft, int viewPaddingRight, int layoutPaddingLeft, int layoutPaddingRight) {
        RubySpan[] rubySpanArr;
        if (text instanceof Spanned) {
            int width = Integer.MIN_VALUE;
            for (RubySpan s : (RubySpan[]) ((Spanned) text).getSpans(0, text.length(), RubySpan.class)) {
                if (width == Integer.MIN_VALUE) {
                    width = ((((this._player.getDisplay().getWidth() / 2) - viewPaddingLeft) - viewPaddingRight) - layoutPaddingLeft) - layoutPaddingRight;
                }
                s.setMaxWidth(width);
            }
        }
    }

    private void updateRubySpans(StrokeView v, CharSequence text) {
        updateRubySpans(v, text, v.getPaddingLeft(), v.getPaddingRight(), getPaddingLeft(), getPaddingRight());
    }

    private void updateRubySpans() {
        if (getChildCount() == 2) {
            StrokeView v = (StrokeView) getChildAt(0);
            updateRubySpans(v, v.getText());
            StrokeView v2 = (StrokeView) getChildAt(1);
            updateRubySpans(v2, v2.getText());
        }
    }

    public void onOrientationChanged(int orientation) {
        setSubtitlePadding(DeviceUtils.DIPToPixel(P.subtitleBottomPaddingDp));
        updateRubySpans();
    }

    private void onSlidingEnd() {
        if (this._slidingTargetPos >= 0) {
            this._player.seekTo(subtitleTimeToMediaTime(this._slidingTargetPos), 6000);
            this._player.resume();
            this._slidingTargetPos = -1;
            unlockUpdate();
        }
    }

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

    public void setPlayer(IPlayer player) {
        this._player = player;
        setSubtitlePadding(DeviceUtils.DIPToPixel(P.subtitleBottomPaddingDp));
    }

    public void setClient(Client client) {
        this._client = client;
    }

    public void addSubtitle(ISubtitle subtitle, boolean enable) {
        SubtitleData data = new SubtitleData(subtitle);
        this._subtitles.add(data);
        if (enable) {
            doEnableSubtitle(data, true);
        }
        subtitle.setTranslation(this._sync, this._speed);
        updateSubtitleOverlay();
        refresh();
    }

    public void removeSubtile(ISubtitle subtitle) {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.subtitle == subtitle) {
                if (data.enabled) {
                    subtitle.enable(false);
                }
                it.remove();
                updateSubtitleOverlay();
                refresh();
                return;
            }
        }
    }

    public void clear() {
        this._sync = 0;
        this._speed = 1.0d;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData sub = it.next();
            if (sub.enabled) {
                sub.subtitle.enable(false);
            }
        }
        this._subtitles.clear();
        resetUpdateLock();
        setText("", TextView.BufferType.NORMAL);
        setVisibility(8);
        if (this._subtitleOverlay != null) {
            removeSubtitleOverlay();
        }
    }

    private void createSubtitleOverlay() {
        this._subtitleOverlay = this._client.createSubtitleOverlay(this);
        this._subtitleOverlay.setListener(this);
    }

    private void removeSubtitleOverlay() {
        this._subtitleOverlay.clearFrames();
        this._client.removeSubtitleOverlay(this, this._subtitleOverlay);
        this._subtitleOverlay.setListener(null);
        this._subtitleOverlay = null;
    }

    private void updateSubtitleOverlay() {
        boolean needOverlay = false;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData d = it.next();
            if (d.enabled && (d.flags & ISubtitle.CONTENT_FRAME) != 0) {
                needOverlay = true;
            }
        }
        if (needOverlay) {
            if (this._subtitleOverlay != null) {
                this._subtitleOverlay.clearFrames();
            } else {
                createSubtitleOverlay();
            }
            updateSubtitleOverlayComplexity();
        } else if (this._subtitleOverlay != null) {
            removeSubtitleOverlay();
        }
    }

    private void updateSubtitleOverlayComplexity() {
        boolean complex = false;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubtitleData data = it.next();
            ISubtitle sub = data.subtitle;
            if ((data.flags & ISubtitle.CONTENT_FRAME) != 0 && sub.isRenderingComplex()) {
                complex = true;
                break;
            }
        }
        this._subtitleOverlay.setRenderingComplex(complex);
    }

    public boolean containsSubtitle(Class<? extends ISubtitle> type) {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (type.isInstance(data.subtitle)) {
                return true;
            }
        }
        return false;
    }

    public int getSubtitleCount() {
        return this._subtitles.size();
    }

    public int getEnabledSubtitleCount() {
        int count = 0;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData e = it.next();
            if (e.enabled) {
                count++;
            }
        }
        return count;
    }

    public ISubtitle findSubtitle(Uri uri) {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (uri.equals(data.subtitle.uri())) {
                return data.subtitle;
            }
        }
        return null;
    }

    public ISubtitle[] getAllSubtitles() {
        int numSubs = this._subtitles.size();
        ISubtitle[] subs = new ISubtitle[numSubs];
        for (int i = 0; i < numSubs; i++) {
            subs[i] = this._subtitles.get(i).subtitle;
        }
        return subs;
    }

    public ISubtitle getSubtitle(int index) {
        return this._subtitles.get(index).subtitle;
    }

    public int[] getEnabledSubtitles() {
        int enabledCount = getEnabledSubtitleCount();
        int[] enabled = new int[enabledCount];
        if (enabledCount > 0) {
            int at = 0;
            for (int i = 0; i < this._subtitles.size(); i++) {
                if (this._subtitles.get(i).enabled) {
                    enabled[at] = i;
                    at++;
                }
            }
        }
        return enabled;
    }

    public boolean isSubtitleEnabled(int index) {
        return this._subtitles.get(index).enabled;
    }

    public boolean containsEnabledSubtitle() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData e = it.next();
            if (e.enabled) {
                return true;
            }
        }
        return false;
    }

    public void enableSubtitle(ISubtitle subtitle, boolean enable) {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.subtitle == subtitle) {
                enableSubtitle(data, enable);
                return;
            }
        }
    }

    public void enableSubtitle(int index, boolean enable) throws IllegalArgumentException {
        if (index < 0 || index >= this._subtitles.size()) {
            throw new IllegalArgumentException();
        }
        enableSubtitle(this._subtitles.get(index), enable);
    }

    private void enableSubtitle(SubtitleData data, boolean enable) {
        if (data.enabled != enable) {
            doEnableSubtitle(data, enable);
            updateSubtitleOverlay();
            refresh();
            this._client.onSubtitleVisibilityChanged(this, data.subtitle);
        }
    }

    private void doEnableSubtitle(SubtitleData data, boolean enable) {
        data.enabled = enable;
        if (!doUpdateDirectRendering(data)) {
            data.needRendering = enable;
        }
        data.subtitle.enable(enable);
    }

    public void lockUpdate() {
        this._updateLockCount++;
    }

    public void unlockUpdate() {
        int i = this._updateLockCount - 1;
        this._updateLockCount = i;
        if (i < 0) {
            this._updateLockCount = 0;
        }
    }

    public void resetUpdateLock() {
        this._updateLockCount = 0;
    }

    public boolean hasSubtitles() {
        return this._subtitles.size() > 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x000c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean hasFileSubtitles() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            Uri uri = data.subtitle.uri();
            String scheme = uri.getScheme();
            if (scheme == null || scheme.equals(MediaListFragment.TYPE_FILE)) {
                return true;
            }
            while (it.hasNext()) {
            }
        }
        return false;
    }

    public boolean hasVisibleTextSubtitles() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.enabled && (data.flags & 2097152) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleSSASubtitles() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.enabled && (data.flags & 1048576) != 0) {
                return true;
            }
        }
        return false;
    }

    public ISubtitle getFirstVisibleSubtitle() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.enabled) {
                return data.subtitle;
            }
        }
        return null;
    }

    public void setSSARenderingMode(boolean directRendering, ISubtitleClient subtitleClient) {
        if (this._subStationAlphaDirectRendering != directRendering || this._subtitleClient != subtitleClient) {
            this._subStationAlphaDirectRendering = directRendering;
            this._subtitleClient = subtitleClient;
            Iterator<SubtitleData> it = this._subtitles.iterator();
            while (it.hasNext()) {
                SubtitleData data = it.next();
                doUpdateDirectRendering(data);
            }
            updateSubtitleOverlay();
            refresh();
        }
    }

    private boolean doUpdateDirectRendering(SubtitleData data) {
        if ((data.flags & 1048576) != 0) {
            if (this._subtitleClient != null) {
                doUpdateSSAMedia();
            }
            if (this._subStationAlphaDirectRendering) {
                data.flags &= -4194305;
                data.needRendering = false;
                if (data.enabled) {
                    if (this._client.attachSubtitleToMediaPlayer(data.subtitle)) {
                        return true;
                    }
                    data.flags |= ISubtitle.CONTENT_FRAME;
                    data.needRendering = true;
                    return true;
                }
                this._client.detachSubtitleFromMediaPlayer(data.subtitle);
                return true;
            }
            data.flags |= ISubtitle.CONTENT_FRAME;
            if (data.enabled) {
                data.needRendering = true;
            }
            this._client.detachSubtitleFromMediaPlayer(data.subtitle);
            return true;
        }
        return false;
    }

    private void doUpdateSSAMedia() {
        this._ssa = this._subtitleClient.getSubStationAlphaMedia(1, null);
        if (this._ssa != null) {
            this._ssa.setDirectRendering(this._subStationAlphaDirectRendering);
        }
    }

    private void dispatchTranslation() {
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData sub = it.next();
            sub.subtitle.setTranslation(this._sync, this._speed);
        }
    }

    public void setSync(int sync) {
        if (sync != this._sync) {
            this._sync = sync;
            dispatchTranslation();
            update(this._player.currentPosition());
        }
    }

    public int getSync() {
        return this._sync;
    }

    public void setSpeed(double speed) {
        if (speed < 0.001d) {
            speed = 0.001d;
        }
        if (speed != this._speed) {
            this._speed = speed;
            dispatchTranslation();
            update(this._player.currentPosition());
        }
    }

    public double getSpeed() {
        return this._speed;
    }

    public void setTranslation(int sync, double speed) {
        if (speed < 0.001d) {
            speed = 0.001d;
        }
        if (sync != this._sync || speed != this._speed) {
            this._sync = sync;
            this._speed = speed;
            dispatchTranslation();
            update(this._player.currentPosition());
        }
    }

    public void update(int pos) {
        update(pos, false, true);
    }

    public void update(int pos, boolean forceUpdate, boolean animateHide) {
        if (this._updateLockCount == 0) {
            update_l(mediaTimeToSubtitleTime(pos), 0, forceUpdate, animateHide);
        }
    }

    public int mediaTimeToSubtitleTime(int time) {
        return (int) ((time - this._sync) * this._speed);
    }

    public int subtitleTimeToMediaTime(int time) {
        return (int) ((time / this._speed) + this._sync);
    }

    public void enableItalicTag(boolean enable) {
        this._italicTag = enable;
        refresh();
    }

    private SpannableStringBuilder appendTextFrame(SpannableStringBuilder b, SubtitleData data, CharSequence frame) {
        if (this._italicTag && (data.flags & 1) == 0) {
            frame = PolishStylizer.stylize(SpannableStringBuilder.valueOf(frame));
        }
        if (frame.length() > 0) {
            if (b == null) {
                b = new SpannableStringBuilder();
            }
            int end = b.length();
            if (end > 0) {
                StringUtils.closeSpans(b);
                b.append('\n');
            }
            b.append(frame);
        }
        return b;
    }

    private boolean setText(CharSequence text, TextView.BufferType type, int animation) {
        StrokeView current = (StrokeView) getCurrentView();
        if ((text == null || text.length() == 0) && current.getText().length() == 0) {
            return false;
        }
        switch (animation) {
            case 0:
                setStrokeViewText(current, text, type);
                onAnimationEnd(null);
                break;
            case 1:
                setStrokeViewText((StrokeView) getNextView(), text, type);
                showNext();
                break;
            case 2:
                setStrokeViewText((StrokeView) getNextView(), text, type);
                if (this._animSlideInRight == null) {
                    this._animSlideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                    this._animSlideInRight.setAnimationListener(this);
                }
                if (this._animSlideOutLeft == null) {
                    this._animSlideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
                }
                setInAnimation(this._animSlideInRight);
                setOutAnimation(this._animSlideOutLeft);
                showNext();
                setInAnimation(null);
                setOutAnimation(this._animFadeOut);
                break;
            case 3:
                setStrokeViewText((StrokeView) getNextView(), text, type);
                if (this._animSlideInLeft == null) {
                    this._animSlideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                    this._animSlideInLeft.setAnimationListener(this);
                }
                if (this._animSlideOutRight == null) {
                    this._animSlideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
                }
                setInAnimation(this._animSlideInLeft);
                setOutAnimation(this._animSlideOutRight);
                showNext();
                setInAnimation(null);
                setOutAnimation(this._animFadeOut);
                break;
            default:
                Log.e(TAG, "Unknown animation code " + animation);
                return false;
        }
        return true;
    }

    @Override // android.widget.ViewAnimator
    public void showNext() {
        super.showNext();
        if (getOutAnimation() == null) {
            onAnimationEnd(null);
        }
    }

    private boolean update_l(int pos, int direction, boolean forceUpdate, boolean animateHide) {
        boolean changed;
        boolean textEmpty;
        boolean changed2;
        int anim;
        int anim2;
        Object content;
        Object[] objArr;
        int pos2 = pos + this._client.getIntrinsicOffset();
        if (forceUpdate) {
            changed = true;
            Iterator<SubtitleData> it = this._subtitles.iterator();
            while (it.hasNext()) {
                SubtitleData data = it.next();
                if (data.needRendering) {
                    data.subtitle.update(pos2);
                }
            }
        } else {
            changed = false;
            Iterator<SubtitleData> it2 = this._subtitles.iterator();
            while (it2.hasNext()) {
                SubtitleData data2 = it2.next();
                if (data2.needRendering) {
                    changed = data2.subtitle.update(pos2) || changed;
                }
            }
        }
        if (changed) {
            this._handler.removeMessages(1);
            List<Frame> frames = null;
            SpannableStringBuilder ssb = null;
            Iterator<SubtitleData> it3 = this._subtitles.iterator();
            while (it3.hasNext()) {
                SubtitleData data3 = it3.next();
                if (data3.needRendering && (content = data3.subtitle.content(this._readFlags)) != null) {
                    if (content instanceof CharSequence) {
                        ssb = appendTextFrame(ssb, data3, (CharSequence) content);
                    } else if (content instanceof Frame) {
                        if (frames == null) {
                            frames = new ArrayList<>();
                        }
                        frames.add((Frame) content);
                    } else {
                        for (Object element : (Object[]) content) {
                            if (element instanceof CharSequence) {
                                ssb = appendTextFrame(ssb, data3, (CharSequence) element);
                            } else {
                                if (frames == null) {
                                    frames = new ArrayList<>();
                                }
                                frames.add((Frame) element);
                            }
                        }
                    }
                }
            }
            if (ssb != null) {
                int len = ssb.length();
                if (len > 0) {
                    textEmpty = false;
                    if (P.subtitle_force_ltr) {
                        ssb.insert(0, "\u200e", 0, 1);
                        int len2 = len + 1;
                        int i = 1;
                        while (i < len2) {
                            if (ssb.charAt(i) == '\n') {
                                i++;
                                ssb.insert(i, "\u200e", 0, 1);
                                len2++;
                            }
                            i++;
                        }
                    }
                } else {
                    textEmpty = true;
                }
            } else {
                textEmpty = true;
            }
            boolean changed3 = false;
            if (direction == 0) {
                if (this._subtitleOverlay != null) {
                    if (frames == null && animateHide && pos2 + 1000 < getNextPosition()) {
                        anim2 = 1;
                    } else {
                        anim2 = 0;
                    }
                    changed3 = this._subtitleOverlay.setFrames(frames, anim2) || 0 != 0;
                }
                if (textEmpty) {
                    if (animateHide && pos2 + 1000 < getNextPosition()) {
                        anim = 1;
                    } else {
                        anim = 0;
                    }
                    changed2 = setText(ssb, TextView.BufferType.NORMAL, anim) || changed3;
                } else {
                    changed2 = setText(ssb, TextView.BufferType.SPANNABLE, 0) || changed3;
                }
            } else {
                int anim3 = direction < 0 ? 3 : 2;
                if (this._subtitleOverlay != null) {
                    changed3 = this._subtitleOverlay.setFrames(frames, anim3) || 0 != 0;
                }
                changed2 = setText(ssb, textEmpty ? TextView.BufferType.NORMAL : TextView.BufferType.SPANNABLE, anim3) || changed3;
            }
            return changed2;
        }
        return false;
    }

    private int getNextPosition() {
        int next;
        int nearst = Integer.MAX_VALUE;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (it.hasNext()) {
            SubtitleData data = it.next();
            if (data.enabled && (next = data.subtitle.next()) < nearst) {
                nearst = next;
            }
        }
        return nearst;
    }

    public void seekAdjascent(int direction) {
        int target;
        int next;
        int prev;
        if (direction != 0 && this._player.isSeekable()) {
            if (direction < 0) {
                target = Integer.MIN_VALUE;
                Iterator<SubtitleData> it = this._subtitles.iterator();
                while (it.hasNext()) {
                    SubtitleData data = it.next();
                    if (data.enabled && target < (prev = data.subtitle.previous())) {
                        target = prev;
                    }
                }
            } else {
                target = Integer.MAX_VALUE;
                Iterator<SubtitleData> it2 = this._subtitles.iterator();
                while (it2.hasNext()) {
                    SubtitleData data2 = it2.next();
                    if (data2.enabled && (next = data2.subtitle.next()) < target) {
                        target = next;
                    }
                }
            }
            if (target >= 0 && target != Integer.MAX_VALUE) {
                if (update_l(target, direction, false, false)) {
                    if (this._slidingTargetPos < 0) {
                        lockUpdate();
                    }
                    this._player.update(subtitleTimeToMediaTime(target));
                    this._player.pause(Player.FLAG_FLASH);
                    this._slidingTargetPos = target;
                    return;
                }
                this._player.seekTo(subtitleTimeToMediaTime(target), 6000);
            }
        }
    }

    public void updateVisibility() {
        boolean hasEnabled = false;
        Iterator<SubtitleData> it = this._subtitles.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubtitleData e = it.next();
            if (e.enabled) {
                hasEnabled = true;
                break;
            }
        }
        setVisibility(hasEnabled ? 0 : 8);
    }

    public void refresh() {
        if (this._updateLockCount <= 0) {
            updateVisibility();
            if (!this._handler.hasMessages(1)) {
                this._handler.sendEmptyMessage(1);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            update_l(mediaTimeToSubtitleTime(this._player.currentPosition()), 0, true, true);
            return true;
        } else if (msg.what == 2) {
            onSlidingEnd();
            return true;
        } else {
            return false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }
}
