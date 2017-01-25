package com.android.ww.mmrequest.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fighter on 2016/9/7.
 */
public class StreamUtils {
    /**
     * {@value}
     */
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB
    /**
     * {@value}
     */
    public static final int DEFAULT_STREAM_TOTAL_SIZE = 500 * 1024; // 500 Kb
    /**
     * {@value}
     */
    public static final int CONTINUE_LOADING_PERCENTAGE = 75;

    private StreamUtils() {
    }

    public static boolean copyStream(InputStream is, OutputStream os) throws IOException {
        return copyStream(is, os, DEFAULT_BUFFER_SIZE);
    }

    public static boolean copyStream(InputStream is, OutputStream os, int bufferSize)
            throws IOException {
        int current = 0;
        int total = is.available();
        if (total <= 0) {
            total = DEFAULT_STREAM_TOTAL_SIZE;
        }

        final byte[] bytes = new byte[bufferSize];
        int count;
        while ((count = is.read(bytes, 0, bufferSize)) != -1) {
            os.write(bytes, 0, count);
            current += count;
        }
        os.flush();
        return true;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
