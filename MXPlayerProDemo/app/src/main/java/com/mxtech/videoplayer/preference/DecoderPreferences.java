package com.mxtech.videoplayer.preference;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.mxtech.FileUtils;
import com.mxtech.StringUtils;
import com.mxtech.app.ActivityRegistry;
import com.mxtech.app.AppUtils;
import com.mxtech.app.DialogRegistry;
import com.mxtech.os.Cpu;
import com.mxtech.preference.AppCompatCheckBoxPreference;
import com.mxtech.preference.AppCompatListPreference;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.FileChooser;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes.dex */
public final class DecoderPreferences extends AbstractPreferenceActivity {
    static final int MB = 1048576;
    private static final String TAG = App.TAG + ".DecoderPreferences";
    private static CustomCodecChecker _runningChecked;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_decoder);
        updateDecoder(this, (PreferenceGroup) findPreference("category_hardware_decoder"), (AppCompatListPreference) findPreference(Key.CORE_LIMIT), findPreference(Key.CUSTOM_CODEC_PATH), findPreference(Key.OMX_DECODER), findPreference(Key.OMX_DECODER_LOCAL), findPreference(Key.OMX_DECODER_NETWORK), findPreference(Key.TRY_HW_IF_OMX_FAILS), findPreference(Key.OMX_VIDEO_CODECS), findPreference(Key.OMX_AUDIO_WITH_SW_VIDEO), findPreference(Key.TRY_OMX_IF_HW_FAILS), (PreferenceGroup) findPreference("category_general"), findPreference("download_custom_codec"), findPreference(Key.SOFTWARE_AUDIO), findPreference(Key.SOFTWARE_AUDIO_LOCAL), findPreference(Key.SOFTWARE_AUDIO_NETWORK));
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_decoder);
            DecoderPreferences.updateDecoder(getActivity(), (PreferenceGroup) findPreference("category_hardware_decoder"), (AppCompatListPreference) findPreference(Key.CORE_LIMIT), findPreference(Key.CUSTOM_CODEC_PATH), findPreference(Key.OMX_DECODER), findPreference(Key.OMX_DECODER_LOCAL), findPreference(Key.OMX_DECODER_NETWORK), findPreference(Key.TRY_HW_IF_OMX_FAILS), findPreference(Key.OMX_VIDEO_CODECS), findPreference(Key.OMX_AUDIO_WITH_SW_VIDEO), findPreference(Key.TRY_OMX_IF_HW_FAILS), (PreferenceGroup) findPreference("category_general"), findPreference("download_custom_codec"), findPreference(Key.SOFTWARE_AUDIO), findPreference(Key.SOFTWARE_AUDIO_LOCAL), findPreference(Key.SOFTWARE_AUDIO_NETWORK));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CustomCodecChooser implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private AbstractPreferenceActivity _activity;
        private File _current;
        private Preference _pref;

        private CustomCodecChooser() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            File folder;
            Context context = preference.getContext();
            this._activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (this._activity != null && !this._activity.isFinishing()) {
                this._pref = preference;
                FileChooser chooser = new FileChooser(context);
                chooser.setCanceledOnTouchOutside(true);
                String filePath = App.prefs.getString(Key.CUSTOM_CODEC_PATH, null);
                if (filePath == null) {
                    this._current = null;
                    folder = Environment.getExternalStorageDirectory();
                    File downloadDir = new File(folder, "Download");
                    if (downloadDir.isDirectory()) {
                        folder = downloadDir;
                    }
                } else {
                    this._current = new File(filePath);
                    folder = this._current.getParentFile();
                    if (!folder.exists()) {
                        folder = Environment.getExternalStorageDirectory();
                    }
                }
                chooser.setTitle(R.string.custom_codec_select);
                chooser.setExtensions(new String[]{"zip"});
                chooser.setPrefix(new String[]{"libffmpeg.mx.so."});
                chooser.setDirectory(folder);
                chooser.setButton(-3, context.getString(R.string.use_default_codec), this);
                chooser.setButton(-2, context.getString(17039360), (DialogInterface.OnClickListener) null);
                chooser.setOnDismissListener(this);
                this._activity.getDialogRegistry().register(chooser);
                chooser.show();
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -3 && this._current != null) {
                SharedPreferences.Editor edit = this._pref.getEditor();
                edit.remove(Key.CUSTOM_CODEC_PATH);
                L.removeCustomCodecChecksum(edit);
                edit.commit();
                L.deleteLoadedCustomCodecFiles();
                this._pref.setSummary(R.string.custom_codec_summary);
                L.restart(this._activity, R.string.restart_app_to_change_codec);
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            this._activity.getDialogRegistry().unregister(dialog);
            FileChooser chooser = (FileChooser) dialog;
            File selected = chooser.getSelectedFile();
            if (selected != null) {
                setCustomCodec(selected);
            }
        }

        private void setCustomCodec(File file) {
            if (!file.equals(this._current)) {
                try {
                    L.CustomCodecVersionName cc = L.getCustomCodecVersionName();
                    if (DecoderPreferences.testCustomCodec(file, cc, null)) {
                        this._pref.setSummary(file.getAbsolutePath());
                        DecoderPreferences.changeCustomCodec(file, cc.name, this._activity);
                    } else if (!this._activity.isFinishing()) {
                        this._activity.showDialog((AbstractPreferenceActivity) new AlertDialog.Builder(this._activity).setTitle(R.string.custom_codec_select).setMessage(Html.fromHtml(StringUtils.getString_s(R.string.cannot_find_custom_codec_file, cc.version, "<i>" + L.getSysArchName() + "</i>"))).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create());
                    }
                } catch (IllegalStateException e) {
                    Log.e(DecoderPreferences.TAG, "", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean testCustomCodec(File file, L.CustomCodecVersionName cc, String nameContains) {
        if (!file.isFile()) {
            return false;
        }
        long size = file.length();
        String name = file.getName();
        if (nameContains != null && !name.contains(nameContains)) {
            return false;
        }
        if (cc.name.equalsIgnoreCase(name)) {
            return 1048576 < size && size < 20971520;
        }
        if (StringUtils.endsWithIgnoreCase(name, ".zip")) {
            if (!(1048576 < size && size < 104857600)) {
                return false;
            }
            try {
                ZipFile zip = new ZipFile(file);
                Iterator it = Collections.list(zip.entries()).iterator();
                while (it.hasNext()) {
                    ZipEntry entry = (ZipEntry) it.next();
                    if (!entry.isDirectory() && cc.name.equalsIgnoreCase(entry.getName())) {
                        long size2 = entry.getSize();
                        if (1048576 < size2 && size2 < 20971520) {
                            zip.close();
                            return true;
                        }
                    }
                }
                zip.close();
            } catch (IOException e) {
                Log.w(TAG, file.getPath(), e);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void changeCustomCodec(File file, String libname, Activity activity) {
        SharedPreferences.Editor editor = App.prefs.edit();
        editor.putString(Key.CUSTOM_CODEC_PATH, file.getPath());
        editor.putString(Key.CUSTOM_CODEC_LIBNAME, libname);
        L.removeCustomCodecChecksum(editor);
        editor.commit();
        L.deleteLoadedCustomCodecFiles();
        L.restart(activity, R.string.restart_app_to_change_codec);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CustomCodecChecker implements FileFilter, DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private final Activity _activity;
        private File _candidate;
        private AlertDialog _dialog;
        private final DialogRegistry _dialogRegistry;
        private final L.CustomCodecVersionName _versionName = L.getCustomCodecVersionName();
        private final Set<File> _codecFilesSkippedByUser = new HashSet();

        CustomCodecChecker(Activity activity) throws IllegalStateException {
            String[] split;
            this._activity = activity;
            this._dialogRegistry = DialogRegistry.registryOf(activity);
            Pattern fileSizeSplitPattern = null;
            for (String file_size : App.prefs.getString(Key.CUSTOM_CODEC_CHECK_EXCLUDED, "").split(File.pathSeparator)) {
                fileSizeSplitPattern = fileSizeSplitPattern == null ? Pattern.compile("\\?") : fileSizeSplitPattern;
                String[] tokens = fileSizeSplitPattern.split(file_size);
                if (tokens.length == 3) {
                    try {
                        File skipped = new File(tokens[0]);
                        int size = Integer.parseInt(tokens[1]);
                        long time = Long.parseLong(tokens[2]);
                        if (skipped.length() == size && skipped.lastModified() == time) {
                            this._codecFilesSkippedByUser.add(skipped);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(DecoderPreferences.TAG, "", e);
                    }
                }
            }
            File sdcard = Environment.getExternalStorageDirectory();
            File downDir = new File(sdcard, "Download");
            File[] files = FileUtils.listFiles(downDir, this);
            if (files != null) {
                File uncheckedFile = getUncheckedFile(files);
                this._candidate = uncheckedFile;
                if (uncheckedFile != null) {
                    ask(activity, this._candidate);
                    return;
                }
            }
            File[] files2 = FileUtils.listFiles(sdcard, this);
            if (files2 != null) {
                File uncheckedFile2 = getUncheckedFile(files2);
                this._candidate = uncheckedFile2;
                if (uncheckedFile2 != null) {
                    ask(activity, this._candidate);
                }
            }
        }

        private File getUncheckedFile(File[] files) {
            if (files.length > 0) {
                return files[0];
            }
            return null;
        }

        private void skip(File file) {
            this._codecFilesSkippedByUser.add(file);
            L.sb.setLength(0);
            for (File skipped : this._codecFilesSkippedByUser) {
                long size = skipped.length();
                if (size > 0) {
                    L.sb.append(skipped.getPath()).append('?').append(size).append('?').append(skipped.lastModified()).append(File.pathSeparatorChar);
                }
            }
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putString(Key.CUSTOM_CODEC_CHECK_EXCLUDED, L.sb.toString());
            AppUtils.apply(editor);
        }

        @SuppressLint({"InflateParams"})
        private void ask(Activity activity, File file) {
            this._dialog = new AlertDialog.Builder(activity).setTitle(R.string.custom_codec).setNegativeButton(17039369, this).setPositiveButton(17039379, this).create();
            View v = this._dialog.getLayoutInflater().inflate(R.layout.ask_auto_searched_custom_codec, (ViewGroup) null);
            ((TextView) v.findViewById(R.id.name)).setText(file.getName());
            ((TextView) v.findViewById(R.id.path)).setText(file.getParent());
            ((TextView) v.findViewById(R.id.size)).setText(Formatter.formatShortFileSize(activity, file.length()));
            ((TextView) v.findViewById(R.id.time)).setText(DateUtils.formatDateTime(activity, file.lastModified(), 21));
            this._dialog.setView(v);
            this._dialog.setCanceledOnTouchOutside(true);
            this._dialog.setOnDismissListener(this);
            if (this._dialogRegistry != null) {
                this._dialogRegistry.register(this._dialog);
            }
            this._dialog.show();
            if (DecoderPreferences._runningChecked != null) {
                DecoderPreferences._runningChecked.cancel();
            }
            CustomCodecChecker unused = DecoderPreferences._runningChecked = this;
        }

        private void cancel() {
            this._dialog.dismiss();
        }

        @Override // java.io.FileFilter
        public boolean accept(File file) {
            return !this._codecFilesSkippedByUser.contains(file) && DecoderPreferences.testCustomCodec(file, this._versionName, this._versionName.version);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                DecoderPreferences.changeCustomCodec(this._candidate, this._versionName.name, this._activity);
            } else if (which == -2) {
                skip(this._candidate);
                if (DecoderPreferences._runningChecked == this) {
                    DecoderPreferences.checkCustomCodecFile(this._activity);
                }
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this._dialogRegistry != null) {
                this._dialogRegistry.unregister(dialog);
            }
            if (DecoderPreferences._runningChecked == this) {
                CustomCodecChecker unused = DecoderPreferences._runningChecked = null;
            }
        }
    }

    public static final void checkCustomCodecFile(Activity activity) {
        if (!L.isUsingCustomCodec() && ActivityRegistry.isVisible(activity) && !activity.isFinishing()) {
            try {
                new CustomCodecChecker(activity);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OMXVideoCodecHandler implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener, View.OnClickListener, DialogInterface.OnMultiChoiceClickListener {
        private AbstractPreferenceActivity _activity;
        private boolean[] _checked;
        private AlertDialog _dialog;
        private int[] _values;

        OMXVideoCodecHandler(Preference pref) {
            pref.setOnPreferenceClickListener(this);
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Context context = preference.getContext();
            this._activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (this._activity != null && !this._activity.isFinishing()) {
                Resources res = context.getResources();
                int codecs = P.getOMXVideoCodecs();
                this._values = res.getIntArray(R.array.omx_video_codec_values);
                this._checked = new boolean[this._values.length];
                for (int i = 0; i < this._values.length; i++) {
                    this._checked[i] = (this._values[i] & codecs) != 0;
                }
                this._dialog = (AlertDialog) this._activity.showDialog((AbstractPreferenceActivity) new AlertDialog.Builder(context).setMultiChoiceItems(R.array.omx_video_codecs, this._checked, this).setTitle(R.string.omx_video_codecs).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, this).setNeutralButton(R.string.decoder_abbr_default, (DialogInterface.OnClickListener) null).create());
                Button defaultButton = this._dialog.getButton(-3);
                if (defaultButton != null) {
                    defaultButton.setOnClickListener(this);
                }
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            SparseBooleanArray checked = this._dialog.getListView().getCheckedItemPositions();
            if (checked != null) {
                int codecs = 0;
                for (int i = checked.size() - 1; i >= 0; i--) {
                    if (checked.valueAt(i)) {
                        codecs |= this._values[checked.keyAt(i)];
                    }
                }
                SharedPreferences.Editor editor = App.prefs.edit();
                editor.putInt(Key.OMX_VIDEO_CODECS, codecs);
                AppUtils.apply(editor);
            }
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ListView listView = this._dialog.getListView();
            int codecs = P.getDefaultOMXVideoCodecs();
            for (int i = 0; i < this._values.length; i++) {
                this._checked[i] = (this._values[i] & codecs) != 0;
                listView.setItemChecked(i, this._checked[i]);
            }
        }
    }

    static void updateDecoder(final Activity activity, PreferenceGroup hardwareDecoderGroup, AppCompatListPreference prefCoreLimit, Preference prefCustomCodec, Preference prefOmx, Preference prefOmxLocal, Preference prefOmxNetwork, Preference prefTryHW, Preference prefOmxVideoCodecs, final Preference prefOmxAudio, final Preference prefTryOmx, PreferenceGroup generalGroup, Preference prefDownloadCustomCodec, Preference prefSWAudio, final Preference prefSWAudioLocal, final Preference prefSWAudioNetwork) {
        int procCount = Math.max(Cpu.coreCount, 8);
        String[] values = new String[procCount + 1];
        String[] texts = new String[procCount + 1];
        values[0] = Tuner.TAG_STYLE;
        texts[0] = activity.getString(R.string.font_default);
        for (int i = 1; i <= procCount; i++) {
            String num = Integer.toString(i);
            values[i] = num;
            texts[i] = num;
        }
        prefCoreLimit.setEntries(texts);
        prefCoreLimit.setEntryValues(values);
        String customCodecPath = App.prefs.getString(Key.CUSTOM_CODEC_PATH, null);
        if (customCodecPath != null) {
            prefCustomCodec.setSummary(customCodecPath);
        }
        prefCustomCodec.setOnPreferenceClickListener(new CustomCodecChooser());
        if (L.hasOMXDecoder) {
            boolean omxDecoderEnabled = P.isOMXDecoderEnabled();
            prefOmxAudio.setEnabled(!omxDecoderEnabled);
            prefTryOmx.setEnabled(!omxDecoderEnabled);
            ((AppCompatCheckBoxPreference) prefOmx).setChecked(omxDecoderEnabled);
            prefOmx.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.DecoderPreferences.1
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    prefOmxAudio.setEnabled(!((Boolean) newValue).booleanValue());
                    prefTryOmx.setEnabled(((Boolean) newValue).booleanValue() ? false : true);
                    return true;
                }
            });
            new OMXVideoCodecHandler(prefOmxVideoCodecs);
        } else {
            hardwareDecoderGroup.removePreference(prefOmx);
            hardwareDecoderGroup.removePreference(prefOmxLocal);
            hardwareDecoderGroup.removePreference(prefOmxNetwork);
            hardwareDecoderGroup.removePreference(prefTryHW);
            hardwareDecoderGroup.removePreference(prefOmxVideoCodecs);
            hardwareDecoderGroup.removePreference(prefOmxAudio);
            hardwareDecoderGroup.removePreference(prefTryOmx);
        }
        if (L.authorizer.getCustomCodecInteractionMode() == 0) {
            if (customCodecPath == null) {
                prefCustomCodec.setSummary(Html.fromHtml(activity.getString(R.string.notify_custom_codec_type, new Object[]{"<i>" + L.getSysArchName() + "</i>"})));
            }
            generalGroup.removePreference(prefDownloadCustomCodec);
        } else {
            prefDownloadCustomCodec.setSummary(StringUtils.getString_s(R.string.download_custom_codec_summary, L.getSysArchName()));
            prefDownloadCustomCodec.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.mxtech.videoplayer.preference.DecoderPreferences.2
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(L.getCustomCodecRealUrl())));
                        return true;
                    } catch (Exception e) {
                        Log.e(DecoderPreferences.TAG, "", e);
                        return true;
                    }
                }
            });
        }
        prefSWAudioLocal.setEnabled(!P.softAudio);
        prefSWAudioNetwork.setEnabled(!P.softAudio);
        prefSWAudio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.mxtech.videoplayer.preference.DecoderPreferences.3
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefSWAudioLocal.setEnabled(!((Boolean) newValue).booleanValue());
                prefSWAudioNetwork.setEnabled(((Boolean) newValue).booleanValue() ? false : true);
                return true;
            }
        });
    }
}
