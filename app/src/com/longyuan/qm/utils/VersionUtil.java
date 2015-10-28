package com.longyuan.qm.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.longyuan.qm.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionUtil {

    private final int VERSION_DOWNLOAD_START = 0;

    private final int VERSION_DOWNLOAD = 1;

    private final int VERSION_DOWNLOAD_FINISH = 2;

    private final int VERSION_DOWNLOAD_ERROR = 3;

    private Context mContext;

    private HttpURLConnection conn;

    /**
     * 下载地址
     */
    private URL downUrl;
    /**
     * 下载文件保存地址
     */
    private File downFile;
    /**
     * 下载文件临时地址
     */
    private File infoFile;
    /**
     * 已下载完成大小
     */
    private long totalFinish;
    /**
     * 文件大小
     */
    private long fileSize;

    private Notification downLoadNotification;
    private NotificationManager downLoadNotificationManager;
    private PendingIntent downLoadPendingIntent;
    private RemoteViews contentView;
    /**
     * 最新版本名称
     */
    private String version;
    /**
     * APP 名称
     */
    private String title;
    /**
     * APP Logo
     */
    private int icon;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VERSION_DOWNLOAD_START:
                    downLoadNotificationManager = (NotificationManager) mContext
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    downLoadNotification = new Notification();
                    downLoadNotification.icon = R.drawable.ic_download;
                    downLoadNotification.tickerText = "正在下载应用";
                    downLoadNotification.flags = Notification.FLAG_AUTO_CANCEL;

                    contentView = new RemoteViews(mContext.getPackageName(),
                            R.layout.layout_notification_apk_download);
                    contentView.setTextViewText(R.id.tv_notification_title,
                            "Download: " + title + "v" + version);
                    contentView
                            .setTextViewText(R.id.tv_notification_percent, "0% ");
                    contentView.setImageViewResource(R.id.iv_notification_icon,
                            icon);
                    contentView.setProgressBar(R.id.pb_notification_progress, 100,
                            0, false);
                    downLoadNotification.contentView = contentView;
                    downLoadPendingIntent = PendingIntent.getActivity(
                            mContext.getApplicationContext(), 0, new Intent(), 0);
                    downLoadNotification.contentIntent = downLoadPendingIntent;
                    downLoadNotificationManager.notify(1, downLoadNotification);
                    ActivityUtil.getSharedPreferences(mContext).edit()
                            .putBoolean("isDown", true).commit();
                    break;
                case VERSION_DOWNLOAD:
                    contentView.setTextViewText(R.id.tv_notification_percent,
                            ((int) (totalFinish * 100 / fileSize)) + "% ");
                    contentView.setProgressBar(R.id.pb_notification_progress, 100,
                            (int) (totalFinish * 100 / fileSize), false);
                    downLoadNotificationManager.notify(1, downLoadNotification);
                    break;
                case VERSION_DOWNLOAD_FINISH:
                    contentView.setTextViewText(R.id.tv_notification_percent,
                            "100% ");
                    contentView.setProgressBar(R.id.pb_notification_progress, 100,
                            100, false);
                    downLoadNotificationManager.notify(1, downLoadNotification);
                    downLoadNotificationManager.cancel(1);
                    openFile(downFile);
                    break;
                case VERSION_DOWNLOAD_ERROR:
                    ToastUtils.showToastShort(mContext, "下载失败!");
                    downLoadNotificationManager.cancel(1);
                    break;
            }
        }
    };
    /**
     * 下载进度刷新大小
     */
    private int flag = 1024 * 200;
    ;

    /**
     * 调用setTitle和setIcon设置应用名和应用Logo
     *
     * @param context
     */
    public VersionUtil(Context context) {
        this.mContext = context;
    }

    /**
     * @param context
     * @param title   应用名
     * @param icon    应用Logo
     */
    public VersionUtil(Context context, String title, int icon) {
        this.mContext = context;
        this.title = title;
        this.icon = icon;
    }

    /**
     * 设置应用名
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置应用Logo
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * 下载安装文件
     */
    public void download(String address, String version) {
        this.version = version;
        boolean isDown = initDownFile(address);
        if (isDown) {
            new AsyncTask<String, Integer, Long>() {
                @Override
                protected Long doInBackground(String... params) {
                    fileSize = getFileSize(downUrl);
                    return fileSize;
                }

                @Override
                protected void onPostExecute(Long result) {
                    if (result != null && result > 0) {
                        startDownLoad();
                    } else {
                        ToastUtils.showToastShort(mContext, "安装包获取失败!");
                    }
                }
            }.execute();
        }
    }

    private boolean initDownFile(String address) {
        File dirPath = FileUtil.getFileCacheDir(mContext);
        downFile = new File(dirPath,
                address.substring(address.lastIndexOf("/") + 1));
        infoFile = new File(dirPath, downFile.getName() + ".info");
        try {
            downUrl = new URL(address);
        } catch (MalformedURLException e) {
            ToastUtils.showToastShort(mContext, "安装包地址不可用!");
            return false;
        }
        return true;
    }

    /**
     * 获取下载文件大小
     *
     * @return
     */
    private long getFileSize(URL url) {
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setConnectTimeout(8000);
            return conn.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 开始下载安装包
     */
    private void startDownLoad() {
        Message msg = Message.obtain();
        msg.what = VERSION_DOWNLOAD_START;
        Bundle data = new Bundle();
        data.putLong("totalLen", fileSize);
        msg.setData(data);
        mHandler.sendMessage(msg);
        new DownloadThread().start();
    }

    /**
     * 打开安装文件进行安装
     *
     * @param file
     */
    private void openFile(File file) {
        mContext.startActivity(ActivityUtil.getApkFileIntent(file
                .getAbsolutePath()));
    }

    /**
     * 下载线程类
     */
    private class DownloadThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream in = conn.getInputStream();
                FileOutputStream fileRaf = new FileOutputStream(downFile);
                byte[] buffer = new byte[1024 * 10];
                int len;
                int temp = 0;
                while ((len = in.read(buffer)) != -1) {
                    fileRaf.write(buffer, 0, len);
                    totalFinish += len;
                    temp += len;
                    if (temp > flag) {
                        temp = 0;
                        Message finishMsg = mHandler
                                .obtainMessage(VERSION_DOWNLOAD);
                        Bundle data = new Bundle();
                        data.putLong("totalFinish", totalFinish);
                        finishMsg.setData(data);
                        mHandler.sendMessage(finishMsg);
                    }
                    if (totalFinish == fileSize) {
                        mHandler.sendMessage(mHandler
                                .obtainMessage(VERSION_DOWNLOAD_FINISH));
                    }
                }
                in.close();
                fileRaf.close();
                if (totalFinish == fileSize)
                    infoFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(VERSION_DOWNLOAD_ERROR);
            }
        }
    }
}
