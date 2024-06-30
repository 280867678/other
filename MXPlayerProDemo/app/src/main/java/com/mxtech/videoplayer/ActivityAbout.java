package com.mxtech.videoplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import com.google.android.gms.plus.PlusShare;
import com.mxtech.IOUtils;
import com.mxtech.LocaleUtils;
import com.mxtech.StringUtils;
import com.mxtech.ViewUtils;
import com.mxtech.WebViewUtils;
import com.mxtech.videoplayer.L;
import com.mxtech.videoplayer.preference.Key;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.pro.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class ActivityAbout extends ActivityThemed {
    private static final String CUSTOM_CODEC = "fdb";
    private static final String CUSTOM_CODEC_DISPLAY_URL = "fdb_display_url";
    private static final String CUSTOM_CODEC_ENTITY_VISIBLITY = "display_fdb";
    private static final String CUSTOM_CODEC_REAL_URL = "fdb_real_url";
    private static final int MAX_BUFFER = 32768;
    private static final String PREFIX_TEXT = "text:";
    public static final String TAG = App.TAG + ".About";
    private WebView _wv;

    /* JADX INFO: Access modifiers changed from: private */
    public static void appendSupport(StringBuilder b, String name, String content) {
        b.append("<b>").append(name).append("</b>").append(" - ").append(content).append("<br/>");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String handleBlock(String html, String tag) {
        L.sb.setLength(0);
        Pattern p = Pattern.compile(L.sb.append("\\<\\%").append(tag).append("(.+?)\\%\\>").toString(), 32);
        Matcher m = p.matcher(html);
        String html2 = m.replaceAll("$1");
        Pattern p2 = Pattern.compile("\\<\\%.+?\\%\\>", 32);
        Matcher m2 = p2.matcher(html2);
        return m2.replaceAll("");
    }

    /* loaded from: classes.dex */
    private static class Translator implements Comparable<Translator> {
        private static final int MATCH_LANGUAGE = -1;
        private static final int MATCH_LOCALE = -2;
        private static final int MATCH_NONE = 0;
        final String lang;
        final String name;
        final int priority;

        Translator(String name, String lang, String code, String currentLocale, String currentLanguage) {
            int i;
            this.name = name;
            this.lang = lang;
            if (currentLocale.equals(code)) {
                i = -2;
            } else {
                i = currentLanguage.equals(code) ? -1 : 0;
            }
            this.priority = i;
        }

        @Override // java.lang.Comparable
        public int compareTo(Translator another) {
            return this.priority - another.priority;
        }

        void append(StringBuilder b, boolean translatorOfCurrentLanguage) {
            String displayName;
            String[] names = this.name.split("\\|");
            if (names.length == 2) {
                if (translatorOfCurrentLanguage) {
                    displayName = names[0];
                } else {
                    displayName = names[1];
                }
            } else {
                displayName = this.name;
            }
            int taskResId = R.string.translation;
            int colon = displayName.indexOf(58);
            if (colon == displayName.length() - 2) {
                if (displayName.charAt(colon + 1) == 'p') {
                    taskResId = R.string.proofreading;
                }
                displayName = displayName.substring(0, colon);
            }
            if (displayName.charAt(0) == '<') {
                b.append(displayName);
            } else {
                b.append("<b>").append(displayName).append("</b>");
            }
            b.append(" - ").append(StringUtils.getString_s(taskResId, this.lang)).append("<br/>");
        }
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.about);
        new Creator();
    }

    /* loaded from: classes.dex */
    private class Creator extends WebViewClient {
        Creator() {
            String template;
            boolean displayCustomCodec = L.authorizer.getCustomCodecInteractionMode() >= 1;
            try {
                PackageInfo packageInfo = ActivityAbout.this.getPackageManager().getPackageInfo(ActivityAbout.this.getPackageName(), 0);
                TypedArray a = ActivityAbout.this.obtainStyledAttributes(R.styleable.About);
                ActivityAbout.this._wv = (WebView) ActivityAbout.this.findViewById(R.id.content);
                HashMap<String, String> map = new HashMap<>();
                Resources res = ActivityAbout.this.getResources();
                byte[] buffer = new byte[32768];
                InputStream input = res.openRawResource(R.raw.about);
                try {
                    int bytes = IOUtils.readToEnd(input, buffer);
                    String template2 = new String(buffer, 0, bytes);
                    map.put("direction", isCurrentLocaleRtl() ? "rtl" : "ltr");
                    map.put("cpu_arch", L.getSysArchName());
                    map.put(PlusShare.KEY_CALL_TO_ACTION_LABEL, res.getString(packageInfo.applicationInfo.labelRes));
                    map.put("change_log", res.getString(R.string.change_log));
                    map.put("support", res.getString(R.string.support));
                    map.put("support_content", res.getString(R.string.support_content));
                    map.put("home", res.getString(R.string.home));
                    map.put("home_url", res.getString(R.string.home_url));
                    map.put("faq", res.getString(R.string.faq));
                    map.put("faq_url", res.getString(R.string.faq_url));
                    map.put("forum", res.getString(R.string.forum));
                    map.put("forum_url", res.getString(R.string.forum_url));
                    map.put("translation_project", res.getString(R.string.translation_project));
                    map.put("error_report", res.getString(R.string.error_report));
                    map.put("thanks_to", res.getString(R.string.thanks_to));
                    map.put("thanks_to_all", res.getString(R.string.thanks_to_all));
                    map.put("open_source_license", res.getString(R.string.cfg_open_source_license));
                    map.put("open_source_license_content", StringUtils.makeSquareBracketHyperlinkAnchor(R.string.open_source_license_content, "text:open_source_license"));
                    map.put("primary_text_color", String.format("#%06X", Integer.valueOf(a.getColor(R.styleable.About_aboutPrimaryTextColor, 0) & ViewCompat.MEASURED_SIZE_MASK)));
                    map.put("secondary_text_color", String.format("#%06X", Integer.valueOf(a.getColor(R.styleable.About_aboutSecondaryTextColor, 0) & ViewCompat.MEASURED_SIZE_MASK)));
                    map.put("line_color", String.format("#%06X", Integer.valueOf(a.getColor(R.styleable.About_aboutLineColor, 0) & ViewCompat.MEASURED_SIZE_MASK)));
                    if (displayCustomCodec) {
                        map.put(ActivityAbout.CUSTOM_CODEC_ENTITY_VISIBLITY, "visible");
                        map.put(ActivityAbout.CUSTOM_CODEC, res.getString(R.string.custom_codec));
                        map.put(ActivityAbout.CUSTOM_CODEC_REAL_URL, L.getCustomCodecRealUrl());
                        map.put(ActivityAbout.CUSTOM_CODEC_DISPLAY_URL, L.getCustomCodecDisplayUrl());
                    } else {
                        map.put(ActivityAbout.CUSTOM_CODEC_ENTITY_VISIBLITY, "hidden");
                    }
                    L.sb.setLength(0);
                    L.sb.append(res.getString(R.string.version)).append(' ').append(packageInfo.versionName);
                    map.put("version", L.sb.toString());
                    Boolean licensed = ((App) ActivityAbout.this.getApplication()).isLicenseVerified();
                    if (licensed == null) {
                        template = ActivityAbout.this.handleBlock(template2, "free");
                    } else if (licensed.booleanValue()) {
                        map.put("license_statement", res.getString(R.string.license_verified));
                        template = ActivityAbout.this.handleBlock(template2, "licensed");
                    } else {
                        map.put("license_statement", res.getString(R.string.license_not_verified));
                        template = ActivityAbout.this.handleBlock(template2, "not_licensed");
                    }
                    if (App.prefs.contains(Key.CUSTOM_CODEC_PATH)) {
                        try {
                            L.CustomCodecVersionName cc = L.getCustomCodecVersionName();
                            L.sb.setLength(0);
                            L.sb.append(", ").append(res.getString(R.string.custom_codec).toLowerCase()).append(' ').append(cc.version);
                            map.put("custom_codec_version", L.sb.toString());
                        } catch (Exception e) {
                        }
                    }
                    L.sb.setLength(0);
                    ActivityAbout.appendSupport(L.sb, res.getString(R.string.user_supporters), res.getString(R.string.user_support));
                    L.sb.append("<br/>");
                    String[] names = res.getStringArray(R.array.translator_names);
                    String[] langs = res.getStringArray(R.array.translator_languages);
                    String[] codes = res.getStringArray(R.array.translator_codes);
                    if (names.length != langs.length || langs.length != codes.length) {
                        Log.e(ActivityAbout.TAG, "translator_names=" + names.length + " translator_languages=" + langs.length + " translator_codes=" + codes.length);
                        a.recycle();
                        return;
                    }
                    Locale locale = Locale.getDefault();
                    String currentLocale = locale.toString();
                    String currentLanguage = locale.getLanguage();
                    int numTranslators = names.length;
                    List<Translator> translators = new ArrayList<>(numTranslators);
                    for (int i = 0; i < numTranslators; i++) {
                        translators.add(new Translator(names[i], langs[i], codes[i], currentLocale, currentLanguage));
                    }
                    Collections.sort(translators);
                    translators.get(0).append(L.sb, true);
                    for (int i2 = 1; i2 < numTranslators; i2++) {
                        translators.get(i2).append(L.sb, false);
                    }
                    map.put("translators", L.sb.toString());
                    ActivityAbout.this._wv.loadDataWithBaseURL("file:///android_asset/", StringUtils.format(template, map, false), "text/html", "utf-8", null);
                    input.close();
                    ActivityAbout.this._wv.setBackgroundColor(0);
                    ActivityAbout.this._wv.setWebViewClient(this);
                    a.recycle();
                } finally {
                    input.close();
                }
            } catch (Exception e2) {
                Log.e(ActivityAbout.TAG, "", e2);
            }
        }

        private boolean isCurrentLocaleRtl() {
            Locale current = Locale.getDefault();
            try {
                Locale[] locales = P.getNativeLocales();
                String[] localeNames = P.getNativeLocaleNames();
                String currentCode = current.toString();
                for (int i = 0; i < locales.length; i++) {
                    if (locales[i].equals(currentCode)) {
                        return LocaleUtils.isAnyRtl(localeNames[i]);
                    }
                }
                String currentLanguage = current.getLanguage();
                for (int i2 = 0; i2 < locales.length; i2++) {
                    if (locales[i2].getLanguage().equals(currentLanguage)) {
                        return LocaleUtils.isAnyRtl(localeNames[i2]);
                    }
                }
            } catch (Exception e) {
                Log.e(ActivityAbout.TAG, "", e);
            }
            return LocaleUtils.isAnyRtl(current.getDisplayName());
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ActivityAbout.PREFIX_TEXT)) {
                if (!ActivityAbout.this.isFinishing()) {
                    String path = url.substring(ActivityAbout.PREFIX_TEXT.length());
                    if ("open_source_license".equals(path)) {
                        try {
                            ActivityAbout.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(ActivityAbout.this.getString(R.string.open_source_license_url))));
                        } catch (Exception e) {
                            Log.e(ActivityAbout.TAG, "", e);
                        }
                    }
                }
            } else {
                try {
                    ActivityAbout.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                } catch (Exception e2) {
                    Log.e(ActivityAbout.TAG, "", e2);
                }
            }
            return true;
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        WebViewUtils.onResume(this._wv);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.videoplayer.ActivityThemed, com.mxtech.videoplayer.ActivityVPBase, com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        WebViewUtils.enableTimers(true);
    }

    @Override // com.mxtech.app.ToolbarAppCompatActivity, com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        WebViewUtils.enableTimers(false);
        WebViewUtils.onPause(this._wv);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.mxtech.app.MXAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ViewUtils.removeFromParent(this._wv);
        this._wv.destroy();
        this._wv = null;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"NewApi"})
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
