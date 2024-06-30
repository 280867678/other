package com.example.floatdragview.util;

import android.app.Activity;
import android.app.TaskInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.example.floatdragview.bin.DownloadTaskInfo;

import java.io.File;

public class DownloadCenterControl {



    public static void m10710a(Context context, DownloadTaskInfo taskInfo, String str) {
//        DownloadCenterPageUtil.m10915a();
//        boolean m10878e = DownloadCenterConfig.m10878e();
//        if (DownloadCenterConfig.m10879d() || m10878e) {
//            DownloadCenterPageUtil.m10909b(context, taskInfo.getTaskId(), str);
//            return;
//        }

        DownloadTaskInfo downloadTaskInfo = new DownloadTaskInfo();
//        downloadTaskInfo.mSearchName
        Intent xLIntent = new Intent("com.xunLei.downloadCenter.MoreOperate");
        xLIntent.putExtra("taskInfo", downloadTaskInfo);
        xLIntent.putExtra("from", str);
        context.sendBroadcast(xLIntent);
    }





    public final void m10702a(TaskInfo taskInfo, String str, String str2) {
//        BTSubTaskInfo bTSubTaskInfo;
//        if (taskInfo == null) {
//            return;
//        }
//        String str3 = taskInfo.mLocalFileName;
//        String str4 = taskInfo.mTitle;
//        if (TextUtils.isEmpty(str3)) {
//            return;
//        }
//        File file = new File(str3);
//        if (taskInfo.mTaskType != DownloadManager.TaskType.BT) {
//            if (!file.exists()) {
//                m10703a(taskInfo, str);
//            } else if (taskInfo.mFileSize == 0) {
//                XLToast.showToast(this.f18209a.getApplicationContext(), this.f18209a.getString(R.string.download_list_invalid_file));
//            } else {
//                taskInfo.setConsumed(true);
//                taskInfo.mRevision++;
//                TaskExtraInfoController.m10427a().m10421c(taskInfo.getTaskId());
//                TaskInfo taskInfo2 = null;
//                if (!C5604e.m8313e(str4) && !C5604e.m8313e(str3)) {
//                    if (!C5604e.m8312f(str4) && taskInfo.mTaskType != DownloadManager.TaskType.MAGNET) {
//                        long taskId = taskInfo.getTaskId();
//                        if (XLFileTypeUtil.getFileCategoryTypeByName(str3) == XLFileTypeUtil.EFileCategoryType.E_VIDEO_CATEGORY && taskInfo != null) {
//                            DLCenterReporter.m9608a("dl_finish_open_video", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                        } else if (taskInfo != null) {
//                            DLCenterReporter.m9608a("dl_finish_other", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                        }
//                        Activity activity = this.f18209a;
//                        if (XLFileTypeUtil.isLocalVodSupport(str3)) {
//                            DownloadTaskManager.m10252a();
//                            FindTaskResult m10194i = DownloadTaskManager.m10194i(str3);
//                            if (m10194i != null && m10194i.mo10266c() != null) {
//                                taskInfo2 = m10194i.mo10266c();
//                                bTSubTaskInfo = m10194i.mo10265d();
//                            } else if (taskId >= 0) {
//                                DownloadTaskManager.m10252a();
//                                taskInfo2 = DownloadTaskManager.m10204f(taskId);
//                                bTSubTaskInfo = null;
//                            } else {
//                                bTSubTaskInfo = null;
//                            }
//                            if (taskInfo2 != null) {
//                                VodPlayerActivityNew.m2219a(activity, taskInfo2, bTSubTaskInfo, str2);
//                                return;
//                            } else {
//                                VodPlayerActivityNew.m2220a(activity, new TaskPlayInfo(str3), str2, null, true, 0, null);
//                                return;
//                            }
//                        }
//                        LocalFileOpenHelper.m10097a(activity, str3, false);
//                        return;
//                    }
//                    if (taskInfo != null) {
//                        DLCenterReporter.m9608a("dl_finish_other", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                    }
//                    DownloadTaskManager.m10252a();
//                    DownloadBtFileExplorerActivity.m10624a(this.f18209a, Uri.fromFile(new File(str3)).toString(), DownloadTaskManager.m10210d("file://" + str3), 9, "", taskInfo.mRefUrl, taskInfo.mWebsiteName);
//                    return;
//                }
//                ApkHelper.ApkInfo apkInfo = ApkHelper.getApkInfo(this.f18209a.getBaseContext(), str3);
//                if (ApkHelper.compareLocalApp(this.f18209a.getBaseContext(), apkInfo) != 4) {
//                    if (str3 != null) {
//                        if (taskInfo != null) {
//                            DLCenterReporter.m9608a("dl_finish_install", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                        }
//                        LocalFileOpenHelper.m10097a(this.f18209a, str3, false);
//                        return;
//                    }
//                    return;
//                }
//                String packageName = apkInfo.getPackageName();
//                if (packageName != null && packageName.equals(this.f18209a.getPackageName())) {
//                    MainTabActivity.m8379b(this.f18209a, "thunder", null);
//                    return;
//                }
//                if (taskInfo != null) {
//                    DLCenterReporter.m9608a("dl_finish_open_app", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                }
//                ApkHelper.launchAppByPackageName(this.f18209a.getBaseContext(), packageName);
//            }
//        } else if (!file.exists()) {
//            XLToast.showToast(this.f18209a.getApplicationContext(), this.f18209a.getString(R.string.task_detail_file_noexist));
//        } else if (file.listFiles() != null && file.listFiles().length == 1) {
//            XLFileTypeUtil.EFileCategoryType fileCategoryTypeByName = XLFileTypeUtil.getFileCategoryTypeByName(file.listFiles()[0].getAbsolutePath());
//            if (fileCategoryTypeByName != null && (fileCategoryTypeByName == XLFileTypeUtil.EFileCategoryType.E_MUSIC_CATEGORY || fileCategoryTypeByName == XLFileTypeUtil.EFileCategoryType.E_VIDEO_CATEGORY)) {
//                String absolutePath = file.listFiles()[0].getAbsolutePath();
//                DLCenterReporter.m9608a("dl_finish_open_video", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//                LocalFileOpenHelper.m10097a(this.f18209a, absolutePath, false);
//                return;
//            }
//            if (taskInfo != null) {
//                DLCenterReporter.m9608a("dl_finish_other", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//            }
//            m10710a(this.f18209a, taskInfo, "dlcenter_total_bar");
//        } else {
//            if (taskInfo != null) {
//                DLCenterReporter.m9608a("dl_finish_other", "finish", str, DLCenterReporter.m9596b(taskInfo), taskInfo.mCreateOrigin);
//            }
//            m10710a(this.f18209a, taskInfo, "dlcenter_total_bar");
//        }
    }





}
