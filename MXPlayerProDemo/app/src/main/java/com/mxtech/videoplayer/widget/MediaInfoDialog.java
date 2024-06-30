package com.mxtech.videoplayer.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDoneException;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.mxtech.Misc;
import com.mxtech.NumericUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.media.IMediaInfo;
import com.mxtech.media.IMediaInfoAux;
import com.mxtech.media.IStreamInfo;
import com.mxtech.media.MediaUtils;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.IMediaRuntimeInfo;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.widget.PropertyDialog;
import java.io.File;
import java.util.Locale;

/* loaded from: classes.dex */
public final class MediaInfoDialog extends PropertyDialog implements CompoundButton.OnCheckedChangeListener {
    static final int AV_CH_BACK_CENTER = 256;
    static final int AV_CH_BACK_LEFT = 16;
    static final int AV_CH_BACK_RIGHT = 32;
    static final int AV_CH_FRONT_CENTER = 4;
    static final int AV_CH_FRONT_LEFT = 1;
    static final int AV_CH_FRONT_LEFT_OF_CENTER = 64;
    static final int AV_CH_FRONT_RIGHT = 2;
    static final int AV_CH_FRONT_RIGHT_OF_CENTER = 128;
    static final int AV_CH_LAYOUT_2_1 = 259;
    static final int AV_CH_LAYOUT_2_2 = 1539;
    static final int AV_CH_LAYOUT_4POINT0 = 263;
    static final int AV_CH_LAYOUT_5POINT0 = 1543;
    static final int AV_CH_LAYOUT_5POINT0_BACK = 55;
    static final int AV_CH_LAYOUT_5POINT1 = 1551;
    static final int AV_CH_LAYOUT_5POINT1_BACK = 63;
    static final int AV_CH_LAYOUT_7POINT0 = 1591;
    static final int AV_CH_LAYOUT_7POINT1 = 1599;
    static final int AV_CH_LAYOUT_7POINT1_WIDE = 255;
    static final int AV_CH_LAYOUT_MONO = 4;
    static final int AV_CH_LAYOUT_QUAD = 51;
    static final int AV_CH_LAYOUT_STEREO = 3;
    static final int AV_CH_LAYOUT_STEREO_DOWNMIX = 1610612736;
    static final int AV_CH_LAYOUT_SURROUND = 7;
    static final int AV_CH_LOW_FREQUENCY = 8;
    static final int AV_CH_SIDE_LEFT = 512;
    static final int AV_CH_SIDE_RIGHT = 1024;
    static final int AV_CH_STEREO_LEFT = 536870912;
    static final int AV_CH_STEREO_RIGHT = 1073741824;
    static final int AV_CH_TOP_BACK_CENTER = 65536;
    static final int AV_CH_TOP_BACK_LEFT = 32768;
    static final int AV_CH_TOP_BACK_RIGHT = 131072;
    static final int AV_CH_TOP_CENTER = 2048;
    static final int AV_CH_TOP_FRONT_CENTER = 8192;
    static final int AV_CH_TOP_FRONT_LEFT = 4096;
    static final int AV_CH_TOP_FRONT_RIGHT = 16384;
    public static final int FLAG_SECURE_URI = 8;
    public static final int FLAG_SHOW_RECORD = 1;
    public static final int FLAG_SHOW_SUBTITLE = 2;
    public static final int FLAG_TITLE_DETAIL = 4;
    private boolean _initialLearnMore;
    private CheckBox _learnMore;

