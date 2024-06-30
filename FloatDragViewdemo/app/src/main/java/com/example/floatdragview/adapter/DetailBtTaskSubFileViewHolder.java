package com.example.floatdragview.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.floatdragview.R;
import com.example.floatdragview.datasource.TaskDetailItem;

import java.io.File;

public class DetailBtTaskSubFileViewHolder extends TaskDetailViewHolder {

    /* renamed from: c */
    private static final String f19677c = "b";

    /* renamed from: a */
    View f19678a;

    /* renamed from: b */
    public BTSubTaskInfoItemEditListener f19679b;

    /* renamed from: i */
    private TextView f19680i;

    /* renamed from: j */
    private ImageView f19681j;

    /* renamed from: k */
    private TextView f19682k;

    /* renamed from: l */
    private TextView f19683l;

    /* renamed from: m */
    private ProgressBar f19684m;

    /* renamed from: n */
    private ImageView f19685n;

    /* renamed from: o */
    private View f19686o;

    /* renamed from: p */
    private TextView f19687p;

    /* renamed from: q */
    private ImageView f19688q;

    /* renamed from: r */
    private View f19689r;

    /* renamed from: s */
    private View f19690s;

    /* renamed from: t */
    private TextView f19691t;

    /* renamed from: u */
    private View f19692u;

    /* renamed from: v */
    private View.OnClickListener f19693v;

    /* renamed from: w */
//    private C5240a f19694w;

    /* renamed from: x */
    private Handler f19695x;

    /* renamed from: y */
    private Runnable f19696y;

