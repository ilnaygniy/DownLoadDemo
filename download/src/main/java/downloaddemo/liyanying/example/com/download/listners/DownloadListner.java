package downloaddemo.liyanying.example.com.download.listners;

public interface DownloadListner {

    void onTotalSize(long totalSize);
    void onCurrentSize(long currentSize);
    void onPause();
    void onFinished();
    void onError(int errorCode);
}
