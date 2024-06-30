package com.example.floatdragview.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.floatdragview.R;
import com.example.floatdragview.adapter.TaskDetailAdapter;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.util.DownloadCenterControl;
import com.example.floatdragview.util.DipPixelUtil;

public class DownloadTaskNameAndIconView extends FrameLayout {

    /* renamed from: R */
    private static final String f19771R = "DownloadTaskNameAndIconView";

    /* renamed from: A */
//    public ZHTextViewExpandable f19772A;

    /* renamed from: B */
    public View f19773B;

    /* renamed from: C */
    public View f19774C;

    /* renamed from: D */
    public View f19775D;

    /* renamed from: E */
    public View f19776E;

    /* renamed from: F */
    public View f19777F;

    /* renamed from: G */
    public TextView f19778G;

    /* renamed from: H */
    public TextView f19779H;

    /* renamed from: I */
    public ImageView f19780I;

    /* renamed from: J */
    public String f19781J;

    /* renamed from: K */
    public Context f19782K;

    /* renamed from: L */
//    public SampleSnapshotTagViewHolder f19783L;

    /* renamed from: M */
    public DownloadTaskInfo f19784M;

    /* renamed from: N */
//    public TaskSpeedCountInfo f19785N;

    /* renamed from: O */
    public boolean f19786O;

    /* renamed from: P */
    public int f19787P;

    /* renamed from: Q */
//    public SampleSnapshotTagViewHolder.InterfaceC5472a f19788Q;

    /* renamed from: S */
    private View f19789S;

    /* renamed from: T */
    private View f19790T;

    /* renamed from: U */
    private View f19791U;

    /* renamed from: V */
    private View f19792V;

    /* renamed from: W */
    private View f19793W;

    /* renamed from: a */
    public ImageView f19794a;

    /* renamed from: aa */
    private View f19795aa;

    /* renamed from: ab */
    private boolean f19796ab;

    /* renamed from: ac */
    private boolean f19797ac;

    /* renamed from: ad */
    private boolean f19798ad;

    /* renamed from: ae */
    private DownloadCenterControl f19799ae;

    /* renamed from: af */
    private TaskDetailAdapter f19800af;

    /* renamed from: ag */
    private View.OnClickListener f19801ag;

    /* renamed from: ah */
    private TaskDetailAdapter.InterfaceC5203a f19802ah;

    /* renamed from: b */
    public TextView f19803b;

    /* renamed from: c */
    public View f19804c;

    /* renamed from: d */
    public TextView f19805d;

    /* renamed from: e */
//    public View f19806e;

    /* renamed from: f */
//    public TextView f19807f;

    /* renamed from: g */
    public TextView f19808g;

    /* renamed from: h */
    public View f19809h;

    /* renamed from: i */
    public TextView f19810i;

    /* renamed from: j */
    public TextView f19811j;

    /* renamed from: k */
    public ImageView f19812k;

    /* renamed from: l */
    public TextView f19813l;

    /* renamed from: m */
    public TextView f19814m;

    /* renamed from: n */
    public TextView f19815n;

    /* renamed from: o */
    public TextView f19816o;

    /* renamed from: p */
    public TextView f19817p;

    /* renamed from: q */
    public TextView f19818q;

    /* renamed from: r */
    public View f19819r;

    /* renamed from: s */
    public TextView f19820s;

    /* renamed from: t */
    public TextView f19821t;

    /* renamed from: u */
    public View f19822u;

    /* renamed from: v */
    public View f19823v;

    /* renamed from: w */
    public TextView f19824w;

    /* renamed from: x */
    public View f19825x;

    /* renamed from: y */
    public View f19826y;

    /* renamed from: z */
    public View f19827z;

    public DownloadTaskNameAndIconView(Context context) {
        super(context);
        this.f19796ab = true;
        this.f19797ac = true;
        this.f19798ad = true;
        this.f19801ag = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f19797ac) {
                    m9297c();
                } else {
                    m9295d();
                }
                if (f19802ah != null) {
                    f19802ah.mo9105a(f19797ac);
                }
            }
        };
