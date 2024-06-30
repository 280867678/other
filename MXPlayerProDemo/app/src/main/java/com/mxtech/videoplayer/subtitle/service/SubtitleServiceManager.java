package com.mxtech.videoplayer.subtitle.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mxtech.FileUtils;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.app.AppCompatProgressDialog;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogPlatform;
import com.mxtech.app.DialogPlatformWrapper;
import com.mxtech.database.DataSetObservable2;
import com.mxtech.os.AsyncTask2;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.text.TextViewUtils;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.service.SiteSelector;
import com.mxtech.videoplayer.subtitle.service.SubtitleFinder;
import com.mxtech.videoplayer.subtitle.service.SubtitleService;
import com.mxtech.videoplayer.subtitle.service.SubtitleUploader;
import com.mxtech.videoplayer.widget.LocaleMultiSelector;
import com.mxtech.videoplayer.widget.LocaleSingleSelector;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/* loaded from: classes.dex */
public final class SubtitleServiceManager implements SubtitleFinder.IListener, DialogInterface.OnDismissListener {
    public static final int CAP_RATE = 2;
    public static final int CAP_SEARCH = 1;
    public static final int CAP_UPLOAD = 4;
    private static final int DIALOG = 1;
    public static final int FLAG_DOWNLOADING_TOAST = 8;
    public static final int FLAG_DOWNLOAD_ERROR_DIALOG = 128;
    public static final int FLAG_DOWNLOAD_ERROR_TOAST = 64;
    public static final int FLAG_DOWNLOAD_SUCCESS_DIALOG = 32;
    public static final int FLAG_DOWNLOAD_SUCCESS_TOAST = 16;
    public static final int FLAG_SEARCH_ERROR_DIALOG = 4;
    public static final int FLAG_SEARCH_ERROR_TOAST = 2;
    public static final int FLAG_SEARCH_SHOW_PROGRESS_DIALOG = 1;
    private static final int IGNORE = 0;
    private static final int PROCESS_CONFIRM = 0;
    private static final int PROCESS_DOWNLOAD = 2;
    private static final int PROCESS_SEARCH = 1;
    private static final int TOAST = 2;
    private AlertDialog _confirmDialog;
    private final Context _context;
    private final DialogPlatformWrapper _dialogPlatform;
    private boolean _everSearchSucceeded;
    @Nullable
    private SubtitleFinder _finder;
    private final int _flags;
    @Nullable
    private Locale[] _locales;
    private final HashMap<Object, SearchResultHandler> _mediaHandlers = new HashMap<>();
    private Media[] _medias;
    @Nullable
    private OnDownloadListener _onDownloadListener;
    private AppCompatProgressDialog _progressDialog;
    private AlertDialog _resultDialog;
    @Nullable
    private String[] _sites;
    private Toast _toast;
    private static final String TAG = App.TAG + ".SubtitleServiceManager";
    private static final StyleSpan ITALIC_SPAN = new StyleSpan(2);

    /* loaded from: classes.dex */
    public interface OnDownloadListener {
        void onDowloadBegin(SubtitleServiceManager subtitleServiceManager);

        void onDownloadEnd(SubtitleServiceManager subtitleServiceManager);

        void onDwonloaded(SubtitleServiceManager subtitleServiceManager, @Nullable Media media, @Nullable String str, File file);
    }

    /* loaded from: classes.dex */
    public static class MediaSubtitle {
        public final Media media;
        public final Subtitle subtitle;

        public MediaSubtitle(Media media, Subtitle subtitle) {
            this.media = media;
            this.subtitle = subtitle;
        }
    }

    public static int getCapabilities(@Nullable Media media, int numberOfExternalSubs) {
        int caps = OpenSubtitles.getCapabilities(media);
        if (numberOfExternalSubs == 0) {
            return caps & (-7);
        }
        return caps;
    }

    public SubtitleServiceManager(DialogPlatform dialogPlatform, int flags) {
        this._context = dialogPlatform.getContext();
        this._dialogPlatform = new DialogPlatformWrapper(dialogPlatform, dialogPlatform.getDialogRegistry().newSubRegistry());
        this._flags = flags;
    }

    public void setOnDownloadListener(@Nullable OnDownloadListener onDownloadCompleteListener) {
        this._onDownloadListener = onDownloadCompleteListener;
    }

