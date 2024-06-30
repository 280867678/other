package com.mxtech.videoplayer.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import com.mxtech.app.AppUtils;
import com.mxtech.media.BuiltinPlayer;
import com.mxtech.preference.AppCompatListPreference;
import com.mxtech.preference.MXPreferenceFragment;
import com.mxtech.videoplayer.App;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.widget.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlrpc.android.IXMLRPCSerializer;

/* loaded from: classes.dex */
public final class SubtitlePreferences extends AbstractPreferenceActivity {
    private static final String TAG = App.TAG + ".SubtitlePreferences";
    private static CharSequence[] _charsetEntries;
    private static CharSequence[] _charsetEntryValues;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.preference.AbstractPreferenceActivity, com.mxtech.preference.ToolbarPreferenceActivity, com.mxtech.preference.MXPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_subtitle);
        updateSubtitleCharset(findPreference(Key.SUBTITLE_CHARSET));
        updateSubtitle(getPreferenceScreen(), findPreference(Key.SUBTITLE_FOLDER), findPreference(Key.SUBTITLE_SHOW_HW));
        updateFontFolder(findPreference(Key.FONT_FOLDER));
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        Preference pref = findPreference("tuner_subtitle_text");
        if (pref != null) {
            ((TunerSubtitleText) pref).onLowMemory();
        }
        super.onLowMemory();
    }

    @TargetApi(11)
    /* loaded from: classes.dex */
    public static final class Fragment extends MXPreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_subtitle);
            SubtitlePreferences.updateSubtitleCharset(findPreference(Key.SUBTITLE_CHARSET));
            SubtitlePreferences.updateSubtitle(getPreferenceScreen(), findPreference(Key.SUBTITLE_FOLDER), findPreference(Key.SUBTITLE_SHOW_HW));
            SubtitlePreferences.updateFontFolder(findPreference(Key.FONT_FOLDER));
        }

        @Override // android.app.Fragment, android.content.ComponentCallbacks
        public void onLowMemory() {
            Preference pref = findPreference("tuner_subtitle_text");
            if (pref != null) {
                ((TunerSubtitleText) pref).onLowMemory();
            }
            super.onLowMemory();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SubtitleFolderChooser implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener {
        private Preference _pref;

        private SubtitleFolderChooser() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            File folder;
            Context context = preference.getContext();
            AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (activity != null && !activity.isFinishing()) {
                this._pref = preference;
                FileChooser chooser = new FileChooser(context);
                chooser.setCanceledOnTouchOutside(true);
                String path = App.prefs.getString(Key.SUBTITLE_FOLDER, null);
                if (path == null) {
                    folder = Environment.getExternalStorageDirectory();
                } else {
                    folder = new File(path);
                }
                chooser.setTitle(R.string.subtitle_folder_choose);
                chooser.setExtensions(new String[0]);
                chooser.setDirectory(folder);
                chooser.setButton(-1, context.getString(17039370), this);
                chooser.setButton(-2, context.getString(17039360), (DialogInterface.OnClickListener) null);
                chooser.setOnDismissListener(activity.getDialogRegistry());
                activity.getDialogRegistry().register(chooser);
                chooser.show();
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            FileChooser chooser = (FileChooser) dialog;
            String folder = which == -1 ? chooser.getCurrentDirectory().getPath() : null;
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putString(Key.SUBTITLE_FOLDER, folder);
            AppUtils.apply(editor);
            if (folder != null) {
                this._pref.setSummary(folder);
            } else {
                this._pref.setSummary(R.string.subtitle_folder_summary);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSubtitle(PreferenceGroup subtitleLoad, Preference prefFolder, Preference prefShowHW) {
        String folder = App.prefs.getString(Key.SUBTITLE_FOLDER, null);
        if (folder != null) {
            prefFolder.setSummary(folder);
        }
        prefFolder.setOnPreferenceClickListener(new SubtitleFolderChooser());
        if (!BuiltinPlayer.isHardSubtitleSupported() && !subtitleLoad.removePreference(prefShowHW)) {
            Log.e(TAG, "Can't remove preference: " + prefShowHW.getKey());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FontFolderChooser implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener {
        private Preference _pref;

        private FontFolderChooser() {
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Context context = preference.getContext();
            AbstractPreferenceActivity activity = (AbstractPreferenceActivity) AppUtils.getActivityFrom(context);
            if (activity != null && !activity.isFinishing()) {
                this._pref = preference;
                FileChooser chooser = new FileChooser(context);
                chooser.setCanceledOnTouchOutside(true);
                chooser.setTitle(R.string.font_browse_title);
                chooser.setExtensions(new String[0]);
                chooser.setDirectory(preference.getSummary().toString());
                chooser.setButton(-1, context.getString(17039370), this);
                chooser.setButton(-2, context.getString(17039360), (DialogInterface.OnClickListener) null);
                chooser.setOnDismissListener(activity.getDialogRegistry());
                activity.getDialogRegistry().register(chooser);
                chooser.show();
            }
            return true;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            FileChooser chooser = (FileChooser) dialog;
            String folder = which == -1 ? chooser.getCurrentDirectory().getPath() : null;
            SharedPreferences.Editor editor = App.prefs.edit();
            editor.putString(Key.FONT_FOLDER, folder);
            AppUtils.apply(editor);
            this._pref.setSummary(folder);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateFontFolder(Preference pref) {
        File folder = P.getFontFolder();
        pref.setSummary(folder.getPath());
        pref.setOnPreferenceClickListener(new FontFolderChooser());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSubtitleCharset(Preference pref) {
        if (_charsetEntries == null) {
            ArrayList<CharSequence> entries = new ArrayList<>();
            ArrayList<CharSequence> values = new ArrayList<>();
            Context context = pref.getContext();
            entries.add(context.getString(R.string.auto_detect));
            values.add("");
            XmlResourceParser xml = context.getResources().getXml(R.xml.charsets);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(17170448));
            try {
                for (int eventType = xml.getEventType(); eventType != 1; eventType = xml.next()) {
                    if (eventType == 2) {
                        if (xml.getName().equals("charset")) {
                            String name = xml.getAttributeValue(null, IXMLRPCSerializer.TAG_NAME);
                            String displayName = xml.getAttributeValue(null, "display_name");
                            if (Charset.isSupported(name)) {
                                if (displayName == null) {
                                    entries.add(name);
                                } else {
                                    SpannableString s = new SpannableString(displayName + " (" + name + ")");
                                    s.setSpan(colorSpan, displayName.length(), s.length(), 33);
                                    entries.add(s);
                                }
                                values.add(name);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "", e);
            } catch (XmlPullParserException e2) {
                Log.e(TAG, "", e2);
            }
            _charsetEntries = (CharSequence[]) entries.toArray(new CharSequence[entries.size()]);
            _charsetEntryValues = (CharSequence[]) values.toArray(new CharSequence[values.size()]);
        }
        AppCompatListPreference listPref = (AppCompatListPreference) pref;
        listPref.setEntries(_charsetEntries);
        listPref.setEntryValues(_charsetEntryValues);
    }
}
