package com.mxtech.videoplayer.list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import com.mxtech.FileUtils;
import com.mxtech.NumericUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.media.MediaUtils;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.directory.MediaFile;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.widget.PropertyDialog;
import java.io.File;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class Entry implements Comparable<Entry> {
    static final int CAN_SORT_BY_DATE = 131072;
    static final int CAN_SORT_BY_DURATION = 8388608;
    static final int CAN_SORT_BY_EXTENSION = 262144;
    static final int CAN_SORT_BY_LOCATION = 2097152;
    static final int CAN_SORT_BY_RESOLUTION = 4194304;
    static final int CAN_SORT_BY_SIZE = 65536;
    static final int CAN_SORT_BY_STATUS = 1048576;
    static final int CAN_SORT_BY_WATCH_TIME = 524288;
    static final int FEATURE_CAN_DELETE = 8;
    static final int FEATURE_CAN_PLAY = 64;
    static final int FEATURE_CAN_RENAME = 4;
    static final int FEATURE_HAS_EXTENSION = 32;
    static final int FEATURE_HIERARCHICAL = 512;
    static final int MASK_SORT_PRIORITY = 1879048192;
    static final int MASK_SUPPORT_SORT = 268369920;
    static final int SORT_PRIORITY_0 = 0;
    static final int SORT_PRIORITY_1 = 268435456;
    static final int SORT_PRIORITY_2 = 536870912;
    static final int SORT_PRIORITY_3 = 805306368;
    static final int SORT_PRIORITY_4 = 1073741824;
    static final int SORT_PRIORITY_5 = 1342177280;
    static final int SORT_PRIORITY_6 = 1610612736;
    static final int SORT_PRIORITY_7 = 1879048192;
    static final int SORT_PRIORITY_MAX = 1879048192;
    static final int SORT_PRIORITY_MIN = 0;
    static final String TAG = "MX.List.Entry";
    static final int TYPE_AUDIO = 1;
    static final int TYPE_MIXED = 3;
    static final int TYPE_UNKNOWN = 0;
    static final int TYPE_VIDEO = 2;
    final MediaListFragment content;
    int displayType;
    final String ext;
    final int features;
    final MediaFile file;
    long lastWatchTime = -1;
    final String location;
    final String name;
    final String name_noext;
    long size;
    String title;
    final Uri uri;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract File[] getAssociatedFiles(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getDisplayType(long j, long j2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getLayoutResourceId();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void open();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean renameTo(String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void render(View view);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int type(@Nullable List<Uri> list);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Entry(Uri uri, MediaFile file, MediaListFragment content, int features) {
        this.features = features;
        this.content = content;
        this.uri = uri;
        this.file = file;
        this.name = uri.getLastPathSegment();
        if (file != null) {
            String parent = file.base.getParent();
            if (parent != null) {
                this.location = parent;
            } else {
                this.location = "";
            }
        } else {
            String uriStr = uri.toString();
            int lastSep = uriStr.lastIndexOf(File.separatorChar);
            if (lastSep > 0) {
                this.location = uriStr.substring(0, lastSep);
            } else {
                this.location = "";
            }
        }
        if (this.name != null && (features & 32) != 0) {
            this.ext = FileUtils.getExtension(this.name);
        } else {
            this.ext = null;
        }
        if (this.ext != null) {
            this.name_noext = this.name.substring(0, (this.name.length() - this.ext.length()) - 1);
        } else {
            this.name_noext = this.name;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getTitle() {
        if ((P.list_fields & 16) != 0) {
            if (this.name != null) {
                return MediaUtils.capitalizeWithDictionary(this.name, this.content.helper.titleSb);
            }
        } else if (this.name_noext != null) {
            return MediaUtils.capitalizeWithDictionary(this.name_noext, this.content.helper.titleSb);
        }
        return this.uri.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPendingJobsCanceled() {
    }

    @Override // java.lang.Comparable
    public final int compareTo(Entry right) {
        int[] iArr;
        int factor;
        int comp = (right.features & 1879048192) - (this.features & 1879048192);
        if (comp != 0) {
            return comp;
        }
        for (int sorting : P.list_sortings) {
            if (sorting < 0) {
                sorting = -sorting;
                factor = -1;
            } else {
                factor = 1;
            }
            switch (sorting) {
                case 1:
                    comp = compareToByTitle(right);
                    break;
                case 2:
                    comp = compareToByNumericTitle(right);
                    break;
                case 3:
                    if ((this.features & 65536) != 0) {
                        if ((right.features & 65536) != 0) {
                            comp = compareToBySize(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 65536) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 4:
                    if ((this.features & 131072) != 0) {
                        if ((right.features & 131072) != 0) {
                            comp = compareToByDate(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 131072) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 5:
                    if ((this.features & 262144) != 0) {
                        if ((right.features & 262144) != 0) {
                            comp = compareToByExtension(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 262144) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 6:
                    if ((this.features & 524288) != 0) {
                        if ((right.features & 524288) != 0) {
                            comp = compareToByWatchTime(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 524288) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 7:
                    if ((this.features & 1048576) != 0) {
                        if ((right.features & 1048576) != 0) {
                            comp = compareToByStatus(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 1048576) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 8:
                    if ((this.features & 2097152) != 0) {
                        if ((right.features & 2097152) != 0) {
                            comp = compareToByLocation(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 2097152) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 16:
                    if ((this.features & 4194304) != 0) {
                        if ((right.features & 4194304) != 0) {
                            comp = compareToByResolution(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 4194304) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 32:
                    if ((this.features & 4194304) != 0) {
                        if ((right.features & 4194304) != 0) {
                            comp = compareToByFrameRate(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 4194304) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
                case 64:
                    if ((this.features & 8388608) != 0) {
                        if ((right.features & 8388608) != 0) {
                            comp = compareToByDuration(right);
                            break;
                        } else {
                            return -factor;
                        }
                    } else if ((right.features & 8388608) == 0) {
                        continue;
                    } else {
                        return factor;
                    }
            }
            if (comp != 0) {
                return factor * comp;
            }
        }
        return 0;
    }

    protected final int compareToByNumericTitle(Entry right) {
        return StringUtils.compareToIgnoreCaseNumeric(this.title, right.title);
    }

    protected final int compareToByTitle(Entry right) {
        return StringUtils.compareToIgnoreCase(this.title, right.title);
    }

    protected final int compareToByExtension(Entry right) {
        return StringUtils.compareToIgnoreCase(this.ext, right.ext);
    }

    protected final int compareToBySize(Entry right) {
        long diff = this.size - right.size;
        if (diff < 0) {
            return -1;
        }
        return diff > 0 ? 1 : 0;
    }

    protected final int compareToByDate(Entry right) {
        if (this.file == null) {
            return right.file != null ? 1 : 0;
        } else if (right.file != null) {
            long diff = this.file.lastModified() - right.file.lastModified();
            if (diff < 0) {
                return -1;
            }
            return diff > 0 ? 1 : 0;
        } else {
            return -1;
        }
    }

    protected final int compareToByWatchTime(Entry right) {
        if (this.lastWatchTime < right.lastWatchTime) {
            return -1;
        }
        if (this.lastWatchTime > right.lastWatchTime) {
            return 1;
        }
        return 0;
    }

    protected final int compareToByStatus(Entry right) {
        int leftVal = this.displayType & 1;
        int rightVal = right.displayType & 1;
        if (leftVal != rightVal) {
            return rightVal - leftVal;
        }
        int leftVal2 = this.displayType & 2;
        int rightVal2 = right.displayType & 2;
        if (leftVal2 != rightVal2) {
            return rightVal2 - leftVal2;
        }
        return (this.displayType & 4) - (right.displayType & 4);
    }

    protected final int compareToByLocation(Entry right) {
        return this.location.compareToIgnoreCase(right.location);
    }

    protected final int compareToByResolution(Entry right) {
        if (this instanceof PlayableEntry) {
            if (right instanceof PlayableEntry) {
                PlayableEntry leftPlayable = (PlayableEntry) this;
                PlayableEntry rightPlayable = (PlayableEntry) right;
                int heightDiff = leftPlayable.height - rightPlayable.height;
                if (heightDiff == 0) {
                    return leftPlayable.width - rightPlayable.width;
                }
                return heightDiff;
            } else if (((PlayableEntry) this).hasResolution()) {
                return -1;
            }
        } else if ((right instanceof PlayableEntry) && ((PlayableEntry) right).hasResolution()) {
            return 1;
        }
        return 0;
    }

    protected final int compareToByDuration(Entry right) {
        int leftDuration = this instanceof PlayableEntry ? ((PlayableEntry) this).durationMs : 0;
        int rightDuration = right instanceof PlayableEntry ? ((PlayableEntry) right).durationMs : 0;
        return leftDuration - rightDuration;
    }

    protected final int compareToByFrameRate(Entry right) {
        if (this instanceof PlayableEntry) {
            if (right instanceof PlayableEntry) {
                PlayableEntry leftPlayable = (PlayableEntry) this;
                PlayableEntry rightPlayable = (PlayableEntry) right;
                int leftFrametime = leftPlayable.frameTimeNs > 0 ? leftPlayable.frameTimeNs : Integer.MAX_VALUE;
                int rightFrametime = rightPlayable.frameTimeNs > 0 ? rightPlayable.frameTimeNs : Integer.MAX_VALUE;
                return rightFrametime - leftFrametime;
            } else if (((PlayableEntry) this).frameTimeNs > 0) {
                return -1;
            }
        } else if ((right instanceof PlayableEntry) && ((PlayableEntry) right).frameTimeNs > 0) {
            return 1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Uri[] getItems() {
        return new Uri[]{this.uri};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshThumb() {
        Uri[] items;
        for (Uri item : getItems()) {
            L.thumbCache.remove(item);
        }
    }

    /* loaded from: classes2.dex */
    private class EntryPropertyDialog extends PropertyDialog {
        public EntryPropertyDialog(Context context) {
            super(context);
            setTitle(StringUtils.getString_s(R.string.detail_title_detail, Entry.this.title));
            if (Entry.this.file != null) {
                if (Entry.this instanceof DirectoryEntry) {
                    addGroup(R.string.detail_group_folder);
                    addRow(R.string.detail_folder, Entry.this.file.path);
                } else {
                    addGroup(R.string.detail_group_file);
                    addRow(R.string.detail_file, Entry.this.file.path);
                }
                addRow(R.string.detail_date, DateUtils.formatDateTime(context, Entry.this.file.lastModified(), 21));
            } else {
                addRow(R.string.detail_uri, Entry.this.uri.toString());
            }
            if (Entry.this instanceof ListableEntry) {
                ListableEntry listableEntry = (ListableEntry) Entry.this;
                addRow(R.string.detail_video_total_size, NumericUtils.formatShortAndLongSize(context, Entry.this.size));
                addRow(R.string.property_item_contains, listableEntry.getContentsText());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showPropertyDialog() {
        Activity context = this.content.helper.context;
        if (!context.isFinishing()) {
            EntryPropertyDialog dlg = new EntryPropertyDialog(context);
            dlg.setCanceledOnTouchOutside(true);
            dlg.setButton(-1, context.getString(17039370), (DialogInterface.OnClickListener) null);
            DialogRegistry dialogRegistry = DialogRegistry.registryOf(context);
            if (dialogRegistry != null) {
                dlg.setOnDismissListener(dialogRegistry);
                dialogRegistry.register(dlg);
            }
            dlg.show();
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Entry) {
            return this.uri.equals(((Entry) o).uri);
        }
        return false;
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public String toString() {
        return this.uri.toString();
    }
}
