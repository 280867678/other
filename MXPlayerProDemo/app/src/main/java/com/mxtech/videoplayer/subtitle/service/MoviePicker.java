package com.mxtech.videoplayer.subtitle.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.mxtech.StringUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public class MoviePicker implements DialogInterface.OnShowListener, DialogInterface.OnCancelListener, DialogInterface.OnClickListener, View.OnClickListener, AdapterView.OnItemClickListener, TitleCreationListener {
    private final ArrayAdapter<CharSequence> _adapter;
    private final List<MovieCandidate> _candidates = new ArrayList();
    private final AlertDialog _dialog;
    private final DialogPlatform _dialogPlatform;
    @Nullable
    private ListView _listView;
    private final Listener _listener;
    private final Media _media;
    @Nullable
    private View _newTitleButton;
    @Nullable
    private View _okButton;
    @Nullable
    private View _searchTitleButton;
    private final SubtitleService _service;
    @Nullable
    private TitleCreator _titleCreator;
    @Nullable
    private TitleSearcher _titleSearcher;
    private final TextView _warningView;

    /* loaded from: classes2.dex */
    interface Listener {
        void onCancelled(MoviePicker moviePicker);

        void onTitleSelected(MoviePicker moviePicker, long j, String str, int i, int i2, int i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"InflateParams"})
    public MoviePicker(SubtitleService service, DialogPlatform dialogPlatform, Media media, List<MovieCandidate> candidates, Listener listener) {
        this._candidates.addAll(candidates);
        this._service = service;
        this._dialogPlatform = dialogPlatform;
        this._media = media;
        this._listener = listener;
        Context context = dialogPlatform.getContext();
        this._dialog = new AlertDialog.Builder(context).setTitle(StringUtils.getStringItalic_s(R.string.movie_for, media.displayName)).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, this).create();
        View layout = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_upload_pickup_title, (ViewGroup) null);
        this._listView = (ListView) layout.findViewById(16908298);
        this._newTitleButton = layout.findViewById(R.id.new_title);
        this._searchTitleButton = layout.findViewById(R.id.search_title);
        this._warningView = (TextView) layout.findViewById(R.id.warning);
        this._adapter = new ArrayAdapter<>(context, R.layout.subtitle_upload_pickup_title_item);
        for (MovieCandidate candidate : candidates) {
            this._adapter.add(getMovieText(candidate));
        }
        this._listView.setChoiceMode(1);
        this._listView.setAdapter((ListAdapter) this._adapter);
        this._listView.setOnItemClickListener(this);
        this._searchTitleButton.setOnClickListener(this);
        this._newTitleButton.setOnClickListener(this);
        this._dialog.setOnCancelListener(this);
        this._dialog.setOnShowListener(this);
        this._dialog.setView(layout);
        dialogPlatform.showDialog(this._dialog);
    }

    private CharSequence getMovieText(MovieCandidate movie) {
        return getMovieText(movie.title, movie.year, movie.season, movie.episode);
    }

    private CharSequence getMovieText(String title, int year, int season, int episode) {
        if (year > 0 || season > 0 || episode >= 0) {
            SpannableStringBuilder sb = new SpannableStringBuilder(title);
            int spanStart = sb.length();
            if (year > 0) {
                sb.append((CharSequence) " (").append((CharSequence) Integer.toString(year)).append(')');
            }
            if (season > 0 || episode >= 0) {
                sb.append('\n');
                if (season > 0) {
                    sb.append((CharSequence) StringUtils.getString_s(R.string.season_with_number, Integer.valueOf(season)));
                }
                if (episode >= 0) {
                    if (season > 0) {
                        sb.append(' ');
                    }
                    sb.append((CharSequence) StringUtils.getString_s(R.string.episode_with_number, Integer.valueOf(episode)));
                }
            }
            int spanEnd = sb.length();
            sb.setSpan(L.getSecondaryColorSizeSpan(), spanStart, spanEnd, 33);
            return sb;
        }
        return title;
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        this._okButton = ((AlertDialog) dialog).getButton(-1);
        this._okButton.setOnClickListener(this);
        this._okButton.setEnabled(false);
        if (this._candidates.size() == 0 && !this._dialogPlatform.isFinishing()) {
            setWarning(StringUtils.getStringItalic_s(R.string.request_title, this._media.displayName));
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this._okButton.setEnabled(this._listView.getCheckedItemPosition() >= 0);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.TitleCreationListener
    public void onNewTitle(MovieCandidate movie) {
        if (!this._candidates.contains(movie)) {
            this._candidates.add(movie);
            this._adapter.add(getMovieText(movie));
            setWarning((CharSequence) null);
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.TitleCreationListener
    public void onNewTitles(List<MovieCandidate> movies) {
        for (MovieCandidate movie : movies) {
            if (!this._candidates.contains(movie)) {
                this._candidates.add(movie);
                this._adapter.add(getMovieText(movie));
                setWarning((CharSequence) null);
            }
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        this._listener.onCancelled(this);
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialog) {
        this._listener.onCancelled(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (!this._dialogPlatform.isFinishing()) {
            if (v == this._okButton) {
                int index = this._listView.getCheckedItemPosition();
                if (index >= 0) {
                    MovieCandidate movie = this._candidates.get(index);
                    this._listener.onTitleSelected(this, movie.id, movie.title, movie.year, movie.season, movie.episode);
                    this._dialog.dismiss();
                }
            } else if (v == this._searchTitleButton) {
                if (!this._dialogPlatform.getDialogRegistry().hasDialogAfter(this._dialog)) {
                    this._titleSearcher = new TitleSearcher(this._service, this._dialogPlatform, this._media, this);
                }
            } else if (v == this._newTitleButton && !this._dialogPlatform.getDialogRegistry().hasDialogAfter(this._dialog)) {
                if (SubtitleServiceManager.hasCredential(this._service.name())) {
                    this._titleCreator = new TitleCreator(this._service, this._dialogPlatform, this._media, this);
                } else {
                    setWarning(StringUtils.getStringItalic_s(R.string.need_login_to_create_new_title, this._service.name()));
                }
            }
        }
    }

    void setWarning(int resId) {
        setWarning(this._dialogPlatform.getContext().getString(resId));
    }

    void setWarning(@Nullable CharSequence text) {
        if (text != null && text.length() > 0) {
            this._warningView.setText(text);
            this._warningView.setVisibility(0);
            return;
        }
        this._warningView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        if (this._titleCreator != null) {
            this._titleCreator.cancel();
        }
        if (this._titleSearcher != null) {
            this._titleSearcher.cancel();
        }
        this._dialog.dismiss();
    }
}
