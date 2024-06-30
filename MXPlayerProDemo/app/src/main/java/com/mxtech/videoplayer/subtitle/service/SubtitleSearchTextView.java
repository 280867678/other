package com.mxtech.videoplayer.subtitle.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import com.mxtech.StringUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.videoplayer.widget.PersistentTextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public final class SubtitleSearchTextView extends PersistentTextView {
    private static final int MIN_CONSTRAINT_LENGTH = 1;
    private static final String TAG = App.TAG + ".SubtitleSearchTextView";
    private Map<String, Boolean> _predefinedCandidates;
    private OpenSubtitles _service;
    private Toast _toast;

    public SubtitleSearchTextView(Context context) {
        super(context);
    }

    public SubtitleSearchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubtitleSearchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public synchronized void addPredefinedCandidate(String title, boolean showAlways) {
        if (this._predefinedCandidates == null) {
            this._predefinedCandidates = new ArrayMap();
        }
        this._predefinedCandidates.put(title, Boolean.valueOf(showAlways));
    }

    @Override // com.mxtech.videoplayer.widget.PersistentTextView, com.mxtech.widget.FilterTextProvider
    public String[] runQuery(CharSequence constraint) {
        List<String> predefined;
        String prefix = constraint.toString().trim();
        String[] titles = null;
        List<String> predefined2 = null;
        synchronized (this) {
            try {
                if (this._predefinedCandidates != null) {
                    List<String> predefined3 = null;
                    for (Map.Entry<String, Boolean> p : this._predefinedCandidates.entrySet()) {
                        try {
                            String text = p.getKey();
                            Boolean showAlways = p.getValue();
                            if (showAlways.booleanValue() || StringUtils.startsWithIgnoreCase(text, prefix)) {
                                predefined = predefined3 == null ? new ArrayList<>() : predefined3;
                                predefined.add(text);
                            } else {
                                predefined = predefined3;
                            }
                            predefined3 = predefined;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                    predefined2 = predefined3;
                }
                if (prefix.length() >= 1) {
                    titles = super.runQuery(prefix);
                    List<String> found = TitleSuggestionCache.search(prefix, false);
                    if (found != null) {
                        return merge(predefined2, titles, found);
                    }
                    try {
                        List<String> found2 = searchOpenSubtitles(prefix);
                        if (this._toast != null) {
                            this._toast.cancel();
                        }
                        if (found2 != null) {
                            return merge(predefined2, titles, found2);
                        }
                    } catch (SubtitleService.UnauthorizedException e) {
                        Log.e(TAG, "", e);
                        CharSequence message = SubtitleServiceManager.getErrorMessage(e, "opensubtitles.org");
                        if (message != null) {
                            if (this._toast == null) {
                                this._toast = Toast.makeText(getContext(), message, 0);
                            } else {
                                this._toast.setText(message);
                            }
                            this._toast.show();
                        }
                    } catch (SubtitleService.SubtitleServiceException e2) {
                        Log.e(TAG, "", e2);
                        List<String> found3 = TitleSuggestionCache.search(prefix, true);
                        if (found3 != null) {
                            return merge(predefined2, titles, found3);
                        }
                    }
                }
                return predefined2 != null ? merge(predefined2, titles, null) : titles;
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    @Nullable
    private List<String> searchOpenSubtitles(String prefix) throws SubtitleService.SubtitleServiceException {
        if (this._service == null) {
            this._service = new OpenSubtitles();
        }
        List<String> found = this._service.searchMoviesStartsWith(prefix);
        TitleSuggestionCache.put(prefix, found);
        if (found.size() > 0) {
            return found;
        }
        return null;
    }

    private String[] merge(@Nullable List<String> list0, @Nullable String[] list1, @Nullable List<String> list2) {
        List<String> merged = new ArrayList<>();
        if (list0 != null) {
            merged.addAll(list0);
        }
        if (list1 != null) {
            for (String s : list1) {
                if (!merged.contains(s)) {
                    merged.add(s);
                }
            }
        }
        if (list2 != null) {
            for (String s2 : list2) {
                if (!merged.contains(s2)) {
                    merged.add(s2);
                }
            }
        }
        return (String[]) merged.toArray(new String[merged.size()]);
    }
}
