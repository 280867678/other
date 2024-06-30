package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogUtils;
import com.mxtech.preference.AppCompatCheckBoxPreference;
import com.mxtech.preference.AppCompatListPreference;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.MediaDatabase;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.Serializer;
import com.mxtech.widget.FileChooser;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class GeneralPreferences extends AbstractPreferenceActivity {
    private static final String EXPORT_FILE_EXT = "xml";
    private static final String TAG = App.TAG + ".GeneralPreferences";

    static /* synthetic */ String access$600() {
        return getDefaultExportFilename();
    }

    static /* synthetic */ boolean access$700() {
        return clearHistory();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_general);
        updateUserLocale(findPreference(Key.USER_LOCALE));
        updatePlayVideoLinks((AppCompatCheckBoxPreference) findPreference("play_video_links"));
        updateSettingsPrefs(findPreference("export"), findPreference("import_from_file"), findPreference("import_from_app"), findPreference("reset_settings"));
        updateClearHistory(findPreference("clear_history"));
        updateClearThumbnail(findPreference("clear_thumbnail"));
        updateClearFontCache(findPreference("clear_font_cache"));
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_general);
            GeneralPreferences.updateUserLocale(findPreference(Key.USER_LOCALE));
            GeneralPreferences.updatePlayVideoLinks((AppCompatCheckBoxPreference) findPreference("play_video_links"));
            GeneralPreferences.updateSettingsPrefs(findPreference("export"), findPreference("import_from_file"), findPreference("import_from_app"), findPreference("reset_settings"));
            GeneralPreferences.updateClearHistory(findPreference("clear_history"));
            GeneralPreferences.updateClearThumbnail(findPreference("clear_thumbnail"));
            GeneralPreferences.updateClearFontCache(findPreference("clear_font_cache"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateUserLocale(Preference pref) {
        String[] stringArray;
        Context context = pref.getContext();
        final AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
        AppCompatListPreference listPref = (AppCompatListPreference) pref;
        Resources res = activity.getResources();
        String current = App.prefs.getString(Key.USER_LOCALE, "");
        TreeMap<String, String> entries = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String locale : res.getStringArray(R.array.translated_locales)) {
            String displayName = P.getNativeLocaleName(LocaleUtils.parse(locale));
            if (displayName.length() > 0) {
                entries.put(displayName, locale);
                if (locale.equals(current)) {
                    listPref.setSummary(displayName);
                }
            }
        }
        CharSequence[] names = new CharSequence[entries.size() + 1];
        CharSequence[] codes = new CharSequence[entries.size() + 1];
        names[0] = res.getString(R.string.system_default);
        codes[0] = "";
        int i = 1;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            names[i] = entry.getKey();
            codes[i] = entry.getValue();
            i++;
        }
        if (current.length() == 0) {
            listPref.setSummary(names[0]);
        }
        listPref.setEntries(names);
        listPref.setEntryValues(codes);
        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.1
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                String localeText = (String) obj;
                if (localeText.length() > 0) {
                    ((App) App.context).setCustomLocale(LocaleUtils.parse(localeText));
                }
                L.restart(AbstractPreferenceActivity.this, R.string.restart_app_to_change_language, true);
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updatePlayVideoLinks(AppCompatCheckBoxPreference pref) {
        pref.setChecked(P.isPlayLink());
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.2
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int newState = ((Boolean) newValue).booleanValue() ? 1 : 2;
                PackageManager pm = preference.getContext().getPackageManager();
                pm.setComponentEnabledSetting(P.webDelegateName, newState, 1);
                if (P.isAudioPlayer) {
                    pm.setComponentEnabledSetting(P.webAudioDelegateName, newState, 1);
                }
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SettingsResetHandler implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener {
        private AbstractPreferenceActivity _activity;

        private SettingsResetHandler() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference pref) {
            Context context = pref.getContext();
            this._activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (this._activity != null && !this._activity.isFinishing()) {
                AlertDialog dialog = new AlertDialog.Builder(this._activity).setTitle(R.string.reset_settings).setMessage(R.string.reset_settings_confirm).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).create();
                this._activity.showDialog((AbstractPreferenceActivity) dialog);
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dlg, int what) {
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.clear();
            editor.commit();
            L.deleteLoadedCustomCodecFiles();
            P.resetVideoExtensionList(null);
            PackageManager pm = this._activity.getPackageManager();
            pm.setComponentEnabledSetting(P.webDelegateName, 0, 1);
            pm.setComponentEnabledSetting(P.webAudioDelegateName, 0, 1);
            pm.setComponentEnabledSetting(P.localAudioDelegateName, 0, 1);
            L.restart(this._activity, R.string.reset_settings_complete);
        }
    }

    private static String getDefaultExportFilename() {
        L.sb.setLength(0);
        L.sb.append(App.context.getString(R.string.app_name_base)).append('_').append(L.getMyPackageInfo().versionName.replace('/', '_')).append('.').append(EXPORT_FILE_EXT);
        return L.sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExportHandler implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {
        private AbstractPreferenceActivity _activity;
        private File _file;
        private AlertDialog _materialSelector;
        private int _materials;

        private ExportHandler() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Context context = preference.getContext();
            this._activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (this._activity != null && !this._activity.isFinishing()) {
                AlertDialog dialog = new AlertDialog.Builder(this._activity).setTitle(R.string.export).setMultiChoiceItems(R.array.export_import_materials, new boolean[]{true, true}, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).create();
                this._materials = 63;
                this._materialSelector = (AlertDialog) this._activity.showDialog((AbstractPreferenceActivity) dialog);
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            int changed;
            if (which == 0) {
                changed = 1;
            } else {
                changed = 62;
            }
            if (isChecked) {
                this._materials |= changed;
            } else {
                this._materials &= changed ^ (-1);
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            String finalMessage;
            if (!this._activity.isFinishing()) {
                if (dialog == this._materialSelector) {
                    if (this._materials != 0) {
                        FileChooser chooser = new FileChooser(this._activity, 2);
                        chooser.setCanceledOnTouchOutside(true);
                        chooser.setTitle(R.string.export);
                        chooser.setExtensions(new String[]{GeneralPreferences.EXPORT_FILE_EXT});
                        chooser.setOutputFilename(GeneralPreferences.access$600());
                        chooser.setDirectory(Environment.getExternalStorageDirectory());
                        chooser.setButton(-1, this._activity.getString(17039370), this);
                        chooser.setButton(-2, this._activity.getString(17039360), (DialogInterface.OnClickListener) null);
                        chooser.setOnDismissListener(this._activity.getDialogRegistry());
                        this._activity.getDialogRegistry().register(chooser);
                        chooser.show();
                        return;
                    }
                    return;
                }
                if (dialog instanceof FileChooser) {
                    FileChooser chooser2 = (FileChooser) dialog;
                    String filename = chooser2.getOutputFilename();
                    if (filename.length() != 0) {
                        this._file = new File(chooser2.getCurrentDirectory(), filename);
                        if (!this._file.isDirectory()) {
                            if (this._file.exists()) {
                                this._activity.showDialog((AbstractPreferenceActivity) new AlertDialog.Builder(this._activity).setMessage(StringUtils.getString_s(R.string.confirm_overwrite, filename)).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).setPositiveButton(17039379, this).create());
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (Serializer.exportTo(this._file, this._materials)) {
                    finalMessage = StringUtils.getString_s(R.string.export_succeeded, this._file.getName());
                } else {
                    finalMessage = this._activity.getString(R.string.export_failed);
                }
                DialogUtils.alert((Activity) this._activity, (CharSequence) finalMessage);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ImportFileHandler implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnDismissListener {
        private AbstractPreferenceActivity _activity;
        private File _file;
        private int _materials;

        private ImportFileHandler() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Context context = preference.getContext();
            this._activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (this._activity == null || this._activity.isFinishing()) {
                return false;
            }
            FileChooser chooser = new FileChooser(this._activity);
            chooser.setCanceledOnTouchOutside(true);
            chooser.setTitle(R.string.import_from_file);
            chooser.setExtensions(new String[]{GeneralPreferences.EXPORT_FILE_EXT});
            chooser.setDirectory(Environment.getExternalStorageDirectory());
            chooser.setButton(-1, this._activity.getString(17039370), (DialogInterface.OnClickListener) null);
            chooser.setButton(-2, this._activity.getString(17039360), (DialogInterface.OnClickListener) null);
            chooser.setOnDismissListener(this);
            this._activity.getDialogRegistry().register(chooser);
            chooser.show();
            return true;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            this._activity.getDialogRegistry().unregister(dialog);
            if (!this._activity.isFinishing()) {
                this._file = ((FileChooser) dialog).getSelectedFile();
                if (this._file != null) {
                    Serializer.ExportFileTestResult testResult = Serializer.testExportedFile(this._file);
                    if (testResult != null) {
                        int i = testResult.contains;
                        this._materials = i;
                        if (i != 0) {
                            boolean[] checked = new boolean[2];
                            checked[0] = (this._materials & 1) != 0;
                            checked[1] = (this._materials & 62) != 0;
                            this._activity.showDialog((AbstractPreferenceActivity) new AlertDialog.Builder(this._activity).setTitle(R.string.import_from_file).setMultiChoiceItems(R.array.export_import_materials, checked, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).create());
                            return;
                        }
                    }
                    DialogUtils.alert(this._activity, R.string.export_file_invalid);
                }
            }
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            int changed;
            if (which == 0) {
                changed = 1;
            } else {
                changed = 62;
            }
            if (isChecked) {
                this._materials |= changed;
            } else {
                this._materials &= changed ^ (-1);
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (!this._activity.isFinishing() && this._materials != 0) {
                if ((this._materials & 1) != 0) {
                    SharedPreferences.Editor editor = App.prefs.edit();
                    editor.clear();
                    editor.commit();
                    P.resetVideoExtensionList(null);
                }
                if ((this._materials & 62) != 0) {
                    GeneralPreferences.access$700();
                }
                if (Serializer.importFrom(this._file, this._materials)) {
                    if ((this._materials & 1) != 0) {
                        L.restart(this._activity, R.string.import_succeeded_require_reboot);
                        return;
                    } else {
                        DialogUtils.alert(this._activity, R.string.import_succeeded);
                        return;
                    }
                }
                DialogUtils.alert((Activity) this._activity, (CharSequence) StringUtils.getString_s(R.string.import_failed, this._file.getName()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSettingsPrefs(Preference prefExport, Preference prefImportFile, Preference prefImportApp, Preference prefReset) {
        prefExport.setOnPreferenceClickListener(new ExportHandler());
        prefImportFile.setOnPreferenceClickListener(new ImportFileHandler());
        prefReset.setOnPreferenceClickListener(new SettingsResetHandler());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateClearHistory(Preference pref) {
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.3
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference pref2) {
                Context context = pref2.getContext();
                final AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
                if (activity != null && !activity.isFinishing()) {
                    AlertDialog.Builder b = new AlertDialog.Builder(activity);
                    b.setTitle(R.string.cfg_clear_history);
                    b.setMessage(R.string.cfg_inquire_clear_history);
                    b.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
                    b.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.3.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dlg, int what) {
                            if (GeneralPreferences.access$700()) {
                                Toast.makeText(activity, R.string.cfg_message_history_cleared, 0).show();
                            } else if (!activity.isFinishing()) {
                                DialogUtils.alert(activity, R.string.error_database);
                            }
                        }
                    });
                    AlertDialog dlg = b.create();
                    dlg.setCanceledOnTouchOutside(true);
                    dlg.setOnDismissListener(activity.getDialogRegistry());
                    activity.getDialogRegistry().register(dlg);
                    dlg.show();
                }
                return true;
            }
        });
    }

    private static boolean clearHistory() {
        try {
            MediaDatabase mdb = MediaDatabase.getInstance();
            mdb.clearHistory();
            mdb.release();
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateClearThumbnail(Preference pref) {
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.4
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference pref2) {
                Context context = pref2.getContext();
                final AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
                if (activity != null && !activity.isFinishing()) {
                    AlertDialog.Builder b = new AlertDialog.Builder(activity);
                    b.setTitle(R.string.cfg_clear_thumbnail);
                    b.setMessage(R.string.cfg_inquire_clear_thumbnail);
                    b.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
                    b.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.4.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dlg, int what) {
                            try {
                                MediaDatabase mdb = MediaDatabase.getInstance();
                                L.thumbStorage.clear();
                                mdb.clearCachedInfo(false);
                                L.thumbCache.clear();
                                Toast.makeText(activity, R.string.cfg_message_thumbnail_cleared, 0).show();
                                mdb.release();
                            } catch (SQLiteException e) {
                                Log.e(GeneralPreferences.TAG, "", e);
                                if (!activity.isFinishing()) {
                                    DialogUtils.alert(activity, R.string.error_database);
                                }
                            }
                        }
                    });
                    AlertDialog dlg = b.create();
                    dlg.setCanceledOnTouchOutside(true);
                    dlg.setOnDismissListener(activity.getDialogRegistry());
                    activity.getDialogRegistry().register(dlg);
                    dlg.show();
                }
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateClearFontCache(Preference pref) {
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.5
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference pref2) {
                Context context = pref2.getContext();
                final AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
                if (activity != null && !activity.isFinishing()) {
                    AlertDialog.Builder b = new AlertDialog.Builder(activity);
                    b.setTitle(R.string.clear_font_cache);
                    b.setMessage(R.string.clear_font_cache_confirm);
                    b.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
                    b.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.mxtech.videoplayer.preference.GeneralPreferences.5.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dlg, int what) {
                            L.clearFontCache();
                            Toast.makeText(activity, R.string.clear_font_cache_completed, 0).show();
                        }
                    });
                    AlertDialog dlg = b.create();
                    dlg.setCanceledOnTouchOutside(true);
                    dlg.setOnDismissListener(activity.getDialogRegistry());
                    activity.getDialogRegistry().register(dlg);
                    dlg.show();
                }
                return true;
            }
        });
    }
}
