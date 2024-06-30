package com.mxtech.videoplayer.subtitle.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import com.mxtech.ViewUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.os.AsyncTaskInteractive;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.videoplayer.widget.PersistentTextView;
import java.util.Calendar;

/* JADX INFO: Access modifiers changed from: package-private */
@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public final class TitleCreator implements DialogInterface.OnShowListener, View.OnClickListener {
    private static final String TAG = App.TAG + ".TitleCreator";
    private static final int YEAR_LAST = Calendar.getInstance().get(1);
    private static final int YEAR_NONE = 1879;
    private AlertDialog _dialog;
    private final DialogPlatform _dialogPlatform;
    private final View _episodeInput;
    private final TitleCreationListener _listener;
    private final Media _media;
    private final boolean _numberPickers;
    private View _okButton;
    private AsyncTaskInteractive<Void, Void, Object> _registeringTask;
    private final View _seasonInput;
    private final SubtitleService _service;
    private final PersistentTextView _titleInput;
    private final TextView _warningView;
    private final View _yearInput;

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"InflateParams"})
    public TitleCreator(SubtitleService service, DialogPlatform dialogPlatform, Media media, TitleCreationListener listener) {
        this._service = service;
        this._dialogPlatform = dialogPlatform;
        this._media = media;
        this._listener = listener;
        Context context = dialogPlatform.getContext();
        this._dialog = new AlertDialog.Builder(context).setTitle(R.string.new_title).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        View layout = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_upload_new_title, (ViewGroup) null);
        this._warningView = (TextView) layout.findViewById(R.id.warning);
        this._titleInput = (PersistentTextView) layout.findViewById(R.id.title);
        this._yearInput = layout.findViewById(R.id.year);
        this._seasonInput = layout.findViewById(R.id.season);
        this._episodeInput = layout.findViewById(R.id.episode);
        this._numberPickers = this._yearInput instanceof NumberPicker;
        TextViewUtils.makeClearable((ViewGroup) this._titleInput.getParent(), this._titleInput, (ImageView) layout.findViewById(R.id.clear_btn));
        final String yearText = context.getString(R.string.detail_year);
        final String seasonText = context.getString(R.string.season);
        final String episodeText = context.getString(R.string.episode);
        if (this._numberPickers) {
            NumberPicker yearPicker = (NumberPicker) this._yearInput;
            yearPicker.setMinValue(YEAR_NONE);
            yearPicker.setMaxValue(YEAR_LAST);
            yearPicker.setFormatter(new NumberPicker.Formatter() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.1
                @Override // android.widget.NumberPicker.Formatter
                public String format(int value) {
                    return value == TitleCreator.YEAR_NONE ? yearText : Integer.toString(value);
                }
            });
            yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.2
                @Override // android.widget.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    TitleCreator.this.onValueChanged(null, newVal);
                }
            });
            NumberPicker seasonPicker = (NumberPicker) this._seasonInput;
            seasonPicker.setMinValue(0);
            seasonPicker.setMaxValue(99);
            seasonPicker.setFormatter(new NumberPicker.Formatter() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.3
                @Override // android.widget.NumberPicker.Formatter
                public String format(int value) {
                    return value == 0 ? seasonText : Integer.toString(value);
                }
            });
            NumberPicker episodePicker = (NumberPicker) this._episodeInput;
            episodePicker.setMinValue(0);
            episodePicker.setMaxValue(100);
            episodePicker.setFormatter(new NumberPicker.Formatter() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.4
                @Override // android.widget.NumberPicker.Formatter
                public String format(int value) {
                    return value == 0 ? episodeText : Integer.toString(value - 1);
                }
            });
        } else {
            AppCompatSpinner yearSpinner = (AppCompatSpinner) this._yearInput;
            AppCompatSpinner seasonSpinner = (AppCompatSpinner) this._seasonInput;
            AppCompatSpinner episodeSpinner = (AppCompatSpinner) this._episodeInput;
            CharSequence[] years = new CharSequence[(YEAR_LAST - 1879) + 1];
            years[0] = yearText;
            int year = YEAR_LAST;
            int i = 1;
            while (year > YEAR_NONE) {
                years[i] = Integer.toString(year);
                year--;
                i++;
            }
            ViewUtils.setSpinnerEntries(yearSpinner, years);
            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.5
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TitleCreator.this.onValueChanged(null, 0);
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                    TitleCreator.this.onValueChanged(null, 0);
                }
            });
            CharSequence[] zero_to_99 = new CharSequence[100];
            for (int i2 = 0; i2 < 100; i2++) {
                zero_to_99[i2] = Integer.toString(i2);
            }
            CharSequence[] seasons = new CharSequence[100];
            seasons[0] = seasonText;
            for (int i3 = 1; i3 <= 99; i3++) {
                seasons[i3] = zero_to_99[i3];
            }
            ViewUtils.setSpinnerEntries(seasonSpinner, seasons);
            CharSequence[] episodes = new CharSequence[101];
            episodes[0] = episodeText;
            for (int i4 = 1; i4 <= 100; i4++) {
                episodes[i4] = zero_to_99[i4 - 1];
            }
            ViewUtils.setSpinnerEntries(episodeSpinner, episodes);
        }
        if (this._media.title != null) {
            this._titleInput.setText(this._media.title);
        }
        this._titleInput.addTextChangedListener(new TextWatcher() { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.6
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TitleCreator.this.onValueChanged(s, 0);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        this._dialog.setOnShowListener(this);
        this._dialog.setView(layout);
        dialogPlatform.showDialog(this._dialog);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        if (this._dialog != null) {
            this._dialog.dismiss();
        }
        if (this._registeringTask != null) {
            this._registeringTask.cancel(true);
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        this._okButton = ((AlertDialog) dialog).getButton(-1);
        this._okButton.setOnClickListener(this);
        this._okButton.setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onValueChanged(@Nullable CharSequence title, int year) {
        boolean z = false;
        if (title == null) {
            title = this._titleInput.getText();
        }
        if (year == 0) {
            year = valueFrom(this._yearInput, 0);
        }
        View view = this._okButton;
        if (title.toString().trim().length() > 0 && YEAR_NONE < year && year <= YEAR_LAST) {
            z = true;
        }
        view.setEnabled(z);
    }

    private int valueFrom(Object o, int defaultValue) {
        if (o instanceof CharSequence) {
            try {
                String text = o.toString().trim();
                if (text.length() > 0) {
                    return Integer.parseInt(text);
                }
            } catch (NumberFormatException e) {
            }
        }
        if (o instanceof NumberPicker) {
            if (o == this._episodeInput) {
                return ((NumberPicker) o).getValue() - 1;
            }
            return ((NumberPicker) o).getValue();
        } else if (o instanceof Spinner) {
            return valueFrom(((Spinner) o).getSelectedItem(), defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this._registeringTask == null && !this._dialogPlatform.isFinishing()) {
            final String title = this._titleInput.getText().toString().trim();
            final int year = valueFrom(this._yearInput, 0);
            final int season = valueFrom(this._seasonInput, 0);
            final int episode = valueFrom(this._episodeInput, -1);
            if (title.length() > 0 && YEAR_NONE < year && year <= YEAR_LAST) {
                new AsyncTaskInteractive<Void, Void, Object>(this._dialogPlatform, R.string.registering_title) { // from class: com.mxtech.videoplayer.subtitle.service.TitleCreator.7
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onPreExecute() {
                        super.onPreExecute();
                        TitleCreator.this._registeringTask = this;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Object doInBackground(Void... params) {
                        try {
                            return Long.valueOf(TitleCreator.this._service.registerMovie(TitleCreator.this._media, title, year, season, episode));
                        } catch (Exception e) {
                            Log.w(TitleCreator.TAG, "", e);
                            return e;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onCancelled() {
                        super.onCancelled();
                        TitleCreator.this._registeringTask = null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.mxtech.os.AsyncTaskInteractive, android.os.AsyncTask
                    public void onPostExecute(Object result) {
                        CharSequence message;
                        super.onPostExecute(result);
                        TitleCreator.this._registeringTask = null;
                        if (result instanceof Long) {
                            TitleCreator.this._titleInput.save();
                            TitleCreator.this._listener.onNewTitle(new MovieCandidate(((Long) result).longValue(), title, year, season, episode));
                            if (TitleCreator.this._dialog != null) {
                                TitleCreator.this._dialog.dismiss();
                            }
                        } else if ((result instanceof SubtitleService.SubtitleServiceException) && (message = SubtitleServiceManager.getErrorMessage((SubtitleService.SubtitleServiceException) result, TitleCreator.this._service.name(), null, null)) != null) {
                            TitleCreator.this.setWarning(message);
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
