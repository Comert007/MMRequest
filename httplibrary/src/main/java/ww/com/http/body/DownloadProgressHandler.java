package ww.com.http.body;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import ww.com.http.bean.Progress;
import ww.com.http.core.RequestConstants;
import ww.com.http.interfaces.DownloadProgressListener;

class DownloadProgressHandler extends Handler {

    private final WeakReference<DownloadProgressListener> mDownloadProgressListenerWeakRef;

    public DownloadProgressHandler(DownloadProgressListener downloadProgressListener) {
        super(Looper.getMainLooper());
        mDownloadProgressListenerWeakRef = new WeakReference<>(downloadProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        final DownloadProgressListener downloadProgressListener = mDownloadProgressListenerWeakRef.get();
        switch (msg.what) {
            case RequestConstants.UPDATE:
                if (downloadProgressListener != null) {
                    final Progress progress = (Progress) msg.obj;
                    downloadProgressListener.onProgress(progress.currentBytes,
                            progress.totalBytes, progress.done);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}