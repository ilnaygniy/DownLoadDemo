package downloaddemo.liyanying.example.com.download;

import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import downloaddemo.liyanying.example.com.download.listners.DownloadListner;
import downloaddemo.liyanying.example.com.download.module.FilePoint;
import downloaddemo.liyanying.example.com.download.thread.DownloadThread;

/**
 * 下载管理器
 */

public class DownloadManager {

    private String DEFAULT_FILE_DIR;//默认下载目录
    private DownloadThread downloadThread;//现在线程

    /**
     * 下载文件
     */
    public void startDownload() {
        downloadThread.start();
    }

    /**
     * 通过url获取下载文件的名称
     */
    public String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 暂停
     */
    public void pause() {
        downloadThread.pause();
    }

    /**
     * 取消下载
     */
    public void cancel() {
        downloadThread.cancel();
    }

    /**
     * 获取默认下载目录
     *
     * @return
     */

    private String getDefaultDirectory() {
        if (TextUtils.isEmpty(DEFAULT_FILE_DIR)) {
            DEFAULT_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "1" + File.separator;
        }
        return DEFAULT_FILE_DIR;
    }

    /**
     * 是否正在下载
     * @return boolean
     */

    public boolean isDownloading() {
        return downloadThread.isDownloading();
    }

    public static DownloadManager getInstance(String urlPath, String filePath, String fileName, DownloadListner l) {

        return new DownloadManager(urlPath,filePath,fileName,l);
    }
    /**
     * 初始化下载管理器
     */
    private DownloadManager(String urlPath, String filePath, String fileName, DownloadListner l) {
        if (TextUtils.isEmpty(filePath)) {//没有指定下载目录,使用默认目录
            filePath = getDefaultDirectory();
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = getFileName(urlPath);
        }
        downloadThread=new DownloadThread(new FilePoint(urlPath, filePath, fileName), l);
    }

}