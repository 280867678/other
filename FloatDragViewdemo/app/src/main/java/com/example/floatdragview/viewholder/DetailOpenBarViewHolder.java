package com.example.floatdragview.viewholder;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.floatdragview.R;
import com.example.floatdragview.adapter.TaskDetailViewHolder;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.util.HandlerUtil;
import com.example.floatdragview.datasource.TaskDetailItem;

public final class DetailOpenBarViewHolder extends TaskDetailViewHolder {

    /* renamed from: a */
    public View f19705a;

    /* renamed from: b */
    HandlerUtil.MessageListener f19706b;

    /* renamed from: c */
    HandlerUtil.StaticHandler f19707c;

    /* renamed from: i */
    private TextView f19708i;

    /* renamed from: j */
    private TextView f19709j;

    /* renamed from: k */
    private ImageView f19710k;

    /* renamed from: l */
    private View f19711l;

    /* renamed from: m */
    private TextView f19712m;

    /* renamed from: n */
    private DownloadTaskInfo f19713n;

    /* renamed from: o */
    private View f19714o;

    /* renamed from: p */
    private LottieAnimationView f19715p;

    /* renamed from: q */
    private TextView f19716q;

    /* renamed from: r */
    private boolean f19717r;

    /* renamed from: a */
    public static View m9338a(Context context) {
        return View.inflate(context, R.layout.layout_task_detail_play_downing, null);
    }

    public DetailOpenBarViewHolder(View view) {
        super(view);
        this.f19706b = new HandlerUtil.MessageListener() {
            @Override
            public void handleMessage(Message message) {

            }
        };
        this.f19707c = new HandlerUtil.StaticHandler(this.f19706b);
        this.f19705a = view.findViewById(R.id.play_downloading_container);
        this.f19708i = (TextView) view.findViewById(R.id.play_downloading_tip);
        this.f19709j = (TextView) view.findViewById(R.id.text);
        this.f19710k = (ImageView) view.findViewById(R.id.icon);
        this.f19711l = view.findViewById(R.id.play_downloading_btn);
        this.f19712m = (TextView) view.findViewById(R.id.re_download);
        this.f19714o = view.findViewById(R.id.playing);
        this.f19715p = (LottieAnimationView) view.findViewById(R.id.lottie_icon_playing);
        this.f19716q = (TextView) view.findViewById(R.id.text_playing);
//        this.f19705a.setOnClickListener(new View$OnClickListenerC5246j(this));
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9109a(TaskDetailItem taskDetailItem) {
        m9360b(taskDetailItem);
        this.f19713n = taskDetailItem.f19671d;
        DownloadTaskInfo downloadTaskInfo = this.f19713n;
        this.f19712m.setVisibility(8);
        this.f19711l.setVisibility(0);
//        if (TaskHelper.m8449d((TaskInfo) downloadTaskInfo)) {
//            this.f19707c.postDelayed(new RunnableC5248l(this, taskDetailItem), 500L);
//        } else {
//            if (this.f19676h != null ? this.f19676h.m9956E() : false) {
//                this.f19711l.setVisibility(8);
//                this.f19714o.setVisibility(0);
//                this.f19715p.setAnimation("lottie/downloadlist/task_playing.json");
//                this.f19715p.m25387a(true);
//                this.f19715p.m25391a();
//            } else {
                this.f19711l.setVisibility(0);
                this.f19709j.setText(this.itemView.getContext().getString(R.string.task_detail_new_bxbb));
//                this.f19715p.m25385c();
                this.f19714o.setVisibility(8);
//            }
            this.f19708i.setText(this.itemView.getContext().getString(R.string.task_detail_new_bxbb_tip));
            this.f19710k.setImageResource(R.drawable.download_detail_play);
//        }
        if (this.f19717r) {
            return;
        }
        this.f19717r = true;
//        if (DownloadCenterConfig.m10878e() && TaskHelper.m8447e(this.f19672d)) {
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f19705a.getLayoutParams();
//            layoutParams.setMargins(0, 0, 0, DipPixelUtil.dip2px(5.0f));
//            this.f19705a.setLayoutParams(layoutParams);
//        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static /* synthetic */ void m9336a(DetailOpenBarViewHolder detailOpenBarViewHolder, TaskDetailItem taskDetailItem) {
        if (detailOpenBarViewHolder.f19674f != null) {
            detailOpenBarViewHolder.f19674f.m9449a(taskDetailItem);
        }
    }
}