//        this.f19788Q = new C5272n(this);
        m9305a(context);
    }

    public DownloadTaskNameAndIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f19796ab = true;
        this.f19797ac = true;
        this.f19798ad = true;
        this.f19801ag = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f19797ac) {
                    m9297c();
                } else {
                    m9295d();
                }
                if (f19802ah != null) {
                    f19802ah.mo9105a(f19797ac);
                }
            }
        };
//        this.f19788Q = new C5272n(this);
        m9305a(context);
    }

    public DownloadTaskNameAndIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f19796ab = true;
        this.f19797ac = true;
        this.f19798ad = true;
        this.f19801ag = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f19797ac) {
                    m9297c();
                } else {
                    m9295d();
                }
                if (f19802ah != null) {
                    f19802ah.mo9105a(f19797ac);
                }
            }
        };
//        this.f19788Q = new C5272n(this);
        m9305a(context);
    }

    public void setControl(DownloadCenterControl downloadCenterControl) {
        this.f19799ae = downloadCenterControl;
    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    private void m9305a(Context context) {
//        this.f19787P = DownloadCenterConfig.m10877f();
        this.f19782K = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_task_detail_title, this);
        this.f19790T = inflate.findViewById(R.id.name_view_when_hide_all_data);
        this.f19789S = inflate.findViewById(R.id.task_icon_title_layout);
        this.f19794a = (ImageView) inflate.findViewById(R.id.iconImageView);
        ViewGroup.LayoutParams layoutParams = this.f19794a.getLayoutParams();
        if (this.f19787P == 0) {
            layoutParams.width = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_width);
            layoutParams.height = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_height);
        } else if (this.f19787P == 1) {
            layoutParams.width = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_style1_width);
            layoutParams.height = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_style1_height);
            this.f19794a.setImageResource(R.drawable.ic_dl_video_default_style1);
        } else {
            layoutParams.width = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_style2_width);
            layoutParams.height = this.f19794a.getResources().getDimensionPixelSize(R.dimen.task_card_icon_image_style2_height);
            this.f19794a.setImageResource(R.drawable.ic_dl_video_default_style1);
        }
        this.f19794a.setLayoutParams(layoutParams);
//        this.f19783L = new SampleSnapshotTagViewHolder(inflate.findViewById(R.id.tagSnapshot));
        this.f19803b =  inflate.findViewById(R.id.titleTextView);
        this.f19804c = inflate.findViewById(R.id.title_fore_ground);
        this.f19805d = (TextView) inflate.findViewById(R.id.tagSize);
        this.f19808g = (TextView) inflate.findViewById(R.id.tagEpisode);
//        this.f19806e = inflate.findViewById(R.id.tagPlay);
//        this.f19807f = (TextView) inflate.findViewById(R.id.tagPlay);
        this.f19809h = inflate.findViewById(R.id.tagNew);
        this.f19810i = (TextView) inflate.findViewById(R.id.speed);
        this.f19826y = inflate.findViewById(R.id.download_status_container);
        this.f19811j = (TextView) inflate.findViewById(R.id.download_status_text);
        this.f19812k = (ImageView) inflate.findViewById(R.id.download_status_iv);
        this.f19827z = inflate.findViewById(R.id.arrow_layout);
        this.f19827z.setVisibility(0);
