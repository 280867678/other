package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
//import com.google.android.gms.actions.SearchIntents;
import com.mxtech.DeviceUtils;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.preference.OrderedSharedPreferences;
import com.mxtech.videoplayer.list.MediaListFragment;
import com.mxtech.videoplayer.preference.ActivityPreferences;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.ListView2;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class ActivityList extends ActivityThemed implements FragmentManager.OnBackStackChangedListener, OrderedSharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DRAWABLE_MAX_LEVEL = 10000;
    private static final int MINIMUM_SPIN_TIME = 1000;
    protected static final int RESCAN_FROM_MENU = 1;
    protected static final int RESCAN_FROM_SWIPE = 2;
    private static final int ROTATE_ANIM_INTERVAL_MILLIS = 40;
    private static final int ROTATION_SPEED = 540;
    public static final String TAG = App.TAG + ".List";
    private boolean _forceShowRefreshButton;
    private boolean _fragmentsSaved;
    private LeanbackVoiceSearcher _leanbackVoiceSearcher;
    private MediaScanActionHandler _mediaScanSpinnable;
    @Nullable
    private MenuItem _searchActionItem;
    private SwipeRefresher _swipeRefresher;
    protected FragmentManager fragmentManager;
    public Menu optionsMenu;
    protected ViewGroup topLayout;

    protected abstract boolean rescan(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle saved) {
        this.fragmentManager = getSupportFragmentManager();
        super.onCreate(saved, R.layout.list);
        this.topLayout = (ViewGroup) findViewById(R.id.topLayout);
        this.fragmentManager.addOnBackStackChangedListener(this);
        this._swipeRefresher = (SwipeRefresher) findViewById(R.id.swipeRefresher);
        App.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.actionMode != null) {
            this.actionMode.finish();
        }
        if (this._searchActionItem != null) {
            MenuItemCompat.collapseActionView(this._searchActionItem);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityThemed, com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        ActivityWebBrowser.updateComponentState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this._fragmentsSaved = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this._fragmentsSaved = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        App.prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // com.mxtech.preference.OrderedSharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(OrderedSharedPreferences prefs, String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case -2014376706:
                if (key.equals(Key.LIST_REFRESH_METHODS)) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                updateRefreshUI();
                return;
            default:
                return;
        }
    }

    @Override // com.mxtech.videoplayer.ActivityThemed, com.mxtech.app.ToolbarAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        ListView2 listView = (ListView2) findViewById(16908298);
        if (listView != null) {
            listView.setNextFocusLeftId(R.id.all);
            listView.setNextFocusRightId(R.id.property);
            listView.enableDPadLeftUp(false);
        }
    }

    @Override // com.mxtech.videoplayer.ActivityThemed, com.mxtech.app.ToolbarAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v7.app.AppCompatCallback
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        ListView2 listView = (ListView2) findViewById(16908298);
        if (listView != null) {
            listView.setNextFocusLeftId(-1);
            listView.setNextFocusRightId(R.id.media_scan);
            listView.enableDPadLeftUp(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRefreshUI() {
        MenuItem item;
        if (this.optionsMenu != null && (item = this.optionsMenu.findItem(R.id.media_scan)) != null) {
            MenuItemCompat.setShowAsAction(item, (this._forceShowRefreshButton || (P.list_refresh_methods & 1) != 0) ? 2 : 0);
        }
        if (this._swipeRefresher != null) {
            this._swipeRefresher.setEnabled((P.list_refresh_methods & 2) != 0);
        }
    }

    public void startSpin(int from) {
        if (from == 1) {
            if (this._mediaScanSpinnable != null) {
                this._mediaScanSpinnable.startSpin();
            }
        } else if (this._swipeRefresher != null) {
            this._swipeRefresher.startSpin();
        }
    }

    public void stopSpin() {
        if (this._mediaScanSpinnable != null && this._mediaScanSpinnable.isSpinning()) {
            this._mediaScanSpinnable.stopSpin();
        }
        if (this._swipeRefresher != null && this._swipeRefresher.isSpinning()) {
            this._swipeRefresher.stopSpin();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canStartRescan() {
        if (this._mediaScanSpinnable == null || !this._mediaScanSpinnable.isAnimating()) {
            return this._swipeRefresher == null || !this._swipeRefresher.isAnimating();
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static class SwipeRefresher extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, Runnable {
        private ActivityList _activity;
        private boolean _animating;
        private long _refreshBeginTime;
        private boolean _spinning;

        public SwipeRefresher(Context context) {
            super(context);
            init(context);
        }

        public SwipeRefresher(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            this._activity = (ActivityList) ViewUtils.getActivityFrom(this);
            setOnRefreshListener(this);
        }

        @Override // android.support.v4.widget.SwipeRefreshLayout
        public boolean canChildScrollUp() {
            ListView listView = (ListView) findViewById(16908298);
            if (listView == null || listView.getVisibility() != 0) {
                return false;
            }
            if (Build.VERSION.SDK_INT < 14) {
                if (listView.getChildCount() > 0) {
                    return listView.getFirstVisiblePosition() > 0 || listView.getChildAt(0).getTop() < listView.getPaddingTop();
                }
                return false;
            }
            return ViewCompat.canScrollVertically(listView, -1);
        }

        @Override // android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
        public void onRefresh() {
            if (!this._activity.rescan(2)) {
                setRefreshing(false);
            }
        }

        void startSpin() {
            App.handler.removeCallbacks(this);
            this._refreshBeginTime = SystemClock.uptimeMillis();
            setRefreshing(true);
            this._spinning = true;
            this._animating = true;
        }

        boolean isAnimating() {
            return this._animating;
        }

        boolean isSpinning() {
            return this._spinning;
        }

        void stopSpin() {
            this._spinning = false;
            long now = SystemClock.uptimeMillis();
            if (now < this._refreshBeginTime + 1000) {
                App.handler.postDelayed(this, (this._refreshBeginTime + 1000) - now);
            } else {
                doStopSpin();
            }
        }

        private void doStopSpin() {
            this._animating = false;
            setRefreshing(false);
        }

        @Override // java.lang.Runnable
        public void run() {
            doStopSpin();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MediaScanActionHandler implements Runnable {
        private boolean _animating;
        private final Drawable _icon;
        private boolean _spinning;

        MediaScanActionHandler(Drawable icon) {
            this._icon = icon;
        }

        public void startSpin() {
            App.handler.removeCallbacks(this);
            App.handler.postDelayed(this, 40L);
            this._spinning = true;
            this._animating = true;
            if ((P.list_refresh_methods & 1) == 0) {
                ActivityList.this._forceShowRefreshButton = true;
                ActivityList.this.updateRefreshUI();
            }
        }

        public void stopSpin() {
            this._spinning = false;
        }

        public boolean isAnimating() {
            return this._animating;
        }

        public boolean isSpinning() {
            return this._spinning;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean scheduleNext;
            int currDegree = (this._icon.getLevel() * 360) / 10000;
            int nextDegree = currDegree + 21;
            if (this._spinning) {
                nextDegree %= 360;
                scheduleNext = true;
            } else if (nextDegree < 360 && ActivityList.this.started) {
                scheduleNext = true;
            } else {
                nextDegree = 0;
                scheduleNext = false;
            }
            this._icon.setLevel((nextDegree * 10000) / 360);
            this._icon.invalidateSelf();
            if (scheduleNext) {
                App.handler.postDelayed(this, 40L);
                return;
            }
            this._animating = false;
            if (ActivityList.this._forceShowRefreshButton) {
                ActivityList.this._forceShowRefreshButton = false;
                ActivityList.this.updateRefreshUI();
            }
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        getMenuInflater().inflate(R.menu.list, menu);
        colorizeMenuIcons(menu);
        MenuItem item = menu.findItem(R.id.media_scan);
        if (item != null) {
            Drawable icon = item.getIcon();
            if (icon != null) {
                this._mediaScanSpinnable = new MediaScanActionHandler(item.getIcon());
            }
        }
        Resources res = getResources();
        MenuItem foldersItem = menu.findItem(R.id.folders);
        if (foldersItem != null) {
            foldersItem.setTitle(StringUtils.capitalize(res.getQuantityString(R.plurals.folders, 10000), L.sb));
        }
        MenuItem filesItem = menu.findItem(R.id.files);
        if (filesItem != null) {
            filesItem.setTitle(StringUtils.capitalize(res.getQuantityString(R.plurals.files, 10000), L.sb));
        }
        MediaListFragment frag = (MediaListFragment) getCurrentFragment();
        if (frag != null) {
            frag.onPostCreateOptionsMenu(menu);
        }
        MenuItem searchItem = menu.findItem(R.id.search);
        if (MenuItemCompat.getActionView(searchItem) instanceof SearchView) {
            this._searchActionItem = searchItem;
            prepareSearchView(searchItem, false);
        } else {
            this._searchActionItem = null;
        }
        updateRefreshUI();
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem quitItem = menu.findItem(R.id.quit);
        if (quitItem != null) {
            boolean enabled = App.prefs.getBoolean(Key.QUIT_BUTTON, false);
            quitItem.setVisible(enabled);
            quitItem.setEnabled(enabled);
        }
        MediaListFragment frag = (MediaListFragment) getCurrentFragment();
        if (frag != null) {
            frag.onPrepareOptionsMenu(menu);
        }
        MenuItem selectItem = menu.findItem(R.id.select);
        if (selectItem != null) {
            selectItem.setVisible(!P.list_selection_mode);
            selectItem.setEnabled(P.list_selection_mode ? false : true);
        }
        return true;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        if (this._searchActionItem != null && MenuItemCompat.isActionViewExpanded(this._searchActionItem) && ((keyCode = event.getKeyCode()) == 4 || keyCode == 111)) {
            if (event.getAction() == 1) {
                MenuItemCompat.collapseActionView(this._searchActionItem);
                return true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(17)
    /* loaded from: classes.dex */
    public class LeanbackVoiceSearcher implements View.OnClickListener {
        private static final int REQUEST_SPEECH = 16;
        private SearchView _searchView;
        private View _voiceSearchBtn;

        private LeanbackVoiceSearcher(SearchView searchView, View voiceSearchBtn) {
            setSearchView(searchView, voiceSearchBtn);
        }

        void setSearchView(SearchView searchView, View voiceSearchBtn) {
            this._searchView = searchView;
            if (this._voiceSearchBtn != voiceSearchBtn) {
                this._voiceSearchBtn = voiceSearchBtn;
                voiceSearchBtn.setOnClickListener(this);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Intent recognizerIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
            recognizerIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
            recognizerIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
            CharSequence hint = this._searchView.getQueryHint();
            if (hint != null) {
                recognizerIntent.putExtra("android.speech.extra.PROMPT", hint);
            }
            try {
                ActivityList.this.startActivityForResult(recognizerIntent, 16);
            } catch (Exception e) {
                Log.e(ActivityList.TAG, "Cannot find activity for speech recognizer", e);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        void onResult(int resultCode, Intent data) {
            switch (resultCode) {
                case -1:
                    ArrayList<String> matches = data.getStringArrayListExtra("android.speech.extra.RESULTS");
                    if (matches != null && matches.size() > 0) {
                        try {
                            Context context = ActivityList.this.getApplicationContext();
                            Intent intent = new Intent(context, AppUtils.findActivityKindOf(context, ActivityMediaList.class));
                            intent.setAction("android.intent.action.SEARCH");
                            intent.putExtra(SearchIntents.EXTRA_QUERY, matches.get(0));
                            intent.putExtra("android.speech.extra.RESULTS", matches);
                            ActivityList.this.startActivity(intent);
                            return;
                        } catch (Exception e) {
                            Log.e(ActivityList.TAG, "", e);
                            return;
                        }
                    }
                    break;
                case 0:
                    return;
                case 1:
                    break;
                case 2:
                default:
                    ActivityList.this.showToast(R.string.voice_search_unknown_error);
                    return;
                case 3:
                    ActivityList.this.showToast(R.string.voice_search_server_error);
                    return;
                case 4:
                    ActivityList.this.showToast(R.string.voice_search_no_network);
                    return;
            }
            ActivityList.this.showToast(R.string.voice_search_no_catch);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(int resId) {
        Toast.makeText(this, resId, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void prepareSearchView(MenuItem searchActionItem, boolean expandRightnow) {
        SearchManager searchManager;
        View searchVoiceBtn;
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchActionItem);
        if (searchView != null) {
            if (Build.VERSION.SDK_INT >= 8 && (searchManager = (SearchManager) getSystemService(MediaListFragment.TYPE_SEARCH)) != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                if (DeviceUtils.isLeanbackTV && (searchVoiceBtn = searchView.findViewById(R.id.search_voice_btn)) != null) {
                    if (this._leanbackVoiceSearcher != null) {
                        this._leanbackVoiceSearcher.setSearchView(searchView, searchVoiceBtn);
                    } else {
                        this._leanbackVoiceSearcher = new LeanbackVoiceSearcher(searchView, searchVoiceBtn);
                    }
                }
            }
            if (expandRightnow) {
                searchView.setIconifiedByDefault(false);
                searchView.setIconified(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityVPBase, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 16) {
            if (this._leanbackVoiceSearcher != null) {
                this._leanbackVoiceSearcher.onResult(resultCode, data);
                return;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SearchActionMode implements ActionMode.Callback {
        private SearchActionMode() {
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            ActivityList.this.getMenuInflater().inflate(R.menu.list_search_mode, menu);
            ActivityList.this.prepareSearchView(menu.findItem(R.id.search), true);
            return true;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menu) {
            return false;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"InlinedApi"})
    public boolean onSearchRequested() {
        if (Build.VERSION.SDK_INT < 8) {
            return super.onSearchRequested();
        }
        if (DeviceUtils.isLeanbackTV) {
        }
        if (this._searchActionItem != null) {
            MenuItemCompat.expandActionView(this._searchActionItem);
        } else {
            startSupportActionMode(new SearchActionMode());
        }
        return true;
    }

    @Override // com.mxtech.app.MXAppCompatActivity, com.mxtech.app.PopupMenuHack.OptionsItemSelector
    public boolean onOptionsItemSelected2(MenuItem item) {
        if (isFinishing()) {
            return true;
        }
        int itemId = item.getItemId();
        if (itemId == R.id.search) {
            onSearchRequested();
            return true;
        } else if (itemId == R.id.preference) {
            try {
                startActivity(new Intent(getApplicationContext(), AppUtils.findActivityKindOf(this, ActivityPreferences.class)));
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
            return true;
        } else if (((App) getApplication()).handleHelpCommand(this, itemId)) {
            return true;
        } else {
            if (itemId == R.id.quit) {
                App.quit();
                return true;
            }
            MediaListFragment frag = (MediaListFragment) getCurrentFragment();
            if (frag == null || !frag.onOptionsItemSelected(item)) {
                return super.onOptionsItemSelected2(item);
            }
            return true;
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public final void onBackPressed() {
        if (this.actionMode != null) {
            this.actionMode.finish();
        } else if (!this._fragmentsSaved && this.fragmentManager.getBackStackEntryCount() > 0) {
            this.fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 7:
            case 8:
            case 9:
                P.hasWheelHeuristic = true;
                break;
        }
        return super.onGenericMotionEvent(event);
    }

    @Nullable
    public final Fragment getCurrentFragment() {
        return this.fragmentManager.findFragmentById(R.id.list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void newFragment(Bundle args, boolean backupCurrent) {
        MediaListFragment current = (MediaListFragment) getCurrentFragment();
        MediaListFragment new_ = new MediaListFragment();
        new_.setArguments(args);
        FragmentTransaction tx = this.fragmentManager.beginTransaction();
        if (current != null) {
            if (backupCurrent) {
                tx.setBreadCrumbTitle(current.title());
                tx.addToBackStack(null);
            }
            tx.remove(current);
        }
        tx.add(R.id.list, new_);
        tx.commitAllowingStateLoss();
        this.fragmentManager.executePendingTransactions();
    }

    public void clearBackStack() {
        if (!this._fragmentsSaved) {
            while (this.fragmentManager.getBackStackEntryCount() > 0) {
                this.fragmentManager.popBackStackImmediate();
            }
        }
    }

    @Override // android.support.v4.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        updateHomeAsUp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (this.fragmentManager.getBackStackEntryCount() > 0) {
                actionBar.setDisplayOptions(4, 4);
            } else {
                actionBar.setDisplayOptions(0, 4);
            }
        }
    }
}
