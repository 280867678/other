package com.mxtech.videoplayer.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.mxtech.DateTimeUtils;
import com.mxtech.StringUtils;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.ActivityMediaList;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaLoader;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.SubtitleDirectory;
import com.mxtech.videoplayer.ThumbShaper;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;

/* loaded from: classes.dex */
public final class ListHelper {
    private static final int BUTTON_DISABLE_ALPHA = 64;
    public static final Comparator<Uri> CASE_INSENSITIVE_NUMERIC_URI_ORDER = new Comparator<Uri>() { // from class: com.mxtech.videoplayer.list.ListHelper.1
        @Override // java.util.Comparator
        public int compare(Uri left, Uri right) {
            return StringUtils.compareToIgnoreCaseNumeric(left.getPath(), right.getPath());
        }
    };
    static final int DISPLAY_FINISHED = 4;
    static final int DISPLAY_LAST = 1;
    static final int DISPLAY_NEW = 2;
    private static final int ICON_ALPHA = 32;
    private static final long PLAYEDTIME_RELATIVE_TRANSITION_TIME = 2592000000L;
    public static final String TAG = "MX.List";
    private static final int THUMB_FRAME_TYPE_MASKED = 2;
    private static final int THUMB_FRAME_TYPE_NONE = 0;
    private static final int THUMB_FRAME_TYPE_SOLID = 1;
    private Bitmap _audioIcon;
    private Bitmap _blankThumb;
    private Bitmap _fileIcon;
    private ColorFilter _grayFilter;
    private Drawable _listDividerDrawable;
    private int _listDividerHeight;
    private Bitmap _videoIcon;
    int borderColor;
    final ActivityMediaList context;
    ColorStateList finishedColor;
    final Handler handler;
    ColorFilter iconFinishedColorFilter;
    ColorFilter iconLastColorFilter;
    ColorFilter iconRegularColorFilter;
    ColorStateList lastColor;
    final LayoutInflater layoutInflater;
    final MediaLoader loader;
    long nextPlaytimeUpdateTimeMs;
    ColorStateList regularColor;
    final Resources res;
    ColorStateList secondaryTextColor;
    final ThumbShaper thumbShaper;
    final StringBuilder titleSb = new StringBuilder();
    private HashMap<TagCacheKey, Object> _tags = new HashMap<>();
    final File primaryExternalStorage = Environment.getExternalStorageDirectory();
    @Nullable
    SubtitleDirectory sdir = SubtitleDirectory.getInstance();

    /* loaded from: classes2.dex */
    private static class TagCacheKey {
        final int drawableAttr;
        final boolean grayed;
        final String tag;
        final int textAppearanceAttr;

        TagCacheKey(String tag, int textAppearanceAttr, int drawableAttr, boolean grayed) {
            this.tag = tag;
            this.textAppearanceAttr = textAppearanceAttr;
            this.drawableAttr = drawableAttr;
            this.grayed = grayed;
        }

        public boolean equals(Object o) {
            if (o instanceof TagCacheKey) {
                TagCacheKey other = (TagCacheKey) o;
                return this.tag.equals(other.tag) && this.textAppearanceAttr == other.textAppearanceAttr && this.drawableAttr == other.drawableAttr && this.grayed == other.grayed;
            }
            return false;
        }

        public int hashCode() {
            return (this.grayed ? 1 : 0) + this.tag.hashCode();
        }

        public String toString() {
            return "[Tag:" + this.tag + " grayed:" + this.grayed + ']';
        }
    }

    static int blendAlpha(int color, int alpha) {
        return (16777215 & color) | (alpha << 24);
    }