    /* renamed from: a */
    public static View m9357a(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.download_center_task_detail_btl_list, viewGroup, false);
    }



    public DetailBtTaskSubFileViewHolder(View view) {
        super(view);
//        this.f19693v = new View$OnClickListenerC5241c(this);
//        this.f19694w = new C5240a();
        this.f19695x = new Handler();
//        this.f19696y = new RunnableC5243e(this);
        this.f19678a = view;
        this.f19680i = (TextView) view.findViewById(R.id.titleTextView);
        this.f19681j = (ImageView) view.findViewById(R.id.iconImageView);
        this.f19682k = (TextView) view.findViewById(R.id.tagSize);
        this.f19683l = (TextView) view.findViewById(R.id.open_text);
        this.f19688q = (ImageView) view.findViewById(R.id.open_icon);
        this.f19689r = view.findViewById(R.id.openContainer);
        this.f19686o = view.findViewById(R.id.select_btn_container);
        this.f19690s = view.findViewById(R.id.taskItemLayout);
        this.f19691t = (TextView) view.findViewById(R.id.play_flag);
        this.f19684m = (ProgressBar) view.findViewById(R.id.progressBar);
        this.f19685n = (ImageView) view.findViewById(R.id.edit_mode_select_btn);
        this.f19687p = (TextView) view.findViewById(R.id.statusTextView);
        this.f19692u = view.findViewById(R.id.bt_expand_white_space);
        view.setOnClickListener(this.f19693v);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                Handler handler;
                Runnable runnable;
                Handler handler2;
                Runnable runnable2;
                if (motionEvent.getAction() == 0) {
                    handler2 = f19695x;
                    runnable2 = f19696y;
                    handler2.postDelayed(runnable2, 500L);
                }
                if ((motionEvent.getAction() == 3 || motionEvent.getAction() == 8 || motionEvent.getAction() == 1) && motionEvent.getX() != 0.0f) {
                    handler = f19695x;
                    runnable = f19696y;
                    handler.removeCallbacks(runnable);
                    return false;
                }
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
//    @Nullable
//    /* renamed from: e */
//    public BTSubTaskItem m9347e() {
//        if (m9361b() == null) {
//            return null;
//        }
//        return (BTSubTaskItem) m9361b().m9364a(BTSubTaskItem.class);
//    }

    @SuppressLint("WrongConstant")
    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9109a(TaskDetailItem taskDetailItem) {
        String byteConvert;
        String m8453b;
        m9360b(taskDetailItem);
//        if (DownloadError.m8506a(taskDetailItem.f19671d) == DownloadError.FailureCode.TORRENT_NOT_EXIST) {
//            this.f19678a.setOnClickListener(null);
//        } else {
            this.f19678a.setOnClickListener(this.f19693v);
//        }
//        BTSubTaskItem bTSubTaskItem = (BTSubTaskItem) taskDetailItem.m9364a(BTSubTaskItem.class);
//        bTSubTaskItem.setViewModel(this.f19694w);
        this.f19680i.setText("bTSubTaskItem");
        this.f19680i.requestLayout();
//        XLFileTypeUtil.getFileCategoryTypeByName(bTSubTaskItem.mLocalFileName);
//        int fileIconResId = XLFileTypeUtil.getFileIconResId(bTSubTaskItem.mLocalFileName);
//        if (fileIconResId == R.drawable.ic_dl_apk) {
//            Drawable m8457a = TaskHelper.m8457a(bTSubTaskItem.mLocalFileName, this.itemView.getContext());
//            if (m8457a != null) {
//                this.f19681j.setImageDrawable(m8457a);
//            } else {
//                this.f19681j.setImageResource(fileIconResId);
//            }
//        } else {
            this.f19681j.setImageResource(R.drawable.ic_dl_video);
//        }
//        if (bTSubTaskItem.mFileSize == 0) {
            byteConvert =  "未知大小";
//        } else {
//            byteConvert = ConvertUtil.byteConvert(bTSubTaskItem.mFileSize);
//            if ((bTSubTaskItem.mTaskStatus == 8 || bTSubTaskItem.mTaskStatus == 16) && bTSubTaskItem.mTaskStatus == 8 && bTSubTaskItem.mTitle.endsWith(ShareConstants.PATCH_SUFFIX) && (m8453b = TaskHelper.m8453b(bTSubTaskItem.mLocalFileName, this.itemView.getContext())) != null) {
//                byteConvert = "版本:" + m8453b + "  " + byteConvert;
//            }
//        }
        this.f19682k.setText(byteConvert);
//        if (bTSubTaskItem != null) {
//            if (m9350c(bTSubTaskItem)) {
//                this.f19684m.setVisibility(8);
//            } else {
                this.f19684m.setVisibility(0);
                this.f19684m.setProgress((int) (2 * 100.0f));
//            }
//        }
        TextView textView = this.f19687p;
        String str = "";
//        int i = bTSubTaskItem.mTaskStatus;
//        if (i == 1) {
            str = "等待下载";
//        } else if (i == 4) {
//            str = "下载暂停";
//        } else if (i != 8 && i == 16) {
//            str = "下载失败";
//        }
//        if (bTSubTaskItem.mTaskStatus == 2 && bTSubTaskItem.mFileSize != 0 && bTSubTaskItem.mDownloadedSize >= bTSubTaskItem.mFileSize) {
            str = "校验中";
//        }
        textView.setTextColor(Color.parseColor("#94969f"));
//        if (!TextUtils.isEmpty(bTSubTaskItem.mLocalFileName) && bTSubTaskItem.isFileMissing() && bTSubTaskItem.mTaskStatus == 8) {
//            str = "";
//        }
        if (TextUtils.isEmpty(str)) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
        }
        textView.setText(str);
        this.f19689r.setBackgroundResource(R.drawable.task_btn_normal_blue_bg);
        this.f19691t.setVisibility(8);
//        if (m9350c(bTSubTaskItem)) {
//            if (bTSubTaskItem.isFileMissing()) {
//                this.f19691t.setText("文件已移除");
//                this.f19691t.setVisibility(0);
//                this.f19691t.setTextColor(this.itemView.getContext().getResources().getColor(R.color.DownloadTaskItemStatusTextColor));
//                this.f19689r.setBackgroundResource(R.drawable.task_detail_play_downloading_btn_bg_gray);
//            } else if (m9348d(bTSubTaskItem) && m9350c(bTSubTaskItem)) {
//                C7002v.m4877a().m4869a(bTSubTaskItem.mLocalFileName, new C5244f(this, bTSubTaskItem), null, true);
//            }
//        } else if (!m9348d(bTSubTaskItem)) {
//            this.f19689r.setBackgroundResource(R.drawable.task_detail_play_downloading_btn_bg_gray);
//        }
        if (m9344f()) {
            this.f19686o.setVisibility(0);
            this.f19689r.setVisibility(8);
//            if (bTSubTaskItem.isSelected()) {
//                this.f19685n.setImageResource(R.drawable.big_selected);
//                return;
//            } else {
                this.f19685n.setImageResource(R.drawable.big_unselected);
                return;
//            }
        }
        this.f19686o.setVisibility(8);
        this.f19689r.setVisibility(0);
        this.f19689r.setClickable(false);
        this.f19689r.setEnabled(false);
//        if (m9345e(bTSubTaskItem) == XLFileTypeUtil.EFileCategoryType.E_SOFTWARE_CATEGORY) {
//            this.f19683l.setText("安装");
//            this.f19688q.setImageResource(R.drawable.download_detail_install);
//        } else if (m9348d(bTSubTaskItem)) {
//            if (bTSubTaskItem.mTaskStatus == 16 || bTSubTaskItem.mTaskStatus == 2 || bTSubTaskItem.mTaskStatus == 1) {
//                this.f19683l.setText("边下边播");
//            } else {
                this.f19683l.setText("播放");
//            }
            this.f19688q.setImageResource(R.drawable.download_detail_play);
//        } else {
//            this.f19683l.setText("打开");
//            this.f19688q.setImageResource(R.drawable.download_detail_open);
//        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
//    public static boolean m9350c(BTSubTaskItem bTSubTaskItem) {
//        return bTSubTaskItem != null && bTSubTaskItem.mTaskStatus == 8;
//    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
//    public static boolean m9348d(BTSubTaskItem bTSubTaskItem) {
//        return m9345e(bTSubTaskItem) == XLFileTypeUtil.EFileCategoryType.E_VIDEO_CATEGORY;
//    }

    /* renamed from: e */
//    private static XLFileTypeUtil.EFileCategoryType m9345e(BTSubTaskItem bTSubTaskItem) {
//        XLFileTypeUtil.EFileCategoryType fileCategoryTypeByName;
//        if (bTSubTaskItem == null) {
//            return null;
//        }
//        if (bTSubTaskItem.mFileCategoryType == null || bTSubTaskItem.mFileCategoryType == XLFileTypeUtil.EFileCategoryType.E_OTHER_CATEGORY) {
//            if (!TextUtils.isEmpty(bTSubTaskItem.mLocalFileName)) {
//                fileCategoryTypeByName = XLFileTypeUtil.getFileCategoryTypeByName(bTSubTaskItem.mLocalFileName);
//            } else {
//                fileCategoryTypeByName = XLFileTypeUtil.getFileCategoryTypeByName(bTSubTaskItem.mTitle);
//            }
//            bTSubTaskItem.mFileCategoryType = fileCategoryTypeByName;
//            return fileCategoryTypeByName;
//        }
//        return bTSubTaskItem.mFileCategoryType;
//    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public boolean m9344f() {
        if (this.f19674f != null) {
            return this.f19674f.f19563g;
        }
        return false;
    }

    /* compiled from: DetailBtTaskSubFileViewHolder.java */
    /* renamed from: com.xunlei.downloadprovider.download.taskdetails.items.b$a */
    /* loaded from: classes2.dex */
