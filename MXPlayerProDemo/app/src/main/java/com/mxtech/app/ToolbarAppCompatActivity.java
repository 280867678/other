package com.mxtech.app;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class ToolbarAppCompatActivity extends MXAppCompatActivity {
    public static final int HIDING = 2;
    public static final int NONE = 0;
    public static final int SHOWING = 1;
    private boolean _actionModeAllowed;
    private ActionModeCompat _actionModeCompat;
    private int _splitbarLayoutResId;
    private int _splitbarMenuResId;
    private ToolbarAnimationListener _toolbarAnimationListener;
    private Animation _toolbarHideAnimation;
    private Animation _toolbarShowAnimation;
    protected ActionMode actionMode;
    protected int orientation;
    protected Toolbar splitbar;
    protected Toolbar toolbar;
    private int _lastToolbarOrientation = -1;
    private int _currentToolbarTransformation = 0;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < 14) {
            this._actionModeAllowed = false;
        } else {
            this._actionModeAllowed = true;
        }
        super.onCreate(savedInstanceState);
        Configuration cfg = getResources().getConfiguration();
        this.orientation = cfg.orientation;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        resetToolbarsIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        if (this.toolbar != null) {
            this.toolbar.dismissPopupMenus();
        }
        super.onStop();
    }

    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        resetToolbarsIfNeeded();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.orientation != newConfig.orientation) {
            this.orientation = newConfig.orientation;
            onOrientationChanged(this.orientation);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onOrientationChanged(int orientation) {
        resetToolbarsIfNeeded(orientation);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this._actionModeCompat != null) {
            this._actionModeCompat.finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity
    public void setSupportActionBar(Toolbar toolbar) {
        if (this.toolbar != null) {
            if (this._currentToolbarTransformation == 1) {
                getSupportActionBar().show();
            } else if (this._currentToolbarTransformation == 2) {
                getSupportActionBar().hide();
            }
            this.toolbar.clearAnimation();
            setToolbarTransformation(0);
        }
        this.toolbar = toolbar;
        AppCompatUtils.applyToolbarDisplayOptions(toolbar);
        if (this.actionMode != null) {
            toolbar.setVisibility(4);
        }
        super.setSupportActionBar(toolbar);
    }

    private void resetToolbarsIfNeeded() {
        resetToolbarsIfNeeded(getResources().getConfiguration().orientation);
    }

    private void resetToolbarsIfNeeded(int orientation) {
        if ((this.toolbar == null || this.started) && orientation != this._lastToolbarOrientation) {
            resetToolbars(orientation);
        }
    }

    private void resetToolbars(int orientation) {
        this._lastToolbarOrientation = orientation;
        if (this.splitbar != null) {
            Toolbar newSplitbar = AppCompatUtils.resetSplitToolbar(this, this._splitbarLayoutResId);
            if (this._splitbarMenuResId != 0) {
                newSplitbar.inflateMenu(this._splitbarMenuResId);
                colorizeMenuIcons(newSplitbar.getMenu());
            } else {
                int numChildren = this.splitbar.getChildCount();
                for (int i = 0; i < numChildren; i++) {
                    View child = this.splitbar.getChildAt(i);
                    this.splitbar.removeViewAt(i);
                    newSplitbar.addView(child);
                }
            }
            Toolbar old = this.splitbar;
            this.splitbar = newSplitbar;
            onSplitToolbarChanged(old, newSplitbar);
        }
        if (this._actionModeCompat != null) {
            this._actionModeCompat.reset();
            return;
        }
        Toolbar toolbar = AppCompatUtils.resetToolbar(this, 0);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public final void setActionBarShowAnimation(int resId) {
        setActionBarShowAnimation(AnimationUtils.loadAnimation(this, resId));
    }

    public final void setActionBarShowAnimation(Animation animation) {
        this._toolbarShowAnimation = animation;
        if (this._toolbarAnimationListener == null) {
            this._toolbarAnimationListener = new ToolbarAnimationListener();
        }
        animation.setAnimationListener(this._toolbarAnimationListener);
    }

    public final void setActionBarHideAnimation(int resId) {
        setActionBarHideAnimation(AnimationUtils.loadAnimation(this, resId));
    }

    public final void setActionBarHideAnimation(Animation animation) {
        this._toolbarHideAnimation = animation;
        if (this._toolbarAnimationListener == null) {
            this._toolbarAnimationListener = new ToolbarAnimationListener();
        }
        animation.setAnimationListener(this._toolbarAnimationListener);
    }

    public void showActionBar(boolean animate) {
        this._currentToolbarTransformation = 0;
        if (this.toolbar != null) {
            this.toolbar.clearAnimation();
            if (animate && this._toolbarShowAnimation != null) {
                this.toolbar.setVisibility(4);
                this.toolbar.startAnimation(this._toolbarShowAnimation);
                setToolbarTransformation(1);
                return;
            }
            getSupportActionBar().show();
        }
    }

    public void hideActionBar(boolean animate) {
        this._currentToolbarTransformation = 0;
        if (this.toolbar != null) {
            this.toolbar.clearAnimation();
            if (animate && this._toolbarHideAnimation != null) {
                this.toolbar.startAnimation(this._toolbarHideAnimation);
                setToolbarTransformation(2);
                return;
            }
            getSupportActionBar().hide();
        }
    }

    public final int getToolbarTransformation() {
        return this._currentToolbarTransformation;
    }

    protected void onToolbarTransformationChanged(int from, int to) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ToolbarAnimationListener implements Animation.AnimationListener {
        private ToolbarAnimationListener() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (ToolbarAppCompatActivity.this._currentToolbarTransformation == 1) {
                if (animation == ToolbarAppCompatActivity.this._toolbarShowAnimation) {
                    ToolbarAppCompatActivity.this.getSupportActionBar().show();
                    ToolbarAppCompatActivity.this.setToolbarTransformation(0);
                }
            } else if (ToolbarAppCompatActivity.this._currentToolbarTransformation == 2 && animation == ToolbarAppCompatActivity.this._toolbarHideAnimation) {
                ToolbarAppCompatActivity.this.getSupportActionBar().hide();
                ToolbarAppCompatActivity.this.setToolbarTransformation(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setToolbarTransformation(int transformation) {
        if (this._currentToolbarTransformation != transformation) {
            int was = this._currentToolbarTransformation;
            this._currentToolbarTransformation = transformation;
            onToolbarTransformationChanged(was, transformation);
        }
    }

    @Override // android.support.v7.app.AppCompatActivity
    public ActionMode startSupportActionMode(ActionMode.Callback callback) {
        if (this._actionModeAllowed) {
            return super.startSupportActionMode(callback);
        }
        if (this._actionModeCompat != null) {
            this._actionModeCompat.finish();
        }
        return new ActionModeCompat(callback);
    }

    public Toolbar addSplitToolbar(boolean actionMode) {
        return addSplitToolbar(actionMode ? R.layout.toolbar_actionmode : R.layout.toolbar);
    }

    public Toolbar addSplitToolbar(int layoutResId) {
        removeSplitToolbar();
        this._splitbarLayoutResId = layoutResId;
        this._splitbarMenuResId = 0;
        this.splitbar = AppCompatUtils.resetSplitToolbar(this, this._splitbarLayoutResId);
        onSplitToolbarAdded(this.splitbar);
        return this.splitbar;
    }

    public void removeSplitToolbar() {
        if (this.splitbar != null) {
            ((ViewGroup) this.splitbar.getParent()).removeView(this.splitbar);
            Toolbar old = this.splitbar;
            this.splitbar = null;
            onSplitToolbarRemoved(old);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSplitToolbarAdded(Toolbar splitbar) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSplitToolbarRemoved(Toolbar splitbar) {
    }

    protected void onSplitToolbarChanged(Toolbar oldSplitbar, Toolbar newSplitbar) {
    }

    public void inflateSplitbarMenu(int resId) {
        this._splitbarMenuResId = resId;
        this.splitbar.inflateMenu(resId);
        colorizeMenuIcons(this.splitbar.getMenu());
    }

    public Toolbar getSplitToolbar() {
        return this.splitbar;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        this.actionMode = mode;
        if (this.toolbar != null && this._actionModeAllowed) {
            this.toolbar.setVisibility(4);
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        this.actionMode = null;
        if (this.toolbar != null && this._actionModeAllowed) {
            this.toolbar.setVisibility(0);
        }
    }

    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this._actionModeCompat == null || keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override // com.mxtech.app.MXAppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this._actionModeCompat != null && keyCode == 82) {
            if (this.toolbar.isOverflowMenuShowing()) {
                this.toolbar.hideOverflowMenu();
            } else {
                this.toolbar.showOverflowMenu();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActionModeCompat extends ActionMode implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
        private final ActionMode.Callback _callback;

        ActionModeCompat(ActionMode.Callback callback) {
            ToolbarAppCompatActivity.this._actionModeCompat = this;
            this._callback = callback;
            ToolbarAppCompatActivity.this.toolbar = createActionModeBar();
            TypedArray a = ToolbarAppCompatActivity.this.toolbar.getContext().obtainStyledAttributes(new int[]{R.attr.actionModeCloseDrawable});
            ToolbarAppCompatActivity.this.toolbar.setNavigationIcon(a.getResourceId(0, 0));
            a.recycle();
            Menu menu = ToolbarAppCompatActivity.this.toolbar.getMenu();
            callback.onCreateActionMode(this, menu);
            ToolbarAppCompatActivity.this.onSupportActionModeStarted(this);
            callback.onPrepareActionMode(this, menu);
        }

        void reset() {
            CharSequence title = ToolbarAppCompatActivity.this.toolbar.getTitle();
            CharSequence subtitle = ToolbarAppCompatActivity.this.toolbar.getSubtitle();
            Drawable navigationIcon = ToolbarAppCompatActivity.this.toolbar.getNavigationIcon();
            ToolbarAppCompatActivity.this.toolbar.setNavigationIcon((Drawable) null);
            ToolbarAppCompatActivity.this.toolbar = createActionModeBar();
            ToolbarAppCompatActivity.this.toolbar.setTitle(title);
            ToolbarAppCompatActivity.this.toolbar.setSubtitle(subtitle);
            ToolbarAppCompatActivity.this.toolbar.setNavigationIcon(navigationIcon);
            Menu menu = ToolbarAppCompatActivity.this.toolbar.getMenu();
            ToolbarAppCompatActivity.this._actionModeCompat._callback.onCreateActionMode(ToolbarAppCompatActivity.this._actionModeCompat, menu);
            ToolbarAppCompatActivity.this._actionModeCompat._callback.onPrepareActionMode(ToolbarAppCompatActivity.this._actionModeCompat, menu);
        }

        private Toolbar createActionModeBar() {
            Toolbar bar = AppCompatUtils.resetToolbar(ToolbarAppCompatActivity.this, R.layout.toolbar_actionmode);
            bar.setOnMenuItemClickListener(this);
            bar.setNavigationOnClickListener(this);
            return bar;
        }

        @Override // android.support.v7.view.ActionMode
        public void finish() {
            this._callback.onDestroyActionMode(this);
            ToolbarAppCompatActivity.this._actionModeCompat = null;
            ToolbarAppCompatActivity.this.onSupportActionModeFinished(this);
            Toolbar toolbar = AppCompatUtils.resetToolbar(ToolbarAppCompatActivity.this, 0);
            if (toolbar != null) {
                ToolbarAppCompatActivity.this.setSupportActionBar(toolbar);
            }
        }

        @Override // android.support.v7.widget.Toolbar.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            return this._callback.onActionItemClicked(this, item);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            finish();
        }

        @Override // android.support.v7.view.ActionMode
        public View getCustomView() {
            return null;
        }

        @Override // android.support.v7.view.ActionMode
        public Menu getMenu() {
            return ToolbarAppCompatActivity.this.toolbar.getMenu();
        }

        @Override // android.support.v7.view.ActionMode
        public MenuInflater getMenuInflater() {
            return ToolbarAppCompatActivity.this.getMenuInflater();
        }

        @Override // android.support.v7.view.ActionMode
        public CharSequence getSubtitle() {
            return ToolbarAppCompatActivity.this.toolbar.getSubtitle();
        }

        @Override // android.support.v7.view.ActionMode
        public CharSequence getTitle() {
            return ToolbarAppCompatActivity.this.toolbar.getTitle();
        }

        @Override // android.support.v7.view.ActionMode
        public void invalidate() {
            this._callback.onPrepareActionMode(this, ToolbarAppCompatActivity.this.toolbar.getMenu());
        }

        @Override // android.support.v7.view.ActionMode
        public void setCustomView(View view) {
        }

        @Override // android.support.v7.view.ActionMode
        public void setSubtitle(CharSequence subtitle) {
            ToolbarAppCompatActivity.this.toolbar.setTitle(subtitle);
        }

        @Override // android.support.v7.view.ActionMode
        public void setSubtitle(int resId) {
            ToolbarAppCompatActivity.this.toolbar.setSubtitle(resId);
        }

        @Override // android.support.v7.view.ActionMode
        public void setTitle(CharSequence title) {
            ToolbarAppCompatActivity.this.toolbar.setTitle(title);
        }

        @Override // android.support.v7.view.ActionMode
        public void setTitle(int resId) {
            ToolbarAppCompatActivity.this.toolbar.setTitle(resId);
        }
    }
}
