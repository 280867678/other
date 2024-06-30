package com.mxtech.videoplayer;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.mxtech.FileUtils;
import com.mxtech.ImageUtils;
import com.mxtech.videoplayer.preference.P;
import java.io.File;
import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public final class ThumbCache {
    private static final int MB = 1048576;
    public static final String TAG = App.TAG + ".ThumbCache";
    private static int capacity;
    private int _activeCount;
    @Nullable
    private CacheImpl _impl;
    private SoftReference<CacheImpl> _implRef;

    static {
        long capacity2;
        long totalRam = Runtime.getRuntime().maxMemory();
        if (Build.VERSION.SDK_INT < 12) {
            capacity2 = ((float) totalRam) / 4.8f;
        } else {
            capacity2 = totalRam / 4;
        }
        if (capacity2 > 2147483647L) {
            capacity = Integer.MAX_VALUE;
        } else {
            capacity = (int) capacity2;
        }
        Log.i(TAG, "Runtime.maxMemory: " + (totalRam / 1048576) + "MB Cache capacity: " + (capacity2 / 1048576) + "MB");
    }

    /* loaded from: classes.dex */
    public static class CacheEntry {
        final File coverFile;
        final int coverFileSize;
        final long coverFileTime;
        final ThumbDrawable drawable;
        final int shapedSize;

        CacheEntry(ThumbDrawable drawable, File coverFile) {
            this.drawable = drawable;
            this.shapedSize = ImageUtils.getSize(drawable.getBitmap());
            this.coverFile = coverFile;
            if (coverFile != null) {
                this.coverFileSize = (int) coverFile.length();
                this.coverFileTime = coverFile.lastModified();
                return;
            }
            this.coverFileSize = 0;
            this.coverFileTime = 0L;
        }
    }

    public synchronized void put(Uri key, ThumbDrawable drawable, File coverFile) {
        if (this._impl != null) {
            this._impl.put(key, new CacheEntry(drawable, coverFile));
        }
    }

    @Nullable
    public synchronized ThumbDrawable get(Uri key, File coverFile) {
        CacheEntry entry;
        ThumbDrawable thumbDrawable = null;
        synchronized (this) {
            if (this._impl != null && (entry = this._impl.get(key)) != null) {
                ThumbDrawable drawable = entry.drawable;
                if (!FileUtils.equals(entry.coverFile, coverFile)) {
                    this._impl.remove(key);
                } else if (coverFile != null && (entry.coverFileSize != coverFile.length() || entry.coverFileTime != coverFile.lastModified())) {
                    this._impl.remove(key);
                } else if (P.list_duration_display != 2 && drawable.hasDuration) {
                    this._impl.remove(key);
                } else {
                    if (P.list_duration_display == 2) {
                        if (drawable.hasThumb && drawable.hasDuration) {
                            drawable.shrink();
                        }
                    } else if (drawable.hasThumb) {
                        drawable.shrink();
                    }
                    thumbDrawable = entry.drawable;
                }
            }
        }
        return thumbDrawable;
    }

    public synchronized void clear() {
        if (this._impl != null) {
            this._impl.evictAll();
        } else {
            CacheImpl impl = getInternalCacheNoCreate_l();
            if (impl != null) {
                impl.evictAll();
            }
        }
    }

    public synchronized void remove(Uri key) {
        if (this._impl != null) {
            this._impl.remove(key);
        } else {
            CacheImpl impl = getInternalCacheNoCreate_l();
            if (impl != null) {
                impl.remove(key);
            }
        }
    }

    public synchronized void activate() {
        int i = this._activeCount + 1;
        this._activeCount = i;
        if (i == 1) {
            this._impl = getInternalCacheNoCreate_l();
            if (this._impl == null) {
                this._impl = new CacheImpl(capacity);
                this._implRef = new SoftReference<>(this._impl);
            }
        }
    }

    public synchronized void deactiavte() {
        int i = this._activeCount - 1;
        this._activeCount = i;
        if (i == 0) {
            this._impl = null;
        }
    }

    private CacheImpl getInternalCacheNoCreate_l() {
        if (this._implRef != null) {
            return this._implRef.get();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CacheImpl extends LruCache<Uri, CacheEntry> {
        private CacheImpl(int maxSize) {
            super(maxSize);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.support.v4.util.LruCache
        public int sizeOf(Uri key, CacheEntry value) {
            return value.shapedSize;
        }
    }
}
