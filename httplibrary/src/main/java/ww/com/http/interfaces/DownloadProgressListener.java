package ww.com.http.interfaces;

public interface DownloadProgressListener {
    void onProgress(long bytesDownloaded, long totalBytes, boolean done);
}