package com.mxtech.videoplayer.list;

import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogUtils;
import com.mxtech.media.MediaUtils;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.ActivityVPBase;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.ImmutableMediaDirectory;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import java.io.File;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class DirectoryEntry extends ListableEntry implements ActivityVPBase.FileOperationSink {
    private final boolean _primaryExternalStorage;
    int numDirectItems;
    int numDirectSubDirectories;

    public DirectoryEntry(MediaFile file, MediaListFragment content, boolean hierarchical) {
        super(file.uri(), file, content, (hierarchical ? 512 : 0) | 1882259532);
        this._primaryExternalStorage = file.base.equals(content.helper.primaryExternalStorage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public String getTitle() {
        return this._primaryExternalStorage ? this.content.helper.res.getString(R.string.internal_memory) : MediaUtils.capitalizeWithDictionary(this.file.name(), this.content.helper.titleSb);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.ListableEntry
    public CharSequence getContentsText() {
        StringBuilder sb = new StringBuilder();
        if (this.numDirectItems > 0) {
            if (P.isAudioPlayer) {
                sb.append(StringUtils.getQuantityString_s(R.plurals.count_media, this.numDirectItems, Integer.valueOf(this.numDirectItems)));
            } else {
                sb.append(StringUtils.getQuantityString_s(R.plurals.count_video, this.numDirectItems, Integer.valueOf(this.numDirectItems)));
            }
        }
        if (this.numDirectSubDirectories > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(StringUtils.getQuantityString_s(R.plurals.count_folders, this.numDirectSubDirectories, Integer.valueOf(this.numDirectSubDirectories)));
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getLayoutResourceId() {
        return R.layout.list_row_listable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void render(View view) {
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView countView = (TextView) view.findViewById(R.id.count);
        TextView infoNormalView = (TextView) view.findViewById(R.id.info_normal);
        TextView infoOppositeView = (TextView) view.findViewById(R.id.info_opposite);
        SpannableStringBuilder b = new SpannableStringBuilder(this.title);
        if ((this.displayType & 2) != 0) {
            this.content.helper.appendTag(b, "New", R.attr.tagNewText, R.attr.tagNew, false);
        }
        this.content.helper.stylize(titleView, this.displayType, iconView, countView, infoNormalView, infoOppositeView);
        titleView.setText(b, TextView.BufferType.SPANNABLE);
        countView.setText(getContentsText(), TextView.BufferType.NORMAL);
        if ((P.list_fields & 8) != 0 && !this.content.isInHomogenousLocation()) {
            infoNormalView.setText(this.location, TextView.BufferType.NORMAL);
            infoNormalView.setVisibility(0);
        } else {
            infoNormalView.setVisibility(8);
        }
        if ((P.list_fields & 2) != 0) {
            infoOppositeView.setVisibility(0);
            infoOppositeView.setText(Formatter.formatShortFileSize(this.content.helper.context, this.size), TextView.BufferType.NORMAL);
            return;
        }
        infoOppositeView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public File[] getAssociatedFiles(int fileTypes) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public Uri[] getItems() {
        ImmutableMediaDirectory mdir = L.directoryService.getMediaDirectory();
        int flags = (this.features & 512) != 0 ? 33 : 1;
        MediaFile[] files = mdir.list(this.file.path, flags);
        int numFiles = files.length;
        Uri[] uris = new Uri[numFiles];
        for (int i = 0; i < numFiles; i++) {
            uris[i] = files[i].uri();
        }
        return uris;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public boolean renameTo(String newName) {
        String oldName = this.file.name();
        if (newName.length() <= 0 || newName.equalsIgnoreCase(oldName)) {
            return false;
        }
        ActivityMediaList context = this.content.helper.context;
        File parent = this.file.base.getParentFile();
        if (parent != null) {
            File newDir = new File(parent, newName);
            if (newDir.exists()) {
                DialogUtils.alert(context, context.getString(R.string.edit_error_rename_folder_fail) + ' ' + context.getString(R.string.error_rename_duplicates), context.getString(R.string.edit_rename_to));
                return false;
            } else if (FileUtils.renameTo(context, this.file.base, newDir)) {
                this.content.clearChoices();
                MediaUtils.renameDirectoryInStorages(this.file.base, newDir);
                return true;
            } else {
                context.handleFileWritingFailure(this.file.base, 1, 1, this);
                return false;
            }
        }
        return false;
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
    public void onFileOperationRetry() {
        this.content.helper.context.defaultFileOperationRetry();
    }

    @Override // com.mxtech.videoplayer.ActivityVPBase.FileOperationSink
    public void onFileOperationFailed(int numTotal, int numFailed) {
        L.alertFileWriteFailureMessage(this.content.helper.context, R.string.edit_error_rename_folder_fail, R.string.edit_rename_to);
    }
}
