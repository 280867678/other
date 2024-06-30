package com.example.floatdragview.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.floatdragview.R;
import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.datasource.TaskDetailItem;
import com.example.floatdragview.adapter.TaskDetailViewHolder;
import com.example.floatdragview.util.DipPixelUtil;
import com.example.floatdragview.util.TaskImageLoader;
import com.example.floatdragview.widget.DownloadTaskNameAndIconView;

public final class DetailTaskBasicInfoViewHolder extends TaskDetailViewHolder {

    /* renamed from: a */
    public final DownloadTaskNameAndIconView f19735a;

    /* renamed from: a */
    public static View m9327a(Context context) {
        return new DownloadTaskNameAndIconView(context);
    }

    public DetailTaskBasicInfoViewHolder(View view) {
        super(view);
        this.f19735a = (DownloadTaskNameAndIconView) view;
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9111a() {
        if (this.f19735a != null) {
            DownloadTaskNameAndIconView downloadTaskNameAndIconView = this.f19735a;
            if (downloadTaskNameAndIconView.f19784M == null || downloadTaskNameAndIconView.f19780I == null) {
                return;
            }
//            downloadTaskNameAndIconView.f19786O = WebsiteHelper.m1721a().m1714a(downloadTaskNameAndIconView.f19781J);
//            if (downloadTaskNameAndIconView.f19786O) {
//                downloadTaskNameAndIconView.f19780I.setSelected(true);
//            } else {
//                downloadTaskNameAndIconView.f19780I.setSelected(false);
//            }
        }
    }

    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder
    /* renamed from: a */
    public final void mo9108a(boolean z) {
        if (this.f19735a != null) {
            this.f19735a.setSelected(z);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:130:0x0400  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0449  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0457  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x046f  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0491  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0494  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x04f5  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x04f9  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x050c  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x052c  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x0560  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0567  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x057f  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x05b8  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x05c7  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x05cf  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x05e6  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x05ed  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00c8  */
    @SuppressLint("WrongConstant")
    @Override // com.xunlei.downloadprovider.download.taskdetails.items.p416a.TaskDetailViewHolder

    public final void mo9109a(TaskDetailItem taskDetailItem) {
//        String str;
//        long j;
//        String str2;
//        DownloadTaskInfo downloadTaskInfo;
//        String str3;
//        DownloadTaskInfo downloadTaskInfo2;
//        DownloadTaskInfo downloadTaskInfo3;
//        String string;
//        int i;
//        DownloadTaskInfo downloadTaskInfo4;
//        String taskDownloadUrl;
//        long j2;
//        long j3;
//        String string2;
//        String m8502a;
//        boolean z;
//        m9360b(taskDetailItem);
//        DownloadTaskInfo currentTask = this.f19735a.getCurrentTask();
        DownloadTaskInfo downloadTaskInfo5 = taskDetailItem.f19671d;
//        if (downloadTaskInfo5 != currentTask) {
//            this.f19735a.m9302a(downloadTaskInfo5);
//        } else if (this.f19674f != null && this.f19674f.f19566j) {
//            this.f19735a.m9302a(downloadTaskInfo5);
//            this.f19674f.f19566j = false;
//        }
        DownloadTaskNameAndIconView downloadTaskNameAndIconView = this.f19735a;
//        boolean z2 = true;
//        int i2 = 8;
//        if (downloadTaskInfo5 != null && downloadTaskInfo5 != downloadTaskNameAndIconView.f19784M) {
//            if (downloadTaskInfo5 != null && !TextUtils.isEmpty(downloadTaskInfo5.mWebsiteName)) {
//                if (!TextUtils.isEmpty(downloadTaskInfo5.mRefUrl)) {
//                    downloadTaskNameAndIconView.f19781J = downloadTaskInfo5.mRefUrl;
//                } else if (downloadTaskInfo5.mExtraInfo != null && !TextUtils.isEmpty(downloadTaskInfo5.mExtraInfo.mRefUrl)) {
//                    downloadTaskNameAndIconView.f19781J = downloadTaskInfo5.mExtraInfo.mRefUrl;
//                }
//                if (!TextUtils.isEmpty(downloadTaskNameAndIconView.f19781J)) {
//                    z = true;
//                    if (!z && downloadTaskNameAndIconView.f19825x != null) {
                        downloadTaskNameAndIconView.f19825x.setVisibility(0);
//                        if (downloadTaskNameAndIconView.f19825x.getVisibility() == 0) {
//                            if (TextUtils.isEmpty(downloadTaskInfo5.mWebsiteName)) {
//                                downloadTaskNameAndIconView.f19779H.setVisibility(8);
//                            } else {
                                downloadTaskNameAndIconView.f19779H.setVisibility(View.VISIBLE);
//                                downloadTaskNameAndIconView.f19779H.setText(downloadTaskInfo5.mWebsiteName);
//                            }
//                            if (!TextUtils.isEmpty(downloadTaskNameAndIconView.f19781J)) {
                                downloadTaskNameAndIconView.f19778G.setText(downloadTaskNameAndIconView.f19781J);
//                            }
//                            downloadTaskNameAndIconView.f19786O = WebsiteHelper.m1721a().m1714a(downloadTaskNameAndIconView.f19781J);
//                            if (downloadTaskNameAndIconView.f19786O) {
//                                downloadTaskNameAndIconView.f19780I.setSelected(true);
//                            } else {
//                                downloadTaskNameAndIconView.f19780I.setSelected(false);
//                            }
//                        }
//                    } else if (downloadTaskNameAndIconView.f19825x != null) {
//                        downloadTaskNameAndIconView.f19825x.setVisibility(8);
//                    }
//                }
//            }
//            z = false;
//        }
//        this.f19735a.setControl(this.f19675g);
        this.f19735a.setAdapter(this.f19674f);
//        this.f19735a.setTaskSpeedCountInfo((TaskSpeedCountInfo) taskDetailItem.m9364a(TaskSpeedCountInfo.class));
        DownloadTaskNameAndIconView downloadTaskNameAndIconView2 = this.f19735a;
//        if (downloadTaskInfo5 != null) {
//            Context context = downloadTaskNameAndIconView2.getContext();
            TextView zHTextView = downloadTaskNameAndIconView2.f19803b;
            View view = downloadTaskNameAndIconView2.f19809h;
//            String m8498a = DownloadCenterTaskUtil.m8498a(downloadTaskInfo5, context);
//            boolean m8454b = TaskHelper.m8454b(downloadTaskInfo5);
//            if (m8454b) {
                view.setVisibility(0);
//            } else {
//                view.setVisibility(8);
//            }

//            if (m8454b) {
//                zHTextView.setTextIndentPadding(Math.max(view.getWidth(), DipPixelUtil.dip2px(14.0f)));
//            } else {
//                zHTextView.setTextIndentPadding(0.0f);
//            }
//            zHTextView.setText(m8498a);
//            zHTextView.requestLayout();
//            zHTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver$OnGlobalLayoutListenerC5273o(downloadTaskNameAndIconView2, zHTextView));
            ImageView imageView = downloadTaskNameAndIconView2.f19794a;
//            if (downloadTaskNameAndIconView2.f19787P == 0) {
//                if (downloadTaskNameAndIconView2.f19783L != null) {
//                    downloadTaskNameAndIconView2.f19783L.mo8751a(8);
//                }
                TaskImageLoader.m8492a();
                TaskImageLoader.m8484b(downloadTaskInfo5, imageView);
//            } else {
//                if (downloadTaskNameAndIconView2.f19783L != null) {
//                    downloadTaskNameAndIconView2.f19783L.mo8751a(8);
//                }
//                if (downloadTaskNameAndIconView2.f19784M == null || downloadTaskInfo5 != downloadTaskNameAndIconView2.f19784M) {
//                    TaskImageLoader.m8492a();
//                    TaskImageLoader.m8489a(downloadTaskInfo5, imageView);
//                }
                TaskImageLoader.m8492a();
//                TaskImageLoader.m8487a(downloadTaskInfo5, imageView, downloadTaskNameAndIconView2.f19783L, DownloadCenterConfig.m10877f(), downloadTaskNameAndIconView2.f19788Q);
//            }
            DownloadTaskNameAndIconView.m9301a(downloadTaskInfo5, downloadTaskNameAndIconView2.f19805d, downloadTaskNameAndIconView2.f19808g);
//        }
        DownloadTaskNameAndIconView downloadTaskNameAndIconView3 = this.f19735a;
//        if (downloadTaskInfo5 != null) {
//            downloadTaskNameAndIconView3.f19784M = downloadTaskInfo5;
//            DownloadTaskInfo downloadTaskInfo6 = downloadTaskNameAndIconView3.f19784M;
//            downloadTaskNameAndIconView3.f19826y.setVisibility(8);
//            downloadTaskNameAndIconView3.f19810i.setVisibility(8);
//            if (downloadTaskInfo6 != null) {
                downloadTaskNameAndIconView3.f19811j.setTextColor(downloadTaskNameAndIconView3.getContext().getResources().getColor(R.color.download_list_detail_color_t1));
//                if (downloadTaskInfo6.getTaskStatus() != 8) {
//                    if (4 != downloadTaskInfo6.getTaskStatus()) {
//                        if (2 != downloadTaskInfo6.getTaskStatus()) {
//                            if (downloadTaskInfo6.getTaskStatus() != 16) {
//                                if (downloadTaskInfo6.getTaskStatus() != 1) {
//                                    i2 = 8;
//                                    downloadTaskNameAndIconView3.f19826y.setVisibility(8);
//                                    downloadTaskNameAndIconView3.f19810i.setVisibility(8);
//                                } else {
                                    downloadTaskNameAndIconView3.f19826y.setVisibility(0);
//                                    i2 = 8;
//                                    downloadTaskNameAndIconView3.f19812k.setVisibility(8);
//                                    downloadTaskNameAndIconView3.f19811j.setText(downloadTaskNameAndIconView3.getContext().getResources().getString(R.string.download_item_task_status_waiting));
//                                }
//                            } else {
                                downloadTaskNameAndIconView3.f19826y.setVisibility(0);
                                downloadTaskNameAndIconView3.f19812k.setVisibility(0);
                                downloadTaskNameAndIconView3.f19812k.setImageDrawable(downloadTaskNameAndIconView3.getContext().getResources().getDrawable(R.drawable.ic_detail_download_error_style2));
                                downloadTaskNameAndIconView3.f19811j.setText("f19811j");
//                                i2 = 8;
//                            }
//                        } else {
                            downloadTaskNameAndIconView3.f19810i.setVisibility(0);
//                            if (downloadTaskInfo6.mDownloadSpeed < 1) {
                                downloadTaskNameAndIconView3.f19810i.setText(R.string.download_item_task_status_linking);
//                            } else {
//                                if (downloadTaskInfo6.mHasVipChannelSpeedup || downloadTaskInfo6.mVipAcceleratedSpeed > 100) {
//                                    String m8502a2 = DownloadCenterTaskUtil.m8502a(downloadTaskInfo6.mDownloadSpeed);
//                                    DownloadError.SpeedupFailureCode m8504b = DownloadError.m8504b(downloadTaskInfo6);
//                                    if (m8504b != null && downloadTaskInfo6.mVipAcceleratedSpeed < 100) {
//                                        if (m8504b == DownloadError.SpeedupFailureCode.SENSITIVE_RESOURCE_LIMITED) {
//                                            string2 = downloadTaskNameAndIconView3.getResources().getString(R.string.download_item_task_speedup_fail_sensitive_resource_limited);
//                                        } else {
//                                            string2 = downloadTaskNameAndIconView3.getResources().getString(R.string.download_item_task_speedup_fail);
//                                        }
//                                        String str4 = MessageStore.f10590s + string2 + MessageStore.f10591t;
//                                        String str5 = m8502a2 + str4;
//                                        SpannableString spannableString = new SpannableString(str5);
//                                        spannableString.setSpan(new ForegroundColorSpan(downloadTaskNameAndIconView3.getResources().getColor(R.color.DownloadTaskItemSpeedupErrorStatusTextColor)), str5.length() - str4.length(), str5.length(), 34);
//                                        spannableString.setSpan(new AbsoluteSizeSpan(DipPixelUtil.dip2px(12.0f)), str5.length() - str4.length(), str5.length(), 34);
//                                        downloadTaskNameAndIconView3.f19810i.setText(spannableString);
//                                    } else {
//                                        String format = String.format("(+ %s)", DownloadCenterTaskUtil.m8502a(downloadTaskInfo6.mVipAcceleratedSpeed));
//                                        String str6 = m8502a2 + format;
//                                        SpannableString spannableString2 = new SpannableString(str6);
//                                        spannableString2.setSpan(new ForegroundColorSpan(downloadTaskNameAndIconView3.getContext().getResources().getColor(R.color.common_blue_button_normal)), str6.length() - format.length(), str6.length(), 34);
//                                        spannableString2.setSpan(new AbsoluteSizeSpan(DipPixelUtil.dip2px(12.0f)), str6.length() - format.length(), str6.length(), 34);
                                        downloadTaskNameAndIconView3.f19810i.setText("spannableString2");
//                                    }
//                                } else {
//                                    if (downloadTaskInfo6.mDownloadSpeed < 1) {
//                                        m8502a = downloadTaskNameAndIconView3.getContext().getString(R.string.download_item_task_status_linking);
//                                    } else {
//                                        m8502a = DownloadCenterTaskUtil.m8502a(downloadTaskInfo6.mDownloadSpeed);
//                                    }
//                                    downloadTaskNameAndIconView3.f19810i.setText(m8502a);
//                                }
//                                i2 = 8;
//                            }
//                        }
//                    } else {
//                        downloadTaskNameAndIconView3.f19826y.setVisibility(0);
//                        downloadTaskNameAndIconView3.f19811j.setText(downloadTaskNameAndIconView3.getContext().getResources().getString(R.string.download_item_task_status_paused));
//                        downloadTaskNameAndIconView3.f19812k.setVisibility(8);
//                    }
//                } else {
//                    downloadTaskNameAndIconView3.f19810i.setVisibility(8);
//                    downloadTaskNameAndIconView3.f19826y.setVisibility(0);
//                    if (downloadTaskInfo6.mIsFileMissing) {
//                        downloadTaskNameAndIconView3.f19811j.setTextColor(downloadTaskNameAndIconView3.getContext().getResources().getColor(R.color.DownloadTaskItemHintStatusTextColor));
//                        downloadTaskNameAndIconView3.f19812k.setVisibility(8);
//                        downloadTaskNameAndIconView3.f19811j.setText(R.string.download_item_task_file_not_exist);
//                    } else {
//                        downloadTaskNameAndIconView3.f19812k.setVisibility(0);
//                        downloadTaskNameAndIconView3.f19812k.setImageDrawable(downloadTaskNameAndIconView3.getContext().getResources().getDrawable(R.drawable.ic_detail_download_finish));
//                        downloadTaskNameAndIconView3.f19811j.setText(R.string.download_item_task_download_finish);
//                    }
//                }
//            }
//            DownloadTaskInfo downloadTaskInfo7 = downloadTaskNameAndIconView3.f19784M;
//            if (downloadTaskInfo7.getTaskStatus() == i2) {
//                if (downloadTaskInfo7.mDownloadedSize != 0) {
//                    try {
//                        j2 = (((((float) (downloadTaskInfo7.mDcdnReceivedSize + downloadTaskInfo7.mVipChannelReceivedSize)) * 1.0f) / ((float) downloadTaskInfo7.mDownloadedSize)) * ((float) downloadTaskInfo7.mDownloadDurationTime)) / 1000.0f;
//                        j3 = 0;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (j2 > j3) {
//                        str = DateTimeUtil.getTimeString((int) j2, downloadTaskNameAndIconView3.getContext());
//                        if (str.contains("-")) {
//                            str = "--";
//                        }
//                        downloadTaskNameAndIconView3.f19818q.setText(str);
//                        downloadTaskNameAndIconView3.f19814m.setText(DateTimeUtil.formatTimeWithDefault(downloadTaskNameAndIconView3.f19784M.mCreateTime));
//                        DownloadTaskInfo downloadTaskInfo8 = downloadTaskNameAndIconView3.f19784M;
//                        j = (downloadTaskInfo8.mDownloadedSize > 0 || downloadTaskInfo8.mDownloadDurationTime <= 0) ? 0L : ((((float) downloadTaskInfo8.mDownloadedSize) * 1.0f) / ((float) downloadTaskInfo8.mDownloadDurationTime)) * 1000.0f;
//                        if (downloadTaskNameAndIconView3.f19785N != null) {
//                            downloadTaskNameAndIconView3.f19785N = new TaskSpeedCountInfo();
//                            downloadTaskNameAndIconView3.f19785N.mHighestSpeed = downloadTaskInfo8.mDownloadSpeed;
//                        } else if (downloadTaskInfo8.mDownloadSpeed > downloadTaskNameAndIconView3.f19785N.mHighestSpeed) {
//                            downloadTaskNameAndIconView3.f19785N.mHighestSpeed = downloadTaskInfo8.mDownloadSpeed;
//                        }
//                        if (downloadTaskNameAndIconView3.f19785N.mHighestSpeed < j) {
//                            downloadTaskNameAndIconView3.f19785N.mHighestSpeed = j + (j / 3);
//                        }
//                        new StringBuilder(" High speed: ").append(downloadTaskNameAndIconView3.f19785N.mHighestSpeed);
//                        if (downloadTaskNameAndIconView3.f19785N.mHighestSpeed != 0) {
//                            str2 = "--";
//                        } else {
//                            str2 = ConvertUtil.byteConvert(downloadTaskNameAndIconView3.f19785N.mHighestSpeed) + "/s";
//                        }
//                        downloadTaskNameAndIconView3.f19815n.setText(str2);
//                        downloadTaskInfo = downloadTaskNameAndIconView3.f19784M;
//                        if (downloadTaskInfo.mDownloadedSize > 0 || downloadTaskInfo.mDownloadDurationTime <= 0) {
//                            z2 = false;
//                            str3 = "";
//                        } else {
//                            long j4 = ((((float) downloadTaskInfo.mDownloadedSize) * 1.0f) / ((float) downloadTaskInfo.mDownloadDurationTime)) * 1000.0f;
//                            str3 = ConvertUtil.byteConvert(j4) + "/s";
//                            if (j4 < DownloadManager.MAX_BYTES_OVER_MOBILE) {
//                                z2 = false;
//                            }
//                        }
//                        if (TextUtils.isEmpty(str3)) {
//                            str3 = "--";
//                        }
//                        if (!z2) {
//                            downloadTaskNameAndIconView3.f19816o.setTextColor(downloadTaskNameAndIconView3.f19782K.getResources().getColor(R.color.download_list_detail_color_t4));
//                        } else {
//                            downloadTaskNameAndIconView3.f19816o.setTextColor(downloadTaskNameAndIconView3.f19782K.getResources().getColor(R.color.download_list_detail_color_t3));
//                        }
//                        downloadTaskNameAndIconView3.f19816o.setText(str3);
//                        if (2 == downloadTaskNameAndIconView3.f19784M.getTaskStatus()) {
//                            downloadTaskNameAndIconView3.f19819r.setVisibility(0);
//                            downloadTaskNameAndIconView3.f19817p.setText(String.valueOf(downloadTaskInfo2.mResLinkUsed) + "/" + String.valueOf(downloadTaskInfo2.mResLinkTotal));
//                        }
//                        downloadTaskInfo3 = downloadTaskNameAndIconView3.f19784M;
//                        if (downloadTaskInfo3.mFileSize <= 0) {
//                            string = DownloadCenterTaskUtil.m8494c(downloadTaskInfo3.mFileSize);
//                        } else {
//                            string = downloadTaskNameAndIconView3.getContext().getString(R.string.download_item_task_unknown_filesize);
//                        }
//                        downloadTaskNameAndIconView3.f19820s.setText(string);
//                        if (8 == downloadTaskInfo3.getTaskStatus()) {
//                            downloadTaskNameAndIconView3.f19822u.setVisibility(0);
//                            if (downloadTaskInfo3.mFileSize == 0) {
//                                downloadTaskNameAndIconView3.f19821t.setText("--");
//                            } else {
//                                int i3 = (int) ((((float) downloadTaskInfo3.mDownloadedSize) / ((float) downloadTaskInfo3.mFileSize)) * 100.0f);
//                                downloadTaskNameAndIconView3.f19821t.setText(i3 + "%");
//                            }
//                            i = 8;
//                        } else {
//                            i = 8;
//                            downloadTaskNameAndIconView3.f19822u.setVisibility(8);
//                        }
//                        downloadTaskInfo4 = downloadTaskNameAndIconView3.f19784M;
//                        if (downloadTaskInfo4.getTaskStatus() == i) {
//                            downloadTaskNameAndIconView3.f19824w.setText("--");
//                        } else {
//                            downloadTaskNameAndIconView3.f19824w.setText(DateTimeUtil.formatTimeWithDefault(downloadTaskInfo4.mLastModifiedTime));
//                        }
//                        taskDownloadUrl = downloadTaskInfo5.getTaskDownloadUrl();
//                        if (!taskDownloadUrl.startsWith("thunder:")) {
//                            taskDownloadUrl = taskDownloadUrl.substring(8);
//                        } else if (taskDownloadUrl.startsWith("thunder")) {
//                            taskDownloadUrl = taskDownloadUrl.substring(7);
//                        }
//                        downloadTaskNameAndIconView3.f19772A.setText(taskDownloadUrl);
//                        downloadTaskNameAndIconView3.m9306a();
//                    }
//                }
//                j3 = 0;
//                j2 = 0;
//                if (j2 > j3) {
//                }
//            }
//            str = "--";
//            downloadTaskNameAndIconView3.f19818q.setText(str);
//            downloadTaskNameAndIconView3.f19814m.setText(DateTimeUtil.formatTimeWithDefault(downloadTaskNameAndIconView3.f19784M.mCreateTime));
//            DownloadTaskInfo downloadTaskInfo82 = downloadTaskNameAndIconView3.f19784M;
//
//            downloadTaskNameAndIconView3.f19815n.setText(str2);
//            downloadTaskInfo = downloadTaskNameAndIconView3.f19784M;
//
//            z2 = false;
//            str3 = "";
//
//            downloadTaskNameAndIconView3.f19816o.setText(str3);
//
//            downloadTaskInfo3 = downloadTaskNameAndIconView3.f19784M;
//
//            downloadTaskNameAndIconView3.f19820s.setText(string);
//
//            downloadTaskInfo4 = downloadTaskNameAndIconView3.f19784M;
//
//            taskDownloadUrl = downloadTaskInfo5.getTaskDownloadUrl();
//            downloadTaskNameAndIconView3.f19772A.setText(taskDownloadUrl);
//            downloadTaskNameAndIconView3.m9306a();
//        }
    }
}
