package com.mxtech.videoplayer.subtitle.service;

import android.annotation.TargetApi;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.mxtech.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@TargetApi(9)
/* loaded from: classes.dex */
public final class TitleSuggestionCache {
    private static final int CACHING_TIME = 600000;
    private static final TreeMap<String, CacheEntry> _cache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class CacheEntry {
        final long time = SystemClock.uptimeMillis();
        final List<String> titles;

        CacheEntry(List<String> titles) {
            this.titles = titles;
        }
    }

    public static synchronized void put(String prefix, List<String> titles) {
        synchronized (TitleSuggestionCache.class) {
            _cache.put(prefix, new CacheEntry(titles));
        }
    }

    public static synchronized void onLowMemory() {
        synchronized (TitleSuggestionCache.class) {
            _cache.clear();
        }
    }

    @Nullable
    public static synchronized List<String> search(String prefix, boolean allowPartial) {
        List<String> found;
        List<String> upper;
        synchronized (TitleSuggestionCache.class) {
            long now = SystemClock.uptimeMillis();
            found = find_l(prefix, now);
            if (found == null) {
                if (allowPartial && (upper = findUpperlevel_l(prefix, now)) != null) {
                    List<String> deduced = new ArrayList<>();
                    for (String title : upper) {
                        if (!StringUtils.startsWithIgnoreCase(title, prefix)) {
                            break;
                        }
                        deduced.add(title);
                    }
                    found = deduced;
                } else {
                    found = null;
                }
            }
        }
        return found;
    }

    @Nullable
    static List<String> find_l(String prefix, long now) {
        CacheEntry found = _cache.get(prefix);
        if (found != null) {
            if (now < found.time + 600000) {
                return found.titles;
            }
            _cache.remove(prefix);
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x000d, code lost:
        return null;
     */
    @Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    static List<String> findUpperlevel_l(String prefix, long now) {
        int prefixLen = prefix.length();
        String last = prefix;
        while (true) {
            Map.Entry<String, CacheEntry> entry = _cache.lowerEntry(last);
            if (entry == null) {
                break;
            }
            String cachePrefix = entry.getKey();
            CacheEntry cacheEntry = entry.getValue();
            if (now >= cacheEntry.time + 600000) {
                _cache.remove(cachePrefix);
            } else if (prefixLen <= cachePrefix.length()) {
                last = cachePrefix;
            } else if (StringUtils.startsWithIgnoreCase(prefix, cachePrefix)) {
                return cacheEntry.titles;
            }
        }
    }
}