//    class C5240a extends BTSubTaskViewModel {
//        @Override // com.xunlei.downloadprovider.download.taskdetails.subtask.BTSubTaskViewModel
//        /* renamed from: a */
//        public final void mo9081a() {
//        }
//
//        @Override // com.xunlei.downloadprovider.download.taskdetails.subtask.BTSubTaskViewModel
//        /* renamed from: a */
//        public final void mo9080a(BTSubTaskItem bTSubTaskItem) {
//        }
//
//        C5240a() {
//        }
//    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
//    public static /* synthetic */ void m9355a(DetailBtTaskSubFileViewHolder detailBtTaskSubFileViewHolder, BTSubTaskItem bTSubTaskItem) {
//        if (bTSubTaskItem.mTaskStatus != 8) {
//            DownloadTaskInfo d = detailBtTaskSubFileViewHolder.m9358d();
//            if (TaskHelper.m8458a(bTSubTaskItem.mTitle) && d != null && detailBtTaskSubFileViewHolder.m9359c() != null) {
//                DLCenterReporter.m9613a("dl_bxbb", d);
//                C5083m.m9935a(detailBtTaskSubFileViewHolder.f19672d.getTaskId(), bTSubTaskItem.mBTSubIndex, false);
//                VodPlayerActivityNew.m2219a(detailBtTaskSubFileViewHolder.m9359c(), detailBtTaskSubFileViewHolder.f19672d, bTSubTaskItem, "download_detail");
//                return;
//            }
//            XLToast.showToast(detailBtTaskSubFileViewHolder.m9359c(), "文件下载未完成");
//            return;
//        }
//        String str = bTSubTaskItem.mLocalFileName;
//        if (!new File(str).exists()) {
//            XLToast.showToast(detailBtTaskSubFileViewHolder.m9359c(), "文件不存在");
//        } else if (C5604e.m8312f(bTSubTaskItem.mTitle)) {
//            DownloadBtFileExplorerActivity.m10625a(detailBtTaskSubFileViewHolder.m9359c(), Uri.fromFile(new File(str)).toString(), 9);
//        } else if (XLFileTypeUtil.isLocalVodSupport(str)) {
//            if (str.contains("/")) {
//                str.substring(str.lastIndexOf("/") + 1);
//            }
//            if (detailBtTaskSubFileViewHolder.m9359c() != null) {
//                C5083m.m9935a(detailBtTaskSubFileViewHolder.f19672d.getTaskId(), bTSubTaskItem.mBTSubIndex, false);
//                VodPlayerActivityNew.m2219a(detailBtTaskSubFileViewHolder.m9359c(), detailBtTaskSubFileViewHolder.f19672d, bTSubTaskItem, "download_detail");
//            }
//        } else {
//            LocalFileOpenHelper.m10097a(detailBtTaskSubFileViewHolder.m9359c(), str, false);
//        }
//    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: h */
//    public static /* synthetic */ void m9341h(DetailBtTaskSubFileViewHolder detailBtTaskSubFileViewHolder) {
//        BTSubTaskItem m9347e = detailBtTaskSubFileViewHolder.m9347e();
//        if (m9347e != null) {
//            m9347e.setSelected(true);
//            detailBtTaskSubFileViewHolder.f19685n.setImageResource(R.drawable.big_selected);
//            if (detailBtTaskSubFileViewHolder.f19679b != null) {
//                detailBtTaskSubFileViewHolder.f19679b.mo9104b();
//            }
//        }
//    }
}
