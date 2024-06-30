package com.example.floatdragview.bin;

import java.io.Serializable;

public class DownloadTaskInfo implements Serializable{
    private static final long serialVersionUID = 1;
    public boolean hasRequestRedList;
    public boolean mHasCloseRedEnvelopeBanner;
    public boolean mShowSpeedupButton;
    public String mEpisodeTagText = null;
    public String mSearchName = null;
    public boolean mIsFileMissing = false;
    public long mIsFileMissingLMT = 0;
    public String mDownloadingPlayUrl = null;
    public long mDownloadingPlayUrlLMT = 0;
    public long mSnapshotCheckLMT = 0;
    public EFileCategoryType mFileCategoryType = null;
    public int mAppInstalledType = 0;
    public long mAppInstalledTypeLastModifyTime = 0;
    public int mVideoPlayedTime = 0;
    public int mVideoDuration = 0;
    public long mVideoDurationLMT = 0;
    public boolean mIsDisplayDownloadException = false;
    public long mRemainderSeconds = 20;
    public int mIsSensitiveResource = 0;




    public enum EFileCategoryType {
        E_OTHER_CATEGORY,
        E_VIDEO_CATEGORY,
        E_MUSIC_CATEGORY,
        E_BOOK_CATEGORY,
        E_SOFTWARE_CATEGORY,
        E_PICTURE_CATEGORY,
        E_ZIP_CATEGORY,
        E_TORRENT_CATEGORY,
        E_XLFILE_UPPER,
        E_XLDIR_CATEGORY
    }
}
