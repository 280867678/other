package com.carrydream.cardrecorder.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* loaded from: classes.dex */
public class DownloadUtil {
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    /* loaded from: classes.dex */
    public interface OnDownloadListener {
        void onDownloadFailed(Exception exc);

        void onDownloadSuccess(File file);

        void onDownloading(int i);
    }

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
    }

    public void download(String str, final String str2, final String str3, final OnDownloadListener onDownloadListener) {
        this.okHttpClient.newCall(new Request.Builder().url(str).build()).enqueue(new Callback() { // from class: com.carrydream.cardrecorder.tool.DownloadUtil.1
            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException iOException) {
                onDownloadListener.onDownloadFailed(iOException);
            }

            /* JADX WARN: Removed duplicated region for block: B:54:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:60:0x0087 A[EXC_TOP_SPLITTER, SYNTHETIC] */
            @Override // okhttp3.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onResponse(Call call, Response response) throws IOException {
                FileOutputStream fileOutputStream;
                byte[] bArr = new byte[2048];
                File file = new File(str2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(file, str3);
                InputStream inputStream = null;
                try {
                    InputStream byteStream = response.body().byteStream();
                    try {
                        long contentLength = response.body().contentLength();
                        fileOutputStream = new FileOutputStream(file2);
                        long j = 0;
                        while (true) {
                            try {
                                int read = byteStream.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                                j += read;
                                onDownloadListener.onDownloading((int) (((((float) j) * 1.0f) / ((float) contentLength)) * 100.0f));
                            } catch (Exception e) {
                                e = e;
                                inputStream = byteStream;
                                try {
                                    onDownloadListener.onDownloadFailed(e);
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException unused) {
                                        }
                                    }
                                    if (fileOutputStream == null) {
                                        return;
                                    }
                                    fileOutputStream.close();
                                } catch (Throwable th) {
                                    th = th;
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException unused2) {
                                        }
                                    }
                                    if (fileOutputStream != null) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException unused3) {
                                        }
                                    }
                                    throw th;
                                }
                            } catch (Throwable th2) {
//                                th = th2;
//                                inputStream = byteStream;
//                                if (inputStream != null) {
//                                }
//                                if (fileOutputStream != null) {
//                                }
//                                throw th;
                            }
                        }
                        fileOutputStream.flush();
                        onDownloadListener.onDownloadSuccess(file2);
                        if (byteStream != null) {
                            try {
                                byteStream.close();
                            } catch (IOException unused4) {
                            }
                        }
                    } catch (Exception e2) {
//                        e = e2;
                        fileOutputStream = null;
                    } catch (Throwable th3) {
//                        th = th3;
                        fileOutputStream = null;
                    }
                } catch (Exception e3) {
//                    e = e3;
                    fileOutputStream = null;
                } catch (Throwable th4) {
//                    th = th4;
                    fileOutputStream = null;
                }
                try {
                    fileOutputStream.close();
                } catch (IOException unused5) {
                }
            }
        });
    }
}
