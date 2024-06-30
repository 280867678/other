package com.mxtech.videoplayer.subtitle.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mxtech.app.DialogPlatform;
import com.mxtech.os.AsyncTaskInteractive;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public final class TitleSearcher implements DialogInterface.OnShowListener, View.OnClickListener, TextWatcher {
    private static final String TAG = App.TAG + ".TitleSearcher";
    private AlertDialog _dialog;
    private final DialogPlatform _dialogPlatform;
    private final TitleCreationListener _listener;
    private final Media _media;
    private View _okButton;
    private AsyncTaskInteractive<Void, Void, Object> _searchTask;
    private final SubtitleService _service;
    private final SubtitleSearchTextView _titleInput;
    private final TextView _warningView;

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"InflateParams"})
    public TitleSearcher(SubtitleService service, DialogPlatform dialogPlatform, Media media, TitleCreationListener listener) {
        this._service = service;
        this._dialogPlatform = dialogPlatform;
        this._media = media;
        this._listener = listener;
        Context context = dialogPlatform.getContext();
        this._dialog = new AlertDialog.Builder(context).setTitle(R.string.search_title).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        View layout = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_upload_search_title, (ViewGroup) null);
        this._warningView = (TextView) layout.findViewById(R.id.warning);
        this._titleInput = (SubtitleSearchTextView) layout.findViewById(R.id.title);
        if (this._media.title != null) {
            this._titleInput.setText(this._media.title);
            this._titleInput.addPredefinedCandidate(this._media.title, false);
        }
        this._titleInput.addTextChangedListener(this);
        TextViewUtils.makeClearable((ViewGroup) this._titleInput.getParent(), this._titleInput, (ImageView) layout.findViewById(R.id.clear_btn));
        this._dialog.setView(layout);
        this._dialog.setOnShowListener(this);
        dialogPlatform.showDialog(this._dialog);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        if (this._dialog != null) {
            this._dialog.dismiss();
        }
        if (this._searchTask != null) {
            this._searchTask.cancel(true);
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        this._okButton = ((AlertDialog) dialog).getButton(-1);
        this._okButton.setOnClickListener(this);
        this._okButton.setEnabled(this._titleInput.getText().toString().trim().length() > 0);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this._okButton.setEnabled(s.toString().trim().length() > 0);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (!this._dialogPlatform.isFinishing()) {
            final String title = this._titleInput.getText().toString().trim();
            if (title.length() > 0) {
                new AsyncTaskInteractive<Void, Void, Object>(this._dialogPlatform, R.string.searching_movies) { // from class: com.mxtech.videoplayer.subtitle.service.TitleSearcher.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onPreExecute() {
                        super.onPreExecute();
                        TitleSearcher.this._searchTask = this;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Object doInBackground(Void... params) {
                        try {
                            return TitleSearcher.this._service.searchMovies(title);
                        } catch (Exception e) {
                            Log.w(TitleSearcher.TAG, "", e);
                            return e;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onCancelled() {
                        super.onCancelled();
                        TitleSearcher.this._searchTask = null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onPostExecute(Object result) {
                        CharSequence message;
                        super.onPostExecute(result);
                        TitleSearcher.this._searchTask = null;
                        if (result instanceof List) {
                            TitleSearcher.this._titleInput.save();
                            List<MovieCandidate> movies = (List) result;
                            if (movies.size() > 0) {
                                TitleSearcher.this._listener.onNewTitles(movies);
                                TitleSearcher.this._dialog.dismiss();
                                return;
                            }
                            TitleSearcher.this.setWarning(R.string.error_no_matching_movies);
                        } else if ((result instanceof SubtitleService.SubtitleServiceException) && (message = SubtitleServiceManager.getErrorMessage((SubtitleService.SubtitleServiceException) result, TitleSearcher.this._service.name(), null, null)) != null) {
                            TitleSearcher.this.setWarning(message);
                        }
                    }
                }.executeParallel(new Void[0]);
                setWarning((CharSequence) null);
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
}
