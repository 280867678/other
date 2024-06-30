package com.mxtech.videoplayer.list;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.DeviceUtils;
import com.mxtech.FileUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.media.FFPlayer;
import com.mxtech.media.FFReader;
import com.mxtech.media.IMediaInfoAux;
import com.mxtech.media.MediaUtils;
import com.mxtech.media.service.IFFService;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.IMediaRuntimeInfo;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.MediaLoader;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.ThumbDrawable;
import com.mxtech.videoplayer.ThumbShaper;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.service.Media;
import com.mxtech.videoplayer.widget.MediaInfoDialog;
import java.io.File;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class FileEntry extends PlayableEntry implements ThumbShaper.Receiver, MediaLoader.Receiver, IMediaRuntimeInfo {
    private static final int[] DRAWABLE_ATTRS = {R.attr.tagSrt, R.attr.tagSsa, R.attr.tagSub, R.attr.tagSmi, R.attr.tagTxt, R.attr.tagSub, R.attr.tagMpl, R.attr.tagVtt, R.attr.tagPsb, R.attr.tagPjs};
    private static final int[] INBOUND_DRAWABLE_ATTRS = {R.attr.tagInboundDvd, R.attr.tagInboundDvb, R.attr.tagInboundTxt, R.attr.tagInboundXsub, R.attr.tagInboundSsa, R.attr.tagInboundTxt, R.attr.tagInboundPgs, R.attr.tagInboundTel, R.attr.tagInboundSrt, -1, -1, -1, -1, -1, -1, -1, R.attr.tagInboundSrt, R.attr.tagInboundVtt, -1, -1, -1, R.attr.tagInboundSsa, -1, -1};
    static final String TAG = "MX.List.Entry/File";
    byte audioTrackCount;
    File coverFile;
    boolean infoRead;
    int loadings;
    boolean noThumbnail;
    File[] subFiles;
    byte subtitleTrackCount;
    int subtitleTrackTypes;
    int thumbShapingCount;
    byte videoTrackCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileEntry(MediaFile file, MediaListFragment content) {
        super(file.uri(), file, content, 16711724);
        this.size = file.length();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getDisplayType(long unfinishedLastMediaWatchTime, long currentTime) {
        int type = this.lastWatchTime == unfinishedLastMediaWatchTime ? 1 : 0;
        if (this.finishTime < 0) {
            long fileTime = this.fileTimeOverriden != 0 ? this.fileTimeOverriden : this.file.lastModified();
            if (this.lastWatchTime < 0 && currentTime < P.newTaggedPeriodMs + fileTime) {
                return type | 2;
            }
            return type;
        }
        return type | 4;
    }

    @Override // com.mxtech.videoplayer.IMediaRuntimeInfo
    public Uri getExternalCoverArt() {
        if (this.coverFile != null) {
            return Uri.fromFile(this.coverFile);
        }
        return null;
    }

    @Override // com.mxtech.videoplayer.list.PlayableEntry, com.mxtech.videoplayer.list.Entry
    int type(@Nullable List<Uri> uris) {
        uris.add(this.uri);
        if (this.videoTrackCount > 0) {
            return 2;
        }
        if (this.audioTrackCount > 0) {
            return 1;
        }
        return super.type(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void onPendingJobsCanceled() {
        super.onPendingJobsCanceled();
        this.loadings = 0;
        this.thumbShapingCount = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public int getLayoutResourceId() {
        return R.layout.list_row_media;
    }

    private void render() {
        View row = this.content.getRenderingTarget(this);
        if (row != null) {
            render(row);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void render(View view) {
        ThumbDrawable thumbDrawable;
        boolean needThumbnail;
        if ((P.list_fields & 1) != 0) {
            thumbDrawable = L.thumbCache.get(this.uri, this.coverFile);
            needThumbnail = thumbDrawable == null || !thumbDrawable.hasThumb;
        } else {
            thumbDrawable = null;
            needThumbnail = false;
        }
        render(view, thumbDrawable);
        if (this.content.started) {
            int loads = 0;
            if (!this.infoRead) {
                loads = 0 | 1;
            }
            if (needThumbnail) {
                if (this.coverFile != null) {
                    loads |= 4;
                }
                if (!this.noThumbnail) {
                    loads |= 2;
                }
            }
            if (loads != 0) {
                int loads2 = loads & (this.loadings ^ (-1));
                if (loads2 != 0) {
                    Bitmap defaultThumb = null;
                    if ((P.list_fields & 1) != 0) {
                        if (thumbDrawable != null && thumbDrawable.source != null) {
                            defaultThumb = thumbDrawable.source;
                        } else if ((loads2 & 6) == 0) {
                            if (this.coverFile != null) {
                                loads2 |= 4;
                            }
                            if (!this.noThumbnail) {
                                loads2 |= 2;
                            }
                        }
                    }
                    requestInfo(loads2, defaultThumb, false);
                }
            } else if ((P.list_fields & 1) == 0 || P.list_duration_display != 2 || this.durationMs <= 0) {
            } else {
                if (thumbDrawable == null || !thumbDrawable.hasDuration) {
                    requestShape(thumbDrawable != null ? thumbDrawable.source : null);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean preloadMediaInfo() {
        ThumbDrawable thumbDrawable;
        boolean needThumbnail = false;
        if (!this.infoRead && this.loadings == 0 && this.thumbShapingCount == 0) {
            if ((P.list_fields & 1) != 0) {
                thumbDrawable = L.thumbCache.get(this.uri, this.coverFile);
                if (thumbDrawable == null || !thumbDrawable.hasThumb) {
                    needThumbnail = true;
                }
            } else {
                thumbDrawable = null;
                needThumbnail = false;
            }
            int loads = 1;
            if (needThumbnail) {
                if (this.coverFile != null) {
                    loads = 1 | 4;
                }
                if (!this.noThumbnail) {
                    loads |= 2;
                }
            }
            Bitmap defaultThumb = null;
            if ((P.list_fields & 1) != 0) {
                if (thumbDrawable != null && thumbDrawable.source != null) {
                    defaultThumb = thumbDrawable.source;
                } else if ((loads & 6) == 0) {
                    if (this.coverFile != null) {
                        loads |= 4;
                    }
                    if (!this.noThumbnail) {
                        loads |= 2;
                    }
                }
            }
            requestInfo(loads, defaultThumb, true);
            return true;
        }
        return false;
    }

    private void requestInfo(int loads, @Nullable Bitmap defaultThumb, boolean preload) {
        if ((P.list_fields & 1) != 0 && !preload) {
            this.content.requestInfoWithThumbShaping(this, loads, this.file.base, this.coverFile, P.list_duration_display == 2 ? ListHelper.formatDuration(this.durationMs) : null, defaultThumb, false);
        } else {
            this.content.requestInfo(this, loads, this.file.base, this.coverFile, preload);
        }
        this.loadings |= loads;
        this.thumbShapingCount++;
    }

    private void requestShape(@Nullable Bitmap source) {
        this.content.helper.thumbShaper.buildParallel(source, P.list_duration_display == 2 ? ListHelper.formatDuration(this.durationMs) : null, this, this.uri, this.coverFile);
        this.thumbShapingCount++;
    }

    @Override // com.mxtech.videoplayer.ThumbShaper.Receiver
    public void onCompleted(ThumbShaper shaper) {
        if (this.thumbShapingCount > 0) {
            this.thumbShapingCount--;
        }
        render();
    }

    @Override // com.mxtech.videoplayer.MediaLoader.Receiver
    public void onCompleted(MediaLoader reader, MediaLoader.Result result) {
        if (this.thumbShapingCount > 0) {
            this.thumbShapingCount--;
        }
        this.loadings &= result.requested ^ (-1);
        if ((result.tried & 1) != 0) {
            this.infoRead = true;
            this.videoTrackCount = result.videoTrackCount;
            this.audioTrackCount = result.audioTrackCount;
            this.subtitleTrackCount = result.subtitleTrackCount;
            this.subtitleTrackTypes = result.subtitleTrackTypes;
            this.durationMs = result.duration;
            this.frameTimeNs = result.frameTime;
            this.width = result.width;
            this.height = result.height;
            this.interlaced = result.interlaced;
            if (P.list_sortings.length > 0) {
                switch (P.list_sortings[0]) {
                    case -64:
                    case 64:
                        if (this.durationMs > 0) {
                            this.content.refresh(true);
                            break;
                        }
                        break;
                    case -32:
                    case -16:
                    case 16:
                    case 32:
                        if (this.width > 0 && this.height > 0) {
                            this.content.refresh(true);
                            break;
                        }
                        break;
                }
            }
        }
        if (result.noThumb) {
            this.noThumbnail = true;
        }
        render();
    }

    private void render(View view, @Nullable Drawable thumb) {
        String durationText;
        File[] fileArr;
        boolean grayed = ListHelper.getColorType(this.displayType) == 4;
        SpannableStringBuilder ssb = new SpannableStringBuilder(this.title);
        StringBuilder sb = new StringBuilder();
        View iconView = view.findViewById(R.id.icon);
        ImageView thumbView = (ImageView) view.findViewById(R.id.thumb);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView fileInfoView = (TextView) view.findViewById(R.id.file_info);
        TextView infoView = (TextView) view.findViewById(R.id.info);
        if (this.subtitleTrackCount > 0) {
            boolean appended = false;
            for (int i = 0; i < 24; i++) {
                if ((this.subtitleTrackTypes & (1 << i)) != 0) {
                    String name = FFPlayer.SUBTITLE_CODEC_SHORTNAMES[i];
                    int drawableAttr = INBOUND_DRAWABLE_ATTRS[i];
                    if (name != null && drawableAttr >= 0) {
                        this.content.helper.appendTag(ssb, name, R.attr.tagInboundText, drawableAttr, grayed);
                        appended = true;
                    }
                }
            }
            if (!appended) {
                this.content.helper.appendTag(ssb, "CAP", R.attr.tagInboundText, R.attr.tagInboundGeneral, grayed);
            }
        }
        if (this.subFiles != null) {
            for (File subFile : this.subFiles) {
                int extId = SubtitleFactory.getExtensionId(subFile);
                if (extId >= 0) {
                    this.content.helper.appendTag(ssb, SubtitleFactory.EXT_NAMES[extId], R.attr.tagOutboundText, DRAWABLE_ATTRS[extId], grayed);
                }
            }
        }
        if ((this.displayType & 2) != 0) {
            this.content.helper.appendTag(ssb, "New", R.attr.tagNewText, R.attr.tagNew, grayed);
        }
        if ((P.list_fields & 1) != 0) {
            if (thumb == null && (thumb = (Drawable) thumbView.getTag()) == null) {
                thumb = new ThumbDrawable(this.content.helper.res, this.content.helper.getBlankThumb(), null, false, false);
                thumbView.setTag(thumb);
            }
            thumbView.setImageDrawable(thumb);
            thumbView.setVisibility(0);
            iconView.setVisibility(8);
        } else {
            thumbView.setVisibility(8);
            this.content.helper.setIconViewImage((ImageView) iconView, this.videoTrackCount, this.audioTrackCount);
            iconView.setVisibility(0);
        }
        this.content.helper.stylize(titleView, this.displayType, iconView, fileInfoView, infoView);
        titleView.setText(ssb);
        boolean wideDisplay = DeviceUtils.getScreenWidthDp(this.content.helper.res) >= 500;
        boolean showResolution = (P.list_fields & 64) != 0 && hasResolution();
        boolean showFrameTime = (P.list_fields & 128) != 0 && this.frameTimeNs > 0;
        boolean showDate = (P.list_fields & 4) != 0;
        boolean showSize = (P.list_fields & 2) != 0;
        if (showResolution) {
            if (wideDisplay) {
                sb.append(this.width).append(" x ").append(this.height);
            } else {
                sb.append(this.height);
            }
            if (this.interlaced != null) {
                if (this.interlaced.booleanValue()) {
                    sb.append('i');
                } else {
                    sb.append('p');
                }
            }
        }
        if (showFrameTime) {
            String frameRateStr = MediaUtils.formatFrameTime(this.frameTimeNs, 1);
            if (showResolution) {
                if (wideDisplay) {
                    sb.append(" @").append(frameRateStr).append("fps");
                } else {
                    sb.append('@').append(frameRateStr);
                }
            } else {
                sb.append(frameRateStr).append(" fps");
            }
        }
        if (showDate) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(this.content.helper.formatTime(this.file.lastModified(), System.currentTimeMillis()));
        }
        if (showSize) {
            if (wideDisplay) {
                if (showDate) {
                    sb.append(", ");
                } else if (sb.length() > 0) {
                    sb.append('\n');
                }
            } else if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(Formatter.formatShortFileSize(this.content.helper.context, this.size));
        }
        if (sb.length() > 0) {
            fileInfoView.setText(sb.toString(), TextView.BufferType.NORMAL);
            fileInfoView.setVisibility(0);
            sb.setLength(0);
        } else {
            fileInfoView.setVisibility(8);
        }
        if ((P.list_fields & 32) != 0 && this.lastWatchTime > 0) {
            long now = System.currentTimeMillis();
            sb.append(this.content.helper.formatPlayedTime(this.lastWatchTime, now));
            if (this.content.helper.nextPlaytimeUpdateTimeMs != Long.MAX_VALUE) {
                this.content.scheduleRefresh(SystemClock.uptimeMillis() + (this.content.helper.nextPlaytimeUpdateTimeMs - now));
            }
        }
        if (P.list_duration_display == 3 && (durationText = ListHelper.formatDuration(this.durationMs)) != null) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(App.context.getString(R.string.detail_playtime)).append(' ').append(durationText);
        }
        if ((P.list_fields & 8) != 0 && !this.content.isInHomogenousLocation()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(this.location);
        }
        if (sb.length() > 0) {
            infoView.setText(sb);
            infoView.setVisibility(0);
            return;
        }
        infoView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public File[] getAssociatedFiles(int fileTypes) {
        File[] fileArr;
        HashSet<File> files = new HashSet<>();
        if ((fileTypes & 2) != 0 && this.coverFile != null) {
            files.add(this.coverFile);
        }
        if ((fileTypes & 1) != 0 && this.subFiles != null) {
            String directory = this.file.base.getParent();
            for (File sub : this.subFiles) {
                if (FileUtils.isDirectAscendantOf(sub.getPath(), directory)) {
                    files.add(sub);
                }
            }
        }
        return (File[]) files.toArray(new File[files.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void refreshThumb() {
        this.noThumbnail = false;
        int loads = 2;
        if (this.coverFile != null) {
            loads = 2 | 4;
        }
        int loads2 = loads & (this.loadings ^ (-1));
        if (loads2 != 0) {
            requestInfo(loads2, null, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public boolean renameTo(String newName) {
        return MediaUtils.renameMediaTo(this.content.helper.context, this.file.base, FileUtils.changeName(this.file.base, newName), this.subFiles, this.coverFile) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.mxtech.videoplayer.list.Entry
    public void showPropertyDialog() {
        IMediaInfoAux core;
        NullInfo nullInfo;
        Activity context = this.content.helper.context;
        if (!context.isFinishing()) {
            try {
                MediaDatabase mdb = MediaDatabase.getInstance();
                IFFService service = null;
                try {
                    try {
                        service = this.content.container.getFFService(null);
                    } catch (Exception e) {
                        Log.w(TAG, "", e);
                        core = null;
                        nullInfo = new NullInfo();
                    }
                    if (service == null) {
                        this.content.container.scheduleToRunOnServiceConnection(new Runnable() { // from class: com.mxtech.videoplayer.list.FileEntry.1
                            @Override // java.lang.Runnable
                            public void run() {
                                FileEntry.this.showPropertyDialog();
                            }
                        });
                        mdb.release();
                        return;
                    }
                    IMediaInfoAux fFReader = new FFReader(service, this.uri, true);
                    nullInfo = fFReader;
                    core = fFReader;
                    MediaInfoDialog dlg = new MediaInfoDialog(context, this.uri, nullInfo, core, this, mdb, 7);
                    dlg.setCanceledOnTouchOutside(true);
                    DialogRegistry dialogRegistry = DialogRegistry.registryOf(context);
                    if (dialogRegistry != null) {
                        dlg.setOnDismissListener(dialogRegistry);
                        dialogRegistry.register(dlg);
                    }
                    dlg.setButton(-1, this.content.helper.res.getString(17039370), (DialogInterface.OnClickListener) null);
                    dlg.show();
                    nullInfo.close();
                    if (service != null) {
                        this.content.container.releaseFFService(service);
                    }
                    mdb.release();
                } finally {
                    if (0 != 0) {
                        this.content.container.releaseFFService(null);
                    }
                }
            } catch (SQLiteException e2) {
                Log.e(TAG, "", e2);
            }
        }
    }

    public final Media getSubtitleSearchMedia() {
        return new Media(this.uri, null, this.file.name(), this.file.base, getTitle(), null, this.durationMs, this.frameTimeNs);
    }
}
