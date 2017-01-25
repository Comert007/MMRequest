package ww.com.http.bean;

import java.io.Serializable;

public class Progress implements Serializable {

    public long currentBytes;
    public long totalBytes;
    public boolean done;

    public Progress(long currentBytes, long totalBytes,
                    boolean done) {
        this.currentBytes = currentBytes;
        this.totalBytes = totalBytes;
        this.done = done;
    }
}