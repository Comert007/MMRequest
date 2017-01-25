package ww.com.http.interfaces;

public interface UploadProgressListener {
    void onProgress(long bytesUploaded, long totalBytes, boolean done);
}