    public MediaInfoDialog(Context context, Uri uri, IMediaInfo info, IMediaInfoAux core, IMediaRuntimeInfo runtime, MediaDatabase mdb, int flags) {
        super(context);
        String fileName;
        File videoFolder;
        int resId;
        String text;
        File[] scan;
        File file = MediaUtils.fileFromUri(uri);
        long fileTime = 0;
        long fileSize = 0;
        int width = info.displayWidth();
        int height = info.displayHeight();
        String resolution = (width <= 0 || height <= 0) ? null : String.format(Locale.US, "%d x %d", Integer.valueOf(width), Integer.valueOf(height));
        if (file != null) {
            fileTime = file.lastModified();
            fileSize = file.length();
        }
        if ((flags & 4) != 0) {
            int i = R.string.detail_title_detail;
            Object[] objArr = new Object[1];
            objArr[0] = MediaUtils.retrieveTitle(uri, (P.list_fields & 16) != 0, L.sb);
            setTitle(StringUtils.getString_s(i, objArr));
        } else {
            setTitle(R.string.menu_property);
        }
        int recordItems = 0;
        if (file != null) {
            fileName = file.getName();
            videoFolder = file.getParentFile();
            if ((flags & 8) == 0) {
                fileItems = 0 + addRow(R.string.detail_file, fileName) + addRow(R.string.detail_folder, videoFolder.getPath()) + addRow(R.string.detail_size, NumericUtils.formatShortAndLongSize(context, fileSize)) + addRow(R.string.detail_date, DateUtils.formatDateTime(context, fileTime, 21));
            }
        } else {
            fileItems = (flags & 8) == 0 ? 0 + addRow(R.string.detail_uri, uri.toString()) : 0;
            fileName = null;
            videoFolder = null;
        }
        Uri coverArt = runtime.getExternalCoverArt();
        fileItems = coverArt != null ? fileItems + addRow(R.string.detail_cover_art, coverArt.getLastPathSegment()) : fileItems;
        if ((flags & 2) != 0 && file != null) {
            L.sb.setLength(0);
            boolean containsSubtitle = core != null ? core.hasEmbeddedSubtitle() : false;
            if (containsSubtitle) {
                L.sb.append(context.getString(R.string.detail_value_contained_subtitle));
            }
            for (File subtitleFile : SubtitleFactory.scan(fileName, videoFolder, P.subtitleFolder)) {
                if (L.sb.length() > 0) {
                    L.sb.append(", ");
                }
                L.sb.append(subtitleFile.getName());
            }
            fileItems += addRow(R.string.detail_subtitle, L.sb.toString());
        }
        int mediaItems = 0 + addRow(R.string.detail_title, info.title()) + addRow(R.string.detail_description, info.description()) + addRow(R.string.detail_format, info.format()) + addRow(R.string.detail_mime, info.mimeType()) + addRow(R.string.detail_resolution, resolution);
        int duration = info.duration();
        int mediaItems2 = (duration > 0 ? mediaItems + addRow(R.string.detail_playtime, DateUtils.formatElapsedTime(L.sb, duration / 1000)) : mediaItems) + addRow(R.string.detail_genre, info.genre()) + addRow(R.string.detail_year, info.year()) + addRow(R.string.detail_language, info.displayLocales()) + addRow(R.string.detail_album, info.album()) + addRow(R.string.detail_artist, info.artist()) + addRow(R.string.detail_composer, info.composer()) + addRow(R.string.detail_performer, info.performer()) + addRow(R.string.detail_album_artist, info.albumArtist()) + addRow(R.string.detail_publisher, info.publisher()) + addRow(R.string.detail_copyright, info.copyright()) + addRow(R.string.detail_encoded_by, info.encoded_by()) + addRow(R.string.detail_encoder, info.encoder());
        if ((flags & 1) != 0) {
            if (file != null) {
                try {
                    Cursor c = mdb.db.rawQuery("SELECT LastWatchTime, FinishTime FROM VideoFile WHERE Id=" + mdb.getFileIDOrThrow(file), null);
                    if (c.moveToFirst()) {
                        int recordItems2 = c.isNull(1) ? 0 + addRow(R.string.detail_finished, context.getString(R.string.detail_value_unfinished)) : 0 + addRow(R.string.detail_finished, context.getString(R.string.detail_value_finished));
                        recordItems = c.isNull(0) ? recordItems2 + addRow(R.string.detail_last_watch_time, context.getString(R.string.detail_value_none)) : recordItems2 + addRow(R.string.detail_last_watch_time, DateUtils.formatDateTime(context, c.getLong(0), 21));
                    } else {
                        recordItems = 0 + addRow(R.string.detail_finished, context.getString(R.string.detail_value_unfinished)) + addRow(R.string.detail_last_watch_time, context.getString(R.string.detail_value_none));
                    }
                    c.close();
                } catch (SQLiteDoneException e) {
                    recordItems = recordItems + addRow(R.string.detail_finished, context.getString(R.string.detail_value_unfinished)) + addRow(R.string.detail_last_watch_time, context.getString(R.string.detail_value_none));
                }
            }
            Integer lastPosition = mdb.getPosition(uri);
            recordItems = lastPosition == null ? recordItems + addRow(R.string.detail_last_position, context.getString(R.string.detail_value_none)) : recordItems + addRow(R.string.detail_last_position, DateUtils.formatElapsedTime(L.sb, lastPosition.intValue() / 1000));
        }
        if (recordItems > 0) {
            addGroup(R.string.detail_group_record, fileItems + mediaItems2);
        }
        if (mediaItems2 > 0) {
            addGroup(R.string.detail_group_media, fileItems);
        }
        if (fileItems > 0) {
            addGroup(R.string.detail_group_file, 0);
        }
        if (core != null) {
            int count = core.getStreamCount();
            int knownIndex = 0;
            for (int i2 = 0; i2 < count; i2++) {
                IStreamInfo stream = core.streamInfo(i2);
                try {
                    int type = stream.type();
                    switch (type) {
                        case 0:
                            if ((stream.disposition() & 1024) == 0) {
                                resId = R.string.detail_group_video;
                                break;
                            } else {
                                continue;
                            }
                        case 1:
                            resId = R.string.audio;
                            break;
                        case 2:
                        default:
                            continue;
                        case 3:
                            resId = R.string.subtitle;
                            break;
                    }
                    knownIndex++;
                    addGroup((CharSequence) StringUtils.getString_s(R.string.stream_with_index, Integer.valueOf(knownIndex)), false);
                    addRow(R.string.type, (CharSequence) context.getString(resId), false);
                    addRow(R.string.detail_title, (CharSequence) stream.title(), false);
                    addRow(R.string.detail_description, (CharSequence) stream.description(), false);
                    addRow(R.string.codec, (CharSequence) stream.format(), false);
                    addRow(R.string.profile, (CharSequence) stream.profile(), false);
                    if (type == 0) {
                        int width2 = stream.displayWidth();
                        int height2 = stream.height();
                        if (width2 > 0 && height2 > 0) {
                            addRow(R.string.detail_resolution, (CharSequence) (width2 + " x " + height2), false);
                        }
                        int frameTime = stream.frameTime();
                        if (frameTime > 0) {
                            addRow(R.string.frame_rate, (CharSequence) MediaUtils.formatFrameTime(frameTime, 0), false);
                        }
                    }
                    if (type == 1) {
                        int sampleRate = stream.sampleRate();
                        if (sampleRate > 0) {
                            addRow(R.string.sample_rate, (CharSequence) (Integer.toString(sampleRate) + " Hz"), false);
                        }
                        long layout = stream.channelLayout();
                        if (layout != 0) {
                            switch ((int) layout) {
                                case 3:
                                    text = context.getString(R.string.channel_stereo);
                                    break;
                                case 4:
                                    text = context.getString(R.string.channel_mono);
                                    break;
                                case 7:
                                    text = context.getString(R.string.channel_surround);
                                    break;
                                case 63:
                                    text = "5.1 " + context.getString(R.string.channel) + " (back)";
                                    break;
                                case 255:
                                    text = "7.1 " + context.getString(R.string.channel) + " (wide)";
                                    break;
                                case 259:
                                    text = "2.1 " + context.getString(R.string.channel);
                                    break;
                                case AV_CH_LAYOUT_5POINT1 /* 1551 */:
                                    text = "5.1 " + context.getString(R.string.channel);
                                    break;
                                case AV_CH_LAYOUT_7POINT1 /* 1599 */:
                                    text = "7.1 " + context.getString(R.string.channel);
                                    break;
                                default:
                                    text = Integer.toString(Misc.numberOfSetBits((int) layout)) + ' ' + context.getString(R.string.channel);
                                    break;
                            }
                            addRow(R.string.channels, (CharSequence) text, false);
                        }
                    }
                    int bitRate = stream.bitRate();
                    if (bitRate > 0) {
                        if (bitRate >= 1000000000) {
                            addRow(R.string.bit_rate, (CharSequence) (String.format(Locale.US, "%.1f", Double.valueOf(bitRate / 1.0E9d)) + " Gbits/sec"), false);
                        } else if (bitRate >= 1000000) {
                            addRow(R.string.bit_rate, (CharSequence) (String.format(Locale.US, "%.1f", Double.valueOf(bitRate / 1000000.0d)) + " Mbits/sec"), false);
                        } else if (bitRate >= 1000) {
                            addRow(R.string.bit_rate, (CharSequence) (String.format(Locale.US, "%.1f", Double.valueOf(bitRate / 1000.0d)) + " kbits/sec"), false);
                        } else {
                            addRow(R.string.bit_rate, (CharSequence) (Integer.toString(bitRate) + " bits/sec"), false);
                        }
                    }
                    addRow(R.string.detail_genre, (CharSequence) stream.genre(), false);
                    addRow(R.string.detail_year, (CharSequence) stream.year(), false);
                    addRow(R.string.detail_language, (CharSequence) stream.displayLocales(), false);
                    addRow(R.string.detail_album, (CharSequence) stream.album(), false);
                    addRow(R.string.detail_artist, (CharSequence) stream.artist(), false);
                    addRow(R.string.detail_composer, (CharSequence) stream.composer(), false);
                    addRow(R.string.detail_performer, (CharSequence) stream.performer(), false);
                    addRow(R.string.detail_album_artist, (CharSequence) stream.albumArtist(), false);
                    addRow(R.string.detail_publisher, (CharSequence) stream.publisher(), false);
                    addRow(R.string.detail_copyright, (CharSequence) stream.copyright(), false);
                    addRow(R.string.detail_encoded_by, (CharSequence) stream.encoded_by(), false);
                    addRow(R.string.detail_encoder, (CharSequence) stream.encoder(), false);
                    stream.close();
                } finally {
                    stream.close();
                }
            }
            this._learnMore = (CheckBox) getLayoutInflater().inflate(R.layout.learn_more_button, (ViewGroup) this.layout, false);
            this._learnMore.setOnCheckedChangeListener(this);
            this.layout.addView(this._learnMore);
            this._initialLearnMore = App.prefs.getBoolean(Key.PROPERTY_LEARN_MORE, false);
            if (this._initialLearnMore) {
                this._learnMore.setChecked(true);
            }
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        showHiddenItems(isChecked);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatDialog, android.app.Dialog
    public void onStop() {
        if (this._learnMore != null && this._initialLearnMore != this._learnMore.isChecked()) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putBoolean(Key.PROPERTY_LEARN_MORE, this._learnMore.isChecked());
            AppUtils.apply(editor);
        }
        super.onStop();
    }
}
