package com.mxtech.media;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import com.mxtech.LocaleUtils;
import com.mxtech.media.service.IFFService;
import com.mxtech.videoplayer.App;
import java.util.Locale;

/* loaded from: classes.dex */
public final class FFReader implements IMediaInfo, IMediaInfoAux {
    static final int KEY_ALBUM = 1;
    static final int KEY_ALBUMARTIST = 13;
    static final int KEY_ARTIST = 2;
    static final int KEY_COMMENT = 103;
    static final int KEY_COMPOSER = 4;
    static final int KEY_COPYRIGHT = 14;
    static final int KEY_DATE = 5;
    static final int KEY_ENCODED_BY = 16;
    static final int KEY_ENCODER = 15;
    static final int KEY_GENRE = 6;
    static final int KEY_LANGUAGE = 102;
    static final int KEY_MIMETYPE = 12;
    static final int KEY_PERFORMER = 17;
    static final int KEY_PUBLISHER = 18;
    static final int KEY_TITLE = 7;
    public static final int NAME_LONG = 1;
    public static final int NAME_SHORT = 0;
    private long _context;
    private boolean _rot90;
    private int _rotationDegrees;
    private final IFFService _service;

    public FFReader(IFFService service, Uri uri, boolean localFileOnly) throws Exception {
        this._service = service;
        this._context = this._service.r_create(MediaUtils.ffmpegPathFrom(uri), localFileOnly);
        if (this._context == 0) {
            throw new Exception();
        }
        this._rotationDegrees = this._service.r_rotation(this._context);
        this._rot90 = this._rotationDegrees == 90 || this._rotationDegrees == 270;
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override // com.mxtech.media.IMediaInfo
    public void close() {
        if (this._context != 0) {
            try {
                this._service.r_release(this._context);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
            }
            this._context = 0L;
        }
    }

    public void cancel() {
        try {
            this._service.r_cancel(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public String format() {
        return getFormat(1);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String description() {
        return getMetadata(103);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String album() {
        return getMetadata(1);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String artist() {
        return getMetadata(2);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String composer() {
        return getMetadata(4);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String genre() {
        return getMetadata(6);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String title() {
        return getMetadata(7);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String mimeType() {
        return getMetadata(12);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String albumArtist() {
        return getMetadata(13);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String copyright() {
        return getMetadata(14);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String encoder() {
        return getMetadata(15);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String encoded_by() {
        return getMetadata(16);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String performer() {
        return getMetadata(17);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String publisher() {
        return getMetadata(18);
    }

    @Override // com.mxtech.media.IMediaInfo
    public String year() {
        return getMetadata(5);
    }

    @Override // com.mxtech.media.IMediaInfo
    public Locale[] locales() {
        String langs = getMetadata(102);
        return (langs == null || langs.length() == 0 || LocaleUtils.UNDEFINED.equalsIgnoreCase(langs)) ? new Locale[0] : LocaleUtils.parseLocales(langs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String toDisplayLocales(Locale[] locales) {
        StringBuilder b = new StringBuilder();
        for (Locale locale : locales) {
            if (b.length() > 0) {
                b.append(' ');
            }
            b.append(locale.getDisplayName());
        }
        return b.toString();
    }

    @Override // com.mxtech.media.IMediaInfo
    public String displayLocales() {
        return toDisplayLocales(locales());
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int frameTime() {
        try {
            return this._service.r_frameTime(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int duration() {
        try {
            return this._service.r_duration(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int height() {
        int r_height;
        try {
            if (this._rot90) {
                r_height = this._service.r_width(this._context);
            } else {
                r_height = this._service.r_height(this._context);
            }
            return r_height;
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int width() {
        int r_width;
        try {
            if (this._rot90) {
                r_width = this._service.r_height(this._context);
            } else {
                r_width = this._service.r_width(this._context);
            }
            return r_width;
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int displayWidth() {
        int r_displayWidth;
        try {
            if (this._rot90) {
                r_displayWidth = this._service.r_displayHeight(this._context);
            } else {
                r_displayWidth = this._service.r_displayWidth(this._context);
            }
            return r_displayWidth;
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfo
    public int displayHeight() {
        int r_displayHeight;
        try {
            if (this._rot90) {
                r_displayHeight = this._service.r_displayWidth(this._context);
            } else {
                r_displayHeight = this._service.r_displayHeight(this._context);
            }
            return r_displayHeight;
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    public Boolean isInterlaced() {
        Boolean bool = null;
        try {
            int val = this._service.r_isInterlaced(this._context);
            if (val == 1) {
                bool = true;
            } else if (val == 0) {
                bool = false;
            }
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
        }
        return bool;
    }

    private String getFormat(int mode) {
        try {
            return this._service.r_getFormat(this._context, mode);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return null;
        }
    }

    private String getMetadata(int key) {
        try {
            return this._service.r_getMetadata(this._context, key, LocaleUtils.getIOS3DefaultLanguage());
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return null;
        }
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public boolean hasEmbeddedSubtitle() {
        try {
            return this._service.r_hasEmbeddedSubtitle(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return false;
        }
    }

    public Bitmap extractThumb(int width, int height, int iteration, boolean allowBlanc) {
        try {
            if (this._rot90) {
                width = height;
                height = width;
            }
            Bitmap thumb = this._service.r_extractThumb(this._context, width, height, iteration, allowBlanc);
            if (thumb != null && this._rotationDegrees != 0) {
                Matrix m = new Matrix();
                m.setRotate(this._rotationDegrees);
                return Bitmap.createBitmap(thumb, 0, 0, width, height, m, false);
            }
            return thumb;
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return null;
        }
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int getStreamCount() {
        try {
            return this._service.r_getStreamCount(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public IStreamInfo streamInfo(int streamIndex) {
        return new StreamInfo(streamIndex);
    }

    /* loaded from: classes2.dex */
    private class StreamInfo implements IStreamInfo {
        private final int _index;

        StreamInfo(int index) {
            this._index = index;
        }

        @Override // com.mxtech.media.IMediaInfo
        public void close() {
        }

        private String getStreamMetadata(int key) {
            try {
                return FFReader.this._service.r_getStreamMetadata(FFReader.this._context, this._index, key, LocaleUtils.getIOS3DefaultLanguage());
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return null;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public boolean isValid() {
            return true;
        }

        @Override // com.mxtech.media.IMediaInfo
        public String description() {
            return getStreamMetadata(103);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String album() {
            return getStreamMetadata(1);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String artist() {
            return getStreamMetadata(2);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String composer() {
            return getStreamMetadata(4);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String genre() {
            return getStreamMetadata(6);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String title() {
            return getStreamMetadata(7);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String mimeType() {
            return getStreamMetadata(12);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String albumArtist() {
            return getStreamMetadata(13);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String copyright() {
            return getStreamMetadata(14);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoder() {
            return getStreamMetadata(15);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String encoded_by() {
            return getStreamMetadata(16);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String performer() {
            return getStreamMetadata(17);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String publisher() {
            return getStreamMetadata(18);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String year() {
            return getStreamMetadata(5);
        }

        @Override // com.mxtech.media.IMediaInfo
        public Locale[] locales() {
            String langs = getStreamMetadata(102);
            return (langs == null || langs.length() == 0 || LocaleUtils.UNDEFINED.equalsIgnoreCase(langs)) ? new Locale[0] : LocaleUtils.parseLocales(langs);
        }

        @Override // com.mxtech.media.IMediaInfo
        public String displayLocales() {
            return FFReader.toDisplayLocales(locales());
        }

        @Override // com.mxtech.media.IMediaInfo
        public int duration() {
            return FFReader.this.duration();
        }

        @Override // com.mxtech.media.IMediaInfo
        public int width() {
            try {
                return FFReader.this._rot90 ? FFReader.this._service.r_getStreamHeight(FFReader.this._context, this._index) : FFReader.this._service.r_getStreamWidth(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IMediaInfo
        public int height() {
            try {
                return FFReader.this._rot90 ? FFReader.this._service.r_getStreamWidth(FFReader.this._context, this._index) : FFReader.this._service.r_getStreamHeight(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayWidth() {
            try {
                return FFReader.this._rot90 ? FFReader.this._service.r_getStreamDisplayHeight(FFReader.this._context, this._index) : FFReader.this._service.r_getStreamDisplayWidth(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IMediaInfo
        public int displayHeight() {
            try {
                return FFReader.this._rot90 ? FFReader.this._service.r_getStreamDisplayWidth(FFReader.this._context, this._index) : FFReader.this._service.r_getStreamDisplayHeight(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int type() {
            try {
                return FFReader.this._service.r_getStreamType(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int disposition() {
            try {
                return FFReader.this._service.r_getStreamDisposition(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IMediaInfo
        public String format() {
            try {
                return FFReader.this._service.r_getStreamCodec(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return null;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public String profile() {
            try {
                return FFReader.this._service.r_getStreamProfile(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return null;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int frameTime() {
            try {
                return FFReader.this._service.r_getStreamFrameTime(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int bitRate() {
            try {
                return FFReader.this._service.r_getStreamBitRate(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int sampleRate() {
            try {
                return FFReader.this._service.r_getStreamSampleRate(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public long channelLayout() {
            try {
                return FFReader.this._service.r_getStreamChannelLayout(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0L;
            }
        }

        @Override // com.mxtech.media.IStreamInfo
        public int channelCount() {
            try {
                return FFReader.this._service.r_getStreamChannelCount(FFReader.this._context, this._index);
            } catch (RemoteException e) {
                Log.e(App.TAG, "", e);
                return 0;
            }
        }
    }

    @Override // com.mxtech.media.IMediaInfoAux
    public int[] getStreamTypes() {
        try {
            return this._service.r_getStreamTypes(this._context);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return new int[0];
        }
    }

    public int getStreamCodecId(int index) {
        try {
            return this._service.r_getStreamCodecId(this._context, index);
        } catch (RemoteException e) {
            Log.e(App.TAG, "", e);
            return 0;
        }
    }
}