    public ListHelper(ActivityMediaList context, LayoutInflater layoutInflater, Handler handler, IListContainer container) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.res = context.getResources();
        this.handler = handler;
        TypedArray attrs = context.obtainStyledAttributes(R.styleable.ListAppearance);
        try {
            this.regularColor = attrs.getColorStateList(R.styleable.ListAppearance_listTitleNormalColor);
            this.finishedColor = attrs.getColorStateList(R.styleable.ListAppearance_listTitleFinishColor);
            this.lastColor = attrs.getColorStateList(R.styleable.ListAppearance_listTitleLastColor);
            int secondaryTextAppearance = attrs.getResourceId(R.styleable.ListAppearance_listSecondaryTextAppearance, 0);
            this.borderColor = attrs.getColor(R.styleable.ListAppearance_borderColor, 0);
            ColorMatrix grayMatrix = new ColorMatrix();
            grayMatrix.setSaturation(0.0f);
            this._grayFilter = new ColorMatrixColorFilter(grayMatrix);
            ThumbShaper.Config thumbConfig = new ThumbShaper.Config();
            thumbConfig.defaultWidth = L.THUMB_WIDTH;
            thumbConfig.defaultHeight = L.THUMB_HEIGHT;
            thumbConfig.durationPaddingLeft = this.res.getDimensionPixelSize(R.dimen.listPlayTimePaddingLeft);
            thumbConfig.durationPaddingTop = this.res.getDimensionPixelSize(R.dimen.listPlayTimePaddingTop);
            thumbConfig.durationPaddingRight = this.res.getDimensionPixelSize(R.dimen.listPlayTimePaddingRight);
            thumbConfig.durationPaddingBottom = this.res.getDimensionPixelSize(R.dimen.listPlayTimePaddingBottom);
            int thumbFrameType = attrs.getInt(R.styleable.ListAppearance_listThumbFrameType, 0);
            switch (thumbFrameType) {
                case 1:
                    Drawable thumbFrame = attrs.getDrawable(R.styleable.ListAppearance_listThumbSoldFrame);
                    if (thumbFrame != null) {
                        ThumbShaper.SolidFrame frame = new ThumbShaper.SolidFrame();
                        frame.overlay = thumbFrame;
                        thumbConfig.frame = frame;
                        break;
                    }
                    break;
                case 2:
                    ThumbShaper.TranslucentFrame frame2 = new ThumbShaper.TranslucentFrame();
                    Drawable drawable = attrs.getDrawable(R.styleable.ListAppearance_listThumbMaskShadow);
                    if (drawable instanceof BitmapDrawable) {
                        frame2.shadow = ((BitmapDrawable) drawable).getBitmap();
                    }
                    if (frame2.shadow == null) {
                        frame2.shadow = BitmapFactory.decodeResource(this.res, R.drawable.thumb_round_shadow);
                    }
                    frame2.eraseMask = BitmapFactory.decodeResource(this.res, R.drawable.thumb_round_mask);
                    frame2.erasePaint = new Paint();
                    frame2.erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                    thumbConfig.frame = frame2;
                    break;
            }
            this.thumbShaper = new ThumbShaper(thumbConfig, handler);
            this.loader = new MediaLoader(container, this.thumbShaper, handler);
            attrs.recycle();
            attrs = context.obtainStyledAttributes(secondaryTextAppearance, new int[]{16842904});
            try {
                this.secondaryTextColor = attrs.getColorStateList(0);
                attrs.recycle();
                this.iconRegularColorFilter = new PorterDuffColorFilter(blendAlpha(this.regularColor.getDefaultColor(), 32), PorterDuff.Mode.SRC_IN);
                this.iconLastColorFilter = new PorterDuffColorFilter(blendAlpha(this.lastColor.getDefaultColor(), 32), PorterDuff.Mode.SRC_IN);
                this.iconFinishedColorFilter = new PorterDuffColorFilter(blendAlpha(this.finishedColor.getDefaultColor(), 32), PorterDuff.Mode.SRC_IN);
            } finally {
            }
        } finally {
        }
    }

    public void cancelAllPendingJobs() {
        this.loader.cancelAllPendingRequests();
        this.thumbShaper.cancelAllPendingRequests();
    }

    public void close(boolean join) {
        this.loader.close(join);
        this.thumbShaper.closeParallel();
        this.sdir.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getBlankThumb() {
        if (this._blankThumb == null) {
            this._blankThumb = this.thumbShaper.build(null, null);
        }
        return this._blankThumb;
    }

    public static final int getColorType(int displayType) {
        if ((displayType & 1) != 0) {
            return 1;
        }
        if ((displayType & 4) != 0) {
            return 4;
        }
        return 0;
    }

    public void stylize(TextView titleView, int displayType, View... secondaryViews) {
        ColorStateList titleColors;
        int titleTypefaceStyle;
        ColorStateList secondaryColors;
        ColorFilter secondaryColorFilter;
        if ((displayType & 1) != 0) {
            titleColors = this.lastColor;
            titleTypefaceStyle = P.list_last_media_typeface;
            secondaryColors = this.lastColor;
            secondaryColorFilter = this.iconLastColorFilter;
        } else if ((displayType & 4) != 0) {
            titleColors = this.finishedColor;
            titleTypefaceStyle = 0;
            secondaryColors = this.finishedColor;
            secondaryColorFilter = this.iconFinishedColorFilter;
        } else {
            titleColors = this.regularColor;
            titleTypefaceStyle = 0;
            secondaryColors = this.secondaryTextColor;
            secondaryColorFilter = this.iconRegularColorFilter;
        }
        titleView.setTextColor(titleColors);
        titleView.setTypeface(null, titleTypefaceStyle);
        for (View v : secondaryViews) {
            if (v instanceof ImageView) {
                ImageView iv = (ImageView) v;
                Drawable d = iv.getDrawable();
                if (d != null) {
                    GraphicUtils.safeMutate(d).setColorFilter(secondaryColorFilter);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTextColor(secondaryColors);
            }
        }
    }

    public void stylizeListView(AbsListView listView, Builder builder) {
        Drawable dividerDrawable;
        int dividerHeight;
        if (listView instanceof ListView) {
            ListView lv = (ListView) listView;
            if (builder.isItemComplex()) {
                if (this._listDividerDrawable == null) {
                    this._listDividerDrawable = new ColorDrawable(this.borderColor);
                    this._listDividerHeight = this.res.getDimensionPixelSize(R.dimen.border_width);
                }
                dividerDrawable = this._listDividerDrawable;
                dividerHeight = this._listDividerHeight;
            } else {
                dividerDrawable = null;
                dividerHeight = 0;
            }
            lv.setDivider(dividerDrawable);
            lv.setDividerHeight(dividerHeight);
        }
    }

    public void setIconViewImage(ImageView iconView, int numVideoTrack, int numAudioTrack) {
        if (numVideoTrack > 0) {
            if (this._videoIcon == null) {
                this._videoIcon = BitmapFactory.decodeResource(this.res, R.drawable.ic_local_movies_white_24dp);
            }
            iconView.setImageBitmap(this._videoIcon);
        } else if (numAudioTrack > 0) {
            if (this._audioIcon == null) {
                this._audioIcon = BitmapFactory.decodeResource(this.res, R.drawable.ic_music_box_white_24dp);
            }
            iconView.setImageBitmap(this._audioIcon);
        } else {
            if (this._fileIcon == null) {
                this._fileIcon = BitmapFactory.decodeResource(this.res, R.drawable.ic_insert_drive_file_white_24dp);
            }
            iconView.setImageBitmap(this._fileIcon);
        }
    }

    public void appendTag(SpannableStringBuilder sb, String name, int textAppearanceAttr, int drawableAttr, boolean grayed) {
        TagCacheKey key = new TagCacheKey(name, textAppearanceAttr, drawableAttr, grayed);
        Object span = this._tags.get(key);
        if (span == null) {
            TypedArray a = this.context.obtainStyledAttributes(new int[]{textAppearanceAttr, drawableAttr, R.attr.tagGrayText});
            try {
                Drawable drawable = a.getDrawable(1);
                if (drawable != null) {
                    if (grayed) {
                        Drawable drawable2 = new BitmapDrawable(this.context.getResources(), ((BitmapDrawable) drawable).getBitmap());
                        drawable2.setColorFilter(this._grayFilter);
                        drawable = drawable2;
                    }
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    span = new ImageSpan(drawable, 1);
                } else {
                    int textAppearance = a.getResourceId(0, 0);
                    if (textAppearance != 0) {
                        if (grayed) {
                            span = new TextAppearanceSpan(this.context, a.getResourceId(2, 0));
                        } else {
                            span = new TextAppearanceSpan(this.context, textAppearance);
                        }
                    }
                }
                a.recycle();
                this._tags.put(key, span);
            } catch (Throwable th) {
                a.recycle();
                throw th;
            }
        }
        if (sb.getSpanStart(span) < 0) {
            int oldLen = sb.length();
            if (oldLen > 0 && !Character.isWhitespace(sb.charAt(oldLen - 1))) {
                sb.append(' ');
            }
            sb.append((CharSequence) name);
            sb.setSpan(span, sb.length() - name.length(), sb.length(), 33);
            sb.append(' ');
        }
    }

    public static String formatDuration(int duration) {
        if (duration <= 0) {
            return null;
        }
        return DateUtils.formatElapsedTime(duration / 1000);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String formatTime(long time, long currentTime) {
        return DateTimeUtils.formatShortest(this.context, time, currentTime);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String formatPlayedTime(long lastPlayedTime, long currentTime) {
        int unitTime;
        int resId;
        long elapsed = currentTime - lastPlayedTime;
        if (elapsed < 60000) {
            this.nextPlaytimeUpdateTimeMs = 60000 + lastPlayedTime;
            return this.res.getString(R.string.played_now);
        } else if (elapsed < PLAYEDTIME_RELATIVE_TRANSITION_TIME) {
            if (elapsed < 3600000) {
                unitTime = (int) (elapsed / 60000);
                resId = R.plurals.count_minutes;
                this.nextPlaytimeUpdateTimeMs = ((unitTime + 1) * 60000) + lastPlayedTime;
            } else if (elapsed < 86400000) {
                unitTime = (int) (elapsed / 3600000);
                resId = R.plurals.count_hours;
                this.nextPlaytimeUpdateTimeMs = ((unitTime + 1) * 3600000) + lastPlayedTime;
            } else {
                unitTime = (int) (elapsed / 86400000);
                resId = R.plurals.count_days;
                this.nextPlaytimeUpdateTimeMs = ((unitTime + 1) * 86400000) + lastPlayedTime;
            }
            return StringUtils.getString_s(R.string.played_ago, StringUtils.getQuantityString_s(resId, unitTime, Integer.valueOf(unitTime)));
        } else {
            this.nextPlaytimeUpdateTimeMs = Long.MAX_VALUE;
            return StringUtils.getString_s(R.string.played_at_no_preposition, DateUtils.getRelativeTimeSpanString((Context) this.context, lastPlayedTime, true));
        }
    }

    void enableDrawable(Drawable drawable, boolean enable) {
        if (!drawable.isStateful()) {
            GraphicUtils.safeMutate(drawable);
            if (enable) {
                drawable.setAlpha(255);
            } else {
                drawable.setAlpha(64);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableMenuItem(MenuItem item, boolean enable) {
        item.setEnabled(enable);
        Drawable icon = item.getIcon();
        if (icon != null) {
            enableDrawable(icon, enable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableView(View view, boolean enable) {
        view.setEnabled(enable);
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            Drawable d = iv.getDrawable();
            if (d != null) {
                enableDrawable(d, enable);
            }
        }
    }
}