//        this.f19772A = (ZHTextViewExpandable) inflate.findViewById(R.id.task_url_content);
        this.f19773B = inflate.findViewById(R.id.task_detail_info);
        this.f19773B.setVisibility(8);
        this.f19803b.setMaxLines(4);
        this.f19774C = inflate.findViewById(R.id.container_need_fold);
        this.f19775D = inflate.findViewById(R.id.detail_expand_space_view);
        this.f19776E = inflate.findViewById(R.id.download_size_container);
        this.f19777F = inflate.findViewById(R.id.max_speed_container);
        this.f19813l = (TextView) inflate.findViewById(R.id.download_max_speed_text);
        this.f19814m = (TextView) inflate.findViewById(R.id.download_create_time);
        this.f19815n = (TextView) inflate.findViewById(R.id.download_max_speed);
        this.f19816o = (TextView) inflate.findViewById(R.id.download_aver_speed);
        this.f19818q = (TextView) inflate.findViewById(R.id.download_save_time);
        this.f19817p = (TextView) inflate.findViewById(R.id.download_linked_resource);
        this.f19819r = inflate.findViewById(R.id.linked_resource_container);
        this.f19820s = (TextView) inflate.findViewById(R.id.downloaded_size);
        this.f19822u = inflate.findViewById(R.id.progressContainer);
        this.f19821t = (TextView) inflate.findViewById(R.id.progress);
        this.f19823v = inflate.findViewById(R.id.finish_time_container);
        this.f19824w = (TextView) inflate.findViewById(R.id.finish_time);
        this.f19825x = inflate.findViewById(R.id.source_container);
        this.f19778G = (TextView) inflate.findViewById(R.id.task_ref_url);
        this.f19779H = (TextView) inflate.findViewById(R.id.web_site_name);
        this.f19791U = inflate.findViewById(R.id.web_container);
        this.f19780I = (ImageView) inflate.findViewById(R.id.iv_collection);
        this.f19792V = inflate.findViewById(R.id.tagDivider1);
        this.f19793W = inflate.findViewById(R.id.tagDivider2);
        this.f19795aa = inflate.findViewById(R.id.task_detail_fold_container);
