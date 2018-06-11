package downloaddemo.liyanying.example.com.download.thread;


import android.os.Handler;
import android.os.Message;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import downloaddemo.liyanying.example.com.download.listners.DownloadListner;
import downloaddemo.liyanying.example.com.download.module.FilePoint;
import downloaddemo.liyanying.example.com.download.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;

public class DownloadThread extends Handler {

    private FilePoint mPoint;
    private long mFileLength;//文件大小
    private boolean isDownloading = false;//是否正在下载

    private HttpUtil mHttpUtil;//http网络通信工具
    private long mProgress;//下载进度
    private File cacheFile;//临时缓存文件
    private File mTmpFile;//临时占位文件
    private boolean pause;//是否暂停
    private boolean cancel;//是否取消下载
    private final int MSG_PROGRESS = 1;//进度
    private final int MSG_FINISH = 2;//完成下载
    private final int MSG_PAUSE = 3;//暂停
    private final int MSG_CANCEL = 4;//取消下载
    private DownloadListner mListner;//下载回调监听

    private static int DOWNINTERRUPT = -1;//下载中断

    public DownloadThread(FilePoint point, DownloadListner l) {
        this.mPoint = point;
        this.mListner = l;
        this.mHttpUtil = HttpUtil.getInstance();
    }

    /**
     * 开始下载
     */

    public synchronized void start() {
        try {
            if (isDownloading) {
                return;
            }
            isDownloading = true;
            mHttpUtil.getContentLength(mPoint.getUrl(), new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        close(response.body());
                        resetStutus();
                        return;
                    }
                    // 获取资源大小
                    mFileLength = response.body().contentLength();
                    close(response.body());
                    // 在本地创建一个与资源同样大小的文件来占位
                    mTmpFile = new File(mPoint.getFilePath(), mPoint.getFileName() + ".tmp");
                    if (!mTmpFile.getParentFile().exists()) {
                        mTmpFile.getParentFile().mkdirs();
                    }
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                    tmpAccessFile.setLength(mFileLength);
                    if (mListner != null) {
                        mListner.onTotalSize(mFileLength);
                    }
                    download(mFileLength);// 开启线程下载
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    resetStutus();
                    if (mListner != null) {
                        mListner.onError(DOWNINTERRUPT);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            resetStutus();
            if (mListner != null) {
                mListner.onError(DOWNINTERRUPT);
            }
        }
    }

    /**
     * 下载
     *
     * @param endIndex 下载结束位置
     * @throws IOException
     */

    public void download(final long endIndex) throws IOException {

        long newStartIndex = 0L;

        // 加载下载位置缓存数据文件
        cacheFile = new File(mPoint.getFilePath(), mPoint.getFileName() + ".cache");

        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");
        if (cacheFile.exists()) {// 如果文件存在
            String startIndexStr = cacheAccessFile.readLine();
            try {
                newStartIndex = Integer.parseInt(startIndexStr);//重新设置下载起点
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        final long finalStartIndex = newStartIndex;
        mHttpUtil.downloadFileByRange(mPoint.getUrl(), finalStartIndex, endIndex, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() != 206) {// 206：请求部分资源成功码，表示服务器支持断点续传
                    resetStutus();
                    return;
                }
                try {
                    InputStream is = response.body().byteStream();// 获取流
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");// 获取前面已创建的文件.
                    tmpAccessFile.seek(finalStartIndex);// 文件写入的开始位置.
                    /*  将网络流中的文件写入本地*/
                    byte[] buffer = new byte[1024 << 2];
                    int length = -1;
                    int total = 0;// 记录本次下载文件的大小
                    long progress = 0;
                    while ((length = is.read(buffer)) > 0) {//读取流
                        if (cancel) {
                            close(cacheAccessFile, is, response.body());//关闭资源
                            cleanFile(cacheFile);//删除对应缓存文件
                            sendMessage(MSG_CANCEL);
                            return;
                        }
                        if (pause) {
                            //关闭资源
                            close(cacheAccessFile, is, response.body());
                            //发送暂停消息
                            sendMessage(MSG_PAUSE);
                            return;
                        }
                        tmpAccessFile.write(buffer, 0, length);
                        total += length;
                        progress = finalStartIndex + total;

                        //将该线程最新完成下载的位置记录并保存到缓存数据文件中
                        //建议转成Base64码，防止数据被修改，导致下载文件出错
                        cacheAccessFile.seek(0);
                        cacheAccessFile.write((progress + "").getBytes("UTF-8"));
                        //发送进度消息
                        mProgress = progress;
                        sendMessage(MSG_PROGRESS);
                    }
                    //关闭资源
                    close(cacheAccessFile, is, response.body());
                    // 删除临时文件
                    cleanFile(cacheFile);
                    //发送完成消息
                    sendMessage(MSG_FINISH);
                } catch (Exception e) {
                    resetStutus();
                    if (mListner != null) {
                        mListner.onError(DOWNINTERRUPT);
                    }
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                resetStutus();
                if (mListner != null) {
                    mListner.onError(DOWNINTERRUPT);
                }
            }
        });
    }

    /**
     * 轮回消息回调
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (null == mListner) {
            return;
        }
        switch (msg.what) {

            case MSG_PROGRESS://进度

                if (mListner != null) {
                    mListner.onCurrentSize(mProgress);
                }

                break;

            case MSG_PAUSE://暂停
                resetStutus();
                if (mListner != null) {
                    mListner.onPause();
                }
                break;

            case MSG_FINISH://完成
                mTmpFile.renameTo(new File(mPoint.getFilePath(), mPoint.getFileName()));//下载完毕后，重命名目标文件名
                resetStutus();
                if (mListner != null) {
                    mListner.onFinished();
                }
                break;

            case MSG_CANCEL://取消

                resetStutus();
                mProgress = 0;
//                mListner.onCancel();
                break;

            default:
                break;

        }

    }

    /**
     * 发送消息到轮回器
     *
     * @param what
     */

    private void sendMessage(int what) {
        //发送暂停消息
        Message message = new Message();
        message.what = what;
        sendMessage(message);
    }

    /**
     * 关闭资源
     *
     * @param closeables
     */

    private void close(Closeable... closeables) {

        int length = closeables.length;
        try {
            for (int i = 0; i < length; i++) {
                Closeable closeable = closeables[i];
                if (null != closeable) {
                    closeables[i].close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }


    /**
     * 暂停
     */

    public void pause() {
        pause = true;
    }


    /**
     * 取消
     */

    public void cancel() {
        cancel = true;
        cleanFile(mTmpFile);
        if (!isDownloading) {//针对非下载状态的取消，如暂停
            if (null != mListner) {
                cleanFile(cacheFile);
                resetStutus();
//                mListner.onCancel();
            }
        }
    }

    /**
     * 重置下载状态
     */

    private void resetStutus() {
        pause = false;
        cancel = false;
        isDownloading = false;
    }

    /**
     * 删除临时文件
     */

    private void cleanFile(File... files) {
        for (int i = 0, length = files.length; i < length; i++) {
            if (null != files[i]) {
                files[i].delete();
            }
        }
    }

    /**
     * 获取下载状态
     *
     * @return boolean
     */
    public boolean isDownloading() {
        return isDownloading;
    }


}
