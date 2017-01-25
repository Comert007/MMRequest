package ww.com.http.body;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import ww.com.http.bean.Progress;
import ww.com.http.core.RequestConstants;
import ww.com.http.interfaces.UploadProgressListener;

class UploadProgressHandler extends Handler {

    private final WeakReference<UploadProgressListener> mUploadProgressListenerWeakRef;

    public UploadProgressHandler(UploadProgressListener uploadProgressListener) {
        super(Looper.getMainLooper());
        mUploadProgressListenerWeakRef = new WeakReference<>(uploadProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        final UploadProgressListener uploadProgressListener = mUploadProgressListenerWeakRef.get();
        switch (msg.what) {
            case RequestConstants.UPDATE:
                if (uploadProgressListener != null) {
                    final Progress progress = (Progress) msg.obj;
                    uploadProgressListener.onProgress(progress.currentBytes, progress.totalBytes,
                            progress.done);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}