//        this.f19791U.setOnClickListener(new View$OnClickListenerC5264f(this));
//        this.f19780I.setOnClickListener(new View$OnClickListenerC5265g(this));
        this.f19827z.setOnClickListener(this.f19801ag);
        this.f19803b.setOnClickListener(this.f19801ag);
        this.f19795aa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m9295d();
                if (f19802ah != null) {
                    f19802ah.mo9105a(f19797ac);
                }
            }
        });
        this.f19775D.setVisibility(8);
        this.f19776E.setVisibility(0);
        this.f19777F.setVisibility(0);
        m9299a(true);
    }

    public void setCollaped(boolean z) {
        if (z) {
            m9295d();
        } else {
            m9297c();
        }
    }

    public boolean getIsInCollapedState() {
        return this.f19797ac;
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        if (this.f19780I == null) {
            return;
        }
        this.f19780I.setSelected(z);
    }

    public void setAdapter(TaskDetailAdapter taskDetailAdapter) {
        this.f19800af = taskDetailAdapter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    @SuppressLint("WrongConstant")
    public void m9297c() {
        this.f19797ac = false;
        this.f19773B.setVisibility(0);
        this.f19803b.setMaxLines(100);
        this.f19804c.setVisibility(8);
        this.f19827z.setVisibility(8);
        m9299a(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    @SuppressLint("WrongConstant")
    public void m9295d() {
        this.f19797ac = true;
        this.f19773B.setVisibility(8);
//        if (this.f19803b.getCurrentLineNum() > 4) {
//            this.f19804c.setVisibility(0);
//        }
        this.f19803b.setMaxLines(4);
        this.f19827z.setVisibility(0);
        m9299a(true);
    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public final void m9306a() {
        if (this.f19792V != null) {
            this.f19792V.setVisibility(8);
        }
        if (this.f19793W != null) {
            this.f19793W.setVisibility(8);
        }
        if (this.f19808g.getVisibility() == 0 && this.f19805d.getVisibility() == 0 && this.f19792V != null) {
            this.f19792V.setVisibility(0);
        }
        if (this.f19805d.getVisibility() == 0  && this.f19793W != null) {
            this.f19793W.setVisibility(0);
        }
        if (this.f19808g.getVisibility() == 0 && this.f19805d.getVisibility() == 8  && this.f19792V != null) {
            this.f19792V.setVisibility(0);
        }
    }

//    public void setTaskSpeedCountInfo(TaskSpeedCountInfo taskSpeedCountInfo) {
////        this.f19785N = taskSpeedCountInfo;
////        new StringBuilder("taskCountInfo :  ").append(taskSpeedCountInfo);
//    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public static void m9301a(DownloadTaskInfo downloadTaskInfo, TextView textView, TextView textView2) {
//        if (downloadTaskInfo.mFileSize > 0) {
//            textView.setText(DownloadCenterTaskUtil.m8494c(downloadTaskInfo.mFileSize));
//        } else {
            textView.setText(R.string.download_item_task_unknown_filesize);
//        }
        if (!TextUtils.isEmpty(downloadTaskInfo.mEpisodeTagText)) {
            textView2.setVisibility(0);
            textView2.setText(downloadTaskInfo.mEpisodeTagText);
            return;
        }
        textView2.setVisibility(8);
    }

    /* renamed from: a */
    @SuppressLint("WrongConstant")
    public final void m9302a(DownloadTaskInfo downloadTaskInfo) {
        if (downloadTaskInfo == null) {
            return;
        }
        boolean m8449d = true ;//TaskHelper.m8449d((TaskInfo) downloadTaskInfo);
        boolean m8447e = true; //TaskHelper.m8447e(downloadTaskInfo);
//        this.f19772A.setListener(this);
        if (m8447e) {
            if (this.f19796ab) {
                this.f19827z.setVisibility(0);
                m9299a(true);
                this.f19773B.setVisibility(8);
                this.f19803b.setMaxLines(4);
                this.f19797ac = true;
            } else {
                this.f19827z.setVisibility(8);
                m9299a(false);
                this.f19773B.setVisibility(0);
                this.f19803b.setMaxLines(100);
                this.f19774C.setVisibility(0);
                this.f19797ac = false;
            }
        } else if (this.f19796ab) {
            this.f19827z.setVisibility(0);
            m9299a(true);
            this.f19773B.setVisibility(8);
            this.f19803b.setMaxLines(4);
            this.f19797ac = true;
        } else {
            this.f19827z.setVisibility(8);
            m9299a(false);
            this.f19773B.setVisibility(0);
            this.f19803b.setMaxLines(100);
            this.f19774C.setVisibility(0);
            if (m8449d) {
                this.f19775D.setVisibility(8);
            } else {
                this.f19775D.setVisibility(0);
            }
            this.f19797ac = false;
        }
//        this.f19772A.setMaxLine(2);
        if (m8449d) {
            this.f19813l.setText("最快速度");
            this.f19798ad = false;
            return;
        }
        this.f19798ad = true;
    }

    /* renamed from: a */
    private void m9299a(boolean z) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f19811j.getLayoutParams();
        layoutParams.setMargins(0, 0, DipPixelUtil.dip2px(z ? 50 : 0), 0);
        this.f19811j.setLayoutParams(layoutParams);
    }

    public DownloadTaskInfo getCurrentTask() {
        return this.f19784M;
    }

    public void setNeedFold(boolean z) {
        this.f19796ab = z;
    }

    /* renamed from: a */
    public static void m9300a(String str, DownloadTaskInfo downloadTaskInfo) {
//        DLCenterReporter.m9613a(str, downloadTaskInfo);
    }

//    @Override // com.xunlei.downloadprovider.download.taskdetails.items.views.ZHTextViewExpandable.InterfaceC5256a
//    /* renamed from: b */
//    public final void mo9167b() {
////        this.f19772A.setListener(null);
//    }

    public void setRefreshListener(TaskDetailAdapter.InterfaceC5203a interfaceC5203a) {
        this.f19802ah = interfaceC5203a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
    public static /* synthetic */ void m9293e(DownloadTaskNameAndIconView downloadTaskNameAndIconView) {
//        Activity m11714c = AppStatusChgObserver.m11715b().m11714c();
//        if (m11714c != null) {
//            m11714c.runOnUiThread(new RunnableC5271m(downloadTaskNameAndIconView));
//        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: f */
    public static /* synthetic */ void m9292f(DownloadTaskNameAndIconView downloadTaskNameAndIconView) {
//        Activity m11714c = AppStatusChgObserver.m11715b().m11714c();
//        if (m11714c != null) {
//            m11714c.runOnUiThread(new RunnableC5270l(downloadTaskNameAndIconView));
//        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: l */
    public static /* synthetic */ void m9286l(DownloadTaskNameAndIconView downloadTaskNameAndIconView) {
        Intent xLIntent = new Intent("com.xunlei.downloadprovider.web.website.fragment.CollectionAndHistoryFragment");
        if (downloadTaskNameAndIconView.f19782K != null) {
            LocalBroadcastManager.getInstance(downloadTaskNameAndIconView.f19782K).sendBroadcast(xLIntent);
        }
    }
}
