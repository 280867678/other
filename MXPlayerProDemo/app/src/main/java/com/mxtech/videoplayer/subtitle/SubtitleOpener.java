package com.mxtech.videoplayer.subtitle;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import com.mxtech.app.DialogRegistry;
import com.mxtech.media.MediaUtils;
import com.mxtech.subtitle.SubtitleFactory;
import com.mxtech.videoplayer.pro.R;
import com.mxtech.videoplayer.preference.P;
import com.mxtech.videoplayer.subtitle.SubtitlePanel;
import com.mxtech.widget.FileChooser;
import java.io.File;

/* loaded from: classes.dex */
public class SubtitleOpener implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private final boolean _alreadyHasSubtitles;
    private final SubtitlePanel.Client _client;
    private final Activity _context;
    private final DialogRegistry _dialogRegistry;
    private File _selected;

    public SubtitleOpener(Activity context, DialogRegistry dialogRegistry, Uri videoUri, boolean alreadyHasSubtitles, SubtitlePanel.Client client) {
        this._context = context;
        this._dialogRegistry = dialogRegistry;
        this._alreadyHasSubtitles = alreadyHasSubtitles;
        this._client = client;
        if (!dialogRegistry.containsInstanceOf(FileChooser.class) && !context.isFinishing()) {
            File initialDir = client.getLastSubtitleDir();
            if (initialDir == null && videoUri != null && MediaUtils.isFileUri(videoUri)) {
                initialDir = new File(videoUri.getPath()).getParentFile();
            }
            initialDir = initialDir == null ? P.subtitleFolder : initialDir;
            initialDir = initialDir == null ? Environment.getExternalStorageDirectory() : initialDir;
            FileChooser chooser = new FileChooser(context);
            chooser.setCanceledOnTouchOutside(true);
            chooser.setTitle(R.string.choose_subtitle_file);
            chooser.setExtensions(SubtitleFactory.ALL_EXTENSIONS);
            chooser.setDirectory(initialDir);
            chooser.setOnDismissListener(this);
            dialogRegistry.register(chooser);
            chooser.show();
            chooser.setOwnerActivity(context);
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            this._client.onLoadSubtitle(this._selected, false);
        } else {
            this._client.onLoadSubtitle(this._selected, true);
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this._dialogRegistry.unregister(dialog);
        if (dialog instanceof FileChooser) {
            FileChooser chooser = (FileChooser) dialog;
            this._selected = chooser.getSelectedFile();
            if (this._selected != null && !this._context.isFinishing()) {
                if (this._alreadyHasSubtitles) {
                    AlertDialog.Builder b = new AlertDialog.Builder(this._context);
                    b.setTitle(R.string.subtitle_replace_inquire_title);
                    b.setMessage(R.string.subtitle_replace_inquire);
                    b.setPositiveButton(R.string.replace, this);
                    b.setNegativeButton(R.string.add, this);
                    Dialog dlg = b.create();
                    dlg.setCanceledOnTouchOutside(true);
                    dlg.setOnDismissListener(this);
                    this._dialogRegistry.register(dlg);
                    dlg.show();
                    dlg.setOwnerActivity(this._context);
                    return;
                }
                this._client.onLoadSubtitle(this._selected, false);
            }
        }
    }
}
