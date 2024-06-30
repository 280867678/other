package com.mxtech.videoplayer.list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.app.DialogUtils;
import com.mxtech.media.MediaUtils;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.MediaScanner;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class LocalBuilder extends Builder {
    public static final String TAG = "MX.List.Builder.Local";

    static /* synthetic */ int access$000() {
        return getAuxFileTypesForDeletion();
    }

    public LocalBuilder(ActivityMediaList context, MediaListFragment content, int features) {
        super(context, content, features);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Map<String, Entry> createFilesEntries(MediaFile[] files) {
        Map<File, String> sharedSubs;
        Map<String, Entry> entries = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Set<File> subs = new HashSet<>();
        for (MediaFile file : files) {
            if (file.state == 16) {
                entries.put(file.path, this.content.newEntry(file));
            }
        }
        if (this.content.helper.sdir != null) {
            sharedSubs = this.content.helper.sdir.list();
        } else {
            sharedSubs = null;
        }
        for (Entry entry : entries.values()) {
            if (entry instanceof FileEntry) {
                FileEntry fileEntry = (FileEntry) entry;
                subs.clear();
                for (MediaFile file2 : files) {
                    if (file2.state == 18) {
                        if (SubtitleFactory.isSubtitleOf(fileEntry.name, file2.name(), false)) {
                            subs.add(file2.base);
                        }
                    } else if (file2.state == 17 && fileEntry.coverFile == null && FileUtils.equalsNoExtension(fileEntry.name, file2.name(), true)) {
                        fileEntry.coverFile = file2.base;
                    }
                }
                if (sharedSubs != null) {
                    for (Map.Entry<File, String> sub : sharedSubs.entrySet()) {
                        if (SubtitleFactory.isSubtitleOf(fileEntry.name, sub.getValue(), false)) {
                            subs.add(sub.getKey());
                        }
                    }
                }
                fileEntry.subFiles = (File[]) subs.toArray(new File[subs.size()]);
            }
        }
        return entries;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00c8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int updateDirectoryEntry(MediaDatabase mdb, DirectoryEntry entry, String dir, MediaFile[] files, int from, long currentTime) throws SQLiteException {
        Iterator<MediaFile> it;
        long lastWatchTime;
        long finishTime;
        long fileTime;
        SortedMap<String, MediaFile> nameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        int i = from;
        while (i < files.length) {
            MediaFile file = files[i];
            if (file.isDirectory()) {
                break;
            }
            if (file.state == 16) {
                nameMap.put(file.name(), file);
                entry.size += file.length();
            }
            i++;
        }
        entry.count += nameMap.size();
        int id = mdb.getDirectoryId(dir);
        Cursor c = mdb.db.rawQuery("SELECT FileName, LastWatchTime, FinishTime, FileTimeOverriden FROM VideoFile WHERE Directory=" + id, null);
        try {
            if (c.moveToFirst()) {
                do {
                    String name = c.getString(0);
                    MediaFile file2 = nameMap.get(name);
                    if (file2 != null) {
                        if (c.isNull(1)) {
                            lastWatchTime = -1;
                        } else {
                            lastWatchTime = c.getLong(1);
                        }
                        if (c.isNull(2)) {
                            finishTime = -1;
                        } else {
                            finishTime = c.getLong(2);
                        }
                        if (c.isNull(3)) {
                            fileTime = file2.lastModified();
                        } else {
                            fileTime = c.getLong(3);
                        }
                        if (finishTime < 0) {
                            if (lastWatchTime < 0 && currentTime < P.newTaggedPeriodMs + fileTime) {
                                entry.numNew++;
                            }
                        } else {
                            entry.numFinished++;
                        }
                        if (entry.lastWatchTime < lastWatchTime) {
                            entry.lastWatchTime = lastWatchTime;
                        }
                        updateLastMedia(file2.uri(), lastWatchTime, finishTime);
                        nameMap.remove(name);
                    }
                } while (c.moveToNext());
                c.close();
                it = nameMap.values().iterator();
                while (it.hasNext()) {
                    if (currentTime < it.next().lastModified() + P.newTaggedPeriodMs) {
                        entry.numNew++;
                    }
                }
                return i;
            }
            c.close();
            it = nameMap.values().iterator();
            while (it.hasNext()) {
            }
            return i;
        } catch (Throwable th) {
            c.close();
            throw th;
        }
    }

    protected final int updateSubDirectories(MediaDatabase mdb, MediaFile[] files, int from, String root, DirectoryEntry entry, long currentTime) {
        int i = from;
        String directAscendant = null;
        while (i < files.length) {
            MediaFile file = files[i];
            if (!FileUtils.isLocatedIn(file.path, root)) {
                break;
            } else if (file.state == 16) {
                String thisDirectAscendant = FileUtils.getDirectAscendantPath(root, file.path);
                if (!StringUtils.equalsIgnoreCase(directAscendant, thisDirectAscendant)) {
                    directAscendant = thisDirectAscendant;
                    entry.numDirectSubDirectories++;
                }
                i = updateDirectoryEntry(mdb, entry, file.base.getParent(), files, i, currentTime);
            } else {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void createDirectoryEntries(MediaDatabase mdb, Map<String, Entry> entries, String root, MediaFile[] files) throws SQLiteException {
        String directAscendant;
        DirectoryEntry entry;
        long currentTime = System.currentTimeMillis();
        boolean hierarchical = (this.features & 512) != 0;
        int i = 0;
        ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
        while (i < files.length) {
            MediaFile file = files[i];
            if (file.isDirectory() && (directAscendant = FileUtils.getDirectAscendantPath(root, file.path)) != null) {
                if (directAscendant != file.path) {
                    entry = this.content.newDirectoryEntry(mdir.newFile(directAscendant, null, 34), hierarchical);
                } else {
                    entry = this.content.newDirectoryEntry(file, hierarchical);
                    i = updateDirectoryEntry(mdb, entry, file.path, files, i + 1, currentTime);
                    entry.numDirectItems = entry.count;
                }
                i = updateSubDirectories(mdb, files, i, directAscendant, entry, currentTime);
                if (entry.count > 0) {
                    entries.put(directAscendant, entry);
                }
            } else {
                i++;
            }
        }
    }

    private void getEntryItems(Entry entry, Set<File> items, int fileTypes) {
        if (entry.file != null) {
            items.add(entry.file.base);
            File[] associated = entry.getAssociatedFiles(fileTypes);
            if (associated != null) {
                items.addAll(Arrays.asList(associated));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanDirectories(Collection<File> mediaFiles, Collection<File> auxFiles, Entry[] entries, int auxFileTypes) {
        int i = 0;
        if ((this.features & 512) != 0) {
            int length = entries.length;
            while (i < length) {
                Entry entry = entries[i];
                if (entry instanceof DirectoryEntry) {
                    MediaScanner.getMediaFilesRecursive(entry.file.base, mediaFiles, auxFiles, auxFileTypes, true);
                }
                i++;
            }
            return;
        }
        int length2 = entries.length;
        while (i < length2) {
            Entry entry2 = entries[i];
            if (entry2 instanceof DirectoryEntry) {
                MediaScanner.getMediaFiles(entry2.file.base, mediaFiles, auxFiles, auxFileTypes, true);
            }
            i++;
        }
    }

    /* loaded from: classes2.dex */
    private class DeleteListener implements DialogInterface.OnClickListener, ActivityVPBase.FileOperationSink {
        private final Entry[] _entries;
        private final Collection<File> _files;

        public DeleteListener(Entry[] entries, Collection<File> items) {
            this._entries = entries;
            this._files = items;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dlg, int what) {
            if (!LocalBuilder.this.context.isFinishing()) {
                Iterator<File> it = this._files.iterator();
                while (it.hasNext()) {
                    if (!it.next().isFile()) {
                        it.remove();
                    }
                }
                LocalBuilder.this.scanDirectories(this._files, this._files, this._entries, LocalBuilder.access$000());
                Log.v(LocalBuilder.TAG, "Deleting " + this._files.size() + " files + updating database.");
                MediaDatabase mdb = MediaDatabase.getInstance();
                int failCount = 0;
                int triedCount = 0;
                try {
                    mdb.db.beginTransaction();
                    try {
                        for (File file : this._files) {
                            if (file.exists() && !file.isDirectory()) {
                                Log.v(LocalBuilder.TAG, "Deleting " + file.getPath());
                                triedCount++;
                                if (!MediaUtils.delete(mdb, 0, file)) {
                                    Log.v(LocalBuilder.TAG, file.getPath() + " was NOT deleted. (exists:" + file.exists() + " canRead:" + file.canRead() + " canWrite:" + file.canWrite() + ")");
                                    failCount++;
                                }
                            }
                        }
                        mdb.clearUnreferencedDirectories(false);
                        mdb.db.setTransactionSuccessful();
                        mdb.db.endTransaction();
                        LocalBuilder.this.content.clearChoices();
                        if (failCount > 0) {
                            LocalBuilder.this.context.handleFileWritingFailure(null, triedCount, failCount, this);
                        }
                    } catch (Throwable th) {
                        mdb.db.endTransaction();
                        throw th;
                    }
                } catch (SQLiteException e) {
                    Log.e(LocalBuilder.TAG, "", e);
                } finally {
                    mdb.release();
                }
            }
        }

        @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
        public void onFileOperationRetry() {
            LocalBuilder.this.content.helper.context.defaultFileOperationRetry();
        }

        @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
        public void onFileOperationFailed(int numTotal, int numFailed) {
            L.alertFileWriteFailureMessage(LocalBuilder.this.context, App.getFileDeletionFailureMessage(numTotal, numFailed));
        }
    }

    private static int getAuxFileTypesForDeletion() {
        return P.deleteSubtitleFilesTogether ? 3 : 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    @SuppressLint({"InflateParams"})
    public final void deleteUi(Entry[] entries) {
        CharSequence msg;
        if (!this.context.isFinishing()) {
            Set<File> files = new HashSet<>();
            int numDirEntries = 0;
            int numFileEntries = 0;
            for (Entry entry : entries) {
                if (entry instanceof DirectoryEntry) {
                    numDirEntries++;
                } else {
                    numFileEntries++;
                }
                getEntryItems(entry, files, getAuxFileTypesForDeletion());
            }
            L.sb.setLength(0);
            for (File item : files) {
                if (L.sb.length() > 0) {
                    L.sb.append(", ");
                }
                L.sb.append(item.getName());
            }
            AlertDialog dlg = new AlertDialog.Builder(this.context).setTitle(R.string.menu_delete).setPositiveButton(17039370, new DeleteListener(entries, files)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            Resources res = this.context.getResources();
            if (numDirEntries == 0) {
                msg = StringUtils.getString_s(R.string.edit_inquire_delete, res.getQuantityText(R.plurals.files, entries.length));
            } else {
                msg = numFileEntries > 0 ? StringUtils.getString_s(R.string.edit_inquire_delete, res.getQuantityText(R.plurals.items, entries.length)) : StringUtils.capitalize(StringUtils.getString_s(R.string.edit_inquire_delete_folder, P.mediaText, res.getQuantityString(R.plurals.folders, entries.length)));
            }
            View v = dlg.getLayoutInflater().inflate(R.layout.delete_confirm, (ViewGroup) null);
            ((TextView) v.findViewById(R.id.message)).setText(msg);
            ((TextView) v.findViewById(R.id.content)).setText(L.sb.toString());
            dlg.setView(v);
            dlg.setCanceledOnTouchOutside(true);
            DialogRegistry dialogRegistry = DialogRegistry.registryOf(this.context);
            if (dialogRegistry != null) {
                dlg.setOnDismissListener(dialogRegistry);
                dialogRegistry.register(dlg);
            }
            dlg.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Builder
    public final void renameUi(final Entry entry) {
        if (!this.context.isFinishing() && entry.file != null) {
            String oldName = entry.file.name();
            if ((entry.features & 32) != 0) {
                oldName = FileUtils.stripExtension(oldName);
            }
            DialogUtils.showSimpleInputDialog(this.context, oldName, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.list.LocalBuilder.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    if (!LocalBuilder.this.context.isFinishing()) {
                        String newName = StringUtils.trimRight(((TextView) ((Dialog) dialog).findViewById(16908291)).getText().toString().trim(), '.');
                        if (entry.renameTo(newName)) {
                            LocalBuilder.this.content.clearChoices();
                        }
                    }
                }
            }, R.string.edit_rename_to);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final CharSequence getRootEmptyMessage() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append((CharSequence) this.content.getEmptyStringWithStateMessage(P.isAudioPlayer ? R.string.no_media_at_all : R.string.no_videos_at_all));
        ssb.append((CharSequence) "\n\n");
        TypedArray a = this.context.obtainStyledAttributes(new int[]{R.attr.listSecondaryLargeTextAppearance});
        int appearance = a.getResourceId(0, 0);
        a.recycle();
        int startIndex = ssb.length();
        L.localizeSettingsPath(R.string.no_videos_at_all_more, ssb);
        if (appearance != 0) {
            ssb.setSpan(new TextAppearanceSpan(this.context, appearance), startIndex, ssb.length(), 33);
        }
        return ssb;
    }
}
