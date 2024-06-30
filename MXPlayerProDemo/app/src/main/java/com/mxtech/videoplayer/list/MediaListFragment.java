package com.mxtech.videoplayer.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.mxtech.ArrayUtils;
import com.mxtech.DeviceUtils;
import com.mxtech.NumericUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.database.DataSetObservable2;
import com.mxtech.media.IMediaPlayer;
import com.mxtech.media.MediaUtils;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.ActivityScreen;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.MediaLoader;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaDirectoryService;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.service.FileSubtitle;
import com.mxtech.videoplayer.subtitle.service.Media;
import com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager;
import com.mxtech.videoplayer.widget.DirectMediaOpener;
import com.mxtech.widget.FlatListView;
import com.mxtech.widget.PropertyDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/* loaded from: classes.dex */
public final class MediaListFragment extends Fragment implements Runnable, MediaLoader.Receiver, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ActionMode.Callback, MediaDirectoryService.OnMockDirectoryChangeListener {
    private static final int FIELD_POS_DATE = 8;
    private static final int FIELD_POS_DURATION = 1;
    private static final int FIELD_POS_EXTENSION = 2;
    private static final int FIELD_POS_FRAME_RATE = 5;
    private static final int FIELD_POS_LOCATION = 6;
    private static final int FIELD_POS_PLAYED_TIME = 3;
    private static final int FIELD_POS_RESOLUTION = 4;
    private static final int FIELD_POS_SIZE = 7;
    private static final int FIELD_POS_THUMBNAIL = 0;
    private static final String KEY_NEW_ARGS = "media_list:new_args";
    public static final String KEY_TARGET = "media_list:target";
    public static final String KEY_TYPE = "media_list:type";
    private static final int LOADING_TEXT_DISPLAY_DELAY = 500;
    private static final int MAX_PRELOAD = 64;
    private static final int MAX_PRELOAD_HALF = 32;
    private static final int MAX_REQUESTS = 2;
    private static final int NB_FIELD = 9;
    private static final int NB_SORT = 10;
    private static final int PRELOADING_DELAY_MILLIS = 1000;
    private static final int SORT_POS_DATE = 8;
    private static final int SORT_POS_DURATION = 4;
    private static final int SORT_POS_FRAME_RATE = 5;
    private static final int SORT_POS_LOCATION = 6;
    private static final int SORT_POS_RESOLUTION = 3;
    private static final int SORT_POS_SIZE = 7;
    private static final int SORT_POS_STATE = 1;
    private static final int SORT_POS_TITLE = 0;
    private static final int SORT_POS_TYPE = 9;
    private static final int SORT_POS_WATCH_TIME = 2;
    public static final String TYPE_FILE = "file";
    public static final String TYPE_ROOT = "root";
    public static final String TYPE_SEARCH = "search";
    public static final String TYPE_SEARCH_MULTI = "search_multi";
    public static final String TYPE_URI = "uri";
    private View _actionButtonDelete;
    private View _actionButtonMarkAs;
    private View _actionButtonRename;
    private View _actionButtonSelectAll;
    private ActionMode _actionMode;
    private Builder _builder;
    private TextView _emptyTextView;
    private ListView _listView;
    private Runnable _loadingTextDisplayTask;
    private Bundle _newArgs;
    private boolean _preloading;
    private Runnable _preloadingTask;
    private boolean _preloadingTaskPosted;
    @Nullable
    private SubtitleServiceManager _ssm;
    ActivityMediaList container;
    ListHelper helper;
    boolean started;
    public static final String TAG = ActivityMediaList.TAG + "/Fragment/ML";
    private static final Object[] _emptySection = new Object[0];
    private final DataSetObservable _listViewObserver = new DataSetObservable2();
    private final View.OnClickListener _actionItemClickListener = new View.OnClickListener() { // from class: com.mxtech.videoplayer.list.MediaListFragment.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            MediaListFragment.this.onActionItemClicked(v.getId());
        }
    };
    private Entry[] _entries = new Entry[0];
    private boolean _firstBuild = true;
    private long _nextRefreshUptimeMs = Long.MAX_VALUE;
    private Runnable _statusUpdater = new Runnable() { // from class: com.mxtech.videoplayer.list.MediaListFragment.2
        @Override // java.lang.Runnable
        public void run() {
            MediaListFragment.this._builder.updateStatusText();
        }
    };
    private final Map<FileEntry, InfoRequest> _pendingInfoRequests = new HashMap();
    private final List<FileEntry> _infoRequests = new ArrayList();

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        Bundle args;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_NEW_ARGS)) {
            args = savedInstanceState.getBundle(KEY_NEW_ARGS);
        } else {
            args = getArguments();
        }
        ActivityMediaList activity = (ActivityMediaList) getActivity();
        this.helper = activity.getListHelper();
        this.container = activity;
        doCreate(args);
    }

    private void doCreate(Bundle args) {
        String type = args.getString(KEY_TYPE);
        ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
        if (TYPE_SEARCH.equals(type)) {
            this._builder = new FilesBuilder(args.getString(KEY_TARGET), this.helper.context, this);
        } else if (TYPE_SEARCH_MULTI.equals(type)) {
            this._builder = new FilesBuilder(args.getStringArrayList(KEY_TARGET), this.helper.context, this);
        } else if (TYPE_FILE.equals(type)) {
            File file = new File(args.getString(KEY_TARGET));
            if (file.isDirectory()) {
                this._builder = new DirectoryBuilder(mdir.newFile(null, file, 34), P.list_view != 3 ? 0 : 1, this.container, this);
            } else {
                this._builder = new PlaylistBuilder(file, this.helper.context, this);
            }
        } else if (TYPE_URI.equals(type)) {
            Uri uri = (Uri) args.getParcelable(KEY_TARGET);
            String scheme = uri.getScheme();
            if (scheme == null || TYPE_FILE.equals(scheme)) {
                String path = uri.getPath();
                File file2 = new File(path);
                if (file2.isDirectory()) {
                    this._builder = new DirectoryBuilder(mdir.newFile(path, file2, 34), P.list_view != 3 ? 0 : 1, this.helper.context, this);
                } else {
                    this._builder = new PlaylistBuilder(file2, this.helper.context, this);
                }
            } else if ("filter".equals(scheme)) {
                this._builder = new FilesBuilder(uri.getPath(), this.helper.context, this);
            } else {
                this._builder = new PlaylistBuilder(uri, this.helper.context, this);
            }
        } else if ((P.list_view & 1) != 0) {
            if (P.list_view == 1) {
                this._builder = new AllDirectoriesBuilder(this.helper.context, this);
            } else {
                this._builder = new DirectoryBuilder(null, 2, this.helper.context, this);
            }
        } else {
            this._builder = new FilesBuilder(this.helper.context, this);
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        this._listView = (ListView) view.findViewById(16908298);
        this._emptyTextView = (TextView) view.findViewById(16908292);
        this._listView.setEmptyView(this._emptyTextView);
        this._listView.setOnItemClickListener(this);
        this._listView.setOnItemLongClickListener(this);
        this._listView.setAdapter((ListAdapter) new Adapter());
        return view;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        this.started = true;
        L.thumbCache.activate();
        super.onStart();
        doStart();
    }

    private void doStart() {
        this._builder.start();
        build();
        L.directoryService.registerMockDirectoryChangeListener(this);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        doResume();
    }

    private void doResume() {
        this._listView.requestFocus();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        doPause();
    }

    private void doPause() {
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.started = false;
        super.onStop();
        doStop();
        L.thumbCache.deactiavte();
    }

    private void doStop() {
        Entry[] entryArr;
        this.helper.cancelAllPendingJobs();
        for (Entry entry : this._entries) {
            entry.onPendingJobsCanceled();
        }
        this._builder.stop();
        cancelLoadingTextDisplayTask();
        cancelScheduledRefresh();
        clearInfoLoading();
        L.directoryService.unregisterMockDirectoryChangeListener(this);
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        doDestroy();
        super.onDestroy();
    }

    private void doDestroy() {
        cancelStatusTextUpdate();
        cancelScheduledRefresh();
        this._builder.close();
        clearInfoLoading();
        if (this._ssm != null) {
            this._ssm.cancel();
            this._ssm = null;
        }
    }

    @Override // com.mxtech.videoplayer.directory.MediaDirectoryService.OnMockDirectoryChangeListener
    public void onMockDirectoryChanged(ImmutableMediaDirectory mdir) {
        build();
    }

    public void reset(Bundle args) {
        this._newArgs = args;
        if (this._builder != null) {
            if (this.started) {
                if (isResumed()) {
                    doPause();
                }
                doStop();
            }
            doDestroy();
        }
        doCreate(args);
        if (this.started) {
            doStart();
            if (isResumed()) {
                doResume();
            }
        }
        Menu menu = this.container.optionsMenu;
        if (menu != null) {
            onPostCreateOptionsMenu(menu);
            onPrepareOptionsMenu(menu);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this._newArgs != null) {
            outState.putBundle(KEY_NEW_ARGS, this._newArgs);
        }
    }

    public void onPostCreateOptionsMenu(Menu menu) {
        updatePlayLastMenu();
    }

    @Override // android.support.v4.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item;
        switch (P.list_view) {
            case 0:
                item = menu.findItem(R.id.files);
                break;
            case 1:
            case 2:
            default:
                item = menu.findItem(R.id.all_folders);
                break;
            case 3:
                item = menu.findItem(R.id.folders);
                break;
        }
        if (item != null) {
            item.setChecked(true);
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.container.isFinishing()) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.open_url) {
            new DirectMediaOpener(this.container);
            return true;
        } else if (id == R.id.all_folders) {
            P.setListView(1);
            return true;
        } else if (id == R.id.folders) {
            P.setListView(3);
            return true;
        } else if (id == R.id.files) {
            P.setListView(0);
            return true;
        } else if (id == R.id.sort_by) {
            runSortingMethodSelectionDialog();
            return true;
        } else if (id == R.id.fields) {
            new FieldsSelectionDialogRunner();
            return true;
        } else if (id == R.id.select) {
            selectItemInActionMode(-1);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void play(Entry[] entries, byte decoder) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (Entry entry : entries) {
            if ((entry.features & 64) != 0) {
                Uri[] items = entry.getItems();
                if (items.length > 1) {
                    ArrayUtils.safeSort(items, ListHelper.CASE_INSENSITIVE_NUMERIC_URI_ORDER);
                    uris.addAll(Arrays.asList(items));
                } else if (items.length == 1) {
                    uris.add(items[0]);
                }
            }
        }
        play(null, (Uri[]) uris.toArray(new Uri[uris.size()]), decoder);
    }

    public void playLast() {
        Uri lastPlayedMedia = this._builder.lastMediaUri;
        if (lastPlayedMedia != null) {
            play(lastPlayedMedia, null, (byte) 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void play(Uri uri) {
        play(uri, null, (byte) 0);
    }

    void play(@Nullable Uri uri, @Nullable Uri[] playList) {
        play(uri, playList, (byte) 0);
    }

    void play(@Nullable Uri uri, @Nullable Uri[] playList, byte decoder) {
        if (playList != null && playList.length > 0) {
            if (P.shuffle) {
                uri = playList[NumericUtils.ThreadLocalRandom.get().nextInt(playList.length)];
            } else if (uri == null) {
                uri = playList[0];
            }
        } else if (uri != null) {
            if (!(this._builder instanceof DirectoryBuilder)) {
                playList = getPlaylistFor(uri);
            }
        } else if (App.DEBUG) {
            throw new IllegalArgumentException("Both target uri and play list are not provided.");
        } else {
            Log.e(TAG, "Both target uri and play list is not provided.");
            return;
        }
        if (this._actionMode != null) {
            this._actionMode.finish();
        }
        ActivityScreen.launch(this.helper.context, uri, playList, decoder);
    }

    @Nullable
    private Uri[] getPlaylistFor(Uri mediaUri) {
        Entry[] entryArr;
        if (this._entries.length > 0) {
            boolean mediaFound = false;
            ArrayList<Uri> uris = new ArrayList<>(this._entries.length);
            for (Entry entry : this._entries) {
                if (entry instanceof PlayableEntry) {
                    uris.add(entry.uri);
                    if (!mediaFound && entry.uri.equals(mediaUri)) {
                        mediaFound = true;
                    }
                }
            }
            if (mediaFound) {
                return (Uri[]) uris.toArray(new Uri[uris.size()]);
            }
        }
        return null;
    }

    private void runSortingMethodSelectionDialog() {
        int currentIndex;
        AlertDialog.Builder b = new AlertDialog.Builder(this.container);
        CharSequence[] methods = {getString(R.string.detail_title).replace('\n', ' '), getString(R.string.state).replace('\n', ' '), getString(R.string.watch_time).replace('\n', ' '), getString(R.string.detail_resolution).replace('\n', ' '), getString(R.string.detail_playtime).replace('\n', ' '), getString(R.string.frame_rate).replace('\n', ' '), getString(R.string.detail_folder).replace('\n', ' '), getString(R.string.detail_size).replace('\n', ' '), getString(R.string.detail_date).replace('\n', ' '), getString(R.string.type).replace('\n', ' ')};
        if (P.list_sortings.length > 0) {
            switch (P.list_sortings[0]) {
                case -64:
                case 64:
                    currentIndex = 4;
                    break;
                case -32:
                case 32:
                    currentIndex = 5;
                    break;
                case -16:
                case 16:
                    currentIndex = 3;
                    break;
                case -8:
                case 8:
                    currentIndex = 6;
                    break;
                case -7:
                case 7:
                    currentIndex = 1;
                    break;
                case -6:
                case 6:
                    currentIndex = 2;
                    break;
                case -5:
                case 5:
                    currentIndex = 9;
                    break;
                case IMediaPlayer.ERESTART /* -4 */:
                case 4:
                    currentIndex = 8;
                    break;
                case IMediaPlayer.EFAIL /* -3 */:
                case 3:
                    currentIndex = 7;
                    break;
                case -2:
                case -1:
                case 1:
                case 2:
                    currentIndex = 0;
                    break;
                default:
                    currentIndex = -1;
                    break;
            }
        } else {
            currentIndex = -1;
        }
        b.setTitle(R.string.sort_by);
        b.setSingleChoiceItems(methods, currentIndex, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.list.MediaListFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                int[] sortingRules;
                int fields = P.list_fields;
                switch (which) {
                    case 0:
                        sortingRules = P.getFullSortingRule(2);
                        break;
                    case 1:
                        sortingRules = P.getFullSortingRule(7);
                        break;
                    case 2:
                        sortingRules = P.getFullSortingRule(6);
                        fields |= 32;
                        break;
                    case 3:
                        sortingRules = P.getFullSortingRule(16);
                        fields |= 64;
                        break;
                    case 4:
                        sortingRules = P.getFullSortingRule(64);
                        fields |= 256;
                        break;
                    case 5:
                        sortingRules = P.getFullSortingRule(32);
                        fields |= 128;
                        break;
                    case 6:
                        sortingRules = P.getFullSortingRule(8);
                        fields |= 8;
                        break;
                    case 7:
                        sortingRules = P.getFullSortingRule(3);
                        fields |= 2;
                        break;
                    case 8:
                        sortingRules = P.getFullSortingRule(4);
                        fields |= 4;
                        break;
                    case 9:
                        sortingRules = P.getFullSortingRule(5);
                        fields |= 16;
                        break;
                    default:
                        return;
                }
                SharedPreferences.Editor editor = App.prefs.edit();
                if (fields != P.list_fields) {
                    P.list_fields = fields;
                    P.updateDispDuration();
                    editor.putInt(Key.LIST_FIELDS, fields);
                }
                P.changeSortingRules(sortingRules, editor);
                AppUtils.apply(editor);
                dialog.dismiss();
            }
        });
        this.container.showDialog((ActivityMediaList) b.create());
    }

    /* loaded from: classes2.dex */
    private class FieldsSelectionDialogRunner implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final CheckBox _drawPlaytimeOverThumbnailCheckbox;
        private final ListView _listView;

        @SuppressLint({"InflateParams"})
        FieldsSelectionDialogRunner() {
            CharSequence[] fields = {MediaListFragment.this.getString(R.string.thumbnail).replace('\n', ' '), MediaListFragment.this.getString(R.string.detail_playtime).replace('\n', ' '), MediaListFragment.this.getString(R.string.file_extension).replace('\n', ' '), MediaListFragment.this.getString(R.string.watch_time).replace('\n', ' '), MediaListFragment.this.getString(R.string.detail_resolution).replace('\n', ' '), MediaListFragment.this.getString(R.string.frame_rate).replace('\n', ' '), MediaListFragment.this.getString(R.string.detail_folder).replace('\n', ' '), MediaListFragment.this.getString(R.string.detail_size).replace('\n', ' '), MediaListFragment.this.getString(R.string.detail_date).replace('\n', ' ')};
            AlertDialog dialog = new AlertDialog.Builder(MediaListFragment.this.container).setTitle(R.string.fields).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).create();
            Context context = dialog.getContext();
            View layout = dialog.getLayoutInflater().inflate(R.layout.list_fields_selection, (ViewGroup) null);
            this._listView = (ListView) layout.findViewById(16908298);
            this._drawPlaytimeOverThumbnailCheckbox = (CheckBox) layout.findViewById(R.id.draw_playtime_over_thumbnail);
            TypedArray a = context.obtainStyledAttributes(R.styleable.AlertDialog);
            int itemLayoutId = a.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, R.layout.select_dialog_multichoice_material);
            a.recycle();
            this._listView.setAdapter((ListAdapter) new ArrayAdapter(context, itemLayoutId, fields));
            this._listView.setItemChecked(0, (P.list_fields & 1) != 0);
            this._listView.setItemChecked(3, (P.list_fields & 32) != 0);
            this._listView.setItemChecked(6, (P.list_fields & 8) != 0);
            this._listView.setItemChecked(4, (P.list_fields & 64) != 0);
            this._listView.setItemChecked(1, (P.list_fields & 256) != 0);
            this._listView.setItemChecked(5, (P.list_fields & 128) != 0);
            this._listView.setItemChecked(7, (P.list_fields & 2) != 0);
            this._listView.setItemChecked(8, (P.list_fields & 4) != 0);
            this._listView.setItemChecked(2, (P.list_fields & 16) != 0);
            if (P.list_draw_playtime_over_thumbnail) {
                this._drawPlaytimeOverThumbnailCheckbox.setChecked(true);
            }
            this._drawPlaytimeOverThumbnailCheckbox.setOnCheckedChangeListener(this);
            dialog.setView(layout);
            MediaListFragment.this.container.showDialog((ActivityMediaList) dialog);
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                this._listView.setItemChecked(0, true);
                this._listView.setItemChecked(1, true);
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                int newFields = 0;
                SparseBooleanArray checked = this._listView.getCheckedItemPositions();
                int numItems = checked.size();
                for (int i = 0; i < numItems; i++) {
                    if (checked.valueAt(i)) {
                        switch (checked.keyAt(i)) {
                            case 0:
                                newFields |= 1;
                                continue;
                            case 1:
                                newFields |= 256;
                                continue;
                            case 2:
                                newFields |= 16;
                                continue;
                            case 3:
                                newFields |= 32;
                                continue;
                            case 4:
                                newFields |= 64;
                                continue;
                            case 5:
                                newFields |= 128;
                                continue;
                            case 6:
                                newFields |= 8;
                                continue;
                            case 7:
                                newFields |= 2;
                                continue;
                            case 8:
                                newFields |= 4;
                                continue;
                        }
                    }
                }
                boolean fieldsChanged = P.list_fields != newFields;
                boolean drawPlaytimeChanged = this._drawPlaytimeOverThumbnailCheckbox.isChecked() != P.list_draw_playtime_over_thumbnail;
                if (fieldsChanged || drawPlaytimeChanged) {
                    SharedPreferences.Editor editor = App.prefs.edit();
                    if (fieldsChanged) {
                        P.list_fields = newFields;
                        editor.putInt(Key.LIST_FIELDS, newFields);
                    }
                    if (drawPlaytimeChanged) {
                        P.list_draw_playtime_over_thumbnail = P.list_draw_playtime_over_thumbnail ? false : true;
                        editor.putBoolean(Key.LIST_DRAW_PLAYTIME_OVER_THUMBNAIL, P.list_draw_playtime_over_thumbnail);
                    }
                    P.updateDispDuration();
                    AppUtils.apply(editor);
                }
            }
        }
    }

    private boolean isPlaylist(String path) {
        return false;
    }

    Entry newEntry(Uri uri) {
        File file;
        String scheme = uri.getScheme();
        String path = uri.getPath();
        if (scheme == null || TYPE_FILE.equals(scheme)) {
            file = new File(path);
            if (file.isDirectory()) {
                return new DirectoryEntry(L.directoryService.getMediaDirectory().newFile(path, file, 34), this, false);
            }
        } else {
            file = null;
        }
        if (isPlaylist(uri.toString())) {
            if (file != null) {
                return new PlaylistEntry(uri, L.directoryService.getMediaDirectory().newFile(null, file, 16), this);
            }
            return new PlaylistEntry(uri, this);
        } else if (file != null) {
            return new FileEntry(L.directoryService.getMediaDirectory().newFile(path, file, 16), this);
        } else {
            return new UrlEntry(uri, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Entry newEntry(MediaFile file) {
        if (file.isDirectory()) {
            return new DirectoryEntry(file, this, false);
        }
        if (isPlaylist(file.path)) {
            return new PlaylistEntry(file.uri(), file, this);
        }
        return new FileEntry(file, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DirectoryEntry newDirectoryEntry(MediaFile file, boolean hierarchical) {
        return new DirectoryEntry(file, this, hierarchical);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInHomogenousLocation() {
        return (this._builder.features & 256) != 0;
    }

    public boolean isVolatile() {
        return (this._builder.features & 1) != 0;
    }

    boolean isInTopDirs() {
        return (this._builder instanceof DirectoryBuilder) && ((DirectoryBuilder) this._builder).getHierarchyMode() == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBuildPrepared(Builder loader, boolean success) {
        if (success) {
            this.container.rebuildAsync();
        } else {
            this._emptyTextView.setText(this._builder.getMessage(2));
        }
    }

    private void cancelStatusTextUpdate() {
        this.helper.handler.removeCallbacks(this._statusUpdater);
    }

    private void enqueueStatusUpdate() {
        this.helper.handler.removeCallbacks(this._statusUpdater);
        this.helper.handler.post(this._statusUpdater);
    }

    public void build() {
        SortedSet<Uri> checked;
        if (!L.directoryService.getMediaDirectory().isDummy() && this._builder.prepareBuild()) {
            cancelLoadingTextDisplayTask();
            this.helper.cancelAllPendingJobs();
            cancelScheduledRefresh();
            clearInfoLoading();
            if (this._actionMode != null) {
                checked = new TreeSet<>();
                for (Entry entry : getCheckedItems()) {
                    checked.add(entry.uri);
                }
            } else {
                checked = null;
            }
            try {
                long begin = SystemClock.uptimeMillis();
                this._entries = this._builder.build();
                enqueueStatusUpdate();
                ArrayUtils.safeSort(this._entries);
                if (this._firstBuild) {
                    this._firstBuild = false;
                    if (P.scrollDownToLastMedia && this._builder.lastMediaUri != null) {
                        int i = 0;
                        Entry[] entryArr = this._entries;
                        int length = entryArr.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length) {
                                break;
                            }
                            Entry entry2 = entryArr[i2];
                            if (entry2.lastWatchTime == this._builder.lastMediaWatchTime) {
                                int position = indexToPosition(i);
                                if (this._listView instanceof FlatListView) {
                                    ((FlatListView) this._listView).ensureVisible(position);
                                }
                            } else {
                                i++;
                                i2++;
                            }
                        }
                    }
                }
                Log.v(TAG, this._entries.length + " items are built up. (" + (SystemClock.uptimeMillis() - begin) + "ms)");
            } catch (SQLiteException e) {
                Log.e(TAG, "", e);
                this._entries = new Entry[0];
                if (!this.helper.context.isFinishing()) {
                    DialogUtils.alert(this.helper.context, R.string.error_database);
                }
            }
            this._listViewObserver.notifyChanged();
            if (this._entries.length == 0) {
                this._emptyTextView.setText(this._builder.getMessage(1));
            } else if (checked != null) {
                int numEntries = this._entries.length;
                for (int i3 = 0; i3 < numEntries; i3++) {
                    if (checked.remove(this._entries[i3].uri)) {
                        this._listView.setItemChecked(indexToPosition(i3), true);
                    }
                }
                updateActionMenu();
            }
            updatePlayLastMenu();
            tryPreloading();
            if (this.container.getCurrentFragment() == this) {
                this.container.setTitle(title());
            }
            this.helper.stylizeListView(this._listView, this._builder);
            return;
        }
        if (this._loadingTextDisplayTask != null) {
            this._loadingTextDisplayTask = new Runnable() { // from class: com.mxtech.videoplayer.list.MediaListFragment.4
                @Override // java.lang.Runnable
                public void run() {
                    MediaListFragment.this._emptyTextView.setText(MediaListFragment.this._builder.getMessage(0));
                }
            };
        }
        this._emptyTextView.setText("");
        App.handler.postDelayed(this._loadingTextDisplayTask, 500L);
    }

    private void cancelLoadingTextDisplayTask() {
        if (this._loadingTextDisplayTask != null) {
            App.handler.removeCallbacks(this._loadingTextDisplayTask);
        }
    }

    public String title() {
        return this._builder.title();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getPathToCurrentContent() {
        StringBuilder sb = new StringBuilder();
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            int numBackEntry = fm.getBackStackEntryCount();
            for (int i = 0; i < numBackEntry; i++) {
                CharSequence title = fm.getBackStackEntryAt(i).getBreadCrumbTitle();
                if (title != null) {
                    if (i != 0) {
                        sb.append(" > ");
                    }
                    sb.append(title);
                }
            }
            if (sb.length() > 0) {
                sb.append(" > ");
            }
        }
        sb.append(title());
        return sb;
    }

    public void refresh(boolean reorder) {
        cancelScheduledRefresh();
        boolean showExtension = (P.list_fields & 16) != 0;
        if (showExtension != this._builder.titleContainsExtension) {
            this._builder.resetTitle(this._entries);
            reorder = true;
        }
        if (reorder) {
            ArrayUtils.safeSort(this._entries);
        }
        this._listViewObserver.notifyChanged();
        enqueueStatusUpdate();
        updatePlayLastMenu();
        this.helper.stylizeListView(this._listView, this._builder);
    }

    void cancelScheduledRefresh() {
        if (this._nextRefreshUptimeMs != Long.MAX_VALUE) {
            this._nextRefreshUptimeMs = Long.MAX_VALUE;
            this.helper.handler.removeCallbacks(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleRefresh(long uptimeMs) {
        if (uptimeMs < this._nextRefreshUptimeMs) {
            if (this._nextRefreshUptimeMs != Long.MAX_VALUE) {
                this.helper.handler.removeCallbacks(this);
            }
            this._nextRefreshUptimeMs = uptimeMs;
            this.helper.handler.postAtTime(this, uptimeMs);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        this._nextRefreshUptimeMs = Long.MAX_VALUE;
        refresh(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getRenderingTarget(Entry entry) {
        return this._listView.findViewWithTag(entry);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearChoices() {
        if (this._listView != null) {
            this._listView.clearChoices();
            if (this._actionMode != null) {
                updateActionMenuOrFinish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class InfoRequest {
        final File coverFile;
        final int loads;
        final File mediaFile;

        InfoRequest(int loads, File mediaFile, File coverFile) {
            this.loads = loads;
            this.mediaFile = mediaFile;
            this.coverFile = coverFile;
        }

        void request(MediaLoader loader, MediaLoader.Receiver receiver, Object tag) {
            loader.request(this.loads, this.mediaFile, this.coverFile, receiver, tag);
        }
    }

    /* loaded from: classes2.dex */
    static class InfoRequestWithThumbShaping extends InfoRequest {
        final Bitmap defaultThumb;
        final String durationText;
        final Uri mediaUri;

        InfoRequestWithThumbShaping(int loads, Uri mediaUri, File mediaFile, File coverFile, String durationText, Bitmap defaultThumb) {
            super(loads, mediaFile, coverFile);
            this.durationText = durationText;
            this.defaultThumb = defaultThumb;
            this.mediaUri = mediaUri;
        }

        @Override // com.mxtech.videoplayer.list.MediaListFragment.InfoRequest
        void request(MediaLoader loader, MediaLoader.Receiver receiver, Object tag) {
            loader.requestWithThumbShaping(this.loads, this.mediaUri, this.mediaFile, this.coverFile, this.durationText, this.defaultThumb, receiver, tag, P.list_duration_display == 2);
        }
    }

    private void tryPreloading() {
        if (this.started && P.list_background_load_thumbnail) {
            if (this._preloading) {
                preloadMediaInfo();
                return;
            }
            if (this._preloadingTask == null) {
                this._preloadingTask = new Runnable() { // from class: com.mxtech.videoplayer.list.MediaListFragment.5
                    @Override // java.lang.Runnable
                    public void run() {
                        MediaListFragment.this._preloadingTaskPosted = false;
                        if (MediaListFragment.this.started && P.list_background_load_thumbnail) {
                            MediaListFragment.this._preloading = true;
                            MediaListFragment.this.preloadMediaInfo();
                        }
                    }
                };
            }
            if (!this._preloadingTaskPosted) {
                this._preloadingTaskPosted = this.helper.handler.postDelayed(this._preloadingTask, 1000L);
            }
        }
    }

    private void stopPreloading() {
        if (this._preloading) {
            this._preloading = false;
        }
        if (this._preloadingTaskPosted) {
            this._preloadingTaskPosted = false;
            this.helper.handler.removeCallbacks(this._preloadingTask);
        }
    }

    private void clearInfoLoading() {
        this._pendingInfoRequests.clear();
        this._infoRequests.clear();
        stopPreloading();
    }

    public void onUserInteraction() {
        stopPreloading();
        tryPreloading();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preloadMediaInfo() {
        int numChecked = 0;
        for (int forward = positionToNextIndex(this._listView.getLastVisiblePosition()) + 1; numChecked < 32 && forward < this._entries.length; forward++) {
            if (this._infoRequests.size() < 2) {
                Entry entry = this._entries[forward];
                if (entry instanceof FileEntry) {
                    ((FileEntry) entry).preloadMediaInfo();
                }
                numChecked++;
            } else {
                return;
            }
        }
        int numChecked2 = 0;
        for (int backward = positionToNextIndex(this._listView.getFirstVisiblePosition()) - 1; numChecked2 < 32 && backward >= 0 && this._infoRequests.size() < 2; backward--) {
            Entry entry2 = this._entries[backward];
            if (entry2 instanceof FileEntry) {
                ((FileEntry) entry2).preloadMediaInfo();
            }
            numChecked2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestInfoWithThumbShaping(FileEntry entry, int loads, File mediaFile, @Nullable File coverFile, @Nullable String durationText, @Nullable Bitmap defaultThumb, boolean preload) {
        if (!preload) {
            stopPreloading();
        }
        if ((loads & 1) == 0 || this._infoRequests.size() < 2) {
            this.helper.loader.requestWithThumbShaping(loads, entry.uri, mediaFile, coverFile, durationText, defaultThumb, this, entry, P.list_duration_display == 2);
            this._infoRequests.add(entry);
            return;
        }
        this._pendingInfoRequests.put(entry, new InfoRequestWithThumbShaping(loads, entry.uri, mediaFile, coverFile, durationText, defaultThumb));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestInfo(FileEntry entry, int loads, File mediaFile, File coverFile, boolean preload) {
        if (!preload) {
            stopPreloading();
        }
        if ((loads & 1) == 0 || this._infoRequests.size() < 2) {
            this.helper.loader.request(loads, mediaFile, coverFile, this, entry);
            this._infoRequests.add(entry);
            return;
        }
        this._pendingInfoRequests.put(entry, new InfoRequest(loads, mediaFile, coverFile));
    }

    @Override // com.mxtech.videoplayer.MediaLoader.Receiver
    public void onCompleted(MediaLoader reader, MediaLoader.Result r) {
        InfoRequest req;
        FileEntry fileEntry = (FileEntry) r.tag;
        this._infoRequests.remove(fileEntry);
        fileEntry.onCompleted(reader, r);
        if (isVisible() && this._infoRequests.size() < 2) {
            if (!this._pendingInfoRequests.isEmpty()) {
                int firstVisible = positionToNextIndex(this._listView.getFirstVisiblePosition());
                int lastVisible = positionToNextIndex(this._listView.getLastVisiblePosition());
                for (int i = firstVisible; i <= lastVisible && i < this._entries.length; i++) {
                    Entry entry = this._entries[i];
                    if ((entry instanceof FileEntry) && (req = this._pendingInfoRequests.remove(entry)) != null) {
                        req.request(this.helper.loader, this, entry);
                        this._infoRequests.add((FileEntry) entry);
                        if (this._infoRequests.size() >= 2) {
                            return;
                        }
                    }
                }
            }
            Iterator<Map.Entry<FileEntry, InfoRequest>> it = this._pendingInfoRequests.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<FileEntry, InfoRequest> reqEntry = it.next();
                FileEntry entry2 = reqEntry.getKey();
                reqEntry.getValue().request(this.helper.loader, this, entry2);
                this._infoRequests.add(entry2);
                it.remove();
                if (this._infoRequests.size() >= 2) {
                    return;
                }
            }
            tryPreloading();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getEmptyStringWithStateMessage(int resId) {
        String msg = this.helper.res.getString(resId);
        if (!DeviceUtils.isExternalStorageMounted(true)) {
            L.sb.setLength(0);
            return L.sb.append(msg).append("\n(").append(this.helper.res.getString(R.string.error_media_unmounted)).append(')').toString();
        }
        return msg;
    }

    private int getBelongingGroup(int index) {
        int numGroups = this._builder.getGroupCount();
        for (int i = 0; i < numGroups; i++) {
            index -= this._builder.getChildrenCount(i);
            if (index < 0) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getGroup(int position) {
        int numGroups = this._builder.getGroupCount();
        for (int i = 0; i < numGroups; i++) {
            position -= this._builder.getChildrenCount(i) + 1;
            if (position < 0) {
                return i;
            }
        }
        return -1;
    }

    private int indexToPosition(int index) {
        return getBelongingGroup(index) + 1 + index;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int positionToIndex(int position) {
        int numGroups = this._builder.getGroupCount();
        int numChildren = 0;
        for (int i = 0; i < numGroups; i++) {
            if (numChildren == position) {
                return -1;
            }
            position--;
            numChildren += this._builder.getChildrenCount(i);
            if (position < numChildren) {
                break;
            }
        }
        return position;
    }

    private int positionToNextIndex(int position) {
        int numGroups = this._builder.getGroupCount();
        int numChildren = 0;
        for (int i = 0; i < numGroups && numChildren != position; i++) {
            position--;
            numChildren += this._builder.getChildrenCount(i);
            if (position < numChildren) {
                break;
            }
        }
        return position;
    }

    public void updatePlayLastMenu() {
        String title;
        Uri lastMediaUri = this._builder.lastMediaUri;
        if (lastMediaUri != null) {
            String name = lastMediaUri.getLastPathSegment();
            if (name != null) {
                title = L.getDisplayName(name);
            } else {
                title = lastMediaUri.toString();
            }
            updatePlayLastMenu(title);
            return;
        }
        updatePlayLastMenu(null);
    }

    private void updatePlayLastMenu(String title) {
        MenuItem playMenu;
        if (isVisible()) {
            Menu menu = this.container.optionsMenu;
            if (menu != null && (playMenu = menu.findItem(R.id.play)) != null) {
                if (title != null) {
                    playMenu.setEnabled(true);
                    playMenu.setVisible(true);
                    playMenu.setTitle(title);
                } else {
                    playMenu.setEnabled(false);
                    playMenu.setVisible(false);
                }
            }
            this.container.setPlayLastButton(title);
        }
    }

    private int getNumCheckedItems() {
        SparseBooleanArray array = this._listView.getCheckedItemPositions();
        if (array != null) {
            int numChecked = 0;
            for (int i = array.size() - 1; i >= 0; i--) {
                if (array.valueAt(i)) {
                    numChecked++;
                }
            }
            return numChecked;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Entry> getCheckedItems() {
        int index;
        ArrayList<Entry> checked = new ArrayList<>();
        SparseBooleanArray array = this._listView.getCheckedItemPositions();
        if (array != null) {
            int numArray = array.size();
            for (int i = 0; i < numArray; i++) {
                if (array.valueAt(i) && (index = positionToIndex(array.keyAt(i))) >= 0 && index < this._entries.length) {
                    checked.add(this._entries[index]);
                }
            }
        }
        return checked;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this._actionMode != null) {
            updateActionMenuOrFinish();
            return;
        }
        if (P.list_selection_mode && (view instanceof MediaListItemLayout)) {
            MediaListItemLayout layout = (MediaListItemLayout) view;
            if (layout.isIconAreaTouched()) {
                selectItemInActionMode(position);
                return;
            }
        }
        Entry entry = (Entry) view.getTag();
        if (entry != null) {
            entry.open();
        }
    }

    @Override // android.widget.AdapterView.OnItemLongClickListener
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectItemInActionMode(position);
        return true;
    }

    private void selectItemInActionMode(int position) {
        if (isResumed()) {
            if (this._actionMode == null) {
                this._actionMode = this.container.startSupportActionMode(this);
            }
            if (this._actionMode != null) {
                if (position >= 0) {
                    this._listView.setItemChecked(position, true);
                }
                updateActionMenu();
            }
        }
    }

    private void updateActionMenuOrFinish() {
        int numChecked = getNumCheckedItems();
        if (numChecked > 0) {
            updateActionMenu();
        } else {
            this._actionMode.finish();
        }
    }

    @Override // android.support.v7.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuItem groupItem;
        SubMenu subMenu;
        this._listView.setChoiceMode(2);
        this.container.getMenuInflater().inflate(R.menu.list_action_mode, menu);
        if (!L.canShare(this.container)) {
            menu.removeItem(R.id.share);
        }
        if ((this._builder.features & 4) == 0 || !P.allowEditing) {
            menu.removeItem(R.id.rename);
        }
        if ((this._builder.features & 8) == 0 || !P.allowEditing) {
            menu.removeItem(R.id.delete);
        }
        if ((this._builder.features & 1024) == 0) {
            menu.removeItem(R.id.rebuild_thumbnail);
        }
        if (!P.isOMXDecoderVisible() && (groupItem = menu.findItem(R.id.play_using)) != null && (subMenu = groupItem.getSubMenu()) != null) {
            subMenu.removeItem(R.id.play_omx);
        }
        this.container.colorizeMenuIcons(menu);
        Toolbar splitbar = this.container.addSplitToolbar(true);
        Context splitbarContext = splitbar.getContext();
        View layout = ((LayoutInflater) splitbarContext.getSystemService("layout_inflater")).inflate(R.layout.media_list_action_mode_split, (ViewGroup) splitbar, false);
        this.container.colorizeDrawables(layout);
        this._actionButtonSelectAll = layout.findViewById(R.id.all);
        this._actionButtonMarkAs = layout.findViewById(R.id.mark_as);
        this._actionButtonDelete = layout.findViewById(R.id.delete);
        this._actionButtonRename = layout.findViewById(R.id.rename);
        this._actionButtonSelectAll.setOnClickListener(this._actionItemClickListener);
        this._actionButtonMarkAs.setOnClickListener(this._actionItemClickListener);
        if ((this._builder.features & 8) == 0 || !P.allowEditing) {
            this._actionButtonDelete.setVisibility(8);
            this._actionButtonRename.setVisibility(8);
        } else {
            this._actionButtonDelete.setOnClickListener(this._actionItemClickListener);
            this._actionButtonRename.setOnClickListener(this._actionItemClickListener);
        }
        splitbar.addView(layout);
        return true;
    }

    @Override // android.support.v7.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    private void updateActionMenu() {
        File[] subFiles;
        Menu menu = this._actionMode.getMenu();
        List<Entry> checked = getCheckedItems();
        int numChecked = checked.size();
        int numEntriesHasAssociatedSubtitleFiles = 0;
        this.helper.titleSb.setLength(0);
        this.helper.titleSb.append(numChecked).append(" / ").append(this._entries.length);
        this._actionMode.setTitle(this.helper.titleSb.toString());
        int numCanRename = 0;
        int numCanDelete = 0;
        int numCanPlay = 0;
        int numDir = 0;
        for (Entry entry : checked) {
            int features = entry.features;
            if ((features & 4) != 0) {
                numCanRename++;
            }
            if ((features & 8) != 0) {
                numCanDelete++;
            }
            if ((features & 64) != 0) {
                numCanPlay++;
            }
            if (entry instanceof DirectoryEntry) {
                numDir++;
            } else if ((entry instanceof FileEntry) && (subFiles = ((FileEntry) entry).subFiles) != null && subFiles.length > 0) {
                numEntriesHasAssociatedSubtitleFiles++;
            }
        }
        MenuItem playItem = menu.findItem(R.id.play);
        MenuItem playUsingItem = menu.findItem(R.id.play_using);
        MenuItem markAs = menu.findItem(R.id.mark_as);
        boolean hasPlayable = numCanPlay > 0;
        if (playItem != null) {
            this.helper.enableMenuItem(playItem, hasPlayable);
        }
        if (playUsingItem != null) {
            this.helper.enableMenuItem(playUsingItem, hasPlayable);
        }
        if (markAs != null) {
            this.helper.enableMenuItem(markAs, hasPlayable);
        } else if (this._actionButtonMarkAs != null) {
            this.helper.enableView(this._actionButtonMarkAs, hasPlayable);
        }
        MenuItem deleteItem = menu.findItem(R.id.delete);
        if (deleteItem != null) {
            this.helper.enableMenuItem(deleteItem, numCanDelete > 0);
        } else if (this._actionButtonDelete != null) {
            this.helper.enableView(this._actionButtonDelete, numCanDelete > 0);
        }
        MenuItem renameItem = menu.findItem(R.id.rename);
        if (renameItem != null) {
            this.helper.enableMenuItem(renameItem, numCanRename == 1);
        } else if (this._actionButtonRename != null) {
            this.helper.enableView(this._actionButtonRename, numCanRename == 1);
        }
        MenuItem hideItem = menu.findItem(R.id.hide);
        if (hideItem != null) {
            this.helper.enableMenuItem(hideItem, numDir > 0);
        }
        MenuItem shareItem = menu.findItem(R.id.share);
        if (shareItem != null) {
            this.helper.enableMenuItem(shareItem, numChecked > 0);
        }
        MenuItem subtitleSearch = menu.findItem(R.id.subtitle_search);
        MenuItem subtitleRate = menu.findItem(R.id.subtitle_rate);
        MenuItem subtitleUpload = menu.findItem(R.id.subtitle_upload);
        this.helper.enableMenuItem(subtitleSearch, numChecked > 0);
        if (subtitleRate != null) {
            this.helper.enableMenuItem(subtitleRate, numEntriesHasAssociatedSubtitleFiles > 0);
        }
        if (subtitleUpload != null) {
            this.helper.enableMenuItem(subtitleUpload, numEntriesHasAssociatedSubtitleFiles > 0);
        }
        MenuItem propertyItem = menu.findItem(R.id.property);
        if (propertyItem != null) {
            this.helper.enableMenuItem(propertyItem, numChecked > 0);
        }
    }

    @SuppressLint({"InflateParams"})
    private void hide(final List<DirectoryEntry> entries) {
        AlertDialog dlg = new AlertDialog.Builder(this.container).setTitle(R.string.hide).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.list.MediaListFragment.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                for (DirectoryEntry entry : entries) {
                    P.scanRoots.put(entry.file.base, false);
                }
                P.setScanRoots(P.scanRoots);
            }
        }).create();
        View v = dlg.getLayoutInflater().inflate(R.layout.hide_confirm, (ViewGroup) null);
        TextView tv = (TextView) v.findViewById(R.id.content);
        L.sb.setLength(0);
        for (DirectoryEntry entry : entries) {
            if (L.sb.length() > 0) {
                L.sb.append('\n');
            }
            L.sb.append(entry.file.path);
        }
        tv.setText(L.sb.toString());
        TextView messageView = (TextView) v.findViewById(R.id.message2);
        L.sb.setLength(0);
        L.localizeSettingsPath(R.string.inquire_hide_folder_aux, L.sb);
        messageView.setText(L.sb.toString());
        dlg.setView(v);
        dlg.setCanceledOnTouchOutside(true);
        DialogRegistry dialogRegistry = DialogRegistry.registryOf(this.container);
        if (dialogRegistry != null) {
            dlg.setOnDismissListener(dialogRegistry);
            dialogRegistry.register(dlg);
        }
        dlg.show();
    }

    private void rebuildThumbnails(List<Entry> entries) {
        Uri[] items;
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            for (Entry entry : entries) {
                for (Uri uri : entry.getItems()) {
                    if (MediaUtils.isFileUri(uri)) {
                        File file = new File(uri.getPath());
                        mdb.deleteThumbnail(file);
                    }
                }
                entry.refreshThumb();
            }
            mdb.release();
        } catch (SQLiteException e) {
            Log.e(TAG, "", e);
        }
    }

    private void selectAll(boolean check) {
        int numItems = this._listView.getCount();
        for (int i = numItems - 1; i >= 0; i--) {
            this._listView.setItemChecked(i, check);
        }
        updateActionMenu();
    }

    private void showMultiPropertyDialog(Activity context, List<Entry> entries) {
        MultiEntryPropertyDialog dlg = new MultiEntryPropertyDialog(context, entries);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setButton(-1, context.getString(17039370), (DialogInterface.OnClickListener) null);
        DialogRegistry dialogRegistry = DialogRegistry.registryOf(context);
        if (dialogRegistry != null) {
            dlg.setOnDismissListener(dialogRegistry);
            dialogRegistry.register(dlg);
        }
        dlg.show();
    }

    @Override // android.support.v7.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menu) {
        onActionItemClicked(menu.getItemId());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onActionItemClicked(int id) {
        if (!this.container.isFinishing() && this._actionMode != null) {
            if (id == R.id.all) {
                selectAll(getNumCheckedItems() < this._listView.getCount());
                return;
            }
            List<Entry> checked = getCheckedItems();
            int numChecked = checked.size();
            if (id == R.id.play) {
                play((Entry[]) checked.toArray(new Entry[numChecked]), (byte) 0);
            } else if (id == R.id.play_hw) {
                play((Entry[]) checked.toArray(new Entry[numChecked]), (byte) 1);
            } else if (id == R.id.play_omx) {
                play((Entry[]) checked.toArray(new Entry[numChecked]), (byte) 4);
            } else if (id == R.id.play_sw) {
                play((Entry[]) checked.toArray(new Entry[numChecked]), (byte) 2);
            } else if (id == R.id.mark_as) {
                AlertDialog.Builder b = new AlertDialog.Builder(this.container).setSingleChoiceItems(R.array.mark_as_entries, -1, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.list.MediaListFragment.7
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Uri[] items;
                        Resources res = MediaListFragment.this.container.getResources();
                        List<Entry> checked2 = MediaListFragment.this.getCheckedItems();
                        try {
                            MediaDatabase mdb = MediaDatabase.getInstance();
                            SQLiteDatabase db = mdb.db;
                            db.beginTransaction();
                            ContentValues values = new ContentValues(3);
                            try {
                                if (which == res.getInteger(R.integer.index_mark_last_played)) {
                                    values.put("LastWatchTime", Long.valueOf(System.currentTimeMillis()));
                                } else if (which == res.getInteger(R.integer.index_mark_new)) {
                                    values.put("FileTimeOverriden", Long.valueOf(System.currentTimeMillis()));
                                    values.putNull("LastWatchTime");
                                    values.putNull("FinishTime");
                                } else if (which == res.getInteger(R.integer.index_mark_finished)) {
                                    values.put("FinishTime", Long.valueOf(System.currentTimeMillis()));
                                } else {
                                    values.put("FileTimeOverriden", (Integer) 1);
                                    values.putNull("LastWatchTime");
                                    values.putNull("FinishTime");
                                }
                                for (Entry entry : checked2) {
                                    if ((entry.features & 64) != 0) {
                                        for (Uri uri : entry.getItems()) {
                                            mdb.upsertUri(uri, values);
                                        }
                                    }
                                }
                                db.setTransactionSuccessful();
                                mdb.release();
                            } finally {
                                db.endTransaction();
                            }
                        } catch (SQLiteException e) {
                            Log.e(MediaListFragment.TAG, "", e);
                        }
                        dialog.dismiss();
                    }
                }).setTitle(R.string.mark_as_dialog_box_title);
                this.container.showDialog((ActivityMediaList) b.create());
            } else if (id == R.id.delete) {
                this._builder.deleteUi((Entry[]) checked.toArray(new Entry[numChecked]));
            } else if (id == R.id.rename) {
                if (numChecked > 0) {
                    this._builder.renameUi(checked.get(0));
                }
            } else if (id == R.id.hide) {
                ArrayList<DirectoryEntry> dirEntries = new ArrayList<>();
                for (Entry entry : checked) {
                    if (entry instanceof DirectoryEntry) {
                        dirEntries.add((DirectoryEntry) entry);
                    }
                }
                if (dirEntries.size() > 0) {
                    hide(dirEntries);
                }
            } else if (id == R.id.rebuild_thumbnail) {
                rebuildThumbnails(checked);
            } else if (id == R.id.property) {
                if (numChecked == 1) {
                    checked.get(0).showPropertyDialog();
                } else {
                    showMultiPropertyDialog(this.container, checked);
                }
            } else if (id == R.id.share) {
                if (numChecked > 0) {
                    shareItems(this.container, checked);
                }
            } else if (id == R.id.subtitle_search) {
                if (numChecked > 0) {
                    searchSubtitles(checked);
                }
            } else if (id == R.id.subtitle_rate) {
                if (numChecked > 0) {
                    rateSubtitles(checked);
                }
            } else if (id == R.id.subtitle_upload && numChecked > 0) {
                uploadSubtitles(checked);
            }
        }
    }

    private static void shareItems(Activity activity, Collection<Entry> entries) {
        String mime;
        Intent intent = new Intent();
        ArrayList<Uri> uris = new ArrayList<>();
        int numVideo = 0;
        int numAudio = 0;
        int numEntries = entries.size();
        for (Entry entry : entries) {
            switch (entry.type(uris)) {
                case 1:
                    numAudio++;
                    break;
                case 2:
                    numVideo++;
                    break;
            }
        }
        int numUris = uris.size();
        if (numUris > 0) {
            if (numAudio == numEntries) {
                mime = "audio/*";
            } else if (numVideo == numEntries) {
                mime = "video/*";
            } else {
                mime = "*/*";
            }
            intent.setType(mime);
            if (numUris == 1) {
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.STREAM", uris.get(0));
            } else {
                intent.setAction("android.intent.action.SEND_MULTIPLE");
                intent.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
            }
            Intent chooser = Intent.createChooser(intent, activity.getString(R.string.share));
            chooser.addFlags(268435456);
            try {
                activity.startActivity(chooser);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    private SubtitleServiceManager getSubtitleServiceManager() {
        if (this._ssm == null) {
            ActivityVPBase activity = (ActivityVPBase) getActivity();
            this._ssm = new SubtitleServiceManager(activity, 93);
        }
        return this._ssm;
    }

    private void searchSubtitles(Collection<Entry> entries) {
        SubtitleServiceManager ssm = getSubtitleServiceManager();
        ssm.cancel();
        Collection<Media> medias = getMediasFrom(entries);
        if (medias.size() > 0) {
            ssm.search((Media[]) medias.toArray(new Media[medias.size()]));
        }
    }

    private void rateSubtitles(Collection<Entry> entries) {
        SubtitleServiceManager ssm = getSubtitleServiceManager();
        ssm.cancel();
        Collection<SubtitleServiceManager.MediaSubtitle> subs = getMediaSubtitlesFrom(entries);
        if (subs.size() > 0) {
            ssm.rate((SubtitleServiceManager.MediaSubtitle[]) subs.toArray(new SubtitleServiceManager.MediaSubtitle[subs.size()]));
        }
    }

    private void uploadSubtitles(Collection<Entry> entries) {
        SubtitleServiceManager ssm = getSubtitleServiceManager();
        ssm.cancel();
        Collection<SubtitleServiceManager.MediaSubtitle> subs = getMediaSubtitlesFrom(entries);
        if (subs.size() > 0) {
            ssm.upload((SubtitleServiceManager.MediaSubtitle[]) subs.toArray(new SubtitleServiceManager.MediaSubtitle[subs.size()]), false);
        }
    }

    @Nullable
    private static Media getMediaFromUri(Uri uri) {
        if (MediaUtils.isFileUri(uri)) {
            File file = new File(uri.getPath());
            String name = file.getName();
            return new Media(uri, null, name, file, name, null, 0, 0);
        }
        return null;
    }

    private static Collection<Media> getMediasFrom(Collection<Entry> entries) {
        HashSet<Media> medias = new HashSet<>();
        ArrayList<Uri> uris = null;
        for (Entry entry : entries) {
            if (entry instanceof FileEntry) {
                medias.add(((FileEntry) entry).getSubtitleSearchMedia());
            } else {
                if (uris == null) {
                    uris = new ArrayList<>();
                } else {
                    uris.clear();
                }
                entry.type(uris);
                Iterator<Uri> it = uris.iterator();
                while (it.hasNext()) {
                    Uri uri = it.next();
                    Media media = getMediaFromUri(uri);
                    if (media != null) {
                        medias.add(media);
                    }
                }
            }
        }
        return medias;
    }

    private static Collection<SubtitleServiceManager.MediaSubtitle> getMediaSubtitlesFrom(Collection<Entry> entries) {
        File[] fileArr;
        ArrayList<SubtitleServiceManager.MediaSubtitle> subs = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry instanceof FileEntry) {
                FileEntry fileEntry = (FileEntry) entry;
                if (fileEntry.subFiles != null) {
                    Media media = fileEntry.getSubtitleSearchMedia();
                    for (File file : fileEntry.subFiles) {
                        subs.add(new SubtitleServiceManager.MediaSubtitle(media, new FileSubtitle(file)));
                    }
                }
            }
        }
        return subs;
    }

    @Override // android.support.v7.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode actionMode) {
        if (this._listView != null) {
            this._listView.clearChoices();
            this._listView.setChoiceMode(0);
        }
        this._actionMode = null;
        this.container.removeSplitToolbar();
    }

    /* loaded from: classes2.dex */
    private class Adapter extends BaseAdapter implements SectionIndexer {
        private final int _colorAccent;

        Adapter() {
            TypedArray a = MediaListFragment.this.container.obtainStyledAttributes(R.styleable.MediaListAdapter);
            this._colorAccent = a.getColor(R.styleable.MediaListAdapter_colorAccent, 0);
            a.recycle();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 3;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            int index = MediaListFragment.this.positionToIndex(position);
            if (index >= 0) {
                if (index < MediaListFragment.this._entries.length) {
                    Entry entry = MediaListFragment.this._entries[index];
                    int layoutId = entry.getLayoutResourceId();
                    if (layoutId == R.layout.list_row_listable) {
                        return 1;
                    }
                    return 2;
                }
                return super.getItemViewType(position);
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View view, ViewGroup parent) {
            int index = MediaListFragment.this.positionToIndex(position);
            if (index < 0) {
                int group = MediaListFragment.this.getGroup(position);
                if (group >= 0) {
                    return getGroupView(group, view, parent);
                }
            } else if (index < MediaListFragment.this._entries.length) {
                return getItemView(MediaListFragment.this._entries[index], view, parent, position);
            }
            Log.e(MediaListFragment.TAG, "View requested for invalid position. group-count:" + MediaListFragment.this._builder.getGroupCount() + ", entries:" + MediaListFragment.this._entries.length + ", position:" + position + ", index:" + index);
            return view;
        }

        private View getGroupView(int group, View view, ViewGroup parent) {
            if (view == null) {
                view = MediaListFragment.this.helper.layoutInflater.inflate(R.layout.list_row_group_header, parent, false);
            }
            ((TextView) view).setText(MediaListFragment.this._builder.getGroupText(group));
            return view;
        }

        private View getItemView(Entry entry, View view, ViewGroup parent, int position) {
            int layoutResId = entry.getLayoutResourceId();
            if (view == null) {
                view = MediaListFragment.this.helper.layoutInflater.inflate(layoutResId, parent, false);
            }
            view.setTag(entry);
            entry.render(view);
            if (view instanceof MediaListItemLayout) {
                ((MediaListItemLayout) view).updatePadding();
            }
            if (MediaListFragment.this._actionMode == null && (view instanceof Checkable)) {
                ((Checkable) view).setChecked(false);
            }
            if (this._colorAccent != 0) {
                L.colorizeVLinedBackground(view, this._colorAccent);
            }
            return view;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return MediaListFragment.this._builder.getGroupCount() + MediaListFragment.this._entries.length;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return (MediaListFragment.this._builder.features & 2048) == 0;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return MediaListFragment.this.positionToIndex(position) >= 0;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return false;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean isEmpty() {
            return MediaListFragment.this._entries.length == 0;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver observer) {
            MediaListFragment.this._listViewObserver.registerObserver(observer);
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            MediaListFragment.this._listViewObserver.unregisterObserver(observer);
        }

        @Override // android.widget.SectionIndexer
        public int getPositionForSection(int section) {
            return 0;
        }

        @Override // android.widget.SectionIndexer
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override // android.widget.SectionIndexer
        public Object[] getSections() {
            return MediaListFragment._emptySection;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MultiEntryPropertyDialog extends PropertyDialog {
        public MultiEntryPropertyDialog(Context context, List<Entry> entries) {
            super(context);
            setTitle(R.string.menu_property);
            int numItems = 0;
            long totalSize = 0;
            for (Entry entry : entries) {
                numItems += entry.getItems().length;
                totalSize += entry.size;
            }
            addRow(R.string.property_item_contains, StringUtils.getQuantityString_s(P.isAudioPlayer ? R.plurals.count_media : R.plurals.count_video, numItems, Integer.valueOf(numItems)));
            addRow(R.string.detail_video_total_size, NumericUtils.formatShortAndLongSize(context, totalSize));
        }
    }
}
