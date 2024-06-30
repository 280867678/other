package com.mxtech.videoplayer.subtitle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.mxtech.app.DialogRegistry;
import com.mxtech.subtitle.ISubtitle;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import java.io.File;

/* loaded from: classes.dex */
public final class SubtitlePanel implements CompoundButton.OnCheckedChangeListener, Runnable, View.OnClickListener {
    private static final int CHECKBOX_ID_BASE = 9119;
    private Bar _bar;
    private final Client _client;
    private final DialogRegistry _dialogRegistry;
    private ViewGroup _layout;
    private final LayoutInflater _layoutInflater;
    private ImageButton _loadButton;
    private final ViewGroup _parent;
    private ViewGroup _subtitleContainer;
    private final Uri _uri;
    private final SubView theView;

    /* loaded from: classes.dex */
    public interface Client {
        boolean getFullScreen();

        File getLastSubtitleDir();

        void onClose();

        void onLoadSubtitle(File file, boolean z);

        void updateLayout();
    }

    public SubtitlePanel(ViewGroup parent, SubView subtitleView, DialogRegistry dialogRegistry, LayoutInflater layoutInflater, Client client, Uri uri) {
        this._parent = parent;
        this.theView = subtitleView;
        this._dialogRegistry = dialogRegistry;
        this._layoutInflater = layoutInflater;
        this._client = client;
        this._uri = uri;
    }

    public int getBarHeight() {
        if (this._bar != null) {
            return this._bar.getHeight();
        }
        return 0;
    }

    public void enableLoad(boolean enable) {
        if (this._layout != null) {
            this._loadButton.setEnabled(enable);
        }
    }

    public void show() {
        if (this._layout == null) {
            this._layout = (ViewGroup) this._layoutInflater.inflate(R.layout.subtitle_panel, this._parent, false);
            this._bar = (Bar) this._layout.findViewById(R.id.bar);
            this._loadButton = (ImageButton) this._layout.findViewById(R.id.loadSubtitle);
            this._subtitleContainer = (ViewGroup) this._layout.findViewById(R.id.subtitleContainer);
            ImageButton closeButton = (ImageButton) this._layout.findViewById(16908327);
            closeButton.setOnClickListener(this);
            TypedArray a = this.theView.getContext().obtainStyledAttributes(new int[]{R.attr.colorControlNormal});
            try {
                int colorAccent = a.getColor(0, 0);
                if (colorAccent != 0) {
                    ColorFilter colorFilter = new PorterDuffColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);
                    this._loadButton.setColorFilter(colorFilter);
                    closeButton.setColorFilter(colorFilter);
                }
                a.recycle();
                this._bar.link(this);
                this._loadButton.setOnClickListener(new View.OnClickListener() { // from class: com.mxtech.videoplayer.subtitle.SubtitlePanel.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        new SubtitleOpener((Activity) SubtitlePanel.this.theView.getContext(), SubtitlePanel.this._dialogRegistry, SubtitlePanel.this._uri, SubtitlePanel.this.theView.getSubtitleCount() > 0, SubtitlePanel.this._client);
                    }
                });
                this._parent.addView(this._layout);
            } catch (Throwable th) {
                a.recycle();
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSizeChanged() {
        this.theView.post(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        update();
        this._client.updateLayout();
    }

    public void hide() {
        if (this._layout != null) {
            ((ViewGroup) this._layout.getParent()).removeView(this._layout);
            _clearBoxes();
            this._bar.link(null);
            this._loadButton.setOnClickListener(null);
            this._layout = null;
            this._bar = null;
            this._loadButton = null;
            this._subtitleContainer = null;
        }
    }

    private void _clearBoxes() {
        for (int i = this._subtitleContainer.getChildCount() - 1; i >= 0; i--) {
            View v = this._subtitleContainer.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setOnCheckedChangeListener(null);
                this._subtitleContainer.removeViewAt(i);
            }
        }
    }

    public void update() {
        if (this._layout != null) {
            _clearBoxes();
            int subtitleCount = this.theView.getSubtitleCount();
            ISubtitle[] allSubs = this.theView.getAllSubtitles();
            int i = 0;
            int boxId = CHECKBOX_ID_BASE;
            while (i < subtitleCount) {
                ISubtitle subtitle = this.theView.getSubtitle(i);
                CheckBox box = (CheckBox) this._layoutInflater.inflate(R.layout.subtitle_check_button, this._subtitleContainer, false);
                box.setTag(subtitle);
                box.setOnCheckedChangeListener(this);
                box.setId(boxId);
                box.setText(SubtitleFactory.getDecorativeName(subtitle, allSubs));
                box.setChecked(this.theView.isSubtitleEnabled(i));
                if (!subtitle.isSupported()) {
                    box.setEnabled(false);
                }
                this._subtitleContainer.addView(box);
                i++;
                boxId++;
            }
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        try {
            this.theView.enableSubtitle((ISubtitle) checkBox.getTag(), isChecked);
            P.setAutoSelectSubtitle(this.theView.getEnabledSubtitleCount() > 0);
        } catch (IllegalStateException e) {
            Log.e(App.TAG, "", e);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        this._client.onClose();
    }

    /* loaded from: classes2.dex */
    public static final class Bar extends RelativeLayout {
        private SubtitlePanel panel;

        public Bar(Context context) {
            super(context);
        }

        public Bar(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void link(SubtitlePanel panel) {
            this.panel = panel;
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.panel != null) {
                this.panel.onSizeChanged();
                this.panel = null;
            }
        }

        @Override // android.view.View
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }
    }
}