    public void search(Media... medias) {
        this._everSearchSucceeded = false;
        this._medias = medias;
        new SearchConfirmHandler();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* loaded from: classes2.dex */
    public class SearchConfirmHandler implements DialogInterface.OnShowListener, DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, SiteSelector.OnSiteSeletedListener {
        private final CheckBox _acceptSearchTextView;
        private Button _cancelButton;
        private final TextView _messageView;
        private Button _okButton;
        private final View _searchTextGroup;
        private final SubtitleSearchTextView _searchTextInput;
        private ClickableSpan _languageSpan = new ClickableSpan() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.SearchConfirmHandler.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                SearchConfirmHandler.this.changeLanguage();
            }
        };
        private ClickableSpan _siteSpan = new ClickableSpan() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.SearchConfirmHandler.2
            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                SearchConfirmHandler.this.changeSite();
            }
        };

        @SuppressLint({"InflateParams"})
        SearchConfirmHandler() {
            Media[] mediaArr;
            SubtitleServiceManager.this._confirmDialog = new AlertDialog.Builder(SubtitleServiceManager.this._context).setTitle(R.string.download_subtitle).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            View layout = SubtitleServiceManager.this._confirmDialog.getLayoutInflater().inflate(R.layout.subtitle_search_confirm, (ViewGroup) null);
            this._messageView = (TextView) layout.findViewById(16908299);
            this._acceptSearchTextView = (CheckBox) layout.findViewById(R.id.accept_search_text);
            this._searchTextInput = (SubtitleSearchTextView) layout.findViewById(R.id.search_text);
            this._searchTextGroup = layout.findViewById(R.id.search_text_group);
            TextViewUtils.makeClearable((ViewGroup) this._searchTextGroup, this._searchTextInput, (ImageView) layout.findViewById(R.id.clear_btn));
            if (SubtitleServiceManager.this._medias.length == 1) {
                this._searchTextInput.setText(SubtitleServiceManager.this._medias[0].fileName);
            }
            for (Media media : SubtitleServiceManager.this._medias) {
                if (media.title != null) {
                    this._searchTextInput.addPredefinedCandidate(media.title, false);
                }
            }
            this._messageView.setMovementMethod(LinkMovementMethod.getInstance());
            this._messageView.setTextColor(this._messageView.getTextColors().getDefaultColor());
            this._messageView.setText(buildMessage());
            this._acceptSearchTextView.setOnCheckedChangeListener(this);
            SubtitleServiceManager.this._confirmDialog.setView(layout);
            SubtitleServiceManager.this._confirmDialog.setOnShowListener(this);
            SubtitleServiceManager.this._dialogPlatform.showDialog(SubtitleServiceManager.this._confirmDialog);
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            this._okButton = SubtitleServiceManager.this._confirmDialog.getButton(-1);
            this._cancelButton = SubtitleServiceManager.this._confirmDialog.getButton(-2);
            enableSearchTextInput(this._acceptSearchTextView.isChecked());
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == this._acceptSearchTextView) {
                enableSearchTextInput(isChecked);
            }
        }

        private void enableSearchTextInput(boolean enable) {
            this._searchTextInput.setEnabled(enable);
            if (enable) {
                this._searchTextGroup.setVisibility(0);
                this._acceptSearchTextView.setNextFocusDownId(R.id.search_text);
                this._searchTextInput.setNextFocusUpId(R.id.accept_search_text);
                if (this._okButton != null && this._cancelButton != null) {
                    this._okButton.setNextFocusUpId(R.id.search_text);
                    this._cancelButton.setNextFocusUpId(R.id.search_text);
                }
                this._searchTextInput.requestFocus();
                return;
            }
            this._searchTextGroup.setVisibility(8);
            this._acceptSearchTextView.setNextFocusDownId(-1);
            if (this._okButton != null && this._cancelButton != null) {
                this._okButton.setNextFocusUpId(R.id.accept_search_text);
                this._cancelButton.setNextFocusUpId(R.id.accept_search_text);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence buildMessage() {
            String message = SubtitleServiceManager.this._context.getString(R.string.subtitle_search_confirm);
            SpannableStringBuilder sb = new SpannableStringBuilder(message);
            int indexLangs = message.indexOf("%1$s");
            if (indexLangs >= 0) {
                Locale[] locales = SubtitleServiceManager.this.getLocales();
                CharSequence localeNames = P.getNativeLocaleNames(locales, true);
                sb.replace(indexLangs, indexLangs + 4, localeNames);
                sb.setSpan(this._languageSpan, indexLangs, localeNames.length() + indexLangs, 33);
            }
            int indexSites = sb.toString().indexOf("%2$s");
            if (indexSites >= 0) {
                String[] sites = SubtitleServiceManager.this.getSites();
                CharSequence siteNames = TextUtils.join(", ", sites);
                sb.replace(indexSites, indexSites + 4, siteNames);
                sb.setSpan(this._siteSpan, indexSites, siteNames.length() + indexSites, 33);
            }
            return sb;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing()) {
                if (!this._acceptSearchTextView.isChecked()) {
                    SubtitleServiceManager.this.doSearch(SubtitleServiceManager.this.getSites(), SubtitleServiceManager.this.getLocales(), "");
                    return;
                }
                ViewUtils.trimTextView(this._searchTextInput);
                this._searchTextInput.save();
                SubtitleServiceManager.this.doSearch(SubtitleServiceManager.this.getSites(), SubtitleServiceManager.this.getLocales(), this._searchTextInput.getText().toString());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeLanguage() {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing() && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().hasDialogAfter(SubtitleServiceManager.this._confirmDialog)) {
                final LocaleMultiSelector selector = new LocaleMultiSelector(SubtitleServiceManager.this._context);
                selector.setDefaultLocales(SubtitleServiceManager.this._locales);
                AlertDialog dialog = selector.create();
                dialog.setTitle(R.string.detail_language);
                dialog.setButton(-2, SubtitleServiceManager.this._context.getString(17039360), (DialogInterface.OnClickListener) null);
                dialog.setButton(-1, SubtitleServiceManager.this._context.getString(17039370), new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.SearchConfirmHandler.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        SubtitleServiceManager.this.saveLocales(selector.getSelected());
                        SearchConfirmHandler.this._messageView.setText(SearchConfirmHandler.this.buildMessage());
                    }
                });
                SubtitleServiceManager.this._dialogPlatform.showDialog(dialog);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeSite() {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing() && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().hasDialogAfter(SubtitleServiceManager.this._confirmDialog)) {
                new SiteSelector(SubtitleServiceManager.this._dialogPlatform, SubtitleServiceManager.this._sites, this);
            }
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SiteSelector.OnSiteSeletedListener
        public void onSiteSelected(SiteSelector selector, String[] sites) {
            SubtitleServiceManager.this.saveSites(sites);
            this._messageView.setText(buildMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Locale[] getLocales() {
        if (this._locales == null) {
            this._locales = LocaleUtils.parseLocales(App.prefs.getString(Key.SUBTITLE_SEARCH_LOCALES, null));
            if (this._locales.length == 0) {
                this._locales = P.preferredSubtitleLocales;
            }
        }
        return this._locales;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getSites() {
        if (this._sites == null) {
            this._sites = App.prefs.getString(Key.SUBTITLE_SEARCH_SITES, "opensubtitles.org").split(",");
        }
        return this._sites;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubtitleService[] getServices(@Nullable SubtitleService[] services) {
        String[] sites = getSites();
        int numSites = sites.length;
        SubtitleService[] newServices = new SubtitleService[numSites];
        for (int i = 0; i < numSites; i++) {
            String site = sites[i];
            if (services != null) {
                for (SubtitleService service : services) {
                    if (service.name().equals(site)) {
                        newServices[i] = service;
                    }
                }
            }
            char c = 65535;
            switch (site.hashCode()) {
                case 1781894087:
                    if (site.equals("opensubtitles.org")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    newServices[i] = new OpenSubtitles();
                    continue;
            }
        }
        return newServices;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveLocales(@Nullable Locale[] locales) {
        SharedPreferences.Editor editor = App.prefs.edit();
        if (locales != null && locales.length > 0) {
            editor.putString(Key.SUBTITLE_SEARCH_LOCALES, TextUtils.join(",", locales));
            this._locales = locales;
        } else {
            editor.remove(Key.SUBTITLE_SEARCH_LOCALES);
            this._locales = P.preferredSubtitleLocales;
        }
        AppUtils.apply(editor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSites(String[] sites) {
        SharedPreferences.Editor editor = App.prefs.edit();
        if (sites != null && sites.length > 0) {
            editor.putString(Key.SUBTITLE_SEARCH_SITES, TextUtils.join(",", sites));
            this._sites = sites;
        } else {
            editor.remove(Key.SUBTITLE_SEARCH_SITES);
            this._sites = "opensubtitles.org".split(",");
        }
        AppUtils.apply(editor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSearch(String[] sites, Locale[] locales, String queryText) {
        if ((this._flags & 1) != 0) {
            this._progressDialog = new AppCompatProgressDialog(this._context);
            this._progressDialog.setProgressStyle(0);
            this._progressDialog.setMessage(this._context.getString(R.string.subtitle_searching2));
            this._dialogPlatform.showDialog(this._progressDialog, this);
        }
        ensureFinder();
        this._finder.search(this._medias, sites, locales, queryText);
    }

    private void ensureFinder() {
        if (this._finder == null) {
            this._finder = new SubtitleFinder();
            this._finder.setListener(this);
        }
    }

    public void cancel() {
        if (this._finder != null) {
            this._finder.cancel();
        }
        this._mediaHandlers.clear();
        this._dialogPlatform.getDialogRegistry().dismissAll();
        this._confirmDialog = null;
        this._progressDialog = null;
        this._resultDialog = null;
        if (this._toast != null) {
            this._toast.cancel();
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleFinder.IListener
    public void onSubtitleSearched(SubtitleFinder finder, String serviceName, SubtitleEntry[] subtitles) {
        this._everSearchSucceeded = true;
        handleSubtitleSearchResult(finder, serviceName, subtitles, null);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleFinder.IListener
    public void onSubtitleSearchFailed(SubtitleFinder finder, String serviceName, SubtitleService.SubtitleServiceException exception) {
        handleSubtitleSearchResult(finder, serviceName, null, exception);
    }

    private void handleSubtitleSearchResult(SubtitleFinder finder, String serviceName, @Nullable SubtitleEntry[] subtitles, @Nullable SubtitleService.SubtitleServiceException exception) {
        int errorMessageDisplay = 0;
        if ((exception instanceof SubtitleService.UnauthorizedException) || (exception instanceof SubtitleService.DownloadLimitReachedException)) {
            errorMessageDisplay = showErrorMessage(exception, serviceName, 1);
        }
        if (subtitles == null || subtitles.length == 0) {
            if (finder.getSearchesRunning() == 0 && getTotalResultCount() == 0) {
                if (this._progressDialog != null) {
                    Dialog dlg = this._progressDialog;
                    this._progressDialog = null;
                    dlg.dismiss();
                }
                if (errorMessageDisplay == 0) {
                    if (this._everSearchSucceeded || exception == null) {
                        showMessage(R.string.subtitle_search_no_subtitle, 1, false);
                        return;
                    } else {
                        showErrorMessage(exception, serviceName, 1);
                        return;
                    }
                }
                return;
            }
            return;
        }
        if (this._progressDialog != null) {
            Dialog dlg2 = this._progressDialog;
            this._progressDialog = null;
            dlg2.dismiss();
        }
        for (SubtitleEntry entry : subtitles) {
            SearchResultHandler handler = getSearchResultHandler(entry.media, entry.queryText);
            handler.addResult(entry);
        }
        if (this._resultDialog == null) {
            showNextSearchResultDialog();
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleFinder.IListener
    public void onSubtitleDownloaded(SubtitleFinder finder, String serviceName, File file, SubtitleEntry entry) {
        if (this._onDownloadListener != null) {
            this._onDownloadListener.onDwonloaded(this, entry.media, entry.queryText, file);
        }
        if ((this._flags & 48) != 0) {
            showMessage(StringUtils.getStringItalic_s(R.string.subtitle_download_success, file.getName()), 2, true);
        }
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleFinder.IListener
    public void onSubtitleDownloadFailed(SubtitleFinder finder, String serviceName, File file, SubtitleEntry entry, SubtitleService.SubtitleServiceException exception) {
        showErrorMessage(exception, serviceName, null, entry.fileName, 2);
    }

    @Override // com.mxtech.videoplayer.subtitle.service.SubtitleFinder.IListener
    public void onSubtitleDownloadEnded(SubtitleFinder downloader) {
        if (this._onDownloadListener != null) {
            this._onDownloadListener.onDownloadEnd(this);
        }
    }

    private void showNextSearchResultDialog() {
        if (!this._dialogPlatform.isFinishing()) {
            for (SearchResultHandler handler : this._mediaHandlers.values()) {
                if (!handler.showedUp && handler.entrySize() > 0) {
                    handler.showedUp = true;
                    this._resultDialog = handler.createDialog();
                    this._dialogPlatform.showDialog(this._resultDialog, this);
                    return;
                }
            }
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this._dialogPlatform.getDialogRegistry().onDismiss(dialog);
        if (dialog == this._progressDialog) {
            this._progressDialog = null;
            cancel();
        } else if (dialog == this._resultDialog) {
            this._resultDialog = null;
            showNextSearchResultDialog();
        }
    }

    private int getTotalResultCount() {
        int total = 0;
        for (SearchResultHandler handler : this._mediaHandlers.values()) {
            total += handler.entrySize();
        }
        return total;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private SearchResultHandler getSearchResultHandler(@Nullable Media media, @Nullable String queryText) {
        if (media == null && this._medias.length == 1) {
            media = this._medias[0];
        }
        Object key = media != null ? media : queryText;
        SearchResultHandler handler = this._mediaHandlers.get(key);
        if (handler == null) {
            SearchResultHandler handler2 = new SearchResultHandler(media, queryText);
            this._mediaHandlers.put(key, handler2);
            return handler2;
        }
        return handler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasCredential(String serviceName) {
        char c = 65535;
        switch (serviceName.hashCode()) {
            case 1781894087:
                if (serviceName.equals("opensubtitles.org")) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return App.prefs.contains(Key.CREDENTIAL_OPENSUBTITLES);
            default:
                return false;
        }
    }

    private int showErrorMessage(SubtitleService.SubtitleServiceException exception, String serviceName, int process) {
        return showErrorMessage(exception, serviceName, null, null, process);
    }

    private int showErrorMessage(SubtitleService.SubtitleServiceException exception, String serviceName, @Nullable String mediaFilename, @Nullable String subtitleFilename, int process) {
        CharSequence message = getErrorMessage(exception, serviceName, mediaFilename, subtitleFilename);
        if (message != null) {
            return showMessage(message, process, false);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static CharSequence getErrorMessage(SubtitleService.SubtitleServiceException exception, String site) {
        return getErrorMessage(exception, site, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static CharSequence getErrorMessage(SubtitleService.SubtitleServiceException exception, String site, @Nullable String mediaFilename, @Nullable String subtitleFilename) {
        String exceptionName = exception.getClass().getSimpleName();
        int innerClassChar = exceptionName.lastIndexOf(36);
        if (innerClassChar >= 0) {
            exceptionName = exceptionName.substring(innerClassChar + 1);
        }
        char c = 65535;
        switch (exceptionName.hashCode()) {
            case -2081027611:
                if (exceptionName.equals("SubtitleFileReadException")) {
                    c = '\b';
                    break;
                }
                break;
            case -1837675298:
                if (exceptionName.equals("SubtitleFormatUnrecognized")) {
                    c = 1;
                    break;
                }
                break;
            case -1765812137:
                if (exceptionName.equals("NotImplemtedException")) {
                    c = 15;
                    break;
                }
                break;
            case -1543811792:
                if (exceptionName.equals("DownloadLimitReachedException")) {
                    c = 2;
                    break;
                }
                break;
            case -1362904677:
                if (exceptionName.equals("UnauthorizedException")) {
                    c = 0;
                    break;
                }
                break;
            case -1145043644:
                if (exceptionName.equals("LocalException")) {
                    c = 14;
                    break;
                }
                break;
            case -857708174:
                if (exceptionName.equals("SubtitleServiceException")) {
                    c = 16;
                    break;
                }
                break;
            case -699377546:
                if (exceptionName.equals("SubtitleFileEmptyException")) {
                    c = 6;
                    break;
                }
                break;
            case -683721912:
                if (exceptionName.equals("SubtitleNotFoundException")) {
                    c = 4;
                    break;
                }
                break;
            case -205754911:
                if (exceptionName.equals("NetworkException")) {
                    c = '\f';
                    break;
                }
                break;
            case -130009516:
                if (exceptionName.equals("SubtitleFileTooLargeException")) {
                    c = 7;
                    break;
                }
                break;
            case 350177828:
                if (exceptionName.equals("SubtitleFileWriteException")) {
                    c = 5;
                    break;
                }
                break;
            case 1469051948:
                if (exceptionName.equals("ServerException")) {
                    c = '\n';
                    break;
                }
                break;
            case 1853015001:
                if (exceptionName.equals("MediaFileReadException")) {
                    c = '\t';
                    break;
                }
                break;
            case 1922129976:
                if (exceptionName.equals("SubtitleAlreadyExistException")) {
                    c = 3;
                    break;
                }
                break;
            case 2114449579:
                if (exceptionName.equals("ServerDataFormatException")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (hasCredential(site)) {
                    return StringUtils.getStringItalic_s(R.string.subtitle_unauthorized, site);
                }
                return StringUtils.getStringItalic_s(R.string.error_unauthorized_on, site);
            case 1:
                return StringUtils.getStringItalic_s(R.string.error_unrecognized_subtitle_format_on, site);
            case 2:
                if (hasCredential(site)) {
                    return StringUtils.getStringItalic_s(R.string.subtitle_quota_named_user, site);
                }
                return StringUtils.getStringItalic_s(R.string.subtitle_quota_anonymous, site);
            case 3:
                if (subtitleFilename != null) {
                    return StringUtils.getStringItalic_s(R.string.error_file_already_exist, subtitleFilename, site);
                }
                return null;
            case 4:
                return StringUtils.getStringItalic_s(R.string.error_subtitle_not_exist_on, site);
            case 5:
                if (subtitleFilename != null) {
                    return StringUtils.getStringItalic_s(R.string.error_downloaded_subtitle_file_write, subtitleFilename);
                }
                return null;
            case 6:
            case 7:
                if (subtitleFilename != null) {
                    return StringUtils.getStringItalic_s(R.string.error_invalid_subtitle_file_named, subtitleFilename);
                }
                return null;
            case '\b':
                if (subtitleFilename != null) {
                    return StringUtils.getStringItalic_s(R.string.error_file_read, subtitleFilename);
                }
                return null;
            case '\t':
                if (mediaFilename != null) {
                    return StringUtils.getStringItalic_s(R.string.error_file_read, mediaFilename);
                }
                return null;
            case '\n':
                return StringUtils.getStringItalic_s(R.string.error_server_from, site);
            case 11:
                return StringUtils.getStringItalic_s(R.string.error_server_data_format_from, site);
            case '\f':
                return App.context.getString(R.string.error_network);
            case '\r':
            default:
                Log.w(TAG, "Unexpected exception: " + exceptionName);
                return App.context.getString(R.string.error_unknown);
            case 14:
            case 15:
            case 16:
                return App.context.getString(R.string.error_unknown);
        }
    }

    private int showMessage(int resId, int process, boolean success) {
        return showMessage(this._context.getString(resId), process, success);
    }

    private int showMessage(CharSequence text, int process, boolean success) {
        if (!this._dialogPlatform.isFinishing()) {
            if (process == 1) {
                if (!success) {
                    if ((this._flags & 4) != 0) {
                        this._dialogPlatform.showSimpleDialogMessage(text);
                        return 1;
                    } else if ((this._flags & 2) != 0) {
                        showToast(text);
                        return 2;
                    }
                }
            } else if (process == 2) {
                if (success) {
                    if ((this._flags & 32) != 0) {
                        this._dialogPlatform.showSimpleDialogMessage(text);
                        return 1;
                    } else if ((this._flags & 16) != 0) {
                        showToast(text);
                        return 2;
                    }
                } else if ((this._flags & 128) != 0) {
                    this._dialogPlatform.showSimpleDialogMessage(text);
                    return 1;
                } else if ((this._flags & 64) != 0) {
                    showToast(text);
                    return 2;
                }
            }
        }
        return 0;
    }

    private void showToast(int resId) {
        showToast(resId, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(int resId, int duration) {
        showToast(this._context.getString(resId), duration);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(CharSequence text) {
        showToast(text, 0);
    }

    private void showToast(CharSequence text, int duration) {
        if (this._toast == null) {
            this._toast = Toast.makeText(this._context, text, duration);
        } else {
            this._toast.setText(text);
        }
        this._toast.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SubtitleListEntry {
        SubtitleEntry entry;
        @Nullable
        File file;
        CharSequence text;

        private SubtitleListEntry() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* loaded from: classes2.dex */
    public class SearchResultHandler extends DataSetObservable2 implements DialogInterface.OnClickListener, ListAdapter, DialogInterface.OnShowListener, View.OnClickListener, AdapterView.OnItemClickListener {
        private AlertDialog _dialog;
        @Nullable
        private Button _downloadButton;
        private ListView _listView;
        @Nullable
        private final Media _media;
        @Nullable
        private final String _queryText;
        private boolean _saveDirecotryTested;
        @Nullable
        private File _saveDirectory;
        private TextView _warningView;
        boolean showedUp;
        private final ArrayList<SubtitleListEntry> _entries = new ArrayList<>();
        private final ArrayMap<File, Boolean> _testedFiles = new ArrayMap<>();

        SearchResultHandler(Media media, String queryText) {
            this._media = media;
            this._queryText = queryText;
        }

        @Nullable
        private File getSaveDirectory() {
            if (!this._saveDirecotryTested) {
                this._saveDirecotryTested = true;
                if (this._media != null && this._media.file() != null) {
                    File directory = this._media.file().getParentFile();
                    if (!FileUtils.checkDirectoryWritable(directory)) {
                        Log.w(SubtitleServiceManager.TAG, "Media directory " + directory + " is not writable.");
                    } else {
                        this._saveDirectory = directory;
                    }
                }
                if (this._saveDirectory == null) {
                    if (!FileUtils.checkDirectoryWritable(P.subtitleFolder)) {
                        Log.w(SubtitleServiceManager.TAG, "Subtitle directory " + P.subtitleFolder + " is not writable.");
                    } else {
                        return this._saveDirectory;
                    }
                }
            }
            return this._saveDirectory;
        }

        void addResult(SubtitleEntry entry) {
            SubtitleListEntry listEntry = new SubtitleListEntry();
            listEntry.text = getDecorString(entry);
            listEntry.file = getTargetFile(entry);
            listEntry.entry = entry;
            this._entries.add(listEntry);
            if (this._listView != null) {
                super.notifyChanged();
            }
        }

        @SuppressLint({"InflateParams"})
        AlertDialog createDialog() {
            String title;
            if (this._media != null) {
                if (SubtitleServiceManager.this._medias.length == 1) {
                    title = SubtitleServiceManager.this._context.getString(R.string.subtitles);
                } else {
                    title = StringUtils.getString_s(R.string.subtitles_for, this._media.displayName);
                }
            } else {
                title = StringUtils.getString_s(R.string.subtitles_for, '\"' + this._queryText + '\"');
            }
            this._dialog = new AlertDialog.Builder(SubtitleServiceManager.this._context).setTitle(title).setPositiveButton(R.string.download, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            View layout = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_search_result, (ViewGroup) null);
            this._listView = (ListView) layout.findViewById(16908298);
            this._warningView = (TextView) layout.findViewById(R.id.warning);
            this._listView.setAdapter((ListAdapter) this);
            File saveDirectory = getSaveDirectory();
            if (saveDirectory == null) {
                setWarning(StringUtils.getStringItalic_s(R.string.error_subtitle_folder_permission, P.subtitleFolder));
            } else if (saveDirectory.equals(P.subtitleFolder)) {
                setWarning(StringUtils.getStringItalic_s(R.string.notify_subtitle_download_location, saveDirectory));
            }
            this._dialog.setView(layout);
            this._dialog.setOnShowListener(this);
            return this._dialog;
        }

        int entrySize() {
            return this._entries.size();
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerObserver(observer);
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            super.unregisterObserver(observer);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this._entries.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this._entries.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = this._dialog.getLayoutInflater().inflate(R.layout.subtitle_result_item, parent, false);
            }
            CheckedTextView textView = (CheckedTextView) view.findViewById(16908308);
            textView.setText(this._entries.get(position).text);
            return view;
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return this._entries.size() == 0;
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return this._entries.get(position).file != null;
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            AlertDialog dlg = (AlertDialog) dialog;
            this._downloadButton = dlg.getButton(-1);
            this._downloadButton.setOnClickListener(this);
            this._downloadButton.setEnabled(false);
            this._listView.setOnItemClickListener(this);
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            List<Integer> checked = ViewUtils.getCheckedItemPositions(this._listView);
            this._downloadButton.setEnabled(checked.size() > 0);
            File file = this._entries.get(position).file;
            if (file != null) {
                for (Integer pos : checked) {
                    if (pos.intValue() != position) {
                        SubtitleListEntry entry = this._entries.get(pos.intValue());
                        if (FileUtils.equalsIgnoreCase(entry.file, file)) {
                            this._listView.setItemChecked(pos.intValue(), false);
                        }
                    }
                }
            }
        }

        @Override // android.view.View.OnClickListener
        @SuppressLint({"InflateParams"})
        public void onClick(View v) {
            ArrayList<File> dupFiles = null;
            SparseBooleanArray positions = this._listView.getCheckedItemPositions();
            int numPositions = positions.size();
            for (int i = 0; i < numPositions; i++) {
                if (positions.valueAt(i)) {
                    SubtitleListEntry entry = this._entries.get(positions.keyAt(i));
                    if (entry.file != null && entry.file.exists()) {
                        if (dupFiles == null) {
                            dupFiles = new ArrayList<>();
                        }
                        dupFiles.add(entry.file);
                    }
                }
            }
            if (dupFiles != null) {
                if (!SubtitleServiceManager.this._dialogPlatform.isFinishing()) {
                    L.sb.setLength(0);
                    Iterator<File> it = dupFiles.iterator();
                    while (it.hasNext()) {
                        File file = it.next();
                        if (L.sb.length() > 0) {
                            L.sb.append(", ");
                        }
                        L.sb.append(file.getName());
                    }
                    AlertDialog confirmDialog = new AlertDialog.Builder(SubtitleServiceManager.this._context).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                    View layout = confirmDialog.getLayoutInflater().inflate(R.layout.overwrite_confirm, (ViewGroup) null);
                    TextView messageView = (TextView) layout.findViewById(R.id.message);
                    TextView contentView = (TextView) layout.findViewById(R.id.content);
                    messageView.setText(SubtitleServiceManager.this._context.getResources().getQuantityString(R.plurals.ask_replace_file, dupFiles.size()));
                    contentView.setText(L.sb.toString());
                    confirmDialog.setView(layout);
                    SubtitleServiceManager.this._dialogPlatform.showDialog(confirmDialog);
                    return;
                }
                return;
            }
            this._dialog.dismiss();
            performDownload();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                performDownload();
                if (dialog != this._dialog) {
                    this._dialog.dismiss();
                }
            }
        }

        private void performDownload() {
            if (SubtitleServiceManager.this._onDownloadListener != null) {
                SubtitleServiceManager.this._onDownloadListener.onDowloadBegin(SubtitleServiceManager.this);
            }
            if ((SubtitleServiceManager.this._flags & 8) != 0) {
                SubtitleServiceManager.this.showToast(R.string.subtitle_downloading, 1);
            }
            SparseBooleanArray positions = this._listView.getCheckedItemPositions();
            int numPositions = positions.size();
            for (int i = 0; i < numPositions; i++) {
                if (positions.valueAt(i)) {
                    SubtitleListEntry entry = this._entries.get(positions.keyAt(i));
                    if (entry.file != null) {
                        SubtitleServiceManager.this._finder.download(entry.file, entry.entry);
                    }
                }
            }
        }

        @Nullable
        private File getTargetFile(SubtitleEntry entry) {
            File saveDirectory = getSaveDirectory();
            if (saveDirectory == null) {
                return null;
            }
            if (this._media != null) {
                String extension = FileUtils.getExtension(entry.fileName);
                if (extension == null) {
                    Log.w(SubtitleServiceManager.TAG, "Subtitle extension is unknown, use .srt as default.");
                    extension = "srt";
                }
                File file = new File(saveDirectory, FileUtils.stripExtension(this._media.fileName) + "." + extension);
                Boolean valid = this._testedFiles.get(file);
                if (valid == null) {
                    try {
                        if (file.createNewFile()) {
                            file.delete();
                        }
                        this._testedFiles.put(file, true);
                        return file;
                    } catch (IOException e) {
                        Log.e(SubtitleServiceManager.TAG, "", e);
                        this._testedFiles.put(file, false);
                    }
                } else if (valid.booleanValue()) {
                    return file;
                }
            }
            return new File(saveDirectory, entry.fileName);
        }

        private CharSequence getDecorString(SubtitleEntry entry) {
            SpannableStringBuilder sb = new SpannableStringBuilder(entry.fileName);
            if (entry.locale != null || entry.size > 0 || entry.rating >= 2.0d) {
                int startIndex = sb.length();
                sb.append(' ');
                boolean first = true;
                if (entry.locale != null) {
                    if (1 != 0) {
                        first = false;
                    } else {
                        sb.append((CharSequence) ", ");
                    }
                    sb.append((CharSequence) P.getNativeLocaleName(entry.locale));
                }
                if (entry.size > 0) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append((CharSequence) ", ");
                    }
                    sb.append((CharSequence) Formatter.formatShortFileSize(App.context, entry.size));
                }
                if (entry.rating >= 2.0d) {
                    if (!first) {
                        sb.append((CharSequence) ", ");
                    }
                    DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
                    fmt.setMaximumFractionDigits(2);
                    sb.append((CharSequence) fmt.format(entry.rating)).append((CharSequence) "/10");
                }
                sb.setSpan(L.getSecondaryColorSizeSpan(), startIndex, sb.length(), 33);
            }
            return sb;
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

    private MediaSubtitle[] canonicalizeList(MediaSubtitle[] subs) {
        TreeSet<String> exclusives = null;
        for (MediaSubtitle sub : subs) {
            if (SubtitleFactory.getExtensionId(sub.subtitle.filename()) == 5) {
                if (exclusives == null) {
                    exclusives = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                }
                exclusives.add(FileUtils.stripExtension(sub.subtitle.sourceUri.toString()) + ".sub");
            }
        }
        if (exclusives != null) {
            ArrayList<MediaSubtitle> newSubs = new ArrayList<>();
            for (MediaSubtitle sub2 : subs) {
                if (!exclusives.contains(sub2.subtitle.sourceUri.toString())) {
                    newSubs.add(sub2);
                }
            }
            return (MediaSubtitle[]) newSubs.toArray(new MediaSubtitle[newSubs.size()]);
        }
        return subs;
    }

    public void rate(MediaSubtitle[] subsGiven) {
        if (this._confirmDialog != null) {
            this._confirmDialog.dismiss();
            this._confirmDialog = null;
        }
        final MediaSubtitle[] subs = canonicalizeList(subsGiven);
        if (subs.length == 1) {
            doRate(subs[0]);
            return;
        }
        int numItems = subs.length;
        ArrayList<String> lowerNamesOnly = new ArrayList<>(numItems);
        CharSequence[] items = new CharSequence[numItems];
        for (int i = 0; i < numItems; i++) {
            String filename = subs[i].subtitle.filename();
            String lowFilename = filename.toLowerCase(Locale.US);
            int prevIndex = lowerNamesOnly.indexOf(lowFilename);
            if (prevIndex >= 0) {
                if (!(items[prevIndex] instanceof Spanned)) {
                    items[prevIndex] = getDecorFilenameWithDirectory(subs[prevIndex].subtitle);
                }
                items[i] = getDecorFilenameWithDirectory(subs[i].subtitle);
            } else {
                items[i] = filename;
            }
            lowerNamesOnly.add(lowFilename);
        }
        this._confirmDialog = new AlertDialog.Builder(this._context).setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SubtitleServiceManager.this.doRate(subs[which]);
                dialog.dismiss();
            }
        }).create();
        this._dialogPlatform.showDialog(this._confirmDialog);
    }

    private CharSequence getDecorFilenameWithDirectory(Subtitle subtitle) {
        SpannableStringBuilder sb = new SpannableStringBuilder(subtitle.filename()).append(' ');
        int spanStart = sb.length();
        sb.append('(').append((CharSequence) subtitle.directory()).append(')');
        int spanEnd = sb.length();
        sb.setSpan(L.getSecondaryColorSizeSpan(), spanStart, spanEnd, 33);
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRate(MediaSubtitle sub) {
        if (!this._dialogPlatform.isFinishing()) {
            new RatingHandler(sub);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* loaded from: classes2.dex */
    public class RatingHandler implements View.OnClickListener, DialogInterface.OnShowListener, SiteSelector.OnSiteSeletedListener, RatingBar.OnRatingBarChangeListener {
        private boolean _canSubmit;
        private final EditText _commentView;
        private final TextView _messageView;
        private final RatingBar _ratingBar;
        private boolean _ratingChoiced;
        private final TextView _ratingDescView;
        private SubtitleService[] _services;
        private ClickableSpan _siteSpan = new ClickableSpan() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.RatingHandler.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                RatingHandler.this.changeSite();
            }
        };
        private final MediaSubtitle _sub;
        @Nullable
        private Button _submitButton;
        private AsyncTask2<String, CharSequence, Void> _task;
        private final TextView _warningView;

        @SuppressLint({"InflateParams"})
        RatingHandler(MediaSubtitle sub) {
            this._sub = sub;
            SubtitleServiceManager.this._confirmDialog = new AlertDialog.Builder(SubtitleServiceManager.this._context).setTitle(R.string.rate).setPositiveButton(R.string.submit, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            View layout = SubtitleServiceManager.this._confirmDialog.getLayoutInflater().inflate(R.layout.subtitle_rate_confirm, (ViewGroup) null);
            this._messageView = (TextView) layout.findViewById(16908299);
            this._ratingBar = (RatingBar) layout.findViewById(R.id.rating);
            this._ratingDescView = (TextView) layout.findViewById(R.id.rating_desc);
            this._commentView = (EditText) layout.findViewById(R.id.comment);
            this._warningView = (TextView) layout.findViewById(R.id.warning);
            this._messageView.setMovementMethod(LinkMovementMethod.getInstance());
            this._messageView.setTextColor(this._messageView.getTextColors().getDefaultColor());
            this._messageView.setText(buildMessage());
            this._ratingBar.setOnRatingBarChangeListener(this);
            TextViewUtils.makeClearable((ViewGroup) this._commentView.getParent(), this._commentView, (ImageView) layout.findViewById(R.id.clear_btn));
            SubtitleServiceManager.this._confirmDialog.setView(layout);
            SubtitleServiceManager.this._confirmDialog.setOnShowListener(this);
            SubtitleServiceManager.this._dialogPlatform.showDialog(SubtitleServiceManager.this._confirmDialog);
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            AlertDialog dlg = (AlertDialog) dialog;
            this._submitButton = dlg.getButton(-1);
            this._submitButton.setOnClickListener(this);
            updateServices();
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SiteSelector.OnSiteSeletedListener
        public void onSiteSelected(SiteSelector selector, String[] sites) {
            SubtitleServiceManager.this.saveSites(sites);
            this._messageView.setText(buildMessage());
            updateServices();
        }

        @Override // android.widget.RatingBar.OnRatingBarChangeListener
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (rating == 0.0f) {
                this._ratingDescView.setText(R.string.rating_desc_0);
            } else if (rating <= 1.0f) {
                this._ratingDescView.setText(R.string.rating_desc_1);
            } else if (rating <= 2.0f) {
                this._ratingDescView.setText(R.string.rating_desc_2);
            } else if (rating <= 3.0f) {
                this._ratingDescView.setText(R.string.rating_desc_3);
            } else if (rating <= 4.0f) {
                this._ratingDescView.setText(R.string.rating_desc_4);
            } else {
                this._ratingDescView.setText(R.string.rating_desc_5);
            }
            this._ratingChoiced = true;
            updateButtonsState();
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (SubtitleServiceManager.this._confirmDialog != null && this._task == null) {
                StringBuilder sbNeedLogin = null;
                String[] sites = SubtitleServiceManager.this.getSites();
                for (String site : sites) {
                    char c = 65535;
                    switch (site.hashCode()) {
                        case 1781894087:
                            if (site.equals("opensubtitles.org")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (App.prefs.contains(Key.CREDENTIAL_OPENSUBTITLES)) {
                                break;
                            } else {
                                if (sbNeedLogin == null) {
                                    sbNeedLogin = L.sb;
                                    sbNeedLogin.setLength(0);
                                } else {
                                    sbNeedLogin.append(", ");
                                }
                                sbNeedLogin.append(site);
                                break;
                            }
                    }
                }
                if (sbNeedLogin == null) {
                    SubtitleServiceManager.this._confirmDialog.dismiss();
                    final String comment = this._commentView.getText().toString().trim();
                    final float rating = this._ratingBar.getRating();
                    this._task = new AsyncTask2<String, CharSequence, Void>() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.RatingHandler.2
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public Void doInBackground(String... sites2) {
                            SubtitleService[] subtitleServiceArr;
                            for (SubtitleService service : RatingHandler.this._services) {
                                String site2 = service.name();
                                try {
                                    service.rate(RatingHandler.this._sub.media, RatingHandler.this._sub.subtitle, (int) (rating * 2.0f), comment);
                                    publishProgress(new CharSequence[]{StringUtils.getStringItalic_s(R.string.completed_rating_on, site2)});
                                } catch (SubtitleService.SubtitleServiceException e) {
                                    Log.w(SubtitleServiceManager.TAG, "", e);
                                    CharSequence message = SubtitleServiceManager.getErrorMessage(e, site2, RatingHandler.this._sub.media.fileName, RatingHandler.this._sub.subtitle.filename());
                                    if (message != null) {
                                        publishProgress(new CharSequence[]{message});
                                    }
                                }
                            }
                            return null;
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public void onProgressUpdate(CharSequence... values) {
                            SubtitleServiceManager.this.showToast(values[0]);
                        }
                    };
                    this._task.executeParallel(sites);
                    return;
                }
                this._warningView.setText(StringUtils.getString_s(R.string.need_login_to_give_rating, sbNeedLogin.toString()));
                this._warningView.setVisibility(0);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeSite() {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing() && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().hasDialogAfter(SubtitleServiceManager.this._confirmDialog)) {
                new SiteSelector(SubtitleServiceManager.this._dialogPlatform, SubtitleServiceManager.this._sites, this);
                this._warningView.setVisibility(8);
            }
        }

        private CharSequence buildMessage() {
            String message = SubtitleServiceManager.this._context.getString(R.string.subtitle_rating_confirm);
            SpannableStringBuilder sb = new SpannableStringBuilder(message);
            int indexFilename = message.indexOf("%1$s");
            if (indexFilename >= 0) {
                String filename = this._sub.subtitle.filename();
                sb.replace(indexFilename, indexFilename + 4, (CharSequence) filename);
                sb.setSpan(SubtitleServiceManager.ITALIC_SPAN, indexFilename, filename.length() + indexFilename, 33);
            }
            int indexSites = sb.toString().indexOf("%2$s");
            if (indexSites >= 0) {
                String[] sites = SubtitleServiceManager.this.getSites();
                CharSequence siteNames = TextUtils.join(", ", sites);
                sb.replace(indexSites, indexSites + 4, siteNames);
                sb.setSpan(this._siteSpan, indexSites, siteNames.length() + indexSites, 33);
            }
            return sb;
        }

        private void updateServices() {
            SubtitleService[] subtitleServiceArr;
            this._services = SubtitleServiceManager.this.getServices(this._services);
            this._canSubmit = false;
            StringBuilder sb = null;
            for (SubtitleService service : this._services) {
                if (service.isSupported(this._sub.subtitle.filename())) {
                    this._canSubmit = true;
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                    } else {
                        sb.append('\n');
                    }
                    sb.append(StringUtils.getString_s(R.string.error_not_supported_file_format_on, service.name()));
                }
            }
            this._ratingBar.setEnabled(this._canSubmit);
            this._commentView.setEnabled(this._canSubmit);
            updateButtonsState();
            if (sb != null) {
                this._warningView.setText(sb);
                this._warningView.setVisibility(0);
            }
        }

        private void updateButtonsState() {
            this._submitButton.setEnabled(this._canSubmit && this._ratingChoiced);
        }
    }

    public void upload(MediaSubtitle[] subs, boolean subtitleShifed) {
        if (this._confirmDialog != null) {
            this._confirmDialog.dismiss();
            this._confirmDialog = null;
        }
        new UploadHandler(canonicalizeList(subs), subtitleShifed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class UploadItem {
        @Nullable
        String directory;
        boolean enabled;
        final String filename;
        @Nullable
        Locale locale;
        final MediaSubtitle sub;

        UploadItem(MediaSubtitle sub) {
            this.sub = sub;
            this.filename = sub.subtitle.filename();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* loaded from: classes2.dex */
    public class UploadHandler extends DataSetObservable2 implements DialogInterface.OnShowListener, View.OnClickListener, SiteSelector.OnSiteSeletedListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ListAdapter, DialogInterface.OnDismissListener, View.OnTouchListener, SubtitleUploader.Client {
        private CharSequence _defaultWarning;
        private final TextView _detectLanguageButton;
        private final ColorStateList _detectLanguageButtonLinkColor;
        private final ColorStateList _detectLanguageButtonNormalColor;
        private final UploadItem[] _items;
        private AsyncTask2<Subtitle, Void, Object> _langDetector;
        private final LayoutInflater _layoutInflater;
        private final ListView _listView;
        private final TextView _messageView;
        private final int _numSubs;
        private SubtitleService[] _services;
        private Button _submitButton;
        private SubtitleUploader _uploader;
        private CharSequence _warning;
        private final TextView _warningView;
        private ClickableSpan _languageSpan = new ClickableSpan() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.UploadHandler.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                Object position = widget.getTag();
                if (position instanceof Integer) {
                    UploadHandler.this.changeLanguage(((Integer) position).intValue());
                }
            }
        };
        private ClickableSpan _siteSpan = new ClickableSpan() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.UploadHandler.2
            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                UploadHandler.this.changeSite();
            }
        };

        @SuppressLint({"InflateParams"})
        UploadHandler(MediaSubtitle[] subs, boolean subtitleShifed) {
            this._numSubs = subs.length;
            this._items = new UploadItem[this._numSubs];
            ArrayList<String> lowNames = new ArrayList<>(this._numSubs);
            for (int i = 0; i < this._numSubs; i++) {
                UploadItem item = new UploadItem(subs[i]);
                this._items[i] = item;
                String lowFilename = item.filename.toLowerCase(Locale.US);
                int prevIndex = lowNames.indexOf(lowFilename);
                if (prevIndex >= 0) {
                    if (this._items[prevIndex].directory == null) {
                        this._items[prevIndex].directory = subs[prevIndex].subtitle.directory();
                    }
                    item.directory = subs[i].subtitle.directory();
                }
                lowNames.add(lowFilename);
            }
            updateServices();
            SubtitleServiceManager.this._confirmDialog = new AlertDialog.Builder(SubtitleServiceManager.this._context).setTitle(R.string.upload).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.submit, (DialogInterface.OnClickListener) null).create();
            this._layoutInflater = SubtitleServiceManager.this._confirmDialog.getLayoutInflater();
            View layout = this._layoutInflater.inflate(R.layout.subtitle_upload_confirm, (ViewGroup) null);
            this._messageView = (TextView) layout.findViewById(16908299);
            this._listView = (ListView) layout.findViewById(16908298);
            this._detectLanguageButton = (TextView) layout.findViewById(R.id.detect_language);
            this._warningView = (TextView) layout.findViewById(R.id.warning);
            this._messageView.setMovementMethod(LinkMovementMethod.getInstance());
            this._messageView.setTextColor(this._messageView.getTextColors().getDefaultColor());
            this._messageView.setText(buildMessage());
            this._detectLanguageButtonLinkColor = this._detectLanguageButton.getLinkTextColors();
            this._detectLanguageButtonNormalColor = this._detectLanguageButton.getTextColors();
            this._detectLanguageButton.setPaintFlags(this._detectLanguageButton.getPaintFlags() | 8);
            this._detectLanguageButton.setOnClickListener(this);
            this._listView.setOnItemClickListener(this);
            this._listView.setOnItemLongClickListener(this);
            this._listView.setChoiceMode(2);
            this._listView.setAdapter((ListAdapter) this);
            SubtitleServiceManager.this._confirmDialog.setView(layout);
            SubtitleServiceManager.this._confirmDialog.setOnShowListener(this);
            if (subtitleShifed) {
                setDefaultWarning(R.string.subtitle_upload_warning_shifted);
            }
            SubtitleServiceManager.this._dialogPlatform.showDialog(SubtitleServiceManager.this._confirmDialog, this);
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            AlertDialog dlg = (AlertDialog) dialog;
            this._submitButton = dlg.getButton(-1);
            this._submitButton.setOnClickListener(this);
            updateButtonsState();
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SiteSelector.OnSiteSeletedListener
        public void onSiteSelected(SiteSelector selector, String[] sites) {
            SubtitleServiceManager.this.saveSites(sites);
            updateServices();
            this._messageView.setText(buildMessage());
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerObserver(observer);
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            super.unregisterObserver(observer);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this._numSubs;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this._items[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View view, ViewGroup parent) {
            TextView textView;
            String localeName;
            if (view == null) {
                view = this._layoutInflater.inflate(R.layout.subtitle_upload_confirm_list_item, parent, false);
                textView = (TextView) view.findViewById(16908308);
                textView.setOnTouchListener(this);
            } else {
                textView = (TextView) view.findViewById(16908308);
            }
            UploadItem item = this._items[position];
            SpannableStringBuilder sb = new SpannableStringBuilder(item.filename);
            if (item.directory != null) {
                sb.append(' ');
                int spanStart = sb.length();
                sb.append((CharSequence) item.directory);
                int spanEnd = sb.length();
                sb.setSpan(L.getSecondaryColorSizeSpan(), spanStart, spanEnd, 33);
            }
            if (item.enabled) {
                if (item.locale == null) {
                    localeName = SubtitleServiceManager.this._context.getString(R.string.auto_detect);
                } else {
                    localeName = P.getNativeLocaleName(item.locale, true);
                }
                sb.append(' ');
                int spanStart2 = sb.length();
                sb.append((CharSequence) localeName);
                int spanEnd2 = sb.length();
                sb.setSpan(this._languageSpan, spanStart2, spanEnd2, 33);
            }
            textView.setText(sb);
            textView.setTag(Integer.valueOf(position));
            textView.setEnabled(item.enabled);
            return view;
        }

        @Override // android.view.View.OnTouchListener
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            ClickableSpan[] clickableSpanArr;
            boolean ret = false;
            TextView textView = (TextView) v;
            CharSequence text = textView.getText();
            if (text instanceof Spanned) {
                Spanned spanned = (Spanned) text;
                int action = event.getAction();
                if (action == 1 || action == 0) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int x2 = x - textView.getTotalPaddingLeft();
                    int y2 = y - textView.getTotalPaddingTop();
                    int x3 = x2 + textView.getScrollX();
                    Layout layout = textView.getLayout();
                    int line = layout.getLineForVertical(y2 + textView.getScrollY());
                    int off = layout.getOffsetForHorizontal(line, x3);
                    if (off >= 0 && off < spanned.length()) {
                        for (ClickableSpan span : (ClickableSpan[]) spanned.getSpans(off, off, ClickableSpan.class)) {
                            if (action == 1) {
                                span.onClick(textView);
                            }
                            ret = true;
                        }
                    }
                }
            }
            return ret;
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return this._numSubs == 0;
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return this._items[position].enabled;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setWarning((CharSequence) null);
            updateButtonsState();
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            changeLanguage(position);
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            UploadItem[] uploadItemArr;
            UploadItem[] uploadItemArr2;
            setWarning((CharSequence) null);
            if (v == this._submitButton) {
                if (this._langDetector == null && this._uploader == null && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.isFinishing()) {
                    ArrayList<UploadItem> uploadableItems = new ArrayList<>();
                    int i = 0;
                    for (UploadItem item : this._items) {
                        if (item.enabled && this._listView.isItemChecked(i)) {
                            uploadableItems.add(item);
                        }
                        i++;
                    }
                    if (uploadableItems.size() == 0) {
                        setWarning(R.string.subtitle_select_any);
                        return;
                    }
                    SubtitleServiceManager.this._progressDialog = new AppCompatProgressDialog(SubtitleServiceManager.this._context);
                    SubtitleServiceManager.this._progressDialog.setProgressStyle(0);
                    SubtitleServiceManager.this._dialogPlatform.showDialog(SubtitleServiceManager.this._progressDialog, new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.UploadHandler.3
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog) {
                            SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().onDismiss(dialog);
                            if (UploadHandler.this._uploader != null) {
                                UploadHandler.this._uploader.cancel();
                            }
                        }
                    });
                    this._uploader = new SubtitleUploader(SubtitleServiceManager.this._dialogPlatform, this._services, uploadableItems, this);
                    SubtitleServiceManager.this._confirmDialog.dismiss();
                }
            } else if (v == this._detectLanguageButton && this._langDetector == null && this._uploader == null) {
                final ArrayList<Integer> indexes = new ArrayList<>(this._numSubs);
                ArrayList<Subtitle> subs = new ArrayList<>(this._numSubs);
                int i2 = 0;
                for (UploadItem item2 : this._items) {
                    if (item2.enabled && item2.locale == null) {
                        subs.add(item2.sub.subtitle);
                        indexes.add(Integer.valueOf(i2));
                    }
                    i2++;
                }
                final int numItems = subs.size();
                if (numItems > 0) {
                    setDetector(new AsyncTask2<Subtitle, Void, Object>() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.UploadHandler.4
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public Object doInBackground(Subtitle... subtitles) {
                            try {
                                return OpenSubtitles.detectLanguage(subtitles);
                            } catch (SubtitleService.SubtitleServiceException e) {
                                Log.w(SubtitleServiceManager.TAG, "", e);
                                return e;
                            } catch (Exception e2) {
                                Log.w(SubtitleServiceManager.TAG, "", e2);
                                return null;
                            }
                        }

                        @Override // android.os.AsyncTask
                        protected void onCancelled() {
                            UploadHandler.this.setDetector(null);
                            UploadHandler.this.updateButtonsState();
                        }

                        @Override // android.os.AsyncTask
                        protected void onPostExecute(Object result) {
                            UploadHandler.this.setDetector(null);
                            if (result instanceof Locale[]) {
                                Locale[] locales = (Locale[]) result;
                                for (int i3 = 0; i3 < numItems; i3++) {
                                    UploadHandler.this._items[((Integer) indexes.get(i3)).intValue()].locale = locales[i3];
                                }
                                UploadHandler.this.notifyChanged();
                            } else if (result instanceof SubtitleService.SubtitleServiceException) {
                                UploadHandler.this.setWarning(SubtitleServiceManager.getErrorMessage((SubtitleService.SubtitleServiceException) result, "opensubtitles.org"));
                            }
                            UploadHandler.this.updateButtonsState();
                        }
                    });
                    this._langDetector.executeParallel(subs.toArray(new Subtitle[numItems]));
                    updateButtonsState();
                }
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().onDismiss(dialog);
            if (this._langDetector != null) {
                this._langDetector.cancel(true);
            }
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SubtitleUploader.Client
        public void setProgressMessage(SubtitleUploader uploader, CharSequence message) {
            if (SubtitleServiceManager.this._progressDialog != null) {
                SubtitleServiceManager.this._progressDialog.setMessage(message);
            }
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SubtitleUploader.Client
        public void showToast(SubtitleUploader uploader, CharSequence message) {
            SubtitleServiceManager.this.showToast(message);
        }

        @Override // com.mxtech.videoplayer.subtitle.service.SubtitleUploader.Client
        public void onUploadingCompleted(SubtitleUploader uploader) {
            this._uploader = null;
            if (SubtitleServiceManager.this._progressDialog != null) {
                SubtitleServiceManager.this._progressDialog.dismiss();
            }
        }

        private void setDefaultWarning(@Nullable CharSequence message) {
            this._defaultWarning = message;
            if (this._warning == null || this._warning.length() == 0) {
                this._warningView.setText(this._defaultWarning);
                this._warningView.setVisibility(0);
            }
        }

        private void setDefaultWarning(int resId) {
            setDefaultWarning(SubtitleServiceManager.this._context.getString(resId));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWarning(@Nullable CharSequence message) {
            this._warning = message;
            if (message != null && message.length() > 0) {
                this._warningView.setText(message);
                this._warningView.setVisibility(0);
            } else if (this._defaultWarning != null && this._defaultWarning.length() > 0) {
                this._warningView.setText(this._defaultWarning);
                this._warningView.setVisibility(0);
            } else {
                this._warningView.setText("");
                this._warningView.setVisibility(8);
            }
        }

        private void setWarning(int resId) {
            setWarning(SubtitleServiceManager.this._context.getString(resId));
        }

        private CharSequence buildMessage() {
            String message = SubtitleServiceManager.this._context.getString(R.string.uploading_confirm);
            SpannableStringBuilder sb = new SpannableStringBuilder(message);
            int indexSites = sb.toString().indexOf("%1$s");
            if (indexSites >= 0) {
                String[] sites = SubtitleServiceManager.this.getSites();
                CharSequence siteNames = TextUtils.join(", ", sites);
                sb.replace(indexSites, indexSites + 4, siteNames);
                sb.setSpan(this._siteSpan, indexSites, siteNames.length() + indexSites, 33);
            }
            return sb;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeSite() {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing() && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().hasDialogAfter(SubtitleServiceManager.this._confirmDialog)) {
                new SiteSelector(SubtitleServiceManager.this._dialogPlatform, SubtitleServiceManager.this._sites, this);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeLanguage(final int itemPosition) {
            if (!SubtitleServiceManager.this._dialogPlatform.isFinishing() && SubtitleServiceManager.this._confirmDialog != null && !SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().hasDialogAfter(SubtitleServiceManager.this._confirmDialog)) {
                final LocaleSingleSelector selector = new LocaleSingleSelector(SubtitleServiceManager.this._context);
                selector.setDefaultLocale(this._items[itemPosition].locale);
                selector.setFlags(1);
                AlertDialog dialog = selector.create();
                dialog.setTitle(R.string.detail_language);
                SubtitleServiceManager.this._dialogPlatform.showDialog(dialog, new DialogInterface.OnDismissListener() { // from class: com.mxtech.videoplayer.subtitle.service.SubtitleServiceManager.UploadHandler.5
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialog2) {
                        SubtitleServiceManager.this._dialogPlatform.getDialogRegistry().onDismiss(dialog2);
                        UploadHandler.this._items[itemPosition].locale = selector.getLocale();
                        UploadHandler.this.notifyChanged();
                        UploadHandler.this.updateButtonsState();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDetector(AsyncTask2<Subtitle, Void, Object> detector) {
            this._langDetector = detector;
            String buttonText = detector != null ? SubtitleServiceManager.this._context.getString(R.string.detecting) : SubtitleServiceManager.this._context.getString(R.string.detect_language);
            this._detectLanguageButton.setText(buttonText);
            updateButtonsState();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateButtonsState() {
            UploadItem[] uploadItemArr;
            boolean hasEmptyLocale = false;
            boolean hasUploadableItems = false;
            int i = 0;
            for (UploadItem item : this._items) {
                if (item.enabled) {
                    if (item.locale == null) {
                        hasEmptyLocale = true;
                    }
                    if (this._listView.isItemChecked(i)) {
                        hasUploadableItems = true;
                    }
                }
                i++;
            }
            this._submitButton.setEnabled(hasUploadableItems);
            if (this._langDetector == null && hasEmptyLocale) {
                this._detectLanguageButton.setEnabled(true);
                this._detectLanguageButton.setTextColor(this._detectLanguageButtonLinkColor);
            } else {
                this._detectLanguageButton.setEnabled(false);
                this._detectLanguageButton.setTextColor(this._detectLanguageButtonNormalColor);
            }
            if (hasEmptyLocale) {
                this._detectLanguageButton.setVisibility(0);
                this._listView.setNextFocusDownId(R.id.detect_language);
                return;
            }
            this._detectLanguageButton.setVisibility(8);
            this._listView.setNextFocusDownId(16908313);
        }

        private void updateServices() {
            UploadItem[] uploadItemArr;
            SubtitleService[] subtitleServiceArr;
            this._services = SubtitleServiceManager.this.getServices(this._services);
            for (UploadItem item : this._items) {
                item.enabled = false;
                for (SubtitleService service : this._services) {
                    if (service.isSupported(item.sub.subtitle.filename())) {
                        item.enabled = true;
                    }
                }
            }
            super.notifyChanged();
        }
    }
}
